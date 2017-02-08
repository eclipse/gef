/*******************************************************************************
 * Copyright (c) 2009, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation (see bug #277380)
 *     Tamas Miklossy (itemis AG) - Add support for all dot attributes (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.language.dot.GraphType;
import org.eclipse.gef.dot.internal.language.layout.Layout;
import org.eclipse.gef.dot.internal.language.style.EdgeStyle;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;

/**
 * Util class for different tests.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class DotTestUtils {

	public static final String RESOURCES_TESTS = "resources/"; //$NON-NLS-1$

	private DotTestUtils() { /* Enforce non-instantiability */
	}

	public static Graph getLabeledGraph() {
		/* Global settings: */
		Graph.Builder graph = new Graph.Builder()
				.attr(DotAttributes::_setName, "LabeledGraph")
				.attr(DotAttributes::_setType, GraphType.DIGRAPH);

		/* Nodes: */
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.attr(DotAttributes::setLabel, "one \"1\"").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.attr(DotAttributes::setLabel, "two").buildNode();
		Node n3 = new Node.Builder().attr(DotAttributes::_setName, "3") //$NON-NLS-1$
				.buildNode();
		Node n4 = new Node.Builder().attr(DotAttributes::_setName, "4") //$NON-NLS-1$
				.buildNode();

		/* Connection from n1 to n2: */
		Edge e1 = new Edge.Builder(n1, n2).attr(DotAttributes::setLabel, "+1")
				.buildEdge();

		/* Connection from n1 to n3: */
		Edge e2 = new Edge.Builder(n1, n3).attr(DotAttributes::setLabel, "+2")
				.buildEdge();

		/* Connection from n3 to n4: */
		Edge e3 = new Edge.Builder(n3, n4).buildEdge();

		return graph.nodes(n1, n2, n3, n4).edges(e1, e2, e3).build();
	}

	public static Graph getSimpleDiGraph() {

		/* Global settings, here we set the directed property: */
		Graph.Builder graph = new Graph.Builder()
				.attr(DotAttributes::_setName, "SimpleDigraph")
				.attr(DotAttributes::_setType, GraphType.DIGRAPH);

		/* Nodes: */
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.buildNode();
		Node n3 = new Node.Builder().attr(DotAttributes::_setName, "3") //$NON-NLS-1$
				.buildNode();

		/* Connection from n1 to n2: */
		Edge e1 = new Edge.Builder(n1, n2).buildEdge();

		/* Connection from n2 to n3: */
		Edge e2 = new Edge.Builder(n2, n3).buildEdge();

		return graph.nodes(n1, n2, n3).edges(e1, e2).build();
	}

	public static Graph getSimpleGraph() {
		/* Set a layout algorithm: */
		Graph.Builder graph = new Graph.Builder()
				.attr(DotAttributes::_setName, "SimpleGraph")
				.attr(DotAttributes::_setType, GraphType.GRAPH);

		/* Set the nodes: */
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.buildNode();
		Node n3 = new Node.Builder().attr(DotAttributes::_setName, "3") //$NON-NLS-1$
				.buildNode();

		/* Connection from n1 to n2: */
		Edge e1 = new Edge.Builder(n1, n2).buildEdge();

		/* Connection from n1 to n3: */
		Edge e2 = new Edge.Builder(n1, n3).buildEdge();

		return graph.nodes(n1, n2, n3).edges(e1, e2).build();
	}

	public static Graph getStyledGraph() {
		/* Global properties: */
		Graph.Builder graph = new Graph.Builder()
				.attr(DotAttributes::_setName, "StyledGraph")
				.attr(DotAttributes::_setType, GraphType.DIGRAPH)
				.attr(DotAttributes::setLayoutParsed, Layout.DOT);

		/* Nodes: */
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.buildNode();
		Node n3 = new Node.Builder().attr(DotAttributes::_setName, "3") //$NON-NLS-1$
				.buildNode();
		Node n4 = new Node.Builder().attr(DotAttributes::_setName, "4") //$NON-NLS-1$
				.buildNode();
		Node n5 = new Node.Builder().attr(DotAttributes::_setName, "5") //$NON-NLS-1$
				.buildNode();

		/* Connection from n1 to n2: */
		Edge e1 = new Edge.Builder(n1, n2)
				.attr(DotAttributes::setStyle, EdgeStyle.DASHED.toString())
				.buildEdge();

		/* Connection from n2 to n3: */
		Edge e2 = new Edge.Builder(n2, n3)
				.attr(DotAttributes::setStyle, EdgeStyle.DOTTED.toString())
				.buildEdge();

		/* Connection from n3 to n4: */
		Edge e3 = new Edge.Builder(n3, n4)
				.attr(DotAttributes::setStyle, EdgeStyle.DASHED.toString())
				.buildEdge();

		/* Connection from n3 to n5: */
		Edge e4 = new Edge.Builder(n3, n5)
				.attr(DotAttributes::setStyle, EdgeStyle.DASHED.toString())
				.buildEdge();

		Edge e5 = new Edge.Builder(n4, n5)
				.attr(DotAttributes::setStyle, EdgeStyle.SOLID.toString())
				.buildEdge();

		return graph.nodes(n1, n2, n3, n4, n5).edges(e1, e2, e3, e4, e5)
				.build();
	}

	public static Graph getClusteredGraph() {
		/*
		 * digraph { subgraph cluster1 { a; b; a -> b; } subgraph cluster2 { p;
		 * q; r; s; t; p -> q; q -> r; r -> s; s -> t; t -> p; } b -> q; t -> a;
		 * }
		 */
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.DIGRAPH);

		/* Nodes: */
		Node cluster1 = new Node.Builder().buildNode();
		Node a = new Node.Builder().attr(DotAttributes::_setName, "a") //$NON-NLS-1$
				.buildNode();
		Node b = new Node.Builder().attr(DotAttributes::_setName, "b") //$NON-NLS-1$
				.buildNode();
		cluster1.setNestedGraph(
				new Graph.Builder().attr(DotAttributes::_setName, "cluster1")
						.nodes(a, b).edge(a, b).build());

		Node cluster2 = new Node.Builder().buildNode();
		Node p = new Node.Builder().attr(DotAttributes::_setName, "p") //$NON-NLS-1$
				.buildNode();
		Node q = new Node.Builder().attr(DotAttributes::_setName, "q") //$NON-NLS-1$
				.buildNode();
		Node r = new Node.Builder().attr(DotAttributes::_setName, "r") //$NON-NLS-1$
				.buildNode();
		Node s = new Node.Builder().attr(DotAttributes::_setName, "s") //$NON-NLS-1$
				.buildNode();
		Node t = new Node.Builder().attr(DotAttributes::_setName, "t") //$NON-NLS-1$
				.buildNode();
		cluster2.setNestedGraph(
				new Graph.Builder().attr(DotAttributes::_setName, "cluster2")
						.nodes(p, q, r, s, t).edge(p, q).edge(q, r).edge(r, s)
						.edge(s, t).edge(t, p).build());

		return graph.nodes(cluster1, cluster2).edge(b, q).edge(t, a).build();
	}
}
