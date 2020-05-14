/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 *
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
package org.eclipse.gef.fx.nodes;

import org.eclipse.gef.fx.utils.Geometry2Shape;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.BezierCurve;
import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Point;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Abstract base class for {@link IConnectionInterpolator} implementations,
 * which supports updating the geometry for an {@link IGeometry} curve node, as
 * well as arranging and clipping the decorations.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public abstract class AbstractInterpolator implements IConnectionInterpolator {

	/**
	 * Arranges the given decoration according to the passed-in values.
	 *
	 * @param decoration
	 *            The decoration {@link Node} to arrange.
	 * @param offset
	 *            The offset for the decoration visual.
	 * @param direction
	 *            The direction of the {@link Connection} at the point where the
	 *            decoration is arranged.
	 */
	protected void arrangeDecoration(Node decoration, Point offset,
			Vector direction) {
		// arrange on start of curve
		AffineTransform transform = new AffineTransform().translate(offset.x,
				offset.y);
		// arrange on curve direction
		if (!direction.isNull()) {
			Angle angleCW = new Vector(1, 0).getAngleCW(direction);
			transform.rotate(angleCW.rad(), 0, 0);
		}
		// compensate stroke (ensure decoration 'ends' at curve end).
		transform.translate(-NodeUtils.getShapeBounds(decoration).getX(), 0);
		// apply transform
		decoration.getTransforms().setAll(Geometry2FX.toFXAffine(transform));
	}

	private void arrangeEndDecoration(Node endDecoration, ICurve curve,
			Point endPoint) {
		if (endDecoration == null) {
			return;
		}

		// determine curve end point and curve end direction
		// TODO: check if we can obtain end point as curve.get(1).
		if (curve == null || endPoint == null) {
			return;
		}

		BezierCurve[] beziers = curve.toBezier();
		if (beziers.length == 0) {
			return;
		}

		BezierCurve endDerivative = beziers[beziers.length - 1].getDerivative();
		Point slope = endDerivative.get(1);
		if (slope.equals(0, 0)) {
			/*
			 * This is the case when beziers[-1] is a degenerated curve where
			 * the last control point equals the end point. As a work around, we
			 * evaluate the derivative at t = 0.99.
			 */
			slope = endDerivative.get(0.99);
		}
		Vector endDirection = new Vector(slope.getNegated());

		arrangeDecoration(endDecoration, endPoint, endDirection);
	}

	private void arrangeStartDecoration(Node startDecoration, ICurve curve,
			Point startPoint) {
		// TODO: check if we can use curve.get(0) to obtain start point

		// determine curve start point and curve start direction
		if (curve == null || startPoint == null) {
			return;
		}

		BezierCurve[] beziers = curve.toBezier();
		if (beziers.length == 0) {
			return;
		}

		BezierCurve startDerivative = beziers[0].getDerivative();
		Point slope = startDerivative.get(0);
		if (slope.equals(0, 0)) {
			/*
			 * This is the case when beziers[0] is a degenerated curve where the
			 * start point equals the first control point. As a work around, we
			 * evaluate the derivative at t = 0.01.
			 */
			slope = startDerivative.get(0.01);
		}
		Vector curveStartDirection = new Vector(slope);
		arrangeDecoration(startDecoration, startPoint, curveStartDirection);
	}

	/**
	 * Adjusts the curveClip so that the curve node does not paint through the
	 * given decoration.
	 *
	 * @param curveShape
	 *            A shape describing the {@link ICurve} geometry, which is used
	 *            for clipping.
	 *
	 * @param curveClip
	 *            A shape that represents the clip of the curve node,
	 *            interpreted in scene coordinates.
	 * @param decoration
	 *            The decoration to clip the curve node from.
	 * @return A shape representing the resulting clip, interpreted in scene
	 *         coordinates.
	 */
	protected Shape clipAtDecoration(Shape curveShape, Shape curveClip,
			Shape decoration) {
		// first intersect curve shape with decoration layout bounds,
		// then subtract the curve shape from the result, and the decoration
		// from that
		Path decorationShapeBounds = new Path(
				Geometry2Shape.toPathElements(NodeUtils
						.localToScene(decoration,
								NodeUtils.getShapeBounds(decoration))
						.toPath()));
		decorationShapeBounds.setFill(Color.RED);
		Shape clip = Shape.intersect(decorationShapeBounds, curveShape);
		clip = Shape.subtract(clip, decoration);
		clip = Shape.subtract(curveClip, clip);
		return clip;
	}

	/**
	 * Computes an {@link ICurve} geometry from the {@link Connection}'s points,
	 * which is used to update the {@link Connection#getCurve() curve node}.
	 *
	 * @param connection
	 *            The {@link Connection}, for which to compute a new
	 *            {@link ICurve} geometry.
	 * @return An {@link ICurve} that represents the to be rendered geometry.
	 */
	protected abstract ICurve computeCurve(Connection connection);

	@Override
	public void interpolate(Connection connection) {
		// compute new curve (this can lead to another refreshGeometry() call
		// which is not executed)
		ICurve newGeometry = computeCurve(connection);

		// XXX: we can only deal with geometry nodes so far
		@SuppressWarnings("unchecked")
		final GeometryNode<ICurve> curveNode = (GeometryNode<ICurve>) connection
				.getCurve();
		if (curveNode instanceof GeometryNode
				&& !newGeometry.equals(curveNode.getGeometry())) {
			// TODO: we need to prevent positions are re-calculated as a
			// result of the changed geometry. -> the static anchors should not
			// update their positions because of layout bounds changes.
			// System.out.println("New geometry: " + newGeometry);
			curveNode.setGeometry(newGeometry);
		}

		Node startDecoration = connection.getStartDecoration();
		if (startDecoration != null) {
			arrangeStartDecoration(startDecoration, newGeometry,
					newGeometry.getP1());
		}

		Node endDecoration = connection.getEndDecoration();
		if (endDecoration != null) {
			arrangeEndDecoration(endDecoration, newGeometry,
					newGeometry.getP2());
		}

		if (!newGeometry.getBounds().isEmpty()
				&& (startDecoration != null || endDecoration != null)) {
			// XXX Use scene coordinates, as the clip node does not provide a
			// parent.

			// union curve node's children's bounds-in-parent
			org.eclipse.gef.geometry.planar.Rectangle unionBoundsInCurveNode = new org.eclipse.gef.geometry.planar.Rectangle();
			ObservableList<Node> childrenUnmodifiable = curveNode
					.getChildrenUnmodifiable();
			for (Node child : childrenUnmodifiable) {
				Bounds boundsInParent = child.getBoundsInParent();
				org.eclipse.gef.geometry.planar.Rectangle rectangle = FX2Geometry
						.toRectangle(boundsInParent);
				unionBoundsInCurveNode.union(rectangle);
			}

			// convert unioned bounds to scene coordinates
			Bounds visualBounds = curveNode.localToScene(
					Geometry2FX.toFXBounds(unionBoundsInCurveNode));

			// create clip
			Shape clip = new Rectangle(visualBounds.getMinX(),
					visualBounds.getMinY(), visualBounds.getWidth(),
					visualBounds.getHeight());
			clip.setFill(Color.RED);

			// can only clip Shape decorations
			if (startDecoration != null && startDecoration instanceof Shape) {
				clip = clipAtDecoration(curveNode.getGeometricShape(), clip,
						(Shape) startDecoration);
			}
			// can only clip Shape decorations
			if (endDecoration != null && endDecoration instanceof Shape) {
				clip = clipAtDecoration(curveNode.getGeometricShape(), clip,
						(Shape) endDecoration);
			}

			// XXX: All CAG operations deliver result shapes that reflect areas
			// in scene coordinates.
			AffineTransform sceneToLocalTx = NodeUtils
					.getSceneToLocalTx(curveNode);
			clip.getTransforms().add(Geometry2FX.toFXAffine(sceneToLocalTx));

			// set clip
			curveNode.setClip(clip);
		} else {
			curveNode.setClip(null);
		}
	}
}
