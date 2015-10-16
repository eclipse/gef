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

import org.eclipse.gef4.fx.nodes.FXGridLayer;
import org.eclipse.gef4.fx.nodes.ScrollPaneEx;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.GridModel;
import org.eclipse.gef4.mvc.models.ViewportModel;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

/**
 * The {@link FXGridBehavior} can be registered on an {@link FXRootPart} to
 * apply the information from the {@link GridModel} to the {@link FXGridLayer}
 * that is managed by the {@link FXViewer}.
 *
 * @author anyssen
 *
 */
public class FXGridBehavior extends AbstractBehavior<Node>
		implements PropertyChangeListener {

	private boolean isListeningOnViewport = false;

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
	 * Applies the given cell height to the {@link FXGridLayer}.
	 *
	 * @param height
	 *            The new cell height for the {@link FXGridLayer}.
	 */
	protected void applyGridCellHeight(double height) {
		getGridLayer().setGridHeight(height);
	}

	/**
	 * Applies the given cell width to the {@link FXGridLayer}.
	 *
	 * @param width
	 *            The new cell width for the {@link FXGridLayer}.
	 */
	protected void applyGridCellWidth(double width) {
		getGridLayer().setGridWidth(width);
	}

	/**
	 * Enables/Disables the {@link FXGridLayer}.
	 *
	 * @param showGrid
	 *            <code>true</code> to enable the {@link FXGridLayer}, otherwise
	 *            <code>false</code>.
	 */
	protected void applyShowGrid(boolean showGrid) {
		if (showGrid) {
			getGridLayer().setVisible(true);
			getGridLayer().setManaged(true);
		} else {
			getGridLayer().setVisible(false);
			getGridLayer().setManaged(false);
		}
	}

	/**
	 * Enables/Disables zooming of the {@link FXGridLayer}. Registers a listener
	 * on the {@link ViewportModel} to keep the {@link FXGridLayer}'s zoom level
	 * in sync with the viewport zoom level.
	 *
	 * @param zoomGrid
	 *            <code>true</code> to enable grid zooming, otherwise
	 *            <code>false</code>.
	 */
	protected void applyZoomGrid(boolean zoomGrid) {
		ViewportModel viewportModel = getHost().getRoot().getViewer()
				.getAdapter(ViewportModel.class);
		if (zoomGrid) {
			if (!isListeningOnViewport) {
				viewportModel.addPropertyChangeListener(this);
				isListeningOnViewport = true;
				// apply current contents transform
				getGridLayer().gridTransformProperty()
						.bind(getScrollPane().contentTransformProperty());
			}
		} else {
			if (isListeningOnViewport) {
				viewportModel.removePropertyChangeListener(this);
				isListeningOnViewport = false;
				// reset grid scale to (1, 1)
				getGridLayer().gridTransformProperty().unbind();
				getGridLayer().gridTransformProperty().set(new Affine());
			}
		}
	}

	@Override
	public void deactivate() {
		getHost().getRoot().getViewer().getAdapter(GridModel.class)
				.removePropertyChangeListener(this);

		if (isListeningOnViewport) {
			ViewportModel viewportModel = getHost().getRoot().getViewer()
					.getAdapter(ViewportModel.class);
			viewportModel.removePropertyChangeListener(this);
		}

		super.deactivate();
	}

	/**
	 * Returns the {@link FXGridLayer} of the {@link #getHost() host's}
	 * {@link FXViewer}.
	 *
	 * @return The {@link FXGridLayer} of the {@link #getHost() host's}
	 *         {@link FXViewer}.
	 */
	protected FXGridLayer getGridLayer() {
		return ((FXViewer) getHost().getRoot().getViewer()).getGridLayer();
	}

	/**
	 * Returns the {@link ScrollPaneEx} of the {@link #getHost() host's}
	 * {@link FXViewer}.
	 *
	 * @return The {@link ScrollPaneEx} of the {@link #getHost() host's}
	 *         {@link FXViewer}.
	 */
	protected ScrollPaneEx getScrollPane() {
		return ((FXViewer) getHost().getRoot().getViewer()).getScrollPane();
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
