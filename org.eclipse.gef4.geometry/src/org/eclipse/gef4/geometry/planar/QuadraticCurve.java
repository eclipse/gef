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
import org.eclipse.gef4.geometry.utils.CurveUtils;
import org.eclipse.gef4.geometry.utils.PolynomCalculationUtils;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * Represents the geometric shape of a quadratic Bézier curve.
 * 
 * @author anyssen
 * 
 */
public class QuadraticCurve extends BezierCurve {

	private static final long serialVersionUID = 1L;

	private static Point[] getIntersections(QuadraticCurve p, double ps,
			double pe, Line l) {
		// parameter convergence test
		double pm = (ps + pe) / 2;

		if (PrecisionUtils.equal(ps, pe, +2)) {
			return new Point[] { p.get(pm) };
		}

		// no parameter convergence

		// clip the curve
		QuadraticCurve pc = p.clip(ps, pe);

		// check the control polygon
		Polygon polygon = pc.getControlPolygon();

		if (polygon.intersects(l)) {
			// area test
			if (PrecisionUtils.equal(polygon.getBounds().getArea(), 0, +2)) {
				// line/line intersection fallback for such small curves
				Point poi = new Line(pc.getP1(), pc.getP2()).getIntersection(l);
				if (poi != null) {
					return new Point[] { poi };
				}
				return new Point[] {};
			}

			// individually test the curves left and right sides for points of
			// intersection
			HashSet<Point> intersections = new HashSet<Point>();

			intersections.addAll(Arrays.asList(getIntersections(p, ps, pm, l)));
			intersections.addAll(Arrays.asList(getIntersections(p, pm, pe, l)));

			return intersections.toArray(new Point[] {});
		}

		// no intersections
		return new Point[] {};
	}

	private static Point[] getIntersections(QuadraticCurve p, double ps,
			double pe, QuadraticCurve q, double qs, double qe) {
		// parameter convergence test
		double pm = (ps + pe) / 2;
		double qm = (qs + qe) / 2;

		if (PrecisionUtils.equal(ps, pe)) {
			return new Point[] { p.get(pm) };
		}

		if (PrecisionUtils.equal(qs, qe)) {
			return new Point[] { q.get(qm) };
		}

		// no parameter convergence

		// clip to parameter ranges
		QuadraticCurve pc = p.clip(ps, pe);
		QuadraticCurve qc = q.clip(qs, qe);

		// check the control polygons
		Polygon pPoly = pc.getControlPolygon();
		Polygon qPoly = qc.getControlPolygon();

		if (pPoly.intersects(qPoly)) {
			// check the polygon's areas
			double pArea = pPoly.getBounds().getArea();
			double qArea = qPoly.getBounds().getArea();

			if (PrecisionUtils.equal(pArea, 0, -2)
					&& PrecisionUtils.equal(qArea, 0, -2)) {
				// return line/line intersection
				Point poi = new Line(pc.getP1(), pc.getP2())
						.getIntersection(new Line(qc.getP1(), qc.getP2()));
				if (poi != null) {
					return new Point[] { poi };
				}
				return new Point[] {};
			}

			// areas not small enough

			// individually test the left and right parts of the curves for
			// points of intersection
			HashSet<Point> intersections = new HashSet<Point>();

			intersections.addAll(Arrays.asList(getIntersections(p, ps, pm, q,
					qs, qm)));
			intersections.addAll(Arrays.asList(getIntersections(p, ps, pm, q,
					qm, qe)));
			intersections.addAll(Arrays.asList(getIntersections(p, pm, pe, q,
					qs, qm)));
			intersections.addAll(Arrays.asList(getIntersections(p, pm, pe, q,
					qm, qe)));

			return intersections.toArray(new Point[] {});
		}

		// no intersections
		return new Point[] {};
	}

	/**
	 * Constructs a new {@link QuadraticCurve} from the given sequence of x- and
	 * y-coordinates of the start-, the control-, and the end-point.
	 * 
	 * @param coordinates
	 *            a sequence containing the x- and y-coordinates
	 * 
	 * @see QuadraticCurve#QuadraticCurve(double, double, double, double,
	 *      double, double)
	 */
	public QuadraticCurve(double... coordinates) {
		this(coordinates[0], coordinates[1], coordinates[2], coordinates[3],
				coordinates[4], coordinates[5]);
	}

