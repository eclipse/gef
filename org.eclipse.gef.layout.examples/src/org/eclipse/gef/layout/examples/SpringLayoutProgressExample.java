/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 * Note: Parts of this class have been transferred from org.eclipse.gef.zest.examples.layout.SpringLayoutProgress
 *
 *******************************************************************************/
package org.eclipse.gef.layout.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;
import org.eclipse.gef.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;
import org.eclipse.gef.zest.examples.AbstractZestExample;
import org.eclipse.gef.zest.fx.ZestProperties;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class SpringLayoutProgressExample extends AbstractZestExample {

	public static void main(String[] args) {
		launch(args);
	}

	private ToggleButton toggleLayoutButton;
	private SpringLayoutAlgorithm layoutAlgorithm;

	public SpringLayoutProgressExample() {
		super("GEF Layouts - Spring Layout Progress Example");
	}

	@Override
	protected Graph createGraph() {
		List<Node> nodes = new ArrayList<>();
		List<Edge> edges = new ArrayList<>();

		Node root = n(LABEL, "Root");
		nodes.add(root);

		Node aa = n(LABEL, "A");
		Node bb = n(LABEL, "B");
		Node cc = n(LABEL, "C");
		nodes.addAll(Arrays.asList(aa, bb, cc));

		Node dd = n(LABEL, "D");
		Node ee = n(LABEL, "E");
		Node ff = n(LABEL, "F");
		nodes.addAll(Arrays.asList(dd, ee, ff));

		edges.addAll(Arrays.asList(e(root, aa, LABEL, ""),
				e(root, bb, LABEL, ""), e(root, cc, LABEL, "")));

		edges.addAll(Arrays.asList(e(aa, bb, LABEL, ""), e(bb, cc, LABEL, ""),
				e(cc, aa, LABEL, ""), e(aa, dd, LABEL, ""),
				e(bb, ee, LABEL, ""), e(cc, ff, LABEL, ""),
				e(cc, dd, LABEL, ""), e(dd, ee, LABEL, ""),
				e(ee, ff, LABEL, "")));

		Node[] mix = new Node[3];
		mix[0] = aa;
		mix[1] = bb;
		mix[2] = cc;

		for (int k = 0; k < 1; k++) {
			for (int i = 0; i < 8; i++) {
				Node n = n(LABEL, "1 - " + i);
				nodes.add(n);
				for (int j = 0; j < 5; j++) {
					Node n2 = n(LABEL, "2 - " + j);
					nodes.add(n2);
					Edge e = e(n, n2, LABEL, "", "weight", "-1");
					edges.addAll(
							Arrays.asList(e, e(mix[j % 3], n2, LABEL, "")));
				}
				edges.add(e(root, n, LABEL, ""));
			}
		}

		for (Node n : nodes) {
			n.attributesProperty().put(LayoutProperties.LOCATION_PROPERTY,
					new Point(200, 200));
		}

		return new Graph.Builder().nodes(nodes.toArray(new Node[] {}))
				.edges(edges.toArray(new Edge[] {}))
				.attr(ZestProperties.LAYOUT_ALGORITHM__G,
						new SpringLayoutAlgorithm())
				.build();
	}

	@Override
	protected Scene createScene(IViewer viewer) {
		Scene scene = super.createScene(viewer);
		Group overlay = ((InfiniteCanvasViewer) viewer).getCanvas()
				.getOverlayGroup();
		toggleLayoutButton = new ToggleButton("step");
		layoutAlgorithm = new SpringLayoutAlgorithm();
		layoutAlgorithm.setRandom(true);
		ZestProperties.setLayoutAlgorithm(graph, layoutAlgorithm);
		overlay.getChildren().add(toggleLayoutButton);
		return scene;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		super.start(primaryStage);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				LayoutContext layoutContext = viewer.getContentPartMap()
						.get(graph).getAdapter(LayoutContext.class);
				layoutContext.applyLayout(true);
				new AnimationTimer() {
					private long last = 0;
					private final long NANOS_PER_MILLI = 1000000;
					private final long NANOS_PER_ITERATION = 10
							* NANOS_PER_MILLI;

					@Override
					public void handle(long now) {
						if (toggleLayoutButton.isSelected()) {
							long elapsed = now - last;
							if (elapsed > NANOS_PER_ITERATION) {
								int n = (int) (elapsed / NANOS_PER_ITERATION);
								layoutAlgorithm.performNIteration(n);
								last = now;
							}
						} else {
							last = now;
						}
					}
				}.start();
			}
		});
	}

}
