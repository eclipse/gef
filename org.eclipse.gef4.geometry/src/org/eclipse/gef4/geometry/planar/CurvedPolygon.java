/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * A {@link CurvedPolygon} is an {@link IShape} with {@link BezierCurve} edges.
 * 
 * @author mwienand
 * 
 */
public class CurvedPolygon extends AbstractGeometry implements IShape {

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
									+ curvedSides + ")");
				}
			} else if (!c.getP2().equals(curvedSides[i + 1].getP1())) {
				throw new IllegalArgumentException(
						"Subsequent BezierCurves used to construct a CurvedPolygon need to be connected with each other. The "
								+ i
								+ "th and "
								+ (i + 1)
								+ "th passed-in BezierCurves violate this condition. new CurvedPolygon("
								+ curvedSides + ")");
			}

			edges[i] = c.getCopy();
			edges[i].setP2(curvedSides[i == edges.length - 1 ? 0 : i + 1]
					.getP1());
		}
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

	public boolean contains(IGeometry g) {
		return ShapeUtils.contains(this, g);
	}

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

	public Rectangle getBounds() {
		Rectangle bounds = new Rectangle();
		for (BezierCurve c : edges) {
			bounds.union(c.getBounds());
		}
		return bounds;
	}

	public CurvedPolygon getCopy() {
		return new CurvedPolygon(edges);
	}

	public PolyBezier getOutline() {
		return new PolyBezier(edges);
	}

	public BezierCurve[] getOutlineSegments() {
		return CurveUtils.getCopy(edges);
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

	public Path toPath() {
		return CurveUtils.toPath(edges);
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

}
