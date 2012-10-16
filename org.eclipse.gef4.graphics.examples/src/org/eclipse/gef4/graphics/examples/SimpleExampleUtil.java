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

import java.io.IOException;
import java.net.URL;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.Image;
import org.eclipse.gef4.graphics.render.IGraphics;

public class SimpleExampleUtil {

	protected static final Color RED = new Color(255, 0, 0, 255),
			YELLOW = new Color(255, 255, 0, 128), BLUE = new Color(0, 0, 255,
					255), BLACK = new Color(0, 0, 0, 255);

	protected static void draw(IGraphics g) throws IOException {
		URL imageResource = SimpleExampleUtil.class.getResource("test.png");
		Image image = new Image(imageResource);

		PolyBezier cubicInterpolation = PolyBezier.interpolateCubic(new Point(
				50, 50), new Point(200, 100), new Point(150, 200), new Point(
				50, 300), new Point(150, 350), new Point(150, 200), new Point(
				200, 75), new Point(300, 100), new Point(150, 400));

		g.drawProperties().setColor(RED);
		g.fillProperties().setColor(YELLOW);

		Rectangle rectangle = new Rectangle(20, 20, 400, 400);

		g.fill(rectangle);
		g.draw(rectangle.getOutline());

		g.pushState();

		g.fillProperties().setColor(BLUE);

		g.fill(cubicInterpolation.toPath());

		g.popState();
		g.pushState();

		g.drawProperties().setColor(BLACK).setLineWidth(3);

		g.draw(cubicInterpolation);

		g.popState();

		rectangle.shrink(150, 150);

		g.fill(rectangle);
		g.draw(rectangle.getOutline());

		g.pushState();

		g.canvasProperties().setAffineTransform(
				g.canvasProperties().getAffineTransform().translate(270, 50));

		String text = "This is a first test example.";

		g.write(text);

		Dimension textDimension = g.fontUtils().getTextDimension(text);

		g.draw(new Rectangle(0, 0, textDimension.width, textDimension.height)
				.getOutline());

		g.pushState();

		AffineTransform at = g.canvasProperties().getAffineTransform();
		at.translate(50, 50).rotate(0.3);

		g.canvasProperties().setAffineTransform(at);

		g.blit(image);

		g.popState();
		g.popState();
	}

}
