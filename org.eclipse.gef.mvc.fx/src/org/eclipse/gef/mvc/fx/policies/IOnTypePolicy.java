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
package org.eclipse.gef.mvc.fx.policies;

import java.util.Set;

import org.eclipse.gef.mvc.fx.gestures.TypeGesture;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * An interaction policy that implements the {@link IOnTypePolicy} interface
 * will be notified about keyboard events by the {@link TypeGesture}.
 *
 * @author mwienand
 *
 */
public interface IOnTypePolicy extends IPolicy {

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