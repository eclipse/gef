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
package org.eclipse.gef4.graphics.examples.doc;

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.LineCap;
import org.eclipse.gef4.graphics.LineJoin;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.swt.SwtGraphics;
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
		shell.redraw(); // platform independently triggers a PaintEvent

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	@Override
	public void paintControl(PaintEvent e) {
		SwtGraphics g = new SwtGraphics(e.gc);
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

		g.setFill(new Color(255, 0, 0)).setDraw(new Color(128, 0, 0))
				.setDashArray(25, 10).setLineWidth(3);

		g.fill(ellipse).draw(ellipse.getOutline());

		g.setFill(new Color(0, 0, 255)).setDraw(new Color())
				.setLineJoin(LineJoin.ROUND).setLineCap(LineCap.ROUND);

		g.fill(rectangle).draw(rectangle.getOutline());

		g.setFill(new Color(0, 255, 0)).setDraw(new Color(0, 128, 0))
				.setLineJoin(LineJoin.MITER);

		g.fill(triangle).draw(triangle.getOutline());
	}

}
