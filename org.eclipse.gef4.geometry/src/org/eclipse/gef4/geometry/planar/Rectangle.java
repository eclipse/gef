/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation 
 *     Alexander Nyßen (itemis AG) - migration to double precision
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *    
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

import org.eclipse.gef4.geometry.Angle;
import org.eclipse.gef4.geometry.Dimension;
import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.transform.AffineTransform;
import org.eclipse.gef4.geometry.utils.PointListUtils;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * Represents the geometric shape of a rectangle, where a rectangle is
 * characterized by means of its upper left corner (x,y) and its size (width,
 * height).
 * 
 * Note that while all manipulations (e.g. within shrink, expand) within this
 * class are based on double precision, all comparisons (e.g. within contains,
 * intersects, equals, etc.) are based on a limited precision (with an accuracy
 * defined within {@link PrecisionUtils}) to compensate for rounding effects.
 * 
 * @author ebordeau
 * @author rhudson
 * @author msorens
 * @author pshah
 * @author sshaw
 * @author ahunter
 * @author anyssen
 */
public class Rectangle extends AbstractGeometry implements IShape {

	private static final long serialVersionUID = 1L;

	private double x;
	private double y;
	private double width;
	private double height;

	/**
	 * Constructs a {@link Rectangle} with location (0,0) and a size of (0,0).
	 */
	public Rectangle() {
	}

	/**
	 * Constructs a Rectangle from the given values for its location (upper-left
	 * corner point) and its size.
	 * 
	 * @param x
	 *            the x location of the new {@link Rectangle}
	 * @param y
	 *            the y location of the new {@link Rectangle}
	 * @param width
	 *            the width of the new {@link Rectangle}
	 * @param height
	 *            the height of the new {@link Rectangle}
	 */
	public Rectangle(double x, double y, double width, double height) {
		if (width < 0) {
			width = 0;
		}
		if (height < 0) {
			height = 0;
		}
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * Constructs a new {@link Rectangle}, whose x, y, width, and height values
	 * are initialized with those of the given
	 * {@link org.eclipse.swt.graphics.Rectangle}.
	 * 
	 * @param r
	 *            The {@link org.eclipse.swt.graphics.Rectangle}, whose values
	 *            will be used to initialize the new {@link Rectangle}
	 */
	public Rectangle(org.eclipse.swt.graphics.Rectangle r) {
		this(r.x, r.y, r.width, r.height);
	}

	/**
	 * Constructs a new {@link Rectangle} with the given location and size.
	 * 
	 * @param location
	 *            the location of the new {@link Rectangle}
	 * @param size
	 *            the size of the new {@link Rectangle}
	 * 
	 */
	public Rectangle(Point location, Dimension size) {
		this(location.x, location.y, size.width, size.height);
	}

	/**
	 * Constructs a new {@link Rectangle}, which is the smallest one containing
	 * both given {@link Point}s.
	 * 
	 * @param p1
	 *            the first point used to construct the new {@link Rectangle}
	 * @param p2
	 *            the second point used to construct the new {@link Rectangle}
	 * 
	 */
	public Rectangle(Point p1, Point p2) {
		this(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p2.x - p1.x),
				Math.abs(p2.y - p1.y));
	}

