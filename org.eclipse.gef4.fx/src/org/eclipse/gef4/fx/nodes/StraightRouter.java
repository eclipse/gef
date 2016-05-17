/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen  (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.nodes;

import java.util.List;

import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;

import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * The {@link StraightRouter} is an {@link IConnectionRouter} that leaves the
 * {@link Connection}'s control points untouched and only provides reference
 * points for the {@link Connection}'s anchors.
 *
 * @author mwienand
 *
 */
public class StraightRouter extends AbstractRouter {

	/**
	 * Returns the reference point for the anchor at the given index.
	 *
	 * @param connection
	 *            The {@link Connection} that is currently routed.
	 * @param index
	 *            The index specifying the anchor for which to provide a
	 *            reference point.
	 * @return The reference point for the anchor at the given index within the
	 *         local coordinate system of the anchored, which is the
	 *         Connection's curve.
	 */
	@Override
	protected Point getAnchoredReferencePoint(Connection connection,
			int index) {
		if (index < 0 || index >= connection.getPointsUnmodifiable().size()) {
			throw new IndexOutOfBoundsException();
		}

		// compute new reference point
		Point newRef = null;
		Point pred = getPred(connection, index);
		Point succ = getSucc(connection, index);
		if (pred == null && succ == null) {
			/*
			 * Neither predecessor nor successor can be identified. This can
			 * happen for the initialization of connections when a static
			 * position is inside the anchorage of the current anchor. This
			 * means, the reference point that is returned now will be discarded
			 * in a succeeding call (we have to come up with some value here for
			 * the DynamicAnchor to work with).
			 */
			newRef = new Point();
		} else if (pred != null) {
			newRef = pred;
		} else if (succ != null) {
			newRef = succ;
		} else {
			newRef = new Line(pred, succ).get(0.5);
		}
		return newRef;
	}

	// TODO: move to utility && replace with safe algorithm
	private Point getCenter(Connection connection, Node anchorageNode) {
		Point center = FX2Geometry
				.toRectangle(connection.getCurve()
						.sceneToLocal(anchorageNode
								.localToScene(anchorageNode.getLayoutBounds())))
				.getCenter();
		if (Double.isNaN(center.x) || Double.isNaN(center.y)) {
			return null;
		}
		return center;
	}

	private Point getNeighbor(Connection connection, int anchorIndex,
			int step) {
		List<IAnchor> anchors = connection.getAnchorsUnmodifiable();
		IAnchor anchor = anchors.get(anchorIndex);
		if (!(anchor instanceof DynamicAnchor)) {
			throw new IllegalStateException(
					"Specified anchor '" + anchor + "' is no DynamicAnchor.");
		}
		Node anchorage = anchor.getAnchorage();

		// first uncontained static anchor (no anchorage)
		// or first anchorage center
		for (int i = anchorIndex + step; i < anchors.size()
				&& i >= 0; i += step) {
			IAnchor predAnchor = anchors.get(i);
			if (predAnchor == null) {
				throw new IllegalStateException(
						"connection inconsistent (null anchor)");
			}
			Node predAnchorage = predAnchor.getAnchorage();
			if (predAnchorage == null || predAnchorage == connection) {
				// anchor is static
				AnchorKey anchorKey = connection.getAnchorKey(i);
				Point position = predAnchor.getPosition(anchorKey);
				if (position == null) {
					throw new IllegalStateException(
							"connection inconsistent (null position)");
				}
				Point2D local = anchorage.sceneToLocal(anchorKey.getAnchored()
						.localToScene(position.x, position.y));
				// TODO: NPE maybe local is null?
				if (!anchorage.contains(local)) {
					return position;
				}
			} else {
				// anchor position depends on anchorage
				Point position = getCenter(connection, predAnchorage);
				if (position == null) {
					throw new IllegalStateException(
							"cannot determine anchorage center");
				}
				return position;
			}
		}

		// no neighbor found
		return null;
	}

	private Point getPred(Connection connection, int anchorIndex) {
		return getNeighbor(connection, anchorIndex, -1);
	}

	private Point getSucc(Connection connection, int anchorIndex) {
		return getNeighbor(connection, anchorIndex, 1);
	}

	@Override
	public void route(Connection connection) {
		if (connection.getPointsUnmodifiable().size() < 2) {
			return;
		}
		List<IAnchor> anchors = connection.getAnchorsUnmodifiable();
		for (int i = 0; i < anchors.size(); i++) {
			// we do not have to update the reference point for the
			// given key, because the corresponding position just
			// changed, so it was updated already
			if (anchors.get(i) instanceof DynamicAnchor) {
				updateComputationParameters(connection, i);
			}
		}
	}

	@Override
	public boolean wasInserted(IAnchor anchor) {
		return false;
	}

}
