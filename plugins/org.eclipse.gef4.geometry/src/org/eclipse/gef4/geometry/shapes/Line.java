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
package org.eclipse.gef4.geometry.shapes;

import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.euclidean.Straight;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.transform.AffineTransform;
import org.eclipse.gef4.geometry.utils.PointListUtils;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * Represents the geometric shape of a line (or linear curve).
 * 
 * Note that while all manipulations (e.g. within shrink, expand) within this
 * class are based on double precision, all comparisons (e.g. within contains,
 * intersects, equals, etc.) are based on a limited precision (with an accuracy
 * defined within {@link PrecisionUtils}) to compensate for rounding effects.
 * 
 * @author anyssen
 */
public class Line implements Geometry {

	private static final long serialVersionUID = 1L;

	private double x1, y1, x2, y2;

	/**
	 * Constructs a new {@link Line}, which connects the two {@link Point}s
	 * given indirectly by their coordinates
	 * 
	 * @param x1
	 *            the x-coordinate of the start point
	 * @param y1
	 *            the y-coordinate of the start point
	 * @param x2
	 *            the x-coordinate of the end point
	 * @param y2
	 *            the y-coordinate of the end point
	 */
	public Line(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	/**
	 * Constructs a new {@link Line} which connects the two given {@link Point}s
	 * 
	 * @param p1
	 *            the start point
	 * @param p2
	 *            the end point
	 */
	public Line(Point p1, Point p2) {
		this(p1.x, p1.y, p2.x, p2.y);
	}

	@Override
	public Object clone() {
		return getCopy();
	}

	public boolean contains(Point p) {
		// TODO: optimize w.r.t object creation
		Point p1 = getP1();
		Point p2 = getP2();

		if (p1.equals(p2)) {
			return p.equals(p1);
		}

		return new Straight(p1, p2).containsWithinSegment(new Vector(p1),
				new Vector(p2), new Vector(p));
	}

	public boolean contains(Rectangle r) {
		// TODO: may contain the rectangle only in case the rectangle is
		// degenerated...
		return false;
	}

	/**
	 * Tests whether this {@link Line} is equal to the line given implicitly by
	 * the given point coordinates.
	 * 
	 * @param x1
	 *            the x-coordinate of the start point of the line to test
	 * @param y1
	 *            the y-coordinate of the start point of the line to test
	 * @param x2
	 *            the x-coordinate of the end point of the line to test
	 * @param y2
	 *            the y-coordinate of the end point of the line to test
	 * @return <code>true</code> if the given start and end point coordinates
	 *         are (imprecisely) equal to this {@link Line} 's start and end
	 *         point coordinates
	 */
	public boolean equals(double x1, double y1, double x2, double y2) {
		return PrecisionUtils.equal(x1, this.x1)
				&& PrecisionUtils.equal(y1, this.y1)
				&& PrecisionUtils.equal(x2, this.x2)
				&& PrecisionUtils.equal(y2, this.y2)
				|| PrecisionUtils.equal(x2, this.x1)
				&& PrecisionUtils.equal(y2, this.y1)
				&& PrecisionUtils.equal(x1, this.x2)
				&& PrecisionUtils.equal(y1, this.y2);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof Line) {
			Line l = (Line) o;
			return equals(l.getX1(), l.getY1(), l.getX2(), l.getY2());
		}
		return false;
	}

	/**
	 * Returns the smallest {@link Rectangle} containing this {@link Line}'s
	 * start and end point
	 * 
	 * @see Geometry#getBounds()
	 */
	public Rectangle getBounds() {
		return new Rectangle(getP1(), getP2());
	}

	/**
	 * Returns a new {@link Line}, which has the same start and end point
	 * coordinates as this one.
	 * 
	 * @return a new {@link Line} with the same start and end point coordinates
	 */
	public Line getCopy() {
		return new Line(x1, y1, x2, y2);
	}

	public Point[] getIntersections(Arc arc) {
		return arc.getIntersections(this);
	}

	public Point[] getIntersections(CubicCurve c) {
		return c.getIntersections(this);
	}

	public Point[] getIntersections(Ellipse e) {
		return e.getIntersections(this);
	}

	public Point[] getIntersections(Polygon p) {
		return p.getIntersections(this);
	}

	public Point[] getIntersections(Polyline p) {
		HashSet<Point> intersections = new HashSet<Point>();

		for (Line seg : p.getSegments()) {
			intersections.add(getIntersection(seg));
		}

		return intersections.toArray(new Point[] {});
	}

	public Point[] getIntersections(QuadraticCurve c) {
		return c.getIntersections(this);
	}

