/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.graph.tests.dot.test_data;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.GraphConnection;
import org.eclipse.gef4.graph.GraphNode;
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
		setConnectionStyle(ZestStyle.CONNECTIONS_DIRECTED);
		setLayoutAlgorithm(new TreeLayoutAlgorithm(), true);

		/* Nodes: */
		GraphNode n1 = new GraphNode(this, "1"); //$NON-NLS-1$
		GraphNode n2 = new GraphNode(this, "2"); //$NON-NLS-1$
		GraphNode n3 = new GraphNode(this, "3"); //$NON-NLS-1$

		/* Connection from n1 to n2: */
		new GraphConnection(this, n1, n2);

		/* Connection from n2 to n3: */
		new GraphConnection(this, n2, n3);

	}
}
