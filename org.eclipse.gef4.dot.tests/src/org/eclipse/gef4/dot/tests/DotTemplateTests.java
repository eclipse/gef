/*******************************************************************************
 * Copyright (c) 2009, 2015 Fabian Steeg and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation (see bug #277380)
 *******************************************************************************/
package org.eclipse.gef4.dot.tests;

import org.eclipse.gef4.dot.internal.DotAttributes;
import org.eclipse.gef4.dot.internal.DotTemplate;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the generated {@link DotTemplate} class.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class DotTemplateTests {

	/**
	 * Zest-To-Dot transformation for a Zest graph itself (no subclass used).
	 */
	@Test
	public void zestGraph() {
		Graph.Builder graph = new Graph.Builder();
		graph.attr(DotAttributes.GRAPH_LAYOUT, DotAttributes.GRAPH_LAYOUT_DOT);
		graph.attr(DotAttributes.GRAPH_RANKDIR, DotAttributes.GRAPH_RANKDIR_LR);
		Node node1 = new Node.Builder().attr(DotAttributes.NODE_LABEL, "Node 1")
				.buildNode();
		Node node2 = new Node.Builder().attr(DotAttributes.NODE_LABEL, "Node 2")
				.buildNode();
		Edge edge = new Edge.Builder(node1, node2)
				.attr(DotAttributes.EDGE_LABEL, "A dotted edge")
				.attr(DotAttributes.EDGE_STYLE, DotAttributes.EDGE_STYLE_DOTTED)
				.buildEdge();
		graph.attr(DotAttributes.GRAPH_TYPE, DotAttributes.GRAPH_TYPE_DIRECTED)
				.edges(edge);
		String dot = new DotTemplate().generate(graph.build());
		Assert.assertTrue(
				"Graph with horizontal tree layout should contain rankdir=LR",
				dot.contains("rankdir=LR"));
		testDotGeneration(graph.build());
	}

	/**
	 * Zest-To-Dot transformation for a full sample graph showing all that is
	 * currently supported in the Zest-To-Dot transformation.
	 */
	@Test
	public void sampleGraph() {
		testDotGeneration(DotTestUtils.getSampleGraph());
	}

	/** Zest-To-Dot transformation for a minimal undirected graph. */
	@Test
	public void simpleGraph() {
		testDotGeneration(DotTestUtils.getSimpleGraph());
	}

	/** Zest-To-Dot transformation for a minimal directed graph. */
	@Test
	public void directedGraph() {
		testDotGeneration(DotTestUtils.getSimpleDiGraph());
	}

	/** Zest-To-Dot transformation for a graph with edge and node labels. */
	@Test
	public void labeledGraph() {
		testDotGeneration(DotTestUtils.getLabeledGraph());
	}

	/**
	 * Zest-To-Dot transformation for a graph with styled edges (dotted, etc).
	 */
	@Test
	public void styledGraph() {
		testDotGeneration(DotTestUtils.getStyledGraph());
	}

	protected void testDotGeneration(final Graph graph) {
		String dot = new DotTemplate().generate(graph);
		/*
		 * We need to care for naming the DOT graph, as calling it 'Graph'
		 * causes Graphviz to fail when rendering.
		 */
		Assert.assertFalse("DOT graph must not be named 'Graph',", //$NON-NLS-1$
				dot.contains("graph Graph")); //$NON-NLS-1$
		Assert.assertTrue(
				"DOT representation must contain simple class name of Dot input!", //$NON-NLS-1$
				dot.contains(graph.getClass().getSimpleName()));
		Assert.assertTrue(DotAttributes.GRAPH_TYPE_DIRECTED
				.equals(DotAttributes.getType(graph)) ? dot.contains("digraph") //$NON-NLS-1$
						: !dot.contains("digraph")); //$NON-NLS-1$
		System.out.println(dot);
	}
}
