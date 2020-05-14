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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.geometry.internal.utils.PointListUtils;
import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;

/**
 * Represents the geometric shape of a convex polygon.
 *
 * Note that while all manipulations (e.g. within shrink, expand) within this
 * class are based on double precision, all comparisons (e.g. within contains,
 * intersects, equals, etc.) are based on a limited precision (with an accuracy
 * defined within {@link PrecisionUtils}) to compensate for rounding effects.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class Polygon extends AbstractPointListBasedGeometry<Polygon>
		implements IShape {

	/**
	 * Pair of {@link Line} segment and integer counter to count segments of
	 * {@link Polygon}s.
	 */
	private class SegmentCounter {
		public Line segment;
		public int count;

		public SegmentCounter(Line segment, int count) {
			this.segment = segment;
			this.count = count;
		}
	}

	/**
	 * List of {@link SegmentCounter}s to count segments of {@link Polygon}s.
	 */
	private class SegmentList {
		public ArrayList<SegmentCounter> segmentCounterList;

		public SegmentList() {
			segmentCounterList = new ArrayList<>();
		}

		public SegmentCounter find(Line segment) {
			for (SegmentCounter i : segmentCounterList) {
				if (segment.equals(i.segment)) {
					return i;
				}
			}
			// segment not in list, create new segment counter for it
			SegmentCounter newSegCounter = new SegmentCounter(segment, 0);
			segmentCounterList.add(newSegCounter);
			return newSegCounter;
		}
	}

	private static Polygon clipEar(Polygon p, int[] ear,
			ArrayList<Polygon> ears) {
		Point[] points = p.getPoints();
		ears.add(new Polygon(points[ear[0]], points[ear[1]], points[ear[2]]));
		return new Polygon(getPointsWithout(points, ear[1]));
	}

	/**
	 * Searches the given list of {@link Point}s for a vertex that starts an
	 * ear. An ear is a list of 3 vertices which build up a triangle that lies
	 * inside the {@link Polygon} respective to the list of {@link Point}s and
	 * can be clipped out of it so that the remaining {@link Polygon} remains
	 * simple.
	 *
	 * @param points
	 * @return
	 */
	private static int[] findEarVertex(Polygon p) {
		Point[] points = p.getPoints();

		for (int start = 0; start < points.length; start++) {
			int mid = start == points.length - 1 ? 0 : start + 1;
			int end = start == points.length - 2 ? 0
					: start == points.length - 1 ? 1 : start + 2;

			if (p.contains(new Line(points[start], points[end]))) {
				return new int[] { start, mid, end };
			}
		}

		// this should never happen (for simple polygons)
		return null;
	}

	private static Point[] getPointsWithout(Point[] points,
			int... indicesToRemove) {
		Point[] rest = new Point[points.length - indicesToRemove.length];
		Arrays.sort(indicesToRemove);
		for (int i = 0, j = 0; i < indicesToRemove.length; i++) {
			for (int r = j; r < indicesToRemove[i]; r++) {
				rest[r - i] = points[r];
			}
			j = indicesToRemove[i] + 1;
		}
		for (int i = indicesToRemove[indicesToRemove.length - 1]
				+ 1; i < points.length; i++) {
			rest[i - indicesToRemove.length] = points[i];
		}
		return rest;
	}

	/**
	 * Clips exactly one ear off of the given {@link Polygon} and adds it to the
	 * list of ears. If the resulting {@link Polygon} is a triangle, this is
	 * added to the list of ears, too. Otherwise, the method recurses.
	 *
	 * @param p
	 * @param ears
	 */
	private static void triangulate(Polygon p, ArrayList<Polygon> ears) {
		if (p == null) {
			throw new IllegalArgumentException(
					"The given Polygon may not be null.");
		}
		if (ears == null) {
			throw new IllegalArgumentException(
					"The given ear-list may not be null.");
		}
		if (p.points.length < 3) {
			throw new IllegalArgumentException(
					"The given Polygon may not have less than three vertices.");
		}

		if (p.points.length == 3) {
			ears.add(p.getCopy());
			return;
		}

		int[] ear = findEarVertex(p);
		Polygon rest = clipEar(p, ear, ears);

		// recurse
		triangulate(rest, ears);
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@link Polygon} from a even-numbered sequence of
	 * coordinates.
	 *
	 * @param coordinates
	 *            an alternating, even-numbered sequence of x and y coordinates,
	 *            representing the {@link Point}s from which the {@link Polygon}
	 *            is to be created
	 * @see #Polygon(Point...)
	 */
	public Polygon(double... coordinates) {
		super(coordinates);
	}

	/**
	 * Constructs a new {@link Polygon} from the given sequence of {@link Point}
	 * s. The {@link Polygon} that is created will be automatically closed, i.e.
	 * it will not only contain a segment between succeeding points of the
	 * sequence but as well back from the last to the first point.
	 *
	 * @param points
	 *            a sequence of points, from which the {@link Polygon} is to be
	 *            created.
	 */
	public Polygon(Point... points) {
		super(points);
	}

	/**
	 * Assures that this {@link Polygon} is simple, i.e. it does not have any
	 * self-intersections. We do not need to test for voids as they are not
	 * considered in the interpretation of the {@link Polygon}'s {@link Point}s.
	 *
	 * If the {@link Polygon} does not have at least three vertices, a
	 * {@link IllegalStateException} is thrown.
	 *
	 * The edges are added to the {@link Polygon} one after the other. If a
	 * self-intersection is found an {@link IllegalStateException} is thrown.
	 */
	private void assureSimplicity() {
		if (points.length < 3) {
			throw new IllegalStateException(
					"A polygon can only be constructed of at least 3 vertices.");
		}

		for (Line e1 : getOutlineSegments()) {
			for (Line e2 : getOutlineSegments()) {
				if (!e1.getP1().equals(e2.getP1())
						&& !e1.getP2().equals(e2.getP1())
						&& !e1.getP1().equals(e2.getP2())
						&& !e1.getP2().equals(e2.getP2())) {
					if (e1.touches(e2)) {
						throw new IllegalStateException(
								"Only simple polygons allowed. A polygon without any self-intersections is considered to be simple. This polygon is not simple.");
					}
				}
			}
		}
	}

	/**
	 * Checks whether the point that is represented by its x- and y-coordinates
	 * is contained within this {@link Polygon}.
	 *
	 * @param x
	 *            the x-coordinate of the point to test
	 * @param y
	 *            the y-coordinate of the point to test
	 * @return <code>true</code> if the point represented by its coordinates if
	 *         contained within this {@link Polygon}, <code>false</code>
	 *         otherwise
	 */
	public boolean contains(double x, double y) {
		return contains(new Point(x, y));
	}

	@Override
	public boolean contains(IGeometry g) {
		if (g instanceof Line) {
			return contains((Line) g);
		} else if (g instanceof Polygon) {
			return contains((Polygon) g);
		} else if (g instanceof Polyline) {
			return contains((Polyline) g);
		} else if (g instanceof Rectangle) {
			return contains((Rectangle) g);
		}
		return ShapeUtils.contains(this, g);
	}

	/**
	 * Checks whether the given {@link Line} is fully contained within this
	 * {@link Polygon}.
	 *
	 * @param line
	 *            The {@link Line} to test for containment
	 * @return <code>true</code> if the given {@link Line} is fully contained,
	 *         <code>false</code> otherwise
	 */
	public boolean contains(Line line) {
		// quick rejection test: if the end points are not contained, the line
		// may not be contained
		if (!contains(line.getP1()) || !contains(line.getP2())) {
			return false;
		}

		Set<Double> intersectionParams = new HashSet<>();

		for (Line seg : getOutlineSegments()) {
			Point poi = seg.getIntersection(line);
			if (poi != null) {
				intersectionParams.add(line.getParameterAt(poi));
			}
		}

		if (intersectionParams.size() <= 1) {
			return true;
		}

		Double[] poiParams = intersectionParams.toArray(new Double[] {});
		Arrays.sort(poiParams, new Comparator<Double>() {
			@Override
			public int compare(Double t, Double u) {
				double d = t - u;
				return d < 0 ? -1 : d > 0 ? 1 : 0;
			}
		});

		// check the points between the intersections for containment
		if (!contains(line.get(poiParams[0] / 2))) {
			return false;
		}
		for (int i = 0; i < poiParams.length - 1; i++) {
			if (!contains(line.get((poiParams[i] + poiParams[i + 1]) / 2))) {
				return false;
			}
		}
		return contains(line.get((poiParams[poiParams.length - 1] + 1) / 2));
	}

	/**
	 * @see IGeometry#contains(Point)
	 */
	@Override
	public boolean contains(Point p) {
		if (points.length == 0) {
			return false;
		} else if (points.length == 1) {
			return points[0].equals(p);
		} else if (points.length == 2) {
			return new Line(points[0], points[1]).contains(p);
		} else {
			// perform a quick rejection test via the bounds
			Rectangle bounds = getBounds();
			if (!bounds.contains(p)) {
				return false;
			}

			/*
			 * choose a point p' outside the polygon:
			 */
			Point pp = new Point(p.x + bounds.getWidth() + 1, p.y);

			/*
			 * construct a line from p to p':
			 */
			Line testLine = new Line(p, pp);

			/*
			 * compute if there is an even or odd number of intersection of the
			 * test line with all sides of the polygon; handle the special case
			 * the point is located on one of the sides
			 */
			boolean odd = false;
			for (int i = 0; i < points.length; i++) {
				Point p1 = points[i];
				Point p2 = points[i + 1 < points.length ? i + 1 : 0];

				// check whether the point is located on the current side
				if (p1.equals(p2)) {
					if (p1.equals(p)) {
						return true;
					}
					continue;
				}

				Line segment = new Line(p1, p2);

				if (segment.contains(p)) {
					return true;
				}

				/*
				 * check if one of the two vertices of the link line is
				 * contained by the test line. the containment test is done to
				 * handle special cases where the intersection has to be counted
				 * appropriately.
				 * 
				 * 1) if the vertex is above (greater y-component) the other
				 * point of the line, it is counted once.
				 * 
				 * 2) if the vertex is below (lower y-component) or on the same
				 * height as the other point of the line, it is omitted.
				 */
				boolean p1contained = testLine.contains(p1);
				boolean p2contained = testLine.contains(p2);

				// TODO: is imprecision needed for this test?
				if (p1contained || p2contained) {
					if (p1contained) {
						if (p1.y > p2.y) {
							odd = !odd;
						}
					}
					if (p2contained) {
						if (p2.y > p1.y) {
							odd = !odd;
						}
					}
					continue;
				}

				/*
				 * check the current link for an intersection with the test
				 * line. if there is an intersection, change state.
				 * 
				 * Special case error prevention: If the point in question (p)
				 * is very near to an edge of and inside the polygon, it can
				 * happen, that the edge.contains(p) is false, but an
				 * intersection can be found, although p is right to the edge.
				 * To prevent a wrong state change, the intersection has to be
				 * right-of p.
				 */
				Point poi = testLine.getIntersection(segment);
				if (poi != null && poi.x >= p.x) {
					odd = !odd;
				}
			}
			return odd;
		}
	}

	/**
	 * Checks whether the given {@link Polygon} is fully contained within this
	 * {@link Polygon}.
	 *
	 * @param p
	 *            The {@link Polygon} to test for containment
	 * @return <code>true</code> if the given {@link Polygon} is fully
	 *         contained, <code>false</code> otherwise.
	 */
	public boolean contains(Polygon p) {
		// all segments of the given polygon have to be contained
		Line[] otherSegments = p.getOutlineSegments();
		for (int i = 0; i < otherSegments.length; i++) {
			if (!contains(otherSegments[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests if the given {@link Polyline} p is contained in this
	 * {@link Polygon}.
	 *
	 * @param p
	 *            The {@link Polyline} to test for containment.
	 * @return true if it is contained, false otherwise
	 */
	public boolean contains(Polyline p) {
		// all segments of the given polygon have to be contained
		Line[] otherSegments = p.getCurves();
		for (int i = 0; i < otherSegments.length; i++) {
			if (!contains(otherSegments[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether the given {@link Rectangle} is fully contained within this
	 * {@link Polygon}.
	 *
	 * @param r
	 *            the {@link Rectangle} to test for containment
	 * @return <code>true</code> if the given {@link Rectangle} is fully
	 *         contained, <code>false</code> otherwise.
	 */
	public boolean contains(Rectangle r) {
		return contains(r.toPolygon());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof Polygon) {
			Polygon p = (Polygon) o;
			return equals(p.getPoints());
		}
		return false;
	}

	/**
	 * Checks whether this {@link Polygon} and the one that is indirectly given
	 * via the given array of points are regarded to be equal. The
	 * {@link Polygon}s will be regarded equal, if they are characterized by the
	 * same segments. As a {@link Polygon} is always regarded to be closed, the
	 * list of points may not have to correspond in each index value, they may
	 * also be shifted by a certain offset. Moreover, the vertices of two
	 * equally {@link Polygon}s may be reverted in order.
	 *
	 * @param points
	 *            an array of {@link Point} characterizing a {@link Polygon} to
	 *            be checked for equality
	 * @return <code>true</code> if the sequence of points that characterize
	 *         this {@link Polygon} and the {@link Polygon} indirectly given via
	 *         the array of points are regarded to form the same segments.
	 */
	public boolean equals(Point[] points) {
		if (points.length != this.points.length) {
			return false;
		}

		// walk through the segments of this polygon and count them
		SegmentList segments = new SegmentList();

		for (Line seg : getOutlineSegments()) {
			SegmentCounter sc = segments.find(seg);
			sc.count++;
		}

		// walk through the segments of the other polygon and decrement their
		// counter
		for (Line seg : new Polygon(points).getOutlineSegments()) {
			SegmentCounter sc = segments.find(seg);
			if (sc.count == 0) {
				// if we add a new one or we delete one too often, these
				// polygons are not equal
				return false;
			}
			sc.count--;
		}

		return true;
	}

	/**
	 * Computes the area of this {@link Polygon}.
	 *
	 * @return the area of this {@link Polygon}
	 */
	public double getArea() {
		return Math.abs(getSignedArea());
	}

	/**
	 * Returns a copy of this {@link Polygon}, which is made up by the same
	 * points.
	 *
	 * @return a new {@link Polygon} with an identical set of points.
	 */
	@Override
	public Polygon getCopy() {
		return new Polygon(getPoints());
	}

	@Override
	public Polyline getOutline() {
		return new Polyline(PointListUtils.toSegmentsArray(points, true));
	}

	/**
	 * Returns a sequence of {@link Line}s, representing the segments that are
	 * obtained by linking each two successive point of this {@link Polygon}
	 * (including the last and the first one).
	 *
	 * @return an array of {@link Line}s, representing the segments that make up
	 *         this {@link Polygon}
	 */
	@Override
	public Line[] getOutlineSegments() {
		return PointListUtils.toSegmentsArray(points, true);
	}

	/**
	 * Computes the signed area of this {@link Polygon}. The sign of the area is
	 * negative for counter clockwise ordered vertices. It is positive for
	 * clockwise ordered vertices.
	 *
	 * @return the signed area of this {@link Polygon}
	 */
	public double getSignedArea() {
		if (points.length < 3) {
			return 0;
		}

		double area = 0;
		for (int i = 0; i < points.length - 1; i++) {
			area += points[i].x * points[i + 1].y
					- points[i].y * points[i + 1].x;
		}

		// closing segment
		area += points[points.length - 1].x * points[0].y
				- points[points.length - 1].y * points[0].x;

		return area * 0.5;
	}

	/**
	 * @see IGeometry#getTransformed(AffineTransform)
	 */
	@Override
	public Polygon getTransformed(AffineTransform t) {
		// shape type should remain polygon (not path)
		return new Polygon(t.getTransformed(points));
	}

	/**
	 * Naive, recursive ear-clipping algorithm to triangulate this simple,
	 * planar {@link Polygon}.
	 *
	 * @return triangulation {@link Polygon}s (triangles)
	 */
	public Polygon[] getTriangulation() {
		assureSimplicity();
		ArrayList<Polygon> ears = new ArrayList<>(points.length - 2);
		triangulate(this, ears);
		return ears.toArray(new Polygon[] {});
	}

	/**
	 * @see IGeometry#toPath()
	 */
	@Override
	public Path toPath() {
		Path path = new Path();
		if (points.length > 0) {
			path.moveTo(points[0].x, points[0].y);
			for (int i = 1; i < points.length; i++) {
				path.lineTo(points[i].x, points[i].y);
			}
			path.close();
		}
		return path;
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer("Polygon: ");
		if (points.length > 0) {
			for (int i = 0; i < points.length; i++) {
				stringBuffer
						.append("(" + points[i].x + ", " + points[i].y + ")");
				stringBuffer.append(" -> ");
			}
			stringBuffer.append("(" + points[0].x + ", " + points[0].y + ")");
		} else {
			stringBuffer.append("<no points>");
		}
		return stringBuffer.toString();
	}

}
