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
		applyGridEnabled(gridModel.isGridEnabled());
		applyGridWidth(gridModel.getGridWidth());
		applyGridHeight(gridModel.getGridHeight());
	}

	protected abstract void applyGridEnabled(boolean enabled);

	protected abstract void applyGridHeight(double height);

	protected abstract void applyGridWidth(double width);

	@Override
	public void deactivate() {
		getHost().getRoot().getViewer().getAdapter(GridModel.class)
		.removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (GridModel.GRID_ENABLED_PROPERTY.equals(evt.getPropertyName())) {
			applyGridEnabled(((Boolean) evt.getNewValue()).booleanValue());
		} else if (GridModel.GRID_WIDTH_PROPERTY.equals(evt.getPropertyName())) {
			applyGridWidth(((Double) evt.getNewValue()).doubleValue());
		} else if (GridModel.GRID_HEIGHT_PROPERTY.equals(evt.getPropertyName())) {
			applyGridHeight(((Double) evt.getNewValue()).doubleValue());
		}
	}

}