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

import org.eclipse.gef4.graphics.color.Color;
import org.junit.Test;

public class ColorTests {

	private static final int PIXEL_B = 0xff;
	private static final int PIXEL_G = 0x00;
	private static final int PIXEL_R = 0xf0;
	private static final int PIXEL_A = 0x0f;
	private static final int PIXEL = 0x0ff000ff;

	@Test
	public void test_components_out_of_range() {
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
	public void test_computePixelIntensity() {
		assertEquals(128, Color.computePixelIntensity(0x00808080));
		assertEquals(128, Color.computePixelIntensity(0xff808080));
		assertEquals(128, Color.computePixelIntensity(0xffff8002));
		assertEquals(128,
				Color.computePixelIntensity(new int[] { 180, 24, 180 }));
		assertEquals(128, Color.computePixelIntensity(
				new int[] { 255, 128, 255 }, 0, 1, 0));
	}

	@Test
	public void test_constructor_default() {
		Color c = new Color();
		assertNotNull(c);
		assertEquals(c, c);
		assertEquals(Color.DEFAULT_RED, c.getRed());
		assertEquals(Color.DEFAULT_GREEN, c.getGreen());
		assertEquals(Color.DEFAULT_BLUE, c.getBlue());
		assertEquals(Color.DEFAULT_ALPHA, c.getAlpha());
	}

	@Test
	public void test_constructor_pixel() {
		Color c = new Color(PIXEL);
		assertEquals(PIXEL_A, c.getAlpha());
		assertEquals(PIXEL_R, c.getRed());
		assertEquals(PIXEL_G, c.getGreen());
		assertEquals(PIXEL_B, c.getBlue());
	}

	@Test
	public void test_constructor_rgba() {
		Color c = new Color(255, 0, 0, 255);
		assertEquals(255, c.getRed());
		assertEquals(0, c.getGreen());
		assertEquals(0, c.getBlue());
		assertEquals(255, c.getAlpha());
		assertEquals(c, new Color(255, 0, 0));
	}

	@Test
	public void test_equals() {
		Color c0 = new Color(255, 0, 0, 255);
		Color c1 = new Color(255, 0, 0, 255);
		assertFalse(c1.equals(new Object()));
		assertEquals(c0, c1);
		assertEquals(c1, c0);
		assertEquals(c0, c0);

		c1.setRed(254);
		assertEquals(c1, c1);
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
	public void test_getBlended() {
		Color c0 = new Color(0, 255, 0);
		Color c1 = new Color(0, 0, 255);
		assertEquals(c0, c0.getBlended(c1, 1));
		assertEquals(c0, c1.getBlended(c0, 0));

		Color c0_5 = c0.getBlended(c1);
		assertEquals(new Color(0, 127, 127), c0_5);

		assertEquals(new Color(0, 127, 255), c0.getBlended(c1, 0, 0.5, 0, 0));
	}

	@Test
	public void test_getChannelClamped() {
		assertEquals(128, Color.getChannelClamped(128));
		assertEquals(255, Color.getChannelClamped(255));
		assertEquals(255, Color.getChannelClamped(256));
		assertEquals(255, Color.getChannelClamped(10000));
		assertEquals(0, Color.getChannelClamped(0));
		assertEquals(0, Color.getChannelClamped(-1));
		assertEquals(0, Color.getChannelClamped(-10000));
	}

	@Test
	public void test_getCopy() {
		Color c0 = new Color();
		Color c1 = c0.getCopy();
		assertEquals(c0, c1);
		assertNotSame(c0, c1);
	}

	@Test
	public void test_getPixel() {
		assertEquals(PIXEL, Color.getPixel(PIXEL_R, PIXEL_G, PIXEL_B, PIXEL_A));

		boolean thrown = false;
		try {
			Color.getPixel(null);
		} catch (IllegalArgumentException x) {
			thrown = true;
		}
		assertTrue(thrown);

		thrown = false;
		try {
			Color.getPixel(1, 2);
		} catch (IllegalArgumentException x) {
			thrown = true;
		}
		assertTrue(thrown);
	}

	@Test
	public void test_getPixelRGBA() {
		assertEquals(PIXEL_R, Color.getPixelRed(PIXEL));
		assertEquals(PIXEL_G, Color.getPixelGreen(PIXEL));
		assertEquals(PIXEL_B, Color.getPixelBlue(PIXEL));
		assertEquals(PIXEL_A, Color.getPixelAlpha(PIXEL));

		int[] pixelRGBA = Color.getPixelRGBA(PIXEL);
		assertEquals(PIXEL_R, pixelRGBA[0]);
		assertEquals(PIXEL_G, pixelRGBA[1]);
		assertEquals(PIXEL_B, pixelRGBA[2]);
		assertEquals(PIXEL_A, pixelRGBA[3]);
	}

	@Test
	public void test_getRGBA() {
		Color c = new Color(2, 4, 8, 16);
		int[] rgba = c.getRGBA();
		assertEquals(c.getRed(), rgba[0]);
		assertEquals(c.getGreen(), rgba[1]);
		assertEquals(c.getBlue(), rgba[2]);
		assertEquals(c.getAlpha(), rgba[3]);
	}

	@Test
	public void test_toString() {
		Color purpleGlass = new Color(228, 64, 255, 64);
		assertEquals("Color(r = 228, g = 64, b = 255, a = 64)",
				purpleGlass.toString());
	}

}
