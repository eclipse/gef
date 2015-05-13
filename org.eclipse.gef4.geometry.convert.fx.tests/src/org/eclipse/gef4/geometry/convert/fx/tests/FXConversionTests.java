/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef4.geometry.convert.fx.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.Path;
import org.junit.Test;

public class FXConversionTests {

	@Test
	public void test_PathConversion() {
		// create a simple path and convert it from geometry to javafx and back
		// again!
		Path p = new Path().moveTo(50, 50).lineTo(100, 100)
				.quadTo(100, 150, 50, 150).cubicTo(20, 120, 20, 80, 50, 50)
				.close();
		assertEquals(p, JavaFX2Geometry.toPath(Geometry2JavaFX.toPath(p)));
	}

}
