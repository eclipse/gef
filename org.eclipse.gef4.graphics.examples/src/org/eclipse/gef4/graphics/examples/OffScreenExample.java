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

import java.util.Random;

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.Gradient;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.IImageGraphics;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.image.Image;

public class OffScreenExample implements IExample {

	private static final int PAD = 30;
	private static final int IMG_HEIGHT = 300;
	private static final int IMG_WIDTH = 400;

	private Image image;

	@Override
	public int getHeight() {
		return IMG_HEIGHT + PAD + PAD;
	}

	@Override
	public String getTitle() {
		return "GEF4 Graphics - Off Screen Example";
	}

	@Override
	public int getWidth() {
		return IMG_WIDTH + PAD + PAD;
	}

	private void initResource(IGraphics g) {
		if (image == null) {
			image = new Image(IMG_WIDTH, IMG_HEIGHT, new Color(255, 255, 255));

			IImageGraphics ig = g.createImageGraphics(image);

			ig.setFill(new Gradient.Linear(new Point(), new Point(400, 300))
					.addStop(0, new Color(255, 255, 0))
					.addStop(0.25, new Color(255, 128, 0))
					.addStop(0.75, new Color(255, 255, 0))
					.addStop(1, new Color(255, 0, 0)));

			ig.fill(new Rectangle(0, 0, IMG_WIDTH, IMG_HEIGHT));

			// draw bubbles
			Random rng = new Random(System.currentTimeMillis());
			for (int i = 0; i < 25; i++) {
				int side = rng.nextInt(35) + 15;
				Ellipse bubble = new Ellipse(0, 0, side, side);

				ig.pushState().translate(rng.nextInt(IMG_WIDTH - 50),
						rng.nextInt(IMG_HEIGHT - 50));

				ig.setFill(new Gradient.Radial(bubble).addStop(0,
						new Color(64, 255, 255)).addStop(1,
						new Color(64, 64, 255)));
				ig.fill(bubble);

				ig.popState();
			}

			ig.cleanUp();
		}
	}

	@Override
	public void renderScene(IGraphics g) {
		initResource(g);
		g.translate(PAD, PAD);
		g.paint(image);
	}

}
