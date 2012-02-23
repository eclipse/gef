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
package org.eclipse.gef4.geometry.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.planar.Line;
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
	 * Compares two array of {@link Point} for equality.
	 * 
	 * @param p1
	 *            the first array of points to compare
	 * @param p2
	 *            the second array of points to compare
	 * @return <code>true</code> in case both arrays are of the same lenght and
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
	public static final Point[] getCopy(Point[] points) {
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
	public static final double[] getCopy(double[] coordinates) {
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
					&& CurveUtils.getSignedDistance(stack.get(1), stack.get(0),
							points[i]) > 0) {
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

}
