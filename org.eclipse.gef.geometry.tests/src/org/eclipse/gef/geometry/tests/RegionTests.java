/*******************************************************************************
 * Copyright (c) 2012, 2016 itemis AG and others.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;
import org.eclipse.gef.geometry.planar.Path;
import org.eclipse.gef.geometry.planar.Path.Segment;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.geometry.planar.Region;
import org.junit.Test;

public class RegionTests {

	@Test
	public void test_constructor() {
		Region region = new Region();
		assertEquals(0, region.getShapes().length);

		region = new Region(new Rectangle(0, 0, 100, 100));
		assertEquals(1, region.getShapes().length);

		region = new Region(region);
		assertEquals(1, region.getShapes().length);
	}

	@Test
	public void test_copy_semantics() {
		Rectangle r1 = new Rectangle(0, 0, 100, 100);
		Region a1 = new Region(r1);
		r1.setWidth(50);

		// constructor copies Rectangles:
		// changing r1 does not change a1
		assertTrue(PrecisionUtils.equal(100, a1.getShapes()[0].getWidth()));

		Region a2 = a1.getCopy();
		a2.getShapes()[0].setWidth(50);

		// getCopy() copies Rectangles:
		// changing a2 does not change a1
		assertTrue(PrecisionUtils.equal(100, a1.getShapes()[0].getWidth()));

		a2 = new Region(a1);
		a2.getShapes()[0].setWidth(50);

		// constructor copies Rectangles:
		// changing a2 does not change a1
		assertTrue(PrecisionUtils.equal(100, a1.getShapes()[0].getWidth()));
	}

	@Test
	public void test_cover_single_rectangle() {
		Rectangle r1 = new Rectangle(100, 100, 100, 100);
		Region region = new Region(r1);

		assertTrue(region.contains(r1));
		assertTrue(
				"A Region of just a single Rectangle should use this Rectangle as its only internal shape.",
				region.getShapes()[0].equals(r1));

		assertFalse(region.contains(new Rectangle(0, 0, 50, 50)));
		assertFalse(region.contains(new Rectangle(50, 50, 100, 100)));
	}

	@Test
	public void test_cover_two_distinct_rectangles() {
		Rectangle r1 = new Rectangle(100, 100, 100, 100);
		Rectangle r2 = new Rectangle(500, 100, 100, 100);
		Region region = new Region(r1, r2);

		assertTrue(region.contains(r1));
		assertTrue(region.contains(r2));

		assertFalse(region.contains(new Rectangle(0, 0, 50, 50)));
		assertFalse(region.contains(new Rectangle(50, 50, 100, 100)));
	}

	@Test
	public void test_cover_two_intersecting_rectangles() {
		Rectangle r1 = new Rectangle(50, 50, 50, 200);
		Rectangle r2 = new Rectangle(50, 200, 200, 50);
		Region region = new Region(r1, r2);

		assertTrue(region.contains(r1));
		assertTrue(region.contains(r2));
		assertTrue(region.contains(r1.getIntersected(r2)));

		assertFalse(region.contains(new Rectangle(0, 0, 10, 10)));
		assertFalse(region.contains(new Rectangle(25, 25, 50, 50)));
	}

	@Test
	public void test_equals() {
		Region r0 = new Region(new Rectangle(0, 0, 100, 100));
		Region r1 = new Region(new Rectangle(0, 0, 50, 100),
				new Rectangle(50, 0, 50, 100));
		assertEquals(r0, r1);
		assertEquals(r1, r0);
		r0 = new Region(new Rectangle(0, 0, 100, 50),
				new Rectangle(0, 50, 100, 50));
		assertEquals(r0, r1);
		assertEquals(r1, r0);

		r1 = new Region(new Rectangle(0, 0, 100, 100),
				new Rectangle(50, 50, 100, 100));
		assertFalse(r0.equals(r1));
		assertFalse(r1.equals(r0));
	}

	@Test
	public void test_toPath() {
		// empty Region
		Region region = new Region();
		assertEquals(new Path(), region.toPath());

		// one rectangle
		region = new Region(new Rectangle(0, 0, 100, 50));
		Path path = region.toPath();
		Segment[] segs = path.getSegments();
		assertEquals(6, segs.length);
		assertEquals(Path.Segment.MOVE_TO, segs[0].getType());
		assertEquals(Path.Segment.CLOSE, segs[5].getType());

		// overlapping rectangles
		region = new Region(new Rectangle(0, 0, 100, 100),
				new Rectangle(50, 50, 100, 100));
		path = region.toPath();
		segs = path.getSegments();
		assertEquals(12, segs.length);
		assertEquals(Path.Segment.MOVE_TO, segs[0].getType());
		assertEquals(Path.Segment.CLOSE, segs[11].getType());

		// distinct rectangles
		region = new Region(new Rectangle(0, 0, 50, 50),
				new Rectangle(60, 60, 50, 50));
		path = region.toPath();
		segs = path.getSegments();
		assertEquals(10, segs.length);
		assertEquals(Path.Segment.MOVE_TO, segs[0].getType());
		assertEquals(Path.Segment.CLOSE, segs[4].getType());
		assertEquals(Path.Segment.MOVE_TO, segs[5].getType());
		assertEquals(Path.Segment.CLOSE, segs[9].getType());
	}

	@Test
	public void test_toPath_with_void() {
		Region r = new Region(new Rectangle(0, 0, 200, 50),
				new Rectangle(0, 0, 50, 150), new Rectangle(0, 100, 200, 50),
				new Rectangle(150, 0, 50, 150));

		Path p = r.toPath();

		// obviously outside
		assertFalse(p.contains(new Point(-10, -10)));
		assertFalse(p.contains(new Point(-10, 75)));
		assertFalse(p.contains(new Point(-10, 160)));
		assertFalse(p.contains(new Point(100, -10)));
		assertFalse(p.contains(new Point(100, 160)));
		assertFalse(p.contains(new Point(210, -10)));
		assertFalse(p.contains(new Point(210, 75)));
		assertFalse(p.contains(new Point(210, 160)));

		// obviously inside
		assertTrue(p.contains(new Point(25, 25)));
		assertTrue(p.contains(new Point(25, 125)));
		assertTrue(p.contains(new Point(100, 25)));
		assertTrue(p.contains(new Point(100, 125)));
		assertTrue(p.contains(new Point(175, 25)));
		assertTrue(p.contains(new Point(175, 125)));

		// the void
		assertFalse(p.contains(new Point(100, 75)));
	}

}
