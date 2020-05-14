/*******************************************************************************
 * Copyright (c) 2011, 2017 itemis AG and others.
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

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.geometry.convert.awt.AWT2Geometry;
import org.eclipse.gef.geometry.convert.awt.Geometry2AWT;
import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;

/**
 * Represents the geometric shape of a path, which may consist of independent
 * subgraphs.
 *
 * Note that while all manipulations (e.g. within shrink, expand) within this
 * class are based on double precision, all comparisons (e.g. within contains,
 * intersects, equals, etc.) are based on a limited precision (with an accuracy
 * defined within {@link PrecisionUtils}) to compensate for rounding effects.
 *
 * @author anyssen
 *
 */
public class Path extends AbstractGeometry implements IGeometry {

	/**
	 * Representation for different types of {@link Segment}s.
	 *
	 * @see #MOVE_TO
	 * @see #LINE_TO
	 * @see #QUAD_TO
	 * @see #CUBIC_TO
	 */
	public static class Segment {

		/**
		 * A {@link #MOVE_TO} {@link Segment} represents a change of position
		 * while piecewise building a {@link Path}, without inserting a new
		 * curve.
		 *
		 * @see Path#moveTo(double, double)
		 */
		public static final int MOVE_TO = 0;

		/**
		 * A {@link #LINE_TO} {@link Segment} represents a {@link Line} from the
		 * previous position of a {@link Path} to the {@link Point} at index 0
		 * associated with the {@link Segment}.
		 *
		 * @see Path#lineTo(double, double)
		 */
		public static final int LINE_TO = 1;

		/**
		 * A {@link #QUAD_TO} {@link Segment} represents a
		 * {@link QuadraticCurve} from the previous position of a {@link Path}
		 * to the {@link Point} at index 1 associated with the {@link Segment}.
		 * The {@link Point} at index 0 is used as the handle {@link Point} of
		 * the {@link QuadraticCurve}.
		 *
		 * @see Path#quadTo(double, double, double, double)
		 */
		public static final int QUAD_TO = 2;

		/**
		 * A {@link #CUBIC_TO} {@link Segment} represents a {@link CubicCurve}
		 * from the previous position of a {@link Path} to the {@link Point} at
		 * index 2 associated with the {@link Segment}. The {@link Point}s at
		 * indices 0 and 1 are used as the handle {@link Point}s of the
		 * {@link CubicCurve}.
		 *
		 * @see Path#cubicTo(double, double, double, double, double, double)
		 */
		public static final int CUBIC_TO = 3;

		/**
		 * A {@link #CLOSE} {@link Segment} represents the link from the current
		 * position of a {@link Path} to the position of the last
		 * {@link #MOVE_TO} {@link Segment}.
		 *
		 * @see Path#close()
		 */
		public static final int CLOSE = 4;

		private int type;
		private Point[] points;

