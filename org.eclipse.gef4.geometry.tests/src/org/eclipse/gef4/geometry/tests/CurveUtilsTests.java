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

import java.util.Random;

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.planar.CubicCurve;
import org.eclipse.gef4.geometry.planar.QuadraticCurve;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.utils.CurveUtils;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;
import org.junit.Test;

public class CurveUtilsTests {

	private static final long SEED = 123;

	@Test
	public void test_getSignedDistance_direction() {
		// sign-checks:
		// first quadrant (y-axis is inverted)
		assertTrue(CurveUtils.getSignedDistance(new Point(),
				new Point(10, -10), new Point(0, -10)) > 0);
		assertTrue(CurveUtils.getSignedDistance(new Point(10, -10),
				new Point(), new Point(0, -10)) < 0);
		assertTrue(CurveUtils.getSignedDistance(new Point(),
				new Point(10, -10), new Point(1, -1)) == 0);

		// second quadrant (y-axis is inverted)
		assertTrue(CurveUtils.getSignedDistance(new Point(),
				new Point(-10, -10), new Point(0, -10)) < 0);
		assertTrue(CurveUtils.getSignedDistance(new Point(-10, -10),
				new Point(), new Point(0, -10)) > 0);
		assertTrue(CurveUtils.getSignedDistance(new Point(),
				new Point(-10, -10), new Point(-1, -1)) == 0);

		// third quadrant (y-axis is inverted)
		assertTrue(CurveUtils.getSignedDistance(new Point(), new Point(10, 10),
				new Point(0, 10)) < 0);
		assertTrue(CurveUtils.getSignedDistance(new Point(10, 10), new Point(),
				new Point(0, 10)) > 0);
		assertTrue(CurveUtils.getSignedDistance(new Point(), new Point(10, 10),
				new Point(1, 1)) == 0);

		// forth quadrant (y-axis is inverted)
		assertTrue(CurveUtils.getSignedDistance(new Point(),
				new Point(-10, 10), new Point(0, 10)) > 0);
		assertTrue(CurveUtils.getSignedDistance(new Point(-10, 10),
				new Point(), new Point(0, 10)) < 0);
		assertTrue(CurveUtils.getSignedDistance(new Point(),
				new Point(-10, 10), new Point(-1, 1)) == 0);
	}

	@Test
	public void test_getSignedDistance_abs() {
		// do only check for the absolute value of the signed distance

		assertTrue(PrecisionUtils.equal(Math.abs(CurveUtils.getSignedDistance(
				new Point(0, -5), new Point(0, 5), new Point(5, 0))), 5));
		assertTrue(PrecisionUtils.equal(Math.abs(CurveUtils.getSignedDistance(
				new Point(0, 5), new Point(0, -5), new Point(5, 0))), 5));

		assertTrue(PrecisionUtils.equal(Math.abs(CurveUtils.getSignedDistance(
				new Point(0, -1), new Point(0, 1), new Point(5, 0))), 5));
		assertTrue(PrecisionUtils.equal(Math.abs(CurveUtils.getSignedDistance(
				new Point(0, 1), new Point(0, -1), new Point(5, 0))), 5));

		assertTrue(PrecisionUtils.equal(Math.abs(CurveUtils.getSignedDistance(
				new Point(-5, 0), new Point(5, 0), new Point(0, 5))), 5));
		assertTrue(PrecisionUtils.equal(Math.abs(CurveUtils.getSignedDistance(
				new Point(-5, 0), new Point(5, 0), new Point(0, 5))), 5));

		assertTrue(PrecisionUtils.equal(Math.abs(CurveUtils.getSignedDistance(
				new Point(-1, 0), new Point(1, 0), new Point(0, 5))), 5));
		assertTrue(PrecisionUtils.equal(Math.abs(CurveUtils.getSignedDistance(
				new Point(-1, 0), new Point(1, 0), new Point(0, 5))), 5));
	}

	@Test
	public void test_getSignedDistance() {
		// check both, direction and value:
		// first quadrant (y-axis is inverted)
		double len = 10d / Math.sqrt(2);
		assertTrue(PrecisionUtils.equal(CurveUtils.getSignedDistance(
				new Point(), new Point(10, -10), new Point(0, -10)), len));
		assertTrue(PrecisionUtils.equal(CurveUtils.getSignedDistance(new Point(
				10, -10), new Point(), new Point(0, -10)), -len));
		assertTrue(PrecisionUtils.equal(CurveUtils.getSignedDistance(
				new Point(), new Point(10, -10), new Point(1, -1)), 0));

		// second quadrant (y-axis is inverted)
		assertTrue(PrecisionUtils.equal(CurveUtils.getSignedDistance(
				new Point(), new Point(-10, -10), new Point(0, -10)), -len));
		assertTrue(PrecisionUtils.equal(CurveUtils.getSignedDistance(new Point(
				-10, -10), new Point(), new Point(0, -10)), len));
		assertTrue(PrecisionUtils.equal(CurveUtils.getSignedDistance(
				new Point(), new Point(-10, -10), new Point(-1, -1)), 0));

		// third quadrant (y-axis is inverted)
		assertTrue(PrecisionUtils.equal(CurveUtils.getSignedDistance(
				new Point(), new Point(10, 10), new Point(0, 10)), -len));
		assertTrue(PrecisionUtils.equal(CurveUtils.getSignedDistance(new Point(
				10, 10), new Point(), new Point(0, 10)), len));
		assertTrue(PrecisionUtils.equal(CurveUtils.getSignedDistance(
				new Point(), new Point(10, 10), new Point(1, 1)), 0));

		// forth quadrant (y-axis is inverted)
		assertTrue(PrecisionUtils.equal(CurveUtils.getSignedDistance(
				new Point(), new Point(-10, 10), new Point(0, 10)), len));
		assertTrue(PrecisionUtils.equal(CurveUtils.getSignedDistance(new Point(
				-10, 10), new Point(), new Point(0, 10)), -len));
		assertTrue(PrecisionUtils.equal(CurveUtils.getSignedDistance(
				new Point(), new Point(-10, 10), new Point(-1, 1)), 0));
	}

