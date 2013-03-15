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

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.LineCap;
import org.eclipse.gef4.graphics.LineJoin;
import org.eclipse.gef4.graphics.color.Color;

public class SimpleGraphicsExample implements IExample {

	@Override
	public int getHeight() {
		return 480;
	}

	@Override
	public String getTitle() {
		return "GEF4 Graphics - Simple Graphics Example";
	}

	@Override
	public int getWidth() {
		return 640;
	}

	@Override
	public void renderScene(IGraphics g) {
		// prepare geometries
		final Ellipse ellipse = new Ellipse(50, 50, 350, 200);
		final Rectangle rectangle = new Rectangle(100, 160, 125, 220);
		final Polygon triangle = new Polygon(260, 170, 190, 300, 330, 300);

		// prepare colors
		final Color red = new Color(255, 0, 0);
		final Color darkRed = new Color(128, 0, 0);
		final Color blue = new Color(0, 0, 255);
		final Color green = new Color(0, 255, 0);
		final Color darkGreen = new Color(0, 128, 0);

		g.setLineWidth(4);
		g.pushState(); // save the current set of properties on the stack

		g.setFill(red).setDraw(darkRed).setDashArray(25, 10);
		g.fill(ellipse).draw(ellipse.getOutline());

		g.restoreState(); // restore the previously saved properties

		g.setFill(blue).setLineJoin(LineJoin.ROUND).setLineCap(LineCap.ROUND);
		g.fill(rectangle).draw(rectangle.getOutline());

		g.popState(); // removes the previously saved properties from the stack
						// and enables the properties that were set in advance

		g.setFill(green).setDraw(darkGreen).setLineJoin(LineJoin.MITER);
		g.fill(triangle).draw(triangle.getOutline());
	}

}