		/**
		 * Constructs a new {@link Segment} of the given type. The passed-in
		 * {@link Point}s are associated with this {@link Segment}.
		 *
		 * @param type
		 *            The type of the new {@link Segment}. It is one of
		 *            <ul>
		 *            <li>{@link #MOVE_TO}</li>
		 *            <li>{@link #LINE_TO}</li>
		 *            <li>{@link #QUAD_TO}</li>
		 *            <li>{@link #CUBIC_TO}</li>
		 *            </ul>
		 * @param points
		 *            the {@link Point}s to associate with this {@link Segment}
		 */
		public Segment(int type, Point... points) {
			switch (type) {
			case MOVE_TO:
				if (points == null || points.length != 1) {
					throw new IllegalArgumentException(
							"A Segment of type MOVE_TO has to be associate with exactly 1 point: new Segment("
									+ type + ", " + (points == null ? "null"
											: Arrays.asList(points))
									+ ")");
				}
				break;
			case LINE_TO:
				if (points == null || points.length != 1) {
					throw new IllegalArgumentException(
							"A Segment of type LINE_TO has to be associate with exactly 1 point: new Segment("
									+ type + ", " + (points == null ? "null"
											: Arrays.asList(points))
									+ ")");
				}
				break;
			case QUAD_TO:
				if (points == null || points.length != 2) {
					throw new IllegalArgumentException(
							"A Segment of type QUAD_TO has to be associate with exactly 2 points: new Segment("
									+ type + ", " + (points == null ? "null"
											: Arrays.asList(points))
									+ ")");
				}
				break;
			case CUBIC_TO:
				if (points == null || points.length != 3) {
					throw new IllegalArgumentException(
							"A Segment of type CUBIC_TO has to be associate with exactly 3 point: new Segment("
									+ type + ", " + (points == null ? "null"
											: Arrays.asList(points))
									+ ")");
				}
				break;
			case CLOSE:
				if (points != null && points.length != 0) {
					throw new IllegalArgumentException(
							"A Segment of type CLOSE is not to be associated with any points: new Segment("
									+ type + ", " + (points == null ? "null"
											: Arrays.asList(points))
									+ ")");
				}
				break;
			default:
				throw new IllegalArgumentException(
						"You can only create Segments of types MOVE_TO, LINE_TO, QUAD_TO, or CUBIC_TO: new Segment("
								+ type + ", " + (points == null ? "null"
										: Arrays.asList(points))
								+ ")");
			}

			this.type = type;
			this.points = points == null ? new Point[] {}
					: Point.getCopy(points);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Segment) {
				Segment s = (Segment) obj;
				if (s.type == type && Arrays.equals(s.points, points)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Returns a copy of this {@link Segment}. The associated {@link Point}s
		 * are copied, too.
		 *
		 * @return a copy of this {@link Segment}
		 */
		public Segment getCopy() {
			return new Segment(type, getPoints());
		}

		/**
		 * Returns a copy of the {@link Point}s associated with this
		 * {@link Segment}.
		 *
		 * @return a copy of the {@link Point}s associated with this
		 *         {@link Segment}.
		 */
		public Point[] getPoints() {
			return Point.getCopy(points);
		}

		/**
		 * Returns the type of this {@link Segment}.
		 *
		 * @return the type of this {@link Segment}
		 * @see #MOVE_TO
		 * @see #LINE_TO
		 * @see #QUAD_TO
		 * @see #CUBIC_TO
		 */
		public int getType() {
			return type;
		}

		@Override
		public int hashCode() {
			return type;
		}

	}

	/**
	 * Winding rule for determining the interior of the {@link Path}. Indicates
	 * that a {@link Point} is regarded to lie inside the {@link Path}, if any
	 * ray starting in that {@link Point} and pointing to infinity crosses the
	 * {@link Segment}s of the {@link Path} an odd number of times.
	 */
	public static final int WIND_EVEN_ODD = 0;

	/**
	 * Winding rule for determining the interior of the {@link Path}. Indicates
	 * that a {@link Point} is regarded to lie inside the {@link Path}, if any
	 * ray starting from that {@link Point} and pointing to infinity is crossed
	 * by {@link Path} {@link Segment}s a different number of times in the
	 * counter-clockwise direction than in the clockwise direction.
	 */
	public static final int WIND_NON_ZERO = 1;

	private static final long serialVersionUID = 1L;

	/**
	 * Unions the two specified {@link Path}s
	 *
	 * @param pa
	 *            the first area to add
	 * @param pb
	 *            the second area to add
	 * @return the sum of the areas
	 */
	public static Path add(Path pa, Path pb) {
		Area a = new Area(Geometry2AWT.toAWTPath(pa));
		Area b = new Area(Geometry2AWT.toAWTPath(pb));
		a.add(b);
		return AWT2Geometry.toPath(new Path2D.Double(a));
	}

	/**
	 * Computes the area covered by the first or the second but not both given
	 * areas.
	 *
	 * @param pa
	 *            the first area to compute the xor for
	 * @param pb
	 *            the second area to compute the xor for
	 * @return the exclusive-or of the areas
	 */
	public static Path exclusiveOr(Path pa, Path pb) {
		Area a = new Area(Geometry2AWT.toAWTPath(pa));
		Area b = new Area(Geometry2AWT.toAWTPath(pb));
		a.exclusiveOr(b);
		return AWT2Geometry.toPath(new Path2D.Double(a));
	}

	/**
	 * Intersects the given areas.
	 *
	 * @param pa
	 *            the first area to intersect
	 * @param pb
	 *            the second area to intersect
	 * @return the intersection of the areas, i.e. the area covered by both
	 *         areas
	 */
	public static Path intersect(Path pa, Path pb) {
		Area a = new Area(Geometry2AWT.toAWTPath(pa));
		Area b = new Area(Geometry2AWT.toAWTPath(pb));
		a.intersect(b);
		return AWT2Geometry.toPath(new Path2D.Double(a));
	}

	/**
	 * Subtracts the second given area from the first given area.
	 *
	 * @param pa
	 *            the area to subtract from
	 * @param pb
	 *            the area to subtract
	 * @return the area covered by the first but not the second given area
	 */
	public static Path subtract(Path pa, Path pb) {
		Area a = new Area(Geometry2AWT.toAWTPath(pa));
		Area b = new Area(Geometry2AWT.toAWTPath(pb));
		a.subtract(b);
		return AWT2Geometry.toPath(new Path2D.Double(a));
	}

	private int windingRule = WIND_NON_ZERO;

	private List<Segment> segments = new ArrayList<>();

	/**
	 * Creates a new empty path with a default winding rule of
	 * {@link #WIND_NON_ZERO}.
	 */
	public Path() {
	}

	/**
	 * Creates a new empty path with given winding rule.
	 *
	 * @param windingRule
	 *            the winding rule to use; one of {@link #WIND_EVEN_ODD} or
	 *            {@link #WIND_NON_ZERO}
	 */
	public Path(int windingRule) {
		this.windingRule = windingRule;
	}

	/**
	 * Creates a path from the given segments, using the given winding rule.
	 *
	 * @param windingRule
	 *            the winding rule to use; one of {@link #WIND_EVEN_ODD} or
	 *            {@link #WIND_NON_ZERO}
	 * @param segments
	 *            The segments to initialize the path with
	 */
	public Path(int windingRule, Segment... segments) {
		this(windingRule);
		for (Segment s : segments) {
			this.segments.add(s.getCopy());
		}
	}

	/**
	 * Creates a path from the given segments, using the default winding rule
	 * {@link #WIND_NON_ZERO}.
	 *
	 * @param segments
	 *            The segments to initialize the path with
	 */
	public Path(Segment... segments) {
		this(WIND_NON_ZERO, segments);
	}

	/**
	 * Adds the given {@link List} of {@link Segment}s to this {@link Path}.
	 *
	 * @param segments
	 *            The {@link Segment}s to add to this {@link Path}.
	 * @return <code>this</code> for convenience.
	 */
	public final Path add(List<Segment> segments) {
		this.segments.addAll(segments);
		return this;
	}

	/**
	 * Adds the given {@link Segment}s to this {@link Path}.
	 *
	 * @param segments
	 *            The {@link Segment}s to add to this {@link Path}.
	 * @return <code>this</code> for convenience.
	 */
	public final Path add(Segment... segments) {
		this.segments.addAll(Arrays.asList(segments));
		return this;
	}

	/**
	 * Closes the current sub-path by drawing a straight line (line-to) to the
	 * location of the last move to.
	 *
	 * @return <code>this</code> for convenience
	 */
	public final Path close() {
		segments.add(new Segment(Segment.CLOSE));
		return this;
	}

	@Override
	public boolean contains(Point p) {
		return Geometry2AWT.toAWTPath(this)
				.contains(Geometry2AWT.toAWTPoint(p));
	}

	/**
	 * Returns <code>true</code> if the given {@link Rectangle} is contained
	 * within {@link IGeometry}, <code>false</code> otherwise.
	 *
	 * TODO: Generalize to arbitrary {@link IGeometry} objects.
	 *
	 * @param r
	 *            The {@link Rectangle} to test
	 * @return <code>true</code> if the {@link Rectangle} is fully contained
	 *         within this {@link IGeometry}
	 */
	public boolean contains(Rectangle r) {
		return Geometry2AWT.toAWTPath(this)
				.contains(Geometry2AWT.toAWTRectangle(r));
	}

	/**
	 * Adds a cubic Bezier curve segment from the current position to the
	 * specified end position, using the two provided control points as Bezier
	 * control points.
	 *
	 * @param control1X
	 *            The x-coordinate of the first Bezier control point
	 * @param control1Y
	 *            The y-coordinate of the first Bezier control point
	 * @param control2X
	 *            The x-coordinate of the second Bezier control point
	 * @param control2Y
	 *            The y-coordinate of the second Bezier control point
	 * @param x
	 *            The x-coordinate of the desired target point
	 * @param y
	 *            The y-coordinate of the desired target point
	 * @return <code>this</code> for convenience
	 */
	public final Path cubicTo(double control1X, double control1Y,
			double control2X, double control2Y, double x, double y) {
		segments.add(
				new Segment(Segment.CUBIC_TO, new Point(control1X, control1Y),
						new Point(control2X, control2Y), new Point(x, y)));
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Path) {
			// test if they are composed by the same segments
			// TODO: Even if the segments are not identical, the two Path
			// objects can be equal to each other.
			Segment[] thisSegments = getSegments();
			Segment[] objSegments = ((Path) obj).getSegments();
			return Arrays.equals(thisSegments, objSegments);
		}
		return false;
	}

