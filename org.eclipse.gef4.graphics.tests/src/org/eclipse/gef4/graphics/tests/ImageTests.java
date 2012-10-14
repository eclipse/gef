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
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import org.eclipse.gef4.graphics.Image;
import org.junit.Test;

public class ImageTests {

	// TODO: Test with actual images!
	// -> implement gradients -> synthesize test images

	@Test
	public void test_constructor() {
		Image img = new Image(new BufferedImage(640, 480,
				BufferedImage.TYPE_3BYTE_BGR));

		assertEquals(640, img.bufferedImage().getWidth());
		assertEquals(480, img.bufferedImage().getHeight());

		// Image transforms to ARGB color space
		assertEquals(4, img.bufferedImage().getRaster().getNumBands());

		boolean thrown = false;
		try {
			new Image((URL) null);
		} catch (IOException x) {
			thrown = true;
		}

		assertTrue(thrown);
	}

	@Test
	public void test_getCopy() {
		// TODO
	}

	@Test
	public void test_toString() {
		// TODO: write a better toString() method.
		Image img = new Image(new BufferedImage(640, 480,
				BufferedImage.TYPE_3BYTE_BGR));
		assertTrue(img.toString().contains("Image(bufferedImage = "));
	}

}
