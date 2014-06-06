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

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;

import org.eclipse.gef4.fx.examples.FXApplication;
import org.eclipse.gef4.fx.nodes.FXCurveConnection;
import org.eclipse.gef4.fx.nodes.IFXDecoration;
import org.eclipse.gef4.fx.widgets.FXLabeledConnection;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;

public class FXConnectionSnippet extends FXApplication {

	public static class ArrowHead extends Polyline implements IFXDecoration {
		public ArrowHead() {
			super(15.0, 0.0, 10.0, 0.0, 10.0, 3.0, 0.0, 0.0, 10.0, -3.0, 10.0,
					0.0);
		}

		@Override
		public Point getLocalStartPoint() {
			return new Point(0, 0);
		}

		@Override
		public Point getLocalEndPoint() {
			return new Point(15, 0);
		}

		@Override
		public Node getVisual() {
			return this;
		}
	}
	
	public static void main(String[] args) {
		launch();
	}
	
	@Override
	public Scene createScene() {
		FXLabeledConnection connection = new FXLabeledConnection(new Text(), new FXCurveConnection() {
			@Override
			public ICurve computeGeometry(Point[] points) {
				return PolyBezier.interpolateCubic(points);
			}
		});
		connection.setLabel("FXLabeledConnection");
		FXCurveConnection c = connection.getConnection();
		c.setStartDecoration(new ArrowHead());
		c.setEndDecoration(new ArrowHead());
		c.setStartPoint(new Point(100, 100));
		c.setEndPoint(new Point(300, 300));
		c.addWayPoint(0, new Point(300, 100));
		
		Pane root = new Pane();
		root.getChildren().addAll(connection);
		return new Scene(root);
	}

}