	public Point[] getIntersections(Rectangle r) {
		HashSet<Point> intersections = new HashSet<Point>();

		for (Line seg : r.getSegments()) {
			intersections.addAll(Arrays.asList(getIntersection(seg)));
		}

		return intersections.toArray(new Point[] {});
	}

	public Point[] getIntersections(RoundedRectangle rr) {
		HashSet<Point> intersections = new HashSet<Point>();

		// line segments
		intersections.add(getIntersection(rr.getTop()));
		intersections.add(getIntersection(rr.getLeft()));
		intersections.add(getIntersection(rr.getBottom()));
		intersections.add(getIntersection(rr.getRight()));

		// arc segments
		intersections.addAll(Arrays.asList(getIntersections(rr.getTopRight())));
		intersections.addAll(Arrays.asList(getIntersections(rr.getTopLeft())));
		intersections
				.addAll(Arrays.asList(getIntersections(rr.getBottomLeft())));
		intersections
				.addAll(Arrays.asList(getIntersections(rr.getBottomRight())));

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Returns the single intersection point between this {@link Line} and the
	 * given one, in case it exists. Note that even in case
	 * {@link Line#intersects} returns true, there may not be a single
	 * intersection point in case both lines overlap in more than one point.
	 * 
	 * @param l
	 *            the Line, for which to compute the intersection point
	 * @return the single intersection point between this {@link Line} and the
	 *         given one, in case it intersects, <code>null</code> instead
	 */
	public Point getIntersection(Line l) {
		// if there is no intersection we may not return an intersection point
		if (!intersects(l)) {
			return null;
		}

		Point p1 = getP1();
		Point p2 = getP2();
		Straight s1 = new Straight(p1, p2);
		Point lp1 = l.getP1();
		Point lp2 = l.getP2();
		Straight s2 = new Straight(lp1, lp2);
		// handle overlap in exactly one point
		if (s1.equals(s2)) {
			// lines may overlap in exactly one end point
			if ((p1.equals(lp1) || p1.equals(lp2))
					&& !(p2.equals(lp1) || p2.equals(lp2))) {
				return p1;
			} else if (p2.equals(lp1) || p2.equals(lp2)
					&& !(p1.equals(lp1) || p1.equals(lp2))) {
				return p2;
			} else {
				return null;
			}
		}
		// return the single intersection point
		return s1.getIntersection(s2).toPoint();
	}

	/**
	 * Returns a {@link Point} representing the start point of this {@link Line}
	 * 
	 * @return a new {@link Point} with the x and y coordinates of this
	 *         {@link Line}'s start point
	 */
	public Point getP1() {
		return new Point(x1, y1);
	}

	/**
	 * Returns a {@link Point} representing the end point of this {@link Line}
	 * 
	 * @return a {@link Point} with the x and y coordinates of this {@link Line}
	 *         's end point
	 */
	public Point getP2() {
		return new Point(x2, y2);
	}

	/**
	 * Returns an array, which contains two {@link Point}s representing the
	 * start and end points of this {@link Line}
	 * 
	 * @return an array with two {@link Point}s, whose x and y coordinates match
	 *         those of this {@link Line}'s start and end point
	 */
	public Point[] getPoints() {
		return new Point[] { getP1(), getP2() };
	}

	/**
	 * @see Geometry#getTransformed(AffineTransform)
	 */
	public Geometry getTransformed(AffineTransform localTransform) {
		Point[] transformed = localTransform.getTransformed(getPoints());
		return new Line(transformed[0], transformed[1]);
	}

	/**
	 * Returns the x-coordinate of this {@link Line}'s start point
	 * 
	 * @return the x-coordinate of the start point
	 */
	public double getX1() {
		return x1;
	}

	/**
	 * Returns the x-coordinate of this {@link Line}'s end point
	 * 
	 * @return the x-coordinate of the end point
	 */
	public double getX2() {
		return x2;
	}

	/**
	 * Returns the y-coordinate of this {@link Line}'s start point
	 * 
	 * @return the y-coordinate of the start point
	 */
	public double getY1() {
		return y1;
	}

	/**
	 * Returns the y-coordinate of this {@link Line}'s end point
	 * 
	 * @return the y-coordinate of the end point
	 */
	public double getY2() {
		return y2;
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
	 * Tests whether this {@link Line} and the given one intersect, i.e. if they
	 * share at least one common point.
	 * 
	 * @param l
	 *            The {@link Line} to test.
	 * @return <code>true</code> if this {@link Line} and the given one share at
	 *         least one common point, <code>false</code> otherwise.
	 */
	public boolean intersects(Line l) {
		// TODO: optimize w.r.t. object creation
		Point p1 = getP1();
		Point p2 = getP2();

		if (p1.equals(p2)) {
			return l.contains(p1);
		}

		Point lp1 = l.getP1();
		Point lp2 = l.getP2();

		if (lp1.equals(lp2)) {
			return contains(lp1);
		}

		Straight s1 = new Straight(p1, p2);
		Straight s2 = new Straight(lp1, lp2);
		Vector v1 = new Vector(p1);
		Vector v2 = new Vector(p2);
		Vector lv1 = new Vector(lp1);
		Vector lv2 = new Vector(lp2);
		// intersection within Straight does not cover overlap, therefore we
		// have to check this separately here (via equality and containment of
		// the end points)
		return s1.equals(s2)
				&& (s1.containsWithinSegment(v1, v2, lv1) || s1
						.containsWithinSegment(v1, v2, lv2))
				|| s1.intersectsWithinSegment(v1, v2, s2)
				&& s2.intersectsWithinSegment(lv1, lv2, s1);
	}

	/**
	 * @see Geometry#intersects(Rectangle)
	 */
	public boolean intersects(Rectangle r) {
		return r.intersects(this);
	}

	/**
	 * Initializes this {@link Line} with the given start and end point
	 * coordinates
	 * 
	 * @param x1
	 *            the x-coordinate of the start point
	 * @param y1
	 *            the y-coordinate of the start point
	 * @param x2
	 *            the x-coordinate of the end point
	 * @param y2
	 *            the y-coordinate of the end point
	 */
	public void setLine(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	/**
	 * Initializes this {@link Line} with the start and end point coordinates of
	 * the given one.
	 * 
	 * @param l
	 *            the {@link Line} whose start and end point coordinates should
	 *            be used for initialization
	 */
	public void setLine(Line l) {
		setLine(l.getX1(), l.getY1(), l.getX2(), l.getY2());
	}

	/**
	 * Initializes this {@link Line} with the start and end point coordinates
	 * provided by the given points
	 * 
	 * @param p1
	 *            the Point whose coordinates should be used as the start point
	 *            coordinates of this {@link Line}
	 * @param p2
	 *            the Point whose coordinates should be used as the end point
	 *            coordinates of this {@link Line}
	 */
	public void setLine(Point p1, Point p2) {
		setLine(p1.x, p1.y, p2.x, p2.y);
	}

	/**
	 * Initializes the start point coordinates of this line with the values of
	 * the given {@link Point}
	 * 
	 * @param p1
	 *            the Point whose x and y coordinates should be used to
	 *            initialize this {@link Line}'s start point coordinates
	 */
	public void setP1(Point p1) {
		this.x1 = p1.x;
		this.y1 = p1.y;
	}

	/**
	 * Initializes the end point coordinates of this line with the values of the
	 * given {@link Point}
	 * 
	 * @param p2
	 *            the Point whose x and y coordinates should be used to
	 *            initialize this {@link Line}'s end point coordinates
	 */
	public void setP2(Point p2) {
		this.x2 = p2.x;
		this.y2 = p2.y;
	}

	/**
	 * Initializes the start point x-coordinate of this line with the given
	 * value.
	 * 
	 * @param x1
	 *            the value to use as the new x-coordinate of this {@link Line}s
	 *            start point
	 */
	public void setX1(double x1) {
		this.x1 = x1;
	}

	/**
	 * Initializes the end point x-coordinate of this line with the given value.
	 * 
	 * @param x2
	 *            the value to use as the new x-coordinate of this {@link Line}s
	 *            end point
	 */
	public void setX2(double x2) {
		this.x2 = x2;
	}

	/**
	 * Initializes the start point y-coordinate of this line with the given
	 * value.
	 * 
	 * @param y1
	 *            the value to use as the new y-coordinate of this {@link Line}s
	 *            start point
	 */
	public void setY1(double y1) {
		this.y1 = y1;
	}

	/**
	 * Initializes the end point y-coordinate of this line with the given value.
	 * 
	 * @param y2
	 *            the value to use as the new y-coordinate of this {@link Line}s
	 *            end point
	 */
	public void setY2(double y2) {
		this.y2 = y2;
	}

	/**
	 * @see Geometry#toPath()
	 */
	public Path toPath() {
		Path path = new Path();
		path.moveTo(x1, y1);
		path.lineTo(x2, y2);
		return path;
	}

	@Override
	public String toString() {
		return "Line: (" + x1 + ", " + y1 + ") -> (" + x2 + ", " + y2 + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}

	/**
	 * Returns an integer array of dimension 4, whose values represent the
	 * integer-based coordinates of this {@link Line}'s start and end point.
	 * 
	 * @return an array containing integer values, which are obtained by casting
	 *         x1, y1, x2, y2
	 */
	public int[] toSWTPointArray() {
		return PointListUtils.toIntegerArray(PointListUtils
				.toCoordinatesArray(getPoints()));
	}

}