	@Override
	public Rectangle getBounds() {
		List<ICurve> outlines = getOutlines();
		if (outlines.size() > 0) {
			Rectangle outlineBounds = outlines.get(0).getBounds();
			for (int i = 1; i < outlines.size(); i++) {
				outlineBounds.union(outlines.get(i).getBounds());
			}
			return outlineBounds;
		}
		return new Rectangle();
	}

	@Override
	public Path getCopy() {
		return new Path(getWindingRule(), getSegments());
	}

	/**
	 * Returns a {@link List} of {@link ICurve}s, representing the outline of
	 * <code>this</code> {@link Path}. For every {@link Segment#LINE_TO},
	 * {@link Segment#QUAD_TO}, {@link Segment#CUBIC_TO}, and
	 * {@link Segment#CLOSE}, one {@link BezierCurve} is created that resembles
	 * that segment.
	 *
	 * @return A {@link List} of {@link ICurve}s representing the outline of the
	 *         given {@link Path}.
	 */
	public List<ICurve> getOutlines() {
		List<ICurve> curves = new ArrayList<>();
		Segment[] segments = getSegments();
		// save the segment start point as it is not contained within individual
		// path segments
		Point segmentStart = null;
		// save the last move_to position which is later needed for a close
		// segment
		Point moveTo = null;
		for (Segment s : segments) {
			if (s.getType() == Segment.MOVE_TO) {
				// save MOVE_TO position
				moveTo = s.getPoints()[0];
				// set segment start position to the move_to position
				segmentStart = moveTo;
			} else {
				// for all other segments a curve is created
				if (segmentStart == null) {
					throw new IllegalStateException(
							"This Path does not start with a MOVE_TO, therefore, no start position could be determined.");
				} else {
					if (s.getType() == Segment.LINE_TO) {
						curves.add(new Line(segmentStart, s.getPoints()[0]));
						segmentStart = s.getPoints()[0];
					} else if (s.getType() == Segment.QUAD_TO) {
						curves.add(new QuadraticCurve(segmentStart,
								s.getPoints()[0], s.getPoints()[1]));
						segmentStart = s.getPoints()[1];
					} else if (s.getType() == Segment.CUBIC_TO) {
						curves.add(
								new org.eclipse.gef.geometry.planar.CubicCurve(
										segmentStart, s.getPoints()[0],
										s.getPoints()[1], s.getPoints()[2]));
						segmentStart = s.getPoints()[2];
					} else if (s.getType() == Segment.CLOSE) {
						curves.add(new Line(segmentStart, moveTo));
						segmentStart = moveTo;
					} else {
						throw new IllegalStateException(
								"This Path contains an unsupported Segment: <"
										+ s + ">.");
					}
				}
			}
		}
		return curves;
	}

