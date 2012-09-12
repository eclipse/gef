package org.eclipse.gef4.graphics.images;

import java.awt.image.BufferedImage;

import org.eclipse.gef4.graphics.Image;

public abstract class AbstractPixelFilter implements IImageFilter {

	private static void readPixel(BufferedImage img, int x, int y, int[] argb) {
		int pixel = img.getRGB(x, y);
		argb[0] = (pixel & 0xff000000) >>> 24;
		argb[1] = (pixel & 0xff0000) >>> 16;
		argb[2] = (pixel & 0xff00) >>> 8;
		argb[3] = (pixel & 0xff);
	}

	private static void writePixel(BufferedImage img, int x, int y, int[] argb) {
		int pixel = argb[0] << 24;
		pixel |= argb[1] << 16;
		pixel |= argb[2] << 8;
		pixel |= argb[3];
		img.setRGB(x, y, pixel);
	}

	public Image apply(final Image image) {
		BufferedImage src = image.bufferedImage();
		Image result = new Image(src);

		BufferedImage dst = result.bufferedImage();

		int[] argbIn = new int[4];
		int[] argbOut = new int[4];

		for (int x = 0; x < dst.getWidth(); x++) {
			for (int y = 0; y < dst.getHeight(); y++) {
				readPixel(src, x, y, argbIn);
				filter(argbIn, argbOut);
				writePixel(dst, x, y, argbOut);
			}
		}

		return result;
	}

	protected abstract void filter(int[] argbIn, int[] argbOut);

}
