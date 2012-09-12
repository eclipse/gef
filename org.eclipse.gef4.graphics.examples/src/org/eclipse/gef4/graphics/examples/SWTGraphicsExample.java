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

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.IDrawProperties.LineCap;
import org.eclipse.gef4.graphics.IDrawProperties.LineJoin;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.internal.swt.DisplayGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SWTGraphicsExample implements PaintListener {

	public static void main(String[] args) {
		new SWTGraphicsExample("GEF4 Graphics - SWT");
	}

	public SWTGraphicsExample(String title) {
		Display display = new Display();

		Shell shell = new Shell(display, SWT.SHELL_TRIM | SWT.DOUBLE_BUFFERED);
		shell.setText(title);
		shell.setBounds(0, 0, 640, 480);
		shell.open();

		shell.addPaintListener(this);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void paintControl(PaintEvent e) {
		DisplayGraphics g = new DisplayGraphics(e.gc);
		renderScene(g);
		g.cleanUp();
	}

	public void renderScene(IGraphics g) {
		final Ellipse ellipse = new Ellipse(50, 50, 350, 200);
		final Rectangle rectangle = new Rectangle(100, 160, 125, 220);
		final Polygon triangle = new Polygon(260, 170, 190, 300, 330, 300);
		final Color red = new Color(255, 0, 0, 255);
		final Color darkRed = new Color(128, 0, 0, 255);
		final Color blue = new Color(0, 0, 255, 128);
		final Color green = new Color(0, 255, 0, 128);
		final Color darkGreen = new Color(0, 128, 0, 255);

		g.pushState();

		g.drawProperties().setLineWidth(4).setAntialiasing(true);
		g.fillProperties().setAntialiasing(true);
		g.pushState();

		g.fillProperties().setColor(red);
		g.drawProperties().setDashArray(25, 10).setColor(darkRed);
		g.fill(ellipse);
		g.draw(ellipse.getOutline());

		g.popState();
		g.pushState();

		g.fillProperties().setColor(blue);
		g.drawProperties().setLineJoin(LineJoin.ROUND)
				.setLineCap(LineCap.ROUND);
		g.fill(rectangle);
		g.draw(rectangle.getOutline());

		g.popState();

		g.fillProperties().setColor(green);
		g.drawProperties().setColor(darkGreen).setLineJoin(LineJoin.MITER);
		g.fill(triangle);
		g.draw(triangle.getOutline());

		// g.popState();
		// g.drawProperties().setColor(new Color(0, 128, 255, 255))
		// .setDashArray(30, 10, 5, 10).setDashBegin(15)
		// .setLineCap(LineCap.ROUND).setLineWidth(4);
		// g.draw(new Line(50, 20, 350, 20));

		// g.drawProperties().setColor(new Color()).setDashArray(30, 10, 5, 10)
		// .setDashBegin(15);
		// g.draw(new Line(325, 375, 500, 270));
		//
		// g.popState();
		//
		// String text = "The quick brown fox jumps over the lazy dog.";
		//
		// g.writeProperties().setFont(g.writeProperties().getFont().setSize(20));
		// Dimension textDimension = g.getTextDimension(text);
		// g.draw(new Rectangle(new Point(), textDimension).getOutline());
		// g.write(text);
		//
		// g.canvasProperties().setAffineTransform(
		// g.canvasProperties().getAffineTransform()
		// .translate(0, textDimension.height));
		//
		// g.writeProperties().setFont(g.writeProperties().getFont().setSize(10));
		// textDimension = g.getTextDimension(text);
		// g.draw(new Rectangle(new Point(), textDimension).getOutline());
		// g.write(text);
	}

}