	@Test
	public void test_split_with_QuadraticCurve() {
		final int numPoints = 4;
		final double step = 0.123456789;

		Random rng = new Random(SEED);

		for (int i = 0; i < 1000; i++) {
			Point[] points = new Point[numPoints];
			for (int j = 0; j < numPoints; j++) {
				points[j] = new Point(rng.nextDouble(), rng.nextDouble());
			}

			QuadraticCurve c = new QuadraticCurve(points);
			for (double t = 0; t <= 1; t += step) {
				QuadraticCurve[] cs = c.split(t);
				assertEquals(c.get(t), cs[0].get(1));
				assertEquals(c.get(t), cs[1].get(0));
				assertEquals(c.get(0), cs[0].get(0));
				assertEquals(c.get(1), cs[1].get(1));
			}
		}
	}

	@Test
	public void test_clip_with_QuadraticCurve() {
		final int numPoints = 4;
		final double step = 0.123456789;

		Random rng = new Random(SEED);

		for (int i = 0; i < 1000; i++) {
			Point[] points = new Point[numPoints];
			for (int j = 0; j < numPoints; j++) {
				points[j] = new Point(rng.nextDouble(), rng.nextDouble());
			}

			QuadraticCurve c = new QuadraticCurve(points);
			for (double t1 = 0; t1 <= 1; t1 += step) {
				for (double t2 = 0; t2 <= 1; t2 += step) {
					QuadraticCurve cc = c.clip(t1, t2);
					assertEquals(c.get(t1), cc.get(0));
					assertEquals(c.get(t2), cc.get(1));
				}
			}
		}
	}

	@Test
	public void test_split_with_CubicCurve() {
		final int numPoints = 6;
		final double step = 0.123456789;

		Random rng = new Random(SEED);

		for (int i = 0; i < 1000; i++) {
			Point[] points = new Point[numPoints];
			for (int j = 0; j < numPoints; j++) {
				points[j] = new Point(rng.nextDouble(), rng.nextDouble());
			}

			CubicCurve c = new CubicCurve(points);
			for (double t = 0; t <= 1; t += step) {
				CubicCurve[] cs = c.split(t);
				assertEquals(c.get(t), cs[0].get(1));
				assertEquals(c.get(t), cs[1].get(0));
				assertEquals(c.get(0), cs[0].get(0));
				assertEquals(c.get(1), cs[1].get(1));
			}
		}
	}

	@Test
	public void test_clip_with_CubicCurve() {
		final int numPoints = 6;
		final double step = 0.123456789;

		Random rng = new Random(SEED);

		for (int i = 0; i < 1000; i++) {
			Point[] points = new Point[numPoints];
			for (int j = 0; j < numPoints; j++) {
				points[j] = new Point(rng.nextDouble(), rng.nextDouble());
			}

			CubicCurve c = new CubicCurve(points);
			for (double t1 = 0; t1 <= 1; t1 += step) {
				for (double t2 = 0; t2 <= 1; t2 += step) {
					CubicCurve cc = c.clip(t1, t2);
					assertEquals(c.get(t1), cc.get(0));
					assertEquals(c.get(t2), cc.get(1));
				}
			}
		}
	}

	@Test
	public void test_contains_with_QuadraticCurve() {
		final int numPoints = 4;
		final double step = 0.123456789;

		Random rng = new Random(SEED);

		for (int i = 0; i < 1000; i++) {
			Point[] points = new Point[numPoints];
			for (int j = 0; j < numPoints; j++) {
				points[j] = new Point(rng.nextDouble(), rng.nextDouble());
			}

			QuadraticCurve c = new QuadraticCurve(points);
			for (double t = 0; t <= 1; t += step) {
				assertTrue(c.contains(c.get(t)));
			}
		}
	}

	@Test
	public void test_contains_with_CubicCurve() {
		final int numPoints = 6;
		final double step = 0.123456789;

		Random rng = new Random(SEED);

		for (int i = 0; i < 1000; i++) {
			Point[] points = new Point[numPoints];
			for (int j = 0; j < numPoints; j++) {
				points[j] = new Point(rng.nextDouble(), rng.nextDouble());
			}

			CubicCurve c = new CubicCurve(points);
			for (double t = 0; t <= 1; t += step) {
				assertTrue(c.contains(c.get(t)));
			}
		}
	}

	@Test
	public void test_getControlBounds() {
		final int numPoints = 6;
		final double step = 0.123456789;

		Random rng = new Random(SEED);

		for (int i = 0; i < 1000; i++) {
			Point[] points = new Point[numPoints];
			for (int j = 0; j < numPoints; j++) {
				points[j] = new Point(rng.nextDouble(), rng.nextDouble());
			}

			CubicCurve c = new CubicCurve(points);
			Rectangle bounds = CurveUtils.getControlBounds(c);
			for (double t = 0; t <= 1; t += step) {
				assertTrue(bounds.contains(c.get(t)));
			}
			assertTrue(bounds.contains(c.get(1)));
		}
	}

	@Test
	public void test_getIntersections_BezierClipping_simple() {
		CubicCurve c1 = new CubicCurve(new double[] { 100, 200, 200, 100, 300,
				300, 400, 200 });
		CubicCurve c2 = new CubicCurve(new double[] { 250, 100, 350, 200, 150,
				300, 250, 400 });

		assertEquals(1, c1.getIntersections(c2).length);
	}

}
