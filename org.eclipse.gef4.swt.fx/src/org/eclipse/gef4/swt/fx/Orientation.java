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
package org.eclipse.gef4.swt.fx;

/**
 * {@link Orientation} specifies if a node's width is dependent on its height or
 * vice versa.
 * 
 * @author mwienand
 * 
 */
public enum Orientation {

	/**
	 * Height depends on width.
	 * 
	 * @see #VERTICAL
	 * @see #NONE
	 */
	HORIZONTAL,

	/**
	 * Width depends on height.
	 * 
	 * @see #HORIZONTAL
	 * @see #NONE
	 */
	VERTICAL,

	/**
	 * Width and height are independent of each other.
	 * 
	 * @see #VERTICAL
	 * @see #HORIZONTAL
	 */
	NONE,

}
