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
public abstract class AbstractFXPinchSpreadGesture extends AbstractFXGesture {

	private boolean inZoom = false;

	private EventHandler<? super ZoomEvent> zoomStartedHandler = new EventHandler<ZoomEvent>() {
		@Override
		public void handle(ZoomEvent event) {
			/*
			 * Sometimes a zoom gesture will fire multiple ZOOM_STARTED events.
			 * These should not be reported since zooming is still in progress.
			 */
			if (!inZoom) {
				inZoom = true;
				zoomStarted(event);
			}
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
			inZoom = false;
		}
	};

	@Override
	protected void register() {
		getScene()
				.addEventHandler(ZoomEvent.ZOOM_FINISHED, zoomFinishedHandler);
		getScene().addEventHandler(ZoomEvent.ZOOM_STARTED, zoomStartedHandler);
		getScene().addEventHandler(ZoomEvent.ZOOM, zoomHandler);
	}

	@Override
	protected void unregister() {
		getScene().removeEventHandler(ZoomEvent.ZOOM_FINISHED,
				zoomFinishedHandler);
		getScene().removeEventHandler(ZoomEvent.ZOOM_STARTED,
				zoomStartedHandler);
		getScene().removeEventHandler(ZoomEvent.ZOOM, zoomHandler);
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
