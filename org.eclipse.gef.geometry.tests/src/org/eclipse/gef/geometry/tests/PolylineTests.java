/*******************************************************************************
 * Copyright (c) 2008, 2016 IBM Corporation and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *    Alexander Shatalin (Borland) - initial API and implementation
 *    Alexander Nyssen (itemis) - Bugzilla #162082: testLinesIntersect()
 *    
 *******************************************************************************/
package org.eclipse.gef.geometry.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polyline;
import org.junit.Test;

public class PolylineTests {

	private static final Point[] POINTS = new Point[] { new Point(0, 0),
			new Point(1, 0), new Point(6, 5) };
	private static final Polyline POLYLINE = new Polyline(POINTS);

	@Test
	public void test_contains_with_Point() {
		// points are not on polyline
		assertFalse(POLYLINE.contains(new Point(9, 5)));
		assertFalse(POLYLINE.contains(new Point(1, 4)));

		// point are on polyline
		assertTrue(POLYLINE.contains(new Point(0, 0)));
		assertTrue(POLYLINE.contains(new Point(1, 0)));
		assertTrue(POLYLINE.contains(new Point(2, 1)));
	}

	@Test
	public void test_equals() {
		assertEquals(POLYLINE, POLYLINE);
		assertFalse(POLYLINE.equals((Object) null));
		assertFalse(POLYLINE.equals(new Line(1, 2, 3, 4)));

		List<Point> points = Arrays.asList(POINTS);
		Collections.reverse(points);
		assertEquals(POLYLINE, new Polyline(points.toArray(new Point[] {})));
	}

	@Test
	public void test_toBezier() {
		Line[] beziers = new Polyline(new double[] {}).toBezier();
		assertEquals(0, beziers.length);
	}

}
