/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.tests.dot;

import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.internal.dot.DotTemplate;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef4.zest.tests.dot.test_data.LabeledGraph;
import org.eclipse.gef4.zest.tests.dot.test_data.SampleGraph;
import org.eclipse.gef4.zest.tests.dot.test_data.SimpleDigraph;
import org.eclipse.gef4.zest.tests.dot.test_data.SimpleGraph;
import org.eclipse.gef4.zest.tests.dot.test_data.StyledGraph;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the generated {@link DotTemplate} class.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class TestDotTemplate {
	private static Shell shell = new Shell();

	/** Zest-To-Dot transformation for a Zest graph itself (no subclass used). */
	@Test
	public void zestGraph() {
		Graph graph = new Graph(shell, SWT.NONE);
		graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(
				TreeLayoutAlgorithm.LEFT_RIGHT), true);
		graph.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		GraphConnection edge = new GraphConnection(graph, SWT.NONE,
				new GraphNode(graph, SWT.NONE, "Node 1"), new GraphNode(graph, //$NON-NLS-1$
						SWT.NONE, "Node 2")); //$NON-NLS-1$
		edge.setText("A dotted edge"); //$NON-NLS-1$
		edge.setLineStyle(SWT.LINE_DOT);
		String dot = new DotTemplate().generate(graph);
		Assert.assertTrue(
				"Graph with horizontal tree layout should contain rankdir=LR",
				dot.contains("rankdir=LR"));
		testDotGeneration(graph);
	}

	/**
	 * Zest-To-Dot transformation for a full sample graph showing all that is
	 * currently supported in the Zest-To-Dot transformation.
	 */
	@Test
	public void sampleGraph() {
		testDotGeneration(new SampleGraph(shell, SWT.NONE));
	}

	/** Zest-To-Dot transformation for a minimal undirected graph. */
	@Test
	public void simpleGraph() {
		testDotGeneration(new SimpleGraph(shell, SWT.NONE));
	}

	/** Zest-To-Dot transformation for a minimal directed graph. */
	@Test
	public void directedGraph() {
		testDotGeneration(new SimpleDigraph(shell, SWT.NONE));
	}

	/** Zest-To-Dot transformation for a graph with edge and node labels. */
	@Test
	public void labeledGraph() {
		testDotGeneration(new LabeledGraph(shell, SWT.NONE));
	}

	/** Zest-To-Dot transformation for a graph with styled edges (dotted, etc). */
	@Test
	public void styledGraph() {
		testDotGeneration(new StyledGraph(shell, SWT.NONE));
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
				"DOT representation must contain simple class name of Zest input!", //$NON-NLS-1$
				dot.contains(graph.getClass().getSimpleName()));
		Assert.assertTrue(graph.getConnectionStyle() == ZestStyles.CONNECTIONS_DIRECTED ? dot
				.contains("digraph") : !dot.contains("digraph")); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println(dot);
	}
}
