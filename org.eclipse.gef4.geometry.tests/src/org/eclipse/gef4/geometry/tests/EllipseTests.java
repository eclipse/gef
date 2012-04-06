/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.tests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.junit.Test;


/**
 * Unit tests for {@link Ellipse}.
 * 
 * @author anyssen
 * 
 */
public class EllipseTests {

	private static final double PRECISION_FRACTION = TestUtils
			.getPrecisionFraction();

	@Test
	public void test_contains_with_Point() {
		Rectangle r = new Rectangle(34.3435, 56.458945, 123.3098, 146.578);
		Ellipse e = new Ellipse(r);

		assertTrue(e.contains(r.getCentroid()));

		assertTrue(e.contains(r.getLeft()));
		assertTrue(e.contains(r.getLeft().getTranslated(PRECISION_FRACTION * 1,
				0)));
		assertFalse(e.contains(r.getLeft().getTranslated(
				-PRECISION_FRACTION * 1000, 0)));

		assertTrue(e.contains(r.getTop()));
		assertTrue(e.contains(r.getTop().getTranslated(0,
				PRECISION_FRACTION * 100)));
		assertFalse(e.contains(r.getTop().getTranslated(0,
				-PRECISION_FRACTION * 100)));

		assertTrue(e.contains(r.getRight()));
		assertTrue(e.contains(r.getRight().getTranslated(
				-PRECISION_FRACTION * 100, 0)));
		assertFalse(e.contains(r.getRight().getTranslated(
				PRECISION_FRACTION * 100, 0)));

		assertTrue(e.contains(r.getBottom()));
		assertTrue(e.contains(r.getBottom().getTranslated(0,
				-PRECISION_FRACTION * 100)));
		assertFalse(e.contains(r.getBottom().getTranslated(0,
				PRECISION_FRACTION * 100)));

		for (Point p : e.getIntersections(new Line(r.getTopLeft(), r
				.getBottomRight()))) {
			assertTrue(e.contains(p));
		}
		for (Point p : e.getIntersections(new Line(r.getTopRight(), r
				.getBottomLeft()))) {
			assertTrue(e.contains(p));
		}
	}

	@Test
	public void test_intersects_with_Line() {
		Rectangle r = new Rectangle(34.3435, 56.458945, 123.3098, 146.578);
		Ellipse e = new Ellipse(r);
		for (Line l : r.getOutlineSegments()) {
			assertTrue(e.touches(l)); // line touches ellipse (tangent)
		}
	}

	@Test
	public void test_get_intersections_with_Ellipse_strict() {
		Rectangle r = new Rectangle(34.3435, 56.458945, 123.3098, 146.578);
		Ellipse e1 = new Ellipse(r);
		Ellipse e2 = new Ellipse(r);

		// ellipses are identical = returns no intersections, user can check
		// this via equals()
		Point[] intersections = e1.getIntersections(e2);
		assertEquals(0, intersections.length);

		// touching left
		Rectangle r2 = r.getExpanded(0, -10, -10, -10);
		e2 = new Ellipse(r2);
		intersections = e1.getIntersections(e2);
		for (Point poi : intersections) {
			assertTrue(e1.contains(poi));
			assertTrue(e2.contains(poi));
		}
		assertEquals(1, intersections.length);

		// if we create an x-scaled ellipse at the same position as before, they
		// should have 3 poi (the touching point and two crossing intersections)
		r2 = r.getExpanded(0, 0, 100, 0);
		e2 = new Ellipse(r2);
		intersections = e1.getIntersections(e2);
		assertEquals(3, intersections.length);

		// if we create a y-scaled ellipse at the same position as before, they
		// should have 3 poi (the touching point and two crossing intersections)
		r2 = r.getExpanded(0, 0, 0, 100);
		e2 = new Ellipse(r2);
		intersections = e1.getIntersections(e2);
		assertEquals(3, intersections.length);

		// if we create an x-scaled ellipse at the same y-position as before,
		// the
		// two should touch at two positions:
		r2 = r.getExpanded(50, 0, 50, 0);
		e2 = new Ellipse(r2);
		intersections = e1.getIntersections(e2);
		assertEquals(2, intersections.length);

		// the two poi are top and bottom border mid-points:
		int equalsTop = 0;
		int equalsBottom = 0;

		Rectangle bounds = e1.getBounds();
		for (Point poi : intersections) {
			// we need to losen the equality test, because the points of
			// intersection may be to unprecise
			Point top = bounds.getTop();
			if (top.equals(poi)) {
				equalsTop++;
			}
			if (bounds.getBottom().equals(poi)) {
				equalsBottom++;
			}
		}

		assertEquals(
				"The top border mid-point should be one of the two intersections.",
				1, equalsTop);
		assertEquals(
				"The bottom border mid-point should be one of the two intersections.",
				1, equalsBottom);

		// if we create a y-scaled ellipse at the same x-position as before, the
		// two should touch at two positions:
		r2 = r.getExpanded(0, 50, 0, 50);
		e2 = new Ellipse(r2);
		intersections = e1.getIntersections(e2);
		assertEquals(2, intersections.length);

		// the two poi are left and right border mid-points:
		int equalsLeft = 0;
		int equalsRight = 0;

		for (Point poi : intersections) {
			if (bounds.getLeft().equals(poi)) {
				equalsLeft++;
			}
			if (bounds.getRight().equals(poi)) {
				equalsRight++;
			}
		}

		assertEquals(
				"The left border mid-point should be one of the two intersections.",
				1, equalsLeft);
		assertEquals(
				"The right border mid-point should be one of the two intersections.",
				1, equalsRight);
	}

