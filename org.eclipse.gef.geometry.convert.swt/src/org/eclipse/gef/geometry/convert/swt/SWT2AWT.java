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
 *     Matthias Wienand (itemis AG) - remove AffineTransform from resulting PathIterator
 *     
 *******************************************************************************/
package org.eclipse.gef.geometry.convert.swt;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.PathData;

/**
 * A utility class to convert geometric objects from SWT to AWT, i.e. Java2D.
 * 
 * @author anyssen
 * 
 */
public class SWT2AWT {

	/**
	 * Converts an SWT {@link PathData} into an equivalent AWT
	 * {@link PathIterator}.
	 * 
	 * @param pathData
	 *            the {@link PathData} to convert.
	 * @param windingRule
	 *            the winding rule to use when constructing the
	 *            {@link PathIterator}, i.e. one of {@link SWT#FILL_WINDING} or
	 *            {@link SWT#FILL_EVEN_ODD}.
	 * @return a new {@link PathIterator} representing the same path
	 */
	public static PathIterator toAWTPathIterator(PathData pathData, int windingRule) {
		if (windingRule != SWT.FILL_WINDING && windingRule != SWT.FILL_EVEN_ODD) {
			throw new IllegalArgumentException(
					"Unsupported winding rule. Must be one of SWT.FILL_WINDING or SWT.FILL_EVEN_ODD");
		}
		Path2D.Double path = new Path2D.Double(
				windingRule == SWT.FILL_EVEN_ODD ? Path2D.WIND_EVEN_ODD : Path2D.WIND_NON_ZERO);
		int j = 0;
		byte[] types = pathData.types;
		float[] points = pathData.points;
		double x, y, x2, y2, x3, y3;
		for (int i = 0; i < types.length; i++) {

			switch (types[i]) {
			case SWT.PATH_MOVE_TO:
				x = points[j++];
				y = points[j++];
				path.moveTo(x, y);
				break;
			case SWT.PATH_LINE_TO:
				x = points[j++];
				y = points[j++];
				path.lineTo(x, y);
				break;
			case SWT.PATH_QUAD_TO:
				x = points[j++];
				y = points[j++];
				x2 = points[j++];
				y2 = points[j++];
				path.quadTo(x, y, x2, y2);
				break;
			case SWT.PATH_CUBIC_TO:
				x = points[j++];
				y = points[j++];
				x2 = points[j++];
				y2 = points[j++];
				x3 = points[j++];
				y3 = points[j++];
				path.curveTo(x, y, x2, y2, x3, y3);
				break;
			case SWT.PATH_CLOSE:
				path.closePath();
				break;
			default:
				break;
			}
		}
		return path.getPathIterator(null);
	}

	private SWT2AWT() {
		// this class should not be instantiated by clients
	}

}
