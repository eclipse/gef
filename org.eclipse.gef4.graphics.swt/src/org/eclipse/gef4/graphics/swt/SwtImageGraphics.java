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
package org.eclipse.gef4.graphics.swt;

import org.eclipse.gef4.graphics.IImageGraphics;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.image.Image;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

public class SwtImageGraphics extends SwtGraphics implements IImageGraphics {

	private Image image;
	private org.eclipse.swt.graphics.Image swtImage;

	public SwtImageGraphics(Image image) {
		this(SwtGraphicsUtils.createSwtImage(image));
		this.image = image;
		initialize();
		setDeviceDpi(getLogicalDpi());
	}

	private SwtImageGraphics(org.eclipse.swt.graphics.Image swtImage) {
		super(new GC(swtImage));
		this.swtImage = swtImage;
	}

	@Override
	public void cleanUp() {
		updateImage();
		getGC().dispose();
	}

	@Override
	public Image getImage() {
		return image;
	}

	@Override
	public IImageGraphics updateImage() {
		ImageData imageData = swtImage.getImageData();
		PaletteData palette = imageData.palette;
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				RGB rgb = palette.getRGB(imageData.getPixel(x, y));
				int alpha = imageData.getAlpha(x, y);
				image.setPixel(x, y, new Color(rgb.red, rgb.green, rgb.blue,
						alpha));
			}
		}
		return this;
	}
}
