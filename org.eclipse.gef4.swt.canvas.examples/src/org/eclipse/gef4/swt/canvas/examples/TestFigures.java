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
package org.eclipse.gef4.swt.canvas.examples;

import java.util.Arrays;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swt.canvas.CanvasFigure;
import org.eclipse.gef4.swt.canvas.Group;
import org.eclipse.gef4.swt.canvas.ShapeFigure;
import org.eclipse.gef4.swt.canvas.gc.GraphicsContext;
import org.eclipse.gef4.swt.canvas.gc.RgbaColor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TestFigures {

	public static void main(String[] args) {
		new TestFigures();
	}

	private Display display;
	private Shell shell;

	public TestFigures() {
		display = new Display();
		shell = new Shell(display);
		shell.setText("org.eclipse.gef4.swt.canvas");
		shell.setLayout(new GridLayout());

		Group root = new Group(shell);
		root.setLayoutData(new GridData(GridData.FILL_BOTH));

		ShapeFigure rect = new ShapeFigure(new Rectangle(0, 0, 100, 100));
		colorize(rect, new RgbaColor(0, 128, 128, 128));
		trafo(rect).translate(50, 50);

		ShapeFigure ellipse = new ShapeFigure(new Ellipse(0, 0, 100, 100));
		colorize(ellipse, new RgbaColor(128, 0, 128, 128));
		trafo(ellipse).translate(100, 100);

		CanvasFigure canvas = new CanvasFigure(100, 100);
		GraphicsContext g = canvas.getGraphicsContext();
		g.setStroke(new RgbaColor(255, 0, 0, 255));
		g.strokeText("canvas output", 0, 0);

		root.getFigures().addAll(Arrays.asList(rect, ellipse, canvas));

		Button button = new Button(root, SWT.PUSH);
		button.setText("push me");
		button.setLocation(300, 100);
		button.setSize(100, 50);

		shell.pack();
		shell.open();
		shell.redraw();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void colorize(ShapeFigure sf, RgbaColor color) {
		sf.getPaintStateByReference().getFillByReference()
				.setColorByReference(color);
	}

	private AffineTransform trafo(ShapeFigure sf) {
		return sf.getPaintStateByReference().getTransformByReference();
	}

}