	/**
	 * Constructs a new QuadraticCurve object from the given point coordinates.
	 * 
	 * @param x1
	 *            the start point's x-coordinate
	 * @param y1
	 *            the start point's y-coordinate
	 * @param ctrlX
	 *            the control point's x-coordinate
	 * @param ctrlY
	 *            the control point's y-coordinate
	 * @param x2
	 *            the end point's x-coordinate
	 * @param y2
	 *            the end point's y-coordinate
	 */
	public QuadraticCurve(double x1, double y1, double ctrlX, double ctrlY,
			double x2, double y2) {
		super(x1, y1, ctrlX, ctrlY, x2, y2);
	}

	/**
	 * Constructs a new {@link QuadraticCurve} from the given sequence of
	 * {@link Point}s formed by start-, control-, and end-point.
	 * 
	 * @param points
	 *            the control {@link Point}s
	 * 
	 * @see QuadraticCurve#QuadraticCurve(Point, Point, Point)
	 */
	public QuadraticCurve(Point... points) {
		this(points[0], points[1], points[2]);
	}

	/**
	 * Constructs a new QuadraticCurve object from the given points.
	 * 
	 * @param p1
	 *            the start point
	 * @param pCtrl
	 *            the control point
	 * @param p2
	 *            the end point
	 */
	public QuadraticCurve(Point p1, Point pCtrl, Point p2) {
		this(p1.x, p1.y, pCtrl.x, pCtrl.y, p2.x, p2.y);
	}

	/**
	 * Clips this {@link QuadraticCurve} at parameter values t1 and t2 so that
	 * the resulting {@link QuadraticCurve} is the section of the original
	 * {@link QuadraticCurve} for the parameter interval [t1, t2].
	 * 
	 * @param t1
	 * @param t2
	 * @return the {@link QuadraticCurve} on the interval [t1, t2]
	 */
	public QuadraticCurve clip(double t1, double t2) {
		return CurveUtils.clip(this, t1, t2);
	}

	/**
	 * Check if the given {@link Point} (approximately) lies on the curve.
	 * 
	 * @param p
	 *            the {@link Point} in question
	 * @return true if p lies on the curve, false otherwise
	 */
	public boolean contains(Point p) {
		return CurveUtils.contains(this, p);
	}

	public boolean equals(Object other) {
		QuadraticCurve o = (QuadraticCurve) other;

		Polygon myPoly = getControlPolygon();
		Polygon otherPoly = o.getControlPolygon();

		return myPoly.equals(otherPoly);
	}

	/**
	 * Get a single {@link Point} on this QuadraticCurve at parameter t.
	 * 
	 * @param t
	 *            in range [0,1]
	 * @return the {@link Point} at parameter t
	 */
	public Point get(double t) {
		return CurveUtils.get(this, t);
	}

