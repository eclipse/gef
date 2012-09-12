/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.graphics.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef4.graphics.Color;
import org.junit.Ignore;
import org.junit.Test;

public class ColorTests {

	@Test
	public void components_out_of_range() {
		for (int i = 0; i < 4; i++) {
			for (int v : new int[] { -1000, -1, 256, 2000 }) {
				boolean thrown = false;
				try {
					new Color(i == 0 ? v : 0, i == 1 ? v : 0, i == 2 ? v : 0,
							i == 3 ? v : 0);
				} catch (IllegalArgumentException x) {
					thrown = true;
				}
				assertTrue(thrown);

				Color c = new Color();

				thrown = false;
				try {
					switch (i) {
					case 0:
						c.setRed(v);
						break;
					case 1:
						c.setGreen(v);
						break;
					case 2:
						c.setBlue(v);
						break;
					case 3:
						c.setAlpha(v);
						break;
					default:
						assertTrue(false);
					}
				} catch (IllegalArgumentException x) {
					thrown = true;
				}
				assertTrue(thrown);
			}
		}
	}

	@Test
	public void default_constructor() {
		Color c = new Color();
		assertNotNull(c);
		assertEquals(c, c);
		assertEquals(Color.DEFAULT_RED, c.getRed());
		assertEquals(Color.DEFAULT_GREEN, c.getGreen());
		assertEquals(Color.DEFAULT_BLUE, c.getBlue());
		assertEquals(Color.DEFAULT_ALPHA, c.getAlpha());
	}

	@Test
	public void equals() {
		Color c0 = new Color(255, 0, 0, 255);
		Color c1 = new Color(255, 0, 0, 255);
		assertFalse(c1.equals(new Object()));
		assertEquals(c0, c1);
		assertEquals(c1, c0);

		c1.setRed(254);
		assertFalse(c0.equals(c1));
		c1.setRed(255).setGreen(1);
		assertFalse(c0.equals(c1));
		c1.setGreen(0).setBlue(25);
		assertFalse(c0.equals(c1));
		c1.setBlue(0).setAlpha(128);
		assertFalse(c0.equals(c1));

		c1.setTo(c0);
		assertEquals(c0, c1);
		assertEquals(c1, c0);
	}

	@Test
	public void getCopy() {
		Color c0 = new Color();
		Color c1 = c0.getCopy();
		assertEquals(c0, c1);
		assertNotSame(c0, c1);
	}

	@Ignore
	@Test
	public void hsva() {
		// NYI
	}

	@Test
	public void rgba_constructor() {
		Color c = new Color(255, 0, 0, 255);
		assertFalse(c.equals(null));
		assertEquals(c, c);
		assertEquals(255, c.getRed());
		assertEquals(0, c.getGreen());
		assertEquals(0, c.getBlue());
		assertEquals(255, c.getAlpha());
	}

	@Test
	public void test_toString() {
		Color purpleGlass = new Color(228, 64, 255, 64);
		assertEquals("Color(r = 228, g = 64, b = 255, a = 64)",
				purpleGlass.toString());
	}

}
