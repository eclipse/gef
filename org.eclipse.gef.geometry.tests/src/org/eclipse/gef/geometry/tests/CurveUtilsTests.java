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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.euclidean.Straight;
import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;
import org.eclipse.gef.geometry.planar.BezierCurve;
import org.eclipse.gef.geometry.planar.CubicCurve;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Path;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polyline;
import org.eclipse.gef.geometry.planar.QuadraticCurve;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.geometry.planar.Path.Segment;
import org.junit.Test;

public class CurveUtilsTests {

	private static final long SEED = 123;

	@Test
	public void test_check_contains_difficult_cases() {
		BezierCurve c1 = new BezierCurve(
				new Point(0.5402658595791646, 0.9741509829024984),
				new Point(0.16279154085195757, 0.6904753002704389),
				new Point(0.5362586913177897, 0.03544287335013263),
				new Point(0.34435494116180165, 0.31041629374338775),
				new Point(0.3850664934271003, 0.5238288178983336),
				new Point(0.13829366099352602, 0.7410634269933081),
				new Point(0.8948987750498976, 0.6198888125981984),
				new Point(0.11349279987471517, 0.3388985501965609));

		BezierCurve c2 = new BezierCurve(
				new Point(0.3494484760769454, 0.47795072857018706),
				new Point(0.9841912220562209, 0.10765341979304721),
				new Point(0.27977429726230696, 0.7303050467844633),
				new Point(0.28022390386455787, 0.3313265057575542),
				new Point(0.3914004373221056, 0.6451799723354514),
				new Point(0.2875477493472879, 0.5132577259019093),
				new Point(0.13579952633028602, 0.5665583087171101),
				new Point(0.09831516780965299, 0.25959706254491044));

		Point p = new Point(0.3736012772933578, 0.4639965007739409);

		assertTrue(c1.contains(p));
		assertTrue(c2.contains(p));
	}

