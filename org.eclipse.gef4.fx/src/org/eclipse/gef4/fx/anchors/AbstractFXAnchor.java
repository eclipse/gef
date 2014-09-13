/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.fx.listeners.VisualChangeListener;
import org.eclipse.gef4.geometry.planar.Point;

public abstract class AbstractFXAnchor implements IFXAnchor {

	private ReadOnlyObjectWrapper<Node> anchorageProperty = new ReadOnlyObjectWrapper<Node>();
	private ReadOnlyMapWrapper<AnchorKey, Point> positionProperty = new ReadOnlyMapWrapper<AnchorKey, Point>(
			FXCollections.<AnchorKey, Point> observableHashMap());
	private Map<Node, Set<AnchorKey>> keys = new HashMap<Node, Set<AnchorKey>>();
	private Map<Node, VisualChangeListener> vcls = new HashMap<Node, VisualChangeListener>();
	private Set<Node> registerLater = new HashSet<Node>();

	private ChangeListener<Scene> anchorageVisualSceneChangeListener = new ChangeListener<Scene>() {
		@Override
		public void changed(ObservableValue<? extends Scene> observable,
				Scene oldValue, Scene newValue) {
			if (oldValue != null) {
				unregisterVCLs();
			}
			if (newValue != null) {
				registerVCLs();
			}
		}
	};

	private ChangeListener<Node> anchorageChangeListener = new ChangeListener<Node>() {
		@Override
		public void changed(ObservableValue<? extends Node> observable,
				Node oldAnchorage, Node newAnchorage) {
			if (oldAnchorage != null) {
				unregisterVCLs();
				oldAnchorage.sceneProperty().removeListener(
						anchorageVisualSceneChangeListener);
			}
			if (newAnchorage != null) {
				// register listener on scene property, so we can react to
				// changes of the scene property of the anchorage node
				newAnchorage.sceneProperty().addListener(
						anchorageVisualSceneChangeListener);
				// if scene is already set, register anchorage visual listener
				// directly (else do this within scene change listener)
				Scene scene = newAnchorage.getScene();
				if (scene != null) {
					registerVCLs();
				}
			}
		}
	};

	public AbstractFXAnchor(Node anchorage) {
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
		}
		keys.get(anchored).add(key);

		if (!vcls.containsKey(anchored)) {
			VisualChangeListener vcl = createVCL(anchored);
			vcls.put(anchored, vcl);
			if (canRegister(anchored)) {
				vcl.register(anchorageProperty.get(), anchored);
			} else {
				registerLater(anchored);
			}
		}

		recomputePosition(key);
	}

	private boolean canRegister(Node anchored) {
		return getAnchorage() != null && getAnchorage().getScene() != null
				&& anchored != null && anchored.getScene() != null;
	}

	private VisualChangeListener createVCL(final Node anchored) {
		return new VisualChangeListener() {
			@Override
			protected void boundsInLocalChanged(Bounds oldBounds,
					Bounds newBounds) {
				recomputePositions(anchored);
			}

			@Override
			protected void localToParentTransformChanged(Node observed,
					Transform oldTransform, Transform newTransform) {
				recomputePositions(anchored);
			}

			@Override
			public void register(Node observed, Node observer) {
				super.register(observed, observer);
				/*
				 * The visual change listener is registered when the anchorage
				 * is attached to a Scene. Therefore, the anchorages
				 * bounds/transformation could have "changed" until
				 * registration, so we have to recompute anchored's positions
				 * now.
				 */
				recomputePositions(anchored);
			}
		};
	}

	@Override
	public void detach(AnchorKey key, IAdaptable info) {
		Node anchored = key.getAnchored();
		if (!isAttached(key)) {
			throw new IllegalArgumentException(
					"The given AnchorKey was not previously attached to this IFXAnchor.");
		}

		keys.get(anchored).remove(key);

		if (keys.get(anchored).isEmpty()) {
			keys.remove(anchored);
			VisualChangeListener vcl = vcls.remove(anchored);
			// unregister if currently registered
			if (vcl.isRegistered()) {
				vcl.unregister();
			}
		}
	}

	@Override
	public Node getAnchorage() {
		return anchorageProperty.get();
	}

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

	protected abstract void recomputePosition(AnchorKey key);

	protected abstract void recomputePositions(Node anchored);

	private void registerLater(final Node anchored) {
		if (registerLater.contains(anchored)) {
			return;
		}
		registerLater.add(anchored);

		ChangeListener<Scene> changeListener = new ChangeListener<Scene>() {
			@Override
			public void changed(ObservableValue<? extends Scene> observed,
					Scene oldScene, Scene newScene) {
				if (getAnchorage() == null || getAnchorage().getScene() == null) {
					return;
				}
				VisualChangeListener vcl = vcls.get(anchored);
				if (vcl == null) {
					return;
				}
				if (oldScene != null) {
					if (vcl.isRegistered()) {
						registerLater.remove(anchored);
						vcl.unregister();
					}
				}
				if (newScene != null) {
					if (!vcl.isRegistered()) {
						vcl.register(getAnchorage(), anchored);
						observed.removeListener(this);
					}
				}
			}
		};
		anchored.sceneProperty().addListener(changeListener);
	}

	protected void registerVCLs() {
		for (Node anchored : vcls.keySet().toArray(new Node[] {})) {
			if (canRegister(anchored)) {
				vcls.get(anchored).register(getAnchorage(), anchored);
			} else {
				registerLater(anchored);
			}
		}
	}

	protected void setAnchorage(Node anchorage) {
		anchorageProperty.set(anchorage);
	}

	protected void unregisterVCLs() {
		for (Node anchored : vcls.keySet().toArray(new Node[] {})) {
			vcls.get(anchored).unregister();
		}
	}

}
