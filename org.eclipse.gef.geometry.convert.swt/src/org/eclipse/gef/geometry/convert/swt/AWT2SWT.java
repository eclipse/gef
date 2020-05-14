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
 *     
 *******************************************************************************/
package org.eclipse.gef.geometry.convert.swt;

import java.awt.geom.PathIterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.PathData;

/**
 * Utility class to support conversions between Java2D's geometry API and SWT's
 * geometry API.
 * 
 * @author anyssen
 * 
 */
public class AWT2SWT {

	private static float[] getPointAppended(float[] points, float x, float y) {
		float[] pointsTmp = new float[points.length + 2];
		System.arraycopy(points, 0, pointsTmp, 0, points.length);
		pointsTmp[points.length] = x;
		pointsTmp[points.length + 1] = y;
		return pointsTmp;
	}

	private static byte[] getTypeAppended(byte[] types, byte type) {
		byte[] typesTmp = new byte[types.length + 1];
		System.arraycopy(types, 0, typesTmp, 0, types.length);
		typesTmp[types.length] = type;
		return typesTmp;
	}

	/**
	 * Converts a Java2D {@link PathIterator} into an SWT {@link PathData}. Note
	 * that while Java2D's {@link PathIterator} contains the specification of a
	 * {@link PathIterator#WIND_EVEN_ODD} or {@link PathIterator#WIND_NON_ZERO}
	 * winding rule ({@link PathIterator#getWindingRule()}), this information is
	 * not kept in SWT's {@link PathData}, but is instead specified when drawing
	 * an SWT {@link Path} (which can be constructed from the {@link PathData})
	 * on an SWT {@link GC} (via {@link SWT#FILL_WINDING} or
	 * {@link SWT#FILL_EVEN_ODD}). Therefore the returned SWT {@link PathData}
	 * will not contain any information about the winding rule that was
	 * specified in the passed in {@link PathIterator}.
	 * 
	 * @param iterator
	 *            the {@link PathIterator} to transform
	 * @return a new {@link PathData} representing the same geometric path
	 */
	public static PathData toSWTPathData(PathIterator iterator) {
		byte[] types = new byte[0];
		float[] points = new float[0];
		while (!iterator.isDone()) {
			float[] segment = new float[6];
			int type = iterator.currentSegment(segment);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				types = getTypeAppended(types, (byte) SWT.PATH_MOVE_TO);
				points = getPointAppended(points, segment[0], segment[1]);
				break;
			case PathIterator.SEG_LINETO:
				types = getTypeAppended(types, (byte) SWT.PATH_LINE_TO);
				points = getPointAppended(points, segment[0], segment[1]);
				break;
			case PathIterator.SEG_QUADTO:
				types = getTypeAppended(types, (byte) SWT.PATH_QUAD_TO);
				points = getPointAppended(points, segment[0], segment[1]);
				points = getPointAppended(points, segment[2], segment[3]);
				break;
			case PathIterator.SEG_CUBICTO:
				types = getTypeAppended(types, (byte) SWT.PATH_CUBIC_TO);
				points = getPointAppended(points, segment[0], segment[1]);
				points = getPointAppended(points, segment[2], segment[3]);
				points = getPointAppended(points, segment[4], segment[5]);
				break;
			case PathIterator.SEG_CLOSE:
				types = getTypeAppended(types, (byte) SWT.PATH_CLOSE);
				break;
			}
			iterator.next();
		}
		PathData pathData = new PathData();
		pathData.points = points;
		pathData.types = types;
		return pathData;
	}

	private AWT2SWT() {
		// this class should not be instantiated by clients
	}

}
