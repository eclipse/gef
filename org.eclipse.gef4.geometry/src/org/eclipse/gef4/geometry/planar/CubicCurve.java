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

import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.transform.AffineTransform;
import org.eclipse.gef4.geometry.utils.CurveUtils;
import org.eclipse.gef4.geometry.utils.PolynomCalculationUtils;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * Represents the geometric shape of a cubic Bézier curve.
 * 
 * @author anyssen
 * 
 */
public class CubicCurve extends BezierCurve {

	private static final long serialVersionUID = 1L;

	private static Point[] getIntersections(CubicCurve p, double ps, double pe,
			Line l) {
		// parameter convergence test
		double pm = (ps + pe) / 2;

		if (PrecisionUtils.equal(ps, pe, -2)) {
			return new Point[] { p.get(pm) };
		}

		// no parameter convergence
		// clip the curve
		CubicCurve pc = p.clip(ps, pe);

		// check the control polygon
		Polygon polygon = pc.getControlPolygon();

		if (polygon.intersects(l)) {
			// area test
			if (PrecisionUtils.equal(polygon.getBounds().getArea(), 0, -2)) {
				// line/line intersection fallback for such small curves
				Point poi = new Line(pc.getP1(), pc.getP2()).getIntersection(l);
				if (poi != null) {
					return new Point[] { poi };
				}
				return new Point[] {};
			}

			// "split" the curve to get precise intersections
			HashSet<Point> intersections = new HashSet<Point>();

			intersections.addAll(Arrays.asList(getIntersections(p, ps, pm, l)));
			intersections.addAll(Arrays.asList(getIntersections(p, pm, pe, l)));

			return intersections.toArray(new Point[] {});
		}

		// no intersections
		return new Point[] {};
	}

	/**
	 * Constructs a new {@link CubicCurve} object with the given sequence of x-
	 * and y-coordinates of the start-, the first and second control-, and the
	 * end-point.
	 * 
	 * @param coordinates
	 *            a sequence of coordinates
	 * 
	 * @see CubicCurve#CubicCurve(double, double, double, double, double,
	 *      double, double, double)
	 */
	public CubicCurve(double... coordinates) {
		this(coordinates[0], coordinates[1], coordinates[2], coordinates[3],
				coordinates[4], coordinates[5], coordinates[6], coordinates[7]);
	}

	/**
	 * Constructs a new {@link CubicCurve} object with the given control point
	 * coordinates.
	 * 
	 * @param x1
	 *            x-coordinate of the start point
	 * @param y1
	 *            y-coordinate of the start point
	 * @param ctrl1X
	 *            x-coordinate of the first control point
	 * @param ctrl1Y
	 *            y-coordinate of the first control point
	 * @param ctrl2X
	 *            x-coordinate of the second control point
	 * @param ctrl2Y
	 *            y-coordinate of the second control point
	 * @param x2
	 *            x-coordinate of the end point
	 * @param y2
	 *            y-coordinate of the end point
	 */
	public CubicCurve(double x1, double y1, double ctrl1X, double ctrl1Y,
			double ctrl2X, double ctrl2Y, double x2, double y2) {
		super(x1, y1, ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, x2, y2);
	}

	/**
	 * Constructs a new {@link CubicCurve} with the given sequence of points,
	 * which is expected in the order: start point, first and second control
	 * point, and end point.
	 * 
	 * @param points
	 *            a sequence of {@link Point}s
	 * 
	 * @see CubicCurve#CubicCurve(Point, Point, Point, Point)
	 */
	public CubicCurve(Point... points) {
		this(points[0].x, points[0].y, points[1].x, points[1].y, points[2].x,
				points[2].y, points[3].x, points[3].y);
	}

	/**
	 * Constructs a new {@link CubicCurve} object with the given control
	 * {@link Point}s.
	 * 
	 * @param start
	 *            the start point
	 * @param ctrl1
	 *            the first control point
	 * @param ctrl2
	 *            the second control point
	 * @param end
	 *            the end point
	 */
	public CubicCurve(Point start, Point ctrl1, Point ctrl2, Point end) {
		this(start.x, start.y, ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y, end.x, end.y);
	}

