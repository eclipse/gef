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
package org.eclipse.gef.geometry.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.eclipse.gef.geometry.convert.awt.AWT2Geometry;
import org.eclipse.gef.geometry.convert.awt.Geometry2AWT;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Path;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.geometry.planar.RoundedRectangle;
import org.eclipse.gef.geometry.planar.Path.Segment;
import org.junit.Test;

public class AWTConversionTests {

	@Test
	public void test_AffineTransform() {
		AffineTransform at = new AffineTransform(0, 1, 2, 3, 4, 5);
		java.awt.geom.AffineTransform atAWT = Geometry2AWT
				.toAWTAffineTransform(at);
		assertEquals(at, AWT2Geometry.toAffineTransform(atAWT));
	}

	@Test
	public void test_Line() {
		Line line = new Line(-10, -20, 30, 40);
		java.awt.geom.Line2D.Double lineAWT = Geometry2AWT.toAWTLine(line);
		assertEquals(line, AWT2Geometry.toLine(lineAWT));
	}

	@Test
	public void test_Path() {
		Path path = new Path(Path.WIND_NON_ZERO,
				new Segment(Segment.MOVE_TO, new Point(-10, -20)),
				new Segment(Segment.LINE_TO, new Point(20, 10)),
				new Segment(Segment.QUAD_TO, new Point(15, 30),
						new Point(-5, 15)),
				new Segment(Segment.CUBIC_TO, new Point(-5, 0),
						new Point(5, -20), new Point(10, 0)),
				new Segment(Segment.CLOSE));
		java.awt.geom.Path2D.Double pathAWT = Geometry2AWT.toAWTPath(path);
		assertEquals(path, AWT2Geometry.toPath(pathAWT));
	}

	@Test
	public void test_Point() {
		Point point = new Point(1, 2);
		java.awt.geom.Point2D.Double pointAWT = Geometry2AWT.toAWTPoint(point);
		assertEquals(point, AWT2Geometry.toPoint(pointAWT));
	}

	@Test
	public void test_Points() {
		Point[] points = new Point[] { new Point(-5, -10), new Point(-5, 10),
				new Point(5, -10), new Point(5, 10) };
		java.awt.geom.Point2D.Double[] pointsAWT = Geometry2AWT
				.toAWTPoints(points);
		assertArrayEquals(points, AWT2Geometry.toPoints(pointsAWT));
	}

	@Test
	public void test_Rectangle() {
		Rectangle rect = new Rectangle(1, -1, 100, 50);
		java.awt.geom.Rectangle2D.Double rectAWT = Geometry2AWT
				.toAWTRectangle(rect);
		assertEquals(rect, AWT2Geometry.toRectangle(rectAWT));
	}

	@Test
	public void test_RoundedRectangle() {
		RoundedRectangle roundRect = new RoundedRectangle(-1, 1, 50, 100, 10,
				10);
		java.awt.geom.RoundRectangle2D.Double roundRectAWT = Geometry2AWT
				.toAWTRoundRectangle(roundRect);
		assertEquals(roundRect, AWT2Geometry.toRoundedRectangle(roundRectAWT));
	}

}
