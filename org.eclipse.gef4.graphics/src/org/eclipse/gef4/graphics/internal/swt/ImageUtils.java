package org.eclipse.gef4.graphics.internal.swt;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.graphics.IImageUtils;
import org.eclipse.gef4.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

public class ImageUtils implements IImageUtils {

	public Dimension getImageDimension(Image image) {
		org.eclipse.swt.graphics.Image swtImage = Utils.createSWTImage(image);
		Rectangle imgBounds = swtImage.getBounds();
		Dimension imgDim = new Dimension(imgBounds.width, imgBounds.height);
		swtImage.dispose();
		return imgDim;
	}

}
