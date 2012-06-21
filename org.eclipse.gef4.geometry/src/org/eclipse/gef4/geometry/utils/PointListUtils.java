/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.euclidean.Straight;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Polyline;
import org.eclipse.gef4.geometry.planar.Rectangle;

/**
 * Common utilities for point manipulations as needed e.g. within
 * {@link Polygon} and {@link Polyline}.
 * 
 * @author anyssen
 */
public class PointListUtils {

	/**
	 * Compares two arrays of {@link Point} for equality.
	 * 
	 * TODO: What is the benefit over using Arrays.equals()?
	 * 
	 * @param p1
	 *            the first array of points to compare
	 * @param p2
	 *            the second array of points to compare
	 * @return <code>true</code> in case both arrays are of the same length and
	 *         for each index <code>i</code> it holds that <code>p1[i]</code>
	 *         equals <code>p2[i]</code>, <code>false</code> otherwise
	 */
	public static boolean equals(Point[] p1, Point[] p2) {
		if (p1.length != p2.length) {
			return false;
		}
		for (int i = 0; i < p1.length; i++) {
			if (!p1[i].equals(p2[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Compares two arrays of {@link Point} for reverse equality, i.e. if one
	 * array is the reverse of the other array.
	 * 
	 * @param p1
	 *            the first array of {@link Point} to compare
	 * @param p2
	 *            the second array of {@link Point} to compare
	 * @return <code>true</code> in case one array is the reverse of the other
	 *         array, <code>false</code> otherwise
	 */
	public static boolean equalsReverse(Point[] p1, Point[] p2) {
		if (p1.length != p2.length) {
			return false;
		}
		for (int i = 0; i < p1.length; i++) {
			if (!p1[i].equals(p2[p1.length - i - 1])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the smallest {@link Rectangle} that encloses all {@link Point}s
	 * in the given sequence. Note that the right and bottom borders of a
	 * {@link Rectangle} are regarded as being part of the {@link Rectangle}.
	 * 
	 * @param points
	 *            a sequence of {@link Point}s which should all be contained in
	 *            the to be computed {@link Rectangle}
	 * @return a new {@link Rectangle}, which is the smallest {@link Rectangle}
	 *         that contains all given {@link Point}s
	 */
	public static Rectangle getBounds(Point[] points) {
		// compute the top left and bottom right points by means of the maximum
		// and minimum x and y coordinates
		if (points.length == 0) {
			return new Rectangle();
		}
		// calculate bounds
		Point topLeft = points[0];
		Point bottomRight = points[0];
		for (Point p : points) {
			topLeft = Point.min(topLeft, p);
			bottomRight = Point.max(bottomRight, p);
		}
		return new Rectangle(topLeft, bottomRight);
	}

	/**
	 * Copies an array of points, by copying each point contained in the array.
	 * 
	 * @param points
	 *            the array of {@link Point}s to copy
	 * @return a new array, which contains copies of the given {@link Point}s at
	 *         the respective index positions
	 */
	public static final Point[] copy(Point[] points) {
		Point[] copy = new Point[points.length];
		for (int i = 0; i < points.length; i++) {
			copy[i] = points[i].getCopy();
		}
		return copy;
	}

	/**
	 * Copies an array of coordinates.
	 * 
	 * @param coordinates
	 *            the array of coordinates to copy
	 * @return a new array containing identical coordinates
	 */
	public static final double[] copy(double[] coordinates) {
		double[] copy = new double[coordinates.length];
		for (int i = 0; i < coordinates.length; i++) {
			copy[i] = coordinates[i];
		}
		return copy;
	}

	private static void swapLowestYPointToStart(Point[] points) {
		int minIdx = 0;
		Point min = points[minIdx];
		for (int i = 1; i < points.length; i++) {
			if (points[i].y < min.y || points[i].y == min.y
					&& points[i].x < min.x) {
				min = points[i];
				minIdx = i;
			}
		}
		Point tmp = points[0];
		points[0] = points[minIdx];
		points[minIdx] = tmp;
	}

	private static void sortPointsByAngleToStart(Point[] points) {
		final Point p0 = points[0];
		Arrays.sort(points, 1, points.length, new Comparator<Point>() {
			public int compare(Point p1, Point p2) {
				double m1 = (p1.x - p0.x) / (-p1.y + p0.y);
				double m2 = (p2.x - p0.x) / (-p2.y + p0.y);
				if (m1 < m2) {
					return -1;
				}
				return 1;
			}
		});
	}

	private static ArrayList<Point> initializeStack(Point[] points) {
		ArrayList<Point> stack = new ArrayList<Point>();
		if (points.length > 0) {
			stack.add(0, points[0]);
			if (points.length > 1) {
				stack.add(0, points[1]);
				if (points.length > 2) {
					stack.add(0, points[2]);
				}
			}
		}
		return stack;
	}

	private static void expandToFullConvexHull(ArrayList<Point> stack,
			Point[] points) {
		for (int i = 3; i < points.length; i++) {
			// do always turn right
			while (stack.size() > 2
					&& Straight.getSignedDistanceCCW(stack.get(1),
							stack.get(0), points[i]) > 0) {
				stack.remove(0);
			}
			stack.add(0, points[i]);
		}
	}

	/**
	 * Computes the convex hull of the given set of {@link Point}s using the
	 * Graham scan algorithm.
	 * 
	 * @param points
	 *            the set of {@link Point}s to calculate the convex hull for
	 * @return the convex hull of the given set of {@link Point}s
	 */
	public static Point[] getConvexHull(Point[] points) {
		// do a graham scan to find the convex hull of the given set of points
		swapLowestYPointToStart(points);
		sortPointsByAngleToStart(points);
		ArrayList<Point> convexHull = initializeStack(points);
		expandToFullConvexHull(convexHull, points);
		return convexHull.toArray(new Point[] {});
	}

	/**
	 * Converts a given array of {@link Point} into an array of doubles
	 * containing the x and y coordinates of the given points, where the x and y
	 * coordinates of the n-th {@link Point} can be found at positions 2*n and
	 * 2*n+1.
	 * 
	 * @param points
	 *            an array of {@link Point}s to convert
	 * @return a new array of doubles, containing the x and y coordinates of the
	 *         given {@link Point}s
	 */
	public static double[] toCoordinatesArray(Point[] points) {
		double[] coordinates = new double[points.length * 2];
		for (int i = 0; i < points.length; i++) {
			coordinates[i * 2] = points[i].x;
			coordinates[i * 2 + 1] = points[i].y;
		}
		return coordinates;
	}

	/**
	 * Converts a given array of x/y coordinate values into an array of
	 * {@link Point}s.
	 * 
	 * @param coordinates
	 * @return a new array of {@link Point}s, representing the given x and y
	 *         coordinates
	 */
	public static Point[] toPointsArray(double[] coordinates) {
		if (coordinates.length % 2 != 0) {
			throw new IllegalArgumentException(
					"The coordinates array may not have an odd number of items.");
		}
		Point[] points = new Point[coordinates.length / 2];
		for (int i = 0; i < points.length; i++) {
			points[i] = new Point(coordinates[2 * i], coordinates[2 * i + 1]);
		}
		return points;
	}

	/**
	 * Converts an array of double values into an array of integer values by
	 * casting them.
	 * 
	 * @param doubles
	 *            an array of doubles to convert
	 * @return a new array of integer values, which is created by casting the
	 *         double values
	 */
	public static int[] toIntegerArray(double[] doubles) {
		int[] ints = new int[doubles.length];
		for (int i = 0; i < doubles.length; i++) {
			ints[i] = (int) doubles[i];
		}
		return ints;
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

	/**
	 * Translates an array of {@link Point}s by translating each individual
	 * point by a given x and y offset.
	 * 
	 * @param points
	 *            an array of points to translate
	 * @param dx
	 *            the x offset to translate each {@link Point} by
	 * @param dy
	 *            the y offset to translate each {@link Point} by
	 */
	public static void translate(Point[] points, double dx, double dy) {
		for (int i = 0; i < points.length; i++) {
			points[i].x += dx;
			points[i].y += dy;
		}
	}

	private PointListUtils() {
		// this class should not be instantiated by clients
	}

	/**
	 * Transforms a sequence of {@link Line}s into a list of {@link Point}s.
	 * Consecutive {@link Line}s are expected to share one of their end
	 * {@link Point}s. The start {@link Point}s of the {@link Line}s are
	 * returned. Additionally, the end {@link Point} of the last {@link Line} is
	 * returned, too if the given boolean flag <code>open</code> is set to
	 * <code>false</code>.
	 * 
	 * @param segmentsArray
	 * @param open
	 *            indicates whether to omit the end {@link Point} of the last
	 *            {@link Line}
	 * @return the start {@link Point}s of the {@link Line}s and the end
	 *         {@link Point} of the last {@link Line} according to
	 *         <code>open</code>
	 */
	public static Point[] toPointsArray(Line[] segmentsArray, boolean open) {
		Point[] points = new Point[segmentsArray.length + (open ? 0 : 1)];

		for (int i = 0; i < segmentsArray.length; i++) {
			points[i] = segmentsArray[i].getP1();
		}

		if (!open) {
			points[points.length - 1] = segmentsArray[segmentsArray.length - 1]
					.getP2();
		}

		return points;
	}

	/**
	 * Computes the centroid of the given {@link Point}s. The centroid is the
	 * "center of gravity", i.e. assuming the {@link Polygon} spanned by the
	 * {@link Point}s is made of a material of constant density, it will be in a
	 * balanced state, if you put it on a pin that is placed exactly on its
	 * centroid.
	 * 
	 * @param points
	 * @return the center {@link Point} (or centroid) of the given {@link Point}
	 *         s
	 */
	public static Point computeCentroid(Point... points) {
		if (points.length == 0) {
			return null;
		} else if (points.length == 1) {
			return points[0].getCopy();
		}

		double cx = 0, cy = 0, a, sa = 0;
		for (int i = 0; i < points.length - 1; i++) {
			a = points[i].x * points[i + 1].y - points[i].y * points[i + 1].x;
			sa += a;
			cx += (points[i].x + points[i + 1].x) * a;
			cy += (points[i].y + points[i + 1].y) * a;
		}

		// closing segment
		a = points[points.length - 2].x * points[points.length - 1].y
				- points[points.length - 2].y * points[points.length - 1].x;
		sa += a;
		cx += (points[points.length - 2].x + points[points.length - 1].x) * a;
		cy += (points[points.length - 2].x + points[points.length - 1].x) * a;

		return new Point(cx / (3 * sa), cy / (3 * sa));
	}

	/**
	 * Rotates (in-place) the given {@link Point}s counter-clock-wise (CCW) by
	 * the specified {@link Angle} around the given center {@link Point}.
	 * 
	 * @param points
	 * @param angle
	 * @param cx
	 * @param cy
	 */
	public static void rotateCCW(Point[] points, Angle angle, double cx,
			double cy) {
		translate(points, -cx, -cy);
		for (Point p : points) {
			Point np = new Vector(p).rotateCCW(angle).toPoint();
			p.x = np.x;
			p.y = np.y;
		}
		translate(points, cx, cy);
	}

	/**
	 * Rotates (in-place) the given {@link Point}s clock-wise (CW) by the
	 * specified {@link Angle} around the given center {@link Point}.
	 * 
	 * @param points
	 * @param angle
	 * @param cx
	 * @param cy
	 */
	public static void rotateCW(Point[] points, Angle angle, double cx,
			double cy) {
		translate(points, -cx, -cy);
		for (Point p : points) {
			Point np = new Vector(p).rotateCW(angle).toPoint();
			p.x = np.x;
			p.y = np.y;
		}
		translate(points, cx, cy);
	}

	/**
	 * Scales the given array of {@link Point}s by the given x and y scale
	 * factors around the given center {@link Point} (cx, cy).
	 * 
	 * @param points
	 * @param fx
	 * @param fy
	 * @param cx
	 * @param cy
	 */
	public static void scale(Point[] points, double fx, double fy, double cx,
			double cy) {
		translate(points, -cx, -cy);
		for (Point p : points) {
			p.scale(fx, fy);
		}
		translate(points, cx, cy);
	}

}
