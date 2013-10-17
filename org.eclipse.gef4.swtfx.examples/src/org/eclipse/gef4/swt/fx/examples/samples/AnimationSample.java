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
package org.eclipse.gef4.swt.fx.examples.samples;

import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.swt.fx.examples.Application;
import org.eclipse.gef4.swtfx.ControlNode;
import org.eclipse.gef4.swtfx.Group;
import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.TextFigure;
import org.eclipse.gef4.swtfx.event.ActionEvent;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.gef4.swtfx.layout.HBoxSimple;
import org.eclipse.gef4.swtfx.layout.Pane;
import org.eclipse.gef4.swtfx.layout.VBoxSimple;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

public class AnimationSample extends Application {

	public static void main(String[] args) {
		new AnimationSample();
	}

	private Group dragonGroup;

	private void spinOff() {
	}

	private void spinOn() {
	}

	@Override
	public Scene start(Shell shell) {
		HBoxSimple root = new HBoxSimple();
		Scene scene = new Scene(shell, root);

		VBoxSimple vbox = new VBoxSimple();
		Pane pane = new Pane();
		root.addChildNodes(vbox, pane);
		root.setGrower(pane);

		dragonGroup = new Group();
		ShapeFigure<Polygon> dragon = new ShapeFigure<Polygon>(new Polygon(70,
				0, 90, 50, 140, 70, 90, 90, 70, 140, 50, 90, 0, 70, 50, 50));
		dragon.setFill(new RgbaColor(1, 0.5, 0.1));
		TextFigure label = new TextFigure("Dragon!");
		label.relocate(90, 20);
		dragonGroup.addChildNodes(dragon, label);
		dragonGroup.relocate(100, 100);

		pane.addChildNodes(dragonGroup);

		// add buttons
		vbox.add(toggle(scene, "Spin", new IEventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Object source = event.getSource();
				if (source instanceof Button) {
					Button button = (Button) source;
					if (button.getSelection()) {
						spinOn();
					} else {
						spinOff();
					}
				}
			}
		}), true);

		root.setPrefWidth(400);
		root.setPrefHeight(300);

		return scene;
	}

	private ControlNode<Button> toggle(Scene scene, String label,
			IEventHandler<ActionEvent> actionHandler) {
		ControlNode<Button> cnode = new ControlNode<Button>(new Button(scene,
				SWT.TOGGLE));
		cnode.getControl().setText(label);
		cnode.addEventHandler(ActionEvent.ACTION, actionHandler);
		return cnode;
	}

}
