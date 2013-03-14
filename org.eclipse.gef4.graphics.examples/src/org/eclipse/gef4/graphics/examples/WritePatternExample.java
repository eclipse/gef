package org.eclipse.gef4.graphics.examples;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.Gradient;
import org.eclipse.gef4.graphics.Gradient.CycleMode;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.font.Font;

public class WritePatternExample implements IExample {

	@Override
	public int getHeight() {
		return 480;
	}

	@Override
	public String getTitle() {
		return "Write Pattern Example";
	}

	@Override
	public int getWidth() {
		return 640;
	}

	@Override
	public void renderScene(IGraphics g) {
		Gradient.Linear gradient = new Gradient.Linear(new Point(), new Point(
				100, 0), CycleMode.REFLECT);
		gradient.addStop(0, new Color(255, 0, 0));
		gradient.addStop(1, new Color(255, 255, 0));
		g.setWrite(gradient);
		g.setFontSize(32);
		g.setFontStyle(Font.STYLE_BOLD | Font.STYLE_UNDERLINED);

		g.setDraw(new Color(0, 0, 255));
		g.draw(new Rectangle(0, 0, 100, 100).getOutline());

		String text = getTitle();
		Dimension textSize = g.getTextDimension(text);
		g.write(text);
		g.translate(50, textSize.height);
		g.write(text);
	}
}
