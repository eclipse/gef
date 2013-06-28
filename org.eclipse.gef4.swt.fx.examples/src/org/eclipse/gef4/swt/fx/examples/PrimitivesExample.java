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
package org.eclipse.gef4.swt.fx.examples;

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swt.fx.Group;
import org.eclipse.gef4.swt.fx.ShapeFigure;
import org.eclipse.gef4.swt.fx.gc.ArcType;
import org.eclipse.gef4.swt.fx.gc.GraphicsContext;
import org.eclipse.gef4.swt.fx.gc.RgbaColor;
import org.eclipse.swt.graphics.FontData;

public class PrimitivesExample implements IExample {

	public static void main(String[] args) {
		new Example(new PrimitivesExample());
	}

	public PrimitivesExample() {
	}

	@Override
	public void addUi(Group c) {
		c.addFigures(new ShapeFigure(new Rectangle(0, 0, 640, 480)) {
			@Override
			public void paint(GraphicsContext g) {
				Rectangle bounds = this.getBounds().getTransformedShape()
						.getBounds();
				g.clearRect(0, 0, bounds.getWidth(), bounds.getHeight());

				g.setFill(new RgbaColor(255, 0, 0));
				g.fillArc(20, 20, 100, 100, 30, 130, ArcType.ROUND);

				g.setFill(new RgbaColor(0, 255, 0));
				g.fillOval(140, 20, 100, 100);

				g.setFill(new RgbaColor(0, 0, 255));
				g.fillPolygon(new double[] { 70, 120, 20 }, new double[] { 140,
						220, 220 }, 3);

				g.setFill(new RgbaColor(255, 255, 0));
				g.fillRect(140, 140, 100, 100);

				g.setFill(new RgbaColor(255, 0, 255));
				g.fillRoundRect(260, 20, 100, 100, 20, 20);

				g.setFill(new RgbaColor(0, 255, 255));

				for (int size = 16, y = 140; size > 6; size -= 2, y += 20) {
					FontData fontData = g.getFont().getFontData()[0];
					fontData.setHeight(size);
					g.setFont(fontData);
					g.fillText("Too long for 100px?", 260, y, 100);
				}
			}
		});
	}

	@Override
	public int getHeight() {
		return 480;
	}

	@Override
	public String getTitle() {
		return "Primitives";
	}

	@Override
	public int getWidth() {
		return 640;
	}

}
