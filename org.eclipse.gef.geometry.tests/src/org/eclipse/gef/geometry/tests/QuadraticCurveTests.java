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

import java.util.Random;

import org.eclipse.gef.geometry.planar.CubicCurve;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.QuadraticCurve;
import org.junit.Test;

public class QuadraticCurveTests {
	private static final int SEED = 123;
	private final Point p = new Point(-10, -10), c = new Point(10, 0),
			q = new Point(0, 10);

	@Test
	public void test_contains_Point() {
		QuadraticCurve curve = new QuadraticCurve(p, c, q);

		// check fix points:
		assertEquals(curve.contains(p), true);
		assertEquals(curve.contains(q), true);
		assertEquals(curve.contains(c), false); // not always true, but for our
												// c it is

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
		QuadraticCurve curve = new QuadraticCurve(p, c, q);

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
	public void test_get_Bounds() {
		QuadraticCurve curve = new QuadraticCurve(p, c, q);

		// p is the top-left point: (y-coordinates are inverted)
		assertEquals(curve.getBounds().getTopLeft(), p);
	}

	@Test
	public void test_getElevated() {
		Random rng = new Random(SEED);

		for (int i = 0; i < 100; i++) {
			Point[] points = new Point[3];
			for (int j = 0; j < 3; j++) {
				points[j] = new Point(rng.nextDouble(), rng.nextDouble());
			}
			QuadraticCurve qc = new QuadraticCurve(points);
			CubicCurve cc = qc.getElevated();

			for (double t = 0; t <= 1; t += 0.0123456789) {
				assertTrue(cc.contains(qc.get(t)));
				assertTrue(qc.contains(cc.get(t)));
			}
		}
	}

	@Test
	public void test_getIntersections_QuadraticCurve() {
		// some general cases
		Point p1 = new Point(164.0, 43.0);
		Point p2 = new Point(169.0, 165.0);
		Point p3 = new Point(307.0, 239.0);
		Point q1 = new Point(100.0, 100.0);
		Point q2 = new Point(200.0, 200.0);
		Point q3 = new Point(300.0, 100.0);

		QuadraticCurve p = new QuadraticCurve(p1, p2, p3);
		QuadraticCurve q = new QuadraticCurve(q1, q2, q3);

		Point[] intersections = q.getIntersections(p);
		assertEquals("p and q have exactly one intersection", 1,
				intersections.length);

		for (Point poi : intersections) {
			assertEquals("each point of intersection lies on p", true,
					p.contains(poi));
			assertEquals("each point of intersection lies on q", true,
					q.contains(poi));
		}

		p1 = new Point(200.0, 100.0);
		p2 = new Point(304.0, 203.0);
		p3 = new Point(300.0, 300.0);

		p = new QuadraticCurve(p1, p2, p3);

		intersections = q.getIntersections(p);
		assertEquals("p and q have exactly one intersection", 1,
				intersections.length);

		for (Point poi : intersections) {
			assertEquals("each point of intersection lies on p", true,
					p.contains(poi));
			assertEquals("each point of intersection lies on q", true,
					q.contains(poi));
		}

		p1 = new Point(144.0, 59.0);
		p2 = new Point(358.0, 130.0);
		p3 = new Point(300.0, 300.0);

		p = new QuadraticCurve(p1, p2, p3);

		intersections = q.getIntersections(p);
		assertEquals("p and q have exactly one intersection", 1,
				intersections.length);

		for (Point poi : intersections) {
			assertEquals("each point of intersection lies on p", true,
					p.contains(poi));
			assertEquals("each point of intersection lies on q", true,
					q.contains(poi));
		}

		p1 = new Point(151.0, 272.0);
		p2 = new Point(101.0, 187.0);
		p3 = new Point(205.0, 48.0);

		p = new QuadraticCurve(p1, p2, p3);

		intersections = q.getIntersections(p);
		assertEquals("p and q have exactly one intersection", 1,
				intersections.length);

		for (Point poi : intersections) {
			assertEquals("each point of intersection lies on p", true,
					p.contains(poi));
			assertEquals("each point of intersection lies on q", true,
					q.contains(poi));
		}

		p1 = new Point(184.0, 83.0);
		p2 = new Point(400.0, 200.0);
		p3 = new Point(300.0, 300.0);

		p = new QuadraticCurve(p1, p2, p3);

		intersections = p.getIntersections(q);
		assertEquals("p and q have exactly one intersection", 1,
				intersections.length);

		for (Point poi : intersections) {
			assertEquals("each point of intersection lies on p", true,
					p.contains(poi));
			assertEquals("each point of intersection lies on q", true,
					q.contains(poi));
		}

		p1 = new Point(196.0, 89.0);
		p2 = new Point(335.0, 215.0);
		p3 = new Point(300.0, 300.0);

		p = new QuadraticCurve(p1, p2, p3);

		intersections = q.getIntersections(p);
		assertEquals("p and q have exactly one intersection", 1,
				intersections.length);

		for (Point poi : intersections) {
			assertEquals("each point of intersection lies on p", true,
					p.contains(poi));
			assertEquals("each point of intersection lies on q", true,
					q.contains(poi));
		}

		// special tangential cases
		// TODO

		// special
	}

	@Test
	public void test_getters_and_setters() {
		QuadraticCurve curve = new QuadraticCurve(p, c, q);
		assertEquals(curve.getP1(), p);
		assertEquals(curve.getP2(), q);
		assertEquals(curve.getCtrl(), c);
		Point newP = new Point(-5, -5);
		Point newC = new Point(5, -5);
		Point newQ = new Point(-5, 5);
		curve.setP1(newP);
		curve.setP2(newQ);
		curve.setCtrl(newC);
		assertEquals(curve.getP1(), newP);
		assertEquals(curve.getP2(), newQ);
		assertEquals(curve.getCtrl(), newC);
	}

	public void test_intersects_Line() {
		// TODO
	}

	public void test_intersects_Rectangle() {
		// TODO
	}

}
