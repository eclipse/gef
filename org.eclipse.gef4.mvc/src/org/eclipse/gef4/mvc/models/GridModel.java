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

public class GridModel implements IPropertyChangeNotifier {

	// add grid styles??
	public static final String GRID_CELL_WIDTH_PROPERTY = "gridCellWidth";
	public static final String GRID_CELL_HEIGHT_PROPERTY = "gridCellHeight";

	// whether grid should be shown
	public static final String SHOW_GRID_PROPERTY = "showGrid";
	public static final String ZOOM_GRID_PROPERTY = "zoomGrid";
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

	public double getGridCellHeight() {
		return gridCellHeight;
	}

	public double getGridCellWidth() {
		return gridCellWidth;
	}

	public boolean isShowGrid() {
		return showGrid;
	}

	public boolean isSnapToGrid() {
		return snapToGrid;
	}

	public boolean isZoomGrid() {
		return zoomGrid;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void setGridCellHeight(double gridCellHeight) {
		double oldGridHeight = this.gridCellHeight;
		this.gridCellHeight = gridCellHeight;
		pcs.firePropertyChange(GRID_CELL_WIDTH_PROPERTY, oldGridHeight,
				gridCellHeight);
	}

	public void setGridCellWidth(double gridCellWidth) {
		double oldGridCellWidth = this.gridCellWidth;
		this.gridCellWidth = gridCellWidth;
		pcs.firePropertyChange(GRID_CELL_WIDTH_PROPERTY, oldGridCellWidth,
				gridCellWidth);
	}

	public void setShowGrid(boolean showGrid) {
		boolean oldShowGrid = this.showGrid;
		this.showGrid = showGrid;
		pcs.firePropertyChange(SHOW_GRID_PROPERTY, oldShowGrid, showGrid);
	}

	public void setSnapToGrid(boolean snapToGrid) {
		boolean oldSnapToGrid = this.snapToGrid;
		this.snapToGrid = snapToGrid;
		pcs.firePropertyChange(SNAP_TO_GRID_PROPERTY, oldSnapToGrid,
				snapToGrid);
	}

	public void setZoomGrid(boolean zoomGrid) {
		boolean oldZoomGrid = this.zoomGrid;
		this.zoomGrid = zoomGrid;
		pcs.firePropertyChange(ZOOM_GRID_PROPERTY, oldZoomGrid, zoomGrid);
	}

}
