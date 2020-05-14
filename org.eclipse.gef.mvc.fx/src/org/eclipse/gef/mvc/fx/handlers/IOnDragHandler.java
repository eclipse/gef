/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.handlers;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.fx.gestures.ClickDragGesture;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * An interaction handler that implements the {@link IOnDragHandler} interface
 * will be notified about mouse press-drag-release events by the
 * {@link ClickDragGesture} .
 *
 * @author mwienand
 *
 */
public interface IOnDragHandler extends IHandler {

	/**
	 * This callback method is invoked when the mouse drag gesture is aborted,
	 * i.e. the gesture ends unexpectedly, without a mouse release event being
	 * fired. The policy can decide to commit or roll back its transaction. The
	 * execution transaction to which operations are added during a mouse drag
	 * gesture is closed after execution of this method.
	 */
	void abortDrag();

	/**
	 * This callback method is invoked when the mouse is moved while a button is
	 * pressed.
	 *
	 * @param e
	 *            The original {@link MouseEvent}.
	 * @param delta
	 *            The mouse offset since {@link #startDrag(MouseEvent)} (in
	 *            pixel).
	 */
	void drag(MouseEvent e, Dimension delta);

	/**
	 * This callback method is invoked when the initially pressed mouse button
	 * is released, which ends the gesture. The execution transaction to which
	 * operations are added during a mouse drag gesture is closed after
	 * execution of this method.
	 *
	 * @param e
	 *            The original {@link MouseEvent}.
	 * @param delta
	 *            The mouse offset since {@link #startDrag(MouseEvent)} (in
	 *            pixel).
	 */
	void endDrag(MouseEvent e, Dimension delta);

	/**
	 * Restores the original mouse cursor when it was previously changed by a
	 * call to {@link #showIndicationCursor(KeyEvent)} or
	 * {@link #showIndicationCursor(MouseEvent)}.
	 */
	void hideIndicationCursor();

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

	/**
	 * This callback method is invoked when a mouse button is pressed on the
	 * host, which starts a mouse drag gesture. An execution transaction is
	 * opened prior to execution of this method to which all operations are
	 * added that are executed during a mouse drag gesture.
	 *
	 * @param e
	 *            The original {@link MouseEvent}
	 */
	void startDrag(MouseEvent e);

}