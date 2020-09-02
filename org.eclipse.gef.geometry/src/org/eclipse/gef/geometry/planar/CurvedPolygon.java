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

import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;

/**
 * A {@link CurvedPolygon} is an {@link IShape} with {@link BezierCurve} edges.
 *
 * @author mwienand
 *
 */
public class CurvedPolygon extends AbstractGeometry
		implements IShape, ITranslatable<CurvedPolygon>,
		IRotatable<CurvedPolygon>, IScalable<CurvedPolygon> {

	private static final long serialVersionUID = 1L;
	private BezierCurve[] edges;

	/**
	 * Constructs a new {@link CurvedPolygon} from the given {@link BezierCurve}
	 * s. Subsequent {@link BezierCurve}s need to be connected with each other
	 * and the closing segment has to be supplied, too, otherwise an
	 * {@link IllegalArgumentException} is thrown.
	 *
	 * @param curvedSides
	 *            the {@link BezierCurve}s representing the edges of the new
	 *            {@link CurvedPolygon}
	 */
	public CurvedPolygon(BezierCurve... curvedSides) {
		edges = new BezierCurve[curvedSides.length];

		for (int i = 0; i < edges.length; i++) {
			BezierCurve c = curvedSides[i];

			// assure that the curves are connected
			if (i == edges.length - 1) {
				if (!c.getP2().equals(edges[0].getP1())) {
					throw new IllegalArgumentException(
							"The last passed-in BezierCurve is not a closing segment. new CurvedPolygon("
									+ Arrays.asList(curvedSides) + ")");
				}
			} else if (!c.getP2().equals(curvedSides[i + 1].getP1())) {
				throw new IllegalArgumentException(
						"Subsequent BezierCurves used to construct a CurvedPolygon need to be connected with each other. The "
								+ i + "th and " + (i + 1)
								+ "th passed-in BezierCurves violate this condition. new CurvedPolygon("
								+ Arrays.asList(curvedSides) + ")");
			}

			edges[i] = c.getCopy();
			edges[i].setP2(
					curvedSides[i == edges.length - 1 ? 0 : i + 1].getP1());
		}
	}

	/**
	 * Constructs a new {@link CurvedPolygon} from the given list of
	 * {@link BezierCurve}s. Subsequent {@link BezierCurve}s need to be
	 * connected with each other and the closing segment has to be supplied,
	 * too, otherwise an {@link IllegalArgumentException} is thrown.
	 *
	 * @param curvedSides
	 *            the {@link BezierCurve}s representing the edges of the new
	 *            {@link CurvedPolygon}
	 *
	 * @see #CurvedPolygon(BezierCurve...)
	 */
	public CurvedPolygon(List<BezierCurve> curvedSides) {
		this(curvedSides.toArray(new BezierCurve[] {}));
	}

	private int computeLineWindingNumber(BezierCurve seg, Point p) {
		// seg left of p?
		double sx = seg.getX1();
		double ex = seg.getX2();
		if (sx < p.x && ex < p.x) {
			return 0;
		}

		// seg below or above p?
		double sy = seg.getY1();
		double ey = seg.getY2();
		if (sy < p.y && ey < p.y) {
			return 0;
		}
		if (sy > p.y && ey > p.y) {
			return 0;
		}

		// static x or y?
		if (sx == ex) {
			return ey >= sy ? 1 : -1;
		}
		if (sy == ey) {
			return 0;
		}

		// compute intersection
		double m = (ey - sy) / (ex - sx);
		double xi = (p.y - sy + m * sx) / m;

		// intersection left of p?
		if (p.x > xi) {
			return 0;
		}

		return ey >= sy ? 1 : -1;
	}

	/*
	 * Computes the winding number for the Point relative to the BezierCurve. A
	 * ray is cast from the Point, to the right, parallel to the x-axis. If the
	 * BezierCurve crosses that ray from top to bottom, the winding number is
	 * +1. If it crosses the ray from bottom to top, the winding number is -1.
	 * Otherwise, 0 is returned.
	 *
	 * The algorithm implemented here is a generalized version of the one
	 * realized within the sun.awt.geom.Curve#pointCrossingsFor*() methods.
	 */
	private int computeWindingNumber(BezierCurve seg, Point p) {
		if (isLinear(seg)) {
			int lineWindingNumber = computeLineWindingNumber(seg, p);
			// System.out.println("lineWindingNumber = " + lineWindingNumber);
			return lineWindingNumber;
		}

		// if the BezierCurve is left of p, above, or below p, than we can
		// unworried return 0
		if (isLeftOfP(seg, p) || isAboveP(seg, p) || isBelowP(seg, p)) {
			return 0;
		}

		// if the BezierCurve is right of p, we have to check for a crossing
		if (isRightEqualP(seg, p)) {
			if (p.y >= seg.getY1() && p.y < seg.getY2()) {
				return 1;
			}
			if (p.y < seg.getY1() && p.y >= seg.getY2()) {
				return -1;
			}
			return 0;
		}

		// none of the return conditions evaluates to true, so we have to
		// subdivide the BezierCurve and check the left and right parts
		// individually
		BezierCurve[] split = seg.split(0.5);

		return computeWindingNumber(split[0], p)
				+ computeWindingNumber(split[1], p);
	}

	@Override
	public boolean contains(IGeometry g) {
		return ShapeUtils.contains(this, g);
	}

	@Override
	public boolean contains(Point p) {
		if (edges.length == 0) {
			return false;
		} else if (edges.length == 1) {
			return edges[0].contains(p);
		}

		// compute the winding number for the given Point
		int w = 0;
		for (BezierCurve seg : edges) {
			if (seg.contains(p)) {
				return true;
			}
			w += computeWindingNumber(seg, p);
		}

		// the winding number is 0 if the Point is outside of this CurvedPolygon
		return w != 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof CurvedPolygon) {
			CurvedPolygon p = (CurvedPolygon) o;
			return getOutline().equals(p.getOutline());
		}
		return false;
	}

	@Override
	public Rectangle getBounds() {
		if (edges == null || edges.length == 0) {
			return new Rectangle();
		}

		Rectangle bounds = edges[0].getBounds();
		for (int i = 1; i < edges.length; i++) {
			bounds.union(edges[i].getBounds());
		}

		return bounds;
	}

	/**
	 * Returns an approximated center {@link Point} of this
	 * {@link CurvedPolygon}.
	 *
	 * @return an approximated center {@link Point} of this
	 *         {@link CurvedPolygon}
	 */
	public Point getCenter() {
		Point[] edgeCenters = new Point[edges.length];
		for (int i = 0; i < edges.length; i++) {
			edgeCenters[i] = Point.getCentroid(edges[i].getPoints());
		}
		return Point.getCentroid(edgeCenters);
	}

	@Override
	public CurvedPolygon getCopy() {
		return new CurvedPolygon(edges);
	}

	@Override
	public PolyBezier getOutline() {
		return new PolyBezier(edges);
	}

	@Override
	public BezierCurve[] getOutlineSegments() {
		return CurveUtils.getCopy(edges);
	}

	@Override
	public CurvedPolygon getRotatedCCW(Angle angle) {
		Point c = getCenter();
		return getRotatedCCW(angle, c.x, c.y);
	}

	@Override
	public CurvedPolygon getRotatedCCW(Angle angle, double cx, double cy) {
		return getCopy().rotateCCW(angle, cx, cy);
	}

	@Override
	public CurvedPolygon getRotatedCCW(Angle angle, Point center) {
		return getRotatedCCW(angle, center.x, center.y);
	}

	@Override
	public CurvedPolygon getRotatedCW(Angle angle) {
		Point c = getCenter();
		return getRotatedCW(angle, c.x, c.y);
	}

	@Override
	public CurvedPolygon getRotatedCW(Angle angle, double cx, double cy) {
		return getCopy().rotateCW(angle, cx, cy);
	}

	@Override
	public CurvedPolygon getRotatedCW(Angle angle, Point center) {
		return getRotatedCW(angle, center.x, center.y);
	}

	@Override
	public CurvedPolygon getScaled(double factor) {
		Point c = getCenter();
		return getScaled(factor, factor, c.x, c.y);
	}

	@Override
	public CurvedPolygon getScaled(double fx, double fy) {
		Point c = getCenter();
		return getScaled(fx, fy, c.x, c.y);
	}

	@Override
	public CurvedPolygon getScaled(double factor, double cx, double cy) {
		return getScaled(factor, factor, cx, cy);
	}

	@Override
	public CurvedPolygon getScaled(double fx, double fy, double cx, double cy) {
		return getCopy().scale(fx, fy, cx, cy);
	}

	@Override
	public CurvedPolygon getScaled(double fx, double fy, Point center) {
		return getScaled(fx, fy, center.x, center.y);
	}

	@Override
	public CurvedPolygon getScaled(double factor, Point center) {
		return getScaled(factor, factor, center.x, center.y);
	}

	/**
	 * @see IGeometry#getTransformed(AffineTransform)
	 */
	@Override
	public CurvedPolygon getTransformed(AffineTransform t) {
		BezierCurve[] transformed = new BezierCurve[edges.length];
		for (int i = 0; i < edges.length; i++) {
			transformed[i] = edges[i].getTransformed(t);
		}
		return new CurvedPolygon(transformed);
	}

	@Override
	public CurvedPolygon getTranslated(double dx, double dy) {
		return getCopy().translate(dx, dy);
	}

	@Override
	public CurvedPolygon getTranslated(Point d) {
		return getTranslated(d.x, d.y);
	}

	private boolean isAboveP(BezierCurve seg, Point p) {
		for (Point cp : seg.getPoints()) {
			if (cp.y >= p.y) {
				return false;
			}
		}
		return true;
	}

	private boolean isBelowP(BezierCurve seg, Point p) {
		for (Point cp : seg.getPoints()) {
			if (cp.y <= p.y) {
				return false;
			}
		}
		return true;
	}

	private boolean isLeftOfP(BezierCurve seg, Point p) {
		for (Point cp : seg.getPoints()) {
			if (cp.x >= p.x) {
				return false;
			}
		}
		return true;
	}

	private boolean isLinear(BezierCurve seg) {
		double d0 = seg.getP1().getDistance(seg.getP2());
		double d1 = 0;
		Point[] points = seg.getPoints();
		for (int i = 0; i < points.length - 1; i++) {
			d1 += points[i].getDistance(points[i + 1]);
		}
		return PrecisionUtils.greaterEqual(d0, d1);
	}

	private boolean isRightEqualP(BezierCurve seg, Point p) {
		for (Point cp : seg.getPoints()) {
			if (cp.x < p.x) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Rotates this {@link CurvedPolygon} counter-clockwise (CCW) by the given
	 * {@link Angle} around the center {@link Point} of this
	 * {@link CurvedPolygon}.
	 *
	 * @param angle
	 *            the rotation {@link Angle}
	 * @return <code>this</code> for convenience
	 */
	public CurvedPolygon rotateCCW(Angle angle) {
		Point c = getCenter();
		return rotateCCW(angle, c.x, c.y);
	}

	/**
	 * Rotates this {@link CurvedPolygon} counter-clockwise (CCW) by the given
	 * {@link Angle} around the {@link Point} specified by the given x and y
	 * coordinates.
	 *
	 * @param angle
	 *            the rotation {@link Angle}
	 * @param cx
	 *            the x coordinate of the {@link Point} to rotate around
	 * @param cy
	 *            the y coordinate of the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public CurvedPolygon rotateCCW(Angle angle, double cx, double cy) {
		for (BezierCurve c : edges) {
			c.rotateCCW(angle, cx, cy);
		}
		return this;
	}

	/**
	 * Rotates this {@link CurvedPolygon} counter-clockwise (CCW) by the given
	 * {@link Angle} around the given {@link Point}.
	 *
	 * @param angle
	 *            the rotation {@link Angle}
	 * @param center
	 *            the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public CurvedPolygon rotateCCW(Angle angle, Point center) {
		return rotateCCW(angle, center.x, center.y);
	}

	/**
	 * Rotates this {@link CurvedPolygon} counter-clockwise (CCW) by the given
	 * {@link Angle} around the center {@link Point} of this
	 * {@link CurvedPolygon}.
	 *
	 * @param angle
	 *            the rotation {@link Angle}
	 * @return <code>this</code> for convenience
	 */
	public CurvedPolygon rotateCW(Angle angle) {
		Point c = getCenter();
		return rotateCW(angle, c.x, c.y);
	}

	/**
	 * Rotates this {@link CurvedPolygon} clockwise (CW) by the given
	 * {@link Angle} around the {@link Point} specified by the given x and y
	 * coordinates.
	 *
	 * @param angle
	 *            the rotation {@link Angle}
	 * @param cx
	 *            the x coordinate of the {@link Point} to rotate around
	 * @param cy
	 *            the y coordinate of the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public CurvedPolygon rotateCW(Angle angle, double cx, double cy) {
		for (BezierCurve c : edges) {
			c.rotateCW(angle, cx, cy);
		}
		return this;
	}

	/**
	 * Rotates this {@link CurvedPolygon} clockwise (CW) by the given
	 * {@link Angle} around the given {@link Point}.
	 *
	 * @param angle
	 *            the rotation {@link Angle}
	 * @param center
	 *            the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public CurvedPolygon rotateCW(Angle angle, Point center) {
		return rotateCW(angle, center.x, center.y);
	}

	@Override
	public CurvedPolygon scale(double factor) {
		Point c = getCenter();
		return scale(factor, factor, c.x, c.y);
	}

	@Override
	public CurvedPolygon scale(double fx, double fy) {
		Point c = getCenter();
		return scale(fx, fx, c.x, c.y);
	}

	@Override
	public CurvedPolygon scale(double factor, double cx, double cy) {
		return scale(factor, factor, cx, cy);
	}

	@Override
	public CurvedPolygon scale(double fx, double fy, double cx, double cy) {
		for (BezierCurve c : edges) {
			c.scale(fx, fy, cx, cy);
		}
		return this;
	}

	@Override
	public CurvedPolygon scale(double fx, double fy, Point center) {
		return scale(fx, fx, center.x, center.y);
	}

	@Override
	public CurvedPolygon scale(double factor, Point center) {
		return scale(factor, factor, center.x, center.y);
	}

	@Override
	public Path toPath() {
		return CurveUtils.toPath(edges).close();
	}

	@Override
	public String toString() {
		String s = "CurvedPolygon(";
		for (int i = 0; i < edges.length; i++) {
			if (i == edges.length - 1) {
				s = s + edges[i];
			} else {
				s = s + edges[i] + " -> ";
			}
		}
		return s + ")";
	}

	@Override
	public CurvedPolygon translate(double dx, double dy) {
		for (BezierCurve c : edges) {
			c.translate(dx, dy);
		}
		return this;
	}

	@Override
	public CurvedPolygon translate(Point d) {
		return translate(d.x, d.y);
	}

}
