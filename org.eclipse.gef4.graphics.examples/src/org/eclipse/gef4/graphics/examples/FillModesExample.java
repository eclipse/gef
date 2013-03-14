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

class FillModesExample implements IExample {

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

	private Image image;

	@Override
	public int getHeight() {
		return 480;
	}

	private Image getImage() {
		if (image == null) {
			try {
				image = new Image(ImageIO.read(FillModesExampleAwt.class
						.getResource(IMAGE_FILE))).apply(
						ImageOperations.getInvertOperation()).getSubImage(0, 0,
						50, 50);
			} catch (IOException x) {
				x.printStackTrace();
				System.exit(1);
			}
		}

		return image;
	}

	@Override
	public String getTitle() {
		return "GEF4 Graphics - Fill Modes";
	}

	@Override
	public int getWidth() {
		return 640;
	}

	@Override
	public void renderScene(IGraphics g) {
		long time = System.currentTimeMillis();

		// g.setFill(loadImage());
		// g.fill(new Rectangle(0, 0, 100, 100));
		// g.fill(new Rectangle(100, 100, 100, 100));

		Rectangle rectangle = new Rectangle(0, 0, 100, 100);
		Ellipse circle = new Ellipse(rectangle);

		// ColorFill
		g.translate(50, 50);
		g.setFill(MAGENTA);
		g.fill(rectangle);

		// Gradient.Linear 1
		g.translate(0, 150);

		Gradient.Linear linearGradient = new Gradient.Linear(new Point(0, 0),
				new Point(100, 0)).addStop(0, RED).addStop(0.5, GREEN)
				.addStop(1, BLUE);
		g.setFillPatternMode(Mode.GRADIENT).setFillPatternGradient(
				linearGradient);

		g.fill(rectangle);

		// Gradient.Linear 2
		g.translate(150, -150);

		linearGradient = new Gradient.Linear(new Point(0, 0), new Point(100,
				100)).addStop(0, LIGHT_GREY).addStop(0.5, GREY)
				.addStop(1, LIGHT_GREY);
		g.setFillPatternGradient(linearGradient);

		g.fill(rectangle);

		// Gradient.Radial
		g.translate(0, 150);

		Gradient.Radial radialGradient = new Gradient.Radial(circle, new Point(
				25, 25)).addStop(0, BLUE).addStop(1, DARK_BLUE);
		g.setFillPatternGradient(radialGradient);

		g.fill(circle);

		// highlights
		radialGradient = new Gradient.Radial(circle, new Point(25, 25))
				.addStop(0, WHITE_A192).addStop(0.5, WHITE_A0);
		g.setFillPatternGradient(radialGradient);

		g.fill(circle);

		// ImageFill
		g.translate(150, -150);
		g.setFillPatternMode(Mode.IMAGE).setFillPatternImage(getImage());
		g.fill(circle);

		// show render time
		time = System.currentTimeMillis() - time;
		System.out.println("render time = " + time + "ms");
	}

}