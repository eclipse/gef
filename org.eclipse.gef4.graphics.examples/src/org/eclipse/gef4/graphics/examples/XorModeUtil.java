package org.eclipse.gef4.graphics.examples;

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.color.Color;

public class XorModeUtil {

	public XorModeUtil() {
	}

	public void renderScene(IGraphics g) {
		g.scale(3, 3);
		g.setFill(new Color(0, 0, 255));
		g.fill(new Rectangle(5, 5, 90, 45));
		g.setXorMode(true);
		g.setFill(new Color(255, 255, 255));
		g.fill(new Rectangle(20, 20, 50, 50));
		g.setFill(new Color(255, 0, 0));
		g.fill(new Ellipse(80, 20, 50, 50));

		g.setXorMode(true);
		g.setDraw(new Color(255, 0, 255)).setLineWidth(12);
		g.draw(new Line(100, 100, 200, 200));
	}

}