	/**
	 * Clips this {@link CubicCurve} at parameter values t1 and t2 so that the
	 * resulting {@link CubicCurve} is the section of the original
	 * {@link CubicCurve} for the parameter interval [t1, t2].
	 * 
	 * @param t1
	 * @param t2
	 * @return the {@link CubicCurve} on the interval [t1, t2]
	 */
	public CubicCurve clip(double t1, double t2) {
		return CurveUtils.clip(this, t1, t2);
	}

	/**
	 * @see IGeometry#contains(Point)
	 */
	public boolean contains(Point p) {
		return CurveUtils.contains(this, p);
	}

	@Override
	public boolean equals(Object other) {
		CubicCurve o = (CubicCurve) other;

		Polygon myPoly = getControlPolygon();
		Polygon otherPoly = o.getControlPolygon();

		return myPoly.equals(otherPoly);
	}

	/**
	 * Get a single {@link Point} on this CubicCurve at parameter t.
	 * 
	 * @param t
	 *            in range [0,1]
	 * @return the {@link Point} at parameter t
	 */
	public Point get(double t) {
		return CurveUtils.get(this, t);
	}

	/**
	 * @see IGeometry#getBounds()
	 */
	public Rectangle getBounds() {
		// extremes of the x(t) and y(t) functions:
		double[] xts;
		try {
			xts = PolynomCalculationUtils.getQuadraticRoots(-3 * getX1() + 9
					* getCtrl1X() - 9 * getCtrl2X() + 3 * getX2(), 6 * getX1()
					- 12 * getCtrl1X() + 6 * getCtrl2X(), 3 * getCtrl1X() - 3
					* getX1());
		} catch (ArithmeticException x) {
			return new Rectangle(getP1(), getP2());
		}

		double xmin = getX1(), xmax = getX1();
		if (getX2() < xmin) {
			xmin = getX2();
		} else {
			xmax = getX2();
		}

		for (double t : xts) {
			if (t >= 0 && t <= 1) {
				double x = get(t).x;
				if (x < xmin) {
					xmin = x;
				} else if (x > xmax) {
					xmax = x;
				}
			}
		}

		double[] yts;
		try {
			yts = PolynomCalculationUtils.getQuadraticRoots(-3 * getY1() + 9
					* getCtrl1Y() - 9 * getCtrl2Y() + 3 * getY2(), 6 * getY1()
					- 12 * getCtrl1Y() + 6 * getCtrl2Y(), 3 * getCtrl1Y() - 3
					* getY1());
		} catch (ArithmeticException x) {
			return new Rectangle(new Point(xmin, getP1().y), new Point(xmax,
					getP2().y));
		}

		double ymin = getY1(), ymax = getY1();
		if (getY2() < ymin) {
			ymin = getY2();
		} else {
			ymax = getY2();
		}

		for (double t : yts) {
			if (t >= 0 && t <= 1) {
				double y = get(t).y;
				if (y < ymin) {
					ymin = y;
				} else if (y > ymax) {
					ymax = y;
				}
			}
		}

		return new Rectangle(new Point(xmin, ymin), new Point(xmax, ymax));
	}

	private Polygon getControlPolygon() {
		return new Polygon(getP1(), getCtrl1(), getCtrl2(), getP2());
	}

	/**
	 * Returns a new {@link CubicCurve}, which has the same start, end, and
	 * control point coordinates as this one.
	 * 
	 * @return a new {@link CubicCurve} with the same start, end, and control
	 *         point coordinates
	 */
	public BezierCurve getCopy() {
		return new CubicCurve(getP1(), getCtrl1(), getCtrl2(), getP2());
	}

	/**
	 * Returns the first control {@link Point}.
	 * 
	 * @return the first control {@link Point}.
	 */
	public Point getCtrl1() {
		return new Point(getCtrl1X(), getCtrl1Y());
	}

	/**
	 * Returns the first control {@link Point}'s x-coordinate.
	 * 
	 * @return the first control {@link Point}'s x-coordinate.
	 */
	public double getCtrl1X() {
		return getCtrlX(0);
	}

	/**
	 * Returns the first control {@link Point}'s y-coordinate.
	 * 
	 * @return the first control {@link Point}'s y-coordinate.
	 */
	public double getCtrl1Y() {
		return getCtrlY(0);
	}

	/**
	 * Returns the second control {@link Point}.
	 * 
	 * @return the second control {@link Point}.
	 */
	public Point getCtrl2() {
		return new Point(getCtrl2X(), getCtrl2Y());
	}

