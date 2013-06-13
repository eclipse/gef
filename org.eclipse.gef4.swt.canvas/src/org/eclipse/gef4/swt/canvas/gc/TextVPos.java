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
 * The {@link TextVPos} determines the vertical positioning of text relative to
 * the transformed origin of the current {@link GraphicsContext}:
 * 
 * <ul>
 * <li>{@link #TOP}</li>
 * <li>{@link #BOTTOM}</li>
 * <li>{@link #CENTER}</li>
 * <li>{@link #BASELINE}</li>
 * </ul>
 * 
 * @author mwienand
 * 
 */
public enum TextVPos {

	/**
	 * Y = 0 is at the ascent line of the text.
	 * 
	 * @see #BOTTOM
	 * @see #CENTER
	 * @see #BASELINE
	 */
	TOP,

	/**
	 * Y = 0 is at the center line of the text.
	 * 
	 * @see #TOP
	 * @see #BOTTOM
	 * @see #BASELINE
	 */
	CENTER,

	/**
	 * Y = 0 is at the descent line of the text.
	 * 
	 * @see #TOP
	 * @see #CENTER
	 * @see #BASELINE
	 */
	BOTTOM,

	/**
	 * Y = 0 is at the baseline of the text.
	 * 
	 * @see #TOP
	 * @see #BOTTOM
	 * @see #CENTER
	 */
	BASELINE;

	/**
	 * The default {@link TextVPos} is {@link #TOP}.
	 */
	public static final TextVPos DEFAULT = TOP;

}
