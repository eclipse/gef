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
import java.net.URL;

import javax.imageio.ImageIO;

import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.image.Image;
import org.eclipse.gef4.graphics.image.operations.AbstractPixelNeighborhoodFilterOperation.EdgeMode;
import org.eclipse.gef4.graphics.image.operations.ImageOperations;

public class BlurExample implements IExample {

	private static final String IMAGE_FILENAME = "test.png";
	private static final int CONST_COLOR = new Color(255, 0, 0).toPixelARGB();
	private static final double STANDARD_DEVIATION = 4;

	private Image imageNormal;
	private Image imageBlurredNoOp;
	private Image imageBlurredConstPixel;
	private Image imageBlurredOverlap;
	private Image imageBlurredConstNeighbors;

	@Override
	public int getHeight() {
		return 320;
	}

	@Override
	public String getTitle() {
		return "Blur Example";
	}

	@Override
	public int getWidth() {
		return 840;
	}

	private void initResources() {
		if (imageNormal != null) {
			return;
		}

		System.out.println("loading resources...");
		URL resource = this.getClass().getResource(IMAGE_FILENAME);
		try {
			imageNormal = new Image(ImageIO.read(resource));
		} catch (IOException x) {
			System.out.println("error: cannot read resource '" + IMAGE_FILENAME
					+ "'");
			System.exit(1);
		}

		System.out.println("blurring image without edges...");
		imageBlurredNoOp = ImageOperations.getGaussianBlur(STANDARD_DEVIATION,
				new EdgeMode.NoOperation()).apply(imageNormal);

		System.out.println("blurring image with constant edges...");
		imageBlurredConstPixel = ImageOperations.getGaussianBlur(
				STANDARD_DEVIATION, new EdgeMode.ConstantPixel(CONST_COLOR))
				.apply(imageNormal);

		System.out.println("blurring image overlapping...");
		imageBlurredOverlap = ImageOperations.getGaussianBlur(
				STANDARD_DEVIATION, new EdgeMode.Overlap()).apply(imageNormal);

		System.out.println("blurring image with constant edge neighbors...");
		imageBlurredConstNeighbors = ImageOperations.getGaussianBlur(
				STANDARD_DEVIATION,
				new EdgeMode.ConstantPixelNeighbors(CONST_COLOR)).apply(
				imageNormal);
	}

	@Override
	public void renderScene(IGraphics g) {
		initResources();
		g.pushState();
		g.paint(imageNormal).translate(0, imageNormal.getHeight())
				.paint(imageBlurredNoOp);
		g.restoreState();
		g.translate(imageNormal.getWidth(), 0).paint(imageBlurredConstPixel);
		g.pushState();
		g.translate(0, imageNormal.getHeight()).paint(
				imageBlurredConstNeighbors);
		g.popState();
		g.translate(imageNormal.getWidth(), 0).paint(imageBlurredOverlap);
		g.popState();
	}

}
