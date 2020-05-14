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
package org.eclipse.gef.mvc.fx.handlers;

import java.util.List;
import java.util.function.Predicate;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 * The {@link SnapToGeometry} is an {@link ISnapToStrategy} implementation that
 * queries {@link SnappingLocation}s for all {@link #isRelevant(IContentPart)
 * relevant} {@link IContentPart}s within the viewer of the currently
 * {@link #getSnappedPart() snapped} part.
 */
public class SnapToGeometry extends AbstractSnapTo implements ISnapToStrategy {

	/**
	 * The role for the adapter that provides snapping locations for relevant
	 * parts.
	 */
	public static final String TARGET_SNAPPING_LOCATION_PROVIDER = "SnapToGeometryTargetSnappingLocationProvider";

	/**
	 * The role for the adapter that provides snapping locations for relevant
	 * parts.
	 */
	public static final String SOURCE_SNAPPING_LOCATION_PROVIDER = "SnapToGeometrySourceSnappingLocationProvider";

	/**
	 * The IS_VISIBLE {@link Predicate} tests if the given {@link IVisualPart}
	 * is fully visible within the viewport.
	 */
	protected Predicate<IContentPart<? extends Node>> IS_VISIBLE = (p) -> {
		// get viewport
		InfiniteCanvas canvas = (InfiniteCanvas) getSnappedPart().getViewer()
				.getCanvas();
		// no snapping feedback for parts outside the viewport
		Bounds boundsInCanvas = canvas.sceneToLocal(
				p.getVisual().localToScene(p.getVisual().getLayoutBounds()));
		if (boundsInCanvas.getMinX() > canvas.getWidth()
				|| boundsInCanvas.getMinY() > canvas.getHeight()
				|| boundsInCanvas.getMaxX() < 0
				|| boundsInCanvas.getMaxY() < 0) {
			return false;
		}
		return true;
	};

	/**
	 * The IS_LEAF {@link Predicate} tests if the given {@link IVisualPart} has
	 * no children.
	 */
	protected Predicate<IContentPart<? extends Node>> IS_LEAF = (p) -> {
		return p.getChildrenUnmodifiable().isEmpty();
	};

	@Override
	protected String getTargetLocationProviderRole() {
		return TARGET_SNAPPING_LOCATION_PROVIDER;
	}

	@Override
	public String getSourceLocationProviderRole() {
		return SOURCE_SNAPPING_LOCATION_PROVIDER;
	}

	@Override
	protected boolean isRelevant(IContentPart<? extends Node> part) {
		return IS_VISIBLE.test(part) && IS_LEAF.test(part);
	}

	@Override
	public Dimension snap(Orientation orientation, double positionInScene) {
		double minDistance = 0d;
		SnappingLocation snappingLocation = null;
		if (orientation != Orientation.HORIZONTAL
				&& orientation != Orientation.VERTICAL) {
			throw new IllegalArgumentException("Wrong Orientation");
		}
		boolean horizontal = orientation == Orientation.HORIZONTAL;
		List<SnappingLocation> testLocations = horizontal
				? getHorizontalTargetLocations()
				: getVerticalTargetLocations();
		// TODO: binary search
		for (SnappingLocation sl : testLocations) {
			double location = sl.getPositionInScene();
			double distance = positionInScene - location;
			if (snappingLocation == null
					|| Math.abs(distance) < Math.abs(minDistance)) {
				minDistance = distance;
				snappingLocation = sl;
			}
		}
		if (snappingLocation == null) {
			return null;
		}
		double d = snappingLocation.getPositionInScene() - positionInScene;
		return horizontal ? new Dimension(d, 0) : new Dimension(0, d);
	}
}