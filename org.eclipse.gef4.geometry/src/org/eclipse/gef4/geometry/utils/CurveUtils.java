/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.euclidean.Straight;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.BezierCurve.IntervalPair;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IPolyCurve;
import org.eclipse.gef4.geometry.planar.IPolyShape;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.PolyBezier;

/**
 * The {@link CurveUtils} class provides functionality that can be used for
 * linear curves ({@link Line}, {@link Straight}), as well as quadratic and
 * cubic Bezier curves.
 * 
 * @author wienand
 * 
 */
public class CurveUtils {

	/**
	 * Delegates to the BezierCurve.getIntersections(ICurve) method.
	 * 
	 * @param curve1
	 * @param curve2
	 * @return points of intersection
	 */
	public static Point[] getIntersections(ICurve curve1, ICurve curve2) {
		Set<Point> intersections = new HashSet<Point>();

		for (BezierCurve bezier : curve1.toBezier()) {
			intersections
					.addAll(Arrays.asList(bezier.getIntersections(curve2)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Delegates to the getIntersections(ICurve, ICurve) method.
	 * 
	 * @param curve
	 * @param shape
	 * @return points of intersection
	 */
	public static Point[] getIntersections(ICurve curve, IShape shape) {
		Set<Point> intersections = new HashSet<Point>();

		for (ICurve curve2 : shape.getOutlineSegments()) {
			intersections
					.addAll(Arrays.asList(getIntersections(curve, curve2)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Delegates to the getIntersections(ICurve, ICurve) method.
	 * 
	 * @param curve
	 * @param polyCurve
	 * @return points of intersection
	 */
	public static Point[] getIntersections(ICurve curve, IPolyCurve polyCurve) {
		Set<Point> intersections = new HashSet<Point>();

		for (ICurve curve2 : polyCurve.getCurves()) {
			intersections
					.addAll(Arrays.asList(getIntersections(curve, curve2)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Delegates to the getIntersections(ICurve, IShape) method.
	 * 
	 * @param curve
	 * @param polyShape
	 * @return points of intersection
	 */
	public static Point[] getIntersections(ICurve curve, IPolyShape polyShape) {
		Set<Point> intersections = new HashSet<Point>();

		for (IShape shape : polyShape.getShapes()) {
			intersections.addAll(Arrays.asList(getIntersections(curve, shape)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Delegates to one of the above getIntersections() methods.
	 * 
	 * @param curve
	 * @param geom
	 * @return points of intersection
	 */
	public static Point[] getIntersections(ICurve curve, IGeometry geom) {
		if (geom instanceof ICurve) {
			return getIntersections(curve, (ICurve) geom);
		} else if (geom instanceof IShape) {
			return getIntersections(curve, (IShape) geom);
		} else if (geom instanceof IPolyCurve) {
			return getIntersections(curve, (IPolyCurve) geom);
		} else if (geom instanceof IPolyShape) {
			return getIntersections(curve, (IPolyShape) geom);
		} else {
			throw new UnsupportedOperationException("Not yet implemented.");
		}
	}

	/**
	 * Delegates to one of the above getIntersections() methods.
	 * 
	 * @param geom1
	 * @param geom2
	 * @return points of intersection
	 */
	public static Point[] getIntersections(IGeometry geom1, IGeometry geom2) {
		if (geom1 instanceof ICurve) {
			return getIntersections((ICurve) geom1, geom2);
		} else {
			Set<Point> intersections = new HashSet<Point>();

			if (geom1 instanceof IPolyCurve) {
				for (ICurve curve : ((IPolyCurve) geom1).getCurves()) {
					intersections.addAll(Arrays.asList(getIntersections(curve,
							geom2)));
				}
			} else if (geom1 instanceof IShape) {
				for (ICurve curve : ((IShape) geom1).getOutlineSegments()) {
					intersections.addAll(Arrays.asList(getIntersections(curve,
							geom2)));
				}
			} else if (geom1 instanceof IPolyShape) {
				for (IShape shape : ((IPolyShape) geom1).getShapes()) {
					for (ICurve curve : shape.getOutlineSegments()) {
						intersections.addAll(Arrays.asList(getIntersections(
								curve, geom2)));
					}
				}
			} else {
				throw new UnsupportedOperationException("Not yet implemented.");
			}

			return intersections.toArray(new Point[] {});
		}
	}

	/**
	 * Tests the given {@link ICurve}s for a finite number of intersections.
	 * Returns <code>true</code> if the given {@link ICurve}s have a finite
	 * number of intersection points. Otherwise, returns <code>false</code>.
	 * 
	 * @param c1
	 * @param c2
	 * @return <code>true</code> if both {@link ICurve}s have a finite set of
	 *         intersection points, otherwise <code>false</code>
	 */
	public static boolean intersects(ICurve c1, ICurve c2) {
		return getIntersections(c1, c2).length > 0;
	}

	/**
	 * Tests the given {@link ICurve}s for an infinite number of intersections.
	 * Returns <code>true</code> if the given {@link ICurve}s have an infinite
	 * number of intersection points. Otherwise, returns <code>false</code>.
	 * 
	 * @param c1
	 * @param c2
	 * @return <code>true</code> if both {@link ICurve}s have an infinite set of
	 *         intersection points, otherwise <code>false</code>
	 */
	public static boolean overlaps(ICurve c1, ICurve c2) {
		for (BezierCurve seg1 : c1.toBezier()) {
			for (BezierCurve seg2 : c2.toBezier()) {
				if (seg1.overlaps(seg2)) {
					return true;
				}
			}
		}
		return false;
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

		Set<Double> intersectionParams = new HashSet<Double>();

		for (ICurve segC : shape.getOutlineSegments()) {
			for (BezierCurve seg : segC.toBezier()) {
				Set<Point> inters = new HashSet<Point>();
				Set<IntervalPair> ips = c.getIntersectionIntervalPairs(
						new BezierCurve(seg.getP1(), seg.getP2()), inters);
				for (IntervalPair ip : ips) {
					intersectionParams.add(ip.p == c ? ip.pi.getMid() : ip.qi
							.getMid());
				}
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
		 * if the Bezier curve has a self intersections at that point.
		 */
		if (intersectionParams.size() <= 1) {
			return true;
		}

		Double[] poiParams = intersectionParams.toArray(new Double[] {});
		Arrays.sort(poiParams, new Comparator<Double>() {
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
	 * {@link IShape} ({@link CurveUtils#contains(IShape, BezierCurve)}).
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
	 * Returns <code>true</code> if the second {@link IShape} is fully contained
	 * by the first {@link IShape}. Otherwise, <code>false</code> is returned.
	 * 
	 * A {@link IShape} is contained by another {@link IShape} if all of its
	 * outline segments ({@link IShape#getOutlineSegments()}) are contained by
	 * the other {@link IShape} ({@link CurveUtils#contains(IShape, ICurve)}).
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
	 * Returns <code>true</code> if the given {@link IPolyCurve} is fully
	 * contained by the given {@link IShape}. Otherwise, <code>false</code> is
	 * returned.
	 * 
	 * A {@link IPolyCurve} is contained by a {@link IShape} if all of its sub-
	 * {@link ICurve}s are contained by the shape (see
	 * {@link IPolyCurve#getCurves()} and
	 * {@link CurveUtils#contains(IShape, ICurve)}).
	 * 
	 * @param shape
	 *            the {@link IShape} that is tested to contain the
	 *            {@link IPolyCurve}
	 * @param polyCurve
	 *            the {@link IPolyCurve} that is tested to be contained by the
	 *            {@link IShape}
	 * @return <code>true</code> if the {@link IShape} contains the
	 *         {@link IPolyCurve}, otherwise <code>false</code>
	 */
	public static boolean contains(IShape shape, IPolyCurve polyCurve) {
		for (ICurve seg : polyCurve.getCurves()) {
			if (!contains(shape, seg)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns <code>true</code> if the given {@link IShape} fully contains the
	 * given {@link IPolyShape}. Otherwise, <code>false</code> is returned.
	 * 
	 * A {@link IPolyShape} is contained by a {@link IShape} if all of its sub-
	 * {@link IShape}s are contained by the {@link IShape} (see
	 * {@link IPolyShape#getShapes()} and
	 * {@link CurveUtils#contains(IShape, IShape)}).
	 * 
	 * @param shape
	 *            the {@link IShape} that is tested to contain the
	 *            {@link IPolyShape}
	 * @param polyShape
	 *            the {@link IPolyShape} that is tested to be contained by the
	 *            {@link IShape}
	 * @return <code>true</code> if the {@link IShape} contains the
	 *         {@link IPolyShape}, otherwise <code>false</code>
	 */
	public static boolean contains(IShape shape, IPolyShape polyShape) {
		for (IShape seg : polyShape.getShapes()) {
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
	 * @see CurveUtils#contains(IShape, ICurve)
	 * @see CurveUtils#contains(IShape, IPolyCurve)
	 * @see CurveUtils#contains(IShape, IShape)
	 * @see CurveUtils#contains(IShape, IPolyShape)
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
		} else if (geom instanceof IPolyCurve) {
			return contains(shape, (IPolyCurve) geom);
		} else if (geom instanceof IShape) {
			return contains(shape, (IShape) geom);
		} else if (geom instanceof IPolyShape) {
			return contains(shape, (IPolyShape) geom);
		} else {
			throw new UnsupportedOperationException("Not yet implemented.");
		}
	}

	/**
	 * Returns <code>true</code> if the second {@link IGeometry} is fully
	 * contained by the first {@link IGeometry}. Otherwise, <code>false</code>
	 * is returned.
	 * 
	 * @param geom1
	 * @param geom2
	 * @return <code>true</code> if the first {@link IGeometry} contains the
	 *         second {@link IGeometry}, otherwise <code>false</code>
	 */
	public static boolean contains(IGeometry geom1, IGeometry geom2) {
		if (geom1 instanceof IShape) {
			return contains((IShape) geom1, geom2);
		} else if (geom1 instanceof IPolyShape) {
			throw new UnsupportedOperationException("Not yet implemented.");
		} else {
			return false;
		}
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

		ArrayList<BezierCurve> beziers = new ArrayList<BezierCurve>(
				curves.length);

		for (ICurve c : curves) {
			for (BezierCurve b : c.toBezier()) {
				beziers.add(b);
			}
		}

		return new PolyBezier(beziers.toArray(new BezierCurve[] {}));
	}
}
