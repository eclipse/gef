/*******************************************************************************
 * Copyright (c) 2012, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.planar;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.geometry.euclidean.Angle;

/**
 * An {@link AbstractArcBasedGeometry} describes the {@link Arc} of an
 * {@link Ellipse}. It provides functionality to modify and query attributes of
 * the {@link Arc} and to compute a {@link BezierCurve} approximation of the
 * {@link Arc}.
 *
 * @param <T>
 *            the type of the inheriting class
 * @param <S>
 *            the type of rotated objects (see {@link IRotatable})
 *
 * @author mwienand
 *
 */
abstract class AbstractArcBasedGeometry<T extends AbstractArcBasedGeometry<?, ?>, S extends IGeometry>
		extends AbstractRectangleBasedGeometry<T, S> {

	private static final long serialVersionUID = 1L;

	/**
	 * The counter-clockwise (CCW) {@link Angle} to the x-axis at which this
	 * {@link AbstractArcBasedGeometry} begins.
	 */
	protected Angle startAngle;

	/**
	 * The counter-clockwise (CCW) {@link Angle} that spans this
	 * {@link AbstractArcBasedGeometry}.
	 */
	protected Angle angularExtent;

	/**
	 * Constructs a new {@link AbstractArcBasedGeometry} so that it is fully
	 * contained within the framing {@link Rectangle} defined by x, y, width,
	 * and height, spanning the given extend (in CCW direction) from the given
	 * start angle (relative to the x-axis).
	 *
	 * @param x
	 *            the x coordinate of the framing {@link Rectangle}
	 * @param y
	 *            the y coordinate of the framing {@link Rectangle}
	 * @param width
	 *            the width of the framing {@link Rectangle}
	 * @param height
	 *            the height of the framing {@link Rectangle}
	 * @param startAngle
	 *            the CCW {@link Angle} to the x-axis at which this
	 *            {@link AbstractArcBasedGeometry} begins
	 * @param angularExtent
	 *            the CCW {@link Angle} that spans this
	 *            {@link AbstractArcBasedGeometry}
	 */
	public AbstractArcBasedGeometry(double x, double y, double width,
			double height, Angle startAngle, Angle angularExtent) {
		super(x, y, width, height);
		this.startAngle = startAngle;
		this.angularExtent = angularExtent;
	}

	/**
	 * Computes a {@link CubicCurve} approximation for this
	 * {@link AbstractArcBasedGeometry}. It is approximated by a maximum of four
	 * {@link CubicCurve}s, each of which covers a maximum of 90 degrees.
	 *
	 * @return a {@link CubicCurve} approximation for this
	 *         {@link AbstractArcBasedGeometry}
	 */
	protected CubicCurve[] computeBezierApproximation() {
		double start = getStartAngle().rad();
		double end = getStartAngle().rad() + getAngularExtent().rad();

		// approximation is for arcs with angle < 90 degrees, so we may have to
		// split the arc into up to 4 cubic curves
		List<CubicCurve> segments = new ArrayList<>();
		if (angularExtent.deg() <= 90.0) {
			segments.add(ShapeUtils.computeEllipticalArcApproximation(x, y,
					width, height, Angle.fromRad(start), Angle.fromRad(end)));
		} else {
			// two or more segments, the first will be an ellipse segment
			// approximation
			segments.add(ShapeUtils.computeEllipticalArcApproximation(x, y,
					width, height, Angle.fromRad(start),
					Angle.fromRad(start + Math.PI / 2)));
			if (angularExtent.deg() <= 180.0) {
				// two segments, calculate the second (which is below 90
				// degrees)
				segments.add(ShapeUtils.computeEllipticalArcApproximation(x, y,
						width, height, Angle.fromRad(start + Math.PI / 2),
						Angle.fromRad(end)));
			} else {
				// three or more segments, so calculate the second one
				segments.add(ShapeUtils.computeEllipticalArcApproximation(x, y,
						width, height, Angle.fromRad(start + Math.PI / 2),
						Angle.fromRad(start + Math.PI)));
				if (angularExtent.deg() <= 270.0) {
					// three segments, calculate the third (which is below 90
					// degrees)
					segments.add(ShapeUtils.computeEllipticalArcApproximation(x,
							y, width, height, Angle.fromRad(start + Math.PI),
							Angle.fromRad(end)));
				} else {
					// four segments (fourth below 90 degrees), so calculate the
					// third and fourth
					segments.add(ShapeUtils.computeEllipticalArcApproximation(x,
							y, width, height, Angle.fromRad(start + Math.PI),
							Angle.fromRad(start + 3 * Math.PI / 2)));
					segments.add(ShapeUtils.computeEllipticalArcApproximation(x,
							y, width, height,
							Angle.fromRad(start + 3 * Math.PI / 2),
							Angle.fromRad(end)));
				}
			}
		}
		return segments.toArray(new CubicCurve[] {});
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
	 * Returns the start {@link Point} of this {@link AbstractArcBasedGeometry}.
	 *
	 * @return the start {@link Point} of this {@link AbstractArcBasedGeometry}
	 */
	public Point getP1() {
		return getPoint(Angle.fromRad(0));
	}

	/**
	 * Returns the end {@link Point} of this {@link AbstractArcBasedGeometry}.
	 *
	 * @return the end {@link Point} of this {@link AbstractArcBasedGeometry}
	 */
	public Point getP2() {
		return getPoint(angularExtent);
	}

	/**
	 * Computes a {@link Point} on this {@link AbstractArcBasedGeometry}. The
	 * {@link Point}'s coordinates are calculated by moving the given
	 * {@link Angle} on this {@link AbstractArcBasedGeometry} starting at this
	 * {@link AbstractArcBasedGeometry}'s start {@link Point}.
	 *
	 * @param angularExtent
	 *            the {@link Angle} to move from the start {@link Point} of this
	 *            {@link AbstractArcBasedGeometry}
	 * @return the {@link Point} at the given extension {@link Angle}
	 */
	public Point getPoint(Angle angularExtent) {
		double a = width / 2;
		double b = height / 2;
		return new Point(
				x + a + a * Math.cos(startAngle.rad() + angularExtent.rad()),
				y + b - b * Math.sin(startAngle.rad() + angularExtent.rad()));
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
	 * Returns the x coordinate of the start {@link Point} of this
	 * {@link AbstractArcBasedGeometry}.
	 *
	 * @return the x coordinate of the start {@link Point} of this
	 *         {@link AbstractArcBasedGeometry}
	 */
	public double getX1() {
		return getP1().x;
	}

	/**
	 * Returns the x coordinate of the end {@link Point} of this
	 * {@link AbstractArcBasedGeometry}.
	 *
	 * @return the x coordinate of the end {@link Point} of this
	 *         {@link AbstractArcBasedGeometry}
	 */
	public double getX2() {
		return getP2().x;
	}

	/**
	 * Returns the y coordinate of the start {@link Point} of this
	 * {@link AbstractArcBasedGeometry}.
	 *
	 * @return the y coordinate of the start {@link Point} of this
	 *         {@link AbstractArcBasedGeometry}
	 */
	public double getY1() {
		return getP1().y;
	}

	/**
	 * Returns the y coordinate of the end {@link Point} of this
	 * {@link AbstractArcBasedGeometry}.
	 *
	 * @return the y coordinate of the end {@link Point} of this
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
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T setAngularExtent(Angle angularExtent) {
		this.angularExtent = angularExtent;
		return (T) this;
	}

	/**
	 * Sets the start {@link Angle} of this {@link AbstractArcBasedGeometry}.
	 *
	 * @param startAngle
	 *            the new start {@link Angle} for this
	 *            {@link AbstractArcBasedGeometry}
	 * @return <code>this</code> for convenience
	 */
	@SuppressWarnings("unchecked")
	public T setStartAngle(Angle startAngle) {
		this.startAngle = startAngle;
		return (T) this;
	}

	/**
	 * @see IGeometry#toPath()
	 */
	@Override
	public Path toPath() {
		return CurveUtils.toPath(computeBezierApproximation());
	}

}
