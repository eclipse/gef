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

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public abstract class AbstractFXColorPicker extends Group {

	public static final Color DEFAULT_COLOR = Color.WHITE;

	private SimpleObjectProperty<Color> colorProperty = new SimpleObjectProperty<Color>(
			DEFAULT_COLOR);

	private SimpleObjectProperty<Color> borderColorProperty = new SimpleObjectProperty<Color>(
			Color.GREY);
	private SimpleObjectProperty<Color> backgroundColorProperty = new SimpleObjectProperty<Color>(
			Color.LIGHTGREY);

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

	private static String toRgbString(Color color) {
		return "rgb(" + (int) (255 * color.getRed()) + ","
				+ (int) (255 * color.getGreen()) + ","
				+ (int) (255 * color.getBlue()) + ")";
	}

	public AbstractFXColorPicker() {
		// container
		HBox hbox = new HBox();
		getChildren().add(hbox);

		// color wheel
		WritableImage colorWheelImage = new WritableImage(64, 64);
		ColorWheel.render(colorWheelImage, 0, 0, 64);
		ImageView colorWheel = new ImageView(colorWheelImage);
		colorWheel.setFitWidth(16);
		colorWheel.setFitHeight(16);

		BorderPane buttonPane = new BorderPane();
		Insets insets = new Insets(2.0);
		buttonPane.setPadding(insets);
		buttonPane.setCenter(colorWheel);

		Line l = new Line();
		l.setStartX(0);
		l.setStartY(0);
		l.setEndX(0);
		l.endYProperty().bind(buttonPane.heightProperty());

		// color rect
		Rectangle colorRect = new Rectangle(50, 20);
		// bind to ColorWheel instead of buttonPane to prevent layout problems.
		colorRect.widthProperty().bind(colorWheel.fitWidthProperty().add(insets.getLeft()).add(insets.getRight()).multiply(2.5).subtract(l.strokeWidthProperty()));
		colorRect.heightProperty().bind(buttonPane.heightProperty());
		colorRect.fillProperty().bind(colorProperty);

		// bindings related to styling of control
		l.strokeProperty().bind(borderColorProperty);
		hbox.styleProperty().bind(new StringBinding() {
			{
				bind(borderColorProperty, backgroundColorProperty);
			}
			
			@Override
			protected String computeValue() {
				return "-fx-border-color: "
						+ toRgbString(borderColorProperty.get())
						+ "; -fx-background-color: "
						+ toRgbString(backgroundColorProperty.get());
			}
		});
		

		hbox.getChildren().addAll(colorRect, l, buttonPane);

		// interaction
		buttonPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
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

	public ObjectProperty<Color> colorProperty() {
		return colorProperty;
	}

	public Color getColor() {
		return colorProperty.get();
	}

	public void setColor(Color color) {
		colorProperty.set(color);
	}

	public ObjectProperty<Color> borderColorProperty() {
		return borderColorProperty;
	}

	public ObjectProperty<Color> backgroundColorProperty() {
		return backgroundColorProperty;
	}

}
