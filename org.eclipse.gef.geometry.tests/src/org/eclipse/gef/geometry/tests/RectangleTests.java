/*******************************************************************************
 * Copyright (c) 2006, 2016 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Path.Segment;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polygon;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.junit.Test;

/**
 * Unit tests for {@link Rectangle}.
 *
 * @author sshaw
 * @author ahunter
 * @author anyssen
 * @author mwienand
 *
 */
public class RectangleTests {

	private interface IAction {
		void action(Rectangle rect, Point tl, Point br);
	}

	private static final double PRECISION_FRACTION = TestUtils
			.getPrecisionFraction();

	private static final double RECOGNIZABLE_FRACTION = PRECISION_FRACTION
			+ PRECISION_FRACTION / 10;

	private static final double UNRECOGNIZABLE_FRACTION = PRECISION_FRACTION
			- PRECISION_FRACTION / 10;

	private void forRectangles(IAction action) {
		for (double x1 = -2; x1 <= 2; x1 += 0.4) {
			for (double y1 = -2; y1 <= 2; y1 += 0.4) {
				Point p1 = new Point(x1, y1);
				for (double x2 = -2; x2 <= 2; x2 += 0.4) {
					for (double y2 = -2; y2 <= 2; y2 += 0.4) {
						Point p2 = new Point(x2, y2);
						action.action(new Rectangle(p1, p2), Point.min(p1, p2),
								Point.max(p1, p2));
					}
				}
			}
		}
	}

	private Polygon fromRectangle(double x, double y, double w, double h) {
		return new Polygon(x, y, x + w, y, x + w, y + h, x, y + h);
	}

	@Test
	public void test_clone() {
		Rectangle preciseRect = new Rectangle(-9.486614173228347,
				-34.431496062992125, 41.99055118110236, 25.92755905511811);
		Rectangle clone = (Rectangle) preciseRect.clone();
		assertTrue(preciseRect.equals(clone));
		assertFalse(preciseRect == clone);
	}

	@Test
	public void test_contains_Point() {
		Rectangle preciseRect = new Rectangle(-9.486614173228347,
				-34.431496062992125, 41.99055118110236, 25.92755905511811);

		// test border points
		Point topLeft = preciseRect.getTopLeft();
		assertTrue(preciseRect.contains(topLeft));
		assertTrue(preciseRect.contains(topLeft.x, topLeft.y));
		assertFalse(preciseRect.contains(topLeft.getTranslated(
				-RECOGNIZABLE_FRACTION, -RECOGNIZABLE_FRACTION)));

		Point top = preciseRect.getTop();
		assertTrue(preciseRect.contains(top));
		assertTrue(preciseRect.contains(top.x, top.y));
		assertFalse(preciseRect
				.contains(top.getTranslated(0, -RECOGNIZABLE_FRACTION)));

		Point topRight = preciseRect.getTopRight();
		assertTrue(preciseRect.contains(topRight));
		assertTrue(preciseRect.contains(topRight.x, topRight.y));
		assertFalse(preciseRect.contains(topRight
				.getTranslated(RECOGNIZABLE_FRACTION, -RECOGNIZABLE_FRACTION)));

		Point left = preciseRect.getLeft();
		assertTrue(preciseRect.contains(left));
		assertTrue(preciseRect.contains(left.x, left.y));
		assertFalse(preciseRect
				.contains(left.getTranslated(-RECOGNIZABLE_FRACTION, 0)));

		Point right = preciseRect.getRight();
		assertTrue(preciseRect.contains(right));
		assertTrue(preciseRect.contains(right.x, right.y));
		assertFalse(preciseRect
				.contains(right.getTranslated(RECOGNIZABLE_FRACTION, 0)));

		Point bottomLeft = preciseRect.getBottomLeft();
		assertTrue(preciseRect.contains(bottomLeft));
		assertTrue(preciseRect.contains(bottomLeft.x, bottomLeft.y));
		assertFalse(preciseRect.contains(bottomLeft
				.getTranslated(-RECOGNIZABLE_FRACTION, RECOGNIZABLE_FRACTION)));

		Point bottom = preciseRect.getBottom();
		assertTrue(preciseRect.contains(bottom));
		assertTrue(preciseRect.contains(bottom.x, bottom.y));
		assertFalse(preciseRect
				.contains(bottom.getTranslated(0, RECOGNIZABLE_FRACTION)));

		Point bottomRight = preciseRect.getBottomRight();
		assertTrue(preciseRect.contains(bottomRight));
		assertTrue(preciseRect.contains(bottomRight.x, bottomRight.y));
		assertFalse(preciseRect.contains(bottomRight
				.getTranslated(RECOGNIZABLE_FRACTION, RECOGNIZABLE_FRACTION)));
	}

