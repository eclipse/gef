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

import org.eclipse.gef4.dot.internal.DotAttributes;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;

public final class GraphBuilderExample {

	public static void main(final String[] args) {
		// Nodes, edges, and attributes can be added in arbitrary order */
		Graph g1 = new Graph.Builder()
				.attr(DotAttributes.GRAPH_TYPE,
						DotAttributes.GRAPH_TYPE_UNDIRECTED)//
				.node("n1")//
				.attr(DotAttributes.NODE_LABEL, "1")//
				.attr(DotAttributes.NODE_ID, "1")//
				.node("n2")//
				.attr(DotAttributes.NODE_LABEL, "2")//
				.attr(DotAttributes.NODE_ID, "2")//
				.node("n3")//
				.attr(DotAttributes.NODE_LABEL, "3")//
				.attr(DotAttributes.NODE_ID, "3")//
				.edge("n1", "n2")//
				.edge("n1", "n3")//
				.build();

		/* Like nodes, graphs and edges have attributes, too: */
		Graph g2 = new Graph.Builder()
				.attr(DotAttributes.GRAPH_TYPE,
						DotAttributes.GRAPH_TYPE_UNDIRECTED)//
				.attr("g_attr", "g1").node("n1")//
				.attr(DotAttributes.NODE_LABEL, "1")//
				.attr(DotAttributes.NODE_ID, "1")//
				.node("n2")//
				.attr(DotAttributes.NODE_LABEL, "2")//
				.attr(DotAttributes.NODE_ID, "2")//
				.node("n3")//
				.attr(DotAttributes.NODE_LABEL, "3")//
				.attr(DotAttributes.NODE_ID, "3")//
				.edge("n1", "n2").attr(DotAttributes.EDGE_LABEL, "e1")
				.edge("n1", "n3").build();

		/* Builders can also be used without being chained */
		Node n1 = new Node.Builder().attr(DotAttributes.NODE_LABEL, "1")//
				.attr(DotAttributes.NODE_ID, "1").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes.NODE_LABEL, "2")//
				.attr(DotAttributes.NODE_ID, "2").buildNode();
		Node n3 = new Node.Builder().attr(DotAttributes.NODE_LABEL, "3")//
				.attr(DotAttributes.NODE_ID, "3").buildNode();
		Graph g3 = new Graph.Builder()
				.attr(DotAttributes.GRAPH_TYPE,
						DotAttributes.GRAPH_TYPE_UNDIRECTED)
				.nodes(n1, n2, n3)//
				.edge(n1, n2)//
				.edge(n1, n3)//
				.build();
	}
}
