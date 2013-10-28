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

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swt.fx.examples.Application;
import org.eclipse.gef4.swtfx.Group;
import org.eclipse.gef4.swtfx.INode;
import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.MouseEvent;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.gef4.swtfx.layout.Pane;
import org.eclipse.swt.widgets.Shell;

public class GroupSample extends Application {

	public static void main(String[] args) {
		new GroupSample();
	}

	private void makeDraggable(final INode n) {
		final boolean[] dragging = new boolean[] { false };
		final double[] coords = new double[2];

		final IEventHandler<MouseEvent> handler = new IEventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (dragging[0]) {
					double dx = event.getDisplayX() - coords[0];
					double dy = event.getDisplayY() - coords[1];

					Point pos = n.localToParent(0, 0);
					n.relocate((int) pos.x + (int) dx, (int) pos.y + (int) dy);

					dragging[0] = event.getEventType().isa(
							MouseEvent.MOUSE_MOVED);
				} else {
					dragging[0] = event.getEventType().isa(
							MouseEvent.MOUSE_PRESSED);
				}

				coords[0] = event.getDisplayX();
				coords[1] = event.getDisplayY();
			}
		};
		n.addEventHandler(MouseEvent.MOUSE_PRESSED, handler);
		n.addEventHandler(MouseEvent.MOUSE_MOVED, handler);
		n.addEventHandler(MouseEvent.MOUSE_RELEASED, handler);
	}

	@Override
	public Scene start(Shell shell) {
		Pane root = new Pane();
		Group parent = new Group();
		root.addChildNodes(parent);

		root.setPrefWidth(640);
		root.setPrefHeight(480);

		ShapeFigure<Rectangle> rect = new ShapeFigure<Rectangle>(new Rectangle(
				0, 0, 200, 100));
		ShapeFigure<Ellipse> oval = new ShapeFigure<Ellipse>(new Ellipse(0, 0,
				100, 200));

		parent.addChildNodes(rect, oval);

		rect.setFill(new RgbaColor(0, 0, 255));
		oval.setFill(new RgbaColor(255, 0, 0));

		for (INode n : new INode[] { rect, oval }) {
			makeDraggable(n);
		}

		final AffineTransform rotationTransform = new AffineTransform();
		final Angle theta = Angle.fromDeg(0);
		parent.getTransforms().add(rotationTransform);

		parent.addEventHandler(MouseEvent.MOUSE_RELEASED,
				new IEventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						if (event.getButton() == 3) {
							theta.setDeg(theta.deg() + 15);
							rotationTransform.setToRotation(theta.rad());
						}
					}
				});

		return new Scene(shell, root);
	}

}
