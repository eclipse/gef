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

import java.lang.reflect.Field;
import java.util.HashMap;

import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.anchors.OrthogonalProjectionStrategy;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.fx.nodes.OrthogonalRouter;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

public class ConnectionSnippet extends AbstractFxExample {

	public static class ArrowHead extends Polygon {
		public ArrowHead() {
			super(0, 0, 10, 3, 10, -3);
			setFill(Color.WHITE);
			setStroke(Color.BLACK);
			setStrokeLineJoin(StrokeLineJoin.ROUND);
		}
	}

	public static void main(String[] args) {
		// XXX: This is a workaround for JDK-8143907, which happens in
		// standalone
		// applications on Mac OS X El Capitan
		{
			// TODO: Remove when dropping support for JavaSE-1.7
			if (System.getProperty("java.version").startsWith("1.7.0")
					&& System.getProperty("os.name").equals("Mac OS X")) {
				try {
					Class<?> macFontFinderClass = Class
							.forName("com.sun.t2k.MacFontFinder");
					Field psNameToPathMapField = macFontFinderClass
							.getDeclaredField("psNameToPathMap");
					psNameToPathMapField.setAccessible(true);
					psNameToPathMapField.set(null,
							new HashMap<String, String>());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		launch();
	}

	public ConnectionSnippet() {
		super("Connection Snippet");
	}

	@Override
	public Scene createScene() {
		GeometryNode<RoundedRectangle> end1 = new GeometryNode<>(
				new RoundedRectangle(50, 50, 30, 30, 20, 20));
		end1.setFill(Color.RED);
		end1.setStrokeWidth(3);
		end1.setStrokeType(StrokeType.OUTSIDE);
		makeDraggable(end1);

		// use a shape
		javafx.scene.shape.Rectangle end2 = new javafx.scene.shape.Rectangle(
				200, 50, 30, 30);
		end2.setArcWidth(20);
		end2.setArcHeight(20);
		end2.setStroke(Color.BLACK);
		end2.setFill(Color.RED);
		end2.setStrokeWidth(3);
		end2.setStrokeType(StrokeType.OUTSIDE);
		makeDraggable(end2);

		// use a control as start, where layout bounds are always (0, 0, width,
		// height); this demonstrates anchor positions are calculated properly
		Label start = new Label("Some label");
		start.setLayoutX(150);
		start.setLayoutY(150);
		makeDraggable(start);
		// start.setBackground(new Background(new BackgroundFill(Color.GREY,
		// CornerRadii.EMPTY, new Insets(0))));
		// start.setBorder(
		// new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID,
		// CornerRadii.EMPTY, new BorderWidths(10))));

		// set start point and end anchor
		// create connection, provide decoration
		Connection connection1 = new Connection();
		connection1.setEndDecoration(new ArrowHead());
		connection1.setStartAnchor(new DynamicAnchor(start));
		connection1.setEndAnchor(new DynamicAnchor(end1));

		Connection connection2 = new Connection();
		connection2.setRouter(new OrthogonalRouter());
		connection2.setEndDecoration(new ArrowHead());
		connection2.setStartAnchor(
				new DynamicAnchor(start, new OrthogonalProjectionStrategy()));
		connection2.setEndAnchor(
				new DynamicAnchor(end2, new OrthogonalProjectionStrategy()));

		Group root = new Group();
		root.getChildren().addAll(start, end1, end2, connection1, connection2);
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
