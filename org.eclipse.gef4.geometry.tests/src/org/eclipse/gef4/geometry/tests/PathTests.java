package org.eclipse.gef4.geometry.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
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

}
