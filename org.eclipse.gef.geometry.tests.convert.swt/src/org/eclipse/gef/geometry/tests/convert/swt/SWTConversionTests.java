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
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.geometry.tests.convert.swt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.geom.PathIterator;

import org.eclipse.gef.geometry.convert.swt.AWT2SWT;
import org.eclipse.gef.geometry.convert.swt.Geometry2SWT;
import org.eclipse.gef.geometry.convert.swt.SWT2AWT;
import org.eclipse.gef.geometry.convert.swt.SWT2Geometry;
import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Path;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polygon;
import org.eclipse.gef.geometry.planar.Polyline;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.PathData;
import org.junit.Test;

public class SWTConversionTests {

	@Test
	public void test_AWT_Path() {
		PathIterator pathIteratorBefore = new java.awt.geom.Path2D.Double(
				new java.awt.geom.RoundRectangle2D.Double(0, 0, 100, 80, 10, 10)).getPathIterator(null);

		PathData swtPathData = AWT2SWT.toSWTPathData(pathIteratorBefore);
		int windingRuleSWT = pathIteratorBefore.getWindingRule() == java.awt.geom.Path2D.WIND_EVEN_ODD
				? SWT.FILL_EVEN_ODD : SWT.FILL_WINDING;

		PathIterator pathIteratorAfter = SWT2AWT.toAWTPathIterator(swtPathData, windingRuleSWT);

		while (!pathIteratorBefore.isDone()) {
			assertFalse(pathIteratorAfter.isDone());

			float[] coordsBefore = new float[6];
			float[] coordsAfter = new float[6];
			int typeBefore = pathIteratorBefore.currentSegment(coordsBefore);
			int typeAfter = pathIteratorAfter.currentSegment(coordsAfter);
			assertEquals(typeBefore, typeAfter);

			int numCoords = 0;
			switch (typeBefore) {
			case java.awt.geom.PathIterator.SEG_MOVETO:
			case java.awt.geom.PathIterator.SEG_LINETO:
				numCoords = 2;
				break;
			case java.awt.geom.PathIterator.SEG_QUADTO:
				numCoords = 4;
				break;
			case java.awt.geom.PathIterator.SEG_CUBICTO:
				numCoords = 6;
				break;
			}

			for (int i = 0; i < numCoords; i++) {
				assertTrue(PrecisionUtils.equal(coordsBefore[i], coordsAfter[i]));
			}

			pathIteratorBefore.next();
			pathIteratorAfter.next();
		}
	}

	@Test
	public void test_LineConversion() {
		// Create a Line with integer coordinates so that we can compare the
		// Line with an equivalent SWT Point Array as the SWT Point Array
		// consists of integer coordinates.
		for (int x0 = -5; x0 <= 5; x0++) {
			for (int y0 = -5; y0 <= 5; y0++) {
				for (int x1 = -5; x1 <= 5; x1++) {
					for (int y1 = -5; y1 <= 5; y1++) {
						Line l = new Line(x0, y0, x1, y1);
						assertEquals(l, SWT2Geometry.toLine(Geometry2SWT.toSWTPointArray(l)));
					}
				}
			}
		}
	}

	@Test
	public void test_PathConversion() {
		// Create a Path whose coordinate values are convertible from double to
		// float without loosing precision so that we can compare the Path with
		// an SWT equivalent as an SWT Path consists of float coordinates.
		Path p = new Path().moveTo(50, 50).lineTo(100, 100).quadTo(100, 150, 50, 150).cubicTo(20, 120, 20, 80, 50, 50)
				.close();
		assertEquals(p, SWT2Geometry.toPath(Path.WIND_NON_ZERO, Geometry2SWT.toSWTPathData(p)));
	}

	@Test
	public void test_PointConversion() {
		// Create a Point with integer coordinates so that we can compare the
		// Point with an SWT equivalent as an SWT Point consists of integer
		// coordinates.
		for (int x = -15; x <= 15; x++) {
			for (int y = -15; y <= 15; y++) {
				Point p = new Point(12, -3);
				assertEquals(p, SWT2Geometry.toPoint(Geometry2SWT.toSWTPoint(p)));
			}
		}
	}

	@Test
	public void test_PolygonConversion() {
		// Create a Polygon with integer coordinates so that we can compare it
		// with an equivalent SWT Point Array as the SWT Point Array consists of
		// integer coordinates.
		for (int x0 = -5; x0 <= 5; x0++) {
			for (int y1 = -5; y1 <= 5; y1++) {
				for (int x2 = -5; x2 <= 5; x2++) {
					for (int y3 = -5; y3 <= 5; y3++) {
						Polygon polygon = new Polygon(x0, -1, 1, y1, x2, 10, 1, y3);
						assertEquals(polygon, SWT2Geometry.toPolygon(Geometry2SWT.toSWTPointArray(polygon)));
					}
				}
			}
		}
	}

	@Test
	public void test_PolylineConversion() {
		// Create a Polygon with integer coordinates so that we can compare it
		// with an equivalent SWT Point Array as the SWT Point Array consists of
		// integer coordinates.
		for (int x0 = -5; x0 <= 5; x0++) {
			for (int y1 = -5; y1 <= 5; y1++) {
				for (int x2 = -5; x2 <= 5; x2++) {
					for (int y3 = -5; y3 <= 5; y3++) {
						Polyline polyline = new Polyline(x0, -1, 1, y1, x2, 10, 1, y3);
						assertEquals(polyline, SWT2Geometry.toPolyline(Geometry2SWT.toSWTPointArray(polyline)));
					}
				}
			}
		}
	}

	@Test
	public void test_RectangleConversion() {
		// Create a Rectangle with integer coordinates so that we can compare it
		// with the SWT equivalent as an SWT Rectangle consists of integer
		// coordinates.
		for (int x = -5; x <= 5; x++) {
			for (int y = -5; y <= 5; y++) {
				for (int w = -5; w <= 5; w++) {
					for (int h = -5; h <= 5; h++) {
						Rectangle r = new Rectangle(x, y, w, h);
						assertEquals(r, SWT2Geometry.toRectangle(Geometry2SWT.toSWTRectangle(r)));
					}
				}
			}
		}
	}

}
