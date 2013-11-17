/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.graph.tests.dot;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.GraphConnection;
import org.eclipse.gef4.graph.GraphNode;
import org.eclipse.gef4.graph.internal.dot.DotImport;
import org.eclipse.gef4.graph.internal.dot.ZestStyle;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for import of DOT snippets into an existing Zest graph instance.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class TestSnippetDotImport {

	@Test
	public void sampleUsage() {
		Graph graph = new Graph(); // or from DOT, see below
		new DotImport("node[label=zested]; 1->2; 1->3").into(graph); //$NON-NLS-1$
		assertNodesEdgesCount(3, 2, graph);
		new DotImport("edge[style=dashed]; 2->4; 3->5").into(graph); //$NON-NLS-1$
		assertNodesEdgesCount(5, 4, graph);
	}

	@Test
	public void addToEmptyGraph() {
		Graph graph = new Graph();
		graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(), true);
		Assert.assertEquals(0, graph.getNodes().size());
		Assert.assertEquals(0, graph.getConnections().size());
		/* The DOT input, can be given as a String, File or IFile: */
		new DotImport("1->2").into(graph); //$NON-NLS-1$
		assertNodesEdgesCount(2, 1, graph);
		new DotImport("3;4").into(graph); //$NON-NLS-1$
		assertNodesEdgesCount(4, 1, graph);
		new DotImport("5->6").into(graph); //$NON-NLS-1$
		assertNodesEdgesCount(6, 2, graph);
		Assert.assertEquals(ZestStyle.CONNECTIONS_DIRECTED,
				graph.getConnectionStyle());
	}

	@Test
	public void useExistingNodes() {
		Graph graph = new DotImport("digraph{1->2}").newGraphInstance();
		assertNodesEdgesCount(2, 1, graph);
		new DotImport("1->3").into(graph); // should reuse node 1 from above
		assertNodesEdgesCount(3, 2, graph);
		new DotImport("3->4").into(graph); // should reuse node 3 from above
		assertNodesEdgesCount(4, 3, graph);
	}

	@Test
	public void useExistingLabeledNodes() {
		Graph graph = new DotImport("digraph{1[label=one];2[label=two]}")
				.newGraphInstance();
		assertNodesEdgesCount(2, 0, graph);
		new DotImport("1->2").into(graph); // reuse nodes 1 and 2 from above
		assertNodesEdgesCount(2, 1, graph);
		new DotImport("1->3").into(graph); // reuse node 1 from above
		assertNodesEdgesCount(3, 2, graph);
		Assert.assertEquals(ZestStyle.CONNECTIONS_DIRECTED,
				graph.getConnectionStyle());
	}

	@Test
	public void undirectedEdgeDotSyntax() {
		Graph graph = new Graph();
		new DotImport("1--2").into(graph); // reuse nodes 1 and 2 from above
		assertNodesEdgesCount(2, 1, graph);
		new DotImport("1--3").into(graph); // reuse node 1 from above
		assertNodesEdgesCount(3, 2, graph);
		Assert.assertEquals(ZestStyle.CONNECTIONS_SOLID,
				graph.getConnectionStyle());
	}

	@Test
	public void addLayoutAlgorithm() {
		Graph graph = new Graph();
		Assert.assertEquals(null, graph.getLayoutAlgorithm());
		new DotImport("rankdir=LR").into(graph);
		Assert.assertEquals(TreeLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
		Assert.assertEquals(TreeLayoutAlgorithm.LEFT_RIGHT,
				((TreeLayoutAlgorithm) (graph.getLayoutAlgorithm()))
						.getDirection());
		new DotImport("rankdir=TD").into(graph);
		Assert.assertEquals(TreeLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
		Assert.assertEquals(TreeLayoutAlgorithm.TOP_DOWN,
				((TreeLayoutAlgorithm) (graph.getLayoutAlgorithm()))
						.getDirection());
	}

	@Test
	public void addStyledEdge() {
		Graph graph = new Graph();
		Assert.assertEquals(ZestStyle.NONE, graph.getConnectionStyle());
		assertNodesEdgesCount(0, 0, graph);
		new DotImport("1->2[style=dashed label=dashed]").into(graph);
		assertNodesEdgesCount(2, 1, graph);
		GraphConnection edge = (GraphConnection) graph.getConnections().get(0);
		Assert.assertEquals(ZestStyle.LINE_DASH, edge.getLineStyle());
		Assert.assertEquals("dashed", edge.getText());
		new DotImport("2->3[style=dotted label=dotted]").into(graph);
		assertNodesEdgesCount(3, 2, graph);
		edge = (GraphConnection) graph.getConnections().get(1);
		Assert.assertEquals(ZestStyle.LINE_DOT, edge.getLineStyle());
		Assert.assertEquals("dotted", edge.getText());
	}

	@Test
	public void addStyledNode() {
		Graph graph = new Graph();
		Assert.assertEquals(ZestStyle.NONE, graph.getConnectionStyle());
		assertNodesEdgesCount(0, 0, graph);
		new DotImport("1[label=one]").into(graph);
		assertNodesEdgesCount(1, 0, graph);
		GraphNode node = (GraphNode) graph.getNodes().get(0);
		Assert.assertEquals("one", node.getText());
		new DotImport("2[label=two]; 3[label=three]").into(graph);
		assertNodesEdgesCount(3, 0, graph);
		Assert.assertEquals("two",
				((GraphNode) graph.getNodes().get(1)).getText());
		Assert.assertEquals("three",
				((GraphNode) graph.getNodes().get(2)).getText());
	}

	private void assertNodesEdgesCount(int n, int e, Graph graph) {
		Assert.assertEquals(n, graph.getNodes().size());
		Assert.assertEquals(e, graph.getConnections().size());
	}
}
