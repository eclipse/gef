/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import org.eclipse.gef4.fx.utils.CursorUtils;
import org.eclipse.gef4.mvc.policies.AbstractInteractionPolicy;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

/**
 * The {@link AbstractFXInteractionPolicy} is a JavaFX-specific extension to the
 * {@link AbstractInteractionPolicy} class. It provides methods to replace and
 * restore the mouse {@link Cursor}:
 * <ol>
 * <li>{@link #storeAndReplaceCursor(Cursor)}
 * <li>{@link #restoreCursor()}
 * </ol>
 *
 * @author mwienand
 *
 */
public abstract class AbstractFXInteractionPolicy
		extends AbstractInteractionPolicy<Node> {

	/**
	 * The original mouse {@link Cursor}.
	 */
	private Cursor originalCursor;

	/**
	 * Returns the {@link Cursor} that should be shown to indicate that this
	 * policy is going to handle a following interaction.
	 *
	 * @param event
	 *            The {@link MouseEvent} that initiated the determination of an
	 *            indication cursor.
	 * @return Returns the {@link Cursor} that should be shown to indicate that
	 *         this policy is going to handle a following interaction.
	 */
	public Cursor getIndicationCursor(MouseEvent event) {
		return null;
	}

	/**
	 * Restores the mouse {@link Cursor} that was replaced by a previous
	 * {@link #storeAndReplaceCursor(Cursor)} call. If the mouse {@link Cursor}
	 * has never been replaced, it is not changed.
	 *
	 * @see #storeAndReplaceCursor(Cursor)
	 */
	public void restoreCursor() {
		Scene scene = getHost().getVisual().getScene();
		if (scene.getCursor() != originalCursor) {
			scene.setCursor(originalCursor);
			CursorUtils.forceCursorUpdate(scene);
		}
	}

	/**
	 * Changes the mouse {@link Cursor} to the given value if necessary. Stores
	 * the original mouse {@link Cursor}, so that it can later be restored.
	 *
	 * @param cursor
	 *            The new mouse {@link Cursor}.
	 * @see #restoreCursor()
	 */
	public void storeAndReplaceCursor(Cursor cursor) {
		Scene scene = getHost().getVisual().getScene();
		if (originalCursor == null) {
			originalCursor = scene.getCursor();
		}
		if (cursor != originalCursor) {
			scene.setCursor(cursor);
			CursorUtils.forceCursorUpdate(scene);
		}
	}

}
