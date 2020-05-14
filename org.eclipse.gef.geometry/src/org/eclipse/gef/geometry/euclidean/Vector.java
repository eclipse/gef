/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Alexander Nyßen (Research Group Software Construction, RWTH Aachen University) - contribution for Bugzilla #245182
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.euclidean;

import java.io.Serializable;

import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;
import org.eclipse.gef.geometry.planar.Point;

/**
 * Represents a {@link Vector} within 2-dimensional Euclidean space.
 *
 * @author ebordeau
 * @author rhudson
 * @author pshah
 * @author ahunter
 * @author anyssen
 * @author mwienand
 *
 */
public class Vector implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The (0,0) vector.
	 */
	public static final Vector NULL = new Vector(0, 0);

	/** The x coordinate of this {@link Vector}. */
	public double x;

	/** The y coordinate of this {@link Vector}. */
	public double y;

	/**
	 * Constructs a {@link Vector} that points in the specified direction.
	 *
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 */
	public Vector(double x, double y) {
		if (Double.isNaN(x)) {
			throw new IllegalArgumentException(
					"x coordinate has to be differen from NaN.");
		}
		if (Double.isNaN(y)) {
			throw new IllegalArgumentException(
					"y coordinate has to be differen from NaN.");
		}
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructs a {@link Vector} that is the position {@link Vector} of the
	 * given {@link Point}.
	 *
	 * @param p
	 *            the {@link Point} to construct a position {@link Vector} for
	 */
	public Vector(Point p) {
		this(p.x, p.y);
	}

	/**
	 * Constructs a {@link Vector} representing the direction and magnitude
	 * between to provided {@link Point}s.
	 *
	 * @param start
	 *            the start {@link Point}
	 * @param end
	 *            the end {@link Point}
	 */
	public Vector(Point start, Point end) {
		x = end.x - start.x;
		y = end.y - start.y;
	}

	/**
	 * Constructs a {@link Vector} representing the difference between two
	 * provided {@link Vector}s.
	 *
	 * @param start
	 *            the start {@link Vector}
	 * @param end
	 *            the end {@link Vector}
	 */
	public Vector(Vector start, Vector end) {
		x = end.x - start.x;
		y = end.y - start.y;
	}

	/**
	 * Clones the given {@link Vector} using {@link Vector#getCopy()}.
	 *
	 * @return a copy of this {@link Vector} object
	 */
	@Override
	public Vector clone() {
		return getCopy();
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Vector) {
			Vector r = (Vector) obj;
			return PrecisionUtils.equal(this.x, r.x)
					&& PrecisionUtils.equal(this.y, r.y);
		}
		return false;
	}

	/**
	 * Returns a new {@link Vector} that represents the sum of this
	 * {@link Vector} and the given other {@link Vector}.
	 *
	 * @param other
	 *            the {@link Vector} that is added to this {@link Vector}
	 * @return a new {@link Vector} representing the sum of this {@link Vector}
	 *         and the given other {@link Vector}
	 */
	public Vector getAdded(Vector other) {
		return new Vector(x + other.x, y + other.y);
	}

	/**
	 * Returns the smallest {@link Angle} between this {@link Vector} and the
	 * provided one.
	 *
	 * @param other
	 *            the {@link Vector} for which the smallest {@link Angle} to
	 *            this {@link Vector} is calculated
	 * @return the smallest {@link Angle} between this {@link Vector} and the
	 *         provided one
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
	 * Returns the counter-clockwise (CCW) {@link Angle} between this
	 * {@link Vector} and the provided one.
	 *
	 * @param other
	 *            the {@link Vector} for which the CCW {@link Angle} to this
	 *            {@link Vector} is calculated
	 * @return the counter-clockwise {@link Angle} between this {@link Vector}
	 *         and the provided one
	 */
	public Angle getAngleCCW(Vector other) {
		Angle angle = getAngle(other);
		if (getCrossProduct(other) > 0) {
			return angle.getOppositeFull();
		}
		return angle;
	}

	/**
	 * Returns the clockwise (CW) {@link Angle} between this {@link Vector} and
	 * the provided one.
	 *
	 * @param other
	 *            the {@link Vector} for which the CW {@link Angle} to this
	 *            {@link Vector} is calculated
	 * @return the clockwise {@link Angle} between this {@link Vector} and the
	 *         provided one
	 */
	public Angle getAngleCW(Vector other) {
		return getAngleCCW(other).getOppositeFull();
	}

	/**
	 * Creates a new {@link Vector} which represents the average of this
	 * {@link Vector} with the provided one.
	 *
	 * @param other
	 *            the {@link Vector} for which the average with this
	 *            {@link Vector} is calculated
	 * @return a new {@link Vector} which represents the average of this
	 *         {@link Vector} and the provided one
	 */
	public Vector getAveraged(Vector other) {
		return new Vector((x + other.x) / 2, (y + other.y) / 2);
	}

	/**
	 * Returns a copy of this {@link Vector} object.
	 *
	 * @return a copy of this {@link Vector} object
	 */
	public Vector getCopy() {
		return new Vector(x, y);
	}

	/**
	 * Calculates the cross product of this {@link Vector} (lhs) and the given
	 * other {@link Vector} (rhs).
	 *
	 * @param other
	 *            the rhs {@link Vector} for which the cross product with this
	 *            {@link Vector} is calculated
	 * @return the cross product of this {@link Vector} (lhs) and the given
	 *         other {@link Vector} (rhs)
	 */
	public double getCrossProduct(Vector other) {
		return x * other.y - y * other.x;
	}

	/**
	 * Calculates the magnitude of the cross product of this {@link Vector} with
	 * the given other {@link Vector}. This method normalizes both
	 * {@link Vector}s before calculating the cross product. The resulting
	 * dissimilarity value represents the amount by which two {@link Vector}s
	 * are directionally different. For parallel {@link Vector}s 0 is returned.
	 *
	 * @param other
	 *            the {@link Vector} to compare to this {@link Vector}
	 * @return the dissimilarity of both {@link Vector}s
	 */
	public double getDissimilarity(Vector other) {
		return Math.abs(getNormalized().getCrossProduct(other.getNormalized()));
	}

	/**
	 * Creates a new {@link Vector} which represents this {@link Vector} divided
	 * by the provided scalar value.
	 *
	 * @param factor
	 *            the divisor
	 * @return a new {@link Vector} which represents this {@link Vector} divided
	 *         by the provided scalar value
	 */
	public Vector getDivided(double factor) {
		if (factor == 0) {
			throw new ArithmeticException("Division by zero.");
		}
		return new Vector(x / factor, y / factor);
	}

	/**
	 * Calculates the dot product of this {@link Vector} and the given other
	 * {@link Vector}.
	 *
	 * @param other
	 *            the {@link Vector} for which the dot product with this
	 *            {@link Vector} is calculated
	 * @return the dot product of the two {@link Vector}s
	 */
	public double getDotProduct(Vector other) {
		return x * other.x + y * other.y;
	}

	/**
	 * Returns the length of this {@link Vector}.
	 *
	 * @return the length of this {@link Vector}
	 */
	public double getLength() {
		return Math.sqrt(getDotProduct(this));
	}

	/**
	 * Creates a new {@link Vector} which represents this {@link Vector}
	 * multiplied by the provided scalar value.
	 *
	 * @param factor
	 *            the scalar multiplication factor to scale this {@link Vector}
	 * @return a new {@link Vector} which represents this {@link Vector}
	 *         multiplied by the provided scalar value
	 */
	public Vector getMultiplied(double factor) {
		return new Vector(x * factor, y * factor);
	}

	/**
	 * Creates a new {@link Vector} that has the same direction as this
	 * {@link Vector} and a length of 1.
	 *
	 * @return a new {@link Vector} with the same direction as this
	 *         {@link Vector} and a length of 1
	 */
	public Vector getNormalized() {
		return clone().getMultiplied(1 / getLength());
	}

	/**
	 * Returns the orthogonal complement of this {@link Vector}, which is
	 * defined to be (-y, x).
	 *
	 * @return the orthogonal complement of this {@link Vector}
	 */
	public Vector getOrthogonalComplement() {
		return new Vector(-y, x);
	}

	/**
	 * Returns a new {@link Vector} which corresponds to this {@link Vector}
	 * after rotating it counter-clockwise (CCW) by the given {@link Angle}.
	 *
	 * @param angle
	 *            the rotation {@link Angle}
	 * @return a new {@link Vector} which represents the result of the CCW
	 *         rotation of this {@link Vector}
	 */
	public Vector getRotatedCCW(Angle angle) {
		return clone().rotateCCW(angle);
	}

	/**
	 * Returns a new {@link Vector} which corresponds to this {@link Vector}
	 * after rotating it clockwise (CW) by the given {@link Angle}.
	 *
	 * @param angle
	 *            the rotation {@link Angle}
	 * @return a new {@link Vector} which represents the result of the CW
	 *         rotation of this {@link Vector}
	 */
	public Vector getRotatedCW(Angle angle) {
		return clone().rotateCW(angle);
	}

	/**
	 * Calculates the similarity of this {@link Vector} and the provided one.
	 * The similarity is defined as the absolute value of the dotProduct(). For
	 * orthogonal {@link Vector}s, 0 is returned.
	 *
	 * @param other
	 *            the {@link Vector} for which the similarity to this
	 *            {@link Vector} is calculated
	 * @return the similarity of this {@link Vector} and the provided one
	 * @see Vector#getDissimilarity(Vector)
	 */
	public double getSimilarity(Vector other) {
		return Math.abs(getDotProduct(other));
	}

	/**
	 * Returns a new {@link Vector} that represents the difference of this
	 * {@link Vector} and the provided one.
	 *
	 * @param other
	 *            the {@link Vector} that is subtracted from this {@link Vector}
	 * @return a new {@link Vector} representing the difference of this
	 *         {@link Vector} and the provided one
	 */
	public Vector getSubtracted(Vector other) {
		return new Vector(x - other.x, y - other.y);
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
	 * Checks if this {@link Vector} is horizontal, i.e. whether its horizontal
	 * component (the x coordinate) does not equal 0, while its vertical
	 * component (the y coordinate) does.
	 *
	 * @return <code>true</code> if this {@link Vector}'s x coordinate does not
	 *         equal 0 and this {@link Vector}'s y coordinate does equal 0,
	 *         otherwise <code>false</code>
	 */
	public boolean isHorizontal() {
		return !PrecisionUtils.equal(x, 0) && PrecisionUtils.equal(y, 0);
	}

	/**
	 * Checks if this {@link Vector}'s x and y coordinates are equal to 0.
	 *
	 * @return <code>true</code> if this {@link Vector}'s x and y coordinates
	 *         are equal to 0, otherwise <code>false</code>
	 */
	public boolean isNull() {
		return equals(NULL);
	}

	/**
	 * Checks if this {@link Vector} and the provided one are orthogonal to each
	 * other.
	 *
	 * @param other
	 *            the {@link Vector} which is checked for orthogonality to this
	 *            {@link Vector}
	 * @return <code>true</code> if this {@link Vector} and the provided one are
	 *         orthogonal to each other, otherwise <code>false</code>
	 */
	public boolean isOrthogonalTo(Vector other) {
		return PrecisionUtils.equal(getSimilarity(other), 0);
	}

	/**
	 * Checks if this {@link Vector} and the provided one are parallel to each
	 * other.
	 *
	 * @param other
	 *            the {@link Vector} that is checked to be parallel to this
	 *            {@link Vector}
	 * @return <code>true</code> if this {@link Vector} and the provided one are
	 *         parallel, otherwise <code>false</code>
	 */
	public boolean isParallelTo(Vector other) {
		Angle alpha = getAngle(other);
		alpha.setRad(2d * alpha.rad());
		return alpha.equals(Angle.fromRad(0d));
	}

	/**
	 * Checks if this {@link Vector} is vertical, i.e. whether its vertical
	 * component (the x coordinate) does not equal 0, while its horizontal
	 * component (the y coordinate) does.
	 *
	 * @return <code>true</code> if this {@link Vector}'s y coordinate does not
	 *         equal 0 and this {@link Vector}'s x coordinate does equal 0,
	 */
	public boolean isVertical() {
		return !PrecisionUtils.equal(y, 0) && PrecisionUtils.equal(x, 0);
	}

	/**
	 * Rotates this {@link Vector} counter-clockwise (CCW) by the given
	 * {@link Angle}.
	 *
	 * @param angle
	 *            the rotation {@link Angle}
	 * @return <code>this</code> for convenience
	 */
	public Vector rotateCCW(Angle angle) {
		return rotateCW(angle.getOppositeFull());
	}

	/**
	 * Rotates this {@link Vector} clockwise (CW) by the given {@link Angle}.
	 *
	 * @param angle
	 *            the rotation {@link Angle}
	 * @return <code>this</code> for convenience
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
	 * Returns a {@link Point} representing this {@link Vector}.
	 *
	 * @return a {@link Point} representing this {@link Vector}
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

}
