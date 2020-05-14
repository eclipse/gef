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

import org.eclipse.gef.mvc.fx.gestures.RotateGesture;

import javafx.scene.input.RotateEvent;

/**
 * An interaction handler that implements the {@link IOnRotateHandler} interface
 * will be notified about touch rotate events by the {@link RotateGesture}.
 *
 * @author mwienand
 *
 */
public interface IOnRotateHandler extends IHandler {

	/**
	 * Reaction to the unexpected finish of a rotate gesture.
	 */
	void abortRotate();

	/**
	 * Reaction to the finish of rotate gestures.
	 *
	 * @param e
	 *            The original {@link RotateEvent}.
	 */
	void endRotate(RotateEvent e);

	/**
	 * Continuous reaction to rotation gestures. Called continuously on finger
	 * movement, after the gesture has been started, and before it has been
	 * finished.
	 *
	 * @param e
	 *            The original {@link RotateEvent}.
	 */
	void rotate(RotateEvent e);

	/**
	 * Reaction to the detection of rotate gestures.
	 *
	 * @param e
	 *            The original {@link RotateEvent}.
	 */
	void startRotate(RotateEvent e);

}