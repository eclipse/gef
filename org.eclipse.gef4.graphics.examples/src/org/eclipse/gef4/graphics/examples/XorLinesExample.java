package org.eclipse.gef4.graphics.examples;

import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.color.Color;

public class XorLinesExample implements IExample {

	@Override
	public int getHeight() {
		return 480;
	}

	@Override
	public String getTitle() {
		return "Xor Lines Example";
	}

	@Override
	public int getWidth() {
		return 640;
	}

	@Override
	public void renderScene(IGraphics g) {
		g.setAntiAliasing(false);

		g.setFill(new Color(0, 0, 0));
		g.fill(new Rectangle(30, 20, 80, 80));

		g.setXorMode(true);

		g.setLineWidth(20);

		g.setDraw(new Color(255, 0, 0));
		g.draw(new Line(60, 50, 500, 50));

		g.setDraw(new Color(0, 255, 0));
		g.draw(new Line(40, 60, 450, 60));

		g.setDraw(new Color(0, 0, 255));
		g.draw(new Line(60, 70, 400, 70));
	}

}
