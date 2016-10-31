/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy   (itemis AG) - initial API and implementation
 *     Matthias Wieannd (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.eclipse.gef.dot.internal.ui.DotBSplineInterpolator;
import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.geometry.planar.Point;
import org.junit.Test;

import javafx.scene.shape.Rectangle;

public class DotBSplineInterpolatorTests {

	@SuppressWarnings("unchecked")
	@Test
	public void test_diagonal_decoration_angle() {
		// Anchorage [+72.0 +72.0 54.0 x 36.0]
		// Ref-Point [84.50399802398681, 61.006999862670895]
		// Anchorage [+36.0 +0.0 54.0 x 36.0]
		// Ref-Point [77.61499808502197, 47.22899998474121]

		// create anchorages
		Rectangle startAnchorage = new Rectangle(72, 72, 54, 36);
		Rectangle endAnchorage = new Rectangle(36, 0, 54, 36);

		// create connection
		Connection connection = new Connection();

		// XXX: Use DynamicAnchor so that the start/end point is computed
		// (although the DotBSplineInterpolator should overwrite the computed
		// value).
		connection.setStartAnchor(new DynamicAnchor(startAnchorage));
		connection.setEndAnchor(new DynamicAnchor(endAnchorage));

		// Connection:
		// sp) Point(84.50399780273438, 72.0) (hint=Point(89.916, 71.831))
		// cp01) Point(84.50399780273438, 61.00699996948242)
		// cp02) Point(82.23400115966797, 56.46799850463867)
		// cp03) Point(79.88700103759766, 51.77399826049805)
		// cp04) Point(77.61499786376953, 47.229000091552734)
		// ep) Point(77.61499786376953, 36.0) (hint=Point(72.207, 36.413))
		Point dotStartPointHint = new Point(89.916, 71.831);
		Point dotEndPointHint = new Point(72.207, 36.413);
		connection.setStartPointHint(dotStartPointHint);
		connection.setEndPointHint(dotEndPointHint);
		connection.setControlPoints(
				Arrays.asList(new Point(84.50399780273438, 61.00699996948242),
						new Point(82.23400115966797, 56.46799850463867),
						new Point(79.88700103759766, 51.77399826049805),
						new Point(77.61499786376953, 47.229000091552734)));

		// interpolate and refresh
		connection.setInterpolator(new DotBSplineInterpolator());

		// verify that the hints determine the start and end positions of the
		// connection geometry
		assertEquals(dotStartPointHint,
				((GeometryNode<ICurve>) connection.getCurve()).getGeometry()
						.getP1());
		assertEquals(dotEndPointHint,
				((GeometryNode<ICurve>) connection.getCurve()).getGeometry()
						.getP2());
	}

}
