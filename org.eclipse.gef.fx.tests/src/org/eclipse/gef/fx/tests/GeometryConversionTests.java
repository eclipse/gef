/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
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
package org.eclipse.gef.fx.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef.fx.utils.Geometry2Shape;
import org.eclipse.gef.fx.utils.Shape2Geometry;
import org.eclipse.gef.geometry.planar.Path;
import org.junit.Test;

public class GeometryConversionTests {
	
	@Test
	public void test_PathConversion() {
		// create a simple path and convert it from geometry to javafx and back
		// again!
		Path p = new Path().moveTo(50, 50).lineTo(100, 100).quadTo(100, 150, 50, 150).cubicTo(20, 120, 20, 80, 50, 50)
				.close();
		assertEquals(p, Shape2Geometry.toPath(Geometry2Shape.toPath(p)));
	}
	
}

