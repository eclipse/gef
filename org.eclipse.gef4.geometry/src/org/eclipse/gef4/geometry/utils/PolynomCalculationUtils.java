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
package org.eclipse.gef4.geometry.utils;

/**
 * Utility class that implements common polynom calculations such as finding the
 * roots of polynoms of degree 2 or 3.
 * 
 * @author wienand
 * 
 */
public final class PolynomCalculationUtils {
	/**
	 * Solves a linear polynomial equation of the form a*x + b = 0.
	 * 
	 * @param a
	 *            the coefficient of x^1
	 * @param b
	 *            the absolute element
	 * @return the roots of this polynom
	 */
	public static final double[] getLinearRoots(double a, double b) {
		if (a == 0) {
			if (b == 0) {
				throw new ArithmeticException(
						"The given polynomial equation is always true.");
			}
			return new double[] {};
		}
		return new double[] { -b / a };
	}

	/**
	 * Solves a quadratic polynomial equation of the form a*x^2 + b*x + c = 0.
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return all real solutions of the given equation. An empty array in the
	 *         case of no solutions.
	 */
	public static final double[] getQuadraticRoots(double a, double b, double c) {
		if (a == 0) {
			return getLinearRoots(b, c);
		}

		// p-q-formula
		double p = b / a;
		double q = c / a;
		double D = p * p / 4 - q;

		if (PrecisionUtils.equal(D, 0, +4)) {
			return new double[] { -p / 2 };
		} else if (D > 0) {
			double sqrt = Math.sqrt(D);
			return new double[] { sqrt - p / 2, -sqrt - p / 2 };
		} else {
			return new double[] {};
		}
	}

	/**
	 * Solves a cubic polynomial equation of the form Ax^3 + Bx^2 + Cx + D = 0.
	 * 
	 * @param A
	 * @param B
	 * @param C
	 * @param D
	 * @return all real solutions of the given equation
	 */
	public static final double[] getCubicRoots(double A, double B, double C,
			double D) {
		// TODO: use an algorithm that abstracts the polynom's order. A
		// possibility would be to use the CurveUtils$BezierCurve#contains(Point
		// p) method.

		if (A == 0) {
			return getQuadraticRoots(B, C, D);
		}

		double b = B / A;
		double c = C / A;
		double d = D / A;

		// reduce to t^3 + pt + q = 0 by substituting x = t - b/3
		// (http://en.wikipedia.org/wiki/Cubic_function#Cardano.27s_method)
		double p = c - b * b / 3;
		double q = 2 * b * b * b / 27 - b * c / 3 + d;

		// short-cut for p = 0
		if (PrecisionUtils.equal(p, 0, +4)) {
			// t^3 + q = 0 => t^3 = -q => t = cbrt(-q) => t - b/3 = cbrt(-q) -
			// b/3 => x = cbrt(-q) - b/3
			return new double[] { Math.cbrt(-q) - b / 3 };
		}

		double p_3 = p / 3;
		double q_2 = q / 2;

		D = q_2 * q_2 + p_3 * p_3 * p_3;

		if (PrecisionUtils.equal(D, 0, +4)) {
			// two real solutions
			return new double[] { 3 * q / p - b / 3, -3 * q / (2 * p) - b / 3 };
		} else if (D > 0) {
			// one real solution
			double u = Math.cbrt(-q_2 + Math.sqrt(D));
			double v = Math.cbrt(-q_2 - Math.sqrt(D));

			return new double[] { u + v - b / 3 };
		} else {
			// three real solutions
			double r = Math.sqrt(-p_3 * p_3 * p_3);
			double phi = Math.acos(-q / (2 * r));
			double co = 2 * Math.cbrt(r);

			// co * cos((phi + k * pi)/3) - a/3, k = 2n, n in N
			return new double[] { co * Math.cos(phi / 3) - b / 3,
					co * Math.cos((phi + 2 * Math.PI) / 3) - b / 3,
					co * Math.cos((phi + 4 * Math.PI) / 3) - b / 3 };
		}
	}

}
