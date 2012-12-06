package org.eclipse.gef4.graphics.examples;

import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.Image;
import org.eclipse.gef4.graphics.images.ArithmeticOperations;
import org.eclipse.gef4.graphics.render.ColorFill;
import org.eclipse.gef4.graphics.render.GradientFill;
import org.eclipse.gef4.graphics.render.IGraphics;
import org.eclipse.gef4.graphics.render.ImageFill;

class FillModesUtil {

	public static final String TITLE = "GEF4 Graphics - Fill Modes";
	public static final int HEIGHT = 480;
	public static final int WIDTH = 640;

	private static final String IMAGE_FILE = "test.png";
	private static final Color WHITE_A0 = new Color(255, 255, 255, 0);
	private static final Color WHITE_A192 = new Color(255, 255, 255, 192);
	private static final Color DARK_BLUE = new Color(0, 0, 64);
	private static final Color GREY = new Color(128, 128, 128);
	private static final Color LIGHT_GREY = new Color(192, 192, 192);
	private static final Color BLUE = new Color(0, 0, 255);
	private static final Color GREEN = new Color(0, 255, 0);
	private static final Color RED = new Color(255, 0, 0);
	private static final Color MAGENTA = new Color(255, 0, 255);

	/**
	 * @return
	 * @throws IOException
	 */
	private static Image loadImage() {
		Image image = null;
		try {
			image = new Image(ImageIO.read(FillModesAWT.class
					.getResource(IMAGE_FILE))).apply(
					ArithmeticOperations.getInvertOperation()).getSubImage(0,
					0, 50, 50);
		} catch (IOException x) {
			x.printStackTrace();
		}
		return image;
	}

	public static void renderScene(IGraphics g) {
		long time = System.currentTimeMillis();

		Rectangle rectangle = new Rectangle(0, 0, 100, 100);
		Ellipse circle = new Ellipse(rectangle);

		// ColorFill
		g.canvasProperties().setAffineTransform(
				g.canvasProperties().getAffineTransform().translate(50, 50));

		g.fillProperties().setMode(new ColorFill(MAGENTA));
		g.fill(rectangle);

		// GradientFill.Linear 1
		g.canvasProperties().setAffineTransform(
				g.canvasProperties().getAffineTransform().translate(0, 150));

		GradientFill.Linear linearGradient = new GradientFill.Linear(new Point(
				0, 0), new Point(100, 0)).addStop(0, RED).addStop(0.5, GREEN)
				.addStop(1, BLUE);

		g.fillProperties().setMode(linearGradient);
		g.fill(rectangle);

		// GradientFill.Linear 2
		g.canvasProperties().setAffineTransform(
				g.canvasProperties().getAffineTransform().translate(150, -150));

		linearGradient = new GradientFill.Linear(new Point(0, 0), new Point(
				100, 100)).addStop(0, LIGHT_GREY).addStop(0.5, GREY)
				.addStop(1, LIGHT_GREY);

		g.fillProperties().setMode(linearGradient);
		g.fill(rectangle);

		// GradientFill.Radial
		g.canvasProperties().setAffineTransform(
				g.canvasProperties().getAffineTransform().translate(0, 150));

		GradientFill.Radial radialGradient = new GradientFill.Radial(circle,
				new Point(25, 25)).addStop(0, BLUE).addStop(1, DARK_BLUE);

		g.fillProperties().setMode(radialGradient);
		g.fill(circle);

		// highlights
		radialGradient = new GradientFill.Radial(circle, new Point(25, 25))
				.addStop(0, WHITE_A192).addStop(0.5, WHITE_A0);

		g.fillProperties().setMode(radialGradient);
		g.fill(circle);

		// ImageFill
		g.canvasProperties().setAffineTransform(
				g.canvasProperties().getAffineTransform().translate(150, -150));

		g.fillProperties().setMode(new ImageFill(loadImage()));
		g.fill(circle);

		// show render time
		time = System.currentTimeMillis() - time;
		System.out.println("render time = " + time + "ms");
	}
}