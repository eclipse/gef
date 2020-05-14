/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.graph.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Graph.Builder;
import org.eclipse.gef.graph.Node;
import org.junit.Test;

import javafx.collections.ObservableList;

public class GraphBuilderTests {

	public static class Semantic {
		public String data;

		public Semantic(String data) {
			this.data = data;
		}
	}

	private void addNodeBuilders(Graph.Builder graphBuilder, int count,
			int startNumber) {
		for (int i = 0; i < count; i++) {
			graphBuilder.node().attr("label", "" + (startNumber + i));
		}
	}

	private void addNodes(Graph.Builder graphBuilder, int count,
			int startNumber) {
		List<Node> nodes = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			nodes.add(new Node.Builder().attr("label", "" + (startNumber + i))
					.buildNode());
		}
		graphBuilder.nodes(nodes);
	}

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
	public void buildSemanticTwice() {
		Builder b = new Graph.Builder();
		Semantic s1 = new Semantic("a");
		b.node(s1).attr("test", "value");
		b.node(s1).attr("test2", "value2");
		Graph graph = b.build();
		assertEquals(1, graph.getNodes().size());
		assertEquals(0, graph.getEdges().size());
		assertEquals("value",
				graph.getNodes().get(0).getAttributes().get("test"));
		assertEquals("value2",
				graph.getNodes().get(0).getAttributes().get("test2"));
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

	/**
	 * Test that values are overwritten in the order they are specified in the
	 * builder.
	 */
	@Test
	public void overrideAttributeWithSetter() {
		Builder b = new Graph.Builder();
		b.attr(new BiConsumer<Graph, String>() {

			@Override
			public void accept(Graph t, String u) {
				t.attributesProperty().put("test", u);
			}
		}, "v1");
		assertEquals("v1", b.build().attributesProperty().get("test"));
		b.attr("test", "v2");
		assertEquals("v2", b.build().attributesProperty().get("test"));
		b.attr(new BiConsumer<Graph, String>() {

			@Override
			public void accept(Graph t, String u) {
				t.attributesProperty().put("test", u);
			}
		}, "v3");
		assertEquals("v3", b.build().attributesProperty().get("test"));
		b.attr(new BiConsumer<Graph, String>() {

			@Override
			public void accept(Graph t, String u) {
				t.attributesProperty().put("test", u);
			}
		}, "v4");
		assertEquals("v4", b.build().attributesProperty().get("test"));
	}

	@Test
	public void preserveNodeOrder() {
		// first nodes, then builders
		Builder gb = new Graph.Builder();
		addNodes(gb, 5, 1);
		addNodeBuilders(gb, 5, 6);
		Graph g = gb.build();
		assertEquals(10, g.getNodes().size());
		for (int i = 0; i < g.getNodes().size(); i++) {
			assertEquals(new Integer(i + 1).toString(),
					g.getNodes().get(i).getAttributes().get("label"));
		}

		// first builders, then nodes
		gb = new Graph.Builder();
		addNodeBuilders(gb, 5, 1);
		addNodes(gb, 5, 6);
		g = gb.build();
		assertEquals(10, g.getNodes().size());
		for (int i = 0; i < g.getNodes().size(); i++) {
			assertEquals(new Integer(i + 1).toString(),
					g.getNodes().get(i).getAttributes().get("label"));
		}

		// a few nodes, all builders, rest nodes
		gb = new Graph.Builder();
		addNodes(gb, 2, 1);
		addNodeBuilders(gb, 5, 3);
		addNodes(gb, 3, 8);
		g = gb.build();
		assertEquals(10, g.getNodes().size());
		for (int i = 0; i < g.getNodes().size(); i++) {
			assertEquals(new Integer(i + 1).toString(),
					g.getNodes().get(i).getAttributes().get("label"));
		}

		// a few builders, all nodes, rest builders
		gb = new Graph.Builder();
		addNodeBuilders(gb, 2, 1);
		addNodes(gb, 5, 3);
		addNodeBuilders(gb, 3, 8);
		g = gb.build();
		assertEquals(10, g.getNodes().size());
		for (int i = 0; i < g.getNodes().size(); i++) {
			assertEquals(new Integer(i + 1).toString(),
					g.getNodes().get(i).getAttributes().get("label"));
		}

		// alternating nodes and builders
		gb = new Graph.Builder();
		for (int i = 0; i < 10; i++) {
			if (i % 2 == 0) {
				addNodes(gb, 1, i + 1);
			} else {
				addNodeBuilders(gb, 1, i + 1);
			}
		}
		g = gb.build();
		assertEquals(10, g.getNodes().size());
		for (int i = 0; i < g.getNodes().size(); i++) {
			assertEquals(new Integer(i + 1).toString(),
					g.getNodes().get(i).getAttributes().get("label"));
		}
	}

}
