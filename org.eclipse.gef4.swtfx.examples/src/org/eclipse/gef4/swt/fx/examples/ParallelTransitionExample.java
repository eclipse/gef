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

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.SwtControlAdapterNode;
import org.eclipse.gef4.swtfx.Group;
import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.animation.IInterpolator;
import org.eclipse.gef4.swtfx.animation.ParallelTransition;
import org.eclipse.gef4.swtfx.animation.PathTransition;
import org.eclipse.gef4.swtfx.animation.RotateTransition;
import org.eclipse.gef4.swtfx.event.ActionEvent;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

public class ParallelTransitionExample extends Application {
	public static void main(String[] args) {
		new ParallelTransitionExample();
	}

	/**
	 * @param box
	 * @return
	 */
	private ParallelTransition createTransition(ShapeFigure box) {
		RotateTransition rotateTransition = new RotateTransition(
				box.getScene(), 2500, 1, false, box, 0, 720);

		PathTransition pathTransition = new PathTransition(box.getScene(),
				2500, 1, false, box, new Rectangle(0, 0, 300, 250).toPath());

		ParallelTransition parallelTransition = new ParallelTransition(
				box.getScene(), 1, false, pathTransition, rotateTransition);

		parallelTransition.setInterpolator(IInterpolator.SMOOTH_STEP);
		return parallelTransition;
	}

	@Override
	public Scene start(Shell shell) {
		Group root = new Group();
		root.setPrefWidth(400);
		root.setPrefHeight(300);

		ShapeFigure<Rectangle> box = new ShapeFigure<Rectangle>(new Rectangle(
				0, 0, 100, 50));
		box.setPivot(box.getLayoutBounds().getCenter());
		box.setFill(new RgbaColor(255, 0, 0));

		root.addChildNodes(box);
		Scene scene = new Scene(shell, root);

		final ParallelTransition parallelTransition = createTransition(box);

		SwtControlAdapterNode<Button> button = new SwtControlAdapterNode<Button>(new Button(
				root.getScene(), SWT.PUSH));
		button.getControl().setText("Play!");
		button.addEventHandler(ActionEvent.ACTION,
				new IEventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						parallelTransition.play();
					}
				});

		root.addChildNodes(button);
		return scene;
	}
}