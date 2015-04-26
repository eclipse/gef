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
package org.eclipse.gef4.fx.gestures;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.RotateEvent;

public abstract class FXRotateGesture {

	private Scene scene;

	private EventHandler<? super RotateEvent> rotateStartedHandler = new EventHandler<RotateEvent>() {
		@Override
		public void handle(RotateEvent event) {
			rotationStarted(event);
		}
	};

	private EventHandler<? super RotateEvent> rotateHandler = new EventHandler<RotateEvent>() {
		@Override
		public void handle(RotateEvent event) {
			rotate(event);
		}
	};

	private EventHandler<? super RotateEvent> rotateFinishedHandler = new EventHandler<RotateEvent>() {
		@Override
		public void handle(RotateEvent event) {
			rotationFinished(event);
		}
	};

	protected abstract void rotate(RotateEvent event);

	protected abstract void rotationFinished(RotateEvent event);

	protected abstract void rotationStarted(RotateEvent event);

	public void setScene(Scene scene) {
		if (this.scene == scene) {
			return;
		}

		if (this.scene != null) {
			this.scene.removeEventHandler(RotateEvent.ROTATION_FINISHED,
					rotateFinishedHandler);
			this.scene.removeEventHandler(RotateEvent.ROTATION_STARTED,
					rotateStartedHandler);
			this.scene.removeEventHandler(RotateEvent.ROTATE, rotateHandler);
		}

		this.scene = scene;

		if (this.scene != null) {
			this.scene.addEventHandler(RotateEvent.ROTATION_FINISHED,
					rotateFinishedHandler);
			this.scene.addEventHandler(RotateEvent.ROTATION_STARTED,
					rotateStartedHandler);
			this.scene.addEventHandler(RotateEvent.ROTATE, rotateHandler);
		}

	}

}
