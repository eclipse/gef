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

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.Group;
import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.animation.IInterpolator;
import org.eclipse.gef4.swtfx.animation.ParallelTransition;
import org.eclipse.gef4.swtfx.animation.PathTransition;
import org.eclipse.gef4.swtfx.animation.RotateTransition;
import org.eclipse.swt.widgets.Shell;

public class ParallelTransitionExample extends Application {
	public static void main(String[] args) {
		new ParallelTransitionExample();
	}

	@Override
	public Scene start(Shell shell) {
		Group root = new Group();
		root.setPrefWidth(400);
		root.setPrefHeight(300);

		ShapeFigure box = new ShapeFigure(new Rectangle(0, 0, 100, 50));
		box.setPivot(new Point(50, 25));

		root.addChildNodes(box);
		Scene scene = new Scene(shell, root);

		RotateTransition rotateTransition = new RotateTransition(2500, box, 0,
				720);

		PathTransition pathTransition = new PathTransition(2500, new Rectangle(
				0, 0, 300, 250).toPath(), box);

		ParallelTransition parallelTransition = new ParallelTransition(
				pathTransition, rotateTransition);

		parallelTransition.setInterpolator(IInterpolator.SMOOTH_STEP);
		parallelTransition.play();

		return scene;
	}
}