	/**
	 * Constructs a new {@link Rectangle} with x, y, width, and height values of
	 * the given {@link Rectangle}.
	 * 
	 * @param r
	 *            the {@link Rectangle}, whose x, y, width, and height values
	 *            should be used to initialize the new {@link Rectangle}
	 * 
	 */
	public Rectangle(Rectangle r) {
		this(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * Returns whether the point given by x and y is within the boundaries of
	 * this Rectangle.
	 * 
	 * @param x
	 *            the x-coordinate of the point to test
	 * @param y
	 *            the y-coordinate of the point to test
	 * @return true if the Point is (imprecisely) contained within this
	 *         Rectangle
	 */
	public boolean contains(double x, double y) {
		return PrecisionUtils.greaterEqual(y, this.y)
				&& PrecisionUtils.smallerEqual(y, this.y + this.height)
				&& PrecisionUtils.greaterEqual(x, this.x)
				&& PrecisionUtils.smallerEqual(x, this.x + this.width);
	}

	/**
	 * Returns true in case the rectangle specified by (x, y, width, height) is
	 * contained within this {@link Rectangle}.
	 * 
	 * @param x
	 *            The x coordinate of the rectangle to be tested for containment
	 * @param y
	 *            The y coordinate of the rectangle to be tested for containment
	 * @param width
	 *            The width of the rectangle to be tested for containment
	 * @param height
	 *            The height of the rectangle to be tested for containment
	 * @return <code>true</code> if the rectangle characterized by (x,y, width,
	 *         height) is (imprecisely) fully contained within this
	 *         {@link Rectangle}, <code>false</code> otherwise
	 */
	public boolean contains(double x, double y, double width, double height) {
		return PrecisionUtils.smallerEqual(this.x, x)
				&& PrecisionUtils.smallerEqual(this.y, y)
				&& PrecisionUtils.greaterEqual(this.width, width)
				&& PrecisionUtils.greaterEqual(this.height, height);
	}

	/**
	 * Returns whether the given point is within the boundaries of this
	 * Rectangle. The boundaries are inclusive of the top and left edges, but
	 * exclusive of the bottom and right edges.
	 * 
	 * @param p
	 *            Point being tested for containment
	 * @return true if the Point is within this Rectangle
	 * 
	 */
	public boolean contains(Point p) {
		return contains(p.x(), p.y());
	}

	/**
	 * @see IGeometry#contains(Rectangle)
	 */
	public boolean contains(Rectangle r) {
		return contains(r.x, r.y, r.width, r.height);
	}

	/**
	 * Returns <code>true</code> if this Rectangle's x, y, width, and height
	 * values are identical to the provided ones.
	 * 
	 * @param x
	 *            The x value to test
	 * @param y
	 *            The y value to test
	 * @param width
	 *            The width value to test
	 * @param height
	 *            The height value to test
	 * @return <code>true</code> if this Rectangle's x, y, width, and height
	 *         values are (imprecisely) equal to the provided ones,
	 *         <code>false</code> otherwise
	 */
	public boolean equals(double x, double y, double width, double height) {
		return PrecisionUtils.equal(this.x, x)
				&& PrecisionUtils.equal(this.y, y)
				&& PrecisionUtils.equal(this.width, width)
				&& PrecisionUtils.equal(this.height, height);
	}

	/**
	 * Returns whether the input object is equal to this Rectangle or not.
	 * Rectangles are equivalent if their x, y, height, and width values are the
	 * same.
	 * 
	 * @param o
	 *            Object being tested for equality
	 * @return Returns the result of the equality test
	 * 
	 */
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof Rectangle) {
			Rectangle r = (Rectangle) o;
			return equals(r.x, r.y, r.width, r.height);
		}
		return false;
	}

	/**
	 * Expands the horizontal and vertical sides of this Rectangle with the
	 * values provided as input, and returns this for convenience. The location
	 * of its center is kept constant.
	 * 
	 * @param h
	 *            Horizontal increment
	 * @param v
	 *            Vertical increment
	 * @return <code>this</code> Rectangle for convenience
	 */
	public Rectangle expand(double h, double v) {
		x -= h;
		width += h + h;
		y -= v;
		height += v + v;
		return this;
	}

	/**
	 * Expands the horizontal and vertical sides of this Rectangle by the width
	 * and height of the given Insets, and returns this for convenience.
	 * 
	 * @param left
	 *            - the amount to expand the left side
	 * @param top
	 *            - the amount to expand the top side
	 * @param right
	 *            - the amount to expand the right side
	 * @param bottom
	 *            - the amount to expand the bottom side
	 * @return <code>this</code> Rectangle for convenience
	 * 
	 */
	public Rectangle expand(double left, double top, double right, double bottom) {
		x -= left;
		y -= top;
		width += left + right;
		height += top + bottom;
		return this;
	}

