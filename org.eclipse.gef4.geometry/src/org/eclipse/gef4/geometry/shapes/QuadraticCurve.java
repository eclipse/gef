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
import org.eclipse.gef4.geometry.transform.AffineTransform;
import org.eclipse.gef4.geometry.utils.CurveUtils;
import org.eclipse.gef4.geometry.utils.PolynomCalculationUtils;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * Represents the geometric shape of a quadratic Bézier curve.
 * 
 * @author anyssen
 * 
 */
public class QuadraticCurve implements Geometry {

	private static final long serialVersionUID = 1L;
	private double x1, y1, ctrlX, ctrlY, x2, y2;

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
		setP1(p1);
		setCtrl(pCtrl);
		setP2(p2);
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
	 * Constructs a new QuadraticCurve object from the given point coordinates.
	 * 
	 * @param x1
	 *            the start point's x-coordinate
	 * @param y1
	 *            the start point's y-coordinate
	 * @param xCtrl
	 *            the control point's x-coordinate
	 * @param yCtrl
	 *            the control point's y-coordinate
	 * @param x2
	 *            the end point's x-coordinate
	 * @param y2
	 *            the end point's y-coordinate
	 */
	public QuadraticCurve(double x1, double y1, double xCtrl, double yCtrl,
			double x2, double y2) {
		setP1(new Point(x1, y1));
		setCtrl(new Point(xCtrl, yCtrl));
		setP2(new Point(x2, y2));
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
	 * Get the control point.
	 * 
	 * @return a Point object representing the control point
	 */
	public Point getCtrl() {
		return new Point(ctrlX, ctrlY);
	}

	/**
	 * Get the control point's x-coordinate.
	 * 
	 * @return the control point's x-coordinate
	 */
	public double getCtrlX() {
		return ctrlX;
	}

	/**
	 * Get the control point's y-coordinate.
	 * 
	 * @return the control point's y-coordinate
	 */
	public double getCtrlY() {
		return ctrlY;
	}

	/**
	 * Get the curve's starting point.
	 * 
	 * @return the curve's starting point
	 */
	public Point getP1() {
		return new Point(x1, y1);
	}

	/**
	 * Get the curve's ending point.
	 * 
	 * @return the curve's ending point
	 */
	public Point getP2() {
		return new Point(x2, y2);
	}

	public Geometry getTransformed(AffineTransform t) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the x-coordinate of the curve's starting point.
	 * 
	 * @return the x-coordinate of the curve's starting point
	 */
	public double getX1() {
		return x1;
	}

	/**
	 * Returns the x-coordinate of the curve's ending point.
	 * 
	 * @return the x-coordinate of the curve's ending point
	 */
	public double getX2() {
		return x2;
	}

	/**
	 * Returns the y-coordinate of the curve's starting point.
	 * 
	 * @return the y-coordinate of the curve's starting point
	 */
	public double getY1() {
		return y1;
	}

	/**
	 * Returns the y-coordinate of the curve's ending point.
	 * 
	 * @return the y-coordinate of the curve's ending point
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
	 * Sets the curve's control point.
	 * 
	 * @param ctrl
	 */
	public void setCtrl(Point ctrl) {
		this.ctrlX = ctrl.x;
		this.ctrlY = ctrl.y;
	}

	/**
	 * Sets the x-coordinate of the curve's control point.
	 * 
	 * @param ctrlX
	 */
	public void setCtrlX(double ctrlX) {
		this.ctrlX = ctrlX;
	}

	/**
	 * Sets the y-coordinate of the curve's control point.
	 * 
	 * @param ctrlY
	 */
	public void setCtrlY(double ctrlY) {
		this.ctrlY = ctrlY;
	}

	/**
	 * Sets the curve's starting point.
	 * 
	 * @param p1
	 */
	public void setP1(Point p1) {
		this.x1 = p1.x;
		this.y1 = p1.y;
	}

	/**
	 * Sets the curve's ending point.
	 * 
	 * @param p2
	 */
	public void setP2(Point p2) {
		this.x2 = p2.x;
		this.y2 = p2.y;
	}

	/**
	 * Sets the x-coordinate of the curve's starting point.
	 * 
	 * @param x1
	 */
	public void setX1(double x1) {
		this.x1 = x1;
	}

	/**
	 * Sets the x-coordinate of the curve's ending point.
	 * 
	 * @param x2
	 */
	public void setX2(double x2) {
		this.x2 = x2;
	}

	/**
	 * Sets the y-coordinate of the curve's starting point.
	 * 
	 * @param y1
	 */
	public void setY1(double y1) {
		this.y1 = y1;
	}

	/**
	 * Sets the y-coordinate of the curve's ending point.
	 * 
	 * @param y2
	 */
	public void setY2(double y2) {
		this.y2 = y2;
	}

	/**
	 * Transform the QuadraticCurve object to a {@link Path} object with the
	 * same shape.
	 * 
	 * @return a {@link Path} object representing the curve
	 */
	public Path toPath() {
		Path p = new Path();
		p.moveTo(x1, y1);
		p.quadTo(ctrlX, ctrlY, x2, y2);
		return p;
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
	 * Check if the given {@link Point} (approximately) lies on the curve.
	 * 
	 * @param p
	 *            the {@link Point} in question
	 * @return true if p lies on the curve, false otherwise
	 */
	public boolean contains(Point p) {
		return CurveUtils.contains(this, p);
	}

	/**
	 * How can it possibly contain a {@link Rectangle}?
	 * 
	 * @param r
	 *            the {@link Rectangle} in question
	 * @return always false
	 */
	public boolean contains(Rectangle r) {
		return false;
	}

	public boolean equals(Object other) {
		QuadraticCurve o = (QuadraticCurve) other;

		Polygon myPoly = getControlPolygon();
		Polygon otherPoly = o.getControlPolygon();

		return myPoly.equals(otherPoly);
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
	 * Returns the points of intersection between this {@link QuadraticCurve}
	 * and the given {@link Line} l.
	 * 
	 * @param l
	 * @return The points of intersection.
	 */
	public Point[] getIntersections(Line l) {
		return getIntersections(this, 0, 1, l);
	}

	private static double getArea(Polygon p) {
		Rectangle r = p.getBounds();
		return r.getWidth() * r.getHeight();
	}

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
			if (PrecisionUtils.equal(getArea(polygon), 0, +2)) {
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
			double pArea = getArea(pPoly);
			double qArea = getArea(qPoly);

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
	 * Checks if two {@link QuadraticCurve}s intersect each other. (Costly)
	 * 
	 * @param other
	 * @return true if the two curves intersect. false otherwise
	 */
	public boolean intersects(QuadraticCurve other) {
		return getIntersections(other).length > 0;
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

	public boolean intersects(Rectangle r) {
		for (Line l : r.getSegments()) {
			if (intersects(l)) {
				return true;
			}
		}
		return false;
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

}
