/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo.parts;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public class FXCreateCurveHoverHandlePart extends
		AbstractLogoHoverHandlePart<StackPane> {

	@Override
	protected StackPane createVisual() {
		StackPane stackPane = new StackPane();
		stackPane.setPickOnBounds(true);
		stackPane.getStyleClass().add("FXCreateCurveHoverHandlePart");
		stackPane.getStylesheets().add(
				getClass().getResource("hoverhandles.css").toExternalForm());
		final Circle shape = new Circle(7.5);
		shape.setFill(Color.DARKGREY);
		Text label = new Text("+");
		label.setBoundsType(TextBoundsType.VISUAL);
		stackPane.getChildren().addAll(shape, label);

		// add hover effect
		stackPane.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				shape.setFill(Color.DARKGREEN);
			}
		});
		stackPane.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				shape.setFill(Color.DARKGREY);
			}
		});

		return stackPane;
	}

}
