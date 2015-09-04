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
package org.eclipse.gef4.mvc.fx.ui.properties;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

/**
 * The {@link AbstractFXColorPicker} is a control that can be used to select a
 * color. It consists of an area that is filled with the currently selected
 * color, as well as a color wheel next to it.
 * <p>
 * Subclasses have to implement the {@link #pickColor()} method that is used to
 * change the selected color. The method is called when the color wheel is
 * pressed.
 *
 * @author mwienand
 *
 */
public abstract class AbstractFXColorPicker extends Group {

	/**
	 * The {@link ColorWheel} provides a
	 * {@link #render(WritableImage, int, int, int)} method to draw a color
	 * wheel into a {@link WritableImage}.
	 */
	public static class ColorWheel {
		/**
		 * Draws a color wheel into the given {@link WritableImage}, starting at
		 * the given offsets, in the given size (in pixel).
		 *
		 * @param image
		 *            The {@link WritableImage} in which the color wheel is
		 *            drawn.
		 * @param offsetX
		 *            The horizontal offset (in pixel).
		 * @param offsetY
		 *            The vertical offset (in pixel).
		 * @param size
		 *            The size (in pixel).
		 */
		public static void render(WritableImage image, int offsetX, int offsetY,
				int size) {
			PixelWriter px = image.getPixelWriter();
			double radius = size / 2;
			Point2D mid = new Point2D(radius, radius);
			for (int y = 0; y < size; y++) {
				for (int x = 0; x < size; x++) {
					double d = mid.distance(x, y);
					if (d <= radius) {
						// compute hue angle
						double angleRad = d == 0 ? 0
								: Math.atan2(y - mid.getY(), x - mid.getX());
						// compute saturation depending on distance to middle
						// ([0;1])
						double sat = d / radius;
						Color color = Color.hsb(angleRad * 180 / Math.PI, sat,
								1);
						px.setColor(offsetX + x, offsetY + y, color);
					} else {
						px.setColor(offsetX + x, offsetY + y,
								Color.TRANSPARENT);
					}
				}
			}
		}
	}

	/**
	 * The default color for a color picker.
	 */
	public static final Color DEFAULT_COLOR = Color.WHITE;

	private static String toRgbString(Color color) {
		return "rgb(" + (int) (255 * color.getRed()) + ","
				+ (int) (255 * color.getGreen()) + ","
				+ (int) (255 * color.getBlue()) + ")";
	}

	private SimpleObjectProperty<Color> colorProperty = new SimpleObjectProperty<Color>(
			DEFAULT_COLOR);

	private SimpleObjectProperty<Color> borderColorProperty = new SimpleObjectProperty<Color>(
			Color.GREY);

	private SimpleObjectProperty<Color> backgroundColorProperty = new SimpleObjectProperty<Color>(
			Color.LIGHTGREY);

	/**
	 * Constructs a new {@link AbstractFXColorPicker}. Builds the visualization.
	 */
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
		colorRect.widthProperty()
				.bind(colorWheel.fitWidthProperty().add(insets.getLeft())
						.add(insets.getRight()).multiply(2.5)
						.subtract(l.strokeWidthProperty()));
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

	/**
	 * Returns the "background-color" property of this
	 * {@link AbstractFXColorPicker}.
	 *
	 * @return The "background-color" property of this
	 *         {@link AbstractFXColorPicker}.
	 */
	public ObjectProperty<Color> backgroundColorProperty() {
		return backgroundColorProperty;
	}

	/**
	 * Returns the "border-color" property of this {@link AbstractFXColorPicker}
	 * .
	 *
	 * @return The "border-color" property of this {@link AbstractFXColorPicker}
	 *         .
	 */
	public ObjectProperty<Color> borderColorProperty() {
		return borderColorProperty;
	}

	/**
	 * Returns the "color" property of this {@link AbstractFXColorPicker}.
	 *
	 * @return The "color" property of this {@link AbstractFXColorPicker}.
	 */
	public ObjectProperty<Color> colorProperty() {
		return colorProperty;
	}

	/**
	 * Returns the currently selected {@link Color}.
	 *
	 * @return The currently selected {@link Color}.
	 */
	public Color getColor() {
		return colorProperty.get();
	}

	/**
	 * Let's the user select a {@link Color} and returns that {@link Color}.
	 *
	 * @return The user selected {@link Color}.
	 */
	public abstract Color pickColor();

	/**
	 * Changes the currently selected color to the given value.
	 *
	 * @param color
	 *            The newly selected {@link Color}.
	 */
	public void setColor(Color color) {
		colorProperty.set(color);
	}

}
