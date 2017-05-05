/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * The {@link SnappingUtil} class is a collection of utility methods that can be
 * used to provide {@link SnappingLocation}s for an {@link ISnappablePart}.
 *
 * In general, the snapping locations can be provided for ratios which are
 * applied to the width/height of the {@link ISnappablePart}'s visual:
 * {@link #computeSnappingLocations(ISnappablePart, Orientation, double...)}.
 *
 * Additionally, there is a dedicated method for determining the
 * {@link SnappingLocation}s for a part that uses a {@link Connection} for its
 * visualization:
 * {@link #computeSnappingLocations(ISnappablePart, Orientation)}.
 */
public class SnappingUtil {

	/**
	 * Returns the {@link SnappingLocation}s for the individual start/end points
	 * of the {@link Connection} that visualizes the given
	 * {@link ISnappablePart}. Depending on the given {@link Orientation},
	 * either the horizontal or vertical coordinates are evaluated for the
	 * {@link SnappingLocation}s.
	 *
	 * @param part
	 *            The {@link ISnappablePart} for which to determine the
	 *            {@link SnappingLocation}s.
	 * @param orientation
	 *            The {@link Orientation} for the {@link SnappingLocation}s.
	 * @return A {@link List} containing the determined
	 *         {@link SnappingLocation}s according to the given parameters.
	 */
	public static List<SnappingLocation> computeSnappingLocations(
			ISnappablePart<? extends Connection> part,
			Orientation orientation) {
		List<SnappingLocation> locations = new ArrayList<>();
		// consider all segments of the connection
		ObservableList<Point> points = part.getVisual().getPointsUnmodifiable();
		for (int i = 0; i < points.size() - 1; i++) {
			// current segment from point(i) to point(i+1)
			Point start = points.get(i);
			Point end = points.get(i + 1);
			// determine orientation
			Orientation segmentOrientation = start.x == end.x
					? Orientation.VERTICAL : Orientation.HORIZONTAL;
			if (segmentOrientation != orientation) {
				continue;
			}
			// transform position to scene
			Point2D startInScene = part.getVisual().localToScene(start.x,
					start.y);
			// save location
			locations.add(new SnappingLocation(part, segmentOrientation,
					orientation == Orientation.VERTICAL ? startInScene.getX()
							: startInScene.getY()));
		}
		return locations;
	}

	/**
	 * Returns the {@link SnappingLocation}s for the given
	 * {@link ISnappablePart}, {@link Orientation}, and size ratios. The size
	 * ratios are applied to the width or height of the {@link ISnappablePart}'s
	 * visual in order to determine the actual {@link SnappingLocation}s. The
	 * given {@link Orientation} determines if horizontal or vertical
	 * {@link SnappingLocation}s are determined.
	 *
	 * @param part
	 *            The {@link ISnappablePart} for which to determine the
	 *            {@link SnappingLocation}s.
	 * @param orientation
	 *            The {@link Orientation} for the {@link SnappingLocation}s.
	 * @param ratios
	 *            The width/height ratios for the {@link SnappingLocation}s.
	 * @return A {@link List} containing the determined
	 *         {@link SnappingLocation}s according to the given parameters.
	 */
	public static List<SnappingLocation> computeSnappingLocations(
			ISnappablePart<? extends Node> part, Orientation orientation,
			double... ratios) {
		Bounds boundsInScene = part.getVisual()
				.localToScene(part.getVisual().getLayoutBounds());
		List<SnappingLocation> locations = new ArrayList<>();
		for (int i = 0; i < ratios.length; i++) {
			double min, size;
			if (orientation == Orientation.VERTICAL) {
				min = boundsInScene.getMinX();
				size = boundsInScene.getWidth();
			} else {
				min = boundsInScene.getMinY();
				size = boundsInScene.getHeight();
			}
			double location = min + size * ratios[i];
			locations.add(new SnappingLocation(part, orientation, location));
		}
		return locations;
	}
}
