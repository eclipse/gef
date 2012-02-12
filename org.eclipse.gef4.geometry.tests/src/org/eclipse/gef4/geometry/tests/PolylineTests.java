/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alexander Shatalin (Borland) - initial API and implementation
 *    Alexander Nyssen (itemis) - Bugzilla #162082: testLinesIntersect()
 *    
 *******************************************************************************/
package org.eclipse.gef4.geometry.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.planar.Polyline;
import org.junit.Test;

public class PolylineTests {

	private static final Polyline POLYLINE = new Polyline(new Point[] {
			new Point(0, 0), new Point(1, 0), new Point(6, 5) });

	@Test
	public void test_contains_with_Point() {
		// points are not on polyline
		assertFalse(POLYLINE.contains(new Point(9, 5)));
		assertFalse(POLYLINE.contains(new Point(1, 4)));

		// point are on polyline
		assertTrue(POLYLINE.contains(new Point(0, 0)));
		assertTrue(POLYLINE.contains(new Point(1, 0)));
		assertTrue(POLYLINE.contains(new Point(2, 1)));
	}

}
