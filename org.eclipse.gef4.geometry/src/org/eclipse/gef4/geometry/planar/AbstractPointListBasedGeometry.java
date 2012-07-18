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

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.utils.PointListUtils;

abstract class AbstractPointListBasedGeometry<T extends AbstractPointListBasedGeometry<?>>
		extends AbstractGeometry implements ITranslatable<T>, IScalable<T>,
		IRotatable<T> {

	private static final long serialVersionUID = 1L;

	Point[] points;

	public AbstractPointListBasedGeometry(double... coordinates) {
		points = new Point[coordinates.length / 2];
		for (int i = 0; i < coordinates.length / 2; i++) {
			points[i] = new Point(coordinates[i * 2], coordinates[i * 2 + 1]);
		}
	}

	public AbstractPointListBasedGeometry(Point... points) {
		this.points = Point.getCopy(points);
	}

	public final Rectangle getBounds() {
		return Point.getBounds(points);
	}

	/**
	 * Computes the centroid of this {@link AbstractPointListBasedGeometry}. The
	 * centroid is the "center of gravity", i.e. assuming a {@link Polygon} is
	 * spanned by the {@link Point}s of this
	 * {@link AbstractPointListBasedGeometry} and it is made of a material of
	 * constant density, then it is in a balanced state, if you put it on a pin
	 * that is placed exactly on its centroid.
	 * 
	 * @return the center {@link Point} (or centroid) of this
	 *         {@link AbstractPointListBasedGeometry}
	 */
	public Point getCentroid() {
		return Point.getCentroid(points);
	}

	/**
	 * Returns a double array which represents the sequence of coordinates of
	 * the {@link Point}s that make up this
	 * {@link AbstractPointListBasedGeometry}.
	 * 
	 * @return an array that alternately contains the x and y coordinates of
	 *         this {@link AbstractPointListBasedGeometry}'s points
	 */
	public final double[] getCoordinates() {
		return PointListUtils.toCoordinatesArray(points);
	}

	/**
	 * Returns a copy of the {@link Point}s that make up this
	 * {@link AbstractPointListBasedGeometry}.
	 * 
	 * @return an array of {@link Point}s representing the {@link Point}s that
	 *         make up this {@link AbstractPointListBasedGeometry}
	 */
	public final Point[] getPoints() {
		return Point.getCopy(points);
	}

	public T getRotatedCCW(Angle alpha) {
		return getRotatedCCW(alpha, getCentroid());
	}

	public T getRotatedCCW(Angle angle, double cx, double cy) {
		return getRotatedCCW(angle, new Point(cx, cy));
	}

	@SuppressWarnings("unchecked")
	public T getRotatedCCW(Angle alpha, Point center) {
		return (T) ((T) getCopy()).rotateCCW(alpha, center);
	}

	public T getRotatedCW(Angle alpha) {
		return getRotatedCW(alpha, getCentroid());
	}

	public T getRotatedCW(Angle angle, double cx, double cy) {
		return getRotatedCW(angle, new Point(cx, cy));
	}

	@SuppressWarnings("unchecked")
	public T getRotatedCW(Angle alpha, Point center) {
		return (T) ((T) getCopy()).rotateCW(alpha, center);
	}

	@SuppressWarnings("unchecked")
	public T getScaled(double factor) {
		return (T) ((T) getCopy()).scale(factor);
	}

	@SuppressWarnings("unchecked")
	public T getScaled(double factorX, double factorY) {
		return (T) ((T) getCopy()).scale(factorX, factorY);
	}

	public T getScaled(double factor, double cx, double cy) {
		return getScaled(factor, factor, new Point(cx, cy));
	}

	public T getScaled(double fx, double fy, double cx, double cy) {
		return getScaled(fx, fy, new Point(cx, cy));
	}

	@SuppressWarnings("unchecked")
	public T getScaled(double factorX, double factorY, Point center) {
		return (T) ((T) getCopy()).scale(factorX, factorY, center);
	}

	@SuppressWarnings("unchecked")
	public T getScaled(double factor, Point center) {
		return (T) ((T) getCopy()).scale(factor, center);
	}

	@SuppressWarnings("unchecked")
	public T getTranslated(double dx, double dy) {
		return (T) ((T) getCopy()).translate(dx, dy);
	}

	@SuppressWarnings("unchecked")
	public T getTranslated(Point pt) {
		return (T) ((T) getCopy()).translate(pt);
	}

	/**
	 * Rotates this {@link AbstractPointListBasedGeometry} counter-clockwise
	 * (CCW) by the given {@link Angle} around its centroid (see
	 * {@link #getCentroid()}).
	 * 
	 * @param alpha
	 *            the rotation {@link Angle}
	 * @return <code>this</code> for convenience
	 * @see #rotateCCW(Angle, Point)
	 */
	public T rotateCCW(Angle alpha) {
		return rotateCCW(alpha, getCentroid());
	}

	/**
	 * Rotates this {@link AbstractPointListBasedGeometry} counter-clockwise
	 * (CCW) by the given {@link Angle} around the {@link Point} specified by
	 * the passed-in x and y coordinates.
	 * 
	 * @param alpha
	 *            the rotation {@link Angle}
	 * @param cx
	 *            the x coordinate of the {@link Point} to rotate around
	 * @param cy
	 *            the y coordinate of the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 * @see #rotateCCW(Angle, Point)
	 */
	public T rotateCCW(Angle alpha, double cx, double cy) {
		return rotateCCW(alpha, new Point(cx, cy));
	}

	/**
	 * Rotates this {@link AbstractPointListBasedGeometry} counter-clockwise
	 * (CCW) by the given {@link Angle} around the given {@link Point}.
	 * 
	 * The rotation is done by
	 * <ol>
	 * <li>translating this {@link AbstractPointListBasedGeometry} by the
	 * negated {@link Point} center</li>
	 * <li>rotating each {@link Point} of this
	 * {@link AbstractPointListBasedGeometry} counter-clockwise by the given
	 * {@link Angle}</li>
	 * <li>translating this {@link AbstractPointListBasedGeometry} back by the
	 * {@link Point} center</li>
	 * </ol>
	 * 
	 * @param alpha
	 *            the rotation {@link Angle}
	 * @param center
	 *            the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T rotateCCW(Angle alpha, Point center) {
		translate(center.getNegated());
		for (Point p : points) {
			Point np = new Vector(p).rotateCCW(alpha).toPoint();
			p.x = np.x;
			p.y = np.y;
		}
		translate(center);
		return (T) this;
	}

	/**
	 * Rotates this {@link AbstractPointListBasedGeometry} clockwise (CW) by the
	 * given {@link Angle} around its centroid (see {@link #getCentroid()}).
	 * 
	 * @param alpha
	 *            the rotation {@link Angle}
	 * @return <code>this</code> for convenience
	 * @see #rotateCW(Angle, Point)
	 */
	public T rotateCW(Angle alpha) {
		return rotateCW(alpha, getCentroid());
	}

	/**
	 * Rotates this {@link AbstractPointListBasedGeometry} clockwise (CW) by the
	 * given {@link Angle} around the {@link Point} specified by the passed-in x
	 * and y coordinates.
	 * 
	 * @param alpha
	 *            the rotation {@link Angle}
	 * @param cx
	 *            the x coordinate of the {@link Point} to rotate around
	 * @param cy
	 *            the y coordinate of the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 * @see #rotateCW(Angle, Point)
	 */
	public T rotateCW(Angle alpha, double cx, double cy) {
		return rotateCW(alpha, new Point(cx, cy));
	}

	/**
	 * Rotates this {@link AbstractPointListBasedGeometry} clockwise (CW) by the
	 * given {@link Angle} around the given {@link Point}.
	 * 
	 * The rotation is done by
	 * <ol>
	 * <li>translating this {@link AbstractPointListBasedGeometry} by the
	 * negated {@link Point} center</li>
	 * <li>rotating each {@link Point} of this
	 * {@link AbstractPointListBasedGeometry} clockwise by the given
	 * {@link Angle}</li>
	 * <li>translating this {@link AbstractPointListBasedGeometry} back by the
	 * {@link Point} center</li>
	 * </ol>
	 * 
	 * @param alpha
	 *            the rotation {@link Angle}
	 * @param center
	 *            the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T rotateCW(Angle alpha, Point center) {
		translate(center.getNegated());
		for (Point p : points) {
			Point np = new Vector(p).rotateCW(alpha).toPoint();
			p.x = np.x;
			p.y = np.y;
		}
		translate(center);
		return (T) this;
	}

	public T scale(double factor) {
		return scale(factor, factor);
	}

	public T scale(double fx, double fy) {
		return scale(fx, fy, getCentroid());
	}

	public T scale(double factor, double cx, double cy) {
		return scale(factor, factor, new Point(cx, cy));
	}

	public T scale(double fx, double fy, double cx, double cy) {
		return scale(fx, fy, new Point(cx, cy));
	}

	@SuppressWarnings("unchecked")
	public T scale(double fx, double fy, Point center) {
		for (Point p : points) {
			Point np = p.getScaled(fx, fy, center);
			p.x = np.x;
			p.y = np.y;
		}
		return (T) this;
	}

	public T scale(double factor, Point center) {
		return scale(factor, factor, center);
	}

	/**
	 * <p>
	 * Returns an integer array, which represents the sequence of coordinates of
	 * the {@link Point}s that make up this
	 * {@link AbstractPointListBasedGeometry}. The x and y coordinate values are
	 * transfered to integer values by either applying
	 * {@link Math#floor(double)} or {@link Math#ceil(double)} to them,
	 * dependent on their relative position to the centroid of this
	 * {@link AbstractPointListBasedGeometry} (see {@link #getCentroid()}).
	 * </p>
	 * <p>
	 * If the x coordinate of a {@link Point} is smaller than the x coordinate
	 * of the centroid, then the x coordinate of that {@link Point} is rounded
	 * down. Otherwise it is rounded up. Accordingly, if the y coordinate of a
	 * {@link Point} is smaller than the y coordinate of the centroid, it is
	 * rounded down. Otherwise, it is rounded up.
	 * </p>
	 * 
	 * @return an integer array of the x and y coordinates of this
	 *         {@link AbstractPointListBasedGeometry}
	 */
	public final int[] toSWTPointArray() {
		return PointListUtils.toIntegerArray(PointListUtils
				.toCoordinatesArray(points));
	}

	@SuppressWarnings("unchecked")
	public T translate(double dx, double dy) {
		Point.translate(points, dx, dy);
		return (T) this;
	}

	public T translate(Point p) {
		return translate(p.x, p.y);
	}

}
