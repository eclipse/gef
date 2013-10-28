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

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.INode;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.TextFigure;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.MouseEvent;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.gef4.swtfx.layout.AnchorPane;
import org.eclipse.gef4.swtfx.layout.AnchorPaneConstraints;
import org.eclipse.gef4.swtfx.layout.Pane;
import org.eclipse.gef4.swtfx.layout.VBox;

public class LabPane extends VBox {

	private Pane contentPane;
	private AnchorPaneConstraints contentConstraints;
	private TextFigure titleFigure;
	private boolean dragging;

	public LabPane(final String title) {
		super();

		titleFigure = new TextFigure(" " + title);
		contentPane = new Pane();
		AnchorPane contentAnchor = new AnchorPane();
		contentAnchor.add(contentPane, new AnchorPaneConstraints(10d, 10d, 10d,
				10d));

		addChildNodes(createBackground(), titleFigure, contentAnchor);

		dragging = false;
		final double[] coords = new double[2];

		final IEventHandler<MouseEvent> handler = new IEventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (dragging) {
					dragging = event.getEventType().isa(MouseEvent.MOUSE_MOVED);
					double dx = event.getDisplayX() - coords[0];
					double dy = event.getDisplayY() - coords[1];
					Point newPos = new Point();
					localToParent(new Point(), newPos);
					relocate(newPos.x + dx, newPos.y + dy);
				} else {
					dragging = event.getEventType().isa(
							MouseEvent.MOUSE_PRESSED);
				}
				coords[0] = event.getDisplayX();
				coords[1] = event.getDisplayY();
			}
		};
		addEventFilter(MouseEvent.MOUSE_PRESSED, handler);
		addEventFilter(MouseEvent.MOUSE_MOVED, handler);
		addEventFilter(MouseEvent.MOUSE_RELEASED, handler);
	}

	public void addContentNodes(INode... nodes) {
		contentPane.addChildNodes(nodes);
	}

	/**
	 * @return
	 */
	private Pane createBackground() {
		final Pane bgPane = new Pane() {
			@Override
			public boolean isManaged() {
				return false;
			}
		};
		bgPane.addChildNodes(new ShapeFigure<Rectangle>(new Rectangle()) {
			@Override
			public void doPaint(GraphicsContext g) {
				g.setFill(new RgbaColor(128, 128, 128, 128));
				g.beginPath();
				g.rect(0, 0, LabPane.this.getWidth(), LabPane.this.getHeight());
				g.fill();
				g.stroke();
			}
		});
		return bgPane;
	}

	public Pane getContentGroup() {
		return contentPane;
	}

	public TextFigure getTitleFigure() {
		return titleFigure;
	}

	protected boolean isDragging() {
		return dragging;
	}

	protected void setDragging(boolean dragging) {
		this.dragging = dragging;
	}

}
