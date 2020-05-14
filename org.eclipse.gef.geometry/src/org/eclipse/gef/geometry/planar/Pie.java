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
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;

/**
 * The {@link Pie} is a closed {@link AbstractArcBasedGeometry}. It is the
 * complement of the {@link Arc}, which is an open
 * {@link AbstractArcBasedGeometry}.
 *
 * The {@link Pie} covers an area, therefore it implements the {@link IShape}
 * interface.
 *
 * @author anyssen
 * @author mwienand
 */
public class Pie extends AbstractArcBasedGeometry<Pie, Path> implements IShape {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@link Pie} from the given values.
	 *
	 * @param r
	 *            The {@link AbstractRectangleBasedGeometry} which provides the
	 *            size for this {@link Pie}.
	 * @param startAngle
	 *            The counter-clockwise (CCW) {@link Angle} to the x-axis at
	 *            which this {@link Pie} begins.
	 * @param angularExtent
	 *            The counter-clockwise (CCW) {@link Angle} that spans this
	 *            {@link Pie}.
	 */
	public Pie(AbstractRectangleBasedGeometry<?, ?> r, Angle startAngle,
			Angle angularExtent) {
		super(r.x, r.y, r.width, r.height, startAngle, angularExtent);
	}

	/**
	 * Constructs a new {@link Pie} from the given {@link Arc}.
	 *
	 * @param arc
	 *            The {@link Arc} which provides size, start angle, and angular
	 *            extent for this {@link Pie}.
	 */
	public Pie(Arc arc) {
		super(arc.x, arc.y, arc.width, arc.height, arc.startAngle,
				arc.angularExtent);
	}

	/**
	 * Constructs a new {@link Pie} from the given values.
	 *
	 * @see AbstractArcBasedGeometry#AbstractArcBasedGeometry(double, double,
	 *      double, double, Angle, Angle)
	 *
	 * @param x
	 *            The x-coordinate of the rectangular area which encloses thie
	 *            {@link Pie}.
	 * @param y
	 *            The y-coordinate of the rectangular area which encloses thie
	 *            {@link Pie}.
	 * @param width
	 *            The width of the rectangular area which encloses thie
	 *            {@link Pie}.
	 * @param height
	 *            The height of the rectangular area which encloses thie
	 *            {@link Pie}.
	 * @param startAngle
	 *            The counter-clockwise (CCW) {@link Angle} to the x-axis at
	 *            which this {@link Pie} begins.
	 * @param angularExtent
	 *            The counter-clockwise (CCW) {@link Angle} that spans this
	 *            {@link Pie}.
	 */
	public Pie(double x, double y, double width, double height,
			Angle startAngle, Angle angularExtent) {
		super(x, y, width, height, startAngle, angularExtent);
	}

	@Override
	public boolean contains(IGeometry g) {
		return ShapeUtils.contains(this, g);
	}

	/*
	 * TODO: Add additional methods to rotate a Pie so that it remains a Pie.
	 */

	@Override
	public boolean contains(Point p) {
		// check if the point is in the arc's angle
		Angle pAngle = new Vector(1, 0).getAngleCCW(new Vector(getCenter(), p));
		if (!(PrecisionUtils.greater(pAngle.rad(), startAngle.rad())
				&& PrecisionUtils.smaller(pAngle.rad(),
						startAngle.getAdded(angularExtent).rad()))) {
			return false;
		}

		// angle is correct, check if the point is inside the bounding ellipse
		return new Ellipse(x, y, width, height).contains(p);
	}

	@Override
	public Rectangle getBounds() {
		return getOutline().getBounds();
	}

	/**
	 * @see org.eclipse.gef.geometry.planar.IGeometry#getCopy()
	 */
	@Override
	public Pie getCopy() {
		return new Pie(x, y, width, height, startAngle, angularExtent);
	}

	@Override
	public PolyBezier getOutline() {
		return new PolyBezier(getOutlineSegments());
	}

	@Override
	public BezierCurve[] getOutlineSegments() {
		CubicCurve[] arcSegs = computeBezierApproximation();
		BezierCurve[] outlineSegs = new BezierCurve[arcSegs.length + 2];
		for (int i = 0; i < arcSegs.length; i++) {
			outlineSegs[i] = arcSegs[i];
		}
		outlineSegs[outlineSegs.length - 2] = new Line(
				outlineSegs[outlineSegs.length - 3].getP2(), getCenter());
		outlineSegs[outlineSegs.length - 1] = new Line(getCenter(),
				outlineSegs[0].getP1());
		return outlineSegs;
	}

	@Override
	public Path getRotatedCCW(Angle angle) {
		return getRotatedCCW(angle, getCenter());
	}

	@Override
	public Path getRotatedCCW(Angle angle, double cx, double cy) {
		return new PolyBezier(computeBezierApproximation())
				.rotateCCW(angle, cx, cy).toPath();
	}

	@Override
	public Path getRotatedCCW(Angle angle, Point center) {
		return new PolyBezier(computeBezierApproximation())
				.rotateCCW(angle, center).toPath();
	}

	@Override
	public Path getRotatedCW(Angle angle) {
		return getRotatedCW(angle, getCenter());
	}

	@Override
	public Path getRotatedCW(Angle angle, double cx, double cy) {
		return new PolyBezier(computeBezierApproximation())
				.rotateCW(angle, cx, cy).toPath();
	}

	@Override
	public Path getRotatedCW(Angle angle, Point center) {
		return new PolyBezier(computeBezierApproximation())
				.rotateCW(angle, center).toPath();
	}

	/**
	 * @see IGeometry#getTransformed(AffineTransform)
	 */
	@Override
	public CurvedPolygon getTransformed(AffineTransform t) {
		return new CurvedPolygon(getOutlineSegments()).getTransformed(t);
	}

	@Override
	public Path toPath() {
		CubicCurve[] arc = computeBezierApproximation();
		Line endToMid = new Line(arc[arc.length - 1].getP2(), getCenter());
		Line midToStart = new Line(getCenter(), arc[0].getP1());
		ICurve[] curves = new ICurve[arc.length + 2];
		for (int i = 0; i < arc.length; i++) {
			curves[i] = arc[i];
		}
		curves[arc.length] = endToMid;
		curves[arc.length + 1] = midToStart;
		return CurveUtils.toPath(curves).close();
	}

}
