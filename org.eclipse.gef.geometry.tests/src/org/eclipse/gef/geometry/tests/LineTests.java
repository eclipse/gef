/*******************************************************************************
 * Copyright (c) 2010, 2016 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (Research Group Software Construction, RWTH Aachen University) - contribution for Bugzilla 245182
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.junit.Test;

/**
 * Unit tests for {@link Line}.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class LineTests {

	private static final double PRECISION_FRACTION = TestUtils
			.getPrecisionFraction();

	private static final double RECOGNIZABLE_FRACTION = PRECISION_FRACTION
			+ PRECISION_FRACTION / 10;
	private static final double UNRECOGNIZABLE_FRACTION = PRECISION_FRACTION
			- PRECISION_FRACTION / 10;

	@Test
	public void test_constructors() {
		Line l1 = new Line(0, 0, 5, 0);
		assertTrue(l1.equals(new Line(new Point(), new Point(5, 0))));
	}

	@Test
	public void test_contains_Point() {
		Line l1 = new Line(0, 0, 5, 0);
		assertTrue(l1.contains(l1.getP1()));
		assertTrue(l1.contains(l1.getP2()));
		for (double x = -5; x <= 10; x += 0.1) {
			assertTrue(l1.contains(
					new Point(x, 0)) == (PrecisionUtils.smallerEqual(0, x)
							&& PrecisionUtils.smallerEqual(x, 5)));
		}

		l1 = new Line(0, 0, 0, 0);
		assertTrue(l1.contains(l1.getP1()));
		assertTrue(l1.contains(l1.getP2()));
		assertFalse(l1.contains(new Point(1, 1)));
	}

	@Test
	public void test_copy() {
		Line l1 = new Line(0, 0, 5, 0);
		assertTrue(l1.equals(l1.getCopy()));
		assertTrue(l1.equals(l1.clone()));
		assertTrue(l1.getCopy().equals(l1.clone()));
	}

	@Test
	public void test_equals() {
		Line l1 = new Line(0, 0, 5, 0);
		assertTrue(l1.equals(l1));
		assertTrue(l1.equals(new Line(0.0, 0.0, 5.0, 0.0)));
		assertTrue(l1.equals(new Line(5.0, 0.0, 0.0, 0.0)));
		assertFalse(l1.equals(new Line(0.1, 0.0, 5.0, 0.0)));
		assertFalse(l1.equals(new Line(0.0, 0.1, 5.0, 0.0)));
		assertFalse(l1.equals(new Line(0.0, 0.0, 5.1, 0.0)));
		assertFalse(l1.equals(new Line(0.0, 0.0, 5.0, 0.1)));
		assertFalse(l1.equals(new Point()));
		assertFalse(l1.equals(null));

		assertTrue(l1.equals(new Line(UNRECOGNIZABLE_FRACTION,
				UNRECOGNIZABLE_FRACTION, 5.0 + UNRECOGNIZABLE_FRACTION,
				UNRECOGNIZABLE_FRACTION)));
		assertTrue(l1.equals(new Line(-UNRECOGNIZABLE_FRACTION,
				-UNRECOGNIZABLE_FRACTION, 5.0 - UNRECOGNIZABLE_FRACTION,
				-UNRECOGNIZABLE_FRACTION)));
		assertFalse(
				l1.equals(new Line(RECOGNIZABLE_FRACTION, RECOGNIZABLE_FRACTION,
						5.0 + RECOGNIZABLE_FRACTION, RECOGNIZABLE_FRACTION)));
		assertFalse(l1
				.equals(new Line(-RECOGNIZABLE_FRACTION, -RECOGNIZABLE_FRACTION,
						5.0 - RECOGNIZABLE_FRACTION, -RECOGNIZABLE_FRACTION)));
	}

	@Test
	public void test_get() {
		Line l1 = new Line(100, 100, 200, 200);
		assertEquals(new Point(100, 100), l1.get(0));
		assertEquals(new Point(125, 125), l1.get(0.25));
		assertEquals(new Point(150, 150), l1.get(0.5));
		assertEquals(new Point(175, 175), l1.get(0.75));
		assertEquals(new Point(200, 200), l1.get(1));

		Line l2 = new Line(200, 200, 100, 100);
		assertEquals(new Point(200, 200), l2.get(0));
		assertEquals(new Point(175, 175), l2.get(0.25));
		assertEquals(new Point(150, 150), l2.get(0.5));
		assertEquals(new Point(125, 125), l2.get(0.75));
		assertEquals(new Point(100, 100), l2.get(1));

	}

	@Test
	public void test_getBounds() {
		Line l1 = new Line(0, 0, 5, 0);
		Rectangle bounds = l1.getBounds();
		assertTrue(bounds.getLeft().equals(l1.getP1()));
		assertTrue(bounds.getRight().equals(l1.getP2()));
		assertTrue(bounds.getTopLeft().equals(l1.getP1()));
		assertTrue(bounds.getBottomLeft().equals(l1.getP1()));
		assertTrue(bounds.getTopRight().equals(l1.getP2()));
		assertTrue(bounds.getBottomRight().equals(l1.getP2()));

		l1 = new Line(-5, -5, 5, 5);
		bounds = l1.getBounds();
		assertTrue(bounds.getTopLeft().equals(l1.getP1()));
		assertTrue(bounds.getBottomRight().equals(l1.getP2()));

		l1 = new Line(-5, 5, 5, -5);
		bounds = l1.getBounds();
		assertTrue(bounds.getBottomLeft().equals(l1.getP1()));
		assertTrue(bounds.getTopRight().equals(l1.getP2()));
	}

	@Test
	public void test_getDirectionCCW() {
		// build lines by stepping 45deg in CCW order around the origin
		Line line = new Line(0, 0, 10, 0);
		assertTrue(PrecisionUtils.equal(0, line.getDirectionCCW().deg()));

		line = new Line(0, 0, 10, 10);
		assertTrue(PrecisionUtils.equal(45, line.getDirectionCCW().deg()));

		line = new Line(0, 0, 0, 10);
		assertTrue(PrecisionUtils.equal(90, line.getDirectionCCW().deg()));

		line = new Line(0, 0, -10, 10);
		assertTrue(PrecisionUtils.equal(135, line.getDirectionCCW().deg()));

		line = new Line(0, 0, -10, 0);
		assertTrue(PrecisionUtils.equal(180, line.getDirectionCCW().deg()));

		line = new Line(0, 0, -10, -10);
		assertTrue(PrecisionUtils.equal(225, line.getDirectionCCW().deg()));

		line = new Line(0, 0, 0, -10);
		assertTrue(PrecisionUtils.equal(270, line.getDirectionCCW().deg()));

		line = new Line(0, 0, 10, -10);
		assertTrue(PrecisionUtils.equal(315, line.getDirectionCCW().deg()));
	}

	@Test
	public void test_getDirectionCW() {
		// build lines by stepping 45deg in CW order around the origin
		Line line = new Line(0, 0, 10, 0);
		assertTrue(PrecisionUtils.equal(0, line.getDirectionCW().deg()));

		line = new Line(0, 0, 10, -10);
		assertTrue(PrecisionUtils.equal(45, line.getDirectionCW().deg()));

		line = new Line(0, 0, 0, -10);
		assertTrue(PrecisionUtils.equal(90, line.getDirectionCW().deg()));

		line = new Line(0, 0, -10, -10);
		assertTrue(PrecisionUtils.equal(135, line.getDirectionCW().deg()));

		line = new Line(0, 0, -10, 0);
		assertTrue(PrecisionUtils.equal(180, line.getDirectionCW().deg()));

		line = new Line(0, 0, -10, 10);
		assertTrue(PrecisionUtils.equal(225, line.getDirectionCW().deg()));

		line = new Line(0, 0, 0, 10);
		assertTrue(PrecisionUtils.equal(270, line.getDirectionCW().deg()));

		line = new Line(0, 0, 10, 10);
		assertTrue(PrecisionUtils.equal(315, line.getDirectionCW().deg()));
	}

	@Test
	public void test_getIntersection_specials() {
		// degenerated cases
		Line degen = new Line(new Point(), new Point());
		Line normal = new Line(new Point(-5, 0), new Point(5, 0));
		assertEquals(new Point(), degen.getIntersection(normal));
		assertEquals(new Point(), normal.getIntersection(degen));

		// identical
		assertNull(normal.getIntersection(normal));

		// intersection within precision, no real intersection
		Line close = new Line(new Point(-5, UNRECOGNIZABLE_FRACTION),
				new Point(5, UNRECOGNIZABLE_FRACTION));

		// parallel so we do not return an intersection point
		assertNull(normal.getIntersection(close));
		assertNull(close.getIntersection(normal));

		// non parallel, start point intersection
		Line closeSp = new Line(new Point(-5, UNRECOGNIZABLE_FRACTION),
				new Point(-5, 10));
		assertEquals(new Point(-5, 0), normal.getIntersection(closeSp));
		assertEquals(new Point(-5, 0), closeSp.getIntersection(normal));

		// non parallel, end point intersection
		Line closeEp = new Line(new Point(-5, 10),
				new Point(-5, UNRECOGNIZABLE_FRACTION));
		assertEquals(new Point(-5, 0), normal.getIntersection(closeEp));
		assertEquals(new Point(-5, 0), closeEp.getIntersection(normal));

		// intersection within precision, straights do intersect too, but the
		// intersection of the straights is out of precision
		Line slope = new Line(new Point(-5, UNRECOGNIZABLE_FRACTION),
				new Point(5, 2 * UNRECOGNIZABLE_FRACTION));

		// no point of intersection can be identified, because both endpoints
		// lie on the line. is is assumed to be parallel.
		assertNull(normal.getIntersection(slope));
		assertNull(slope.getIntersection(normal));

		// no intersection, straights do intersect
		Line elsewhere = new Line(new Point(-5, 1), new Point(5, 10));
		assertNull(normal.getIntersection(elsewhere));
		assertNull(elsewhere.getIntersection(normal));

		// single end point intersection with parallel lines:
		// X-------X-------X
		Line l1 = new Line(400.0, 102.48618784530387, 399.99999999999994,
				100.0);
		Line l2 = new Line(400.0, 51.10497237569061, 399.99999999999994, 100.0);
		assertNotNull(l1.getIntersection(l2));
	}

	@Test
	public void test_getIntersection_with_Line() {
		// simple intersection
		Line l1 = new Line(0, 0, 4, 4);
		Line l2 = new Line(0, 4, 4, 0);
		assertTrue(l1.touches(l2));
		assertTrue(l1.getIntersection(l2).equals(new Point(2, 2)));
		assertTrue(l2.getIntersection(l1).equals(new Point(2, 2)));

		// lines touch in one point
		Line l3 = new Line(4, 4, 7, 9);
		assertTrue(l1.getIntersection(l3).equals(new Point(4, 4)));
		assertTrue(l3.getIntersection(l1).equals(new Point(4, 4)));

		// lines overlap
		Line l4 = new Line(2, 2, 6, 6);
		assertTrue(l1.getIntersection(l4) == null);
		assertTrue(l4.getIntersection(l1) == null);

		// lines overlap in one end point
		Line l5 = new Line(4, 4, 6, 6);
		assertTrue(l1.getIntersection(l5).equals(new Point(4, 4)));
		assertTrue(l5.getIntersection(l1).equals(new Point(4, 4)));
		l5 = new Line(6, 6, 4, 4);
		assertTrue(l1.getIntersection(l5).equals(new Point(4, 4)));
		assertTrue(l5.getIntersection(l1).equals(new Point(4, 4)));

		Line l6 = new Line(-1, -1, 0, 0);
		assertTrue(l1.getIntersection(l6).equals(new Point()));
		assertTrue(l6.getIntersection(l1).equals(new Point()));
		l6 = new Line(0, 0, -1, -1);
		assertTrue(l1.getIntersection(l6).equals(new Point()));
		assertTrue(l6.getIntersection(l1).equals(new Point()));

		// lines do not intersect
		Line l7 = new Line(4, 0, 5, 4);
		assertNull(l1.getIntersection(l7));
		assertNull(l7.getIntersection(l1));
	}

	@Test
	public void test_getProjection() {
		Line x100 = new Line(0, 0, 100, 0);
		// check start point
		assertEquals(new Point(0, 0), x100.getProjection(new Point(-10, 0)));
		assertEquals(new Point(0, 0), x100.getProjection(new Point(-10, 10)));
		assertEquals(new Point(0, 0), x100.getProjection(new Point(-10, -10)));
		// check end point
		assertEquals(new Point(100, 0), x100.getProjection(new Point(110, 0)));
		assertEquals(new Point(100, 0),
				x100.getProjection(new Point(110, -10)));
		assertEquals(new Point(100, 0), x100.getProjection(new Point(110, 10)));
		// check middle
		assertEquals(new Point(50, 0), x100.getProjection(new Point(50, 0)));
		assertEquals(new Point(50, 0), x100.getProjection(new Point(50, 10)));
		assertEquals(new Point(50, 0), x100.getProjection(new Point(50, -10)));
		// 25
		assertEquals(new Point(25, 0), x100.getProjection(new Point(25, 0)));
		assertEquals(new Point(25, 0), x100.getProjection(new Point(25, 10)));
		assertEquals(new Point(25, 0), x100.getProjection(new Point(25, -10)));
		// 75
		assertEquals(new Point(75, 0), x100.getProjection(new Point(75, 0)));
		assertEquals(new Point(75, 0), x100.getProjection(new Point(75, 10)));
		assertEquals(new Point(75, 0), x100.getProjection(new Point(75, -10)));

		Line y100 = new Line(0, 0, 0, 100);
		// check start point
		assertEquals(new Point(0, 0), y100.getProjection(new Point(0, -10)));
		assertEquals(new Point(0, 0), y100.getProjection(new Point(10, -10)));
		assertEquals(new Point(0, 0), y100.getProjection(new Point(-10, -10)));
		// check end point
		assertEquals(new Point(0, 100), y100.getProjection(new Point(0, 110)));
		assertEquals(new Point(0, 100),
				y100.getProjection(new Point(-10, 110)));
		assertEquals(new Point(0, 100), y100.getProjection(new Point(10, 110)));
		// check middle
		assertEquals(new Point(0, 50), y100.getProjection(new Point(0, 50)));
		assertEquals(new Point(0, 50), y100.getProjection(new Point(10, 50)));
		assertEquals(new Point(0, 50), y100.getProjection(new Point(-10, 50)));
		// 25
		assertEquals(new Point(0, 25), y100.getProjection(new Point(0, 25)));
		assertEquals(new Point(0, 25), y100.getProjection(new Point(10, 25)));
		assertEquals(new Point(0, 25), y100.getProjection(new Point(-10, 25)));
		// 75
		assertEquals(new Point(0, 75), y100.getProjection(new Point(0, 75)));
		assertEquals(new Point(0, 75), y100.getProjection(new Point(10, 75)));
		assertEquals(new Point(0, 75), y100.getProjection(new Point(-10, 75)));
	}

	@Test
	public void test_getters() {
		for (double x1 = -2; x1 <= 2; x1 += 0.5) {
			for (double y1 = -2; y1 <= 2; y1 += 0.5) {
				Point p1 = new Point(x1, y1);

				for (double x2 = -2; x2 <= 2; x2 += 0.5) {
					for (double y2 = -2; y2 <= 2; y2 += 0.5) {
						Point p2 = new Point(x2, y2);
						Line line = new Line(p1, p2);
						assertTrue(line.getP1().equals(p1));
						assertTrue(line.getP2().equals(p2));
						assertTrue(PrecisionUtils.equal(line.getX1(), p1.x));
						assertTrue(PrecisionUtils.equal(line.getX2(), p2.x));
						assertTrue(PrecisionUtils.equal(line.getY1(), p1.y));
						assertTrue(PrecisionUtils.equal(line.getY2(), p2.y));

						Point[] points = line.getPoints();
						assertTrue(points[0].equals(p1));
						assertTrue(points[1].equals(p2));
					}
				}
			}
		}
	}

	@Test
	public void test_hashCode() {
		// hashCode() has to be the same for two equal()ly lines
		// (I think this is impossible with the current PrecisionUtils.)
		Line l1 = new Line(0, 0, 5, 0);

		assertEquals(l1.hashCode(), l1.hashCode());
		assertEquals(l1.hashCode(), new Line(0.0, 0.0, 5.0, 0.0).hashCode());

		assertTrue(l1.hashCode() == new Line(UNRECOGNIZABLE_FRACTION,
				UNRECOGNIZABLE_FRACTION, 5.0 + UNRECOGNIZABLE_FRACTION,
				UNRECOGNIZABLE_FRACTION).hashCode());
		assertTrue(l1.hashCode() == new Line(-UNRECOGNIZABLE_FRACTION,
				-UNRECOGNIZABLE_FRACTION, 5.0 - UNRECOGNIZABLE_FRACTION,
				-UNRECOGNIZABLE_FRACTION).hashCode());
	}

	@Test
	public void test_intersects_specials() {
		// degenerated cases
		Line degen = new Line(new Point(), new Point());
		Line normal = new Line(new Point(-5, 0), new Point(5, 0));
		assertTrue(degen.touches(normal));
		assertTrue(normal.touches(degen));

		// identical
		assertTrue(normal.touches(normal));

		// intersection within precision. no real intersection
		Line close = new Line(new Point(-5, UNRECOGNIZABLE_FRACTION),
				new Point(5, UNRECOGNIZABLE_FRACTION));
		assertTrue(normal.touches(close));
		assertTrue(close.touches(normal));

		Line closeSp = new Line(new Point(-5, UNRECOGNIZABLE_FRACTION),
				new Point(-5, 10));
		assertTrue(normal.touches(closeSp));
		assertTrue(closeSp.touches(normal));

		Line closeEp = new Line(new Point(-5, 10),
				new Point(-5, UNRECOGNIZABLE_FRACTION));
		assertTrue(normal.touches(closeEp));
		assertTrue(closeEp.touches(normal));

		// intersection within precision, straights do intersect too, but the
		// intersection of the straights is out of precision
		Line slope = new Line(new Point(-5, UNRECOGNIZABLE_FRACTION),
				new Point(5, 2 * UNRECOGNIZABLE_FRACTION));
		assertTrue(normal.touches(slope));
		assertTrue(slope.touches(normal));

		// no intersection, straights do intersect
		Line elsewhere = new Line(new Point(-5, 1), new Point(5, 10));
		assertTrue(!normal.touches(elsewhere));
		assertTrue(!elsewhere.touches(normal));

		// big lines, imprecisely parallel but intersecting
		Line bigX = new Line(new Point(-1000, 0), new Point(1000, 0));
		Line impreciselyParallel = new Line(
				new Point(-1000, -UNRECOGNIZABLE_FRACTION),
				new Point(1000, UNRECOGNIZABLE_FRACTION));
		assertTrue(new Vector(bigX.getP1(), bigX.getP2())
				.isParallelTo(new Vector(impreciselyParallel.getP1(),
						impreciselyParallel.getP2())));
		assertTrue(bigX.touches(impreciselyParallel));
	}

	@Test
	public void test_intersects_with_Line() {
		// simple intersection
		Line l1 = new Line(0, 0, 4, 4);
		Line l2 = new Line(0, 4, 4, 0);
		assertTrue(l1.touches(l2));
		assertTrue(l2.touches(l1));

		// lines touch in one point
		Line l3 = new Line(4, 4, 7, 9);
		assertTrue(l1.touches(l3));
		assertTrue(l3.touches(l1));

		// lines overlap
		Line l4 = new Line(2, 2, 6, 6);
		assertTrue(l1.touches(l4));
		assertTrue(l4.touches(l1));

		// one line is a single point
		Line l5 = new Line(1, 1, 1, 1);
		assertTrue(l5.touches(l1));
		assertTrue(l1.touches(l5));

		// straights would intersect, but these lines do not
		Line l6 = new Line(4, 0, 5, 4);
		assertFalse(l6.touches(l1));
		assertFalse(l1.touches(l6));
	}

	@Test
	public void test_intersects_with_Rect() {
		Line l1 = new Line(0, 0, 4, 4);
		Rectangle r1 = new Rectangle(0, 4, 4, 4);
		assertTrue(l1.touches(r1));
	}

	@Test
	public void test_setters() {
		for (double x1 = -2; x1 <= 2; x1 += 0.5) {
			for (double y1 = -2; y1 <= 2; y1 += 0.5) {
				Point p1 = new Point(x1, y1);

				for (double x2 = -2; x2 <= 2; x2 += 0.5) {
					for (double y2 = -2; y2 <= 2; y2 += 0.5) {
						Point p2 = new Point(x2, y2);

						Line line = new Line(new Point(-5, -5),
								new Point(-10, -10));

						assertFalse(line.getP1().equals(p1));
						assertFalse(line.getP2().equals(p2));

						line.setP1(p1);
						line.setP2(p2);

						assertTrue(line.getP1().equals(p1));
						assertTrue(line.getP2().equals(p2));

						line.setX1(-5);
						line.setY1(-5);
						assertTrue(line.getP1().equals(new Point(-5, -5)));

						line.setX2(5);
						line.setY2(5);
						assertTrue(line.getP2().equals(new Point(5, 5)));

						Line l2 = new Line(p1, p2);
						line.setLine(l2);
						assertTrue(line.equals(l2));

						l2 = new Line(p2, p1);
						line.setLine(p2, p1);
						assertTrue(line.equals(l2));

						line.setLine(0, 1, 2, 3);
						assertTrue(line.equals(new Line(0, 1, 2, 3)));
					}
				}
			}
		}
	}

	@Test
	public void test_toString() {
		Line l1 = new Line(0, 0, 5, 0);
		assertEquals("Line: (0.0, 0.0) -> (5.0, 0.0)", l1.toString());
	}

}
