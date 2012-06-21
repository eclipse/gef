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
package org.eclipse.gef4.geometry.planar;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@link CurveUtils} class provides functionality that can be used for all
 * {@link ICurve}s, independent on their construction kind.
 * 
 * @author wienand
 * 
 */
class CurveUtils {

	private CurveUtils() {
		// this class should not be instantiated by clients
	}

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
	 * Builds up a {@link Path} from the given {@link ICurve}s. Only
	 * {@link Line}, {@link QuadraticCurve} and {@link CubicCurve} objects can
	 * be integrated into the constructed {@link Path}.
	 * 
	 * @param curves
	 * @return a {@link Path} representing the given {@link ICurve}s
	 */
	public static final Path toPath(ICurve... curves) {
		Path p = new Path();
		for (int i = 0; i < curves.length; i++) {
			if (i == 0) {
				p.moveTo(curves[i].getX1(), curves[i].getY1());
			}
			ICurve c = curves[i];
			if (c instanceof Line) {
				p.lineTo(c.getX2(), c.getY2());
			} else if (c instanceof QuadraticCurve) {
				p.quadTo(((QuadraticCurve) c).getCtrlX(),
						((QuadraticCurve) c).getCtrlY(), c.getX2(), c.getY2());
			} else if (c instanceof CubicCurve) {
				p.curveTo(((CubicCurve) c).getCtrlX1(),
						((CubicCurve) c).getCtrlY1(),
						((CubicCurve) c).getCtrlX2(),
						((CubicCurve) c).getCtrlY2(), ((CubicCurve) c).getX2(),
						((CubicCurve) c).getY2());
			} else {
				throw new UnsupportedOperationException(
						"This type of ICurve is not yet implemented.");
			}
		}
		return p;
	}
	
	/**
	 * Transforms a sequence of {@link Point} coordinates into a sequence of
	 * {@link Line} segments, by creating a {@link Line} segment for each two
	 * adjacent points in the array. In case it is specified to close the
	 * segment list, a {@link Line} segment is furthermore created between the
	 * last and the first point in the list.
	 * 
	 * @param points
	 *            the array of {@link Point}s to convert
	 * @param close
	 *            a flag indicating whether a line segment will be created from
	 *            the last point in the list back to the first one
	 * @return an array of {@link Line} segments, which is created by creating a
	 *         {@link Line} for each two adjacent {@link Point}s in the given
	 *         array, which includes a {@link Line} segment between the last
	 *         point in the given array in the first one, if and only if the
	 *         parameter close is given as <code>true</code>
	 */
	public static Line[] toSegmentsArray(Point[] points, boolean close) {
		int segmentCount = close ? points.length : points.length - 1;
		Line[] segments = new Line[segmentCount];
		for (int i = 0; i < segmentCount; i++) {
			segments[i] = new Line(points[i],
					points[i + 1 < points.length ? i + 1 : 0]);
		}
		return segments;
	}

}
