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
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.planar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;
import org.eclipse.gef.geometry.planar.BezierCurve.IntervalPair;

/**
 * The {@link ShapeUtils} class provides functionality that can be used for all
 * shapes, independent on their construction kind.
 *
 * @author anyssen
 *
 */
class ShapeUtils {

	/**
	 * <p>
	 * Computes a {@link CubicCurve} that approximates the elliptical
	 * {@link Arc} given by the location, the width, and the height of the
	 * implied ellipse and the start and end {@link Angle}s of the arc.
	 * </p>
	 *
	 * <p>
	 * The given start and end {@link Angle}s may not span an {@link Angle} of
	 * more than 90 degrees.
	 * </p>
	 *
	 * @param x
	 *            left coordinate value of the aforementioned ellipse
	 * @param y
	 *            top coordinate value of the aforementioned ellipse
	 * @param width
	 *            width of the aforementioned ellipse
	 * @param height
	 *            height of the aforementioned ellipse
	 * @param start
	 *            start angle (in radiant) of the elliptical arc
	 * @param end
	 *            end angle (in radiant) of the elliptical arc
	 * @return {@link CubicCurve} approximating the determinated elliptical arc
	 */
	public static CubicCurve computeEllipticalArcApproximation(double x,
			double y, double width, double height, Angle start, Angle end) {
		// TODO: verify that the following test is valid
		if (!PrecisionUtils.smallerEqual(
				end.getAdded(start.getOppositeFull()).deg(), 90)) {
			throw new IllegalArgumentException(
					"Only angular extents of up to 90 degrees are allowed.");
		}

		// compute major and minor axis length
		double a = width / 2;
		double b = height / 2;

		double srad = start.rad();
		double erad = end.rad();

		// calculate start and end points of the arc from start to end
		Point startPoint = new Point(x + a + a * Math.cos(srad),
				y + b - b * Math.sin(srad));
		Point endPoint = new Point(x + a + a * Math.cos(erad),
				y + b - b * Math.sin(erad));

		// approximation by cubic Bezier according to approximation provided in:
		// http://www.spaceroots.org/documents/ellipse/elliptical-arc.pdf
		double t = Math.tan((erad - srad) / 2);
		double alpha = Math.sin(erad - srad)
				* (Math.sqrt(4.0d + 3.0d * t * t) - 1) / 3;
		Point controlPoint1 = new Point(
				startPoint.x + alpha * -a * Math.sin(srad),
				startPoint.y - alpha * b * Math.cos(srad));
		Point controlPoint2 = new Point(
				endPoint.x - alpha * -a * Math.sin(erad),
				endPoint.y + alpha * b * Math.cos(erad));

		Point[] points = new Point[] { startPoint, controlPoint1, controlPoint2,
				endPoint };
		return new CubicCurve(points);
	}

	/**
	 * Returns <code>true</code> if the second {@link IGeometry} is fully
	 * contained by the first {@link IGeometry}. Otherwise, <code>false</code>
	 * is returned.
	 *
	 * @param geom1
	 *            The {@link IGeometry} which is tested to contain the given
	 *            other {@link IGeometry}.
	 * @param geom2
	 *            The {@link IGeometry} which is tested for containment.
	 * @return <code>true</code> if the first {@link IGeometry} contains the
	 *         second {@link IGeometry}, otherwise <code>false</code>
	 */
	public static boolean contains(IGeometry geom1, IGeometry geom2) {
		if (geom1 instanceof IShape) {
			return contains((IShape) geom1, geom2);
		} else if (geom1 instanceof IMultiShape) {
			return contains((IMultiShape) geom1, geom2);
		} else {
			return false;
		}
	}

