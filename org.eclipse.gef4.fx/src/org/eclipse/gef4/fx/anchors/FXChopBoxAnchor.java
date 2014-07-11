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
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.Node;

import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;

public class FXChopBoxAnchor extends AbstractFXAnchor {

	private SimpleMapProperty<AnchorKey, Point> referencePointProperty = new SimpleMapProperty<AnchorKey, Point>(
			FXCollections.<AnchorKey, Point> observableHashMap());

	private MapChangeListener<AnchorKey, Point> referencePointChangeListener = new MapChangeListener<AnchorKey, Point>() {
		@Override
		public void onChanged(
				javafx.collections.MapChangeListener.Change<? extends AnchorKey, ? extends Point> change) {
			if (change.wasAdded()) {
				if (change.getKey() == null) {
					throw new IllegalStateException(
							"Attempt to put <null> key into reference point map!");
				}
				if (change.getValueAdded() == null) {
					throw new IllegalStateException(
							"Attempt to put <null> value into reference point map!");
				}
				recomputePosition(change.getKey(), change.getValueAdded());
			}
		}
	};

	public FXChopBoxAnchor(Node anchorage) {
		super(anchorage);
		referencePointProperty.addListener(referencePointChangeListener);
	}

	/**
	 * @param anchored
	 *            The to be anchored {@link Node} for which the anchor position
	 *            is to be determined.
	 * @param referencePoint
	 *            A reference {@link Point} used for calculation of the anchor
	 *            position, provided within the local coordinate system of the
	 *            to be anchored {@link Node}.
	 * @return Point The anchor position within the local coordinate system of
	 *         the to be anchored {@link Node}.
	 */
	public Point computePosition(Node anchored, Point referencePoint) {
		// compute intersection point between outline of anchorage reference
		// shape and line through anchorage and anchor reference points.

		AffineTransform anchorageToSceneTransform = JavaFX2Geometry
				.toAffineTransform(getAnchorageNode()
						.getLocalToSceneTransform());

		if (!isValidTransform(anchorageToSceneTransform)) {
			anchorageToSceneTransform = new AffineTransform();
		}

		AffineTransform anchoredToSceneTransform = JavaFX2Geometry
				.toAffineTransform(anchored.getLocalToSceneTransform());

		if (!isValidTransform(anchoredToSceneTransform)) {
			anchoredToSceneTransform = new AffineTransform();
		}

		Point anchorageReferencePointInScene = anchorageToSceneTransform
				.getTransformed(getAnchorageReferencePoint());
		Point anchorReferencePointInScene = anchoredToSceneTransform
				.getTransformed(referencePoint);
		Line referenceLineInScene = new Line(anchorageReferencePointInScene,
				anchorReferencePointInScene);

		IShape anchorageReferenceShapeInScene = getAnchorageReferenceShape()
				.getTransformed(anchorageToSceneTransform);

		Point[] intersectionPoints = anchorageReferenceShapeInScene
				.getOutline().getIntersections(referenceLineInScene);
		if (intersectionPoints.length > 0) {
			Point point = JavaFX2Geometry.toPoint(anchored
					.sceneToLocal(Geometry2JavaFX
							.toFXPoint(intersectionPoints[0])));
			return point;
		}

		// do not fail hard... use center
		Point point = JavaFX2Geometry.toPoint(anchored
				.sceneToLocal(Geometry2JavaFX
						.toFXPoint(anchorageReferencePointInScene)));
		return point;
	}

	/**
	 * @return The anchorage reference point within the local coordinate system
	 *         of the anchorage {@link Node}.
	 */
	protected Point getAnchorageReferencePoint() {
		return getAnchorageReferenceShape().getBounds().getCenter();
	}

	/**
	 * Returns the anchorage reference {@link IShape} which is used to compute
	 * the intersection point which is used as the anchor position. By default,
	 * a {@link Rectangle} matching the layout-bounds of the anchorage
	 * {@link Node} is returned. Clients may override this method to use other
	 * geometric shapes instead.
	 * 
	 * @return The anchorage reference {@link IShape} within the local
	 *         coordinate system of the anchorage {@link Node}
	 */
	protected IShape getAnchorageReferenceShape() {
		return JavaFX2Geometry
				.toRectangle(getAnchorageNode().getLayoutBounds());
	}

	/**
	 * @param anchored
	 * @return reference point for the given anchored
	 */
	public Point getReferencePoint(AnchorKey key) {
		return referencePointProperty.get(key);
	}

	private boolean isValidTransform(AffineTransform t) {
		for (double d : t.getMatrix()) {
			if (Double.isNaN(d)) {
				return false;
			}
		}
		return true;
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
	protected void recomputePosition(AnchorKey key, Point referencePoint) {
		Point position = computePosition(key.getAnchored(), referencePoint);
		if (!Double.isNaN(position.x) && !Double.isNaN(position.y)) {
			positionProperty().put(key, position);
		}
	}

	@Override
	public void recomputePositions() {
		ObservableMap<AnchorKey, Point> ref = referencePointProperty == null ? null
				: referencePointProperty.get();
		if (ref == null) {
			return;
		}
		AnchorKey[] keys = ref.keySet().toArray(new AnchorKey[] {});
		for (AnchorKey key : keys) {
			Point referencePoint = referencePointProperty().get(key);
			if (referencePoint != null) {
				recomputePosition(key, referencePoint);
			}
		}
	}

	/**
	 * @return property storing reference points for anchoreds (map)
	 */
	public MapProperty<AnchorKey, Point> referencePointProperty() {
		return referencePointProperty;
	}

	/**
	 * Assigns the given reference point to the given anchored in the reference
	 * point map.
	 * 
	 * @param anchored
	 * @param referencePoint
	 */
	public void setReferencePoint(AnchorKey key, Point referencePoint) {
		referencePointProperty.put(key, referencePoint);
	}

}
