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
package org.eclipse.gef4.graphics.swt;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.Font;
import org.eclipse.gef4.graphics.Image;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;

class Utils {

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

	static org.eclipse.swt.graphics.Image createSWTImage(Image img) {
		BufferedImage bufImg = img.bufferedImage();
		int width = bufImg.getWidth();
		int height = bufImg.getHeight();
		int depth = 24;
		ImageData swtdat = new ImageData(width, height,
				depth, new PaletteData(
						0xFF0000, 0xFF00, 0xFF));
		if (swtdat.alphaData == null) {
			swtdat.alphaData = new byte[width * height];
		}

		Raster raster = bufImg.getData();
		int numBands = raster.getNumBands();
		int scanLineWidth = width * numBands;
		int[] awtdat = raster.getPixels(0, 0, width, height, new int[height
		                                                             * scanLineWidth]);

		for (int y = 0; y < height; y++) {
			int swtdatOffset = y * swtdat.bytesPerLine;
			int awtdatOffset = y * scanLineWidth;
			for (int x = 0; x < width; x++) {
				int swtdatIdx = swtdatOffset + x * 3;
				int awtdatIdx = awtdatOffset + x * numBands;
				swtdat.data[swtdatIdx++] = (byte) awtdat[awtdatIdx];
				swtdat.data[swtdatIdx++] = (byte) awtdat[awtdatIdx + 1];
				swtdat.data[swtdatIdx++] = (byte) awtdat[awtdatIdx + 2];
				if (numBands == 4) {
					swtdat.alphaData[y * width + x] = (byte) awtdat[awtdatIdx + 3];
				}
			}
		}

		return new org.eclipse.swt.graphics.Image(Display.getCurrent(), swtdat);
	}

	static void dispose(org.eclipse.swt.graphics.Resource res) {
		if (res != null && !res.isDisposed()) {
			res.dispose();
		}
	}

}
