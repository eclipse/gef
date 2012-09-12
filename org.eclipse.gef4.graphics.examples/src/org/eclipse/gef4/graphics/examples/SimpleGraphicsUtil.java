package org.eclipse.gef4.graphics.examples;

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.IDrawProperties.LineCap;
import org.eclipse.gef4.graphics.IDrawProperties.LineJoin;
import org.eclipse.gef4.graphics.IGraphics;

public class SimpleGraphicsUtil {

	public static void renderScene(IGraphics g) {
		final Ellipse ellipse = new Ellipse(50, 50, 350, 200);
		final Rectangle rectangle = new Rectangle(100, 160, 125, 220);
		final Polygon triangle = new Polygon(260, 170, 190, 300, 330, 300);
		final Color red = new Color(255, 0, 0, 255);
		final Color darkRed = new Color(128, 0, 0, 255);
		final Color blue = new Color(0, 0, 255, 255);
		final Color green = new Color(0, 255, 0, 255);
		final Color darkGreen = new Color(0, 128, 0, 255);

		g.pushState();

		g.drawProperties().setLineWidth(4).setAntialiasing(true);
		g.fillProperties().setAntialiasing(true);
		g.pushState();

		g.fillProperties().setColor(red);
		g.drawProperties().setDashArray(25, 10).setColor(darkRed);
		g.fill(ellipse);
		g.draw(ellipse.getOutline());

		g.popState();
		g.pushState();

		g.fillProperties().setColor(blue);
		g.drawProperties().setLineJoin(LineJoin.ROUND)
				.setLineCap(LineCap.ROUND);
		g.fill(rectangle);
		g.draw(rectangle.getOutline());

		g.popState();

		g.fillProperties().setColor(green);
		g.drawProperties().setColor(darkGreen).setLineJoin(LineJoin.MITER);
		g.fill(triangle);
		g.draw(triangle.getOutline());
	}

}
