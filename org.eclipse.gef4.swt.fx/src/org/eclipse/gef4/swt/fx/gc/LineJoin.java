/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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
package org.eclipse.gef4.swt.fx.gc;

/**
 * <p>
 * The LineJoin specifies how to display the connection point of two displayed
 * lines. A displayed line does always have a width. That's why one displayed
 * line does really consist of two parallel lines, filled with some color. When
 * two such lines are connected with each other in one end point, a bent corner
 * is formed in this end point. The LineJoin specifies how to fill that bent
 * corner.
 * </p>
 * <p>
 * The different LineJoins are:
 * <ul>
 * <li>{@link #BEVEL}</li>
 * <li>{@link #MITER}</li>
 * <li>{@link #ROUND}</li>
 * </ul>
 * </p>
 */
public enum LineJoin {
	/**
	 * A BEVEL {@link LineJoin} fills the bent corner triangular.
	 * 
	 * @see {@link #MITER}
	 * @see {@link #ROUND}
	 */
	BEVEL,

	/**
	 * A MITER {@link LineJoin} fills the bent corner up to the intersection
	 * point of the two outermost lines if its distance to the middle
	 * intersection is less than or equal to the
	 * {@link IDrawContext#getMiterLimit miter limit}. In case of exceeding the
	 * {@link IDrawContext#getMiterLimit miter limit}, the {@link #BEVEL}
	 * {@link LineJoin} is used.
	 * 
	 * @see {@link #MITER}
	 * @see {@link #ROUND}
	 */
	MITER,

	/**
	 * A ROUND {@link LineJoin} fills the bent corner with a circular arc.
	 * 
	 * @see {@link #BEVEL}
	 * @see {@link #MITER}
	 */
	ROUND
}