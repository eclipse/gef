/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.dot.tests.dot.test_data;

import org.eclipse.gef4.dot.Edge;
import org.eclipse.gef4.dot.Graph;
import org.eclipse.gef4.dot.Node;
import org.eclipse.gef4.dot.Graph.Attr;
import org.eclipse.gef4.dot.internal.dot.ZestStyle;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;

/**
 * Zest graph sample input for the Zest-To-Dot transformation. Contains
 * everything that is currently supported by the transformation: node and edge
 * labels, edge styles.
 */
public class SampleGraph {
	/**
	 */
	public Graph getGraph() {
		/* Global settings: */
		Graph.Builder graph = new Graph.Builder()//
				.attr(Attr.EDGE_STYLE, ZestStyle.CONNECTIONS_DIRECTED)//
				.attr(Attr.LAYOUT, new TreeLayoutAlgorithm());

		/* Nodes: */
		Node n1 = new Node.Builder().attr(Attr.LABEL, "Node").build(); //$NON-NLS-1$
		Node n2 = new Node.Builder().attr(Attr.LABEL, "Node").build(); //$NON-NLS-1$
		Node n3 = new Node.Builder().attr(Attr.LABEL, "Leaf1").build(); //$NON-NLS-1$
		Node n4 = new Node.Builder().attr(Attr.LABEL, "Leaf2").build(); //$NON-NLS-1$

		/* Connection from n1 to n2: */
		new Edge.Builder(n1, n2).attr(Attr.LABEL, "Edge")
				.attr(Attr.EDGE_STYLE, ZestStyle.LINE_DASH).build();

		/* Connection from n2 to n3: */
		Edge e1 = new Edge.Builder(n2, n3).attr(Attr.LABEL, "Edge")
				.attr(Attr.EDGE_STYLE, ZestStyle.LINE_DASH).build();

		/* Connection from n2 to n4: */
		Edge e2 = new Edge.Builder(n2, n4).attr(Attr.LABEL, "Dotted")
				.attr(Attr.EDGE_STYLE, ZestStyle.LINE_DOT).build();

		return graph.nodes(n1, n2, n3, n4).edges(e1, e2).build();
	}
}