	@Test
	public void test_contains_Rectangle() {
		Rectangle preciseRect = new Rectangle(-9.486614173228347,
				-34.431496062992125, 41.99055118110236, 25.92755905511811);

		// test self containment
		assertTrue(preciseRect.contains(preciseRect));
		assertFalse(preciseRect.contains(preciseRect
				.getExpanded(RECOGNIZABLE_FRACTION, RECOGNIZABLE_FRACTION)));

		// test precision tolerance, therefore increment by an amount not
		// 'recognizable'

		Rectangle unrecognizableExpanded = preciseRect.getExpanded(
				UNRECOGNIZABLE_FRACTION, UNRECOGNIZABLE_FRACTION, 0, 0);
		Rectangle unrecognizableShrinked = preciseRect.getShrinked(0, 0,
				UNRECOGNIZABLE_FRACTION, UNRECOGNIZABLE_FRACTION);

		// contains should not recognized the changes
		assertTrue(preciseRect.contains(unrecognizableExpanded));
		assertTrue(preciseRect.contains(unrecognizableShrinked));
		assertTrue(unrecognizableExpanded.contains(preciseRect));
		assertTrue(unrecognizableShrinked.contains(preciseRect));
		assertTrue(unrecognizableExpanded.contains(unrecognizableShrinked));

		// now increment by an amount 'recognizable'
		Rectangle recognizableExpanded = preciseRect.getExpanded(
				RECOGNIZABLE_FRACTION, RECOGNIZABLE_FRACTION, 0, 0);
		Rectangle recognizableShrinked = preciseRect.getShrinked(0, 0,
				RECOGNIZABLE_FRACTION, RECOGNIZABLE_FRACTION);

		// contains should now recognized the changes
		assertFalse(preciseRect.contains(recognizableExpanded));
		assertTrue(recognizableExpanded.contains(preciseRect));
		assertFalse(recognizableShrinked.contains(preciseRect));
		assertFalse(recognizableShrinked.contains(recognizableExpanded));

		// Regression test for a contains() bug that caused false positives for
		// a "containing" Rectangle with smaller x and y coordinates and greater
		// width and height as the "contained" one.
		assertFalse(new Rectangle(0, 0, 100, 100)
				.contains(new Rectangle(200, 200, 1, 1)));
	}

	@Test
	public void test_equals() {
		Rectangle preciseRect = new Rectangle(-9.486614173228347,
				-34.431496062992125, 41.99055118110236, 25.92755905511811);

		assertTrue(preciseRect.equals(preciseRect));
		assertTrue(preciseRect.equals(preciseRect.getCopy()));
		assertTrue(preciseRect.equals(preciseRect.clone()));
		assertFalse(preciseRect.equals(null));
		assertFalse(preciseRect.equals(new Object()));

		// test precision tolerance, therefore increment by an amount not
		// 'recognizable'
		Rectangle unrecognizableExpanded = preciseRect.getExpanded(
				UNRECOGNIZABLE_FRACTION, UNRECOGNIZABLE_FRACTION, 0, 0);
		Rectangle unrecognizableShrinked = preciseRect.getShrinked(0, 0,
				UNRECOGNIZABLE_FRACTION, UNRECOGNIZABLE_FRACTION);
		// equals should not recognize the changes
		assertTrue(preciseRect.equals(unrecognizableExpanded));
		assertTrue(preciseRect.equals(unrecognizableShrinked));

		// increment by an amount 'recognizable'
		Rectangle recognizableExpanded = preciseRect.getExpanded(
				RECOGNIZABLE_FRACTION, RECOGNIZABLE_FRACTION, 0, 0);
		Rectangle recognizableShrinked = preciseRect.getShrinked(0, 0,
				RECOGNIZABLE_FRACTION, RECOGNIZABLE_FRACTION);
		// equals should now recognize the changes
		assertFalse(preciseRect.equals(recognizableExpanded));
		assertFalse(preciseRect.equals(recognizableShrinked));
		assertFalse(recognizableExpanded.equals(recognizableShrinked));
	}

