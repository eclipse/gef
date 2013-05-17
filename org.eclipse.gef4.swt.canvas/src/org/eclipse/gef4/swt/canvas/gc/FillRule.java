/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swt.canvas.gc;

import org.eclipse.gef4.geometry.planar.Path;

/**
 * The {@link FillRule} specifies how the interior and exterior of a
 * {@link Path} is determined:
 * <ul>
 * <li>{@link #EVEN_ODD}</li>
 * <li>{@link #WIND_NON_ZERO}</li>
 * </ul>
 * 
 * @author mwienand
 * 
 */
public enum FillRule {

	/**
	 * Cast a ray from the point in question and count the number of
	 * intersections of the ray and the {@link Path}. If the number of
	 * intersections is even, the point lies outside. Otherwise, it lies inside.
	 * 
	 * @see #WIND_NON_ZERO
	 */
	EVEN_ODD,

	/**
	 * Count the total number of counterclockwise turns that an object moving on
	 * the {@link Path} makes around the point in question. If this number is
	 * zero, the point lies outside. Otherwise, it lies inside.
	 * 
	 * @see #EVEN_ODD
	 */
	WIND_NON_ZERO,

}
