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
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef.geometry.planar.CubicCurve;
import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.QuadraticCurve;
import org.junit.Test;

public class CubicCurveTests {

	private final Point p = new Point(-10, -10), c1 = new Point(0, -10),
			c2 = new Point(10, 0), q = new Point(0, 10);

	@Test
	public void test_contains_Point() {
		CubicCurve curve = new CubicCurve(p, c1, c2, q);

		// check fix points:
		assertEquals(true, curve.contains(p));
		assertEquals(true, curve.contains(q));
		assertEquals(false, curve.contains(c1)); // not always true, but for our
													// c1 it is
		assertEquals(false, curve.contains(c2)); // not always true, but for our
													// c2 it is

		// check 0 <= t <= 1:
		for (double t = 0; t <= 1; t += 0.0123456789) {
			assertEquals(
					"curve.get(t = " + t
							+ " in range [0, 1]) lies on the curve",
					true, curve.contains(curve.get(t)));
		}
	}

	@Test
	public void test_get() {
		CubicCurve curve = new CubicCurve(p, c1, c2, q);

		assertEquals("curve.get(0) returns the curve's start point", p,
				curve.get(0));
		assertEquals("curve.get(1) returns the curve's end point", q,
				curve.get(1));

		boolean thrown = false;
		try {
			curve.get(-0.1);
		} catch (IllegalArgumentException x) {
			thrown = true;
		}
		assertTrue("curve.get(t < 0) throws an IllegalArgumentException",
				thrown);

		thrown = false;
		try {
			curve.get(1.1);
		} catch (IllegalArgumentException x) {
			thrown = true;
		}
		assertTrue("curve.get(t > 1) throws an IllegalArgumentException",
				thrown);
	}

	@Test
	public void test_getBounds() {
		ICurve curve = new CubicCurve(p, c1, c2, q);

		// p is the top-left point: (y-coordinates are inverted)
		assertEquals(curve.getBounds().getTopLeft(), p);
	}

	@Test
	public void test_getIntersections_Line() {
		Line l = new Line(100.0, 150.0, 550.0, 150.0);
		CubicCurve c = new CubicCurve(504.0, 225.0, 504.0, 150.94119100209343,
				418.68233259706915, 90.0, 315.0, 90.0);
		Point[] inters = c.getIntersections(l);
		assertEquals(1, inters.length);
	}

	@Test
	public void test_getIntersections_linear_CubicCurve() {
		CubicCurve cc = new CubicCurve(100.0, 150.0, 200, 150, 300, 150, 550.0,
				150.0);
		CubicCurve c = new CubicCurve(504.0, 225.0, 504.0, 150.94119100209343,
				418.68233259706915, 90.0, 315.0, 90.0);
		Point[] inters = c.getIntersections(cc);
		assertEquals(1, inters.length);
	}

	@Test
	public void test_getIntersections_linear_QuadraticCurve() {
		QuadraticCurve qc = new QuadraticCurve(100.0, 150.0, 300, 150, 550.0,
				150.0);
		CubicCurve c = new CubicCurve(504.0, 225.0, 504.0, 150.94119100209343,
				418.68233259706915, 90.0, 315.0, 90.0);
		Point[] inters = c.getIntersections(qc);
		assertEquals(1, inters.length);
	}

