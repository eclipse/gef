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
 *     Matthias Wienand (itemis AG) - javadoc comment enhancements
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.convert.awt;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;

import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Path;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.geometry.planar.RoundedRectangle;
import org.eclipse.gef.geometry.planar.Path.Segment;

/**
 * Utility class to support conversions between GEF's geometry API and AWT
 * Java2D's geometry API.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class Geometry2AWT {

	/**
	 * Converts a GEF {@link AffineTransform} into an AWT
	 * {@link java.awt.geom.AffineTransform}.
	 *
	 * @param t
	 *            the {@link AffineTransform} to transform
	 * @return a new {@link java.awt.geom.AffineTransform} representing an
	 *         identical transformation
	 */
	public static java.awt.geom.AffineTransform toAWTAffineTransform(
			AffineTransform t) {
		double[] matrix = t.getMatrix();
		return new java.awt.geom.AffineTransform(
				Arrays.copyOf(matrix, matrix.length));
	}

	/**
	 * Converts a GEF {@link Line} into an AWT {@link Line2D}.
	 *
	 * @param l
	 *            the {@link Line} to transform
	 * @return a new {@link Line2D}, which is constructed by using the start (
	 *         {@link Line#getP1()}) and end ({@link Line#getP2()})
	 *         {@link Point}s of the passed-in {@link Line}
	 */
	public static Line2D.Double toAWTLine(Line l) {
		return new Line2D.Double(l.getX1(), l.getY1(), l.getX2(), l.getY2());
	}

	/**
	 * Converts a {@link Path} into an equivalent AWT {@link Path2D}.
	 *
	 * @param p
	 *            the {@link Path} to convert
	 * @return a new {@link PathIterator} representing the same path
	 */
	public static Path2D.Double toAWTPath(Path p) {
		Path2D.Double path = new Path2D.Double(
				p.getWindingRule() == Path.WIND_EVEN_ODD ? Path2D.WIND_EVEN_ODD
						: Path2D.WIND_NON_ZERO);
		for (Segment s : p.getSegments()) {
			Point[] points = s.getPoints();
			switch (s.getType()) {
			case Segment.MOVE_TO:
				path.moveTo(points[0].x, points[0].y);
				break;
			case Segment.LINE_TO:
				path.lineTo(points[0].x, points[0].y);
				break;
			case Segment.QUAD_TO:
				path.quadTo(points[0].x, points[0].y, points[1].x, points[1].y);
				break;
			case Segment.CUBIC_TO:
				path.curveTo(points[0].x, points[0].y, points[1].x, points[1].y,
						points[2].x, points[2].y);
				break;
			case Segment.CLOSE:
				path.closePath();
				break;
			default:
				break;
			}
		}
		return path;
	}

	/**
	 * Converts a GEF {@link Point} into an AWT {@link Point2D} with double
	 * precision ({@link java.awt.geom.Point2D.Double}).
	 *
	 * @param p
	 *            the {@link Point} to convert
	 * @return a new {@link Point2D} with double precision (
	 *         {@link java.awt.geom.Point2D.Double}), which is constructed using
	 *         the x and y coordinates of the provided {@link Point}
	 */
	public static final Point2D.Double toAWTPoint(Point p) {
		return new Point2D.Double(p.x, p.y);
	}

	/**
	 * Converts a given array of GEF {@link Point}s into an array of AWT
	 * {@link Point2D}s with double precision (
	 * {@link java.awt.geom.Point2D.Double}).
	 *
	 * @param pts
	 *            the array of {@link Point}s to convert
	 * @return an array of new {@link Point2D}s with double precision (
	 *         {@link java.awt.geom.Point2D.Double}), which are constructed
	 *         using the x and y coordinates of the provided {@link Point}s
	 */
	public static Point2D.Double[] toAWTPoints(Point[] pts) {
		Point2D.Double[] points = new Point2D.Double[pts.length];
		for (int i = 0; i < pts.length; i++) {
			points[i] = toAWTPoint(pts[i]);
		}
		return points;
	}

	/**
	 * <p>
	 * Converts a GEF {@link Rectangle} into an AWT {@link Rectangle2D}. Note
	 * that the new {@link Rectangle2D} is simply constructed by using the
	 * values of the passed-in {@link Rectangle}, not compensating the fact that
	 * the width and height of a rectangle are interpreted differently in Java2D
	 * and GEF.
	 * </p>
	 * <p>
	 * In Java2D, the width and height of a {@link Rectangle2D} are oversized by
	 * exactly 1, i.e. the right and bottom edges of a {@link Rectangle2D} are
	 * not regarded to belong to the visual object.
	 * </p>
	 * <p>
	 * If you wish to retain this interpretation, you have to modify the
	 * passed-in {@link Rectangle} object as follows:<br>
	 * <code>rectangle2d = Geometry2AWT.toAWTRectangle(rectangle.getExpanded(0, 0, 1, 1));</code>
	 * <br>
	 * (see also {@link Rectangle#getExpanded(double, double, double, double)})
	 * </p>
	 *
	 * @param r
	 *            the {@link Rectangle} to convert
	 * @return a new {@link Rectangle2D}, which is constructed using the x, y,
	 *         width, and height values of the passed-in {@link Rectangle}.
	 */
	public static final Rectangle2D.Double toAWTRectangle(Rectangle r) {
		return new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(),
				r.getHeight());
	}

	/**
	 * <p>
	 * Converts a GEF {@link RoundedRectangle} into an AWT
	 * {@link RoundRectangle2D}. Note that the new {@link RoundRectangle2D} is
	 * simply constructed by using the values of the passed in
	 * {@link RoundedRectangle}, not compensating the fact that the width and
	 * height of a rectangle are interpreted differently in Java2D and GEF.
	 * </p>
	 * <p>
	 * In Java2D, the width and height of a {@link RoundRectangle2D} are
	 * oversized by exactly 1, i.e. the right and bottom edges of a
	 * {@link RoundRectangle2D} are not regarded to belong to the visual object.
	 * </p>
	 * <p>
	 * If you wish to retain this interpretation, you have to modify the
	 * passed-in {@link RoundedRectangle} object as follows:<br>
	 * <code>roundRectangle2d = Geometry2AWT.toAWTRoundRectangle(roundedRectangle.getExpanded(0, 0, 1, 1));</code>
	 * <br>
	 * (see also
	 * {@link RoundedRectangle#getExpanded(double, double, double, double)})
	 * </p>
	 *
	 * @param r
	 *            the {@link RoundedRectangle} to convert
	 * @return a new {@link RoundRectangle2D}, which is constructed using the x,
	 *         y, width, height, arcWidth, and arcHeight values of the passed in
	 *         {@link RoundedRectangle}
	 */
	public static RoundRectangle2D.Double toAWTRoundRectangle(
			RoundedRectangle r) {
		return new RoundRectangle2D.Double(r.getX(), r.getY(), r.getWidth(),
				r.getHeight(), r.getArcWidth() / 2, r.getArcHeight() / 2);
	}

	private Geometry2AWT() {
		// this class should not be instantiated by clients
	}

}
