/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import javafx.scene.Node;
import javafx.scene.input.ZoomEvent;

import org.eclipse.gef4.mvc.policies.AbstractPolicy;

public abstract class AbstractFXZoomPolicy extends AbstractPolicy<Node> {

	/**
	 * Reaction to the detection of pinch (close fingers) gestures.
	 */
	public abstract void zoomDetected(ZoomEvent e, double partialFactor);

	/**
	 * Continuous reaction to pinch (close fingers) gestures. Called
	 * continuously on finger movement, after the gesture has been detected, and
	 * before it has been finished.
	 */
	public abstract void zoomed(ZoomEvent e, double partialFactor,
			double totalFactor);

	/**
	 * Reaction to the finish of pinch (close fingers) gestures.
	 */
	public abstract void zoomFinished(ZoomEvent e, double totalFactor);
}
