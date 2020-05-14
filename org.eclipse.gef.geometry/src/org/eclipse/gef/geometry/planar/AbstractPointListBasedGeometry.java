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

import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.internal.utils.PointListUtils;

/**
 * Abstract superclass of geometries that are defined by means of a point list.
 * <p>
 * The type parameter <code>T</code> specifies the type of the inheriting class.
 * This is to be able to return the correct type, so that a type cast is
 * unnecessary.
 * </p>
 *
 * @param <T>
 *            specifies the type of the inheriting class in order to avoid
 *            otherwise necessary type casts
 *
 * @author anyssen
 * @author mwienand
 *
 */
abstract class AbstractPointListBasedGeometry<T extends AbstractPointListBasedGeometry<?>>
		extends AbstractGeometry
		implements ITranslatable<T>, IScalable<T>, IRotatable<T> {

	private static final long serialVersionUID = 1L;

	/**
	 * The points constituting this {@link AbstractPointListBasedGeometry}.
	 */
	Point[] points;

	/**
	 * Constructs a new {@link AbstractPointListBasedGeometry} from a
	 * even-numbered sequence of coordinates.
	 *
	 * @param coordinates
	 *            an alternating, even-numbered sequence of x and y coordinates,
	 *            representing the {@link Point}s from which the
	 *            {@link AbstractPointListBasedGeometry} is to be created
	 * @see #AbstractPointListBasedGeometry(Point...)
	 */
	public AbstractPointListBasedGeometry(double... coordinates) {
		points = new Point[coordinates.length / 2];
		for (int i = 0; i < coordinates.length / 2; i++) {
			points[i] = new Point(coordinates[i * 2], coordinates[i * 2 + 1]);
		}
	}

	/**
	 * Constructs a new {@link AbstractPointListBasedGeometry} from the given
	 * sequence of {@link Point} s.
	 *
	 * @param points
	 *            a sequence of points, from which the
	 *            {@link AbstractPointListBasedGeometry} is to be created.
	 */
	public AbstractPointListBasedGeometry(Point... points) {
		this.points = Point.getCopy(points);
	}

	@Override
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

	@Override
	public T getRotatedCCW(Angle alpha) {
		return getRotatedCCW(alpha, getCentroid());
	}

	@Override
	public T getRotatedCCW(Angle angle, double cx, double cy) {
		return getRotatedCCW(angle, new Point(cx, cy));
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getRotatedCCW(Angle alpha, Point center) {
		return (T) ((T) getCopy()).rotateCCW(alpha, center);
	}

	@Override
	public T getRotatedCW(Angle alpha) {
		return getRotatedCW(alpha, getCentroid());
	}

	@Override
	public T getRotatedCW(Angle angle, double cx, double cy) {
		return getRotatedCW(angle, new Point(cx, cy));
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getRotatedCW(Angle alpha, Point center) {
		return (T) ((T) getCopy()).rotateCW(alpha, center);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getScaled(double factor) {
		return (T) ((T) getCopy()).scale(factor);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getScaled(double factorX, double factorY) {
		return (T) ((T) getCopy()).scale(factorX, factorY);
	}

	@Override
	public T getScaled(double factor, double cx, double cy) {
		return getScaled(factor, factor, new Point(cx, cy));
	}

	@Override
	public T getScaled(double fx, double fy, double cx, double cy) {
		return getScaled(fx, fy, new Point(cx, cy));
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getScaled(double factorX, double factorY, Point center) {
		return (T) ((T) getCopy()).scale(factorX, factorY, center);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getScaled(double factor, Point center) {
		return (T) ((T) getCopy()).scale(factor, center);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getTranslated(double dx, double dy) {
		return (T) ((T) getCopy()).translate(dx, dy);
	}

	@Override
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

	@Override
	public T scale(double factor) {
		return scale(factor, factor);
	}

	@Override
	public T scale(double fx, double fy) {
		return scale(fx, fy, getCentroid());
	}

	@Override
	public T scale(double factor, double cx, double cy) {
		return scale(factor, factor, new Point(cx, cy));
	}

	@Override
	public T scale(double fx, double fy, double cx, double cy) {
		return scale(fx, fy, new Point(cx, cy));
	}

	@Override
	@SuppressWarnings("unchecked")
	public T scale(double fx, double fy, Point center) {
		for (Point p : points) {
			Point np = p.getScaled(fx, fy, center);
			p.x = np.x;
			p.y = np.y;
		}
		return (T) this;
	}

	@Override
	public T scale(double factor, Point center) {
		return scale(factor, factor, center);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T translate(double dx, double dy) {
		Point.translate(points, dx, dy);
		return (T) this;
	}

	@Override
	public T translate(Point p) {
		return translate(p.x, p.y);
	}

}
