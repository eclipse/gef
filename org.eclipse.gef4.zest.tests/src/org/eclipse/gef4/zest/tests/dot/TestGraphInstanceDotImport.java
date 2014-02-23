/*******************************************************************************
 * Copyright (c) 2009, 2014 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.tests.dot;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef4.graph.internal.dot.DotImport;
import org.eclipse.gef4.graph.internal.dot.ZestGraph;
import org.eclipse.gef4.layout.algorithms.GridLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef4.zest.core.widgets.GraphWidget;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for dynamic import of DOT to a Zest graph instance.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class TestGraphInstanceDotImport {

	@Test
	public void minimalUsage() {
		Shell shell = new Shell();
		/* The DOT input, can be given as a String, File or IFile: */
		DotImport dotImport = new DotImport("digraph Simple { 1;2; 1->2 }"); //$NON-NLS-1$
		/* Create a Zest graph instance in a parent, with a style: */
		GraphWidget graph = new ZestGraph(shell, SWT.NONE,
				dotImport.newGraphInstance());
		// open(shell); // sets title, layout, and size, opens the shell
		System.out.println(graph);
	}

	@Test
	public void dotImport() {
		Shell shell = new Shell();
		DotImport importer = new DotImport("digraph Sample{1;2;1->2}"); //$NON-NLS-1$
		GraphWidget graph = new ZestGraph(shell, SWT.NONE,
				importer.newGraphInstance());
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(ZestStyles.CONNECTIONS_DIRECTED,
				graph.getConnectionStyle());
		// open(shell);
	}

	@Test
	public void undeclaredNodes() {
		GraphWidget graph = new ZestGraph(new Shell(), SWT.NONE, new DotImport(
				"digraph{1->2;1->3}").newGraphInstance());
		Assert.assertEquals(3, graph.getNodes().size());
		Assert.assertEquals(2, graph.getConnections().size());
	}

	@Test
	public void subgraphs() {
		Shell shell = new Shell();
		DotImport dotImport = new DotImport(
				"digraph{subgraph {1->2}; subgraph {1->3}}");
		GraphWidget graph = new ZestGraph(shell, SWT.NONE,
				dotImport.newGraphInstance());
		assertEquals("Non-cluster subgraphs should be ignored in rendering", 3,
				graph.getNodes().size());
		assertEquals(2, graph.getConnections().size());
	}

	@Test
	public void clusterSubgraph() {
		Shell shell = new Shell();
		DotImport dotImport = new DotImport(
				"digraph{subgraph cluster_1{1->2}; subgraph cluster_2{1->3}}");
		GraphWidget graph = new ZestGraph(shell, SWT.NONE,
				dotImport.newGraphInstance());
		assertEquals("Cluster subgraphs are ignored in rendering", 3, graph
				.getNodes().size());
		assertEquals(2, graph.getConnections().size());
	}

	@Test
	public void digraphType() {
		GraphWidget graph = parse("digraph Sample{1;2;1->2}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(ZestStyles.CONNECTIONS_DIRECTED,
				graph.getConnectionStyle());
		Assert.assertEquals(ZestStyles.CONNECTIONS_DIRECTED,
				((GraphConnection) graph.getConnections().get(0))
						.getConnectionStyle());
	}

	@Test
	public void graphType() {
		GraphWidget graph = parse("graph Sample{1;2;1--2}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(ZestStyles.CONNECTIONS_SOLID,
				graph.getConnectionStyle());
		Assert.assertEquals(ZestStyles.CONNECTIONS_SOLID,
				((GraphConnection) graph.getConnections().get(0))
						.getConnectionStyle());
	}

	@Test
	public void nodeDefaultLabel() {
		GraphWidget graph = parse("graph Sample{1}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("1", //$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void nodeCount() {
		GraphWidget graph = parse("graph Sample{1;2}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(2, graph.getNodes().size());
	}

	@Test
	public void edgeCount() {
		GraphWidget graph = parse("graph Sample{1;2;1->2;2->2;1->1}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(3, graph.getConnections().size());
	}

	@Test
	public void nodeLabel() {
		GraphWidget graph = parse("graph Sample{1[label=\"Node1\"];}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Node1", //$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void edgeLabel() {
		GraphWidget graph = parse("graph Sample{1;2;1->2[label=\"Edge1\"]}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Edge1", ((GraphConnection) graph.getConnections() //$NON-NLS-1$
				.get(0)).getText());
	}

	@Test
	public void edgeStyle() {
		GraphWidget graph = parse("graph Sample{1;2;1->2[style=dashed]}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(SWT.LINE_DASH, ((GraphConnection) graph
				.getConnections().get(0)).getLineStyle());
	}

	@Test
	public void globalEdgeStyle() {
		GraphWidget graph = parse("graph Sample{edge[style=dashed];1;2;1->2}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(SWT.LINE_DASH, ((GraphConnection) graph
				.getConnections().get(0)).getLineStyle());
	}

	@Test
	public void globalEdgeLabel() {
		GraphWidget graph = parse("graph Sample{edge[label=\"Edge1\"];1;2;1->2}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Edge1", ((GraphConnection) graph.getConnections() //$NON-NLS-1$
				.get(0)).getText());
	}

	@Test
	public void globalNodeLabel() {
		GraphWidget graph = parse("graph Sample{node[label=\"Node1\"];1;}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Node1", //$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void layoutSpring() {
		GraphWidget graph = parse("graph Sample{graph[layout=fdp];1;}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(SpringLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test
	public void layoutGrid() {
		GraphWidget graph = parse("graph Sample{graph[layout=osage];1;}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(GridLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test
	public void layoutRadial() {
		GraphWidget graph = parse("graph Sample{graph[layout=twopi];1;}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(RadialLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test
	public void layoutTree() {
		GraphWidget graph = parse("graph Sample{graph[layout=dot];1;}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(TreeLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test
	public void layoutHorizontalTreeViaLayout() {
		GraphWidget graph = parse("graph Sample{graph[layout=dot];rankdir=LR;1;}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(TreeLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
		Assert.assertEquals(TreeLayoutAlgorithm.LEFT_RIGHT,
				((TreeLayoutAlgorithm) (graph.getLayoutAlgorithm()))
						.getDirection());
	}

	@Test
	public void layoutHorizontalTreeViaAttribute() {
		GraphWidget graph = parse("graph Sample{rankdir=LR;1;}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(TreeLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
		Assert.assertEquals(TreeLayoutAlgorithm.LEFT_RIGHT,
				((TreeLayoutAlgorithm) (graph.getLayoutAlgorithm()))
						.getDirection());
	}

	@Test
	public void globalNodeAttributeAdHocNodes() {
		GraphWidget graph = parse("graph{node[label=\"TEXT\"];1--2}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("TEXT", //$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void globalEdgeAttributeAdHocNodes() {
		GraphWidget graph = parse("graph{edge[label=\"TEXT\"];1--2}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("TEXT", ((GraphConnection) graph.getConnections() //$NON-NLS-1$
				.get(0)).getText());
	}

	@Test
	public void headerCommentGraph() {
		GraphWidget graph = parse("/*A header comment*/\ngraph{1--2}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(2, graph.getNodes().size());
		Assert.assertEquals(1, graph.getConnections().size());
	}

	@Test
	public void nodesBeforeEdges() {
		GraphWidget graph = parse("graph{1;2;3;4; 1->2;2->3;2->4}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getConnections().size());
	}

	@Test
	public void nodesAfterEdges() {
		GraphWidget graph = parse("graph{1->2;2->3;2->4;1[label=\"node\"];2;3;4}"); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getConnections().size());
		Assert.assertEquals("node",
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void useInterpreterTwice() {
		String dot = "graph{1;2;3;4; 1->2;2->3;2->4}"; //$NON-NLS-1$
		GraphWidget graph = parse(dot);
		graph = parse(dot);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getConnections().size());
	}

	@Test
	public void idsWithQuotes() {
		String dot = "graph{\"node 1\";\"node 2\"}"; //$NON-NLS-1$
		GraphWidget graph = parse(dot);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("node 1",//$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
		Assert.assertEquals("node 2",//$NON-NLS-1$
				((GraphNode) graph.getNodes().get(1)).getText());
	}

	@Test
	public void escapedQuotes() {
		String dot = "graph{n1[label=\"node \\\"1\\\"\"]}"; //$NON-NLS-1$
		GraphWidget graph = parse(dot);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("node \"1\"",//$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void fullyQuoted() {
		String dot = "graph{\"n1\";\"n2\";\"n1\"->\"n2\"}"; //$NON-NLS-1$
		GraphWidget graph = parse(dot);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(2, graph.getNodes().size());
		Assert.assertEquals(1, graph.getConnections().size());
		Assert.assertEquals("n1", //$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
		Assert.assertEquals("n2", //$NON-NLS-1$
				((GraphNode) graph.getNodes().get(1)).getText());
	}

	@Test
	public void labelsWithQuotes() {
		String dot = "graph{n1[label=\"node 1\"];n2[label=\"node 2\"];n1--n2[label=\"edge 1\"]}"; //$NON-NLS-1$
		GraphWidget graph = parse(dot);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("node 1",//$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
		Assert.assertEquals("node 2",//$NON-NLS-1$
				((GraphNode) graph.getNodes().get(1)).getText());
		Assert.assertEquals("edge 1", ((GraphConnection) graph.getConnections()//$NON-NLS-1$
				.get(0)).getText());
	}

	@Test
	public void newLinesInLabels() {
		String dot = "graph{n1[label=\"node\n1\"]}"; //$NON-NLS-1$
		GraphWidget graph = parse(dot);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("node\n1",//$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void multiEdgeStatements() {
		GraphWidget graph = parse("digraph{1->2->3->4}"); //$NON-NLS-1$
		assertEquals(4, graph.getNodes().size());
		assertEquals(3, graph.getConnections().size());
		/* Each node should be connected to one other, the next node: */
		assertEquals(1, ((GraphNode) graph.getNodes().get(0))
				.getSourceConnections().size());
		assertEquals(1, ((GraphNode) graph.getNodes().get(1))
				.getSourceConnections().size());
		assertEquals(1, ((GraphNode) graph.getNodes().get(2))
				.getSourceConnections().size());
	}

	@Test
	/* see http://www.graphviz.org/doc/info/attrs.html#d:style */
	public void edgeStyleInvis() {
		GraphWidget graph = parse("digraph{1->2[style=invis]}"); //$NON-NLS-1$
		assertEquals(2, graph.getNodes().size());
		assertEquals(1, graph.getConnections().size());
	}

	@Test
	public void otherUnsupportedStyles() {
		GraphWidget graph = parse("graph Sample{node[style=other];edge[style=other];1[style=other];2;1->2[style=other]}"); //$NON-NLS-1$
		assertEquals(2, graph.getNodes().size());
		assertEquals(1, graph.getConnections().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void faultyLayout() {
		parse("graph Sample{graph[layout=cool];1;}"); //$NON-NLS-1$
	}

	private ZestGraph parse(String dot) {
		return new ZestGraph(new Shell(), SWT.NONE,
				new DotImport(dot).newGraphInstance());
	}

	@SuppressWarnings("unused")
	/* would block when running tests */
	private void open(final Shell shell) {
		shell.setText("Testing"); //$NON-NLS-1$
		shell.setLayout(new FillLayout());
		shell.setSize(600, 300);
		shell.open();
		while (!shell.isDisposed()) {
			while (!shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}
	}
}
