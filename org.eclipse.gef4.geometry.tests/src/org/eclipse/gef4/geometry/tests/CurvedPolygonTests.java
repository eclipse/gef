/*******************************************************************************
 * Copyright (c) 2012, 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef4.geometry.planar.CurvedPolygon;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.junit.Test;

public class CurvedPolygonTests {

	@Test
	public void test_contains_Point() {
		CurvedPolygon curvedPolygon = new CurvedPolygon(PolyBezier
				.interpolateCubic(new Point(100, 100), new Point(200, 200),
						new Point(100, 300), new Point(100, 100)).toBezier());

		assertFalse(curvedPolygon.contains(new Point(0, 0)));
		assertFalse(curvedPolygon.contains(new Point(300, 0)));
		assertFalse(curvedPolygon.contains(new Point(0, 300)));
		assertFalse(curvedPolygon.contains(new Point(300, 300)));
		assertFalse(curvedPolygon.contains(new Point(0, 200)));
		assertFalse(curvedPolygon.contains(new Point(400, 200)));

		assertTrue(curvedPolygon.contains(new Point(150, 200)));
		assertTrue(curvedPolygon.contains(new Point(100, 100)));
	}

	@Test
	public void test_getBounds() {
		// TODO: store points in variables
		CurvedPolygon curvedPolygon = new CurvedPolygon(new Line(100, 100, 200,
				100), new Line(200, 100, 200, 200),
				new Line(200, 200, 100, 200), new Line(100, 200, 100, 100));
		Rectangle expectation = new Rectangle(new Point(100, 100), new Point(
				200, 200));
		Rectangle reality = curvedPolygon.getBounds();
		assertEquals(expectation, reality);
	}

}
