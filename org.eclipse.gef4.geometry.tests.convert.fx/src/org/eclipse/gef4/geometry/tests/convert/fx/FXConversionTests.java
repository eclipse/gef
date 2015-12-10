/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.geometry.tests.convert.fx;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.junit.Test;

public class FXConversionTests {

	@Test
	public void test_Affine() {
		AffineTransform transform = new AffineTransform(0, 1, 2, 3, 4, 5);
		assertEquals(transform, JavaFX2Geometry.toAffineTransform(Geometry2JavaFX.toFXAffine(transform)));
	}
	
	@Test
	public void test_Bounds() {
		Rectangle rectangle = new Rectangle(5, 6, 7, 8);
		assertEquals(rectangle, JavaFX2Geometry.toRectangle(Geometry2JavaFX.toFXBounds(rectangle)));
	}
	
	@Test
	public void test_Point() {
		Point point = new Point(1, 2);
		assertEquals(point, JavaFX2Geometry.toPoint(Geometry2JavaFX.toFXPoint(point)));
	}

}
