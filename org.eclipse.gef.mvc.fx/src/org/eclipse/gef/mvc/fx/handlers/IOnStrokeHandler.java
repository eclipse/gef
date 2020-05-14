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

import javafx.scene.input.KeyEvent;

/**
 * The {@link IOnStrokeHandler} can be used to process key presses and releases.
 * The policy starts processing when the first key is pressed, and stops
 * processing when the last key is released. The initial key press and final key
 * release are handled separately to the presses and releases in-between.
 * <p>
 * If you are interested in typed characters or in a single combination of
 * pressed keys, you can use {@link IOnTypeHandler} instead.
 *
 * @author mwienand
 *
 */
public interface IOnStrokeHandler extends IHandler {

	/**
	 * This callback method is invoked when the viewer loses its focus while a
	 * key press/release gesture is running.
	 */
	void abortPress();

	/**
	 * This callback method is invoked when the user releases a key while the
	 * host has keyboard focus.
	 *
	 * @param event
	 *            The original {@link KeyEvent}.
	 */
	void finalRelease(KeyEvent event);

	/**
	 * This callback method is invoked when the user presses a key while the
	 * host has keyboard focus.
	 *
	 * @param event
	 *            The original {@link KeyEvent}.
	 */
	void initialPress(KeyEvent event);

	/**
	 * This callback method is invoked when the user presses a key while a
	 * keyboard gesture is active, i.e. after the initial press (
	 * {@link #initialPress(KeyEvent)}) and before the final release
	 * ({@link #finalRelease(KeyEvent)}).
	 *
	 * @param event
	 *            The original {@link KeyEvent}.
	 */
	void press(KeyEvent event);

	/**
	 * This callback method is invoked when the user releases a key while a
	 * keyboard gesture is active, i.e. after the initial press (
	 * {@link #initialPress(KeyEvent)}) and before the final release
	 * ({@link #finalRelease(KeyEvent)}).
	 *
	 * @param event
	 *            The original {@link KeyEvent}.
	 */
	void release(KeyEvent event);

}
