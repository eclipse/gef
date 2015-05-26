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

/**
 * An FXRotateGesture can be used to listen to touchpad rotation events. In
 * order to use it, you have to subclass it and implement the
 * {@link #rotationStarted(RotateEvent)}, {@link #rotate(RotateEvent)}, and
 * {@link #rotationFinished(RotateEvent)} methods.
 *
 * @author anyssen
 *
 */
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

	/**
	 * Called upon {@link RotateEvent#ROTATE} events.
	 *
	 * @param event
	 *            The corresponding {@link RotateEvent}.
	 */
	protected abstract void rotate(RotateEvent event);

	/**
	 * Called upon {@link RotateEvent#ROTATION_FINISHED} events.
	 *
	 * @param event
	 *            The corresponding {@link RotateEvent}.
	 */
	protected abstract void rotationFinished(RotateEvent event);

	/**
	 * Called upon {@link RotateEvent#ROTATION_STARTED} events.
	 *
	 * @param event
	 *            The corresponding {@link RotateEvent}.
	 */
	protected abstract void rotationStarted(RotateEvent event);

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
