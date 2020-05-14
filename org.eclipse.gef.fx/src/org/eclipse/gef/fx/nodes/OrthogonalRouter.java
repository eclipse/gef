/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
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

import java.util.List;

import org.eclipse.gef.fx.anchors.AnchorKey;
import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchoredReferencePoint;
import org.eclipse.gef.fx.anchors.DynamicAnchor.PreferredOrientation;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polygon;
import org.eclipse.gef.geometry.planar.Rectangle;

import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * An {@link IConnectionRouter} that interprets the {@link Connection} control
 * points as way points and adjusts the way points (if necessary) so that the
 * {@link Connection} is routed orthogonally.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class OrthogonalRouter extends AbstractRouter {

	private static final double OFFSET = 15;

	/**
	 * Iterates the connection's points starting at the first candidate index (
	 * <i>anchorIndex</i> + <i>step</i>) and stepping by the given step. Returns
	 * the index of the first point that is not contained within the given
	 * anchorage geometry. If all points are contained within the given
	 * anchorage geometry, the first reference candidate (i.e.
	 * <i>anchorIndex</i> + <i>step</i>) is returned.
	 *
	 * @param connection
	 *            The connection.
	 * @param anchorIndex
	 *            The start index within the connection's points.
	 * @param anchorageGeometry
	 *            The anchorage geometry.
	 * @param step
	 *            The step that is used to iterate the connection's points.
	 * @return The index of the first point that is not contained within the
	 *         anchorage geometry.
	 */
	private int findReferenceIndex(List<Point> points, int anchorIndex,
			IGeometry anchorageGeometry, int step) {
		int startIndex = anchorIndex + step;
		for (int i = startIndex; step < 0 ? i >= 0
				: i < points.size(); i += step) {
			Point point = points.get(i);
			if (!anchorageGeometry.contains(point)) {
				return i;
			}
		}
		return startIndex;
	}

	/**
	 * Returns the reference point for the anchor at the given index.
	 *
	 * @param points
	 *            The {@link Connection} that is currently routed.
	 * @param index
	 *            The index specifying the anchor for which to provide a
	 *            reference point.
	 * @return The reference point for the anchor at the given index in the
	 *         local coordinate system of the anchored, which is the
	 *         connection's curve.
	 */
	@Override
	protected Point getAnchoredReferencePoint(List<Point> points, int index) {
		if (index < 0 || index >= points.size()) {
			throw new IndexOutOfBoundsException();
		}
		Connection connection = getConnection();
		IGeometry geometry = getAnchorageGeometry(index);
		int referenceIndex = findReferenceIndex(points, index, geometry,
				index < points.size() - 1 ? 1 : -1);
		IGeometry referenceGeometry = getAnchorageGeometry(referenceIndex);
		if (referenceGeometry != null) {
			if (geometry != null) {
				// find opposite reference index
				int oppositeReferenceIndex = findReferenceIndex(points,
						index == 0 ? points.size() - 1 : 0, geometry,
						index < points.size() - 1 ? -1 : 1);
				if (getAnchorageGeometry(oppositeReferenceIndex) == null) {
					return points.get(oppositeReferenceIndex);
				}

				// XXX: if a position hint is supplied for the current index,
				// return that hint as the reference point.
				if (index == 0) {
					Point startPointHint = connection.getStartPointHint();
					if (startPointHint != null) {
						return startPointHint;
					}
				} else if (index == points.size() - 1) {
					Point endPointHint = connection.getEndPointHint();
					if (endPointHint != null) {
						return endPointHint;
					}
				}

				// XXX: if index and reference index both point to anchors that
				// use a reference geometry, we have to compute a horizontal or
				// vertical projection between both geometries (if existent)
				// before falling back to the super strategy.
				Rectangle bounds = geometry.getBounds();
				Rectangle refBounds = referenceGeometry.getBounds();

				double x1 = Math.max(bounds.getX(), refBounds.getX());
				double x2 = Math.min(bounds.getX() + bounds.getWidth(),
						refBounds.getX() + refBounds.getWidth());
				if (x1 <= x2) {
					// horizontal overlap => return vertically stable position
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
					// vertical overlap => return horizontally stable position
					return new Point(
							refBounds.getX() > bounds.getX() + bounds.getWidth()
									? refBounds.getX()
									: refBounds.getX() + refBounds.getWidth(),
							y1 + (y2 - y1) / 2);
				}
				// fallback to nearest bounds projection
				// TODO: revise handling of this case -> we could optimize this
				// by providing a desired direction
				return getNearestBoundsProjection(referenceGeometry,
						geometry.getBounds().getCenter());
			}
		}
		return points.get(referenceIndex);
	}

	private Point getNearestBoundsProjection(IGeometry g, Point p) {
		Line[] outlineSegments = g.getBounds().getOutlineSegments();
		Point nearestProjection = null;
		double nearestDistance = 0;
		for (Line l : outlineSegments) {
			Point projection = l.getProjection(p);
			double distance = p.getDistance(projection);
			if (nearestProjection == null || distance < nearestDistance) {
				nearestDistance = distance;
				nearestProjection = projection;
			}
		}
		return nearestProjection;
	}

	private Polygon[] getTriangles(Connection connection, int i) {
		Node anchorage = connection.getAnchor(i).getAnchorage();
		Bounds boundsInScene = anchorage
				.localToScene(anchorage.getLayoutBounds());
		Rectangle rectangle = FX2Geometry.toRectangle(boundsInScene);
		Polygon top = new Polygon(rectangle.getTopLeft(),
				rectangle.getTopRight(), rectangle.getCenter());
		Polygon bottom = new Polygon(rectangle.getBottomLeft(),
				rectangle.getBottomRight(), rectangle.getCenter());
		Polygon left = new Polygon(rectangle.getTopLeft(),
				rectangle.getBottomLeft(), rectangle.getCenter());
		Polygon right = new Polygon(rectangle.getTopRight(),
				rectangle.getBottomRight(), rectangle.getCenter());
		return new Polygon[] { top, right, bottom, left };
	}

	private boolean isBottom(Connection connection, int i, Point currentPoint) {
		Point2D pointInScene = connection.localToScene(currentPoint.x,
				currentPoint.y);
		Point point = FX2Geometry.toPoint(pointInScene);
		Polygon[] triangles = getTriangles(connection, i);
		return triangles[2].contains(point);
	}

	private boolean isLeft(Connection connection, int i, Point currentPoint) {
		Point2D pointInScene = connection.localToScene(currentPoint.x,
				currentPoint.y);
		Point point = FX2Geometry.toPoint(pointInScene);
		Polygon[] triangles = getTriangles(connection, i);
		return triangles[3].contains(point);
	}

	private boolean isRight(Connection connection, int i, Point currentPoint) {
		Point2D pointInScene = connection.localToScene(currentPoint.x,
				currentPoint.y);
		Point point = FX2Geometry.toPoint(pointInScene);
		Polygon[] triangles = getTriangles(connection, i);
		return triangles[1].contains(point);
	}

	private boolean isSufficientlyHorizontal(Vector currentDirection) {
		return Math.abs(currentDirection.y) < 0.5
				&& Math.abs(currentDirection.x) > Math.abs(currentDirection.y);
	}

	private boolean isSufficientlyVertical(Vector currentDirection) {
		return Math.abs(currentDirection.y) > Math.abs(currentDirection.x)
				&& Math.abs(currentDirection.x) < 0.5;
	}

	private boolean isTop(Connection connection, int i, Point currentPoint) {
		Point2D pointInScene = connection.localToScene(currentPoint.x,
				currentPoint.y);
		Point point = FX2Geometry.toPoint(pointInScene);
		Polygon[] triangles = getTriangles(connection, i);
		return triangles[0].contains(point);
	}

	private boolean isTopOrBottom(Connection connection, int i,
			Point currentPoint) {
		Point2D pointInScene = connection.localToScene(currentPoint.x,
				currentPoint.y);
		Point point = FX2Geometry.toPoint(pointInScene);
		Polygon[] triangles = getTriangles(connection, i);
		return triangles[0].contains(point) || triangles[2].contains(point);
	}

	@Override
	protected Vector route(ControlPointManipulator cpm, Vector inDirection,
			Vector outDirection) {
		if (Math.abs(outDirection.x) <= 0.05
				&& Math.abs(outDirection.y) <= 0.05) {
			// effectively 0 => do not insert point
			// => use previous direction as current direction
			return inDirection;
		}
		// given the direction, determine if points have to be added
		if (isSufficientlyHorizontal(outDirection)
				|| isSufficientlyVertical(outDirection)) {
			// XXX: We may have to adjust an already orthogonal segment in
			// case it overlaps with an anchorage outline.
			// currentDirection = routeOrthogonalSegment(connection,
			// controlPointManipulator, currentDirection, i,
			// currentPoint);
			return super.route(cpm, inDirection, outDirection);
		} else {
			return routeNonOrthogonalSegment(cpm.getConnection(), cpm,
					inDirection, outDirection, cpm.getIndex(), cpm.getPoint());
		}
	}

	/**
	 * This method is called for a non-orthogonal direction from the last point
	 * on the connection to the current point on the connection.
	 *
	 * @param connection
	 *            The {@link Connection} that is manipulated.
	 * @param controlPointManipulator
	 *            The helper that is used for inserting route points.
	 * @param inDirection
	 *            The previous direction, or <code>null</code> (for the end
	 *            point).
	 * @param outDirection
	 *            The current direction, or <code>null</code> (for the start
	 *            point).
	 * @param i
	 *            The index of the current point.
	 * @param currentPoint
	 *            The current {@link Point}.
	 * @return The manipulated current direction.
	 */
	protected Vector routeNonOrthogonalSegment(Connection connection,
			ControlPointManipulator controlPointManipulator, Vector inDirection,
			Vector outDirection, int i, Point currentPoint) {
		controlPointManipulator.setRoutingData(i + 1, currentPoint,
				outDirection);
		Vector moveVertically = new Vector(0, outDirection.y);
		Vector moveHorizontally = new Vector(outDirection.x, 0);

		if (i == 0 && connection.isStartConnected()
				|| i == connection.getPointsUnmodifiable().size() - 2
						&& connection.isEndConnected()) {
			if (i == 0 && i != connection.getPointsUnmodifiable().size() - 2) {
				// move left/right if current point is on top or
				// bottom anchorage outline
				if (isTopOrBottom(connection, i, currentPoint)) {
					// System.out.println("1");
					// point on top or bottom, move vertically
					outDirection = controlPointManipulator
							.addRoutingPoint(moveVertically);
				} else {
					// System.out.println("2");
					// point on left/right, move horizontally
					outDirection = controlPointManipulator
							.addRoutingPoint(moveHorizontally);
				}
			} else if (i != 0
					&& i == connection.getPointsUnmodifiable().size() - 2) {
				// move left/right if next point is on top or
				// bottom anchorage outline
				if (isTopOrBottom(connection, i + 1, currentPoint
						.getTranslated(outDirection.x, outDirection.y))) {
					// System.out.println("3");
					// point on top or bottom, move horizontally
					outDirection = controlPointManipulator
							.addRoutingPoint(moveHorizontally);
				} else {
					// System.out.println("4");
					// point on left/right, move vertically
					outDirection = controlPointManipulator
							.addRoutingPoint(moveVertically);
				}
			} else {
				// split direction in the middle and generate new
				// control points
				boolean currentIsTopOrBottom = isTopOrBottom(connection, i,
						currentPoint);
				boolean nextIsTopOrBottom = isTopOrBottom(connection, i + 1,
						currentPoint.getTranslated(outDirection.x,
								outDirection.y));
				if (currentIsTopOrBottom && nextIsTopOrBottom) {
					// System.out.println("5");
					// both top/bottom
					controlPointManipulator.addRoutingPoints(i + 1,
							currentPoint, 0, outDirection.y / 2, outDirection.x,
							outDirection.y / 2);
				} else if (!currentIsTopOrBottom && !nextIsTopOrBottom) {
					// System.out.println("6");
					// both left/right
					controlPointManipulator.addRoutingPoints(i + 1,
							currentPoint, outDirection.x / 2, 0,
							outDirection.x / 2, outDirection.y);
				} else {
					// on different sides
					if (currentIsTopOrBottom) {
						// System.out.println("7");
						// use x coordinate of current point
						outDirection = controlPointManipulator
								.addRoutingPoint(moveVertically);
					} else {
						// System.out.println("8");
						// use y coordinate of current point
						outDirection = controlPointManipulator
								.addRoutingPoint(moveHorizontally);
					}
				}
			}
		} else {
			if (inDirection == null) {
				// System.out.println("9");
				// move horizontally first
				outDirection = controlPointManipulator
						.addRoutingPoint(moveHorizontally);
			} else {
				// adjust by inserting a control point; try to follow
				// previous direction as long as possible
				if (inDirection.isHorizontal()) {
					if (inDirection.x < 0 && outDirection.x < 0
							|| inDirection.x > 0 && outDirection.x > 0) {
						// System.out.println("10");
						// prolong current direction horizontally
						outDirection = controlPointManipulator
								.addRoutingPoint(moveHorizontally);
					} else {
						// System.out.println("11");
						// move vertically first
						outDirection = controlPointManipulator
								.addRoutingPoint(moveVertically);
					}
				} else {
					if (inDirection.y < 0 && outDirection.y < 0
							|| inDirection.y > 0 && outDirection.y > 0) {
						// System.out.println("12");
						// prolong current direction vertically
						outDirection = controlPointManipulator
								.addRoutingPoint(moveVertically);
					} else {
						// System.out.println("13");
						// move horizontally first
						outDirection = controlPointManipulator
								.addRoutingPoint(moveHorizontally);
					}
				}
			}
		}
		return outDirection;
	}

	/**
	 * This method is called for an orthogonal direction from the last point on
	 * the connection to the current point on the connection.
	 *
	 * @param connection
	 *            The {@link Connection} that is manipulated.
	 * @param controlPointManipulator
	 *            The helper that is used to insert route points.
	 * @param currentDirection
	 *            The current direction.
	 * @param i
	 *            The index of the current point.
	 * @param currentPoint
	 *            The current {@link Point}.
	 * @return The manipulated current direction.
	 */
	protected Vector routeOrthogonalSegment(Connection connection,
			ControlPointManipulator controlPointManipulator,
			Vector currentDirection, int i, Point currentPoint) {
		// completely horizontal/vertical is not allowed for connected
		// anchors
		if (i == 0 && connection.isStartConnected()
				&& i != connection.getPointsUnmodifiable().size() - 2) {
			// start point, connected
			if (currentDirection.isVertical()) {
				boolean isLeft = isLeft(connection, i, currentPoint);
				boolean isRight = isRight(connection, i, currentPoint);
				boolean isBottom = isBottom(connection, i, currentPoint);
				boolean isTop = isTop(connection, i, currentPoint);
				if ((isLeft || isRight) && !(isBottom || isTop)) {
					// insert two control points
					double offset = isLeft ? -OFFSET : OFFSET;
					controlPointManipulator.addRoutingPoints(i + 1,
							currentPoint, offset, 0, offset,
							currentDirection.y);
					currentDirection = new Vector(-offset, 0);
				}
			} else if (currentDirection.isHorizontal()) {
				boolean isLeft = isLeft(connection, i, currentPoint);
				boolean isRight = isRight(connection, i, currentPoint);
				boolean isBottom = isBottom(connection, i, currentPoint);
				boolean isTop = isTop(connection, i, currentPoint);
				if ((isTop || isBottom) && !(isLeft || isRight)) {
					// insert two control points above
					double offset = isTop ? -OFFSET : OFFSET;
					controlPointManipulator.addRoutingPoints(i + 1,
							currentPoint, 0, offset, currentDirection.x,
							offset);
					currentDirection = new Vector(0, -offset);
				}
			}
		} else if (i != 0 && i == connection.getPointsUnmodifiable().size() - 2
				&& connection.isEndConnected()) {
			// end point, connected
			if (currentDirection.isHorizontal()) {
				boolean isLeft = isLeft(connection, i + 1, currentPoint
						.getTranslated(currentDirection.x, currentDirection.y));
				boolean isRight = isRight(connection, i + 1, currentPoint
						.getTranslated(currentDirection.x, currentDirection.y));
				boolean isTop = isTop(connection, i + 1, currentPoint
						.getTranslated(currentDirection.x, currentDirection.y));
				boolean isBottom = isBottom(connection, i + 1, currentPoint
						.getTranslated(currentDirection.x, currentDirection.y));
				if ((isTop || isBottom) && !(isLeft || isRight)) {
					// insert 2 points above
					double offset = isTop ? -OFFSET : OFFSET;
					controlPointManipulator.addRoutingPoints(i + 1,
							currentPoint, 0, offset, currentDirection.x,
							offset);
					currentDirection = new Vector(0, -offset);
				}
			} else if (currentDirection.isVertical()) {
				boolean isLeft = isLeft(connection, i + 1, currentPoint
						.getTranslated(currentDirection.x, currentDirection.y));
				boolean isRight = isRight(connection, i + 1, currentPoint
						.getTranslated(currentDirection.x, currentDirection.y));
				boolean isTop = isTop(connection, i + 1, currentPoint
						.getTranslated(currentDirection.x, currentDirection.y));
				boolean isBottom = isBottom(connection, i + 1, currentPoint
						.getTranslated(currentDirection.x, currentDirection.y));
				if ((isLeft || isRight) && !(isTop || isBottom)) {
					// insert 2 points on the left
					double offset = isLeft ? -OFFSET : OFFSET;
					controlPointManipulator.addRoutingPoints(i + 1,
							currentPoint, offset, 0, offset,
							currentDirection.y);
					currentDirection = new Vector(-offset, 0);
				}
			}
		} else if (i == 0 && i == connection.getPointsUnmodifiable().size() - 2
				&& connection.isStartConnected()
				&& connection.isEndConnected()) {
			// start and end point, connected
			if (currentDirection.isHorizontal()) {
				boolean isCurrentTop = isTop(connection, i, currentPoint);
				boolean isNextBottom = isBottom(connection, i + 1, currentPoint
						.getTranslated(currentDirection.x, currentDirection.y));
				boolean isCurrentBottom = isBottom(connection, i, currentPoint);
				boolean isNextTop = isTop(connection, i + 1, currentPoint
						.getTranslated(currentDirection.x, currentDirection.y));
				if (isCurrentTop && isNextBottom
						|| isCurrentBottom && isNextTop) {
					double offset = isCurrentTop ? -OFFSET : OFFSET;
					// from top to bottom => insert 4 control points
					controlPointManipulator.addRoutingPoints(i + 1,
							currentPoint, 0, offset, currentDirection.x / 2,
							offset, currentDirection.x / 2,
							currentDirection.y - offset, currentDirection.x,
							currentDirection.y - offset);
					currentDirection = new Vector(0, offset);
				}
			} else if (currentDirection.isVertical()) {
				boolean isCurrentLeft = isLeft(connection, i, currentPoint);
				boolean isNextRight = isRight(connection, i + 1, currentPoint
						.getTranslated(currentDirection.x, currentDirection.y));
				boolean isCurrentRight = isRight(connection, i, currentPoint);
				boolean isNextLeft = isLeft(connection, i + 1, currentPoint
						.getTranslated(currentDirection.x, currentDirection.y));
				if (isCurrentLeft && isNextRight
						|| isCurrentRight && isNextLeft) {
					double offset = isCurrentLeft ? -OFFSET : OFFSET;
					// from left to right => insert 4 control points
					controlPointManipulator.addRoutingPoints(i + 1,
							currentPoint, offset, 0, offset,
							currentDirection.y / 2, currentDirection.x - offset,
							currentDirection.y / 2, currentDirection.x - offset,
							currentDirection.y);
				}
			}
		}
		return currentDirection;
	}

	@Override
	protected void updateComputationParameters(List<Point> points, int index,
			DynamicAnchor anchor, AnchorKey key) {
		// set anchored reference point
		super.updateComputationParameters(points, index, anchor, key);

		// set orientation hint for first and last anchor
		if (index == 0 || index == points.size() - 1) {
			// update orientation hint
			Point neighborPoint = points
					.get(index == 0 ? index + 1 : index - 1);
			Point refPoint = NodeUtils
					.sceneToLocal(getConnection(),
							NodeUtils.localToScene(key.getAnchored(),
									anchor.getComputationParameter(key,
											AnchoredReferencePoint.class)
											.get()));
			Point delta = neighborPoint.getDifference(refPoint);
			Orientation hint = null;
			if (Math.abs(delta.x) < 5
					&& Math.abs(delta.x) < Math.abs(delta.y)) {
				// very small x difference => go in vertically
				hint = Orientation.VERTICAL;
			} else if (Math.abs(delta.y) < 5
					&& Math.abs(delta.y) < Math.abs(delta.x)) {
				// very small y difference => go in horizontally
				hint = Orientation.HORIZONTAL;
			}
			// provide a hint to the anchor's computation strategy
			anchor.getComputationParameter(key, PreferredOrientation.class)
					.set(hint);
		}
	}
}
