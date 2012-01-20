/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Alexander Nyßen (Research Group Software Construction, RWTH Aachen University) - contribution for Bugzilla #245182
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *
 *******************************************************************************/
package org.eclipse.gef4.geometry.euclidean;

import java.io.Serializable;

import org.eclipse.gef4.geometry.Angle;
import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * Represents a vector within 2-dimensional Euclidean space.
 * 
 * @author ebordeau
 * @author rhudson
 * @author pshah
 * @author ahunter
 * @author anyssen
 */
public class Vector implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	/** the X value */
	public double x;
	/** the Y value */
	public double y;

	// internal constant used for comparisons.
	private static final Vector NULL = new Vector(0, 0);

	/**
	 * Constructs a Vector pointed in the specified direction.
	 * 
	 * @param x
	 *            X value.
	 * @param y
	 *            Y value.
	 */
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructs a Vector pointed in the direction specified by a Point.
	 * 
	 * @param p
	 *            the point
	 */
	public Vector(Point p) {
		x = p.x;
		y = p.y;
	}

	/**
	 * Constructs a Vector representing the direction and magnitude between to
	 * provided Points.
	 * 
	 * @param start
	 *            starting point
	 * @param end
	 *            End Point
	 */
	public Vector(Point start, Point end) {
		x = end.x - start.x;
		y = end.y - start.y;
	}

	/**
	 * Constructs a Vector representing the difference between two provided
	 * Vectors.
	 * 
	 * @param start
	 *            The start {@link Vector}
	 * @param end
	 *            The end {@link Vector}
	 */
	public Vector(Vector start, Vector end) {
		x = end.x - start.x;
		y = end.y - start.y;
	}

	/**
	 * Clones the given {@link Vector} using {@link Vector#getCopy()}.
	 * 
	 * @return a copy of this {@link Vector} object.
	 */
	@Override
	public Vector clone() {
		return getCopy();
	}

	/**
	 * Returns a copy of this {@link Vector} object.
	 * 
	 * @return a copy of this {@link Vector} object.
	 */
	public Vector getCopy() {
		return new Vector(x, y);
	}

	/**
	 * Calculates the magnitude of the cross product of this {@link Vector} with
	 * another. Normalized the {@link Vector}s before calculating the cross
	 * product. Represents the amount by which two {@link Vector}s are
	 * directionally different. Parallel {@link Vector}s return a value of 0.
	 * 
	 * @param other
	 *            The {@link Vector} being compared
	 * @return The dissimilarity
	 */
	public double getDissimilarity(Vector other) {
		return Math.abs(getNormalized().getCrossProduct(other.getNormalized()));
	}

	/**
	 * Calculates whether this {@link Vector} and the provided one are parallel
	 * to each other.
	 * 
	 * @param other
	 *            The {@link Vector} to test for parallelism
	 * @return true if this Vector and the provided one are parallel, false
	 *         otherwise.
	 */
	public boolean isParallelTo(Vector other) {
		return PrecisionUtils.equal(getDissimilarity(other), 0);
	}

	/**
	 * Calculates the dot product of this Vector with another.
	 * 
	 * @param other
	 *            the Vector used to calculate the dot product
	 * @return The dot product
	 */
	public double getDotProduct(Vector other) {
		return x * other.x + y * other.y;
	}

	/**
	 * Calculates the cross product of this Vector with another.
	 * 
	 * @param other
	 *            the Vector used to calculate the cross product
	 * @return The cross product.
	 */
	public double getCrossProduct(Vector other) {
		return x * other.y - y * other.x;
	}

	/**
	 * Creates a new Vector which is the sum of this Vector with another.
	 * 
	 * @param other
	 *            Vector to be added to this Vector
	 * @return a new Vector representing the sum
	 */
	public Vector getAdded(Vector other) {
		return new Vector(x + other.x, y + other.y);
	}

	/**
	 * Creates a new Vector which is the difference of this Vector with the
	 * provided Vector.
	 * 
	 * @param other
	 *            Vector to be subtracted from this Vector
	 * @return a new Vector representing the difference.
	 */
	public Vector getSubtracted(Vector other) {
		return new Vector(x - other.x, y - other.y);
	}

	/**
	 * Returns the smallest {@link Angle} between this {@link Vector} and the
	 * provided {@link Vector}.
	 * 
	 * @param other
	 *            {@link Vector} to calculate the {@link Angle}.
	 * @return the smallest {@link Angle} between the two Vectors.
	 */
	public Angle getAngle(Vector other) {
		double length = getLength() * other.getLength();
		if (length == 0) {
			throw new ArithmeticException("Division by zero.");
		}

		double cosAlpha = getDotProduct(other) / length;

		// compensate rounding effects
		if (cosAlpha > 1) {
			cosAlpha = 1;
		} else if (cosAlpha < -1) {
			cosAlpha = -1;
		}

		return Angle.fromRad(Math.acos(cosAlpha));
	}

	/**
	 * Returns the clock-wise (mathematical negative) {@link Angle} between this
	 * {@link Vector} and the provided {@link Vector}.
	 * 
	 * @param other
	 *            {@link Vector} to calculate the {@link Angle}.
	 * @return the clock-wise {@link Angle} between the two Vectors.
	 */
	public Angle getAngleCW(Vector other) {
		return getAngleCCW(other).getOppositeFull();
	}

	/**
	 * Returns the counter-clock-wise (mathematical positive) {@link Angle}
	 * between this {@link Vector} and the provided {@link Vector}.
	 * 
	 * @param other
	 *            {@link Vector} to calculate the {@link Angle}.
	 * @return the counter-clock-wise {@link Angle} between the two Vectors.
	 */
	public Angle getAngleCCW(Vector other) {
		Angle angle = getAngle(other);
		if (getCrossProduct(other) > 0) {
			return angle.getOppositeFull();
		}
		return angle;
	}

	/**
	 * Creates a new Vector which represents the average of this Vector with
	 * another.
	 * 
	 * @param other
	 *            Vector to calculate the average.
	 * @return a new Vector
	 */
	public Vector getAveraged(Vector other) {
		return new Vector((x + other.x) / 2, (y + other.y) / 2);
	}

	/**
	 * Creates a new Vector which represents this Vector multiplied by the
	 * provided scalar factor.
	 * 
	 * @param factor
	 *            Value providing the amount to scale.
	 * @return a new Vector
	 */
	public Vector getMultiplied(double factor) {
		return new Vector(x * factor, y * factor);
	}

	/**
	 * Creates a new Vector which represents this Vector divided by the provided
	 * scalar factor.
	 * 
	 * @param factor
	 *            Value providing the amount to scale.
	 * @return a new Vector
	 */
	public Vector getDivided(double factor) {
		if (factor == 0) {
			throw new ArithmeticException("division by zero");
		}
		return new Vector(x / factor, y / factor);
	}

	/**
	 * Returns the orthogonal complement of this Vector, which is defined to be
	 * (-y, x).
	 * 
	 * @return the orthogonal complement of this Vector
	 */
	public Vector getOrthogonalComplement() {
		return new Vector(-y, x);
	}

	/**
	 * Returns a fresh rotated Vector object. The rotation is clock-wise (CW) by
	 * the given angle.
	 * 
	 * @param angle
	 *            the rotation angle
	 * @return the new rotated Vector
	 */
	public Vector getRotatedCW(Angle angle) {
		return clone().rotateCW(angle);
	}

	/**
	 * Returns a fresh rotated Vector object. The rotation is counter-clock-wise
	 * (CCW) by the given angle.
	 * 
	 * @param angle
	 *            the rotation angle
	 * @return the new rotated Vector
	 */
	public Vector getRotatedCCW(Angle angle) {
		return clone().rotateCCW(angle);
	}

	/**
	 * Returns the length of this Vector.
	 * 
	 * @return Length of this Vector
	 */
	public double getLength() {
		return Math.sqrt(getDotProduct(this));
	}

	/**
	 * Calculates the similarity of this Vector with another. Similarity is
	 * defined as the absolute value of the dotProduct(). Orthogonal vectors
	 * return a value of 0.
	 * 
	 * @param other
	 *            Vector being tested for similarity
	 * @return the Similarity
	 * @see #getDissimilarity(Vector)
	 */
	public double getSimilarity(Vector other) {
		return Math.abs(getDotProduct(other));
	}

	/**
	 * Calculates whether this Vector and the provided one are orthogonal to
	 * each other.
	 * 
	 * @param other
	 *            Vector being tested for orthogonality
	 * @return true, if this Vector and the provide one are orthogonal, false
	 *         otherwise
	 */
	public boolean isOrthogonalTo(Vector other) {
		return PrecisionUtils.equal(getSimilarity(other), 0);
	}

	/**
	 * Checks whether this vector has a horizontal component.
	 * 
	 * @return true if x != 0, false otherwise.
	 */
	public boolean isHorizontal() {
		return !PrecisionUtils.equal(x, 0);
	}

	/**
	 * Checks whether this vector has a vertical component.
	 * 
	 * @return true if y != 0, false otherwise.
	 */
	public boolean isVertical() {
		return !PrecisionUtils.equal(y, 0);
	}

	/**
	 * Checks whether this vector equals (0,0);
	 * 
	 * @return true if x == 0 and y == 0.
	 */
	public boolean isNull() {
		return equals(NULL);
	}

	/**
	 * Returns a point representation of this Vector.
	 * 
	 * @return a PrecisionPoint representation
	 */
	public Point toPoint() {
		return new Point(x, y);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Vector: [" + x + "," + y + "]";//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof Vector) {
			Vector r = (Vector) obj;
			return PrecisionUtils.equal(this.x, r.x)
					&& PrecisionUtils.equal(this.y, r.y);
		}
		return false;
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
	 * Rotates this {@link Vector} counter-clock-wise by the given {@link Angle}
	 * .
	 * 
	 * @param angle
	 *            The rotation {@link Angle}.
	 * @return This (rotated) {@link Vector} object.
	 */
	public Vector rotateCCW(Angle angle) {
		return rotateCW(angle.getOppositeFull());
	}

	/**
	 * Rotates this {@link Vector} clock-wise by the given {@link Angle}.
	 * 
	 * @param angle
	 *            The rotation {@link Angle}.
	 * @return This (rotated) {@link Vector} object.
	 */
	public Vector rotateCW(Angle angle) {
		double alpha = angle.rad();
		double nx = x * Math.cos(alpha) - y * Math.sin(alpha);
		double ny = x * Math.sin(alpha) + y * Math.cos(alpha);
		x = nx;
		y = ny;
		return this;
	}

	/**
	 * Creates a new normalized {@link Vector} that has the same direction as
	 * this {@link Vector} but a length of 1.
	 * 
	 * @return The normalized {@link Vector}.
	 */
	public Vector getNormalized() {
		return clone().getMultiplied(1 / getLength());
	}

}
