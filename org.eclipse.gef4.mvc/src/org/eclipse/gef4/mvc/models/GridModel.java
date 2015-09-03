/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;

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
public class GridModel implements IPropertyChangeNotifier {

	// add grid styles??
	/**
	 * Name of the "grid cell width" property.
	 */
	public static final String GRID_CELL_WIDTH_PROPERTY = "gridCellWidth";
	/**
	 * Name of the "grid cell height" property.
	 */
	public static final String GRID_CELL_HEIGHT_PROPERTY = "gridCellHeight";

	// whether grid should be shown
	/**
	 * Name of the "show grid" property.
	 */
	public static final String SHOW_GRID_PROPERTY = "showGrid";
	/**
	 * Name of the "zoom grid" property.
	 */
	public static final String ZOOM_GRID_PROPERTY = "zoomGrid";
	/**
	 * Name of the "snap to grid" property.
	 */
	public static final String SNAP_TO_GRID_PROPERTY = "snapToGrid";

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private double gridCellWidth = 10;
	private double gridCellHeight = 10;
	private boolean showGrid = true;
	private boolean snapToGrid = false;
	private boolean zoomGrid = true;

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Returns the grid cell height.
	 *
	 * @return The grid cell height.
	 */
	public double getGridCellHeight() {
		return gridCellHeight;
	}

	/**
	 * Returns the grid cell width.
	 *
	 * @return The grid cell width.
	 */
	public double getGridCellWidth() {
		return gridCellWidth;
	}

	/**
	 * Returns <code>true</code> if the grid is visible, otherwise
	 * <code>false</code>.
	 *
	 * @return <code>true</code> if the grid is visible, otherwise
	 *         <code>false</code>.
	 */
	public boolean isShowGrid() {
		return showGrid;
	}

	/**
	 * Returns <code>true</code> if snap to grid is enabled, otherwise
	 * <code>false</code>.
	 *
	 * @return <code>true</code> if snap to grid is enabled, otherwise
	 *         <code>false</code>.
	 */
	public boolean isSnapToGrid() {
		return snapToGrid;
	}

	/**
	 * Returns <code>true</code> if the grid is zooming with the contents,
	 * otherwise <code>false</code>.
	 *
	 * @return <code>true</code> if the grid is zooming with the contents,
	 *         otherwise <code>false</code>.
	 */
	public boolean isZoomGrid() {
		return zoomGrid;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Sets the grid cell height to the given value.
	 *
	 * @param gridCellHeight
	 *            The new grid cell height.
	 */
	public void setGridCellHeight(double gridCellHeight) {
		double oldGridHeight = this.gridCellHeight;
		this.gridCellHeight = gridCellHeight;
		pcs.firePropertyChange(GRID_CELL_WIDTH_PROPERTY, oldGridHeight,
				gridCellHeight);
	}

	/**
	 * Sets the grid cell width to the given value.
	 *
	 * @param gridCellWidth
	 *            The new grid cell width.
	 */
	public void setGridCellWidth(double gridCellWidth) {
		double oldGridCellWidth = this.gridCellWidth;
		this.gridCellWidth = gridCellWidth;
		pcs.firePropertyChange(GRID_CELL_WIDTH_PROPERTY, oldGridCellWidth,
				gridCellWidth);
	}

	/**
	 * Shows/Hides the grid depending on the given value.
	 *
	 * @param showGrid
	 *            <code>true</code> in order to show the grid, or
	 *            <code>false</code> in order to hide it.
	 */
	public void setShowGrid(boolean showGrid) {
		boolean oldShowGrid = this.showGrid;
		this.showGrid = showGrid;
		pcs.firePropertyChange(SHOW_GRID_PROPERTY, oldShowGrid, showGrid);
	}

	/**
	 * Enables/Disables snap to grid depending on the given value.
	 *
	 * @param snapToGrid
	 *            <code>true</code> in order to enable snap-to-grid, or
	 *            <code>false</code> in order to disable it.
	 */
	public void setSnapToGrid(boolean snapToGrid) {
		boolean oldSnapToGrid = this.snapToGrid;
		this.snapToGrid = snapToGrid;
		pcs.firePropertyChange(SNAP_TO_GRID_PROPERTY, oldSnapToGrid,
				snapToGrid);
	}

	/**
	 * Enables/Disables grid zooming depending on the given value.
	 *
	 * @param zoomGrid
	 *            <code>true</code> in order to zoom the grid with the contents,
	 *            or <code>false</code> in order to not zoom the grid.
	 */
	public void setZoomGrid(boolean zoomGrid) {
		boolean oldZoomGrid = this.zoomGrid;
		this.zoomGrid = zoomGrid;
		pcs.firePropertyChange(ZOOM_GRID_PROPERTY, oldZoomGrid, zoomGrid);
	}

}