	/**
	 * Returns the second control {@link Point}'s x-coordinate.
	 * 
	 * @return the second control {@link Point}'s x-coordinate.
	 */
	public double getCtrl2X() {
		return getCtrlX(1);
	}

	/**
	 * Returns the second control {@link Point}'s y-coordinate.
	 * 
	 * @return the second control {@link Point}'s y-coordinate.
	 */
	public double getCtrl2Y() {
		return getCtrlY(1);
	}

	/**
	 * Returns the points of intersection between this {@link CubicCurve} and
	 * the given {@link Arc}.
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
	 * Returns the points of intersection between this {@link CubicCurve} and
	 * the given other {@link CubicCurve}.
	 * 
	 * @param other
	 * @return the points of intersection
	 */
	public Point[] getIntersections(CubicCurve other) {
		if (equals(other)) {
			return new Point[] {};
		}
		return CurveUtils.getIntersections(this, other);
	}

	/**
	 * Returns the points of intersection between this {@link CubicCurve} and
	 * the given {@link Ellipse}.
	 * 
	 * @param e
	 *            The {@link Ellipse} to test for intersections
	 * @return the points of intersection.
	 */
	public Point[] getIntersections(Ellipse e) {
		return e.getIntersections(this);
	}

	/**
	 * Returns the points of intersection between this {@link CubicCurve} and
	 * the given {@link Line} l.
	 * 
	 * @param l
	 * @return the points of intersection
	 */
	public Point[] getIntersections(Line l) {
		return getIntersections(this, 0, 1, l);
	}

	/**
	 * Returns the points of intersection between this {@link CubicCurve} and
	 * the given {@link Polygon}.
	 * 
	 * @param p
	 *            The {@link Polygon} to test for intersections
	 * @return the points of intersection.
	 */
	public Point[] getIntersections(Polygon p) {
		return p.getIntersections(this);
	}

