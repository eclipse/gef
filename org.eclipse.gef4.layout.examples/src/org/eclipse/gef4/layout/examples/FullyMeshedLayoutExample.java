/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.layout.examples;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.zest.examples.AbstractZestExample;
import org.eclipse.gef4.zest.fx.ZestProperties;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public class FullyMeshedLayoutExample extends AbstractZestExample {

	public static void main(String[] args) {
		Application.launch(args);
	}

	private Graph graph = new Graph();

	public FullyMeshedLayoutExample() {
		super("GEF4 Layout - Fully Meshed Example");
	}

	@Override
	protected Graph createGraph() {
		ZestProperties.setLayout(graph, new RadialLayoutAlgorithm());
		return graph;
	}

	@Override
	protected Scene createScene(FXViewer viewer) {
		Scene scene = super.createScene(viewer);
		Group overlay = viewer.getCanvas().getOverlayGroup();
		Button addNodeButton = new Button("add node");
		overlay.getChildren().add(addNodeButton);
		addNodeButton.setOnAction(new EventHandler<ActionEvent>() {
			private int id = 0;
			private List<org.eclipse.gef4.graph.Node> nodes = new ArrayList<org.eclipse.gef4.graph.Node>();

			@Override
			public void handle(ActionEvent event) {
				// add node
				Node newNode = n(graph, LABEL, Integer.toString(id++));
				// connect with all other nodes
				for (org.eclipse.gef4.graph.Node n : nodes) {
					e(graph, n, newNode);
				}
				nodes.add(newNode);
			}
		});
		return scene;
	}

}
