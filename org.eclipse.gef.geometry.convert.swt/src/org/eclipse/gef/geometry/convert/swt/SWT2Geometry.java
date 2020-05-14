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

import org.eclipse.gef.geometry.convert.awt.AWT2Geometry;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Path;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polygon;
import org.eclipse.gef.geometry.planar.Polyline;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.swt.graphics.PathData;

/**
 * Utility class to support conversions between SWT's geometry classes and
 * GEF's geometry API.
 * 
 * @author anyssen
 * 
 */
public class SWT2Geometry {

	private static double[] toDoubles(int[] swtPointArray) {
		final double[] pointArrayAsDoubles = new double[swtPointArray.length];
		for (int i = 0; i < swtPointArray.length; i++) {
			pointArrayAsDoubles[i] = swtPointArray[i];
		}
		return pointArrayAsDoubles;
	}

	/**
	 * Converts the given integer array to a GEF {@link Line}. The integer
	 * array has to consist of exactly 4 values which are interpreted as the x
	 * and y coordinates of the {@link Line}'s start {@link Point} and the x and
	 * y coordinates of the {@link Line}'s end point , respectively.
	 * 
	 * @param swtPointArray
	 *            an integer array which contains the x0, y0, x1, y1 coordinates
	 * @return a new {@link Line} at the specified position
	 */
	public static Object toLine(int... swtPointArray) {
		if (swtPointArray == null || swtPointArray.length != 4) {
			throw new IllegalArgumentException(
					"Cannot convert the given integer array to a GEF Line, because the integer array does not consist of exactly 4 coordinate values.");
		}
		return new Line(swtPointArray[0], swtPointArray[1], swtPointArray[2], swtPointArray[3]);
	}

	/**
	 * Converts the given SWT {@link PathData} to a GEF {@link Path} associated
	 * with the given <i>windingRule</i>.
	 * 
	 * @param windingRule
	 *            one of
	 *            <ul>
	 *            <li>{@link Path#WIND_NON_ZERO}</li>
	 *            <li>{@link Path#WIND_EVEN_ODD}</li>
	 *            </ul>
	 * @param pd
	 *            the {@link PathData} to convert
	 * @return a new {@link Path} representing the given {@link PathData}
	 */
	public static Path toPath(int windingRule, PathData pd) {
		java.awt.geom.Path2D.Double path2d = new java.awt.geom.Path2D.Double();
		path2d.append(SWT2AWT.toAWTPathIterator(pd, windingRule), false);
		return AWT2Geometry.toPath(path2d);
	}

	/**
	 * Converts the given {@link org.eclipse.swt.graphics.Point SWT Point} to a
	 * GEF {@link Point}.
	 * 
	 * @param swtPoint
	 *            the {@link org.eclipse.swt.graphics.Point SWT Point} to
	 *            convert
	 * @return a new {@link Point} at the position of the given
	 *         {@link org.eclipse.swt.graphics.Point SWT Point}
	 */
	public static Point toPoint(org.eclipse.swt.graphics.Point swtPoint) {
		return new Point(swtPoint.x, swtPoint.y);
	}

	/**
	 * Converts the given integer array to a GEF {@link Polygon}. The integer
	 * array is interpreted to consist of alternating x and y coordinates
	 * specifying the {@link Point}s used to construct the new GEF
	 * {@link Polygon}.
	 * 
	 * @param swtPointArray
	 *            the integer array that specifies the {@link Point}s used to
	 *            construct the new GEF {@link Polygon}
	 * @return a new {@link Polygon} from the given coordinates
	 */
	public static Polygon toPolygon(int... swtPointArray) {
		return new Polygon(toDoubles(swtPointArray));
	}

	/**
	 * Converts the given integer array to a GEF {@link Polyline}. The integer
	 * array is interpreted to consist of alternating x and y coordinates
	 * specifying the {@link Point}s used to construct the new GEF
	 * {@link Polyline}.
	 * 
	 * @param swtPointArray
	 *            the integer array that specifies the {@link Point}s used to
	 *            construct the new GEF {@link Polyline}
	 * @return a new {@link Polyline} from the given coordinates
	 */
	public static Object toPolyline(int... swtPointArray) {
		return new Polyline(toDoubles(swtPointArray));
	}

	/**
	 * Converts the given {@link org.eclipse.swt.graphics.Rectangle SWT
	 * Rectangle} to a GEF {@link Rectangle}.
	 * 
	 * @param swtRectangle
	 *            the {@link org.eclipse.swt.graphics.Rectangle SWT Rectangle}
	 *            to convert
	 * @return a new {@link Rectangle} at the position of the given
	 *         {@link org.eclipse.swt.graphics.Rectangle SWT Rectangle}
	 */
	public static Rectangle toRectangle(org.eclipse.swt.graphics.Rectangle swtRectangle) {
		return new Rectangle(swtRectangle.x, swtRectangle.y, swtRectangle.width, swtRectangle.height);
	}

	private SWT2Geometry() {
		// this class should not be instantiated by clients
	}

}
