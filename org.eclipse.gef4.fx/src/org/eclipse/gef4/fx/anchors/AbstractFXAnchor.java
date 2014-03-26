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
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

import org.eclipse.gef4.fx.listeners.VisualChangeListener;
import org.eclipse.gef4.geometry.planar.Point;

public abstract class AbstractFXAnchor implements IFXAnchor {
	
	private ReadOnlyObjectWrapper<Node> anchorageProperty = new ReadOnlyObjectWrapper<Node>();

	private SimpleMapProperty<Node, Point> referencePointProperty = new SimpleMapProperty<Node, Point>(
			FXCollections.<Node, Point> observableHashMap());

	private ReadOnlyMapWrapper<Node, Point> positionProperty = new ReadOnlyMapWrapper<Node, Point>(
			FXCollections.<Node, Point> observableHashMap());

	private MapChangeListener<Node, Point> referencePointChangeListener = new MapChangeListener<Node, Point>() {
		@Override
		public void onChanged(
				javafx.collections.MapChangeListener.Change<? extends Node, ? extends Point> change) {
			recomputePosition(change.getKey(), change.getValueAdded());
		}
	};
	
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

	@Override
	public void recomputePositions() {
		for (Node anchored : referencePointProperty().get().keySet()) {
			recomputePosition(anchored, referencePointProperty().get(anchored));
		}
	}
	
	public ReadOnlyObjectProperty<Node> anchorageNodeProperty() {
		return anchorageProperty.getReadOnlyProperty();
	}
	
	protected void setAnchorageNode(Node anchorage) {
		anchorageProperty.set(anchorage);
	}
	
	public Node getAnchorageNode() {
		return anchorageProperty.get();
	}
	
	private ChangeListener<Node> anchorageChangeListener = new ChangeListener<Node>() {
		@Override
		public void changed(ObservableValue<? extends Node> observable,
				Node oldAnchorage, Node newAnchorage) {
			if (oldAnchorage != null) {
				anchorageVisualListener.unregister();
			}
			if (newAnchorage != null) {
				anchorageVisualListener.register(newAnchorage, newAnchorage.getScene().getRoot());
			}
		}
	};
	
	public AbstractFXAnchor(Node anchorage) {
		referencePointProperty.addListener(referencePointChangeListener);
		anchorageProperty.addListener(anchorageChangeListener);
		setAnchorageNode(anchorage);
	}

	/**
	 * Recomputes the position of this anchor w.r.t. the given anchored
	 * {@link Node} and reference {@link Point}. The
	 * {@link #computePosition(Node, Point)} method is used to determine the new
	 * position, which in turn is put into the {@link #positionProperty()}.
	 * 
	 * @param anchored
	 * @param referencePoint
	 */
	protected void recomputePosition(Node anchored, Point referencePoint) {
		positionProperty.put(anchored,
				computePosition(anchored, referencePoint));
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

	@Override
	public Point getPosition(Node anchored) {
		return positionProperty.get(anchored);
	}

}
