/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 * Note: Parts of this class have been transferred from org.eclipse.draw2d.ManhattanConnectionRouter.
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.nodes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.anchors.DynamicAnchor.AbstractComputationStrategy;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.fx.anchors.StaticAnchor;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polyline;
import org.eclipse.gef4.geometry.planar.Rectangle;

import javafx.scene.Node;

/**
 * An {@link IConnectionRouter} that interprets the {@link Connection} control
 * points as way points and adjusts the way points (if necessary) so that the
 * {@link Connection} is routed orthogonally. Will return a {@link Polyline} as
 * rendering {@link ICurve} for the {@link Connection}.
 *
 * @author anyssen
 *
 */
// TODO: extract abstract orthogonal router (which provides the control point
// manipulation), so that a PolyBezier may be used as ICurve as well (to have
// smooth bend corners)
public class OrthogonalRouter implements IConnectionRouter {

	// private sub-class to 'mark' those way-points that are added by the router
	// (so they can be removed when re-routing)
	private class OrthogonalPolylineRouterAnchor extends StaticAnchor {
		public OrthogonalPolylineRouterAnchor(
				Point referencePositionInAnchorageLocal) {
			super(referencePositionInAnchorageLocal);
		}
	}

	/**
	 * Retrieves the geometry of the anchorage at the given index, in case the
	 * respective anchor is connected.
	 *
	 * @param connection
	 *            The connection which is connected.
	 * @param index
	 *            The index of the anchor whose anchorage geometry is to be
	 *            retrieved.
	 * @return A geometry resembling the anchorage reference geometry of the
	 *         anchor at the given index, or <code>null</code> if the anchor is
	 *         not connected.
	 */
	protected IGeometry getAnchorageGeometry(Connection connection, int index) {
		IAnchor anchor = index == 0 ? connection.getStartAnchor()
				: (index == connection.getPoints().size() - 1
						? connection.getEndAnchor()
						: connection.getControlAnchor(index - 1));
		// TODO: use connection methods to detect whether anchors are connected,
		// don't duplicate logic here
		if (anchor != null && anchor.getAnchorage() != null
				&& anchor.getAnchorage() != connection) {
			Node anchorage = anchor.getAnchorage();
			return NodeUtils.sceneToLocal(connection, NodeUtils.localToScene(
					anchorage,
					AbstractComputationStrategy.getOutlineGeometry(anchorage)));
		}
		return null;
	}

	private Point getAnchorReferencePoint(Connection connection, int index) {
		if (index < 0 || index >= connection.getPoints().size()) {
			throw new IndexOutOfBoundsException();
		}

		IGeometry referenceGeometry = null;
		int referenceIndex = index < connection.getPoints().size() - 1
				? index + 1 : index - 1;
		referenceGeometry = getAnchorageGeometry(connection, referenceIndex);
		if (referenceGeometry != null) {
			IGeometry geometry = getAnchorageGeometry(connection, index);
			if (geometry != null) {
				// XXX: if index and reference index both point to anchors that
				// use a reference geometry, we have to compute a horizontal or
				// vertical projection between both geometries (if existent)
				// before falling back.
				Rectangle bounds = geometry.getBounds();
				Rectangle refBounds = referenceGeometry.getBounds();

				double x1 = Math.max(bounds.getX(), refBounds.getX());
				double x2 = Math.min(bounds.getX() + bounds.getWidth(),
						refBounds.getX() + refBounds.getWidth());
				if (x1 <= x2) {
					// vertical overlap => return horizontally stable position
					return new Point(x1 + (x2 - x1) / 2,
							refBounds.getY() > bounds.getY()
									+ bounds.getHeight() ? refBounds.getY()
											: refBounds.getY()
													+ refBounds.getHeight());
				}

				double y1 = Math.max(bounds.getY(), refBounds.getY());
				double y2 = Math.min(bounds.getY() + bounds.getHeight(),
						refBounds.getY() + refBounds.getHeight());
				if (y1 <= y2) {
					// horizontal overlap => return vertically stable position
					return new Point(
							refBounds.getX() > bounds.getX() + bounds.getWidth()
									? refBounds.getX()
									: refBounds.getX() + refBounds.getWidth(),
							y1 + (y2 - y1) / 2);
				}
			}
			// TODO: revise handling of this case
			// fallback to nearest bounds projection
			return DynamicAnchor.OrthogonalProjectionStrategy
					.getNearestBoundsProjection(referenceGeometry,
							connection.getPoint(index));
		}
		return connection.getPoint(referenceIndex);
	}

	private Vector getDirection(Connection connection, int i) {
		return new Vector(getPosition(connection, i),
				getPosition(connection, i + 1));
	}

