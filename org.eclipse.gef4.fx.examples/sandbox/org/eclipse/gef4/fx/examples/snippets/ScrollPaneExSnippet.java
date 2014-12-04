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
package org.eclipse.gef4.fx.examples.snippets;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import org.eclipse.gef4.fx.examples.FXApplication;

public class ScrollPaneExSnippet extends FXApplication {

	public static void main(String[] args) {
		launch();
	}

	@Override
	public Scene createScene() {
		BorderPane root = new BorderPane();

		final ScrollPaneEx scrollPane = new ScrollPaneEx();
		root.setCenter(scrollPane);
		scrollPane
				.getContentGroup()
				.getChildren()
				.addAll(rect(25, 25, 100, 50, Color.BLUE),
						rect(25, 200, 25, 50, Color.BLUE),
						rect(150, 100, 75, 75, Color.BLUE),
						rect(-100, -100, 30, 60, Color.CYAN));

		// translate to top-left most content node
		Bounds canvasBounds = scrollPane.getCanvas().getBoundsInLocal();
		double minx = canvasBounds.getMinX();
		double miny = canvasBounds.getMinY();
		// scrollPane.getCanvas().setTranslateX(-minx);
		// scrollPane.getCanvas().setTranslateY(-miny);

		scrollPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (MouseButton.SECONDARY.equals(event.getButton())) {
					// TODO: zoom with pivot
				}
			}
		});

		return new Scene(root, 400, 300);
	}

	private Node rect(double layoutX, double layoutY, double width,
			double height, Paint fill) {
		final Rectangle rect = new Rectangle(width, height, fill);
		rect.setLayoutX(layoutX);
		rect.setLayoutY(layoutY);

		// register drag listeners
		final double[] initialLayout = new double[2];
		final double[] initialMouse = new double[2];
		rect.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				initialLayout[0] = rect.getLayoutX();
				initialLayout[1] = rect.getLayoutY();
				initialMouse[0] = event.getSceneX();
				initialMouse[1] = event.getSceneY();
			}
		});
		EventHandler<MouseEvent> dragHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				double dx = event.getSceneX() - initialMouse[0];
				double dy = event.getSceneY() - initialMouse[1];
				rect.setLayoutX(initialLayout[0] + dx);
				rect.setLayoutY(initialLayout[1] + dy);
			}
		};
		rect.setOnMouseDragged(dragHandler);
		rect.setOnMouseReleased(dragHandler);

		return rect;
	}

}
