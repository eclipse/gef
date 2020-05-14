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

/**
 * An instance of the {@link CubicCurve} class represents a {@link BezierCurve}
 * of degree 3, having a start and an end {@link Point} and two handle
 * {@link Point}s.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class CubicCurve extends BezierCurve {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@link CubicCurve} object with the given sequence of x
	 * and y coordinates of the start {@link Point}, the two handle
	 * {@link Point}s, and the end {@link Point}.
	 *
	 * @param coordinates
	 *            the sequence of x and y coordinates specifying this
	 *            {@link CubicCurve}'s control {@link Point}s
	 * @see CubicCurve#CubicCurve(double, double, double, double, double,
	 *      double, double, double)
	 */
	public CubicCurve(double... coordinates) {
		this(coordinates[0], coordinates[1], coordinates[2], coordinates[3],
				coordinates[4], coordinates[5], coordinates[6], coordinates[7]);
		if (coordinates.length != 8) {
			throw new IllegalArgumentException(
					"A CubicCurve may only be defined by 8 coordinates (4 points), while "
							+ coordinates.length + " were passed in.");
		}
	}

	/**
	 * Constructs a new {@link CubicCurve} object from the given control
	 * {@link Point} coordinates.
	 *
	 * @param x1
	 *            the x coordinate of the start {@link Point}
	 * @param y1
	 *            the y coordinate of the start {@link Point}
	 * @param ctrl1X
	 *            the x coordinate of the first handle {@link Point}
	 * @param ctrl1Y
	 *            the y coordinate of the first handle {@link Point}
	 * @param ctrl2X
	 *            the x coordinate of the second handle {@link Point}
	 * @param ctrl2Y
	 *            the y coordinate of the second handle {@link Point}
	 * @param x2
	 *            the x coordinate of the end {@link Point}
	 * @param y2
	 *            the y coordinate of the end {@link Point}
	 */
	public CubicCurve(double x1, double y1, double ctrl1X, double ctrl1Y,
			double ctrl2X, double ctrl2Y, double x2, double y2) {
		super(x1, y1, ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, x2, y2);
	}

	/**
	 * Constructs a new {@link CubicCurve} from the given sequence of
	 * {@link Point}s, which is expected to be in the order: start {@link Point}
	 * , first and second handle {@link Point}s, and end {@link Point}.
	 *
	 * @param points
	 *            the sequence of {@link Point}s from which this
	 *            {@link CubicCurve} is constructed
	 * @see CubicCurve#CubicCurve(Point, Point, Point, Point)
	 * @see CubicCurve#CubicCurve(double, double, double, double, double,
	 *      double, double, double)
	 */
	public CubicCurve(Point... points) {
		this(points[0].x, points[0].y, points[1].x, points[1].y, points[2].x,
				points[2].y, points[3].x, points[3].y);
		if (points.length != 4) {
			throw new IllegalArgumentException(
					"A CubicCurve may only be defined by 4 points, while "
							+ points.length + " were passed in.");
		}
	}

	/**
	 * Constructs a new {@link CubicCurve} object from the given control
	 * {@link Point}s.
	 *
	 * @param start
	 *            the start {@link Point}
	 * @param ctrl1
	 *            the first handle {@link Point}
	 * @param ctrl2
	 *            the second handle {@link Point}
	 * @param end
	 *            the end {@link Point}
	 * @see CubicCurve#CubicCurve(double, double, double, double, double,
	 *      double, double, double)
	 */
	public CubicCurve(Point start, Point ctrl1, Point ctrl2, Point end) {
		this(start.x, start.y, ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y, end.x,
				end.y);
	}

	/**
	 * Returns a new {@link CubicCurve}, which has the same control
	 * {@link Point}s as this one.
	 *
	 * @return a new {@link CubicCurve} with the same control {@link Point}s as
	 *         this one
	 */
	@Override
	public CubicCurve getCopy() {
		return new CubicCurve(getP1(), getCtrl1(), getCtrl2(), getP2());
	}

	/**
	 * Returns the first handle {@link Point}.
	 *
	 * @return the first handle {@link Point}
	 */
	public Point getCtrl1() {
		return new Point(getCtrlX1(), getCtrlY1());
	}

	/**
	 * Returns the second handle {@link Point}.
	 *
	 * @return the second handle {@link Point}
	 */
	public Point getCtrl2() {
		return new Point(getCtrlX2(), getCtrlY2());
	}

	/**
	 * Returns the first handle {@link Point}'s x coordinate.
	 *
	 * @return the first handle {@link Point}'s x coordinate
	 */
	public double getCtrlX1() {
		return getPoint(1).x;
	}

	/**
	 * Returns the second handle {@link Point}'s x coordinate.
	 *
	 * @return the second handle {@link Point}'s x coordinate
	 */
	public double getCtrlX2() {
		return getPoint(2).x;
	}

	/**
	 * Returns the first handle {@link Point}'s y coordinate.
	 *
	 * @return the first handle {@link Point}'s y coordinate
	 */
	public double getCtrlY1() {
		return getPoint(1).y;
	}

	/**
	 * Returns the second handle {@link Point}'s y coordinate.
	 *
	 * @return the second handle {@link Point}'s y coordinate
	 */
	public double getCtrlY2() {
		return getPoint(2).y;
	}

	@Override
	public CubicCurve getTransformed(AffineTransform t) {
		return new CubicCurve(t.getTransformed(getPoints()));
	}

	/**
	 * Sets the first handle {@link Point} of this {@link CubicCurve} to the
	 * given {@link Point}.
	 *
	 * @param ctrl1
	 *            the new first handle {@link Point}
	 * @return <code>this</code> for convenience
	 */
	public CubicCurve setCtrl1(Point ctrl1) {
		setCtrl1X(ctrl1.x);
		setCtrl1Y(ctrl1.y);
		return this;
	}

	/**
	 * Sets the x coordinate of the first handle {@link Point} of this
	 * {@link CubicCurve} to the given value.
	 *
	 * @param ctrl1x
	 *            the new x coordinate of the first handle {@link Point} of this
	 *            {@link CubicCurve}
	 * @return <code>this</code> for convenience
	 */
	public CubicCurve setCtrl1X(double ctrl1x) {
		setPoint(1, new Point(ctrl1x, getCtrlY1()));
		return this;
	}

	/**
	 * Sets the y coordinate of the first handle {@link Point} of this
	 * {@link CubicCurve} to the given value.
	 *
	 * @param ctrl1y
	 *            the new y coordinate of the first handle {@link Point} of this
	 *            {@link CubicCurve}
	 * @return <code>this</code> for convenience
	 */
	public CubicCurve setCtrl1Y(double ctrl1y) {
		setPoint(1, new Point(getCtrlX1(), ctrl1y));
		return this;
	}

	/**
	 * Sets the second handle {@link Point} of this {@link CubicCurve} to the
	 * given {@link Point}.
	 *
	 * @param ctrl2
	 *            the new second handle {@link Point} of this {@link CubicCurve}
	 * @return <code>this</code> for convenience
	 */
	public CubicCurve setCtrl2(Point ctrl2) {
		setCtrl2X(ctrl2.x);
		setCtrl2Y(ctrl2.y);
		return this;
	}

	/**
	 * Sets the x coordinate of the second handle {@link Point} of this
	 * {@link CubicCurve} to the given value.
	 *
	 * @param ctrl2x
	 *            the new x coordinate of the second handle {@link Point} of
	 *            this {@link CubicCurve}
	 * @return <code>this</code> for convenience
	 */
	public CubicCurve setCtrl2X(double ctrl2x) {
		setPoint(2, new Point(ctrl2x, getCtrlY2()));
		return this;
	}

	/**
	 * Sets the y coordinate of the second handle {@link Point} of this
	 * {@link CubicCurve} to the given value.
	 *
	 * @param ctrl2y
	 *            the new y coordinate of the second handle {@link Point} of
	 *            this {@link CubicCurve}
	 * @return <code>this</code> for convenience
	 */
	public CubicCurve setCtrl2Y(double ctrl2y) {
		setPoint(2, new Point(getCtrlX2(), ctrl2y));
		return this;
	}

	/**
	 * Sets all control {@link Point}s of this {@link CubicCurve} to the given
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
	 * @return <code>this</code> for convenience
	 */
	public CubicCurve setCurve(Point p1, Point ctrl1, Point ctrl2, Point p2) {
		setP1(p1);
		setCtrl1(ctrl1);
		setCtrl2(ctrl2);
		setP2(p2);
		return this;
	}

	@Override
	public CubicCurve[] split(double t) {
		BezierCurve[] split = super.split(t);
		return new CubicCurve[] { split[0].toCubic(), split[1].toCubic() };
	}

	@Override
	public Path toPath() {
		Path p = new Path();
		p.moveTo(getX1(), getY1());
		p.cubicTo(getCtrlX1(), getCtrlY1(), getCtrlX2(), getCtrlY2(), getX2(),
				getY2());
		return p;
	}

	@Override
	public String toString() {
		return "CubicCurve(x1 = " + getX1() + ", y1 = " + getY1()
				+ ", ctrl1X = " + getCtrlX1() + ", ctrl1Y = " + getCtrlY1()
				+ ", ctrl2X = " + getCtrlX2() + ", ctrl2Y = " + getCtrlY2()
				+ ", x2 = " + getX2() + ", y2 = " + getY2() + ")";
	}

}
