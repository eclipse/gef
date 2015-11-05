/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.GridModel;

import javafx.scene.Node;

/**
 * The {@link FXGridBehavior} can be registered on an {@link FXRootPart} to
 * apply the information from the {@link GridModel} to the background grid that
 * is managed by the {@link FXViewer}.
 *
 * @author anyssen
 *
 */
public class FXGridBehavior extends AbstractBehavior<Node>
		implements PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		GridModel gridModel = getHost().getRoot().getViewer()
				.getAdapter(GridModel.class);
		gridModel.addPropertyChangeListener(this);
		applyShowGrid(gridModel.isShowGrid());
		applyZoomGrid(gridModel.isZoomGrid());
		applyGridCellWidth(gridModel.getGridCellWidth());
		applyGridCellHeight(gridModel.getGridCellHeight());
	}

	/**
	 * Applies the given cell height to the background grid.
	 *
	 * @param height
	 *            The new cell height for the background grid.
	 */
	protected void applyGridCellHeight(double height) {
		getCanvas().gridCellHeightProperty().set(height);
	}

	/**
	 * Applies the given cell width to the background grid.
	 *
	 * @param width
	 *            The new cell width for the background grid.
	 */
	protected void applyGridCellWidth(double width) {
		getCanvas().gridCellWidthProperty().set(width);
	}

	/**
	 * Enables/Disables the background grid.
	 *
	 * @param showGrid
	 *            <code>true</code> to enable the background grid, otherwise
	 *            <code>false</code>.
	 */
	protected void applyShowGrid(boolean showGrid) {
		getCanvas().setShowGrid(showGrid);
	}

	/**
	 * Enables/Disables zooming of the background grid.
	 *
	 * @param zoomGrid
	 *            <code>true</code> to enable grid zooming, otherwise
	 *            <code>false</code>.
	 */
	protected void applyZoomGrid(boolean zoomGrid) {
		getCanvas().setZoomGrid(zoomGrid);
	}

	@Override
	public void deactivate() {
		getHost().getRoot().getViewer().getAdapter(GridModel.class)
				.removePropertyChangeListener(this);
		super.deactivate();
	}

	/**
	 * Returns the {@link InfiniteCanvas} of the {@link #getHost() host's}
	 * {@link FXViewer}.
	 *
	 * @return The {@link InfiniteCanvas} of the {@link #getHost() host's}
	 *         {@link FXViewer}.
	 */
	protected InfiniteCanvas getCanvas() {
		return ((FXViewer) getHost().getRoot().getViewer()).getCanvas();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (GridModel.SHOW_GRID_PROPERTY.equals(evt.getPropertyName())) {
			applyShowGrid(((Boolean) evt.getNewValue()).booleanValue());
		} else if (GridModel.GRID_CELL_WIDTH_PROPERTY
				.equals(evt.getPropertyName())) {
			applyGridCellWidth(((Double) evt.getNewValue()).doubleValue());
		} else if (GridModel.GRID_CELL_HEIGHT_PROPERTY
				.equals(evt.getPropertyName())) {
			applyGridCellHeight(((Double) evt.getNewValue()).doubleValue());
		}
	}

}
