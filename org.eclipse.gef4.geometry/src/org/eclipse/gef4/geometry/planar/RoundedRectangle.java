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
import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * Represents the geometric shape of a rounded rectangle, i.e. a rectangle with
 * rounded corners.
 * 
 * Note that while all manipulations (e.g. within shrink, expand) within this
 * class are based on double precision, all comparisons (e.g. within contains,
 * intersects, equals, etc.) are based on a limited precision (with an accuracy
 * defined within {@link PrecisionUtils}) to compensate for rounding effects.
 * 
 * @author anyssen
 */
public class RoundedRectangle extends AbstractGeometry implements IShape {

	private static final long serialVersionUID = 1L;

	private double x;
	private double y;
	private double width;
	private double height;
	private double arcWidth;
	private double arcHeight;

	/**
	 * Constructs a new {@link RoundedRectangle} from the given bounds and arc
	 * values.
	 * 
	 * @param x
	 *            the x-coordinate of the new {@link RoundedRectangle}'s bounds
	 * @param y
	 *            the y-coordinate of the new {@link RoundedRectangle}'s bounds
	 * @param width
	 *            the width of the new {@link RoundedRectangle}'s bounds
	 * @param height
	 *            the height of the new {@link RoundedRectangle}'s bounds
	 * @param arcWidth
	 *            the arc width of the new {@link RoundedRectangle} rounded
	 *            corners
	 * @param arcHeight
	 *            the arc height of the new {@link RoundedRectangle} rounded
	 *            corners
	 */
	public RoundedRectangle(double x, double y, double width, double height,
			double arcWidth, double arcHeight) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
	}

	/**
	 * Constructs a new {@link RoundedRectangle} from the bounds of the given
	 * {@link Rectangle} and the given arc values.
	 * 
	 * @param r
	 *            the {@link Rectangle}, whose bounds are used to initialize the
	 *            x, y, width, and height values of the new
	 *            {@link RoundedRectangle}
	 * @param arcWidth
	 *            the arc width of the new {@link RoundedRectangle} rounded
	 *            corners
	 * @param arcHeight
	 *            the arc height of the new {@link RoundedRectangle} rounded
	 *            corners
	 */
	public RoundedRectangle(Rectangle r, double arcWidth, double arcHeight) {
		this(r.getX(), r.getY(), r.getWidth(), r.getHeight(), arcWidth,
				arcHeight);
	}

	/**
	 * @see IGeometry#contains(Point)
	 */
	public boolean contains(final Point p) {
		// quick rejection via bounds
		final Rectangle testRect = getBounds();
		if (!testRect.contains(p)) {
			return false;
		}

		// check for containment within the inner rectangles
		testRect.setBounds(x, y + arcHeight, width, height - 2 * arcHeight);
		if (testRect.contains(p)) {
			return true;
		}
		testRect.setBounds(x + arcWidth, y, width - 2 * arcWidth, height);
		if (testRect.contains(p)) {
			return true;
		}

		// check the arcs
		final Ellipse e = new Ellipse(x, y, 2 * arcWidth, 2 * arcHeight);
		if (e.contains(p)) {
			return true;
		}
		e.setBounds(x, y + height - 2 * arcHeight, 2 * arcWidth, 2 * arcHeight);
		if (e.contains(p)) {
			return true;
		}
		e.setBounds(x + width - 2 * arcWidth, y, 2 * arcWidth, 2 * arcHeight);
		if (e.contains(p)) {
			return true;
		}
		e.setBounds(x + width - 2 * arcWidth, y + height - 2 * arcHeight,
				2 * arcWidth, 2 * arcHeight);
		if (e.contains(p)) {
			return true;
		}
		return false;
	}

	/**
	 * @see IGeometry#contains(Rectangle)
	 */
	public boolean contains(final Rectangle r) {
		// check that all border points of the rectangle are contained.
		return contains(r.getTopLeft()) && contains(r.getTopRight())
				&& contains(r.getBottomLeft()) && contains(r.getBottomRight());
	}

	/**
	 * Returns the arc height of this {@link RoundedRectangle}, which is the
	 * height of the arc used to define its rounded corners.
	 * 
	 * @return the arc height
	 */
	public double getArcHeight() {
		return arcHeight;
	}

	/**
	 * Returns the arc width of this {@link RoundedRectangle}, which is the
	 * width of the arc used to define its rounded corners.
	 * 
	 * @return the arc height
	 */
	public double getArcWidth() {
		return arcWidth;
	}

	/**
	 * @see IGeometry#getBounds()
	 */
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}

	/**
	 * Returns the height of this {@link RoundedRectangle}.
	 * 
	 * @return the height of this {@link RoundedRectangle}
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Returns the width of this {@link RoundedRectangle}.
	 * 
	 * @return the width of this {@link RoundedRectangle}
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Returns the x-coordinate of this {@link RoundedRectangle}.
	 * 
	 * @return the x-coordinate of this {@link RoundedRectangle}
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y-coordinate of this {@link RoundedRectangle}.
	 * 
	 * @return the y-coordinate of this {@link RoundedRectangle}
	 */
	public double getY() {
		return y;
	}

	/**
	 * @see IGeometry#intersects(Rectangle)
	 */
	public boolean intersects(final Rectangle r) {
		// quick rejection via bounds
		final Rectangle testRect = getBounds();
		if (!testRect.intersects(r)) {
			return false;
		}

		// check for intersection within the two inner rectangles
		testRect.setBounds(x, y + arcHeight, width, height - 2 * arcHeight);
		if (testRect.intersects(r)) {
			return true;
		}
		testRect.setBounds(x + arcWidth, y, width - 2 * arcWidth, height);
		if (testRect.contains(r)) {
			return true;
		}

		// check the arcs
		final Ellipse e = new Ellipse(x, y, 2 * arcWidth, 2 * arcHeight);
		if (e.intersects(r)) {
			return true;
		}
		e.setBounds(x, y + height - 2 * arcHeight, 2 * arcWidth, 2 * arcHeight);
		if (e.intersects(r)) {
			return true;
		}
		e.setBounds(x + width - 2 * arcWidth, y, 2 * arcWidth, 2 * arcHeight);
		if (e.intersects(r)) {
			return true;
		}
		e.setBounds(x + width - 2 * arcWidth, y + height - 2 * arcHeight,
				2 * arcWidth, 2 * arcHeight);
		if (e.intersects(r)) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the arc height of this {@link RoundedRectangle}, which is the height
	 * of the arc used to define its rounded corners.
	 * 
	 * @param arcHeight
	 *            the new arc height
	 */
	public void setArcHeight(double arcHeight) {
		this.arcHeight = arcHeight;
	}

	/**
	 * Sets the arc width of this {@link RoundedRectangle}, which is the width
	 * of the arc used to define its rounded corners.
	 * 
	 * @param arcWidth
	 *            the new arc width
	 */
	public void setArcWidth(double arcWidth) {
		this.arcWidth = arcWidth;
	}

	/**
	 * Sets the height of this {@link RoundedRectangle} to the given value.
	 * 
	 * @param height
	 *            the new height
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * Sets the width of this {@link RoundedRectangle} to the given value.
	 * 
	 * @param width
	 *            the new width
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * Sets the x-coordinate of this {@link RoundedRectangle} to the given
	 * value.
	 * 
	 * @param x
	 *            the new x-coordinate
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Sets the y-coordinate of this {@link RoundedRectangle} to the given
	 * value.
	 * 
	 * @param y
	 *            the new y-coordinate
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @see IGeometry#toPath()
	 */
	public Path toPath() {
		// overwritten to optimize w.r.t. object creation (could otherwise use
		// the segments)
		Path path = new Path();
		path.moveTo(x, y - arcHeight);
		path.quadTo(x + arcWidth, y, x, y);
		path.lineTo(x + width - arcWidth * 2, y);
		path.quadTo(x + width, y + arcHeight, x + width, y);
		path.lineTo(x + width, y + height - arcHeight * 2);
		path.quadTo(x + width - arcWidth * 2, y + height, x + width, y + width);
		path.lineTo(x + arcWidth, y + height);
		path.quadTo(x, y + height - arcHeight * 2, x, y + height);
		path.close();
		return path;
	}

	/**
	 * Returns the top edge of this {@link RoundedRectangle}.
	 * 
	 * @return the top edge of this {@link RoundedRectangle}.
	 */
	public Line getTop() {
		return new Line(x + arcWidth, y, x + width - arcWidth, y);
	}

	/**
	 * Returns the bottom edge of this {@link RoundedRectangle}.
	 * 
	 * @return the bottom edge of this {@link RoundedRectangle}.
	 */
	public Line getBottom() {
		return new Line(x + arcWidth, y + height - arcHeight, x + width
				- arcWidth, y + height - arcHeight);
	}

	/**
	 * Returns the right edge of this {@link RoundedRectangle}.
	 * 
	 * @return the right edge of this {@link RoundedRectangle}.
	 */
	public Line getRight() {
		return new Line(x + width, y + arcHeight, x + width, y + height
				- arcHeight);
	}

	/**
	 * @see org.eclipse.gef4.geometry.planar.IShape#getOutlineSegments()
	 */
	public ICurve[] getOutlineSegments() {
		return new ICurve[] {
				new QuadraticCurve(x, y - arcHeight, x + arcWidth, y, x, y),
				new Line(x, y, x + width - arcWidth * 2, y),
				new QuadraticCurve(x + width - arcWidth * 2, y, x + width, y
						+ arcHeight, x + width, y),
				new Line(x + width, y, x + width, y + height - arcHeight * 2),
				new QuadraticCurve(x + width, y + height - arcHeight * 2, x
						+ width - arcWidth * 2, y + height, x + width, y
						+ width),
				new Line(x + width, y + width, x + arcWidth, y + height),
				new QuadraticCurve(x + arcWidth, y + height, x, y + height
						- arcHeight * 2, x, y + height),
				new Line(x, y + height, x, y - arcHeight) };
	}

	/**
	 * Returns the left edge of this {@link RoundedRectangle}.
	 * 
	 * @return the left edge of this {@link RoundedRectangle}.
	 */
	public Line getLeft() {
		return new Line(x, y + arcHeight, x, y + height - arcHeight);
	}

	/**
	 * Returns the top left {@link Arc} of this {@link RoundedRectangle}.
	 * 
	 * @return the top left {@link Arc} of this {@link RoundedRectangle}.
	 */
	public Arc getTopLeftArc() {
		return new Arc(x, y, arcWidth, arcHeight, Angle.fromDeg(90),
				Angle.fromDeg(90));
	}

	/**
	 * Returns the top right {@link Arc} of this {@link RoundedRectangle}.
	 * 
	 * @return the top right {@link Arc} of this {@link RoundedRectangle}.
	 */
	public Arc getTopRightArc() {
		return new Arc(x + width - arcWidth, y, arcWidth, arcHeight,
				Angle.fromDeg(0), Angle.fromDeg(90));
	}

	/**
	 * Returns the bottom left {@link Arc} of this {@link RoundedRectangle}.
	 * 
	 * @return the bottom left {@link Arc} of this {@link RoundedRectangle}.
	 */
	public Arc getBottomLeftArc() {
		return new Arc(x, y + height - arcHeight, arcWidth, arcHeight,
				Angle.fromDeg(180), Angle.fromDeg(90));
	}

	/**
	 * Returns the bottom right {@link Arc} of this {@link RoundedRectangle}.
	 * 
	 * @return the bottom right {@link Arc} of this {@link RoundedRectangle}.
	 */
	public Arc getBottomRightArc() {
		return new Arc(x + width - arcWidth, y + height - arcHeight, arcWidth,
				arcHeight, Angle.fromDeg(270), Angle.fromDeg(90));
	}

	@Override
	public String toString() {
		return "RoundedRectangle: (" + x + ", " + y + ", " + width + ", " + height + ", " + arcWidth + ", " + arcHeight; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	}

	/**
	 * @see IGeometry#getCopy()
	 */
	public RoundedRectangle getCopy() {
		return new RoundedRectangle(x, y, width, height, arcWidth, arcHeight);
	}
}
