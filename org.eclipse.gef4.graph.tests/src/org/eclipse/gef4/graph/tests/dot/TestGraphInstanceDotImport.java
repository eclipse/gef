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

import java.util.List;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
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
				graph.getAttribute(Graph.Attr.EDGE_STYLE.toString()));
	}

	@Test
	public void undeclaredNodes() {
		Graph graph = new DotImport("digraph{1->2;1->3}").newGraphInstance();
		Assert.assertEquals(3, graph.getNodes().size());
		Assert.assertEquals(2, graph.getEdges().size());
	}

	@Test
	public void subgraphs() {
		DotImport dotImport = new DotImport(
				"digraph{subgraph {1->2}; subgraph {1->3}}");
		Graph graph = dotImport.newGraphInstance();
		assertEquals("Non-cluster subgraphs should be ignored in rendering", 3,
				graph.getNodes().size());
		assertEquals(2, graph.getEdges().size());
	}

	@Test
	public void digraphType() {
		Graph graph = interpreter.create(parse("digraph Sample{1;2;1->2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(ZestStyle.CONNECTIONS_DIRECTED,
				graph.getAttribute(Graph.Attr.EDGE_STYLE.toString()));
	}

	@Test
	public void graphType() {
		Graph graph = interpreter.create(parse("graph Sample{1;2;1--2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertNotSame(ZestStyle.CONNECTIONS_DIRECTED,
				graph.getAttribute(Graph.Attr.EDGE_STYLE.toString()));
	}

	@Test
	public void nodeDefaultLabel() {
		Graph graph = interpreter.create(parse("graph Sample{1}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("1", //$NON-NLS-1$
				((Node) graph.getNodes().iterator().next())
						.getAttribute(Graph.Attr.LABEL.toString()));
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
		Assert.assertEquals(3, graph.getEdges().size());
	}

	@Test
	public void nodeLabel() {
		Graph graph = interpreter
				.create(parse("graph Sample{1[label=\"Node1\"];}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Node1", //$NON-NLS-1$
				(graph.getNodes().iterator().next())
						.getAttribute(Graph.Attr.LABEL.toString()));
	}

	@Test
	public void edgeLabel() {
		Graph graph = interpreter
				.create(parse("graph Sample{1;2;1->2[label=\"Edge1\"]}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Edge1", (graph.getEdges() //$NON-NLS-1$
				.iterator().next()).getAttribute(Graph.Attr.LABEL.toString()));
	}

	@Test
	public void edgeStyle() {
		Graph graph = interpreter
				.create(parse("graph Sample{1;2;1->2[style=dashed]}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(ZestStyle.LINE_DASH, (graph.getEdges().iterator()
				.next()).getAttribute(Graph.Attr.EDGE_STYLE.toString()));
	}

	@Test
	public void globalEdgeStyle() {
		Graph graph = interpreter
				.create(parse("graph Sample{edge[style=dashed];1;2;1->2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(ZestStyle.LINE_DASH, (graph.getEdges().iterator()
				.next()).getAttribute(Graph.Attr.EDGE_STYLE.toString()));
	}

	@Test
	public void globalEdgeLabel() {
		Graph graph = interpreter
				.create(parse("graph Sample{edge[label=\"Edge1\"];1;2;1->2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Edge1", (graph.getEdges() //$NON-NLS-1$
				.iterator().next()).getAttribute(Graph.Attr.LABEL.toString()));
	}

	@Test
	public void globalNodeLabel() {
		Graph graph = interpreter
				.create(parse("graph Sample{node[label=\"Node1\"];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Node1", //$NON-NLS-1$
				(graph.getNodes().iterator().next())
						.getAttribute(Graph.Attr.LABEL.toString()));
	}

	@Test
	public void layoutSpring() {
		Graph graph = interpreter
				.create(parse("graph Sample{graph[layout=fdp];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(SpringLayoutAlgorithm.class,
				graph.getAttribute(Graph.Attr.LAYOUT.toString()).getClass());
	}

	@Test
	public void layoutGrid() {
		Graph graph = interpreter
				.create(parse("graph Sample{graph[layout=osage];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(GridLayoutAlgorithm.class,
				graph.getAttribute(Graph.Attr.LAYOUT.toString()).getClass());
	}

	@Test
	public void layoutRadial() {
		Graph graph = interpreter
				.create(parse("graph Sample{graph[layout=twopi];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(RadialLayoutAlgorithm.class,
				graph.getAttribute(Graph.Attr.LAYOUT.toString()).getClass());
	}

	@Test
	public void layoutTree() {
		Graph graph = interpreter
				.create(parse("graph Sample{graph[layout=dot];1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(TreeLayoutAlgorithm.class,
				graph.getAttribute(Graph.Attr.LAYOUT.toString()).getClass());
	}

	@Test
	public void layoutHorizontalTreeViaLayout() {
		Graph graph = interpreter
				.create(parse("graph Sample{graph[layout=dot];rankdir=LR;1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(TreeLayoutAlgorithm.class,
				graph.getAttribute(Graph.Attr.LAYOUT.toString()).getClass());
		Assert.assertEquals(TreeLayoutAlgorithm.LEFT_RIGHT,
				((TreeLayoutAlgorithm) (graph.getAttribute(Graph.Attr.LAYOUT
						.toString()))).getDirection());
	}

	@Test
	public void layoutHorizontalTreeViaAttribute() {
		Graph graph = interpreter.create(parse("graph Sample{rankdir=LR;1;}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(TreeLayoutAlgorithm.class,
				graph.getAttribute(Graph.Attr.LAYOUT.toString()).getClass());
		Assert.assertEquals(TreeLayoutAlgorithm.LEFT_RIGHT,
				((TreeLayoutAlgorithm) (graph.getAttribute(Graph.Attr.LAYOUT
						.toString()))).getDirection());
	}

	@Test
	public void globalNodeAttributeAdHocNodes() {
		Graph graph = interpreter
				.create(parse("graph{node[label=\"TEXT\"];1--2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("TEXT", //$NON-NLS-1$
				((Node) graph.getNodes().iterator().next())
						.getAttribute(Graph.Attr.LABEL.toString()));
	}

	@Test
	public void globalEdgeAttributeAdHocNodes() {
		Graph graph = interpreter
				.create(parse("graph{edge[label=\"TEXT\"];1--2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("TEXT", ((Edge) graph.getEdges() //$NON-NLS-1$
				.iterator().next()).getAttribute(Graph.Attr.LABEL.toString()));
	}

	@Test
	public void headerCommentGraph() {
		Graph graph = interpreter
				.create(parse("/*A header comment*/\ngraph{1--2}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(2, graph.getNodes().size());
		Assert.assertEquals(1, graph.getEdges().size());
	}

	@Test
	public void nodesBeforeEdges() {
		Graph graph = interpreter
				.create(parse("graph{1;2;3;4; 1->2;2->3;2->4}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getEdges().size());
	}

	@Test
	public void nodesAfterEdges() {
		Graph graph = interpreter
				.create(parse("graph{1->2;2->3;2->4;1[label=\"node\"];2;3;4}")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getEdges().size());
		Assert.assertEquals(
				"node",
				graph.getNodes().get(0)
						.getAttribute(Graph.Attr.LABEL.toString()));
	}

	@Test
	public void useInterpreterTwice() {
		String dot = "graph{1;2;3;4; 1->2;2->3;2->4}"; //$NON-NLS-1$
		Graph graph = interpreter.create(parse(dot));
		graph = interpreter.create(parse(dot));
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getEdges().size());
	}

	@Test
	public void idsWithQuotes() {
		String dot = "graph{\"node 1\";\"node 2\"}"; //$NON-NLS-1$
		Graph graph = interpreter.create(parse(dot));
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		List<Node> list = graph.getNodes();
		Assert.assertEquals("node 1",//$NON-NLS-1$
				list.get(0).getAttribute(Graph.Attr.LABEL.toString()));
		Assert.assertEquals("node 2",//$NON-NLS-1$
				list.get(1).getAttribute(Graph.Attr.LABEL.toString()));
	}

	@Test
	public void escapedQuotes() {
		String dot = "graph{n1[label=\"node \\\"1\\\"\"]}"; //$NON-NLS-1$
		Graph graph = interpreter.create(parse(dot));
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("node \"1\"",//$NON-NLS-1$
				((Node) graph.getNodes().iterator().next())
						.getAttribute(Graph.Attr.LABEL.toString()));
	}

	@Test
	public void fullyQuoted() {
		String dot = "graph{\"n1\";\"n2\";\"n1\"->\"n2\"}"; //$NON-NLS-1$
		Graph graph = interpreter.create(parse(dot));
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(2, graph.getNodes().size());
		Assert.assertEquals(1, graph.getEdges().size());
		List<Node> list = graph.getNodes();
		Assert.assertEquals("n1", //$NON-NLS-1$
				list.get(0).getAttribute(Graph.Attr.LABEL.toString()));
		Assert.assertEquals("n2", //$NON-NLS-1$
				list.get(1).getAttribute(Graph.Attr.LABEL.toString()));
	}

	@Test
	public void labelsWithQuotes() {
		String dot = "graph{n1[label=\"node 1\"];n2[label=\"node 2\"];n1--n2[label=\"edge 1\"]}"; //$NON-NLS-1$
		Graph graph = interpreter.create(parse(dot));
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		List<Node> list = graph.getNodes();
		Assert.assertEquals("node 1",//$NON-NLS-1$
				list.get(0).getAttribute(Graph.Attr.LABEL.toString()));
		Assert.assertEquals("node 2",//$NON-NLS-1$
				list.get(1).getAttribute(Graph.Attr.LABEL.toString()));
		Assert.assertEquals("edge 1", graph.getEdges().iterator().next()
				.getAttribute(Graph.Attr.LABEL.toString()));
	}

	@Test
	public void newLinesInLabels() {
		String dot = "graph{n1[label=\"node\n1\"]}"; //$NON-NLS-1$
		Graph graph = interpreter.create(parse(dot));
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("node\n1",//$NON-NLS-1$
				((Node) graph.getNodes().iterator().next())
						.getAttribute(Graph.Attr.LABEL.toString()));
	}

	@Test
	public void multiEdgeStatements() {
		Graph graph = new Graph("digraph{1->2->3->4}"); //$NON-NLS-1$
		assertEquals(4, graph.getNodes().size());
		assertEquals(3, graph.getEdges().size());
		/* Each node should be connected to one other, the previous node: */
		List<Node> list = graph.getNodes();
		assertEquals(1, list.get(1).getSourceConnections(graph).size());
		assertEquals(1, list.get(1).getSourceConnections(graph).size());
		assertEquals(1, list.get(1).getSourceConnections(graph).size());
	}

	@Test
	/* see http://www.graphviz.org/doc/info/attrs.html#d:style */
	public void edgeStyleInvis() {
		Graph graph = new Graph("digraph{1->2[style=invis]}"); //$NON-NLS-1$
		assertEquals(2, graph.getNodes().size());
		assertEquals(1, graph.getEdges().size());
	}

	@Test
	public void otherUnsupportedStyles() {
		Graph graph = interpreter
				.create(parse("graph Sample{node[style=other];edge[style=other];1[style=other];2;1->2[style=other]}")); //$NON-NLS-1$
		assertEquals(2, graph.getNodes().size());
		assertEquals(1, graph.getEdges().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void faultyLayout() {
		interpreter.create(parse("graph Sample{graph[layout=cool];1;}")); //$NON-NLS-1$
	}

	private DotAst parse(String dot) {
		return new DotImport(dot).getDotAst();
	}
}
