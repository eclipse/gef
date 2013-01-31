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
package org.eclipse.gef4.graphics.examples;

import javax.imageio.ImageIO;

import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.image.Image;

public class ImageASCII {

	private static final int HEIGHT = 25;
	private static final int WIDTH = 80;
	// TODO: add more intensity levels
	private static final char[] v2c = { '#', '$', 'W', '%', '§', 'E', '4', 'ä',
			'u', 'i', '=', '*', '-', ',', '.', ' ' };
	private static final double vp = 255 / (v2c.length - 1);

	private static final String imgFile = "bezier-intersection-and-overlap.png";

	public static void main(String[] args) {
		new ImageASCII();
	}

	public ImageASCII() {
		Image img;
		try {
			img = new Image(ImageIO.read(this.getClass().getResource(imgFile)));
		} catch (Exception x) {
			System.out.println("Cannot load image file '" + imgFile + "'");
			return;
		}

		// img = img.apply(ArithmeticOperations.getInvertOperation());
		Image scaled = img.getScaledTo(WIDTH, HEIGHT);

		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				int v = Color.computePixelIntensity(scaled.getPixel(x, y));
				System.out.print(v2c[(int) (v / vp)]);
			}
			System.out.print("\n");
		}
	}
}