	@Test
	public void test_getBounds() {
		forRectangles(new IAction() {
			@Override
			public void action(Rectangle rect, Point tl, Point br) {
				assertEquals(rect, rect.getBounds());
			}
		});
	}

	@Test
	public void test_getCopy() {
		Rectangle preciseRect = new Rectangle(-9.486614173228347,
				-34.431496062992125, 41.99055118110236, 25.92755905511811);
		Rectangle copy = preciseRect.getCopy();
		assertTrue(preciseRect.equals(copy));
		assertFalse(preciseRect == copy);
	}

	@Test
	public void test_getIntersected() {
		Rectangle r1 = new Rectangle(0, 0, 10, 10);

		// check no intersection, containment, partial intersection and identity

		assertEquals(new Rectangle(),
				r1.getIntersected(new Rectangle(-20, -20, 5, 5)));
		assertEquals(new Rectangle(5, 5, 5, 5),
				r1.getIntersected(new Rectangle(5, 5, 10, 10)));
		assertEquals(new Rectangle(2, 2, 6, 6),
				r1.getIntersected(new Rectangle(2, 2, 6, 6)));
		assertEquals(r1, r1.getIntersected(r1));
	}

	@Test
	public void test_getLocation() {
		Rectangle r1 = new Rectangle(5, 10, 1, 2);

		assertEquals(new Point(5, 10), r1.getLocation());
	}

	@Test
	public void test_getRotatedCCW() {
		forRectangles(new IAction() {
			@Override
			public void action(Rectangle rect, Point tl, Point br) {
				assertEquals(
						fromRectangle(tl.x, tl.y - rect.getWidth(),
								rect.getHeight(), rect.getWidth()),
						rect.getRotatedCCW(Angle.fromDeg(90),
								rect.getLocation()));
			}
		});
	}

	@Test
	public void test_getRotatedCW() {
		forRectangles(new IAction() {
			@Override
			public void action(Rectangle rect, Point tl, Point br) {
				Polygon expected = fromRectangle(tl.x - rect.getHeight(), tl.y,
						rect.getHeight(), rect.getWidth());
				Polygon rotatedCW = rect.getRotatedCW(Angle.fromDeg(90),
						rect.getLocation());

				// DEBUG
				// if (!expected.equals(rotatedCW))
				// throw new IllegalStateException("");

				assertEquals(expected, rotatedCW);
			}
		});
	}

	@Test
	public void test_getScaled() {
		Rectangle rect = new Rectangle(-9.486614173228347, -34.431496062992125,
				41.99055118110236, 25.92755905511811)
						.getScaled(26.458333333333332)
						.getScaled(1.0 / 26.458333333333332);

		assertTrue(PrecisionUtils.equal(-9.486614173228347, rect.getX()));
		assertTrue(PrecisionUtils.equal(-34.431496062992125, rect.getY()));
		assertTrue(PrecisionUtils.equal(41.99055118110236, rect.getWidth()));
		assertTrue(PrecisionUtils.equal(25.92755905511811, rect.getHeight()));

		rect = new Rectangle(-9.486614173228347, -34.431496062992125,
				2 * 9.486614173228347, 34.431496062992125).getScaled(2, 0);

		assertTrue(PrecisionUtils.equal(2 * -9.486614173228347, rect.getX()));
		assertTrue(
				PrecisionUtils.equal(0.5 * -34.431496062992125, rect.getY()));
		assertTrue(
				PrecisionUtils.equal(4 * 9.486614173228347, rect.getWidth()));
		assertTrue(PrecisionUtils.equal(0, rect.getHeight()));

		// TODO: is this the desired behavior?
		// assertTrue(PrecisionUtils.equal(-9.486614173228347, rect.getX()));
		// assertTrue(PrecisionUtils.equal(-34.431496062992125, rect.getY()));
		// assertTrue(PrecisionUtils.equal(2 * 41.99055118110236,
		// rect.getWidth()));
		// assertTrue(PrecisionUtils.equal(0, rect.getHeight()));
	}

	@Test
	public void test_getSegments() {
		forRectangles(new IAction() {
			@Override
			public void action(Rectangle rect, Point tl, Point br) {
				Line[] segments = rect.getOutlineSegments();
				// segments are top, right, bottom, left. in order.
				Point tr = tl.getTranslated(br.x - tl.x, 0);
				Point bl = tl.getTranslated(0, br.y - tl.y);
				assertEquals(new Line(tl, tr), segments[0]);
				assertEquals(new Line(tr, br), segments[1]);
				assertEquals(new Line(br, bl), segments[2]);
				assertEquals(new Line(bl, tl), segments[3]);
			}
		});
	}

