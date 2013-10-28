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
package org.eclipse.gef4.swtfx.examples.snippets;

import org.eclipse.gef4.swtfx.Group;
import org.eclipse.gef4.swtfx.ImageFigure;
import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.animation.AbstractTransition;
import org.eclipse.gef4.swtfx.animation.RotateTransition;
import org.eclipse.gef4.swtfx.animation.ScaleTransition;
import org.eclipse.gef4.swtfx.animation.SequentialTransition;
import org.eclipse.gef4.swtfx.controls.SwtButton;
import org.eclipse.gef4.swtfx.controls.SwtButton.Type;
import org.eclipse.gef4.swtfx.event.ActionEvent;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.examples.Application;
import org.eclipse.gef4.swtfx.layout.AnchorPane;
import org.eclipse.gef4.swtfx.layout.AnchorPaneConstraints;
import org.eclipse.gef4.swtfx.layout.BorderPane;
import org.eclipse.gef4.swtfx.layout.BorderPaneConstraints;
import org.eclipse.gef4.swtfx.layout.HBox;
import org.eclipse.gef4.swtfx.layout.Pos;
import org.eclipse.gef4.swtfx.layout.VBox;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

public class AnimationSample extends Application {

	public static void main(String[] args) {
		new AnimationSample();
	}

	private Group animGroup;

	private void addTransitionOptions(VBox vbox, String label,
			final AbstractTransition transition) {
		SwtButton button = new SwtButton(label, Type.TOGGLE);
		AnchorPane inner = new AnchorPane();
		inner.add(button, new AnchorPaneConstraints(10d, 10d, 10d, 10d));
		vbox.add(inner, true);

		button.addEventHandler(ActionEvent.ACTION,
				new IEventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						if (((Button) event.getSource()).getSelection()) {
							if (!transition.isPlaying()) {
								transition.play();
							} else {
								transition.resume();
							}
						} else {
							transition.pause();
						}
					}
				});
	}

	private ImageFigure createImageFigure(Shell shell) {
		final Image imgGefLogo = new Image(shell.getDisplay(),
				"src/org/eclipse/gef4/swtfx/examples/snippets/gef-logo.jpg");
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				imgGefLogo.dispose();
			}
		});

		final ImageFigure imageFigure = new ImageFigure(imgGefLogo);
		return imageFigure;
	}

	/**
	 * Compose a sequence of several scale transitions, so that we start out at
	 * a scale of 1, 1.
	 */
	private AbstractTransition createScaleTransition() {
		ScaleTransition scaleUp1 = new ScaleTransition(750, 1, false,
				animGroup, 1, 1, 2, 2);
		ScaleTransition scaleDown = new ScaleTransition(1500, 1, false,
				animGroup, 2, 2, 0.2, 0.2);
		ScaleTransition scaleUp2 = new ScaleTransition(750, 1, false,
				animGroup, 0.2, 0.2, 1, 1);

		SequentialTransition scaleTransition = new SequentialTransition(-1,
				false, scaleUp1, scaleDown, scaleUp2);
		// scaleTransition.setInterpolator(IInterpolator.SMOOTH_STEP);

		return scaleTransition;
	}

	private AbstractTransition createSpinTransition() {
		return new RotateTransition(3000, -1, false, animGroup, 0, 360);
	}

	@Override
	public Scene start(Shell shell) {
		// create layout
		HBox root = new HBox();
		VBox vbox = new VBox();
		BorderPane borderPane = new BorderPane();

		root.addChildNodes(vbox, borderPane);
		root.setGrower(borderPane);
		root.setFill(borderPane, true);

		// create animation group
		animGroup = new Group(createImageFigure(shell));
		animGroup.setPivot(animGroup.getLayoutBounds().getCenter());
		borderPane.setCenter(animGroup, new BorderPaneConstraints(Pos.CENTER));

		addTransitionOptions(vbox, "Spin", createSpinTransition());
		addTransitionOptions(vbox, "Scale", createScaleTransition());

		// set client area
		root.setPrefWidth(800);
		root.setPrefHeight(600);

		return new Scene(shell, root);
	}

}
