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
package org.eclipse.gef4.geometry.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.eclipse.gef4.geometry.utils.PolynomCalculationUtils;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;
import org.junit.Test;

public class PolynomCalculationUtilsTests {

	@Test
	public void test_get_cubic_roots() {
		double[] solutions = PolynomCalculationUtils.getCubicRoots(0, 0, 1, 0);
		assertEquals("fallthrough", 1, solutions.length);
		assertTrue("fallthrough", PrecisionUtils.equal(solutions[0], 0));

		solutions = PolynomCalculationUtils.getCubicRoots(1, 0, 1, 0);
		Arrays.sort(solutions);
		assertEquals("one real solution", 1, solutions.length);
		assertTrue("x1 = 0", PrecisionUtils.equal(solutions[0], 0));

		solutions = PolynomCalculationUtils.getCubicRoots(1, 0, -1, 0);
		Arrays.sort(solutions);
		assertEquals("three real solutions", 3, solutions.length);
		assertTrue("x1 = -1", PrecisionUtils.equal(solutions[0], -1));
		assertTrue("x2 = 0", PrecisionUtils.equal(solutions[1], 0));
		assertTrue("x3 = 1", PrecisionUtils.equal(solutions[2], 1));

		solutions = PolynomCalculationUtils.getCubicRoots(1, 1, 1, 0);
		Arrays.sort(solutions);
		assertEquals("one real solution", 1, solutions.length);
		assertTrue("x^3 + x^2 + x = 0 <=> x(x^2 + x + 1) = 0 => x = 0",
				PrecisionUtils.equal(0, solutions[0]));

		solutions = PolynomCalculationUtils.getCubicRoots(1, -6, 12, -8);
		assertEquals("one real solution", 1, solutions.length);
		assertTrue("x = 2 solves the polynom",
				PrecisionUtils.equal(2, solutions[0]));

		solutions = PolynomCalculationUtils.getCubicRoots(1, 1, -33, 63);
		Arrays.sort(solutions);
		assertEquals("two real solutions", 2, solutions.length);
		assertTrue("x1 = -7", PrecisionUtils.equal(-7, solutions[0]));
		assertTrue("x2 = 3", PrecisionUtils.equal(3, solutions[1]));

		solutions = PolynomCalculationUtils.getCubicRoots(1, 3, -6, -1);
		Arrays.sort(solutions);
		assertEquals("three real solutions", 3, solutions.length);
		assertTrue("x1 = -4.33181031",
				PrecisionUtils.equal(-4.331810310203, solutions[0]));
		assertTrue("x2 = -0.15524041",
				PrecisionUtils.equal(-0.15524041215, solutions[1]));
		assertTrue("x3 = 1.48705072",
				PrecisionUtils.equal(1.487050722353, solutions[2]));

		// q = 0
		solutions = PolynomCalculationUtils.getCubicRoots(-10, 30, 0, -20);
		Arrays.sort(solutions);
		assertEquals(3, solutions.length);
		assertTrue(PrecisionUtils.equal(-0.7320508075688774, solutions[0]));
		assertTrue(PrecisionUtils.equal(1, solutions[1]));
		assertTrue(PrecisionUtils.equal(2.7320508075688776, solutions[2]));
	}

	@Test
	public void test_get_linear_roots() {
		double[] solutions = PolynomCalculationUtils.getLinearRoots(1, 0);
		assertEquals("one real solution", 1, solutions.length);
		assertTrue("x = 0", PrecisionUtils.equal(0, solutions[0]));

		solutions = PolynomCalculationUtils.getLinearRoots(0, 1);
		assertEquals("1 != 0", 0, solutions.length);

		solutions = PolynomCalculationUtils.getLinearRoots(1, -2);
		assertEquals("one real solution", 1, solutions.length);
		assertTrue("x - 2 = 0 <=> x = 2", PrecisionUtils.equal(2, solutions[0]));

		solutions = PolynomCalculationUtils.getLinearRoots(7, -7);
		assertEquals("one real solution", 1, solutions.length);
		assertTrue("7x - 7 = 0 <=> x = 1",
				PrecisionUtils.equal(1, solutions[0]));

		solutions = PolynomCalculationUtils.getLinearRoots(0.5, -10);
		assertEquals("one real solution", 1, solutions.length);
		assertTrue("0.5x - 10 = 0 <=> x = 20",
				PrecisionUtils.equal(20, solutions[0]));

		solutions = PolynomCalculationUtils.getLinearRoots(5, -0.5);
		assertEquals("one real solution", 1, solutions.length);
		assertTrue("5x - 0.5 = 0 <=> x = 0.1",
				PrecisionUtils.equal(0.1, solutions[0]));

		solutions = PolynomCalculationUtils.getLinearRoots(1, 1);
		assertEquals("one real solution", 1, solutions.length);
		assertTrue("x + 1 = 0 <=> x = -1",
				PrecisionUtils.equal(-1, solutions[0]));
	}

