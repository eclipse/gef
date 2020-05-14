/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.anchors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.common.beans.property.ReadOnlyMapWrapperEx;
import org.eclipse.gef.fx.listeners.VisualChangeListener;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.Point;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.transform.Transform;

/**
 * {@link AbstractAnchor} is the abstract base implementation for
 * {@link IAnchor}s. It provides the facility to bind an anchor to an anchorage
 * {@link Node} ({@link #anchorageProperty()}), to attach and detach
 * {@link Node}s via {@link AnchorKey}s, and to provide positions (
 * {@link #positionsUnmodifiableProperty()}) for the attached {@link AnchorKey}
 * s.
 * <p>
 * It also registers the necessary listeners at the anchorage {@link Node} and
 * the attached {@link Node}s as well as relevant ancestor {@link Node}s, to
 * trigger the (re-)computation of positions.
 * <p>
 * The actual computation of positions for attached nodes is delegated to
 * {@link #computePosition(AnchorKey)}, thus left to subclasses. If a subclass
 * needs additional information to compute positions for attached
 * {@link AnchorKey}s, it may request that an {@link IAdaptable} info gets
 * passed into {@link #attach(AnchorKey)} and {@link #detach(AnchorKey)}, and
 * may overwrite both methods to get access to it.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public abstract class AbstractAnchor implements IAnchor {

	private ReadOnlyObjectWrapper<Node> anchorageProperty = new ReadOnlyObjectWrapper<>();
	private SetMultimap<Node, AnchorKey> keysByNode = HashMultimap.create();

	private ObservableMap<AnchorKey, Point> positions = FXCollections
			.observableHashMap();
	private ObservableMap<AnchorKey, Point> positionsUnmodifiable;
	private ReadOnlyMapWrapper<AnchorKey, Point> positionsUnmodifiableProperty;

	// TODO: push this down to dynamic anchor (as its only needed there)
	private Map<Node, VisualChangeListener> vcls = new HashMap<>();

	private ChangeListener<Scene> anchoredSceneChangeListener = new ChangeListener<Scene>() {
		@Override
		public void changed(ObservableValue<? extends Scene> observable,
				Scene oldValue, Scene newValue) {
			// determine which anchored changed
			for (Node anchored : keysByNode.keySet()) {
				if (anchored.sceneProperty() == observable) {
					if (oldValue == newValue) {
						return;
					}
					if (oldValue != null) {
						// System.out.println(
						// "Try to unregister VCL because anchored "
						// + anchored + " lost scene reference.");
						unregisterVCL(anchored);
					}
					if (newValue != null) {
						// System.out
						// .println("Try to register VCL because anchored "
						// + anchored
						// + " obtained scene reference.");
						registerVCL(anchored);
					}
					break;
				}
			}
		}
	};

	private ChangeListener<Node> anchorageChangeListener = new ChangeListener<Node>() {

		private ChangeListener<Scene> anchorageSceneChangeListener = new ChangeListener<Scene>() {
			@Override
			public void changed(ObservableValue<? extends Scene> observable,
					Scene oldValue, Scene newValue) {
				if (oldValue != null) {
					// System.out.println("Try to unregister VCLs because
					// anchorage
					// "
					// + getAnchorage() + " lost scene reference.");
					unregisterVCLs();
				}
				if (newValue != null) {
					// System.out.println("Try to register VCLs because
					// anchorage "
					// + getAnchorage() + " obtained scene reference.");
					registerVCLs();
				}
			}
		};

		@Override
		public void changed(ObservableValue<? extends Node> observable,
				Node oldAnchorage, Node newAnchorage) {
			if (oldAnchorage != null) {
				// System.out
				// .println("Try to unregister VCLS because old anchorage "
				// + oldAnchorage + " was removed.");
				unregisterVCLs();
				oldAnchorage.sceneProperty()
						.removeListener(anchorageSceneChangeListener);
			}
			if (newAnchorage != null) {
				// register listener on scene property, so we can react to
				// changes of the scene property of the anchorage node
				newAnchorage.sceneProperty()
						.addListener(anchorageSceneChangeListener);
				// System.out.println("Try to register VCLS because new
				// anchorage "
				// + newAnchorage + " was set.");
				registerVCLs();
			}
		}
	};

	/**
	 * Creates a new {@link AbstractAnchor} for the given <i>anchorage</i>
	 * {@link Node}.
	 *
	 * @param anchorage
	 *            The anchorage {@link Node} for this {@link AbstractAnchor}.
	 */
	public AbstractAnchor(Node anchorage) {
		anchorageProperty.addListener(anchorageChangeListener);
		// XXX Set anchorage after registering the anchorageChangeListener, so
		// that its addition is properly tracked (and change listeners are
		// attached)
		setAnchorage(anchorage);
	}

	@Override
	public ReadOnlyObjectProperty<Node> anchorageProperty() {
		return anchorageProperty.getReadOnlyProperty();
	}

	@Override
	public void attach(AnchorKey key) {
		Node anchored = key.getAnchored();
		if (!keysByNode.containsKey(anchored)) {
			anchored.sceneProperty().addListener(anchoredSceneChangeListener);
		}
		keysByNode.put(anchored, key);

		if (!vcls.containsKey(anchored)) {
			VisualChangeListener vcl = createVCL(anchored);
			vcls.put(anchored, vcl);
			// System.out.println(
			// "Try to register VCL, because anchored " + key.getAnchored()
			// + " was attached to anchorage " + getAnchorage());
			registerVCL(anchored);
		}

		updatePosition(key);
	}

	private boolean canRegister(Node anchored) {
		// we can register if there is a common ancestor
		if (getAnchorage() == null || anchored == null) {
			return false;
		}
		return NodeUtils.getNearestCommonAncestor(getAnchorage(),
				anchored) != null;
	}

	/**
	 * Recomputes the position for the given attached {@link AnchorKey} by
	 * delegating to the respective {@link IComputationStrategy}.
	 *
	 * @param key
	 *            The {@link AnchorKey} for which to compute an anchor position.
	 * @return The point for the given {@link AnchorKey} in local coordinates of
	 *         the anchored {@link Node}.
	 */
	protected abstract Point computePosition(AnchorKey key);

	private VisualChangeListener createVCL(final Node anchored) {
		return new VisualChangeListener() {
			@Override
			protected void boundsInLocalChanged(Bounds oldBounds,
					Bounds newBounds) {
				updatePositions(anchored);
			}

			@Override
			protected void localToParentTransformChanged(Node observed,
					Transform oldTransform, Transform newTransform) {
				updatePositions(anchored);
			}

			@Override
			public void register(Node observed, Node observer) {
				super.register(observed, observer);
				/*
				 * The visual change listener is registered when the anchorage
				 * is attached to a scene. Therefore, the anchorages
				 * bounds/transformation could have "changed" until
				 * registration, so we have to recompute anchored's positions
				 * now.
				 */
				updatePositions(anchored);
			}
		};
	}

	@Override
	public void detach(AnchorKey key) {
		Node anchored = key.getAnchored();
		if (!isAttached(key)) {
			throw new IllegalArgumentException(
					"The given AnchorKey was not previously attached to this IAnchor.");
		}

		// remove from positions map so that a change event is fired when it is
		// attached again
		positions.remove(key);

		// remove from keysByNode to indicate it is detached
		keysByNode.remove(anchored, key);

		// clean-up for this anchored if necessary
		if (keysByNode.get(anchored).isEmpty()) {
			anchored.sceneProperty()
					.removeListener(anchoredSceneChangeListener);
			keysByNode.removeAll(anchored);
			// System.out.println("Trying to unregister VCL as anchored "
			// + anchored + " has been detached from anchorage "
			// + getAnchorage());
			unregisterVCL(anchored);
			vcls.remove(anchored);
		}
	}

	@Override
	public Node getAnchorage() {
		return anchorageProperty.get();
	}

	/**
	 * Returns all keys maintained by this anchor.
	 *
	 * @return A set containing all {@link AnchorKey}s.
	 */
	protected Set<AnchorKey> getKeys() {
		Set<AnchorKey> allKeys = new HashSet<>();
		for (Node n : keysByNode.keySet()) {
			allKeys.addAll(keysByNode.get(n));
		}
		return allKeys;
	}

	/**
	 * Returns the {@link Map} which stores the registered {@link AnchorKey}s
	 * per {@link Node} by reference.
	 *
	 * @return The {@link Map} which stores the registered {@link AnchorKey}s
	 *         per {@link Node} by reference.
	 */
	protected SetMultimap<Node, AnchorKey> getKeysByNode() {
		return keysByNode;
	}

	@Override
	public Point getPosition(AnchorKey key) {
		if (!isAttached(key)) {
			throw new IllegalArgumentException(
					"The AnchorKey is not attached to this anchor.");
		}
		return positions.get(key);
	}

	@Override
	public ObservableMap<AnchorKey, Point> getPositionsUnmodifiable() {
		if (positionsUnmodifiable == null) {
			positionsUnmodifiable = FXCollections
					.unmodifiableObservableMap(positions);
		}
		return positionsUnmodifiable;
	}

	@Override
	public boolean isAttached(AnchorKey key) {
		return keysByNode.containsKey(key.getAnchored())
				&& keysByNode.get(key.getAnchored()).contains(key);
	}

	@Override
	public ReadOnlyMapProperty<AnchorKey, Point> positionsUnmodifiableProperty() {
		if (positionsUnmodifiableProperty == null) {
			positionsUnmodifiableProperty = new ReadOnlyMapWrapperEx<>(
					getPositionsUnmodifiable());
		}
		return positionsUnmodifiableProperty.getReadOnlyProperty();
	}

	/**
	 * Registers a {@link VisualChangeListener} for the given anchored
	 * {@link Node}.
	 *
	 * @param anchored
	 *            The anchored {@link Node} to register a
	 *            {@link VisualChangeListener} at.
	 */
	protected void registerVCL(Node anchored) {
		if (canRegister(anchored)) {
			// System.out.println("Register VCL between anchorage "
			// + getAnchorage() + " and anchored " + anchored);
			VisualChangeListener vcl = vcls.get(anchored);
			if (!vcl.isRegistered()) {
				vcl.register(getAnchorage(), anchored);
				updatePositions(anchored);
			}
			// else {
			// System.out.println("VCL is already registered, thus skipping.");
			// }
		}
	}

	/**
	 * Registers {@link VisualChangeListener}s for all anchored {@link Node}s,
	 * or schedules their registration if the VCL cannot be registered yet.
	 */
	protected void registerVCLs() {
		for (Node anchored : vcls.keySet().toArray(new Node[] {})) {
			registerVCL(anchored);
		}
	}

	/**
	 * Sets the anchorage of this {@link AbstractAnchor} to the given value.
	 *
	 * @param anchorage
	 *            The new anchorage for this {@link AbstractAnchor}.
	 */
	protected void setAnchorage(Node anchorage) {
		anchorageProperty.set(anchorage);
	}

	/**
	 * Unregisters the {@link VisualChangeListener}s for the given anchored
	 * {@link Node}.
	 *
	 * @param anchored
	 *            The anchored Node to unregister a {@link VisualChangeListener}
	 *            from.
	 */
	protected void unregisterVCL(Node anchored) {
		// System.out.println("Unregister VCL between anchorage " +
		// getAnchorage()
		// + " and anchored " + anchored);
		VisualChangeListener vcl = vcls.get(anchored);
		if (vcl.isRegistered()) {
			vcl.unregister();
		}
		// else {
		// System.out.println("VCL is not registered, thus skipping.");
		// }
	}

	/**
	 * Unregisters the {@link VisualChangeListener}s for all anchored
	 * {@link Node}s.
	 */
	protected void unregisterVCLs() {
		for (Node anchored : vcls.keySet().toArray(new Node[] {})) {
			unregisterVCL(anchored);
		}
	}

	/**
	 * Updates the position for the given {@link AnchorKey}, i.e.
	 * <ol>
	 * <li>Queries its current position.</li>
	 * <li>Computes its new position.</li>
	 * <li>Checks if the position changed, and fires an appropriate event by
	 * putting the new position into the
	 * {@link #positionsUnmodifiableProperty()}</li>
	 * </ol>
	 *
	 * @param key
	 *            The {@link AnchorKey} for which the position is updated.
	 */
	protected void updatePosition(AnchorKey key) {
		// only update position if key is attached
		if (!isAttached(key)) {
			return;
		}

		// compute new position to see if it has changed
		Point oldPosition = getPosition(key);
		Point newPosition = computePosition(key);

		// System.out.print("[" + key.getId() + "] old = " + oldPosition
		// + ", new = " + newPosition);

		if (oldPosition == null || !oldPosition.equals(newPosition)) {
			// TODO: we could enforce that computePosition may never return
			// null or an invalid position
			if (newPosition != null && !Double.isNaN(newPosition.x)
					&& !Double.isInfinite(newPosition.x)
					&& !Double.isNaN(newPosition.y)
					&& !Double.isInfinite(newPosition.y)) {
				// System.out.println(" !!!");
				positions.put(key, newPosition);
				// return;
			}
		}
		// System.out.println();
	}

	/**
	 * Updates the positions for all attached {@link AnchorKey}s.
	 */
	protected void updatePositions() {
		for (AnchorKey key : getKeys()) {
			updatePosition(key);
		}
	}

	private void updatePositions(Node anchored) {
		SetMultimap<Node, AnchorKey> keys = getKeysByNode();
		if (keys.containsKey(anchored)) {
			Set<AnchorKey> keysCopy = new HashSet<>(keys.get(anchored));
			for (AnchorKey key : keysCopy) {
				updatePosition(key);
			}
		}
	}
}
