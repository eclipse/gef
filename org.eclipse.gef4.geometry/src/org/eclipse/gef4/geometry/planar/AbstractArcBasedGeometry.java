/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.geometry.Angle;
import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.utils.CurveUtils;

/**
 * An {@link AbstractArcBasedGeometry} describes the arc of an {@link Ellipse}.
 * It provides functionality to modify and query attributes of the arc and to
 * compute a Bezier approximation of the arc (the outline).
 * 
 * @param <T>
 *            type of the inheriting class
 */
abstract class AbstractArcBasedGeometry<T extends AbstractArcBasedGeometry<?>>
		extends AbstractRectangleBasedGeometry<T, IGeometry> {

	private static final long serialVersionUID = 1L;

	/**
	 * The CCW (counter-clock-wise) {@link Angle} to the x-axis at which this
	 * {@link AbstractArcBasedGeometry} begins.
	 */
	protected Angle startAngle;

	/**
	 * The CCW (counter-clock-wise) {@link Angle} that spans this
	 * {@link AbstractArcBasedGeometry}.
	 */
	protected Angle angularExtent;

	/**
	 * Constructs a new {@link AbstractArcBasedGeometry} so that it is fully
	 * contained within the framing rectangle defined by (x, y, width, height),
	 * spanning the given extend (in CCW direction) from the given start angle
	 * (relative to the x-axis).
	 * 
	 * @param x
	 *            the x-coordinate of the framing rectangle
	 * @param y
	 *            the y-coordinate of the framing rectangle
	 * @param width
	 * @param height
	 * @param startAngle
	 * @param angularExtent
	 */
	public AbstractArcBasedGeometry(double x, double y, double width,
			double height, Angle startAngle, Angle angularExtent) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.startAngle = startAngle;
		this.angularExtent = angularExtent;
	}

	/**
	 * Returns the extension {@link Angle} of this
	 * {@link AbstractArcBasedGeometry}, i.e. the {@link Angle} defining the
	 * span of this {@link AbstractArcBasedGeometry}.
	 * 
	 * @return the extension {@link Angle} of this
	 *         {@link AbstractArcBasedGeometry}
	 */
	public Angle getAngularExtent() {
		return angularExtent;
	}

	/**
	 * Returns a {@link Point} representing the start {@link Point} of this
	 * {@link AbstractArcBasedGeometry}.
	 * 
	 * @return the start {@link Point} of this {@link AbstractArcBasedGeometry}
	 */
	public Point getP1() {
		return getPoint(Angle.fromRad(0));
	}

	/**
	 * Returns a {@link Point} representing the end {@link Point} of this
	 * {@link AbstractArcBasedGeometry}.
	 * 
	 * @return the end {@link Point} of this {@link AbstractArcBasedGeometry}
	 */
	public Point getP2() {
		return getPoint(angularExtent);
	}

	/**
	 * Computes a {@link Point} on this {@link AbstractArcBasedGeometry}. The
	 * {@link Point}'s coordinates are calculated by moving the given
	 * {@link Angle} on this {@link AbstractArcBasedGeometry} starting at the
	 * {@link AbstractArcBasedGeometry}'s start {@link Point}.
	 * 
	 * @param angularExtent
	 * @return the {@link Point} at the given {@link Angle}
	 */
	public Point getPoint(Angle angularExtent) {
		double a = width / 2;
		double b = height / 2;

		// // calculate start and end points of the arc from start to end
		return new Point(x + a + a
				* Math.cos(startAngle.rad() + angularExtent.rad()), y + b - b
				* Math.sin(startAngle.rad() + angularExtent.rad()));
	}

	/**
	 * Returns this {@link AbstractArcBasedGeometry}'s start {@link Angle}.
	 * 
	 * @return this {@link AbstractArcBasedGeometry}'s start {@link Angle}
	 */
	public Angle getStartAngle() {
		return startAngle;
	}

	/**
	 * Returns the x-coordinate of the start {@link Point} of this
	 * {@link AbstractArcBasedGeometry}.
	 * 
	 * @return the x-coordinate of the start {@link Point} of this
	 *         {@link AbstractArcBasedGeometry}
	 */
	public double getX1() {
		return getP1().x;
	}

	/**
	 * Returns the x-coordinate of the end {@link Point} of this
	 * {@link AbstractArcBasedGeometry}.
	 * 
	 * @return the x-coordinate of the end {@link Point} of this
	 *         {@link AbstractArcBasedGeometry}
	 */
	public double getX2() {
		return getP2().x;
	}

	/**
	 * Returns the y-coordinate of the start {@link Point} of this
	 * {@link AbstractArcBasedGeometry}.
	 * 
	 * @return the y-coordinate of the start {@link Point} of this
	 *         {@link AbstractArcBasedGeometry}
	 */
	public double getY1() {
		return getP1().y;
	}

	/**
	 * Returns the y-coordinate of the end {@link Point} of this
	 * {@link AbstractArcBasedGeometry}.
	 * 
	 * @return the y-coordinate of the end {@link Point} of this
	 *         {@link AbstractArcBasedGeometry}
	 */
	public double getY2() {
		return getP2().y;
	}

	/**
	 * Sets the extension {@link Angle} of this {@link AbstractArcBasedGeometry}
	 * .
	 * 
	 * @param angularExtent
	 *            the new extension {@link Angle} for this
	 *            {@link AbstractArcBasedGeometry}
	 */
	public void setAngularExtent(Angle angularExtent) {
		this.angularExtent = angularExtent;
	}

	/**
	 * Sets the start {@link Angle} of this {@link AbstractArcBasedGeometry}.
	 * 
	 * @param startAngle
	 *            the new start {@link Angle} for this
	 *            {@link AbstractArcBasedGeometry}
	 */
	public void setStartAngle(Angle startAngle) {
		this.startAngle = startAngle;
	}

	/**
	 * Computes a Bezier approximation for this {@link AbstractArcBasedGeometry}
	 * . It is approximated by at most four {@link CubicCurve}s which span at
	 * most 90 degrees.
	 * 
	 * @return a Bezier approximation for this {@link AbstractArcBasedGeometry}
	 */
	protected CubicCurve[] computeBezierApproximation() {
		double start = getStartAngle().rad();
		double end = getStartAngle().rad() + getAngularExtent().rad();

		// approximation is for arcs with angle < 90 degrees, so we may have to
		// split the arc into up to 4 cubic curves
		List<CubicCurve> segments = new ArrayList<CubicCurve>();
		if (angularExtent.deg() <= 90.0) {
			segments.add(CurveUtils.computeEllipticalArcApproximation(x, y,
					width, height, Angle.fromRad(start), Angle.fromRad(end)));
		} else {
			// two or more segments, the first will be an ellipse segment
			// approximation
			segments.add(CurveUtils.computeEllipticalArcApproximation(x, y,
					width, height, Angle.fromRad(start),
					Angle.fromRad(start + Math.PI / 2)));
			if (angularExtent.deg() <= 180.0) {
				// two segments, calculate the second (which is below 90
				// degrees)
				segments.add(CurveUtils.computeEllipticalArcApproximation(x, y,
						width, height, Angle.fromRad(start + Math.PI / 2),
						Angle.fromRad(end)));
			} else {
				// three or more segments, so calculate the second one
				segments.add(CurveUtils.computeEllipticalArcApproximation(x, y,
						width, height, Angle.fromRad(start + Math.PI / 2),
						Angle.fromRad(start + Math.PI)));
				if (angularExtent.deg() <= 270.0) {
					// three segments, calculate the third (which is below 90
					// degrees)
					segments.add(CurveUtils.computeEllipticalArcApproximation(
							x, y, width, height,
							Angle.fromRad(start + Math.PI), Angle.fromRad(end)));
				} else {
					// four segments (fourth below 90 degrees), so calculate the
					// third and fourth
					segments.add(CurveUtils.computeEllipticalArcApproximation(
							x, y, width, height,
							Angle.fromRad(start + Math.PI),
							Angle.fromRad(start + 3 * Math.PI / 2)));
					segments.add(CurveUtils.computeEllipticalArcApproximation(
							x, y, width, height,
							Angle.fromRad(start + 3 * Math.PI / 2),
							Angle.fromRad(end)));
				}
			}
		}
		return segments.toArray(new CubicCurve[] {});
	}

	/**
	 * @see IGeometry#toPath()
	 */
	public Path toPath() {
		return CurveUtils.toPath(computeBezierApproximation());
	}

	@SuppressWarnings("unchecked")
	public T getRotatedCCW(Angle angle) {
		return (T) ((T) getCopy()).rotateCCW(angle);
	}

	/**
	 * Rotates this {@link AbstractArcBasedGeometry} counter-clock-wise (CCW) by
	 * the given {@link Angle} around its center {@link Point}.
	 * 
	 * @param angle
	 *            the rotation {@link Angle}
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T rotateCCW(Angle angle) {
		startAngle.setRad(startAngle.getAdded(angle).rad());
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T getRotatedCW(Angle angle) {
		return (T) ((T) getCopy()).rotateCW(angle);
	}

	/**
	 * Rotates this {@link AbstractArcBasedGeometry} clock-wise (CW) by the
	 * given {@link Angle} around its center {@link Point}.
	 * 
	 * @param angle
	 *            the rotation {@link Angle}
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T rotateCW(Angle angle) {
		startAngle.setRad(startAngle.getAdded(angle.getOppositeFull()).rad());
		return (T) this;
	}

}