	/**
	 * Returns the area of this {@link Rectangle}, i.e. the product of its width
	 * and height.
	 * 
	 * @return the area of this {@link Rectangle}
	 */
	public double getArea() {
		return width * height;
	}

	/**
	 * Returns an array of {@link Point}s representing the top-left, top-right,
	 * bottom-right, and bottom-left border points of this {@link Rectangle}.
	 * 
	 * @return An array containing the border points of this {@link Rectangle}
	 */
	public Point[] getPoints() {
		return new Point[] { getTopLeft(), getTopRight(), getBottomRight(),
				getBottomLeft() };
	}

	/**
	 * Returns a new {@link Rectangle}, whose location is identical to the
	 * location of this {@link Rectangle}, but whose size is scaled by the given
	 * factor.
	 * 
	 * @param scale
	 *            the scale factor, with which to multiply the height and width
	 *            of this {@link Rectangle} when computing the size of the new
	 *            {@link Rectangle} to be returned
	 * @return a new {@link Rectangle} with a location identical to this one's
	 *         and a size that is computed by multiplying width and height of
	 *         this {@link Rectangle} by the given factor.
	 */
	public Rectangle getScaled(double scale) {
		return getScaled(scale, scale);
	}

	/**
	 * Returns a new {@link Rectangle}, whose location is identical to the
	 * location of this {@link Rectangle}, but whose size is scaled by the given
	 * factors.
	 * 
	 * @param scaleX
	 *            the factor, with which to multiply the width of this
	 *            {@link Rectangle}, when computing the size of the new
	 *            {@link Rectangle} to be returned
	 * @param scaleY
	 *            the factor, with which to multiply the height of this
	 *            {@link Rectangle} when computing the size of the new
	 *            {@link Rectangle} to be returned
	 * @return a new {@link Rectangle} with a location identical to this one's
	 *         and a size that is computed by multiplying width and height of
	 *         this {@link Rectangle} by the given factor.
	 */
	public Rectangle getScaled(double scaleX, double scaleY) {
		return getCopy().scale(scaleX, scaleY);
	}

	/**
	 * Returns an array of {@link Line}s representing the top, right, bottom,
	 * and left borders of this {@link Rectangle}.
	 * 
	 * @return An array containing {@link Line} representations of this
	 *         {@link Rectangle}'s borders.
	 */
	public Line[] getOutlineSegments() {
		Line[] segments = new Line[4];
		segments[0] = new Line(x, y, x + width, y);
		segments[1] = new Line(x + width, y, x + width, y + height);
		segments[2] = new Line(x + width, y + height, x, y + height);
		segments[3] = new Line(x, y + height, x, y);
		return segments;
	}

	/**
	 * Returns a new Point representing the middle point of the bottom side of
	 * this Rectangle.
	 * 
	 * @return Point at the bottom of the Rectangle
	 */
	public Point getBottom() {
		return new Point(x + width / 2, y + height);
	}

	/**
	 * Returns a new Point representing the bottom left point of this Rectangle.
	 * 
	 * @return Point at the bottom left of the rectangle
	 */
	public Point getBottomLeft() {
		return new Point(x, y + height);
	}

	/**
	 * Returns a new Point representing the bottom right point of this
	 * Rectangle.
	 * 
	 * @return Point at the bottom right of the rectangle
	 */
	public Point getBottomRight() {
		return new Point(x + width, y + height);
	}

	/**
	 * Returns a copy of this {@link Rectangle}.
	 * 
	 * @see IGeometry#getBounds()
	 */
	public Rectangle getBounds() {
		return getCopy();
	}

	/**
	 * Returns a new point representing the center of this Rectangle.
	 * 
	 * @return Point at the center of the rectangle
	 */
	public Point getCenter() {
		return new Point(x + width / 2, y + height / 2);
	}

