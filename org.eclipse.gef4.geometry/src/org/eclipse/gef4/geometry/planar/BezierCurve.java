/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.utils.PointListUtils;

/**
 * Abstract base class of Bezier Curves.
 * 
 * TODO: make concrete -> leaf specializations in place but delegate
 * functionality to here.
 * 
 * @author anyssen
 * 
 */
public abstract class BezierCurve extends AbstractGeometry implements ICurve {

	double x1;
	double y1;
	double x2;
	double y2;

	// TODO: use point array instead
	double[] ctrlCoordinates = null;

	public BezierCurve(double... coordinates) {
		if (coordinates.length < 4) {
			throw new IllegalArgumentException(
					"A bezier curve needs at least a start and an end point");
		}
		this.x1 = coordinates[0];
		this.y1 = coordinates[1];
		this.x2 = coordinates[coordinates.length - 2];
		this.y2 = coordinates[coordinates.length - 1];
		if (coordinates.length > 4) {
			this.ctrlCoordinates = new double[coordinates.length - 4];
			System.arraycopy(coordinates, 2, ctrlCoordinates, 0,
					coordinates.length - 4);
		}
	}

	public BezierCurve(Point... points) {
		this(PointListUtils.toCoordinatesArray(points));
	}

	public final boolean contains(Rectangle r) {
		// TODO: may contain the rectangle only in case the rectangle is
		// degenerated...
		return false;
	}

	public Point getCtrl(int i) {
		return new Point(getCtrlX(i), getCtrlY(i));
	}

	/**
	 * Returns the point-wise coordinates (i.e. x1, y1, x2, y2, etc.) of the
	 * inner control points of this {@link BezierCurve}, i.e. exclusive of the
	 * start and end points.
	 * 
	 * @see BezierCurve#getCtrls()
	 * 
	 * @return an array containing the inner control points' coordinates
	 */
	public double[] getCtrlCoordinates() {
		return PointListUtils.getCopy(ctrlCoordinates);

	}

	/**
	 * Returns an array of points representing the inner control points of this
	 * curve, i.e. excluding the start and end points. In case of s linear
	 * curve, no control points will be returned, in case of a quadratic curve,
	 * one control point, and so on.
	 * 
	 * @return an array of points with the coordinates of the inner control
	 *         points of this {@link BezierCurve}, i.e. exclusive of the start
	 *         and end point. The number of control points will depend on the
	 *         degree ({@link #getDegree()}) of the curve, so in case of a line
	 *         (linear curve) the array will be empty, in case of a quadratic
	 *         curve, it will be of size <code>1</code>, in case of a cubic
	 *         curve of size <code>2</code>, etc..
	 */
	public Point[] getCtrls() {
		return PointListUtils.toPointsArray(ctrlCoordinates);
	}

	public double getCtrlX(int i) {
		return ctrlCoordinates[2 * i];
	}

	public double getCtrlY(int i) {
		return ctrlCoordinates[2 * i + 1];
	}

	/**
	 * Returns the degree of this curve which corresponds to the number of
	 * overall control points (including start and end point) used to define the
	 * curve. The degree is zero-based, so a line (linear curve) will have
	 * degree <code>1</code>, a quadratic curve will have degree <code>2</code>,
	 * and so on. <code>1</code> in case of a
	 * 
	 * @return The degree of this {@link ICurve}, which corresponds to the
	 *         zero-based overall number of control points (including start and
	 *         end point) used to define this {@link ICurve}.
	 */
	public int getDegree() {
		return getCtrls().length + 1;
	}

	/**
	 * Returns an array of points that represent this {@link BezierCurve}, i.e.
	 * the start point, the inner control points, and the end points.
	 * 
	 * @return an array of points representing the control points (including
	 *         start and end point) of this {@link BezierCurve}
	 */
	public Point[] getPoints() {
		Point[] points = new Point[ctrlCoordinates.length / 2 + 2];
		points[0] = new Point(x1, y1);
		points[points.length - 1] = new Point(x2, y2);
		for (int i = 1; i < points.length - 1; i++) {
			points[i] = new Point(ctrlCoordinates[2 * i - 2],
					ctrlCoordinates[2 * i - 1]);
		}
		return points;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef4.geometry.planar.ICurve#getP1()
	 */
	public Point getP1() {
		return new Point(x1, y1);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef4.geometry.planar.ICurve#getP2()
	 */
	public Point getP2() {
		return new Point(x2, y2);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef4.geometry.planar.ICurve#getX1()
	 */
	public double getX1() {
		return x1;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef4.geometry.planar.ICurve#getX2()
	 */
	public double getX2() {
		return x2;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef4.geometry.planar.ICurve#getY1()
	 */
	public double getY1() {
		return y1;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gef4.geometry.planar.ICurve#getY2()
	 */
	public double getY2() {
		return y2;
	}

	protected void setCtrl(int i, Point p) {
		setCtrlX(i, p.x);
		setCtrlY(i, p.y);
	}

	public void setCtrls(Point... ctrls) {
		ctrlCoordinates = PointListUtils.toCoordinatesArray(ctrls);
	}

	protected void setCtrlX(int i, double x) {
		// TODO: enlarge array if its too small
		ctrlCoordinates[2 * i] = x;
	}

	protected void setCtrlY(int i, double y) {
		// TODO: enlarge array if its too small
		ctrlCoordinates[2 * i + 1] = y;
	}

	/**
	 * Sets the start {@link Point} of this {@link BezierCurve} to the given
	 * {@link Point} p1.
	 * 
	 * @param p1
	 *            the new start {@link Point}
	 */
	public void setP1(Point p1) {
		this.x1 = p1.x;
		this.y1 = p1.y;
	}

	/**
	 * Sets the end {@link Point} of this {@link BezierCurve} to the given
	 * {@link Point} p2.
	 * 
	 * @param p2
	 *            the new end {@link Point}
	 */
	public void setP2(Point p2) {
		this.x2 = p2.x;
		this.y2 = p2.y;
	}

	/**
	 * Sets the x-coordinate of the start {@link Point} of this
	 * {@link BezierCurve} to x1.
	 * 
	 * @param x1
	 *            the new start {@link Point}'s x-coordinate
	 */
	public void setX1(double x1) {
		this.x1 = x1;
	}

	/**
	 * Sets the x-coordinate of the end {@link Point} of this
	 * {@link BezierCurve} to x2.
	 * 
	 * @param x2
	 *            the new end {@link Point}'s x-coordinate
	 */
	public void setX2(double x2) {
		this.x2 = x2;
	}

	/**
	 * Sets the y-coordinate of the start {@link Point} of this
	 * {@link BezierCurve} to y1.
	 * 
	 * @param y1
	 *            the new start {@link Point}'s y-coordinate
	 */
	public void setY1(double y1) {
		this.y1 = y1;
	}

	/**
	 * Sets the y-coordinate of the end {@link Point} of this
	 * {@link BezierCurve} to y2.
	 * 
	 * @param y2
	 *            the new end {@link Point}'s y-coordinate
	 */
	public void setY2(double y2) {
		this.y2 = y2;
	}

}
