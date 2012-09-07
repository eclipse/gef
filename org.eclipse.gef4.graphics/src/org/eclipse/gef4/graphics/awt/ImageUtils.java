package org.eclipse.gef4.graphics.awt;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.graphics.IImageUtils;
import org.eclipse.gef4.graphics.Image;

public class ImageUtils implements IImageUtils {

	public Dimension getImageDimension(Image image) {
		java.awt.Image awtImage = Utils.toAWTImage(image);
		return new Dimension(awtImage.getWidth(null), awtImage.getHeight(null));
	}

}
