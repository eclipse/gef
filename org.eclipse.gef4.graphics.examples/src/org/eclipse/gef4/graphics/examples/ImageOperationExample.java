/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
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
package org.eclipse.gef4.graphics.examples;

import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.image.Image;
import org.eclipse.gef4.graphics.image.operations.IImageOperation;

public class ImageOperationExample implements IExample {

	private static class HistogramEqualizer implements IImageOperation {

		@Override
		public Image apply(Image image) {
			int[] equRed = genEqualizer(image, image.getRedHistogram());
			int[] equGreen = genEqualizer(image, image.getGreenHistogram());
			int[] equBlue = genEqualizer(image, image.getBlueHistogram());

			Image result = image.getCopy();

			for (int x = 0; x < image.getWidth(); x++) {
				for (int y = 0; y < image.getHeight(); y++) {
					int[] rgba = Color.getPixelRGBA(image.getPixel(x, y));
					int resPixel = Color.getPixel(equRed[rgba[0]],
							equGreen[rgba[1]], equBlue[rgba[2]], rgba[3]);
					result.setPixel(x, y, resPixel);
				}
			}

			return result;
		}

		private int[] genEqualizer(Image image, int[] hist) {
			int[] cdf = new int[256];
			int cdfMin = 0;
			boolean noHist = true;

			cdf[0] = hist[0];

			if (hist[0] > 0) {
				cdfMin = cdf[0];
				noHist = false;
			}

			for (int i = 1; i < 256; i++) {
				cdf[i] = cdf[i - 1] + hist[i];
				if (noHist && hist[i] > 0) {
					cdfMin = hist[i];
					noHist = false;
				}
			}

			int[] equalizer = new int[256];
			double s = 255d / (image.getWidth() * image.getHeight());
			for (int i = 0; i < 256; i++) {
				equalizer[i] = (int) ((cdf[i] - cdfMin) * s);
			}

			return equalizer;
		}
	}

	private static String IMAGE_FILE = "tour-eiffel_notre-dame.jpg";

	private Image image;

	@Override
	public int getHeight() {
		return 460;
	}

	@Override
	public String getTitle() {
		return "Image Operation Example - Histogram Equalization";
	}

	@Override
	public int getWidth() {
		return 680;
	}

	private void loadImage() {
		if (image == null) {
			try {
				image = new Image(ImageIO.read(OverviewExample.class
						.getResource(IMAGE_FILE)));
			} catch (IOException x) {
				System.out.println("Cannot load image file '" + IMAGE_FILE
						+ "':");
				x.printStackTrace();
			}
		}
	}

	@Override
	public void renderScene(IGraphics g) {
		loadImage();
		Rectangle halve = image.getBounds().getShrinked(0, 0,
				0.5 * image.getWidth(), 0);
		g.translate(10, 10).pushState().intersectClip(halve);
		g.paint(image);
		g.popState().intersectClip(halve.getTranslated(halve.getWidth(), 0));
		g.paint(new HistogramEqualizer().apply(image));
	}
}
