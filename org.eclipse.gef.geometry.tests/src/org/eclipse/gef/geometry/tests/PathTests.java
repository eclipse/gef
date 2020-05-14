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

import java.util.List;

import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.geometry.planar.Path;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.junit.Test;

public class PathTests {

	@Test
	public void test_CAG_add() {
		Rectangle r0 = new Rectangle(0, 0, 100, 100);
		Rectangle r1 = new Rectangle(50, 50, 100, 100);
		Path unionPath = Path.add(r0.toPath(), r1.toPath());
		assertTrue(unionPath.contains(r0.getCenter()));
		assertTrue(unionPath.contains(r1.getCenter()));
	}

	@Test
	public void test_CAG_exclusiveOr() {
		Rectangle r0 = new Rectangle(0, 0, 100, 100);
		Rectangle r1 = new Rectangle(50, 50, 100, 100);
		Path xorPath = Path.exclusiveOr(r0.toPath(), r1.toPath());
		assertTrue(xorPath.contains(new Point(25, 25)));
		assertFalse(xorPath.contains(new Point(75, 75)));
		assertTrue(xorPath.contains(new Point(125, 125)));
	}

	@Test
	public void test_CAG_intersect() {
		Rectangle r0 = new Rectangle(0, 0, 100, 100);
		Rectangle r1 = new Rectangle(50, 50, 100, 100);
		Path intersectionPath = Path.intersect(r0.toPath(), r1.toPath());
		assertFalse(intersectionPath.contains(new Point(25, 25)));
		assertTrue(intersectionPath.contains(new Point(75, 75)));
		assertFalse(intersectionPath.contains(new Point(125, 125)));
	}

	@Test
	public void test_CAG_subtract() {
		Rectangle r0 = new Rectangle(0, 0, 100, 100);
		Rectangle r1 = new Rectangle(50, 50, 100, 100);
		Path differencePath = Path.subtract(r0.toPath(), r1.toPath());
		assertTrue(differencePath.contains(new Point(25, 25)));
		assertTrue(differencePath.contains(new Point(25, 75)));
		assertTrue(differencePath.contains(new Point(75, 25)));
		assertFalse(differencePath.contains(new Point(75, 75)));
		assertFalse(differencePath.contains(new Point(75, 125)));
		assertFalse(differencePath.contains(new Point(125, 75)));
		assertFalse(differencePath.contains(new Point(125, 125)));
	}

	@Test
	public void test_getBounds_cubic() {
		// create path using all segment types
		Path path = new Path(
				new Path.Segment(Path.Segment.MOVE_TO, new Point(10, 10)),
				new Path.Segment(Path.Segment.LINE_TO, new Point(80, 10)),
				new Path.Segment(Path.Segment.QUAD_TO, new Point(50, 50),
						new Point(80, 80)),
				new Path.Segment(Path.Segment.CUBIC_TO, new Point(50, 50),
						new Point(30, 100), new Point(10, 80)),
				new Path.Segment(Path.Segment.CLOSE));

		// determine bounds
		Rectangle bounds = path.getBounds();

		// verify bounds start at 10, 10
		assertEquals(10, bounds.getX(), 0.01);
		assertEquals(10, bounds.getY(), 0.01);

		// compute and union the individual segment bounds
		List<ICurve> outlines = path.getOutlines();
		Rectangle outlineBounds = outlines.get(0).getBounds();
		for (int i = 1; i < outlines.size(); i++) {
			outlineBounds.union(outlines.get(i).getBounds());
		}

		// verify the computed bounds are equal to the bounds as returned by the
		// path
		assertEquals(outlineBounds, bounds);
	}

	@Test
	public void test_getBounds_linear() {
		// create path using all segment types
		Path path = new Path(
				new Path.Segment(Path.Segment.MOVE_TO, new Point(10, 10)),
				new Path.Segment(Path.Segment.LINE_TO, new Point(80, 10)),
				new Path.Segment(Path.Segment.LINE_TO, new Point(80, 80)),
				new Path.Segment(Path.Segment.LINE_TO, new Point(10, 80)),
				new Path.Segment(Path.Segment.CLOSE));

		// determine bounds
		Rectangle bounds = path.getBounds();

		// verify bounds start at 10, 10
		assertEquals(10, bounds.getX(), 0.01);
		assertEquals(10, bounds.getY(), 0.01);

		// compute and union the individual segment bounds
		List<ICurve> outlines = path.getOutlines();
		Rectangle outlineBounds = outlines.get(0).getBounds();
		for (int i = 1; i < outlines.size(); i++) {
			outlineBounds.union(outlines.get(i).getBounds());
		}

		// verify the computed bounds are equal to the bounds as returned by the
		// path
		assertEquals(outlineBounds, bounds);
	}

	@Test
	public void test_getBounds_quadratic() {
		// create path using all segment types
		Path path = new Path(
				new Path.Segment(Path.Segment.MOVE_TO, new Point(10, 10)),
				new Path.Segment(Path.Segment.LINE_TO, new Point(80, 10)),
				new Path.Segment(Path.Segment.QUAD_TO, new Point(50, 50),
						new Point(80, 80)),
				new Path.Segment(Path.Segment.QUAD_TO, new Point(50, 50),
						new Point(10, 80)),
				new Path.Segment(Path.Segment.CLOSE));

		// determine bounds
		Rectangle bounds = path.getBounds();

		// verify bounds start at 10, 10
		assertEquals(10, bounds.getX(), 0.01);
		assertEquals(10, bounds.getY(), 0.01);

		// compute and union the individual segment bounds
		List<ICurve> outlines = path.getOutlines();
		Rectangle outlineBounds = outlines.get(0).getBounds();
		for (int i = 1; i < outlines.size(); i++) {
			outlineBounds.union(outlines.get(i).getBounds());
		}

		// verify the computed bounds are equal to the bounds as returned by the
		// path
		assertEquals(outlineBounds, bounds);
	}

}
