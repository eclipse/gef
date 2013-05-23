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

/**
 * The {@link Paint}'s Mode specifies where rendering operations obtain the
 * drawing {@link Color} for any particular pixel.
 * 
 * @see PaintMode#COLOR
 * @see PaintMode#GRADIENT
 * @see PaintMode#IMAGE
 * 
 * @author mwienand
 * 
 */
public enum PaintMode {
	/**
	 * The COLOR {@link PaintMode} specifies that rendering operations use the
	 * {@link Paint}'s {@link Color} as the drawing {@link Color} for any
	 * particular pixel.
	 * 
	 * @see PaintMode
	 * @see #GRADIENT
	 * @see #IMAGE
	 */
	COLOR,

	/**
	 * The GRADIENT {@link PaintMode} specifies that rendering operations use
	 * the {@link Paint}'s {@link Gradient} to get the drawing {@link Color} at
	 * any particular pixel.
	 * 
	 * @see PaintMode
	 * @see #COLOR
	 * @see #IMAGE
	 */
	GRADIENT,

	/**
	 * The IMAGE {@link PaintMode} specifies that rendering operations use the
	 * {@link Paint}'s {@link Image} to get the drawing {@link Color} at any
	 * particular pixel.
	 * 
	 * @see PaintMode
	 * @see #COLOR
	 * @see #GRADIENT
	 */
	IMAGE
}