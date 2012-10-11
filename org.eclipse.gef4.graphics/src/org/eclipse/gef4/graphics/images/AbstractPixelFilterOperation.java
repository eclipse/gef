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
