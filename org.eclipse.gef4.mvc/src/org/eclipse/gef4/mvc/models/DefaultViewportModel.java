/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class DefaultViewportModel implements IViewportModel {

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private double width = 0;
	private double height = 0;

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public void setHeight(double height) {
		double oldHeight = this.height;
		this.height = height;
		pcs.firePropertyChange(VIEWPORT_HEIGHT_PROPERTY, oldHeight, height);
	}

	@Override
	public void setWidth(double width) {
		double oldWidth = this.width;
		this.width = width;
		pcs.firePropertyChange(VIEWPORT_WIDTH_PROPERTY, oldWidth, width);
	}

}
