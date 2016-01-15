/*******************************************************************************
 * Copyright (c) 2012, 2016 itemis AG and others.
 *
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.eclipse.gef4.geometry.internal.utils.PrecisionUtils;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.CubicCurve;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.QuadraticCurve;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.junit.Test;

public class BezierCurveTests {

	private void check_values_with_getters(BezierCurve c, Point... points) {
		Point p1 = points[0];
		assertEquals(p1, c.getP1());
		assertTrue(PrecisionUtils.equal(p1.x, c.getX1()));
		assertTrue(PrecisionUtils.equal(p1.y, c.getY1()));

		Point p2 = points[points.length - 1];
		assertEquals(p2, c.getP2());
		assertTrue(PrecisionUtils.equal(p2.x, c.getX2()));
		assertTrue(PrecisionUtils.equal(p2.y, c.getY2()));

		assertTrue(Arrays.equals(c.getPoints(), points));

		for (int i = 0; i < points.length; i++) {
			assertEquals(points[i], c.getPoint(i));
		}
	}

	@Test
	public void test_constructors() {
		BezierCurve c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		assertEquals(c0, new BezierCurve(new Point(1, 1), new Point(1, 10),
				new Point(10, 1), new Point(10, 10)));
		assertEquals(c0,
				new BezierCurve(new CubicCurve(1, 1, 1, 10, 10, 1, 10, 10)));
		BezierCurve c1 = new BezierCurve(1, 1, 10, 1, 10, 10);
		assertEquals(c1, new BezierCurve(new Point(1, 1), new Point(10, 1),
				new Point(10, 10)));
		assertEquals(c1,
				new BezierCurve(new QuadraticCurve(1, 1, 10, 1, 10, 10)));

		// getCopy()
		BezierCurve c0copy = c0.getCopy();
		assertEquals(c0, c0copy);
		assertNotSame(c0, c0copy);

		c0copy.setP1(new Point(100, 100));
		assertFalse(c0.equals(c0copy));
	}

	@Test
	public void test_contains_Point() {
		BezierCurve c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		assertFalse(c0.contains(new Point(0, 0)));
		assertFalse(c0.contains(new Point(3, 3)));
		assertFalse(c0.contains(new Point(3, 8)));
		assertFalse(c0.contains(new Point(7, 3)));
		assertFalse(c0.contains(new Point(7, 8)));
		assertFalse(c0.contains(new Point(11, 11)));
		assertTrue(c0.contains(new Point(1, 1)));
		assertTrue(c0.contains(new Point(10, 10)));

		// evaluate curve at some parameter values and check that the returned
		// points are contained by the curve
		for (double t = 0; t <= 1; t += 0.02) {
			assertTrue(c0.contains(c0.get(t)));
		}
	}

	@Test
	public void test_equals() {
		BezierCurve c = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		assertFalse(c.equals(null));
		assertFalse(c.equals(new Rectangle(1, 2, 3, 4)));
		assertEquals(c, c);

		BezierCurve cr = new BezierCurve(10, 10, 10, 1, 1, 10, 1, 1);
		assertEquals(cr, cr);
		assertEquals(c, cr);
		assertEquals(cr, c);

		BezierCurve ce = c.getElevated();
		BezierCurve cre = cr.getElevated();
		assertEquals(c, ce);
		assertEquals(ce, c);
		assertEquals(cr, cre);
		assertEquals(cre, cr);
		assertEquals(c, cre);
		assertEquals(cre, c);
		assertEquals(cr, ce);
		assertEquals(ce, cr);

		BezierCurve c2 = new BezierCurve(1, 2, 3, 4);
		assertFalse(c.equals(c2));
		assertFalse(c2.equals(c));
		c2 = new BezierCurve(1, 1, 1, 10, 2, 3);
		assertFalse(c.equals(c2));
		assertFalse(c2.equals(c));
		c2 = new BezierCurve(1, 1, 1, 10, 10, 1, 2, 3);
		assertFalse(c.equals(c2));
		assertFalse(c2.equals(c));
		c2 = new BezierCurve(1, 1, 2, 9, 9, 2, 10, 10);
		assertFalse(c.equals(c2));
		assertFalse(c2.equals(c));
	}

	@Test
	public void test_get() {
		BezierCurve c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		assertEquals(new Point(1, 1), c0.get(0));
		assertEquals(new Point(10, 10), c0.get(1));
		assertEquals(new Point(5.5, 5.5), c0.get(0.5));
	}

	@Test
	public void test_getBounds() {
		BezierCurve c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		assertEquals(new Rectangle(1, 1, 9, 9), c0.getBounds());
	}

	@Test
	public void test_getClipped() {
		BezierCurve c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		assertEquals(new BezierCurve(1, 1), c0.getClipped(0, 0));
		assertEquals(new BezierCurve(10, 10), c0.getClipped(1, 1));

		BezierCurve c1 = c0.getClipped(0, 0.5);
		BezierCurve c2 = c0.getClipped(0.5, 1);
		assertEquals(new Point(1, 1), c1.get(0));
		assertEquals(new Point(5.5, 5.5), c1.get(1));
		assertEquals(new Point(5.5, 5.5), c2.get(0));
		assertEquals(new Point(10, 10), c2.get(1));
	}

	@Test
	public void test_getControlBounds() {
		BezierCurve c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		assertEquals(new Rectangle(1, 1, 9, 9), c0.getControlBounds());

		BezierCurve c1 = new BezierCurve(1, 5, 5, 8, 10, 1);
		assertEquals(new Rectangle(1, 1, 9, 7), c1.getControlBounds());
	}

	@Test
	public void test_getDerivative() {
		BezierCurve c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		BezierCurve d0 = c0.getDerivative();
		assertEquals(3, d0.getPoints().length);
		// TODO: check the derivative for some points on the curve
	}

	@Test
	public void test_getIntersections_Rectangle() {
		Rectangle r = new Rectangle(new Point(100, 150), new Point(550, 300));
		Ellipse e = new Ellipse(126.0, 90.0, 378.0, 270.0);
		Point[] inters = e.getIntersections(r.getOutline());
		assertEquals(4, inters.length);
	}

	@Test
	public void test_getOverlap() {
		BezierCurve c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		BezierCurve c1 = c0.getClipped(0, 0.5);
		BezierCurve c2 = c0.getClipped(0.5, 1);

		BezierCurve o01 = c0.getOverlap(c1);
		assertTrue(o01 != null);
		assertTrue(c1.contains(o01));
		// assertEquals(c1, o01);

		/*
		 * TODO: The equality check may not return true for the computed overlap
		 * and the real overlap. This is because the overlap is only
		 * approximated.
		 */

		BezierCurve o02 = c0.getOverlap(c2);
		assertTrue(o02 != null);
		assertTrue(c2.contains(o02));
		// assertEquals(c2, o02);

		assertNull(c1.getOverlap(c2));
	}

	@Test
	public void test_getParameterAt() {
		BezierCurve c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		assertTrue(PrecisionUtils.equal(0, c0.getParameterAt(new Point(1, 1))));
		assertTrue(
				PrecisionUtils.equal(1, c0.getParameterAt(new Point(10, 10))));
		assertTrue(PrecisionUtils.equal(0.5,
				c0.getParameterAt(new Point(5.5, 5.5))));

		boolean thrown = false;
		try {
			c0.getParameterAt(null);
		} catch (IllegalArgumentException x) {
			thrown = true;
		}
		assertTrue(thrown);

		thrown = false;
		try {
			c0.getParameterAt(new Point(3, 3));
		} catch (IllegalArgumentException x) {
			thrown = true;
		}
		assertTrue(thrown);
	}

	@Test
	public void test_getTranslated() {
		BezierCurve c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		BezierCurve t0 = c0.getTranslated(new Point(-1, 4));
		assertEquals(new BezierCurve(0, 5, 0, 14, 9, 5, 9, 14), t0);
	}

	@Test
	public void test_intersection_line() {
		// test data taken from Bugzilla 485607

		Line line = new Line(new Point(14.5428, -10000.0),
				new Point(14.5428, 10000.0));
		BezierCurve curve = new BezierCurve(
				new Point(24.200000000000077, -2.3727936609778726),
				new Point(17.56250052131255, -2.372947931952322),
				new Point(13.137498435758783, -2.3730630604781013),
				new Point(6.499999999999994, -2.372678596158668));
		Point[] intersections = line.getIntersections(curve);
		assertEquals(1, intersections.length);
		assertTrue(PrecisionUtils.equal(14.5428, intersections[0].x, 0));
		assertTrue(PrecisionUtils.equal(-2.3729, intersections[0].y, -4));

		// second one
		line = new Line(new Point(14.5428, -1000.0),
				new Point(14.5428, 1000.0));
		intersections = line.getIntersections(curve);
		assertEquals(1, intersections.length);
		assertTrue(PrecisionUtils.equal(14.5428, intersections[0].x, 0));
		assertTrue(PrecisionUtils.equal(-2.3729, intersections[0].y, -4));
	}

	@Test
	public void test_intersection_performance() {
		// test performance of intersection computation
		// test data taken from Bugzilla #485776

		// fast cases
		Point referencePoint = new Point(15.0, 10000.0);
		Line line = new Line(new Point(15.084666766666667, -10000.0),
				new Point(15.084666766666667, 10000.0));
		BezierCurve curve = new BezierCurve(
				new Point(6.599999999999993, 2.3727936609778726),
				new Point(13.237499975331303, 2.37263938999188),
				new Point(17.662536995636934, 2.374123516379587),
				new Point(24.30000000000008, 2.37226488262784));
		long startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		long endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(15.0, 10000.0);
		line = new Line(new Point(15.254000199999998, -10000.0),
				new Point(15.254000199999998, 10000.0));
		curve = new BezierCurve(
				new Point(6.599999999999993, 2.3727936609778726),
				new Point(13.237499975331303, 2.37263938999188),
				new Point(17.662536995636934, 2.374123516379587),
				new Point(24.30000000000008, 2.37226488262784));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(26.5, -10000.0);
		line = new Line(new Point(26.0428, -10000.0),
				new Point(26.0428, 10000.0));
		curve = new BezierCurve(
				new Point(26.100000000000104, -2.2746851555884886),
				new Point(26.062501730606815, -2.277152214709952),
				new Point(26.0375016726132, -2.2787790557391063),
				new Point(26.000000000000103, -2.2811938345444247));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(26.5, -10000.0);
		line = new Line(new Point(26.3476, -10000.0),
				new Point(26.3476, 10000.0));
		curve = new BezierCurve(
				new Point(26.40000000000011, -2.2543064982196563),
				new Point(26.362501913756493, -2.256935096991749),
				new Point(26.337501851265586, -2.258668963768477),
				new Point(26.300000000000107, -2.2612432934299416));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(26.5, -10000.0);
		line = new Line(new Point(26.6524, -10000.0),
				new Point(26.6524, 10000.0));
		curve = new BezierCurve(
				new Point(26.700000000000113, -2.2326108778904623),
				new Point(26.66250211105171, -2.235407157058545),
				new Point(26.63750204398072, -2.2372521371959477),
				new Point(26.60000000000011, -2.2399921315224565));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(26.5, -10000.0);
		line = new Line(new Point(26.9572, -10000.0),
				new Point(26.9572, 10000.0));
		curve = new BezierCurve(
				new Point(27.000000000000117, -2.2095485650966684),
				new Point(26.96250232596202, -2.212518843259614),
				new Point(26.937502252321718, -2.214479096795463),
				new Point(26.900000000000116, -2.217391003791821));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(26.5, 10000.0);
		line = new Line(new Point(26.0428, -10000.0),
				new Point(26.0428, 10000.0));
		curve = new BezierCurve(
				new Point(26.000000000000103, 2.2811938345444247),
				new Point(26.037501672613196, 2.2787790557391063),
				new Point(26.062501730606815, 2.2771522147099525),
				new Point(26.100000000000104, 2.2746851555884886));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(26.5, 10000.0);
		line = new Line(new Point(26.3476, -10000.0),
				new Point(26.3476, 10000.0));
		curve = new BezierCurve(
				new Point(26.300000000000107, 2.2612432934299416),
				new Point(26.337501851265582, 2.258668963768477),
				new Point(26.362501913756493, 2.256935096991749),
				new Point(26.40000000000011, 2.2543064982196563));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(26.5, 10000.0);
		line = new Line(new Point(26.6524, -10000.0),
				new Point(26.6524, 10000.0));
		curve = new BezierCurve(
				new Point(26.60000000000011, 2.2399921315224565),
				new Point(26.63750204398072, 2.2372521371959477),
				new Point(26.66250211105171, 2.235407157058545),
				new Point(26.700000000000113, 2.2326108778904623));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(26.5, 10000.0);
		line = new Line(new Point(26.9572, -10000.0),
				new Point(26.9572, 10000.0));
		curve = new BezierCurve(
				new Point(26.900000000000116, 2.217391003791821),
				new Point(26.93750225232172, 2.214479096795463),
				new Point(26.96250232596202, 2.212518843259614),
				new Point(27.000000000000117, 2.2095485650966684));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		// slow cases
		referencePoint = new Point(15.0, -10000.0);
		line = new Line(new Point(15.152400199999999, -10000.0),
				new Point(15.152400199999999, 10000.0));
		curve = new BezierCurve(
				new Point(24.200000000000077, -2.3727936609778726),
				new Point(17.56250052131255, -2.372947931952322),
				new Point(13.137498435758783, -2.3730630604781013),
				new Point(6.499999999999994, -2.372678596158668));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(12.999999999999984, 10000.0);
		line = new Line(new Point(12.544999999999984, -10000.0),
				new Point(12.544999999999984, 10000.0));
		curve = new BezierCurve(
				new Point(6.599999999999993, 2.3727936609778726),
				new Point(13.237499975331303, 2.37263938999188),
				new Point(17.662536995636934, 2.374123516379587),
				new Point(24.30000000000008, 2.37226488262784));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(15.0, 10000.0);
		line = new Line(new Point(14.915333333333333, -10000.0),
				new Point(14.915333333333333, 10000.0));
		curve = new BezierCurve(
				new Point(6.599999999999993, 2.3727936609778726),
				new Point(13.237499975331303, 2.37263938999188),
				new Point(17.662536995636934, 2.374123516379587),
				new Point(24.30000000000008, 2.37226488262784));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(15.0, 10000.0);
		line = new Line(new Point(15.254000199999998, -10000.0),
				new Point(15.254000199999998, 10000.0));
		curve = new BezierCurve(
				new Point(6.599999999999993, 2.3727936609778726),
				new Point(13.237499975331303, 2.37263938999188),
				new Point(17.662536995636934, 2.374123516379587),
				new Point(24.30000000000008, 2.37226488262784));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(9.2, -10000.0);
		line = new Line(new Point(9.655000199999996, -10000.0),
				new Point(9.655000199999996, 10000.0));
		curve = new BezierCurve(
				new Point(24.200000000000077, -2.3727936609778726),
				new Point(17.56250052131255, -2.372947931952322),
				new Point(13.137498435758783, -2.3730630604781013),
				new Point(6.499999999999994, -2.372678596158668));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(15.0, -10000.0);
		line = new Line(new Point(14.542800199999999, -10000.0),
				new Point(14.542800199999999, 10000.0));
		curve = new BezierCurve(
				new Point(24.200000000000077, -2.3727936609778726),
				new Point(17.56250052131255, -2.372947931952322),
				new Point(13.137498435758783, -2.3730630604781013),
				new Point(6.499999999999994, -2.372678596158668));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(9.2, 10000.0);
		line = new Line(new Point(8.745, -10000.0), new Point(8.745, 10000.0));
		curve = new BezierCurve(
				new Point(6.599999999999993, 2.3727936609778726),
				new Point(13.237499975331303, 2.37263938999188),
				new Point(17.662536995636934, 2.374123516379587),
				new Point(24.30000000000008, 2.37226488262784));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(15.0, 10000.0);
		line = new Line(new Point(15.084666766666667, -10000.0),
				new Point(15.084666766666667, 10000.0));
		curve = new BezierCurve(
				new Point(6.599999999999993, 2.3727936609778726),
				new Point(13.237499975331303, 2.37263938999188),
				new Point(17.662536995636934, 2.374123516379587),
				new Point(24.30000000000008, 2.37226488262784));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(26.5, 10000.0);
		line = new Line(new Point(26.3476, -10000.0),
				new Point(26.3476, 10000.0));
		curve = new BezierCurve(
				new Point(26.300000000000107, 2.2612432934299416),
				new Point(26.337501851265582, 2.258668963768477),
				new Point(26.362501913756493, 2.256935096991749),
				new Point(26.40000000000011, 2.2543064982196563));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);

		referencePoint = new Point(15.0, 10000.0);
		line = new Line(new Point(14.915333333333333, -10000.0),
				new Point(14.915333333333333, 10000.0));
		curve = new BezierCurve(
				new Point(6.599999999999993, 2.3727936609778726),
				new Point(13.237499975331303, 2.37263938999188),
				new Point(17.662536995636934, 2.374123516379587),
				new Point(24.30000000000008, 2.37226488262784));
		startMillis = System.currentTimeMillis();
		line.getNearestIntersection(curve, referencePoint);
		endMillis = System.currentTimeMillis();
		assertTrue(endMillis - startMillis < 200);
	}

	@Test
	public void test_overlaps() {
		BezierCurve c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		BezierCurve c1 = c0.getClipped(0, 0.5);
		BezierCurve c2 = c0.getClipped(0.5, 1);
		assertTrue(c0.overlaps(c1));
		assertTrue(c1.overlaps(c0));
		assertTrue(c0.overlaps(c2));
		assertTrue(c2.overlaps(c0));
		assertFalse(c1.overlaps(c2));
		assertFalse(c2.overlaps(c1));
	}

	@Test
	public void test_point_getters() {
		BezierCurve c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		check_values_with_getters(c0, new Point[] { new Point(1, 1),
				new Point(1, 10), new Point(10, 1), new Point(10, 10) });
	}

	@Test
	public void test_point_setters() {
		BezierCurve c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		check_values_with_getters(c0, new Point[] { new Point(1, 1),
				new Point(1, 10), new Point(10, 1), new Point(10, 10) });

		c0.setP1(new Point(-30, 5));
		check_values_with_getters(c0, new Point[] { new Point(-30, 5),
				new Point(1, 10), new Point(10, 1), new Point(10, 10) });

		c0.setP2(new Point(31, 11));
		check_values_with_getters(c0, new Point[] { new Point(-30, 5),
				new Point(1, 10), new Point(10, 1), new Point(31, 11) });

		c0.setPoint(1, new Point(3, -3));
		check_values_with_getters(c0, new Point[] { new Point(-30, 5),
				new Point(3, -3), new Point(10, 1), new Point(31, 11) });

		c0.setPoint(2, new Point(-3, 3));
		check_values_with_getters(c0, new Point[] { new Point(-30, 5),
				new Point(3, -3), new Point(-3, 3), new Point(31, 11) });
	}

	@Test
	public void test_split() {
		BezierCurve c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		BezierCurve c1 = c0.getClipped(0, 0.5);
		BezierCurve c2 = c0.getClipped(0.5, 1);
		BezierCurve[] split = c0.split(0.5);
		assertEquals(c1, split[0]);
		assertEquals(c2, split[1]);
	}

	@Test
	public void test_toBezier() {
		BezierCurve c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		BezierCurve[] beziers = c0.toBezier();
		assertEquals(1, beziers.length);
		assertEquals(c0, beziers[0]);
	}

	@Test
	public void test_toCubic() {
		BezierCurve c0 = new BezierCurve(1, 1);
		assertNull(c0.toCubic());
		c0 = new BezierCurve(1, 1, 1, 10);
		assertNull(c0.toCubic());
		c0 = new BezierCurve(1, 1, 1, 10, 10, 1);
		assertNull(c0.toCubic());
		c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		assertEquals(new CubicCurve(1, 1, 1, 10, 10, 1, 10, 10), c0.toCubic());
		c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 67, 89, 10, 10);
		assertEquals(new CubicCurve(1, 1, 1, 10, 10, 1, 10, 10), c0.toCubic());
		c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10, 98, 76);
		assertEquals(new CubicCurve(1, 1, 1, 10, 10, 1, 98, 76), c0.toCubic());
	}

	@Test
	public void test_toLine() {
		BezierCurve c0 = new BezierCurve(1, 1);
		assertNull(c0.toCubic());
		c0 = new BezierCurve(1, 1, 1, 10);
		assertEquals(new Line(1, 1, 1, 10), c0.toLine());
		c0 = new BezierCurve(1, 1, 1, 10, 10, 1);
		assertEquals(new Line(1, 1, 10, 1), c0.toLine());
		c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10);
		assertEquals(new Line(1, 1, 10, 10), c0.toLine());
		c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 10, 10, 98, 76);
		assertEquals(new Line(1, 1, 98, 76), c0.toLine());
	}

	@Test
	public void test_toLineStrip() {
		BezierCurve linear = new BezierCurve(0, 0, 1, 1);
		Line[] lines = linear.toLineStrip(1);
		assertEquals(1, lines.length);
		assertEquals(new Line(0, 0, 1, 1), lines[0]);
		assertEquals(linear.toLine(), lines[0]);

		// TODO: check complicated curves, too
	}

	@Test
	public void test_toQuadratic() {
		BezierCurve c0 = new BezierCurve(1, 1);
		assertNull(c0.toQuadratic());
		c0 = new BezierCurve(1, 1, 1, 10);
		assertNull(c0.toQuadratic());
		c0 = new BezierCurve(1, 1, 1, 10, 10, 1);
		assertEquals(new QuadraticCurve(1, 1, 1, 10, 10, 1), c0.toQuadratic());
		c0 = new BezierCurve(1, 1, 1, 10, 67, 89, 10, 1);
		assertEquals(new QuadraticCurve(1, 1, 1, 10, 10, 1), c0.toQuadratic());
		c0 = new BezierCurve(1, 1, 1, 10, 10, 1, 98, 76);
		assertEquals(new QuadraticCurve(1, 1, 1, 10, 98, 76), c0.toQuadratic());
	}

	@Test
	public void test_toString() {
		BezierCurve c = new BezierCurve(10, 10, 5, 5, 20, 5, 15, 10);
		assertEquals(
				"BezierCurve(Vector3D(10.0, 10.0, 1.0), Vector3D(5.0, 5.0, 1.0), Vector3D(20.0, 5.0, 1.0), Vector3D(15.0, 10.0, 1.0))",
				c.toString());
	}

}
