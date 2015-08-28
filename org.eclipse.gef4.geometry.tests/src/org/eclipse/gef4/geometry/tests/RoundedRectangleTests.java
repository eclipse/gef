/*******************************************************************************
 * Copyright (c) 2012, 2014 itemis AG and others.
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
import static org.junit.Assert.assertTrue;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.Arc;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;
import org.eclipse.gef4.internal.geometry.utils.PrecisionUtils;
import org.junit.Before;
import org.junit.Test;

public class RoundedRectangleTests {

	double x = 1, y = 2, w = 5, h = 6, aw = 1, ah = 2;
	RoundedRectangle rr;

	private void check_Point_containment(IGeometry g) {
		assertTrue(g.contains(new Point(3.5, 5)));
		assertTrue(g.contains(new Point(1.5, 5)));
		assertTrue(g.contains(new Point(1, 5)));
		assertTrue(g.contains(new Point(5.5, 5)));
		// TODO: next test is commented out because the AWT Path does not
		// recognize points on the right and bottom sides
		// assertTrue(g.contains(new Point(6, 5)));
		assertTrue(g.contains(new Point(3.5, 2.5)));
		assertTrue(g.contains(new Point(3.5, 2)));
		assertTrue(g.contains(new Point(3.5, 7.5)));
		// TODO: next test is commented out because the AWT Path does not
		// recognize points on the right and bottom sides
		// assertTrue(g.contains(new Point(3.5, 8)));
		assertTrue(g.contains(new Point(1.5, 3.5)));
		assertTrue(g.contains(new Point(5.5, 3.5)));
		assertTrue(g.contains(new Point(1.5, 6.5)));
		assertTrue(g.contains(new Point(5.5, 6.5)));
		assertFalse(g.contains(new Point(0, 0)));
		assertFalse(g.contains(new Point(4, 0)));
		assertFalse(g.contains(new Point(7, 0)));
		assertFalse(g.contains(new Point(0, 5)));
		assertFalse(g.contains(new Point(7, 5)));
		assertFalse(g.contains(new Point(0, 9)));
		assertFalse(g.contains(new Point(4, 9)));
		assertFalse(g.contains(new Point(7, 9)));
		assertFalse(g.contains(new Point(1, 2)));
		assertFalse(g.contains(new Point(6, 2)));
		assertFalse(g.contains(new Point(1, 8)));
		assertFalse(g.contains(new Point(6, 8)));
	}

	private void check_values_with_getters(RoundedRectangle r, double px,
			double py, double pw, double ph, double paw, double pah) {
		assertTrue(PrecisionUtils.equal(px, r.getX()));
		assertTrue(PrecisionUtils.equal(py, r.getY()));
		assertTrue(PrecisionUtils.equal(pw, r.getWidth()));
		assertTrue(PrecisionUtils.equal(ph, r.getHeight()));
		assertTrue(PrecisionUtils.equal(paw, r.getArcWidth()));
		assertTrue(PrecisionUtils.equal(pah, r.getArcHeight()));
		assertEquals(new Point(px, py), r.getLocation());
		assertEquals(new Rectangle(px, py, pw, ph), r.getBounds());

		// generated arcs have double width and height as specified so that the
		// underlying ellipse fits into the respective rectangle
		assertEquals(
				new Arc(px + pw - 2 * paw, py, 2 * paw, 2 * pah,
						Angle.fromDeg(0), Angle.fromDeg(90)),
				r.getTopRightArc());
		assertEquals(new Arc(px, py, 2 * paw, 2 * pah, Angle.fromDeg(90),
				Angle.fromDeg(90)), r.getTopLeftArc());
		assertEquals(
				new Arc(px, py + ph - 2 * pah, 2 * paw, 2 * pah,
						Angle.fromDeg(180), Angle.fromDeg(90)),
				r.getBottomLeftArc());
		assertEquals(
				new Arc(px + pw - 2 * paw, py + ph - 2 * pah, 2 * paw, 2 * pah,
						Angle.fromDeg(270), Angle.fromDeg(90)),
				r.getBottomRightArc());

		assertEquals(new Line(px + paw, py, px + pw - paw, py), r.getTop());
		assertEquals(new Line(px + paw, py + ph, px + pw - paw, py + ph),
				r.getBottom());
		assertEquals(new Line(px, py + pah, px, py + ph - pah), r.getLeft());
		assertEquals(new Line(px + pw, py + pah, px + pw, py + ph - pah),
				r.getRight());
	}

	@Before
	public void setUp() {
		rr = new RoundedRectangle(x, y, w, h, aw, ah);
	}

	@Test
	public void test_contains_Point() {
		check_Point_containment(rr);
	}

	@Test
	public void test_contains_shape() {
		// translate it by some values and test that the translated versions are
		// not contained
		for (double tx : new double[] { -1, 1 }) {
			for (double ty : new double[] { -1, 1 }) {
				assertFalse(rr.contains(rr.getTranslated(tx, ty)));
			}
		}

		// scale it down by some values and test that the smaller versions are
		// contained
		for (double s = 1; s > 0; s -= 0.1) {
			assertTrue(rr.contains(rr.getScaled(s)));
		}

		// scale it up by some values and test that the greater versions are not
		// contained
		for (double s = 1.1; s < 2; s += 0.1) {
			assertFalse(rr.contains(rr.getScaled(s)));
		}
	}

	@Test
	public void test_equals() {
		assertEquals(rr,
				new RoundedRectangle(new Rectangle(x, y, w, h), aw, ah));
		assertEquals(rr, rr.getCopy());
		assertFalse(rr.equals(null));
		assertFalse(rr.equals(new Point()));

		assertFalse(rr.equals(new RoundedRectangle(x + 10, y, w, h, aw, ah)));
		assertFalse(rr.equals(new RoundedRectangle(x, y + 10, w, h, aw, ah)));
		assertFalse(rr.equals(new RoundedRectangle(x, y, w + 10, h, aw, ah)));
		assertFalse(rr.equals(new RoundedRectangle(x, y, w, h + 10, aw, ah)));
		assertFalse(rr.equals(new RoundedRectangle(x, y, w, h, aw + 10, ah)));
		assertFalse(rr.equals(new RoundedRectangle(x, y, w, h, aw, ah + 10)));
	}

	@Test
	public void test_getOutline() {
		// coherence with getOutlineSegments
		ICurve[] outlineSegments = rr.getOutlineSegments();
		BezierCurve[] outlineBeziers = rr.getOutline().toBezier();
		assertEquals(outlineSegments.length, outlineBeziers.length);
		for (int i = 0; i < 8; i++) {
			assertEquals(outlineSegments[i], outlineBeziers[i]);
		}
	}

	@Test
	public void test_getOutlineSegments() {
		ICurve[] outlineSegments = rr.getOutlineSegments();
		assertEquals(8, outlineSegments.length);

		// consecutive
		for (int i = 0; i < 7; i++) {
			assertEquals(outlineSegments[i].getP2(),
					outlineSegments[i + 1].getP1());
		}
		assertEquals(outlineSegments[7].getP2(), outlineSegments[0].getP1());

		// position
		assertEquals(new Point(x + w, y + ah), outlineSegments[0].getP1());
		assertEquals(new Point(x + w - aw, y), outlineSegments[1].getP1());
		assertEquals(new Point(x + aw, y), outlineSegments[2].getP1());
		assertEquals(new Point(x, y + ah), outlineSegments[3].getP1());
		assertEquals(new Point(x, y + h - ah), outlineSegments[4].getP1());
		assertEquals(new Point(x + aw, y + h), outlineSegments[5].getP1());
		assertEquals(new Point(x + w - aw, y + h), outlineSegments[6].getP1());
		assertEquals(new Point(x + w, y + h - ah), outlineSegments[7].getP1());
	}

	@Test
	public void test_getters() {
		check_values_with_getters(rr, x, y, w, h, aw, ah);
	}

	@Test
	public void test_setters() {
		// TODO: change values and test if the changes are applied correctly
		RoundedRectangle rrCopy = rr.getCopy();

		double nx = 9, ny = 8, nw = 7, nh = 6, naw = 5, nah = 4;

		rrCopy.setX(nx);
		rrCopy.setY(ny);
		rrCopy.setWidth(nw);
		rrCopy.setHeight(nh);
		rrCopy.setArcWidth(naw);
		rrCopy.setArcHeight(nah);

		check_values_with_getters(rrCopy, nx, ny, nw, nh, naw, nah);
		check_values_with_getters(rr, x, y, w, h, aw, ah);
	}

	@Test
	public void test_toPath() {
		check_Point_containment(rr.toPath());
	}

	@Test
	public void test_toString() {
		assertEquals("RoundedRectangle(" + x + ", " + y + ", " + w + ", " + h
				+ ", " + aw + ", " + ah + ")", rr.toString());
	}

}
