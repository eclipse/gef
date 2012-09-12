package org.eclipse.gef4.graphics.internal.swt;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.graphics.IFontUtils;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class FontUtils implements IFontUtils {

	protected IGraphics graphics;

	public FontUtils(IGraphics graphics) {
		this.graphics = graphics;
	}

	public Dimension getTextDimension(String text) {
		graphics.pushState();
		graphics.writeProperties().applyOn(graphics, "");
		GC gc = ((DisplayGraphics) graphics).getGC();
		Point extent = gc.stringExtent(text);
		graphics.popState();
		return new Dimension(extent.x, extent.y);
	}

}
