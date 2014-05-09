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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class AbstractFXColorPicker extends Group {

	public static final Color DEFAULT_COLOR = Color.WHITE;

	private SimpleObjectProperty<Color> colorProperty = new SimpleObjectProperty<Color>(
			DEFAULT_COLOR);

	public static class ColorWheel {
		public static void render(WritableImage image, int offsetX,
				int offsetY, int size) {
			PixelWriter px = image.getPixelWriter();
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
						// ([0;1])
						double sat = d / radius;
						// multiply saturation by itself to produce a bigger
						// white area in the middle
						sat *= sat;

						Color color = Color.hsb(angleRad * 180 / Math.PI, sat,
								1);
						px.setColor(offsetX + x, offsetY + y, color);
					} else {
						px.setColor(offsetX + x, offsetY + y, Color.TRANSPARENT);
					}
				}
			}
		}
	}

	public AbstractFXColorPicker() {
		// container
		HBox hbox = new HBox();
		getChildren().add(hbox);
		hbox.setStyle("-fx-border-color: black; -fx-background-color: lightgrey");

		// color wheel
		WritableImage colorWheelImage = new WritableImage(64, 64);
		ColorWheel.render(colorWheelImage, 0, 0, 64);
		ImageView colorWheel = new ImageView(colorWheelImage);
		colorWheel.setFitWidth(16);
		colorWheel.setFitHeight(16);

		// color rect
		Rectangle colorRect = new Rectangle(40, 16);
		colorRect.fillProperty().bind(colorProperty);

		hbox.getChildren().addAll(colorRect,
				new Separator(Orientation.VERTICAL), colorWheel);

		// interaction
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
