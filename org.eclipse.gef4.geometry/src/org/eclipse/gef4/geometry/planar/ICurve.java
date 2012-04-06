/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
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
	 * A list of {@link BezierCurve}s that approximate the {@link ICurve}. For
	 * example, a {@link Line} or a {@link BezierCurve} in general, could return
	 * a list with the curve itself as its only element. But an {@link Ellipse}
	 * or an {@link Arc} may return a list of consecutive {@link BezierCurve}s
	 * which approximate the curve.
	 * 
	 * @return a list of {@link BezierCurve}s that approximate the
	 *         {@link ICurve}
	 */
	public BezierCurve[] toBezier();

	/**
	 * Returns the points of intersection between this {@link ICurve} and the
	 * given {@link ICurve}.
	 * 
	 * @param c
	 *            the {@link ICurve} to compute intersection points for
	 * @return the points of intersection.
	 */
	public Point[] getIntersections(final ICurve c);

	/**
	 * Tests if this {@link ICurve} and the given {@link ICurve} intersect, i.e.
	 * whether a final set of intersection points exists. Two curves intersect
	 * if they touch (see {@link IGeometry#touches(IGeometry)}) but not overlap
	 * (see {@link ICurve#overlaps(ICurve)}).
	 * 
	 * @param c
	 *            the {@link ICurve} to test for intersections
	 * @return <code>true</code> if they intersect, <code>false</code> otherwise
	 */
	boolean intersects(final ICurve c);

	/**
	 * Tests if this {@link ICurve} and the given {@link ICurve} overlap, i.e.
	 * whether an infinite set of intersection points exists. Two curves overlap
	 * if they touch (see {@link IGeometry#touches(IGeometry)}) but not
	 * intersect (see {@link ICurve#intersects(ICurve)}).
	 * 
	 * @param c
	 *            the {@link ICurve} to test for overlap
	 * @return <code>true</code> if they overlap, <code>false</code> otherwise
	 */
	boolean overlaps(final ICurve c);

	// start point, end point, control points (optional)
	// TODO: need to elevate
}
