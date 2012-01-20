/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry;

import java.io.Serializable;

import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * An {@link Angle} object abstracts the angle's unit. It provides a simple
 * interface to construct it from degrees or from radians. Additionally, some
 * useful calculations are implemented, but for sine/cosine/tangent calculations
 * you may use the Math package.
 * 
 * The {@link AngleUnit} enumeration is used to differentiate between degrees
 * and radians. For the sake of simplicity, the methods that need to
 * differentiate between the angle's unit are available twice. Expecting degrees
 * or radians.
 * 
 * Every {@link Angle} object is normalized. That means, you will never
 * encounter an {@link Angle} object beyond 360\u00b0/2pi or below 0\u00b0/0.
 * 
 * @author Matthias Wienand
 */
public class Angle implements Cloneable, Serializable {

	/**
	 * The {@link Angle#Angle(double, AngleUnit)} constructor uses this
	 * enumeration to differentiate the unit of its first argument.
	 * 
	 * @author wienand
	 */
	public enum AngleUnit {
		/**
		 * Specifies that the angle is given in degrees. The range of an angle
		 * in degrees is from 0\u00b0 to 360\u00b0.
		 */
		DEG,

		/**
		 * Specifies that the angle is given in radians. The range of an angle
		 * in radians is from 0 to 2pi.
		 */
		RAD,
	}

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
	 *            The angle in degrees.
	 * @return An {@link Angle} object representing the given degrees-angle.
	 */
	public static Angle fromDeg(double degrees) {
		return new Angle(degrees, AngleUnit.DEG);
	}

	/**
	 * Constructs a new {@link Angle} object representing the given value. The
	 * value is interpreted as being in radians.
	 * 
	 * @param radians
	 *            The angle in radians.
	 * @return An {@link Angle} object representing the given radians-angle.
	 */
	public static Angle fromRad(double radians) {
		return new Angle(radians, AngleUnit.RAD);
	}

	private double rad = 0d;

	/**
	 * Constructs a new {@link Angle} object with the given value. The
	 * {@link AngleUnit} u is used to differentiate the value's unit.
	 * 
	 * @param v
	 *            The angle's value.
	 * @param u
	 *            The angle's unit.
	 */
	public Angle(double v, AngleUnit u) {
		if (u == AngleUnit.DEG) {
			v *= DEG_TO_RAD;
		}
		setRad(v);
	}

	/**
	 * Constructs a new {@link Angle} object initialized with 0deg/0rad.
	 */
	public Angle() {
	}

	/**
	 * Overwritten with public visibility as proposed in {@link Cloneable}.
	 */
	@Override
	public Angle clone() {
		return getCopy();
	}

	/**
	 * Returns the value of this {@link Angle} object in degrees.
	 * 
	 * @return This {@link Angle}'s value in degrees.
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
	 * @return The sum of this and the given other {@link Angle} object as a new
	 *         {@link Angle} object.
	 */
	public Angle getAdded(Angle other) {
		return Angle.fromRad(this.rad + other.rad);
	}

	/**
	 * Returns a new {@link Angle} object representing this angle multiplied by
	 * the given factor.
	 * 
	 * @param factor
	 *            The multiplication factor.
	 * @return a new {@link Angle} object representing this angle multiplied by
	 *         the given factor.
	 */
	public Angle getMultiplied(double factor) {
		return Angle.fromRad(rad * factor);
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
	 * Returns the opposite {@link Angle} of this {@link Angle} in a full circle
	 * as a new {@link Angle} object.
	 * 
	 * @return The opposite {@link Angle} of this {@link Angle} in a full circle
	 *         as a new {@link Angle} object.
	 */
	public Angle getOppositeFull() {
		return Angle.fromRad(RAD_360 - rad);
	}

	/**
	 * Returns the opposite {@link Angle} of this {@link Angle} in a semi-circle
	 * as a new {@link Angle} object.
	 * 
	 * @return The opposite {@link Angle} of this {@link Angle} in a semi-circle
	 *         as a new {@link Angle} object.
	 */
	public Angle getOppositeSemi() {
		return Angle.fromRad(RAD_180 - rad);
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
	 * Normalizes this {@link Angle} to the range from 0deg to 360deg or rather
	 * from 0 to 2pi.
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
	 * Sets this {@link Angle}'s value to the given angle in degrees.
	 * 
	 * @param degrees
	 *            The angle in degrees.
	 */
	public void setDeg(double degrees) {
		rad = degrees * DEG_TO_RAD;
		normalize();
	}

	/**
	 * Sets this {@link Angle}'s value to the given angle in radians.
	 * 
	 * @param radians
	 *            The angle value in radians.
	 */
	public void setRad(double radians) {
		rad = radians;
		normalize();
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "Angle(" + Double.toString(rad) + "rad ("
				+ Double.toString(deg()) + "deg))";
	}
}
