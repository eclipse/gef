/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.examples.snippets;

import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.examples.AbstractFxExample;
import org.eclipse.gef.fx.nodes.Connection;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DynamicAnchorConnectionSnippet extends AbstractFxExample {

	public static void main(String[] args) {
		launch();
	}

	private Rectangle nodeA;

	private Rectangle nodeB;
	private Rectangle nodeC;
	private DynamicAnchor anchorA;
	private DynamicAnchor anchorB;
	private DynamicAnchor anchorC;

	public DynamicAnchorConnectionSnippet() {
		super("DynamicConnectionSnippet");
	}

	private EventHandler<ActionEvent> createMoveHandler(final String label,
			final Node node, final double x, final double y0, final double y1) {
		return new EventHandler<ActionEvent>() {
			boolean flag = false;

			@Override
			public void handle(ActionEvent event) {
				node.relocate(x, flag ? y0 : y1);
				flag = !flag;
			}
		};
	}

	@Override
	public Scene createScene() {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 640, 480);

		nodeA = new Rectangle(50, 50);
		nodeA.setFill(Color.RED);

		nodeB = new Rectangle(50, 50);
		nodeB.setFill(Color.BLUE);

		nodeC = new Rectangle(50, 50);
		nodeC.setFill(Color.GREEN);

		Button btnA = new Button("move A");
		btnA.setOnAction(createMoveHandler("A", nodeA, 100, 100, 200));
		btnA.relocate(0, 0);

		Button btnB = new Button("move B");
		btnB.setOnAction(createMoveHandler("B", nodeB, 300, 100, 200));
		btnB.relocate(70, 0);

		Button btnC = new Button("move C");
		btnC.setOnAction(createMoveHandler("C", nodeC, 200, 200, 300));
		btnC.relocate(140, 0);

		Connection connectionAB = new Connection();
		Connection connectionBC = new Connection();

		Group group = new Group(nodeA, nodeB, nodeC, connectionAB, connectionBC,
				btnA, btnB, btnC);
		root.getChildren().add(group);

		anchorA = new DynamicAnchor(nodeA);
		anchorB = new DynamicAnchor(nodeB);
		anchorC = new DynamicAnchor(nodeC);
		connectionAB.setStartAnchor(anchorA);
		connectionAB.setEndAnchor(anchorB);
		connectionBC.setStartAnchor(anchorB);
		connectionBC.setEndAnchor(anchorC);

		nodeA.relocate(100, 100);
		nodeB.relocate(300, 100);
		nodeC.relocate(200, 200);

		return scene;
	}

}
