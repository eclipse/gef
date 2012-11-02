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
package org.eclipse.gef4.graphics.render.awt.internal;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.Font;
import org.eclipse.gef4.graphics.Image;

class AWTGraphicsUtils {

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
		BufferedImage res = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				res.setRGB(x, y, image.getPixel(x, y));
			}
		}

		return res;
	}



}
