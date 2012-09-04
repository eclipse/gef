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
import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.swt.DisplayGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SWTGraphicsExample implements PaintListener {

	public static void main(String[] args) {
		new SWTGraphicsExample("GEF 4 Graphics - SWT");
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
		g.fillProperties().setColor(new Color(255, 0, 0, 255));
		g.fill(new Ellipse(50, 50, 350, 200));
	}

}