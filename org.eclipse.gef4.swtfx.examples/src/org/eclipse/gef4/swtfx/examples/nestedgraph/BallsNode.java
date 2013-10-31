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
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.controls.SwtButton;
import org.eclipse.gef4.swtfx.event.ActionEvent;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.MouseEvent;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.gef4.swtfx.layout.HBox;
import org.eclipse.gef4.swtfx.layout.Pane;
import org.eclipse.gef4.swtfx.layout.VBox;

public class BallsNode extends EditLabPane {

	private VBox options;
	private Pane container;

	public BallsNode() {
		super("Balls");

		container = new Pane();
		options = new VBox();

		HBox hbox = new HBox();
		hbox.add(container, false);
		hbox.add(options, false);

		addContentNodes(hbox);
		addOptions();
	}

	private void addOptions() {
		SwtButton btnAddBall = new SwtButton("add ball");
		btnAddBall.addEventHandler(ActionEvent.ACTION,
				new IEventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						double x = Math.max(0, Math.min(Math.random()
								* container.getWidth(),
								container.getWidth() - 10));
						double y = Math.random() * container.getHeight();
						double w = 25 + Math.random() * 100;
						double h = 25 + Math.random() * 100;
						container.addChildren(genBall(x, y, w, h));
					}
				});

		options.addChildren(btnAddBall);
	}

	private ShapeFigure<Ellipse> genBall(double x, double y, double w, double h) {
		final ShapeFigure<Ellipse> ball = new ShapeFigure<Ellipse>(new Ellipse(
				0, 0, w, h));
		ball.relocate(x, y);

		// System.out.println(ball.getLayoutBounds());

		ball.setFill(new RgbaColor((int) (Math.random() * 255), (int) (Math
				.random() * 255), (int) (Math.random() * 255)));

		ball.addEventHandler(MouseEvent.MOUSE_PRESSED,
				new IEventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						container.removeChildren(ball);
					}
				});

		return ball;
	}

}
