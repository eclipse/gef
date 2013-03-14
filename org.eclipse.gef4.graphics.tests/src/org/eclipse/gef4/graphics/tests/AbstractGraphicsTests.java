/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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
package org.eclipse.gef4.graphics.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.Gradient;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.IImageGraphics;
import org.eclipse.gef4.graphics.InterpolationHint;
import org.eclipse.gef4.graphics.LineCap;
import org.eclipse.gef4.graphics.LineJoin;
import org.eclipse.gef4.graphics.Pattern;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.font.Font;
import org.eclipse.gef4.graphics.image.Image;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public abstract class AbstractGraphicsTests<T extends IGraphics> {

	/**
	 * The specific {@link IGraphics} implementation to test.
	 */
	protected T graphics;

	@After
	public void cleanUpGraphics() {
		graphics.cleanUp();
	}

	/**
	 * Returns a specific {@link IGraphics} implementation to test.
	 * 
	 * @return a specific {@link IGraphics} implementation
	 */
	public abstract T createGraphics();

	@Before
	public void initializeGraphics() {
		graphics = createGraphics();
	}

	@Test
	public void test_clip() {
		Color fgColor = new Color(255, 0, 0, 255);
		int fgPixel = fgColor.toPixelARGB();

		Color bgColor = new Color(255, 255, 255, 255);
		int bgPixel = bgColor.toPixelARGB();

		Image image = new Image(400, 300, bgColor);

		IImageGraphics ig = graphics.createImageGraphics(image);

		// assure that no transformations are performed
		ig.setDeviceDpi(ig.getLogicalDpi());

		ig.intersectClip(new Rectangle(100, 100, 200, 100));
		ig.intersectClip(new Rectangle(150, 50, 100, 200));
		Path clip = ig.getClip();

		ig.setFill(fgColor);
		ig.fill(new Rectangle(0, 0, image.getWidth(), image.getHeight()));

		ig.cleanUp();

		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int pixel = image.getPixel(x, y);
				if (clip.contains(new Point(x, y))) {
					assertEquals(fgPixel, pixel);
				} else {
					assertEquals(bgPixel, pixel);
				}
			}
		}
	}

	@Test
	public void test_defaultProperties() {
		/*
		 * As we are checking the default values here, the exact comparison of
		 * floating point numbers is okay.
		 */
		assertEquals(IGraphics.DEFAULT_AFFINE_TRANSFORM,
				graphics.getAffineTransform());
		assertEquals(IGraphics.DEFAULT_ANTI_ALIASING, graphics.isAntiAliasing());

		Path clip = graphics.getClip();
		if (clip == null) {
			assertNull(IGraphics.DEFAULT_CLIPPING_AREA);
		} else {
			assertEquals(IGraphics.DEFAULT_CLIPPING_AREA, clip);
		}

		assertTrue(Arrays.equals(IGraphics.DEFAULT_DASH_ARRAY,
				graphics.getDashArray()));

		assertEquals(IGraphics.DEFAULT_DASH_BEGIN, graphics.getDashBegin(), 0);
		assertEquals(IGraphics.DEFAULT_DRAW_PATTERN, graphics.getDrawPattern());
		assertEquals(IGraphics.DEFAULT_FILL_PATTERN, graphics.getFillPattern());
		assertEquals(IGraphics.DEFAULT_FONT, graphics.getFont());
		assertEquals(IGraphics.DEFAULT_INTERPOLATION_HINT,
				graphics.getInterpolationHint());
		assertEquals(IGraphics.DEFAULT_LINE_CAP, graphics.getLineCap());
		assertEquals(IGraphics.DEFAULT_LINE_JOIN, graphics.getLineJoin());
		assertEquals(IGraphics.DEFAULT_LINE_WIDTH, graphics.getLineWidth(), 0);
		assertEquals(IGraphics.DEFAULT_MITER_LIMIT, graphics.getMiterLimit(), 0);
		assertEquals(IGraphics.DEFAULT_WRITE_BACKGROUND,
				graphics.getWriteBackground());
		assertEquals(IGraphics.DEFAULT_WRITE_PATTERN,
				graphics.getWritePattern());
		assertEquals(IGraphics.DEFAULT_XOR_MODE, graphics.isXorMode());
	}

	@Test
	public void test_drawPatternManipulation() {
		Color color = new Color(1, 2, 3, 4);
		graphics.setDraw(color);
		assertEquals(Pattern.Mode.COLOR, graphics.getDrawPatternMode());
		assertEquals(color, graphics.getDrawPatternColor());

		Gradient.Linear gradient = new Gradient.Linear(new Point(), new Point(
				10, 10));
		graphics.setDraw(gradient);
		assertEquals(Pattern.Mode.GRADIENT, graphics.getDrawPatternMode());
		assertEquals(color, graphics.getDrawPatternColor()); // unaffected?
		assertEquals(gradient, graphics.getDrawPatternGradient());

		Image image = new Image(10, 10);
		graphics.setDraw(image);
		assertEquals(Pattern.Mode.IMAGE, graphics.getDrawPatternMode());
		assertEquals(color, graphics.getDrawPatternColor()); // unaffected?
		assertEquals(gradient, graphics.getDrawPatternGradient()); // unaffected?
		assertEquals(image, graphics.getDrawPatternImage());

		color = new Color(5, 6, 7, 8);
		graphics.setDrawPatternColor(color);
		assertEquals(Pattern.Mode.IMAGE, graphics.getDrawPatternMode()); // unaffected?
		assertEquals(color, graphics.getDrawPatternColor());
		assertEquals(gradient, graphics.getDrawPatternGradient()); // unaffected?
		assertEquals(image, graphics.getDrawPatternImage()); // unaffected?

		gradient = new Gradient.Linear(new Point(-10, -20), new Point());
		graphics.setDrawPatternGradient(gradient);
		assertEquals(Pattern.Mode.IMAGE, graphics.getDrawPatternMode()); // unaffected?
		assertEquals(color, graphics.getDrawPatternColor()); // unaffected?
		assertEquals(gradient, graphics.getDrawPatternGradient());
		assertEquals(image, graphics.getDrawPatternImage()); // unaffected?

		Pattern.Mode mode = Pattern.Mode.COLOR;
		graphics.setDrawPatternMode(mode);
		assertEquals(mode, graphics.getDrawPatternMode());
		assertEquals(color, graphics.getDrawPatternColor()); // unaffected?
		assertEquals(gradient, graphics.getDrawPatternGradient()); // unaffected?
		assertEquals(image, graphics.getDrawPatternImage()); // unaffected?

		image = new Image(20, 20);
		graphics.setDrawPatternImage(image);
		assertEquals(mode, graphics.getDrawPatternMode()); // unaffected?
		assertEquals(color, graphics.getDrawPatternColor()); // unaffected?
		assertEquals(gradient, graphics.getDrawPatternGradient()); // unaffected?
		assertEquals(image, graphics.getDrawPatternImage());

		color = new Color(255, 255, 0);
		gradient = new Gradient.Linear(new Point(), new Point());
		image = new Image(200, 200);
		mode = Pattern.Mode.GRADIENT;
		Pattern newPattern = new Pattern(color, gradient, image, mode);
		graphics.setDrawPattern(newPattern);
		Pattern drawPattern = graphics.getDrawPattern();
		assertEquals(color, drawPattern.getColor());
		assertEquals(gradient, drawPattern.getGradient());
		assertEquals(image, drawPattern.getImage());
		assertEquals(mode, drawPattern.getMode());
	}

	@Test
	public void test_fillPatternManipulation() {
		Color color = new Color(1, 2, 3, 4);
		graphics.setFill(color);
		assertEquals(Pattern.Mode.COLOR, graphics.getFillPatternMode());
		assertEquals(color, graphics.getFillPatternColor());

		Gradient.Linear gradient = new Gradient.Linear(new Point(), new Point(
				10, 10));
		graphics.setFill(gradient);
		assertEquals(Pattern.Mode.GRADIENT, graphics.getFillPatternMode());
		assertEquals(color, graphics.getFillPatternColor()); // unaffected?
		assertEquals(gradient, graphics.getFillPatternGradient());

		Image image = new Image(10, 10);
		graphics.setFill(image);
		assertEquals(Pattern.Mode.IMAGE, graphics.getFillPatternMode());
		assertEquals(color, graphics.getFillPatternColor()); // unaffected?
		assertEquals(gradient, graphics.getFillPatternGradient()); // unaffected?
		assertEquals(image, graphics.getFillPatternImage());

		color = new Color(5, 6, 7, 8);
		graphics.setFillPatternColor(color);
		assertEquals(Pattern.Mode.IMAGE, graphics.getFillPatternMode()); // unaffected?
		assertEquals(color, graphics.getFillPatternColor());
		assertEquals(gradient, graphics.getFillPatternGradient()); // unaffected?
		assertEquals(image, graphics.getFillPatternImage()); // unaffected?

		gradient = new Gradient.Linear(new Point(-10, -20), new Point());
		graphics.setFillPatternGradient(gradient);
		assertEquals(Pattern.Mode.IMAGE, graphics.getFillPatternMode()); // unaffected?
		assertEquals(color, graphics.getFillPatternColor()); // unaffected?
		assertEquals(gradient, graphics.getFillPatternGradient());
		assertEquals(image, graphics.getFillPatternImage()); // unaffected?

		Pattern.Mode mode = Pattern.Mode.COLOR;
		graphics.setFillPatternMode(mode);
		assertEquals(mode, graphics.getFillPatternMode());
		assertEquals(color, graphics.getFillPatternColor()); // unaffected?
		assertEquals(gradient, graphics.getFillPatternGradient()); // unaffected?
		assertEquals(image, graphics.getFillPatternImage()); // unaffected?

		image = new Image(20, 20);
		graphics.setFillPatternImage(image);
		assertEquals(mode, graphics.getFillPatternMode()); // unaffected?
		assertEquals(color, graphics.getFillPatternColor()); // unaffected?
		assertEquals(gradient, graphics.getFillPatternGradient()); // unaffected?
		assertEquals(image, graphics.getFillPatternImage());

		color = new Color(255, 255, 0);
		gradient = new Gradient.Linear(new Point(), new Point());
		image = new Image(200, 200);
		mode = Pattern.Mode.GRADIENT;
		Pattern newPattern = new Pattern(color, gradient, image, mode);
		graphics.setFillPattern(newPattern);
		Pattern fillPattern = graphics.getFillPattern();
		assertEquals(color, fillPattern.getColor());
		assertEquals(gradient, fillPattern.getGradient());
		assertEquals(image, fillPattern.getImage());
		assertEquals(mode, fillPattern.getMode());
	}

	@Test
	public void test_fontManipulation() {
		String family = graphics.getFontFamily();
		double size = graphics.getFontSize();
		int style = graphics.getFontStyle();

		family = "Monospace";
		graphics.setFontFamily(family);
		assertEquals(family, graphics.getFontFamily());
		assertEquals(size, graphics.getFontSize(), 0);
		assertEquals(style, graphics.getFontStyle());

		size = 3.14159;
		graphics.setFontSize(size);
		assertEquals(family, graphics.getFontFamily());
		assertEquals(size, graphics.getFontSize(), 0);
		assertEquals(style, graphics.getFontStyle());

		style = Font.STYLE_BOLD | Font.STYLE_ITALIC | Font.STYLE_UNDERLINED;
		graphics.setFontStyle(style);
		assertEquals(family, graphics.getFontFamily());
		assertEquals(size, graphics.getFontSize(), 0);
		assertEquals(style, graphics.getFontStyle());

		family = "Arial";
		size = 15;
		style = Font.STYLE_NORMAL;
		graphics.setFont(new Font(family, size, style));
		assertEquals(family, graphics.getFontFamily());
		assertEquals(size, graphics.getFontSize(), 0);
		assertEquals(style, graphics.getFontStyle());

		Font font = graphics.getFont();
		assertEquals(family, font.getFamily());
		assertEquals(size, font.getSize(), 0);
		assertEquals(style, font.getStyle());
	}

	@Test
	public void test_interpolationHint() {
		graphics.setInterpolationHint(InterpolationHint.QUALITY);
		assertEquals(InterpolationHint.QUALITY, graphics.getInterpolationHint());
		graphics.setInterpolationHint(InterpolationHint.SPEED);
		assertEquals(InterpolationHint.SPEED, graphics.getInterpolationHint());

		boolean thrown = false;
		try {
			graphics.setInterpolationHint(null);
		} catch (IllegalArgumentException x) {
			thrown = true;
		}
		assertTrue(thrown);

		// TODO: How to test the real interpolation quality?
	}

	@Test
	public void test_rotate() {
		AffineTransform at0 = graphics.getAffineTransform();

		graphics.pushState();

		AffineTransform at1 = graphics.getAffineTransform();
		assertEquals(at0, at1);

		graphics.rotate(Angle.fromDeg(45));
		at1 = graphics.getAffineTransform();
		assertFalse(at0.equals(at1));

		graphics.popState();

		at1 = graphics.getAffineTransform();
		assertEquals(at0, at1);
	}

	@Test
	public void test_scale() {
		AffineTransform at0 = graphics.getAffineTransform();

		graphics.pushState();

		AffineTransform at1 = graphics.getAffineTransform();
		assertEquals(at0, at1);

		graphics.scale(2, 3);
		at1 = graphics.getAffineTransform();
		assertFalse(at0.equals(at1));

		graphics.popState();

		at1 = graphics.getAffineTransform();
		assertEquals(at0, at1);
	}

	@Test
	public void test_setAffineTransform() {
		AffineTransform at = new AffineTransform(1, 2, 3, 4, 5, 6);
		graphics.setAffineTransform(at);
		assertEquals(at, graphics.getAffineTransform());

		graphics.setAffineTransform(null);
		assertTrue(graphics.getAffineTransform().isIdentity());
	}

	@Test
	public void test_setAntiAliasing() {
		graphics.setAntiAliasing(true);
		assertTrue(graphics.isAntiAliasing());
		graphics.setAntiAliasing(false);
		assertFalse(graphics.isAntiAliasing());

		// TODO: How to check for real anti-aliasing?
	}

	@Test
	public void test_setClippingArea() {
		Path clip = new Polygon(50, 100, 100, 50, 150, 100, 100, 150).toPath();
		graphics.setClip(clip);
		assertEquals(clip, graphics.getClip());

		graphics.setClip(null);
		assertEquals(null, graphics.getClip());
	}

	@Test
	public void test_shear() {
		AffineTransform at0 = graphics.getAffineTransform();

		graphics.pushState();

		AffineTransform at1 = graphics.getAffineTransform();
		assertEquals(at0, at1);

		graphics.shear(3, 2);
		at1 = graphics.getAffineTransform();
		assertFalse(at0.equals(at1));

		graphics.popState();

		at1 = graphics.getAffineTransform();
		assertEquals(at0, at1);
	}

	@Test
	public void test_states() {
		double lineWidth = graphics.getLineWidth();
		graphics.pushState();
		graphics.setLineWidth(2 * lineWidth);
		assertEquals(2 * lineWidth, graphics.getLineWidth(), 0);
		graphics.restoreState();
		assertEquals(lineWidth, graphics.getLineWidth(), 0);
		graphics.setLineWidth(3 * lineWidth);
		graphics.popState();
		assertEquals(lineWidth, graphics.getLineWidth(), 0);

		boolean thrown = false;
		try {
			graphics.popState();
		} catch (IllegalStateException x) {
			thrown = true;
		}
		assertTrue(thrown);
	}

	@Test
	public void test_strokeManipulation() {
		LineCap cap = LineCap.ROUND;
		graphics.setLineCap(cap);
		assertEquals(cap, graphics.getLineCap());

		LineJoin join = LineJoin.MITER;
		graphics.setLineJoin(join);
		assertEquals(join, graphics.getLineJoin());

		double width = 3.14159;
		graphics.setLineWidth(width);
		assertEquals(width, graphics.getLineWidth(), 0);

		double miterLimit = 25;
		graphics.setMiterLimit(miterLimit);
		assertEquals(miterLimit, graphics.getMiterLimit(), 0);

		double[] dashes = new double[] { 1, 3, 1, 10 };
		graphics.setDashArray(dashes);
		assertTrue(Arrays.equals(dashes, graphics.getDashArray()));

		double dashBegin = 1.4142;
		graphics.setDashBegin(dashBegin);
		assertEquals(dashBegin, graphics.getDashBegin(), 0);
	}

	@Test
	public void test_translate() {
		AffineTransform at0 = graphics.getAffineTransform();

		graphics.pushState();

		AffineTransform at1 = graphics.getAffineTransform();
		assertEquals(at0, at1);

		graphics.translate(10, 10);
		at1 = graphics.getAffineTransform();
		assertFalse(at0.equals(at1));

		graphics.popState();

		at1 = graphics.getAffineTransform();
		assertEquals(at0, at1);
	}

	@Test
	public void test_unclip() {
		Color fgColor = new Color(255, 0, 0, 255);
		Color bgColor = new Color(255, 255, 255, 255);
		int fgPixel = fgColor.toPixelARGB();
		int bgPixel = bgColor.toPixelARGB();

		Image image = new Image(400, 300, bgColor);
		IImageGraphics ig = graphics.createImageGraphics(image);

		ig.intersectClip(new Rectangle(0, 0, image.getWidth(), image
				.getHeight()));
		ig.unionClip(new Rectangle(100, 100, 50, 50));
		Path clip = ig.getClip();

		ig.setFill(fgColor);
		ig.fill(new Rectangle(0, 0, image.getWidth(), image.getHeight()));
		ig.cleanUp();

		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int pixel = image.getPixel(x, y);
				if (clip.contains(new Point(x, y))) {
					assertEquals(fgPixel, pixel);
				} else {
					assertEquals(bgPixel, pixel);
				}
			}
		}
	}

	@Test
	public void test_writePatternManipulation() {
		Color color = new Color(1, 2, 3, 4);
		graphics.setWrite(color);
		assertEquals(Pattern.Mode.COLOR, graphics.getWritePatternMode());
		assertEquals(color, graphics.getWritePatternColor());

		Gradient.Linear gradient = new Gradient.Linear(new Point(), new Point(
				10, 10));
		graphics.setWrite(gradient);
		assertEquals(Pattern.Mode.GRADIENT, graphics.getWritePatternMode());
		assertEquals(color, graphics.getWritePatternColor()); // unaffected?
		assertEquals(gradient, graphics.getWritePatternGradient());

		Image image = new Image(10, 10);
		graphics.setWrite(image);
		assertEquals(Pattern.Mode.IMAGE, graphics.getWritePatternMode());
		assertEquals(color, graphics.getWritePatternColor()); // unaffected?
		assertEquals(gradient, graphics.getWritePatternGradient()); // unaffected?
		assertEquals(image, graphics.getWritePatternImage());

		color = new Color(5, 6, 7, 8);
		graphics.setWritePatternColor(color);
		assertEquals(Pattern.Mode.IMAGE, graphics.getWritePatternMode()); // unaffected?
		assertEquals(color, graphics.getWritePatternColor());
		assertEquals(gradient, graphics.getWritePatternGradient()); // unaffected?
		assertEquals(image, graphics.getWritePatternImage()); // unaffected?

		gradient = new Gradient.Linear(new Point(-10, -20), new Point());
		graphics.setWritePatternGradient(gradient);
		assertEquals(Pattern.Mode.IMAGE, graphics.getWritePatternMode()); // unaffected?
		assertEquals(color, graphics.getWritePatternColor()); // unaffected?
		assertEquals(gradient, graphics.getWritePatternGradient());
		assertEquals(image, graphics.getWritePatternImage()); // unaffected?

		Pattern.Mode mode = Pattern.Mode.COLOR;
		graphics.setWritePatternMode(mode);
		assertEquals(mode, graphics.getWritePatternMode());
		assertEquals(color, graphics.getWritePatternColor()); // unaffected?
		assertEquals(gradient, graphics.getWritePatternGradient()); // unaffected?
		assertEquals(image, graphics.getWritePatternImage()); // unaffected?

		image = new Image(20, 20);
		graphics.setWritePatternImage(image);
		assertEquals(mode, graphics.getWritePatternMode()); // unaffected?
		assertEquals(color, graphics.getWritePatternColor()); // unaffected?
		assertEquals(gradient, graphics.getWritePatternGradient()); // unaffected?
		assertEquals(image, graphics.getWritePatternImage());

		color = new Color(255, 255, 0);
		gradient = new Gradient.Linear(new Point(), new Point());
		image = new Image(200, 200);
		mode = Pattern.Mode.GRADIENT;
		Pattern newPattern = new Pattern(color, gradient, image, mode);
		graphics.setWritePattern(newPattern);
		Pattern writePattern = graphics.getWritePattern();
		assertEquals(color, writePattern.getColor());
		assertEquals(gradient, writePattern.getGradient());
		assertEquals(image, writePattern.getImage());
		assertEquals(mode, writePattern.getMode());
	}

	@Ignore("Not yet implemented.")
	@Test
	public void test_xorMode() {
		Color fgColor = new Color(255, 0, 0, 255); // red
		Color bgColor = new Color(255, 255, 255, 255); // white
		int bgPixel = bgColor.toPixelARGB();
		int xorPixel = new Color(0, 255, 255).toPixelARGB(); // cyan

		assertFalse(graphics.isXorMode());
		graphics.setXorMode(true);
		assertTrue(graphics.isXorMode());
		graphics.setXorMode(false);
		assertFalse(graphics.isXorMode());

		Image image = new Image(400, 300, bgColor);
		IImageGraphics ig = graphics.createImageGraphics(image);
		assertSame(image, ig.getImage());
		ig.setClip(new Rectangle(100, 100, 50, 50).toPath());
		Path clip = ig.getClip();
		ig.setXorMode(true);
		assertTrue(ig.isXorMode());
		ig.setFill(fgColor);
		ig.fill(new Rectangle(0, 0, image.getWidth(), image.getHeight()));
		ig.cleanUp();

		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int pixel = image.getPixel(x, y);
				if (clip.contains(new Point(x, y))) {
					assertEquals(xorPixel, pixel);
				} else {
					assertEquals(bgPixel, pixel);
				}
			}
		}
	}
}
