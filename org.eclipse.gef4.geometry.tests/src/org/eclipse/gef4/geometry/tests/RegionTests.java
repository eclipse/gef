/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.Region;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;
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

}
