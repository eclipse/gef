/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

public abstract class FXPinchGesture {
	
	private Scene scene;
	
	private EventHandler<? super ZoomEvent> zoomDetectedHandler = new EventHandler<ZoomEvent>() {
		@Override
		public void handle(ZoomEvent event) {
			zoomDetected(event);
		}
	};
	
	private EventHandler<? super ZoomEvent> zoomHandler = new EventHandler<ZoomEvent>() {
		@Override
		public void handle(ZoomEvent event) {
			zoomed(event);
		}
	};
	
	private EventHandler<? super ZoomEvent> zoomFinishedHandler = new EventHandler<ZoomEvent>() {
		@Override
		public void handle(ZoomEvent event) {
			zoomFinished(event);
		}
	};

	protected abstract void zoomFinished(ZoomEvent event);

	protected abstract void zoomed(ZoomEvent event);

	protected abstract void zoomDetected(ZoomEvent event);

	public void setScene(Scene scene) {
		if (this.scene == scene) {
			return;
		}
		
		if (this.scene != null) {
			this.scene.removeEventHandler(ZoomEvent.ZOOM_FINISHED,
					zoomFinishedHandler);
			this.scene.removeEventHandler(ZoomEvent.ZOOM_STARTED,
					zoomDetectedHandler);
			this.scene.removeEventHandler(ZoomEvent.ZOOM,
					zoomHandler);
		}

		this.scene = scene;

		if (this.scene != null) {
			this.scene.addEventHandler(ZoomEvent.ZOOM_FINISHED,
					zoomFinishedHandler);
			this.scene.addEventHandler(ZoomEvent.ZOOM_STARTED,
					zoomDetectedHandler);
			this.scene.addEventHandler(ZoomEvent.ZOOM,
					zoomHandler);
		}
		
	}

}
