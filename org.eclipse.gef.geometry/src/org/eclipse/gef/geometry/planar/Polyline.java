/*******************************************************************************
 * Copyright (c) 2011, 2016 itemis AG and others.
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

import java.util.Arrays;

import org.eclipse.gef.geometry.internal.utils.PointListUtils;
import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;

/**
 * Represents the geometric shape of a polyline.
 *
 * Note that while all manipulations (e.g. within shrink, expand) within this
 * class are based on double precision, all comparisons (e.g. within contains,
 * intersects, equals, etc.) are based on a limited precision (with an accuracy
 * defined within {@link PrecisionUtils}) to compensate for rounding effects.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class Polyline extends AbstractPointListBasedGeometry<Polyline>
		implements ICurve {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@link Polyline} from a even-numbered sequence of
	 * coordinates. Similar to {@link Polyline#Polyline(Point...)}, only that
	 * coordinates of points rather than {@link Point}s are provided.
	 *
	 * @param coordinates
	 *            an alternating, even-numbered sequence of x- and
	 *            y-coordinates, representing the points from which the
	 *            {@link Polyline} is to be created
	 */
	public Polyline(double... coordinates) {
		super(coordinates);
	}

	/**
	 * Constructs a new {@link Polyline} from the given array of {@link Line}
	 * segments.
	 *
	 * @param segmentsArray
	 *            The array of {@link Line}s from which this {@link Polyline} is
	 *            constructed.
	 */
	public Polyline(Line[] segmentsArray) {
		super(PointListUtils.toPointsArray(segmentsArray, false));
	}

	/**
	 * Constructs a new {@link Polyline} from the given sequence of
	 * {@link Point} s. The {@link Polyline} that is created will be
	 * automatically closed, i.e. it will not only contain a segment between
	 * succeeding points of the sequence but as well back from the last to the
	 * first point.
	 *
	 * @param points
	 *            a sequence of points, from which the {@link Polyline} is to be
	 *            created
	 */
	public Polyline(Point... points) {
		super(points);
	}

	/**
	 * Checks whether the point that is represented by its x- and y-coordinates
	 * is contained within this {@link Polyline}.
	 *
	 * @param x
	 *            the x coordinate of the point to test
	 * @param y
	 *            the y coordinate of the point to test
	 * @return <code>true</code> if the point represented by its coordinates if
	 *         contained within this {@link Polyline}, otherwise
	 *         <code>false</code>
	 */
	public boolean contains(double x, double y) {
		return contains(new Point(x, y));
	}

	@Override
	public boolean contains(Point p) {
		for (int i = 0; i + 1 < points.length; i++) {
			Point p1 = points[i];
			Point p2 = points[i + 1];
			if (new Line(p1, p2).contains(p)) {
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
		if (o instanceof Polyline) {
			Polyline p = (Polyline) o;
			return equals(p.getPoints());
		}
		return false;
	}

	/**
	 * Checks whether this {@link Polyline} and the one that is indirectly given
	 * via the sequence of points are regarded to be equal. The {@link Polyline}
	 * s will be regarded equal, if they are characterized by the same segments.
	 *
	 * @param points
	 *            an array of {@link Point} characterizing a {@link Polyline} to
	 *            be checked for equality
	 * @return <code>true</code> if the sequence of points that characterize
	 *         this {@link Polyline} and the {@link Polyline} indirectly given
	 *         via the array of points are regarded to form the same segments
	 */
	public boolean equals(Point... points) {
		if (points.length != this.points.length) {
			return false;
		}
		return Arrays.equals(this.points, points)
				|| Arrays.equals(this.points, Point.getReverseCopy(points));
	}

	@Override
	public Polyline getCopy() {
		return new Polyline(getPoints());
	}

	/**
	 * Returns a sequence of {@link Line}s, representing the segments that are
	 * obtained by linking each two successive point of this {@link Polyline}
	 * (including the last and the first one).
	 *
	 * @return an array of {@link Line}s, representing the segments that make up
	 *         this {@link Polyline}
	 */
	public Line[] getCurves() {
		return PointListUtils.toSegmentsArray(points, false);
	}

	@Override
	public Point[] getIntersections(ICurve c) {
		return CurveUtils.getIntersections(this, c);
	}

	/**
	 * Returns the length of this {@link Polyline} by adding up the lengths of
	 * the individual {@link Line}s.
	 *
	 * @return The length of this {@link Polyline}.
	 */
	// TODO: tests
	public double getLength() {
		double length = 0;
		for (Line line : getCurves()) {
			length += line.getLength();
		}
		return length;
	}

	@Override

	public ICurve[] getOverlaps(ICurve c) {
		return CurveUtils.getOverlaps(this, c);
	}

	@Override
	public Point getP1() {
		return points[0].getCopy();
	}

	@Override
	public Point getP2() {
		return points[points.length - 1].getCopy();
	}

	@Override
	public Point getProjection(Point reference) {
		double minDistance = 0;
		Point minProjection = null;
		for (BezierCurve bc : toBezier()) {
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
	public Polyline getTransformed(AffineTransform t) {
		return new Polyline(t.getTransformed(points));
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

	@Override
	public Line[] toBezier() {
		return PointListUtils.toSegmentsArray(points, false);
	}

	@Override
	public Path toPath() {
		Path path = new Path();
		if (points.length > 0) {
			path.moveTo(points[0].x, points[0].y);
			for (int i = 1; i < points.length; i++) {
				path.lineTo(points[i].x, points[i].y);
			}
		}
		return path;
	}

	/**
	 * Transforms this {@link Polyline} into a {@link PolyBezier}.
	 *
	 * @return a {@link PolyBezier} representing this {@link Polyline}
	 */
	public PolyBezier toPolyBezier() {
		Line[] segments = PointListUtils.toSegmentsArray(points, false);
		return new PolyBezier(segments);
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer("Polyline: ");
		if (points.length > 0) {
			for (int i = 0; i < points.length; i++) {
				stringBuffer
						.append("(" + points[i].x + ", " + points[i].y + ")");
				if (i < points.length - 1) {
					stringBuffer.append(" -> ");
				}
			}
		} else {
			stringBuffer.append("<no points>");
		}
		return stringBuffer.toString();
	}

}
