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
 *     Alexander Nyßen (itemis AG) - adjusted example to show relevant features
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.examples;

import org.eclipse.gef4.fx.anchors.ChopBoxAnchor;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.fx.nodes.IConnectionDecoration;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineJoin;

public class ConnectionSnippet extends AbstractFxExample {

	public static class ArrowHead extends Polyline implements IConnectionDecoration {
		public ArrowHead() {
			super(15.0, 0.0, 10.0, 0.0, 10.0, 3.0, 0.0, 0.0, 10.0, -3.0, 10.0,
					0.0);
			setFill(Color.WHITE);
			setStrokeLineJoin(StrokeLineJoin.ROUND);
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

	public ConnectionSnippet() {
		super("Connection Snippet");
	}

	@Override
	public Scene createScene() {
		GeometryNode<RoundedRectangle> end = new GeometryNode<RoundedRectangle>(
				new RoundedRectangle(0, 0, 30, 30, 10, 10));
		end.setFill(Color.RED);
		end.relocate(50, 50);
		makeDraggable(end);

		// create connection, provide decoration
		Connection connection = new Connection();
		connection.setEndDecoration(new ArrowHead());

		// set start point and end anchor
		connection.setStartPoint(new Point(150, 150));
		connection.setEndAnchor(new ChopBoxAnchor(end));

		Group root = new Group();
		root.getChildren().addAll(end, connection);
		return new Scene(root, 300, 300);
	}

	private void makeDraggable(final Node node) {
		EventHandler<MouseEvent> dragNodeHandler = new EventHandler<MouseEvent>() {
			Point2D initialMouse, initialTranslate;

			@Override
			public void handle(MouseEvent event) {
				if (MouseEvent.MOUSE_PRESSED.equals(event.getEventType())) {
					// save initial mouse location and initial node translation
					// when the node is pressed
					initialMouse = new Point2D(event.getSceneX(),
							event.getSceneY());
					initialTranslate = new Point2D(node.getTranslateX(),
							node.getTranslateY());
				} else {
					// move the node by the mouse offset
					node.setTranslateX(initialTranslate.getX()
							+ event.getSceneX() - initialMouse.getX());
					node.setTranslateY(initialTranslate.getY()
							+ event.getSceneY() - initialMouse.getY());
				}
			}
		};
		node.addEventHandler(MouseEvent.MOUSE_PRESSED, dragNodeHandler);
		node.addEventHandler(MouseEvent.MOUSE_DRAGGED, dragNodeHandler);
		node.addEventHandler(MouseEvent.MOUSE_RELEASED, dragNodeHandler);
	}
}
