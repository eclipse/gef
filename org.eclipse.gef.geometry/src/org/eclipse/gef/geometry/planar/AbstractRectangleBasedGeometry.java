/*******************************************************************************
 * Copyright (c) 2011, 2017 itemis AG and others.
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
 * <p>
 * Abstract superclass of geometries that are defined by means of their upper
 * left coordinate (x, y) and a given width and height.
 * </p>
 * <p>
 * The type parameter <code>T</code> specifies the type of the inheriting class.
 * This is to be able to return the correct type, so that a type cast is
 * unnecessary.
 * </p>
 * <p>
 * The type parameter <code>S</code> specifies the result type of all rotation
 * short-cut methods. See {@link IRotatable} for more information.
 * </p>
 *
 * @param <T>
 *            specifies the type of the inheriting class in order to avoid
 *            otherwise necessary type casts
 * @param <S>
 *            specifies the result type of all rotation short-cut methods (see
 *            {@link IRotatable})
 *
 * @author anyssen
 * @author mwienand
 *
 */
abstract class AbstractRectangleBasedGeometry<T extends AbstractRectangleBasedGeometry<?, ?>, S extends IGeometry>
		extends AbstractGeometry
		implements ITranslatable<T>, IScalable<T>, IRotatable<S> {

	private static final long serialVersionUID = 1L;

	/**
	 * The x-coordinate of this {@link AbstractRectangleBasedGeometry}.
	 */
	double x;
	/**
	 * The y-coordinate of this {@link AbstractRectangleBasedGeometry}.
	 */
	double y;
	/**
	 * The width of this {@link AbstractRectangleBasedGeometry}.
	 */
	double width;
	/**
	 * The height of this {@link AbstractRectangleBasedGeometry}.
	 */
	double height;

	/**
	 * Constructs a new {@link AbstractRectangleBasedGeometry} with the given
	 * position and size. If the width or height is negative, will use
	 * <code>0</code> instead.
	 *
	 * @param x
	 *            The x-coordinate of this
	 *            {@link AbstractRectangleBasedGeometry}
	 * @param y
	 *            The y-coordinate of this
	 *            {@link AbstractRectangleBasedGeometry}
	 * @param width
	 *            the width of this {@link AbstractRectangleBasedGeometry}
	 * @param height
	 *            the height of this {@link AbstractRectangleBasedGeometry}
	 *
	 * @see #setX(double)
	 * @see #setY(double)
	 * @see #setWidth(double)
	 * @see #setHeight(double)
	 */
	public AbstractRectangleBasedGeometry(double x, double y, double width,
			double height) {
		setX(x);
		setY(y);
		setWidth(width);
		setHeight(height);
	}

	/**
	 * Expands the horizontal and vertical sides of this
	 * {@link AbstractRectangleBasedGeometry} with the values provided as input,
	 * and returns <code>this</code> for convenience. The location of its center
	 * is kept constant.
	 *
	 * @param h
	 *            the horizontal increment
	 * @param v
	 *            the vertical increment
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T expand(double h, double v) {
		x -= h;
		width += h + h;
		y -= v;
		height += v + v;
		return (T) this;
	}

	/**
	 * Expands this {@link AbstractRectangleBasedGeometry} by the given amounts,
	 * and returns this for convenience.
	 *
	 * @param left
	 *            the amount to expand the left side
	 * @param top
	 *            the amount to expand the top side
	 * @param right
	 *            the amount to expand the right side
	 * @param bottom
	 *            the amount to expand the bottom side
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T expand(double left, double top, double right, double bottom) {
		x -= left;
		y -= top;
		width += left + right;
		height += top + bottom;
		return (T) this;
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}

	/**
	 * Returns the center {@link Point} of this
	 * {@link AbstractRectangleBasedGeometry}.
	 *
	 * @return the center {@link Point} of this
	 *         {@link AbstractRectangleBasedGeometry}
	 */
	public Point getCenter() {
		return new Point(x + width / 2, y + height / 2);
	}

	/**
	 * Returns a new expanded {@link AbstractRectangleBasedGeometry}, where the
	 * sides are incremented by the horizontal and vertical values provided. The
	 * center of the {@link AbstractRectangleBasedGeometry} is maintained
	 * constant.
	 *
	 * @param h
	 *            The horizontal increment
	 * @param v
	 *            The vertical increment
	 * @return a new expanded {@link AbstractRectangleBasedGeometry}
	 */
	@SuppressWarnings("unchecked")
	public T getExpanded(double h, double v) {
		return (T) ((T) getCopy()).expand(h, v);
	}

	/**
	 * Creates and returns a new {@link AbstractRectangleBasedGeometry} with the
	 * bounds of this {@link AbstractRectangleBasedGeometry} expanded by the
	 * given insets.
	 *
	 * @param left
	 *            the amount to expand the left side
	 * @param top
	 *            the amount to expand the top side
	 * @param right
	 *            the amount to expand the right side
	 * @param bottom
	 *            the amount to expand the bottom side
	 *
	 * @return a new expanded {@link AbstractRectangleBasedGeometry}
	 */
	@SuppressWarnings("unchecked")
	public T getExpanded(double left, double top, double right, double bottom) {
		return (T) ((T) getCopy()).expand(left, top, right, bottom);
	}

	/**
	 * Returns the height of this {@link AbstractRectangleBasedGeometry}.
	 *
	 * @return the height of this {@link AbstractRectangleBasedGeometry}
	 */
	public final double getHeight() {
		return height;
	}

	/**
	 * Returns a {@link Point} specifying the x and y coordinates of this
	 * {@link AbstractRectangleBasedGeometry}.
	 *
	 * @return a {@link Point} representing the x and y coordinates of this
	 *         {@link AbstractRectangleBasedGeometry}
	 */
	public Point getLocation() {
		return new Point(x, y);
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
	@SuppressWarnings("unchecked")
	public T getScaled(double factor, double centerX, double centerY) {
		return (T) ((T) getCopy()).scale(factor, centerX, centerY);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getScaled(double factorX, double factorY, double centerX,
			double centerY) {
		return (T) ((T) getCopy()).scale(factorX, factorY, centerX, centerY);
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

	/**
	 * Returns a new {@link AbstractRectangleBasedGeometry}, where the sides are
	 * shrinked by the horizontal and vertical values supplied. The center of
	 * this {@link AbstractRectangleBasedGeometry} is kept constant.
	 *
	 * @param h
	 *            horizontal reduction amount
	 * @param v
	 *            vertical reduction amount
	 * @return a new, shrinked {@link AbstractRectangleBasedGeometry}
	 */
	@SuppressWarnings("unchecked")
	public T getShrinked(double h, double v) {
		return (T) ((T) getCopy()).shrink(h, v);
	}

	/**
	 * Returns a new {@link AbstractRectangleBasedGeometry} shrinked by the
	 * specified insets.
	 *
	 * @param left
	 *            the amount to shrink the left side
	 * @param top
	 *            the amount to shrink the top side
	 * @param right
	 *            the amount to shrink the right side
	 * @param bottom
	 *            the amount to shrink the bottom side
	 *
	 * @return a new, shrinked {@link AbstractRectangleBasedGeometry}
	 */
	@SuppressWarnings("unchecked")
	public T getShrinked(double left, double top, double right, double bottom) {
		return (T) ((T) getCopy()).shrink(left, top, right, bottom);
	}

	/**
	 * Returns a {@link Dimension} that records the width and height of this
	 * {@link AbstractRectangleBasedGeometry}.
	 *
	 * @return a {@link Dimension} that records the width and height of this
	 *         {@link AbstractRectangleBasedGeometry}
	 */
	public final Dimension getSize() {
		return new Dimension(width, height);
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
	 * Returns the width of this {@link AbstractRectangleBasedGeometry}.
	 *
	 * @return the width of this {@link AbstractRectangleBasedGeometry}
	 */
	public final double getWidth() {
		return width;
	}

	/**
	 * Returns the x coordinate this {@link AbstractRectangleBasedGeometry}.
	 *
	 * @return the x coordinate this {@link AbstractRectangleBasedGeometry}
	 */
	public final double getX() {
		return x;
	}

	/**
	 * Returns the y coordinate of this {@link AbstractRectangleBasedGeometry}.
	 *
	 * @return the y coordinate of this {@link AbstractRectangleBasedGeometry}
	 */
	public final double getY() {
		return y;
	}

	@Override
	public T scale(double factor) {
		return scale(factor, factor);
	}

	@Override
	public T scale(double fx, double fy) {
		return scale(fx, fy, getCenter());
	}

	@Override
	public T scale(double factor, double cx, double cy) {
		return scale(factor, factor, cx, cy);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T scale(double fx, double fy, double cx, double cy) {
		x = (x - cx) * fx + cx;
		y = (y - cy) * fy + cy;
		width *= fx;
		height *= fy;
		return (T) this;
	}

	@Override
	public T scale(double fx, double fy, Point center) {
		return scale(fx, fy, center.x, center.y);
	}

	@Override
	public T scale(double factor, Point center) {
		return scale(factor, center.x, center.y);
	}

	/**
	 * Sets the x, y, width, and height values of this
	 * {@link AbstractRectangleBasedGeometry} to the given values.
	 *
	 * @param x
	 *            the new x-coordinate
	 * @param y
	 *            the new y-coordinate
	 * @param w
	 *            the new width
	 * @param h
	 *            the new height
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public final T setBounds(double x, double y, double w, double h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		return (T) this;
	}

	/**
	 * Sets the x, y, width, and height values of this
	 * {@link AbstractRectangleBasedGeometry} to the respective values specified
	 * by the passed-in {@link Point} and the passed-in {@link Dimension}.
	 *
	 * @param loc
	 *            the {@link Point} specifying the new x and y coordinates of
	 *            this {@link AbstractRectangleBasedGeometry}
	 * @param size
	 *            the {@link Dimension} specifying the new width and height of
	 *            this {@link AbstractRectangleBasedGeometry}
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public final T setBounds(Point loc, Dimension size) {
		setBounds(loc.x, loc.y, size.width, size.height);
		return (T) this;
	}

	/**
	 * Sets the x and y coordinates and the width and height of this
	 * {@link AbstractRectangleBasedGeometry} to the respective values of the
	 * given {@link Rectangle}.
	 *
	 * @param r
	 *            the {@link Rectangle} specifying the new x, y, width, and
	 *            height values of this {@link AbstractRectangleBasedGeometry}
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public final T setBounds(Rectangle r) {
		setBounds(r.x, r.y, r.width, r.height);
		return (T) this;
	}

	/**
	 * Sets the height of this {@link AbstractRectangleBasedGeometry} to the
	 * given value.
	 *
	 * @param height
	 *            the new height
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public final T setHeight(double height) {
		if (height < 0) {
			height = 0;
		}
		this.height = height;
		return (T) this;
	}

	/**
	 * Sets the x and y coordinates of this
	 * {@link AbstractRectangleBasedGeometry} to the specified values.
	 *
	 * @param x
	 *            the new x coordinate of this
	 *            {@link AbstractRectangleBasedGeometry}
	 * @param y
	 *            the new y coordinate of this
	 *            {@link AbstractRectangleBasedGeometry}
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public final T setLocation(double x, double y) {
		this.x = x;
		this.y = y;
		return (T) this;
	}

	/**
	 * Sets the x and y coordinates of this
	 * {@link AbstractRectangleBasedGeometry} to the respective values of the
	 * given {@link Point}.
	 *
	 * @param p
	 *            the {@link Point} specifying the new x and y coordinates of
	 *            this {@link AbstractRectangleBasedGeometry}
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public final T setLocation(Point p) {
		setLocation(p.x, p.y);
		return (T) this;
	}

	/**
	 * Sets the width and height of this {@link AbstractRectangleBasedGeometry}
	 * to the width and height of the given {@link Dimension}.
	 *
	 * @param d
	 *            the {@link Dimension} specifying the new width and height of
	 *            this {@link AbstractRectangleBasedGeometry}
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public final T setSize(Dimension d) {
		setSize(d.width, d.height);
		return (T) this;
	}

	/**
	 * Sets the width and height of this {@link AbstractRectangleBasedGeometry}
	 * to the given values.
	 *
	 * @param w
	 *            the new width of this {@link AbstractRectangleBasedGeometry}
	 * @param h
	 *            the new height of this {@link AbstractRectangleBasedGeometry}
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public final T setSize(double w, double h) {
		if (w < 0) {
			w = 0;
		}
		if (h < 0) {
			h = 0;
		}
		width = w;
		height = h;
		return (T) this;
	}

	/**
	 * Sets the width of this {@link AbstractRectangleBasedGeometry} to the
	 * passed-in value.
	 *
	 * @param width
	 *            the new width of this {@link AbstractRectangleBasedGeometry}
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public final T setWidth(double width) {
		if (width < 0) {
			width = 0;
		}
		this.width = width;
		return (T) this;
	}

	/**
	 * Sets the x-coordinate of this {@link AbstractRectangleBasedGeometry} to
	 * the given value.
	 *
	 * @param x
	 *            The new x-coordinate.
	 * @return <code>this</code> for convenience.
	 */
	@SuppressWarnings("unchecked")
	public final T setX(double x) {
		this.x = x;
		return (T) this;
	}

	/**
	 * Sets the y-coordinate of this {@link AbstractRectangleBasedGeometry} to
	 * the given value.
	 *
	 * @param y
	 *            The new y-coordinate.
	 * @return <code>this</code> for convenience.
	 */
	@SuppressWarnings("unchecked")
	public final T setY(double y) {
		this.y = y;
		return (T) this;
	}

	/**
	 * Shrinks the sides of this {@link AbstractRectangleBasedGeometry} by the
	 * horizontal and vertical values provided as input, and returns this
	 * {@link AbstractRectangleBasedGeometry} for convenience. The center of
	 * this {@link AbstractRectangleBasedGeometry} is kept constant.
	 *
	 * @param h
	 *            horizontal reduction amount
	 * @param v
	 *            vertical reduction amount
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T shrink(double h, double v) {
		x += h;
		width -= h + h;
		y += v;
		height -= v + v;
		return (T) this;
	}

	/**
	 * Shrinks this {@link AbstractRectangleBasedGeometry} by the specified
	 * amounts.
	 *
	 * @param left
	 *            the amount to shrink the left side
	 * @param top
	 *            the amount to shrink the top side
	 * @param right
	 *            the amount to shrink the right side
	 * @param bottom
	 *            the amount to shrink the bottom side
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T shrink(double left, double top, double right, double bottom) {
		x += left;
		y += top;
		width -= (left + right);
		height -= (top + bottom);
		return (T) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T translate(double dx, double dy) {
		x += dx;
		y += dy;
		return (T) this;
	}

	@Override
	public T translate(Point p) {
		return translate(p.x, p.y);
	}

}
