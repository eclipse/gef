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
package org.eclipse.gef4.graphics.render.swt.internal;

import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.Font;
import org.eclipse.gef4.graphics.Image;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

class SWTGraphicsUtils {

	static org.eclipse.swt.graphics.Color createSWTColor(Color color) {
		return new org.eclipse.swt.graphics.Color(Display.getCurrent(),
				color.getRed(), color.getGreen(), color.getBlue());
	}

	static org.eclipse.swt.graphics.Font createSWTFont(Font font) {
		int swtStyle = (font.isBold() ? SWT.BOLD : 0)
				| (font.isItalic() ? SWT.ITALIC : 0);
		return new org.eclipse.swt.graphics.Font(Display.getCurrent(),
				font.getFamily(), (int) font.getSize(), swtStyle);
	}
	
	static org.eclipse.swt.graphics.Image createSWTImage(Image image) {
		PaletteData paletteData = new PaletteData(0xff0000, 0xff00, 0xff);
		ImageData imageData = new ImageData(image.getWidth(), image.getHeight(), 32, paletteData);
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int[] argb = Color.getPixelARGB(image.getPixel(x, y));
				imageData.setPixel(x, y, paletteData.getPixel(new RGB(argb[1], argb[2], argb[3])));
				imageData.setAlpha(x, y, argb[0]);
			}
		}
		
		return new org.eclipse.swt.graphics.Image(Display.getCurrent(), imageData);
	}

	static void dispose(org.eclipse.swt.graphics.Resource res) {
		if (res != null && !res.isDisposed()) {
			res.dispose();
		}
	}

}