	@Test
	public void test_getIntersections_with_CubicCurve() {
		CubicCurve cc1 = new CubicCurve(new Point(-10, -10), new Point(),
				new Point(), new Point(5, 5));
		CubicCurve cc2 = new CubicCurve(new Point(5, -5), new Point(),
				new Point(), new Point(-10, 10));
		assertEquals(1, cc1.getIntersections(cc2).length);
		assertEquals(1, cc2.getIntersections(cc1).length);

		// same end point
		cc1 = new CubicCurve(103.0, 401.0, 400.0, 200.0, 300.0, 300.0, 390.0,
				208.0);
		cc2 = new CubicCurve(584.0, 12.0, 200.0, 200.0, 300.0, 100.0, 390.0,
				208.0);
		assertEquals(1, cc1.getIntersections(cc2).length);
		assertEquals(1, cc2.getIntersections(cc1).length);

		cc1 = new CubicCurve(198.0, 103.0, 410.0, 215.0, 305.0, 320.0, 542.0,
				246.0);
		cc2 = new CubicCurve(101.0, 107.0, 197.0, 218.0, 302.0, 106.0, 542.0,
				246.0);
		assertEquals(2, cc1.getIntersections(cc2).length);
		assertEquals(2, cc2.getIntersections(cc1).length);

		cc1 = new CubicCurve(200.0, 100.0, 400.0, 200.0, 300.0, 300.0, 432.0,
				62.0);
		cc2 = new CubicCurve(100.0, 100.0, 200.0, 200.0, 300.0, 100.0, 432.0,
				62.0);
		assertEquals(2, cc1.getIntersections(cc2).length);
		assertEquals(2, cc2.getIntersections(cc1).length);

		cc1 = new CubicCurve(200.0, 100.0, 400.0, 200.0, 300.0, 300.0, 208.0,
				35.0);
		cc2 = new CubicCurve(100.0, 100.0, 200.0, 200.0, 300.0, 100.0, 208.0,
				35.0);
		assertEquals(3, cc1.getIntersections(cc2).length);
		assertEquals(3, cc2.getIntersections(cc1).length);

		cc1 = new CubicCurve(201.89274447949526, 106.43015521064301,
				403.7854889589905, 212.86031042128602, 302.8391167192429,
				319.290465631929, 81.0, 22.0);
		cc2 = new CubicCurve(100.94637223974763, 106.43015521064301,
				201.89274447949526, 212.86031042128602, 302.8391167192429,
				106.43015521064301, 81.0, 22.0);
		assertEquals(3, cc1.getIntersections(cc2).length);
		assertEquals(3, cc2.getIntersections(cc1).length);

		// torture tests from http://www.truetex.com/bezint.htm
		cc1 = new CubicCurve(1.0, 1.5, 15.5, 0.5, -8.0, 3.5, 5.0, 1.5);
		cc2 = new CubicCurve(4.0, 0.5, 5.0, 15.0, 2.0, -8.5, 4.0, 4.5);
		assertEquals(9, cc1.getIntersections(cc2).length);
		assertEquals(9, cc2.getIntersections(cc1).length);

		cc1 = new CubicCurve(664.00168, 0, 726.11545, 124.22757, 736.89069,
				267.89743, 694.0017, 400.0002);
		cc2 = new CubicCurve(850.66843, 115.55563, 728.515, 115.55563,
				725.21347, 275.15309, 694.0017, 400.0002);
		assertEquals(2, cc1.getIntersections(cc2).length);
		assertEquals(2, cc2.getIntersections(cc1).length);

		cc1 = new CubicCurve(1, 1, 12.5, 6.5, -4, 6.5, 7.5, 1);
		cc2 = new CubicCurve(1, 6.5, 12.5, 1, -4, 1, 7.5, 6);
		assertEquals(6, cc1.getIntersections(cc2).length);
		assertEquals(6, cc2.getIntersections(cc1).length);

		cc1 = new CubicCurve(187.0, 315.0, 345.0, 315.0, 515.0, 234.0, 519.0,
				133.0);
		cc2 = new CubicCurve(426.0, 227.0, 416.0, 245.0, 435.0, 256.0, 453.0,
				256.0);
		assertEquals(1, cc1.getIntersections(cc2).length);

		cc1 = new CubicCurve(164.0, 143.0, 78.0, 122.0, 124.0, 131.0, 126.0,
				113.0);
		cc2 = new CubicCurve(350.0, 61.0, 227.0, 62.0, 78.0, 145.0, 76.0,
				242.0);
		assertEquals(1, cc1.getIntersections(cc2).length);

		// not sure about these: (getIntersections() returns 3)
		// cc1 = new CubicCurve(315.748, 312.84, 312.644, 318.134, 305.836,
		// 319.909, 300.542, 316.804);
		// cc2 = new CubicCurve(317.122, 309.05, 316.112, 315.102, 310.385,
		// 319.19, 304.332, 318.179);

		// not sure about these: (getIntrsections() returns 0)
		// cc1 = new CubicCurve(125.79356, 199.57382, 51.16556, 128.93575,
		// 87.494,
		// 16.67848, 167.29361, 16.67848);
		// cc2 = new CubicCurve(167.29361, 55.81876, 100.36128, 55.81876,
		// 68.64099, 145.4755, 125.7942, 199.57309);
	}

	@Test
	public void test_getIntersections_with_CubicCurve_endPointsCheck() {
		CubicCurve cc1 = new CubicCurve(new Point(0, 0), new Point(0.1, 0),
				new Point(0.1, 0), new Point(0.1, 0.1));
		CubicCurve cc2 = new CubicCurve(new Point(0, 0), new Point(0.05, 0.1),
				new Point(0.05, 0.1), new Point(0.1, -0.1));
		assertEquals(2, cc1.getIntersections(cc2).length);
		assertEquals(2, cc2.getIntersections(cc1).length);
	}

	@Test
	public void test_getters_and_setters() {
		CubicCurve curve = new CubicCurve(p, c1, c2, q);
		assertEquals(curve.getP1(), p);
		assertEquals(curve.getP2(), q);
		assertEquals(curve.getCtrl1(), c1);
		assertEquals(curve.getCtrl2(), c2);
		Point newP = new Point(-5, -5);
		Point newC1 = new Point(5, -5);
		Point newC2 = new Point(5, 0);
		Point newQ = new Point(-5, 5);
		curve.setP1(newP);
		curve.setP2(newQ);
		curve.setCtrl1(newC1);
		curve.setCtrl2(newC2);
		assertEquals(curve.getP1(), newP);
		assertEquals(curve.getP2(), newQ);
		assertEquals(curve.getCtrl1(), newC1);
		assertEquals(curve.getCtrl2(), newC2);
	}

}
