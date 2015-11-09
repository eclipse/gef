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
public abstract class AbstractRotateGesture extends AbstractGesture {

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

	@Override
	protected void register() {
		getScene().addEventHandler(RotateEvent.ROTATION_FINISHED,
				rotateFinishedHandler);
		getScene().addEventHandler(RotateEvent.ROTATION_STARTED,
				rotateStartedHandler);
		getScene().addEventHandler(RotateEvent.ROTATE, rotateHandler);
	}

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

	@Override
	protected void unregister() {
		getScene().removeEventHandler(RotateEvent.ROTATION_FINISHED,
				rotateFinishedHandler);
		getScene().removeEventHandler(RotateEvent.ROTATION_STARTED,
				rotateStartedHandler);
		getScene().removeEventHandler(RotateEvent.ROTATE, rotateHandler);
	}

}
