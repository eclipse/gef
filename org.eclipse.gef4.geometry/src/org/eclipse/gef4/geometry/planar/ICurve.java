/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

import org.eclipse.gef4.geometry.Point;

/**
 * Abstraction over all curve shapes, i.e. lines (linear curves) as well as
 * quadratic, cubic, and arbitrary Bezier curves.
 * 
 * @author anyssen
 * 
 */
public interface ICurve extends IGeometry {

	/**
	 * Returns the end {@link Point}'s y coordinate.
	 * 
	 * @return the end {@link Point}'s y coordinate.
	 */
	public double getY2();

	/**
	 * Returns the start {@link Point}'s y coordinate.
	 * 
	 * @return the start {@link Point}'s y coordinate.
	 */
	public double getY1();

	/**
	 * Returns the end {@link Point}'s x coordinate.
	 * 
	 * @return the end {@link Point}'s x coordinate.
	 */
	public double getX2();

	/**
	 * Returns the start {@link Point}'s x coordinate.
	 * 
	 * @return the start {@link Point}'s x coordinate.
	 */
	public double getX1();

	/**
	 * Returns a {@link Point} representing the end point of this {@link ICurve}
	 * .
	 * 
	 * @return a new {@link Point} with the coordinates of the {@link ICurve}'s
	 *         end point.
	 */
	public Point getP2();

	/**
	 * Returns a {@link Point} representing the start point of this
	 * {@link ICurve} .
	 * 
	 * @return a new {@link Point} with the coordinates of the {@link ICurve}'s
	 *         start point.
	 */
	public Point getP1();

	// start point, end point, control points (optional)
	// TODO: need to elevate
}
