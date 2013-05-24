/*******************************************************************************
 * Copyright (c) 2010, 2012 IBM Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.utils.PointListUtils;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;
import org.junit.Test;

/**
 * Unit tests for {@link Point}.
 * 
 * @author anyssen
 * @author mwienand
 * 
 */
public class PointTests {

	@Test
	public void test_constructors() {
		Point p0 = new Point(0, 0);
		assertTrue(new Point().equals(p0));
		assertTrue(new Point().equals(new Point(p0)));

		Point p1 = new Point(10, 20);
		assertTrue(new Point(10.0, 20.0).equals(p1));
	}

	@Test
	public void test_copy_static() {
		Point[] points = new Point[10];
		for (int i = 0; i < 10; i++) {
			points[i] = new Point(i * i, i + i);
		}

		Point[] copy = Point.getCopy(points);

		for (int i = 0; i < 10; i++) {
			assertTrue(points[i].equals(copy[i]));
		}
	}

	@Test
	public void test_equals() {
		assertTrue(new Point(4, 7).equals(4, 7));
		assertFalse(new Point(3, 6).equals(3, 7));
		assertFalse(new Point(3, 6).equals(4, 6));
		assertTrue(new Point(1.0, 2.0).equals(new Point(1, 2)));
	}

	@Test
	public void test_getBounds() {
		Point[] points = new Point[9];
		for (int x = -4; x <= 4; x++) {
			double y = (1f / 3f) * x * x * x + 2f * x * x - (3f / 2f) * x - 1f
					/ 5f;
			points[x + 4] = new Point(x, y);
		}

		Rectangle bounds = Point.getBounds(points);

		assertTrue(bounds.getTopLeft().equals(-4, points[4].y));
		assertTrue(bounds.getBottomRight().equals(4, points[8].y));

		bounds = Point.getBounds(new Point[] {});

		assertTrue(bounds.getTopLeft().equals(new Point(0, 0)));
		assertTrue(bounds.getBottomRight().equals(new Point(0, 0)));
	}

	@Test
	public void test_getCentroid() {
		Point[] quad = new Point[] { new Point(0, 0), new Point(1, 0),
				new Point(1, 1), new Point(0, 1) };
		assertEquals(new Point(0.5, 0.5), Point.getCentroid(quad));
	}

	@Test
	public void test_getConvexHull1() {
		// test case from
		// http://stackoverflow.com/questions/482278/test-case-data-for-convex-hull

		Point[] points = PointListUtils.toPointsArray(new double[] {
				0.3215348546593775, 0.03629583077160248, 0.02402358131857918,
				-0.2356728797179394, 0.04590851212470659, -0.4156409924995536,
				0.3218384001607433, 0.1379850698988746, 0.11506479756447,
				-0.1059521474930943, 0.2622539999543261, -0.29702873322836,
				-0.161920957418085, -0.4055339716426413, 0.1905378631228002,
				0.3698601009043493, 0.2387090918968516, -0.01629827079949742,
				0.07495888748668034, -0.1659825110491202, 0.3319341836794598,
				-0.1821814101954749, 0.07703635755650362, -0.2499430638271785,
				0.2069242999022122, -0.2232970760420869, 0.04604079532068295,
				-0.1923573186549892, 0.05054295812784038, 0.4754929463150845,
				-0.3900589168910486, 0.2797829520700341, 0.3120693385713448,
				-0.0506329867529059, 0.01138812723698857, 0.4002504701728471,
				0.009645149586391732, 0.1060251100976254, -0.03597933197019559,
				0.2953639456959105, 0.1818290866742182, 0.001454397571696298,
				0.444056063372694, 0.2502497166863175, -0.05301752458607545,
				-0.06553921621808712, 0.4823896228171788, -0.4776170002088109,
				-0.3089226845734964, -0.06356112199235814, -0.271780741188471,
				0.1810810595574612, 0.4293626522918815, 0.2980897964891882,
				-0.004796652127799228, 0.382663812844701, 0.430695573269106,
				-0.2995073500084759, 0.1799668387323309, -0.2973467472915973,
				0.4932166845474547, 0.4928094162538735, -0.3521487911717489,
				0.4352656197131292, -0.4907368011686362, 0.1865826865533206,
				-0.1047924716070224, -0.247073392148198, 0.4374961861758457,
				-0.001606279519951237, 0.003256207800708899,
				-0.2729194320486108, 0.04310378203457577, 0.4452604050238248,
				0.4916198379282093, -0.345391701297268, 0.001675087028811806,
				0.1531837672490476, -0.4404289572876217, -0.2894855991839297 });

		Point[] convexHull = Point.getConvexHull(points);
		assertTrue(new Polygon(PointListUtils.toPointsArray(new double[] {
				-0.161920957418085, -0.4055339716426413, -0.4404289572876217,
				-0.2894855991839297, -0.4907368011686362, 0.1865826865533206,
				-0.3521487911717489, 0.4352656197131292, 0.05054295812784038,
				0.4754929463150845, 0.4932166845474547, 0.4928094162538735,
				0.4916198379282093, -0.345391701297268, 0.4823896228171788,
				-0.4776170002088109 })).equals(new Polygon(convexHull)));
	}

