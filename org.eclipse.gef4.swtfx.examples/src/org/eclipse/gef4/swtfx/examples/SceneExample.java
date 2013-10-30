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
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.swtfx.CanvasFigure;
import org.eclipse.gef4.swtfx.SwtControlAdapterNode;
import org.eclipse.gef4.swtfx.Group;
import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.event.ActionEvent;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.MouseEvent;
import org.eclipse.gef4.swtfx.layout.HBox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SceneExample {

	public static void main(String[] args) {
		new SceneExample();
	}

	public Display display;
	public Shell shell;
	public Scene scene;

	public SceneExample() {
		int w = 400;
		int h = 300;

		display = new Display();
		shell = new Shell(display);
		shell.setText("org.eclipse.gef4.swtfx");
		shell.setLayout(new GridLayout());

		HBox root = new HBox();
		scene = new Scene(shell, root);
		scene.setLayoutData(new GridData(GridData.FILL_BOTH));

		SwtControlAdapterNode<Button> control = new SwtControlAdapterNode<Button>(new Button(scene,
				SWT.PUSH));
		control.getControl().setText("btn");

		root.addChildren(
				new ShapeFigure(new org.eclipse.gef4.geometry.planar.Rectangle(
						0, 0, 100, 100)), control);

		HBox hbox2 = new HBox();
		ShapeFigure shapeFigure = new ShapeFigure(new Ellipse(0, 0, 50, 100));
		hbox2.addChildren(new CanvasFigure(200, 50), shapeFigure);
		hbox2.setRotationAngle(Angle.fromDeg(30));

		Group group = new Group();
		group.addEventHandler(MouseEvent.ANY, new IEventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// System.out.println("mouse event = " + event);
			}
		});

		root.addChildren(hbox2);
		// root.addChildNodes(group);
		// group.addChildNodes(hbox2);

		// root.addChildNodes(hbox2);

		SwtControlAdapterNode<Button> controlNode = new SwtControlAdapterNode<Button>(new Button(
				scene, SWT.TOGGLE));
		controlNode.getControl().setText("blank");
		controlNode.addEventHandler(ActionEvent.ACTION,
				new IEventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						System.out.println("blank click");
					}
				});
		Group group2 = new Group();
		hbox2.addChildren(group2);
		group2.addChildren(controlNode);

		shell.pack();
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
}