	@Test
	public void test_clip_with_CubicCurve() {
		final int numPoints = 4;
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
					CubicCurve cc = c.getClipped(t1, t2).toCubic();
					if (cc != null) {
						assertEquals(c.get(t1), cc.get(0));
						assertEquals(c.get(t2), cc.get(1));
					}
				}
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
				assertTrue(!Double.isNaN(points[j].x));
				assertTrue(!Double.isNaN(points[j].y));
			}

			BezierCurve c = new BezierCurve(points);
			for (double t = 0; t <= 1; t += step) {
				assertTrue(c.contains(c.get(t)));
			}
		}

		BezierCurve c = new BezierCurve(new Point(0.0, 0.0),
				new Point(0.05, 0.1), new Point(0.05, 0.1),
				new Point(0.1, -0.1));

		assertTrue(!c.contains(new Point(0.1, 0.1)));
	}

	@Test
	public void test_contains_with_QuadraticCurve() {
		final int numPoints = 3;
		final double step = 0.123456789;

		Random rng = new Random(SEED);

		for (int i = 0; i < 1000; i++) {
			Point[] points = new Point[numPoints];
			for (int j = 0; j < numPoints; j++) {
				points[j] = new Point(rng.nextDouble(), rng.nextDouble());
			}

			QuadraticCurve c = new QuadraticCurve(points);
			for (double t = 0; t <= 1; t += step) {
				assertTrue("t = " + t, c.contains(c.get(t)));
			}
		}
	}

	@Test
	public void test_getBounds() {
		Random rng = new Random(SEED);

		for (int i = 0; i < 1000; i++) {
			double w = Math.abs(rng.nextDouble()),
					h = Math.abs(rng.nextDouble());
			Rectangle controlBounds = new Rectangle(rng.nextDouble(),
					rng.nextDouble(), w, h);

			for (int n = 2; n < 10; n++) {
				Point[] points = new Point[n];

				points[0] = controlBounds.getBottomLeft();
				points[n - 1] = controlBounds.getBottomRight();

				if (n == 3) {
					points[1] = points[0].getTranslated(points[2])
							.getScaled(0.5);
				}
				if (n > 3) {
					double span = controlBounds.getWidth() / (n - 3);
					double d = 0;
					for (int j = 1; j < n - 1; j++, d += span) {
						points[j] = new Point(controlBounds.getX() + d,
								controlBounds.getY());
					}
				}

				BezierCurve c = new BezierCurve(points);

				// y maximum
				double d = controlBounds.getY() + controlBounds.getHeight()
						- c.get(0.5).y;

				Rectangle bounds = new Rectangle(controlBounds.getX(),
						controlBounds.getY() + controlBounds.getHeight() - d,
						controlBounds.getWidth(), d);

				assertEquals(bounds, c.getBounds());

				// x maximum
				controlBounds = controlBounds
						.getRotatedCCW(Angle.fromDeg(90), new Point())
						.getBounds();
				c.rotateCCW(Angle.fromDeg(90), new Point());

				d = controlBounds.getX() + controlBounds.getWidth()
						- c.get(0.5).x;

				bounds = new Rectangle(
						controlBounds.getX() + controlBounds.getWidth() - d,
						controlBounds.getY(), d, controlBounds.getHeight());

				if (!bounds.equals(c.getBounds())) {
					System.out.println("DEBUG");
				}

				assertEquals(bounds, c.getBounds());
			}
		}
	}

	@Test
	public void test_getClipped_with_QuadraticCurve() {
		final int numPoints = 3;
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
					QuadraticCurve cc = c.getClipped(t1, t2).toQuadratic();
					if (cc != null) {
						assertEquals(c.get(t1), cc.get(0));
						assertEquals(c.get(t2), cc.get(1));
					}
				}
			}
		}
	}

	@Test
	public void test_getControlBounds() {
		final int numPoints = 4;
		final double step = 0.123456789;

		Random rng = new Random(SEED);

		for (int i = 0; i < 1000; i++) {
			Point[] points = new Point[numPoints];
			for (int j = 0; j < numPoints; j++) {
				points[j] = new Point(rng.nextDouble(), rng.nextDouble());
			}

			CubicCurve c = new CubicCurve(points);
			Rectangle bounds = c.getControlBounds();
			for (double t = 0; t <= 1; t += step) {
				assertTrue(bounds.contains(c.get(t)));
			}
			assertTrue(bounds.contains(c.get(1)));
		}
	}

	@Test
	public void test_getIntersections_linear() {
		BezierCurve yAxis = new BezierCurve(new Point(0, 0), new Point(1, 0));

		BezierCurve curve = new BezierCurve(new Point(0, -20),
				new Point(0.3333333333333333, -10.0),
				new Point(0.6666666666666666, 0.0), new Point(1, -10));

		assertEquals(0, yAxis.getIntersections(curve).length);
	}

	@Test
	public void test_getIntersections_overlapping() {
		/*
		 * The algorithm is very slow in the case of an overlap. The execution
		 * speed is depending on the length of the curve. That's why we keep the
		 * coordinate values small.
		 * 
		 * TODO: Make the algorithm return quickly for overlapping curves.
		 */
		CubicCurve baseCurve = new CubicCurve(new Point(), new Point(.1, 0),
				new Point(0, .1), new Point(.1, .1));

		CubicCurve c1 = baseCurve.getClipped(0, 0.75).toCubic();
		CubicCurve c2 = baseCurve.getClipped(0.25, 1).toCubic();

		assertEquals(0, c1.getIntersections(c2).length);
		assertEquals(0, c2.getIntersections(c1).length);

		// c1 contains c2, c2 is contained by c1
		c2 = c1.getClipped(0.25, 0.75).toCubic();
		assertEquals(0, c1.getIntersections(c2).length);
		assertEquals(0, c2.getIntersections(c1).length);

		// single end-point-intersection
		c2 = baseCurve.getClipped(0.75, 1).toCubic();
		assertEquals(1, c1.getIntersections(c2).length);
		assertEquals(1, c2.getIntersections(c1).length);
	}

	@Test
	public void test_getIntersections_random_containment() {
		final int numPoints = 8;

		Random rng = new Random(SEED);

		for (int i = 0; i < 1000; i++) {
			Point[] points = new Point[numPoints];
			for (int j = 0; j < numPoints; j++) {
				points[j] = new Point(rng.nextDouble(), rng.nextDouble());
				assertTrue(!Double.isNaN(points[j].x));
				assertTrue(!Double.isNaN(points[j].y));
			}

			BezierCurve c1 = new BezierCurve(points);

			for (int j = 0; j < numPoints; j++) {
				points[j] = new Point(rng.nextDouble(), rng.nextDouble());
				assertTrue(!Double.isNaN(points[j].x));
				assertTrue(!Double.isNaN(points[j].y));
			}

			BezierCurve c2 = new BezierCurve(points);

			for (Point poi : c1.getIntersections(c2)) {
				// if (!c1.contains(poi) || !c2.contains(poi)) {
				// System.out.println("DEBUG");
				// }
				// TODO: every failing test has to be sourced out into the
				// test_check_contains_difficult_cases() method.
				assertTrue(c1.contains(poi));
				assertTrue(c2.contains(poi));
			}
		}
	}

	@Test
	public void test_getIntersections_simple() {
		CubicCurve c1 = new CubicCurve(
				new double[] { 100, 200, 200, 100, 300, 300, 400, 200 });
		CubicCurve c2 = new CubicCurve(
				new double[] { 250, 100, 350, 200, 150, 300, 250, 400 });

		assertEquals(1, c1.getIntersections(c2).length);

		c1 = new CubicCurve(new Point(201.89274447949526, 106.43015521064301),
				new Point(213.0, 325.0), new Point(387.0, 119.0),
				new Point(118.0, 310.0));
		Line l = new Line(new Point(105.66666666666667, 75.16666666666667),
				new Point(528.3333333333334, 375.8333333333333));

		assertEquals(1, c1.getIntersections(l).length);

		QuadraticCurve q1 = new QuadraticCurve(new Point(200, 50),
				new Point(210, 100), new Point(190, 150));
		l = new Line(new Point(100, 100), new Point(300, 100));

		assertEquals(1, q1.getIntersections(l).length);

		q1 = new QuadraticCurve(new Point(250, 50), new Point(200, 100),
				new Point(200, 150));
		l = new Line(new Point(528, 75), new Point(105, 75));

		assertEquals(1, q1.getIntersections(l).length);

		q1 = new QuadraticCurve(new Point(500, 50), new Point(300, 150),
				new Point(100, 300));

		assertEquals(1, q1.getIntersections(l).length);

		q1 = new QuadraticCurve(new Point(171, 409), new Point(302, 106),
				new Point(345, 310));
		l = new Line(new Point(105.66666666666667, 375.8333333333333),
				new Point(528.3333333333334, 375.8333333333333));

		Point[] testInters = q1.getIntersections(l);
		assertEquals(1, testInters.length);
	}

	@Test
	public void test_getSignedDistance() {
		// check both, direction and value:
		// first quadrant (y-axis is inverted)
		double len = 10d / Math.sqrt(2);
		assertTrue(PrecisionUtils.equal(Straight.getSignedDistanceCCW(
				new Point(), new Point(10, -10), new Point(0, -10)), len));
		assertTrue(PrecisionUtils.equal(Straight.getSignedDistanceCCW(
				new Point(10, -10), new Point(), new Point(0, -10)), -len));
		assertTrue(PrecisionUtils.equal(Straight.getSignedDistanceCCW(
				new Point(), new Point(10, -10), new Point(1, -1)), 0));

		// second quadrant (y-axis is inverted)
		assertTrue(PrecisionUtils.equal(Straight.getSignedDistanceCCW(
				new Point(), new Point(-10, -10), new Point(0, -10)), -len));
		assertTrue(PrecisionUtils.equal(Straight.getSignedDistanceCCW(
				new Point(-10, -10), new Point(), new Point(0, -10)), len));
		assertTrue(PrecisionUtils.equal(Straight.getSignedDistanceCCW(
				new Point(), new Point(-10, -10), new Point(-1, -1)), 0));

		// third quadrant (y-axis is inverted)
		assertTrue(PrecisionUtils.equal(Straight.getSignedDistanceCCW(
				new Point(), new Point(10, 10), new Point(0, 10)), -len));
		assertTrue(PrecisionUtils.equal(Straight.getSignedDistanceCCW(
				new Point(10, 10), new Point(), new Point(0, 10)), len));
		assertTrue(PrecisionUtils.equal(Straight.getSignedDistanceCCW(
				new Point(), new Point(10, 10), new Point(1, 1)), 0));

		// forth quadrant (y-axis is inverted)
		assertTrue(PrecisionUtils.equal(Straight.getSignedDistanceCCW(
				new Point(), new Point(-10, 10), new Point(0, 10)), len));
		assertTrue(PrecisionUtils.equal(Straight.getSignedDistanceCCW(
				new Point(-10, 10), new Point(), new Point(0, 10)), -len));
		assertTrue(PrecisionUtils.equal(Straight.getSignedDistanceCCW(
				new Point(), new Point(-10, 10), new Point(-1, 1)), 0));
	}

	@Test
	public void test_getSignedDistance_abs() {
		// do only check for the absolute value of the signed distance

		assertTrue(PrecisionUtils
				.equal(Math.abs(Straight.getSignedDistanceCCW(new Point(0, -5),
						new Point(0, 5), new Point(5, 0))), 5));
		assertTrue(PrecisionUtils
				.equal(Math.abs(Straight.getSignedDistanceCCW(new Point(0, 5),
						new Point(0, -5), new Point(5, 0))), 5));

		assertTrue(PrecisionUtils
				.equal(Math.abs(Straight.getSignedDistanceCCW(new Point(0, -1),
						new Point(0, 1), new Point(5, 0))), 5));
		assertTrue(PrecisionUtils
				.equal(Math.abs(Straight.getSignedDistanceCCW(new Point(0, 1),
						new Point(0, -1), new Point(5, 0))), 5));

		assertTrue(PrecisionUtils
				.equal(Math.abs(Straight.getSignedDistanceCCW(new Point(-5, 0),
						new Point(5, 0), new Point(0, 5))), 5));
		assertTrue(PrecisionUtils
				.equal(Math.abs(Straight.getSignedDistanceCCW(new Point(-5, 0),
						new Point(5, 0), new Point(0, 5))), 5));

		assertTrue(PrecisionUtils
				.equal(Math.abs(Straight.getSignedDistanceCCW(new Point(-1, 0),
						new Point(1, 0), new Point(0, 5))), 5));
		assertTrue(PrecisionUtils
				.equal(Math.abs(Straight.getSignedDistanceCCW(new Point(-1, 0),
						new Point(1, 0), new Point(0, 5))), 5));
	}

	@Test
	public void test_getSignedDistance_direction() {
		// sign-checks:
		// first quadrant (y-axis is inverted)
		assertTrue(Straight.getSignedDistanceCCW(new Point(),
				new Point(10, -10), new Point(0, -10)) > 0);
		assertTrue(Straight.getSignedDistanceCCW(new Point(10, -10),
				new Point(), new Point(0, -10)) < 0);
		assertTrue(Straight.getSignedDistanceCCW(new Point(),
				new Point(10, -10), new Point(1, -1)) == 0);

		// second quadrant (y-axis is inverted)
		assertTrue(Straight.getSignedDistanceCCW(new Point(),
				new Point(-10, -10), new Point(0, -10)) < 0);
		assertTrue(Straight.getSignedDistanceCCW(new Point(-10, -10),
				new Point(), new Point(0, -10)) > 0);
		assertTrue(Straight.getSignedDistanceCCW(new Point(),
				new Point(-10, -10), new Point(-1, -1)) == 0);

		// third quadrant (y-axis is inverted)
		assertTrue(Straight.getSignedDistanceCCW(new Point(), new Point(10, 10),
				new Point(0, 10)) < 0);
		assertTrue(Straight.getSignedDistanceCCW(new Point(10, 10), new Point(),
				new Point(0, 10)) > 0);
		assertTrue(Straight.getSignedDistanceCCW(new Point(), new Point(10, 10),
				new Point(1, 1)) == 0);

		// forth quadrant (y-axis is inverted)
		assertTrue(Straight.getSignedDistanceCCW(new Point(),
				new Point(-10, 10), new Point(0, 10)) > 0);
		assertTrue(Straight.getSignedDistanceCCW(new Point(-10, 10),
				new Point(), new Point(0, 10)) < 0);
		assertTrue(Straight.getSignedDistanceCCW(new Point(),
				new Point(-10, 10), new Point(-1, 1)) == 0);
	}

	@Test
	public void test_split_with_CubicCurve() {
		final int numPoints = 4;
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
	public void test_split_with_QuadraticCurve() {
		final int numPoints = 3;
		final double step = 0.123456789;

		Random rng = new Random(SEED);

		for (int i = 0; i < 1000; i++) {
			Point[] points = new Point[numPoints];
			for (int j = 0; j < numPoints; j++) {
				points[j] = new Point(rng.nextDouble(), rng.nextDouble());
			}

			BezierCurve c = new BezierCurve(points);
			for (double t = 0; t <= 1; t += step) {
				BezierCurve[] cs = c.split(t);
				assertEquals(c.get(t), cs[0].get(1));
				assertEquals(c.get(t), cs[1].get(0));
				assertEquals(c.get(0), cs[0].get(0));
				assertEquals(c.get(1), cs[1].get(1));
			}
		}
	}

	@Test
	public void test_toPath() {
		Point start = new Point(20, 30);
		Point mid = new Point(20, 70);
		Point end = new Point(20, 90);
		Path path = new Polyline(start, mid, end).toPolyBezier().toPath();
		Segment[] segments = path.getSegments();
		assertNotNull(segments);
		assertEquals(3, segments.length);
		assertEquals(Segment.MOVE_TO, segments[0].getType());
		assertEquals(Segment.LINE_TO, segments[1].getType());
		assertEquals(Segment.LINE_TO, segments[2].getType());
		assertEquals(start, segments[0].getPoints()[0]);
		assertEquals(mid, segments[1].getPoints()[0]);
		assertEquals(end, segments[2].getPoints()[0]);
	}

}
