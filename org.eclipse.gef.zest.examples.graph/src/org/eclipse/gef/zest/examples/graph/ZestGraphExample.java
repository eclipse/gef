/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.examples.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef.zest.examples.AbstractZestExample;
import org.eclipse.gef.zest.fx.ZestProperties;

import com.google.inject.Module;

import javafx.application.Application;

public class ZestGraphExample extends AbstractZestExample {

	private static Graph buildAC(String id) {
		// create nodes "A" to "C"
		List<org.eclipse.gef.graph.Node> nodes = new ArrayList<>();
		nodes.addAll(Arrays.asList(n("A", id, "Alpha"), n("B", id, "Beta"), n("C", id, "Gamma")));

		// create some edges between those nodes
		List<Edge> edges = new ArrayList<>();
		edges.addAll(Arrays.asList(e(nodes.get(0), nodes.get(1)), e(nodes.get(1), nodes.get(2)),
				e(nodes.get(2), nodes.get(0))));

		// directed connections
		HashMap<String, Object> attrs = new HashMap<>();
		attrs.put(ZestProperties.LAYOUT_ALGORITHM__G, new SpringLayoutAlgorithm());
		return new Graph(attrs, nodes, edges);
	}

	private static Graph buildAE(String id) {
		// create nodes "A" to "C"
		List<org.eclipse.gef.graph.Node> nodes = new ArrayList<>();
		nodes.addAll(Arrays.asList(n("A", id, "Alpha"), n("B", id, "Beta"), n("C", id, "Gamma"), n("D", id, "Delta"),
				n("E", id, "Epsilon")));

		// add nested graphs
		nodes.get(4).setNestedGraph(buildAC("c"));

		// create some edges between those nodes
		List<Edge> edges = new ArrayList<>();
		edges.addAll(Arrays.asList(e(nodes.get(0), nodes.get(1)), e(nodes.get(1), nodes.get(2)),
				e(nodes.get(2), nodes.get(3)), e(nodes.get(3), nodes.get(4)), e(nodes.get(4), nodes.get(0))));

		// directed connections
		HashMap<String, Object> attrs = new HashMap<>();
		attrs.put(ZestProperties.LAYOUT_ALGORITHM__G, new SpringLayoutAlgorithm());
		return new Graph(attrs, nodes, edges);
	}

	public static Graph createDefaultGraph() {
		// create nodes "0" to "9"
		List<org.eclipse.gef.graph.Node> nodes = new ArrayList<>();
		nodes.addAll(Arrays.asList(n("0", "", "zero"), n("1", "", "one"), n("2", "", "two"), n("3", "", "three"),
				n("4", "", "four"), n("5", "", "five"), n("6", "", "six"), n("7", "", "seven"), n("8", "", "eight"),
				n("9", "", "nine")));

		// set nested graphs
		nodes.get(0).setNestedGraph(buildAC("a"));
		nodes.get(5).setNestedGraph(buildAE("b"));

		// create some edges between those nodes
		List<Edge> edges = new ArrayList<>();
		edges.addAll(Arrays.asList(e(nodes.get(0), nodes.get(9)), e(nodes.get(1), nodes.get(8)),
				e(nodes.get(2), nodes.get(7)), e(nodes.get(3), nodes.get(6)), e(nodes.get(4), nodes.get(5)),
				e(nodes.get(0), nodes.get(4)), e(nodes.get(1), nodes.get(6)), e(nodes.get(2), nodes.get(8)),
				e(nodes.get(3), nodes.get(5)), e(nodes.get(4), nodes.get(7)), e(nodes.get(5), nodes.get(1))));

		// directed connections
		HashMap<String, Object> attrs = new HashMap<>();
		attrs.put(ZestProperties.LAYOUT_ALGORITHM__G, new SpringLayoutAlgorithm());
		return new Graph(attrs, nodes, edges);
	}

	private static Edge e(Node n, Node m) {
		return e(n, m, ZestProperties.TARGET_DECORATION__E, new javafx.scene.shape.Polygon(0, 0, 10, 3, 10, -3),
				ZestProperties.TARGET_DECORATION_CSS_STYLE__E, "-fx-fill: white;");
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	private static Node n(String label, String idPrefix, String tooltip) {
		return n(ZestProperties.LABEL__NE, label, ZestProperties.CSS_ID__NE, idPrefix + label,
				ZestProperties.TOOLTIP__N, tooltip);
	}

	public ZestGraphExample() {
		super("GEF Zest Graph Example");
	}

	@Override
	protected Graph createGraph() {
		return createDefaultGraph();
	}

	@Override
	protected Module createModule() {
		return new ZestGraphExampleModule();
	}
}
