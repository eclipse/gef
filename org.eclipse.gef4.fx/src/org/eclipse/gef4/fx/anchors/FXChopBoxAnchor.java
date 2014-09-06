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

// TODO: Find an appropriate name for this (outline anchor or shape anchor or perimeter anchor)
//       It has nothing to do with a ChopBox, so this does not seem to be intuitive.
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
	 * Computes the point of intersection between the outline of the anchorage
	 * reference shape and the line through the reference points of anchorage
	 * and anchored.
	 *
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
		/*
		 * The reference shapes/lines/points have to be transformed into the
		 * same coordinate system in order to be able to compute the correct
		 * intersection. We choose the scene coordinate system here. Therefore,
		 * we need access to a local-to-scene-transform for the anchorage and
		 * the anchored.
		 * 
		 * Important: JavaFX Node provides a (lazily computed)
		 * local-to-scene-transform property which we could access to get that
		 * transform. Unfortunately, this property is not updated correctly,
		 * i.e. its value can differ from the actual local-to-scene-transform.
		 * This is reflected in the different values of a) the
		 * Node#localToScene(...) method, and b) transforming using the
		 * concatenated local-to-parent-transforms.
		 * 
		 * Therefore, we compute the local-to-scene-transform for anchorage and
		 * anchored by concatenating the local-to-parent-transforms in the
		 * hierarchy, respectively.
		 */
		AffineTransform anchorageToSceneTransform = getLocalToSceneTx(getAnchorage());
		if (!isValidTransform(anchorageToSceneTransform)) {
			anchorageToSceneTransform = new AffineTransform();
		}

		AffineTransform anchoredToSceneTransform = getLocalToSceneTx(anchored);
		if (!isValidTransform(anchoredToSceneTransform)) {
			anchoredToSceneTransform = new AffineTransform();
		}

		// transform into scene coordinates
		Point anchorageReferencePointInScene = anchorageToSceneTransform
				.getTransformed(getAnchorageReferencePoint());
		Point anchoredReferencePointInScene = anchoredToSceneTransform
				.getTransformed(referencePoint);
		IShape anchorageReferenceShapeInScene = getAnchorageReferenceShape()
				.getTransformed(anchorageToSceneTransform);

		// construct reference line
		Line referenceLineInScene = new Line(anchorageReferencePointInScene,
				anchoredReferencePointInScene);

		// compute intersection
		Point nearestIntersectionInScene = anchorageReferenceShapeInScene
				.getOutline().getNearestIntersection(referenceLineInScene,
						anchoredReferencePointInScene);
		if (nearestIntersectionInScene != null) {
			// transform to anchored coordinate system
			return JavaFX2Geometry.toPoint(anchored
					.sceneToLocal(Geometry2JavaFX
							.toFXPoint(nearestIntersectionInScene)));
		}

		// do not fail hard... use center
		return JavaFX2Geometry.toPoint(anchored.sceneToLocal(Geometry2JavaFX
				.toFXPoint(anchorageReferencePointInScene)));
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
		return JavaFX2Geometry.toRectangle(getAnchorage().getLayoutBounds());
	}

	/**
	 * Concatenates the local-to-parent transforms of the given
	 *
	 * @param node
	 * @return
	 */
	private AffineTransform getLocalToSceneTx(Node node) {
		AffineTransform tx = JavaFX2Geometry.toAffineTransform(node
				.getLocalToParentTransform());
		Node tmp = node;
		while (tmp.getParent() != null) {
			tmp = tmp.getParent();
			tx = JavaFX2Geometry.toAffineTransform(
					tmp.getLocalToParentTransform()).concatenate(tx);
		}
		return tx;
	}

	/**
	 * @param key
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
	 * @param key
	 * @param referencePoint
	 */
	protected void recomputePosition(AnchorKey key, Point referencePoint) {
		Point old = getPosition(key);
		Point position = computePosition(key.getAnchored(), referencePoint);
		if (!position.equals(old)) {
			if (!Double.isNaN(position.x) && !Double.isNaN(position.y)) {
				positionProperty().put(key, position);
			}
		}
	}

	@Override
	public void recomputePositions(Node anchored) {
		ObservableMap<AnchorKey, Point> ref = referencePointProperty == null ? null
				: referencePointProperty.get();
		if (ref == null) {
			return;
		}
		for (AnchorKey key : getKeys().get(anchored)) {
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
	 * @param key
	 * @param referencePoint
	 */
	public void setReferencePoint(AnchorKey key, Point referencePoint) {
		referencePointProperty.put(key, referencePoint);
	}

}
