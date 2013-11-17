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
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.graph.internal.dot.ZestStyle;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;

/**
 * Zest graph sample input for the Zest-To-Dot transformation demonstrating
 * directed graph support.
 */
public class SimpleDigraph extends Graph {
	/**
	 */
	public SimpleDigraph() {

		/* Global settings, here we set the directed property: */
		withAttribute(Graph.Attr.EDGE_STYLE.toString(),
				ZestStyle.CONNECTIONS_DIRECTED).withAttribute(
				Graph.Attr.LAYOUT.toString(), new TreeLayoutAlgorithm());

		/* Nodes: */
		Node n1 = new Node().withAttribute(Graph.Attr.LABEL.toString(), "1"); //$NON-NLS-1$
		Node n2 = new Node().withAttribute(Graph.Attr.LABEL.toString(), "2"); //$NON-NLS-1$
		Node n3 = new Node().withAttribute(Graph.Attr.LABEL.toString(), "3"); //$NON-NLS-1$

		/* Connection from n1 to n2: */
		Edge e1 = new Edge(n1, n2);

		/* Connection from n2 to n3: */
		Edge e2 = new Edge(n2, n3);

		withNodes(n1, n2, n3).withEdges(e1, e2);

	}
}
