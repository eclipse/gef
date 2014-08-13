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

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.eclipse.gef4.fx.examples.FXApplication;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Pie;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;

public class FXGeometryNodeSnippet extends FXApplication {

	public static void main(String[] args) {
		FXGeometryNodeSnippet.launch(args);
	}

	private static <T extends IShape> FXGeometryNode<T> shape(T shape,
			double r, double g, double b) {
		FXGeometryNode<T> fxShape = new FXGeometryNode<T>(shape);
		fxShape.setFill(new Color(r, g, b, 1));
		fxShape.setStroke(new Color(0, 0, 0, 1));
		return fxShape;
	}

	@Override
	public Scene createScene() {
		HBox hbox = new HBox();
		VBox col1 = new VBox();
		VBox col2 = new VBox();
		hbox.getChildren().addAll(col1, col2);
		HBox.setHgrow(col1, Priority.ALWAYS);
		HBox.setHgrow(col2, Priority.ALWAYS);

		col1.getChildren().addAll(
				new Button("abc"),
				shape(new Polygon(50, 0, 100, 100, 0, 100), 0, 1, 0),
				shape(new Pie(0, 0, 100, 100, Angle.fromDeg(15), Angle
						.fromDeg(120)), 0, 1, 1), new Button("123"));

		col2.getChildren().addAll(shape(new Ellipse(0, 0, 60, 80), 1, 0, 0),
				shape(new Rectangle(0, 0, 100, 50), 0, 0, 1),
				new Button("foobar"),
				shape(new RoundedRectangle(0, 0, 100, 100, 10, 10), 1, 0, 1));

		return new Scene(hbox, 400, 300);
	}

}
