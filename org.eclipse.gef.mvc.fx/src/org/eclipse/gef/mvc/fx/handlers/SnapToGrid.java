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

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.models.GridModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;

import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * The {@link SnapToGrid} is an {@link ISnapToStrategy} implementation that
 * snaps to grid locations according to the {@link GridModel}.
 */
public class SnapToGrid extends AbstractSnapTo implements ISnapToStrategy {

	/**
	 * The role for the adapter that provides snapping locations for relevant
	 * parts.
	 */
	public static final String SOURCE_SNAPPING_LOCATION_PROVIDER = "SnapToGridSourceSnappingLocationProvider";

	/**
	 * Determines a visual within the given {@link IViewer} in which grid
	 * positions are at
	 * <code>(n * grid-cell-width, m * grid-cell-height)</code>. Per default,
	 * the content group of the {@link InfiniteCanvas} is returned for an
	 * {@link InfiniteCanvasViewer}. For other {@link IViewer} implementations,
	 * the visual of the root part is used.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to determine a grid-local
	 *            visual.
	 * @return A grid-local visual for the given {@link IViewer}.
	 */
	protected Node getGridLocalVisual(IViewer viewer) {
		return viewer instanceof InfiniteCanvasViewer
				? ((InfiniteCanvasViewer) viewer).getCanvas().getContentGroup()
				: viewer.getRootPart().getVisual();
	}

	@Override
	public double getMaximumSnappingDistance() {
		return Double.MAX_VALUE;
	}

	@Override
	protected String getTargetLocationProviderRole() {
		return null;
	}

	/**
	 * Returns the horizontal granularity for "snap-to-grid" where
	 * <code>1</code> means it will snap to integer grid positions.
	 *
	 * @return The horizontal granularity for "snap-to-grid".
	 */
	protected double getSnapToGridGranularityX() {
		return 1;
	}

	/**
	 * Returns the vertical granularity for "snap-to-grid" where <code>1</code>
	 * means it will snap to integer grid positions.
	 *
	 * @return The vertical granularity for "snap-to-grid".
	 */
	protected double getSnapToGridGranularityY() {
		return 1;
	}

	@Override
	public String getSourceLocationProviderRole() {
		return SOURCE_SNAPPING_LOCATION_PROVIDER;
	}

	@Override
	protected boolean isRelevant(IContentPart<? extends Node> part) {
		// TODO: prevent part iteration
		return false;
	}

	@Override
	public Dimension snap(Orientation orientation, double positionInScene) {
		if (orientation != Orientation.HORIZONTAL
				&& orientation != Orientation.VERTICAL) {
			throw new IllegalArgumentException(
					"The given Orientation is neither HORIZONTAL nor VERTICAL.");
		}
		IViewer viewer = getSnappedPart().getViewer();
		Point snapped = snapToGrid(positionInScene, positionInScene,
				viewer.<GridModel> getAdapter(GridModel.class),
				getSnapToGridGranularityX(), getSnapToGridGranularityY(),
				getGridLocalVisual(viewer));
		if (orientation == Orientation.HORIZONTAL) {
			return new Dimension(snapped.x - positionInScene, 0);
		} else {
			return new Dimension(0, snapped.y - positionInScene);
		}
	}

	/**
	 * Snaps the given position (in scene coordinates) to a grid position (in
	 * scene coordinates). The grid positions are specified by the given
	 * {@link GridModel} and the given cell size fractions.
	 *
	 * @param sceneX
	 *            The x-coordinate of the current position (in scene
	 *            coordinates).
	 * @param sceneY
	 *            The y-coordinate of the current position (in scene
	 *            coordinates).
	 * @param gridModel
	 *            The {@link GridModel} that specifies the grid positions.
	 * @param gridCellWidthFraction
	 *            The cell width fraction that determines if the x-coordinate is
	 *            snapped to full (1.0), halve (0.5), etc. grid positions.
	 * @param gridCellHeightFraction
	 *            The cell height fraction that determines if the y-coordinate
	 *            is snapped to full (1.0), halve (0.5), etc. grid positions.
	 * @param gridLocalVisual
	 *            A visual within the coordinate system where grid positions are
	 *            at <code>(n * grid-cell-width, m * grid-cell-height)</code>.
	 * @return The resulting snapped position in scene coordinates.
	 */
	protected Point snapToGrid(final double sceneX, final double sceneY,
			GridModel gridModel, final double gridCellWidthFraction,
			final double gridCellHeightFraction, Node gridLocalVisual) {
		// transform to grid local coordinates
		Point2D gridLocalPosition = gridLocalVisual.sceneToLocal(sceneX,
				sceneY);
		// snap to (nearest) grid point (add 0.5 so that the nearest grid
		// position is computed)
		double gcw = gridCellWidthFraction * gridModel.getGridCellWidth();
		double nearest = gridLocalPosition.getX() > 0 ? 0.5 : -0.5;
		int xs = (int) (gridLocalPosition.getX() / gcw + nearest);
		double gch = gridCellHeightFraction * gridModel.getGridCellHeight();
		nearest = gridLocalPosition.getY() > 0 ? 0.5 : -0.5;
		int ys = (int) (gridLocalPosition.getY() / gch + nearest);
		double nx = xs * gcw;
		double ny = ys * gch;
		// transform to scene coordinates
		Point2D newPositionInScene = gridLocalVisual.localToScene(nx, ny);
		return new Point(newPositionInScene.getX(), newPositionInScene.getY());
	}
}
