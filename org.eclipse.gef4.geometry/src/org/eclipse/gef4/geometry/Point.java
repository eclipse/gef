/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Alexander Nyßen (itemis AG) - migration do double precision
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry;

import java.io.Serializable;

import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * Represents a point (x, y) in 2-dimensional space. This class provides various
 * methods for manipulating this point or creating new derived geometrical
 * objects.
 * 
 * @author ebordeau
 * @author rhudson
 * @author pshah
 * @author ahunter
 * @author anyssen
 */
public class Point implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The x value.
	 */
	public double x;

	/**
	 * The y value.
	 */
	public double y;

	/**
	 * Creates a new Point representing the MAX of two provided Points.
	 * 
	 * @param p1
	 *            first point
	 * @param p2
	 *            second point
	 * @return A new Point representing the Max()
	 */
	public static Point max(Point p1, Point p2) {
		return new Point(Math.max(p1.x, p2.x), Math.max(p1.y, p2.y));
	}

	/**
	 * Creates a new Point representing the MIN of two provided Points.
	 * 
	 * @param p1
	 *            first point
	 * @param p2
	 *            second point
	 * @return A new Point representing the Min()
	 */
	public static Point min(Point p1, Point p2) {
		return new Point(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y));
	}

	/**
	 * Constructs a Point at location (0,0).
	 * 
	 */
	public Point() {
	}

	/**
	 * Constructs a Point at the specified x and y locations.
	 * 
	 * @param x
	 *            x value
	 * @param y
	 *            y value
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructs a Point at the specified x and y locations.
	 * 
	 * @param x
	 *            x value
	 * @param y
	 *            y value
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructs a Point at the same location as the given SWT Point.
	 * 
	 * @param p
	 *            Point from which the initial values are taken.
	 */
	public Point(org.eclipse.swt.graphics.Point p) {
		x = p.x;
		y = p.y;
	}

	/**
	 * Constructs a Point at the same location as the given Point.
	 * 
	 * @param p
	 *            Point from which the initial values are taken.
	 */
	public Point(Point p) {
		x = p.x;
		y = p.y;
	}

	/**
	 * Overwritten with public visibility as proposed in {@link Cloneable}.
	 */
	@Override
	public Point clone() {
		return getCopy();
	}

	/**
	 * Returns <code>true</code> if this Points x and y are equal to the given x
	 * and y.
	 * 
	 * @param x
	 *            the x value
	 * @param y
	 *            the y value
	 * @return <code>true</code> if this point's x and y are equal to those
	 *         given.
	 */
	public boolean equals(double x, double y) {
		return PrecisionUtils.equal(this.x, x)
				&& PrecisionUtils.equal(this.y, y);
	}

	/**
	 * Test for equality.
	 * 
	 * @param o
	 *            Object being tested for equality
	 * @return true if both x and y values are equal
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Point) {
			Point p = (Point) o;
			return equals(p.x, p.y);
		}
		return false;
	}

	/**
	 * @return a copy of this Point
	 */
	public Point getCopy() {
		return new Point(this);
	}

	/**
	 * Calculates the distance from this Point to the one specified.
	 * 
	 * @param p
	 *            The Point being compared to this
	 * @return The distance
	 */
	public double getDistance(Point p) {
		double i = p.x - x;
		double j = p.y - y;
		return Math.sqrt(i * i + j * j);
	}

	/**
	 * Creates a Point with negated x and y values.
	 * 
	 * @return A new Point
	 */
	public Point getNegated() {
		return getCopy().negate();
	}

	/**
	 * Creates a new Point from this Point by scaling by the specified amount.
	 * 
	 * @param factor
	 *            scale factor
	 * @return A new Point
	 */
	public Point getScaled(double factor) {
		return getCopy().scale(factor);
	}

	/**
	 * Creates a new Point from this Point by scaling by the specified values.
	 * 
	 * @param xFactor
	 *            horizontal scale factor
	 * @param yFactor
	 *            vertical scale factor
	 * @return A new Point
	 */
	public Point getScaled(double xFactor, double yFactor) {
		return getCopy().scale(xFactor, yFactor);
	}

	/**
	 * Creates a new SWT {@link org.eclipse.swt.graphics.Point Point} from this
	 * Point.
	 * 
	 * @return A new SWT Point
	 */
	public org.eclipse.swt.graphics.Point toSWTPoint() {
		return new org.eclipse.swt.graphics.Point((int) x, (int) y);
	}

	/**
	 * Creates a new Point which is translated by the values of the input
	 * Dimension.
	 * 
	 * @param d
	 *            Dimension which provides the translation amounts.
	 * @return A new Point
	 */
	public Point getTranslated(Dimension d) {
		return getCopy().translate(d);
	}

	/**
	 * Creates a new Point which is translated by the specified x and y values
	 * 
	 * @param x
	 *            horizontal component
	 * @param y
	 *            vertical component
	 * @return A new Point
	 */
	public Point getTranslated(double x, double y) {
		return getCopy().translate(x, y);
	}

	/**
	 * Creates a new Point which is translated by the values of the provided
	 * Point.
	 * 
	 * @param p
	 *            Point which provides the translation amounts.
	 * @return A new Point
	 */
	public Point getTranslated(Point p) {
		return getCopy().translate(p);
	}

	/**
	 * Creates a new Point with the transposed values of this Point. Can be
	 * useful in orientation change calculations.
	 * 
	 * @return A new Point
	 */
	public Point getTransposed() {
		return getCopy().transpose();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// calculating a better hashCode is not possible, because due to the
		// imprecision, equals() is no longer transitive
		return 0;
	}

	/**
	 * Negates the x and y values of this Point.
	 * 
	 * @return <code>this</code> for convenience
	 */
	public Point negate() {
		scale(-1.0d);
		return this;
	}

	/**
	 * Scales this Point by the specified amount.
	 * 
	 * @return <code>this</code> for convenience
	 * @param factor
	 *            scale factor
	 */
	public Point scale(double factor) {
		return scale(factor, factor);
	}

	/**
	 * Scales this Point by the specified values.
	 * 
	 * @param xFactor
	 *            horizontal scale factor
	 * @param yFactor
	 *            vertical scale factor
	 * @return <code>this</code> for convenience
	 */
	public Point scale(double xFactor, double yFactor) {
		x *= xFactor;
		y *= yFactor;
		return this;
	}

	/**
	 * Sets the location of this Point to the provided x and y locations.
	 * 
	 * @return <code>this</code> for convenience
	 * @param x
	 *            the x location
	 * @param y
	 *            the y location
	 */
	public Point setLocation(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	/**
	 * Sets the location of this Point to the specified Point.
	 * 
	 * @return <code>this</code> for convenience
	 * @param p
	 *            the Location
	 */
	public Point setLocation(Point p) {
		x = p.x;
		y = p.y;
		return this;
	}

	/**
	 * Sets the x value of this Point to the given value.
	 * 
	 * @param x
	 *            The new x value
	 * @return this for convenience
	 */
	public Point setX(double x) {
		this.x = x;
		return this;
	}

	/**
	 * Sets the y value of this Point to the given value;
	 * 
	 * @param y
	 *            The new y value
	 * @return this for convenience
	 */
	public Point setY(double y) {
		this.y = y;
		return this;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "Point(" + x + ", " + y + ")";//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
	}

	/**
	 * Shifts this Point by the values of the Dimension along each axis, and
	 * returns this for convenience.
	 * 
	 * @param d
	 *            Dimension by which the origin is being shifted.
	 * @return <code>this</code> for convenience
	 */
	public Point translate(Dimension d) {
		return translate(d.width, d.height);
	}

	/**
	 * Shifts this Point by the values supplied along each axes, and returns
	 * this for convenience.
	 * 
	 * @param dx
	 *            Amount by which point is shifted along X axis.
	 * @param dy
	 *            Amount by which point is shifted along Y axis.
	 * @return <code>this</code> for convenience
	 */
	public Point translate(double dx, double dy) {
		x += dx;
		y += dy;
		return this;
	}

	/**
	 * Shifts the location of this Point by the location of the input Point
	 * along each of the axes, and returns this for convenience.
	 * 
	 * @param p
	 *            Point to which the origin is being shifted.
	 * @return <code>this</code> for convenience
	 */
	public Point translate(Point p) {
		return translate(p.x, p.y);
	}

	/**
	 * Transposes this object. X and Y values are exchanged.
	 * 
	 * @return <code>this</code> for convenience
	 */
	public Point transpose() {
		double temp = x;
		x = y;
		y = temp;
		return this;
	}

	/**
	 * Returns the x value of this Point.
	 * 
	 * @return The current x value
	 */
	public double x() {
		return x;
	}

	/**
	 * Returns the y value of this Point.
	 * 
	 * @return The current y value
	 */
	public double y() {
		return y;
	}
}
