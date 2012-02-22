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

	/**
	 * Returns an array of points representing the inner control points of this
	 * curve, i.e. excluding the start and end points. In case of s linear
	 * curve, no control points will be returned, in case of a quadratic curve,
	 * one control point, and so on.
	 * 
	 * @return an array of points with the coordinates of the inner control
	 *         points of this {@link ICurve}, i.e. exclusive of the start and
	 *         end point. The number of control points will depend on the degree
	 *         ({@link #getDegree()}) of the curve, so in case of a line (linear
	 *         curve) the array will be empty, in case of a quadratic curve, it
	 *         will be of size <code>1</code>, in case of a cubic curve of size
	 *         <code>2</code>, etc..
	 */
	public Point[] getCtrls();

	/**
	 * Returns the point-wise coordinates (i.e. x1, y1, x2, y2, etc.) of the
	 * inner control points of this {@link ICurve}, i.e. exclusive of the start
	 * and end points.
	 * 
	 * @see ICurve#getCtrls()
	 * 
	 * @return an array containing the inner control points' coordinates
	 */
	public double[] getCtrlCoordinates();

	/**
	 * Returns the degree of this curve which corresponds to the number of
	 * overall control points (including start and end point) used to define the
	 * curve. The degree is zero-based, so a line (linear curve) will have
	 * degree <code>1</code>, a quadratic curve will have degree <code>2</code>,
	 * and so on. <code>1</code> in case of a
	 * 
	 * @return The degree of this {@link ICurve}, which corresponds to the
	 *         zero-based overall number of control points (including start and
	 *         end point) used to define this {@link ICurve}.
	 */
	public int getDegree();

	// start point, end point, control points (optional)
	// TODO: need to elevate
}
