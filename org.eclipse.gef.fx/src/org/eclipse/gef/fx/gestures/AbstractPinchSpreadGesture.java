/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.gestures;

import javafx.event.EventHandler;
import javafx.scene.input.ZoomEvent;

/**
 * An FXPinchSpreadGesture can be used to listen to touchpad pinch and spread
 * events. In order to use it, you have to subclass it and implement the
 * {@link #zoomStarted(ZoomEvent)}, {@link #zoom(ZoomEvent)}, and
 * {@link #zoomFinished(ZoomEvent)} methods.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public abstract class AbstractPinchSpreadGesture extends AbstractGesture {

	private boolean inZoom = false;

	private EventHandler<? super ZoomEvent> zoomFilter = new EventHandler<ZoomEvent>() {
		@Override
		public void handle(ZoomEvent event) {
			if (!event.isInertia()) {
				if (event.getEventType() == ZoomEvent.ZOOM_STARTED) {
					// prevent that multiple start events occur.
					if (!inZoom) {
						inZoom = true;
						zoomStarted(event);
					}
				} else if (event.getEventType() == ZoomEvent.ZOOM) {
					zoom(event);
				} else if (event.getEventType() == ZoomEvent.ZOOM_FINISHED) {
					zoomFinished(event);
					inZoom = false;
				}
			}
		}
	};

	@Override
	protected void register() {
		getScene().addEventFilter(ZoomEvent.ZOOM_FINISHED, zoomFilter);
		getScene().addEventFilter(ZoomEvent.ZOOM_STARTED, zoomFilter);
		getScene().addEventFilter(ZoomEvent.ZOOM, zoomFilter);
	}

	@Override
	protected void unregister() {
		getScene().removeEventFilter(ZoomEvent.ZOOM_STARTED, zoomFilter);
		getScene().removeEventFilter(ZoomEvent.ZOOM_FINISHED, zoomFilter);
		getScene().removeEventFilter(ZoomEvent.ZOOM, zoomFilter);
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
