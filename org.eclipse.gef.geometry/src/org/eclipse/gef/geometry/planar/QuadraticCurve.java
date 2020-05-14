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
 * Represents the geometric shape of a quadratic Bézier curve.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class QuadraticCurve extends BezierCurve {

	private static final long serialVersionUID = 1L;

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
		if (coordinates.length != 6) {
			throw new IllegalArgumentException(
					"A QuadraticCurve may only be defined by 6 coordinates (3 points), while "
							+ coordinates.length + " were passed in.");
		}
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
		if (points.length != 3) {
			throw new IllegalArgumentException(
					"A QuadraticCurve may only be defined by three points, while "
							+ points.length + " were passed in.");
		}
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
	 * Erroneous getBounds() implementation... use the generic one instead.
	 *
	 * TODO: find out why the mathematical solution is erroneous in some cases.
	 *
	 * Returns the bounds of this QuadraticCurve. The bounds are calculated by
	 * examining the extreme points of the x(t) and y(t) function
	 * representations of this QuadraticCurve.
	 *
	 * @return the bounds {@link Rectangle}
	 */

	/**
	 * Returns a new {@link QuadraticCurve}, which has the same start, end, and
	 * control point coordinates as this one.
	 *
	 * @return a new {@link QuadraticCurve} with the same start, end, and
	 *         control point coordinates
	 */
	@Override
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
		return getPoint(1).x;
	}

	/**
	 * Get the control point's y-coordinate.
	 *
	 * @return the control point's y-coordinate
	 */
	public double getCtrlY() {
		return getPoint(1).y;
	}

	/**
	 * Degree elevation: Returns a {@link CubicCurve} representation of this
	 * {@link QuadraticCurve}.
	 *
	 * @return A {@link CubicCurve} that represents this {@link QuadraticCurve}.
	 */
	@Override
	public CubicCurve getElevated() {
		Point[] controlPoints = new Point[4];

		// "Curves and Surfaces for Computer Aided Geometric Design" by Farin,
		// Gerald E., Academic Press 1988
		controlPoints[0] = getP1();
		controlPoints[1] = getP1().getScaled(1d / 3d)
				.getTranslated(getCtrl().getScaled(2d / 3d));
		controlPoints[2] = getCtrl().getScaled(2d / 3d)
				.getTranslated(getP2().getScaled(1d / 3d));
		controlPoints[3] = getP2();

		return new CubicCurve(controlPoints);
	}

	@Override
	public QuadraticCurve getTransformed(AffineTransform t) {
		return new QuadraticCurve(t.getTransformed(getPoints()));
	}

	/**
	 * Sets the curve's control point.
	 *
	 * @param ctrl
	 *            The new curve's control point.
	 * @return <code>this</code> for convenience
	 */
	public QuadraticCurve setCtrl(Point ctrl) {
		setCtrlX(ctrl.x);
		setCtrlY(ctrl.y);
		return this;
	}

	/**
	 * Sets the x-coordinate of the curve's control point.
	 *
	 * @param ctrlX
	 *            The new x-coordinate of the curve's control point.
	 * @return <code>this</code> for convenience
	 */
	public QuadraticCurve setCtrlX(double ctrlX) {
		setPoint(1, new Point(ctrlX, getCtrlY()));
		return this;
	}

	/**
	 * Sets the y-coordinate of the curve's control point.
	 *
	 * @param ctrlY
	 *            The y-coordinate of the curve's control point.
	 * @return <code>this</code> for convenience
	 */
	public QuadraticCurve setCtrlY(double ctrlY) {
		setPoint(1, new Point(getCtrlX(), ctrlY));
		return this;
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
	@Override
	public QuadraticCurve[] split(double t) {
		BezierCurve[] split = super.split(t);
		return new QuadraticCurve[] { split[0].toQuadratic(),
				split[1].toQuadratic() };
	}

	/**
	 * Transform the QuadraticCurve object to a {@link Path} object with the
	 * same shape.
	 *
	 * @return a {@link Path} object representing the curve
	 */
	@Override
	public Path toPath() {
		Path p = new Path();
		p.moveTo(getX1(), getY1());
		p.quadTo(getCtrlX(), getCtrlY(), getX2(), getY2());
		return p;
	}

	@Override
	public String toString() {
		return "QuadraticCurve(x1 = " + getX1() + ", y1 = " + getY1()
				+ ", ctrlX = " + getCtrlX() + ", ctrlY = " + getCtrlY()
				+ ", x2 = " + getX2() + ", y2 = " + getY2() + ")";
	}

}
