/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * The {@link GridModel} stores information about a background grid, i.e. cell
 * width and cell height. It also stores flags indicating if the grid should be
 * visible, if the grid should zoom with the contents, and if contents should
 * snap to the grid.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class GridModel {

	/**
	 * The default value for {@link #isZoomGrid()}.
	 */
	public static final boolean ZOOM_GRID_DEFAULT = true;

	/**
	 * The default value for {@link #isShowGrid()}.
	 */
	public static final boolean SHOW_GRID_DEFAULT = true;

	/**
	 * The default grid cell height.
	 */
	public static final double GRID_CELL_HEIGHT_DEFAULT = 10;

	/**
	 * The default grid cell width.
	 */
	public static final double GRID_CELL_WIDTH_DEFAULT = 10;

	/**
	 * Name of the "grid cell width" property.
	 */
	public static final String GRID_CELL_WIDTH_PROPERTY = "gridCellWidth";

	/**
	 * Name of the "grid cell height" property.
	 */
	public static final String GRID_CELL_HEIGHT_PROPERTY = "gridCellHeight";

	/**
	 * Name of the "show grid" property.
	 */
	public static final String SHOW_GRID_PROPERTY = "showGrid";

	/**
	 * Name of the "zoom grid" property.
	 */
	public static final String ZOOM_GRID_PROPERTY = "zoomGrid";

	private DoubleProperty gridCellWidthProperty = new SimpleDoubleProperty(
			this, GRID_CELL_WIDTH_PROPERTY, GRID_CELL_WIDTH_DEFAULT);
	private DoubleProperty gridCellHeightProperty = new SimpleDoubleProperty(
			this, GRID_CELL_HEIGHT_PROPERTY, GRID_CELL_HEIGHT_DEFAULT);
	private BooleanProperty showGridProperty = new SimpleBooleanProperty(this,
			SHOW_GRID_PROPERTY, SHOW_GRID_DEFAULT);
	private BooleanProperty zoomGridProperty = new SimpleBooleanProperty(this,
			ZOOM_GRID_PROPERTY, ZOOM_GRID_DEFAULT);

	/**
	 * Returns the grid cell height.
	 *
	 * @return The grid cell height.
	 */
	public double getGridCellHeight() {
		return gridCellHeightProperty.get();
	}

	/**
	 * Returns the grid cell width.
	 *
	 * @return The grid cell width.
	 */
	public double getGridCellWidth() {
		return gridCellWidthProperty.get();
	}

	/**
	 * Returns a double property representing the grid cell height.
	 *
	 * @return A double property named {@link #GRID_CELL_HEIGHT_PROPERTY}.
	 */
	public DoubleProperty gridCellHeightProperty() {
		return gridCellHeightProperty;
	}

	/**
	 * Returns a double property representing the grid cell width.
	 *
	 * @return A double property named {@link #GRID_CELL_WIDTH_PROPERTY}.
	 */
	public DoubleProperty gridCellWidthProperty() {
		return gridCellWidthProperty;
	}

	/**
	 * Returns <code>true</code> if the grid is visible, otherwise
	 * <code>false</code>.
	 *
	 * @return <code>true</code> if the grid is visible, otherwise
	 *         <code>false</code>.
	 */
	public boolean isShowGrid() {
		return showGridProperty.get();
	}

	/**
	 * Returns <code>true</code> if the grid is zooming with the contents,
	 * otherwise <code>false</code>.
	 *
	 * @return <code>true</code> if the grid is zooming with the contents,
	 *         otherwise <code>false</code>.
	 */
	public boolean isZoomGrid() {
		return zoomGridProperty.get();
	}

	/**
	 * Sets the grid cell height to the given value.
	 *
	 * @param gridCellHeight
	 *            The new grid cell height.
	 */
	public void setGridCellHeight(double gridCellHeight) {
		gridCellHeightProperty.set(gridCellHeight);
	}

	/**
	 * Sets the grid cell width to the given value.
	 *
	 * @param gridCellWidth
	 *            The new grid cell width.
	 */
	public void setGridCellWidth(double gridCellWidth) {
		gridCellWidthProperty.set(gridCellWidth);
	}

	/**
	 * Shows/Hides the grid depending on the given value.
	 *
	 * @param showGrid
	 *            <code>true</code> in order to show the grid, or
	 *            <code>false</code> in order to hide it.
	 */
	public void setShowGrid(boolean showGrid) {
		showGridProperty.set(showGrid);
	}

	/**
	 * Enables/Disables grid zooming depending on the given value.
	 *
	 * @param zoomGrid
	 *            <code>true</code> in order to zoom the grid with the contents,
	 *            or <code>false</code> in order to not zoom the grid.
	 */
	public void setZoomGrid(boolean zoomGrid) {
		zoomGridProperty.set(zoomGrid);
	}

	/**
	 * Returns a boolean property whose value indicates whether grid is to be
	 * shown.
	 *
	 * @return A boolean property named {@link #SHOW_GRID_PROPERTY}.
	 */
	public BooleanProperty showGridProperty() {
		return showGridProperty;
	}

	/**
	 * Returns a boolean property whose value indicates whether grid is to be
	 * zoomed.
	 *
	 * @return A boolean property named {@link #ZOOM_GRID_PROPERTY}.
	 */
	public BooleanProperty zoomGridProperty() {
		return zoomGridProperty;
	}
}
