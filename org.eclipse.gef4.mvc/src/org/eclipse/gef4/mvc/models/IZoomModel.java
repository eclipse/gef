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

import org.eclipse.gef4.mvc.IPropertyChangeSupport;
import org.eclipse.gef4.mvc.parts.IRootPart;

/**
 * The {@link IZoomModel} is used to store the current viewer's zoom factor,
 * which should get adjusted by a zooming tool as a response to user interaction
 * (via mouse or gesture events, or by using scroll bars). TODO: We should
 * provide a base implementation for a zooming tool here
 * 
 * A tool is also needed to propagate the current zoom factor to the
 * {@link IZoomPolicy} of the {@link IRootPart}, which has to apply the zoom
 * factor to the view. TODO: Provide base implementations for this tool and the
 * edit policy as well.
 * 
 * @author mwienand
 * 
 */
public interface IZoomModel extends IPropertyChangeSupport {

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

	/**
	 * Returns the zoom factor. The zoom factor is a positive value.
	 * 
	 * @return the zoom factor
	 */
	public double getZoomFactor();

	/**
	 * Sets the zoom factor to the given value. Fires a
	 * {@link PropertyChangeEvent}.
	 * 
	 * @param zoomFactor
	 *            a positive floating point value stored as the zoom factor
	 * @throws IllegalArgumentException
	 *             when <code><i>zoomFactor</i> &lt;= 0</code>
	 */
	public void setZoomFactor(double zoomFactor);

}