	@Test
	public void test_getSize() {
		forRectangles(new IAction() {
			@Override
			public void action(Rectangle rect, Point tl, Point br) {
				assertEquals(new Dimension(br.x - tl.x, br.y - tl.y),
						rect.getSize());
			}
		});
	}

	@Test
	public void test_getTranslated() {
		Rectangle r1 = new Rectangle(1, 2, 3, 4);

		assertEquals(new Rectangle(0, 3, 3, 4),
				r1.getTranslated(new Point(-1, 1)));
		assertEquals(new Rectangle(0, 3, 3, 4), r1.getTranslated(-1, 1));
	}

	@Test
	public void test_getTransposed() {
		forRectangles(new IAction() {
			@Override
			public void action(Rectangle rect, Point tl, Point br) {
				assertEquals(
						new Rectangle(tl.y, tl.x, br.y - tl.y, br.x - tl.x),
						rect.getTransposed());
			}
		});
	}

	@Test
	public void test_getUnioned_with_Point() {
		Rectangle r1 = new Rectangle(1, 2, 2, 2);

		// check union with coordinates left, on-left, center, on-right, right
		// and above, on-top, center, on-bottom, below, respectively

		assertEquals(new Rectangle(0, 1, 3, 3), r1.getUnioned(new Point(0, 1)));
		assertEquals(new Rectangle(0, 2, 3, 2), r1.getUnioned(new Point(0, 2)));
		assertEquals(new Rectangle(0, 2, 3, 2), r1.getUnioned(new Point(0, 3)));
		assertEquals(new Rectangle(0, 2, 3, 2), r1.getUnioned(new Point(0, 4)));
		assertEquals(new Rectangle(0, 2, 3, 3), r1.getUnioned(new Point(0, 5)));

		assertEquals(new Rectangle(1, 1, 2, 3), r1.getUnioned(new Point(1, 1)));
		assertEquals(new Rectangle(1, 2, 2, 2), r1.getUnioned(new Point(1, 2)));
		assertEquals(new Rectangle(1, 2, 2, 2), r1.getUnioned(new Point(1, 3)));
		assertEquals(new Rectangle(1, 2, 2, 2), r1.getUnioned(new Point(1, 4)));
		assertEquals(new Rectangle(1, 2, 2, 3), r1.getUnioned(new Point(1, 5)));

		assertEquals(new Rectangle(1, 1, 2, 3), r1.getUnioned(new Point(2, 1)));
		assertEquals(new Rectangle(1, 2, 2, 2), r1.getUnioned(new Point(2, 2)));
		assertEquals(new Rectangle(1, 2, 2, 2), r1.getUnioned(new Point(2, 3)));
		assertEquals(new Rectangle(1, 2, 2, 2), r1.getUnioned(new Point(2, 4)));
		assertEquals(new Rectangle(1, 2, 2, 3), r1.getUnioned(new Point(2, 5)));

		assertEquals(new Rectangle(1, 1, 2, 3), r1.getUnioned(new Point(3, 1)));
		assertEquals(new Rectangle(1, 2, 2, 2), r1.getUnioned(new Point(3, 2)));
		assertEquals(new Rectangle(1, 2, 2, 2), r1.getUnioned(new Point(3, 3)));
		assertEquals(new Rectangle(1, 2, 2, 2), r1.getUnioned(new Point(3, 4)));
		assertEquals(new Rectangle(1, 2, 2, 3), r1.getUnioned(new Point(3, 5)));

		assertEquals(new Rectangle(1, 1, 3, 3), r1.getUnioned(new Point(4, 1)));
		assertEquals(new Rectangle(1, 2, 3, 2), r1.getUnioned(new Point(4, 2)));
		assertEquals(new Rectangle(1, 2, 3, 2), r1.getUnioned(new Point(4, 3)));
		assertEquals(new Rectangle(1, 2, 3, 2), r1.getUnioned(new Point(4, 4)));
		assertEquals(new Rectangle(1, 2, 3, 3), r1.getUnioned(new Point(4, 5)));
	}