	/**
	 * Returns a new Rectangle which has the exact same parameters as this
	 * Rectangle.
	 * 
	 * @return Copy of this Rectangle
	 */
	public Rectangle getCopy() {
		return new Rectangle(x, y, width, height);
	}

	/**
	 * Returns a new incremented Rectangle, where the sides are expanded by the
	 * horizontal and vertical values provided. The center of the Rectangle is
	 * maintained constant.
	 * 
	 * @param h
	 *            Horizontal increment
	 * @param v
	 *            Vertical increment
	 * @return A new expanded Rectangle
	 */
	public Rectangle getExpanded(double h, double v) {
		return getCopy().expand(h, v);
	}

	/**
	 * Creates and returns a new Rectangle with the bounds of <code>this</code>
	 * Rectangle, expanded by the given insets.
	 * 
	 * @param left
	 *            - the amount to expand the left side
	 * @param top
	 *            - the amount to expand the top side
	 * @param right
	 *            - the amount to expand the right side
	 * @param bottom
	 *            - the amount to expand the bottom side
	 * 
	 * @return A new expanded Rectangle
	 * 
	 */
	public Rectangle getExpanded(double left, double top, double right,
			double bottom) {
		return getCopy().expand(left, top, right, bottom);
	}

	/**
	 * Returns the height of this {@link Rectangle}.
	 * 
	 * @return the height of this {@link Rectangle}
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Returns a new Rectangle which has the intersection of this Rectangle and
	 * the rectangle provided as input. Returns an empty Rectangle if there is
	 * no intersection.
	 * 
	 * @param rect
	 *            Rectangle provided to test for intersection
	 * @return A new Rectangle representing the intersection
	 */
	public Rectangle getIntersected(Rectangle rect) {
		return getCopy().intersect(rect);
	}

	/**
	 * Returns a new Point representing the middle point of the left hand side
	 * of this Rectangle.
	 * 
	 * @return Point at the left of the Rectangle
	 */
	public Point getLeft() {
		return new Point(x, y + height / 2);
	}

	/**
	 * Returns the location of this {@link Rectangle}.
	 * 
	 * @return The current location
	 */
	public Point getLocation() {
		return new Point(x, y);
	}

	/**
	 * Returns a new Point which represents the middle point of the right hand
	 * side of this Rectangle.
	 * 
	 * @return Point at the right of the Rectangle
	 */
	public Point getRight() {
		return new Point(x + width, y + height / 2);
	}

	/**
	 * Rotates this {@link Rectangle} clock-wise by the given {@link Angle}
	 * alpha around the {@link Point} center.
	 * 
	 * If the rotation {@link Angle} is not an integer multiple 90\u00b0, the
	 * resulting figure cannot be expressed as a {@link Rectangle} object.
	 * That's why this method returns a {@link Polygon} instead.
	 * 
	 * @param alpha
	 *            the rotation angle
	 * @param center
	 *            the center point of the rotation
	 * @return the rotated rectangle ({@link Polygon})
	 */
	public Polygon getRotatedCW(Angle alpha, Point center) {
		return toPolygon().rotateCW(alpha, center);
	}

	/**
	 * Rotates this {@link Rectangle} counter-clock-wise by the given
	 * {@link Angle} alpha around the {@link Point} center.
	 * 
	 * If the rotation {@link Angle} is not an integer multiple 90\u00b0, the
	 * resulting figure cannot be expressed as a {@link Rectangle} object.
	 * That's why this method returns a {@link Polygon} instead.
	 * 
	 * @param alpha
	 *            the rotation angle
	 * @param center
	 *            the center point of the rotation
	 * @return the rotated rectangle ({@link Polygon})
	 */
	public Polygon getRotatedCCW(Angle alpha, Point center) {
		return toPolygon().rotateCCW(alpha, center);
	}

