/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.dot.tests.dot;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef4.dot.internal.dot.DotImport;
import org.eclipse.gef4.dot.internal.dot.ZestStyle;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
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
		Graph.Builder graph = new Graph.Builder(); // or from DOT, see below
		new DotImport("node[label=zested]; 1->2; 1->3").into(graph); //$NON-NLS-1$
		assertNodesEdgesCount(3, 2, graph);
		new DotImport("edge[style=dashed]; 2->4; 3->5").into(graph); //$NON-NLS-1$
		assertNodesEdgesCount(5, 4, graph);
	}

	@Test
	public void addToEmptyGraph() {
		Graph.Builder graph = new Graph.Builder();
		graph.attr(Graph.Attr.LAYOUT.toString(), new TreeLayoutAlgorithm());
		Assert.assertEquals(0, graph.build().getNodes().size());
		Assert.assertEquals(0, graph.build().getEdges().size());
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
		Graph.Builder graph = new Graph.Builder();
		new DotImport("digraph{1->2}").into(graph);
		assertNodesEdgesCount(2, 1, graph);
		new DotImport("1->3").into(graph); // should reuse node 1 from above
		assertNodesEdgesCount(3, 2, graph);
		new DotImport("3->4").into(graph); // should reuse node 3 from above
		assertNodesEdgesCount(4, 3, graph);
	}

	@Test
	public void useExistingLabeledNodes() {
		Graph.Builder graph = new Graph.Builder();
		new DotImport("digraph{1[label=one];2[label=two]}").into(graph);
		assertNodesEdgesCount(2, 0, graph);
		new DotImport("1->2").into(graph); // reuse nodes 1 and 2 from above
		assertNodesEdgesCount(2, 1, graph);
		new DotImport("1->3").into(graph); // reuse node 1 from above
		assertNodesEdgesCount(3, 2, graph);
	}

	@Test
	public void undirectedEdgeDotSyntax() {
		Graph.Builder graph = new Graph.Builder();
		new DotImport("1--2").into(graph); // reuse nodes 1 and 2 from above
		assertNodesEdgesCount(2, 1, graph);
		new DotImport("1--3").into(graph); // reuse node 1 from above
		assertNodesEdgesCount(3, 2, graph);
	}

	@Test
	public void addLayoutAlgorithm() {
		Graph.Builder graph = new Graph.Builder();
		new DotImport("rankdir=LR").into(graph);
		Assert.assertEquals(TreeLayoutAlgorithm.class, graph.build().getAttrs()
				.get(Graph.Attr.LAYOUT.toString()).getClass());
		Assert.assertEquals(TreeLayoutAlgorithm.LEFT_RIGHT,
				((TreeLayoutAlgorithm) (graph.build().getAttrs()
						.get(Graph.Attr.LAYOUT.toString()))).getDirection());
		new DotImport("rankdir=TD").into(graph);
		Assert.assertEquals(TreeLayoutAlgorithm.class, graph.build().getAttrs()
				.get(Graph.Attr.LAYOUT.toString()).getClass());
		Assert.assertEquals(TreeLayoutAlgorithm.TOP_DOWN,
				((TreeLayoutAlgorithm) (graph.build().getAttrs()
						.get(Graph.Attr.LAYOUT.toString()))).getDirection());
	}

	@Test
	public void addStyledEdge() {
		Graph.Builder graph = new Graph.Builder();
		Assert.assertNull(graph.build().getAttrs()
				.get(Graph.Attr.EDGE_STYLE.toString()));
		assertNodesEdgesCount(0, 0, graph);
		new DotImport("1->2[style=dashed label=dashed]").into(graph);
		assertNodesEdgesCount(2, 1, graph);
		Iterator<Edge> iterator = graph.build().getEdges().iterator();
		Edge edge = iterator.next();
		Assert.assertEquals(ZestStyle.LINE_DASH,
				edge.getAttrs().get(Graph.Attr.EDGE_STYLE.toString()));
		Assert.assertEquals("dashed",
				edge.getAttrs().get(Graph.Attr.LABEL.toString()));
		new DotImport("2->3[style=dotted label=dotted]").into(graph);
		assertNodesEdgesCount(3, 2, graph);
		iterator = graph.build().getEdges().iterator();
		iterator.next();
		edge = iterator.next();
		Assert.assertEquals(ZestStyle.LINE_DOT,
				edge.getAttrs().get(Graph.Attr.EDGE_STYLE.toString()));
		Assert.assertEquals("dotted",
				edge.getAttrs().get(Graph.Attr.LABEL.toString()));
	}

	@Test
	public void addStyledNode() {
		Graph.Builder graph = new Graph.Builder();
		Assert.assertNull(graph.build().getAttrs()
				.get(Graph.Attr.EDGE_STYLE.toString()));
		assertNodesEdgesCount(0, 0, graph);
		new DotImport("1[label=one]").into(graph);
		assertNodesEdgesCount(1, 0, graph);
		List<Node> list = graph.build().getNodes();
		Assert.assertEquals("one",
				list.get(0).getAttrs().get(Graph.Attr.LABEL.toString()));
		new DotImport("2[label=two]; 3[label=three]").into(graph);
		assertNodesEdgesCount(3, 0, graph);
		list = graph.build().getNodes();
		Assert.assertEquals("two",
				list.get(1).getAttrs().get(Graph.Attr.LABEL.toString()));
		Assert.assertEquals("three",
				list.get(2).getAttrs().get(Graph.Attr.LABEL.toString()));
	}

	private void assertNodesEdgesCount(int n, int e, Graph.Builder builder) {
		Graph graph = builder.build();
		Assert.assertEquals(n, graph.getNodes().size());
		Assert.assertEquals(e, graph.getEdges().size());
	}
}
