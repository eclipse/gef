/*******************************************************************************
 * Copyright (c) 2009, 2011 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.tests.dot;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphContainer;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.dot.DotGraph;
import org.eclipse.gef4.zest.internal.dot.DotAst;
import org.eclipse.gef4.zest.internal.dot.DotImport;
import org.eclipse.gef4.zest.internal.dot.GraphCreatorInterpreter;
import org.eclipse.gef4.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for dynamic import of DOT to a Zest graph instance.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class TestGraphInstanceDotImport {
	private final GraphCreatorInterpreter interpreter = new GraphCreatorInterpreter();

	@Test
	public void minimalUsage() {
		Shell shell = new Shell();
		/* The DOT input, can be given as a String, File or IFile: */
		DotImport dotImport = new DotImport("digraph Simple { 1;2; 1->2 }"); //$NON-NLS-1$
		/* Create a Zest graph instance in a parent, with a style: */
		Graph graph = dotImport.newGraphInstance(shell, SWT.NONE);
		// open(shell); // sets title, layout, and size, opens the shell
		System.out.println(graph);
	}

	@Test
	public void dotImport() {
		Shell shell = new Shell();
		DotImport importer = new DotImport("digraph Sample{1;2;1->2}"); //$NON-NLS-1$
		Graph graph = importer.newGraphInstance(shell, SWT.NONE);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(ZestStyles.CONNECTIONS_DIRECTED,
				graph.getConnectionStyle());
		// open(shell);
	}

	@Test
	public void undeclaredNodes() {
		Graph graph = new DotImport("digraph{1->2;1->3}").newGraphInstance(
				new Shell(), SWT.NONE);
		Assert.assertEquals(3, graph.getNodes().size());
		Assert.assertEquals(2, graph.getConnections().size());
	}

	@Test
	public void subgraphs() {
		Shell shell = new Shell();
		DotImport dotImport = new DotImport(
				"digraph{subgraph {1->2}; subgraph {1->3}}");
		Graph graph = dotImport.newGraphInstance(shell, SWT.NONE);
		assertEquals("Non-cluster subgraphs should be ignored in rendering", 3,
				graph.getNodes().size());
		assertEquals(2, graph.getConnections().size());
	}

	@Test
	public void clusterSubgraphs() {
		Shell shell = new Shell();
		DotImport dotImport = new DotImport(
				"digraph{subgraph cluster{1->2}; subgraph cluster{1->3}}");
		Graph graph = dotImport.newGraphInstance(shell, SWT.NONE);
		assertEquals(
				"Cluster subgraphs should be rendered as graph containers", 2,
				graph.getNodes().size());
		assertEquals(GraphContainer.class, graph.getNodes().get(0).getClass());
		assertEquals(GraphContainer.class, graph.getNodes().get(1).getClass());
		assertEquals(2, graph.getConnections().size());
	}

	@Test
	public void labeledClusterSubgraph() {
		Shell shell = new Shell();
		DotImport dotImport = new DotImport("digraph{"
				+ "subgraph cluster{graph[layout=twopi]; label=left; 1->2}; "
				+ "subgraph cluster{label=right; 1->3; 3->4; 3->5};}");
		Graph graph = dotImport.newGraphInstance(shell, SWT.NONE);
		// open(shell);
		assertEquals(4, graph.getConnections().size());
		assertEquals("The graph should contain two nodes", 2, graph.getNodes()
				.size());
		assertEquals("left", ((Item) graph.getNodes().get(0)).getText());
		assertEquals("right", ((Item) graph.getNodes().get(1)).getText());
		assertEquals("The first node should be a graph container",
				GraphContainer.class, graph.getNodes().get(0).getClass());
		assertEquals(2, ((GraphContainer) graph.getNodes().get(0)).getNodes()
				.size());
		assertEquals("The second node should be a graph container",
				GraphContainer.class, graph.getNodes().get(1).getClass());
		assertEquals(3, ((GraphContainer) graph.getNodes().get(1)).getNodes()
				.size());
		assertEquals(RadialLayoutAlgorithm.class, ((GraphContainer) graph
				.getNodes().get(0)).getLayoutAlgorithm().getClass());
		// TODO nodes between and after subgraphs
	}

	@Test
	public void layoutClusterSubgraph() {
		Shell shell = new Shell();
		Graph graph = new DotGraph("digraph{"
				+ "subgraph cluster{graph[layout=twopi]}; "
				+ "subgraph cluster{};}", shell, SWT.NONE);
		// open(shell);
		assertEquals("The first node should be a graph container",
				GraphContainer.class, graph.getNodes().get(0).getClass());
		assertEquals("The second node should be a graph container",
				GraphContainer.class, graph.getNodes().get(1).getClass());
		assertEquals(RadialLayoutAlgorithm.class, ((GraphContainer) graph
				.getNodes().get(0)).getLayoutAlgorithm().getClass());
		assertEquals(TreeLayoutAlgorithm.class, ((GraphContainer) graph
				.getNodes().get(1)).getLayoutAlgorithm().getClass());
	}

	@Test
	public void digraphType() {
		Shell shell = new Shell();
		Graph graph = interpreter.create(shell, SWT.NONE,
				parse("digraph Sample{1;2;1->2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(ZestStyles.CONNECTIONS_DIRECTED,
				graph.getConnectionStyle());
		// open(shell);
	}

	@Test
	public void graphType() {
		Shell shell = new Shell();
		Graph graph = interpreter.create(shell, SWT.NONE,
				parse("graph Sample{1;2;1--2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertNotSame(ZestStyles.CONNECTIONS_DIRECTED,
				graph.getConnectionStyle());
		// open(shell);

	}

	@Test
	public void nodeDefaultLabel() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{1}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("1", //$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void nodeCount() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{1;2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(2, graph.getNodes().size());
	}

	@Test
	public void edgeCount() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{1;2;1->2;2->2;1->1}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(3, graph.getConnections().size());
	}

	@Test
	public void nodeLabel() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{1[label=\"Node1\"];}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Node1", //$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void edgeLabel() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{1;2;1->2[label=\"Edge1\"]}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Edge1", ((GraphConnection) graph.getConnections() //$NON-NLS-1$
				.get(0)).getText());
	}

	@Test
	public void edgeStyle() {
		Shell parent = new Shell();
		Graph graph = interpreter.create(parent, SWT.NONE,
				parse("graph Sample{1;2;1->2[style=dashed]}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(SWT.LINE_DASH, ((GraphConnection) graph
				.getConnections().get(0)).getLineStyle());
		// open(parent);
	}

	@Test
	public void globalEdgeStyle() {
		Shell parent = new Shell();
		Graph graph = interpreter.create(parent, SWT.NONE,
				parse("graph Sample{edge[style=dashed];1;2;1->2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(SWT.LINE_DASH, ((GraphConnection) graph
				.getConnections().get(0)).getLineStyle());
		// open(parent);
	}

	@Test
	public void globalEdgeLabel() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{edge[label=\"Edge1\"];1;2;1->2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Edge1", ((GraphConnection) graph.getConnections() //$NON-NLS-1$
				.get(0)).getText());
	}

	@Test
	public void globalNodeLabel() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{node[label=\"Node1\"];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Node1", //$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void layoutSpring() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{graph[layout=fdp];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(SpringLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test
	public void layoutGrid() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{graph[layout=osage];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(GridLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test
	public void layoutRadial() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{graph[layout=twopi];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(RadialLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test
	public void layoutTree() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{graph[layout=dot];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(TreeLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test
	public void layoutHorizontalTreeViaLayout() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{graph[layout=dot];rankdir=LR;1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(TreeLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
		Assert.assertEquals(TreeLayoutAlgorithm.LEFT_RIGHT,
				((TreeLayoutAlgorithm) (graph.getLayoutAlgorithm()))
						.getDirection());
	}

	@Test
	public void layoutHorizontalTreeViaAttribute() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{rankdir=LR;1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(TreeLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
		Assert.assertEquals(TreeLayoutAlgorithm.LEFT_RIGHT,
				((TreeLayoutAlgorithm) (graph.getLayoutAlgorithm()))
						.getDirection());
	}

	@Test
	public void globalNodeAttributeAdHocNodes() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph{node[label=\"TEXT\"];1--2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("TEXT", //$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void globalEdgeAttributeAdHocNodes() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph{edge[label=\"TEXT\"];1--2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("TEXT", ((GraphConnection) graph.getConnections() //$NON-NLS-1$
				.get(0)).getText());
	}

	@Test
	public void headerCommentGraph() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("/*A header comment*/\ngraph{1--2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(2, graph.getNodes().size());
		Assert.assertEquals(1, graph.getConnections().size());
	}

	@Test
	public void nodesBeforeEdges() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph{1;2;3;4; 1->2;2->3;2->4}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getConnections().size());
	}

	@Test
	public void nodesAfterEdges() {
		Graph graph = interpreter.create(new Shell(), SWT.NONE,
				parse("graph{1->2;2->3;2->4;1[label=\"node\"];2;3;4}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getConnections().size());
		Assert.assertEquals("node",
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void useInterpreterTwice() {
		String dot = "graph{1;2;3;4; 1->2;2->3;2->4}"; //$NON-NLS-1$
		Graph graph = interpreter.create(new Shell(), SWT.NONE, parse(dot));
		graph = interpreter.create(new Shell(), SWT.NONE, parse(dot));
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getConnections().size());
	}

	@Test
	public void idsWithQuotes() {
		String dot = "graph{\"node 1\";\"node 2\"}"; //$NON-NLS-1$
		Graph graph = interpreter.create(new Shell(), SWT.NONE, parse(dot));
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("node 1",//$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
		Assert.assertEquals("node 2",//$NON-NLS-1$
				((GraphNode) graph.getNodes().get(1)).getText());
	}

	@Test
	public void escapedQuotes() {
		String dot = "graph{n1[label=\"node \\\"1\\\"\"]}"; //$NON-NLS-1$
		Graph graph = interpreter.create(new Shell(), SWT.NONE, parse(dot));
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("node \"1\"",//$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void fullyQuoted() {
		String dot = "graph{\"n1\";\"n2\";\"n1\"->\"n2\"}"; //$NON-NLS-1$
		Graph graph = interpreter.create(new Shell(), SWT.NONE, parse(dot));
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
		Graph graph = interpreter.create(new Shell(), SWT.NONE, parse(dot));
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
		Graph graph = interpreter.create(new Shell(), SWT.NONE, parse(dot));
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("node\n1",//$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void multiEdgeStatements() {
		Graph graph = new DotGraph("digraph{1->2->3->4}", new Shell(), SWT.NONE); //$NON-NLS-1$
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

	@Test(expected = IllegalArgumentException.class)
	public void faultyLayout() {
		interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{graph[layout=cool];1;}")); //$NON-NLS-1$
	}

	@Test(expected = IllegalArgumentException.class)
	public void faultyStyle() {
		interpreter.create(new Shell(), SWT.NONE,
				parse("graph Sample{1;2;1->2[style=\"dashed++\"]}")); //$NON-NLS-1$
	}

	private DotAst parse(String dot) {
		return new DotImport(dot).getDotAst();
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