	/**
	 * Returns the segments that make up this path.
	 *
	 * @return an array of {@link Segment}s representing the segments of this
	 *         path
	 */
	public Segment[] getSegments() {
		Segment[] segments = new Segment[this.segments.size()];
		for (int i = 0; i < segments.length; i++) {
			segments[i] = this.segments.get(i).getCopy();
		}
		return segments;
	}

	@Override
	public Path getTransformed(AffineTransform t) {
		return AWT2Geometry
				.toPath(new Path2D.Double(Geometry2AWT.toAWTPath(this),
						Geometry2AWT.toAWTAffineTransform(t)));
	}

	/**
	 * Returns the winding rule used to determine the interior of this path.
	 *
	 * @return the winding rule, i.e. one of {@link #WIND_EVEN_ODD} or
	 *         {@link #WIND_NON_ZERO}
	 */
	public int getWindingRule() {
		return windingRule;
	}

	/**
	 * Adds a straight line segment from the current position to the specified
	 * end position.
	 *
	 * @param x
	 *            The x-coordinate of the desired target point
	 * @param y
	 *            The y-coordinate of the desired target point
	 * @return <code>this</code> for convenience
	 */
	public final Path lineTo(double x, double y) {
		segments.add(new Segment(Segment.LINE_TO, new Point(x, y)));
		return this;
	}

