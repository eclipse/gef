package org.eclipse.gef4.graphics.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.gef4.graphics.Image;
import org.junit.Test;

public class ImageTests {

	@Test
	public void getImageFile() throws MalformedURLException {
		URL url = new File("/test").toURI().toURL();
		Image img = new Image(url);
		assertEquals(url, img.getImageFile());
	}

	@Test
	public void test_toString() throws MalformedURLException {
		URL url = new File("/test").toURI().toURL();
		Image img = new Image(url);
		assertEquals("Image(imageFile = file:/test)", img.toString());
	}

}
