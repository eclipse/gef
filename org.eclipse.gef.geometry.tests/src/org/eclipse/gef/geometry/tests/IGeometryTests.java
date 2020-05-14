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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Polygon;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.geometry.planar.Region;
import org.eclipse.gef.geometry.planar.Ring;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class IGeometryTests {

	/**
	 * <p>
	 * Tests the IGeometry.touches(IGeometry) method.
	 * </p>
	 * <p>
	 * The names of the individual test methods contain the type names of the
	 * tested objects.
	 * </p>
	 * <p>
	 * The following situations should be tested, if possible:
	 * <ul>
	 * <li>Identical objects</li>
	 * <li>Shared edge</li>
	 * <li>Overlapping edge (infinite points in common)</li>
	 * <li>Shared vertex (single point in common)</li>
	 * <li>Outline/curve through vertex (single point in common)</li>
	 * <li>Intersecting edge/edge (infinite points in common)</li>
	 * <li>Intersecting edge/vertex (infinite points in common)</li>
	 * <li>Not touching</li>
	 * <li>Full containment</li>
	 * </ul>
	 * </p>
	 * <p>
	 * Furthermore, the touches() method should be commutative. Therefore, every
	 * touches() call has to be tested with the objects in reverse order:
	 * <ul>
	 * <li><code>A.touches(B) == B.touches(A)</code></li>
	 * </ul>
	 * </p>
	 */
	public static class TouchesTests {

		@Test
		public void Line_Line() {
			// identical / shared edge
			Line l1 = new Line(0, 0, 10, 10);
			Line l2 = new Line(0, 0, 10, 10);
			assertTrue(l1.touches(l2));
			assertTrue(l2.touches(l1));

			// overlapping edge
			l2 = new Line(5, 5, 15, 15);
			assertTrue(l1.touches(l2));
			assertTrue(l2.touches(l1));

			// shared vertex
			l2 = new Line(0, 0, 10, -10);
			assertTrue(l1.touches(l2));
			assertTrue(l2.touches(l1));

			// curve through vertex
			l2 = new Line(0, 5, 0, -5);
			assertTrue(l1.touches(l2));
			assertTrue(l2.touches(l1));

			// intersecting edge/edge
			l2 = new Line(0, 10, 10, -10);
			assertTrue(l1.touches(l2));
			assertTrue(l2.touches(l1));

			// intersecting edge/vertex not possible

			// not touching
			l2 = new Line(0, 10, 0, 20);
			assertFalse(l1.touches(l2));
			assertFalse(l2.touches(l1));
		}

		@Test
		public void Line_Rectangle() {
			Rectangle r = new Rectangle(0, 0, 10, 10);

			// identical not possible (different types)

			// shared edge
			Line l = new Line(0, 0, 10, 0);
			assertTrue(l.touches(r));
			assertTrue(r.touches(l));

			// overlapping edge
			l = new Line(5, 0, 15, 0);
			assertTrue(l.touches(r));
			assertTrue(r.touches(l));

			// shared vertex
			l = new Line(0, 0, 5, -5);
			assertTrue(l.touches(r));
			assertTrue(r.touches(l));

			// curve through vertex
			l = new Line(-5, 5, 5, -5);
			assertTrue(l.touches(r));
			assertTrue(r.touches(l));

			// intersecting in edge
			l = new Line(-5, 5, 5, 5);
			assertTrue(l.touches(r));
			assertTrue(r.touches(l));

			// intersecting in vertex
			l = new Line(-5, -5, 5, 5);
			assertTrue(l.touches(r));
			assertTrue(r.touches(l));

			// not touching
			l = new Line(20, 20, 30, 20);
			assertFalse(l.touches(r));
			assertFalse(r.touches(l));

			// containment
			l = new Line(1, 1, 9, 9);
			assertTrue(l.touches(r));
			assertTrue(r.touches(l));
		}

		@Test
		public void Line_Ring() {
			Ring r = new Ring(new Polygon(10, 10, 20, 10, 15, 20),
					new Polygon(10, 25, 20, 25, 15, 15));

			// identical not possible (different types)

			// shared edge
			Line l = new Line(10, 10, 20, 10);
			assertTrue(l.touches(r));
			assertTrue(r.touches(l));

			// overlapping edge
			l = new Line(15, 10, 25, 10);
			assertTrue(l.touches(r));
			assertTrue(r.touches(l));

			// shared vertex
			l = new Line(10, 10, 5, 5);
			assertTrue(l.touches(r));
			assertTrue(r.touches(l));

			// curve through vertex
			l = new Line(5, 15, 15, 5);
			assertTrue(l.touches(r));
			assertTrue(r.touches(l));

			// intersecting edge/edge
			l = new Line(15, 15, 15, 5);
			assertTrue(l.touches(r));
			assertTrue(r.touches(l));

			// intersecting edge/vertex
			l = new Line(5, 5, 15, 12);
			assertTrue(l.touches(r));
			assertTrue(r.touches(l));

			// not touching
			l = new Line(0, 0, 0, 5);
			assertFalse(l.touches(r));
			assertFalse(r.touches(l));

			// containment
			l = new Line(11, 11, 12, 12);
			assertTrue(l.touches(r));
			assertTrue(r.touches(l));

			// check that the ring contains it, although it intersects the
			// polygon borders
			l = new Line(15, 15, 15, 22);
			assertTrue(l.touches(r));
			assertTrue(r.touches(l));
		}

		@Test
		public void Polygon_Polygon() {
			Polygon p1 = new Polygon(10, 10, 20, 10, 15, 20);

			// identical
			Polygon p2 = new Polygon(10, 10, 20, 10, 15, 20);
			assertTrue(p1.touches(p2));
			assertTrue(p2.touches(p1));

			// shared edge
			p2 = new Polygon(10, 10, 20, 10, 15, 0);
			assertTrue(p1.touches(p2));
			assertTrue(p2.touches(p1));

			// overlapping edge
			p2 = new Polygon(15, 10, 25, 10, 20, 0);
			assertTrue(p1.touches(p2));
			assertTrue(p2.touches(p1));

			// shared vertex
			p2 = new Polygon(0, 10, 10, 10, 5, 5);
			assertTrue(p1.touches(p2));
			assertTrue(p2.touches(p1));

			// edge through vertex
			p2 = new Polygon(5, 15, 15, 5, 0, 0);
			assertTrue(p1.touches(p2));
			assertTrue(p2.touches(p1));

			// intersecting edge/edge
			p2 = new Polygon(10, 5, 20, 5, 15, 15);
			assertTrue(p1.touches(p2));
			assertTrue(p2.touches(p1));

			// intersecting edge/vertex
			p2 = new Polygon(15, 15, 25, 5, 15, 25);
			assertTrue(p1.touches(p2));
			assertTrue(p2.touches(p1));

			// not touching
			p2 = new Polygon(20, 20, 30, 20, 25, 30);
			assertFalse(p1.touches(p2));
			assertFalse(p2.touches(p1));

			// containment
			p2 = new Polygon(11, 11, 19, 11, 15, 19);
			assertTrue(p1.touches(p2));
			assertTrue(p2.touches(p1));
		}

		@Test
		public void Polygon_Region() {
			Region r = new Region(new Rectangle(0, 0, 15, 10),
					new Rectangle(5, 5, 15, 10));

			// identical not possible

			// shared edge
			Polygon p = new Polygon(20, 5, 20, 15, 25, 15);
			assertTrue(p.touches(r));
			assertTrue(r.touches(p));

			// overlapping edge
			p = new Polygon(20, 10, 20, 20, 25, 15);
			assertTrue(p.touches(r));
			assertTrue(r.touches(p));

			// shared vertex
			p = new Polygon(20, 5, 30, 5, 25, 0);
			assertTrue(p.touches(r));
			assertTrue(r.touches(p));

			// edge through vertex
			p = new Polygon(25, 10, 15, 20, 25, 20);
			assertTrue(p.touches(r));
			assertTrue(r.touches(p));

			// intersecting edge/edge
			p = new Polygon(15, 10, 15, 20, 10, 20);
			assertTrue(p.touches(r));
			assertTrue(r.touches(p));

			// intersecting edge/vertex
			p = new Polygon(15, 10, 25, 20, 25, 0);
			assertTrue(p.touches(r));
			assertTrue(r.touches(p));

			// not touching
			p = new Polygon(0, 25, 10, 25, 5, 30);
			assertFalse(p.touches(r));
			assertFalse(r.touches(p));

			// containment
			p = new Polygon(2, 2, 8, 8, 10, 2);
			assertTrue(p.touches(r));
			assertTrue(r.touches(p));

			p = new Polygon(10, 2, 10, 13, 11, 13);
			assertTrue(p.touches(r));
			assertTrue(r.touches(p));
		}

		@Test
		public void Ring_Ring() {
			Ring r1 = new Ring(new Polygon(5, 5, 10, 15, 15, 5),
					new Polygon(10, 10, 20, 10, 15, 0));

			// identical
			Ring r2 = new Ring(new Polygon(5, 5, 10, 15, 15, 5),
					new Polygon(10, 10, 20, 10, 15, 0));
			assertTrue(r1.touches(r2));
			assertTrue(r2.touches(r1));

			// shared edge
			r2 = new Ring(new Polygon(15, 0, 20, 10, 25, 0),
					new Polygon(20, -5, 20, 5, 30, 10));
			assertTrue(r1.touches(r2));
			assertTrue(r2.touches(r1));

			// overlapping edge
			r2 = new Ring(new Polygon(17.5, 5, 22.5, 15, 25, 0),
					new Polygon(20, -5, 20, 5, 30, 10));
			assertTrue(r1.touches(r2));
			assertTrue(r2.touches(r1));

			// shared vertex
			r2 = new Ring(new Polygon(20, 10, 20, 15, 25, 10),
					new Polygon(20, 12.5, 22.5, 10, 25, 15));
			assertTrue(r1.touches(r2));
			assertTrue(r2.touches(r1));

			// edge through vertex
			r2 = new Ring(new Polygon(15, 15, 25, 5, 25, 15),
					new Polygon(20, 15, 25, 10, 30, 10));
			assertTrue(r1.touches(r2));
			assertTrue(r2.touches(r1));

			// intersecting edge/edge
			r2 = new Ring(new Polygon(7.5, 7.5, 12.5, 7.5, 10, 0),
					new Polygon(7.5, 2.5, 12.5, 2.5, 0, -5));
			assertTrue(r1.touches(r2));
			assertTrue(r2.touches(r1));

			// intersecting edge/vertex
			r2 = new Ring(new Polygon(10, 7.5, 10, 20, 0, 2.5),
					new Polygon(2.5, 0, 2.5, 15, 0, 10));
			assertTrue(r1.touches(r2));
			assertTrue(r2.touches(r1));

			// not touching
			r2 = new Ring(new Polygon(15, 20, 15, 25, 25, 20),
					new Polygon(20, 20, 20, 25, 25, 25));
			assertFalse(r1.touches(r2));
			assertFalse(r2.touches(r1));

			// containment
			r2 = new Ring(new Polygon(10, 8, 15, 5, 15, 7.5),
					new Polygon(10, 7, 15, 7.5, 15, 8));
			assertTrue(r1.touches(r2));
			assertTrue(r2.touches(r1));
		}

	}

}
