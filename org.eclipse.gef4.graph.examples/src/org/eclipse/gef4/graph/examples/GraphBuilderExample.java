/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.graph.examples;

import org.eclipse.gef4.dot.DotProperties;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;

public final class GraphBuilderExample {

	public static void main(final String[] args) {
		/* With the builders, we incrementally create the objects: */
		/* Nodes are basically key-value attribute mappings: */
		Node n1 = new Node.Builder().attr(DotProperties.NODE_LABEL, "1")
				.attr(DotProperties.NODE_ID, "1").build();
		Node n2 = new Node.Builder().attr(DotProperties.NODE_LABEL, "2")
				.attr(DotProperties.NODE_ID, "2").build();
		Node n3 = new Node.Builder().attr(DotProperties.NODE_LABEL, "3")
				.attr(DotProperties.NODE_ID, "3").build();
		/* Edges connect a source and a target node: */
		Edge e1 = new Edge.Builder(n1, n2).build();
		Edge e2 = new Edge.Builder(n1, n3).build();

		// Nodes, edges, and attributes can be added in arbitrary order */
		Graph.Builder graphBuilder = new Graph.Builder();
		graphBuilder.attr(DotProperties.GRAPH_TYPE,
				DotProperties.GRAPH_TYPE_UNDIRECTED);
		graphBuilder.nodes(n1, n2, n3);
		graphBuilder.edges(e1, e2);
		Graph g1 = graphBuilder.build();

		/* Like nodes, graphs and edges have attributes, too: */
		graphBuilder = new Graph.Builder();
		graphBuilder.attr(DotProperties.GRAPH_TYPE,
				DotProperties.GRAPH_TYPE_UNDIRECTED);
		graphBuilder.attr("g_attr", "g1");
		graphBuilder.edges(new Edge.Builder(n1, n2)
				.attr(DotProperties.EDGE_LABEL, "e1").build(), e2);
		Graph g2 = graphBuilder.nodes(n1, n2, n3).build();

		/* The builders can be chained: */
		Graph g3 = new Graph.Builder()
				.attr(DotProperties.GRAPH_TYPE,
						DotProperties.GRAPH_TYPE_DIRECTED)
				.nodes(n1, n2, n3).edges(e1, e2).build();
	}
}
