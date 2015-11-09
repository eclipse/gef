/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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

import javafx.scene.Scene;

/**
 * The {@link AbstractGesture} is the base class for all gestures defined
 * within GEF4 FX. It manages the (un-)registration of event listeners based on
 * the availability of a JavaFX {@link Scene}.
 *
 * @author mwienand
 *
 */
public abstract class AbstractGesture {

	private Scene scene;

	/**
	 * The {@link Scene} on which this gesture is registered.
	 *
	 * @return The {@link Scene} on which this gesture is registered.
	 */
	public Scene getScene() {
		return scene;
	}

	/**
	 * Called when a {@link Scene} is provided. The new {@link Scene} can be
	 * obtained via {@link #getScene()}. Event handlers are registered here.
	 */
	protected abstract void register();

	/**
	 * Sets the {@link Scene} for this gesture to the given value.
	 * {@link #unregister() Unregisters} previously registered event listeners
	 * and {@link #register() registers} event listeners for this gesture on the
	 * new {@link Scene} when the given {@link Scene} is not <code>null</code>.
	 *
	 * @param scene
	 *            The new {@link Scene} for this gesture.
	 */
	public void setScene(Scene scene) {
		if (this.scene == scene) {
			return;
		}
		if (this.scene != null) {
			unregister();
		}
		this.scene = scene;
		if (scene != null) {
			register();
		}
	}

	/**
	 * Called when the {@link Scene} is removed. You can obtain the old
	 * {@link Scene} via {@link #getScene()} so that event handlers can be
	 * unregistered.
	 */
	protected abstract void unregister();

}
