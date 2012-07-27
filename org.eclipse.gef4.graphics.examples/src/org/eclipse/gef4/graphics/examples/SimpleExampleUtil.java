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

import java.net.URL;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.Image;

public class SimpleExampleUtil {

	protected static final Color RED = new Color(255, 0, 0, 255),
			YELLOW = new Color(255, 255, 0, 255), BLUE = new Color(0, 0, 255,
					255), BLACK = new Color(0, 0, 0, 255);

	protected static void draw(IGraphics g, URL imageFile) {
		PolyBezier cubicInterpolation = PolyBezier.interpolateCubic(new Point(
				50, 50), new Point(200, 100), new Point(150, 200), new Point(
				50, 300), new Point(150, 350), new Point(150, 200), new Point(
				200, 75), new Point(300, 100), new Point(150, 400));

		g.getDrawProperties().setColor(RED);
		g.getFillProperties().setColor(YELLOW);

		Rectangle rectangle = new Rectangle(20, 20, 400, 400);
		g.fill(rectangle);
		g.draw(rectangle.getOutline());

		g.pushState();
		g.getFillProperties().setColor(BLUE);
		g.fill(cubicInterpolation.toPath());
		g.popState();

		g.pushState();
		g.getDrawProperties().setColor(BLACK).setLineWidth(3);
		g.draw(cubicInterpolation);
		g.popState();

		rectangle.shrink(150, 150);
		g.fill(rectangle);
		g.draw(rectangle.getOutline());

		g.pushState();
		g.getCanvasProperties().getAffineTransform().translate(270, 50);
		g.write("This is a first test example.");
		g.pushState();
		AffineTransform at = g.getCanvasProperties().getAffineTransform();
		at.translate(50, 50);
		at.rotate(0.3);
		g.blit(new Image(imageFile));
		g.popState();
		g.popState();
	}

}
