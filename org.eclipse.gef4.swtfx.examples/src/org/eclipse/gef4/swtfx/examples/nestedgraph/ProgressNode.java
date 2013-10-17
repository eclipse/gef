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

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.MouseEvent;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.gef4.swtfx.layout.AnchorPane;
import org.eclipse.gef4.swtfx.layout.HBox;
import org.eclipse.gef4.swtfx.layout.Pane;

public class ProgressNode extends AnchorPane {

	static class HSlider extends Pane {

		static class Sliced extends HSlider {
			public int slices;

			public Sliced(int slices, double startValue, RgbaColor left,
					RgbaColor right) {
				super(startValue, left, right);
				this.slices = slices;
			}

			@Override
			protected void display(GraphicsContext g) {
				int lSlices = (int) (slices * value);
				double sliceWidth = getWidth() / slices;

				g.setFill(lColor);
				for (int i = 0; i < lSlices; i++) {
					g.fillRect(i * sliceWidth, 0, sliceWidth, getHeight());
					g.strokeLine(i * sliceWidth, 0, i * sliceWidth, getHeight());
				}

				g.setFill(rColor);
				for (int i = lSlices; i < slices; i++) {
					g.fillRect(i * sliceWidth, 0, sliceWidth, getHeight());
					g.strokeLine(i * sliceWidth, 0, i * sliceWidth, getHeight());
				}
				g.strokeRect(0, 0, getWidth(), getHeight());
			}
		}

		public double value;
		public RgbaColor lColor, rColor;

		public HSlider(double startValue, RgbaColor left, RgbaColor right) {
			this.value = startValue;
			this.lColor = left;
			this.rColor = right;

			addChildNodes(new ShapeFigure<Rectangle>(new Rectangle()) {
				@Override
				public void doPaint(GraphicsContext g) {
					display(g);
				}
			});

			IEventHandler<MouseEvent> mouseHandler = new IEventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (isPressed()) {
						value = getWidth() == 0 ? 1 : event.getTargetX()
								/ getWidth();
						value = value < 0 ? 0 : value > 1 ? 1 : value;
					}
				}
			};
			addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
			addEventHandler(MouseEvent.MOUSE_MOVED, mouseHandler);
			addEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);
		}

		protected void display(GraphicsContext g) {
			g.setFill(lColor);
			g.fillRect(0, 0, getWidth() * value, getHeight());
			g.setFill(rColor);
			g.fillRect(getWidth() * value, 0, getWidth() * (1 - value),
					getHeight());
			g.strokeRect(0, 0, getWidth(), getHeight());
			g.strokeLine(getWidth() * value, 0, getWidth() * value, getHeight());
		}

	}

	public ProgressNode() {
		HSlider hSlider = new HSlider(0.5, new RgbaColor(196, 255, 148),
				new RgbaColor(255, 124, 60));
		hSlider.setPrefWidth(200);
		hSlider.setPrefHeight(50);

		HSlider.Sliced sliced = new HSlider.Sliced(10, 0.5, new RgbaColor(255,
				124, 60), new RgbaColor(196, 255, 148));
		sliced.setPrefWidth(200);
		sliced.setPrefHeight(50);

		ShapeFigure<Rectangle> separator = new ShapeFigure<Rectangle>(
				new Rectangle(0, 0, 5, 1));
		separator.setFill(new RgbaColor(0, 0, 0, 0));
		separator.setStroke(new RgbaColor(0, 0, 0, 0));

		HBox hBox = new HBox();
		hBox.add(hSlider, false);
		hBox.add(separator, false);
		hBox.add(sliced, false);

		addChildNodes(hBox);
	}

}
