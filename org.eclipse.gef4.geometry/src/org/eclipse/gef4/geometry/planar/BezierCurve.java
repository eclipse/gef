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

	private double x1, y1, x2, y2;
	private double[] ctrlCoordinates = new double[] {};

	public BezierCurve(Point... points) {
		this(PointListUtils.toCoordinatesArray(points));
	}

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

	public Point[] getCtrls() {
		return PointListUtils.toPointsArray(ctrlCoordinates);
	}

	public void setCtrls(Point... ctrls) {
		ctrlCoordinates = PointListUtils.toCoordinatesArray(ctrls);
	}

	public Point getCtrl(int i) {
		return new Point(getCtrlX(i), getCtrlY(i));
	}

	public double getCtrlX(int i) {
		return ctrlCoordinates[2 * i];
	}

	public double getCtrlY(int i) {
		return ctrlCoordinates[2 * i + 1];
	}

	protected void setCtrl(int i, Point p) {
		setCtrlX(i, p.x);
		setCtrlY(i, p.y);
	}

	protected void setCtrlX(int i, double x) {
		// TODO: enlarge array if its too small
		ctrlCoordinates[2 * i] = x;
	}

	protected void setCtrlY(int i, double y) {
		// TODO: enlarge array if its too small
		ctrlCoordinates[2 * i + 1] = y;
	}

	public final boolean contains(Rectangle r) {
		// TODO: may contain the rectangle only in case the rectangle is
		// degenerated...
		return false;
	}

	public int getDegree() {
		return getCtrls().length;
	}

}
