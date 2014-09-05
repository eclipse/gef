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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.gef4.common.notify.IPropertyChangeNotifier;

/**
 *
 * @author mwienand
 *
 */
public class ZoomModel implements IPropertyChangeNotifier {

	/**
	 * The IZoomingModel fires {@link PropertyChangeEvent}s when its zoom factor
	 * changes. This is the key used to identify the zoom factor property when
	 * listening to multiple property change supporters.
	 */
	public static final String ZOOM_FACTOR_PROPERTY = "ZoomFactor";

	/**
	 * This is the default/initial zoom factor.
	 */
	public static final double DEFAULT_ZOOM_FACTOR = 1d;

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private double zoom = DEFAULT_ZOOM_FACTOR;

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Returns the zoom factor. The zoom factor is a positive value.
	 *
	 * @return the zoom factor
	 */
	public double getZoomFactor() {
		return zoom;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Sets the zoom factor to the given value. Fires a
	 * {@link PropertyChangeEvent}.
	 *
	 * @param zoomFactor
	 *            a positive floating point value stored as the zoom factor
	 * @throws IllegalArgumentException
	 *             when <code><i>zoomFactor</i> &lt;= 0</code>
	 */
	public void setZoomFactor(double zoomFactor) {
		if (zoomFactor <= 0) {
			throw new IllegalArgumentException(
					"Expected: Positive double value. Given: <" + zoomFactor
					+ ">.");
		}
		double oldZoom = zoom;
		zoom = zoomFactor;
		pcs.firePropertyChange(ZOOM_FACTOR_PROPERTY, oldZoom, zoom);
	}

}
