/*******************************************************************************
 * Copyright (c) 2012, 2016 itemis AG and others.
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
package org.eclipse.gef.geometry.convert.swt;

import org.eclipse.gef.geometry.convert.awt.Geometry2AWT;
import org.eclipse.gef.geometry.internal.utils.PointListUtils;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Path;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polygon;
import org.eclipse.gef.geometry.planar.Polyline;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.geometry.planar.Region;
import org.eclipse.gef.geometry.planar.Ring;
import org.eclipse.swt.graphics.PathData;

/**
 * Utility class to support conversions between GEF's geometry API and SWT's
 * geometry classes.
 * 
 * @author anyssen
 * 
 */
public class Geometry2SWT {

	/**
	 * Converts the given path into an SWT {@link PathData} representation.
	 * 
	 * @param p
	 *            the {@link Path} to convert
	 * 
	 * @return The {@link PathData} representing this path.
	 */
	public static final PathData toSWTPathData(Path p) {
		return AWT2SWT.toSWTPathData(Geometry2AWT.toAWTPath(p).getPathIterator(null));
	}

	/**
	 * Creates a new SWT {@link org.eclipse.swt.graphics.Point Point} from this
	 * Point.
	 * 
	 * @param p
	 *            the {@link Point} to convert
	 * 
	 * @return A new SWT Point
	 */
	public static final org.eclipse.swt.graphics.Point toSWTPoint(Point p) {
		return new org.eclipse.swt.graphics.Point((int) p.x, (int) p.y);
	}

	/**
	 * Returns an integer array of dimension 4, whose values represent the
	 * integer-based coordinates of this {@link Line}'s start and end point.
	 * 
	 * @param l
	 *            the {@link Line} to convert
	 * 
	 * @return an array containing integer values, which are obtained by casting
	 *         x1, y1, x2, y2
	 */
	public static final int[] toSWTPointArray(Line l) {
		return PointListUtils.toIntegerArray(PointListUtils.toCoordinatesArray(l.getPoints()));
	}

	/**
	 * <p>
	 * Returns an integer array, which represents the sequence of coordinates of
	 * the {@link Point}s that make up this
	 * {@link AbstractPointListBasedGeometry}. The x and y coordinate values are
	 * transfered to integer values by either applying
	 * {@link Math#floor(double)} or {@link Math#ceil(double)} to them,
	 * dependent on their relative position to the centroid of this
	 * {@link AbstractPointListBasedGeometry} (see {@link #getCentroid()}).
	 * </p>
	 * <p>
	 * If the x coordinate of a {@link Point} is smaller than the x coordinate
	 * of the centroid, then the x coordinate of that {@link Point} is rounded
	 * down. Otherwise it is rounded up. Accordingly, if the y coordinate of a
	 * {@link Point} is smaller than the y coordinate of the centroid, it is
	 * rounded down. Otherwise, it is rounded up.
	 * </p>
	 * 
	 * @return an integer array of the x and y coordinates of this
	 *         {@link AbstractPointListBasedGeometry}
	 */
	private static final int[] toSWTPointArray(Point[] points, Point centroid) {
		int[] SWTPointArray = new int[points.length * 2];
		for (int i = 0; i < points.length; i++) {
			SWTPointArray[2 * i] = (int) (points[i].x < centroid.x ? Math.floor(points[i].x) : Math.ceil(points[i].x));
			SWTPointArray[2 * i
					+ 1] = (int) (points[i].y < centroid.y ? Math.floor(points[i].y) : Math.ceil(points[i].y));
		}
		return SWTPointArray;
	}

	/**
	 * <p>
	 * Returns an integer array, which represents the sequence of coordinates of
	 * the {@link Point}s that make up this {@link Polygon}. The x and y
	 * coordinate values are transfered to integer values by either applying
	 * {@link Math#floor(double)} or {@link Math#ceil(double)} to them,
	 * dependent on their relative position to the centroid of this
	 * {@link Polygon} (see {@link Polygon#getCentroid()}).
	 * </p>
	 * <p>
	 * If the x coordinate of a {@link Point} is smaller than the x coordinate
	 * of the centroid, then the x coordinate of that {@link Point} is rounded
	 * down. Otherwise it is rounded up. Accordingly, if the y coordinate of a
	 * {@link Point} is smaller than the y coordinate of the centroid, it is
	 * rounded down. Otherwise, it is rounded up.
	 * </p>
	 * 
	 * @param p
	 *            the {@link Polygon} to convert
	 * 
	 * @return an integer array of the x and y coordinates of this
	 *         {@link Polygon}
	 */
	public static final int[] toSWTPointArray(Polygon p) {
		return toSWTPointArray(p.getPoints(), p.getCentroid());
	}

