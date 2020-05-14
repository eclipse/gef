/*******************************************************************************
 * Copyright (c) 2010, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.internal.utils;

/**
 * A utility class for floating point calculations and comparisons that should
 * guarantee a precision of a given scale, and ignore differences beyond this
 * scale.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class PrecisionUtils {

	/*
	 * Precise calculations on doubles are performed based on BigDecimals,
	 * converting to 8 digits scale, so there are no undesired rounding effects
	 * beyond this precision.
	 */
	private static final int DEFAULT_SCALE = 6;

	/**
	 * Computes the smallest double that is yet recognizable (by comparison)
	 * when shifting the default scale up by the given amount.
	 *
	 * @param shift
	 *            the number of digits to shift precision up (may be negative
	 *            number)
	 * @return the smallest double that will yet be recognizable by the methods
	 *         of this class during comparison, when using the default scale
	 *         shifted by the given amount.
	 */
	public static final double calculateFraction(int shift) {
		return 1 / Math.pow(10, DEFAULT_SCALE + shift);
	}

	/**
	 * @see PrecisionUtils#equal(double, double, int)
	 * @param d1
	 *            The first operand.
	 * @param d2
	 *            The second operand.
	 * @return result of the comparison
	 */
	public static final boolean equal(double d1, double d2) {
		return equal(d1, d2, 0);
	}

	/**
	 * Tests whether the two values are regarded to be equal w.r.t. the given
	 * shift.
	 *
	 * @param d1
	 *            the first value to test
	 * @param d2
	 *            the second value to test
	 * @param shift
	 *            the delta shift used for this test
	 * @return <code>true</code> in case the given two values are identical or
	 *         differ from each other by an amount that is smaller than what is
	 *         recognizable by the shifted delta, <code>false</code> otherwise
	 */
	public static final boolean equal(double d1, double d2, int shift) {
		// Check for undefined values
		if (Double.isNaN(d1) || Double.isNaN(d2)) {
			throw new IllegalArgumentException(
					"Cannot compare undefined values d1 = " + d1 + ", d2 = "
							+ d2);
		}
		return Math.abs(d1 - d2) <= calculateFraction(shift);
	}

	/**
	 * @see PrecisionUtils#greater(double, double, int)
	 * @param d1
	 *            The first operand.
	 * @param d2
	 *            The second operand.
	 * @return result of the comparison
	 */
	public static final boolean greater(double d1, double d2) {
		return greater(d1, d2, 0);
	}

	/**
	 * Tests whether the first given value is regarded to be greater than the
	 * second value w.r.t. the given shift.
	 *
	 * @param d1
	 *            the first value to test
	 * @param d2
	 *            the second value to test
	 * @param shift
	 *            the delta shift used for this test
	 * @return <code>true</code> in case the first value is greater than the
	 *         second value by an amount recognizable by the shifted delta,
	 *         <code>false</code> otherwise
	 */
	public static final boolean greater(double d1, double d2, int shift) {
		// Check for undefined values
		if (Double.isNaN(d1) || Double.isNaN(d2)) {
			throw new IllegalArgumentException(
					"Cannot compare undefined values d1 = " + d1 + ", d2 = "
							+ d2);
		}
		return d1 + calculateFraction(shift) > d2;
	}

	/**
	 * @see PrecisionUtils#greaterEqual(double, double, int)
	 * @param d1
	 *            The first operand.
	 * @param d2
	 *            The second operand.
	 * @return result of the comparison
	 */
	public static final boolean greaterEqual(double d1, double d2) {
		return greaterEqual(d1, d2, 0);
	}

	/**
	 * Tests whether the first given value is regarded to be greater or equal
	 * than the second value w.r.t. the given shift.
	 *
	 * @param d1
	 *            the first value to test
	 * @param d2
	 *            the second value to test
	 * @param shift
	 *            the delta shift used for this test
	 * @return <code>true</code> in case the first value is greater than the
	 *         second value by an amount recognizable by the given scale or
	 *         differs from it by an amount not recognizable by the shifted
	 *         delta, <code>false</code> otherwise
	 */
	public static final boolean greaterEqual(double d1, double d2, int shift) {
		// Check for undefined values
		if (Double.isNaN(d1) || Double.isNaN(d2)) {
			throw new IllegalArgumentException(
					"Cannot compare undefined values d1 = " + d1 + ", d2 = "
							+ d2);
		}
		return d1 + calculateFraction(shift) >= d2;
	}

	/**
	 * @see PrecisionUtils#smaller(double, double, int)
	 * @param d1
	 *            The first operand.
	 * @param d2
	 *            The second operand.
	 * @return result of the comparison
	 */
	public static final boolean smaller(double d1, double d2) {
		return smaller(d1, d2, 0);
	}

	/**
	 * Tests whether the first given value is regarded to be smaller than the
	 * second value w.r.t. the given shift.
	 *
	 * @param d1
	 *            the first value to test
	 * @param d2
	 *            the second value to test
	 * @param shift
	 *            the delta shift used for this test
	 * @return <code>true</code> in case the first value is smaller than the
	 *         second value by an amount recognizable by the shifted delta,
	 *         <code>false</code> otherwise
	 */
	public static final boolean smaller(double d1, double d2, int shift) {
		// Check for undefined values
		if (Double.isNaN(d1) || Double.isNaN(d2)) {
			throw new IllegalArgumentException(
					"Cannot compare undefined values d1 = " + d1 + ", d2 = "
							+ d2);
		}
		return d1 < d2 + calculateFraction(shift);
	}

	/**
	 * @see PrecisionUtils#smallerEqual(double, double, int)
	 * @param d1
	 *            The first operand.
	 * @param d2
	 *            The second operand.
	 * @return result of the comparison
	 */
	public static final boolean smallerEqual(double d1, double d2) {
		return smallerEqual(d1, d2, 0);
	}

	/**
	 * Tests whether the first given value is regarded to be smaller or equal
	 * than the second value w.r.t. the given shift.
	 *
	 * @param d1
	 *            the first value to test
	 * @param d2
	 *            the second value to test
	 * @param shift
	 *            the delta shift used for this test
	 * @return <code>true</code> in case the first value is smaller than the
	 *         second value by an amount recognizable by the given scale or
	 *         differs from it by an amount not recognizable by the shifted
	 *         delta, <code>false</code> otherwise
	 */
	public static final boolean smallerEqual(double d1, double d2, int shift) {
		// Check for undefined values
		if (Double.isNaN(d1) || Double.isNaN(d2)) {
			throw new IllegalArgumentException(
					"Cannot compare undefined values d1 = " + d1 + ", d2 = "
							+ d2);
		}
		return d1 <= d2 + calculateFraction(shift);
	}

	private PrecisionUtils() {
		// this class should not be instantiated by clients
	}
}
