/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.policies.IPolicy;

import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * An interaction policy that implements the {@link IFXOnDragPolicy} interface
 * will be notified about mouse press-drag-release events by the
 * {@link FXClickDragTool} .
 *
 * @author mwienand
 *
 */
public interface IFXOnDragPolicy extends IPolicy<Node> {

	/**
	 * This callback method is invoked when the mouse is moved while a button is
	 * pressed.
	 *
	 * @param e
	 *            The original {@link MouseEvent}.
	 * @param delta
	 *            The mouse offset since {@link #press(MouseEvent)} (in pixel).
	 */
	void drag(MouseEvent e, Dimension delta);

	/**
	 * Restores the original mouse cursor when it was previously changed by a
	 * call to {@link #showIndicationCursor(KeyEvent)} or
	 * {@link #showIndicationCursor(MouseEvent)}.
	 */
	void hideIndicationCursor();

	/**
	 * This callback method is invoked when a mouse button is pressed on the
	 * host.
	 *
	 * @param e
	 *            The original {@link MouseEvent}
	 */
	void press(MouseEvent e);

	/**
	 * This callback method is invoked when the previously pressed mouse button
	 * is released.
	 *
	 * @param e
	 *            The original {@link MouseEvent}.
	 * @param delta
	 *            The mouse offset since {@link #press(MouseEvent)} (in pixel).
	 */
	void release(MouseEvent e, Dimension delta);

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
	boolean showIndicationCursor(KeyEvent event);

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
	boolean showIndicationCursor(MouseEvent event);

}