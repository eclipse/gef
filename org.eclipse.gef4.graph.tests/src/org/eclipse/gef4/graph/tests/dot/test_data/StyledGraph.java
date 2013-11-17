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
 * Zest graph sample input for the Zest-To-Dot transformation demonstrating edge
 * styles support.
 */
public class StyledGraph extends Graph {
	/**
	 */
	public StyledGraph() {
		/* Global properties: */
		setConnectionStyle(ZestStyle.CONNECTIONS_DIRECTED);
		setLayoutAlgorithm(new TreeLayoutAlgorithm(), true);

		/* Nodes: */
		GraphNode n1 = new GraphNode(this, "1"); //$NON-NLS-1$
		GraphNode n2 = new GraphNode(this, "2"); //$NON-NLS-1$
		GraphNode n3 = new GraphNode(this, "3"); //$NON-NLS-1$
		GraphNode n4 = new GraphNode(this, "4"); //$NON-NLS-1$
		GraphNode n5 = new GraphNode(this, "5"); //$NON-NLS-1$

		/* Connection from n1 to n2: */
		GraphConnection n1n2 = new GraphConnection(this, n1, n2);
		n1n2.setLineStyle(ZestStyle.LINE_DASH);

		/* Connection from n2 to n3: */
		GraphConnection n2n3 = new GraphConnection(this, n2, n3);
		n2n3.setLineStyle(ZestStyle.LINE_DOT);

		/* Connection from n3 to n4: */
		GraphConnection n3n4 = new GraphConnection(this, n3, n4);
		n3n4.setLineStyle(ZestStyle.LINE_DASHDOT);

		/* Connection from n3 to n5: */
		GraphConnection n3n5 = new GraphConnection(this, n3, n5);
		n3n5.setLineStyle(ZestStyle.LINE_SOLID);

	}
}
