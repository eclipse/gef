/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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

import java.util.List;
import java.util.Set;

import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchorageReferenceGeometry;
import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchoredReferencePoint;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.Ellipse;
import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.IShape;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Path;
import org.eclipse.gef.geometry.planar.Point;

import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * A specific projection strategy that is based on a center-projection of the
 * given reference point.
 *
 * @author anyssen
 * @author mwienand
 */
public class ChopBoxStrategy extends ProjectionStrategy {

	/**
	 * Computes the anchorage reference position within the coordinate system of
	 * the given {@link IGeometry}. Will return the center of a {@link IShape}
	 * or {@link Path} geometry, if it is contained within the shape or path.
	 * Will return <code>null</code> otherwise to indicate that the computation
	 * should fall back to the nearest projection on the anchorage geometry
	 * outline.
	 *
	 * @param anchorage
	 *            The anchorage visual.
	 * @param geometryInLocal
	 *            The anchorage geometry within the local coordinate system of
	 *            the anchorage visual.
	 * @param anchoredReferencePointInAnchorageLocal
	 *            Reference point of the anchored for which to determine the
	 *            anchorage reference point. Within the local coordinate system
	 *            of the anchorage.
	 * @return A position within the given {@link IGeometry}, or
	 *         <code>null</code> if the computation should rather fall back to
	 *         the nearest projection.
	 */
	protected Point computeAnchorageReferencePointInLocal(Node anchorage,
			IGeometry geometryInLocal,
			Point anchoredReferencePointInAnchorageLocal) {
		if (geometryInLocal instanceof IShape) {
			IShape shape = (IShape) geometryInLocal;
			// in case of an IShape we can pick the bounds center if it
			// is contained, or the vertex nearest to the center point
			Point boundsCenterInLocal = geometryInLocal.getBounds().getCenter();
			if (shape.contains(boundsCenterInLocal)) {
				return boundsCenterInLocal;
			}
		} else if (geometryInLocal instanceof Path) {
			// in case of a Path we can pick the vertex nearest
			// to the center point
			Point boundsCenterInLocal = geometryInLocal.getBounds().getCenter();
			if (geometryInLocal.contains(boundsCenterInLocal)) {
				return boundsCenterInLocal;
			}
		}
		return null;
	}

	/**
	 * Computes the anchorage reference position in scene coordinates, based on
	 * the given anchorage geometry.
	 *
	 * @see #computeAnchorageReferencePointInLocal(Node, IGeometry, Point)
	 * @param anchorage
	 *            The anchorage visual.
	 * @param geometryInLocal
	 *            The anchorage geometry within the coordinate system of the
	 *            anchorage visual.
	 * @param anchoredReferencePointInScene
	 *            The reference {@link Point} of the anchored for which the
	 *            anchorage reference {@link Point} is to be determined.
	 * @return The anchorage reference position in scene coordinates or
	 *         <code>null</code> if the computation should rather fall back to
	 *         the nearest projection.
	 */
	protected Point computeAnchorageReferencePointInScene(Node anchorage,
			IGeometry geometryInLocal, Point anchoredReferencePointInScene) {
		Point2D anchoredReferencePointInAnchorageLocal = anchorage.sceneToLocal(
				anchoredReferencePointInScene.x,
				anchoredReferencePointInScene.y);
		Point anchorageReferencePointInLocal = computeAnchorageReferencePointInLocal(
				anchorage, geometryInLocal,
				new Point(anchoredReferencePointInAnchorageLocal.getX(),
						anchoredReferencePointInAnchorageLocal.getY()));
		if (anchorageReferencePointInLocal == null) {
			return null;
		}
		return NodeUtils.localToScene(anchorage,
				anchorageReferencePointInLocal);
	}

	@Override
	public Point computePositionInScene(Node anchorage, Node anchored,
			Set<Parameter<?>> parameters) {

		// obtain required computation parameters
		IGeometry anchorageReferenceGeometryInLocal = Parameter
				.get(parameters, AnchorageReferenceGeometry.class).get();
		Point anchoredReferencePointInLocal = Parameter
				.get(parameters, AnchoredReferencePoint.class).get();

		Point anchoredReferencePointInScene = NodeUtils.localToScene(anchored,
				anchoredReferencePointInLocal);

		Point anchorageReferencePointInScene = computeAnchorageReferencePointInScene(
				anchorage, anchorageReferenceGeometryInLocal,
				anchoredReferencePointInScene);

		if (anchorageReferencePointInScene == null) {
			return super.computePositionInScene(anchorage, anchored,
					parameters);
		}

		Line referenceLineInScene = new Line(anchorageReferencePointInScene,
				anchoredReferencePointInScene);

		IGeometry anchorageGeometryInScene = NodeUtils.localToScene(anchorage,
				anchorageReferenceGeometryInLocal);

		if (anchorageGeometryInScene instanceof Ellipse) {
			// we optimize for Ellipse here, as its computation can be
			// significantly speeded up
			Point[] intersections = ((Ellipse) anchorageGeometryInScene)
					.getIntersections(referenceLineInScene);
			if (intersections.length > 0) {
				return Point.nearest(anchoredReferencePointInScene,
						intersections);
			} else {
				return intersections[0];
			}
		} else {
			List<ICurve> anchorageOutlinesInScene = getOutlineSegments(
					anchorageGeometryInScene);
			Point nearestProjectionInScene = null;
			double nearestDistance = 0d;
			for (ICurve anchorageOutlineInScene : anchorageOutlinesInScene) {
				// if the reference point is already on the outline, we may
				// directly use it
				if (anchorageOutlineInScene
						.contains(anchoredReferencePointInScene)) {
					return anchoredReferencePointInScene;
				}
				Point[] intersections = anchorageOutlineInScene
						.getIntersections(referenceLineInScene);
				if (intersections.length > 0) {
					Point nearestIntersection = Point.nearest(
							anchoredReferencePointInScene, intersections);
					double distance = anchoredReferencePointInScene
							.getDistance(nearestIntersection);
					if (nearestProjectionInScene == null
							|| distance < nearestDistance) {
						nearestProjectionInScene = nearestIntersection;
						nearestDistance = distance;
					}
				}
			}
			if (nearestProjectionInScene != null) {
				return nearestProjectionInScene;
			}
		}

		return super.computePositionInScene(anchorage, anchored, parameters);
	}
}