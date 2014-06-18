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
package org.eclipse.gef4.fx.listeners;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

/**
 * You can use a VisualChangeListener to register/unregister specific listeners
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

	/*
	 * TODO: Currently, scrolling does cause a StackOverflowError, because
	 * scrolling does change the bounds-in-scene of anchored visuals, which in
	 * turn causes an anchor refresh, which in turn causes a scrollbar change in
	 * special cases.
	 *
	 * Therefore, we need to register the transform listener relative to some
	 * layer, so that scrolling does not affect the "local-to-layer" transform.
	 */

	private class TransformListener implements ChangeListener<Transform> {
		private Node rel;

		public TransformListener(Node relative) {
			super();
			rel = relative;
		}

		@Override
		public void changed(ObservableValue<? extends Transform> observable,
				Transform oldValue, Transform newValue) {
			if (lastChangedRelative != null
					&& rel == lastChangedRelative.getParent()) {
				lastChangedRelative = rel;
				return;
			}
			lastChangedRelative = rel;
			if (isValidTransform(newValue)) {
				transformChanged(oldValue, newValue);
			}
		}
	}

	private final ChangeListener<? super Bounds> boundsInLocalListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldValue, Bounds newValue) {
			boundsChanged(oldValue, newValue);
		}
	};

	private Node lastChangedRelative;

	private ChangeListener<? super Transform> sceneTransformListener = new ChangeListener<Transform>() {
		@Override
		public void changed(ObservableValue<? extends Transform> observable,
				Transform oldValue, Transform newValue) {
			transformChanged(oldValue, newValue);
		}
	};

	private List<ChangeListener<? super Transform>> transformListeners = new ArrayList<ChangeListener<? super Transform>>();

	private Node node;

	private Node parent;

	protected abstract void boundsChanged(Bounds oldBounds, Bounds newBounds);

	private boolean isValidTransform(Transform t) {
		if (Double.isNaN(t.getMxx())) {
			return false;
		}
		if (Double.isNaN(t.getMxy())) {
			return false;
		}
		if (Double.isNaN(t.getMxz())) {
			return false;
		}
		if (Double.isNaN(t.getMyx())) {
			return false;
		}
		if (Double.isNaN(t.getMyy())) {
			return false;
		}
		if (Double.isNaN(t.getMyz())) {
			return false;
		}
		if (Double.isNaN(t.getMzx())) {
			return false;
		}
		if (Double.isNaN(t.getMzy())) {
			return false;
		}
		if (Double.isNaN(t.getMzz())) {
			return false;
		}
		if (Double.isNaN(t.getTx())) {
			return false;
		}
		if (Double.isNaN(t.getTy())) {
			return false;
		}
		if (Double.isNaN(t.getTz())) {
			return false;
		}
		return true;
	}

	/**
	 * Registers change listeners on the given node. Transformation changes are
	 * only reported relative to the given parent node.
	 *
	 * @param node
	 * @param anyParent
	 */
	public void register(Node node, Node anyParent) {
		// unregister listeners from old node
		if (this.node != null) {
			unregister();
		}

		// assign new nodes
		this.node = node;
		parent = anyParent;

		// add bounds listener
		node.boundsInLocalProperty().addListener(boundsInLocalListener);

		// add transform listener
		if (parent == null) {
			// localToScene per default
			node.localToSceneTransformProperty().addListener(
					sceneTransformListener);
		} else {
			// otherwise localToX (X = parent)
			Node tmp = node;
			while (tmp != null && tmp != parent) {
				TransformListener listener = new TransformListener(tmp);
				transformListeners.add(listener);
				tmp.localToParentTransformProperty().addListener(listener);
				tmp = tmp.getParent();
			}
		}
	}

	protected abstract void transformChanged(Transform oldTransform,
			Transform newTransform);

	public void unregister() {
		// remove bounds listener
		node.boundsInLocalProperty().removeListener(boundsInLocalListener);

		// remove transform listener
		if (parent == null) {
			// localToScene per default
			node.localToSceneTransformProperty().removeListener(
					sceneTransformListener);
		} else {
			// otherwise localToX (X = parent)
			Node tmp = node;
			while (tmp != null && tmp != parent
					&& transformListeners.size() > 0) {
				ChangeListener<? super Transform> listener = transformListeners
						.remove(0);
				tmp.localToParentTransformProperty().removeListener(listener);
				tmp = tmp.getParent();
			}
		}
	}

}
