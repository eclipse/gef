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
import java.util.List;

import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;

import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 * The {@link BoundsSnappingLocationProvider} determines
 * {@link SnappingLocation}s along the bounds of the individual
 * {@link IContentPart}s that contribute to snapping. By default, only the
 * minimum and maximum locations within the bounds are used for snapping.
 */
public class BoundsSnappingLocationProvider
		implements ISnappingLocationProvider {

	@Override
	public List<SnappingLocation> getHorizontalSnappingLocations(
			IContentPart<? extends Node> part) {
		return getSnappingLocations(part, Orientation.HORIZONTAL, 0d, 1d);
	}

	/**
	 * Iterates over the given ratios and interpolates positions within the
	 * bounds for the individual ratios.
	 *
	 * @param part
	 *            The {@link IContentPart} for which {@link SnappingLocation}s
	 *            are computed.
	 * @param orient
	 *            The {@link Orientation} for the {@link SnappingLocation}s.
	 * @param ratios
	 *            The ratios at which snapping locations should be placed along
	 *            the bounds.
	 * @return The {@link SnappingLocation}s for the given {@link IContentPart}
	 *         according to its bounds and the given ratios.
	 */
	protected List<SnappingLocation> getSnappingLocations(
			IContentPart<? extends Node> part, Orientation orient,
			double... ratios) {
		Rectangle bounds = NodeUtils.localToScene(part.getVisual(),
				NodeUtils.getShapeBounds(part.getVisual())).getBounds();
		List<SnappingLocation> locs = new ArrayList<>();
		if (orient == Orientation.HORIZONTAL) {
			for (double r : ratios) {
				locs.add(new SnappingLocation(part, Orientation.HORIZONTAL,
						bounds.getX() + r * bounds.getWidth()));
			}
		} else if (orient == Orientation.VERTICAL) {
			for (double r : ratios) {
				locs.add(new SnappingLocation(part, Orientation.VERTICAL,
						bounds.getY() + r * bounds.getHeight()));
			}
		}
		return locs;
	}

	@Override
	public List<SnappingLocation> getVerticalSnappingLocations(
			IContentPart<? extends Node> part) {
		return getSnappingLocations(part, Orientation.VERTICAL, 0d, 1d);
	}
}