	@Test
	public void test_get_quadratic_roots() {
		double[] solutions = PolynomCalculationUtils.getQuadraticRoots(1, 1, 0);
		Arrays.sort(solutions);
		assertEquals("two real solution", 2, solutions.length);
		assertTrue("x^2 + x = 0 <=> x(x + 1) = 0 => x = 0 v x = -1",
				PrecisionUtils.equal(-1, solutions[0]));
		assertTrue("x^2 + x + 0 = 0 <=> x(x + 1) = 0 => x = 0 v x = -1",
				PrecisionUtils.equal(0, solutions[1]));

		solutions = PolynomCalculationUtils.getQuadraticRoots(1, 0, 1);
		assertEquals("x^2 + 1 = 0 => no real solutions", 0, solutions.length);

		solutions = PolynomCalculationUtils.getQuadraticRoots(1, 0, -1);
		Arrays.sort(solutions);
		assertEquals("two real solution", 2, solutions.length);
		assertTrue("x^2 - 1 = 0 => x = +-1",
				PrecisionUtils.equal(-1, solutions[0]));
		assertTrue("x^2 - 1 = 0 => x = +-1",
				PrecisionUtils.equal(1, solutions[1]));

		solutions = PolynomCalculationUtils.getQuadraticRoots(1, 0, 0);
		assertEquals("one real solution", 1, solutions.length);
		assertTrue("x^2 = 0 <=> x = 0", PrecisionUtils.equal(0, solutions[0]));

		solutions = PolynomCalculationUtils.getQuadraticRoots(0, 1, 0);
		assertEquals("one real solution", 1, solutions.length);
		assertTrue("x = 0", PrecisionUtils.equal(0, solutions[0]));

		solutions = PolynomCalculationUtils.getQuadraticRoots(2, 0, -8);
		Arrays.sort(solutions);
		assertEquals("two real solution", 2, solutions.length);
		assertTrue("2x^2 - 8 = 0 <=> x^2 = 4 => x = +-2",
				PrecisionUtils.equal(-2, solutions[0]));
		assertTrue("2x^2 - 8 = 0 <=> x^2 = 4 => x = +-2",
				PrecisionUtils.equal(2, solutions[1]));

		solutions = PolynomCalculationUtils.getQuadraticRoots(1, -3, 2);
		Arrays.sort(solutions);
		assertEquals("two real solution", 2, solutions.length);
		assertTrue("x^2 - 3x + 2 = 0 <=> (x - 2)(x - 1) = 0 => x = 2 v x = 1",
				PrecisionUtils.equal(1, solutions[0]));
		assertTrue("x^2 - 3x + 2 = 0 <=> (x - 2)(x - 1) = 0 => x = 2 v x = 1",
				PrecisionUtils.equal(2, solutions[1]));
	}

	@Test
	public void test_get_roots_always_true() {
		try {
			PolynomCalculationUtils.getLinearRoots(0, 0);
		} catch (ArithmeticException x) {
			assertTrue("0x + 0 = 0  throws an ArithmeticException", true);
		}

		try {
			PolynomCalculationUtils.getQuadraticRoots(0, 0, 0);
		} catch (ArithmeticException x) {
			assertTrue("0x^2 + 0x + 0 = 0  throws an ArithmeticException", true);
		}

		try {
			PolynomCalculationUtils.getCubicRoots(0, 0, 0, 0);
		} catch (ArithmeticException x) {
			assertTrue(
					"0x^3 + 0x^2 + 0x + 0 = 0  throws an ArithmeticException",
					true);
		}
	}

	// @Test
	// public void test_getRoots_arbitrary() {
	// // double[] solutions = PolynomCalculationUtils.getRoots(1, 0, 1, 0);
	// // Arrays.sort(solutions);
	// // assertEquals("one real solution", 1, solutions.length);
	// // assertTrue("x1 = 0", PrecisionUtils.equal(solutions[0], 0));
	//
	// solutions = PolynomCalculationUtils.getCubicRoots(1, 0, -1, 0);
	// Arrays.sort(solutions);
	// assertEquals("three real solutions", 3, solutions.length);
	// assertTrue("x1 = -1", PrecisionUtils.equal(solutions[0], -1));
	// assertTrue("x2 = 0", PrecisionUtils.equal(solutions[1], 0));
	// assertTrue("x3 = 1", PrecisionUtils.equal(solutions[2], 1));
	//
	// solutions = PolynomCalculationUtils.getCubicRoots(1, 1, 1, 0);
	// Arrays.sort(solutions);
	// assertEquals("one real solution", 1, solutions.length);
	// assertTrue("x^3 + x^2 + x = 0 <=> x(x^2 + x + 1) = 0 => x = 0",
	// PrecisionUtils.equal(0, solutions[0]));
	//
	// solutions = PolynomCalculationUtils.getCubicRoots(1, -6, 12, -8);
	// assertEquals("one real solution", 1, solutions.length);
	// assertTrue("x = 2 solves the polynom",
	// PrecisionUtils.equal(2, solutions[0]));
	//
	// solutions = PolynomCalculationUtils.getCubicRoots(1, 1, -33, 63);
	// Arrays.sort(solutions);
	// assertEquals("two real solutions", 2, solutions.length);
	// assertTrue("x1 = -7", PrecisionUtils.equal(-7, solutions[0]));
	// assertTrue("x2 = 3", PrecisionUtils.equal(3, solutions[1]));
	//
	// solutions = PolynomCalculationUtils.getCubicRoots(1, 3, -6, -1);
	// Arrays.sort(solutions);
	// assertEquals("three real solutions", 3, solutions.length);
	// assertTrue("x1 = -4.33181031",
	// PrecisionUtils.equal(-4.331810310203, solutions[0]));
	// assertTrue("x2 = -0.15524041",
	// PrecisionUtils.equal(-0.15524041215, solutions[1]));
	// assertTrue("x3 = 1.48705072",
	// PrecisionUtils.equal(1.487050722353, solutions[2]));
	//
	// // q = 0
	// solutions = PolynomCalculationUtils.getCubicRoots(-10, 30, 0, -20);
	// Arrays.sort(solutions);
	// assertEquals(3, solutions.length);
	// assertTrue(PrecisionUtils.equal(-0.7320508075688774, solutions[0]));
	// assertTrue(PrecisionUtils.equal(1, solutions[1]));
	// assertTrue(PrecisionUtils.equal(2.7320508075688776, solutions[2]));
	// }
}
