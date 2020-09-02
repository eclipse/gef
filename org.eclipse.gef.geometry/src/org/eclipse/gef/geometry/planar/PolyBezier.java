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
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.planar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.internal.utils.PointListUtils;

/**
 * A {@link PolyBezier} is an {@link ICurve} which consists of one or more
 * connected {@link BezierCurve}s.
 *
 * @author mwienand
 *
 */
public class PolyBezier extends AbstractGeometry
		implements ICurve, ITranslatable<PolyBezier>, IScalable<PolyBezier>,
		IRotatable<PolyBezier> {

	/**
	 * The default curve width coefficient used in interpolateCubic(...)
	 * methods.
	 */
	public static final double INTERPOLATION_CURVE_WIDTH_COEFFICIENT = 1d;

	private static final long serialVersionUID = 1L;

	private static BezierCurve[] copy(BezierCurve... beziers) {
		BezierCurve[] copy = new BezierCurve[beziers.length];

		for (int i = 0; i < beziers.length; i++) {
			copy[i] = beziers[i].getCopy();
		}

		return copy;
	}

	/**
	 * <p>
	 * Creates a {@link PolyBezier} consisting of continuous {@link CubicCurve}
	 * segments through the given {@link Point}s.
	 * </p>
	 * <p>
	 * The start angle and start length constrain the curve. Way points are
	 * added to assure the curve starts off with a straight line of said length
	 * in the direction specified by said angle.
	 * </p>
	 * <p>
	 * The end angle and end length constrain the curve. Way points are added to
	 * assure the curve ends in a straight line of said length in the direction
	 * specified by said angle.
	 * </p>
	 *
	 * @param startAngle
	 *            start direction of the curve
	 * @param startLength
	 *            length of the straight start segment of the curve
	 * @param endAngle
	 *            end direction of the curve
	 * @param endLength
	 *            length of the straight end segment of the curve
	 * @param curveWidthCoefficient
	 *            value in the range <code>]0;+Inf[</code> that adjusts the
	 *            width of the curve. A value smaller than 1 sharpens the curve
	 *            and a value greater than 1 thickens the curve.
	 * @param wayPoints
	 *            The array of {@link Point}s which lie on the resulting
	 *            {@link PolyBezier}.
	 * @return {@link PolyBezier} consisting of continuous {@link CubicCurve}
	 *         segments through the given {@link Point}s
	 */
	public static PolyBezier interpolateCubic(Angle startAngle,
			double startLength, Angle endAngle, double endLength,
			double curveWidthCoefficient, Point... wayPoints) {
		// TODO: alter way points => leave space for start/end segments
		PolyBezier interpolation = interpolateCubic(curveWidthCoefficient,
				wayPoints);
		// TODO: compute cubic curves for start/end segments
		return interpolation;
	}

	/**
	 * Creates a {@link PolyBezier} with continuous {@link CubicCurve} segments
	 * through the given {@link Point}s.
	 *
	 * @see #interpolateCubic(Point...)
	 * @param coordinates
	 *            the coordinates of the points that are to be interpolated.
	 * @return {@link PolyBezier} with continuous {@link CubicCurve} segments
	 *         through the points, specified via the given coordinates.
	 */
	public static PolyBezier interpolateCubic(double... coordinates) {
		Point[] points = new Point[coordinates.length / 2];
		for (int i = 0; i < coordinates.length / 2; i++) {
			points[i] = new Point(coordinates[i * 2], coordinates[i * 2 + 1]);
		}
		return interpolateCubic(points);
	}

	/**
	 * Creates a {@link PolyBezier} with continuous {@link CubicCurve} segments
	 * through the given {@link Point}s.
	 *
	 * @param curveWidthCoefficient
	 *            value in the range <code>]0;+Inf[</code> that adjusts the
	 *            width of the curve. A value smaller than one sharpens the
	 *            curve and a value greater than one thickens the curve.
	 * @param points
	 *            The array of {@link Point}s which lie on the resulting
	 *            {@link PolyBezier}.
	 * @return {@link PolyBezier} with continuous {@link CubicCurve} segments
	 *         through the given {@link Point}s.
	 */
	public static PolyBezier interpolateCubic(double curveWidthCoefficient,
			Point... points) {
		if (points == null || points.length < 2) {
			throw new IllegalArgumentException(
					"Not enough points specified (at least 2 required).");
		} else if (points.length == 2) {
			return new PolyBezier(new Line(points[0], points[1]));
		}

		/*
		 * Computes the control points for the cubic bezier curve. The algorithm
		 * is based on what has been published by Maxim Shemanarev for Polygons
		 * (http://www.antigrain.com/research/bezier_interpolation/index.html)
		 * with a modified calculation of the first and the last control points,
		 * so that it can be applied to Polylines.
		 */
		Point[] mids = new Point[points.length - 1];
		for (int i = 0; i < mids.length; i++) {
			mids[i] = points[i].getTranslated(points[i + 1]).getScaled(0.5);
		}

		Line[] lines = PointListUtils.toSegmentsArray(points, true);
		Line[] handleLines = PointListUtils.toSegmentsArray(mids, true);

		Point[] handleAnchors = new Point[handleLines.length];
		for (int i = 0; i < handleLines.length; i++) {
			double d0 = lines[i].getP1().getDistance(lines[i].getP2());
			double d1 = lines[i + 1].getP1().getDistance(lines[i + 1].getP2());
			if (d0 + d1 == 0) {
				d0 = 1;
			}
			handleAnchors[i] = handleLines[i].get(d0 / (d0 + d1));
		}

		for (int i = 0; i < handleLines.length; i++) {
			handleLines[i].scale(curveWidthCoefficient, handleAnchors[i]);
			handleLines[i].translate(points[i + 1].x - handleAnchors[i].x,
					points[i + 1].y - handleAnchors[i].y);
		}

		CubicCurve[] interpolation = new CubicCurve[handleLines.length];

		interpolation[0] = new CubicCurve(points[0], handleLines[0].getP1(),
				points[1], points[1]);

		interpolation[interpolation.length - 1] = new CubicCurve(
				points[points.length - 2],
				handleLines[handleLines.length - 2].getP2(),
				points[points.length - 1], points[points.length - 1]);

		for (int i = 1; i < interpolation.length - 1; i++) {
			interpolation[i] = new CubicCurve(points[i],
					handleLines[i - 1].getP2(), handleLines[i].getP1(),
					points[i + 1]);
		}

		return new PolyBezier(interpolation);
	}

	/**
	 * @see #interpolateCubic(double, Point...)
	 * @param points
	 *            The array of {@link Point}s which lie on the resulting
	 *            {@link PolyBezier}.
	 * @return {@link PolyBezier} with continuous {@link CubicCurve} segments
	 *         through the given {@link Point}s.
	 */
	public static PolyBezier interpolateCubic(Point... points) {
		PolyBezier interp = interpolateCubic(
				INTERPOLATION_CURVE_WIDTH_COEFFICIENT, points);
		return interp;
	}

	private BezierCurve[] beziers;

	/**
	 * Constructs a new {@link PolyBezier} of the given {@link BezierCurve}s.
	 * The {@link BezierCurve}s are expected to be connected with each other.
	 *
	 * @param beziers
	 *            the {@link BezierCurve}s which will constitute this
	 *            {@link PolyBezier}
	 */
	public PolyBezier(BezierCurve... beziers) {
		this.beziers = copy(beziers);
	}

	@Override
	public boolean contains(Point p) {
		for (BezierCurve c : beziers) {
			if (c.contains(p)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof PolyBezier) {
			PolyBezier p = (PolyBezier) o;
			return toPath().equals(p.toPath());
		}
		return false;
	}

	@Override
	public Rectangle getBounds() {
		if (beziers == null || beziers.length == 0) {
			return new Rectangle();
		}

		Rectangle bounds = beziers[0].getBounds();

		for (BezierCurve c : beziers) {
			bounds.union(c.getBounds());
		}

		return bounds;
	}

	@Override
	public PolyBezier getCopy() {
		return new PolyBezier(beziers);
	}

	@Override
	public Point[] getIntersections(ICurve c) {
		return CurveUtils.getIntersections(this, c);
	}

	@Override

	public ICurve[] getOverlaps(ICurve c) {
		return CurveUtils.getOverlaps(this, c);
	}

	@Override
	public Point getP1() {
		return beziers[0].getP1();
	}

	@Override
	public Point getP2() {
		return beziers[beziers.length - 1].getP2();
	}

	@Override
	public Point getProjection(Point reference) {
		double minDistance = 0;
		Point minProjection = null;
		for (BezierCurve bc : beziers) {
			Point projection = bc.getProjection(reference);
			double distance = projection.getDistance(reference);
			if (minProjection == null || distance < minDistance) {
				minProjection = projection;
				minDistance = distance;
			}
		}
		return minProjection;
	}

	@Override
	public PolyBezier getRotatedCCW(Angle angle) {
		return getCopy().rotateCCW(angle);
	}

	@Override
	public PolyBezier getRotatedCCW(Angle angle, double cx, double cy) {
		return getCopy().getRotatedCCW(angle, cx, cy);
	}

	@Override
	public PolyBezier getRotatedCCW(Angle angle, Point center) {
		return getCopy().getRotatedCCW(angle, center);
	}

	@Override
	public PolyBezier getRotatedCW(Angle angle) {
		return getCopy().getRotatedCW(angle);
	}

	@Override
	public PolyBezier getRotatedCW(Angle angle, double cx, double cy) {
		return getCopy().getRotatedCW(angle, cx, cy);
	}

	@Override
	public PolyBezier getRotatedCW(Angle angle, Point center) {
		return getCopy().getRotatedCW(angle, center);
	}

	@Override
	public PolyBezier getScaled(double factor) {
		return getCopy().scale(factor);
	}

	@Override
	public PolyBezier getScaled(double fx, double fy) {
		return getCopy().scale(fx, fy);
	}

	@Override
	public PolyBezier getScaled(double factor, double cx, double cy) {
		return getCopy().scale(factor, cx, cy);
	}

	@Override
	public PolyBezier getScaled(double fx, double fy, double cx, double cy) {
		return getCopy().scale(fx, fy, cx, cy);
	}

	@Override
	public PolyBezier getScaled(double fx, double fy, Point center) {
		return getCopy().scale(fx, fy, center);
	}

	@Override
	public PolyBezier getScaled(double factor, Point center) {
		return getCopy().scale(factor, center);
	}

	@Override
	public PolyBezier getTransformed(AffineTransform t) {
		List<BezierCurve> transformedCurves = new ArrayList<>();
		for (BezierCurve c : beziers) {
			transformedCurves.add(c.getTransformed(t));
		}
		return new PolyBezier(transformedCurves.toArray(new BezierCurve[] {}));
	}

	@Override
	public PolyBezier getTranslated(double dx, double dy) {
		return getCopy().translate(dx, dy);
	}

	@Override
	public PolyBezier getTranslated(Point d) {
		return getCopy().translate(d.x, d.y);
	}

	@Override
	public double getX1() {
		return getP1().x;
	}

	@Override
	public double getX2() {
		return getP2().x;
	}

	@Override
	public double getY1() {
		return getP1().y;
	}

	@Override
	public double getY2() {
		return getP2().y;
	}

	@Override
	public boolean intersects(ICurve c) {
		return CurveUtils.intersect(c, this);
	}

	@Override
	public boolean overlaps(ICurve c) {
		return CurveUtils.overlap(c, this);
	}

	/**
	 * Directly rotates this {@link PolyBezier} counter-clock-wise around its
	 * center {@link Point} by the given {@link Angle}. Direct adaptation means,
	 * that <code>this</code> {@link PolyBezier} is modified in-place.
	 *
	 * @param angle
	 *            rotation {@link Angle}
	 * @return <code>this</code> for convenience
	 */
	public PolyBezier rotateCCW(Angle angle) {
		ArrayList<Point> points = new ArrayList<>();
		for (BezierCurve c : beziers) {
			points.addAll(Arrays.asList(c.getPoints()));
		}
		Point centroid = Point.getCentroid(points.toArray(new Point[] {}));
		return rotateCCW(angle, centroid.x, centroid.y);
	}

	/**
	 * Directly rotates this {@link PolyBezier} counter-clock-wise around the
	 * given point (specified by cx and cy) by the given {@link Angle}. Direct
	 * adaptation means, that <code>this</code> {@link PolyBezier} is modified
	 * in-place.
	 *
	 * @param angle
	 *            rotation {@link Angle}
	 * @param cx
	 *            x-coordinate of the {@link Point} to rotate around
	 * @param cy
	 *            y-coordinate of the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public PolyBezier rotateCCW(Angle angle, double cx, double cy) {
		for (BezierCurve c : beziers) {
			c.rotateCCW(angle, cx, cy);
		}
		return this;
	}

	/**
	 * Directly rotates this {@link PolyBezier} counter-clock-wise around the
	 * given {@link Point} by the given {@link Angle}. Direct adaptation means,
	 * that <code>this</code> {@link PolyBezier} is modified in-place.
	 *
	 * @param angle
	 *            rotation {@link Angle}
	 * @param center
	 *            {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public PolyBezier rotateCCW(Angle angle, Point center) {
		return rotateCCW(angle, center.x, center.y);
	}

	/**
	 * Directly rotates this {@link PolyBezier} clock-wise around its center
	 * {@link Point} by the given {@link Angle}. Direct adaptation means, that
	 * <code>this</code> {@link PolyBezier} is modified in-place.
	 *
	 * @param angle
	 *            rotation {@link Angle}
	 * @return <code>this</code> for convenience
	 */
	public PolyBezier rotateCW(Angle angle) {
		ArrayList<Point> points = new ArrayList<>();
		for (BezierCurve c : beziers) {
			points.addAll(Arrays.asList(c.getPoints()));
		}
		Point centroid = Point.getCentroid(points.toArray(new Point[] {}));
		return rotateCW(angle, centroid.x, centroid.y);
	}

	/**
	 * Directly rotates this {@link PolyBezier} clock-wise around the given
	 * point (specified by cx and cy) by the given {@link Angle}. Direct
	 * adaptation means, that <code>this</code> {@link PolyBezier} is modified
	 * in-place.
	 *
	 * @param angle
	 *            rotation {@link Angle}
	 * @param cx
	 *            x-coordinate of the {@link Point} to rotate around
	 * @param cy
	 *            y-coordinate of the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public PolyBezier rotateCW(Angle angle, double cx, double cy) {
		for (BezierCurve c : beziers) {
			c.rotateCW(angle, cx, cy);
		}
		return this;
	}

	/**
	 * Directly rotates this {@link PolyBezier} clock-wise around the given
	 * {@link Point} by the given {@link Angle}. Direct adaptation means, that
	 * <code>this</code> {@link PolyBezier} is modified in-place.
	 *
	 * @param angle
	 *            rotation {@link Angle}
	 * @param center
	 *            {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public PolyBezier rotateCW(Angle angle, Point center) {
		return rotateCW(angle, center.x, center.y);
	}

	@Override
	public PolyBezier scale(double factor) {
		return scale(factor, factor);
	}

	@Override
	public PolyBezier scale(double fx, double fy) {
		ArrayList<Point> points = new ArrayList<>();
		for (BezierCurve c : beziers) {
			points.addAll(Arrays.asList(c.getPoints()));
		}
		Point centroid = Point.getCentroid(points.toArray(new Point[] {}));
		return scale(fx, fy, centroid.x, centroid.y);
	}

	@Override
	public PolyBezier scale(double factor, double cx, double cy) {
		return scale(factor, factor, cx, cy);
	}

	@Override
	public PolyBezier scale(double fx, double fy, double cx, double cy) {
		for (BezierCurve c : beziers) {
			c.scale(fx, fy, cx, cy);
		}
		return this;
	}

	@Override
	public PolyBezier scale(double fx, double fy, Point center) {
		return scale(fx, fx, center.x, center.y);
	}

	@Override
	public PolyBezier scale(double factor, Point center) {
		return scale(factor, factor, center.x, center.y);
	}

	@Override
	public BezierCurve[] toBezier() {
		return copy(beziers);
	}

	@Override
	public Path toPath() {
		return CurveUtils.toPath(beziers);
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("PolyBezier(");
		for (int i = 0; i < beziers.length; i++) {
			str.append(beziers[i].toString());
			if (i < beziers.length - 1) {
				str.append(", ");
			}
		}
		return str + ")";
	}

	@Override
	public PolyBezier translate(double dx, double dy) {
		for (BezierCurve c : beziers) {
			c.translate(dx, dy);
		}
		return this;
	}

	@Override
	public PolyBezier translate(Point d) {
		return translate(d.x, d.y);
	}
}
