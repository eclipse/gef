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
				.attr(DotAttributes._NAME__GNE, "LabeledGraph")
				.attr(DotAttributes._TYPE__G, DotAttributes._TYPE__G__DIGRAPH);

		/* Nodes: */
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.attr(DotAttributes.LABEL__GNE, "one \"1\"").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.attr(DotAttributes.LABEL__GNE, "two").buildNode();
		Node n3 = new Node.Builder().attr(DotAttributes._NAME__GNE, "3") //$NON-NLS-1$
				.buildNode();
		Node n4 = new Node.Builder().attr(DotAttributes._NAME__GNE, "4") //$NON-NLS-1$
				.buildNode();

		/* Connection from n1 to n2: */
		Edge e1 = new Edge.Builder(n1, n2)
				.attr(DotAttributes._NAME__GNE, "1->2") //$NON-NLS-1$
				.attr(DotAttributes.LABEL__GNE, "+1").buildEdge();

		/* Connection from n1 to n3: */
		Edge e2 = new Edge.Builder(n1, n3)
				.attr(DotAttributes._NAME__GNE, "1->3") //$NON-NLS-1$
				.attr(DotAttributes.LABEL__GNE, "+2").buildEdge();

		/* Connection from n3 to n4: */
		Edge e3 = new Edge.Builder(n3, n4)
				.attr(DotAttributes._NAME__GNE, "3->4").buildEdge();

		return graph.nodes(n1, n2, n3, n4).edges(e1, e2, e3).build();
	}

	public static Graph getSimpleDiGraph() {

		/* Global settings, here we set the directed property: */
		Graph.Builder graph = new Graph.Builder()
				.attr(DotAttributes._NAME__GNE, "SimpleDigraph")
				.attr(DotAttributes._TYPE__G, DotAttributes._TYPE__G__DIGRAPH);

		/* Nodes: */
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.buildNode();
		Node n3 = new Node.Builder().attr(DotAttributes._NAME__GNE, "3") //$NON-NLS-1$
				.buildNode();

		/* Connection from n1 to n2: */
		Edge e1 = new Edge.Builder(n1, n2)
				.attr(DotAttributes._NAME__GNE, "1->2").buildEdge();

		/* Connection from n2 to n3: */
		Edge e2 = new Edge.Builder(n2, n3)
				.attr(DotAttributes._NAME__GNE, "2->3").buildEdge();

		return graph.nodes(n1, n2, n3).edges(e1, e2).build();
	}

	public static Graph getSimpleGraph() {
		/* Set a layout algorithm: */
		Graph.Builder graph = new Graph.Builder()
				.attr(DotAttributes._NAME__GNE, "SimpleGraph")
				.attr(DotAttributes._TYPE__G, DotAttributes._TYPE__G__GRAPH);

		/* Set the nodes: */
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.buildNode();
		Node n3 = new Node.Builder().attr(DotAttributes._NAME__GNE, "3") //$NON-NLS-1$
				.buildNode();

		/* Connection from n1 to n2: */
		Edge e1 = new Edge.Builder(n1, n2)
				.attr(DotAttributes._NAME__GNE, "1--2").buildEdge();

		/* Connection from n1 to n3: */
		Edge e2 = new Edge.Builder(n1, n3)
				.attr(DotAttributes._NAME__GNE, "1--3").buildEdge();

		return graph.nodes(n1, n2, n3).edges(e1, e2).build();
	}

	public static Graph getStyledGraph() {
		/* Global properties: */
		Graph.Builder graph = new Graph.Builder()
				.attr(DotAttributes._NAME__GNE, "StyledGraph")
				.attr(DotAttributes._TYPE__G, DotAttributes._TYPE__G__DIGRAPH)
				.attr(DotAttributes.LAYOUT__G, Layout.DOT.toString());

		/* Nodes: */
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.buildNode();
		Node n3 = new Node.Builder().attr(DotAttributes._NAME__GNE, "3") //$NON-NLS-1$
				.buildNode();
		Node n4 = new Node.Builder().attr(DotAttributes._NAME__GNE, "4") //$NON-NLS-1$
				.buildNode();
		Node n5 = new Node.Builder().attr(DotAttributes._NAME__GNE, "5") //$NON-NLS-1$
				.buildNode();

		/* Connection from n1 to n2: */
		Edge e1 = new Edge.Builder(n1, n2)
				.attr(DotAttributes._NAME__GNE, "1->2")
				.attr(DotAttributes.STYLE__GNE, EdgeStyle.DASHED.toString())
				.buildEdge();

		/* Connection from n2 to n3: */
		Edge e2 = new Edge.Builder(n2, n3)
				.attr(DotAttributes._NAME__GNE, "2->3")
				.attr(DotAttributes.STYLE__GNE, EdgeStyle.DOTTED.toString())
				.buildEdge();

		/* Connection from n3 to n4: */
		Edge e3 = new Edge.Builder(n3, n4)
				.attr(DotAttributes._NAME__GNE, "3->4")
				.attr(DotAttributes.STYLE__GNE, EdgeStyle.DASHED.toString())
				.buildEdge();

		/* Connection from n3 to n5: */
		Edge e4 = new Edge.Builder(n3, n5)
				.attr(DotAttributes._NAME__GNE, "3->5")
				.attr(DotAttributes.STYLE__GNE, EdgeStyle.DASHED.toString())
				.buildEdge();

		Edge e5 = new Edge.Builder(n4, n5)
				.attr(DotAttributes._NAME__GNE, "4->5")
				.attr(DotAttributes.STYLE__GNE, EdgeStyle.SOLID.toString())
				.buildEdge();

		return graph.nodes(n1, n2, n3, n4, n5).edges(e1, e2, e3, e4, e5)
				.build();
	}
}
