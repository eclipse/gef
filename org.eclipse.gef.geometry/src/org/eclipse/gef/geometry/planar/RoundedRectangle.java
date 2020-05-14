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
import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;

/**
 * Represents the geometric shape of a rounded rectangle, i.e. a rectangle with
 * rounded corners.
 *
 * <pre>
 *                  arc-width
 *                         width
 *                 .-------------------.
 *                 .--------.
 *                +----+-----+-----+----+
 *            / / |    ^           ^    |
 *    arc-   | |  |    |           |    |
 *   height  | |  + &lt;- arc end point -&gt; +
 *           | |  |                     |
 *           |  \ |                     |
 *    height |    +                     +
 *           |    |                     |
 *           |    |                     |
 *           |    + &lt;- arc end point -&gt; +
 *           |    |    |           |    |
 *            \   |    v           v    |
 *                +----+-----+-----+----+
 * </pre>
 *
 * The maximum value for the arc-width is the width of the rectangle and the
 * maximum value for the arc-height is the height of the rectangle. For the
 * maximal values, the end points of the arcs are at the centers of the sides of
 * the rectangle.
 * <p>
 * Note that while all manipulations (e.g. within shrink, expand) within this
 * class are based on double precision, all comparisons (e.g. within contains,
 * intersects, equals, etc.) are based on a limited precision (with an accuracy
 * defined within {@link PrecisionUtils}) to compensate for rounding effects.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public final class RoundedRectangle
		extends AbstractRectangleBasedGeometry<RoundedRectangle, PolyBezier>
		implements IShape {

	private static final long serialVersionUID = 1L;

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
		super(x, y, width, height);
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

	@Override
	public boolean contains(IGeometry g) {
		return ShapeUtils.contains(this, g);
	}

	/**
	 * @see IGeometry#contains(Point)
	 */
	@Override
	public boolean contains(final Point p) {
		// quick rejection via bounds
		final Rectangle testRect = getBounds();
		if (!testRect.contains(p)) {
			return false;
		}

		// limit arc width and arc height
		double aw = getEffectiveArcWidth() / 2;
		double ah = getEffectiveArcHeight() / 2;

		// check for containment within the inner rectangles
		testRect.setBounds(x, y + ah, width, height - 2 * ah);
		if (testRect.contains(p)) {
			return true;
		}
		testRect.setBounds(x + aw, y, width - 2 * aw, height);
		if (testRect.contains(p)) {
			return true;
		}

		// check the arcs
		final Ellipse e = new Ellipse(x, y, 2 * aw, 2 * ah);
		if (e.contains(p)) {
			return true;
		}
		e.setBounds(x, y + height - 2 * ah, 2 * aw, 2 * ah);
		if (e.contains(p)) {
			return true;
		}
		e.setBounds(x + width - 2 * aw, y, 2 * aw, 2 * ah);
		if (e.contains(p)) {
			return true;
		}
		e.setBounds(x + width - 2 * aw, y + height - 2 * ah, 2 * aw, 2 * ah);
		if (e.contains(p)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof RoundedRectangle)) {
			return false;
		}
		RoundedRectangle o = (RoundedRectangle) obj;
		return PrecisionUtils.equal(x, o.x) && PrecisionUtils.equal(y, o.y)
				&& PrecisionUtils.equal(width, o.width)
				&& PrecisionUtils.equal(height, o.height)
				&& PrecisionUtils.equal(arcWidth, o.arcWidth)
				&& PrecisionUtils.equal(arcHeight, o.arcHeight);
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
	 * Returns the bottom edge of this {@link RoundedRectangle}.
	 *
	 * @return the bottom edge of this {@link RoundedRectangle}.
	 */
	public Line getBottom() {
		double aw = getEffectiveArcWidth() / 2;
		return new Line(x + aw, y + height, x + width - aw, y + height);
	}

	/**
	 * Returns the bottom left {@link Arc} of this {@link RoundedRectangle}.
	 *
	 * @return the bottom left {@link Arc} of this {@link RoundedRectangle}.
	 */
	public Arc getBottomLeftArc() {
		double aw = getEffectiveArcWidth() / 2;
		double ah = getEffectiveArcHeight() / 2;
		return new Arc(x, y + height - 2 * ah, 2 * aw, 2 * ah,
				Angle.fromDeg(180), Angle.fromDeg(90));
	}

	/**
	 * Returns the bottom right {@link Arc} of this {@link RoundedRectangle}.
	 *
	 * @return the bottom right {@link Arc} of this {@link RoundedRectangle}.
	 */
	public Arc getBottomRightArc() {
		double aw = getEffectiveArcWidth() / 2;
		double ah = getEffectiveArcHeight() / 2;
		return new Arc(x + width - 2 * aw, y + height - 2 * ah, 2 * aw, 2 * ah,
				Angle.fromDeg(270), Angle.fromDeg(90));
	}

	/**
	 * @see IGeometry#getCopy()
	 */
	@Override
	public RoundedRectangle getCopy() {
		return new RoundedRectangle(x, y, width, height, arcWidth, arcHeight);
	}

	/**
	 * Returns the effective arc height, i.e. clamped to the range
	 * <code>[0;height]</code>.
	 *
	 * @return the effective arc height, i.e. clamped to the range
	 *         <code>[0;height]</code>.
	 */
	protected double getEffectiveArcHeight() {
		double ah = arcHeight < 0 ? 0 : arcHeight;
		return ah > height ? height : ah;
	}

	/**
	 * Returns the effective arc width, i.e. clamped to the range
	 * <code>[0;width]</code>.
	 *
	 * @return the effective arc width, i.e. clamped to the range
	 *         <code>[0;width]</code>.
	 */
	protected double getEffectiveArcWidth() {
		double aw = arcWidth < 0 ? 0 : arcWidth;
		return aw > width ? width : aw;
	}

	/**
	 * Returns the left edge of this {@link RoundedRectangle}.
	 *
	 * @return the left edge of this {@link RoundedRectangle}.
	 */
	public Line getLeft() {
		double ah = getEffectiveArcHeight() / 2;
		return new Line(x, y + ah, x, y + height - ah);
	}

	@Override
	public PolyBezier getOutline() {
		return ShapeUtils.getOutline(this);
	}

	/**
	 * @see org.eclipse.gef.geometry.planar.IShape#getOutlineSegments()
	 */
	@Override
	public BezierCurve[] getOutlineSegments() {
		double aw = getEffectiveArcWidth() / 2;
		double ah = getEffectiveArcHeight() / 2;
		return new BezierCurve[] {
				ShapeUtils.computeEllipticalArcApproximation(x + width - 2 * aw,
						y, 2 * aw, 2 * ah, Angle.fromDeg(0), Angle.fromDeg(90)),
				new Line(x + width - aw, y, x + aw, y),
				ShapeUtils.computeEllipticalArcApproximation(x, y, 2 * aw,
						2 * ah, Angle.fromDeg(90), Angle.fromDeg(180)),
				new Line(x, y + ah, x, y + height - ah),
				ShapeUtils.computeEllipticalArcApproximation(x,
						y + height - 2 * ah, 2 * aw, 2 * ah, Angle.fromDeg(180),
						Angle.fromDeg(270)),
				new Line(x + aw, y + height, x + width - aw, y + height),
				ShapeUtils.computeEllipticalArcApproximation(x + width - 2 * aw,
						y + height - 2 * ah, 2 * aw, 2 * ah, Angle.fromDeg(270),
						Angle.fromDeg(360)),
				new Line(x + width, y + height - ah, x + width, y + ah) };
	}

	/**
	 * Returns the right edge of this {@link RoundedRectangle}.
	 *
	 * @return the right edge of this {@link RoundedRectangle}.
	 */
	public Line getRight() {
		double ah = getEffectiveArcHeight() / 2;
		return new Line(x + width, y + ah, x + width, y + height - ah);
	}

	@Override
	public PolyBezier getRotatedCCW(Angle angle) {
		return getOutline().rotateCCW(angle);
	}

	@Override
	public PolyBezier getRotatedCCW(Angle angle, double cx, double cy) {
		return getOutline().rotateCCW(angle, cx, cy);
	}

	@Override
	public PolyBezier getRotatedCCW(Angle angle, Point center) {
		return getOutline().rotateCCW(angle, center);
	}

	@Override
	public PolyBezier getRotatedCW(Angle angle) {
		return getOutline().rotateCW(angle);
	}

	@Override
	public PolyBezier getRotatedCW(Angle angle, double cx, double cy) {
		return getOutline().rotateCW(angle, cx, cy);
	}

	@Override
	public PolyBezier getRotatedCW(Angle angle, Point center) {
		return getOutline().rotateCW(angle, center);
	}

	/**
	 * Returns the top edge of this {@link RoundedRectangle}.
	 *
	 * @return the top edge of this {@link RoundedRectangle}.
	 */
	public Line getTop() {
		double aw = getEffectiveArcWidth() / 2;
		return new Line(x + aw, y, x + width - aw, y);
	}

	/**
	 * Returns the top left {@link Arc} of this {@link RoundedRectangle}.
	 *
	 * @return the top left {@link Arc} of this {@link RoundedRectangle}.
	 */
	public Arc getTopLeftArc() {
		return new Arc(x, y, getEffectiveArcWidth(), getEffectiveArcHeight(),
				Angle.fromDeg(90), Angle.fromDeg(90));
	}

	/**
	 * Returns the top right {@link Arc} of this {@link RoundedRectangle}.
	 *
	 * @return the top right {@link Arc} of this {@link RoundedRectangle}.
	 */
	public Arc getTopRightArc() {
		double aw = getEffectiveArcWidth() / 2;
		double ah = getEffectiveArcHeight() / 2;
		return new Arc(x + width - 2 * aw, y, 2 * aw, 2 * ah, Angle.fromDeg(0),
				Angle.fromDeg(90));
	}

	/**
	 * @see IGeometry#getTransformed(AffineTransform)
	 */
	@Override
	public CurvedPolygon getTransformed(AffineTransform t) {
		return new CurvedPolygon(getOutlineSegments()).getTransformed(t);
	}

	/**
	 * Sets the arc height of this {@link RoundedRectangle}, which is the height
	 * of the arc used to define its rounded corners.
	 *
	 * @param arcHeight
	 *            the new arc height
	 * @return <code>this</code> for convenience
	 */
	public RoundedRectangle setArcHeight(double arcHeight) {
		this.arcHeight = arcHeight;
		return this;
	}

	/**
	 * Sets the arc width of this {@link RoundedRectangle}, which is the width
	 * of the arc used to define its rounded corners.
	 *
	 * @param arcWidth
	 *            the new arc width
	 * @return <code>this</code> for convenience
	 */
	public RoundedRectangle setArcWidth(double arcWidth) {
		this.arcWidth = arcWidth;
		return this;
	}

	@Override
	public Path toPath() {
		return CurveUtils.toPath(getOutlineSegments()).close();
	}

	@Override
	public String toString() {
		return "RoundedRectangle(" + x + ", " + y + ", " + width + ", " + height //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ ", " + arcWidth + ", " + arcHeight + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
