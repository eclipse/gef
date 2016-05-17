/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contributions for Bugzilla #488356
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.anchors.DynamicAnchor.AnchorageReferenceGeometry;
import org.eclipse.gef4.fx.anchors.DynamicAnchor.AnchoredReferencePoint;
import org.eclipse.gef4.fx.anchors.DynamicAnchor.PreferredOrientation;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.fx.anchors.StaticAnchor;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Rectangle;

import javafx.collections.ObservableList;
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

	/**
	 * A {@link ControlPointManipulator} can be used to record, perform, and
	 * roll back control point changes during routing.
	 */
	protected static class ControlPointManipulator {
		private Connection connection;
		private Map<Integer, List<Point>> pointsToInsert = new HashMap<>();
		private int index;
		private Vector direction;
		private Point point;
		private List<IAnchor> initialControlAnchors;

		/**
		 * Constructs a new {@link ControlPointManipulator} for the given
		 * {@link Connection}.
		 *
		 * @param c
		 *            The {@link Connection} that is manipulated.
		 */
		public ControlPointManipulator(Connection c) {
			this.connection = c;
		}

		/**
		 * Performs the recorded changes.
		 */
		public void addPoints() {
			int pointsInserted = 0;
			for (int insertionIndex : pointsToInsert.keySet()) {
				// XXX: We need to keep track of those way points we insert, so
				// we can remove them in a succeeding routing pass; we use a
				// special subclass of StaticAnchor for this purpose, so we can
				// easily identify them through an instance check.
				for (Point pointToInsert : pointsToInsert.get(insertionIndex)) {
					initialControlAnchors.add(
							insertionIndex + pointsInserted - 1,
							new OrthogonalPolylineRouterAnchor(connection,
									pointToInsert));
					pointsInserted++;
				}
			}
			// exchange the connection's points all at once
			connection.setControlAnchors(initialControlAnchors);
		}

		/**
		 * Records the specified change.
		 *
		 * @param index
		 *            The index at which to insert a control point.
		 * @param point
		 *            The start coordinates for the change.
		 * @param dx
		 *            The horizontal component of the out direction.
		 * @param dy
		 *            The vertical component of the out direction.
		 * @return A {@link Vector} specifying the out direction.
		 */
		public Vector addRoutingPoint(int index, Point point, double dx,
				double dy) {
			Point insertion = point.getTranslated(dx, dy);
			if (!pointsToInsert.containsKey(index)) {
				pointsToInsert.put(index, new ArrayList<Point>());
			}
			pointsToInsert.get(index).add(insertion);
			return new Vector(dx, dy);
		}

		/**
		 * Records the specified change.
		 *
		 * @param delta
		 *            A {@link Vector} specifying the out direction.
		 * @return A {@link Vector} specifying the out direction.
		 */
		public Vector addRoutingPoint(Vector delta) {
			direction = direction.getSubtracted(
					addRoutingPoint(index, point, delta.x, delta.y));
			return direction;
		}

		/**
		 * Records the given changes.
		 *
		 * @param index
		 *            The start index for the changes.
		 * @param point
		 *            The start coordinates for the changes.
		 * @param deltas
		 *            The out directions for the new points.
		 */
		public void addRoutingPoints(int index, Point point, double... deltas) {
			if (deltas == null) {
				throw new IllegalArgumentException(
						"Even number of routing point deltas required, but got <null>.");
			}
			if (deltas.length == 0) {
				throw new IllegalArgumentException(
						"Even number of routing point deltas required, but got 0.");
			}
			if (deltas.length % 2 != 0) {
				throw new IllegalArgumentException(
						"Even number of routing point deltas required, but got "
								+ deltas.length + ".");
			}

			// create array list if needed
			if (!pointsToInsert.containsKey(index)) {
				pointsToInsert.put(index, new ArrayList<Point>());
			}

			// insert points
			for (int i = 0; i < deltas.length; i += 2) {
				Point insertion = point.getTranslated(deltas[i], deltas[i + 1]);
				pointsToInsert.get(index).add(insertion);
			}
		}

		/**
		 * Rolls back the changes.
		 */
		public void clearPoints() {
			// XXX: Route may be invoked multiple times until the anchor
			// positions are property computed (because transforms change,
			// etc.); we need to remove those points we have inserted in a
			// preceding pass to guarantee that we only do 'minimal' routing; as
			// we use a special subclass of StaticAnchor, we can easily sort
			// them out through an instance check. However, we cannot remove the
			// anchors one by one, because that will cause a refresh of the
			// connection after each removed control point (leading to a
			// re-entrance here).
			initialControlAnchors = new ArrayList<>();
			for (IAnchor a : connection.getControlAnchors()) {
				if (!(a instanceof OrthogonalPolylineRouterAnchor)) {
					initialControlAnchors.add(a);
				}
			}
			connection.setControlAnchors(initialControlAnchors);
		}

		/**
		 * Initializes this {@link ControlPointManipulator} for the recording of
		 * changes.
		 *
		 * @param index
		 *            The index of the control point after which points are to
		 *            be added.
		 * @param point
		 *            The start coordinates for the changes.
		 * @param direction
		 *            The current direction.
		 */
		public void setRoutingData(int index, Point point, Vector direction) {
			this.index = index;
			this.point = point;
			this.direction = direction;
		}
	}

	// private sub-class to 'mark' those way-points that are added by the router
	// (so they can be removed when re-routing)
	private static class OrthogonalPolylineRouterAnchor extends StaticAnchor {
		public OrthogonalPolylineRouterAnchor(Node anchorage,
				Point referencePositionInAnchorageLocal) {
			super(anchorage, referencePositionInAnchorageLocal);
		}

		@Override
		public String toString() {
			return "OrthogonalRouterAnchor[referencePosition="
					+ getReferencePosition() + "]";
		}
	}

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
	private int findReferenceIndex(Connection connection, int anchorIndex,
			IGeometry anchorageGeometry, int step) {
		ObservableList<Point> points = connection.getPointsUnmodifiable();
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
	private IGeometry getAnchorageGeometry(Connection connection, int index) {
		IAnchor anchor = connection.getAnchor(index);
		// TODO: use connection methods to detect whether anchors are connected,
		// don't duplicate logic here
		if (anchor != null && anchor.getAnchorage() != null
				&& anchor.getAnchorage() != connection) {
			Node anchorage = anchor.getAnchorage();
			if (anchor instanceof DynamicAnchor) {
				IGeometry geometry = ((DynamicAnchor) anchor)
						.getComputationParameter(connection.getAnchorKey(index),
								AnchorageReferenceGeometry.class)
						.get();
				return NodeUtils.sceneToLocal(connection,
						NodeUtils.localToScene(anchorage, geometry));
			}
			// fall back to using the shape outline
			return NodeUtils.sceneToLocal(connection, NodeUtils.localToScene(
					anchorage, NodeUtils.getShapeOutline(anchorage)));
		}
		return null;
	}

	/**
	 * Returns the reference point for the anchor at the given index.
	 *
	 * @param connection
	 *            The {@link Connection} that is currently routed.
	 * @param index
	 *            The index specifying the anchor for which to provide a
	 *            reference point.
	 * @return The reference point for the anchor at the given index in the
	 *         local coordinate system of the anchored, which is the
	 *         connection's curve.
	 */
	@Override
	protected Point getAnchoredReferencePoint(Connection connection,
			int index) {
		if (index < 0 || index >= connection.getPointsUnmodifiable().size()) {
			throw new IndexOutOfBoundsException();
		}
		IGeometry geometry = getAnchorageGeometry(connection, index);
		int referenceIndex = findReferenceIndex(connection, index, geometry,
				index < connection.getPointsUnmodifiable().size() - 1 ? 1 : -1);
		IGeometry referenceGeometry = getAnchorageGeometry(connection,
				referenceIndex);
		if (referenceGeometry != null) {
			if (geometry != null) {
				// XXX: if a position hint is supplied for the current index,
				// return that hint as the reference point.
				if (index == 0) {
					Point startPointHint = connection.getStartPointHint();
					if (startPointHint != null) {
						return NodeUtils.parentToLocal(connection.getCurve(),
								startPointHint);
					}
				} else if (index == connection.getPointsUnmodifiable().size()
						- 1) {
					Point endPointHint = connection.getEndPointHint();
					if (endPointHint != null) {
						return NodeUtils.parentToLocal(connection.getCurve(),
								endPointHint);
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
					return NodeUtils.parentToLocal(connection.getCurve(),
							new Point(x1 + (x2 - x1) / 2,
									refBounds.getY() > bounds.getY()
											+ bounds.getHeight()
													? refBounds.getY()
													: refBounds.getY()
															+ refBounds
																	.getHeight()));
				}

				double y1 = Math.max(bounds.getY(), refBounds.getY());
				double y2 = Math.min(bounds.getY() + bounds.getHeight(),
						refBounds.getY() + refBounds.getHeight());
				if (y1 <= y2) {
					// vertical overlap => return horizontally stable position
					return NodeUtils
							.parentToLocal(connection.getCurve(),
									new Point(refBounds.getX() > bounds.getX()
											+ bounds.getWidth()
													? refBounds.getX()
													: refBounds.getX()
															+ refBounds
																	.getWidth(),
											y1 + (y2 - y1) / 2));
				}
				// fallback to nearest bounds projection
				// TODO: revise handling of this case -> we could optimize this
				// by providing a desired direction
				return NodeUtils.parentToLocal(connection.getCurve(),
						getNearestBoundsProjection(referenceGeometry,
								geometry.getBounds().getCenter()));
			}
		}
		return NodeUtils.parentToLocal(connection.getCurve(),
				connection.getPoint(referenceIndex));
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
	public void route(Connection connection) {
		if (connection.getPointsUnmodifiable().size() < 2) {
			// we cannot route if the connection does not have at least start
			// and end points.
			return;
		}

		// XXX: Route may be invoked multiple times until the anchor
		// positions are property computed (because transforms change,
		// etc.); we need to remove those points we have inserted in a
		// preceding pass to guarantee that we only do 'minimal' routing; as
		// we use a special subclass of StaticAnchor, we can easily sort
		// them out through an instance check.
		ControlPointManipulator controlPointManipulator = new ControlPointManipulator(
				connection);
		controlPointManipulator.clearPoints();

		// The router will respect the connection's anchors already provided
		// and will add control anchors only where needed. It will proceed all
		// anchors from start to end and compute the respective direction to the
		// next anchor. For those anchors that are connected, reference points
		// will be computed.
		Vector inDirection = null;
		Vector outDirection = null;
		for (int i = 0; i < connection.getPointsUnmodifiable().size()
				- 1; i++) {
			IAnchor anchor = connection.getAnchor(i);
			if (anchor instanceof DynamicAnchor) {
				updateComputationParameters(connection, i);
			}
			Point currentPoint = connection.getPoint(i);

			// direction between preceding way/control point and current one has
			// been computed in previous iteration
			inDirection = outDirection;
			// compute the direction between the current way/control point and
			// the succeeding one
			IAnchor nextAnchor = connection.getAnchor(i + 1);
			if (nextAnchor instanceof DynamicAnchor) {
				updateComputationParameters(connection, i + 1);
			}
			outDirection = new Vector(connection.getPoint(i),
					connection.getPoint(i + 1));

			if (Math.abs(outDirection.x) <= 0.05
					&& Math.abs(outDirection.y) <= 0.05) {
				// effectively 0 => do not insert point
				// => use previous direction as current direction
				outDirection = inDirection;
				continue;
			}

			// given the direction, determine if points have to be added
			if (isSufficientlyHorizontal(outDirection)
					|| isSufficientlyVertical(outDirection)) {
				// XXX: We may have to adjust an already orthogonal segment in
				// case it overlaps with an anchorage outline.
				// currentDirection = routeOrthogonalSegment(connection,
				// controlPointManipulator, currentDirection, i,
				// currentPoint);
			} else {
				outDirection = routeNonOrthogonalSegment(connection,
						controlPointManipulator, inDirection, outDirection, i,
						currentPoint);
			}
		}

		// add all inserted points to the connection
		controlPointManipulator.addPoints();
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
	protected void updateComputationParameters(Connection connection,
			int index) {
		// set anchored reference point
		super.updateComputationParameters(connection, index);

		// set orientation hint for first and last anchor
		AnchorKey anchorKey = connection.getAnchorKey(index);
		IAnchor anchor = connection.getAnchor(index);
		if (index == 0
				|| index == connection.getPointsUnmodifiable().size() - 1) {
			// update orientation hint
			Point neighborPoint = connection
					.getPoint(index == 0 ? index + 1 : index - 1);
			Point delta = neighborPoint
					.getDifference(NodeUtils.sceneToLocal(connection,
							NodeUtils.localToScene(anchorKey.getAnchored(),
									((DynamicAnchor) anchor)
											.getComputationParameter(anchorKey,
													AnchoredReferencePoint.class)
											.get())));
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
			((DynamicAnchor) anchor).getComputationParameter(anchorKey,
					PreferredOrientation.class).set(hint);
		}
	}

	@Override
	public boolean wasInserted(IAnchor anchor) {
		return anchor instanceof OrthogonalPolylineRouterAnchor;
	}

}
