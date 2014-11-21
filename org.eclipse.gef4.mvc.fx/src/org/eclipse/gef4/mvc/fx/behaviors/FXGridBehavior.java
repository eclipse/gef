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

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.models.GridModel;
import org.eclipse.gef4.mvc.models.ViewportModel;

public class FXGridBehavior extends AbstractBehavior<Node> implements
		PropertyChangeListener {

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

	protected void applyContentsTransform(AffineTransform contentsTransform) {
		double sx = contentsTransform.getScaleX();
		double sy = contentsTransform.getScaleY();
		((FXRootPart) getHost().getRoot()).getGridLayer().gridScaleProperty()
				.set(new Scale(sx, sy));
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
		ViewportModel viewportModel = getHost().getRoot().getViewer()
				.getAdapter(ViewportModel.class);
		if (zoomGrid) {
			if (!isListeningOnViewport) {
				viewportModel.addPropertyChangeListener(this);
				isListeningOnViewport = true;
				// apply current contents transform
				applyContentsTransform(viewportModel.getContentsTransform());
			}
		} else {
			if (isListeningOnViewport) {
				viewportModel.removePropertyChangeListener(this);
				isListeningOnViewport = false;
				// reset grid scale to (1, 1)
				((FXRootPart) getHost().getRoot()).getGridLayer()
						.gridScaleProperty().set(new Scale(1, 1));
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
		} else if (ViewportModel.VIEWPORT_CONTENTS_TRANSFORM_PROPERTY
				.equals(evt.getPropertyName())) {
			applyContentsTransform((AffineTransform) evt.getNewValue());
		}
	}

}
