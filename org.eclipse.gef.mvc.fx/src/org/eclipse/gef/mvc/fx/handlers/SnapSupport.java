/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.handlers;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.models.GridModel;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;

import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * The {@link SnapSupport} can be used within an {@link IHandler} implementation
 * for snapping scene coordinates to grid points.
 */
public class SnapSupport {

	private IHandler handler;

	/**
	 * Constructs a new {@link SnapSupport} for the given host {@link IHandler},
	 * which gives access to the application's {@link IViewer} and scenegraph.
	 *
	 * @param handler
	 *            The host {@link IHandler} for this {@link SnapSupport}.
	 */
	public SnapSupport(IHandler handler) {
		this.handler = handler;
	}

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

	/**
	 * Snaps the given position (in scene coordinates) to a grid position (in
	 * scene coordinates).
	 *
	 * @param sceneX
	 *            Original position's x-coordinate.
	 * @param sceneY
	 *            Original position's y-coordinate.
	 * @return The snapped position in scene coordinates.
	 */
	public Point snapToGrid(double sceneX, double sceneY) {
		IViewer viewer = handler.getHost().getRoot().getViewer();
		return snapToGrid(sceneX, sceneY,
				viewer.<GridModel> getAdapter(GridModel.class),
				getSnapToGridGranularityX(), getSnapToGridGranularityY(),
				getGridLocalVisual(viewer));
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
	public Point snapToGrid(final double sceneX, final double sceneY,
			GridModel gridModel, final double gridCellWidthFraction,
			final double gridCellHeightFraction, Node gridLocalVisual) {
		// do nothing if snap to grid is disabled
		if (!gridModel.isSnapToGrid()) {
			return new Point(sceneX, sceneY);
		}
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
