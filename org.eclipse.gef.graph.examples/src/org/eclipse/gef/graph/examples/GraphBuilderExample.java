/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
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
package org.eclipse.gef.graph.examples;

import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.language.dot.GraphType;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;

public final class GraphBuilderExample {

	public static void main(final String[] args) {
		// Nodes, edges, and attributes can be added in arbitrary order */
		Graph g1 = new Graph.Builder()
				.attr(DotAttributes::_setType, GraphType.DIGRAPH)//
				.node("n1")//
				.attr(DotAttributes::setLabel, "1")//
				.attr(DotAttributes::setId, "1")//
				.node("n2")//
				.attr(DotAttributes::setLabel, "2")//
				.attr(DotAttributes::setId, "2")//
				.node("n3")//
				.attr(DotAttributes::setLabel, "3")//
				.attr(DotAttributes::setId, "3")//
				.edge("n1", "n2")//
				.edge("n1", "n3")//
				.build();

		/* Like nodes, graphs and edges have attributes, too: */
		Graph g2 = new Graph.Builder()
				.attr(DotAttributes::_setType, GraphType.DIGRAPH)//
				.attr("g_attr", "g1").node("n1")//
				.attr(DotAttributes::setLabel, "1")//
				.attr(DotAttributes::setId, "1")//
				.node("n2")//
				.attr(DotAttributes::setLabel, "2")//
				.attr(DotAttributes::setId, "2")//
				.node("n3")//
				.attr(DotAttributes::setLabel, "3")//
				.attr(DotAttributes::setId, "3")//
				.edge("n1", "n2").attr(DotAttributes::setLabel, "e1")
				.edge("n1", "n3").build();

		/* Builders can also be used without being chained */
		Node n1 = new Node.Builder().attr(DotAttributes::setLabel, "1")//
				.attr(DotAttributes::setId, "1").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::setLabel, "2")//
				.attr(DotAttributes::setId, "2").buildNode();
		Node n3 = new Node.Builder().attr(DotAttributes::setLabel, "3")//
				.attr(DotAttributes::setId, "3").buildNode();
		Graph g3 = new Graph.Builder()
				.attr(DotAttributes::_setType, GraphType.DIGRAPH)
				.nodes(n1, n2, n3)//
				.edge(n1, n2)//
				.edge(n1, n3)//
				.build();
	}
}
