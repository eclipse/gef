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
import org.eclipse.gef4.swtfx.Group;
import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.TextFigure;
import org.eclipse.gef4.swtfx.animation.RotateTransition;
import org.eclipse.gef4.swtfx.animation.ScaleTransition;
import org.eclipse.gef4.swtfx.animation.SequentialTransition;
import org.eclipse.gef4.swtfx.controls.SwtButton;
import org.eclipse.gef4.swtfx.controls.SwtButton.Type;
import org.eclipse.gef4.swtfx.event.ActionEvent;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.gef4.swtfx.layout.BorderPane;
import org.eclipse.gef4.swtfx.layout.BorderPaneConstraints;
import org.eclipse.gef4.swtfx.layout.HBox;
import org.eclipse.gef4.swtfx.layout.Pos;
import org.eclipse.gef4.swtfx.layout.VBox;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

public class AnimationSample extends Application {

	public static void main(String[] args) {
		new AnimationSample();
	}

	private Group dragonGroup;
	private RotateTransition spinTransition;
	private SequentialTransition scaleTransition;

	protected void scaleOff() {
		scaleTransition.pause();
	}

	protected void scaleOn() {
		scaleTransition.resume();
	}

	private void spinOff() {
		spinTransition.pause();
	}

	private void spinOn() {
		spinTransition.resume();
	}

	@Override
	public Scene start(Shell shell) {
		HBox root = new HBox();

		VBox vbox = new VBox();
		BorderPane borderPane = new BorderPane();

		root.addChildNodes(vbox, borderPane);
		root.setGrower(borderPane);
		root.setFill(borderPane, true);

		dragonGroup = new Group();

		ShapeFigure<Polygon> dragon = new ShapeFigure<Polygon>(new Polygon(70,
				0, 90, 50, 140, 70, 90, 90, 70, 140, 50, 90, 0, 70, 50, 50));
		dragon.setFill(new RgbaColor(1, 0.5, 0.1));

		TextFigure label = new TextFigure("Dragon!");
		label.relocate(90, 20);

		dragonGroup.addChildNodes(dragon, label);

		// not necessary in JavaFX, Node#computePivot[X/Y] returns center there
		dragonGroup.setPivot(dragon.getShape().getCentroid());

		// put dragon at center
		borderPane
				.setCenter(dragonGroup, new BorderPaneConstraints(Pos.CENTER));

		// TODO: change transitions so that we do not need to create scene here
		Scene scene = new Scene(shell, root);

		// create spin transition
		spinTransition = new RotateTransition(scene, 2000, -1, false,
				dragonGroup, 0, 360);

		// start listening for pulses, so that we can easily start/stop at the
		// current state via pause() and resume()
		spinTransition.pause();
		spinTransition.play();

		// create scale transition, it is a sequence of several scale
		// transitions, so that we start out at a scale of 1, 1
		ScaleTransition scaleUp1 = new ScaleTransition(scene, 500, 1, false,
				dragonGroup, 1, 1, 2, 2);
		ScaleTransition scaleDown = new ScaleTransition(scene, 1000, 1, false,
				dragonGroup, 2, 2, 0.2, 0.2);
		ScaleTransition scaleUp2 = new ScaleTransition(scene, 500, 1, false,
				dragonGroup, 0.2, 0.2, 1, 1);
		scaleTransition = new SequentialTransition(scene, -1, false, scaleUp1,
				scaleDown, scaleUp2);

		// start listening for pulses
		scaleTransition.pause();
		scaleTransition.play();

		// add buttons
		SwtButton spinButton = new SwtButton("Spin", Type.TOGGLE);
		vbox.add(spinButton, true);

		spinButton.addEventHandler(ActionEvent.ACTION,
				new IEventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						if (((Button) event.getSource()).getSelection()) {
							spinOn();
						} else {
							spinOff();
						}
					}
				});

		SwtButton scaleButton = new SwtButton("Scale", Type.TOGGLE);
		vbox.add(scaleButton, true);

		scaleButton.addEventHandler(ActionEvent.ACTION,
				new IEventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						if (((Button) event.getSource()).getSelection()) {
							scaleOn();
						} else {
							scaleOff();
						}
					}
				});

		root.setPrefWidth(400);
		root.setPrefHeight(300);

		return scene;
	}

}
