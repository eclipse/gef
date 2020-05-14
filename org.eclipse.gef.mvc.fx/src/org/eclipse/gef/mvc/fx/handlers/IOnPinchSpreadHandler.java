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

import org.eclipse.gef.mvc.fx.gestures.PinchSpreadGesture;

import javafx.scene.input.ZoomEvent;

/**
 * An interaction handler that implements the {@link IOnPinchSpreadHandler}
 * interface will be notified about touch pinch/spread events by the
 * {@link PinchSpreadGesture}.
 *
 * @author mwienand
 *
 */
public interface IOnPinchSpreadHandler extends IHandler {

	/**
	 * Reaction to the unexpected finish of a pinch gesture.
	 */
	void abortZoom();

	/**
	 * Reaction to the finish of pinch (close fingers) gestures.
	 *
	 * @param e
	 *            The original {@link ZoomEvent}.
	 */
	void endZoom(ZoomEvent e);

	/**
	 * Reaction to the detection of pinch (close fingers) gestures.
	 *
	 * @param e
	 *            The original {@link ZoomEvent}.
	 */
	void startZoom(ZoomEvent e);

	/**
	 * Continuous reaction to pinch (close fingers) gestures. Called
	 * continuously on finger movement, after the gesture has been detected, and
	 * before it has been finished.
	 *
	 * @param e
	 *            The original {@link ZoomEvent}.
	 */
	void zoom(ZoomEvent e);

}