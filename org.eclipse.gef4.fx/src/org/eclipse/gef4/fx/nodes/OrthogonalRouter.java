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

import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.anchors.DynamicAnchor.AbstractComputationStrategy;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.fx.anchors.StaticAnchor;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Rectangle;

import javafx.geometry.Bounds;
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
public class OrthogonalRouter implements IConnectionRouter {

	private static class ControlPointManipulator {

		private Connection connection;
		private Map<Integer, List<Point>> pointsToInsert = new HashMap<>();
		private int index;
		private Vector direction;
		private Point point;
		private List<IAnchor> initialControlAnchors;

		public ControlPointManipulator(Connection c) {
			this.connection = c;
		}

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

		public Vector addRoutingPoint(int index, Point point, double dx,
				double dy) {
			Point insertion = point.getTranslated(dx, dy);
			if (!pointsToInsert.containsKey(index)) {
				pointsToInsert.put(index, new ArrayList<Point>());
			}
			pointsToInsert.get(index).add(insertion);
			return new Vector(dx, dy);
		}

		public Vector addRoutingPoint(Vector delta) {
			direction = direction.getSubtracted(
					addRoutingPoint(index, point, delta.x, delta.y));
			return direction;
		}

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

		public void clearPoints() {
			// XXX: Route may be invoked multiple times until the anchor
			// positions are property computed (because transforms change,
			// etc.); we need to remove those points we have inserted in a
			// preceding pass to guarantee that we only do 'minimal' routing; as
			// we use a special subclass of StaticAnchor, we can easily sort
			// them out through an instance check.
			int pointsRemoved = 0;
			initialControlAnchors = new ArrayList<>(
					connection.getControlAnchors());
			for (int i = 0; i < initialControlAnchors.size(); i++) {
				if (initialControlAnchors
						.get(i) instanceof OrthogonalPolylineRouterAnchor) {
					connection.removeControlAnchor(i - pointsRemoved);
					pointsRemoved++;
				}
			}
			initialControlAnchors = new ArrayList<>(
					connection.getControlAnchors());
		}

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
	}