	/**
	 * Returns <code>true</code> if the given {@link BezierCurve} is fully
	 * contained by the given {@link IMultiShape}.
	 *
	 * @param multiShape
	 *            The {@link IMultiShape} which is tested to contain the given
	 *            {@link BezierCurve}.
	 * @param c
	 *            The {@link BezierCurve} which is tested for containment.
	 * @return <code>true</code> if the {@link BezierCurve} is contained by the
	 *         {@link IMultiShape}, otherwise <code>false</code>
	 */
	public static boolean contains(IMultiShape multiShape, BezierCurve c) {
		// TODO: generalize the contains() method for IShape and IMultiShape.
		if (!(multiShape.contains(c.getP1())
				&& multiShape.contains(c.getP2()))) {
			return false;
		}

		Set<Double> intersectionParams = new HashSet<>();

		for (ICurve segC : multiShape.getOutlineSegments()) {
			for (BezierCurve seg : segC.toBezier()) {
				Set<Point> inters = new HashSet<>();
				Set<IntervalPair> ips = c.getIntersectionIntervalPairs(seg,
						inters);
				for (IntervalPair ip : ips) {
					intersectionParams
							.add(ip.p == c ? ip.pi.getMid() : ip.qi.getMid());
				}
				for (Point poi : inters) {
					intersectionParams.add(c.getParameterAt(poi));
				}
			}
		}

		/*
		 * Start and end point of the curve are guaranteed to lie inside the
		 * IMultiShape. If the curve would not be contained by the shape, at
		 * least two intersections could be found.
		 * 
		 * TODO: Special case! There is a special case where the Bezier curve
		 * leaves and enters the shape in the same point. This is only possible
		 * if the Bezier curve has a self intersection at that point.
		 */
		if (intersectionParams.size() <= 1) {
			return true;
		}

		Double[] poiParams = intersectionParams.toArray(new Double[] {});
		Arrays.sort(poiParams, new Comparator<Double>() {
			@Override
			public int compare(Double t, Double u) {
				double d = t - u;
				return d < 0 ? -1 : d > 0 ? 1 : 0;
			}
		});

		// check the points between the intersections for containment
		if (!multiShape.contains(c.get(poiParams[0] / 2))) {
			return false;
		}
		for (int i = 0; i < poiParams.length - 1; i++) {
			if (!multiShape
					.contains(c.get((poiParams[i] + poiParams[i + 1]) / 2))) {
				return false;
			}
		}
		return multiShape
				.contains(c.get((poiParams[poiParams.length - 1] + 1) / 2));
	}

