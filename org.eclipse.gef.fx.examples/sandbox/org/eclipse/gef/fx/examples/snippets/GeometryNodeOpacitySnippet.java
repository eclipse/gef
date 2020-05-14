/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.fx.examples.snippets;

import org.eclipse.gef.fx.examples.AbstractFxExample;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.RoundedRectangle;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GeometryNodeOpacitySnippet extends AbstractFxExample {

	public static void main(String[] args) {
		launch();
	}

	public GeometryNodeOpacitySnippet() {
		super("GeometryNode opacity snippet");
	}

	@Override
	public Scene createScene() {
		Pane root = new Pane();
		Scene scene = new Scene(root, 300, 500);

		GeometryNode<RoundedRectangle> geometryNode = new GeometryNode<>(
				new RoundedRectangle(100, 100, 100, 100, 30, 30));
		geometryNode.setStroke(Color.TRANSPARENT);
		geometryNode.setStyle("-fx-fill: red; -fx-opacity: 0.5;");

		Rectangle rectangle = new Rectangle(100, 300, 100, 100);
		rectangle.setArcHeight(30);
		rectangle.setArcWidth(30);
		rectangle.setStroke(Color.TRANSPARENT);
		rectangle.setStyle("-fx-fill: red; -fx-opacity: 0.5;");

		root.getChildren().addAll(geometryNode, rectangle);

		return scene;
	}

}
