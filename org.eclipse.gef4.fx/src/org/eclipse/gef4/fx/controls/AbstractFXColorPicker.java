/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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
package org.eclipse.gef4.fx.controls;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Separator;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class AbstractFXColorPicker extends Group {

	public static final Color DEFAULT_COLOR = Color.WHITE;

	private SimpleObjectProperty<Color> colorProperty = new SimpleObjectProperty<Color>(
			DEFAULT_COLOR);

	public static class ColorWheel extends Group {
		private int size;
		private Canvas canvas;
		private Color backgroundColor = Color.TRANSPARENT;

		public ColorWheel(int size) {
			super();
			canvas = new Canvas();
			getChildren().add(canvas);
			setSize(size);
		}

		public void setSize(int size) {
			this.size = size;
			canvas.setWidth(size);
			canvas.setHeight(size);
			render();
		}

		public int getSize() {
			return size;
		}

		private void render() {
			GraphicsContext g2d = canvas.getGraphicsContext2D();
			PixelWriter px = g2d.getPixelWriter();
			
			double radius = size / 2;
			Point2D mid = new Point2D(radius, radius);
			
			for (int y = 0; y < size; y++) {
				for (int x = 0; x < size; x++) {
					double d = mid.distance(x, y);
					if (d <= radius) {
						// compute hue angle
						double angleRad = d == 0 ? 0 : Math.atan2(
								y - mid.getY(), x - mid.getX());

						// compute saturation depending on distance to middle
						double sat = d / radius;

						// soften saturation
						sat *= sat;

						Color color = Color.hsb(angleRad * 180 / Math.PI, sat,
								1);
						px.setColor(x, y, color);
					} else {
						px.setColor(x, y, backgroundColor);
					}
				}
			}
		}
	}

	public AbstractFXColorPicker() {
		HBox hbox = new HBox();
		getChildren().add(hbox);
		hbox.setStyle("-fx-border-color: black");

		ColorWheel colorWheel = new ColorWheel(64);
		colorWheel.setScaleX(0.25); // 64 * 0.25 = 16
		colorWheel.setScaleY(0.25);
		Rectangle colorRect = new Rectangle(40, 16);

		hbox.getChildren().addAll(colorRect,
				new Separator(Orientation.VERTICAL), new Group(colorWheel));

		colorRect.fillProperty().bind(colorProperty);

		colorWheel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Color colorOrNull = pickColor();
				if (colorOrNull != null) {
					setColor(colorOrNull);
				}
			}
		});
	}

	public abstract Color pickColor();

	public SimpleObjectProperty<Color> colorProperty() {
		return colorProperty;
	}

	public Color getColor() {
		return colorProperty.get();
	}

	public void setColor(Color color) {
		colorProperty.set(color);
	}

}
