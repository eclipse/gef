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
import org.eclipse.gef4.swtfx.CanvasNode;
import org.eclipse.gef4.swtfx.ControlNode;
import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.event.ActionEvent;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.gef4.swtfx.layout.Pane;
import org.eclipse.swt.SWT;
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

		final Pane root = new Pane();
		new Scene(shell, root);

		AbstractFigure rect = new ShapeFigure(new Rectangle(0, 0, 100, 100));
		rect.setFill(new RgbaColor(0, 128, 128));
		rect.relocate(50, 50);

		AbstractFigure ellipse = new ShapeFigure(new Ellipse(0, 0, 100, 100));
		ellipse.setFill(new RgbaColor(128, 0, 128));
		ellipse.relocate(100, 100);

		CanvasNode canvas = new CanvasNode(100, 100);
		{
			GraphicsContext g = canvas.getGraphicsContext();
			g.setFill(new RgbaColor(255, 0, 0));
			g.fillText("canvas output", 0, 0);
			g.cleanUp();
		}

		ControlNode<Button> button = new ControlNode<Button>(new Button(
				root.getScene(), SWT.PUSH));
		button.getControl().setText("push me");
		button.relocate(300, 100);
		button.setPrefWidth(100);
		button.setPrefHeight(50);
		button.addEventHandler(ActionEvent.SELECTION,
				new IEventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						root.getScene().refreshVisuals();
					}
				});

		root.addChildNodes(rect, ellipse, canvas, button);

		shell.pack();
		shell.open();
		shell.redraw();

		root.layout();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