	/**
	 * Returns a new Rectangle, where the sides are shrinked by the horizontal
	 * and vertical values supplied. The center of this Rectangle is kept
	 * constant.
	 * 
	 * @param h
	 *            Horizontal reduction amount
	 * @param v
	 *            Vertical reduction amount
	 * @return the new, shrinked {@link Rectangle}
	 */
	public Rectangle getShrinked(double h, double v) {
		return getCopy().shrink(h, v);
	}

	/**
	 * Returns a new Rectangle shrinked by the specified insets.
	 * 
	 * @param left
	 *            - the amount to shrink the left side
	 * @param top
	 *            - the amount to shrink the top side
	 * @param right
	 *            - the amount to shrink the right side
	 * @param bottom
	 *            - the amount to shrink the bottom side
	 * 
	 * @return shrinked new Rectangle
	 */
	public Rectangle getShrinked(double left, double top, double right,
			double bottom) {
		return getCopy().shrink(left, top, right, bottom);
	}

	/**
	 * Returns the size of this {@link Rectangle}.
	 * 
	 * @return The current size
	 */
	public Dimension getSize() {
		return new Dimension(width, height);
	}

	/**
	 * Returns a new Point which represents the middle point of the top side of
	 * this Rectangle.
	 * 
	 * @return Point at the top of the Rectangle
	 */
	public Point getTop() {
		return new Point(x + width / 2, y);
	}

	/**
	 * Returns a new Point which represents the top left hand corner of this
	 * Rectangle.
	 * 
	 * @return Point at the top left of the rectangle
	 */
	public Point getTopLeft() {
		return new Point(x, y);
	}

	/**
	 * Returns a new Point which represents the top right hand corner of this
	 * Rectangle.
	 * 
	 * @return Point at the top right of the rectangle
	 */
	public Point getTopRight() {
		return new Point(x + width, y);
	}

	/**
	 * Returns a {@link Polygon}, which represents the transformed
	 * {@link Rectangle}.
	 * 
	 * @see IGeometry#getTransformed(AffineTransform)
	 */
	public IGeometry getTransformed(AffineTransform t) {
		// may not be type-intrinsically transformed, so use a polygon
		// representation
		return new Polygon(t.getTransformed(getPoints()));
	}

	/**
	 * Returns a new Rectangle which is shifted along each axis by the passed
	 * values.
	 * 
	 * @param dx
	 *            Displacement along X axis
	 * @param dy
	 *            Displacement along Y axis
	 * @return The new translated rectangle
	 * 
	 */
	public Rectangle getTranslated(double dx, double dy) {
		return getCopy().translate(dx, dy);
	}

	/**
	 * Returns a new Rectangle which is shifted by the position of the given
	 * Point.
	 * 
	 * @param pt
	 *            Point providing the amount of shift along each axis
	 * @return The new translated Rectangle
	 * 
	 */
	public Rectangle getTranslated(Point pt) {
		return getCopy().translate(pt);
	}

	/**
	 * Returns a new rectangle whose width and height have been interchanged, as
	 * well as its x and y values. This can be useful in orientation changes.
	 * 
	 * @return The transposed rectangle
	 * 
	 */
	public Rectangle getTransposed() {
		return getCopy().transpose();
	}

	/**
	 * Returns a new Rectangle which contains both this Rectangle and the Point
	 * supplied as input.
	 * 
	 * @param p
	 *            Point for calculating union
	 * @return A new unioned Rectangle
	 */
	public Rectangle getUnioned(Point p) {
		return getCopy().union(p);
	}

	/**
	 * Returns a new Rectangle which contains both this Rectangle and the
	 * Rectangle supplied as input.
	 * 
	 * @param rect
	 *            Rectangle for calculating union
	 * @return A new unioned Rectangle
	 */
	public Rectangle getUnioned(Rectangle rect) {
		return getCopy().union(rect);
	}

