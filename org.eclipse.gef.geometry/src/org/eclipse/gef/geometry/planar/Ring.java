/*******************************************************************************
 * Copyright (c) 2011, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.planar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.euclidean.Straight;
import org.eclipse.gef.geometry.euclidean.Vector;

/**
 * A combination of Polygons....
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class Ring extends AbstractMultiShape
		implements ITranslatable<Ring>, IScalable<Ring>, IRotatable<Ring> {

	/**
	 * Triangulates the given triangle ({@link Polygon}) at the given
	 * {@link Line}. The triangulation is done using the simpler
	 * {@link #triangulate(Polygon, Point, Point)} method. The real and
	 * imaginary {@link Point}s of intersection of the {@link Line} and the
	 * {@link Polygon} are used as the split {@link Point}s.
	 *
	 * The triangulation is only done, if the {@link Line} intersects the
	 * {@link Polygon}, i.e. at least one {@link Point} of the {@link Line} lies
	 * inside the {@link Polygon} but not on its outline.
	 *
	 * @param p
	 *            the triangle ({@link Polygon}) to triangulate
	 * @param l
	 *            the line that determines the split {@link Point}s
	 * @return at least one and up to three {@link Polygon}s which are the
	 *         resulting triangles
	 */
	private static Polygon[] triangulate(Polygon p, Line l) {
		if (p == null) {
			throw new IllegalArgumentException(
					"The given Polygon parameter may not be null.");
		}
		if (l == null) {
			throw new IllegalArgumentException(
					"The given Line parameter may not be null.");
		}

		boolean intersecting = l.getIntersections(p.getOutline()).length == 2;

		if (!intersecting) {
			// test if at least one point of the line is really inside the
			// polygon
			for (Point lp : l.getPoints()) {
				if (p.contains(lp) && !p.getOutline().contains(lp)) {
					intersecting = true;
					break;
				}
			}

			if (!intersecting) {
				return new Polygon[] { p.getCopy() };
			}
		}

		// calculate real and imaginary intersection points
		Straight s = new Straight(l);
		Point inters[] = new Point[2];
		int i = 0;

		for (Line e : p.getOutlineSegments()) {
			Vector vi = s.getIntersection(new Straight(e));
			if (vi != null) {
				Point poi = vi.toPoint();
				if (e.contains(poi)) {
					if (i > 0 && inters[0].equals(poi)) {
						continue;
					} else {
						inters[i++] = poi;
					}
				}
			}
			if (i > 1) {
				break;
			}
		}

		if (inters[0] == null || inters[1] == null) {
			throw new IllegalStateException(
					"The determined points of intersection do not lie on the polygon. Call: triangulate("
							+ p + ", " + l + ")");
		}

		return triangulate(p, inters[0], inters[1]);
	}

	/**
	 * <p>
	 * Splits a triangle at the line through points p1 and p2, which are
	 * required to lie on the outline of the triangle.
	 * </p>
	 *
	 * <p>
	 * If the points p1 and p2 lie on the same edge on the triangle, a copy of
	 * the given {@link Polygon} is returned.
	 * </p>
	 *
	 * <p>
	 * If one of the points lies on an edge, and the other point lies on a
	 * vertex of the triangle, two {@link Polygon}s are returned. They represent
	 * the areas left and right to the line through p1 and p2.
	 * </p>
	 *
	 * <p>
	 * If both points lie on different edges, three {@link Polygon}s are
	 * returned. One of them represents the triangle which lies on one side of
	 * the line through p1 and p2. The other two triangles are the triangulation
	 * of the tetragon on the other side of the line through p1 and p2.
	 * </p>
	 *
	 * @param p
	 * @param p1
	 * @param p2
	 * @return
	 */
	private static Polygon[] triangulate(Polygon p, Point p1, Point p2) {
		Point[] v = p == null ? new Point[] {} : p.getPoints();
		if (v.length != 3) {
			throw new IllegalArgumentException(
					"Only triangles are allowed as the Polygon parameter.");
		}
		if (p1 == null) {
			throw new IllegalArgumentException(
					"The given p1 Point parameter may not be null.");
		}
		if (p2 == null) {
			throw new IllegalArgumentException(
					"The given p2 Point parameter may not be null.");
		}

		Line[] e = new Line[] { new Line(v[0], v[1]), new Line(v[1], v[2]),
				new Line(v[2], v[0]) };

		// determine the edges on which the points lie
		boolean p1_on_e0 = e[0].contains(p1);
		boolean p1_on_e1 = e[1].contains(p1);
		boolean p1_on_e2 = e[2].contains(p1);
		boolean p2_on_e0 = e[0].contains(p2);
		boolean p2_on_e1 = e[1].contains(p2);
		boolean p2_on_e2 = e[2].contains(p2);

		// if both points lie on the same edge, we have nothing to do
		if (p1_on_e0 && p2_on_e0 || p1_on_e1 && p2_on_e1
				|| p1_on_e2 && p2_on_e2) {
			return new Polygon[] { p.getCopy() };
		}

		// check if both points are on the triangle
		else if (!(p1_on_e0 || p1_on_e1 || p1_on_e2)
				|| !(p2_on_e0 || p2_on_e1 || p2_on_e2)) {
			throw new IllegalArgumentException(
					"The Point objects have to lie on the outline of the Polygon object.");
		}

		// determine if one of the points lies on a vertex
		else if (p1.equals(v[0])) {
			return new Polygon[] { new Polygon(v[0], v[1], p2),
					new Polygon(v[0], v[2], p2) };
		} else if (p1.equals(v[1])) {
			return new Polygon[] { new Polygon(v[0], v[1], p2),
					new Polygon(v[1], v[2], p2) };
		} else if (p1.equals(v[2])) {
			return new Polygon[] { new Polygon(v[0], v[2], p2),
					new Polygon(v[1], v[2], p2) };
		} else if (p2.equals(v[0])) {
			return new Polygon[] { new Polygon(v[0], v[1], p1),
					new Polygon(v[0], v[2], p1) };
		} else if (p2.equals(v[1])) {
			return new Polygon[] { new Polygon(v[0], v[1], p1),
					new Polygon(v[1], v[2], p1) };
		} else if (p2.equals(v[2])) {
			return new Polygon[] { new Polygon(v[0], v[2], p1),
					new Polygon(v[1], v[2], p1) };
		}

		// both points on different edges, determine isolated vertex
		else if (p1_on_e0 && p2_on_e2 || p1_on_e2 && p2_on_e0) {
			// v0 isolated
			return new Polygon[] { new Polygon(v[0], p1, p2),
					new Polygon(p1, p2, v[1]),
					new Polygon(p1_on_e0 ? p2 : p1, v[1], v[2]) };
		} else if (p1_on_e0 && p2_on_e1 || p1_on_e1 && p2_on_e0) {
			// v1 isolated
			return new Polygon[] { new Polygon(v[1], p1, p2),
					new Polygon(p1, p2, v[0]),
					new Polygon(p1_on_e0 ? p2 : p1, v[0], v[2]) };
		} else if (p1_on_e1 && p2_on_e2 || p1_on_e2 && p2_on_e1) {
			// v2 isolated
			return new Polygon[] { new Polygon(v[2], p1, p2),
					new Polygon(p1, p2, v[1]),
					new Polygon(p1_on_e1 ? p2 : p1, v[1], v[0]) };
		} else {
			throw new IllegalStateException(
					"Unreachable, because for two points on a triangle, they have to be located either (edge, edge), (vertex, edge), or (edge, vertex).");
		}
	}

	private static final long serialVersionUID = 1L;

	private ArrayList<Polygon> triangles;

	/**
	 * Constructs a new empty {@link Ring}.
	 */
	public Ring() {
		triangles = new ArrayList<>();
	}

	/**
	 * Constructs a new {@link Ring} from the given {@link Polygon}s.
	 *
	 * @param polygons
	 *            The array of {@link Polygon}s from which this {@link Ring} is
	 *            constructed.
	 */
	public Ring(Polygon... polygons) {
		this();
		for (Polygon p : polygons) {
			add(p);
		}
	}

	/**
	 * Constructs a new {@link Ring} from the given other {@link Ring}. The
	 * internal {@link IShape}s of the other {@link Ring} are copied to prevent
	 * actions at a distance.
	 *
	 * @param other
	 *            The {@link Ring} from which this {@link Ring} is constructed.
	 */
	public Ring(Ring other) {
		this();
		for (Polygon p : other.triangles) {
			add(p);
		}
	}

	/**
	 * Adds the given {@link Polygon} to this {@link Ring}.
	 *
	 * @param p
	 *            The {@link Polygon} which is added to this {@link Ring}.
	 * @return <code>this</code> for convenience
	 */
	public Ring add(Polygon p) {
		Stack<Polygon> toAdd = new Stack<>();
		for (Polygon triangleToAdd : p.getTriangulation()) {
			// do not add "empty" triangles
			if (triangleToAdd.getArea() != 0) {
				toAdd.push(triangleToAdd);
			}
		}

		while (!toAdd.empty()) {
			Polygon triangleToAdd = toAdd.pop();
			Stack<Polygon> localAddends = new Stack<>();
			localAddends.push(triangleToAdd);
			for (Polygon triangleAlreadyThere : triangles) {
				for (Line e : triangleAlreadyThere.getOutlineSegments()) {
					Stack<Polygon> nextAddends = new Stack<>();
					for (Iterator<Polygon> i = localAddends.iterator(); i
							.hasNext();) {
						Polygon addend = i.next();
						i.remove();
						for (Polygon subTriangleToAdd : triangulate(addend,
								e)) {
							if (!triangleAlreadyThere
									.contains(subTriangleToAdd)) {
								nextAddends.push(subTriangleToAdd);
							}
						}
					}
					localAddends = nextAddends;
				}
			}
			for (Polygon addend : localAddends) {
				triangles.add(addend);
			}
		}

		optimizeTriangles();

		return this;
	}

	@Override
	public boolean contains(IGeometry g) {
		return ShapeUtils.contains(this, g);
	}

	@Override
	public boolean equals(Object obj) {
		// TODO: Invent a better algorithm.
		if (obj instanceof Ring) {
			Ring o = (Ring) obj;
			return contains(o) && o.contains(this);
		}
		return false;
	}

	private boolean findSharedAndOuterVertices(Polygon t1, Polygon t2,
			Point[] shared, Point[] outer) {
		Point[] t1Points = t1.getPoints();
		Point[] t2Points = t2.getPoints();
		boolean[] t1IsShared = new boolean[] { false, false, false };
		boolean[] t2IsShared = new boolean[] { false, false, false };

		int sc = 0;
		for (int i = 0; i < t1Points.length; i++) {
			for (int j = 0; j < t2Points.length; j++) {
				if (t1Points[i].equals(t2Points[j])) {
					if (sc++ == 2) {
						return false;
					}
					t1IsShared[i] = true;
					t2IsShared[j] = true;
				}
			}
		}
		if (sc != 2) {
			return false;
		}

		for (int i = 0, c = 0; i < t1Points.length; i++) {
			if (t1IsShared[i]) {
				shared[c++] = t1Points[i];
			} else {
				outer[0] = t1Points[i];
			}

			if (!t2IsShared[i]) {
				outer[1] = t2Points[i];
			}
		}

		return true;
	}

	@Override
	protected Line[] getAllEdges() {
		Stack<Line> edges = new Stack<>();

		for (Polygon t : triangles) {
			for (Line e : t.getOutlineSegments()) {
				edges.push(e);
			}
		}
		return edges.toArray(new Line[] {});
	}

	@Override
	public Rectangle getBounds() {
		if (triangles.size() == 0) {
			return null;
		}

		Rectangle bounds = triangles.get(0).getBounds();
		for (int i = 1; i < triangles.size(); i++) {
			bounds.union(triangles.get(i).getBounds());
		}

		return bounds;
	}

	@Override
	public Ring getCopy() {
		return new Ring(this);
	}

	@Override
	public Ring getRotatedCCW(Angle angle) {
		return getCopy().rotateCCW(angle);
	}

	@Override
	public Ring getRotatedCCW(Angle angle, double cx, double cy) {
		return getCopy().rotateCCW(angle, cx, cy);
	}

	@Override
	public Ring getRotatedCCW(Angle angle, Point center) {
		return getCopy().rotateCCW(angle, center);
	}

	@Override
	public Ring getRotatedCW(Angle angle) {
		return getCopy().rotateCW(angle);
	}

	@Override
	public Ring getRotatedCW(Angle angle, double cx, double cy) {
		return getCopy().rotateCW(angle, cx, cy);
	}

	@Override
	public Ring getRotatedCW(Angle angle, Point center) {
		return getCopy().rotateCW(angle, center);
	}

	@Override
	public Ring getScaled(double factor) {
		return getCopy().scale(factor);
	}

	@Override
	public Ring getScaled(double fx, double fy) {
		return getCopy().scale(fx, fy);
	}

	@Override
	public Ring getScaled(double factor, double cx, double cy) {
		return getCopy().scale(factor, cx, cy);
	}

	@Override
	public Ring getScaled(double fx, double fy, double cx, double cy) {
		return getCopy().scale(fx, fy, cx, cy);
	}

	@Override
	public Ring getScaled(double fx, double fy, Point center) {
		return getCopy().scale(fx, fy, center);
	}

	@Override
	public Ring getScaled(double factor, Point center) {
		return getCopy().scale(factor, center);
	}

	@Override
	public Polygon[] getShapes() {
		return triangles.toArray(new Polygon[] {});
	}

	@Override
	public Ring getTransformed(AffineTransform t) {
		List<Polygon> transformedTriangles = new ArrayList<>();
		for (Polygon p : triangles) {
			transformedTriangles.add(p.getTransformed(t));
		}
		return new Ring(transformedTriangles.toArray(new Polygon[] {}));
	}

	@Override
	public Ring getTranslated(double dx, double dy) {
		return getCopy().translate(dx, dy);
	}

	@Override
	public Ring getTranslated(Point d) {
		return getCopy().translate(d.x, d.y);
	}

	private Polygon mergeTriangles(Polygon t1, Polygon t2) {
		Point[] shared = new Point[2], outer = new Point[2];
		boolean found = findSharedAndOuterVertices(t1, t2, shared, outer);
		if (found) {
			Line outerLink = new Line(outer[0], outer[1]);
			if (outerLink.contains(shared[0])) {
				return new Polygon(outer[0], outer[1], shared[1]);
			} else if (outerLink.contains(shared[1])) {
				return new Polygon(outer[0], outer[1], shared[0]);
			}
		}

		return null;
	}

	private void optimizeTriangles() {
		for (int i = 0; i < triangles.size(); i++) {
			Polygon t1 = triangles.get(i);
			for (int j = i + 1; j < triangles.size(); j++) {
				Polygon t2 = triangles.get(j);
				Polygon merge = mergeTriangles(t1, t2);
				if (merge != null) {
					triangles.set(i, merge);
					t1 = merge;
					triangles.remove(j);
					j = i;
				}
			}
		}
	}

	/**
	 * Directly rotates this {@link Ring} counter-clock-wise around its center
	 * {@link Point} by the given {@link Angle}. Direct adaptation means, that
	 * <code>this</code> {@link PolyBezier} is modified in-place.
	 *
	 * @param angle
	 *            rotation {@link Angle}
	 * @return <code>this</code> for convenience
	 */
	public Ring rotateCCW(Angle angle) {
		Point centroid = getBounds().getCenter();
		return rotateCCW(angle, centroid.x, centroid.y);
	}

	/**
	 * Directly rotates this {@link Ring} counter-clock-wise around the given
	 * point (specified by cx and cy) by the given {@link Angle}. Direct
	 * adaptation means, that <code>this</code> {@link PolyBezier} is modified
	 * in-place.
	 *
	 * @param angle
	 *            rotation {@link Angle}
	 * @param cx
	 *            x-coordinate of the {@link Point} to rotate around
	 * @param cy
	 *            y-coordinate of the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public Ring rotateCCW(Angle angle, double cx, double cy) {
		for (Polygon p : triangles) {
			p.rotateCCW(angle, cx, cy);
		}
		return this;
	}

	/**
	 * Directly rotates this {@link Ring} counter-clock-wise around the given
	 * {@link Point} by the given {@link Angle}. Direct adaptation means, that
	 * <code>this</code> {@link PolyBezier} is modified in-place.
	 *
	 * @param angle
	 *            rotation {@link Angle}
	 * @param center
	 *            {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public Ring rotateCCW(Angle angle, Point center) {
		return rotateCCW(angle, center.x, center.y);
	}

	/**
	 * Directly rotates this {@link Ring} clock-wise around its center
	 * {@link Point} by the given {@link Angle}. Direct adaptation means, that
	 * <code>this</code> {@link PolyBezier} is modified in-place.
	 *
	 * @param angle
	 *            rotation {@link Angle}
	 * @return <code>this</code> for convenience
	 */
	public Ring rotateCW(Angle angle) {
		Point centroid = getBounds().getCenter();
		return rotateCW(angle, centroid.x, centroid.y);
	}

	/**
	 * Directly rotates this {@link Ring} clock-wise around the given point
	 * (specified by cx and cy) by the given {@link Angle}. Direct adaptation
	 * means, that <code>this</code> {@link PolyBezier} is modified in-place.
	 *
	 * @param angle
	 *            rotation {@link Angle}
	 * @param cx
	 *            x-coordinate of the {@link Point} to rotate around
	 * @param cy
	 *            y-coordinate of the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public Ring rotateCW(Angle angle, double cx, double cy) {
		for (Polygon p : triangles) {
			p.rotateCW(angle, cx, cy);
		}
		return this;
	}

	/**
	 * Directly rotates this {@link Ring} clock-wise around the given
	 * {@link Point} by the given {@link Angle}. Direct adaptation means, that
	 * <code>this</code> {@link PolyBezier} is modified in-place.
	 *
	 * @param angle
	 *            rotation {@link Angle}
	 * @param center
	 *            {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public Ring rotateCW(Angle angle, Point center) {
		return rotateCW(angle, center.x, center.y);
	}

	@Override
	public Ring scale(double factor) {
		return scale(factor, factor);
	}

	@Override
	public Ring scale(double fx, double fy) {
		Point center = getBounds().getCenter();
		return scale(fx, fy, center.x, center.y);
	}

	@Override
	public Ring scale(double factor, double cx, double cy) {
		return scale(factor, factor, cx, cy);
	}

	@Override
	public Ring scale(double fx, double fy, double cx, double cy) {
		for (Polygon p : triangles) {
			p.scale(fx, fy, cx, cy);
		}
		return this;
	}

	@Override
	public Ring scale(double fx, double fy, Point center) {
		return scale(fx, fy, center.x, center.y);
	}

	@Override
	public Ring scale(double factor, Point center) {
		return scale(factor, factor, center.x, center.y);
	}

	@Override
	public Ring translate(double dx, double dy) {
		for (Polygon p : triangles) {
			p.translate(dx, dy);
		}
		return this;
	}

	@Override
	public Ring translate(Point d) {
		return translate(d.x, d.y);
	}

}
