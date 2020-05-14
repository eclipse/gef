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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.fx.anchors.DynamicAnchor.PreferredOrientation;
import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;

import javafx.geometry.Orientation;

/**
 * An {@link IComputationStrategy} that computes anchor position by orthogonally
 * projecting the respective anchored reference point to the outline of the
 * anchorage reference geometry so that the respective point has minimal
 * distance to the anchored reference point and resembles the same x- (vertical
 * projection) or y-coordinate (horizontal projection).
 *
 * @author anyssen
 * @author mwienand
 */
public class OrthogonalProjectionStrategy extends ProjectionStrategy {

	@Override
	protected Point computeProjectionInScene(
			List<ICurve> anchorageOutlinesInScene,
			Point anchoredReferencePointInScene, Set<Parameter<?>> parameters) {
		// obtain additionally required parameter
		PreferredOrientation parameter = Parameter.get(parameters,
				PreferredOrientation.class);
		Orientation orientationHint = parameter.get();

		Point nearestOrthogonalProjectionInScene = null;
		double nearestOrthogonalProjectionDistance = Double.MAX_VALUE;
		for (ICurve segment : anchorageOutlinesInScene) {
			// determine nearest orthogonal projection of each curve
			Point projection = getOrthogonalProjection(segment,
					anchoredReferencePointInScene, orientationHint);
			if (projection != null) {
				double distance = projection
						.getDistance(anchoredReferencePointInScene);
				if (nearestOrthogonalProjectionInScene == null
						|| distance < nearestOrthogonalProjectionDistance) {
					nearestOrthogonalProjectionInScene = projection;
					nearestOrthogonalProjectionDistance = distance;
				}
			}
		}

		if (nearestOrthogonalProjectionInScene != null) {
			return nearestOrthogonalProjectionInScene;
		} else {
			// Fall back to nearest projection
			return super.computeProjectionInScene(anchorageOutlinesInScene,
					anchoredReferencePointInScene, parameters);
		}
	}

	/**
	 * Returns a point on the {@link ICurve} for which holds that its
	 * y-coordinate is the same as that of the given reference point, and its
	 * distance to the given reference point is minimal (i.e. there is no other
	 * point with the same y-coordinate that has a smaller distance), if such a
	 * point exists.
	 *
	 * @param curve
	 *            The {@link ICurve} to test. The returned {@link Point} has to
	 *            be contained by it.
	 *
	 * @param reference
	 *            The reference point which is used to determine the distance.
	 * @return The point on the {@link ICurve} that is horizontally nearest to
	 *         the given reference point.
	 */
	private Point getHorizontalProjection(ICurve curve, Point reference) {
		// Determine points on curve with same y-coordinate; by computing a
		// line with the respective y-coordinate inside its bounds; then
		// computing the nearest intersection on the curve
		Rectangle bounds = curve.getBounds();
		Line line = new Line(bounds.getX(), reference.y,
				bounds.getX() + bounds.getWidth(), reference.y);
		Point projection = getNearestOrthogonalProjection(curve, reference,
				line);
		// a horizontal projection is constant in y, therefore, we can
		// ensure that the projection has the same y coordinate as the
		// reference
		if (projection != null) {
			projection.y = reference.y;
		}
		return projection;
	}

	private Point getNearestOrthogonalProjection(ICurve curve, Point reference,
			Line line) {
		if (curve.overlaps(line)) {
			ICurve[] overlaps = curve.getOverlaps(line);
			// XXX: All overlaps have to be lines since a line can only
			// overlap with another line. As such, it is sufficient to check
			// the start and end points of the overlaps.
			Point nearest = null;
			double distance = 0;
			for (ICurve overlap : overlaps) {
				Point currentNearest = Point.nearest(reference,
						new Point[] { overlap.getP1(), overlap.getP2() });
				double currentDistance = reference.getDistance(currentNearest);
				if (nearest == null || currentDistance < distance) {
					nearest = currentNearest;
					distance = currentDistance;
				}
			}
			return nearest;
		} else if (curve.intersects(line)) {
			Point nearest = Point.nearest(reference,
					curve.getIntersections(line));
			return nearest;
		}
		// no point found for the given y-coordinate
		return null;
	}