	@Test
	public void test_getUnioned_with_Rectangle() {
		Rectangle r1 = new Rectangle(1, 2, 2, 2);

		// containment
		Rectangle contained = new Rectangle(1.5, 2.5, 1, 1);
		assertEquals(r1, r1.getUnioned(contained));
		Rectangle contains = new Rectangle(0, 1, 4, 4);
		assertEquals(contains, r1.getUnioned(contains));

		// corners
		Rectangle tl = new Rectangle(0, 1, 2, 2);
		assertEquals(new Rectangle(0, 1, 3, 3), r1.getUnioned(tl));
		Rectangle tr = new Rectangle(2, 1, 2, 2);
		assertEquals(new Rectangle(1, 1, 3, 3), r1.getUnioned(tr));
		Rectangle bl = new Rectangle(0, 3, 2, 2);
		assertEquals(new Rectangle(0, 2, 3, 3), r1.getUnioned(bl));
		Rectangle br = new Rectangle(2, 3, 2, 2);
		assertEquals(new Rectangle(1, 2, 3, 3), r1.getUnioned(br));
	}

	@Test
	public void test_intersects_with_Line() {
		Rectangle r1 = new Rectangle(-5, -5, 10, 10);

		for (Line seg : r1.getOutlineSegments()) {
			assertTrue(r1.touches(seg));
		}

		assertTrue(r1.touches(new Line(r1.getTopLeft(), r1.getBottomRight())));
		assertTrue(r1.touches(new Line(r1.getTop(), r1.getBottom())));

		assertTrue(r1.touches(new Line(-10, 0, 10, 0)));
		assertFalse(r1.touches(new Line(-10, 0, -6, 0)));
		assertFalse(r1.touches(new Line(0, -10, 0, -6)));
		assertFalse(r1.touches(new Line(10, 0, 6, 0)));
		assertFalse(r1.touches(new Line(0, 10, 0, 6)));
	}

	@Test
	public void test_intersects_with_Rectangle() {
		assertTrue(new Rectangle(0, 0, 100, 100)
				.touches(new Rectangle(0, 0, 100, 100)));
		assertTrue(new Rectangle(0, 0, 100, 100)
				.touches(new Rectangle(50, 50, 100, 100)));
		assertTrue(new Rectangle(0, 0, 100, 100)
				.touches(new Rectangle(100, 100, 100, 100)));
		assertTrue(new Rectangle(0, 0, 100, 100)
				.touches(new Rectangle(-100, -100, 100, 100)));
		assertTrue(new Rectangle(0, 0, 100, 100)
				.touches(new Rectangle(-50, 0, 100, 100)));
		assertTrue(new Rectangle(0, 0, 100, 100)
				.touches(new Rectangle(-100, 0, 100, 100)));
		assertTrue(new Rectangle(0, 0, 100, 100)
				.touches(new Rectangle(50, 0, 100, 100)));
		assertTrue(new Rectangle(0, 0, 100, 100)
				.touches(new Rectangle(100, 0, 100, 100)));
		assertTrue(new Rectangle(0, 0, 100, 100)
				.touches(new Rectangle(0, -50, 100, 100)));
		assertTrue(new Rectangle(0, 0, 100, 100)
				.touches(new Rectangle(0, -100, 100, 100)));
		assertTrue(new Rectangle(0, 0, 100, 100)
				.touches(new Rectangle(0, 50, 100, 100)));
		assertTrue(new Rectangle(0, 0, 100, 100)
				.touches(new Rectangle(0, 100, 100, 100)));
	}

	@Test
	public void test_isEmpty() {
		assertFalse(new Rectangle(0, 0, 10, 10).isEmpty());
		assertTrue(new Rectangle(0, 0, 0, 10).isEmpty());
		assertTrue(new Rectangle(0, 0, 10, 0).isEmpty());
		assertTrue(new Rectangle(0, 0, -10, 10).isEmpty());
		assertTrue(new Rectangle(0, 0, 10, -10).isEmpty());
		assertTrue(new Rectangle(0, 0, UNRECOGNIZABLE_FRACTION, 10).isEmpty());
		assertTrue(new Rectangle(0, 0, 10, UNRECOGNIZABLE_FRACTION).isEmpty());
		assertFalse(new Rectangle(0, 0, RECOGNIZABLE_FRACTION, 10).isEmpty());
		assertFalse(new Rectangle(0, 0, 10, RECOGNIZABLE_FRACTION).isEmpty());
	}

