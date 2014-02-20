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
package org.eclipse.gef4.fx.listener;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

/**
 * You can use a NodeVisualListener to register/unregister specific listeners
 * for catching changes in the visual representation of a JavaFX {@link Node}.
 * Depending on the changed property, either the
 * {@link #boundsChanged(Bounds, Bounds)} or the
 * {@link #transformChanged(Transform, Transform)} method is called. A
 * bounds-in-local change occurs when the target node's effect, clip, stroke,
 * local transformations, or geometric bounds change. A local-to-scene-transform
 * change occurs when any node in the hierarchy of the target node undergoes a
 * transformation change.
 * 
 * @author mwienand
 * 
 */
public abstract class VisualChangeListener {

	private final ChangeListener<? super Bounds> boundsInLocalListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldValue, Bounds newValue) {
			boundsChanged(oldValue, newValue);
		}
	};

	private ChangeListener<? super Transform> localToSceneListener = new ChangeListener<Transform>() {
		@Override
		public void changed(ObservableValue<? extends Transform> observable,
				Transform oldValue, Transform newValue) {
			transformChanged(oldValue, newValue);
		}
	};
	
	private Node node;
	
	public void register(Node node) {
		if (this.node != null)
			unregister();
		this.node = node;
		node.boundsInLocalProperty().addListener(boundsInLocalListener);
		node.localToSceneTransformProperty().addListener(localToSceneListener);
	}
	
	public void unregister() {
		node.boundsInLocalProperty().removeListener(boundsInLocalListener);
		node.localToSceneTransformProperty().removeListener(localToSceneListener);
	}

	protected abstract void transformChanged(Transform oldTransform,
			Transform newTransform);

	protected abstract void boundsChanged(Bounds oldBounds, Bounds newBounds);

}
