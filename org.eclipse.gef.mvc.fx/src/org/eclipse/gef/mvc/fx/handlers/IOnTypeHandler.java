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

import java.util.Set;

import org.eclipse.gef.mvc.fx.gestures.TypeStrokeGesture;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * An interaction handler that implements the {@link IOnTypeHandler} interface
 * will be notified about keyboard events by the {@link TypeStrokeGesture}.
 *
 * @author mwienand
 *
 */
public interface IOnTypeHandler extends IHandler {

	/**
	 * This callback method is called whenever the user presses keyboard keys so
	 * that a character is generated. The most recent {@link KeyEvent} of type
	 * {@link KeyEvent#KEY_TYPED} and a {@link Set} of currently pressed
	 * {@link KeyCode}s is passed to the method for processing.
	 *
	 * @param keyEvent
	 *            The original {@link KeyEvent}.
	 * @param pressedKeys
	 *            The {@link Set} of pressed keys.
	 */
	public void type(KeyEvent keyEvent, Set<KeyCode> pressedKeys);

}