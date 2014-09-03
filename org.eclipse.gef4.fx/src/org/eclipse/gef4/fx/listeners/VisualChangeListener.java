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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.transform.Transform;

/**
 * You can use a VisualChangeListener to register/unregister specific listeners
 * for catching changes in the visual representation of a JavaFX {@link Node}.
 * Depending on the changed property, either the
 * {@link #boundsInLocalChanged(Bounds, Bounds)} or the
 * {@link #localToParentTransformChanged(Node, Transform, Transform)} method is
 * called. A bounds-in-local change occurs when the target node's effect, clip,
 * stroke, local transformations, or geometric bounds change. A
 * local-to-parent-transform change occurs when the node undergoes a
 * transformation change. Transformation listeners are registered for all nodes
 * in the hierarchy up to a specific parent.
 *
 * @author mwienand
 *
 */
public abstract class VisualChangeListener {

	private HashMap<ChangeListener<Transform>, Node> localToParentTransformListeners = new HashMap<>();
	private final ChangeListener<? super Bounds> boundsInLocalListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldValue, Bounds newValue) {
			// only fire a visual change event if the new bounds are valid
			if (isValidBounds(newValue)) {
				boundsInLocalChanged(oldValue, newValue);
			}
		}
	};
	private Node observed;

	private Node parent;

	protected abstract void boundsInLocalChanged(Bounds oldBounds,
			Bounds newBounds);

	private Node getNearestCommonAncestor(Node source, Node target) {
		if (source == target) {
			return source;
		}

		Set<Node> parents = new HashSet<Node>();
		Node m = source;
		Node n = target;
		while (m != null || n != null) {
			if (m != null) {
				if (parents.contains(m)) {
					return m;
				}
				parents.add(m);
				if (n != null && parents.contains(n)) {
					return n;
				}
				m = m.getParent();
			}
			if (n != null) {
				if (parents.contains(n)) {
					return n;
				}
				parents.add(n);
				if (m != null && parents.contains(m)) {
					return m;
				}
				n = n.getParent();
			}
		}

		// could not find a common parent
		return null;
	}

	/**
	 * Checks if the given Bounds contain NaN values. Returns <code>true</code>
	 * if no NaN values are found, otherwise <code>false</code>.
	 *
	 * @param b
	 * @return
	 */
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

	/**
	 * Checks if the given Transform contains NaN values. Returns
	 * <code>true</code> if no NaN values are found, otherwise
	 * <code>false/<code>.
	 *
	 * @param t
	 * @return
	 */
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

	protected abstract void localToParentTransformChanged(Node observed,
			Transform oldTransform, Transform newTransform);

	/**
	 * Registers this listener on the given pair of observed and observer nodes
	 * to recognize visual changes of the observed node relative to the common
	 * parent of observer and observed node.
	 * <p>
	 * In detail, two kind of changes will be reported as visual changes:
	 * <ul>
	 * <li>changes to the bounds-in-local property of the observed node (
	 * {@link #boundsInLocalChanged(Bounds, Bounds)}) itself</li>
	 * <li>changes to the local-to-parent-transform property of any node in the
	 * observed node hierarchy up to (but excluding) the common parent of the
	 * observed and observer nodes (
	 * {@link #localToParentTransformChanged(Node, Transform, Transform)}).</li>
	 * </ul>
	 * <p>
	 * The use of a visual change lister allows to react to relative transform
	 * changes only. If the common parent of both nodes is for instance nested
	 * below a {@link ScrollPane}, this allows to ignore transform changes that
	 * result from scrolling, as these will (in most cases) not indicate a
	 * visual change.
	 *
	 * @param observed
	 *            The observed {@link Node} to be observed for visual changes,
	 *            which includes bounds-in-local changes for the source node
	 *            itself, as well as local-to-parent-transform changes for all
	 *            ancestor nodes (including the source node) up to (but
	 *            excluding) the common parent node of source and target.
	 * @param observer
	 *            A {@link Node} in the same {@link Scene} as the given observed
	 *            node, relative to which transform changes will be reported.
	 *            That is, local-to-parent-transform changes will only be
	 *            reported for all nodes in the hierarchy up to (but excluding)
	 *            the common parent of observed and observer.
	 */
	public void register(Node observed, Node observer) {
		if (observed == null) {
			throw new IllegalArgumentException("Observed may not be null.");
		}
		if (observer == null) {
			throw new IllegalArgumentException("Observer not be null.");
		}

		Node commonAncestor = getNearestCommonAncestor(observed, observer);
		if (commonAncestor == null) {
			throw new IllegalArgumentException(
					"Source and target do not share a common ancestor.");
		}

		Node tmp = observed;
		while (tmp != null && tmp != commonAncestor) {
			tmp = tmp.getParent();
		}
		if (tmp == null) {
			throw new IllegalArgumentException(
					"TransformReference needs to be ancestor of the given observed node.");
		}

		// unregister old listeners
		if (this.observed != null) {
			unregister();
		}

		// assign new nodes
		this.observed = observed;
		parent = commonAncestor;

		// add bounds listener
		observed.boundsInLocalProperty().addListener(boundsInLocalListener);
		// add transform listeners
		tmp = observed;
		while (tmp != null && tmp != parent) {
			final Node current = tmp;
			ChangeListener<Transform> transformChangeListener = new ChangeListener<Transform>() {
				@Override
				public void changed(
						ObservableValue<? extends Transform> observable,
						Transform oldValue, Transform newValue) {
					// only fire a visual change event if the new transform is
					// valid
					if (isValidTransform(newValue)) {
						localToParentTransformChanged(current, oldValue,
								newValue);
					}
				}
			};
			tmp.localToParentTransformProperty().addListener(
					transformChangeListener);
			localToParentTransformListeners.put(transformChangeListener, tmp);
			tmp = tmp.getParent();
		}
	}

	public void unregister() {
		// remove bounds listener
		observed.boundsInLocalProperty().removeListener(boundsInLocalListener);

		// remove transform listeners
		for (ChangeListener<Transform> l : localToParentTransformListeners
				.keySet()) {
			localToParentTransformListeners.get(l)
			.localToParentTransformProperty().removeListener(l);
		}
		localToParentTransformListeners.clear();
	}

}
