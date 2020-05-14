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
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.planar;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@link CurveUtils} class provides functionality that can be used for all
 * {@link ICurve}s, independent on their construction kind.
 *
 * @author mwienand
 *
 */
class CurveUtils {

	/**
	 * Creates copies of the given {@link BezierCurve}s.
	 *
	 * @param curves
	 *            the {@link BezierCurve}s to copy
	 * @return an array containing the copies
	 */
	public static BezierCurve[] getCopy(BezierCurve... curves) {
		BezierCurve[] copies = new BezierCurve[curves.length];
		for (int i = 0; i < curves.length; i++) {
			copies[i] = curves[i].getCopy();
		}
		return copies;
	}

	/**
	 * Delegates to the {@link BezierCurve#getIntersections(ICurve)} method.
	 *
	 * @param curve1
	 *            The first {@link ICurve} to intersect
	 * @param curve2
	 *            The second {@link ICurve} to intersect
	 * @return An array of intersection {@link Point}s
	 */
	public static Point[] getIntersections(ICurve curve1, ICurve curve2) {
		Set<Point> intersections = new HashSet<>();

		for (BezierCurve bezier : curve1.toBezier()) {
			intersections
					.addAll(Arrays.asList(bezier.getIntersections(curve2)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Delegates to the appropriate getIntersections() method for the passed-in
	 * {@link IGeometry} depending on its type.
	 *
	 * @param curve
	 *            the {@link ICurve} to intersect
	 * @param geom
	 *            the {@link IGeometry} to intersect
	 * @return points of intersection
	 * @see #getIntersections(ICurve, ICurve)
	 * @see #getIntersections(ICurve, IShape)
	 * @see #getIntersections(ICurve, IMultiShape)
	 */
	public static Point[] getIntersections(ICurve curve, IGeometry geom) {
		if (geom instanceof ICurve) {
			return getIntersections(curve, (ICurve) geom);
		} else if (geom instanceof IShape) {
			return getIntersections(curve, (IShape) geom);
		} else if (geom instanceof IMultiShape) {
			return getIntersections(curve, (IMultiShape) geom);
		} else {
			throw new UnsupportedOperationException("Not yet implemented.");
		}
	}

	/**
	 * Delegates to the {@link #getIntersections(ICurve, IShape)} method.
	 *
	 * @param curve
	 *            the {@link ICurve} to intersect
	 * @param multiShape
	 *            the {@link IMultiShape} of which the outline is intersected
	 * @return an array of intersection {@link Point}s
	 */
	public static Point[] getIntersections(ICurve curve,
			IMultiShape multiShape) {
		Set<Point> intersections = new HashSet<>();

		for (IShape shape : multiShape.getShapes()) {
			intersections.addAll(Arrays.asList(getIntersections(curve, shape)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Delegates to the {@link #getIntersections(ICurve, ICurve)} method.
	 *
	 * @param curve
	 *            the {@link ICurve} to intersect
	 * @param shape
	 *            the {@link IShape} of which the outline is intersected
	 * @return an array of intersection {@link Point}s
	 */
	public static Point[] getIntersections(ICurve curve, IShape shape) {
		Set<Point> intersections = new HashSet<>();

		for (ICurve curve2 : shape.getOutlineSegments()) {
			intersections
					.addAll(Arrays.asList(getIntersections(curve, curve2)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Delegates to the {@link #getIntersections(ICurve, IGeometry)} method.
	 *
	 * @param geom1
	 *            the first {@link IGeometry} to intersect
	 * @param geom2
	 *            the second {@link IGeometry} to intersect
	 * @return points of intersection
	 */
	public static Point[] getIntersections(IGeometry geom1, IGeometry geom2) {
		if (geom1 instanceof ICurve) {
			return getIntersections((ICurve) geom1, geom2);
		} else {
			Set<Point> intersections = new HashSet<>();

			if (geom1 instanceof IShape) {
				for (ICurve curve : ((IShape) geom1).getOutlineSegments()) {
					intersections.addAll(
							Arrays.asList(getIntersections(curve, geom2)));
				}
			} else if (geom1 instanceof IMultiShape) {
				for (IShape shape : ((IMultiShape) geom1).getShapes()) {
					for (ICurve curve : shape.getOutlineSegments()) {
						intersections.addAll(
								Arrays.asList(getIntersections(curve, geom2)));
					}
				}
			} else {
				throw new UnsupportedOperationException("Not yet implemented.");
			}

			return intersections.toArray(new Point[] {});
		}
	}

	/**
	 * Returns the overlaps between the given two {@link ICurve curves}, if the
	 * given two {@link ICurve curves} overlap.
	 *
	 * @param curve1
	 *            The first {@link ICurve}, for which to compute overlaps.
	 * @param curve2
	 *            The second {@link ICurve}, for which to compute overlaps.
	 * @return An array of {@link ICurve curves} representing overlaps between
	 *         the two given curves. An empty array in case the curves do not
	 *         overlap.
	 */
	public static ICurve[] getOverlaps(ICurve curve1, ICurve curve2) {
		Set<ICurve> overlaps = new HashSet<>();

		for (BezierCurve bezier1 : curve1.toBezier()) {
			for (BezierCurve bezier2 : curve2.toBezier()) {
				BezierCurve overlap = bezier1.getOverlap(bezier2);
				if (overlap != null) {
					overlaps.add(overlap);
				}
			}
		}

		return overlaps.toArray(new ICurve[] {});
	}

	/**
	 * Checks if the given {@link ICurve}s intersect in a finite number of
	 * {@link Point}s.
	 *
	 * @param c1
	 *            The first {@link ICurve} to check for intersection
	 *            {@link Point}s
	 * @param c2
	 *            The second {@link ICurve} to check for intersection
	 *            {@link Point}s
	 * @return <code>true</code> if both {@link ICurve}s have a finite set of
	 *         intersection {@link Point}s, otherwise <code>false</code>
	 */
	public static boolean intersect(ICurve c1, ICurve c2) {
		return getIntersections(c1, c2).length > 0;
	}

	/**
	 * Checks if the given {@link ICurve}s overlap, i.e. both {@link ICurve}s
	 * have an infinite number of intersection {@link Point}s.
	 *
	 * @param c1
	 *            the first {@link ICurve} to check for overlap
	 * @param c2
	 *            the second {@link ICurve} to check for overlap
	 * @return <code>true</code> if both {@link ICurve}s overlap, otherwise
	 *         <code>false</code>
	 */
	public static boolean overlap(ICurve c1, ICurve c2) {
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
	 *            the {@link ICurve}s from which the {@link Path} is constructed
	 * @return a new {@link Path} representing the given {@link ICurve}s
	 */
	public static final Path toPath(ICurve... curves) {
		Path p = new Path();
		for (int i = 0; i < curves.length; i++) {
			ICurve c = curves[i];
			if (i == 0) {
				p.moveTo(c.getX1(), c.getY1());
			}
			if (c instanceof Line) {
				p.lineTo(c.getX2(), c.getY2());
			} else if (c instanceof QuadraticCurve) {
				p.quadTo(((QuadraticCurve) c).getCtrlX(),
						((QuadraticCurve) c).getCtrlY(), c.getX2(), c.getY2());
			} else if (c instanceof CubicCurve) {
				p.cubicTo(((CubicCurve) c).getCtrlX1(),
						((CubicCurve) c).getCtrlY1(),
						((CubicCurve) c).getCtrlX2(),
						((CubicCurve) c).getCtrlY2(), ((CubicCurve) c).getX2(),
						((CubicCurve) c).getY2());
			} else if (c instanceof BezierCurve) {
				Point[] points = ((BezierCurve) c).getPoints();
				int length = points.length;
				if (length == 1) {
					p.moveTo(c.getX1(), c.getY1());
				} else if (length == 2) {
					p.lineTo(c.getX2(), c.getY2());
				} else if (length == 3) {
					p.quadTo(points[1].x, points[1].y, points[2].x,
							points[2].y);
				} else if (length == 4) {
					p.cubicTo(points[1].x, points[1].y, points[2].x,
							points[2].y, points[3].x, points[3].y);
				} else {
					throw new UnsupportedOperationException(
							"A BezierCurve with more than 4 control points cannot be integrated in a Path. Only singular, linear, quadratic, or cubic BezierCurves can be integrated in a Path.");
				}
			} else {
				throw new UnsupportedOperationException(
						"This type of ICurve is not yet implemented: toPath("
								+ Arrays.asList(curves) + ")");
			}
		}
		return p;
	}

	/**
	 * Transforms a sequence of {@link Point}s into a sequence of {@link Line}
	 * segments, by creating a {@link Line} segment for each two adjacent
	 * {@link Point}s in the given array. In case it is specified to close the
	 * segment list, another {@link Line} segment is created that connects the
	 * last and the first {@link Point} in the array.
	 *
	 * @param points
	 *            the array of {@link Point}s to convert
	 * @param close
	 *            a flag indicating whether a {@link Line} segment will be
	 *            created from the last {@link Point} in the list back to the
	 *            first one
	 * @return an array of {@link Line} segments, which is created by creating a
	 *         {@link Line} for each two adjacent {@link Point}s in the given
	 *         array, which includes a {@link Line} segment from the last
	 *         {@link Point} in the given array to the first one if the
	 *         <i>close</i> flag is set to <code>true</code>
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

	private CurveUtils() {
		// this class should not be instantiated by clients
	}

}