	@Test
	public void test_new() {
		Point topLeft = new Point(0, 0);
		Point topRight = new Point(10, 0);
		Point bottomLeft = new Point(0, 10);
		Point bottomRight = new Point(10, 10);

		Rectangle rect1 = new Rectangle(topLeft, bottomRight);
		Rectangle rect2 = new Rectangle(topRight, bottomLeft);
		assertEquals(rect1, rect2);

		Dimension size = new Dimension(10, 10);
		Rectangle rect3 = new Rectangle(topLeft, size);
		assertEquals(rect2, rect3);

		Rectangle rect4 = new Rectangle(0, 0, 10, 10);
		assertEquals(rect3, rect4);

		// negative width/height?
		assertEquals(new Rectangle(), new Rectangle(0, 0, -10, -10));
		assertEquals(new Rectangle(5, 5, 0, 10), new Rectangle(5, 5, -10, 10));
		assertEquals(new Rectangle(5, 5, 10, 0), new Rectangle(5, 5, 10, -10));

		assertEquals(new Rectangle(),
				new Rectangle(new Rectangle(0, 0, -10, -10)));
		assertEquals(new Rectangle(5, 5, 0, 10),
				new Rectangle(new Rectangle(5, 5, -10, 10)));
		assertEquals(new Rectangle(5, 5, 10, 0),
				new Rectangle(new Rectangle(5, 5, 10, -10)));

		assertEquals(new Rectangle(),
				new Rectangle(new Point(0, 0), new Dimension(-10, -10)));
		assertEquals(new Rectangle(5, 5, 0, 10),
				new Rectangle(new Point(5, 5), new Dimension(-10, 10)));
		assertEquals(new Rectangle(5, 5, 10, 0),
				new Rectangle(new Point(5, 5), new Dimension(10, -10)));

		assertEquals(new Rectangle(-10, -10, 10, 10),
				new Rectangle(new Point(0, 0), new Point(-10, -10)));
		assertEquals(new Rectangle(-10, 5, 15, 5),
				new Rectangle(new Point(5, 5), new Point(-10, 10)));
		assertEquals(new Rectangle(5, -10, 5, 15),
				new Rectangle(new Point(5, 5), new Point(10, -10)));
	}

	@Test
	public void test_scale() {
		Rectangle rect = new Rectangle(-9.486614173228347, -34.431496062992125,
				41.99055118110236, 25.92755905511811);
		rect.scale(26.458333333333332);
		rect.scale(1.0 / 26.458333333333332);

		assertTrue(PrecisionUtils.equal(-9.486614173228347, rect.getX()));
		assertTrue(PrecisionUtils.equal(-34.431496062992125, rect.getY()));
		assertTrue(PrecisionUtils.equal(41.99055118110236, rect.getWidth()));
		assertTrue(PrecisionUtils.equal(25.92755905511811, rect.getHeight()));
	}

	@Test
	public void test_setBounds() {
		Rectangle r = new Rectangle();

		r.setBounds(new Rectangle(10, 10, 10, 10));
		assertEquals(new Point(10, 10), r.getTopLeft());
		assertEquals(new Point(20, 20), r.getBottomRight());

		r.setBounds(new Point(-5, -5), new Dimension(5, 5));
		assertEquals(new Point(-5, -5), r.getTopLeft());
		assertEquals(new Point(), r.getBottomRight());

		r.setBounds(3, 2, 1, 0);
		assertEquals(new Point(3, 2), r.getTopLeft());
		assertEquals(new Point(4, 2), r.getBottomRight());
	}

	@Test
	public void test_setSize() {
		Rectangle r = new Rectangle();

		r.setSize(new Dimension(10, 20));
		assertEquals(new Point(), r.getTopLeft());
		assertEquals(new Point(10, 20), r.getBottomRight());

		r.setSize(5, -10);
		assertEquals(new Point(), r.getTopLeft());
		assertEquals(new Point(5, 0), r.getBottomRight());

		r.setSize(-5, 10);
		assertEquals(new Point(), r.getTopLeft());
		assertEquals(new Point(0, 10), r.getBottomRight());
	}

	@Test
	public void test_setters() {
		Rectangle r = new Rectangle();

		r.setX(10);
		assertTrue(PrecisionUtils.equal(10, r.getX()));

		r.setY(1);
		assertTrue(PrecisionUtils.equal(1, r.getY()));

		r.setWidth(5);
		assertTrue(PrecisionUtils.equal(5, r.getWidth()));

		r.setHeight(6);
		assertTrue(PrecisionUtils.equal(6, r.getHeight()));

		r.setWidth(-10);
		assertTrue(PrecisionUtils.equal(0, r.getWidth()));

		r.setHeight(-1);
		assertTrue(PrecisionUtils.equal(0, r.getHeight()));
	}

