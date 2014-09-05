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

import org.eclipse.gef4.common.notify.IPropertyChangeNotifier;

public class GridModel implements IPropertyChangeNotifier {

	public static final String GRID_WIDTH_PROPERTY = "gridWidth";
	public static final String GRID_HEIGHT_PROPERTY = "gridHeight";
	public static final String GRID_ENABLED_PROPERTY = "gridEnabled";

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private double gridWidth = 10;
	private double gridHeight = 10;
	private boolean gridEnabled = true;

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public double getGridHeight() {
		return gridHeight;
	}

	public double getGridWidth() {
		return gridWidth;
	}

	public boolean isGridEnabled() {
		return gridEnabled;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void setGridEnabled(boolean gridEnabled) {
		boolean oldGridEnabled = this.gridEnabled;
		this.gridEnabled = gridEnabled;
		pcs.firePropertyChange(GRID_ENABLED_PROPERTY, oldGridEnabled,
				gridEnabled);
	}

	public void setGridHeight(double gridHeight) {
		double oldGridHeight = this.gridHeight;
		this.gridHeight = gridHeight;
		pcs.firePropertyChange(GRID_WIDTH_PROPERTY, oldGridHeight, gridHeight);
	}

	public void setGridWidth(double gridWidth) {
		double oldGridWidth = this.gridWidth;
		this.gridWidth = gridWidth;
		pcs.firePropertyChange(GRID_WIDTH_PROPERTY, oldGridWidth, gridWidth);
	}

}
