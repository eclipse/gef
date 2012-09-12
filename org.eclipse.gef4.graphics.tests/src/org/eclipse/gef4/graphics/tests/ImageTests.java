package org.eclipse.gef4.graphics.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;

import org.eclipse.gef4.graphics.Image;
import org.junit.Test;

public class ImageTests {

	@Test
	public void test_constructor() {
		Image img = new Image(new BufferedImage(640, 480,
				BufferedImage.TYPE_3BYTE_BGR));
		assertEquals(640, img.bufferedImage().getWidth());
		assertEquals(480, img.bufferedImage().getHeight());
		assertEquals(3, img.bufferedImage().getRaster().getNumBands());
	}

	@Test
	public void test_toString() throws MalformedURLException {
		Image img = new Image(new BufferedImage(640, 480,
				BufferedImage.TYPE_3BYTE_BGR));
		assertTrue(img.toString().contains("Image(bufferedImage = "));
	}

}
