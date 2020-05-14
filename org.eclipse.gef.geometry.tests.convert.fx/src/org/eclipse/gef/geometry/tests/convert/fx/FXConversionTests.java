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
package org.eclipse.gef.geometry.tests.convert.fx;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.Path;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.junit.Test;

public class FXConversionTests {

	@Test
	public void test_Affine() {
		AffineTransform transform = new AffineTransform(0, 1, 2, 3, 4, 5);
		assertEquals(transform, FX2Geometry.toAffineTransform(Geometry2FX.toFXAffine(transform)));
	}
	
	@Test
	public void test_Bounds() {
		Rectangle rectangle = new Rectangle(5, 6, 7, 8);
		assertEquals(rectangle, FX2Geometry.toRectangle(Geometry2FX.toFXBounds(rectangle)));
	}
	
	@Test
	public void test_Point() {
		Point point = new Point(1, 2);
		assertEquals(point, FX2Geometry.toPoint(Geometry2FX.toFXPoint(point)));
	}

}