	private Point getPosition(Connection connection, int index) {
		int numPoints = connection.getPoints().size();
		if (index == 0 || index == numPoints - 1) {
			IAnchor anchor = index == 0 ? connection.getStartAnchor()
					: (index == numPoints - 1 ? connection.getEndAnchor()
							: connection.getControlAnchor(index - 1));
			if (anchor == null) {
				return connection.getPoint(index);
			}
			// XXX: If we have an anchor, we have to use it to compute the
			// position. To obtain a stable position, we have to provide our own
			// (stable) reference point.
			if (anchor instanceof DynamicAnchor) {
				// TODO: maybe we can generalize this (so we do not have to
				// perform an instance test
				Point referencePoint = getAnchorReferencePoint(connection,
						index);

				// update reference point for the anchor key at the given index
				connection.referencePointProperty()
						.put(connection.getAnchorKey(index), referencePoint);

				System.out.println("ref point: " + referencePoint);
				Point computePosition = ((DynamicAnchor) anchor)
						.computePosition(connection, referencePoint);
				System.out.println("computed position = " + computePosition);
				return computePosition;
			}
		}
		return connection.getPoint(index);
	}

	@Override
	public void route(Connection connection) {
		// XXX: Route may be invoked multiple times until the anchor positions
		// are property computed (because transforms change, etc.); we need to
		// clean those points we have inserted before to guarantee that we only
		// do 'minimal' routing
		Point[] points = connection.getPoints().toArray(new Point[] {});
		if (points == null || points.length < 2) {
			return;
		}

		System.out.println("input: " + Arrays.asList(points));

		// remove way points added through a preceding routing step
		int pointsRemoved = 0;
		List<IAnchor> controlAnchors = connection.getControlAnchors();
		for (int i = 0; i < controlAnchors.size(); i++) {
			if (controlAnchors
					.get(i) instanceof OrthogonalPolylineRouterAnchor) {
				connection.removeControlAnchor(i - pointsRemoved);
				pointsRemoved++;
			}
		}
		// System.out.println("route " + connection.getPoints());

		// The router will respect the connection's anchors already provided
		// and will add control anchors were needed. It will proceed all anchors
		// from start to end and compute the respective direction to the next
		// anchor. The start and end anchors are treated specifically in case
		// they are connected, because the automatic position computation of the
		// anchor has to be ignored here.
		Map<Integer, Point> pointsToInsert = new HashMap<>();
		Vector previousDirection = null;
		Vector currentDirection = null;
		for (int i = 0; i < connection.getPoints().size() - 1; i++) {
			Point currentPoint = getPosition(connection, i);
			// direction between previous way point and current one
			previousDirection = currentDirection;
			// compute the direction between the current reference
			// geometry/point and the next one
			currentDirection = getDirection(connection, i);
			System.out.println("direction: " + currentDirection);

			// given the direction, determine if points have to be added
			if (!currentDirection.isHorizontal()
					&& !currentDirection.isVertical()) {
				if (previousDirection == null) {
					// move horizontally first
					// TODO only start to left/right when we have to move in
					// that direction
					// -> if the direction points completely up, start up/down
					// -> we also have to compensate the 'offset' of the anchor
					// here -> 0.2 .. 0.8 of parameter
					// TODO: maybe this is not the best approach
					Point pointToInsert = currentPoint
							.getTranslated(currentDirection.x, 0);
					pointsToInsert.put(i + 1, pointToInsert);
					currentDirection = new Vector(0, currentDirection.y);
				} else {
					// adjust by inserting a control point; try to follow
					// previous
					// direction as long as possible
					if (previousDirection.isHorizontal()) {
						if (previousDirection.x < 0 && currentDirection.x < 0
								|| previousDirection.x > 0
										&& currentDirection.x > 0) {
							// prolong current direction horizontally
							Point pointToInsert = currentPoint
									.getTranslated(currentDirection.x, 0);
							pointsToInsert.put(i + 1, pointToInsert);
							currentDirection = new Vector(0,
									currentDirection.y);
						} else {
							// move up/down first
							Point pointToInsert = currentPoint.getTranslated(0,
									currentDirection.y);
							pointsToInsert.put(i + 1, pointToInsert);
							currentDirection = new Vector(currentDirection.x,
									0);
						}
					} else {
						if (previousDirection.y < 0 && currentDirection.y < 0
								|| previousDirection.y > 0
										&& currentDirection.y > 0) {
							// prolong current direction vertically
							Point pointToInsert = currentPoint.getTranslated(0,
									currentDirection.y);
							pointsToInsert.put(i + 1, pointToInsert);
							currentDirection = new Vector(currentDirection.x,
									0);
						} else {
							// move left/right first
							Point pointToInsert = currentPoint
									.getTranslated(currentDirection.x, 0);
							pointsToInsert.put(i + 1, pointToInsert);
							currentDirection = new Vector(0,
									currentDirection.y);
						}
					}
				}
			}
		}

		int pointsInserted = 0;
		for (int insertionIndex : pointsToInsert.keySet()) {
			// XXX: we need to keep track of those way points inserted here, so
			// we can remove them in a succeeding routing pass
			connection.addControlAnchor(insertionIndex + pointsInserted - 1,
					new OrthogonalPolylineRouterAnchor(
							pointsToInsert.get(insertionIndex)));
			pointsInserted++;
		}

		// TODO: optimize (we could remove points that now lie within a single
		// segment (i.e. two adjacent segments in same direction (but this would
		// not be 'minimal' -> should be done by bend policy!

		// return a polyline through the points
		System.out.println("routed: " + Arrays
				.asList(connection.getPoints().toArray(new Point[] {})));
	}
}