	@Test
	public void test_shrink() {
		Rectangle prect = new Rectangle(new Rectangle(100, 100, 250, 250));
		Rectangle copy = prect.getCopy();
		prect.translate(30, 30);
		prect.scale(2f);
		prect.shrink(2, 2, 2, 2);
		prect.scale(1 / 2f);
		prect.translate(-30, -30);

		prect = new Rectangle(new Rectangle(0, 0, 3, 3));
		copy = prect.getCopy();
		prect.translate(1, 1);
		prect.scale(4f);
		prect.shrink(1, 1, -1, -1);
		prect.scale(1 / 4f);
		prect.translate(-1, -1);

		assertTrue(!prect.equals(copy));
	}

	@Test
	public void test_shrink_AND_expand() {
		Rectangle preciseRect = new Rectangle(-9.486614173228347,
				-34.431496062992125, 41.99055118110236, 25.92755905511811);
		Rectangle recognizableExpanded = preciseRect
				.getExpanded(RECOGNIZABLE_FRACTION, RECOGNIZABLE_FRACTION);
		Rectangle recognizableShrinked = preciseRect
				.getShrinked(RECOGNIZABLE_FRACTION, RECOGNIZABLE_FRACTION);
		assertFalse(preciseRect.equals(recognizableExpanded));
		assertFalse(preciseRect.equals(recognizableShrinked));
		assertFalse(recognizableExpanded.equals(recognizableShrinked));
		recognizableExpanded.shrink(RECOGNIZABLE_FRACTION,
				RECOGNIZABLE_FRACTION);
		recognizableShrinked.expand(RECOGNIZABLE_FRACTION,
				RECOGNIZABLE_FRACTION);
		assertEquals(preciseRect, recognizableExpanded);
		assertEquals(preciseRect, recognizableShrinked);
	}

	public void test_toPath() {
		Rectangle r = new Rectangle(50, 100, 200, 300);
		Segment[] segments = r.toPath().getSegments();
		// check path is closed
		assertTrue(segments[segments.length - 1].getType() == Segment.CLOSE);
	}

	@Test
	public void test_toPolygon() {
		Point[] points = new Point[] { new Point(10, 10),
				new Point(10 + 25, 10), new Point(10 + 25, 10 + 50),
				new Point(10, 10 + 50) };
		Rectangle r = new Rectangle(points[0], points[2]);
		Polygon pr = new Polygon(points);
		assertTrue(pr.equals(r.toPolygon()));
	}

	@Test
	public void test_toString() {
		Rectangle r = new Rectangle();
		assertEquals("Rectangle: (0.0, 0.0, 0.0, 0.0)", r.toString());
	}

	@Test
	public void test_union() {
		// check union behaves like constructor
		Point p1 = new Point(0, 0);
		Point p2 = new Point(10, 10);

		Rectangle origRect = new Rectangle();
		origRect.setLocation(p1);
		origRect.union(p2);

		assertEquals(origRect, new Rectangle(p1, p2));

		Point p3 = new Point(5, 5);
		assertEquals(origRect, new Rectangle(p1, p3).union(origRect));
	}

	@Test
	public void testBorderPointsCalculation() {
		forRectangles(new IAction() {
			@Override
			public void action(Rectangle rect, Point tl, Point br) {
				Point to = new Point((tl.x + br.x) / 2, tl.y);
				Point le = new Point(tl.x, (tl.y + br.y) / 2);
				Point tr = new Point(br.x, tl.y);
				Point ri = new Point(br.x, le.y);
				Point bl = new Point(tl.x, br.y);
				Point bo = new Point(to.x, br.y);
				Point ce = new Point(to.x, le.y);
				assertEquals(tl, rect.getTopLeft());
				assertEquals(to, rect.getTop());
				assertEquals(tr, rect.getTopRight());
				assertEquals(le, rect.getLeft());
				assertEquals(ri, rect.getRight());
				assertEquals(bl, rect.getBottomLeft());
				assertEquals(bo, rect.getBottom());
				assertEquals(br, rect.getBottomRight());
				assertEquals(ce, rect.getCenter());
			}
		});
	}

}