	/**
	 * Returns the bounds of this QuadraticCurve. The bounds are calculated by
	 * examining the extreme points of the x(t) and y(t) function
	 * representations of this QuadraticCurve.
	 * 
	 * @return the bounds {@link Rectangle}
	 */
	public Rectangle getBounds() {
		// extremes of the x(t) and y(t) functions:
		double[] xts;
		try {
			xts = PolynomCalculationUtils.getLinearRoots(2 * (getX1() - 2
					* getCtrlX() + getX2()), 2 * (getCtrlX() - getX1()));
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
			yts = PolynomCalculationUtils.getLinearRoots(2 * (getY1() - 2
					* getCtrlY() + getY2()), 2 * (getCtrlY() - getY1()));
		} catch (ArithmeticException x) {
			return new Rectangle(getP1(), getP2());
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
		return new Polygon(getP1(), getCtrl(), getP2());
	}

	/**
	 * Returns a new {@link QuadraticCurve}, which has the same start, end, and
	 * control point coordinates as this one.
	 * 
	 * @return a new {@link QuadraticCurve} with the same start, end, and
	 *         control point coordinates
	 */
	public QuadraticCurve getCopy() {
		return new QuadraticCurve(getP1(), getCtrl(), getP2());
	}

	/**
	 * Get the control point.
	 * 
	 * @return a Point object representing the control point
	 */
	public Point getCtrl() {
		return new Point(getCtrlX(), getCtrlY());
	}

	/**
	 * Get the control point's x-coordinate.
	 * 
	 * @return the control point's x-coordinate
	 */
	public double getCtrlX() {
		return getCtrlX(0);
	}

	/**
	 * Get the control point's y-coordinate.
	 * 
	 * @return the control point's y-coordinate
	 */
	public double getCtrlY() {
		return getCtrlY(0);
	}

	/**
	 * Degree elevation: Returns a {@link CubicCurve} representation of this
	 * {@link QuadraticCurve}.
	 * 
	 * @return A {@link CubicCurve} that represents this {@link QuadraticCurve}.
	 */
	public CubicCurve getElevated() {
		Point[] controlPoints = new Point[4];

		// "Curves and Surfaces for Computer Aided Geometric Design" by Farin,
		// Gerald E., Academic Press 1988
		controlPoints[0] = getP1();
		controlPoints[1] = getP1().getScaled(1d / 3d).getTranslated(
				getCtrl().getScaled(2d / 3d));
		controlPoints[2] = getCtrl().getScaled(2d / 3d).getTranslated(
				getP2().getScaled(1d / 3d));
		controlPoints[3] = getP2();

		return new CubicCurve(controlPoints);
	}

	/**
	 * Returns the points of intersection between this {@link QuadraticCurve}
	 * and the given {@link Line} l.
	 * 
	 * @param l
	 * @return The points of intersection.
	 */
	public Point[] getIntersections(Line l) {
		return getIntersections(this, 0, 1, l);
	}

	/**
	 * Calculates the intersections of two {@link QuadraticCurve}s using the
	 * subdivision algorithm.
	 * 
	 * @param other
	 * @return points of intersection
	 */
	public Point[] getIntersections(QuadraticCurve other) {
		return getIntersections(this, 0, 1, other, 0, 1);
	}

	/**
	 * Checks if this {@link QuadraticCurve} intersects with the given line.
	 * (Costly)
	 * 
	 * TODO: implement a faster algorithm for this intersection-test.
	 * 
	 * @param l
	 * @return true if they intersect, false otherwise.
	 */
	public boolean intersects(Line l) {
		return getIntersections(l).length > 0;
	}

	/**
	 * Checks if two {@link QuadraticCurve}s intersect each other. (Costly)
	 * 
	 * @param other
	 * @return true if the two curves intersect. false otherwise
	 */
	public boolean intersects(QuadraticCurve other) {
		return getIntersections(other).length > 0;
	}

	public boolean intersects(Rectangle r) {
		for (Line l : r.getOutlineSegments()) {
			if (intersects(l)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets the curve's control point.
	 * 
	 * @param ctrl
	 */
	public void setCtrl(Point ctrl) {
		setCtrlX(ctrl.x);
		setCtrlY(ctrl.y);
	}

	/**
	 * Sets the x-coordinate of the curve's control point.
	 * 
	 * @param ctrlX
	 */
	public void setCtrlX(double ctrlX) {
		setCtrlX(0, ctrlX);
	}

	/**
	 * Sets the y-coordinate of the curve's control point.
	 * 
	 * @param ctrlY
	 */
	public void setCtrlY(double ctrlY) {
		setCtrlY(0, ctrlY);
	}

	/**
	 * Splits this QuadraticCurve using the de Casteljau algorithm at parameter
	 * t into two separate QuadraticCurve objects. The returned
	 * {@link QuadraticCurve}s are the curves for [0, t] and [t, 1].
	 * 
	 * @param t
	 *            in range [0,1]
	 * @return two QuadraticCurve objects constituting the original curve: 1.
	 *         [0, t] 2. [t, 1]
	 */
	public QuadraticCurve[] split(double t) {
		return CurveUtils.split(this, t);
	}

	/**
	 * Transform the QuadraticCurve object to a {@link Path} object with the
	 * same shape.
	 * 
	 * @return a {@link Path} object representing the curve
	 */
	public Path toPath() {
		Path p = new Path();
		p.moveTo(getX1(), getY1());
		p.quadTo(getCtrlX(), getCtrlY(), getX1(), getY2());
		return p;
	}

}
