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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.utils.PointListUtils;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;
import org.junit.Test;

public class PointListUtilsTests {

	@Test
	public void test_bounds() {
		Point[] points = new Point[9];
		for (int x = -4; x <= 4; x++) {
			double y = (1f / 3f) * x * x * x + 2f * x * x - (3f / 2f) * x - 1f
					/ 5f;
			points[x + 4] = new Point(x, y);
		}

		Rectangle bounds = PointListUtils.getBounds(points);

		assertTrue(bounds.getTopLeft().equals(-4, points[4].y));
		assertTrue(bounds.getBottomRight().equals(4, points[8].y));

		bounds = PointListUtils.getBounds(new Point[] {});

		assertTrue(bounds.getTopLeft().equals(new Point(0, 0)));
		assertTrue(bounds.getBottomRight().equals(new Point(0, 0)));
	}

	@Test
	public void test_copy() {
		Point[] points = new Point[10];
		for (int i = 0; i < 10; i++) {
			points[i] = new Point(i * i, i + i);
		}

		Point[] copy = PointListUtils.copy(points);

		for (int i = 0; i < 10; i++) {
			assertTrue(points[i].equals(copy[i]));
		}
	}

	@Test
	public void test_equals() {
		Point[] pPoints = new Point[5];
		ArrayList<Point> qPoints = new ArrayList<Point>();

		for (int i = 0; i < 5; i++) {
			pPoints[i] = new Point(i, i);
			qPoints.add(new Point(i, i));
		}

		assertTrue(PointListUtils.equals(pPoints,
				qPoints.toArray(new Point[] {})));

		qPoints.add(new Point());
		assertFalse(PointListUtils.equals(pPoints,
				qPoints.toArray(new Point[] {})));

		qPoints.remove(5);
		assertTrue(PointListUtils.equals(pPoints,
				qPoints.toArray(new Point[] {})));

		qPoints.get(2).setX(-1);
		assertFalse(PointListUtils.equals(pPoints,
				qPoints.toArray(new Point[] {})));
	}

	@Test
	public void test_toCoordinatesArray() {
		Point[] points = new Point[5];

		for (int i = 0; i < 5; i++) {
			points[i] = new Point(i, i);
		}

		double[] coords = PointListUtils.toCoordinatesArray(points);

		for (int i = 0; i < 10; i += 2) {
			assertTrue(PrecisionUtils.equal(points[i / 2].x, coords[i]));
			assertTrue(PrecisionUtils.equal(points[i / 2].y, coords[i + 1]));
		}
	}

	@Test
	public void test_toIntegerArray() {
		double[] doubles = new double[10];

		for (int i = 0; i < 10; i++) {
			doubles[i] = (double) i / 2f;
		}

		int[] ints = PointListUtils.toIntegerArray(doubles);

		for (int i = 0; i < 10; i++) {
			assertTrue(PrecisionUtils.equal(ints[i], (int) doubles[i]));
		}
	}

	@Test
	public void test_toSegmentsArray() {
		Point[] points = new Point[5];

		for (int i = 0; i < points.length; i++) {
			points[i] = new Point(i, i);
		}

		Line[] segments = PointListUtils.toSegmentsArray(points, false);
		assertTrue(PrecisionUtils.equal(segments.length, points.length - 1));

		for (int i = 0; i < segments.length; i++) {
			assertTrue(segments[i].getP1().equals(points[i]));
			assertTrue(segments[i].getP2().equals(points[i + 1]));
		}

		segments = PointListUtils.toSegmentsArray(points, true);
		assertTrue(PrecisionUtils.equal(segments.length, points.length));

		for (int i = 0; i < segments.length; i++) {
			assertTrue(segments[i].getP1().equals(points[i]));
			if (i == points.length - 1) {
				assertTrue(segments[i].getP2().equals(points[0]));
			} else {
				assertTrue(segments[i].getP2().equals(points[i + 1]));
			}
		}
	}

	@Test
	public void test_translate() {
		Point[] points = new Point[5];

		for (int i = 0; i < 5; i++) {
			points[i] = new Point(i, i);
		}

		PointListUtils.translate(points, 1, -1);

		for (int i = 0; i < 5; i++) {
			assertTrue(PrecisionUtils.equal(points[i].x, i + 1));
			assertTrue(PrecisionUtils.equal(points[i].y, i - 1));
		}
	}

	@Test
	public void test_getConvexHull() {
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

		Point[] convexHull = PointListUtils.getConvexHull(points);

		assertTrue(new Polygon(PointListUtils.toPointsArray(new double[] {
				-0.161920957418085, -0.4055339716426413, -0.4404289572876217,
				-0.2894855991839297, -0.4907368011686362, 0.1865826865533206,
				-0.3521487911717489, 0.4352656197131292, 0.05054295812784038,
				0.4754929463150845, 0.4932166845474547, 0.4928094162538735,
				0.4916198379282093, -0.345391701297268, 0.4823896228171788,
				-0.4776170002088109 })).equals(new Polygon(convexHull)));

		convexHull = PointListUtils.getConvexHull(new Point[] {
				new Point(0.0, 75.0),
				new Point(0.3333333333333333, 0.9411910020934172),
				new Point(0.6666666666666666, -60.0), new Point(1.0, -60.0) });

		assertEquals(
				new Polygon(PointListUtils.toPointsArray(new double[] { 0, 75,
						0.3333333333333333, 0.9411910020934172,
						0.6666666666666666, -60, 1, -60 })), new Polygon(
						convexHull));

		convexHull = PointListUtils.getConvexHull(new Point[] {
				new Point(0.0, -1.8277675577160887E-4),
				new Point(0.3333333333333333, -1.1294769632887472E-4),
				new Point(0.6666666666666666, -4.311817922240293E-5),
				new Point(1.0, 2.671179560675793E-5) });

		// TODO
		assertTrue(convexHull.length == 3);
	}
}
