/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.anchors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.fx.internal.ReadOnlyMapWrapperEx;
import org.eclipse.gef4.fx.listeners.VisualChangeListener;
import org.eclipse.gef4.geometry.planar.Point;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.transform.Transform;

/**
 * {@link AbstractAnchor} is the abstract base implementation for
 * {@link IAnchor}s. It provides the facility to bind an anchor to an anchorage
 * {@link Node} ({@link #anchorageProperty()}), to attach and detach
 * {@link Node}s via {@link AnchorKey}s, and to provide positions (
 * {@link #positionProperty()}) for the attached {@link AnchorKey}s.
 * <p>
 * It also registers the necessary listeners at the anchorage {@link Node} and
 * the attached {@link Node}s as well as relevant ancestor {@link Node}s, to
 * trigger the (re-)computation of positions.
 * <p>
 * The actual computation of positions for attached nodes is delegated to
 * {@link #computePosition(AnchorKey)}, thus left to subclasses. If a subclass
 * needs additional information to compute positions for attached
 * {@link AnchorKey}s, it may request that an {@link IAdaptable} info gets
 * passed into {@link #attach(AnchorKey, IAdaptable)} and
 * {@link #detach(AnchorKey, IAdaptable)}, and may overwrite both methods to get
 * access to it.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public abstract class AbstractAnchor implements IAnchor {

	private ReadOnlyObjectWrapper<Node> anchorageProperty = new ReadOnlyObjectWrapper<>();
	private ReadOnlyMapWrapper<AnchorKey, Point> positionProperty = new ReadOnlyMapWrapperEx<>(
			FXCollections.<AnchorKey, Point> observableHashMap());

	private Map<Node, Set<AnchorKey>> keys = new HashMap<>();
	private Map<Node, VisualChangeListener> vcls = new HashMap<>();

	private ChangeListener<Scene> anchoredSceneChangeListener = new ChangeListener<Scene>() {
		@Override
		public void changed(ObservableValue<? extends Scene> observable,
				Scene oldValue, Scene newValue) {
			// determine which anchored changed
			for (Node anchored : keys.keySet()) {
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

	private ChangeListener<Scene> anchorageSceneChangeListener = new ChangeListener<Scene>() {
		@Override
		public void changed(ObservableValue<? extends Scene> observable,
				Scene oldValue, Scene newValue) {
			if (oldValue != null) {
				// System.out.println("Try to unregister VCLs because anchorage
				// "
				// + getAnchorage() + " lost scene reference.");
				unregisterVCLs();
			}
			if (newValue != null) {
				// System.out.println("Try to register VCLs because anchorage "
				// + getAnchorage() + " obtained scene reference.");
				registerVCLs();
			}
		}
	};

	private ChangeListener<Node> anchorageChangeListener = new ChangeListener<Node>() {
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
		setAnchorage(anchorage);
	}

	@Override
	public ReadOnlyObjectProperty<Node> anchorageProperty() {
		return anchorageProperty.getReadOnlyProperty();
	}

	@Override
	public void attach(AnchorKey key, IAdaptable info) {
		Node anchored = key.getAnchored();
		if (!keys.containsKey(anchored)) {
			keys.put(anchored, new HashSet<AnchorKey>());
			anchored.sceneProperty().addListener(anchoredSceneChangeListener);
		}
		keys.get(anchored).add(key);

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
		return getAnchorage() != null && getAnchorage().getScene() != null
				&& anchored != null && anchored.getScene() != null;
	}

	/**
	 * Computes and returns the position for the given {@link AnchorKey}.
	 *
	 * @param key
	 *            The {@link AnchorKey} for which the position is computed.
	 * @return The position for the given {@link AnchorKey}.
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
				// XXX: The update has to be postponed because of a JavaFX
				// Bug in Java 7
				// (https://bugs.openjdk.java.net/browse/JDK-8124231) that
				// causes a ConcurrentModificationException when changing/ the
				// scene graph in response to scene-property changes.
				// With Java 8 this would not be necessary.
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						updatePositions(anchored);
					}
				});
			}
		};
	}

	@Override
	public void detach(AnchorKey key, IAdaptable info) {
		Node anchored = key.getAnchored();
		if (!isAttached(key)) {
			throw new IllegalArgumentException(
					"The given AnchorKey was not previously attached to this IAnchor.");
		}

		// remove from positions map so that a change event is fired when it is
		// attached again
		positionProperty.remove(key);

		// remove from keys to indicate it is detached
		keys.get(anchored).remove(key);

		// clean-up for this anchored if necessary
		if (keys.get(anchored).isEmpty()) {
			anchored.sceneProperty()
					.removeListener(anchoredSceneChangeListener);
			keys.remove(anchored);
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
	 * Returns the {@link Map} which stores the registered {@link AnchorKey}s
	 * per {@link Node} by reference.
	 *
	 * @return The {@link Map} which stores the registered {@link AnchorKey}s
	 *         per {@link Node} by reference.
	 */
	protected Map<Node, Set<AnchorKey>> getKeys() {
		return keys;
	}

	@Override
	public Point getPosition(AnchorKey key) {
		Node anchored = key.getAnchored();
		if (!keys.containsKey(anchored) || !keys.get(anchored).contains(key)) {
			throw new IllegalArgumentException(
					"The AnchorKey is not attached to this anchor.");
		}

		if (!positionProperty.containsKey(key)) {
			return null;
		}
		return positionProperty.get(key);
	}

	@Override
	public boolean isAttached(AnchorKey key) {
		return keys.containsKey(key.getAnchored())
				&& keys.get(key.getAnchored()).contains(key);
	}

	@Override
	public ReadOnlyMapProperty<AnchorKey, Point> positionProperty() {
		return positionProperty.getReadOnlyProperty();
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
	 * putting the new position into the {@link #positionProperty()}</li>
	 * </ol>
	 *
	 * @param key
	 *            The {@link AnchorKey} for which the position is updated.
	 */
	protected void updatePosition(AnchorKey key) {
		Point oldPosition = getPosition(key);
		Point newPosition = computePosition(key);
		if (oldPosition == null || !oldPosition.equals(newPosition)) {
			// TODO: we could enforce that computePosition may never return
			// null or an invalid position
			if (newPosition != null && !Double.isNaN(newPosition.x)
					&& !Double.isInfinite(newPosition.x)
					&& !Double.isNaN(newPosition.y)
					&& !Double.isInfinite(newPosition.y)) {
				positionProperty().put(key, newPosition);
			}
		}
	}

	private void updatePositions(Node anchored) {
		if (getKeys().containsKey(anchored)) {
			for (AnchorKey key : getKeys().get(anchored)) {
				updatePosition(key);
			}
		}
	}

}
