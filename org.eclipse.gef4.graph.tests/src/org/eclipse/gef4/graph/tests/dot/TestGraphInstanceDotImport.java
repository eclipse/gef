/*******************************************************************************
 * Copyright (c) 2009, 2011 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.graph.tests.dot;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef4.graph.DotGraph;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.GraphConnection;
import org.eclipse.gef4.graph.GraphNode;
import org.eclipse.gef4.graph.internal.dot.DotAst;
import org.eclipse.gef4.graph.internal.dot.DotImport;
import org.eclipse.gef4.graph.internal.dot.GraphCreatorInterpreter;
import org.eclipse.gef4.graph.internal.dot.ZestStyle;
import org.eclipse.gef4.layout.algorithms.GridLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
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
		/* The DOT input, can be given as a String, File or IFile: */
		DotImport dotImport = new DotImport("digraph Simple { 1;2; 1->2 }"); //$NON-NLS-1$
		/* Create a Zest graph instance: */
		Graph graph = dotImport.newGraphInstance();
		System.out.println(graph);
	}

	@Test
	public void dotImport() {
		DotImport importer = new DotImport("digraph Sample{1;2;1->2}"); //$NON-NLS-1$
		Graph graph = importer.newGraphInstance();
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(ZestStyle.CONNECTIONS_DIRECTED,
				graph.getConnectionStyle());
	}

	@Test
	public void undeclaredNodes() {
		Graph graph = new DotImport("digraph{1->2;1->3}").newGraphInstance();
		Assert.assertEquals(3, graph.getNodes().size());
		Assert.assertEquals(2, graph.getConnections().size());
	}

	@Test
	public void subgraphs() {
		DotImport dotImport = new DotImport(
				"digraph{subgraph {1->2}; subgraph {1->3}}");
		Graph graph = dotImport.newGraphInstance();
		assertEquals("Non-cluster subgraphs should be ignored in rendering", 3,
				graph.getNodes().size());
		assertEquals(2, graph.getConnections().size());
	}

	@Test
	public void digraphType() {
		Graph graph = interpreter.create(parse("digraph Sample{1;2;1->2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(ZestStyle.CONNECTIONS_DIRECTED,
				graph.getConnectionStyle());
	}

	@Test
	public void graphType() {
		Graph graph = interpreter.create(parse("graph Sample{1;2;1--2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertNotSame(ZestStyle.CONNECTIONS_DIRECTED,
				graph.getConnectionStyle());
	}

	@Test
	public void nodeDefaultLabel() {
		Graph graph = interpreter.create(parse("graph Sample{1}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("1", //$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void nodeCount() {
		Graph graph = interpreter.create(parse("graph Sample{1;2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(2, graph.getNodes().size());
	}

	@Test
	public void edgeCount() {
		Graph graph = interpreter
				.create(parse("graph Sample{1;2;1->2;2->2;1->1}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(3, graph.getConnections().size());
	}

	@Test
	public void nodeLabel() {
		Graph graph = interpreter
				.create(parse("graph Sample{1[label=\"Node1\"];}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Node1", //$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void edgeLabel() {
		Graph graph = interpreter
				.create(parse("graph Sample{1;2;1->2[label=\"Edge1\"]}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Edge1", ((GraphConnection) graph.getConnections() //$NON-NLS-1$
				.get(0)).getText());
	}

	@Test
	public void edgeStyle() {
		Graph graph = interpreter
				.create(parse("graph Sample{1;2;1->2[style=dashed]}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(ZestStyle.LINE_DASH, ((GraphConnection) graph
				.getConnections().get(0)).getLineStyle());
	}

	@Test
	public void globalEdgeStyle() {
		Graph graph = interpreter
				.create(parse("graph Sample{edge[style=dashed];1;2;1->2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(ZestStyle.LINE_DASH, ((GraphConnection) graph
				.getConnections().get(0)).getLineStyle());
	}

	@Test
	public void globalEdgeLabel() {
		Graph graph = interpreter
				.create(parse("graph Sample{edge[label=\"Edge1\"];1;2;1->2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Edge1", ((GraphConnection) graph.getConnections() //$NON-NLS-1$
				.get(0)).getText());
	}

	@Test
	public void globalNodeLabel() {
		Graph graph = interpreter
				.create(parse("graph Sample{node[label=\"Node1\"];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Node1", //$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void layoutSpring() {
		Graph graph = interpreter
				.create(parse("graph Sample{graph[layout=fdp];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(SpringLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test
	public void layoutGrid() {
		Graph graph = interpreter
				.create(parse("graph Sample{graph[layout=osage];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(GridLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test
	public void layoutRadial() {
		Graph graph = interpreter
				.create(parse("graph Sample{graph[layout=twopi];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(RadialLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test
	public void layoutTree() {
		Graph graph = interpreter
				.create(parse("graph Sample{graph[layout=dot];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(TreeLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
	}

	@Test
	public void layoutHorizontalTreeViaLayout() {
		Graph graph = interpreter
				.create(parse("graph Sample{graph[layout=dot];rankdir=LR;1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(TreeLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
		Assert.assertEquals(TreeLayoutAlgorithm.LEFT_RIGHT,
				((TreeLayoutAlgorithm) (graph.getLayoutAlgorithm()))
						.getDirection());
	}

	@Test
	public void layoutHorizontalTreeViaAttribute() {
		Graph graph = interpreter.create(parse("graph Sample{rankdir=LR;1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(TreeLayoutAlgorithm.class, graph
				.getLayoutAlgorithm().getClass());
		Assert.assertEquals(TreeLayoutAlgorithm.LEFT_RIGHT,
				((TreeLayoutAlgorithm) (graph.getLayoutAlgorithm()))
						.getDirection());
	}

	@Test
	public void globalNodeAttributeAdHocNodes() {
		Graph graph = interpreter
				.create(parse("graph{node[label=\"TEXT\"];1--2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("TEXT", //$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void globalEdgeAttributeAdHocNodes() {
		Graph graph = interpreter
				.create(parse("graph{edge[label=\"TEXT\"];1--2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("TEXT", ((GraphConnection) graph.getConnections() //$NON-NLS-1$
				.get(0)).getText());
	}

	@Test
	public void headerCommentGraph() {
		Graph graph = interpreter
				.create(parse("/*A header comment*/\ngraph{1--2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(2, graph.getNodes().size());
		Assert.assertEquals(1, graph.getConnections().size());
	}

	@Test
	public void nodesBeforeEdges() {
		Graph graph = interpreter
				.create(parse("graph{1;2;3;4; 1->2;2->3;2->4}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getConnections().size());
	}

	@Test
	public void nodesAfterEdges() {
		Graph graph = interpreter
				.create(parse("graph{1->2;2->3;2->4;1[label=\"node\"];2;3;4}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getConnections().size());
		Assert.assertEquals("node",
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void useInterpreterTwice() {
		String dot = "graph{1;2;3;4; 1->2;2->3;2->4}"; //$NON-NLS-1$
		Graph graph = interpreter.create(parse(dot));
		graph = interpreter.create(parse(dot));
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getConnections().size());
	}

	@Test
	public void idsWithQuotes() {
		String dot = "graph{\"node 1\";\"node 2\"}"; //$NON-NLS-1$
		Graph graph = interpreter.create(parse(dot));
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("node 1",//$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
		Assert.assertEquals("node 2",//$NON-NLS-1$
				((GraphNode) graph.getNodes().get(1)).getText());
	}

	@Test
	public void escapedQuotes() {
		String dot = "graph{n1[label=\"node \\\"1\\\"\"]}"; //$NON-NLS-1$
		Graph graph = interpreter.create(parse(dot));
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("node \"1\"",//$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void fullyQuoted() {
		String dot = "graph{\"n1\";\"n2\";\"n1\"->\"n2\"}"; //$NON-NLS-1$
		Graph graph = interpreter.create(parse(dot));
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
		Graph graph = interpreter.create(parse(dot));
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
		Graph graph = interpreter.create(parse(dot));
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("node\n1",//$NON-NLS-1$
				((GraphNode) graph.getNodes().get(0)).getText());
	}

	@Test
	public void multiEdgeStatements() {
		Graph graph = new DotGraph("digraph{1->2->3->4}"); //$NON-NLS-1$
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
		interpreter.create(parse("graph Sample{graph[layout=cool];1;}")); //$NON-NLS-1$
	}

	@Test(expected = IllegalArgumentException.class)
	public void faultyStyle() {
		interpreter.create(parse("graph Sample{1;2;1->2[style=\"dashed++\"]}")); //$NON-NLS-1$
	}

	private DotAst parse(String dot) {
		return new DotImport(dot).getDotAst();
	}
}
