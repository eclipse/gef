/*******************************************************************************
 * Copyright (c) 2012, 2013 itemis AG and others.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.image.Image;
import org.junit.Test;

public class ImageTests {

	private static final int W = 640;
	private static final int H = 480;
	private static final int BG = new Color(255, 0, 0, 255).toPixelARGB();

	@Test
	public void test_constructor() {
		Image img = new Image(W, H);

		assertEquals(W, img.getWidth());
		assertEquals(H, img.getHeight());

		img = new Image(W, H, BG);
		assertEquals(W, img.getWidth());
		assertEquals(H, img.getHeight());

		for (int x = 0; x < img.getWidth(); x += 10) {
			for (int y = 0; y < img.getHeight(); y += 10) {
				assertEquals(BG, img.getPixel(x, y));
			}
		}
	}

	@Test
	public void test_getCopy() {
		Image image = new Image(W, H, BG);
		Image copy = image.getCopy();
		assertNotNull(copy);
		assertNotSame(image, copy);
		assertEquals(image, copy);
	}

	@Test
	public void test_toString() {
		Image img = new Image(W, H, BG);
		assertTrue(img.toString()
				.contains(
						"Image(width = " + W + ", height = " + H
								+ ", bufferedImage = "));
	}

}
