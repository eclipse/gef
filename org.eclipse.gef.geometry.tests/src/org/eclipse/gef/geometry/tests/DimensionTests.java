/*******************************************************************************
 * Copyright (c) 2008, 2016 IBM Corporation and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *     
 *******************************************************************************/
package org.eclipse.gef.geometry.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;
import org.eclipse.gef.geometry.planar.Dimension;
import org.junit.Test;

/**
 * Unit tests for {@link Dimension}.
 * 
 * @author ahunter
 * 
 */
public class DimensionTests {

	@Test
	public void test_constructors() {
		Dimension d0 = new Dimension();
		assertTrue(d0.equals(new Dimension(0, 0)));
		assertTrue(d0.equals(d0));
	}

	@Test
	public void test_contains() {
		Dimension d1 = new Dimension(0.1, 0.1);
		Dimension d2 = new Dimension(0.2, 0.2);
		Dimension d3 = new Dimension(0.1, 0.3);

		assertTrue(d2.contains(d1));
		assertFalse(d1.contains(d2));
		assertFalse(d2.contains(d3));
		assertTrue(d3.contains(d1));

		d1 = new Dimension(1, 1);
		d2 = new Dimension(1 + TestUtils.getPrecisionFraction() / 10d,
				1 + TestUtils.getPrecisionFraction() / 10d);

		assertTrue(d1.contains(d2));
	}

	@Test
	public void test_empty() {
		assertTrue(new Dimension(0, 0).isEmpty());
		assertTrue(new Dimension(-1, 1).isEmpty());
		assertTrue(new Dimension(1, -1).isEmpty());
		assertTrue(new Dimension(-1, -1).isEmpty());
		assertFalse(new Dimension(1, 1).isEmpty());
	}

	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=227977
	 */
	@Test
	public void test_equals() {
		Dimension d1 = new Dimension(0.1, 0.1);
		Dimension d2 = new Dimension(0.2, 0.2);
		Dimension d3 = new Dimension(0.1, 0.2);
		Dimension d4 = new Dimension(0.2, 0.1);
		assertFalse(d1.equals(d2));
		assertFalse(d1.equals(d3));
		assertFalse(d1.equals(d4));

		d1 = new Dimension(0.2, 0.2);
		assertTrue(d1.equals(d2));
	}

	@Test
	public void test_expand() {
		Dimension d1 = new Dimension(0.1, 0.1);
		Dimension d2 = new Dimension(0.2, 0.2);

		Dimension dx = d1.getExpanded(d1);
		d1.expand(d1);
		assertTrue(dx.equals(d1));

		assertTrue(d1.equals(d2));
		assertTrue(d1.contains(d2));
		assertTrue(d2.contains(d1));

		dx = d1.getExpanded(d1.getWidth(), d1.getHeight());
		d1.expand(d1);
		assertTrue(dx.equals(d1));

		assertTrue(d1.contains(d2));
		assertFalse(d2.contains(d1));
	}

	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=124904
	 */
	@Test
	public void test_getCopy() {
		Dimension p1 = new Dimension(0.1, 0.1);
		assertTrue(p1.equals(p1.getCopy()));
		assertTrue(p1.equals(p1.clone()));
		assertTrue(p1.getCopy().equals(p1.clone()));
	}

	@Test
	public void test_intersection() {
		Dimension d1 = new Dimension(1, 2);
		Dimension d2 = new Dimension(2, 1);

		Dimension di = new Dimension(1, 1);
		Dimension da = new Dimension(2, 2);

		assertTrue(d1.getIntersected(d2).equals(di));
		assertTrue(di.getIntersected(da).equals(di));
		assertTrue(da.getIntersected(da).equals(da));

	}

