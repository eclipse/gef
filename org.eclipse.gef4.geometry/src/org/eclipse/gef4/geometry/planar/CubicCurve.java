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

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.transform.AffineTransform;

/**
 * Represents the geometric shape of a cubic Bézier curve.
 * 
 * TODO: Overwrite all BezierCurve methods that return a BezierCurve and add a
 * cast to a CubicCurve. OR: Make BezierCurve parameterized
 * 
 * @author anyssen
 */
public class CubicCurve extends BezierCurve {

	private static final long serialVersionUID = 1L;

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
		return super.getClipped(t1, t2).toCubic();
	}

	@Override
	public boolean equals(Object other) {
		CubicCurve o = (CubicCurve) other;

		Polygon myPoly = getControlPolygon();
		Polygon otherPoly = o.getControlPolygon();

		return myPoly.equals(otherPoly);
	}

	/**
	 * Erroneous getBounds() implementation... use the generic one instead.
	 * 
	 * TODO: find out why the mathematical solution is erroneous in some cases.
	 * 
	 * @see IGeometry#getBounds()
	 */
	/*
	 * public Rectangle getBounds() { // extremes of the x(t) and y(t)
	 * functions: double[] xts; try { xts =
	 * PolynomCalculationUtils.getQuadraticRoots(-3 * getX1() + 9 getCtrlX1() -
	 * 9 * getCtrlX2() + 3 * getX2(), 6 * getX1() - 12 * getCtrlX1() + 6 *
	 * getCtrlX2(), 3 * getCtrlX1() - 3 getX1()); } catch (ArithmeticException
	 * x) { return new Rectangle(getP1(), getP2()); }
	 * 
	 * double xmin = getX1(), xmax = getX1(); if (getX2() < xmin) { xmin =
	 * getX2(); } else { xmax = getX2(); }
	 * 
	 * for (double t : xts) { if (t >= 0 && t <= 1) { double x = get(t).x; if (x
	 * < xmin) { xmin = x; } else if (x > xmax) { xmax = x; } } }
	 * 
	 * double[] yts; try { yts = PolynomCalculationUtils.getQuadraticRoots(-3 *
	 * getY1() + 9 getCtrlY1() - 9 * getCtrlY2() + 3 * getY2(), 6 * getY1() - 12
	 * * getCtrlY1() + 6 * getCtrlY2(), 3 * getCtrlY1() - 3 getY1()); } catch
	 * (ArithmeticException x) { return new Rectangle(new Point(xmin,
	 * getP1().y), new Point(xmax, getP2().y)); }
	 * 
	 * double ymin = getY1(), ymax = getY1(); if (getY2() < ymin) { ymin =
	 * getY2(); } else { ymax = getY2(); }
	 * 
	 * for (double t : yts) { if (t >= 0 && t <= 1) { double y = get(t).y; if (y
	 * < ymin) { ymin = y; } else if (y > ymax) { ymax = y; } } }
	 * 
	 * return new Rectangle(new Point(xmin, ymin), new Point(xmax, ymax)); }
	 */

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
	@Override
	public CubicCurve getCopy() {
		return new CubicCurve(getP1(), getCtrl1(), getCtrl2(), getP2());
	}

	/**
	 * Returns the first control {@link Point}.
	 * 
	 * @return the first control {@link Point}.
	 */
	public Point getCtrl1() {
		return new Point(getCtrlX1(), getCtrlY1());
	}

	/**
	 * Returns the first control {@link Point}'s x-coordinate.
	 * 
	 * @return the first control {@link Point}'s x-coordinate.
	 */
	public double getCtrlX1() {
		return getPoint(1).x;
	}

	/**
	 * Returns the first control {@link Point}'s y-coordinate.
	 * 
	 * @return the first control {@link Point}'s y-coordinate.
	 */
	public double getCtrlY1() {
		return getPoint(1).y;
	}

	/**
	 * Returns the second control {@link Point}.
	 * 
	 * @return the second control {@link Point}.
	 */
	public Point getCtrl2() {
		return new Point(getCtrlX2(), getCtrlY2());
	}

	/**
	 * Returns the second control {@link Point}'s x-coordinate.
	 * 
	 * @return the second control {@link Point}'s x-coordinate.
	 */
	public double getCtrlX2() {
		return getPoint(2).x;
	}

	/**
	 * Returns the second control {@link Point}'s y-coordinate.
	 * 
	 * @return the second control {@link Point}'s y-coordinate.
	 */
	public double getCtrlY2() {
		return getPoint(2).y;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see IGeometry#getTransformed(AffineTransform)
	 */
	@Override
	public IGeometry getTransformed(AffineTransform t) {
		return null;
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
		setPoint(1, new Point(ctrl1x, getCtrlY1()));
	}

	/**
	 * Sets the first control {@link Point}'s y-coordinate to the given
	 * y-coordinate ctrl1y.
	 * 
	 * @param ctrl1y
	 *            the new first control {@link Point}'s y-coordinate
	 */
	public void setCtrl1Y(double ctrl1y) {
		setPoint(1, new Point(getCtrlX1(), ctrl1y));
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
		setPoint(2, new Point(ctrl2x, getCtrlY2()));
	}

	/**
	 * Sets the second control {@link Point}'s y-coordinate to the given
	 * y-coordinate ctrl2y.
	 * 
	 * @param ctrl2y
	 *            the new second control {@link Point}'s y-coordinate
	 */
	public void setCtrl2Y(double ctrl2y) {
		setPoint(2, new Point(getCtrlX2(), ctrl2y));
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
	@Override
	public CubicCurve[] split(double t) {
		BezierCurve[] split = super.split(t);
		return new CubicCurve[] { split[0].toCubic(), split[1].toCubic() };
	}

	/**
	 * @see org.eclipse.gef4.geometry.planar.IGeometry#toPath()
	 */
	@Override
	public Path toPath() {
		Path p = new Path();
		p.moveTo(getX1(), getY1());
		p.curveTo(getCtrlX1(), getCtrlY1(), getCtrlX2(), getCtrlY2(), getX2(),
				getY2());
		return p;
	}

	@Override
	public String toString() {
		return "CubicCurve(x1 = " + getX1() + ", y1 = " + getY1()
				+ ", ctrl1X = " + getCtrlX1() + ", ctrl1Y = " + getCtrlY1()
				+ ", ctrl2X = " + getCtrlX2() + ", ctrl2Y = " + getCtrlY2()
				+ ", x2 = " + getX2() + ", y2 = " + getY2();
	}

}
