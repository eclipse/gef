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
package org.eclipse.gef4.graphics.awt;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.Font;
import org.eclipse.gef4.graphics.IBlitProperties;
import org.eclipse.gef4.graphics.Image;
import org.eclipse.gef4.graphics.filters.ConvolutionFilter;
import org.eclipse.gef4.graphics.filters.IImageFilter;

class Utils {

	public static BufferedImage applyImageFilters(Image image,
			IBlitProperties blitProperties) {
		BufferedImage awtImage = Utils.toAWTImage(image);
		for (IImageFilter filter : blitProperties.filters()) {
			if (filter instanceof ConvolutionFilter) {
				ConvolutionFilter f = (ConvolutionFilter) filter;
				double[] kernelAsDoubles = f.getKernel();
				float[] kernelAsFloats = new float[kernelAsDoubles.length];
				for (int i = 0; i < kernelAsFloats.length; i++) {
					kernelAsFloats[i] = (float) kernelAsDoubles[i];
				}
				Kernel kernel = new Kernel(f.getDimension(), f.getDimension(),
						kernelAsFloats);
				ConvolveOp op = new ConvolveOp(kernel);
				awtImage = op.filter(awtImage, null);
			} else {
				throw new UnsupportedOperationException("The specified IImageFilter " + filter + " is not yet supported.");
			}
		}
		return awtImage;
	}

	/**
	 * Constructs a new {@link java.awt.Color} representation of the given
	 * {@link Color}.
	 * 
	 * @param color
	 *            the {@link Color} object to transform
	 * @return a new {@link java.awt.Color} representation of this {@link Color}
	 */
	public static java.awt.Color toAWTColor(Color color) {
		return new java.awt.Color(color.getRed(), color.getGreen(),
				color.getBlue(), color.getAlpha());
	}

	public static java.awt.Font toAWTFont(Font font) {
		/*
		 * As AWT assumes a screen resolution of 72dpi independent of the actual
		 * screen resolution, we have to convert the font size appropriately.
		 * (http://www.eclipse.org/articles/Article-Swing-SWT-Integration/)
		 */
		double resolution = Toolkit.getDefaultToolkit().getScreenResolution();
		int awtSize = (int) Math.round(font.getSize() * resolution / 72d);

		int awtStyle = (font.isBold() ? java.awt.Font.BOLD : 0)
				| (font.isItalic() ? java.awt.Font.ITALIC : 0);

		return new java.awt.Font(font.getFamily(), awtStyle, awtSize);
	}

	public static BufferedImage toAWTImage(Image image) {
		BufferedImage awtImage = null;
		try {
			awtImage = ImageIO.read(image.getImageFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return awtImage;
	}

}
