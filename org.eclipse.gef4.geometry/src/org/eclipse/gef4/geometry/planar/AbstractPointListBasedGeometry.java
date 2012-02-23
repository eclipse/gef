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
package org.eclipse.gef4.geometry.planar;

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.utils.PointListUtils;

abstract class AbstractPointListBasedGeometry extends AbstractGeometry {

	private static final long serialVersionUID = 1L;

	Point[] points;

	public AbstractPointListBasedGeometry(Point... points) {
		this.points = PointListUtils.getCopy(points);
	}

	public AbstractPointListBasedGeometry(double... coordinates) {
		points = new Point[coordinates.length / 2];
		for (int i = 0; i < coordinates.length / 2; i++) {
			points[i] = new Point(coordinates[i * 2], coordinates[i * 2 + 1]);
		}
	}

	/**
	 * @see IGeometry#getBounds()
	 */
	public final Rectangle getBounds() {
		return PointListUtils.getBounds(points);
	}

	/**
	 * Returns a double array, which represents the sequence of coordinates of
	 * the {@link Point}s that make up this {@link Polygon}.
	 * 
	 * @return an array that alternately contains the x- and y-coordinates of
	 *         this {@link Polygon}'s points.
	 */
	public final double[] getCoordinates() {
		return PointListUtils.toCoordinatesArray(points);
	}

	/**
	 * Returns a copy of the points that make up this {@link Polygon}, where a
	 * segment of the {@link Polygon} is represented between each two succeeding
	 * {@link Point}s in the sequence, and from the last back to the first.
	 * 
	 * @return an array of {@link Point}s representing the points that make up
	 *         this {@link Polygon}
	 */
	public final Point[] getPoints() {
		return PointListUtils.getCopy(points);
	}

	/**
	 * Returns an integer array, which represents the sequence of coordinates of
	 * the {@link Point}s that make up this {@link Polygon}.
	 * 
	 * @return an array containing integer values, which are obtained by casting
	 *         the x- and y-coordinates of this {@link Polygon}.
	 */
	public final int[] toSWTPointArray() {
		return PointListUtils.toIntegerArray(PointListUtils
				.toCoordinatesArray(points));
	}
}