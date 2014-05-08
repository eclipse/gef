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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

public abstract class AbstractFXColorPicker extends Group {
	
	public static final Color DEFAULT_COLOR = Color.WHITE;
	
	private SimpleObjectProperty<Color> colorProperty = new SimpleObjectProperty<Color>(DEFAULT_COLOR);

	public static class ColorWheel extends Canvas {
		private Color backgroundColor = new Color(1, 1, 1, 0);
		private Scale scale = new Scale();
		private int genSize;
		private double width = 25, height = 25;

		public ColorWheel() {
			getTransforms().add(scale);
			setGenSize(51);
		}

		public ColorWheel(double w, double h) {
			this();
			setDisplayWidth(w);
			setDisplayHeight(h);
		}

		private void setDisplayHeight(double h) {
			height = h;
			scale.setY(height / genSize);
		}

		private void setDisplayWidth(double w) {
			width = w;
			scale.setX(width / genSize);
		}

		private void setGenSize(int genSize) {
			this.genSize = genSize;
			setWidth(genSize);
			setHeight(genSize);
			
			// adjust scaling
			scale.setX(width / genSize);
			scale.setY(height / genSize);
			
			// compute donut
			Point2D mid = new Point2D(genSize / 2, genSize / 2);
			double donutMin = genSize / 6;
			double donutMax = genSize / 2 - 2;

			// render color wheel
			GraphicsContext g2d = getGraphicsContext2D();
			PixelWriter px = g2d.getPixelWriter();
			for (int y = 0; y < genSize; y++) {
				for (int x = 0; x < genSize; x++) {
					double d = mid.distance(x, y);
					if (donutMin < d && d < donutMax) {
						// inside donut
						double angleRad = Math.atan2(y - mid.getY(), x
								- mid.getX());
						Color color = Color.hsb(angleRad * 180 / Math.PI,
								1, 1);
						// TODO: anti-aliasing at borders
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

		ColorWheel colorWheel = new ColorWheel(20, 20);
		Button button = new Button(null, new Group(colorWheel));
		button.setStyle("-fx-padding: 3; -fx-focus-color: transparent;");

		Rectangle colorRect = new Rectangle(75, 25);
		colorRect.heightProperty().bind(button.heightProperty());

		hbox.getChildren().addAll(colorRect, new Separator(Orientation.VERTICAL), button);

		colorRect.fillProperty().bind(colorProperty);
		
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
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