	@Test
	public void test_getConvexHull2() {
		Point[] convexHull = Point.getConvexHull(new Point[] {
				new Point(0.0, 75.0),
				new Point(0.3333333333333333, 0.9411910020934172),
				new Point(0.6666666666666666, -60.0), new Point(1.0, -60.0) });
		assertEquals(
				new Polygon(PointListUtils.toPointsArray(new double[] { 0, 75,
						0.3333333333333333, 0.9411910020934172,
						0.6666666666666666, -60, 1, -60 })), new Polygon(
						convexHull));
	}

	@Test
	public void test_getConvexHull3() {
		Point[] convexHull = Point.getConvexHull(new Point[] {
				new Point(0.0, -1.8277675577160887E-4),
				new Point(0.3333333333333333, -1.1294769632887472E-4),
				new Point(0.6666666666666666, -4.311817922240293E-5),
				new Point(1.0, 2.671179560675793E-5) });
		assertEquals(3, convexHull.length);
	}

	@Test
	public void test_getConvexHull4() {
		Point[] convexHull = Point.getConvexHull(new Point[] { new Point(0, 0),
				new Point(0, 0), new Point(10, 0), new Point(10, 10),
				new Point(0, 10) });
		assertEquals(new Rectangle(0, 0, 10, 10).toPolygon(), new Polygon(
				convexHull));
	}

	@Test
	public void test_getConvexHull5() {
		Point[] convexHull = Point.getConvexHull(new Point[] { new Point(0, 0),
				new Point(10, 0), new Point(10, 10), new Point(0, 0),
				new Point(0, 10) });
		assertEquals(new Rectangle(0, 0, 10, 10).toPolygon(), new Polygon(
				convexHull));
	}

	@Test
	public void test_getConvexHull6() {
		Point[] convexHull = Point.getConvexHull(new Point[] { new Point(0, 0),
				new Point(10, 0), new Point(), new Point(10, 10), new Point(),
				new Point(0, 10), new Point(), new Point(10, 10) });
		assertEquals(new Rectangle(0, 0, 10, 10).toPolygon(), new Polygon(
				convexHull));
	}

	@Test
	public void test_getConvexHull7() {
		Point[] convexHull = Point.getConvexHull(new Point[] {
				new Point(10, 10), new Point(5, 5), new Point(0, 0),
				new Point(10, 10), new Point(5, 5), new Point(10, 0),
				new Point(10, 10), new Point(0, 10), new Point(10, 10) });
		assertEquals(new Rectangle(0, 0, 10, 10).toPolygon(), new Polygon(
				convexHull));
	}

	@Test
	public void test_getCopy() {
		Point p0 = new Point(0, 0);
		Point p1 = new Point(10, 20);

		assertTrue(p0.getCopy().equals(p0));
		assertTrue(p0.clone().equals(p0));
		assertTrue(p0.getCopy().equals(p0.clone()));

		assertTrue(p1.getCopy().equals(p1));
		assertTrue(p1.clone().equals(p1));
		assertTrue(p1.getCopy().equals(p1.clone()));
	}

	@Test
	public void test_getDistance() {
		Point p = new Point(4, 3);
		assertTrue(PrecisionUtils.equal(p.getDistance(new Point()), 5));

		Point q = new Point(104, 3);
		assertTrue(PrecisionUtils.equal(p.getDistance(q), 100));

		q = new Point(4, 13);
		assertTrue(PrecisionUtils.equal(p.getDistance(q), 10));
	}

	@Test
	public void test_getReverseCopy() {
		Point p0 = new Point(0, 0);
		Point p1 = new Point(1, 1);
		Point p2 = new Point(2, 2);
		Point p3 = new Point(3, 3);
		Point p4 = new Point(4, 4);

		assertTrue(Arrays.equals(new Point[] { p4, p3, p2, p1, p0 },
				Point.getReverseCopy(new Point[] { p0, p1, p2, p3, p4 })));

		assertTrue(Arrays.equals(new Point[] { p4, p3, p1, p0 },
				Point.getReverseCopy(new Point[] { p0, p1, p3, p4 })));
	}

