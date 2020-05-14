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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.fx.anchors.AnchorKey;
import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchorageReferenceGeometry;
import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchoredReferencePoint;
import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.anchors.StaticAnchor;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Point;

import javafx.beans.binding.ObjectBinding;
import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * Abstract base class for {@link IConnectionRouter}s implementing a routing
 * strategy that can be specialized by subclasses:
 * <ol>
 * <li>Remove anchors previously inserted by the router.
 * <li>Copy connection points before updating the computation parameters.
 * <li>Update computation parameters based on the copied connection points (i.e.
 * not influenced by parameter changes).
 * <li>Record connection point manipulations using
 * {@link ControlPointManipulator}.
 * <li>Apply all recorded changes to the connection.
 * </ol>
 *
 * @author anyssen
 * @author mwienand
 */
public abstract class AbstractRouter implements IConnectionRouter {

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
		private List<IAnchor> controlAnchors;

		/**
		 * Constructs a new {@link ControlPointManipulator} for the given
		 * {@link Connection}.
		 *
		 * @param c
		 *            The {@link Connection} that is manipulated.
		 */
		public ControlPointManipulator(Connection c) {
			this.connection = c;
			this.controlAnchors = new ArrayList<>(
					connection.getControlAnchors());
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
		 * Performs the recorded changes.
		 */
		public void applyChanges() {
			if (controlAnchors == null) {
				throw new IllegalStateException("Cannot apply changes twice.");
			}

			int pointsInserted = 0;
			for (int insertionIndex : pointsToInsert.keySet()) {
				// XXX: We need to keep track of those way points we insert, so
				// we can remove them in a succeeding routing pass; we use a
				// special subclass of StaticAnchor for this purpose, so we can
				// easily identify them through an instance check.
				for (Point pointToInsert : pointsToInsert.get(insertionIndex)) {
					controlAnchors.add(insertionIndex + pointsInserted - 1,
							new VolatileStaticAnchor(connection,
									pointToInsert));
					pointsInserted++;
				}
			}

			// exchange the connection's points all at once
			connection.setControlAnchors(controlAnchors);

			// guard against applying changes twice
			controlAnchors = null;
		}

		/**
		 * Returns the {@link Connection} that is manipulated.
		 *
		 * @return The {@link Connection} that is manipulated.
		 */
		public Connection getConnection() {
			return connection;
		}

		/**
		 * Returns the current insertion index for manipulations.
		 *
		 * @return The current index.
		 */
		public int getIndex() {
			return index;
		}

		/**
		 * Returns the current {@link Point} on the {@link Connection}.
		 *
		 * @return The current {@link Point}.
		 */
		public Point getPoint() {
			return point;
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

	/**
	 * The {@link VolatileStaticAnchor} is a {@link StaticAnchor} that may be
	 * inserted by an {@link AbstractRouter} during
	 * {@link AbstractRouter#route(Connection) route(Connection)}, and, hence,
	 * will be removed when routing is performed again. A subtype is used so
	 * that the inserted anchors can easily be identified.
	 */
	protected static class VolatileStaticAnchor extends StaticAnchor {

		/**
		 * Constructs a new {@link VolatileStaticAnchor}. Uses the given
		 * {@link Connection} as the anchorage, and the given {@link Point} as
		 * the {@link #getReferencePosition() reference position}.
		 *
		 * @param connection
		 *            The {@link Connection} that serves as the anchorage for
		 *            this {@link VolatileStaticAnchor}.
		 * @param referencePositionInAnchorageLocal
		 *            The {@link Point} that specifies the
		 *            {@link #getReferencePosition() reference position} for
		 *            this {@link VolatileStaticAnchor}, interpreted in the
		 *            local coordinate system of the {@link Connection}.
		 */
		public VolatileStaticAnchor(Connection connection,
				Point referencePositionInAnchorageLocal) {
			super(connection, referencePositionInAnchorageLocal);
		}

		@Override
		public String toString() {
			return "VolatileStaticAnchor[referencePosition="
					+ getReferencePosition() + "]";
		}
	}

	private Connection connection;

	/**
	 * Returns a newly created {@link ControlPointManipulator} that can be used
	 * to insert control points into the given {@link Connection}.
	 *
	 * @param connection
	 *            The {@link Connection} for which to create a
	 *            {@link ControlPointManipulator}.
	 * @return The {@link ControlPointManipulator} for the given
	 *         {@link Connection}.
	 */
	protected ControlPointManipulator createControlPointManipulator(
			Connection connection) {
		return new ControlPointManipulator(connection);
	}

	/**
	 * Retrieves the geometry of the anchorage at the given index within the
	 * coordinate system of the {@link Connection}, in case the respective
	 * anchor is connected.
	 *
	 * @param index
	 *            The index of the anchor whose anchorage geometry is to be
	 *            retrieved.
	 * @return A geometry resembling the anchorage reference geometry of the
	 *         anchor at the given index, or <code>null</code> if the anchor is
	 *         not connected.
	 */
	protected IGeometry getAnchorageGeometry(int index) {
		IAnchor anchor = connection.getAnchor(index);
		if (connection.isConnected(anchor)) {
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
	 * Returns the {@link AnchoredReferencePoint} parameter value (within the
	 * coordinate system of the {@link Connection}) for the anchor specified by
	 * the given index.
	 *
	 * @param points
	 *            The list of {@link Point}s from which the {@link Connection}
	 *            is currently constituted.
	 * @param index
	 *            The index of the {@link IAnchor} for which to compute the
	 *            {@link AnchoredReferencePoint} parameter value.
	 * @return The anchored reference {@link Point} for the specified anchor.
	 */
	protected abstract Point getAnchoredReferencePoint(List<Point> points,
			int index);

	/**
	 * Returns the {@link Connection} of the last {@link #route(Connection)}
	 * call.
	 *
	 * @return The {@link Connection} passed into {@link #route(Connection)}.
	 */
	protected Connection getConnection() {
		return connection;
	}

	/**
	 * Inserts router anchors into the {@link Connection}.
	 *
	 * @param connection
	 *            The {@link Connection}.
	 */
	protected void insertRouterAnchors(Connection connection) {
		// XXX: Copy points just to be sure they are not modified.
		List<Point> pts = new ArrayList<>(connection.getPointsUnmodifiable());
		for (int i = 0; i < pts.size(); i++) {
			Point pos = connection.getAnchor(i)
					.getPosition(connection.getAnchorKey(i));
			pts.set(i, FX2Geometry.toPoint(connection.getCurve()
					.localToParent(Geometry2FX.toFXPoint(pos))));
		}
		ControlPointManipulator cpm = createControlPointManipulator(connection);

		Vector inDirection = null;
		Vector outDirection = null;
		for (int i = 0; i < pts.size() - 1; i++) {
			Point currentPoint = pts.get(i);

			// direction between preceding way/control point and current one has
			// been computed in previous iteration
			inDirection = outDirection;

			// compute the direction between the current way/control point and
			// the succeeding one
			outDirection = new Vector(currentPoint, pts.get(i + 1));

			// prepare CPM for manipulations
			cpm.setRoutingData(i, currentPoint, outDirection);

			// insert router anchors if necessary
			outDirection = route(cpm, inDirection, outDirection);
		}

		cpm.applyChanges();
	}

	/**
	 * Removes volatile anchors (i.e. {@link #wasInserted(IAnchor) inserted by
	 * the router}).
	 *
	 * @param connection
	 *            The {@link Connection} from which to remove volatile anchors.
	 */
	protected void removeVolatileAnchors(Connection connection) {
		List<IAnchor> realAnchors = new ArrayList<>();
		for (IAnchor a : connection.getControlAnchors()) {
			if (!wasInserted(a)) {
				realAnchors.add(a);
			}
		}
		connection.setControlAnchors(realAnchors);
	}

	@Override
	public void route(Connection connection) {
		this.connection = connection;

		// Remove previously inserted route points, so that the Connection is
		// only constituted by the user-defined anchors.
		removeVolatileAnchors(connection);

		// Compute dynamic anchor parameters.
		updateComputationParameters(connection);

		// Insert route points where necessary.
		insertRouterAnchors(connection);
	}

	/**
	 * Inserts router anchors where necessary. Returns the {@link Vector} that
	 * points to the next point.
	 *
	 * @param cpm
	 *            The {@link ControlPointManipulator} that can be used to insert
	 *            points.
	 * @param inDirection
	 *            The {@link Vector} from the previous point to the current
	 *            point.
	 * @param outDirection
	 *            The {@link Vector} from the current point to the next point.
	 * @return The adjusted {@link Vector} from the current point to the next
	 *         point.
	 */
	protected Vector route(ControlPointManipulator cpm, Vector inDirection,
			Vector outDirection) {
		return outDirection;
	}

	/**
	 * Updates all computation parameters for the anchors of the given
	 * {@link Connection}.
	 *
	 * @param connection
	 *            The {@link Connection}.
	 */
	protected void updateComputationParameters(Connection connection) {
		// XXX: Copy current connection points before updating the computation
		// parameters for the individual DynamicAnchors so that the computation
		// of the first parameter does not influence the computation of a
		// parameter that is computed later on.
		List<Point> pts = new ArrayList<>(connection.getPointsUnmodifiable());

		// update parameters for all DynamicAnchors
		for (int i = 0; i < pts.size(); i++) {
			IAnchor anchor = connection.getAnchor(i);
			if (anchor instanceof DynamicAnchor) {
				DynamicAnchor da = ((DynamicAnchor) anchor);
				AnchorKey key = connection.getAnchorKey(i);
				// XXX: The independent copy of the points is passed to the
				// computation method.
				updateComputationParameters(pts, i, da, key);
			}
		}
	}

	/**
	 * Update's the reference point of the anchor with the given index.
	 *
	 * @param points
	 *            The {@link Connection}'s points (snapshot taken before
	 *            parameters are updated, i.e. independent from the parameter
	 *            changes).
	 * @param index
	 *            The index of the connection anchor (and anchor key) for which
	 *            the computation parameters are updated.
	 * @param anchor
	 *            The {@link DynamicAnchor} for which to update the computation
	 *            parameters.
	 * @param key
	 *            The {@link AnchorKey}, corresponding to the index, for which
	 *            to update the computation parameters.
	 */
	protected void updateComputationParameters(List<Point> points, int index,
			DynamicAnchor anchor, AnchorKey key) {
		// only update if necessary (when it changes)
		AnchoredReferencePoint referencePointParameter = anchor
				.getComputationParameter(key, AnchoredReferencePoint.class);
		Point oldRef = referencePointParameter.get();
		Point oldRefInScene = oldRef == null ? null
				: FX2Geometry.toPoint(key.getAnchored()
						.localToScene(Geometry2FX.toFXPoint(oldRef)));

		// if we have a position hint for the anchor, we need to use this as the
		// reference point
		// Point newRef = getAnchoredReferencePoint(points, index);
		Point2D newRefInConnection = Geometry2FX
				.toFXPoint(getAnchoredReferencePoint(points, index));
		Point newRefInScene = FX2Geometry
				.toPoint(getConnection().localToScene(newRefInConnection));
		if (oldRefInScene == null || !newRefInScene.equals(oldRefInScene)) {
			ObjectBinding<Point> refBinding = new ObjectBinding<Point>() {
				{
					bind(key.getAnchored().localToParentTransformProperty());
				}

				@Override
				protected Point computeValue() {
					return FX2Geometry.toPoint(key.getAnchored()
							.parentToLocal(newRefInConnection));
				}
			};
			referencePointParameter.bind(refBinding);
		}
	}

	@Override
	public boolean wasInserted(IAnchor anchor) {
		return anchor instanceof VolatileStaticAnchor;
	}
}
