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
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef.geometry.planar.CubicCurve;
import org.eclipse.gef.geometry.planar.Ellipse;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Path;
import org.eclipse.gef.geometry.planar.Path.Segment;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.junit.Test;

/**
 * Unit tests for {@link Ellipse}.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class EllipseTests {

	private static final double PRECISION_FRACTION = TestUtils
			.getPrecisionFraction();

	private void checkPointContainment(Rectangle r, IGeometry g) {
		assertFalse(g.contains(r.getTopLeft()));
		assertFalse(g.contains(r.getTopRight()));
		assertFalse(g.contains(r.getBottomLeft()));
		assertFalse(g.contains(r.getBottomRight()));

		assertTrue(g.contains(r.getCenter()));

		assertTrue(g.contains(r.getLeft()));
		assertTrue(g.contains(
				r.getLeft().getTranslated(PRECISION_FRACTION * 1, 0)));
		assertFalse(g.contains(
				r.getLeft().getTranslated(-PRECISION_FRACTION * 1000, 0)));

		// due to AWT's behavior, we won't check getTop() but a point very near
		// to it, so that the Path() will survive these tests, too
		assertTrue(g.contains(r.getTop().getTranslated(0, 1)));
		assertTrue(g.contains(
				r.getTop().getTranslated(0, PRECISION_FRACTION * 100)));
		assertFalse(g.contains(
				r.getTop().getTranslated(0, -PRECISION_FRACTION * 100)));

		// due to AWT's behavior, we won't check getRight() but a point very
		// near to it, so that the Path() will survive these tests, too
		assertTrue(g.contains(r.getRight().getTranslated(-1, 0)));
		assertTrue(g.contains(
				r.getRight().getTranslated(-PRECISION_FRACTION * 100, 0)));
		assertFalse(g.contains(
				r.getRight().getTranslated(PRECISION_FRACTION * 100, 0)));

		// due to AWT's behavior, we won't check getBottom() but a point very
		// near to it, so that the Path() will survive these tests, too
		assertTrue(g.contains(r.getBottom().getTranslated(0, -1)));
		assertTrue(g.contains(
				r.getBottom().getTranslated(0, -PRECISION_FRACTION * 100)));
		assertFalse(g.contains(
				r.getBottom().getTranslated(0, PRECISION_FRACTION * 100)));
	}

	private void checkPoints(Point[] expected, Point[] obtained) {
		assertEquals(expected.length, obtained.length);
		for (Point e : expected) {
			boolean found = false;
			for (Point o : obtained) {
				if (e.equals(o)) {
					found = true;
					break;
				}
			}
			assertTrue(found);
		}
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
			assertTrue(
					"An expected point of intersection " + expected[i]
							+ " not found in the list of intersections.",
					foundExpected[i]);
		}
	}

	@Test
	public void test_contains_Line() {
		Ellipse e = new Ellipse(0, 0, 100, 50);
		assertFalse(e.contains(new Line(-10, -10, 10, -10)));
		assertFalse(e.contains(new Line(-10, -10, 50, 50)));
		assertTrue(e.contains(new Line(1, 25, 99, 25)));
		assertTrue(e.contains(new Line(0, 25, 100, 25)));
	}

	@Test
	public void test_contains_Point() {
		Rectangle r = new Rectangle(34.3435, 56.458945, 123.3098, 146.578);
		Ellipse e = new Ellipse(r);

		checkPointContainment(r, e);

		// these things could not be tested in the general case, because of
		// AWT's behavior
		assertTrue(e.contains(r.getTop()));
		assertTrue(e.contains(r.getRight()));
		assertTrue(e.contains(r.getBottom()));

		for (Point p : e.getIntersections(
				new Line(r.getTopLeft(), r.getBottomRight()))) {
			assertTrue(e.contains(p));
		}
		for (Point p : e.getIntersections(
				new Line(r.getTopRight(), r.getBottomLeft()))) {
			assertTrue(e.contains(p));
		}

		for (CubicCurve c : e.getOutlineSegments()) {
			assertTrue(e.contains(c.get(0.5)));
		}
	}

	@Test
	public void test_equals() {
		Ellipse e = new Ellipse(0, 0, 100, 50);
		assertFalse(e.equals(null));
		assertFalse(e.equals(new Point()));
		assertEquals(e, e);
		assertEquals(e, new Ellipse(0, 0, 100, 50));
		assertEquals(e, new Ellipse(new Rectangle(0, 0, 100, 50)));
		assertEquals(e, e.getCopy());
		assertFalse(e.equals(new Ellipse(0, 0, 100, 10)));
		assertFalse(e.equals(new Ellipse(0, 0, 10, 50)));
		assertFalse(e.equals(new Ellipse(10, 0, 100, 50)));
		assertFalse(e.equals(new Ellipse(0, 10, 100, 50)));
	}

	@Test
	public void test_get_intersections_Ellipse_strict() {
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

	@Test
	public void test_getCenter() {
		Ellipse e = new Ellipse(0, 0, 100, 50);
		assertEquals(new Point(50, 25), e.getCenter());
		e.scale(2);
		assertEquals(new Point(50, 25), e.getCenter());
		e.scale(0.5);
		e.scale(2, new Point());
		assertEquals(new Point(100, 50), e.getCenter());
		e.translate(-100, -50);
		assertEquals(new Point(), e.getCenter());
	}

	// @Ignore("This test is too strict. For a liberal test see below:
	// test_getIntersections_with_Ellipse_Bezier_special_tolerance")
	@Test
	public void test_getIntersections_Ellipse_Bezier_special() {
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

		e1 = new Ellipse(126.0, 90.0, 378.0, 270.0);
		e2 = new Ellipse(397.0, 327.0, 26.0, 22.0);
		assertEquals(2, e1.getIntersections(e2).length);
	}

	@Test
	public void test_getIntersections_Ellipse_Bezier_special_tolerance() {
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

	@Test
	public void test_getIntersections_Ellipse_tolerance() {
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

	@Test
	public void test_getIntersections_Line() {
		Ellipse e = new Ellipse(0, 0, 100, 50);
		Line lh = new Line(0, 25, 100, 25);
		Point[] is = e.getIntersections(lh);
		checkPoints(new Point[] { new Point(0, 25), new Point(100, 25) }, is);
		Line lv = new Line(50, 0, 50, 50);
		is = e.getIntersections(lv);
		checkPoints(new Point[] { new Point(50, 0), new Point(50, 50) }, is);

		lh = lh.getTranslated(new Point(0, -25)).toLine();
		is = e.getIntersections(lh);
		checkPoints(new Point[] { new Point(50, 0) }, is);

		lv = lv.getTranslated(new Point(-50, 0)).toLine();
		is = e.getIntersections(lv);
		checkPoints(new Point[] { new Point(0, 25) }, is);

		Line li = new Line(-100, 100, 0, 50);
		is = e.getIntersections(li);
		assertEquals(0, is.length);
	}

	@Test
	public void test_getIntersections_Line_failing() {
		Ellipse e = new Ellipse(0.0, 0.0, 100.0, 100.0);
		Point p1 = new Point(25.0, 25.0);
		Point p2 = new Point(25.0, -93.0);

		assertTrue(e.contains(p1));
		assertFalse(e.contains(p2));

		Line l = new Line(p1, p2);
		Point[] intersections = e.getIntersections(l);

		assertEquals(1, intersections.length);
	}

	@Test
	public void test_getShrinked() {
		Ellipse e = new Ellipse(0, 0, 100, 100);
		assertEquals(new Ellipse(50, 0, 50, 100), e.getShrinked(50, 0, 0, 0));
		assertEquals(new Ellipse(0, 50, 100, 50), e.getShrinked(0, 50, 0, 0));
		assertEquals(new Ellipse(0, 0, 50, 100), e.getShrinked(0, 0, 50, 0));
		assertEquals(new Ellipse(0, 0, 100, 50), e.getShrinked(0, 0, 0, 50));
	}

	@Test
	public void test_intersects_Line() {
		Rectangle r = new Rectangle(34.3435, 56.458945, 123.3098, 146.578);
		Ellipse e = new Ellipse(r);
		for (Line l : r.getOutlineSegments()) {
			assertTrue(e.touches(l)); // line touches ellipse (tangent)
		}
	}

	@Test
	public void test_toPath() {
		Rectangle r = new Rectangle(0, 0, 100, 50);
		Ellipse e = new Ellipse(r);

		Path path = e.toPath();
		checkPointContainment(r, path);

		// check path is closed
		Segment[] segments = path.getSegments();
		assertTrue(segments[segments.length - 1].getType() == Segment.CLOSE);
	}

	@Test
	public void test_toString() {
		Ellipse e = new Ellipse(0, 0, 100, 50);
		assertEquals("Ellipse (0.0, 0.0, 100.0, 50.0)", e.toString());
	}

}
