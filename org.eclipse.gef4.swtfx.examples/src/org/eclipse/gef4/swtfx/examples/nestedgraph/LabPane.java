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

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.INode;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.MouseEvent;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.gef4.swtfx.layout.AnchorPane;
import org.eclipse.gef4.swtfx.layout.AnchorPaneConstraints;
import org.eclipse.gef4.swtfx.layout.Pane;

public class LabPane extends AnchorPane {

	private Pane contentGroup;
	private AnchorPaneConstraints contentConstraints;

	public LabPane(final String title) {
		super();

		final ShapeFigure titleText = new ShapeFigure(new Rectangle()) {
			@Override
			public void doPaint(GraphicsContext g) {
				Dimension textExtent = g.textExtent(title);
				((Rectangle) getShape()).setSize(textExtent);
				contentConstraints.setTop(10 + textExtent.height);
				g.strokeText(title, 0, 0);
				g.fillText(title, 0, 0);
			}
		};

		final Pane bgPane = new Pane();
		bgPane.addChildNodes(new ShapeFigure(new Rectangle()) {
			@Override
			public void doPaint(GraphicsContext g) {
				g.setFill(getFillPaint());
				g.beginPath();
				g.rect(0, 0, getWidth(), getHeight());
				g.fill();
				g.stroke();
			}
		});

		contentGroup = new Pane();

		add(bgPane, new AnchorPaneConstraints(0d, 0d, 0d, 0d));
		add(titleText, new AnchorPaneConstraints(5d, null, null, 5d));
		contentConstraints = new AnchorPaneConstraints(10d, 10d, 10d, 10d);
		add(contentGroup, contentConstraints);

		final boolean[] rel = new boolean[1];
		rel[0] = false;
		final double[] coords = new double[2];

		final IEventHandler<MouseEvent> handler = new IEventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// only handle events on the background pane or the title text
				if (event.getTarget() != bgPane
						&& event.getTarget() != titleText) {
					return;
				}

				if (rel[0]) {
					rel[0] = event.getEventType().isa(MouseEvent.MOUSE_MOVED);
					double dx = event.getDisplayX() - coords[0];
					double dy = event.getDisplayY() - coords[1];
					Point newPos = new Point();
					localToParent(new Point(), newPos);
					relocate(newPos.x + dx, newPos.y + dy);
				} else {
					rel[0] = event.getEventType().isa(MouseEvent.MOUSE_PRESSED);
				}
				coords[0] = event.getDisplayX();
				coords[1] = event.getDisplayY();
			}
		};
		addEventHandler(MouseEvent.MOUSE_PRESSED, handler);
		addEventHandler(MouseEvent.MOUSE_MOVED, handler);
		addEventHandler(MouseEvent.MOUSE_RELEASED, handler);
	}

	public void addContentNodes(INode... nodes) {
		contentGroup.addChildNodes(nodes);
	}

	private Object getFillPaint() {
		return new RgbaColor(128, 128, 128, 128);
		// return new LinearGradient(new Point(), new Point(0, getHeight()),
		// CycleMethod.NO_CYCLE).addStop(0, new RgbaColor(255, 255, 255))
		// .addStop(1, new RgbaColor(128, 128, 255));
	}

}
