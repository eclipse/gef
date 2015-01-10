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
import org.eclipse.gef4.graph.Graph.Attr.Key;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.examples.AbstractZestExample;

import com.google.inject.Module;

public class ZestGraphExample extends AbstractZestExample {

	private static Graph buildAC() {
		// create nodes "A" to "C"
		List<org.eclipse.gef4.graph.Node> nodes = new ArrayList<org.eclipse.gef4.graph.Node>();
		nodes.addAll(Arrays.asList(n(Key.LABEL.toString(), "A", "tooltip", "Alpha"),
				n(Key.LABEL.toString(), "B", "tooltip", "Beta"), n(Key.LABEL.toString(), "C", "tooltip", "Gamma")));

		// create some edges between those nodes
		List<Edge> edges = new ArrayList<Edge>();
		edges.addAll(Arrays.asList(e(nodes.get(0), nodes.get(1)), e(nodes.get(1), nodes.get(2)),
				e(nodes.get(2), nodes.get(0))));

		// directed connections
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		attrs.put(Graph.Attr.Key.GRAPH_TYPE.toString(), Graph.Attr.Value.GRAPH_DIRECTED);
		return new Graph(attrs, nodes, edges);
	}

	public static Graph createDefaultGraph() {
		// create nodes "0" to "9"
		List<org.eclipse.gef4.graph.Node> nodes = new ArrayList<org.eclipse.gef4.graph.Node>();
		nodes.addAll(Arrays.asList(n(Key.LABEL.toString(), "0", "tooltip", "zero"),
				n(Key.LABEL.toString(), "1", "tooltip", "one"), n(Key.LABEL.toString(), "2", "tooltip", "two"),
				n(Key.LABEL.toString(), "3", "tooltip", "three"), n(Key.LABEL.toString(), "4", "tooltip", "four"),
				n(Key.LABEL.toString(), "5", "tooltip", "five"), n(Key.LABEL.toString(), "6", "tooltip", "six"),
				n(Key.LABEL.toString(), "7", "tooltip", "seven"), n(Key.LABEL.toString(), "8", "tooltip", "eight"),
				n(Key.LABEL.toString(), "9", "tooltip", "nine")));

		// set nested graph for node "0"
		nodes.get(0).setNestedGraph(buildAC());

		// create some edges between those nodes
		List<Edge> edges = new ArrayList<Edge>();
		edges.addAll(Arrays.asList(e(nodes.get(0), nodes.get(9)), e(nodes.get(1), nodes.get(8)),
				e(nodes.get(2), nodes.get(7)), e(nodes.get(3), nodes.get(6)), e(nodes.get(4), nodes.get(5)),
				e(nodes.get(0), nodes.get(4)), e(nodes.get(1), nodes.get(6)), e(nodes.get(2), nodes.get(8)),
				e(nodes.get(3), nodes.get(5)), e(nodes.get(4), nodes.get(7)), e(nodes.get(5), nodes.get(1))));

		// directed connections
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		attrs.put(Graph.Attr.Key.GRAPH_TYPE.toString(), Graph.Attr.Value.GRAPH_DIRECTED);
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
	protected LayoutAlgorithm createLayoutAlgorithm() {
		return new SpringLayoutAlgorithm();
	}

	@Override
	protected Module createModule() {
		return new ZestGraphExampleModule();
	}

}
