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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Path;
import org.eclipse.gef.geometry.planar.Path.Segment;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polygon;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.geometry.planar.Ring;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class RingTests {

	public static class ContainmentTests {

		@Test
		public void cover_single_polygon() {
			Polygon p1 = new Polygon(1, 2, 1, 3, 2, 4, 3, 4, 4, 3, 4, 2, 3, 1,
					2, 1);
			Ring ring = new Ring(p1);

			assertFalse(ring.contains(new Polygon(0, 0, 1, 0, 1, 1, 0, 1)));
			assertFalse(ring.contains(new Polygon(1, 1, 3, 1, 2, 2)));
			assertTrue(ring.contains(new Polygon(2, 2, 2, 3, 3, 3, 3, 2)));
			assertTrue(ring.contains(p1));
		}

		@Test
		public void cover_two_distinct_polygons() {
			Polygon p1 = new Polygon(1, 2, 1, 3, 2, 4, 3, 4, 4, 3, 4, 2, 3, 1,
					2, 1);
			Polygon p2 = new Polygon(4, 4, 4, 5, 5, 5, 5, 4);
			Ring ring = new Ring(p1, p2);

			assertFalse(ring.contains(new Polygon(0, 0, 1, 0, 1, 1, 0, 1)));
			assertFalse(ring.contains(new Polygon(1, 1, 3, 1, 2, 2)));
			assertFalse(ring.contains(
					new Polygon(4.5, 4.5, 4.5, 5.5, 5.5, 5.5, 5.5, 4.5)));
			assertFalse(ring.contains(new Polygon(3, 3, 5, 3, 5, 5)));
			assertTrue(ring.contains(new Polygon(2, 2, 2, 3, 3, 3, 3, 2)));
			assertTrue(ring.contains(
					new Polygon(4.1, 4.1, 4.9, 4.1, 4.9, 4.9, 4.1, 4.9)));
			assertTrue(ring.contains(p1));
			assertTrue(ring.contains(p2));
		}

		@Test
		public void cover_two_intersecting_polygons() {
			Polygon p1 = new Polygon(1, 2, 1, 3, 2, 4, 3, 4, 4, 3, 4, 2, 3, 1,
					2, 1);
			Polygon p2 = new Polygon(2.5, 2.5, 2.5, 5, 5, 5, 5, 2.5);
			Ring ring = new Ring(p1, p2);

			assertFalse(ring.contains(new Polygon(0, 0, 1, 0, 1, 1, 0, 1)));
			assertFalse(ring.contains(new Polygon(1, 1, 3, 1, 2, 2)));
			assertFalse(ring.contains(
					new Polygon(4.5, 4.5, 4.5, 5.5, 5.5, 5.5, 5.5, 4.5)));
			assertTrue(ring.contains(new Polygon(2, 2, 2, 3, 3, 3, 3, 2)));
			assertTrue(ring.contains(new Polygon(3, 3, 5, 3, 5, 5)));
			assertTrue(ring.contains(p1));
			assertTrue(ring.contains(p2));
			assertTrue(ring.contains(new Polygon(2, 2, 2, 3, 3, 3, 3, 2)));
		}

		@Test
		public void equals() {
			Ring r0 = new Ring(new Polygon(0, 0, 100, 100, 0, 100));
			Ring r1 = new Ring(new Polygon(0, 0, 50, 50, 0, 100),
					new Polygon(50, 50, 0, 100, 100, 100));
			assertEquals(r0, r1);
			assertEquals(r1, r0);
			r0 = new Ring(new Polygon(0, 0, 100, 100, 0, 50),
					new Polygon(0, 50, 0, 100, 100, 100));
			assertEquals(r0, r1);
			assertEquals(r1, r0);

			r1 = new Ring(new Polygon(0, 0, 100, 100, 0, 100),
					new Polygon(50, 50, 100, 100, 100, 50));
			assertFalse(r0.equals(r1));
			assertFalse(r1.equals(r0));
		}

	}

	public static class ToPathTests {

		@Test
		public void toPath() {
			// empty Region
			Ring ring = new Ring();
			assertEquals(new Path(), ring.toPath());

			// one rectangle
			ring = new Ring(new Rectangle(0, 0, 100, 50).toPolygon());
			Path path = ring.toPath();
			Segment[] segs = path.getSegments();
			assertEquals(6, segs.length);
			assertEquals(Path.Segment.MOVE_TO, segs[0].getType());
			assertEquals(Path.Segment.CLOSE, segs[5].getType());

			// overlapping rectangles
			ring = new Ring(new Rectangle(0, 0, 100, 100).toPolygon(),
					new Rectangle(50, 50, 100, 100).toPolygon());
			path = ring.toPath();
			segs = path.getSegments();
			assertEquals(10, segs.length);
			assertEquals(Path.Segment.MOVE_TO, segs[0].getType());
			assertEquals(Path.Segment.CLOSE, segs[9].getType());

			// distinct rectangles
			ring = new Ring(new Rectangle(0, 0, 50, 50).toPolygon(),
					new Rectangle(60, 60, 50, 50).toPolygon());
			path = ring.toPath();
			segs = path.getSegments();
			assertEquals(10, segs.length);
			assertEquals(Path.Segment.MOVE_TO, segs[0].getType());
			assertEquals(Path.Segment.CLOSE, segs[4].getType());
			assertEquals(Path.Segment.MOVE_TO, segs[5].getType());
			assertEquals(Path.Segment.CLOSE, segs[9].getType());
		}

		@Test
		public void toPath_with_void() {
			Ring r = new Ring(
					new Polygon(0, 0, 100, 0, 100, 50, 50, 50, 50, 100, 100,
							100, 100, 150, 0, 150),
					new Polygon(100, 0, 200, 0, 200, 150, 100, 150, 100, 100,
							150, 100, 150, 50, 100, 50));

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

	/**
	 * <p>
	 * The {@link Ring#triangulate(Polygon, Line)} method is tested here.
	 * </p>
	 *
	 * <p>
	 * The test names indicate the various situations that are tested. Each
	 * situation comprises the location of the start and end point of the
	 * {@link Line} and the real and imaginary intersection {@link Point}s of
	 * the {@link Line} and the {@link Polygon}. Real {@link Point}s of
	 * intersection are the intersection {@link Point}s of the {@link Line} and
	 * the {@link Polygon}. Imaginary {@link Point}s of intersection do not lie
	 * on the {@link Line} but on its expansion to infinity in both directions.
	 * </p>
	 *
	 * <p>
	 * The first two characters indicate the location of the start and the end
	 * {@link Point} of the {@link Line} relative to the {@link Polygon}. 'o'
	 * means 'outside the polygon'. 'i' means 'inside the polygon'. 'e' means
	 * 'on an edge of the polygon'. 'v' means 'on a vertex of the polygon'.
	 * </p>
	 *
	 * <p>
	 * After that, number and type of expected intersections are stated. Real
	 * intersection {@link Point}s ('r' for 'real') are named before the
	 * imaginary ('i' for 'imaginary') intersections. The characters after 'r'
	 * or 'i' define the type of intersection. 'v' means 'the intersection point
	 * is a vertex of the polygon'. 'e' means 'the intersection point is on an
	 * edge of the polygon'.
	 * </p>
	 *
	 * <p>
	 * The postfix indicates the expected number of resulting {@link Polygon}s.
	 * 'ntd' means 'nothing to do' and therefore, it appears when a copy of the
	 * original {@link Polygon} is expected as the result. Otherwise, 's' is
	 * followed by the number of results. 'overlaps_edge' means that a
	 * {@link Polygon}s edge is overlapped by the {@link Line}.
	 * </p>
	 */
	public static class TriangulateTriangleWithLine {

		Polygon p;

		@Test
		public void no_line() {
			try {
				triangulate(p, null);
			} catch (IllegalStateException x) {
				Throwable cause = x;
				while (cause.getCause() != null) {
					cause = cause.getCause();
				}

				assertTrue(cause.getClass()
						.equals(IllegalArgumentException.class));
			}
		}

		@Test
		public void no_polygon() {
			try {
				triangulate(null, new Line(1, 2, 3, 4));
			} catch (IllegalStateException x) {
				Throwable cause = x;
				while (cause.getCause() != null) {
					cause = cause.getCause();
				}

				assertTrue(cause.getClass()
						.equals(IllegalArgumentException.class));
			}
		}

		@Before
		public void setUp() {
			p = new Polygon(new Point(100, 100), new Point(100, 300),
					new Point(300, 200));
		}

		private Polygon[] triangulate(Polygon p, Line s) {
			try {
				Class<?> parameterTypes[] = new Class<?>[] { Polygon.class,
						Line.class };
				Method triangulate = Ring.class.getDeclaredMethod("triangulate",
						parameterTypes);
				triangulate.setAccessible(true);
				return (Polygon[]) triangulate.invoke(null, p, s);
			} catch (Exception x) {
				throw new IllegalStateException(x);
			}
		}

		@Test
		public void triangulate_ee_overlaps_edge_ntd() {
			// p1 on edge, p2 on edge, overlaps edge, nothing to do
			Polygon[] r = triangulate(p,
					new Line(new Point(100, 150), new Point(100, 250)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(150, 125), new Point(250, 175)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(150, 275), new Point(250, 225)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);
		}

		@Test
		public void triangulate_ee2ree_s3() {
			// p1 on edge, p2 on edge, 2 real intersections (edge, edge), split
			// into
			// 3 pieces
			Polygon[] r = triangulate(p, new Line(100, 200, 200, 150));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p, new Line(100, 200, 200, 250));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p, new Line(200, 250, 200, 150));
			assertEquals("split into three", 3, r.length);

			// swap start and end point
			r = triangulate(p, new Line(200, 150, 100, 200));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p, new Line(200, 250, 100, 200));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p, new Line(200, 150, 200, 250));
			assertEquals("split into three", 3, r.length);
		}

		@Test
		public void triangulate_ei1re1ie_s3() {
			// p1 on edge, p2 inside, 1 real intersection (edge), 1 imaginary
			// intersection (edge), split into 3 pieces
			Polygon[] r = triangulate(p, new Line(100, 200, 150, 175));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p, new Line(100, 200, 150, 225));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p, new Line(200, 250, 200, 200));
			assertEquals("split into three", 3, r.length);
		}

		@Test
		public void triangulate_ei1re1iv_s2() {
			// p1 on edge, p2 inside, 1 real intersection (edge), 1 imaginary
			// intersection (vertex), split into 2 pieces
			Polygon[] r = triangulate(p, new Line(100, 200, 200, 200));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(200, 150, 150, 225));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(200, 250, 150, 175));
			assertEquals("split into two", 2, r.length);
		}

		@Test
		public void triangulate_eo1re_ntd() {
			// p1 on edge, p2 outside, 1 real intersection (edge), nothing to do
			Polygon[] r = triangulate(p,
					new Line(new Point(100, 200), new Point(100, 0)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(200, 150), new Point(200, 0)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(200, 250), new Point(200, 300)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);
		}

		@Test
		public void triangulate_ev_overlaps_edge_ntd() {
			// p1 on edge, p2 on vertex, overlaps edge, nothing to do
			Polygon[] r = triangulate(p,
					new Line(new Point(100, 200), new Point(100, 100)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(150, 125), new Point(100, 100)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(150, 125), new Point(300, 200)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(150, 275), new Point(300, 200)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(150, 275), new Point(100, 300)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(100, 200), new Point(100, 300)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);
		}

		@Test
		public void triangulate_ev2rve_s2() {
			// p1 on edge, p2 on vertex, 2 real intersections (vertex, edge),
			// split
			// into 2 pieces
			Polygon[] r = triangulate(p, new Line(200, 250, 100, 100));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(100, 200, 300, 200));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(200, 150, 100, 300));
			assertEquals("split into two", 2, r.length);
		}

		@Test
		public void triangulate_ie1re1ie_s3() {
			// p1 inside, p2 on edge, 1 real intersection (edge), 1 imaginary
			// intersection (edge), split into 3 pieces
			Polygon[] r = triangulate(p, new Line(150, 175, 100, 200));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p, new Line(150, 225, 100, 200));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p, new Line(200, 200, 200, 250));
			assertEquals("split into three", 3, r.length);
		}

		@Test
		public void triangulate_ie1re1iv_s2() {
			// p1 inside, p2 on edge, 1 real intersection (edge), 1 imaginary
			// intersection (vertex), split into 2 pieces
			Polygon[] r = triangulate(p, new Line(200, 200, 100, 200));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(150, 225, 200, 150));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(150, 175, 200, 250));
			assertEquals("split into two", 2, r.length);
		}

		@Test
		public void triangulate_ii2iee_s3() {
			// p1 inside, p2 inside, 2 imaginary intersections (edge, edge),
			// split
			// into 3 pieces
			Polygon[] r = triangulate(p, new Line(125, 200, 150, 175));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p, new Line(200, 175, 200, 225));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p, new Line(125, 200, 150, 225));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p, new Line(150, 175, 125, 200));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p, new Line(200, 225, 200, 175));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p, new Line(150, 225, 125, 200));
			assertEquals("split into three", 3, r.length);
		}

		@Test
		public void triangulate_ii2iev_s2() {
			// p1 inside, p2 inside, 2 imaginary intersections (edge, vertex),
			// split
			// into 2 pieces
			Polygon[] r = triangulate(p, new Line(150, 200, 125, 150));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(150, 200, 200, 200));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(150, 200, 125, 250));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(125, 150, 150, 200));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(200, 200, 150, 200));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(125, 250, 150, 200));
			assertEquals("split into two", 2, r.length);
		}

		@Test
		public void triangulate_io1re1ie_s3() {
			// p1 inside, p2 outside, 1 real intersection (edge), 1 imaginary
			// intersection (edge), split into 3 pieces
			Polygon[] r = triangulate(p,
					new Line(new Point(150, 200), new Point(150, 50)));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p,
					new Line(new Point(150, 200), new Point(250, 100)));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p,
					new Line(new Point(150, 200), new Point(150, 350)));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p,
					new Line(new Point(150, 200), new Point(250, 300)));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p,
					new Line(new Point(150, 200), new Point(50, 300)));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p,
					new Line(new Point(150, 200), new Point(50, 100)));
			assertEquals("split into three", 3, r.length);
		}

		@Test
		public void triangulate_io1re1iv_s2() {
			// p1 inside, p2 outside, 1 real intersection (edge), 1 imaginary
			// intersection (vertex), split into 2 pieces
			Polygon[] r = triangulate(p,
					new Line(new Point(150, 200), new Point(200, 100)));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p,
					new Line(new Point(150, 200), new Point(200, 300)));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p,
					new Line(new Point(150, 200), new Point(50, 200)));
			assertEquals("split into two", 2, r.length);
		}

		@Test
		public void triangulate_io1rv1ie_s2() {
			// p1 inside, p2 outside, 1 real intersection (vertex), 1 imaginary
			// intersection (edge), split into 2 pieces
			Polygon[] r = triangulate(p,
					new Line(new Point(150, 200), new Point(50, 400)));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(new Point(150, 200), new Point(50, 0)));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p,
					new Line(new Point(150, 200), new Point(400, 200)));
			assertEquals("split into two", 2, r.length);
		}

		@Test
		public void triangulate_iv1rv1ie_s2() {
			// p1 inside, p2 on vertex, 1 real intersection (vertex), 1
			// imaginary
			// intersection (edge), split into 2 pieces
			Polygon[] r = triangulate(p, new Line(200, 250, 100, 100));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(100, 200, 300, 200));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(200, 150, 100, 300));
			assertEquals("split into two", 2, r.length);
		}

		@Test
		public void triangulate_oe1re_ntd() {
			// p1 outside, p2 on edge, 1 real intersection (edge), nothing to do
			Polygon[] r = triangulate(p,
					new Line(new Point(100, 0), new Point(100, 200)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(200, 0), new Point(200, 150)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(200, 300), new Point(200, 250)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);
		}

		@Test
		public void triangulate_oi1re1ie_s3() {
			// p1 outside, p2 inside, 1 real intersection (edge), 1 imaginary
			// intersection (edge), split into 3 pieces
			Polygon[] r = triangulate(p,
					new Line(new Point(150, 50), new Point(150, 200)));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p,
					new Line(new Point(250, 100), new Point(150, 200)));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p,
					new Line(new Point(150, 350), new Point(150, 200)));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p,
					new Line(new Point(250, 300), new Point(150, 200)));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p,
					new Line(new Point(50, 300), new Point(150, 200)));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p,
					new Line(new Point(50, 100), new Point(150, 200)));
			assertEquals("split into three", 3, r.length);
		}

		@Test
		public void triangulate_oi1re1iv_s2() {
			// p1 outside, p2 inside, 1 real intersection (edge), 1 imaginary
			// intersection (vertex), split into 2 pieces
			Polygon[] r = triangulate(p,
					new Line(new Point(200, 100), new Point(150, 200)));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p,
					new Line(new Point(200, 300), new Point(150, 200)));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p,
					new Line(new Point(50, 200), new Point(150, 200)));
			assertEquals("split into two", 2, r.length);
		}

		@Test
		public void triangulate_oi1rv1ie_s2() {
			// p1 outside, p2 inside, 1 real intersection (vertex), 1 imaginary
			// intersection (edge), split into 2 pieces
			Polygon[] r = triangulate(p,
					new Line(new Point(50, 400), new Point(150, 200)));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(new Point(50, 0), new Point(150, 200)));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p,
					new Line(new Point(400, 200), new Point(150, 200)));
			assertEquals("split into two", 2, r.length);
		}

		@Test
		public void triangulate_oo_ntd() {
			// p1 outside, p2 outside, nothing to do
			Polygon[] r = triangulate(p,
					new Line(new Point(0, 0), new Point(400, 0)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);
		}

		@Test
		public void triangulate_oo_overlaps_edge_ntd() {
			// p1 outside, p2 outside, overlaps edge, nothing to do
			Polygon[] r = triangulate(p,
					new Line(new Point(0, 50), new Point(400, 250)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(0, 350), new Point(400, 150)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(100, 50), new Point(100, 350)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);
		}

		@Test
		public void triangulate_oo1rv_ntd() {
			// p1 outside, p2 outside, 1 real intersection (vertex), nothing to
			// do
			Polygon[] r = triangulate(p,
					new Line(new Point(0, 100), new Point(200, 100)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(0, 300), new Point(200, 300)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(300, 100), new Point(300, 300)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);
		}

		@Test
		public void triangulate_oo2ree_s3() {
			// p1 outside, p2 outside, 2 real intersections (edge, edge), split
			// into
			// 3 pieces
			Polygon[] r = triangulate(p,
					new Line(new Point(200, 100), new Point(200, 300)));
			assertEquals("split into three", 3, r.length);
			// TODO: verify that the created three polygons are those you wanted
			// to
			// get back

			r = triangulate(p,
					new Line(new Point(50, 150), new Point(250, 300)));
			assertEquals("split into three", 3, r.length);

			r = triangulate(p,
					new Line(new Point(50, 250), new Point(250, 100)));
			assertEquals("split into three", 3, r.length);
		}

		@Test
		public void triangulate_oo2rve_s2() {
			// p1 outside, p2 outside, 2 real intersections (vertex, edge),
			// split into 2 pieces
			Polygon[] r = triangulate(p,
					new Line(new Point(50, 200), new Point(350, 200)));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p,
					new Line(new Point(50, 50), new Point(300, 300)));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p,
					new Line(new Point(50, 350), new Point(350, 50)));
			assertEquals("split into two", 2, r.length);
		}

		@Test
		public void triangulate_ov1rv_ntd() {
			// p1 outside, p2 on vertex, 1 real intersection (vertex), nothing
			// to do
			Polygon[] r = triangulate(p,
					new Line(new Point(200, 100), new Point(100, 100)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(200, 300), new Point(100, 300)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(300, 300), new Point(300, 200)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);
		}

		@Test
		public void triangulate_precision_error() {
			Polygon t = new Polygon(100.0, 100.0, 371.1146624051138,
					197.80263683579705, 370.0, 189.99999999999997);
			Line l = new Line(370.0, 190.0, 400.0, 400.0);

			// throws an exception if it fails
			triangulate(t, l);
		}

		@Test
		public void triangulate_ve_overlaps_edge_ntd() {
			// p1 on vertex, p2 on edge, overlaps edge, nothing to do
			Polygon[] r = triangulate(p,
					new Line(new Point(100, 100), new Point(100, 200)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(100, 100), new Point(150, 125)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(300, 200), new Point(150, 125)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(300, 200), new Point(150, 275)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(100, 300), new Point(150, 275)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(100, 300), new Point(100, 200)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);
		}

		@Test
		public void triangulate_ve2rve_s2() {
			// p1 on vertex, p2 on edge, 2 real intersections (vertex, edge),
			// split
			// into 2 pieces
			Polygon[] r = triangulate(p, new Line(100, 100, 200, 250));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(300, 200, 100, 200));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(100, 300, 200, 150));
			assertEquals("split into two", 2, r.length);
		}

		@Test
		public void triangulate_vi1rv1ie_s2() {
			// p1 on vertex, p2 inside, 1 real intersection (vertex), 1
			// imaginary
			// intersection (edge), split into 2 pieces
			Polygon[] r = triangulate(p, new Line(100, 100, 200, 250));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(300, 200, 100, 200));
			assertEquals("split into two", 2, r.length);

			r = triangulate(p, new Line(100, 300, 200, 150));
			assertEquals("split into two", 2, r.length);
		}

		@Test
		public void triangulate_vo1rv_ntd() {
			// p1 on vertex, p2 outside, 1 real intersection (vertex), nothing
			// to do
			Polygon[] r = triangulate(p,
					new Line(new Point(100, 100), new Point(200, 100)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(100, 300), new Point(200, 300)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(300, 200), new Point(300, 300)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);
		}

		@Test
		public void triangulate_vv_overlaps_edge_ntd() {
			// p1 on vertex, p2 on vertex, overlaps edge, nothing to do
			Polygon[] r = triangulate(p,
					new Line(new Point(100, 100), new Point(100, 300)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(100, 100), new Point(300, 200)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);

			r = triangulate(p,
					new Line(new Point(300, 200), new Point(100, 300)));
			assertEquals("nothing to do", 1, r.length);
			assertEquals("polygon remains the same", p, r[0]);
		}

	}

	public static class TriangulateTriangleWithOutlinePoints {

		private static boolean exists(Polygon[] list, Polygon item) {
			for (Polygon p : list) {
				if (p.equals(item)) {
					return true;
				}
			}
			return false;
		}

		Polygon p;

		@Test
		public void both_points_on_first_edge() {
			// inner
			Polygon[] r = triangulate(p, new Point(100, 150),
					new Point(100, 250));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			r = triangulate(p, new Point(100, 250), new Point(100, 150));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			// start - inner
			r = triangulate(p, new Point(100, 100), new Point(100, 200));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			r = triangulate(p, new Point(100, 200), new Point(100, 100));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			// end - inner
			r = triangulate(p, new Point(100, 300), new Point(100, 200));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			r = triangulate(p, new Point(100, 200), new Point(100, 300));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			// start - end
			r = triangulate(p, new Point(100, 100), new Point(100, 300));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			r = triangulate(p, new Point(100, 300), new Point(100, 100));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);
		}

		@Test
		public void both_points_on_second_edge() {
			// inner
			Polygon[] r = triangulate(p, new Point(250, 225),
					new Point(150, 275));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			r = triangulate(p, new Point(150, 275), new Point(250, 225));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			// start - inner
			r = triangulate(p, new Point(300, 200), new Point(200, 250));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			r = triangulate(p, new Point(200, 250), new Point(300, 200));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			// end - inner
			r = triangulate(p, new Point(100, 300), new Point(200, 250));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			r = triangulate(p, new Point(200, 250), new Point(100, 300));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			// start - end
			r = triangulate(p, new Point(100, 300), new Point(300, 200));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			r = triangulate(p, new Point(300, 200), new Point(100, 300));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);
		}

		@Test
		public void both_points_on_third_edge() {
			// inner
			Polygon[] r = triangulate(p, new Point(150, 125),
					new Point(250, 175));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			r = triangulate(p, new Point(250, 175), new Point(150, 125));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			// start - inner
			r = triangulate(p, new Point(100, 100), new Point(250, 175));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			r = triangulate(p, new Point(250, 175), new Point(100, 100));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			// end - inner
			r = triangulate(p, new Point(150, 125), new Point(300, 200));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			r = triangulate(p, new Point(300, 200), new Point(150, 125));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			// start - end
			r = triangulate(p, new Point(100, 100), new Point(300, 200));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);

			r = triangulate(p, new Point(300, 200), new Point(100, 100));
			assertEquals("1 resulting polygon", 1, r.length);
			assertEquals("result equal to original", r[0], p);
			assertNotSame("a copy is returned", r[0], p);
		}

		@Test
		public void edge1_and_edge2() {
			Polygon[] r = triangulate(p, new Point(100, 200),
					new Point(200, 250));
			assertEquals("3 resulting polygons", 3, r.length);
			assertEquals("isolated-vertex polygon exists", true,
					exists(r, new Polygon(new Point(100, 300),
							new Point(100, 200), new Point(200, 250))));
			// TODO: test for existence of the other two pieces

			r = triangulate(p, new Point(200, 250), new Point(100, 200));
			assertEquals("3 resulting polygons", 3, r.length);
			assertEquals("isolated-vertex polygon exists", true,
					exists(r, new Polygon(new Point(100, 300),
							new Point(100, 200), new Point(200, 250))));
			// TODO: test for existence of the other two pieces
		}

		@Test
		public void edge1_and_edge3() {
			Polygon[] r = triangulate(p, new Point(100, 200),
					new Point(200, 150));
			assertEquals("3 resulting polygons", 3, r.length);
			assertEquals("isolated-vertex polygon exists", true,
					exists(r, new Polygon(new Point(100, 100),
							new Point(100, 200), new Point(200, 150))));
			// TODO: test for existence of the other two pieces

			r = triangulate(p, new Point(200, 150), new Point(100, 200));
			assertEquals("3 resulting polygons", 3, r.length);
			assertEquals("isolated-vertex polygon exists", true,
					exists(r, new Polygon(new Point(100, 100),
							new Point(100, 200), new Point(200, 150))));
			// TODO: test for existence of the other two pieces
		}

		@Test
		public void edge1_vertex3() {
			Polygon[] r = triangulate(p, new Point(100, 200),
					new Point(300, 200));
			assertEquals("2 resulting polygons", 2, r.length);
			assertEquals("left-side polygon exists", true,
					exists(r, new Polygon(100, 100, 100, 200, 300, 200)));
			assertEquals("right-side polygon exists", true,
					exists(r, new Polygon(100, 300, 100, 200, 300, 200)));

			r = triangulate(p, new Point(300, 200), new Point(100, 200));
			assertEquals("2 resulting polygons", 2, r.length);
			assertEquals("left-side polygon exists", true,
					exists(r, new Polygon(100, 100, 100, 200, 300, 200)));
			assertEquals("right-side polygon exists", true,
					exists(r, new Polygon(100, 300, 100, 200, 300, 200)));
		}

		@Test
		public void edge2_and_edge3() {
			Polygon[] r = triangulate(p, new Point(200, 250),
					new Point(200, 150));
			assertEquals("3 resulting polygons", 3, r.length);
			assertEquals("isolated-vertex polygon exists", true,
					exists(r, new Polygon(new Point(200, 250),
							new Point(200, 150), new Point(300, 200))));
			// TODO: test for existence of the other two pieces

			r = triangulate(p, new Point(200, 150), new Point(200, 250));
			assertEquals("3 resulting polygons", 3, r.length);
			assertEquals("isolated-vertex polygon exists", true,
					exists(r, new Polygon(new Point(200, 250),
							new Point(200, 150), new Point(300, 200))));
			// TODO: test for existence of the other two pieces
		}

		@Test
		public void edge2_vertex1() {
			Polygon[] r = triangulate(p, new Point(200, 250),
					new Point(100, 100));
			assertEquals("2 resulting polygons", 2, r.length);
			assertEquals("left-side polygon exists", true,
					exists(r, new Polygon(100, 100, 200, 250, 100, 300)));
			assertEquals("right-side polygon exists", true,
					exists(r, new Polygon(100, 100, 200, 250, 300, 200)));

			r = triangulate(p, new Point(100, 100), new Point(200, 250));
			assertEquals("2 resulting polygons", 2, r.length);
			assertEquals("left-side polygon exists", true,
					exists(r, new Polygon(100, 100, 200, 250, 100, 300)));
			assertEquals("right-side polygon exists", true,
					exists(r, new Polygon(100, 100, 200, 250, 300, 200)));
		}

		@Test
		public void edge3_vertex2() {
			Polygon[] r = triangulate(p, new Point(200, 150),
					new Point(100, 300));
			assertEquals("2 resulting polygons", 2, r.length);
			assertEquals("left-side polygon exists", true,
					exists(r, new Polygon(200, 150, 100, 300, 100, 100)));
			assertEquals("right-side polygon exists", true,
					exists(r, new Polygon(200, 150, 100, 300, 300, 200)));

			r = triangulate(p, new Point(100, 300), new Point(200, 150));
			assertEquals("2 resulting polygons", 2, r.length);
			assertEquals("left-side polygon exists", true,
					exists(r, new Polygon(200, 150, 100, 300, 100, 100)));
			assertEquals("right-side polygon exists", true,
					exists(r, new Polygon(200, 150, 100, 300, 300, 200)));
		}

		@Test
		public void no_polygon() {
			try {
				triangulate(null, new Point(0, 0), new Point(1, 1));
			} catch (IllegalStateException x) {
				Throwable cause = x;
				while (cause.getCause() != null) {
					cause = cause.getCause();
				}

				assertTrue(cause.getClass()
						.equals(IllegalArgumentException.class));
			}
		}

		@Test
		public void no_triangle() {
			try {
				triangulate(new Polygon(0, 0, 1, 0, 1, 1, 0, 1),
						new Point(0, 0), new Point(1, 1));
			} catch (IllegalStateException x) {
				Throwable cause = x;
				while (cause.getCause() != null) {
					cause = cause.getCause();
				}

				assertTrue(cause.getClass()
						.equals(IllegalArgumentException.class));
			}
		}

		@Test
		public void p1_not_on_polygon() {
			try {
				triangulate(new Polygon(0, 0, 1, 1, 0, 1), new Point(1, 0),
						new Point(0, 1));
			} catch (IllegalStateException x) {
				Throwable cause = x;
				while (cause.getCause() != null) {
					cause = cause.getCause();
				}

				assertTrue(cause.getClass()
						.equals(IllegalArgumentException.class));
			}
		}

		@Test
		public void p2_not_on_polygon() {
			try {
				triangulate(new Polygon(0, 0, 1, 1, 0, 1), new Point(0, 1),
						new Point(1, 0));
			} catch (IllegalStateException x) {
				Throwable cause = x;
				while (cause.getCause() != null) {
					cause = cause.getCause();
				}

				assertTrue(cause.getClass()
						.equals(IllegalArgumentException.class));
			}
		}

		@Before
		public void setUp() {
			p = new Polygon(new Point(100, 100), new Point(100, 300),
					new Point(300, 200));
		}

		private Polygon[] triangulate(Polygon p, Point p1, Point p2) {
			try {
				Class<?> parameterTypes[] = new Class<?>[] { Polygon.class,
						Point.class, Point.class };
				Method triangulate = Ring.class.getDeclaredMethod("triangulate",
						parameterTypes);
				triangulate.setAccessible(true);
				return (Polygon[]) triangulate.invoke(null, p, p1, p2);
			} catch (Exception x) {
				throw new IllegalStateException(x);
			}
		}

	}

}
