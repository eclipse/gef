/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import org.eclipse.gef4.fx.nodes.FXConnection;
import org.eclipse.gef4.fx.nodes.IFXDecoration;
import org.eclipse.gef4.geometry.planar.Point;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;

public class FXConnectionSnippet extends AbstractFXExample {

	public static class ArrowHead extends Polyline implements IFXDecoration {
		public ArrowHead() {
			super(15.0, 0.0, 10.0, 0.0, 10.0, 3.0, 0.0, 0.0, 10.0, -3.0, 10.0,
					0.0);
		}

		@Override
		public Point getLocalEndPoint() {
			return new Point(15, 0);
		}

		@Override
		public Point getLocalStartPoint() {
			return new Point(0, 0);
		}

		@Override
		public Node getVisual() {
			return this;
		}
	}

	public static void main(String[] args) {
		launch();
	}

	public FXConnectionSnippet() {
		super("FXConnectionSnippet");
	}

	@Override
	public Scene createScene() {
		FXConnection connection = new FXConnection();

		connection.setStartDecoration(new ArrowHead());
		connection.setEndDecoration(new ArrowHead());

		connection.setStartPoint(new Point(100, 100));
		connection.setEndPoint(new Point(300, 300));

		connection.addWayPoint(0, new Point(300, 100));

		Pane root = new Pane();
		root.getChildren().addAll(connection);
		return new Scene(root);
	}

}
