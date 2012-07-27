package org.eclipse.gef4.graphics.awt;

import java.awt.Toolkit;

import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.Font;

class Utils {

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

}
