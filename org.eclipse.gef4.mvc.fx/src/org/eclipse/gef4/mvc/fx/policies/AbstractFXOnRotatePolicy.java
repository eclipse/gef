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

import org.eclipse.gef4.mvc.fx.tools.FXRotateTool;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

import javafx.scene.Node;
import javafx.scene.input.RotateEvent;

/**
 * An {@link AbstractFXOnRotatePolicy} is called upon touch rotate events by the
 * {@link FXRotateTool}. You can use it as an adapter on any {@link IVisualPart}
 * for which touch rotate interaction is desired, and you can also register
 * multiple instances of {@link AbstractFXOnRotatePolicy} on the same
 * {@link IVisualPart} (with different adapter roles).
 *
 * @author anyssen
 *
 */
public abstract class AbstractFXOnRotatePolicy extends AbstractPolicy<Node> {

	/**
	 * Continuous reaction to rotation gestures. Called continuously on finger
	 * movement, after the gesture has been started, and before it has been
	 * finished.
	 *
	 * @param e
	 *            The original {@link RotateEvent}.
	 */
	public abstract void rotate(RotateEvent e);

	/**
	 * Reaction to the finish of rotate gestures.
	 *
	 * @param e
	 *            The original {@link RotateEvent}.
	 */
	public abstract void rotationFinished(RotateEvent e);

	/**
	 * Reaction to the detection of rotate gestures.
	 *
	 * @param e
	 *            The original {@link RotateEvent}.
	 */
	public abstract void rotationStarted(RotateEvent e);
}
