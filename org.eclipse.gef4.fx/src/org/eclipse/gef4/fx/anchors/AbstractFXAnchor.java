/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Ny??en (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.fx.anchors;

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

import org.eclipse.gef4.fx.listeners.VisualChangeListener;
import org.eclipse.gef4.geometry.planar.Point;

public abstract class AbstractFXAnchor implements IFXAnchor {

	private ReadOnlyObjectWrapper<Node> anchorageProperty = new ReadOnlyObjectWrapper<Node>();

	private ReadOnlyMapWrapper<AnchorKey, Point> positionProperty = new ReadOnlyMapWrapper<AnchorKey, Point>(
			FXCollections.<AnchorKey, Point> observableHashMap());

	private VisualChangeListener anchorageVisualChangeListener = new VisualChangeListener() {
		@Override
		protected void boundsChanged(Bounds oldBounds, Bounds newBounds) {
			recomputePositions();
		}

		@Override
		public void register(Node node, Node anyParent) {
			super.register(node, anyParent);
			/*
			 * The visual change listener is registered when the anchorage is
			 * attached to a Scene. Therefore, the anchorages
			 * bounds/transformation could have "changed" until registration, so
			 * we have to recompute anchored's positions now.
			 */
			recomputePositions();
		}

		@Override
		protected void transformChanged(Transform oldTransform,
				Transform newTransform) {
			recomputePositions();
		}
	};

	private ChangeListener<Scene> anchorageVisualSceneChangeListener = new ChangeListener<Scene>() {

		@Override
		public void changed(ObservableValue<? extends Scene> observable,
				Scene oldValue, Scene newValue) {
			if (oldValue != null) {
				anchorageVisualChangeListener.unregister();
			}
			if (newValue != null) {
				anchorageVisualChangeListener.register(getAnchorageNode(),
						newValue.getRoot());
			}
		}
	};

	private ChangeListener<Node> anchorageChangeListener = new ChangeListener<Node>() {
		@Override
		public void changed(ObservableValue<? extends Node> observable,
				Node oldAnchorage, Node newAnchorage) {
			if (oldAnchorage != null) {
				anchorageVisualChangeListener.unregister();
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
					anchorageVisualChangeListener.register(newAnchorage,
							scene.getRoot());
				}
			}
		}
	};

	public AbstractFXAnchor(Node anchorage) {
		anchorageProperty.addListener(anchorageChangeListener);
		setAnchorageNode(anchorage);
	}

	@Override
	public ReadOnlyObjectProperty<Node> anchorageNodeProperty() {
		return anchorageProperty.getReadOnlyProperty();
	}

	@Override
	public Node getAnchorageNode() {
		return anchorageProperty.get();
	}

	@Override
	public Point getPosition(AnchorKey key) {
		if (!positionProperty.containsKey(key)) {

		}
		return positionProperty.get(key);
	}

	@Override
	public ReadOnlyMapProperty<AnchorKey, Point> positionProperty() {
		return positionProperty.getReadOnlyProperty();
	}

	protected abstract void recomputePositions();

	protected void setAnchorageNode(Node anchorage) {
		anchorageProperty.set(anchorage);
	}

}
