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
package org.eclipse.gef4.swtfx.examples;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.CurvedPolygon;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Pie;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;
import org.eclipse.gef4.swtfx.SwtControlAdapterNode;
import org.eclipse.gef4.swtfx.IFigure;
import org.eclipse.gef4.swtfx.IParent;
import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.event.ActionEvent;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.gef4.swtfx.layout.HBox;
import org.eclipse.gef4.swtfx.layout.Pane;
import org.eclipse.gef4.swtfx.layout.VBox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TryOutPane {

	private static SwtControlAdapterNode<Button> button(Pane pane, final String label) {
		Button button = new Button(pane.getScene(), SWT.PUSH);
		button.setText(label);
		SwtControlAdapterNode<Button> controlNode = new SwtControlAdapterNode<Button>(button);
		controlNode.addEventHandler(ActionEvent.ACTION,
				new IEventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						System.out.println(label);
					}
				});
		return controlNode;
	}

	public static void main(String[] args) {
		new TryOutPane();
	}

	private static IFigure shape(final IShape shape, final double red,
			final double green, final double blue) {
		return new ShapeFigure(shape) {
			{
				setFill(new RgbaColor((int) (red * 255), (int) (green * 255),
						(int) (blue * 255)));
			}
		};
	}

	public Display display;
	public Shell shell;
	public Scene scene;

	public TryOutPane() {
		int w = 640;
		int h = 480;

		display = new Display();
		shell = new Shell(display);
		shell.setText("org.eclipse.gef4.swtfx - HBox & VBox");
		shell.setLayout(new GridLayout());

		Pane root = new Pane();
		scene = new Scene(shell, root);
		scene.setLayoutData(new GridData(GridData.FILL_BOTH));
		addUi(root);

		shell.pack();
		// System.out.println("packed");
		shell.open();
		shell.setBounds(0, 0, w, h);
		Rectangle clientArea = shell.getClientArea();
		shell.setBounds(0, 0, 2 * w - clientArea.width, 2 * h
				- clientArea.height);
		shell.redraw();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void addUi(IParent root) {
		VBox vbox = new VBox() {
			/*
			 * XXX: hack to let the VBox fill the entire available space of root
			 */

			@Override
			public double computePrefHeight(double width) {
				return getParentNode().getHeight();
			}

			@Override
			public double computePrefWidth(double height) {
				return getParentNode().getWidth();
			}
		};
		root.addChildNodes(vbox);

		HBox hbox = new HBox();
		vbox.addChildNodes(hbox);
		hbox.addChildNodes(
				button(hbox, "You"),
				shape(new Ellipse(0, 0, 100, 100), 0, 1, 0),
				shape(new Pie(0, 0, 100, 100, Angle.fromDeg(15), Angle
						.fromDeg(215)), 0, 0, 1));

		HBox hbox2 = new HBox();
		vbox.addChildNodes(hbox2);
		hbox2.addChildNodes(
				shape(new Polygon(30, 0, 60, 60, 0, 60), 0.2, 0.7, 0.3),
				button(hbox2, "can"),
				shape(new CurvedPolygon(PolyBezier.interpolateCubic(
						new Point(10, 10), new Point(100, 50),
						new Point(30, 70), new Point(10, 10)).toBezier()), 1,
						0, 1), button(hbox2, "use"));

		HBox hbox3 = new HBox();
		vbox.addChildNodes(hbox3);
		hbox3.addChildNodes(
				shape(new org.eclipse.gef4.geometry.planar.Rectangle(0, 0, 100,
						100), 1, 0, 0),
				button(hbox3, "SWT"),
				shape(new RoundedRectangle(0, 0, 280, 160, 15, 15), 0.3, 1, 0.7),
				button(hbox3, "controls"));

		root.getScene().refreshVisuals();
	}

}