	private void intersectionsTolerance(Ellipse e1, Ellipse e2,
			Point... expected) {
		Point[] intersections = e1.getIntersections(e2);
		boolean[] foundExpected = new boolean[expected.length];
		for (Point poi : intersections) {
			assertTrue(
					"All points of intersection have to be contained by the first ellipse.",
					e1.contains(poi));
			assertTrue(
					"All points of intersection have to be contained by the second ellipse.",
					e2.contains(poi));
			for (int i = 0; i < expected.length; i++) {
				if (poi.equals(expected[i])) {
					foundExpected[i] = true;
				}
			}
		}
		for (int i = 0; i < expected.length; i++) {
			assertTrue("An expected point of intersection " + expected[i]
					+ " not found in the list of intersections.",
					foundExpected[i]);
		}
	}

	@Test
	public void test_getIntersections_with_Ellipse_tolerance() {
		Rectangle r = new Rectangle(34.3435, 56.458945, 123.3098, 146.578);
		Ellipse e1 = new Ellipse(r);
		Ellipse e2 = new Ellipse(r);

		// ellipses are identical = returns no intersections, user can check
		// this via equals()
		Point[] intersections = e1.getIntersections(e2);
		assertEquals(0, intersections.length);

		// touching left
		Rectangle r2 = r.getExpanded(0, -10, -10, -10);
		e2 = new Ellipse(r2);
		intersectionsTolerance(e1, e2, r.getLeft());

		// if we create an x-scaled ellipse at the same position as before, they
		// should have 3 poi (the touching point and two crossing intersections)
		r2 = r.getExpanded(0, 0, 100, 0);
		e2 = new Ellipse(r2);
		intersectionsTolerance(e1, e2, r.getLeft()); // TODO: other two pois

		// if we create a y-scaled ellipse at the same position as before, they
		// should have 3 poi (the touching point and two crossing intersections)
		r2 = r.getExpanded(0, 0, 0, 100);
		e2 = new Ellipse(r2);
		intersectionsTolerance(e1, e2, r.getTop()); // TODO: other two pois

		// if we create an x-scaled ellipse at the same y-position as before,
		// the two should touch at two positions:
		r2 = r.getExpanded(50, 0, 50, 0);
		e2 = new Ellipse(r2);
		intersectionsTolerance(e1, e2, r.getTop(), r.getBottom());

		// if we create a y-scaled ellipse at the same x-position as before, the
		// two should touch at two positions:
		r2 = r.getExpanded(0, 50, 0, 50);
		e2 = new Ellipse(r2);
		intersectionsTolerance(e1, e2, r.getLeft(), r.getRight());
	}

	// @Ignore("This test is too strict. For a liberal test see below: test_getIntersections_with_Ellipse_Bezier_special_tolerance")
	@Test
	public void test_getIntersections_with_Ellipse_Bezier_special() {
		// 3 nearly tangential intersections
		Ellipse e1 = new Ellipse(126, 90, 378, 270);
		Ellipse e2 = new Ellipse(222, 77, 200, 200);
		assertEquals(2, e1.getIntersections(e2).length);

		e2 = new Ellipse(133, 90, 2 * (315 - 133), 200);
		Point[] intersections = e1.getIntersections(e2);
		assertEquals(3, intersections.length);

		e2 = new Ellipse(143, 90, 2 * (315 - 143), 200);
		assertEquals(3, e1.getIntersections(e2).length);

		e2 = new Ellipse(145, 90, 2 * (315 - 145), 200);
		assertEquals(3, e1.getIntersections(e2).length);
	}

	@Test
	public void test_getIntersections_with_Ellipse_Bezier_special_tolerance() {
		// 3 nearly tangential intersections
		Ellipse e1 = new Ellipse(126, 90, 378, 270);
		Ellipse e2 = new Ellipse(222, 77, 200, 200);
		intersectionsTolerance(e1, e2); // TODO: find out the 2 expected points

		e2 = new Ellipse(133, 90, 2 * (315 - 133), 200);
		intersectionsTolerance(e1, e2); // TODO: find out the 3 expected points

		e2 = new Ellipse(143, 90, 2 * (315 - 143), 200);
		intersectionsTolerance(e1, e2); // TODO: find out the 3 expected points

		e2 = new Ellipse(145, 90, 2 * (315 - 145), 200);
		intersectionsTolerance(e1, e2); // TODO: find out the 3 expected points
	}

}