	private static final double OFFSET = 15;

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
		IAnchor anchor = connection.getAnchor(index);
		// TODO: use connection methods to detect whether anchors are connected,
		// don't duplicate logic here
		if (anchor != null && anchor.getAnchorage() != null
				&& anchor.getAnchorage() != connection) {
			Node anchorage = anchor.getAnchorage();
			IGeometry outlineGeometry = AbstractComputationStrategy
					.getOutlineGeometry(anchorage);
			return NodeUtils.sceneToLocal(connection,
					NodeUtils.localToScene(anchorage, outlineGeometry));
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
				// TODO: revise handling of this case -> we could optimize this
				// by
				// providing a desired direction; fallback to nearest bounds
				// projection
				return DynamicAnchor.OrthogonalProjectionStrategy
						.getNearestBoundsProjection(referenceGeometry,
								geometry.getBounds().getCenter());
			}
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
				((DynamicAnchor) anchor).referencePointProperty()
						.put(connection.getAnchorKey(index), referencePoint);
				Point computePosition = ((DynamicAnchor) anchor)
						.computePosition(connection, referencePoint,
								((DynamicAnchor) anchor).getComputationStrategy(
										connection.getAnchorKey(index)));
				return computePosition;
			}
		}
		return connection.getPoint(index);
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
		if (connection.getPoints().size() < 2) {
			// we cannot route if the connection does not have at least start
			// and end points.
			return;
		}

		ControlPointManipulator controlPointManipulator = new ControlPointManipulator(
				connection);
		// XXX: Route may be invoked multiple times until the anchor
		// positions are property computed (because transforms change,
		// etc.); we need to remove those points we have inserted in a
		// preceding pass to guarantee that we only do 'minimal' routing; as
		// we use a special subclass of StaticAnchor, we can easily sort
		// them out through an instance check.
		controlPointManipulator.clearPoints();

		// The router will respect the connection's anchors already provided
		// and will add control anchors only where needed. It will proceed all
		// anchors from start to end and compute the respective direction to the
		// next anchor. For those anchors that are connected, reference points
		// will be computed.
		Vector previousDirection = null;
		Vector currentDirection = null;
		for (int i = 0; i < connection.getPoints().size() - 1; i++) {
			Point currentPoint = getPosition(connection, i);

			// direction between preceding way/control point and current one has
			// been computed in previous iteration
			previousDirection = currentDirection;
			// compute the direction between the current way/control point and
			// the succeeding one
			currentDirection = getDirection(connection, i);

			// given the direction, determine if points have to be added
			if (currentDirection.isHorizontal()
					|| currentDirection.isVertical()) {
				// XXX: We may have to adjust an already orthogonal segment in
				// case it overlaps with an anchorage outline.
				currentDirection = routeOrthogonalSegment(connection,
						controlPointManipulator, currentDirection, i,
						currentPoint);
			} else {
				currentDirection = routeNonOrthogonalSegment(connection,
						controlPointManipulator, previousDirection,
						currentDirection, i, currentPoint);
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
	 * @param previousDirection
	 *            The previous direction, or <code>null</code>.
	 * @param currentDirection
	 *            The current direction.
	 * @param i
	 *            The index of the current point.
	 * @param currentPoint
	 *            The current {@link Point}.
	 * @return The manipulated current direction.
	 */
	protected Vector routeNonOrthogonalSegment(Connection connection,
			ControlPointManipulator controlPointManipulator,
			Vector previousDirection, Vector currentDirection, int i,
			Point currentPoint) {
		controlPointManipulator.setRoutingData(i + 1, currentPoint,
				currentDirection);
		Vector moveVertically = new Vector(0, currentDirection.y);
		Vector moveHorizontally = new Vector(currentDirection.x, 0);

		if (i == 0 && connection.isStartConnected()
				|| i == connection.getPoints().size() - 2
						&& connection.isEndConnected()) {
			if (i == 0 && i != connection.getPoints().size() - 2) {
				// move left/right if current point is on top or
				// bottom anchorage outline
				if (isTopOrBottom(connection, i, currentPoint)) {
					// point on top or bottom, move vertically
					currentDirection = controlPointManipulator
							.addRoutingPoint(moveVertically);
				} else {
					// point on left/right, move horizontally
					currentDirection = controlPointManipulator
							.addRoutingPoint(moveHorizontally);
				}
			} else if (i != 0 && i == connection.getPoints().size() - 2) {
				// move left/right if next point is on top or
				// bottom anchorage outline
				if (isTopOrBottom(connection, i + 1, currentPoint.getTranslated(
						currentDirection.x, currentDirection.y))) {
					// point on top or bottom, move horizontally
					currentDirection = controlPointManipulator
							.addRoutingPoint(moveHorizontally);
				} else {
					// point on left/right, move vertically
					currentDirection = controlPointManipulator
							.addRoutingPoint(moveVertically);
				}
			} else {
				// split direction in the middle and generate new
				// control points
				boolean currentIsTopOrBottom = isTopOrBottom(connection, i,
						currentPoint);
				boolean nextIsTopOrBottom = isTopOrBottom(connection, i + 1,
						currentPoint.getTranslated(currentDirection.x,
								currentDirection.y));
				if (currentIsTopOrBottom && nextIsTopOrBottom) {
					// both top/bottom
					controlPointManipulator.addRoutingPoints(i + 1,
							currentPoint, 0, currentDirection.y / 2,
							currentDirection.x, currentDirection.y / 2);
				} else if (!currentIsTopOrBottom && !nextIsTopOrBottom) {
					// both left/right
					controlPointManipulator.addRoutingPoints(i + 1,
							currentPoint, currentDirection.x / 2, 0,
							currentDirection.x / 2, currentDirection.y);
				} else {
					// on different sides
					if (currentIsTopOrBottom) {
						// use x coordinate of current point
						currentDirection = controlPointManipulator
								.addRoutingPoint(moveVertically);
					} else {
						// use y coordinate of current point
						currentDirection = controlPointManipulator
								.addRoutingPoint(moveHorizontally);
					}
				}
			}
		} else {
			if (previousDirection == null) {
				// move horizontally first
				currentDirection = controlPointManipulator
						.addRoutingPoint(moveHorizontally);
			} else {
				// adjust by inserting a control point; try to follow
				// previous direction as long as possible
				if (previousDirection.isHorizontal()) {
					if (previousDirection.x < 0 && currentDirection.x < 0
							|| previousDirection.x > 0
									&& currentDirection.x > 0) {
						// prolong current direction horizontally
						currentDirection = controlPointManipulator
								.addRoutingPoint(moveHorizontally);
					} else {
						// move vertically first
						currentDirection = controlPointManipulator
								.addRoutingPoint(moveVertically);
					}
				} else {
					if (previousDirection.y < 0 && currentDirection.y < 0
							|| previousDirection.y > 0
									&& currentDirection.y > 0) {
						// prolong current direction vertically
						currentDirection = controlPointManipulator
								.addRoutingPoint(moveVertically);
					} else {
						// move horizontally first
						currentDirection = controlPointManipulator
								.addRoutingPoint(moveHorizontally);
					}
				}
			}
		}
		return currentDirection;
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
				&& i != connection.getPoints().size() - 2) {
			// start point, connected
			if (currentDirection.isVertical()) {
				boolean isLeft = isLeft(connection, i, currentPoint);
				boolean isRight = isRight(connection, i, currentPoint);
				if (isLeft || isRight) {
					// insert two control points
					double offset = isLeft ? -OFFSET : OFFSET;
					controlPointManipulator.addRoutingPoints(i + 1,
							currentPoint, offset, 0, offset,
							currentDirection.y);
					currentDirection = new Vector(-offset, 0);
				}
			} else if (currentDirection.isHorizontal()) {
				boolean isTop = isTop(connection, i, currentPoint);
				boolean isBottom = isBottom(connection, i, currentPoint);
				if (isTop || isBottom) {
					double offset = isTop ? -OFFSET : OFFSET;
					// insert two control points above
					controlPointManipulator.addRoutingPoints(i + 1,
							currentPoint, 0, offset, currentDirection.x,
							offset);
					currentDirection = new Vector(0, -offset);
				}
			}
		} else if (i != 0 && i == connection.getPoints().size() - 2
				&& connection.isEndConnected()) {
			// end point, connected
			if (currentDirection.isHorizontal()) {
				boolean isTop = isTop(connection, i + 1, currentPoint
						.getTranslated(currentDirection.x, currentDirection.y));
				boolean isBottom = isBottom(connection, i + 1, currentPoint
						.getTranslated(currentDirection.x, currentDirection.y));
				if (isTop || isBottom) {
					double offset = isTop ? -OFFSET : OFFSET;
					// insert 2 points above
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
				if (isLeft || isRight) {
					double offset = isLeft ? -OFFSET : OFFSET;
					// insert 2 points on the left
					controlPointManipulator.addRoutingPoints(i + 1,
							currentPoint, offset, 0, offset,
							currentDirection.y);
					currentDirection = new Vector(-offset, 0);
				}
			}
		} else if (i == 0 && i == connection.getPoints().size() - 2
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
}
