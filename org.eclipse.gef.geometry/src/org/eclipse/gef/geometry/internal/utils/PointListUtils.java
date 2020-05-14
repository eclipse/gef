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
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.internal.utils;

import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polygon;
import org.eclipse.gef.geometry.planar.Polyline;

/**
 * Common utilities for point manipulations as needed e.g. within
 * {@link Polygon} and {@link Polyline}.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class PointListUtils {

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
		if (points == null || points.length == 0) {
			return new double[] {};
		}

		double[] coordinates = new double[points.length * 2];
		for (int i = 0; i < points.length; i++) {
			coordinates[i * 2] = points[i].x;
			coordinates[i * 2 + 1] = points[i].y;
		}
		return coordinates;
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
		if (doubles == null || doubles.length == 0) {
			return new int[] {};
		}

		int[] ints = new int[doubles.length];
		for (int i = 0; i < doubles.length; i++) {
			ints[i] = (int) doubles[i];
		}
		return ints;
	}

	/**
	 * Converts a given array of x/y coordinate values into an array of
	 * {@link Point}s.
	 *
	 * @param coordinates
	 *            The array of coordinates.
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
	 * Transforms a sequence of {@link Line}s into a list of {@link Point}s.
	 * Consecutive {@link Line}s are expected to share one of their end
	 * {@link Point}s. The start {@link Point}s of the {@link Line}s are
	 * returned. Additionally, the end {@link Point} of the last {@link Line} is
	 * returned, too if the given boolean flag <code>open</code> is set to
	 * <code>false</code>.
	 *
	 * @param segmentsArray
	 *            The array of {@link Line}s.
	 * @param open
	 *            indicates whether to omit the end {@link Point} of the last
	 *            {@link Line}
	 * @return the start {@link Point}s of the {@link Line}s and the end
	 *         {@link Point} of the last {@link Line} according to
	 *         <code>open</code>
	 */
	public static Point[] toPointsArray(Line[] segmentsArray, boolean open) {
		if (segmentsArray == null || segmentsArray.length == 0) {
			return new Point[] {};
		}

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
		// cannot construct lines for less than 2 points
		if (points == null || points.length < 2) {
			return new Line[] {};
		}

		int segmentCount = close ? points.length : points.length - 1;
		Line[] segments = new Line[segmentCount];
		for (int i = 0; i < segmentCount; i++) {
			segments[i] = new Line(points[i],
					points[i + 1 < points.length ? i + 1 : 0]);
		}
		return segments;
	}

	private PointListUtils() {
		// this class should not be instantiated by clients
	}
}
