/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.graph.tests;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Comparator;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.junit.Test;

import javafx.collections.ObservableList;

public class GraphBuilderTests {

	@Test
	public void buildGraph() {
		// build graph
		Graph graph = new Graph.Builder().attr("graph_attr", "graph_attr_value")//
				.node("n1")//
				.attr("label", "n1")//
				.attr("node_attr", "n1_node_attr_value")//
				.node("n2")//
				.attr("label", "n2")//
				.attr("node_attr", "n2_node_attr_value")//
				.node("n3")//
				.attr("label", "n3")//
				.attr("node_attr", "n3_node_attr_value")//
				.edge("n1", "n2").attr("label", "n1->n2")//
				.attr("edge_attr", "n1->n2_edge_attr_value")//
				.edge("n1", "n3")//
				.attr("label", "n1->n3")//
				.attr("edge_attr", "n1->n3_edge_attr_value")//
				.build();
		assertEquals(graph.getAttributes().get("graph_attr"),
				"graph_attr_value");
		// sort nodes, so we can safely access them via indices
		ObservableList<Node> nodes = graph.getNodes();
		Collections.sort(nodes, new Comparator<Node>() {

			@Override
			public int compare(Node o1, Node o2) {
				return ((String) o1.attributesProperty().get("label"))
						.compareTo(
								(String) o2.attributesProperty().get("label"));
			}
		});
		// sort edges, so we can safely access them via indices
		ObservableList<Edge> edges = graph.getEdges();
		Collections.sort(edges, new Comparator<Edge>() {

			@Override
			public int compare(Edge o1, Edge o2) {
				return ((String) o1.attributesProperty().get("label"))
						.compareTo(
								(String) o2.attributesProperty().get("label"));
			}
		});

		assertEquals(3, nodes.size());
		assertEquals(2, edges.size());
		assertEquals(nodes.get(0).getAttributes().get("label"), "n1");
		assertEquals(nodes.get(0).getAttributes().get("node_attr"),
				"n1_node_attr_value");
		assertEquals(nodes.get(1).getAttributes().get("label"), "n2");
		assertEquals(nodes.get(1).getAttributes().get("node_attr"),
				"n2_node_attr_value");
		assertEquals(nodes.get(2).getAttributes().get("label"), "n3");
		assertEquals(nodes.get(2).getAttributes().get("node_attr"),
				"n3_node_attr_value");
		assertEquals(edges.get(0).getAttributes().get("label"), "n1->n2");
		assertEquals(edges.get(0).getAttributes().get("edge_attr"),
				"n1->n2_edge_attr_value");
		assertEquals(edges.get(0).getTarget().getAttributes().get("label"),
				"n2");
		assertEquals(edges.get(0).getSource().getAttributes().get("label"),
				"n1");
		assertEquals(edges.get(0).getTarget().getAttributes().get("label"),
				"n2");
		assertEquals(edges.get(1).getAttributes().get("label"), "n1->n3");
		assertEquals(edges.get(1).getAttributes().get("edge_attr"),
				"n1->n3_edge_attr_value");
		assertEquals(edges.get(1).getSource().getAttributes().get("label"),
				"n1");
		assertEquals(edges.get(1).getTarget().getAttributes().get("label"),
				"n3");
	}

	@Test
	public void buildWithExistingNodes() {
		Node n1 = new Node.Builder().attr("label", "n1")//
				.attr("node_attr", "n1_node_attr_value").buildNode();
		Node n2 = new Node.Builder().attr("label", "n2")//
				.attr("node_attr", "n2_node_attr_value").buildNode();
		Node n3 = new Node.Builder().attr("label", "n3")//
				.attr("node_attr", "n3_node_attr_value").buildNode();
		Graph graph = new Graph.Builder().attr("graph_attr", "graph_attr_value")
				.nodes(n1, n2, n3)//
				.edge(n1, n2)//
				.attr("label", "n1->n2")//
				.attr("edge_attr", "n1->n2_edge_attr_value")//
				.edge(n1, n3)//
				.attr("label", "n1->n3")//
				.attr("edge_attr", "n1->n3_edge_attr_value")//
				.build();
		assertEquals(graph.getAttributes().get("graph_attr"),
				"graph_attr_value");
		assertEquals(3, graph.getNodes().size());
		assertEquals(2, graph.getEdges().size());
		assertEquals(graph.getNodes().get(0).getAttributes().get("label"),
				"n1");
		assertEquals(graph.getNodes().get(0).getAttributes().get("node_attr"),
				"n1_node_attr_value");
		assertEquals(graph.getNodes().get(1).getAttributes().get("label"),
				"n2");
		assertEquals(graph.getNodes().get(1).getAttributes().get("node_attr"),
				"n2_node_attr_value");
		assertEquals(graph.getNodes().get(2).getAttributes().get("label"),
				"n3");
		assertEquals(graph.getNodes().get(2).getAttributes().get("node_attr"),
				"n3_node_attr_value");
		assertEquals(graph.getEdges().get(0).getAttributes().get("label"),
				"n1->n2");
		assertEquals(graph.getEdges().get(0).getAttributes().get("edge_attr"),
				"n1->n2_edge_attr_value");
		assertEquals(graph.getEdges().get(0).getTarget().getAttributes()
				.get("label"), "n2");
		assertEquals(graph.getEdges().get(0).getSource().getAttributes()
				.get("label"), "n1");
		assertEquals(graph.getEdges().get(0).getTarget().getAttributes()
				.get("label"), "n2");
		assertEquals(graph.getEdges().get(1).getAttributes().get("label"),
				"n1->n3");
		assertEquals(graph.getEdges().get(1).getAttributes().get("edge_attr"),
				"n1->n3_edge_attr_value");
		assertEquals(graph.getEdges().get(1).getSource().getAttributes()
				.get("label"), "n1");
		assertEquals(graph.getEdges().get(1).getTarget().getAttributes()
				.get("label"), "n3");
	}
}
