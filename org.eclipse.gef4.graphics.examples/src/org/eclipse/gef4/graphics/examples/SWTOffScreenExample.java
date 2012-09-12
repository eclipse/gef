package org.eclipse.gef4.graphics.examples;

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.IDrawProperties.LineCap;
import org.eclipse.gef4.graphics.IDrawProperties.LineJoin;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.internal.swt.OffScreenGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

public class SWTOffScreenExample {
	public static void main(String[] args) {
		new SWTOffScreenExample("GEF4 Graphics - SWT OffScreen");
	}

	public SWTOffScreenExample(String title) {
		Display display = new Display();
		Image image = new Image(display, 640, 480);
		OffScreenGraphics g = new OffScreenGraphics(image);
		renderScene(g);
		g.cleanUp();
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] { image.getImageData() };
		imageLoader.save("/home/wienand/Dokumente/bilder/test.png",
				SWT.IMAGE_PNG);
	}

	public void renderScene(IGraphics g) {
		final Ellipse ellipse = new Ellipse(50, 50, 350, 200);
		final Rectangle rectangle = new Rectangle(100, 160, 125, 220);
		final Polygon triangle = new Polygon(260, 170, 190, 300, 330, 300);
		final Color red = new Color(255, 0, 0, 255);
		final Color darkRed = new Color(128, 0, 0, 255);
		final Color blue = new Color(0, 0, 255, 255);
		final Color green = new Color(0, 255, 0, 255);
		final Color darkGreen = new Color(0, 128, 0, 255);

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