	/**
	 * Returns the width of this {@link Rectangle}.
	 * 
	 * @return the width of this {@link Rectangle}
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Returns the x-coordinate of this {@link Rectangle}.
	 * 
	 * @return The x coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y-coordinate of this {@link Rectangle}.
	 * 
	 * @return The y coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the bounds of this {@link Rectangle} to the intersection of this
	 * {@link Rectangle} with the given one.
	 * 
	 * @param r
	 *            The {@link Rectangle} to intersect this {@link Rectangle}
	 *            with.
	 * @return <code>this</code> for convenience.
	 */
	public Rectangle intersect(Rectangle r) {
		double x1 = Math.max(x, r.x);
		double x2 = Math.min(x + width, r.x + r.width);
		double y1 = Math.max(y, r.y);
		double y2 = Math.min(y + height, r.y + r.height);
		if (PrecisionUtils.greaterEqual(x2 - x1, 0)
				&& PrecisionUtils.greaterEqual(y2 - y1, 0)) {
			return setBounds(x1, y1, x2 - x1 < 0 ? 0 : x2 - x1, y2 - y1 < 0 ? 0
					: y2 - y1);
		}
		return setBounds(0, 0, 0, 0); // no intersection
	}

	/**
	 * Tests whether this {@link Rectangle} and the given {@link Line}
	 * intersect, i.e. whether they have at least one point in common.
	 * 
	 * @param l
	 *            The {@link Line} to test.
	 * @return <code>true</code> if this {@link Rectangle} and the given
	 *         {@link Line} share at least one common point, <code>false</code>
	 *         otherwise.
	 */
	public boolean intersects(Line l) {
		if (contains(l.getP1()) || contains(l.getP2())) {
			return true;
		}

		for (Line segment : getOutlineSegments()) {
			if (segment.intersects(l)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see IGeometry#intersects(Rectangle)
	 */
	public boolean intersects(Rectangle r) {
		return !getIntersected(r).isEmpty();
	}

	/**
	 * Returns <code>true</code> if this Rectangle's width or height is less
	 * than or equal to 0.
	 * 
	 * @return <code>true</code> if this Rectangle is (imprecisely) considered
	 *         to be empty
	 * 
	 */
	public boolean isEmpty() {
		return PrecisionUtils.smallerEqual(width, 0)
				|| PrecisionUtils.smallerEqual(height, 0);
	}

	/**
	 * Scales the size of this Rectangle by the given scale and returns this for
	 * convenience.
	 * 
	 * @param scaleFactor
	 *            The factor by which this size will be scaled
	 * @return <code>this</code> Rectangle for convenience
	 */
	public Rectangle scale(double scaleFactor) {
		return scale(scaleFactor, scaleFactor);
	}

	/**
	 * Scales the size of this Rectangle by the given scales and returns this
	 * for convenience.
	 * 
	 * @param scaleX
	 *            the factor by which the width has to be scaled
	 * @param scaleY
	 *            the factor by which the height has to be scaled
	 * @return <code>this</code> Rectangle for convenience
	 */
	public Rectangle scale(double scaleX, double scaleY) {
		width *= scaleX;
		height *= scaleY;
		return this;
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
	public Rectangle setBounds(double x, double y, double w, double h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		return this;
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
	public Rectangle setBounds(Point loc, Dimension size) {
		setBounds(loc.x, loc.y, size.width, size.height);
		return this;
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
	public Rectangle setBounds(Rectangle r) {
		return setBounds(r.x, r.y, r.width, r.height);
	}

	/**
	 * Sets the height of this {@link Rectangle}
	 * 
	 * @param height
	 *            The new height
	 * @return <code>this</code> for convenience
	 */
	public Rectangle setHeight(double height) {
		if (height < 0) {
			height = 0;
		}
		this.height = height;
		return this;
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
	public Rectangle setLocation(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	/**
	 * Sets the location of this {@link Rectangle} to the point given as input
	 * and returns this for convenience.
	 * 
	 * @param p
	 *            the new location of this Rectangle
	 * @return <code>this</code> Rectangle for convenience
	 */
	public Rectangle setLocation(Point p) {
		return setLocation(p.x, p.y);
	}

	/**
	 * Sets the width and height of this Rectangle to the width and height of
	 * the given Dimension and returns this for convenience.
	 * 
	 * @param d
	 *            The new Dimension
	 * @return <code>this</code> for convenience
	 */
	public Rectangle setSize(Dimension d) {
		return setSize(d.width, d.height);
	}

	/**
	 * Sets the width of this Rectangle to <i>w</i> and the height of this
	 * Rectangle to <i>h</i> and returns this for convenience.
	 * 
	 * @param w
	 *            The new width
	 * @param h
	 *            The new height
	 * @return <code>this</code> Rectangle for convenience
	 */
	public Rectangle setSize(double w, double h) {
		if (w < 0) {
			w = 0;
		}
		if (h < 0) {
			h = 0;
		}
		width = w;
		height = h;
		return this;
	}

	/**
	 * Sets the width of this {@link Rectangle}
	 * 
	 * @param width
	 *            The new width
	 * @return <code>this</code> for convenience
	 */
	public Rectangle setWidth(double width) {
		if (width < 0) {
			width = 0;
		}
		this.width = width;
		return this;
	}

	/**
	 * Sets the x-coordinate of this {@link Rectangle}
	 * 
	 * @param x
	 *            The new x-coordinate
	 * @return <code>this</code> for convenience
	 */
	public Rectangle setX(double x) {
		this.x = x;
		return this;
	}

	/**
	 * Sets the y-coordinate of this {@link Rectangle}
	 * 
	 * @param y
	 *            The new y-coordinate
	 * @return <code>this</code> for convenience
	 */
	public Rectangle setY(double y) {
		this.y = y;
		return this;
	}

	/**
	 * Shrinks the sides of this Rectangle by the horizontal and vertical values
	 * provided as input, and returns this Rectangle for convenience. The center
	 * of this Rectangle is kept constant.
	 * 
	 * @param h
	 *            Horizontal reduction amount
	 * @param v
	 *            Vertical reduction amount
	 * @return <code>this</code> Rectangle for convenience
	 */
	public Rectangle shrink(double h, double v) {
		x += h;
		width -= h + h;
		y += v;
		height -= v + v;
		return this;
	}

	/**
	 * Shrinks this rectangle by the amount specified in <code>insets</code>.
	 * 
	 * @param left
	 *            - the amount to shrink the left side
	 * @param top
	 *            - the amount to shrink the top side
	 * @param right
	 *            - the amount to shrink the right side
	 * @param bottom
	 *            - the amount to shrink the bottom side
	 * @return <code>this</code> Rectangle for convenience
	 */
	public Rectangle shrink(double top, double left, double bottom, double right) {
		x += left;
		y += top;
		width -= (left + right);
		height -= (top + bottom);
		return this;
	}

	/**
	 * @see IGeometry#toPath()
	 */
	public Path toPath() {
		Path path = new Path();
		path.moveTo(x, y);
		path.lineTo(x + width, y);
		path.lineTo(x + width, y + height);
		path.lineTo(x, y + height);
		path.close();
		return path;
	}

	/**
	 * Converts this {@link Rectangle} into a {@link Polygon} representation.
	 * The control points used to construct the polygon are the border points
	 * returned by {@link #getPoints()}.
	 * 
	 * @return A {@link Polygon} representation for this {@link Rectangle}
	 */
	public Polygon toPolygon() {
		return new Polygon(PointListUtils.getCopy(getPoints()));
	}

	@Override
	public String toString() {
		return "Rectangle: (" + x + ", " + y + ", " + //$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
				width + ", " + height + ")";//$NON-NLS-2$//$NON-NLS-1$
	}

	/**
	 * Converts this {@link Rectangle} into an
	 * {@link org.eclipse.swt.graphics.Rectangle}. Note that as
	 * {@link org.eclipse.swt.graphics.Rectangle} is integer-based, this implies
	 * a loss of precision. The returned rectangle is the smallest
	 * integer-precision representation that fully contains this
	 * {@link Rectangle}.
	 * 
	 * @return An {@link org.eclipse.swt.graphics.Rectangle} representation of
	 *         this {@link Rectangle}.
	 */
	public org.eclipse.swt.graphics.Rectangle toSWTRectangle() {
		return new org.eclipse.swt.graphics.Rectangle((int) Math.floor(x),
				(int) Math.floor(y),
				(int) Math.ceil(width + x - Math.floor(x)),
				(int) Math.ceil(height + y - Math.floor(y)));
	}

	/**
	 * Moves this {@link Rectangle} horizontally by dx and vertically by dy.
	 * 
	 * @param dx
	 *            Shift along X axis
	 * @param dy
	 *            Shift along Y axis
	 * @return <code>this</code> for convenience
	 */
	public Rectangle translate(double dx, double dy) {
		x += dx;
		y += dy;
		return this;
	}

	/**
	 * Moves this {@link Rectangle} horizontally by the x value of the given
	 * {@link Point} and vertically by the y value of the given {@link Point}.
	 * 
	 * @param p
	 *            The {@link Point} which provides the translation information
	 * @return <code>this</code> for convenience
	 */
	public Rectangle translate(Point p) {
		return translate(p.x, p.y);
	}

	/**
	 * Switches the x and y values, as well as the width and height of this
	 * Rectangle. Useful for orientation changes.
	 * 
	 * @return <code>this</code> for convenience
	 */
	public Rectangle transpose() {
		double temp = x;
		x = y;
		y = temp;
		temp = width;
		width = height;
		height = temp;
		return this;
	}

	/**
	 * Updates this {@link Rectangle}'s bounds so that the {@link Point} given
	 * by (x,y) is contained.
	 * 
	 * @param x
	 *            The x-coordinate of the {@link Point} to union with
	 * @param y
	 *            The y-coordinate of the {@link Point} to union with
	 * @return <code>this</code> for convenience
	 * 
	 */
	public Rectangle union(double x, double y) {
		if (x < this.x) {
			this.width += this.x - x;
			this.x = x;
		} else if (x > this.x + this.width) {
			this.width += x - this.x - this.width;
		}
		if (y < this.y) {
			this.height += this.y - y;
			this.y = y;
		} else if (y > this.y + this.height) {
			this.height += y - this.y - this.height;
		}
		return this;
	}

	/**
	 * Updates this Rectangle's bounds to the union of this {@link Rectangle}
	 * and the {@link Rectangle} with location (x, y) and size(w, h).
	 * 
	 * @param x
	 *            The x-coordinate of the {@link Rectangle} to union with.
	 * @param y
	 *            The y-coordinate of the {@link Rectangle} to union with
	 * @param w
	 *            The width of the {@link Rectangle} to union with
	 * @param h
	 *            The height of the {@link Rectangle} to union with
	 * @return <code>this</code> for convenience
	 * 
	 */
	public Rectangle union(double x, double y, double w, double h) {
		double right = Math.max(this.x + width, x + w);
		double bottom = Math.max(this.y + height, y + h);
		this.x = Math.min(this.x, x);
		this.y = Math.min(this.y, y);
		this.width = right - this.x;
		this.height = bottom - this.y;
		return this;
	}

	/**
	 * Updates this {@link Rectangle}'s bounds so that the given {@link Point}
	 * is included within.
	 * 
	 * @param p
	 *            The {@link Point} to union with
	 * @return <code>this</code> for convenience
	 */
	public Rectangle union(Point p) {
		return union(p.x, p.y);
	}

	/**
	 * Updates this Rectangle's bounds to the union of this {@link Rectangle}
	 * and the {@link Rectangle}.
	 * 
	 * @param r
	 *            The {@link Rectangle} to union with
	 * @return <code>this</code> for convenience
	 */
	public Rectangle union(Rectangle r) {
		return union(r.x, r.y, r.width, r.height);
	}

}