	/**
	 * Returns a point on the {@link ICurve} for which holds that its
	 * x-coordinate or y-coordinate is the same as that of the given reference
	 * point, and its distance to the given reference point is minimal (i.e.
	 * there is no other point with the same x-coordinate or y-coordinate that
	 * has a smaller distance).
	 *
	 * @param curve
	 *            The {@link ICurve} to test. The returned {@link Point} has to
	 *            be contained by it.
	 *
	 * @param reference
	 *            The reference point which is used to determine the distance.
	 * @param orientationHint
	 *            A preferred {@link Orientation} or <code>null</code> to
	 *            indicate no preference.
	 * @return The point on the {@link ICurve} that is horizontally or
	 *         vertically nearest to the given reference point.
	 */
	private Point getOrthogonalProjection(ICurve curve, Point reference,
			Orientation orientationHint) {
		Point nearestHorizonalProjection = getHorizontalProjection(curve,
				reference);
		if (nearestHorizonalProjection == null) {
			// if there is no horizontal projection, the vertical one has to
			// be minimal (if it exists)
			return getVerticalProjection(curve, reference);
		} else if (orientationHint == Orientation.HORIZONTAL) {
			return nearestHorizonalProjection;
		} else {
			Point nearestVerticalProjection = getVerticalProjection(curve,
					reference);
			if (nearestVerticalProjection == null) {
				// if there is no vertical projection, the horizontal one
				// has to be minimal
				return nearestHorizonalProjection;
			} else if (orientationHint == Orientation.VERTICAL) {
				return nearestVerticalProjection;
			} else {
				// compute whether horizontal or vertical is minimal
				double horizontalDistance = nearestHorizonalProjection
						.getDistance(reference);
				double verticalDistance = nearestVerticalProjection
						.getDistance(reference);
				if (horizontalDistance <= verticalDistance) {
					return nearestHorizonalProjection;
				}
				return nearestVerticalProjection;
			}
		}
	}

	@Override
	public Set<Class<? extends Parameter<?>>> getRequiredParameters() {
		Set<Class<? extends Parameter<?>>> dynamicParameters = new HashSet<>();
		dynamicParameters.addAll(super.getRequiredParameters());
		dynamicParameters.add(PreferredOrientation.class);
		return dynamicParameters;
	}

	/**
	 * Returns a point on the {@link ICurve} for which holds that its
	 * x-coordinate is the same as that of the given reference point, and its
	 * distance to the given reference point is minimal (i.e. there is no other
	 * point with the same x-coordinate that has a smaller distance), if such a
	 * point exists.
	 *
	 * @param curve
	 *            The {@link ICurve} to test. The returned {@link Point} has to
	 *            be contained by it.
	 *
	 * @param reference
	 *            The reference point which is used to determine the distance.
	 * @return The point on the {@link ICurve} that is vertically nearest to the
	 *         given reference point.
	 */
	private Point getVerticalProjection(ICurve curve, Point reference) {
		// Determine points on curve with same x-coordinate; by computing a
		// line with the respective x-coordinate inside its bounds; then
		// computing the nearest intersection on the curve
		Rectangle bounds = curve.getBounds();
		Line line = new Line(reference.x, bounds.getY(), reference.x,
				bounds.getY() + bounds.getHeight());
		Point projection = getNearestOrthogonalProjection(curve, reference,
				line);
		// a vertical projection is constant in x, therefore, we can
		// ensure that the projection has the same x coordinate as the
		// reference
		if (projection != null) {
			projection.x = reference.x;
		}
		return projection;
	}

}