/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.convert;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;

/**
 * Utility class to support conversions between GEF4's geometry API and AWT
 * Java2D's geometry API.
 * 
 * @author anyssen
 * 
 */
public class Geometry2AWT {

	/**
	 * Converts a GEF {@link AffineTransform} into a AWT
	 * {@link java.awt.geom.AffineTransform}
	 * 
	 * @param t
	 *            the {@link AffineTransform} to transform
	 * @return a new {@link java.awt.geom.AffineTransform} representing an
	 *         identical transformation
	 */
	public static java.awt.geom.AffineTransform toAWTAffineTransform(
			AffineTransform t) {
		double[] matrix = t.getMatrix();
		return new java.awt.geom.AffineTransform(Arrays.copyOf(matrix,
				matrix.length));
	}

	/**
	 * Converts a GEF4 {@link Line} into a AWT {@link Line2D}.
	 * 
	 * @param l
	 *            the {@link Line} to transform
	 * @return a new {@link Line2D}, which is constructed by using the start (
	 *         {@link Line#getP1()}) and end ({@link Line#getP2()}) points of
	 *         the passed in {@link Line}
	 */
	public static Line2D.Double toAWTLine(Line l) {
		return new Line2D.Double(l.getX1(), l.getY1(), l.getX2(), l.getY2());
	}

	/**
	 * Converts a GEF4 {@link Point} into a AWT {@link Point2D} with double
	 * precision.
	 * 
	 * @param p
	 *            the {@link Point} to convert
	 * @return a new {@link Point2D}, which is constructed using the x and y
	 *         values of the provided {@link Point}
	 */
	public static final Point2D.Double toAWTPoint(Point p) {
		return new Point2D.Double(p.x, p.y);
	}

	/**
	 * Converts a given array of GEF4 {@link Point}s into an array of AWT
	 * {@link Point2D}s.
	 * 
	 * @param pts
	 *            the array of {@link Point}s to convert
	 * @return an array of new {@link Point2D}s, which are constructed using the
	 *         x and y values of the provided {@link Point}s
	 */
	public static Point2D.Double[] toAWTPoints(Point[] pts) {
		Point2D.Double[] points = new Point2D.Double[pts.length];
		for (int i = 0; i < pts.length; i++) {
			points[i] = toAWTPoint(pts[i]);
		}
		return points;
	}

	/**
	 * Converts a GEF4 {@link Rectangle} into a AWT {@link Rectangle2D}. Note
	 * that the new {@link Rectangle2D} is simply constructed by using the
	 * values of the passed in {@link Rectangle}, not compensating the fact that
	 * the width and height of a rectangle are interpreted differently in Java2D
	 * and GEF4.
	 * 
	 * @param r
	 *            the {@link Rectangle} to convert
	 * @return a new {@link Rectangle2D}, which is constructed using the x, y,
	 *         width, and height values of the passed in {@link Rectangle}.
	 * 
	 * 
	 */
	public static final Rectangle2D.Double toAWTRectangle(Rectangle r) {
		return new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(),
				r.getHeight());
	}

	/**
	 * Converts a GEF4 {@link RoundedRectangle} into a AWT
	 * {@link RoundRectangle2D}. Note that the new {@link RoundRectangle2D} is
	 * simply constructed by using the values of the passed in
	 * {@link RoundedRectangle}, not compensating the fact that the width and
	 * height of a rectangle are interpreted differently in Java2D and GEF4.
	 * 
	 * @param r
	 *            the {@link RoundedRectangle} to convert
	 * @return a new {@link RoundRectangle2D}, which is constructed using the x,
	 *         y, width, height, arcWidth, and arcHeight values of the passed in
	 *         {@link RoundedRectangle}
	 */
	public static RoundRectangle2D toAWTRoundRectangle(RoundedRectangle r) {
		return new RoundRectangle2D.Double(r.getX(), r.getY(), r.getWidth(),
				r.getHeight(), r.getArcWidth(), r.getArcHeight());
	}

	private Geometry2AWT() {
		// this class should not be instantiated by clients
	}

}
