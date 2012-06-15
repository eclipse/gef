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
 * left coordinate (x,y) and a given width and height.
 * </p>
 * 
 * <p>
 * The type parameter <code>T</code> specifies the type of the inheriting class.
 * This is to be able to return the correct type, so that a type cast is
 * unnecessary.
 * </p>
 * 
 * <p>
 * The type parameter <code>S</code> specifies the result type of all rotation
 * short-cut methods. See {@link IRotatable} for more information.
 * </p>
 * 
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

	public Point getCentroid() {
		return new Point(x + width / 2, y + height / 2);
	}

	public final double getHeight() {
		return height;
	}

	/**
	 * Returns the location of this {@link AbstractRectangleBasedGeometry},
	 * which is the location of its bounds.
	 * 
	 * @return a {@link Point} representing the location of this
	 *         {@link AbstractRectangleBasedGeometry}'s bounds
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

	public final double getWidth() {
		return width;
	}

	public final double getX() {
		return x;
	}

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
		return scale(fx, fy, getCentroid());
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
	 * Sets the x, y, width and height values of this {@link Rectangle} to match
	 * those that are given.
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
	 * Sets the location and size of this to match those given.
	 * 
	 * @param loc
	 *            The new location
	 * @param size
	 *            The new size
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public final T setBounds(Point loc, Dimension size) {
		setBounds(loc.x, loc.y, size.width, size.height);
		return (T) this;
	}

	/**
	 * Sets the location and size of this {@link Rectangle} to match those of
	 * the given {@link Rectangle}.
	 * 
	 * @param r
	 *            The {@link Rectangle} whose location and size are to be
	 *            transferred.
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public final T setBounds(Rectangle r) {
		setBounds(r.x, r.y, r.width, r.height);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public final T setHeight(double height) {
		if (height < 0) {
			height = 0;
		}
		this.height = height;
		return (T) this;
	}

	/**
	 * Sets the location of this {@link Rectangle} to the coordinates given as
	 * input and returns this for convenience.
	 * 
	 * @param x
	 *            The new x-coordinate
	 * @param y
	 *            The new y-coordinate
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public final T setLocation(double x, double y) {
		this.x = x;
		this.y = y;
		return (T) this;
	}

	/**
	 * Sets the location of this {@link Rectangle} to the point given as input
	 * and returns this for convenience.
	 * 
	 * @param p
	 *            the new location of this Rectangle
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public final T setLocation(Point p) {
		setLocation(p.x, p.y);
		return (T) this;
	}

	/**
	 * Sets the width and height of this Rectangle to the width and height of
	 * the given Dimension and returns this for convenience.
	 * 
	 * @param d
	 *            the new size
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public final T setSize(Dimension d) {
		setSize(d.width, d.height);
		return (T) this;
	}

	/**
	 * Sets the width of this Rectangle to <i>w</i> and the height of this
	 * Rectangle to <i>h</i> and returns this for convenience.
	 * 
	 * @param w
	 *            The new width
	 * @param h
	 *            The new height
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
