/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
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
package org.eclipse.gef4.swt.canvas.ex;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TestCanvasFx extends Application {

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage frame) throws Exception {
		frame.setTitle("Fx Canvas");
		Group root = new Group();

		Canvas c = new Canvas(640, 480);
		GraphicsContext g = c.getGraphicsContext2D();
		g.setFill(new Color(0, 0.5, 0.5, 1));
		g.fillOval(50, 50, 100, 100);

		Canvas cSub = new Canvas(100, 100);
		cSub.setLayoutX(300);
		cSub.setLayoutY(150);
		g = cSub.getGraphicsContext2D();
		g.setFill(new Color(0.5, 0, 0.5, 1));
		g.fillOval(0, 0, 100, 100);

		root.getChildren().add(c);
		root.getChildren().add(cSub);
		frame.setScene(new Scene(root));
		frame.show();
	}

}
