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
package org.eclipse.gef4.swtfx.gc;

import org.eclipse.gef4.geometry.planar.Arc;

/**
 * The ArcType specifies how an {@link Arc} shall be closed when rendering one
 * with the {@link GraphicsContext}:
 * <ul>
 * <li>{@link #CHORD}</li>
 * <li>{@link #OPEN}</li>
 * <li>{@link #ROUND}</li>
 * </ul>
 * 
 * @author mwienand
 * 
 */
public enum ArcType {

	/**
	 * The {@link Arc} is closed by drawing a straight line segment from the
	 * start point of the {@link Arc} to its end point.
	 * 
	 * @see #OPEN
	 * @see #ROUND
	 */
	CHORD,

	/**
	 * The {@link Arc} is not closed.
	 * 
	 * @see #CHORD
	 * @see #ROUND
	 */
	OPEN,

	/**
	 * The {@link Arc} is closed by drawing a straight line segment from the
	 * start point of the {@link Arc} to the center of the {@link Arc} and from
	 * the center of the {@link Arc} to the end point of the {@link Arc}.
	 * 
	 * @see #CHORD
	 * @see #OPEN
	 */
	ROUND,

}
