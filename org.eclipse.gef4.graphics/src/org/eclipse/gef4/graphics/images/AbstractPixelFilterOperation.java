package org.eclipse.gef4.graphics.images;

import java.awt.image.BufferedImage;

import org.eclipse.gef4.graphics.Image;

public abstract class AbstractPixelFilterOperation implements IImageOperation {

	public Image apply(Image input) {
		BufferedImage in = input.bufferedImage();
		Image output = new Image(in);
		BufferedImage out = output.bufferedImage();

		for (int x = 0; x < in.getWidth(); x++) {
			for (int y = 0; y < in.getHeight(); y++) {
				out.setRGB(x, y, processPixel(in.getRGB(x, y), x, y, input));
			}
		}

		return output;
	}

	protected abstract int processPixel(int argb, int x, int y, Image input);

}
