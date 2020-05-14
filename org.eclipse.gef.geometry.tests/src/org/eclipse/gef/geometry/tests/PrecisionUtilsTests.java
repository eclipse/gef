/*******************************************************************************
 * Copyright (c) 2011, 2016 IBM Corporation and others.
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
package org.eclipse.gef.geometry.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;
import org.junit.Test;

/**
 * Unit tests for {@link PrecisionUtils}.
 * 
 * @author anyssen
 * @author mwienand
 * 
 */
public class PrecisionUtilsTests {

	private static final double PRECISION_FRACTION = TestUtils
			.getPrecisionFraction();

	private static final double RECOGNIZABLE_FRACTION = PRECISION_FRACTION
			+ PRECISION_FRACTION / 10;

	private static final double UNRECOGNIZABLE_FRACTION = PRECISION_FRACTION
			- PRECISION_FRACTION / 10;

	/**
	 * Tests the precision tolerance of
	 * {@link PrecisionUtils#equal(double, double)}, by checking whether two
	 * doubles that differ from each other by an amount beyond the desired
	 * precision scale are regarded to be equal while two that differ by an
	 * amount outside the desired precision scale are considered non-equal.
	 */
	@Test
	public void test_equal() {
		// test equality is recognized in case we increase/decrease beyond the
		// given scale
		double d1 = -9.48661417322834712;
		double d2 = -34.431496062992123985;
		double d3 = 41.99055118110236626;
		double d4 = 25.927559055118116565;

		assertTrue(PrecisionUtils.equal(d1, d1 - UNRECOGNIZABLE_FRACTION));
		assertTrue(PrecisionUtils.equal(d2, d2 - UNRECOGNIZABLE_FRACTION));
		assertTrue(PrecisionUtils.equal(d3, d3 - UNRECOGNIZABLE_FRACTION));
		assertTrue(PrecisionUtils.equal(d4, d4 - UNRECOGNIZABLE_FRACTION));

		assertTrue(PrecisionUtils.equal(d1, d1 + UNRECOGNIZABLE_FRACTION));
		assertTrue(PrecisionUtils.equal(d2, d2 + UNRECOGNIZABLE_FRACTION));
		assertTrue(PrecisionUtils.equal(d3, d3 + UNRECOGNIZABLE_FRACTION));
		assertTrue(PrecisionUtils.equal(d4, d4 + UNRECOGNIZABLE_FRACTION));

		assertFalse(PrecisionUtils.equal(d1, d1 - RECOGNIZABLE_FRACTION));
		assertFalse(PrecisionUtils.equal(d2, d2 - RECOGNIZABLE_FRACTION));
		assertFalse(PrecisionUtils.equal(d3, d3 - RECOGNIZABLE_FRACTION));
		assertFalse(PrecisionUtils.equal(d4, d4 - RECOGNIZABLE_FRACTION));

		assertFalse(PrecisionUtils.equal(d1, d1 + RECOGNIZABLE_FRACTION));
		assertFalse(PrecisionUtils.equal(d2, d2 + RECOGNIZABLE_FRACTION));
		assertFalse(PrecisionUtils.equal(d3, d3 + RECOGNIZABLE_FRACTION));
		assertFalse(PrecisionUtils.equal(d4, d4 + RECOGNIZABLE_FRACTION));
	}

	@Test
	public void test_greater() {
		double unrec = UNRECOGNIZABLE_FRACTION;

		assertTrue(PrecisionUtils.greater(1, 0));
		assertTrue(PrecisionUtils.greater(RECOGNIZABLE_FRACTION, 0));
		assertTrue(PrecisionUtils.greater(unrec, 0));
		assertTrue(PrecisionUtils.greater(0, 0));
		assertTrue(PrecisionUtils.greater(-unrec, 0));
		assertTrue(PrecisionUtils.greater(unrec - PRECISION_FRACTION, 0));
		assertFalse(PrecisionUtils.greater(-1, 0));
		assertFalse(PrecisionUtils.greater(-PRECISION_FRACTION, 0));
	}

	@Test
	public void test_greaterEqual() {
		double unrec = UNRECOGNIZABLE_FRACTION;

		assertTrue(PrecisionUtils.greaterEqual(1, 0));
		assertTrue(PrecisionUtils.greaterEqual(0, 0));
		assertTrue(PrecisionUtils.greaterEqual(-unrec, 0));
		assertTrue(PrecisionUtils.greaterEqual(-PRECISION_FRACTION, 0));
		assertFalse(PrecisionUtils.greaterEqual(-1, 0));
		assertFalse(
				PrecisionUtils.greaterEqual(-PRECISION_FRACTION - unrec, 0));
	}

	@Test
	public void test_smaller() {
		double unrec = UNRECOGNIZABLE_FRACTION;

		assertTrue(PrecisionUtils.smaller(-1, 0));
		assertTrue(PrecisionUtils.smaller(-PRECISION_FRACTION, 0));
		assertTrue(PrecisionUtils.smaller(-unrec, 0));
		assertTrue(PrecisionUtils.smaller(0, 0));
		assertTrue(PrecisionUtils.smaller(unrec, 0));
		assertTrue(PrecisionUtils.smaller(PRECISION_FRACTION - unrec, 0));
		assertFalse(PrecisionUtils.smaller(1, 0));
		assertFalse(PrecisionUtils.smaller(PRECISION_FRACTION, 0));
	}

	@Test
	public void test_smallerEqual() {
		double unrec = UNRECOGNIZABLE_FRACTION;

		assertTrue(PrecisionUtils.smallerEqual(-1, 0));
		assertTrue(PrecisionUtils.smallerEqual(0, 0));
		assertTrue(PrecisionUtils.smallerEqual(unrec, 0));
		assertTrue(PrecisionUtils.smallerEqual(PRECISION_FRACTION, 0));
		assertFalse(PrecisionUtils.smallerEqual(1, 0));
		assertFalse(PrecisionUtils.smallerEqual(PRECISION_FRACTION + unrec, 0));
	}
}
