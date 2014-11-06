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
package org.eclipse.gef4.mvc.fx.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.scene.Node;
import javafx.scene.transform.Scale;

import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.models.GridModel;

public class FXGridBehavior extends AbstractBehavior<Node> implements
		PropertyChangeListener {

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

	protected void applyGridCellHeight(double height) {
		((FXRootPart) getHost().getRoot()).getGridLayer().setGridHeight(height);
	}

	protected void applyGridCellWidth(double width) {
		((FXRootPart) getHost().getRoot()).getGridLayer().setGridWidth(width);
	}

	protected void applyShowGrid(boolean showGrid) {
		if (showGrid) {
			((FXRootPart) getHost().getRoot()).getGridLayer().setVisible(true);
			((FXRootPart) getHost().getRoot()).getGridLayer().setManaged(true);
		} else {
			((FXRootPart) getHost().getRoot()).getGridLayer().setVisible(false);
			((FXRootPart) getHost().getRoot()).getGridLayer().setManaged(false);
		}
	}

	protected void applyZoomGrid(boolean zoomGrid) {
		// TODO: add listener to zoom model instead and update zoom property
		// accordingly
		if (zoomGrid) {
			// bind grid scale to zoom property
			((FXRootPart) getHost().getRoot()).getGridLayer()
					.gridScaleProperty()
					.bind(((FXRootPart) getHost().getRoot()).zoomProperty());
		} else {
			((FXRootPart) getHost().getRoot()).getGridLayer()
					.gridScaleProperty().unbind();
			((FXRootPart) getHost().getRoot()).getGridLayer()
					.gridScaleProperty().set(new Scale(1, 1));
		}
	}

	@Override
	public void deactivate() {
		getHost().getRoot().getViewer().getAdapter(GridModel.class)
				.removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (GridModel.SHOW_GRID_PROPERTY.equals(evt.getPropertyName())) {
			applyShowGrid(((Boolean) evt.getNewValue()).booleanValue());
		} else if (GridModel.GRID_CELL_WIDTH_PROPERTY.equals(evt
				.getPropertyName())) {
			applyGridCellWidth(((Double) evt.getNewValue()).doubleValue());
		} else if (GridModel.GRID_CELL_HEIGHT_PROPERTY.equals(evt
				.getPropertyName())) {
			applyGridCellHeight(((Double) evt.getNewValue()).doubleValue());
		}
	}

}
