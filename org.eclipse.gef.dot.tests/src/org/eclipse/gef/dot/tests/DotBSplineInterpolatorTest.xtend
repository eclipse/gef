/*******************************************************************************
 * Copyright (c) 2016, 2018 itemis AG and others.
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
package org.eclipse.gef.dot.tests

import javafx.scene.shape.Rectangle
import org.eclipse.gef.dot.internal.ui.DotBSplineInterpolator
import org.eclipse.gef.fx.anchors.DynamicAnchor
import org.eclipse.gef.fx.nodes.Connection
import org.eclipse.gef.fx.nodes.GeometryNode
import org.eclipse.gef.geometry.planar.ICurve
import org.eclipse.gef.geometry.planar.Point
import org.junit.Test

import static org.junit.Assert.assertEquals

class DotBSplineInterpolatorTest {

	@Test def test_diagonal_decoration_angle() {
		
		val connection = new Connection
		val dotStartPointHint = new Point(89.916, 71.831)
		val dotEndPointHint = new Point(72.207, 36.413)
		
		connection => [
			// Anchorage [+72.0 +72.0 54.0 x 36.0]
			// Ref-Point [84.50399802398681, 61.006999862670895]
			// Anchorage [+36.0 +0.0 54.0 x 36.0]
			// Ref-Point [77.61499808502197, 47.22899998474121]
			// XXX: Use DynamicAnchor so that the start/end point is computed
			// (although the DotBSplineInterpolator should overwrite the computed value).
			startAnchor = new DynamicAnchor(new Rectangle(72, 72, 54, 36))
			endAnchor = new DynamicAnchor(new Rectangle(36, 0, 54, 36))
			
			// Connection:
			// sp) Point(84.50399780273438, 72.0) (hint=Point(89.916, 71.831))
			// cp01) Point(84.50399780273438, 61.00699996948242)
			// cp02) Point(82.23400115966797, 56.46799850463867)
			// cp03) Point(79.88700103759766, 51.77399826049805)
			// cp04) Point(77.61499786376953, 47.229000091552734)
			// ep) Point(77.61499786376953, 36.0) (hint=Point(72.207, 36.413))
			startPointHint = dotStartPointHint
			endPointHint = dotEndPointHint
			controlPoints = #[
				new Point(84.50399780273438, 61.00699996948242),
				new Point(82.23400115966797, 56.46799850463867),
				new Point(79.88700103759766, 51.77399826049805),
				new Point(77.61499786376953, 47.229000091552734)
			]
			
			// interpolate and refresh
			interpolator = new DotBSplineInterpolator
		]
		
		// verify that the hints determine the start and end positions of the connection geometry
		val geometry = (connection.curve as GeometryNode<ICurve>).geometry
		assertEquals(dotStartPointHint, geometry.p1)
		assertEquals(dotEndPointHint, geometry.p2)
	}
}
