/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.gestures;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.ZoomEvent;

/**
 * An FXPinchSpreadGesture can be used to listen to touchpad pinch and spread
 * events. In order to use it, you have to subclass it and implement the
 * {@link #zoomStarted(ZoomEvent)}, {@link #zoom(ZoomEvent)}, and
 * {@link #zoomFinished(ZoomEvent)} methods.
 *
 * @author mwienand
 *
 */
public abstract class FXPinchSpreadGesture {

	private Scene scene;

	private EventHandler<? super ZoomEvent> zoomStartedHandler = new EventHandler<ZoomEvent>() {
		@Override
		public void handle(ZoomEvent event) {
			zoomStarted(event);
		}
	};

	private EventHandler<? super ZoomEvent> zoomHandler = new EventHandler<ZoomEvent>() {
		@Override
		public void handle(ZoomEvent event) {
			zoom(event);
		}
	};

	private EventHandler<? super ZoomEvent> zoomFinishedHandler = new EventHandler<ZoomEvent>() {
		@Override
		public void handle(ZoomEvent event) {
			zoomFinished(event);
		}
	};

	/**
	 * Sets the {@link Scene} for this gesture to the given value. Unregisters
	 * previously registered event listeners and registers event listeners for
	 * this gesture on the new {@link Scene} when the given {@link Scene} is not
	 * <code>null</code>.
	 *
	 * @param scene
	 *            The new {@link Scene} for this gesture.
	 */
	public void setScene(Scene scene) {
		if (this.scene == scene) {
			return;
		}

		if (this.scene != null) {
			this.scene.removeEventHandler(ZoomEvent.ZOOM_FINISHED,
					zoomFinishedHandler);
			this.scene.removeEventHandler(ZoomEvent.ZOOM_STARTED,
					zoomStartedHandler);
			this.scene.removeEventHandler(ZoomEvent.ZOOM, zoomHandler);
		}

		this.scene = scene;

		if (this.scene != null) {
			this.scene.addEventHandler(ZoomEvent.ZOOM_FINISHED,
					zoomFinishedHandler);
			this.scene.addEventHandler(ZoomEvent.ZOOM_STARTED,
					zoomStartedHandler);
			this.scene.addEventHandler(ZoomEvent.ZOOM, zoomHandler);
		}

	}

	/**
	 * Called upon {@link ZoomEvent#ZOOM} events.
	 *
	 * @param event
	 *            The corresponding {@link ZoomEvent}.
	 */
	protected abstract void zoom(ZoomEvent event);

	/**
	 * Called upon {@link ZoomEvent#ZOOM_FINISHED} events.
	 *
	 * @param event
	 *            The corresponding {@link ZoomEvent}.
	 */
	protected abstract void zoomFinished(ZoomEvent event);

	/**
	 * Called upon {@link ZoomEvent#ZOOM_STARTED} events.
	 *
	 * @param event
	 *            The corresponding {@link ZoomEvent}.
	 */
	protected abstract void zoomStarted(ZoomEvent event);

}