	/**
	 * <p>
	 * Returns an integer array, which represents the sequence of coordinates of
	 * the {@link Point}s that make up this {@link Polyline}. The x and y
	 * coordinate values are transfered to integer values by either applying
	 * {@link Math#floor(double)} or {@link Math#ceil(double)} to them,
	 * dependent on their relative position to the centroid of this
	 * {@link Polyline} (see {@link Polyline#getCentroid()}).
	 * </p>
	 * <p>
	 * If the x coordinate of a {@link Point} is smaller than the x coordinate
	 * of the centroid, then the x coordinate of that {@link Point} is rounded
	 * down. Otherwise it is rounded up. Accordingly, if the y coordinate of a
	 * {@link Point} is smaller than the y coordinate of the centroid, it is
	 * rounded down. Otherwise, it is rounded up.
	 * </p>
	 * 
	 * @param p
	 *            the {@link Polyline} to convert
	 * 
	 * @return an integer array of the x and y coordinates of this
	 *         {@link Polyline}
	 */
	public static final int[] toSWTPointArray(Polyline p) {
		return toSWTPointArray(p.getPoints(), p.getCentroid());
	}

	/**
	 * Converts a {@link Rectangle} into an
	 * {@link org.eclipse.swt.graphics.Rectangle}. Note that as
	 * {@link org.eclipse.swt.graphics.Rectangle} is integer-based, this implies
	 * a loss of precision. The returned rectangle is the smallest
	 * integer-precision representation that fully contains this
	 * {@link Rectangle}.
	 * 
	 * @param r
	 *            the {@link Rectangle} to convert
	 * 
	 * @return An {@link org.eclipse.swt.graphics.Rectangle} representation of
	 *         this {@link Rectangle}.
	 */
	public static final org.eclipse.swt.graphics.Rectangle toSWTRectangle(Rectangle r) {
		return new org.eclipse.swt.graphics.Rectangle((int) Math.floor(r.getX()), (int) Math.floor(r.getY()),
				(int) Math.ceil(r.getWidth() + r.getX() - Math.floor(r.getX())),
				(int) Math.ceil(r.getHeight() + r.getY() - Math.floor(r.getY())));
	}

	/**
	 * <p>
	 * Constructs a new {@link org.eclipse.swt.graphics.Region} that covers the
	 * same area as this {@link Region}. This is to ease the use of a
	 * {@link Region} for clipping:
	 * </p>
	 * 
	 * <p>
	 * <code>gc.setClipping(region.toSWTRegion());</code>
	 * </p>
	 * 
	 * @param r
	 *            the {@link Region} to convert
	 * 
	 * @return a new {@link org.eclipse.swt.graphics.Region} that covers the
	 *         same area as this {@link Region}
	 */
	public static final org.eclipse.swt.graphics.Region toSWTRegion(Region r) {
		org.eclipse.swt.graphics.Region swtRegion = new org.eclipse.swt.graphics.Region();

		for (Rectangle rect : r.getShapes()) {
			swtRegion.add(toSWTRectangle(rect));
		}

		return swtRegion;
	}

	/**
	 * Constructs a new {@link org.eclipse.swt.graphics.Region} from this
	 * {@link Ring}. The SWT {@link Region} can be used as a clipping area as
	 * follows: <code>gc.setClipping(ring.toSWTRegion());</code>
	 * 
	 * @param r
	 *            the {@link Region} to convert
	 * 
	 * @return SWT {@link Region} representation of this {@link Ring}
	 */
	public static org.eclipse.swt.graphics.Region toSWTRegion(Ring r) {
		org.eclipse.swt.graphics.Region region = new org.eclipse.swt.graphics.Region();

		// TODO: Add the individual outlines in xor mode to the SWT Region so
		// that voids are correctly converted, too.
		for (Polyline p : r.getOutlines()) {
			region.add(toSWTPointArray(p));
		}

		return region;
	}

	private Geometry2SWT() {
		// this class should not be instantiated by clients
	}
}
