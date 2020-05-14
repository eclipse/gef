/*******************************************************************************
 * Copyright (c) 2011, 2017 itemis AG and others.
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
 *     Colin Sharples - contribution for Bugzilla #460754
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.planar;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.euclidean.Straight;
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.internal.utils.PointListUtils;
import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;
import org.eclipse.gef.geometry.projective.Vector3D;

/**
 * Represents the geometric shape of a line (or linear curve).
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
public class Line extends BezierCurve {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@link Line} from the given coordinate values.
	 *
	 * @param coordinates
	 *            A varargs of 4 doubles, providing the x and y coordinates of
	 *            the start point, followed by those of the end point
	 * @see BezierCurve#BezierCurve(double[])
	 */
	public Line(double... coordinates) {
		super(coordinates);
		if (coordinates.length != 4) {
			throw new IllegalArgumentException(
					"A Line may only be defined by 4 coordinates (2 points), while "
							+ coordinates.length + " were passed in.");
		}
	}

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
		super(x1, y1, x2, y2);
	}

	/**
	 * Constructs a new {@link Line}, which connects the two {@link Point}s
	 * given.
	 *
	 * @param points
	 *            A varargs of two points serving as the start and end point of
	 *            this line
	 */
	public Line(Point... points) {
		super(points);
		if (points.length != 2) {
			throw new IllegalArgumentException(
					"A Line may only be defined by two points, while "
							+ points.length + " were passed in.");
		}
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
	public boolean contains(Point p) {
		if (p == null) {
			return false;
		}
		if (getP1().equals(p) || getP2().equals(p)) {
			return true;
		}

		double distance = Math.abs(new Straight(getP1(), getP2())
				.getSignedDistanceCCW(new Vector(p)));
		return PrecisionUtils.equal(distance, 0) && getBounds().contains(p);
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
		return PrecisionUtils.equal(x1, getX1())
				&& PrecisionUtils.equal(y1, getY1())
				&& PrecisionUtils.equal(x2, getX2())
				&& PrecisionUtils.equal(y2, getY2())
				|| PrecisionUtils.equal(x2, getX1())
						&& PrecisionUtils.equal(y2, getY1())
						&& PrecisionUtils.equal(x1, getX2())
						&& PrecisionUtils.equal(y1, getY2());
	}

	@Override
	public Point get(double t) {
		// XXX: Overwritten to improve performance for the Line case.
		if (t < 0 || t > 1) {
			throw new IllegalArgumentException("t out of range: " + t);
		}
		return new Point(((1 - t) * getX1() + t * getX2()),
				((1 - t) * getY1() + t * getY2()));
	}

	/**
	 * Returns the smallest {@link Rectangle} containing this {@link Line}'s
	 * start and end point
	 *
	 * @see IGeometry#getBounds()
	 */
	@Override
	public Rectangle getBounds() {
		return new Rectangle(getP1(), getP2());
	}

	/**
	 * Returns a new {@link Line}, which has the same start and end point
	 * coordinates as this one.
	 *
	 * @return a new {@link Line} with the same start and end point coordinates
	 */
	@Override
	public Line getCopy() {
		return new Line(getP1(), getP2());
	}

	/**
	 * Returns the counter-clockwise angle between the x axis and this
	 * {@link Line}.
	 *
	 * @return Returns the counter-clockwise angle between the x axis and this
	 *         {@link Line}.
	 */
	public Angle getDirectionCCW() {
		Point start = getP1();
		Point end = getP2();
		return Angle.fromRad(Math.atan2(end.y - start.y, end.x - start.x));
	}

	/**
	 * Returns the clockwise angle between the x axis and this {@link Line}.
	 *
	 * @return Returns the clockwise angle between the x axis and this
	 *         {@link Line}.
	 */
	public Angle getDirectionCW() {
		return getDirectionCCW().getOppositeFull();
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
		Point p1 = getP1();
		Point p2 = getP2();

		// degenerated case
		if (p1.equals(p2)) {
			if (l.contains(p1)) {
				return p1;
			} else if (l.contains(p2)) {
				return p2;
			}
			return null;
		}

		Point lp1 = l.getP1();
		Point lp2 = l.getP2();

		// degenerated case
		if (lp1.equals(lp2)) {
			if (contains(lp1)) {
				return lp1;
			} else if (contains(lp2)) {
				return lp2;
			}
			return null;
		}

		Straight s1 = new Straight(p1, p2);
		Straight s2 = new Straight(lp1, lp2);

		if (s1.isParallelTo(s2)) {
			Vector vlp1 = new Vector(lp1);
			Vector vlp2 = new Vector(lp2);
			if (s1.contains(vlp1) && s1.contains(vlp2)) {
				// end-point-intersection? (no overlap)
				double u1 = s1.getParameterAt(vlp1);
				double u2 = s1.getParameterAt(vlp2);

				if (PrecisionUtils.equal(u1, 0) && u2 < u1
						|| PrecisionUtils.equal(u1, 1) && u2 > u1) {
					return lp1;
				} else if (PrecisionUtils.equal(u2, 0) && u1 < u2
						|| PrecisionUtils.equal(u2, 1) && u1 > u2) {
					return lp2;
				}
			}

			return null;
		}

		Point intersection = s1.getIntersection(s2).toPoint();
		return contains(intersection) && l.contains(intersection) ? intersection
				: null;
	}

	@Override
	Set<IntervalPair> getIntersectionIntervalPairs(BezierCurve other,
			Set<Point> intersections) {
		if (other instanceof Line) {
			return getIntersectionIntervalPairs((Line) other, intersections);
		}
		return super.getIntersectionIntervalPairs(other, intersections);
	}

	/**
	 * Provides an optimized version of the
	 * {@link BezierCurve#getIntersectionIntervalPairs(BezierCurve, Set)}
	 * method.
	 *
	 * @param other
	 *            The {@link Line} which is searched for points of intersections
	 *            with this {@link BezierCurve}.
	 * @param intersections
	 *            The {@link Set} where intersections are inserted.
	 * @return see
	 *         {@link BezierCurve#getIntersectionIntervalPairs(BezierCurve, Set)}
	 */
	Set<IntervalPair> getIntersectionIntervalPairs(Line other,
			Set<Point> intersections) {
		HashSet<IntervalPair> intervalPairs = new HashSet<>();
		Straight s1 = new Straight(this);
		Straight s2 = new Straight(other);
		Vector vi = s1.getIntersection(s2);
		if (vi != null) {
			Point poi = vi.toPoint();
			if (contains(poi) && other.contains(poi)) {
				double param1 = s1.getParameterAt(vi);
				double param2 = s2.getParameterAt(vi);
				param1 = param1 < 0 ? 0 : param1 > 1 ? 1 : param1;
				param2 = param2 < 0 ? 0 : param2 > 1 ? 1 : param2;
				intervalPairs.add(
						new IntervalPair(this, new Interval(param1, param1),
								other, new Interval(param2, param2)));
			}
		}
		return intervalPairs;
	}

	@Override
	public Point[] getIntersections(BezierCurve curve) {
		if (curve instanceof Line) {
			Point poi = getIntersection((Line) curve);
			if (poi != null) {
				return new Point[] { poi };
			}
			return new Point[] {};
		}
		return super.getIntersections(curve);
	}

	// TODO: add specialized getOverlap()

	/**
	 * Calculates the distance between the {@link Point start} and the
	 * {@link Point end point} of this {@link Line}.
	 *
	 * @see Point#getDistance(Point)
	 * @return The distance between start and end points.
	 */
	public double getLength() {
		return getP1().getDistance(getP2());
	}

	/**
	 * Returns an array, which contains two {@link Point}s representing the
	 * start and end points of this {@link Line}
	 *
	 * @return an array with two {@link Point}s, whose x and y coordinates match
	 *         those of this {@link Line}'s start and end point
	 */
	@Override
	public Point[] getPoints() {
		return new Point[] { getP1(), getP2() };
	}

	@Override
	public Point getProjection(Point p) {
		// XXX: If this line is degenerated to a point (i.e. start equals end
		// point) then we can return the start or end point as the projection.
		// The default computation (see below) cannot handle this case, as a
		// straight needs to have a direction (which cannot be determined for a
		// degenerated line).
		if (getP1().equals(getP2())) {
			return getP1();
		}

		Straight s = new Straight(this);
		Point projected = s.getProjection(new Vector(p)).toPoint();
		// XXX: We can use our bounds to do a simple containment test here, as
		// the point was already projected onto the straight through this line.
		// If its not located within the bounds, its not on this line and we
		// have to return start or end point as nearest point.
		if (Double.isNaN(projected.x) || Double.isNaN(projected.y)
				|| Double.isInfinite(projected.x)
				|| Double.isInfinite(projected.y)) {
			return p;
		}
		if (getP1().equals(getP2())
				&& (getP1().equals(projected) || getP2().equals(projected))
				|| getBounds().contains(projected)) {
			return projected;
		} else {
			return Point.nearest(projected, getPoints());
		}
	}

	/**
	 * @see IGeometry#getTransformed(AffineTransform)
	 */
	@Override
	public Line getTransformed(AffineTransform t) {
		return new Line(t.getTransformed(getPoints()));
	}

	@Override
	public boolean intersects(ICurve c) {
		if (c instanceof Line) {
			return intersects((Line) c);
		}
		return super.intersects(c);
	}

	/**
	 * Provides an optimized version of the
	 * {@link BezierCurve#intersects(ICurve)} method.
	 *
	 * @param l
	 *            The {@link Line} to test for intersections.
	 * @return see {@link BezierCurve#intersects(ICurve)}
	 */
	public boolean intersects(Line l) {
		return getIntersection(l) != null;
	}

	@Override
	public boolean overlaps(BezierCurve c) {
		if (c instanceof Line) {
			return overlaps((Line) c);
		}

		// BezierCurve: in order to overlap, all control points have to lie on a
		// straight through its base line
		Straight s = new Straight(this);
		for (Line seg : PointListUtils.toSegmentsArray(c.getPoints(), false)) {
			if (!s.equals(new Straight(seg))) {
				return false;
			}
		}

		// if the base line overlaps, we are done
		if (overlaps(new Line(c.getP1(), c.getP2()))) {
			return true;
		} else {
			// otherwise, we have to delegate to the general implementation for
			// Bezier curves to take care of a degenerated curve, where the
			// handle points are outside the base line of the Bezier curve.
			return super.touches(c);
		}
	}

	/**
	 * Tests whether this {@link Line} and the given other {@link Line} overlap,
	 * i.e. they share an infinite number of {@link Point}s.
	 *
	 * @param l
	 *            the other {@link Line} to test for overlap with this
	 *            {@link Line}
	 * @return <code>true</code> if this {@link Line} and the other {@link Line}
	 *         overlap, otherwise <code>false</code>
	 * @see ICurve#overlaps(ICurve)
	 */
	public boolean overlaps(Line l) {
		return touches(l) && !intersects(l);
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
	 * @return <code>this</code> for convenience
	 */
	public Line setLine(double x1, double y1, double x2, double y2) {
		setP1(new Point(x1, y1));
		setP2(new Point(x2, y2));
		return this;
	}

	/**
	 * Initializes this {@link Line} with the start and end point coordinates of
	 * the given one.
	 *
	 * @param l
	 *            the {@link Line} whose start and end point coordinates should
	 *            be used for initialization
	 * @return <code>this</code> for convenience
	 */
	public Line setLine(Line l) {
		setLine(l.getX1(), l.getY1(), l.getX2(), l.getY2());
		return this;
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
	 * @return <code>this</code> for convenience
	 */
	public Line setLine(Point p1, Point p2) {
		setLine(p1.x, p1.y, p2.x, p2.y);
		return this;
	}

	/**
	 * Sets the x-coordinate of the start {@link Point} of this {@link Line} to
	 * the given value.
	 *
	 * @param x1
	 *            The new x-coordinate for the start {@link Point} of this
	 *            {@link Line}.
	 * @return <code>this</code> for convenience
	 */
	public Line setX1(double x1) {
		setP1(new Point(x1, getY1()));
		return this;
	}

	/**
	 * Sets the x-coordinate of the end {@link Point} of this {@link Line} to
	 * the given value.
	 *
	 * @param x2
	 *            The new x-coordiante for the end {@link Point} of this
	 *            {@link Line}.
	 * @return <code>this</code> for convenience
	 */
	public Line setX2(double x2) {
		setP2(new Point(x2, getY2()));
		return this;
	}

	/**
	 * Sets the y-coordinate of the start {@link Point} of this {@link Line} to
	 * the given value.
	 *
	 * @param y1
	 *            The new y-coordinate for the start {@link Point} of this
	 *            {@link Line}.
	 * @return <code>this</code> for convenience
	 */
	public Line setY1(double y1) {
		setP1(new Point(getX1(), y1));
		return this;
	}

	/**
	 * Sets the y-coordinate of the end {@link Point} of this {@link Line} to
	 * the given value.
	 *
	 * @param y2
	 *            The new y-coordinate for the end {@link Point} of this
	 *            {@link Line}.
	 * @return <code>this</code> for convenience
	 */
	public Line setY2(double y2) {
		setP2(new Point(getX2(), y2));
		return this;
	}

	@Override
	public Path toPath() {
		Path path = new Path();
		path.moveTo(getX1(), getY1());
		path.lineTo(getX2(), getY2());
		return path;
	}

	@Override
	public String toString() {
		return "Line: (" + getX1() + ", " + getY1() + ") -> (" + getX2() + ", " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ getY2() + ")"; //$NON-NLS-1$
	}

	@Override
	public boolean touches(IGeometry g) {
		if (g instanceof Line) {
			return touches((Line) g);
		}
		return super.touches(g);
	}

	/**
	 * Tests whether this {@link Line} and the given one share at least one
	 * common point.
	 *
	 * @param l
	 *            The {@link Line} to test.
	 * @return <code>true</code> if this {@link Line} and the given one share at
	 *         least one common point, <code>false</code> otherwise.
	 */
	public boolean touches(Line l) {
		// TODO: optimize w.r.t. object creation

		/*
		 * 1) check degenerated (the start and end point imprecisely fall
		 * together) and special cases where the lines have to be regarded as
		 * intersecting, because they touch within the used imprecision, though
		 * they would not intersect with absolute precision.
		 */
		Point p1 = getP1();
		Point p2 = getP2();

		boolean touches = l.contains(p1) || l.contains(p2);
		if (touches || p1.equals(p2)) {
			return touches;
		}

		Point lp1 = l.getP1();
		Point lp2 = l.getP2();

		touches = contains(lp1) || contains(lp2);
		if (touches || lp1.equals(lp2)) {
			return touches;
		}
		Vector3D l1 = new Vector3D(p1).getCrossProduct(new Vector3D(p2));
		Vector3D l2 = new Vector3D(lp1).getCrossProduct(new Vector3D(lp2));

		/*
		 * 2) non-degenerated case. If the two respective straight lines
		 * intersect, the intersection has to be contained by both line segments
		 * for the segments to intersect. If the two respective straight lines
		 * do not intersect, because they are parallel, the getIntersection()
		 * method returns null.
		 */
		Point intersection = l1.getCrossProduct(l2).toPoint();
		return intersection != null && contains(intersection)
				&& l.contains(intersection);
	}

}
