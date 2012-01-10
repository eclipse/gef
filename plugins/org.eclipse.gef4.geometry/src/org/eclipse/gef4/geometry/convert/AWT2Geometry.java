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

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.shapes.Line;
import org.eclipse.gef4.geometry.shapes.Rectangle;
import org.eclipse.gef4.geometry.shapes.RoundedRectangle;
import org.eclipse.gef4.geometry.transform.AffineTransform;

/**
 * Utility class to support conversions from AWT's geometry API to GEF4's
 * geometry API.
 * 
 * @author anyssen
 * 
 */
public class AWT2Geometry {

	/**
	 * Converts a AWT {@link java.awt.geom.AffineTransform} into a GEF4
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
	 * Converts a AWT {@link Line2D} into a GEF4 {@link Line}.
	 * 
	 * @param l
	 *            the {@link Line2D} to transform
	 * @return a new {@link Line}, which is constructed by using the start (
	 *         {@link Line2D#getP1()}) and end ({@link Line2D#getP2()}) points
	 *         of the passed in {@link Line2D}
	 */
	public static final Line toLine(Line2D l) {
		return new Line(AWT2Geometry.toPoint(l.getP1()), AWT2Geometry.toPoint(l
				.getP2()));
	}

	/**
	 * Converts a AWT {@link Point2D} into a GEF4 {@link Point}.
	 * 
	 * @param p
	 *            the {@link Point2D} to transform
	 * @return a new {@link Point}, which is constructed using the x and y
	 *         values of the passed in {@link Point2D}
	 */
	public static final Point toPoint(Point2D p) {
		return new Point(p.getX(), p.getY());
	}

	/**
	 * Converts a given array of AWT {@link Point2D}s into a an array of GEF4
	 * {@link Point}s.
	 * 
	 * @param pts
	 *            the array of {@link Point2D}s to transform
	 * @return an array containing new {@link Point}s, which are constructed by
	 *         using the x and y values of the passed in {@link Point2D}s
	 */
	public static Point[] toPoints(Point2D[] pts) {
		Point[] points = new Point[pts.length];
		for (int i = 0; i < pts.length; i++) {
			points[i] = AWT2Geometry.toPoint(pts[i]);
		}
		return points;
	}

	/**
	 * Converts a AWT {@link Rectangle2D} into a GEF4 {@link Rectangle}. Note
	 * that the new {@link Rectangle} is simply constructed by using the values
	 * of the passed in {@link Rectangle2D}, not compensating the fact that the
	 * width and height of a rectangle are interpreted differently in Java2D and
	 * GEF4.
	 * 
	 * @param r
	 *            the {@link Rectangle2D} to convert
	 * @return a new {@link Rectangle}, which is constructed using the x, y,
	 *         width, and height values of the passed in {@link Rectangle2D}.
	 * 
	 * 
	 */
	public static final Rectangle toRectangle(Rectangle2D r) {
		return new Rectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * Converts a AWT {@link RoundRectangle2D} into a GEF4
	 * {@link RoundedRectangle}. Note that the new {@link RoundedRectangle} is
	 * simply constructed by using the values of the passed in
	 * {@link RoundRectangle2D}, not compensating the fact that the width and
	 * height of a rectangle are interpreted differently in Java2D and GEF4.
	 * 
	 * @param r
	 *            the {@link RoundRectangle2D} to convert
	 * @return a new {@link RoundedRectangle}, which is constructed using the x,
	 *         y, width, height, arcWidth, and arcHeight values of the passed in
	 *         {@link RoundRectangle2D}
	 */
	public static RoundedRectangle toRoundedRectangle(RoundRectangle2D r) {
		return new RoundedRectangle(r.getX(), r.getY(), r.getWidth(),
				r.getHeight(), r.getArcWidth(), r.getArcHeight());
	}

	private AWT2Geometry() {
		// this class should not be instantiated by clients
	}

}
