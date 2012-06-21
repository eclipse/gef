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

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.utils.CurveUtils;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * The {@link Pie} is a closed {@link AbstractArcBasedGeometry}. It is the
 * complement of the {@link Arc}, which is an open
 * {@link AbstractArcBasedGeometry}.
 * 
 * The {@link Pie} covers an area, therefore it implements the {@link IShape}
 * interface.
 */
public class Pie extends AbstractArcBasedGeometry<Pie, Path> implements IShape {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@link Pie} from the given values.
	 * 
	 * @see AbstractArcBasedGeometry#AbstractArcBasedGeometry(double, double,
	 *      double, double, Angle, Angle)
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param startAngle
	 * @param angularExtent
	 */
	public Pie(double x, double y, double width, double height,
			Angle startAngle, Angle angularExtent) {
		super(x, y, width, height, startAngle, angularExtent);
	}

	/**
	 * Constructs a new {@link Pie} from the given values.
	 * 
	 * @param r
	 * @param startAngle
	 * @param angularExtent
	 */
	public Pie(AbstractRectangleBasedGeometry<?, ?> r, Angle startAngle,
			Angle angularExtent) {
		super(r.x, r.y, r.width, r.height, startAngle, angularExtent);
	}

	/**
	 * Constructs a new {@link Pie} from the given {@link Arc}.
	 * 
	 * @param arc
	 */
	public Pie(Arc arc) {
		super(arc.x, arc.y, arc.width, arc.height, arc.startAngle,
				arc.angularExtent);
	}

	/*
	 * TODO: Add additional methods to rotate a Pie so that it remains a Pie.
	 */

	/**
	 * @see org.eclipse.gef4.geometry.planar.IGeometry#getCopy()
	 */
	public Pie getCopy() {
		return new Pie(x, y, width, height, startAngle, angularExtent);
	}

	public PolyBezier getOutline() {
		return new PolyBezier(computeBezierApproximation());
	}

	public CubicCurve[] getOutlineSegments() {
		return computeBezierApproximation();
	}

	public boolean contains(Point p) {
		// check if the point is in the arc's angle
		Angle pAngle = new Vector(1, 0).getAngleCCW(new Vector(getCenter(), p));
		if (!(PrecisionUtils.greater(pAngle.rad(), startAngle.rad()) && PrecisionUtils
				.smaller(pAngle.rad(), startAngle.getAdded(angularExtent).rad()))) {
			return false;
		}

		// angle is correct, check if the point is inside the bounding ellipse
		return new Ellipse(x, y, width, height).contains(p);
	}

	public boolean contains(IGeometry g) {
		return CurveUtils.contains(this, g);
	}

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
		return CurveUtils.toPath(curves);
	}

	public Path getRotatedCCW(Angle angle, double cx, double cy) {
		return new PolyBezier(computeBezierApproximation()).rotateCCW(angle,
				cx, cy).toPath();
	}

	public Path getRotatedCCW(Angle angle, Point center) {
		return new PolyBezier(computeBezierApproximation()).rotateCCW(angle,
				center).toPath();
	}

	public Path getRotatedCW(Angle angle, double cx, double cy) {
		return new PolyBezier(computeBezierApproximation()).rotateCW(angle, cx,
				cy).toPath();
	}

	public Path getRotatedCW(Angle angle, Point center) {
		return new PolyBezier(computeBezierApproximation()).rotateCW(angle,
				center).toPath();
	}

	public Path getRotatedCCW(Angle angle) {
		return getRotatedCCW(angle, getCenter());
	}

	public Path getRotatedCW(Angle angle) {
		return getRotatedCW(angle, getCenter());
	}

}
