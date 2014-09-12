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
package org.eclipse.gef4.mvc.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef4.mvc.models.GridModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractGridBehavior<VR> extends AbstractBehavior<VR>
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

	protected abstract void applyGridCellHeight(double gridCellHeight);

	protected abstract void applyGridCellWidth(double gridCellWidth);

	protected abstract void applyShowGrid(boolean showGrid);

	protected abstract void applyZoomGrid(boolean zoomGrid);

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