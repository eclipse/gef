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

import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Path;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.geometry.planar.RoundedRectangle;

/**
 * Utility class to support conversions from AWT's geometry API to GEF's
 * geometry API.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class AWT2Geometry {

	/**
	 * Converts an AWT {@link java.awt.geom.AffineTransform} into a GEF
	 * {@link AffineTransform}
	 *
	 * @param t
	 *            the {@link java.awt.geom.AffineTransform} to transform
	 * @return a new {@link AffineTransform} representing an identical
	 *         transformation
	 */
	public static AffineTransform toAffineTransform(
			java.awt.geom.AffineTransform t) {
		double[] matrix = new double[6];
		t.getMatrix(matrix);
		return new AffineTransform(matrix);
	}

	/**
	 * Converts an AWT {@link Line2D} into a GEF {@link Line}.
	 *
	 * @param l
	 *            the {@link Line2D} to convert
	 * @return a new {@link Line}, which is constructed by using the start (
	 *         {@link Line2D#getP1()}) and end ({@link Line2D#getP2()}) points
	 *         of the passed-in {@link Line2D}
	 */
	public static final Line toLine(Line2D l) {
		return new Line(AWT2Geometry.toPoint(l.getP1()),
				AWT2Geometry.toPoint(l.getP2()));
	}

	/**
	 * Converts an AWT {@link Path2D} into a GEF {@link Path}.
	 *
	 * @param p
	 *            the {@link Path2D} to convert
	 * @return a new {@link Path}, which is constructed with the same winding
	 *         rule and segments as the passed in {@link Path2D}.
	 */
	public static Path toPath(Path2D p) {
		PathIterator iterator = p.getPathIterator(null);
		Path path = new Path(p.getWindingRule() == Path2D.WIND_NON_ZERO
				? Path.WIND_NON_ZERO : Path.WIND_EVEN_ODD);
		while (!iterator.isDone()) {
			double[] segment = new double[6];
			int type = iterator.currentSegment(segment);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				path.moveTo(segment[0], segment[1]);
				break;
			case PathIterator.SEG_LINETO:
				path.lineTo(segment[0], segment[1]);
				break;
			case PathIterator.SEG_QUADTO:
				path.quadTo(segment[0], segment[1], segment[2], segment[3]);
				break;
			case PathIterator.SEG_CUBICTO:
				path.cubicTo(segment[0], segment[1], segment[2], segment[3],
						segment[4], segment[5]);
				break;
			case PathIterator.SEG_CLOSE:
				path.close();
				break;
			}
			iterator.next();
		}
		return path;
	}

	/**
	 * Converts an AWT {@link Point2D} into a GEF {@link Point}.
	 *
	 * @param p
	 *            the {@link Point2D} to transform
	 * @return a new {@link Point}, which is constructed using the x and y
	 *         coordinates of the passed-in {@link Point2D}
	 */
	public static final Point toPoint(Point2D p) {
		return new Point(p.getX(), p.getY());
	}

	/**
	 * Converts a given array of AWT {@link Point2D}s into a an array of GEF
	 * {@link Point}s.
	 *
	 * @param pts
	 *            the array of {@link Point2D}s to transform
	 * @return an array containing new {@link Point}s, which are constructed by
	 *         using the x and y coordinates of the passed-in {@link Point2D}s
	 */
	public static Point[] toPoints(Point2D[] pts) {
		Point[] points = new Point[pts.length];
		for (int i = 0; i < pts.length; i++) {
			points[i] = AWT2Geometry.toPoint(pts[i]);
		}
		return points;
	}

	/**
	 * <p>
	 * Converts an AWT {@link Rectangle2D} into a GEF {@link Rectangle}. Note
	 * that the new {@link Rectangle} is simply constructed by using the values
	 * of the passed-in {@link Rectangle2D}, not compensating the fact that the
	 * width and height of a rectangle are interpreted differently in Java2D and
	 * GEF.
	 * </p>
	 * <p>
	 * In Java2D, the width and height of a {@link Rectangle2D} are oversized by
	 * exactly 1, i.e. the right and bottom edges of a {@link Rectangle2D} are
	 * not regarded to belong to the visual object.
	 * </p>
	 * <p>
	 * If you wish to retain this interpretation, you have to modify the
	 * resulting GEF {@link Rectangle} object as follows:<br>
	 * <code>rectangle.shrink(0, 0, 1, 1);</code><br>
	 * (see also {@link Rectangle#shrink(double, double, double, double)},
	 * {@link Rectangle#getShrinked(double, double, double, double)})
	 * </p>
	 *
	 * @param r
	 *            the {@link Rectangle2D} to convert
	 * @return a new {@link Rectangle}, which is constructed using the x, y,
	 *         width, and height values of the passed-in {@link Rectangle2D}.
	 *
	 *
	 */
	public static final Rectangle toRectangle(Rectangle2D r) {
		return new Rectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * <p>
	 * Converts an AWT {@link RoundRectangle2D} into a GEF
	 * {@link RoundedRectangle}. Note that the new {@link RoundedRectangle} is
	 * simply constructed by using the values of the passed-in
	 * {@link RoundRectangle2D}, not compensating the fact that the width and
	 * height of a rectangle are interpreted differently in Java2D and GEF.
	 * </p>
	 * <p>
	 * In Java2D, the width and height of a {@link RoundRectangle2D} are
	 * oversized by exactly 1, i.e. the right and bottom edges of a
	 * {@link RoundRectangle2D} are not regarded to belong to the visual object.
	 * </p>
	 * <p>
	 * If you wish to retain this interpretation, you have to modify the
	 * resulting GEF {@link RoundedRectangle} object as follows:<br>
	 * <code>roundedRectangle.shrink(0, 0, 1, 1);</code><br>
	 * (see also {@link RoundedRectangle#shrink(double, double, double, double)}
	 * , {@link RoundedRectangle#getShrinked(double, double, double, double)})
	 * </p>
	 *
	 * @param r
	 *            the {@link RoundRectangle2D} to convert
	 * @return a new {@link RoundedRectangle}, which is constructed using the x,
	 *         y, width, height, arcWidth, and arcHeight values of the passed in
	 *         {@link RoundRectangle2D}
	 */
	public static RoundedRectangle toRoundedRectangle(RoundRectangle2D r) {
		return new RoundedRectangle(r.getX(), r.getY(), r.getWidth(),
				r.getHeight(), r.getArcWidth() * 2, r.getArcHeight() * 2);
	}

	private AWT2Geometry() {
		// this class should not be instantiated by clients
	}

}
