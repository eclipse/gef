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

import org.eclipse.gef4.geometry.Angle;
import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.utils.PointListUtils;

abstract class AbstractPointListBasedGeometry<T extends AbstractPointListBasedGeometry<?>>
		extends AbstractGeometry {

	private static final long serialVersionUID = 1L;

	Point[] points;

	public AbstractPointListBasedGeometry(double... coordinates) {
		points = new Point[coordinates.length / 2];
		for (int i = 0; i < coordinates.length / 2; i++) {
			points[i] = new Point(coordinates[i * 2], coordinates[i * 2 + 1]);
		}
	}

	public AbstractPointListBasedGeometry(Point... points) {
		this.points = PointListUtils.copy(points);
	}

	/**
	 * @see IGeometry#getBounds()
	 */
	public final Rectangle getBounds() {
		return PointListUtils.getBounds(points);
	}

	/**
	 * Computes the centroid of this {@link AbstractPointListBasedGeometry}. The
	 * centroid is the "center of gravity", i.e. assuming the {@link Polygon}
	 * which is spanned by the {@link Point}s of this
	 * {@link AbstractPointListBasedGeometry} is made of a material of constant
	 * density, it is in a balanced state, if you put it on a pin that is placed
	 * exactly on its centroid.
	 * 
	 * @return the center {@link Point} (or centroid) of this
	 *         {@link AbstractPointListBasedGeometry}
	 */
	public Point getCentroid() {
		if (points.length == 0) {
			return null;
		} else if (points.length == 1) {
			return points[0].getCopy();
		}

		double cx = 0, cy = 0, a, sa = 0;
		for (int i = 0; i < points.length - 1; i++) {
			a = points[i].x * points[i + 1].y - points[i].y * points[i + 1].x;
			sa += a;
			cx += (points[i].x + points[i + 1].x) * a;
			cy += (points[i].y + points[i + 1].y) * a;
		}

		// closing segment
		a = points[points.length - 2].x * points[points.length - 1].y
				- points[points.length - 2].y * points[points.length - 1].x;
		sa += a;
		cx += (points[points.length - 2].x + points[points.length - 1].x) * a;
		cy += (points[points.length - 2].x + points[points.length - 1].x) * a;

		return new Point(cx / (3 * sa), cy / (3 * sa));
	}

	/**
	 * Returns a double array, which represents the sequence of coordinates of
	 * the {@link Point}s that make up this {@link Polygon}.
	 * 
	 * @return an array that alternately contains the x- and y-coordinates of
	 *         this {@link Polygon}'s points.
	 */
	public final double[] getCoordinates() {
		return PointListUtils.toCoordinatesArray(points);
	}

	/**
	 * Returns a copy of the points that make up this {@link Polygon}, where a
	 * segment of the {@link Polygon} is represented between each two succeeding
	 * {@link Point}s in the sequence, and from the last back to the first.
	 * 
	 * @return an array of {@link Point}s representing the points that make up
	 *         this {@link Polygon}
	 */
	public final Point[] getPoints() {
		return PointListUtils.copy(points);
	}

	/**
	 * Returns a new {@link AbstractPointListBasedGeometry} which is rotated
	 * counter-clock-wise by the given {@link Angle} around its centroid (see
	 * {@link #getCentroid()}).
	 * 
	 * @see #getCopy()
	 * @see #getRotatedCCW(Angle, Point)
	 * @param alpha
	 *            The rotation {@link Angle}
	 * @return The new rotated {@link AbstractPointListBasedGeometry}
	 */
	public T getRotatedCCW(Angle alpha) {
		return getRotatedCCW(alpha, getCentroid());
	}

	/**
	 * Returns a new {@link AbstractPointListBasedGeometry} which is rotated
	 * counter-clock-wise by the given {@link Angle} around the given
	 * {@link Point}.
	 * 
	 * @see #getCopy()
	 * @see #rotateCW(Angle, Point)
	 * @param alpha
	 *            The rotation {@link Angle}
	 * @param center
	 *            The {@link Point} to rotate around
	 * @return The new rotated {@link AbstractPointListBasedGeometry}
	 */
	@SuppressWarnings("unchecked")
	public T getRotatedCCW(Angle alpha, Point center) {
		return (T) ((T) getCopy()).rotateCCW(alpha, center);
	}

	/**
	 * Returns a new {@link AbstractPointListBasedGeometry} which is rotated
	 * clock-wise by the given {@link Angle} around its centroid (see
	 * {@link #getCentroid()}).
	 * 
	 * @see #getCopy()
	 * @see #getRotatedCW(Angle, Point)
	 * @param alpha
	 *            The rotation {@link Angle}
	 * @return The new rotated {@link AbstractPointListBasedGeometry}
	 */
	public T getRotatedCW(Angle alpha) {
		return getRotatedCW(alpha, getCentroid());
	}

	/**
	 * Returns a new {@link AbstractPointListBasedGeometry} which is rotated
	 * clock-wise by the given {@link Angle} around the given {@link Point}.
	 * 
	 * @see #getCopy()
	 * @see #rotateCW(Angle, Point)
	 * @param alpha
	 *            The rotation {@link Angle}
	 * @param center
	 *            The {@link Point} to rotate around
	 * @return The new rotated {@link AbstractPointListBasedGeometry}
	 */
	@SuppressWarnings("unchecked")
	public T getRotatedCW(Angle alpha, Point center) {
		return (T) ((T) getCopy()).rotateCW(alpha, center);
	}

	/**
	 * Returns a new {@link AbstractPointListBasedGeometry} which is scaled by
	 * the given factor. The {@link AbstractPointListBasedGeometry} is
	 * translated by the negated centroid (see {@link #getCentroid()}) first.
	 * The translation is reversed afterwards.
	 * 
	 * @param factor
	 *            The scale-factor
	 * @return The new scaled {@link AbstractPointListBasedGeometry}
	 * @see #getScaled(double, Point)
	 */
	@SuppressWarnings("unchecked")
	public T getScaled(double factor) {
		return (T) ((T) getCopy()).scale(factor);
	}

	@SuppressWarnings("unchecked")
	public T getScaled(double factorX, double factorY) {
		return (T) ((T) getCopy()).scale(factorX, factorY);
	}

	@SuppressWarnings("unchecked")
	public T getScaled(double factorX, double factorY, Point center) {
		return (T) ((T) getCopy()).scale(factorX, factorY, center);
	}

	@SuppressWarnings("unchecked")
	public T getScaled(double factor, Point center) {
		return (T) ((T) getCopy()).scale(factor, center);
	}

	/**
	 * Returns a new {@link AbstractPointListBasedGeometry} which is shifted
	 * along each axis by the passed values.
	 * 
	 * @param dx
	 *            Displacement along X axis
	 * @param dy
	 *            Displacement along Y axis
	 * @return The new translated {@link AbstractPointListBasedGeometry}
	 */
	@SuppressWarnings("unchecked")
	public T getTranslated(double dx, double dy) {
		return (T) ((T) getCopy()).translate(dx, dy);
	}

	/**
	 * Returns a new {@link AbstractPointListBasedGeometry} which is shifted by
	 * the position of the given {@link Point}.
	 * 
	 * @param pt
	 *            {@link Point} providing the amount of shift along each axis
	 * @return The new translated {@link AbstractPointListBasedGeometry}
	 */
	@SuppressWarnings("unchecked")
	public T getTranslated(Point pt) {
		return (T) ((T) getCopy()).translate(pt);
	}

	/**
	 * Rotates this {@link AbstractPointListBasedGeometry} counter-clock-wise by
	 * the given {@link Angle} around its centroid (see {@link #getCentroid()}).
	 * 
	 * @param alpha
	 *            The rotation {@link Angle}
	 * @return <code>this</code> for convenience
	 * @see #rotateCCW(Angle, Point)
	 */
	public T rotateCCW(Angle alpha) {
		return rotateCCW(alpha, getCentroid());
	}

	/**
	 * Rotates this {@link AbstractPointListBasedGeometry} counter-clock-wise by
	 * the given {@link Angle} around the given {@link Point}.
	 * 
	 * The rotation is done by
	 * <ol>
	 * <li>translating this {@link AbstractPointListBasedGeometry} by the
	 * negated {@link Point} center</li>
	 * <li>rotating each {@link Point} of this
	 * {@link AbstractPointListBasedGeometry} counter-clock-wise by the given
	 * {@link Angle}</li>
	 * <li>translating this {@link AbstractPointListBasedGeometry} back by the
	 * {@link Point} center</li>
	 * </ol>
	 * 
	 * @param alpha
	 *            The rotation {@link Angle}.
	 * @param center
	 *            The {@link Point} to rotate around.
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
	 * Rotates this {@link AbstractPointListBasedGeometry} clock-wise by the
	 * given {@link Angle} around its centroid (see {@link #getCentroid()}).
	 * 
	 * @param alpha
	 *            The rotation {@link Angle}
	 * @return <code>this</code> for convenience
	 * @see #rotateCW(Angle, Point)
	 */
	public T rotateCW(Angle alpha) {
		return rotateCW(alpha, getCentroid());
	}

	/**
	 * Rotates this {@link AbstractPointListBasedGeometry} clock-wise by the
	 * given {@link Angle} around the given {@link Point}.
	 * 
	 * The rotation is done by
	 * <ol>
	 * <li>translating this {@link AbstractPointListBasedGeometry} by the
	 * negated {@link Point} center</li>
	 * <li>rotating each {@link Point} of this
	 * {@link AbstractPointListBasedGeometry} clock-wise by the given
	 * {@link Angle}</li>
	 * <li>translating this {@link AbstractPointListBasedGeometry} back by the
	 * {@link Point} center</li>
	 * </ol>
	 * 
	 * @param alpha
	 *            The rotation {@link Angle}
	 * @param center
	 *            The {@link Point} to rotate around
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

	/**
	 * Scales this {@link AbstractPointListBasedGeometry} by the given factor.
	 * The {@link AbstractPointListBasedGeometry} is translated by its negated
	 * centroid (see {@link #getCentroid()}) first. The translation is reversed
	 * afterwards.
	 * 
	 * @see #scale(double, Point)
	 * @param factor
	 * @return <code>this</code> for convenience
	 */
	public T scale(double factor) {
		return scale(factor, factor);
	}

	public T scale(double factorX, double factorY) {
		return scale(factorX, factorY, getCentroid());
	}

	@SuppressWarnings("unchecked")
	public T scale(double factorX, double factorY, Point center) {
		for (Point p : points) {
			Point np = p.getScaled(factorX, factorY, center);
			p.x = np.x;
			p.y = np.y;
		}
		return (T) this;
	}

	public T scale(double factor, Point center) {
		return scale(factor, factor, center);
	}

	/**
	 * Returns an integer array, which represents the sequence of coordinates of
	 * the {@link Point}s that make up this {@link Polygon}.
	 * 
	 * @return an array containing integer values, which are obtained by casting
	 *         the x- and y-coordinates of this {@link Polygon}.
	 */
	public final int[] toSWTPointArray() {
		return PointListUtils.toIntegerArray(PointListUtils
				.toCoordinatesArray(points));
	}

	/**
	 * Moves this {@link AbstractPointListBasedGeometry} horizontally by dx and
	 * vertically by dy, then returns this
	 * {@link AbstractPointListBasedGeometry} for convenience.
	 * 
	 * @param dx
	 *            Shift along X axis
	 * @param dy
	 *            Shift along Y axis
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T translate(double dx, double dy) {
		PointListUtils.translate(points, dx, dy);
		return (T) this;
	}

	/**
	 * Moves this {@link AbstractPointListBasedGeometry} horizontally by the x
	 * value of the given {@link Point} and vertically by the y value of the
	 * given {@link Point}, then returns this
	 * {@link AbstractPointListBasedGeometry} for convenience.
	 * 
	 * @param p
	 *            {@link Point} which provides translation information
	 * @return <code>this</code> for convenience
	 */
	public T translate(Point p) {
		return translate(p.x, p.y);
	}

}