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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.Point;
import org.junit.Test;

public class AffineTransformTests {

	@Test
	public void test_equals() {
		AffineTransform t0 = new AffineTransform();
		AffineTransform t1 = new AffineTransform();
		assertEquals(t0, t1);
		assertEquals(t0, t0.getCopy());
		t1.setToTranslation(10, 10);
		assertFalse(t0.equals(t1));
		t0.setToTranslation(5, 5);
		assertFalse(t0.equals(t1));
		t1.setToTranslation(5, 5);
		assertEquals(t0, t1);
	}

	@Test
	public void test_rotate90() {
		AffineTransform tx = new AffineTransform();
		// rotation looks clockwise because the Y axis is upside down
		tx.rotate(Angle.fromDeg(90).rad());
		assertEquals(new Point(0, 1), tx.getTransformed(new Point(1, 0)));
	}

}
