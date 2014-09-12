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

import org.eclipse.gef4.common.notify.IPropertyChangeNotifier;

public class ViewportModel implements IPropertyChangeNotifier {

	/*
	 * An IViewportModel fires PropertyChangeEvents when its width or height
	 * changes. These are the keys used to identify those properties when
	 * listening to property change events.
	 */

	/**
	 * When the viewport width changes, this is the property name reported by a
	 * corresponding property change event.
	 */
	public static final String VIEWPORT_WIDTH_PROPERTY = "viewportWidth";

	/**
	 * When the viewport height changes, this is the property name reported by a
	 * corresponding property change event.
	 */
	public static final String VIEWPORT_HEIGHT_PROPERTY = "viewportHeight";

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private double width = 0;
	private double height = 0;

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Returns the height of the current viewport, i.e. rectangular area in
	 * which the viewer/editor is rendered.
	 *
	 * @return height of current viewport
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Returns the width of the current viewport, i.e. rectangular area in which
	 * the viewer/editor is rendered.
	 *
	 * @return width of current viewport
	 */
	public double getWidth() {
		return width;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * <p>
	 * Sets the height of the viewport in this model. This should be called when
	 * the size of the rectangular area in which the viewer/editor is rendered
	 * changes.
	 * </p>
	 * <p>
	 * Fires a property change event for the {@link #VIEWPORT_HEIGHT_PROPERTY}.
	 * </p>
	 *
	 * @param height
	 *            new viewport height
	 */
	public void setHeight(double height) {
		double oldHeight = this.height;
		this.height = height;
		pcs.firePropertyChange(VIEWPORT_HEIGHT_PROPERTY, oldHeight, height);
	}

	/**
	 * <p>
	 * Sets the width of the viewport in this model. This should be called when
	 * the size of the rectangular area in which the viewer/editor is rendered
	 * changes.
	 * </p>
	 * <p>
	 * Fires a property change event for the {@link #VIEWPORT_WIDTH_PROPERTY}.
	 * </p>
	 *
	 * @param width
	 *            new viewport width
	 */
	public void setWidth(double width) {
		double oldWidth = this.width;
		this.width = width;
		pcs.firePropertyChange(VIEWPORT_WIDTH_PROPERTY, oldWidth, width);
	}

}
