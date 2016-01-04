/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import org.eclipse.gef4.fx.utils.CursorUtils;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractInteractionPolicy;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * An {@link AbstractFXOnDragPolicy} is called upon mouse drag events by the
 * {@link FXClickDragTool}. You can use it as an adapter on any
 * {@link IVisualPart} for which mouse drag interaction is desired, and you can
 * also register multiple instances of {@link AbstractFXOnDragPolicy} on the
 * same {@link IVisualPart} (with different adapter roles).
 *
 * @author anyssen
 *
 */
public abstract class AbstractFXOnDragPolicy
		extends AbstractInteractionPolicy<Node> {

	/**
	 * The original mouse {@link Cursor}.
	 */
	private Cursor originalCursor;

	/**
	 * Flag to indicate if the mouse cursor was changed. This is necessary to
	 * differentiate between no cursor change and an original null cursor.
	 */
	private boolean isCursorChanged = false;

	/**
	 * This callback method is invoked when the mouse is moved while a button is
	 * pressed.
	 *
	 * @param e
	 *            The original {@link MouseEvent}.
	 * @param delta
	 *            The mouse offset since {@link #press(MouseEvent)} (in pixel).
	 */
	public abstract void drag(MouseEvent e, Dimension delta);

	/**
	 * Returns the original mouse {@link Cursor} that is stored by this policy.
	 *
	 * @return The original mouse {@link Cursor} that is stored by this policy.
	 */
	protected Cursor getOriginalCursor() {
		return originalCursor;
	}

	/**
	 * Restores the original mouse cursor when it was previously changed by a
	 * call to {@link #showIndicationCursor(KeyEvent)} or
	 * {@link #showIndicationCursor(MouseEvent)}.
	 */
	public void hideIndicationCursor() {
		restoreCursor();
	}

	/**
	 * Returns <code>true</code> if the mouse cursor was changed by this policy.
	 * Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if the mouse cursor was changed by this policy,
	 *         Otherwise <code>false</code>.
	 */
	protected boolean isCursorChanged() {
		return isCursorChanged;
	}

	/**
	 * This callback method is invoked when a mouse button is pressed on the
	 * {@link #getHost() host}.
	 *
	 * @param e
	 *            The original {@link MouseEvent}
	 */
	public abstract void press(MouseEvent e);

	/**
	 * This callback method is invoked when the previously pressed mouse button
	 * is released.
	 *
	 * @param e
	 *            The original {@link MouseEvent}.
	 * @param delta
	 *            The mouse offset since {@link #press(MouseEvent)} (in pixel).
	 */
	public abstract void release(MouseEvent e, Dimension delta);

	/**
	 * Restores the mouse {@link Cursor} that was replaced by a previous
	 * {@link #storeAndReplaceCursor(Cursor)} call. If the mouse {@link Cursor}
	 * has never been replaced, it is not changed.
	 *
	 * @see #storeAndReplaceCursor(Cursor)
	 */
	protected void restoreCursor() {
		if (isCursorChanged) {
			setCursor(originalCursor);
			isCursorChanged = false;
		}
	}

	/**
	 * Sets the given {@link Cursor} as the mouse cursor for the {@link Scene}
	 * of the host visual. Note that this method does not store the original
	 * mouse cursor.
	 *
	 * @param cursor
	 *            The new mouse {@link Cursor}.
	 * @see #storeAndReplaceCursor(Cursor)
	 * @see #restoreCursor()
	 */
	protected void setCursor(Cursor cursor) {
		Scene scene = getHost().getVisual().getScene();
		if (cursor != scene.getCursor()) {
			scene.setCursor(cursor);
			CursorUtils.forceCursorUpdate(scene);
		}
	}

	/**
	 * Changes the mouse cursor depending on the given {@link KeyEvent} to
	 * indicate the action that is performed by this policy. The return value
	 * indicates if the mouse cursor was changed or not.
	 *
	 * @param event
	 *            The {@link KeyEvent} that initiated the determination of an
	 *            indication cursor.
	 * @return <code>true</code> if the mouse cursor was changed, otherwise
	 *         <code>false</code>.
	 */
	public boolean showIndicationCursor(KeyEvent event) {
		return false;
	}

	/**
	 * Changes the mouse cursor depending on the given {@link MouseEvent} to
	 * indicate the action that is performed by this policy. The return value
	 * indicates if the mouse cursor was changed or not.
	 *
	 * @param event
	 *            The {@link MouseEvent} that initiated the determination of an
	 *            indication cursor.
	 * @return <code>true</code> if the mouse cursor was changed, otherwise
	 *         <code>false</code>.
	 */
	public boolean showIndicationCursor(MouseEvent event) {
		return false;
	}

	/**
	 * Changes the mouse {@link Cursor} to the given value if necessary. If this
	 * method is called for the first time (in general or for the first time
	 * after a call to {@link #restoreCursor()}) the original cursor is stored
	 * so that it can later be restored.
	 *
	 * @param cursor
	 *            The new mouse {@link Cursor}.
	 * @see #restoreCursor()
	 */
	protected void storeAndReplaceCursor(Cursor cursor) {
		if (!isCursorChanged) {
			originalCursor = getHost().getVisual().getScene().getCursor();
			isCursorChanged = true;
		}
		setCursor(cursor);
	}

}
