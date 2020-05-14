/*******************************************************************************
 * Copyright (c) 2013, 2016 itemis AG and others.
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

import static org.junit.Assert.assertTrue;

import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.planar.BezierCurve;
import org.eclipse.gef.geometry.planar.CurvedPolygon;
import org.eclipse.gef.geometry.planar.Path.Segment;
import org.eclipse.gef.geometry.planar.Pie;
import org.junit.Test;

public class PieTests {

	@Test
	public void test_getOutlineSegments() {
		Pie pie = new Pie(0, 0, 100, 100, Angle.fromDeg(90),
				Angle.fromDeg(270));
		BezierCurve[] outlineSegments = pie.getOutlineSegments();
		CurvedPolygon bakery = new CurvedPolygon(outlineSegments);
		assertTrue("the curved polygon must contain the pie",
				bakery.contains(pie));
	}

	public void test_toPath() {
		Pie pie = new Pie(0, 0, 100, 100, Angle.fromDeg(90),
				Angle.fromDeg(270));
		Segment[] segments = pie.toPath().getSegments();
		// check path is closed
		assertTrue(segments[segments.length - 1].getType() == Segment.CLOSE);
	}

}
