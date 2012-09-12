package org.eclipse.gef4.graphics.internal.awt;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.graphics.IFontUtils;
import org.eclipse.gef4.graphics.IGraphics;

public class FontUtils implements IFontUtils {

	protected IGraphics graphics;

	public FontUtils(IGraphics graphics) {
		this.graphics = graphics;
	}

	public Dimension getTextDimension(String text) {
		Graphics2D g2d = ((DisplayGraphics) graphics).getGraphics2D();
		FontMetrics fontMetrics = g2d.getFontMetrics();
		return new Dimension(fontMetrics.stringWidth(text),
				fontMetrics.getHeight());
	}

}