	@Test
	public void test_minmax() {
		Dimension d1 = new Dimension(1, 2);
		Dimension d2 = new Dimension(2, 1);

		Dimension di = new Dimension(1, 1);
		Dimension da = new Dimension(2, 2);

		assertTrue(Dimension.min(d1, d2).equals(di));
		assertTrue(Dimension.max(d1, d2).equals(da));

		assertTrue(Dimension.min(di, da).equals(di));
		assertTrue(Dimension.max(di, da).equals(da));

		assertTrue(Dimension.min(da, da).equals(da));
		assertTrue(Dimension.max(di, di).equals(di));
	}

	@Test
	public void test_negate() {
		assertTrue(
				new Dimension(1, 2).getNegated().equals(new Dimension(-1, -2)));
		assertTrue(
				new Dimension(-2, 1).getNegated().equals(new Dimension(2, -1)));
		assertTrue(
				new Dimension(1, -2).getNegated().equals(new Dimension(-1, 2)));
		assertTrue(
				new Dimension(-1, -2).getNegated().equals(new Dimension(1, 2)));
	}

	@Test
	public void test_scale() {
		Dimension d1 = new Dimension(1, 1);
		Dimension d2 = new Dimension(2, 2);

		assertTrue(d1.getScaled(2).equals(d2));
		assertTrue(d2.getScaled(0.5).equals(d1));

		d2 = new Dimension(10, 5);
		assertTrue(d1.getScaled(10, 5).equals(d2));
		assertTrue(d2.getScaled(1f / 10f, 1f / 5f).equals(d1));
	}

	@Test
	public void test_setters() {
		Dimension d = new Dimension();

		d.setWidth(10);
		assertTrue(PrecisionUtils.equal(d.getWidth(), 10));

		d.setHeight(5);
		assertTrue(PrecisionUtils.equal(d.getHeight(), 5));

		d.setSize(5, 10);
		assertTrue(PrecisionUtils.equal(d.getWidth(), 5));
		assertTrue(PrecisionUtils.equal(d.getHeight(), 10));

		d.setSize(new Dimension(-1, -1));
		assertTrue(PrecisionUtils.equal(d.getWidth(), -1));
		assertTrue(PrecisionUtils.equal(d.getHeight(), -1));
	}

	@Test
	public void test_shrink() {
		Dimension d1 = new Dimension(0.2, 0.2);
		Dimension d2 = new Dimension(0.4, 0.4);

		assertTrue(d2.contains(d1));
		assertFalse(d1.contains(d2));

		Dimension dx = d2.getShrinked(d1);
		d2.shrink(d1);
		assertTrue(dx.equals(d2));

		assertTrue(d2.contains(d1));
		assertTrue(d1.contains(d2));
		assertTrue(d2.equals(d1));

		Dimension dy = new Dimension(0.1, 0.1);
		dx = d2.getShrinked(dy.getWidth(), dy.getHeight());
		d2.shrink(dy);
		assertTrue(dx.equals(d2));

		assertFalse(d2.contains(d1));
		assertTrue(d1.contains(d2));
	}

	@Test
	public void test_toString() {
		Dimension d = new Dimension();
		assertEquals("Dimension(0.0, 0.0)", d.toString());
	}

	@Test
	public void test_transpose() {
		Dimension d1 = new Dimension(1, 1);
		Dimension d2 = new Dimension(2, 2);

		assertTrue(d1.getTransposed().equals(d1));
		assertTrue(d2.getTransposed().equals(d2));

		d1 = new Dimension(1, 2);
		d2 = new Dimension(2, 1);

		assertTrue(d1.getTransposed().equals(d2));
		assertTrue(d2.getTransposed().equals(d1));
	}

	@Test
	public void test_union() {
		Dimension d1 = new Dimension(1, 2);
		Dimension d2 = new Dimension(2, 1);

		Dimension di = new Dimension(1, 1);
		Dimension da = new Dimension(2, 2);

		assertTrue(d1.getUnioned(d2).equals(da));
		assertTrue(di.getUnioned(da).equals(da));
		assertTrue(di.getUnioned(di).equals(di));
	}

}
