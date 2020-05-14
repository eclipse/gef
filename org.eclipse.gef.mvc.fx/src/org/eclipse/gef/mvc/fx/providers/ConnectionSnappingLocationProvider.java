/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polyline;
import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;

import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 */
public class ConnectionSnappingLocationProvider
		implements ISnappingLocationProvider {

	/**
	 * Returns the {@link Connection} that is used as the basis for
	 * determination of {@link SnappingLocation}s for the given
	 * {@link IContentPart}.
	 *
	 * @param part
	 *            The {@link IContentPart} for which to determine a
	 *            {@link Connection} visual.
	 * @return The {@link Connection} that is used as the basis for
	 *         determination of {@link SnappingLocation}s for the given
	 *         {@link IContentPart}.
	 */
	protected Connection getConnection(IContentPart<? extends Node> part) {
		Node visual = part.getVisual();
		if (visual instanceof Connection) {
			return (Connection) visual;
		}
		return null;
	}

	@Override
	public List<SnappingLocation> getHorizontalSnappingLocations(
			IContentPart<? extends Node> part) {
		return getSnappingLocations(part, Orientation.HORIZONTAL);
	}

	/**
	 * Returns {@link SnappingLocation}s with given {@link Orientation} for the
	 * {@link #getConnection(IContentPart)} of the given {@link IContentPart}.
	 *
	 * @param part
	 *            The {@link IContentPart} for which to determine
	 *            {@link SnappingLocation}s.
	 * @param orientation
	 *            The {@link Orientation} for the locations.
	 * @return The {@link SnappingLocation}s for the given {@link IContentPart}
	 *         and {@link Orientation}.
	 */
	protected List<SnappingLocation> getSnappingLocations(
			IContentPart<? extends Node> part, Orientation orientation) {
		Connection connection = getConnection(part);
		if (connection == null) {
			return Collections.emptyList();
		}
		List<SnappingLocation> locs = new ArrayList<>();
		// determine snapping locations for orthogonal segments
		Polyline polyline = new Polyline(
				connection.getPointsUnmodifiable().toArray(new Point[] {}));
		for (Line segment : polyline.toBezier()) {
			Point p1 = segment.getP1();
			Point p2 = segment.getP2();
			if (p1.x == p2.x && orientation == Orientation.HORIZONTAL) {
				locs.add(new SnappingLocation(part, orientation, p1.x));
			} else if (p1.y == p2.y && orientation == Orientation.VERTICAL) {
				locs.add(new SnappingLocation(part, orientation, p1.y));
			}
		}
		return locs;
	}

	@Override
	public List<SnappingLocation> getVerticalSnappingLocations(
			IContentPart<? extends Node> part) {
		return getSnappingLocations(part, Orientation.VERTICAL);
	}
}
