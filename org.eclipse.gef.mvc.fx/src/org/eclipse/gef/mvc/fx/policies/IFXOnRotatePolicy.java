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

import org.eclipse.gef.mvc.fx.tools.FXRotateTool;
import org.eclipse.gef.mvc.policies.IPolicy;

import javafx.scene.Node;
import javafx.scene.input.RotateEvent;

/**
 * An interaction policy that implements the {@link IFXOnRotatePolicy} interface
 * will be notified about touch rotate events by the {@link FXRotateTool}.
 *
 * @author mwienand
 *
 */
public interface IFXOnRotatePolicy extends IPolicy<Node> {

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
	 * Reaction to the detection of rotate gestures.
	 *
	 * @param e
	 *            The original {@link RotateEvent}.
	 */
	void startRotate(RotateEvent e);

}