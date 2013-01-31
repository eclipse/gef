/*******************************************************************************
 * Copyright (c) 2012, 2013 itemis AG and others.
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

import org.eclipse.gef4.geometry.convert.swt.Geometry2SWT;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.font.Font;
import org.eclipse.gef4.graphics.image.Image;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class SwtGraphicsUtils {

	public static org.eclipse.swt.graphics.Color createSwtColor(Color color) {
		return new org.eclipse.swt.graphics.Color(Display.getCurrent(),
				color.getRed(), color.getGreen(), color.getBlue());
	}

	public static org.eclipse.swt.graphics.Font createSwtFont(Font font) {
		int swtStyle = (font.isBold() ? SWT.BOLD : 0)
				| (font.isItalic() ? SWT.ITALIC : 0);
		return new org.eclipse.swt.graphics.Font(Display.getCurrent(),
				font.getFamily(), (int) font.getSize(), swtStyle);
	}

	public static org.eclipse.swt.graphics.Image createSwtImage(Image image) {
		PaletteData paletteData = new PaletteData(0xff0000, 0xff00, 0xff);
		ImageData imageData = new ImageData(image.getWidth(),
				image.getHeight(), 32, paletteData);
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int[] rgba = Color.getPixelRGBA(image.getPixel(x, y));
				imageData.setPixel(x, y, paletteData.getPixel(new RGB(rgba[0],
						rgba[1], rgba[2])));
				imageData.setAlpha(x, y, rgba[3]);
			}
		}

		return new org.eclipse.swt.graphics.Image(Display.getCurrent(),
				imageData);
	}

	public static org.eclipse.swt.graphics.Path createSwtPath(Path path,
			Device device) {
		org.eclipse.swt.graphics.Path swtPath = new org.eclipse.swt.graphics.Path(
				device, Geometry2SWT.toSWTPathData(path));
		return swtPath;
	}

}
