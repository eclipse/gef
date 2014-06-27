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

	private final ChangeListener<? super Transform> transformListener = new ChangeListener<Transform>() {
		@Override
		public void changed(ObservableValue<? extends Transform> observable,
				Transform oldValue, Transform newValue) {
			if (isValidTransform(newValue)) {
				transformChanged(oldValue, newValue);
			}
		}
	};

	private final ChangeListener<? super Bounds> boundsInLocalListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldValue, Bounds newValue) {
			if (isValidBounds(newValue)) {
				boundsChanged(oldValue, newValue);
			}
		}
	};
	private Node node;

	private Node parent;

	private List<Node> relatives = new ArrayList<Node>();

	protected abstract void boundsChanged(Bounds oldBounds, Bounds newBounds);

	private boolean isValidBounds(Bounds b) {
		if (Double.isNaN(b.getMinX())) {
			return false;
		}
		if (Double.isNaN(b.getMinY())) {
			return false;
		}
		if (Double.isNaN(b.getMaxX())) {
			return false;
		}
		if (Double.isNaN(b.getMaxY())) {
			return false;
		}
		return true;
	}

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
		if (node == null) {
			throw new IllegalArgumentException("Node may not be null.");
		}
		if (anyParent == null) {
			throw new IllegalArgumentException("Parent may not be null.");
		}

		// unregister listeners from old node
		if (this.node != null) {
			unregister();
		}

		// assign new nodes
		this.node = node;
		parent = anyParent;

		// add bounds listener
		node.boundsInLocalProperty().addListener(boundsInLocalListener);

		// add transform listeners
		Node tmp = node;
		while (tmp != null && tmp != parent) {
			relatives.add(tmp);
			tmp.localToParentTransformProperty().addListener(transformListener);
			tmp = tmp.getParent();
		}
	}

	protected abstract void transformChanged(Transform oldTransform,
			Transform newTransform);

	public void unregister() {
		// remove bounds listener
		node.boundsInLocalProperty().removeListener(boundsInLocalListener);

		// remove transform listeners
		for (Node n : relatives) {
			n.localToParentTransformProperty()
					.removeListener(transformListener);
		}
	}

}
