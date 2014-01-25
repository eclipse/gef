/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.graph.tests.dot.test_data;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Graph.Attr;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.graph.internal.dot.ZestStyle;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;

/**
 * Zest graph sample input for the Zest-To-Dot transformation demonstrating edge
 * styles support.
 */
public class StyledGraph {
	/**
	 */
	public Graph getGraph() {
		/* Global properties: */
		Graph.Builder graph = new Graph.Builder().attr(
				Attr.EDGE_STYLE.toString(), ZestStyle.CONNECTIONS_DIRECTED)
				.attr(Attr.LAYOUT.toString(), new TreeLayoutAlgorithm());

		/* Nodes: */
		Node n1 = new Node.Builder().attr(Attr.LABEL.toString(), "1").build(); //$NON-NLS-1$
		Node n2 = new Node.Builder().attr(Attr.LABEL.toString(), "2").build(); //$NON-NLS-1$
		Node n3 = new Node.Builder().attr(Attr.LABEL.toString(), "3").build(); //$NON-NLS-1$
		Node n4 = new Node.Builder().attr(Attr.LABEL.toString(), "4").build(); //$NON-NLS-1$
		Node n5 = new Node.Builder().attr(Attr.LABEL.toString(), "5").build(); //$NON-NLS-1$

		/* Connection from n1 to n2: */
		Edge e1 = new Edge.Builder(n1, n2).attr(Attr.EDGE_STYLE.toString(),
				ZestStyle.LINE_DASH).build();

		/* Connection from n2 to n3: */
		Edge e2 = new Edge.Builder(n2, n3).attr(Attr.EDGE_STYLE.toString(),
				ZestStyle.LINE_DOT).build();

		/* Connection from n3 to n4: */
		Edge e3 = new Edge.Builder(n3, n4).attr(Attr.EDGE_STYLE.toString(),
				ZestStyle.LINE_DASHDOT).build();

		/* Connection from n3 to n5: */
		Edge e4 = new Edge.Builder(n3, n5).attr(Attr.EDGE_STYLE.toString(),
				ZestStyle.LINE_SOLID).build();

		return graph.nodes(n1, n2, n3, n4, n5).edges(e1, e2, e3, e4).build();
	}
}
