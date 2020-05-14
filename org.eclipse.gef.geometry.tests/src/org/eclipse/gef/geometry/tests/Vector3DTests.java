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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.projective.Vector3D;
import org.junit.Test;

public class Vector3DTests {

	@Test
	public void test_equals() {
		Vector3D v0 = new Vector3D(1, 1, 1);
		assertFalse(v0.equals(null));
		assertFalse(v0.equals(new Point()));
		assertEquals(v0, v0);
		assertEquals(v0, new Vector3D(1, 1, 1));
		assertEquals(v0, new Vector3D(new Point(1, 1)));
		assertEquals(v0, new Vector3D(2, 2, 2));
	}

	@Test
	public void test_getAdded() {
		Vector3D v0 = new Vector3D(1, 0, 5);
		Vector3D v1 = new Vector3D(0, 1, 5);
		assertEquals(new Vector3D(1, 0, 5), v0.getAdded(v0));
		assertEquals(new Vector3D(0, 1, 5), v1.getAdded(v1));
		assertEquals(new Vector3D(1, 1, 10), v0.getAdded(v1));
		assertEquals(new Vector3D(1, 1, 10), v1.getAdded(v0));
		assertEquals(new Vector3D(2, 2, 20), v0.getAdded(v1));
		assertEquals(new Vector3D(2, 2, 20), v1.getAdded(v0));
	}

	@Test
	public void test_getCopy() {
		Vector3D v0 = new Vector3D(1, 2, 3);
		Vector3D v1 = v0.getCopy();
		assertEquals(v0, v1);
		assertNotSame(v0, v1);
		v0.x++;
		v0.y--;
		assertFalse(v0.equals(v1));
	}

	@Test
	public void test_getCrossed() {
		Vector3D v0 = new Vector3D(1, 0, 1);
		Vector3D v1 = new Vector3D(0, 1, 1);
		assertEquals(new Vector3D(-1, -1, 1), v0.getCrossProduct(v1));
		assertEquals(new Vector3D(1, 1, -1), v1.getCrossProduct(v0));
	}

	@Test
	public void test_getDot() {
		Vector3D v0 = new Vector3D(1, 0, 1);
		Vector3D v1 = new Vector3D(0, 1, 1);
		assertTrue(PrecisionUtils.equal(1, v0.getDotProduct(v1)));
		assertTrue(PrecisionUtils.equal(1, v1.getDotProduct(v0)));

		v0 = new Vector3D(1, 2, 3);
		v1 = new Vector3D(3, 2, 1);
		assertTrue(PrecisionUtils.equal(10, v0.getDotProduct(v1)));
		assertTrue(PrecisionUtils.equal(10, v1.getDotProduct(v0)));
	}

	@Test
	public void test_getRatio() {
		Vector3D v0 = new Vector3D(0, 0, 1);
		Vector3D v1 = new Vector3D(10, 10, 1);
		assertEquals(new Vector3D(5, 5, 1), v0.getRatio(v1, 0.5));
		assertEquals(new Vector3D(5, 5, 1), v1.getRatio(v0, 0.5));
	}

	@Test
	public void test_getScaled() {
		Vector3D v0 = new Vector3D(1, 2, 3);
		for (double s = -1.1; s <= 1.1; s += 0.2) {
			assertEquals(new Vector3D(1, 2, 3), v0.getScaled(s));
		}
	}

	@Test
	public void test_getSubtracted() {
		Vector3D v0 = new Vector3D(10, 5, 1);
		Vector3D v1 = new Vector3D(5, 10, 1);
		assertEquals(new Vector3D(0, 0, 0), v0.getSubtracted(v0));
		assertEquals(new Vector3D(0, 0, 0), v1.getSubtracted(v1));
		assertFalse(v0.getSubtracted(v1).equals(new Vector3D(0, 0, 1)));
		assertEquals(new Vector3D(5, -5, 0), v0.getSubtracted(v1));
		assertEquals(new Vector3D(5, -5, 0), v1.getSubtracted(v0));
		assertEquals(new Vector3D(1, -1, 1 / 5), v0.getSubtracted(v1));
		assertEquals(new Vector3D(1, -1, 1 / 5), v1.getSubtracted(v0));
	}

	@Test
	public void test_toString() {
		Vector3D v0 = new Vector3D(1, 2, 3);
		assertEquals("Vector3D(1.0, 2.0, 3.0)", v0.toString());
	}

}
