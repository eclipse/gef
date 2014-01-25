/*******************************************************************************
 * Copyright (c) 2014 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 372365
 *******************************************************************************/
package org.eclipse.gef4.graph.tests;

import static org.eclipse.gef4.graph.Graph.Attr.GRAPH_TYPE;
import static org.eclipse.gef4.graph.Graph.Attr.ID;
import static org.eclipse.gef4.graph.Graph.Attr.LABEL;
import static org.junit.Assert.assertEquals;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.graph.internal.dot.ZestStyle;
import org.junit.Test;

/**
 * Sample usage for provisional GEF4 graph API.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class GraphSampleUsage {

	/* At the core of the API are graph representations in Graphviz DOT: */
	Graph graph = new Graph.Builder("graph { 1--2 ; 1--3 }").build();
	Graph digraph = new Graph.Builder("digraph { 1->2 ; 1->3 }").build();

	/* Nodes are basically key-value attribute mappings: */
	Node n1 = new Node.Builder().attr(LABEL, "1").attr(ID, "1").build();
	Node n2 = new Node.Builder().attr(LABEL, "2").attr(ID, "2").build();
	Node n3 = new Node.Builder().attr(LABEL, "3").attr(ID, "3").build();

	/* Edges connect a source and a target node: */
	Edge e1 = new Edge.Builder(n1, n2).build();
	Edge e2 = new Edge.Builder(n1, n3).build();

	@Test
	public void graph() {
		/* With the builders, we incrementally create the immutable objects: */
		Graph.Builder expected = new Graph.Builder();
		expected.attr(GRAPH_TYPE, ZestStyle.GRAPH_UNDIRECTED);
		expected.nodes(n1, n2, n3);
		expected.edges(e1, e2);
		assertEquals(expected.build(), graph);
	}

	@Test
	public void digraph() {
		/* The builders can be chained: */
		Graph expected = new Graph.Builder()
				.attr(GRAPH_TYPE, ZestStyle.GRAPH_DIRECTED) //
				.nodes(n1, n2, n3) //
				.edges(e1, e2).build();
		assertEquals(expected, digraph);
	}

	@Test
	public void attrs() {
		/* Like nodes, graphs and edges have attributes, too: */
		String attrGraph = "graph { graph[g_attr=g1] 1--2[label=e1] ; 1--3 }";
		Graph graph = new Graph.Builder(attrGraph).build();
		Graph.Builder expected = new Graph.Builder();
		expected.attr(GRAPH_TYPE, ZestStyle.GRAPH_UNDIRECTED);
		expected.attr("g_attr", "g1");
		expected.edges(new Edge.Builder(n1, n2).attr(LABEL, "e1").build(), e2);
		expected.nodes(n1, n2, n3).build();
		assertEquals(expected.build(), graph);
	}

	@Test
	public void dot() {
		/* The initial graph builder can be modified using DOT snippets: */
		Graph.Builder graph = new Graph.Builder("digraph{1->2}").dot("2->3");
		/* The snippets can contain DOT node and edge attributes: */
		graph.dot("node[label=zested]; edge[style=dashed]; 3->5; 4->6");
		/* The final graph contains all the nodes and edges added: */
		assertEquals("Graph {6 nodes, 4 edges}", graph.build().toString());
	}
}
