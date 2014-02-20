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

public abstract class FXPinchSpreadGesture {
	
	/**
	 * Represents the type of gesture:
	 * <ul>
	 * <li>{@link #PINCH}: close fingers</li>
	 * <li>{@link #SPREAD}: open fingers</li>
	 * </ul>
	 */
	private static enum Type {
		/**
		 * If the detection zoom factor is < 1, the gesture type will be PINCH.
		 */
		PINCH,

		/**
		 * If the detection zoom factor is >= 1, the gesture type will be SPREAD.
		 */
		SPREAD
	}

	private Scene scene;
	private Type type;
	
	private EventHandler<? super ZoomEvent> zoomDetectedHandler = new EventHandler<ZoomEvent>() {
		@Override
		public void handle(ZoomEvent event) {
			double partialFactor = event.getZoomFactor();
			double totalFactor = event.getTotalZoomFactor();
			
			if (totalFactor >= 1) {
				type = Type.SPREAD;
			} else {
				type = Type.PINCH;
			}
			
			if (type == Type.PINCH) {
				pinchDetected(event, partialFactor, totalFactor);
			} else {
				spreadDetected(event, partialFactor, totalFactor);
			}
		}
	};
	
	private EventHandler<? super ZoomEvent> zoomHandler = new EventHandler<ZoomEvent>() {
		@Override
		public void handle(ZoomEvent event) {
			double partialFactor = event.getZoomFactor();
			double totalFactor = event.getTotalZoomFactor();
			if (type == Type.PINCH) {
				pinch(event, partialFactor, totalFactor);
			} else {
				spread(event, partialFactor, totalFactor);
			}
		}
	};
	
	private EventHandler<? super ZoomEvent> zoomFinishedHandler = new EventHandler<ZoomEvent>() {
		@Override
		public void handle(ZoomEvent event) {
			double partialFactor = event.getZoomFactor();
			double totalFactor = event.getTotalZoomFactor();
			if (type == Type.PINCH) {
				pinchFinished(event, partialFactor, totalFactor);
			} else {
				spreadFinished(event, partialFactor, totalFactor);
			}
		}
	};

	protected abstract void spreadDetected(ZoomEvent e, double partialFactor, double totalFactor);

	protected abstract void pinchDetected(ZoomEvent e, double partialFactor, double totalFactor);

	protected abstract void spread(ZoomEvent e, double partialFactor, double totalFactor);

	protected abstract void pinch(ZoomEvent e, double partialFactor, double totalFactor);

	protected abstract void spreadFinished(ZoomEvent e, double partialFactor, double totalFactor);

	protected abstract void pinchFinished(ZoomEvent e, double partialFactor, double totalFactor);

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
