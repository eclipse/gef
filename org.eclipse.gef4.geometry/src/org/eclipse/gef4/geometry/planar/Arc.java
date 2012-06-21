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
import org.eclipse.gef4.geometry.utils.CurveUtils;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * Represents the geometric shape of an arc, which is defined by its enclosing
 * framing rectangle, a start angle (relative to the x-axis), and an angular
 * extend (in CCW direction).
 * 
 * @author anyssen
 * 
 */
public final class Arc extends AbstractArcBasedGeometry<Arc, PolyBezier>
		implements ICurve {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@link Arc} of the given values. An {@link Arc} is cut
	 * out of an Ellipse. The start {@link Angle} is the CCW
	 * (counter-clock-wise) {@link Angle} to the x-axis at which the {@link Arc}
	 * begins. The angular extent is the CCW {@link Angle} that spans the
	 * {@link Arc}, i.e. the resulting end {@link Angle} of the {@link Arc} is
	 * the sum of the start {@link Angle} and the angular extent.
	 * 
	 * @param x
	 *            x-coordinate of the {@link Ellipse} bounds
	 * @param y
	 *            y-coordinate of the {@link Ellipse} bounds
	 * @param width
	 *            width of the {@link Ellipse} bounds
	 * @param height
	 *            height of the {@link Ellipse} bounds
	 * @param startAngle
	 *            CCW {@link Angle} at which the {@link Arc} begins
	 * @param angularExtent
	 *            CCW {@link Angle} that spans the {@link Arc}
	 */
	public Arc(double x, double y, double width, double height,
			Angle startAngle, Angle angularExtent) {
		super(x, y, width, height, startAngle, angularExtent);
	}

	/**
	 * Constructs a new {@link Arc} of the given values. An {@link Arc} is cut
	 * out of an Ellipse. The start {@link Angle} is the CCW
	 * (counter-clock-wise) {@link Angle} to the x-axis at which the {@link Arc}
	 * begins. The angular extent is the CCW {@link Angle} that spans the
	 * {@link Arc}, i.e. the resulting end {@link Angle} of the {@link Arc} is
	 * the sum of the start {@link Angle} and the angular extent.
	 * 
	 * @param r
	 *            the {@link AbstractRectangleBasedGeometry} describing the
	 *            {@link Ellipse} bounds
	 * @param startAngle
	 *            CCW {@link Angle} at which the {@link Arc} begins
	 * @param angularExtent
	 *            CCW {@link Angle} that spans the {@link Arc}
	 */
	public Arc(AbstractRectangleBasedGeometry<?, ?> r, Angle startAngle,
			Angle angularExtent) {
		super(r.x, r.y, r.width, r.height, startAngle, angularExtent);
	}

	/*
	 * TODO: Construct Arc from PolyBezier to round out their relation. (Arc
	 * returns PolyBezier objects if it is rotated.)
	 */

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Arc)) {
			return false;
		}
		Arc o = (Arc) obj;
		return PrecisionUtils.equal(x, o.x)
				&& PrecisionUtils.equal(y, o.y)
				&& PrecisionUtils.equal(width, o.width)
				&& PrecisionUtils.equal(height, o.height)
				&& PrecisionUtils.equal(angularExtent.rad(),
						o.angularExtent.rad())
				&& PrecisionUtils.equal(startAngle.rad(), o.startAngle.rad());
	}

	/**
	 * @see org.eclipse.gef4.geometry.planar.IGeometry#getCopy()
	 */
	public Arc getCopy() {
		return new Arc(x, y, width, height, startAngle, angularExtent);
	}

	/**
	 * @see IGeometry#contains(Point)
	 */
	public boolean contains(Point p) {
		for (CubicCurve c : computeBezierApproximation()) {
			if (c.contains(p)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Computes the {@link Point}s of intersection of this {@link Arc} and the
	 * given {@link ICurve}.
	 * 
	 * @param c
	 * @return the intersection {@link Point}s
	 */
	public Point[] getIntersections(ICurve c) {
		return CurveUtils.getIntersections(this, c);
	}

	public boolean intersects(ICurve c) {
		return CurveUtils.getIntersections(this, c).length > 0;
	}

	public boolean overlaps(ICurve c) {
		for (BezierCurve seg1 : computeBezierApproximation()) {
			if (seg1.overlaps(c)) {
				return true;
			}
		}
		return false;
	}

	public CubicCurve[] toBezier() {
		return computeBezierApproximation();
	}

	@Override
	public String toString() {
		return "Arc(" + "x = " + x + ", y = " + y + ", width = " + width
				+ ", height = " + height + ", startAngle = " + startAngle.deg()
				+ ", angularExtend = " + angularExtent.deg() + ")";
	}

	public PolyBezier getRotatedCCW(Angle angle, double cx, double cy) {
		return new PolyBezier(computeBezierApproximation()).rotateCCW(angle,
				cx, cy);
	}

	public PolyBezier getRotatedCCW(Angle angle, Point center) {
		return new PolyBezier(computeBezierApproximation()).rotateCCW(angle,
				center);
	}

	public PolyBezier getRotatedCW(Angle angle, double cx, double cy) {
		return new PolyBezier(computeBezierApproximation()).rotateCW(angle, cx,
				cy);
	}

	public PolyBezier getRotatedCW(Angle angle, Point center) {
		return new PolyBezier(computeBezierApproximation()).rotateCW(angle,
				center);
	}

	public PolyBezier getRotatedCCW(Angle angle) {
		return getRotatedCCW(angle, getCenter());
	}

	public PolyBezier getRotatedCW(Angle angle) {
		return getRotatedCW(angle, getCenter());
	}

}
