/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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
package org.eclipse.gef4.zest.examples.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javafx.application.Application;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.examples.AbstractZestExample;
import org.eclipse.gef4.zest.fx.ZestProperties;

import com.google.inject.Module;

public class ZestGraphExample extends AbstractZestExample {

	private static Graph buildAC(String id) {
		// create nodes "A" to "C"
		List<org.eclipse.gef4.graph.Node> nodes = new ArrayList<org.eclipse.gef4.graph.Node>();
		nodes.addAll(Arrays.asList(
				n(ZestProperties.NODE_LABEL, "A", ZestProperties.NODE_TOOLTIP,
						"Alpha", ZestProperties.NODE_CSS_ID, id + "A"),
				n(ZestProperties.NODE_LABEL, "B", ZestProperties.NODE_TOOLTIP,
						"Beta", ZestProperties.NODE_CSS_ID, id + "B"),
				n(ZestProperties.NODE_LABEL, "C", ZestProperties.NODE_TOOLTIP,
						"Gamma", ZestProperties.NODE_CSS_ID, id + "C")));

		// create some edges between those nodes
		List<Edge> edges = new ArrayList<Edge>();
		edges.addAll(Arrays.asList(e(nodes.get(0), nodes.get(1)),
				e(nodes.get(1), nodes.get(2)), e(nodes.get(2), nodes.get(0))));

		// directed connections
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		attrs.put(ZestProperties.GRAPH_TYPE, ZestProperties.GRAPH_TYPE_DIRECTED);
		return new Graph(attrs, nodes, edges);
	}

	private static Graph buildAE(String id) {
		// create nodes "A" to "C"
		List<org.eclipse.gef4.graph.Node> nodes = new ArrayList<org.eclipse.gef4.graph.Node>();
		nodes.addAll(Arrays.asList(
				n(ZestProperties.NODE_LABEL, "A", ZestProperties.NODE_TOOLTIP,
						"Alpha", ZestProperties.NODE_CSS_ID, id + "A"),
				n(ZestProperties.NODE_LABEL, "B", ZestProperties.NODE_TOOLTIP,
						"Beta", ZestProperties.NODE_CSS_ID, id + "B"),
				n(ZestProperties.NODE_LABEL, "C", ZestProperties.NODE_TOOLTIP,
						"Gamma", ZestProperties.NODE_CSS_ID, id + "C"),
				n(ZestProperties.NODE_LABEL, "D", ZestProperties.NODE_TOOLTIP,
						"Delta", ZestProperties.NODE_CSS_ID, id + "D"),
				n(ZestProperties.NODE_LABEL, "E", ZestProperties.NODE_TOOLTIP,
						"Epsilon", ZestProperties.NODE_CSS_ID, id + "E")));

		// add nested graphs
		nodes.get(4).setNestedGraph(buildAC("c"));

		// create some edges between those nodes
		List<Edge> edges = new ArrayList<Edge>();
		edges.addAll(Arrays.asList(e(nodes.get(0), nodes.get(1)),
				e(nodes.get(1), nodes.get(2)), e(nodes.get(2), nodes.get(3)),
				e(nodes.get(3), nodes.get(4)), e(nodes.get(4), nodes.get(0))));

		// directed connections
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		attrs.put(ZestProperties.GRAPH_TYPE, ZestProperties.GRAPH_TYPE_DIRECTED);
		return new Graph(attrs, nodes, edges);
	}

	public static Graph createDefaultGraph() {
		// create nodes "0" to "9"
		List<org.eclipse.gef4.graph.Node> nodes = new ArrayList<org.eclipse.gef4.graph.Node>();
		nodes.addAll(Arrays.asList(
				n(ZestProperties.NODE_LABEL, "0", ZestProperties.NODE_TOOLTIP,
						"zero"),
				n(ZestProperties.NODE_LABEL, "1", ZestProperties.NODE_TOOLTIP,
						"one"),
				n(ZestProperties.NODE_LABEL, "2", ZestProperties.NODE_TOOLTIP,
						"two"),
				n(ZestProperties.NODE_LABEL, "3", ZestProperties.NODE_TOOLTIP,
						"three"),
				n(ZestProperties.NODE_LABEL, "4", ZestProperties.NODE_TOOLTIP,
						"four"),
				n(ZestProperties.NODE_LABEL, "5", ZestProperties.NODE_TOOLTIP,
						"five"),
				n(ZestProperties.NODE_LABEL, "6", ZestProperties.NODE_TOOLTIP,
						"six"),
				n(ZestProperties.NODE_LABEL, "7", ZestProperties.NODE_TOOLTIP,
						"seven"),
				n(ZestProperties.NODE_LABEL, "8", ZestProperties.NODE_TOOLTIP,
						"eight"),
				n(ZestProperties.NODE_LABEL, "9", ZestProperties.NODE_TOOLTIP,
						"nine")));

		// set nested graphs
		nodes.get(0).setNestedGraph(buildAC("a"));
		nodes.get(5).setNestedGraph(buildAE("b"));

		// create some edges between those nodes
		List<Edge> edges = new ArrayList<Edge>();
		edges.addAll(Arrays.asList(e(nodes.get(0), nodes.get(9)),
				e(nodes.get(1), nodes.get(8)), e(nodes.get(2), nodes.get(7)),
				e(nodes.get(3), nodes.get(6)), e(nodes.get(4), nodes.get(5)),
				e(nodes.get(0), nodes.get(4)), e(nodes.get(1), nodes.get(6)),
				e(nodes.get(2), nodes.get(8)), e(nodes.get(3), nodes.get(5)),
				e(nodes.get(4), nodes.get(7)), e(nodes.get(5), nodes.get(1))));

		// directed connections
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		attrs.put(ZestProperties.GRAPH_TYPE, ZestProperties.GRAPH_TYPE_DIRECTED);
		attrs.put(ZestProperties.GRAPH_LAYOUT, new SpringLayoutAlgorithm());
		return new Graph(attrs, nodes, edges);

	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	public ZestGraphExample() {
		super("GEF4 Zest Graph Example");
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
