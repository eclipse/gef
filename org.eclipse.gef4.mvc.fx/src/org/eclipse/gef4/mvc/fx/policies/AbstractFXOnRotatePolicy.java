/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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

import org.eclipse.gef4.mvc.policies.AbstractPolicy;

import javafx.scene.Node;
import javafx.scene.input.RotateEvent;

public abstract class AbstractFXOnRotatePolicy extends AbstractPolicy<Node> {

	/**
	 * Continuous reaction to rotation gestures. Called continuously on finger
	 * movement, after the gesture has been started, and before it has been
	 * finished.
	 */
	public abstract void rotate(RotateEvent e);

	/**
	 * Reaction to the finish of rotate gestures.
	 */
	public abstract void rotationFinished(RotateEvent e);

	/**
	 * Reaction to the detection of rotate gestures.
	 */
	public abstract void rotationStarted(RotateEvent e);
}
