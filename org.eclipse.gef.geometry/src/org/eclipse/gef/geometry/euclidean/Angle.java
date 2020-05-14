/*******************************************************************************
 * Copyright (c) 2011, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Colin Sharples - contribution for Bugzilla #460754, #491402
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.euclidean;

import java.io.Serializable;

import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;

/**
 * <p>
 * An {@link Angle} object abstracts the angle's unit. It provides a simple
 * interface to construct it from degrees or radians. Additionally, some useful
 * calculations are implemented. But for sine/cosine/tangent calculations you
 * may use the Math package.
 * </p>
 * <p>
 * Every {@link Angle} object is normalized. That means, you will never
 * encounter an {@link Angle} object beyond 360/2pi or below 0/0
 * (degrees/radians).
 * </p>
 *
 * @author mwienand
 *
 */
public class Angle implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	private static final double DEG_TO_RAD = Math.PI / 180d;

	private static final double RAD_TO_DEG = 180d / Math.PI;
	private static final double RAD_180 = Math.PI;
	private static final double RAD_360 = 2 * Math.PI;

	/**
	 * Constructs a new {@link Angle} object representing the given value. The
	 * value is interpreted as being in degrees.
	 *
	 * @param degrees
	 *            the angle in degrees
	 * @return an {@link Angle} object representing the passed-in angle given in
	 *         degrees
	 */
	public static Angle fromDeg(double degrees) {
		return new Angle(degrees * DEG_TO_RAD);
	}

	/**
	 * Constructs a new {@link Angle} object representing the given value. The
	 * value is interpreted as being in radians.
	 *
	 * @param radians
	 *            the angle in radians
	 * @return an {@link Angle} object representing the passed-in angle given in
	 *         radians
	 */
	public static Angle fromRad(double radians) {
		return new Angle(radians);
	}

	private double rad = 0d;

	/**
	 * Constructs a new {@link Angle} object initialized with 0deg/0rad.
	 */
	public Angle() {
	}

	/**
	 * Constructs a new {@link Angle} object with the given value in radians.
	 *
	 * @param rad
	 *            the angle's value
	 */
	public Angle(double rad) {
		setRad(rad);
	}

	/**
	 * Overridden with public visibility as proposed in {@link Cloneable}.
	 */
	@Override
	public Angle clone() {
		return getCopy();
	}

	/**
	 * Returns the value of this {@link Angle} object in degrees.
	 *
	 * @return this {@link Angle}'s value in degrees.
	 */
	public double deg() {
		return rad * RAD_TO_DEG;
	}

	@Override
	public boolean equals(Object otherObj) {
		if (otherObj != null && otherObj instanceof Angle) {
			Angle other = (Angle) otherObj;

			double myRad = this.rad;
			double otherRad = other.rad;

			final double hi = 1.5 * Math.PI;
			final double lo = 0.5 * Math.PI;

			if (myRad > hi && otherRad < lo) {
				otherRad += RAD_360;
			} else if (myRad < lo && otherRad > hi) {
				myRad += RAD_360;
			}

			return PrecisionUtils.equal(myRad, otherRad);
		}
		return false;
	}

	/**
	 * Returns the sum of this and the given other {@link Angle} object as a new
	 * {@link Angle} object.
	 *
	 * @param other
	 *            the {@link Angle} to add
	 * @return the sum of this and the given other {@link Angle} as a new
	 *         {@link Angle} object
	 */
	public Angle getAdded(Angle other) {
		return Angle.fromRad(this.rad + other.rad);
	}

	/**
	 * Creates and returns a copy of this {@link Angle}.
	 *
	 * @return a copy of this {@link Angle}
	 */
	public Angle getCopy() {
		return Angle.fromRad(this.rad);
	}

	/**
	 * Returns the difference between this {@link Angle} and another
	 * {@link Angle} in a counter-clockwise direction
	 *
	 * @param other
	 *            the other angle to compare to
	 * @return the difference between this {@link Angle} and another
	 *         {@link Angle} in a counter-clockwise direction
	 */
	public Angle getDeltaCCW(Angle other) {
		return new Angle(this.rad - other.rad);
	}

	/**
	 * Returns the difference between this {@link Angle} and another
	 * {@link Angle} in a clockwise direction
	 *
	 * @param other
	 *            the other angle to compare to
	 * @return the difference between this {@link Angle} and another
	 *         {@link Angle} in a clockwise direction
	 */
	public Angle getDeltaCW(Angle other) {
		return new Angle(other.rad - this.rad);
	}

	/**
	 * Returns a new {@link Angle} object representing this {@link Angle}
	 * multiplied by the given factor.
	 *
	 * @param factor
	 *            the multiplication factor
	 * @return a new {@link Angle} object representing this {@link Angle}
	 *         multiplied by the given factor
	 */
	public Angle getMultiplied(double factor) {
		return Angle.fromRad(rad * factor);
	}

	/**
	 * Returns the opposite {@link Angle} of this {@link Angle} in a full circle
	 * as a new {@link Angle} object.
	 *
	 * @return the opposite {@link Angle} of this {@link Angle} in a full circle
	 *         as a new {@link Angle} object
	 */
	public Angle getOppositeFull() {
		return Angle.fromRad(RAD_360 - rad);
	}

	/**
	 * Returns the opposite {@link Angle} of this {@link Angle} in a semi-circle
	 * as a new {@link Angle} object.
	 *
	 * @return the opposite {@link Angle} of this {@link Angle} in a semi-circle
	 *         as a new {@link Angle} object
	 */
	public Angle getOppositeSemi() {
		return Angle.fromRad(RAD_180 - rad);
	}

	/**
	 * Returns the reverse {@link Angle} of this {@link Angle} in a full circle
	 * as a new {@link Angle} object.
	 *
	 * @return the reverse {@link Angle} of this {@link Angle} in a full circle
	 *         as a new {@link Angle} object
	 */
	public Angle getReverse() {
		return Angle.fromRad(rad + RAD_180);
	}

	@Override
	public int hashCode() {
		// calculating a better hashCode is not possible, because due to the
		// imprecision, equals() is no longer transitive
		return 0;
	}

	/**
	 * Tests if the other {@link Angle} is within a half-circle clockwise
	 * rotation from this {@link Angle}
	 *
	 * @param other
	 *            the other angle to compare to
	 * @return true if the a clockwise rotation to the other angle is less than
	 *         180deg
	 */
	public boolean isClockwise(Angle other) {
		return getDeltaCW(other).rad <= RAD_180;
	}

	/**
	 * Normalizes this {@link Angle} to the range from 0deg to 360deg or rather
	 * from 0 to 2pi (radians).
	 */
	private Angle normalize() {
		rad -= RAD_360 * Math.floor(rad / RAD_360);
		return this;
	}

	/**
	 * Returns this {@link Angle}'s value in radians.
	 *
	 * @return This {@link Angle}'s value in radians.
	 */
	public double rad() {
		return rad;
	}

	/**
	 * Sets this {@link Angle}'s value to the passed-in value in degrees.
	 *
	 * @param degrees
	 *            the angle's value in degrees
	 * @return <code>this</code> for convenience
	 */
	public Angle setDeg(double degrees) {
		rad = degrees * DEG_TO_RAD;
		normalize();
		return this;
	}

	/**
	 * Sets this {@link Angle}'s value to the passed-in value in radians.
	 *
	 * @param radians
	 *            the angle's value in radians
	 * @return <code>this</code> for convenience
	 */
	public Angle setRad(double radians) {
		rad = radians;
		normalize();
		return this;
	}

	@Override
	public String toString() {
		return "Angle(" + Double.toString(rad) + "rad ("
				+ Double.toString(deg()) + "deg))";
	}

}
