/*******************************************************************************
 * Copyright (c) 2012, 2013 itemis AG and others.
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

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.image.Image;

public class OverviewExample implements IExample {

	private static final String IMAGE_FILE = "test.png";
	private static final Color RED = new Color(255, 0, 0), YELLOW = new Color(
			255, 255, 0, 128), BLUE = new Color(0, 0, 255), BLACK = new Color(
			0, 0, 0);

	private Image image;

	@Override
	public int getHeight() {
		return 480;
	}

	private Image getImage() {
		if (image == null) {
			try {
				image = new Image(ImageIO.read(OverviewExample.class
						.getResource(IMAGE_FILE)));
			} catch (IOException x) {
				System.out.println("Cannot load image file '" + IMAGE_FILE
						+ "':");
				x.printStackTrace();
			}
		}
		return image;
	}

	@Override
	public String getTitle() {
		return "GEF4 Graphics - Simple Example";
	}

	@Override
	public int getWidth() {
		return 640;
	}

	@Override
	public void renderScene(IGraphics g) {
		Rectangle rectangle = new Rectangle(20, 20, 400, 400);

		g.setDraw(RED).setFill(YELLOW).pushState();

		g.fill(rectangle).draw(rectangle.getOutline());

		PolyBezier cubicInterpolation = PolyBezier.interpolateCubic(new Point(
				50, 50), new Point(200, 100), new Point(150, 200), new Point(
				50, 300), new Point(150, 350), new Point(150, 200), new Point(
				200, 75), new Point(300, 100), new Point(150, 400));

		g.setFill(BLUE).setDraw(BLACK).setLineWidth(3);
		g.fill(cubicInterpolation.toPath()).draw(cubicInterpolation);

		g.popState();

		rectangle.shrink(150, 150);
		g.fill(rectangle).draw(rectangle.getOutline());

		String text = "This is a first test example.";
		Dimension textDimension = g.getTextDimension(text);

		g.translate(270, 50);
		g.write(text);
		g.draw(new Rectangle(new Point(), textDimension).getOutline());

		g.translate(50, 50).rotate(Angle.fromDeg(20));
		g.paint(getImage());
	}
}
