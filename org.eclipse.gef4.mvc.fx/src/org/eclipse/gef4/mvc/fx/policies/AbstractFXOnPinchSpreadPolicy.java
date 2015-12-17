/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import org.eclipse.gef4.mvc.fx.tools.FXPinchSpreadTool;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.scene.input.ZoomEvent;

/**
 * An {@link AbstractFXOnPinchSpreadPolicy} is called upon touch pinch/spreadd
 * events by the {@link FXPinchSpreadTool}. You can use it as an adapter on any
 * {@link IVisualPart} for which touch pinch/spread interaction is desired, and
 * you can also register multiple instances of
 * {@link AbstractFXOnPinchSpreadPolicy} on the same {@link IVisualPart} (with
 * different adapter roles).
 *
 * @author anyssen
 *
 */
public abstract class AbstractFXOnPinchSpreadPolicy
		extends AbstractFXInteractionPolicy {

	/**
	 * Continuous reaction to pinch (close fingers) gestures. Called
	 * continuously on finger movement, after the gesture has been detected, and
	 * before it has been finished.
	 *
	 * @param e
	 *            The original {@link ZoomEvent}.
	 */
	public abstract void zoom(ZoomEvent e);

	/**
	 * Reaction to the finish of pinch (close fingers) gestures.
	 *
	 * @param e
	 *            The original {@link ZoomEvent}.
	 */
	public abstract void zoomFinished(ZoomEvent e);

	/**
	 * Reaction to the detection of pinch (close fingers) gestures.
	 *
	 * @param e
	 *            The original {@link ZoomEvent}.
	 */
	public abstract void zoomStarted(ZoomEvent e);
}