	/**
	 * Returns the points of intersection between this {@link CubicCurve} and
	 * the given {@link Polyline}.
	 * 
	 * @param p
	 *            The {@link Polyline} to test for intersections
	 * @return the points of intersection.
	 */
	public Point[] getIntersections(Polyline p) {
		HashSet<Point> intersections = new HashSet<Point>();

		for (Line seg : p.getSegments()) {
			intersections.addAll(Arrays.asList(getIntersections(seg)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Returns the points of intersection between this {@link CubicCurve} and
	 * the given {@link QuadraticCurve}.
	 * 
	 * @param c
	 *            The {@link QuadraticCurve} to test for intersections
	 * @return the points of intersection.
	 */
	public Point[] getIntersections(QuadraticCurve c) {
		return getIntersections(c.getElevated());
	}

	/**
	 * Returns the points of intersection between this {@link CubicCurve} and
	 * the given {@link Rectangle}.
	 * 
	 * @param r
	 *            The {@link Rectangle} to test for intersections
	 * @return the points of intersection.
	 */
	public Point[] getIntersections(Rectangle r) {
		HashSet<Point> intersections = new HashSet<Point>();

		for (Line seg : r.getOutlineSegments()) {
			intersections.addAll(Arrays.asList(getIntersections(seg)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Returns the points of intersection between this {@link CubicCurve} and
	 * the given {@link RoundedRectangle}.
	 * 
	 * @param rr
	 *            The {@link RoundedRectangle} to test for intersections
	 * @return the points of intersection.
	 */
	public Point[] getIntersections(RoundedRectangle rr) {
		HashSet<Point> intersections = new HashSet<Point>();

		// line segments
		intersections.addAll(Arrays.asList(getIntersections(rr.getTop())));
		intersections.addAll(Arrays.asList(getIntersections(rr.getLeft())));
		intersections.addAll(Arrays.asList(getIntersections(rr.getBottom())));
		intersections.addAll(Arrays.asList(getIntersections(rr.getRight())));

		// arc segments
		intersections.addAll(Arrays.asList(getIntersections(rr.getTopRightArc())));
		intersections.addAll(Arrays.asList(getIntersections(rr.getTopLeftArc())));
		intersections
				.addAll(Arrays.asList(getIntersections(rr.getBottomLeftArc())));
		intersections
				.addAll(Arrays.asList(getIntersections(rr.getBottomRightArc())));

		return intersections.toArray(new Point[] {});
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see IGeometry#getTransformed(AffineTransform)
	 */
	public IGeometry getTransformed(AffineTransform t) {
		return null;
	}

	/**
	 * Tests if this {@link CubicCurve} intersects the given {@link Line} r.
	 * 
	 * @param r
	 * @return true if they intersect, false otherwise
	 */
	public boolean intersects(Line r) {
		return getIntersections(r).length > 0;
	}

	/**
	 * @see org.eclipse.gef4.geometry.planar.IGeometry#intersects(Rectangle)
	 */
	public boolean intersects(Rectangle r) {
		return false;
	}

	/**
	 * Sets the first control {@link Point} to the given {@link Point} ctrl1.
	 * 
	 * @param ctrl1
	 *            the new first control {@link Point}
	 */
	public void setCtrl1(Point ctrl1) {
		setCtrl1X(ctrl1.x);
		setCtrl1Y(ctrl1.y);
	}

	/**
	 * Sets the first control {@link Point}'s x-coordinate to the given
	 * x-coordinate ctrl1x.
	 * 
	 * @param ctrl1x
	 *            the new first control {@link Point}'s x-coordinate
	 */
	public void setCtrl1X(double ctrl1x) {
		setCtrlX(0, ctrl1x);
	}

	/**
	 * Sets the first control {@link Point}'s y-coordinate to the given
	 * y-coordinate ctrl1y.
	 * 
	 * @param ctrl1y
	 *            the new first control {@link Point}'s y-coordinate
	 */
	public void setCtrl1Y(double ctrl1y) {
		setCtrlY(0, ctrl1y);
	}

	/**
	 * Sets the second control {@link Point} to the given {@link Point} ctrl2.
	 * 
	 * @param ctrl2
	 *            the new second control {@link Point}
	 */
	public void setCtrl2(Point ctrl2) {
		setCtrl2X(ctrl2.x);
		setCtrl2Y(ctrl2.y);
	}

	/**
	 * Sets the second control {@link Point}'s x-coordinate to the given
	 * x-coordinate ctrl2x.
	 * 
	 * @param ctrl2x
	 *            the new second control {@link Point}'s x-coordinate
	 */
	public void setCtrl2X(double ctrl2x) {
		setCtrlX(1, ctrl2x);
	}

	/**
	 * Sets the second control {@link Point}'s y-coordinate to the given
	 * y-coordinate ctrl2y.
	 * 
	 * @param ctrl2y
	 *            the new second control {@link Point}'s y-coordinate
	 */
	public void setCtrl2Y(double ctrl2y) {
		setCtrlY(1, ctrl2y);
	}

	/**
	 * Sets all control points of this {@link CubicCurve} to the given control
	 * {@link Point}s.
	 * 
	 * @param p1
	 *            the new start {@link Point}
	 * @param ctrl1
	 *            the new first control {@link Point}
	 * @param ctrl2
	 *            the new second control {@link Point}
	 * @param p2
	 *            the new end {@link Point}
	 */
	public void setCurve(Point p1, Point ctrl1, Point ctrl2, Point p2) {
		setP1(p1);
		setCtrl1(ctrl1);
		setCtrl2(ctrl2);
		setP2(p2);
	}

	/**
	 * Subdivides this {@link CubicCurve} into two {@link CubicCurve}s on the
	 * intervals [0, t] and [t, 1] using the de-Casteljau-algorithm.
	 * 
	 * @param t
	 *            split point's parameter value
	 * @return the two {@link CubicCurve}s
	 */
	public CubicCurve[] split(double t) {
		return CurveUtils.split(this, t);
	}

	/**
	 * @see org.eclipse.gef4.geometry.planar.IGeometry#toPath()
	 */
	public Path toPath() {
		Path p = new Path();
		p.moveTo(getX1(), getY1());
		p.curveTo(getCtrl1X(), getCtrl1Y(), getCtrl2X(), getCtrl2Y(), getX2(),
				getY2());
		return p;
	}

	public String toString() {
		return "CubicCurve(x1 = " + getX1() + ", y1 = " + getY1()
				+ ", ctrl1X = " + getCtrl1X() + ", ctrl1Y = " + getCtrl1Y()
				+ ", ctrl2X = " + getCtrl2X() + ", ctrl2Y = " + getCtrl2Y()
				+ ", x2 = " + getX2() + ", y2 = " + getY2();
	}

}
