/*******************************************************************************
 * Copyright (c) 2011 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial tests
 *******************************************************************************/
package org.eclipse.gef4.zest.tests;

import junit.framework.TestCase;

import org.eclipse.gef4.zest.core.widgets.DAGExpandCollapseManager;
import org.eclipse.gef4.zest.core.widgets.DefaultSubgraph;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphItem;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.LayoutFilter;
import org.eclipse.gef4.zest.layouts.LayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.interfaces.LayoutContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.junit.Assert;

/**
 * Tests involving the {@link LayoutAlgorithm} interface.
 * 
 * @author Fabian Steeg (fsteeg)
 * 
 */
public class LayoutAlgorithmTests extends TestCase {

	/**
	 * Access items laid out in a custom layout algorithm (see
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=337144)
	 */
	public void testCustomLayoutSimpleItemAccess() {
		Graph graph = new Graph(new Shell(), SWT.NONE);
		new GraphNode(graph, SWT.NONE);
		graph.setLayoutAlgorithm(new LayoutAlgorithm() {
			public void setLayoutContext(LayoutContext context) {
				Item[] all = context.getEntities()[0].getItems();
				Item[] nodes = context.getNodes()[0].getItems();
				Assert.assertEquals(1, all.length);
				Assert.assertEquals(1, nodes.length);
				Assert.assertTrue(
						"All entity items should be wrapped in a GraphNode[]",
						all instanceof GraphNode[]);
				Assert.assertTrue(
						"Node entity items should be wrapped in a GraphNode[]",
						nodes instanceof GraphNode[]);
				Assert.assertTrue(
						"All entity items should be GraphNode instances",
						all[0] instanceof GraphNode);
				Assert.assertTrue(
						"Node entity items should be GraphNode instances",
						nodes[0] instanceof GraphNode);
			}

			public void applyLayout(boolean clean) {
			}
		}, true);
	}

	/**
	 * Access items laid out in a custom layout algorithm (see
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=337144)
	 */
	public void testCustomLayoutSubgraphItemAccess() {
		Graph graph = new Graph(new Shell(), SWT.NONE);
		graph.setSubgraphFactory(new DefaultSubgraph.PrunedSuccessorsSubgraphFactory());
		graph.setExpandCollapseManager(new DAGExpandCollapseManager(true));
		GraphNode n1 = new GraphNode(graph, SWT.NONE);
		GraphNode n2 = new GraphNode(graph, SWT.NONE);
		GraphNode n3 = new GraphNode(graph, SWT.NONE);
		new GraphConnection(graph, SWT.NONE, n1, n2);
		new GraphConnection(graph, SWT.NONE, n1, n3);
		graph.setLayoutAlgorithm(new LayoutAlgorithm() {
			public void setLayoutContext(LayoutContext context) {
				Assert.assertEquals(1, context.getSubgraphs().length);
				Item[] sub = context.getSubgraphs()[0].getItems();
				Assert.assertEquals(3, sub.length);
				Assert.assertTrue(
						"All subgraph items should be wrapped in a GraphNode[]",
						sub instanceof GraphNode[]);
				Assert.assertTrue(
						"All subgraph items should be GraphNode instances",
						sub[0] instanceof GraphNode);
			}

			public void applyLayout(boolean clean) {
			}
		}, true);
	}

	/**
	 * Access items laid out in a custom layout algorithm (see
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=337144)
	 */
	public void testCustomLayoutSubgraphFilteredAccess() {
		Graph graph = new Graph(new Shell(), SWT.NONE);
		graph.setSubgraphFactory(new DefaultSubgraph.PrunedSuccessorsSubgraphFactory());
		graph.setExpandCollapseManager(new DAGExpandCollapseManager(true));
		GraphNode n1 = new GraphNode(graph, SWT.NONE);
		GraphNode n2 = new GraphNode(graph, SWT.NONE, "filter");
		GraphNode n3 = new GraphNode(graph, SWT.NONE);
		new GraphConnection(graph, SWT.NONE, n1, n2);
		new GraphConnection(graph, SWT.NONE, n1, n3);
		graph.addLayoutFilter(new LayoutFilter() {
			public boolean isObjectFiltered(GraphItem item) {
				if (item instanceof GraphNode) {
					GraphNode node = (GraphNode) item;
					if (node.getText().equals("filter")) {
						return true;
					}
				}
				return false;
			}
		});
		graph.setLayoutAlgorithm(new LayoutAlgorithm() {
			public void setLayoutContext(LayoutContext context) {
				Assert.assertEquals(1, context.getSubgraphs().length);
				Item[] sub = context.getSubgraphs()[0].getItems();
				Assert.assertEquals(2, sub.length); // one filtered
			}

			public void applyLayout(boolean clean) {
			}
		}, true);
	}

	/**
	 * Attempt to reproduce an infinite loop with GridLayoutAlgorithm on an
	 * empty graph (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=382791)
	 */
	public void testGridLayoutAlgorithmEmptyGraph() {
		Graph graph = new Graph(new Shell(), SWT.NONE);
		graph.setLayoutAlgorithm(new GridLayoutAlgorithm(), true);
		Assert.assertEquals(GridLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}
}