	/**
	 * Checks if the given {@link ICurve} is contained by the given
	 * {@link IMultiShape}.
	 *
	 * @param ps
	 *            The {@link IMultiShape} which is tested to contain the given
	 *            {@link ICurve}.
	 * @param c
	 *            The {@link ICurve} which is tested for containment.
	 * @return <code>true</code> if the {@link ICurve} is contained by the
	 *         {@link IMultiShape}, otherwise <code>false</code>
	 */
	public static boolean contains(IMultiShape ps, ICurve c) {
		for (BezierCurve bc : c.toBezier()) {
			if (!contains(ps, bc)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the {@link IGeometry} is contained by the {@link IMultiShape}.
	 *
	 * @param ps
	 *            The {@link IMultiShape} which is tested to contain the given
	 *            {@link IGeometry}.
	 * @param g
	 *            The {@link IGeometry} which is tested for containment.
	 * @return <code>true</code> if the {@link IGeometry} is contained by the
	 *         {@link IMultiShape}, otherwise <code>false</code>
	 */
	public static boolean contains(IMultiShape ps, IGeometry g) {
		if (g instanceof ICurve) {
			return contains(ps, (ICurve) g);
		} else if (g instanceof IShape) {
			return contains(ps, (IShape) g);
		} else if (g instanceof IMultiShape) {
			return contains(ps, (IMultiShape) g);
		} else {
			throw new UnsupportedOperationException("Not yet implemented.");
		}
	}

	/**
	 * Checks if the second {@link IMultiShape} is contained by the first
	 * {@link IMultiShape}.
	 *
	 * @param ps
	 *            The {@link IMultiShape} which is tested to contain the given
	 *            other {@link IMultiShape}.
	 * @param ps2
	 *            The {@link IMultiShape} which is tested for containment.
	 * @return <code>true</code> if the second {@link IMultiShape} is contained
	 *         by the first {@link IMultiShape}, otherwise <code>false</code>
	 */
	public static boolean contains(IMultiShape ps, IMultiShape ps2) {
		for (IShape s : ps2.getShapes()) {
			if (!contains(ps, s)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the {@link IShape} is contained by the {@link IMultiShape}.
	 *
	 * @param ps
	 *            The {@link IMultiShape} which is tested to contain the given
	 *            {@link IShape}.
	 * @param s
	 *            The {@link IShape} which is tested for containment.
	 * @return <code>true</code> if the {@link IShape} is contained by the
	 *         {@link IMultiShape}, otherwise <code>false</code>
	 */
	public static boolean contains(IMultiShape ps, IShape s) {
		for (ICurve c : s.getOutlineSegments()) {
			if (!contains(ps, c)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>
	 * Tests if the given {@link BezierCurve} is fully contained by the given
	 * {@link IShape}. Returns <code>true</code> if the given
	 * {@link BezierCurve} is fully contained by the given {@link IShape}.
	 * Otherwise, returns <code>false</code>.
	 * </p>
	 *
	 * <p>
	 * At first, the algorithm checks if start and end {@link Point} of the
	 * {@link BezierCurve} are contained by the {@link IShape}. If this is not
	 * the case, <code>false</code> is returned.
	 * </p>
	 *
	 * <p>
	 * Subsequently, the {@link Point}s of intersection of the
	 * {@link BezierCurve} and the {@link IShape} are computed. If there are
	 * less then two intersection {@link Point}s, <code>true</code> is returned.
	 * </p>
	 *
	 * <p>
	 * Alternatively, the {@link BezierCurve}'s parameter values for the
	 * individual {@link Point}s of intersection are sorted. For every two
	 * consecutive parameter values, a {@link Point} on the {@link BezierCurve}
	 * between those two parameter values is computed. If any of those
	 * {@link Point}s is not contained by the {@link IShape}, <code>false</code>
	 * is returned. Otherwise, <code>true</code> is returned.
	 * </p>
	 *
	 * <p>
	 * Self-intersection-problem: If the {@link BezierCurve} has a
	 * self-intersection p and p lies on an outline segment of the
	 * {@link IShape} ( {@link IShape#getOutlineSegments()}), <code>true</code>
	 * is returned, although <code>false</code> would be the right answer.
	 * </p>
	 *
	 * @param shape
	 *            the {@link IShape} that is tested to contain the given
	 *            {@link BezierCurve}
	 * @param c
	 *            the {@link BezierCurve} that is tested to be contained by the
	 *            given {@link IShape}
	 * @return <code>true</code> if the given {@link BezierCurve} is fully
	 *         contained by the given {@link IShape}
	 */
	public static boolean contains(IShape shape, BezierCurve c) {
		if (!(shape.contains(c.getP1()) && shape.contains(c.getP2()))) {
			return false;
		}

		Set<Double> intersectionParams = new HashSet<>();

		for (ICurve segC : shape.getOutlineSegments()) {
			for (BezierCurve seg : segC.toBezier()) {
				Set<Point> inters = new HashSet<>();
				c.getIntersectionIntervalPairs(seg, inters);
				for (Point poi : inters) {
					intersectionParams.add(c.getParameterAt(poi));
				}
			}
		}

		/*
		 * Start and end point of the curve are guaranteed to lie inside the
		 * IShape. If the curve would not be contained by the shape, at least
		 * two intersections could be found.
		 * 
		 * TODO: Special case! There is a special case where the Bezier curve
		 * leaves and enters the shape in the same point. This is only possible
		 * if the Bezier curve has a self intersection at that point.
		 */
		if (intersectionParams.size() <= 1) {
			return true;
		}

		Double[] poiParams = intersectionParams.toArray(new Double[] {});
		Arrays.sort(poiParams, new Comparator<Double>() {
			@Override
			public int compare(Double t, Double u) {
				double d = t - u;
				return d < 0 ? -1 : d > 0 ? 1 : 0;
			}
		});

		// check the points between the intersections for containment
		if (!shape.contains(c.get(poiParams[0] / 2))) {
			return false;
		}
		for (int i = 0; i < poiParams.length - 1; i++) {
			if (!shape.contains(c.get((poiParams[i] + poiParams[i + 1]) / 2))) {
				return false;
			}
		}
		return shape.contains(c.get((poiParams[poiParams.length - 1] + 1) / 2));
	}

	/**
	 * Returns <code>true</code> if the given {@link IShape} fully contains the
	 * given {@link ICurve}. Otherwise, <code>false</code> is returned. A
	 * {@link ICurve} is contained by a {@link IShape} if the {@link ICurve}'s
	 * Bezier approximation ({@link ICurve#toBezier()}) is contained by the
	 * {@link IShape} ({@link ShapeUtils#contains(IShape, BezierCurve)}).
	 *
	 * @param shape
	 *            the {@link IShape} that is tested to contain the
	 *            {@link ICurve}
	 * @param curve
	 *            the {@link ICurve} that is tested to be contained by the
	 *            {@link IShape}
	 * @return <code>true</code> if the given {@link IShape} contains the
	 *         {@link ICurve}, otherwise <code>false</code>
	 */
	public static boolean contains(IShape shape, ICurve curve) {
		for (BezierCurve seg : curve.toBezier()) {
			if (!contains(shape, seg)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns <code>true</code> if the given {@link IShape} fully contains the
	 * given {@link IGeometry}. Otherwise, <code>false</code> is returned.
	 *
	 * An <code>instanceof</code> test delegates to the appropriate method.
	 *
	 * @see ShapeUtils#contains(IShape, ICurve)
	 * @see ShapeUtils#contains(IShape, IShape)
	 * @see ShapeUtils#contains(IShape, IMultiShape)
	 * @param shape
	 *            the {@link IShape} that is tested to contain the
	 *            {@link IGeometry}
	 * @param geom
	 *            the {@link IGeometry} that is tested to be contained by the
	 *            {@link IShape}
	 * @return <code>true</code> if the {@link IShape} contains the
	 *         {@link IGeometry}, otherwise <code>false</code>
	 */
	public static boolean contains(IShape shape, IGeometry geom) {
		if (geom instanceof ICurve) {
			return contains(shape, (ICurve) geom);
		} else if (geom instanceof IShape) {
			return contains(shape, (IShape) geom);
		} else if (geom instanceof IMultiShape) {
			return contains(shape, (IMultiShape) geom);
		} else {
			throw new UnsupportedOperationException("Not yet implemented.");
		}
	}

	/**
	 * Returns <code>true</code> if the given {@link IShape} fully contains the
	 * given {@link IMultiShape}. Otherwise, <code>false</code> is returned.
	 *
	 * A {@link IMultiShape} is contained by a {@link IShape} if all of its sub-
	 * {@link IShape}s are contained by the {@link IShape} (see
	 * {@link IMultiShape#getShapes()} and
	 * {@link ShapeUtils#contains(IShape, IShape)}).
	 *
	 * @param shape
	 *            the {@link IShape} that is tested to contain the
	 *            {@link IMultiShape}
	 * @param multiShape
	 *            the {@link IMultiShape} that is tested to be contained by the
	 *            {@link IShape}
	 * @return <code>true</code> if the {@link IShape} contains the
	 *         {@link IMultiShape}, otherwise <code>false</code>
	 */
	public static boolean contains(IShape shape, IMultiShape multiShape) {
		for (IShape seg : multiShape.getShapes()) {
			if (!contains(shape, seg)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns <code>true</code> if the second {@link IShape} is fully contained
	 * by the first {@link IShape}. Otherwise, <code>false</code> is returned.
	 *
	 * A {@link IShape} is contained by another {@link IShape} if all of its
	 * outline segments ({@link IShape#getOutlineSegments()}) are contained by
	 * the other {@link IShape} ({@link ShapeUtils#contains(IShape, ICurve)}).
	 *
	 * @param shape1
	 *            the {@link IShape} that is tested to contain the other
	 *            {@link IShape}
	 * @param shape2
	 *            the {@link IShape} that is tested to be contained by the other
	 *            {@link IShape}
	 * @return <code>true</code> if the second {@link IShape} is contained by
	 *         the first {@link IShape}, otherwise <code>false</code>
	 */
	public static boolean contains(IShape shape1, IShape shape2) {
		for (ICurve seg : shape2.getOutlineSegments()) {
			if (!contains(shape1, seg)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a {@link PolyBezier} that is constructed from the outline
	 * segments ( {@link IShape#getOutlineSegments()}) of the given
	 * {@link IShape}.
	 *
	 * @param shape
	 *            the {@link IShape} to compute the outline for
	 * @return a {@link PolyBezier} that is constructed from the outline
	 *         segments of the {@link IShape}
	 */
	public static PolyBezier getOutline(IShape shape) {
		ICurve[] curves = shape.getOutlineSegments();

		ArrayList<BezierCurve> beziers = new ArrayList<>(
				curves.length);

		for (ICurve c : curves) {
			for (BezierCurve b : c.toBezier()) {
				beziers.add(b);
			}
		}

		return new PolyBezier(beziers.toArray(new BezierCurve[] {}));
	}

	private ShapeUtils() {
		// this class should not be instantiated by clients
	}

}
