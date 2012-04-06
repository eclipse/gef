/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

import java.util.ArrayList;

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.transform.AffineTransform;
import org.eclipse.gef4.geometry.utils.CurveUtils;
import org.eclipse.gef4.geometry.utils.PointListUtils;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * Represents the geometric shape of a convex polygon.
 * 
 * Note that while all manipulations (e.g. within shrink, expand) within this
 * class are based on double precision, all comparisons (e.g. within contains,
 * intersects, equals, etc.) are based on a limited precision (with an accuracy
 * defined within {@link PrecisionUtils}) to compensate for rounding effects.
 * 
 * @author anyssen
 * 
 */
public class Polygon extends AbstractPointListBasedGeometry<Polygon> implements
		IShape {

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
			segmentCounterList = new ArrayList<SegmentCounter>();
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

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@link Polygon} from a even-numbered sequence of
	 * coordinates. Similar to {@link Polygon#Polygon(Point...)}, only that
	 * coordinates of points rather than {@link Point}s are provided.
	 * 
	 * @param coordinates
	 *            an alternating, even-numbered sequence of x- and
	 *            y-coordinates, representing the points from which the
	 *            {@link Polygon} is to be created .
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

	/**
	 * @see IGeometry#contains(Point)
	 */
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

				Line link = new Line(p1, p2);

				if (link.contains(p)) {
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
				 */
				Point poi = testLine.getIntersection(link);
				if (poi != null) {
					odd = !odd;
				}
			}
			return odd;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
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
		if (points.length < 3) {
			return 0;
		}

		double area = 0;
		for (int i = 0; i < points.length - 1; i++) {
			area += points[i].x * points[i + 1].y - points[i].y
					* points[i + 1].x;
		}

		// closing segment
		area += points[points.length - 2].x * points[points.length - 1].y
				- points[points.length - 2].y * points[points.length - 1].x;

		return Math.abs(area) * 0.5;
	}

	/**
	 * Returns a copy of this {@link Polygon}, which is made up by the same
	 * points.
	 * 
	 * @return a new {@link Polygon} with an identical set of points.
	 */
	public Polygon getCopy() {
		return new Polygon(getPoints());
	}

	/**
	 * Returns a sequence of {@link Line}s, representing the segments that are
	 * obtained by linking each two successive point of this {@link Polygon}
	 * (including the last and the first one).
	 * 
	 * @return an array of {@link Line}s, representing the segments that make up
	 *         this {@link Polygon}
	 */
	public Line[] getOutlineSegments() {
		return PointListUtils.toSegmentsArray(points, true);
	}

	/**
	 * @see IGeometry#getTransformed(AffineTransform)
	 */
	public IGeometry getTransformed(AffineTransform t) {
		// shape type should remain polygon (not path)
		return new Polygon(t.getTransformed(points));
	}

	/**
	 * @see IGeometry#toPath()
	 */
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
				stringBuffer.append("(" + points[i].x + ", " + points[i].y
						+ ")");
				stringBuffer.append(" -> ");
			}
			stringBuffer.append("(" + points[0].x + ", " + points[0].y + ")");
		} else {
			stringBuffer.append("<no points>");
		}
		return stringBuffer.toString();
	}

	public Polyline getOutline() {
		return new Polyline(PointListUtils.toSegmentsArray(points, true));
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

		// check for intersections with the segments of this polygon
		for (int i = 0; i < points.length; i++) {
			Point p1 = points[i];
			Point p2 = i + 1 < points.length ? points[i + 1] : points[0];
			Line segment = new Line(p1, p2);
			if (line.intersects(segment)) {
				Point intersection = line.getIntersection(segment);
				if (intersection != null && !line.getP1().equals(intersection)
						&& !line.getP2().equals(intersection)
						&& !segment.getP1().equals(intersection)
						&& !segment.getP2().equals(intersection)) {
					// if we have a single intersection point and this does not
					// match one of the end points of the line, the line is not
					// contained
					return false;
				}
			}
		}
		return true;
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

	/**
	 * Tests if the given {@link Polyline} p is contained in this
	 * {@link Polygon}.
	 * 
	 * @param p
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
		return CurveUtils.contains(this, g);
	}

	// TODO: union point, rectangle, polygon, etc.
}
