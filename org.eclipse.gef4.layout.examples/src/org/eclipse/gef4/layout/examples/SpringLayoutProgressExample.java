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
 * Note: Parts of this class have been transferred from org.eclipse.gef4.zest.examples.layout.SpringLayoutProgress
 *
 *******************************************************************************/
package org.eclipse.gef4.layout.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.ToggleButton;

import org.eclipse.gef4.fx.nodes.ScrollPaneEx;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.examples.AbstractZestExample;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;

public class SpringLayoutProgressExample extends AbstractZestExample {

	/**
	 * The ManualSpringLayoutAlgorithm does not perform full layout passes, so
	 * that we can use the {@link #performNIteration(int)} method to gradually
	 * apply the layout.
	 */
	public static class ManualSpringLayoutAlgorithm
			extends SpringLayoutAlgorithm {
		@Override
		public void applyLayout(boolean clean) {
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public SpringLayoutProgressExample() {
		super("GEF4 Layouts - Spring Layout Progress Example");
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
			n.getAttrs().put(LayoutProperties.LOCATION_PROPERTY,
					new Point(200, 200));
		}

		return new Graph.Builder().nodes(nodes.toArray(new Node[] {}))
				.edges(edges.toArray(new Edge[] {}))
				.attr(ZestProperties.GRAPH_LAYOUT, new SpringLayoutAlgorithm())
				.build();
	}

	@Override
	protected void customizeUi(ScrollPaneEx scrollPane) {
		Group overlay = scrollPane.getScrollbarGroup();
		final ToggleButton button = new ToggleButton("step");
		final ManualSpringLayoutAlgorithm[] layoutAlgorithm = new ManualSpringLayoutAlgorithm[1];
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (layoutAlgorithm[0] == null) {
					layoutAlgorithm[0] = new ManualSpringLayoutAlgorithm();
					layoutAlgorithm[0].setRandom(false);
					ZestProperties.setLayout(graph, layoutAlgorithm[0]);
				} else {
					viewer.getContentPartMap().get(graph)
							.getAdapter(GraphLayoutContext.class)
							.applyStaticLayout(true);
				}
			}
		});
		overlay.getChildren().add(button);
		new AnimationTimer() {
			private long last = 0;
			private final long NANOS_PER_MILLI = 1000000;
			private final long NANOS_PER_ITERATION = 10 * NANOS_PER_MILLI;

			@Override
			public void handle(long now) {
				if (button.isSelected()) {
					long elapsed = now - last;
					if (elapsed > NANOS_PER_ITERATION) {
						int n = (int) (elapsed / NANOS_PER_ITERATION);
						layoutAlgorithm[0].performNIteration(n);
						last = now;
					}
				} else {
					last = now;
				}
			}
		}.start();
	}

}
