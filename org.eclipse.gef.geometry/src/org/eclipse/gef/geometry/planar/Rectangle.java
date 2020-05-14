/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Alexander Nyßen (itemis AG) - migration to double precision
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.planar;

import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;

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
 * @author mwienand
 *
 */
public final class Rectangle extends
		AbstractRectangleBasedGeometry<Rectangle, Polygon> implements IShape {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a {@link Rectangle} with location (0,0) and a size of (0,0).
	 */
	public Rectangle() {
		super(0, 0, 0, 0);
	}

	/**
	 * Constructs a Rectangle from the given values for its location (upper-left
	 * corner point) and its size. If a negative, width or height is passed in,
	 * 0 will be used instead.
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
		super(x, y, width < 0 ? 0 : width, height < 0 ? 0 : height);
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
		this(r.x, r.y, r.width, r.height);
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
				&& PrecisionUtils.greaterEqual(this.x + this.width, x + width)
				&& PrecisionUtils.greaterEqual(this.y + this.height,
						y + height);
	}

	@Override
	public boolean contains(IGeometry g) {
		if (g instanceof Rectangle) {
			return contains((Rectangle) g);
		}
		return ShapeUtils.contains(this, g);
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
	@Override
	public boolean contains(Point p) {
		return contains(p.x(), p.y());
	}

	/**
	 * Tests whether this {@link Rectangle} fully contains the given other
	 * {@link Rectangle}.
	 *
	 * @param r
	 *            the other {@link Rectangle} to test for being contained by
	 *            this {@link Rectangle}
	 * @return <code>true</code> if this {@link Rectangle} contains the other
	 *         {@link Rectangle}, otherwise <code>false</code>
	 * @see IShape#contains(IGeometry)
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
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof Rectangle) {
			Rectangle r = (Rectangle) o;
			return equals(r.x, r.y, r.width, r.height);
		}
		return false;
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
	 * Returns a new Rectangle which has the exact same parameters as this
	 * Rectangle.
	 *
	 * @return Copy of this Rectangle
	 */
	@Override
	public Rectangle getCopy() {
		return new Rectangle(x, y, width, height);
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

	@Override
	public Polyline getOutline() {
		return new Polyline(x, y, x + width, y, x + width, y + height, x,
				y + height, x, y);
	}

	/**
	 * Returns an array of {@link Line}s representing the top, right, bottom,
	 * and left borders of this {@link Rectangle}.
	 *
	 * @return An array containing {@link Line} representations of this
	 *         {@link Rectangle}'s borders.
	 */
	@Override
	public Line[] getOutlineSegments() {
		Line[] segments = new Line[4];
		segments[0] = new Line(x, y, x + width, y);
		segments[1] = new Line(x + width, y, x + width, y + height);
		segments[2] = new Line(x + width, y + height, x, y + height);
		segments[3] = new Line(x, y + height, x, y);
		return segments;
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
	 * Returns a new Point which represents the middle point of the right hand
	 * side of this Rectangle.
	 *
	 * @return Point at the right of the Rectangle
	 */
	public Point getRight() {
		return new Point(x + width, y + height / 2);
	}

	/**
	 * Rotates this {@link Rectangle} counter-clock-wise by the given
	 * {@link Angle} around the center {@link Point} of this {@link Rectangle}
	 * (see {@link AbstractRectangleBasedGeometry#getCenter()}).
	 *
	 * @param alpha
	 *            The rotation {@link Angle}.
	 * @return the resulting {@link Polygon}
	 * @see IRotatable#getRotatedCCW(Angle, Point)
	 */
	@Override
	public Polygon getRotatedCCW(Angle alpha) {
		Point centroid = getCenter();
		return toPolygon().rotateCCW(alpha, centroid.x, centroid.y);
	}

	/**
	 * Rotates this {@link Rectangle} counter-clock-wise by the given
	 * {@link Angle} around the given {@link Point}.
	 *
	 * If the rotation {@link Angle} is not an integer multiple of 90 degrees,
	 * the resulting figure cannot be expressed as a {@link Rectangle} object.
	 * That's why this method returns a {@link Polygon} instead.
	 *
	 * @param alpha
	 *            the rotation angle
	 * @param cx
	 *            x-component of the center point for the rotation
	 * @param cy
	 *            y-component of the center point for the rotation
	 * @return the resulting {@link Polygon}
	 */
	@Override
	public Polygon getRotatedCCW(Angle alpha, double cx, double cy) {
		return toPolygon().rotateCCW(alpha, cx, cy);
	}

	/**
	 * Rotates this {@link Rectangle} counter-clock-wise by the given
	 * {@link Angle} around the given {@link Point}.
	 *
	 * If the rotation {@link Angle} is not an integer multiple of 90 degrees,
	 * the resulting figure cannot be expressed as a {@link Rectangle} object.
	 * That's why this method returns a {@link Polygon} instead.
	 *
	 * @param alpha
	 *            the rotation angle
	 * @param center
	 *            the center point for the rotation
	 * @return the resulting {@link Polygon}
	 */
	@Override
	public Polygon getRotatedCCW(Angle alpha, Point center) {
		return toPolygon().rotateCCW(alpha, center.x, center.y);
	}

	/**
	 * Rotates this {@link Rectangle} clock-wise by the given {@link Angle}
	 * around the center ({@link AbstractRectangleBasedGeometry#getCenter()}) of
	 * this {@link Rectangle}.
	 *
	 * @param alpha
	 *            the rotation {@link Angle}
	 * @return the resulting {@link Polygon}
	 * @see IRotatable#getRotatedCW(Angle, Point)
	 */
	@Override
	public Polygon getRotatedCW(Angle alpha) {
		Point centroid = getCenter();
		return toPolygon().rotateCW(alpha, centroid.x, centroid.y);
	}

	/**
	 * Rotates this {@link Rectangle} clock-wise by the given {@link Angle}
	 * alpha around the given {@link Point} (cx, cy).
	 *
	 * If the rotation {@link Angle} is not an integer multiple of 90 degrees,
	 * the resulting figure cannot be expressed as a {@link Rectangle} object.
	 * That's why this method returns a {@link Polygon} instead.
	 *
	 * @param alpha
	 *            the rotation angle
	 * @param cx
	 *            x-component of the center point for the rotation
	 * @param cy
	 *            y-component of the center point for the rotation
	 * @return the resulting {@link Polygon}
	 */
	@Override
	public Polygon getRotatedCW(Angle alpha, double cx, double cy) {
		return toPolygon().rotateCW(alpha, cx, cy);
	}

	/**
	 * Rotates this {@link Rectangle} clock-wise by the given {@link Angle}
	 * alpha around the given {@link Point}.
	 *
	 * If the rotation {@link Angle} is not an integer multiple of 90 degrees,
	 * the resulting figure cannot be expressed as a {@link Rectangle} object.
	 * That's why this method returns a {@link Polygon} instead.
	 *
	 * @param alpha
	 *            the rotation angle
	 * @param center
	 *            the center point for the rotation
	 * @return the resulting {@link Polygon}
	 */
	@Override
	public Polygon getRotatedCW(Angle alpha, Point center) {
		return toPolygon().rotateCW(alpha, center.x, center.y);
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
	@Override
	public Polygon getTransformed(AffineTransform t) {
		return new Polygon(t.getTransformed(getPoints()));
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
			setBounds(x1, y1, x2 - x1 < 0 ? 0 : x2 - x1,
					y2 - y1 < 0 ? 0 : y2 - y1);
			return this;
		}
		setBounds(0, 0, 0, 0); // no intersection
		return this;
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
	 * @see IGeometry#toPath()
	 */
	@Override
	public Path toPath() {
		return new Path().moveTo(x, y).lineTo(x + width, y)
				.lineTo(x + width, y + height).lineTo(x, y + height).close();
	}

	/**
	 * Converts this {@link Rectangle} into a {@link Polygon} representation.
	 * The control points used to construct the polygon are the border points
	 * returned by {@link #getPoints()}.
	 *
	 * @return A {@link Polygon} representation for this {@link Rectangle}
	 */
	public Polygon toPolygon() {
		return new Polygon(Point.getCopy(getPoints()));
	}

	@Override
	public String toString() {
		return "Rectangle: (" + x + ", " + y + ", " + //$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
				width + ", " + height + ")";//$NON-NLS-2$//$NON-NLS-1$
	}

	@Override
	public boolean touches(IGeometry g) {
		if (g instanceof Line) {
			return touches((Line) g);
		} else if (g instanceof Rectangle) {
			return touches((Rectangle) g);
		}
		return super.touches(g);
	}

	/**
	 * Tests whether this {@link Rectangle} and the given {@link Line} touch,
	 * i.e. whether they have at least one point in common.
	 *
	 * @param l
	 *            The {@link Line} to test.
	 * @return <code>true</code> if this {@link Rectangle} and the given
	 *         {@link Line} share at least one common point, <code>false</code>
	 *         otherwise.
	 */
	public boolean touches(Line l) {
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
	 * Tests whether this {@link Rectangle} and the given other
	 * {@link Rectangle} touch, i.e. whether they have at least one point in
	 * common.
	 *
	 * @param r
	 *            The {@link Rectangle} to test
	 * @return <code>true</code> if this {@link Rectangle} and the given
	 *         {@link Rectangle} share at least one common point,
	 *         <code>false</code> otherwise.
	 * @see IGeometry#touches(IGeometry)
	 */
	public boolean touches(Rectangle r) {
		return PrecisionUtils.smallerEqual(r.x, x + width)
				&& PrecisionUtils.smallerEqual(r.y, y + height)
				&& PrecisionUtils.greaterEqual(r.x + r.width, x)
				&& PrecisionUtils.greaterEqual(r.y + r.height, y);
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
