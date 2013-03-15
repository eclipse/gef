package org.eclipse.gef4.graphics.awt;

import java.awt.image.BufferedImage;

import org.eclipse.gef4.graphics.IImageGraphics;
import org.eclipse.gef4.graphics.image.Image;

public class AwtImageGraphics extends AwtGraphics implements IImageGraphics {

	private Image image;
	private BufferedImage bufferedImage;

	private AwtImageGraphics(BufferedImage image) {
		super(image.createGraphics());
		this.bufferedImage = image;
	}

	/**
	 * Creates a new {@link AwtImageGraphics} to render into the given
	 * {@link Image}. Note that the {@link Image} is not updated immediately.
	 * You have to force the update by calling {@link #updateImage()} or
	 * {@link #cleanUp()}.
	 * 
	 * @param image
	 */
	public AwtImageGraphics(Image image) {
		this(AwtGraphicsUtils.toAwtImage(image));
		this.image = image;
		initialize();
		setDeviceDpi(getLogicalDpi());
	}

	/**
	 * Updates the user's {@link Image} before disposing all allocated system
	 * resources and resetting the underlying drawing toolkit.
	 */
	@Override
	public void cleanUp() {
		updateImage();
		super.cleanUp();
	}

	@Override
	public Image getImage() {
		return image;
	}

	@Override
	public AwtImageGraphics updateImage() {
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				image.setPixel(x, y, bufferedImage.getRGB(x, y));
			}
		}
		return this;
	}

}
