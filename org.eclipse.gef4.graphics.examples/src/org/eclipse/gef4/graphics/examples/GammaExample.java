package org.eclipse.gef4.graphics.examples;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.Gradient;
import org.eclipse.gef4.graphics.Gradient.Linear;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.color.Color;

public class GammaExample implements IExample {

	public static void main(String[] args) {
		new SwtExample(new GammaExample());
	}

	@Override
	public int getHeight() {
		return 300;
	}

	@Override
	public String getTitle() {
		return "Gamma Correction";
	}

	@Override
	public int getWidth() {
		return 560;
	}

	@Override
	public void renderScene(IGraphics g) {
		Rectangle rect = new Rectangle(0, 0, 250, 100);
		Linear gradient = new Gradient.Linear(new Point(0, 0),
				new Point(250, 0)).addStop(0, new Color(255, 0, 0)).addStop(1,
				new Color(255, 255, 0));

		g.setFill(gradient);
		g.translate(20, 20);

		g.pushState();
		text(g, "without gamma correction");
		g.fill(rect);
		g.popState();

		g.translate(270, 0);
		text(g, "with gamma correction");
		g.setFill(gradient.setGammaCorrection(2.2));
		g.fill(rect);
	}

	private void text(IGraphics g, String text) {
		g.write(text);
		g.translate(0, g.getTextDimension(text).getHeight());
	}

}
