/*******************************************************************************
 * Copyright (c) 2010, 2016 Research Group Software Construction,
 *                          RWTH Aachen University and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (Research Group Software Construction, RWTH Aachen University) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.euclidean.Straight;
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;
import org.eclipse.gef.geometry.planar.Point;
import org.junit.Test;

/**
 * Unit tests for {@link Straight}.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class StraightTests {

	@Test
	public void test_constructors() {
		Straight s1 = new Straight(new Vector(0, 0), new Vector(3, 3));
		Straight s2 = new Straight(new Point(0, 0), new Point(3, 3));
		assertTrue(s1.equals(s2));

		s1 = new Straight(new Vector(1, 2), new Vector(3, 4));
		s2 = new Straight(new Point(1, 2), new Point(3, 4));
		assertFalse(s1.equals(s2));

	}

	@Test
	public void test_contains() {
		Straight s1 = new Straight(new Vector(0, 0), new Vector(3, 3));

		assertTrue(s1.contains(new Vector(0, 0)));
		assertTrue(s1.contains(new Vector(1, 1)));
		assertTrue(s1.contains(new Vector(2, 2)));
		assertTrue(s1.contains(new Vector(-1, -1)));
		assertTrue(s1.contains(new Vector(-2, -2)));
		assertFalse(s1.contains(new Vector(0, 1)));
		assertFalse(s1.contains(new Vector(-1, 0)));
	}

	@Test
	public void test_containsWithinSegment() {
		Straight s1 = new Straight(new Point(), new Point(0, -1));

		boolean thrown = false;
		try {
			s1.containsWithinSegment(new Vector(0, 0), new Vector(1, 2),
					new Vector(0, 1));
		} catch (IllegalArgumentException x) {
			thrown = true;
		}
		assertTrue(thrown);

		thrown = false;
		try {
			s1.containsWithinSegment(new Vector(1, 2), new Vector(0, 0),
					new Vector(0, 1));
		} catch (IllegalArgumentException x) {
			thrown = true;
		}
		assertTrue(thrown);

		assertTrue(s1.containsWithinSegment(new Vector(0, 0), new Vector(0, 10),
				new Vector(0, 0)));
		assertTrue(s1.containsWithinSegment(new Vector(0, 0),
				new Vector(0, -10), new Vector(0, 0)));
		assertTrue(s1.containsWithinSegment(new Vector(0, 0), new Vector(0, 0),
				new Vector(0, 0)));
		assertTrue(s1.containsWithinSegment(new Vector(0, 0), new Vector(0, 10),
				new Vector(0, 5)));
		assertTrue(s1.containsWithinSegment(new Vector(0, 0),
				new Vector(0, -10), new Vector(0, -5)));
		assertFalse(s1.containsWithinSegment(new Vector(0, 1),
				new Vector(0, 10), new Vector(0, 0)));
		assertFalse(s1.containsWithinSegment(new Vector(0, -1),
				new Vector(0, -10), new Vector(0, 0)));
		assertFalse(s1.containsWithinSegment(new Vector(0, 0),
				new Vector(0, 10), new Vector(0, -5)));
		assertFalse(s1.containsWithinSegment(new Vector(0, 0),
				new Vector(0, -10), new Vector(0, 5)));

		s1 = new Straight(new Point(), new Point(1, 0));
		assertTrue(s1.containsWithinSegment(new Vector(0, 0), new Vector(3, 0),
				new Vector(2, 0)));
		assertFalse(s1.containsWithinSegment(new Vector(0, 0), new Vector(3, 0),
				new Vector(5, 0)));
	}

	@Test
	public void test_equals() {
		Straight s1 = new Straight(new Vector(0, 0), new Vector(3, 3));
		Straight s2 = new Straight(new Vector(4, 4), new Vector(2, 2));
		assertTrue(s1.equals(s2));
		assertTrue(s2.equals(s1));
		assertFalse(s1.equals(new Straight(new Vector(2, 0), s1.direction)));

		// wrong type
		assertFalse(s1.equals(new Point()));
	}

	@Test
	public void test_getAngle_withStraight() {
		Straight s1 = new Straight(new Vector(0, 0), new Vector(3, 3));
		Straight s2 = new Straight(new Vector(0, 4), new Vector(2, 2));
		assertTrue(s1.getAngle(s2).equals(Angle.fromDeg(0)));
		assertTrue(s1.getAngleCW(s2).equals(Angle.fromDeg(0)));
		assertTrue(s1.getAngleCCW(s2).equals(Angle.fromDeg(0)));

		s1 = new Straight(new Vector(0, 0), new Vector(5, 5));
		s2 = new Straight(new Vector(0, 5), new Vector(0, 5));
		assertTrue(s1.getAngle(s2).equals(Angle.fromDeg(45)));
		assertTrue(s1.getAngleCW(s2).equals(Angle.fromDeg(45)));
		assertTrue(s1.getAngleCCW(s2).equals(Angle.fromDeg(135)));
	}

	@Test
	public void test_getCopy() {
		Straight s1 = new Straight(new Vector(0, 0), new Vector(3, 3));

		assertTrue(s1.getCopy().equals(s1));
		assertTrue(s1.clone().equals(s1));
		assertTrue(s1.getCopy().equals(s1.clone()));
	}

	@Test
	public void test_getDistance() {
		Straight s1 = new Straight(new Point(), new Point(0, 1));

		for (int i = 0; i < 10; i++) {
			Vector v = new Vector(i + 1, i - 1);
			assertTrue(PrecisionUtils.equal(s1.getDistance(v), i + 1));
		}

		// bug #482121 (NPE within getDistance())
		Straight s2 = new Straight(
				new Vector(57.36146803551614, 1.8866002881313908E16),
				new Vector(0.0, 16.0));
		Vector v = new Vector(51.15375383435782, 1.8866002881313916E16);
		s2.getDistance(v);
	}

	@Test
	public void test_getIntersection_with_Straight() {
		// test integer precision
		Vector p = new Vector(1, 1);
		Vector a = new Vector(2, 1);
		Vector q = new Vector(1, 4);
		Vector b = new Vector(1, -1);
		Straight s1 = new Straight(p, a);
		Straight s2 = new Straight(q, b);
		assertTrue(s1.intersects(s2));
		Vector intersection = s1.getIntersection(s2);
		assertTrue(intersection.equals(new Vector(3, 2)));
		assertTrue(s1.contains(intersection));
		assertTrue(s2.contains(intersection));

		// check straight does not intersect itself
		assertFalse(s1.intersects(s1));
		assertFalse(s2.intersects(s2));
		assertNull(new Straight(p, a).getIntersection(new Straight(p, a)));

		// test double precision
		p = new Vector(0, 0);
		a = new Vector(new Point(0, 0), new Point(5, 5));
		q = new Vector(0, 5);
		b = new Vector(new Point(0, 5), new Point(5, 0));
		s1 = new Straight(p, a);
		s2 = new Straight(q, b);
		assertTrue(s1.intersects(s2));
		intersection = s1.getIntersection(s2);
		assertTrue(intersection.equals(new Vector(2.5, 2.5)));
		assertTrue(s1.contains(intersection));
		assertTrue(s2.contains(intersection));

		Point p1 = new Point(-2, 1);
		Point p2 = new Point(1, 1);
		Point p3 = new Point(0, 0);
		Point p4 = new Point(0, 3);
		s1 = new Straight(p1, p2);
		s2 = new Straight(p3, p4);
		assertTrue(s1.intersects(s2));
		intersection = s1.getIntersection(s2);
		assertTrue(intersection.equals(new Vector(0, 1)));
		assertTrue(s1.contains(intersection));
		assertTrue(s2.contains(intersection));

		// check four rounding effects
		p1 = new Point(-50, 5);
		p2 = new Point(7, 104);
		p3 = new Point(0, 0);
		p4 = new Point(0, 3);

		s1 = new Straight(p1, p2);
		s2 = new Straight(p3, p4);
		assertTrue(s1.intersects(s2));
		intersection = s1.getIntersection(s2);
		assertNotNull(intersection);
		assertTrue(s1.contains(intersection));
		assertTrue(s2.contains(intersection));

		// test no intersection
		p1 = new Point(0, 10);
		p2 = new Point(10, 10);
		p3 = new Point(0, 5);
		p4 = new Point(10, 5);

		s1 = new Straight(p1, p2);
		s2 = new Straight(p3, p4);

		assertFalse(s1.intersects(s2));
		assertFalse(s2.intersects(s1));

		intersection = s1.getIntersection(s2);
		assertNull(intersection);
	}

	@Test
	public void test_getProjection() {
		Straight s1 = new Straight(new Point(), new Point(0, 1));

		for (int i = 0; i < 10; i++) {
			Vector v = new Vector(i + 1, i - 1);
			assertTrue(s1.getProjection(v).equals(new Vector(0, i - 1)));
		}

		// bug #482121
		Straight s3 = new Straight(
				new Vector(57.36146803551614, 1.8866002881313908E16),
				new Vector(0.0, 16.0));
		Vector v2 = new Vector(51.15375383435782, 1.8866002881313908E16);
		Vector projection = s3.getProjection(v2);
		assertNotNull(projection);
	}

	@Test
	public void test_getSignedDistance() {
		Straight s1 = new Straight(new Point(), new Point(0, -1));

		for (int i = -5; i <= 0; i++) {
			Vector v = new Vector(i, 0);
			assertTrue(PrecisionUtils.equal(s1.getSignedDistanceCW(v), i));
			assertTrue(PrecisionUtils.equal(s1.getSignedDistanceCCW(v), -i));
		}

		for (int i = 0; i <= 5; i++) {
			Vector v = new Vector(i, 0);
			assertTrue(PrecisionUtils.equal(s1.getSignedDistanceCW(v), i));
			assertTrue(PrecisionUtils.equal(s1.getSignedDistanceCCW(v), -i));
		}
	}

	@Test
	public void test_intersectsWithinSegment() {
		Straight s1 = new Straight(new Point(), new Point(10, 10));
		Straight s2 = new Straight(new Point(), new Point(-10, 10));

		boolean thrown = false;
		try {
			s1.intersectsWithinSegment(new Vector(0, 0), new Vector(0, 1), s2);
		} catch (IllegalArgumentException x) {
			thrown = true;
		}
		assertTrue(thrown);

		thrown = false;
		try {
			s1.intersectsWithinSegment(new Vector(1, 0), new Vector(0, 0), s2);
		} catch (IllegalArgumentException x) {
			thrown = true;
		}
		assertTrue(thrown);

		assertTrue("fallthrough", s1.intersectsWithinSegment(new Vector(0, 0),
				new Vector(0, 0), s2));
		assertTrue(s1.intersectsWithinSegment(new Vector(-1, -1),
				new Vector(0, 0), s2));
		assertTrue(s1.intersectsWithinSegment(new Vector(1, 1),
				new Vector(0, 0), s2));
		assertTrue(s1.intersectsWithinSegment(new Vector(-1, -1),
				new Vector(1, 1), s2));
		assertFalse(s1.intersectsWithinSegment(new Vector(-1, -1),
				new Vector(-20, -20), s2));
		assertFalse(s1.intersectsWithinSegment(new Vector(1, 1),
				new Vector(20, 20), s2));
		assertFalse(s1.intersectsWithinSegment(new Vector(-20, -20),
				new Vector(20, 20),
				new Straight(new Vector(10, 0), new Vector(10, 10))));
	}

	@Test
	public void test_isParallelTo_with_Straight() {
		Straight s1 = new Straight(new Vector(0, 0), new Vector(3, 3));
		Straight s2 = new Straight(new Vector(0, 4), new Vector(2, 2));
		assertTrue(s1.isParallelTo(s2));

		s1 = new Straight(new Vector(0, 0), new Vector(5, 5));
		s2 = new Straight(new Vector(0, 5), new Vector(0, 5));
		assertFalse(s1.isParallelTo(s2));
	}

	@Test
	public void test_parameter_point() {
		// normalized direction vector, so that one can comprehend the
		// parameter's value
		Straight s1 = new Straight(new Point(), new Point(0, -1));

		assertTrue(
				PrecisionUtils.equal(s1.getParameterAt(new Vector(0, 0)), 0));
		assertTrue(
				PrecisionUtils.equal(s1.getParameterAt(new Vector(0, -1)), 1));
		assertTrue(
				PrecisionUtils.equal(s1.getParameterAt(new Vector(0, 1)), -1));

		assertTrue(s1.getPositionVectorAt(0).equals(new Vector(0, 0)));
		assertTrue(s1.getPositionVectorAt(2).equals(new Vector(0, -2)));
		assertTrue(s1.getPositionVectorAt(-2).equals(new Vector(0, 2)));

		s1 = new Straight(new Point(), new Point(1, 0));

		assertTrue(
				PrecisionUtils.equal(s1.getParameterAt(new Vector(0, 0)), 0));
		assertTrue(
				PrecisionUtils.equal(s1.getParameterAt(new Vector(2, 0)), 2));
		assertTrue(
				PrecisionUtils.equal(s1.getParameterAt(new Vector(-2, 0)), -2));

		assertTrue(s1.getPositionVectorAt(0).equals(new Vector(0, 0)));
		assertTrue(s1.getPositionVectorAt(1).equals(new Vector(1, 0)));
		assertTrue(s1.getPositionVectorAt(-1).equals(new Vector(-1, 0)));

		// test 0/0 straight (not a straight anymore)
		s1 = new Straight(new Point(), new Point());
		boolean thrown = false;
		try {
			s1.getParameterAt(new Vector(0, 0));
		} catch (IllegalArgumentException x) {
			thrown = true;
		} catch (IllegalStateException x) {
			thrown = true;
		}
		assertTrue(thrown);
	}

	@Test
	public void test_toString() {
		Straight s1 = new Straight(new Point(), new Point(0, -1));
		assertEquals("Straight: Vector: [0.0,0.0] + s * Vector: [0.0,-1.0]",
				s1.toString());
	}

}
