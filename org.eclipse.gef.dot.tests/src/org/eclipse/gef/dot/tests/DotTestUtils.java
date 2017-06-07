/*******************************************************************************
 * Copyright (c) 2009, 2017 itemis AG and others.
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

	public static String[] expectedDotColorSchemes = { "x11", "svg", "accent3",
			"accent4", "accent5", "accent6", "accent7", "accent8", "blues3",
			"blues4", "blues5", "blues6", "blues7", "blues8", "blues9",
			"brbg10", "brbg11", "brbg3", "brbg4", "brbg5", "brbg6", "brbg7",
			"brbg8", "brbg9", "bugn3", "bugn4", "bugn5", "bugn6", "bugn7",
			"bugn8", "bugn9", "bupu3", "bupu4", "bupu5", "bupu6", "bupu7",
			"bupu8", "bupu9", "dark23", "dark24", "dark25", "dark26", "dark27",
			"dark28", "gnbu3", "gnbu4", "gnbu5", "gnbu6", "gnbu7", "gnbu8",
			"gnbu9", "greens3", "greens4", "greens5", "greens6", "greens7",
			"greens8", "greens9", "greys3", "greys4", "greys5", "greys6",
			"greys7", "greys8", "greys9", "oranges3", "oranges4", "oranges5",
			"oranges6", "oranges7", "oranges8", "oranges9", "orrd3", "orrd4",
			"orrd5", "orrd6", "orrd7", "orrd8", "orrd9", "paired10", "paired11",
			"paired12", "paired3", "paired4", "paired5", "paired6", "paired7",
			"paired8", "paired9", "pastel13", "pastel14", "pastel15",
			"pastel16", "pastel17", "pastel18", "pastel19", "pastel23",
			"pastel24", "pastel25", "pastel26", "pastel27", "pastel28",
			"piyg10", "piyg11", "piyg3", "piyg4", "piyg5", "piyg6", "piyg7",
			"piyg8", "piyg9", "prgn10", "prgn11", "prgn3", "prgn4", "prgn5",
			"prgn6", "prgn7", "prgn8", "prgn9", "pubu3", "pubu4", "pubu5",
			"pubu6", "pubu7", "pubu8", "pubu9", "pubugn3", "pubugn4", "pubugn5",
			"pubugn6", "pubugn7", "pubugn8", "pubugn9", "puor10", "puor11",
			"puor3", "puor4", "puor5", "puor6", "puor7", "puor8", "puor9",
			"purd3", "purd4", "purd5", "purd6", "purd7", "purd8", "purd9",
			"purples3", "purples4", "purples5", "purples6", "purples7",
			"purples8", "purples9", "rdbu10", "rdbu11", "rdbu3", "rdbu4",
			"rdbu5", "rdbu6", "rdbu7", "rdbu8", "rdbu9", "rdgy10", "rdgy11",
			"rdgy3", "rdgy4", "rdgy5", "rdgy6", "rdgy7", "rdgy8", "rdgy9",
			"rdpu3", "rdpu4", "rdpu5", "rdpu6", "rdpu7", "rdpu8", "rdpu9",
			"rdylbu10", "rdylbu11", "rdylbu3", "rdylbu4", "rdylbu5", "rdylbu6",
			"rdylbu7", "rdylbu8", "rdylbu9", "rdylgn10", "rdylgn11", "rdylgn3",
			"rdylgn4", "rdylgn5", "rdylgn6", "rdylgn7", "rdylgn8", "rdylgn9",
			"reds3", "reds4", "reds5", "reds6", "reds7", "reds8", "reds9",
			"set13", "set14", "set15", "set16", "set17", "set18", "set19",
			"set23", "set24", "set25", "set26", "set27", "set28", "set310",
			"set311", "set312", "set33", "set34", "set35", "set36", "set37",
			"set38", "set39", "spectral10", "spectral11", "spectral3",
			"spectral4", "spectral5", "spectral6", "spectral7", "spectral8",
			"spectral9", "ylgn3", "ylgn4", "ylgn5", "ylgn6", "ylgn7", "ylgn8",
			"ylgn9", "ylgnbu3", "ylgnbu4", "ylgnbu5", "ylgnbu6", "ylgnbu7",
			"ylgnbu8", "ylgnbu9", "ylorbr3", "ylorbr4", "ylorbr5", "ylorbr6",
			"ylorbr7", "ylorbr8", "ylorbr9", "ylorrd3", "ylorrd4", "ylorrd5",
			"ylorrd6", "ylorrd7", "ylorrd8", "ylorrd9" };
}
