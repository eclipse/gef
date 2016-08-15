/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.gef.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef.zest.examples.AbstractZestExample;
import org.eclipse.gef.zest.fx.ZestProperties;

import com.google.inject.Module;

import javafx.application.Application;

public class ZestGraphExample extends AbstractZestExample {

	private static Graph buildAC(String id) {
		// create nodes "A" to "C"
		List<org.eclipse.gef.graph.Node> nodes = new ArrayList<>();
		nodes.addAll(Arrays.asList(
				n(ZestProperties.LABEL__NE, "A", ZestProperties.TOOLTIP__N, "Alpha", ZestProperties.CSS_ID__NE,
						id + "A"),
				n(ZestProperties.LABEL__NE, "B", ZestProperties.TOOLTIP__N, "Beta", ZestProperties.CSS_ID__NE,
						id + "B"),
				n(ZestProperties.LABEL__NE, "C", ZestProperties.TOOLTIP__N, "Gamma", ZestProperties.CSS_ID__NE,
						id + "C")));

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
		nodes.addAll(Arrays.asList(
				n(ZestProperties.LABEL__NE, "A", ZestProperties.TOOLTIP__N, "Alpha", ZestProperties.CSS_ID__NE,
						id + "A"),
				n(ZestProperties.LABEL__NE, "B", ZestProperties.TOOLTIP__N, "Beta", ZestProperties.CSS_ID__NE,
						id + "B"),
				n(ZestProperties.LABEL__NE, "C", ZestProperties.TOOLTIP__N, "Gamma", ZestProperties.CSS_ID__NE,
						id + "C"),
				n(ZestProperties.LABEL__NE, "D", ZestProperties.TOOLTIP__N, "Delta", ZestProperties.CSS_ID__NE,
						id + "D"),
				n(ZestProperties.LABEL__NE, "E", ZestProperties.TOOLTIP__N, "Epsilon", ZestProperties.CSS_ID__NE,
						id + "E")));

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
		nodes.addAll(Arrays.asList(n(ZestProperties.LABEL__NE, "0", ZestProperties.TOOLTIP__N, "zero"),
				n(ZestProperties.LABEL__NE, "1", ZestProperties.TOOLTIP__N, "one"),
				n(ZestProperties.LABEL__NE, "2", ZestProperties.TOOLTIP__N, "two"),
				n(ZestProperties.LABEL__NE, "3", ZestProperties.TOOLTIP__N, "three"),
				n(ZestProperties.LABEL__NE, "4", ZestProperties.TOOLTIP__N, "four"),
				n(ZestProperties.LABEL__NE, "5", ZestProperties.TOOLTIP__N, "five"),
				n(ZestProperties.LABEL__NE, "6", ZestProperties.TOOLTIP__N, "six"),
				n(ZestProperties.LABEL__NE, "7", ZestProperties.TOOLTIP__N, "seven"),
				n(ZestProperties.LABEL__NE, "8", ZestProperties.TOOLTIP__N, "eight"),
				n(ZestProperties.LABEL__NE, "9", ZestProperties.TOOLTIP__N, "nine")));

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

	public static void main(String[] args) {
		Application.launch(args);
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
