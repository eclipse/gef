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

import org.eclipse.gef4.geometry.Dimension;
import org.eclipse.gef4.geometry.Point;

/**
 * Abstract superclass of geometries that are defined by means of their upper
 * left coordinate (x,y) and a given width and height.
 * 
 * @author anyssen
 * 
 */
abstract class AbstractRectangleBasedGeometry extends AbstractGeometry {

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

	public final double getHeight() {
		return height;
	}

	/**
	 * Returns the location of this {@link AbstractRectangleBasedGeometry}, which is the
	 * location of its bounds.
	 * 
	 * @return a {@link Point} representing the location of this
	 *         {@link AbstractRectangleBasedGeometry} 's bounds
	 */
	public Point getLocation() {
		return new Point(x, y);
	}

	/**
	 * Returns the size of this {@link Rectangle}.
	 * 
	 * @return The current size
	 */
	public final Dimension getSize() {
		return new Dimension(width, height);
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
	 */
	public final void setBounds(double x, double y, double w, double h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	/**
	 * Sets the location and size of this to match those given.
	 * 
	 * @param loc
	 *            The new location
	 * @param size
	 *            The new size
	 */
	public final void setBounds(Point loc, Dimension size) {
		setBounds(loc.x, loc.y, size.width, size.height);
	}

	/**
	 * Sets the location and size of this {@link Rectangle} to match those of
	 * the given {@link Rectangle}.
	 * 
	 * @param r
	 *            The {@link Rectangle} whose location and size are to be
	 *            transferred.
	 */
	public final void setBounds(Rectangle r) {
		setBounds(r.x, r.y, r.width, r.height);
	}

	public final void setHeight(double height) {
		if (height < 0) {
			height = 0;
		}
		this.height = height;
	}

	/**
	 * Sets the location of this {@link Rectangle} to the coordinates given as
	 * input and returns this for convenience.
	 * 
	 * @param x
	 *            The new x-coordinate
	 * @param y
	 *            The new y-coordinate
	 */
	public final void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets the location of this {@link Rectangle} to the point given as input
	 * and returns this for convenience.
	 * 
	 * @param p
	 *            the new location of this Rectangle
	 */
	public final void setLocation(Point p) {
		setLocation(p.x, p.y);
	}

	/**
	 * Sets the width and height of this Rectangle to the width and height of
	 * the given Dimension and returns this for convenience.
	 * 
	 * @param d
	 *            the new size
	 */
	public final void setSize(Dimension d) {
		setSize(d.width, d.height);
	}

	/**
	 * Sets the width of this Rectangle to <i>w</i> and the height of this
	 * Rectangle to <i>h</i> and returns this for convenience.
	 * 
	 * @param w
	 *            The new width
	 * @param h
	 *            The new height
	 */
	public final void setSize(double w, double h) {
		if (w < 0) {
			w = 0;
		}
		if (h < 0) {
			h = 0;
		}
		width = w;
		height = h;
	}

	public final void setWidth(double width) {
		if (width < 0) {
			width = 0;
		}
		this.width = width;
	}

	public final void setX(double x) {
		this.x = x;
	}

	public final void setY(double y) {
		this.y = y;
	}

}