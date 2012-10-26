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
package org.eclipse.gef4.graphics.examples.doc;

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.render.IDrawProperties.LineCap;
import org.eclipse.gef4.graphics.render.IDrawProperties.LineJoin;
import org.eclipse.gef4.graphics.render.IGraphics;
import org.eclipse.gef4.graphics.render.swt.SWTGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Example001 implements PaintListener {

	public static void main(String[] args) {
		new Example001("Simple Graphics");
	}

	public Example001(String title) {
		Display display = new Display();

		Shell shell = new Shell(display, SWT.SHELL_TRIM | SWT.DOUBLE_BUFFERED);
		shell.setText(title);
		shell.setBounds(0, 0, 640, 480);
		shell.open();

		shell.addPaintListener(this);
		shell.redraw(); // triggers a PaintEvent platform independently

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void paintControl(PaintEvent e) {
		SWTGraphics g = new SWTGraphics(e.gc);
		renderScene(g);
		g.cleanUp();
	}

	public void renderScene(IGraphics g) {
		// The rendering code goes here. It is independent of the actual
		// IGraphics implementation. Therefore, it is independent of the
		// underlying drawing toolkit, too.

		final Ellipse ellipse = new Ellipse(50, 50, 350, 200);
		final Rectangle rectangle = new Rectangle(100, 160, 125, 220);
		final Polygon triangle = new Polygon(260, 170, 190, 300, 330, 300);

		g.drawProperties().setLineWidth(4).setAntialiasing(true);
		g.fillProperties().setAntialiasing(true);

		g.pushState(); // saves the current set of properties on the stack

		g.fillProperties().setColor(new Color(255, 0, 0));
		g.drawProperties().setDashArray(25, 10).setColor(new Color(128, 0, 0));

		g.fill(ellipse);
		g.draw(ellipse.getOutline());

		g.restoreState(); // restores the previously saved properties

		g.fillProperties().setColor(new Color(0, 0, 255));
		g.drawProperties().setLineJoin(LineJoin.ROUND)
				.setLineCap(LineCap.ROUND);

		g.fill(rectangle);
		g.draw(rectangle.getOutline());

		g.popState(); // removes the previously saved properties from the stack
						// and enables the prior set of properties

		g.fillProperties().setColor(new Color(0, 255, 0));
		g.drawProperties().setColor(new Color(0, 128, 0))
				.setLineJoin(LineJoin.MITER);

		g.fill(triangle);
		g.draw(triangle.getOutline());
	}

}
