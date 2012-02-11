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
import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.gef4.geometry.Angle;
import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.euclidean.Straight;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.transform.AffineTransform;
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
public class Polygon implements IGeometry {

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

	private final Point[] points;

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
		points = new Point[coordinates.length / 2];
		for (int i = 0; i < coordinates.length / 2; i++) {
			points[i] = new Point(coordinates[i * 2], coordinates[i * 2 + 1]);
		}
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
		this.points = PointListUtils.getCopy(points);
	}

	/**
	 * Overwritten with public visibility as recommended in {@link Cloneable}.
	 */
	@Override
	public Polygon clone() {
		return getCopy();
	}

	/**
	 * Tests if the given {@link CubicCurve} curve is contained in this
	 * {@link Polygon}.
	 * 
	 * @param curve
	 * @return true if it is contained, false otherwise
	 */
	public boolean contains(CubicCurve curve) {
		if (contains(curve.getP1()) && contains(curve.getP2())) {
			for (Line seg : getSegments()) {
				if (curve.intersects(seg)) {
					return false;
				}
			}
			return true;
		}
		return false;
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
	 * Tests if the given {@link Ellipse} e is contained in this {@link Polygon}
	 * .
	 * 
	 * @param e
	 * @return true if it is contained, false otherwise
	 */
	public boolean contains(Ellipse e) {
		for (CubicCurve curve : e.getSegments()) {
			if (!contains(curve)) {
				return false;
			}
		}
		return true;
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

			// choose a point p' outside the polygon not too close to its bounds
			// (have it outside at 1% of its width and height) and construct a
			// straight through p and p'
			Vector u1 = new Vector(p);
			Vector u2 = new Vector(bounds.getTopLeft().x
					- (bounds.getWidth() / 100), bounds.getTopLeft().y
					- (bounds.getHeight() / 100));
			Straight s1 = new Straight(u1, u2.getSubtracted((u1)));

			// compute if there is an even or odd number of intersection of
			// p->p' with all sides of the polygon; handle the special case the
			// point is located on one of the sides
			boolean odd = false;
			for (int i = 0; i < points.length; i++) {
				Vector v1 = new Vector(points[i]);
				Vector v2 = new Vector(
						points[i + 1 < points.length ? i + 1 : 0]);
				Vector direction = v2.getSubtracted(v1);

				if (direction.isNull()) {
					if (v1.equals(u1)) {
						return true;
					}
					continue;
				}

				Straight s2 = new Straight(v1, direction);

				// check whether the point is located on the current side
				if (s2.containsWithinSegment(v1, v2, u1)) {
					return true;
				}

				// check if there is an intersection within the given segment
				if (s2.intersectsWithinSegment(v1, v2, s1)
						&& s1.intersectsWithinSegment(u1, u2, s2)) {
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
		Line[] otherSegments = p.getSegments();
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
	 * @return true if it is contained, false otherwise
	 */
	public boolean contains(Polyline p) {
		// all segments of the given polygon have to be contained
		Line[] otherSegments = p.getSegments();
		for (int i = 0; i < otherSegments.length; i++) {
			if (!contains(otherSegments[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests if the given {@link QuadraticCurve} curve is contained in this
	 * {@link Polygon}.
	 * 
	 * @param curve
	 * @return true if it is contained, false otherwise
	 */
	public boolean contains(QuadraticCurve curve) {
		if (contains(curve.getP1()) && contains(curve.getP2())) {
			for (Line seg : getSegments()) {
				if (curve.intersects(seg)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * @see IGeometry#contains(Rectangle)
	 */
	public boolean contains(Rectangle rect) {
		return contains(rect.toPolygon());
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

		for (Line seg : getSegments()) {
			SegmentCounter sc = segments.find(seg);
			sc.count++;
		}

		// walk through the segments of the other polygon and decrement their
		// counter
		for (Line seg : new Polygon(points).getSegments()) {
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
	 * @see IGeometry#getBounds()
	 */
	public Rectangle getBounds() {
		return PointListUtils.getBounds(points);
	}

	/**
	 * Returns a double array, which represents the sequence of coordinates of
	 * the {@link Point}s that make up this {@link Polygon}.
	 * 
	 * @return an array that alternately contains the x- and y-coordinates of
	 *         this {@link Polygon}'s points.
	 */
	public double[] getCoordinates() {
		return PointListUtils.toCoordinatesArray(points);
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
	 * Returns the points of intersection between this {@link Polygon} and the
	 * given {@link Arc} arc.
	 * 
	 * @param arc
	 *            The {@link Arc} to test for intersections
	 * @return the points of intersection.
	 */
	public Point[] getIntersections(Arc arc) {
		HashSet<Point> intersections = new HashSet<Point>();

		for (CubicCurve seg : arc.getSegments()) {
			intersections.addAll(Arrays.asList(getIntersections(seg)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Returns the points of intersection between this {@link Polygon} and the
	 * given {@link CubicCurve} c.
	 * 
	 * @param c
	 * @return The points of intersection.
	 */
	public Point[] getIntersections(CubicCurve c) {
		HashSet<Point> intersections = new HashSet<Point>();

		for (Line seg : getSegments()) {
			for (Point poi : c.getIntersections(seg)) {
				intersections.add(poi);
			}
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Returns the points of intersection between this {@link Polygon} and the
	 * given {@link Ellipse} e.
	 * 
	 * @param e
	 * @return The points of intersection.
	 */
	public Point[] getIntersections(Ellipse e) {
		return e.getIntersections(this);
	}

	/**
	 * Returns the points of intersection between this {@link Polygon} and the
	 * given {@link Line} l.
	 * 
	 * @param l
	 * @return The points of intersection.
	 */
	public Point[] getIntersections(Line l) {
		HashSet<Point> intersections = new HashSet<Point>();

		for (Line segment : getSegments()) {
			Point poi = segment.getIntersection(l);
			if (poi != null) {
				intersections.add(poi);
			}
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Returns the points of intersection between this {@link Polygon} and the
	 * given other {@link Polygon} polygon.
	 * 
	 * @param polygon
	 * @return The points of intersection.
	 */
	public Point[] getIntersections(Polygon polygon) {
		HashSet<Point> intersections = new HashSet<Point>();

		for (Line segment : polygon.getSegments()) {
			for (Point poi : getIntersections(segment)) {
				intersections.add(poi);
			}
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Returns the points of intersection between this {@link Polygon} and the
	 * given {@link Polyline} polyline.
	 * 
	 * @param polyline
	 * @return The points of intersection.
	 */
	public Point[] getIntersections(Polyline polyline) {
		HashSet<Point> intersections = new HashSet<Point>();

		for (Line segment : polyline.getSegments()) {
			for (Point poi : getIntersections(segment)) {
				intersections.add(poi);
			}
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Returns the points of intersection between this {@link Polygon} and the
	 * given {@link QuadraticCurve} c.
	 * 
	 * @param c
	 * @return The points of intersection.
	 */
	public Point[] getIntersections(QuadraticCurve c) {
		HashSet<Point> intersections = new HashSet<Point>();

		for (Line seg : getSegments()) {
			for (Point poi : c.getIntersections(seg)) {
				intersections.add(poi);
			}
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Returns the points of intersection between this {@link Polygon} and the
	 * given {@link Rectangle} rect.
	 * 
	 * @param rect
	 * @return The points of intersection.
	 */
	public Point[] getIntersections(Rectangle rect) {
		HashSet<Point> intersections = new HashSet<Point>();

		for (Line segment : rect.getSegments()) {
			for (Point poi : getIntersections(segment)) {
				intersections.add(poi);
			}
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Calculates the points of intersection of this {@link Polygon} and the
	 * given {@link RoundedRectangle}.
	 * 
	 * @param r
	 *            The {@link RoundedRectangle} to compute intersection points
	 *            with
	 * @return an array containing the points of intersection of this
	 *         {@link Polygon} and the given {@link RoundedRectangle}. An empty
	 *         array if there are no points of intersection or indefinitely many
	 *         ones.
	 */
	public Point[] getIntersections(RoundedRectangle r) {
		HashSet<Point> intersections = new HashSet<Point>();

		// line segments
		intersections.addAll(Arrays.asList(getIntersections(r.getTop())));
		intersections.addAll(Arrays.asList(getIntersections(r.getLeft())));
		intersections.addAll(Arrays.asList(getIntersections(r.getBottom())));
		intersections.addAll(Arrays.asList(getIntersections(r.getRight())));

		// arc segments
		intersections.addAll(Arrays.asList(getIntersections(r.getTopRight())));
		intersections.addAll(Arrays.asList(getIntersections(r.getTopLeft())));
		intersections
				.addAll(Arrays.asList(getIntersections(r.getBottomLeft())));
		intersections
				.addAll(Arrays.asList(getIntersections(r.getBottomRight())));

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Returns a copy of the points that make up this {@link Polygon}, where a
	 * segment of the {@link Polygon} is represented between each two succeeding
	 * {@link Point}s in the sequence, and from the last back to the first.
	 * 
	 * @return an array of {@link Point}s representing the points that make up
	 *         this {@link Polygon}
	 */
	public Point[] getPoints() {
		return PointListUtils.getCopy(points);
	}

	/**
	 * Returns a sequence of {@link Line}s, representing the segments that are
	 * obtained by linking each two successive point of this {@link Polygon}
	 * (including the last and the first one).
	 * 
	 * @return an array of {@link Line}s, representing the segments that make up
	 *         this {@link Polygon}
	 */
	public Line[] getSegments() {
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
	 * Returns a new Polygon which is shifted along each axis by the passed
	 * values.
	 * 
	 * @param dx
	 *            Displacement along X axis
	 * @param dy
	 *            Displacement along Y axis
	 * @return The new translated rectangle
	 * @since 2.0
	 */
	public Polygon getTranslated(double dx, double dy) {
		return getCopy().translate(dx, dy);
	}

	/**
	 * Returns a new Polygon which is shifted by the position of the given
	 * Point.
	 * 
	 * @param pt
	 *            Point providing the amount of shift along each axis
	 * @return The new translated Polygon
	 */
	public Polygon getTranslated(Point pt) {
		return getCopy().translate(pt);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// calculating a better hashCode is not possible, because due to the
		// imprecision, equals() is no longer transitive
		return 0;
	}

	/**
	 * Tests if this {@link Polygon} intersects the given {@link Ellipse} e.
	 * 
	 * @param e
	 * @return true if they intersect, false otherwise
	 */
	public boolean intersects(Ellipse e) {
		return e.intersects(this);
	}

	/**
	 * Checks if there is at least one common point between this {@link Polygon}
	 * and the given {@link Line}, which includes the case that the given
	 * {@link Line} is fully contained.
	 * 
	 * @param line
	 *            The {@link Line} to test for intersection
	 * @return <code>true</code> if this {@link Polygon} and the given
	 *         {@link Line} share at least one common point, <code>false</code>
	 *         otherwise.
	 */
	public boolean intersects(Line line) {
		// quick acceptance test: if the end points of the line are contained,
		// we already have a common point and may return
		if (contains(line.getP1()) || contains(line.getP2())) {
			return true;
		}

		// check for intersection with the segments
		Line[] segments = getSegments();
		for (int i = 0; i < segments.length; i++) {
			if (segments[i].intersects(line)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if there is at least a common point between this {@link Polygon}
	 * and the given {@link Polygon}, which includes the case that the given
	 * {@link Polygon} is fully contained within this {@link Polygon} or vice
	 * versa.
	 * 
	 * @param p
	 *            the {@link Polygon} to test for intersection
	 * @return <code>true</code> in case this {@link Polygon} and the given
	 *         {@link Polygon} share at least one common point.
	 */
	public boolean intersects(Polygon p) {
		// reduce to segment intersection test
		Line[] otherSegments = p.getSegments();
		for (int i = 0; i < otherSegments.length; i++) {
			if (intersects(otherSegments[i])) {
				return true;
			}
		}
		// no intersection, so we still need to check for containment
		return p.contains(this);
	}

	/**
	 * Tests if this {@link Polygon} intersects with the given {@link Polyline}
	 * p.
	 * 
	 * @param p
	 * @return true if they intersect, false otherwise
	 */
	public boolean intersects(Polyline p) {
		// reduce to segment intersection test
		Line[] otherSegments = p.getSegments();
		for (int i = 0; i < otherSegments.length; i++) {
			if (intersects(otherSegments[i])) {
				return true;
			}
		}
		// no intersection, so we still need to check for containment
		return contains(p);
	}

	/**
	 * Checks if there is at least a common point between this {@link Polygon}
	 * and the given {@link Rectangle}, which includes the case that the given
	 * {@link Rectangle} is fully contained within this {@link Polygon} or vice
	 * versa.
	 * 
	 * @see IGeometry#intersects(Rectangle)
	 */
	public boolean intersects(Rectangle rect) {
		return intersects(rect.toPolygon());
	}

	/**
	 * Rotates this {@link Polygon} counter-clock-wise by the given
	 * {@link Angle} alpha around the given {@link Point} center.
	 * 
	 * The rotation is done by
	 * <ol>
	 * <li>translating this {@link Polygon} by the negated {@link Point} center</li>
	 * <li>rotating each {@link Point} of this {@link Polygon}
	 * counter-clock-wise by the given {@link Angle} angle</li>
	 * <li>translating this {@link Polygon} back by the {@link Point} center</li>
	 * </ol>
	 * 
	 * @param alpha
	 *            The rotation {@link Angle}.
	 * @param center
	 *            The {@link Point} to rotate around.
	 * @return This (counter-clock-wise-rotated) {@link Polygon} object.
	 */
	public Polygon rotateCCW(Angle alpha, Point center) {
		translate(center.getNegated());
		for (Point p : points) {
			Point np = new Vector(p).rotateCCW(alpha).toPoint();
			p.x = np.x;
			p.y = np.y;
		}
		translate(center);
		return this;
	}

	/**
	 * Rotates this {@link Polygon} clock-wise by the given {@link Angle} alpha
	 * around the given {@link Point} center.
	 * 
	 * The rotation is done by
	 * <ol>
	 * <li>translating this {@link Polygon} by the negated {@link Point} center</li>
	 * <li>rotating each {@link Point} of this {@link Polygon} clock-wise by the
	 * given {@link Angle} angle</li>
	 * <li>translating this {@link Polygon} back by the {@link Point} center</li>
	 * </ol>
	 * 
	 * @param alpha
	 *            The rotation {@link Angle}.
	 * @param center
	 *            The {@link Point} to rotate around.
	 * @return This (clock-wise-rotated) {@link Polygon} object.
	 */
	public Polygon rotateCW(Angle alpha, Point center) {
		translate(center.getNegated());
		for (Point p : points) {
			Point np = new Vector(p).rotateCW(alpha).toPoint();
			p.x = np.x;
			p.y = np.y;
		}
		translate(center);
		return this;
	}

	/**
	 * Scales this {@link Polygon} object by the given factor from the given
	 * center {@link Point}.
	 * 
	 * The scaling is done by
	 * <ol>
	 * <li>translating this {@link Polygon} by the negated center {@link Point}</li>
	 * <li>scaling the individual {@link Polygon} {@link Point}s</li>
	 * <li>translating this {@link Polygon} back</li>
	 * </ol>
	 * 
	 * @param factor
	 *            The scale-factor.
	 * @param center
	 *            The rotation {@link Point}.
	 * @return This (scaled) {@link Polygon} object.
	 */
	public Polygon scale(double factor, Point center) {
		translate(center.getNegated());
		for (Point p : points) {
			Point np = p.getScaled(factor);
			p.x = np.x;
			p.y = np.y;
		}
		translate(center);
		return this;
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

	/**
	 * Returns an integer array, which represents the sequence of coordinates of
	 * the {@link Point}s that make up this {@link Polygon}.
	 * 
	 * @return an array containing integer values, which are obtained by casting
	 *         the x- and y-coordinates of this {@link Polygon}.
	 */
	public int[] toSWTPointArray() {
		return PointListUtils.toIntegerArray(PointListUtils
				.toCoordinatesArray(points));
	}

	/**
	 * Moves this Polygon horizontally by dx and vertically by dy, then returns
	 * this Rectangle for convenience.
	 * 
	 * @param dx
	 *            Shift along X axis
	 * @param dy
	 *            Shift along Y axis
	 * @return <code>this</code> for convenience
	 */
	public Polygon translate(double dx, double dy) {
		PointListUtils.translate(points, dx, dy);
		return this;
	}

	/**
	 * Moves this Polygon horizontally by the x value of the given Point and
	 * vertically by the y value of the given Point, then returns this Rectangle
	 * for convenience.
	 * 
	 * @param p
	 *            Point which provides translation information
	 * @return <code>this</code> for convenience
	 */
	public Polygon translate(Point p) {
		return translate(p.x, p.y);
	}

	// TODO: union point, rectangle, polygon, etc.
}
