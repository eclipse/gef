/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
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
package org.eclipse.gef.fx.examples.snippets;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

/**
 * @author Alexander Nyßen (alexander.nyssen@itemis.de)
 */
public class PolygonLayoutBoundsBug extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		final Polygon p = new Polygon(10, 30, 20, 20, 20, 40);
		p.setFill(Color.RED);
		p.setStroke(Color.BLACK);

		final Rectangle r = new Rectangle();
		r.setFill(new Color(0, 0, 1, 0.5));
		r.setX(p.getLayoutBounds().getMinX());
		r.setY(p.getLayoutBounds().getMinY());
		r.setWidth(p.getLayoutBounds().getWidth());
		r.setHeight(p.getLayoutBounds().getHeight());

		Group g = new Group(r, p);
		g.getTransforms().add(new Scale(10, 10));
		Scene scene = new Scene(g, 500, 500);
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
	}
}
