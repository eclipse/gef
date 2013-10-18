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
package org.eclipse.gef4.swtfx.examples.nestedgraph;

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.swtfx.SwtControlAdapterNode;
import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.event.ActionEvent;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.MouseEvent;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.gef4.swtfx.layout.HBox;
import org.eclipse.gef4.swtfx.layout.Pane;
import org.eclipse.gef4.swtfx.layout.VBox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

public class BallsNode extends LabPane {

	private VBox options;
	private Pane container;

	// TODO: we need another way to get the scene
	public BallsNode(Scene scene) {
		super("Balls");
		setScene(scene);

		container = new Pane();
		options = new VBox();

		HBox hbox = new HBox();
		hbox.add(container, false);
		hbox.add(options, false);

		addContentNodes(hbox);
		addOptions();
	}

	private void addOptions() {
		SwtControlAdapterNode<Button> addBall = new SwtControlAdapterNode<Button>(new Button(
				getScene(), SWT.PUSH));
		addBall.getControl().setText("add ball");
		addBall.addEventHandler(ActionEvent.ACTION,
				new IEventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						double x = Math.random() * container.getWidth();
						double y = Math.random() * container.getHeight();
						double w = 25 + Math.random() * 100;
						double h = 25 + Math.random() * 100;
						container.addChildNodes(genBall(x, y, w, h));
					}
				});

		options.addChildNodes(addBall);
	}

	private ShapeFigure genBall(double x, double y, double w, double h) {
		final ShapeFigure ball = new ShapeFigure(new Ellipse(x, y, w, h));

		// ball.setFill(new RadialGradient(ellipse, ellipse.getCenter(),
		// CycleMethod.NO_CYCLE).addStop(0, rndColor()).addStop(1,
		// rndColor()));
		ball.setFill(rndColor());

		ball.addEventHandler(MouseEvent.MOUSE_PRESSED,
				new IEventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						container.getChildNodes().remove(ball);
					}
				});
		return ball;
	}

	private RgbaColor rndColor() {
		return new RgbaColor((int) (Math.random() * 255),
				(int) (Math.random() * 255), (int) (Math.random() * 255));
	}

}
