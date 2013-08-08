/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swt.fx.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.CurvedPolygon;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.QuadraticCurve;
import org.eclipse.gef4.swtfx.CanvasNode;
import org.eclipse.gef4.swtfx.gc.ArcType;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.gef4.swtfx.gc.LinearGradient;
import org.eclipse.gef4.swtfx.gc.RadialGradient;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GraphicsContextTests {

	private static final RGB BACKGROUND_COLOR = new RGB(255, 255, 255);
	private static final int BACKGROUND_COLOR_PIXEL = 0xffffff;
	private static final int HEIGHT = 100;
	private static final int WIDTH = 100;
	private static final CurvedPolygon SHAPE = generateCurvedPolygon(
			new Point(250, 50), new Point(450, 200), new Point(400, 450),
			new Point(250, 350), new Point(100, 450), new Point(50, 200))
			.getScaled(0.2, new Point());
	private static Display display;
	private static CanvasNode canvas;

	@AfterClass
	public static void class_clean() {
		GraphicsContext graphicsContext = canvas.getGraphicsContext();
		graphicsContext.cleanUp();
		graphicsContext.getGcByReference().dispose();
		canvas.getImage().dispose();
		display.dispose();
	}

	@BeforeClass
	public static void class_init() {
		display = new Display();
		canvas = new CanvasNode(display, WIDTH, HEIGHT, BACKGROUND_COLOR);
	}

	private static CurvedPolygon generateCurvedPolygon(Point... points) {
		Polygon polygon = new Polygon(points);
		Line[] lines = polygon.getOutlineSegments();
		BezierCurve[] curves = new BezierCurve[lines.length];
		for (int i = 0; i < lines.length; i++) {
			Line line = lines[i];
			curves[i] = new QuadraticCurve(line.getP1(), line.get(0.5)
					.translate(0, 20), line.getP2());
		}
		return new CurvedPolygon(curves);
	}

	private GraphicsContext gc;
	private Image image;

	@Before
	public void init() {
		canvas.clear();
		gc = canvas.getGraphicsContext();
		image = canvas.getImage();
	}

	@Test
	public void test_fillArc_with_LinearGradient() {
		gc.setFill(new LinearGradient(new Point(0, 0), new Point(WIDTH, 0))
				.addStop(0, new RgbaColor(255, 0, 0)).addStop(1,
						new RgbaColor(0, 255, 0)));
		gc.fillArc(0, 0, WIDTH, HEIGHT, 30, 120, ArcType.ROUND);

		ImageData imageData = image.getImageData();

		Point p = new Point();
		for (p.x = 0; p.x < canvas.getWidth(); p.x++) {
			for (p.y = 0; p.y < canvas.getHeight(); p.y++) {
				int pixel = imageData.getPixel((int) p.x, (int) p.y);

				if (pixel != BACKGROUND_COLOR_PIXEL) {
					RGB rgb = imageData.palette.getRGB(pixel);
					if (p.x < WIDTH / 2) {
						assertTrue("on the left side (x=" + p.x + "/" + WIDTH
								+ ") should be more red (" + rgb.red
								+ ") than green (" + rgb.green + ")",
								rgb.red >= rgb.green);
					} else if (p.x > WIDTH / 2) {
						assertTrue("on the right side (x=" + p.x + "/" + WIDTH
								+ ") should be more green (" + rgb.green
								+ ") than red (" + rgb.red + ")",
								rgb.green >= rgb.red);
					}
				}
			}
		}
	}

	@Test
	public void test_fillPath_with_LinearGradient() {
		gc.setFill(new LinearGradient(new Point(0, 0), new Point(WIDTH, 0))
				.addStop(0, new RgbaColor(255, 0, 0)).addStop(1,
						new RgbaColor(0, 255, 0)));
		gc.fillPath(SHAPE.toPath());

		// shrink it for the test
		CurvedPolygon interior = SHAPE.getScaled(0.9);

		ImageData imageData = image.getImageData();

		Point p = new Point();
		for (p.x = 0; p.x < canvas.getWidth(); p.x++) {
			for (p.y = 0; p.y < canvas.getHeight(); p.y++) {
				if (interior.contains(p)) {
					int pixel = imageData.getPixel((int) p.x, (int) p.y);
					assertFalse(
							"at " + p + " should not be a background pixel",
							BACKGROUND_COLOR_PIXEL == pixel);
					RGB rgb = imageData.palette.getRGB(pixel);
					if (p.x < canvas.getWidth() / 2) {
						assertTrue(
								"on the left side (x=" + p.x + "/"
										+ canvas.getWidth()
										+ ") should be more red (" + rgb.red
										+ ") than green (" + rgb.green + ")",
								rgb.red >= rgb.green);
					} else if (p.x > canvas.getWidth() / 2) {
						assertTrue(
								"on the right side (x=" + p.x + "/"
										+ canvas.getWidth()
										+ ") should be more green ("
										+ rgb.green + ") than red (" + rgb.red
										+ ")", rgb.green >= rgb.red);
					}
				}
			}
		}
	}

	@Test
	public void test_fillPath_with_RadialGradient() {
		Ellipse boundary = new Ellipse(canvas.getLayoutBounds());
		gc.setFill(new RadialGradient(boundary).addStop(0,
				new RgbaColor(255, 0, 0)).addStop(1, new RgbaColor(0, 255, 0)));
		gc.fillPath(SHAPE.toPath());

		// shrink it for the test
		CurvedPolygon interior = SHAPE.getScaled(0.9);

		ImageData imageData = image.getImageData();

		Point p = new Point();
		for (p.x = 0; p.x < canvas.getWidth(); p.x++) {
			for (p.y = 0; p.y < canvas.getHeight(); p.y++) {
				if (interior.contains(p)) {
					int pixel = imageData.getPixel((int) p.x, (int) p.y);
					assertFalse(
							"at " + p + " should not be a background pixel",
							BACKGROUND_COLOR_PIXEL == pixel);
					RGB rgb = imageData.palette.getRGB(pixel);
					double distance = boundary.getCenter().getDistance(p);
					if (distance < canvas.getWidth() / 4) {
						assertTrue("near the middle (at " + p + ", distance="
								+ distance + ") should be more red (" + rgb.red
								+ ") than green (" + rgb.green + ")",
								rgb.red >= rgb.green);
					} else if (distance > canvas.getWidth() / 4) {
						assertTrue("near the border (distance=" + distance
								+ ") should be more green (" + rgb.green
								+ ") than red (" + rgb.red + ")",
								rgb.green >= rgb.red);
					}
				}
			}
		}
	}
}
