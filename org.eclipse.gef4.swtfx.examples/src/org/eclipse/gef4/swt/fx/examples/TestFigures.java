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

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.AbstractFigure;
import org.eclipse.gef4.swtfx.CanvasFigure;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.gef4.swtfx.layout.Pane;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
		shell.setText("org.eclipse.gef4.swtfx");
		shell.setLayout(new GridLayout());

		final Pane root = new Pane(shell);

		// ShapeFigure curved = new ShapeFigure(generateCurvedPolygon(new Point(
		// 250, 50), new Point(450, 200), new Point(400, 450), new Point(
		// 250, 350), new Point(100, 450), new Point(50, 200)));
		// colorize(curved, new RgbaColor(255, 255, 0));
		// trafo(curved).translate(50, 200);

		AbstractFigure rect = new ShapeFigure(new Rectangle(0, 0, 100, 100));
		colorize(rect, new RgbaColor(0, 128, 128, 128));
		rect.relocate(50, 50);

		AbstractFigure ellipse = new ShapeFigure(new Ellipse(0, 0, 100, 100));
		colorize(ellipse, new RgbaColor(128, 0, 128, 128));
		ellipse.relocate(100, 100);

		CanvasFigure canvas = new CanvasFigure(100, 100);
		GraphicsContext g = canvas.getGraphicsContext();
		g.setStroke(new RgbaColor(255, 0, 0, 255));
		g.strokeText("canvas output", 0, 0);

		// root.addFigures(curved, rect, ellipse, canvas);
		root.addChildNodes(rect, ellipse, canvas);

		Button button = new Button(root, SWT.PUSH);
		button.setText("push me");
		button.setLocation(300, 100);
		button.setSize(100, 50);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				root.doLayout();
				root.updateSwtBounds();
				root.requestRedraw();
			}
		});

		shell.pack();
		shell.open();
		shell.redraw();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void colorize(AbstractFigure sf, RgbaColor color) {
		sf.getPaintStateByReference().getFillByReference()
				.setColorByReference(color);
	}

}