	@Test
	public void test_max() {
		assertTrue(Point.max(new Point(1, 3), new Point(2, 6)).equals(
				new Point(2, 6)));
		assertTrue(Point.max(new Point(4, 8), new Point(2, 6)).equals(
				new Point(4, 8)));
		assertTrue(Point.max(new Point(4, 8), new Point(2, 10)).equals(
				new Point(4, 10)));
		assertTrue(Point.max(new Point(4, 12), new Point(6, 10)).equals(
				new Point(6, 12)));
	}

	@Test
	public void test_min() {
		assertTrue(Point.min(new Point(1, 3), new Point(2, 6)).equals(
				new Point(1, 3)));
		assertTrue(Point.min(new Point(4, 8), new Point(2, 6)).equals(
				new Point(2, 6)));
		assertTrue(Point.min(new Point(4, 8), new Point(2, 10)).equals(
				new Point(2, 8)));
		assertTrue(Point.min(new Point(4, 12), new Point(6, 10)).equals(
				new Point(4, 10)));
	}

	@Test
	public void test_negate() {
		assertTrue(new Point(1, 2).getNegated().equals(new Point(-1, -2)));
		assertTrue(new Point(-1, 2).getNegated().equals(new Point(1, -2)));
		assertTrue(new Point(1, -2).getNegated().equals(new Point(-1, 2)));
		assertTrue(new Point(-1, -2).getNegated().equals(new Point(1, 2)));
	}

	@Test
	public void test_scale() {
		Point p = new Point(1, 1);
		Point q = new Point(2, 2);

		assertTrue(p.getScaled(2).equals(q));
		assertTrue(q.getScaled(0.5).equals(p));

		q = new Point(3, 6);
		assertTrue(p.getScaled(3, 6).equals(q));
		assertTrue(q.getScaled(1f / 3f, 1f / 6f).equals(p));

		// scale around some other point
		Point c = new Point(10, 10);
		assertEquals(new Point(9, 8), q.getScaled(1d / 7d, 1d / 2d, c));
	}

	@Test
	public void test_setLocation() {
		Point p = new Point();
		p.setLocation(4711, 678);
		assertTrue(p.equals(4711, 678));
		p = new Point();
		p.setLocation(new Point(4711, 678));
		assertTrue(p.equals(4711, 678));
	}

	@Test
	public void test_setX() {
		Point p = new Point(4711, 678);
		p.setX(3);
		assertTrue(p.equals(3, 678));
	}

	@Test
	public void test_setY() {
		Point p = new Point(4711, 678);
		p.setY(3);
		assertTrue(p.equals(4711, 3));
	}

	@Test
	public void test_toString() {
		Point p = new Point();
		assertEquals("Point(0.0, 0.0)", p.toString());
	}

	@Test
	public void test_translate() {
		Point p1 = new Point(3, 6);
		Point px = new Point(4711, 567);
		p1.translate(px);
		assertTrue(p1.equals(4714, 573));
		p1.translate(px.negate());

		assertTrue(p1.getTranslated(3, 0).equals(new Point(6, 6)));
		assertTrue(p1.getTranslated(p1).equals(new Point(6, 12)));
		assertTrue(p1.getTranslated(new Dimension(10, 2)).equals(
				new Point(13, 8)));
	}

	@Test
	public void test_translate_static() {
		Point[] points = new Point[5];

		for (int i = 0; i < 5; i++) {
			points[i] = new Point(i, i);
		}

		Point.translate(points, 1, -1);

		for (int i = 0; i < 5; i++) {
			assertTrue(PrecisionUtils.equal(points[i].x, i + 1));
			assertTrue(PrecisionUtils.equal(points[i].y, i - 1));
		}
	}

	@Test
	public void test_transpose() {
		assertTrue(new Point(1, 2).getTransposed().equals(new Point(2, 1)));
		assertTrue(new Point(2, 1).getTransposed().equals(new Point(1, 2)));
		assertTrue(new Point(1, 1).getTransposed().equals(new Point(1, 1)));
	}

	@Test
	public void test_xy() {
		Point p = new Point(1.234, 2.987);
		assertTrue(PrecisionUtils.equal(p.x, 1.234));
		assertTrue(PrecisionUtils.equal(p.x, p.x()));
		assertTrue(PrecisionUtils.equal(p.y, 2.987));
		assertTrue(PrecisionUtils.equal(p.y, p.y()));
	}
}
