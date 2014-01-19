/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.graph.tests.dot;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
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
		graph.withAttribute(Graph.Attr.LAYOUT.toString(),
				new TreeLayoutAlgorithm());
		Assert.assertEquals(0, graph.getNodes().size());
		Assert.assertEquals(0, graph.getEdges().size());
		/* The DOT input, can be given as a String, File or IFile: */
		new DotImport("1->2").into(graph); //$NON-NLS-1$
		assertNodesEdgesCount(2, 1, graph);
		new DotImport("3;4").into(graph); //$NON-NLS-1$
		assertNodesEdgesCount(4, 1, graph);
		new DotImport("5->6").into(graph); //$NON-NLS-1$
		assertNodesEdgesCount(6, 2, graph);
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
		Assert.assertEquals(ZestStyle.GRAPH_DIRECTED,
				graph.getAttribute(Graph.Attr.GRAPH_TYPE.toString()));
	}

	@Test
	public void undirectedEdgeDotSyntax() {
		Graph graph = new Graph();
		new DotImport("1--2").into(graph); // reuse nodes 1 and 2 from above
		assertNodesEdgesCount(2, 1, graph);
		new DotImport("1--3").into(graph); // reuse node 1 from above
		assertNodesEdgesCount(3, 2, graph);
	}

	@Test
	public void addLayoutAlgorithm() {
		Graph graph = new Graph();
		Assert.assertEquals(null,
				graph.getAttribute(Graph.Attr.LAYOUT.toString()));
		new DotImport("rankdir=LR").into(graph);
		Assert.assertEquals(TreeLayoutAlgorithm.class,
				graph.getAttribute(Graph.Attr.LAYOUT.toString()).getClass());
		Assert.assertEquals(TreeLayoutAlgorithm.LEFT_RIGHT,
				((TreeLayoutAlgorithm) (graph.getAttribute(Graph.Attr.LAYOUT
						.toString()))).getDirection());
		new DotImport("rankdir=TD").into(graph);
		Assert.assertEquals(TreeLayoutAlgorithm.class,
				graph.getAttribute(Graph.Attr.LAYOUT.toString()).getClass());
		Assert.assertEquals(TreeLayoutAlgorithm.TOP_DOWN,
				((TreeLayoutAlgorithm) (graph.getAttribute(Graph.Attr.LAYOUT
						.toString()))).getDirection());
	}

	@Test
	public void addStyledEdge() {
		Graph graph = new Graph();
		Assert.assertNull(graph.getAttribute(Graph.Attr.EDGE_STYLE.toString()));
		assertNodesEdgesCount(0, 0, graph);
		new DotImport("1->2[style=dashed label=dashed]").into(graph);
		assertNodesEdgesCount(2, 1, graph);
		Iterator<Edge> iterator = graph.getEdges().iterator();
		Edge edge = iterator.next();
		Assert.assertEquals(ZestStyle.LINE_DASH,
				edge.getAttribute(Graph.Attr.EDGE_STYLE.toString()));
		Assert.assertEquals("dashed",
				edge.getAttribute(Graph.Attr.LABEL.toString()));
		new DotImport("2->3[style=dotted label=dotted]").into(graph);
		assertNodesEdgesCount(3, 2, graph);
		iterator = graph.getEdges().iterator();
		iterator.next();
		edge = iterator.next();
		Assert.assertEquals(ZestStyle.LINE_DOT,
				edge.getAttribute(Graph.Attr.EDGE_STYLE.toString()));
		Assert.assertEquals("dotted",
				edge.getAttribute(Graph.Attr.LABEL.toString()));
	}

	@Test
	public void addStyledNode() {
		Graph graph = new Graph();
		Assert.assertNull(graph.getAttribute(Graph.Attr.EDGE_STYLE.toString()));
		assertNodesEdgesCount(0, 0, graph);
		new DotImport("1[label=one]").into(graph);
		assertNodesEdgesCount(1, 0, graph);
		List<Node> list = graph.getNodes();
		Assert.assertEquals("one",
				list.get(0).getAttribute(Graph.Attr.LABEL.toString()));
		new DotImport("2[label=two]; 3[label=three]").into(graph);
		assertNodesEdgesCount(3, 0, graph);
		list = graph.getNodes();
		Assert.assertEquals("two",
				list.get(1).getAttribute(Graph.Attr.LABEL.toString()));
		Assert.assertEquals("three",
				list.get(2).getAttribute(Graph.Attr.LABEL.toString()));
	}

	private void assertNodesEdgesCount(int n, int e, Graph graph) {
		Assert.assertEquals(n, graph.getNodes().size());
		Assert.assertEquals(e, graph.getEdges().size());
	}
}
