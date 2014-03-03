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

import javafx.beans.property.MapProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

import org.eclipse.gef4.fx.listener.VisualChangeListener;
import org.eclipse.gef4.geometry.planar.Point;

public abstract class AbstractFXNodeAnchor implements IFXNodeAnchor {

	// FIXME: read-only
	private ReadOnlyObjectWrapper<Node> anchorageProperty = new ReadOnlyObjectWrapper<Node>();
//	private SimpleObjectProperty<Node> anchorageProperty = new SimpleObjectProperty<Node>();

	// FIXME: inline (trouble with generics)
	private ObservableMap<Node, Point> _referencePointMap = FXCollections.observableHashMap();
	private SimpleMapProperty<Node, Point> referencePointProperty = new SimpleMapProperty<Node, Point>(_referencePointMap);

	// FIXME: inline (trouble with generics)
	private ObservableMap<Node, Point> _positionMap = FXCollections.observableHashMap();
	private ReadOnlyMapWrapper<Node, Point> positionProperty = new ReadOnlyMapWrapper<Node, Point>(_positionMap);

	private VisualChangeListener anchorageVisualListener = new VisualChangeListener() {
		@Override
		protected void transformChanged(Transform oldTransform,
				Transform newTransform) {
			recomputePositions();
		}

		@Override
		protected void boundsChanged(Bounds oldBounds, Bounds newBounds) {
			recomputePositions();
		}
	};

	private void recomputePositions() {
		for (Node anchored : referencePointProperty.get().keySet()) {
			recomputePosition(anchored, referencePointProperty.get(anchored));
		}
	}

	private void recomputePosition(Node anchored, Point referencePoint) {
		positionProperty.put(anchored,
				computePosition(anchored, referencePoint));
	}

	private ChangeListener<Node> anchorageChangeListener = new ChangeListener<Node>() {
		@Override
		public void changed(ObservableValue<? extends Node> observable,
				Node oldAnchorage, Node newAnchorage) {
			if (oldAnchorage != null) {
				unregisterLayoutListener(oldAnchorage);
			}
			if (newAnchorage != null) {
				registerLayoutListeners(newAnchorage);
			}
		}
	};

	private MapChangeListener<Node, Point> referencePointChangeListener = new MapChangeListener<Node, Point>() {
		@Override
		public void onChanged(
				javafx.collections.MapChangeListener.Change<? extends Node, ? extends Point> change) {
			recomputePosition(change.getKey(), change.getValueAdded());
		}
	};

	public AbstractFXNodeAnchor(Node anchorage) {
		anchorageProperty.addListener(anchorageChangeListener);
		referencePointProperty.addListener(referencePointChangeListener);
		setAnchorage(anchorage);
	}

	@Override
	public ReadOnlyObjectProperty<Node> anchorageProperty() {
		return anchorageProperty.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyMapProperty<Node, Point> positionProperty() {
		return positionProperty.getReadOnlyProperty();
	}

	@Override
	public MapProperty<Node, Point> referencePointProperty() {
		return referencePointProperty;
	}

	@Override
	public void setReferencePoint(Node anchored, Point referencePoint) {
		referencePointProperty.put(anchored, referencePoint);
	}

	@Override
	public Point getReferencePoint(Node anchored) {
		return referencePointProperty.get(anchored);
	}

	protected void setAnchorage(Node anchorage) {
		anchorageProperty.set(anchorage);
	}

	public Node getAnchorage() {
		return anchorageProperty.get();
	}

	@Override
	public Point getPosition(Node anchored) {
		return positionProperty.get(anchored);
	}

	private void registerLayoutListeners(Node anchorageOrAnchored) {
		anchorageVisualListener.register(anchorageOrAnchored,
				anchorageOrAnchored.getScene().getRoot());
	}

	private void unregisterLayoutListener(Node anchorageOrAnchored) {
		anchorageVisualListener.unregister();
	}

}
