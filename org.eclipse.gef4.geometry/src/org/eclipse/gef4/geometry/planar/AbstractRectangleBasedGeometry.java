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
 * @author anyssen
 */
abstract class AbstractRectangleBasedGeometry<T extends AbstractRectangleBasedGeometry<?, ?>, S extends IGeometry>
		extends AbstractGeometry implements ITranslatable<T>, IScalable<T>,
		IRotatable<S> {

	private static final long serialVersionUID = 1L;

	double x;
	double y;
	double width;
	double height;

	public AbstractRectangleBasedGeometry() {
		super();
	}

	public final Rectangle getBounds() {
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

	@SuppressWarnings("unchecked")
	public T getScaled(double factor) {
		return (T) ((T) getCopy()).scale(factor);
	}

	@SuppressWarnings("unchecked")
	public T getScaled(double factor, Point center) {
		return (T) ((T) getCopy()).scale(factor, center);
	}

	@SuppressWarnings("unchecked")
	public T getScaled(double factor, double centerX, double centerY) {
		return (T) ((T) getCopy()).scale(factor, centerX, centerY);
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
	public T getScaled(double factorX, double factorY, double centerX,
			double centerY) {
		return (T) ((T) getCopy()).scale(factorX, factorY, centerX, centerY);
	}

	/**
	 * Returns the size of this {@link Rectangle}.
	 * 
	 * @return The current size
	 */
	public final Dimension getSize() {
		return new Dimension(width, height);
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

	@SuppressWarnings("unchecked")
	public T scale(double fx, double fy, double cx, double cy) {
		x = (x - cx) * fx + cx;
		y = (y - cy) * fy + cy;
		width *= fx;
		height *= fy;
		return (T) this;
	}

	public T scale(double fx, double fy, Point center) {
		return scale(fx, fy, center.x, center.y);
	}

	public T scale(double fx, double fy) {
		return scale(fx, fy, getCenter());
	}

	public T scale(double factor) {
		return scale(factor, factor);
	}

	public T scale(double factor, Point center) {
		return scale(factor, center.x, center.y);
	}

	public T scale(double factor, double cx, double cy) {
		return scale(factor, factor, cx, cy);
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

	@SuppressWarnings("unchecked")
	public final T setX(double x) {
		this.x = x;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public final T setY(double y) {
		this.y = y;
		return (T) this;
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
		x += dx;
		y += dy;
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
