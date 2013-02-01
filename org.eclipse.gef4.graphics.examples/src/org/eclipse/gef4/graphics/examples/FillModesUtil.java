package org.eclipse.gef4.graphics.examples;

import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.Gradient;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.Pattern.Mode;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.image.Image;
import org.eclipse.gef4.graphics.image.operations.ImageOperations;

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
					ImageOperations.getInvertOperation()).getSubImage(0, 0, 50,
					50);
		} catch (IOException x) {
			x.printStackTrace();
		}
		return image;
	}

	public static void renderScene(IGraphics g) {
		long time = System.currentTimeMillis();

		// g.setFill(loadImage());
		// g.fill(new Rectangle(0, 0, 100, 100));
		// g.fill(new Rectangle(100, 100, 100, 100));

		Rectangle rectangle = new Rectangle(0, 0, 100, 100);
		Ellipse circle = new Ellipse(rectangle);

		// ColorFill
		g.translate(50, 50);
		// g.setFill(new Color());
		// g.fill(rectangle);
		g.setXorMode(true);
		g.setFill(MAGENTA);
		g.fill(rectangle);
		g.setXorMode(false);

		// Gradient.Linear 1
		Gradient.Linear linearGradient = new Gradient.Linear(new Point(0, 0),
				new Point(100, 0)).addStop(0, RED).addStop(0.5, GREEN)
				.addStop(1, BLUE);

		g.translate(0, 150);
		g.setFillPatternMode(Mode.GRADIENT).setFillPatternGradient(
				linearGradient);
		g.fill(rectangle);

		// Gradient.Linear 2
		linearGradient = new Gradient.Linear(new Point(0, 0), new Point(100,
				100)).addStop(0, LIGHT_GREY).addStop(0.5, GREY)
				.addStop(1, LIGHT_GREY);

		g.translate(150, -150);
		g.setFillPatternGradient(linearGradient);
		g.fill(rectangle);

		// Gradient.Radial
		Gradient.Radial radialGradient = new Gradient.Radial(circle, new Point(
				25, 25)).addStop(0, BLUE).addStop(1, DARK_BLUE);

		g.translate(0, 150);
		g.setFillPatternGradient(radialGradient);
		g.fill(circle);

		// highlights
		radialGradient = new Gradient.Radial(circle, new Point(25, 25))
				.addStop(0, WHITE_A192).addStop(0.5, WHITE_A0);

		g.setFillPatternGradient(radialGradient);
		g.fill(circle);

		// ImageFill
		g.translate(150, -150);
		g.setFillPatternMode(Mode.IMAGE).setFillPatternImage(loadImage());
		g.fill(circle);

		// show render time
		time = System.currentTimeMillis() - time;
		System.out.println("render time = " + time + "ms");
	}
}