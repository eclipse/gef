/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.swt.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swt.FXCanvas;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * An SWT control that can be used to select a JavaFX color (and indicates the
 * selected color via an image).
 *
 * @author anyssen
 *
 */
public class FXColorPicker extends Composite {

	/**
	 * Property name used in change events related to {@link #colorProperty()}.
	 */
	public static final String COLOR_PROPERTY = "color";

	/**
	 * Opens a {@link ColorDialog} to let the user pick a {@link Color}. Returns
	 * the picked {@link Color}, or <code>null</code> if no color was picked.
	 *
	 * @param shell
	 *            The {@link Shell} which serves as the parent for the
	 *            {@link ColorDialog}.
	 * @param initial
	 *            The initial {@link Color} to display in the
	 *            {@link ColorDialog}.
	 * @return The picked {@link Color}, or <code>null</code>.
	 */
	protected static Color pickColor(Shell shell, Color initial) {
		ColorDialog cd = new ColorDialog(shell);
		RGB rgb = new RGB((int) (255 * initial.getRed()),
				(int) (255 * initial.getGreen()),
				(int) (255 * initial.getBlue()));
		cd.setRGB(rgb);
		RGB newRgb = cd.open();
		if (newRgb != null) {
			return Color.rgb(newRgb.red, newRgb.green, newRgb.blue);
		}
		return null;
	}

	private ObjectProperty<Color> color = new SimpleObjectProperty<>(this,
			COLOR_PROPERTY);
	private Rectangle colorRectangle;

	/**
	 * Constructs a new {@link FXColorPicker}.
	 *
	 * @param parent
	 *            The parent {@link Composite}.
	 * @param color
	 *            The initial {@link Color} to set.
	 */
	public FXColorPicker(final Composite parent, Color color) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout());

		FXCanvas canvas = new FXCanvas(this, SWT.NONE);

		// container
		Group colorPickerGroup = new Group();
		HBox hbox = new HBox();
		colorPickerGroup.getChildren().add(hbox);

		// color wheel
		WritableImage colorWheelImage = new WritableImage(64, 64);
		renderColorWheel(colorWheelImage, 0, 0, 64);
		ImageView colorWheel = new ImageView(colorWheelImage);
		colorWheel.setFitWidth(16);
		colorWheel.setFitHeight(16);

		BorderPane colorWheelPane = new BorderPane();
		Insets insets = new Insets(2.0);
		colorWheelPane.setPadding(insets);
		colorWheelPane.setCenter(colorWheel);
		// use background color of parent composite (the wheel image is
		// transparent outside the wheel, so otherwise the hbox color would look
		// through)
		colorWheelPane.setStyle("-fx-background-color: "
				+ computeRgbString(Color.rgb(parent.getBackground().getRed(),
						parent.getBackground().getGreen(),
						parent.getBackground().getBlue())));

		colorRectangle = new Rectangle(50, 20);
		// bind to ColorWheel instead of buttonPane to prevent layout
		// problems.
		colorRectangle.widthProperty().bind(colorWheel.fitWidthProperty()
				.add(insets.getLeft()).add(insets.getRight()).multiply(2.5));
		colorRectangle.heightProperty().bind(colorWheelPane.heightProperty());

		// draw 'border' around hbox (and fill background, which covers the
		// space beween color rect and wheel
		hbox.setStyle("-fx-border-color: " + computeRgbString(Color.DARKGREY)
				+ "; -fx-background-color: "
				+ computeRgbString(Color.DARKGREY));
		hbox.getChildren().addAll(colorRectangle, colorWheelPane);
		hbox.setSpacing(0.5);

		// interaction
		colorWheelPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Color colorOrNull = FXColorPicker.pickColor(getShell(),
						getColor());
				if (colorOrNull != null) {
					setColor(colorOrNull);
				}
			}
		});

		Scene scene = new Scene(colorPickerGroup);

		// copy background color from parent composite
		org.eclipse.swt.graphics.Color backgroundColor = parent.getBackground();
		scene.setFill(Color.rgb(backgroundColor.getRed(),
				backgroundColor.getGreen(), backgroundColor.getBlue()));
		canvas.setScene(scene);

		// initialize some color
		setColor(color);

		colorRectangle.fillProperty().bind(this.color);
	}

	/**
	 * A writable property for the color controlled by this
	 * {@link FXColorPicker}.
	 *
	 * @return A writable {@link Property}.
	 */
	public Property<Color> colorProperty() {
		return color;
	}

	private String computeRgbString(Color color) {
		return "rgb(" + (int) (255 * color.getRed()) + ","
				+ (int) (255 * color.getGreen()) + ","
				+ (int) (255 * color.getBlue()) + ")";
	}

	/**
	 * Returns the currently selected {@link Color}.
	 *
	 * @return The currently selected {@link Color}.
	 */
	public Color getColor() {
		return color.get();
	}

	/**
	 * Draws a color wheel into the given {@link WritableImage}, starting at the
	 * given offsets, in the given size (in pixel).
	 *
	 * @param image
	 *            The {@link WritableImage} in which the color wheel is drawn.
	 * @param offsetX
	 *            The horizontal offset (in pixel).
	 * @param offsetY
	 *            The vertical offset (in pixel).
	 * @param size
	 *            The size (in pixel).
	 */
	private void renderColorWheel(WritableImage image, int offsetX, int offsetY,
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
					// compute saturation depending on distance to
					// middle
					// ([0;1])
					double sat = d / radius;
					Color color = Color.hsb(angleRad * 180 / Math.PI, sat, 1);
					px.setColor(offsetX + x, offsetY + y, color);
				} else {
					px.setColor(offsetX + x, offsetY + y, Color.TRANSPARENT);
				}
			}
		}
	}

	/**
	 * Changes the currently selected {@link Color} to the given value.
	 *
	 * @param color
	 *            The newly selected {@link Color}.
	 */
	public void setColor(Color color) {
		this.color.set(color);
	}
}