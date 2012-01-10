/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
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

import org.eclipse.gef4.geometry.Dimension;
import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;
import org.junit.Test;

/**
 * Unit tests for {@link Point}.
 * 
 * @author anyssen
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

		org.eclipse.swt.graphics.Point swtPoint = new org.eclipse.swt.graphics.Point(
				10, 20);
		assertTrue(p1.equals(new Point(swtPoint)));
		assertTrue(new Point(10.0, 20.0).equals(new Point(swtPoint)));

	}

	@Test
	public void test_equals() {
		assertTrue(new Point(4, 7).equals(4, 7));
		assertFalse(new Point(3, 6).equals(3, 7));
		assertFalse(new Point(3, 6).equals(4, 6));
		assertTrue(new Point(1.0, 2.0).equals(new Point(1, 2)));

		// wrong type
		assertFalse(new Point(1, 2).equals(new org.eclipse.swt.graphics.Point(
				1, 2)));
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
	public void test_toSWT() {
		Point p = new Point();
		assertTrue(p.equals(new Point(p.toSWTPoint())));

		p = new Point(1, 2);
		assertTrue(p.equals(new Point(p.toSWTPoint())));
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
