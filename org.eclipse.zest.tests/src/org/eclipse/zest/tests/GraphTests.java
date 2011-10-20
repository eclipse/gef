/*******************************************************************************
 * Copyright (c) 2011 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial tests
 *******************************************************************************/
package org.eclipse.zest.tests;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;

/**
 * General tests for the {@link Graph} class.
 * 
 * @author Fabian Steeg (fsteeg)
 * 
 */
public class GraphTests extends TestCase {

	private static final int STYLE = SWT.NONE;

	private GraphNode[] nodes;

	private Graph graph;

	protected void setUp() throws Exception {
		graph = new Graph(new Shell(), STYLE);
		nodes = new GraphNode[] { new GraphNode(graph, STYLE),
				new GraphNode(graph, STYLE) };
		new GraphConnection(graph, STYLE, nodes[0], nodes[1]);
	}

	public void testGraphData() {
		graph.setData("graph data");
		assertEquals("graph data", graph.getData());
	}

	public void testNodeItemData() {
		GraphItem item = (GraphNode) graph.getNodes().get(0);
		item.setData("node item data");
		assertEquals("node item data", item.getData());
	}

	public void testConnectionItemData() {
		GraphItem item = (GraphConnection) graph.getConnections().get(0);
		item.setData("connection item data");
		assertEquals("connection item data", item.getData());
	}

}