	/**
	 * Changes the current position. A new {@link Segment} of type
	 * {@link Segment#MOVE_TO} is added to this Path.
	 *
	 * @param x
	 *            The x-coordinate of the desired target point
	 * @param y
	 *            The y-coordinate of the desired target point
	 * @return <code>this</code> for convenience
	 */
	public final Path moveTo(double x, double y) {
		segments.add(new Segment(Segment.MOVE_TO, new Point(x, y)));
		return this;
	}

	/**
	 * Adds a quadratic curve segment from the current position to the specified
	 * end position, using the provided control point as a parametric control
	 * point.
	 *
	 * @param controlX
	 *            The x-coordinate of the control point
	 * @param controlY
	 *            The y-coordinate of the control point
	 * @param x
	 *            The x-coordinate of the desired target point
	 * @param y
	 *            The y-coordinate of the desired target point
	 * @return <code>this</code> for convenience
	 */
	public final Path quadTo(double controlX, double controlY, double x,
			double y) {
		segments.add(new Segment(Segment.QUAD_TO, new Point(controlX, controlY),
				new Point(x, y)));
		return this;
	}

	/**
	 * Resets the path to be empty.
	 *
	 * @return <code>this</code> for convenience
	 */
	public final Path reset() {
		segments.clear();
		return this;
	}

	/**
	 * Sets the winding rule of this {@link Path} to the passed-in integer
	 * constant which is either of:
	 * <ul>
	 * <li>{@link #WIND_NON_ZERO} (default)</li>
	 * <li>{@link #WIND_EVEN_ODD}</li>
	 * </ul>
	 *
	 * @param windingRule
	 *            the new winding rule of this {@link Path}
	 * @return <code>this</code> for convenience
	 */
	public Path setWindingRule(int windingRule) {
		this.windingRule = windingRule;
		return this;
	}

	/**
	 * @see IGeometry#toPath()
	 */
	@Override
	public Path toPath() {
		return getCopy();
	}

	/**
	 * Tests whether this {@link Path} and the given {@link Rectangle} touch,
	 * i.e. they have at least one {@link Point} in common.
	 *
	 * @param r
	 *            the {@link Rectangle} to test for at least one {@link Point}
	 *            in common with this {@link Path}
	 * @return <code>true</code> if this {@link Path} and the {@link Rectangle}
	 *         touch, otherwise <code>false</code>
	 * @see IGeometry#touches(IGeometry)
	 */
	public boolean touches(Rectangle r) {
		return Geometry2AWT.toAWTPath(this)
				.intersects(Geometry2AWT.toAWTRectangle(r));
	}

}
