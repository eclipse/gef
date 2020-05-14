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

import java.util.List;

import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Point;

import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * The {@link StraightRouter} is an {@link IConnectionRouter} that leaves the
 * {@link Connection}'s control points untouched and only provides reference
 * points for the {@link Connection}'s anchors.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class StraightRouter extends AbstractRouter {

	/**
	 * Returns the reference point for the anchor at the given index.
	 *
	 * @param index
	 *            The index specifying the anchor for which to provide a
	 *            reference point.
	 * @return The reference point for the anchor at the given index within the
	 *         local coordinate system of the anchored, which is the
	 *         Connection's curve.
	 */
	@Override
	protected Point getAnchoredReferencePoint(List<Point> points,
			int index) {
		if (index < 0 || index >= points.size()) {
			throw new IndexOutOfBoundsException();
		}

		// compute new reference point
		Point newRef = null;
		Point pred = getPred(points, index);
		Point succ = getSucc(points, index);
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
		} else if (succ == null && pred != null) {
			newRef = pred;
		} else if (pred == null && succ != null) {
			newRef = succ;
		} else {
			newRef = new Line(pred, succ).get(0.5);
		}
		return newRef;
	}

	private Point getNeighbor(List<Point> points, int anchorIndex, int step) {
		List<IAnchor> anchors = getConnection().getAnchorsUnmodifiable();
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
			if (predAnchorage == null || predAnchorage == getConnection()) {
				// anchor is static
				Point position = points.get(i);
				Point2D local = anchorage.sceneToLocal(
						getConnection().localToScene(position.x, position.y));
				if (!anchorage.contains(local)) {
					return position;
				}
			} else {
				// anchor position depends on anchorage
				Point position = getAnchorageGeometry(i).getBounds()
						.getCenter();
				if (position == null || Double.isNaN(position.x)
						|| Double.isNaN(position.y)) {
					throw new IllegalStateException(
							"cannot determine anchorage center");
				}
				return position;
			}
		}

		// no neighbor found
		return null;
	}

	private Point getPred(List<Point> points, int anchorIndex) {
		return getNeighbor(points, anchorIndex, -1);
	}

	private Point getSucc(List<Point> points, int anchorIndex) {
		return getNeighbor(points, anchorIndex, 1);
	}
}
