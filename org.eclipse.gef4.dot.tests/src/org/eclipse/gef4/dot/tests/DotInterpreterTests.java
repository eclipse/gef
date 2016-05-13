/*******************************************************************************
 * Copyright (c) 2009, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation (see bug #277380)
 *     Tamas Miklossy (itemis AG) - Add support for all dot attributes (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.tests;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.dot.internal.DotAttributes;
import org.eclipse.gef4.dot.internal.DotImport;
import org.eclipse.gef4.dot.internal.DotInterpreter;
import org.eclipse.gef4.dot.internal.parser.DotStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.dot.DotAst;
import org.eclipse.gef4.dot.internal.parser.layout.Layout;
import org.eclipse.gef4.dot.internal.parser.parser.antlr.DotParser;
import org.eclipse.gef4.dot.internal.parser.rankdir.Rankdir;
import org.eclipse.gef4.dot.internal.parser.style.EdgeStyle;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Injector;

/**
 * Tests for dynamic import of DOT to a Zest graph instance.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class DotInterpreterTests {

	private static final Injector dotInjector = new DotStandaloneSetup()
			.createInjectorAndDoEMFRegistration();
	private static final DotParser dotParser = dotInjector
			.getInstance(DotParser.class);

	private final DotInterpreter interpreter = new DotInterpreter();

	@Test
	public void digraphType() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.TWO_NODES_ONE_DIRECTED_EDGE))
				.get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(DotAttributes._TYPE__G__DIGRAPH,
				DotAttributes._getType(graph));
	}

	@Test
	public void graphType() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.TWO_NODES_ONE_EDGE)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(DotAttributes._TYPE__G__GRAPH,
				DotAttributes._getType(graph));
	}

	@Test
	public void nodeDefaultLabel() {
		Graph graph = interpreter.interpret(parse(DotTestGraphs.ONE_NODE))
				.get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("1", //$NON-NLS-1$
				DotAttributes._getName(graph.getNodes().get(0)));
	}

	@Test
	public void nodeCount() {
		Graph graph = interpreter.interpret(parse(DotTestGraphs.TWO_NODES))
				.get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(2, graph.getNodes().size());
	}

	@Test
	public void edgeCount() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.TWO_NODES_AND_THREE_EDGES))
				.get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(3, graph.getEdges().size());
	}

	@Test
	public void nodeLabel() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.NODE_LABEL_LOCAL)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Node1", //$NON-NLS-1$
				DotAttributes.getLabel(graph.getNodes().get(0)));
	}

	@Test
	public void edgeLabel() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.EDGE_LABEL_LOCAL)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Edge1", DotAttributes.getLabel(graph.getEdges() //$NON-NLS-1$
				.get(0)));
	}

	@Test
	public void edgeStyle() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.EDGE_STYLE_LOCAL)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(EdgeStyle.DASHED.toString(),
				DotAttributes.getStyle(graph.getEdges().get(0)));
	}

	@Test
	public void globalEdgeStyle() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.EDGE_STYLE_GLOBAL)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(EdgeStyle.DASHED.toString(),
				DotAttributes.getStyle(graph.getEdges().get(0)));
	}

	@Test
	public void globalEdgeLabel() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.EDGE_LABEL_GLOBAL)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Edge1", DotAttributes.getLabel(graph.getEdges() //$NON-NLS-1$
				.get(0)));
	}

	@Test
	public void globalNodeLabel() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.NODE_LABEL_GLOBAL)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("Node1", //$NON-NLS-1$
				DotAttributes.getLabel(graph.getNodes().get(0)));
	}

	@Test
	public void layoutSpring() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.GRAPH_LAYOUT_FDP)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(Layout.FDP.toString(),
				DotAttributes.getLayout(graph));
	}

	@Test
	public void layoutGrid() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.GRAPH_LAYOUT_OSAGE)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(Layout.OSAGE.toString(),
				DotAttributes.getLayout(graph));
	}

	@Test
	public void layoutRadial() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.GRAPH_LAYOUT_TWOPI)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(Layout.TWOPI.toString(),
				DotAttributes.getLayout(graph));
	}

	@Test
	public void layoutTree() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.GRAPH_LAYOUT_DOT)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(Layout.DOT.toString(),
				DotAttributes.getLayout(graph));
	}

	@Test
	public void layoutHorizontalTreeViaLayout() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.GRAPH_LAYOUT_DOT_HORIZONTAL))
				.get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(Layout.DOT.toString(),
				DotAttributes.getLayout(graph));
		Assert.assertEquals(Rankdir.LR, DotAttributes.getRankdirParsed(graph));
	}

	@Test
	public void layoutHorizontalTreeViaAttribute() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.GRAPH_RANKDIR_LR)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(Rankdir.LR, DotAttributes.getRankdirParsed(graph));
	}

	@Test
	public void globalNodeAttributeAdHocNodes() {
		Graph graph = interpreter
				.interpret(
						parse(DotTestGraphs.GLOBAL_NODE_LABEL_AD_HOC_NODES))
				.get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("TEXT", //$NON-NLS-1$
				DotAttributes.getLabel(graph.getNodes().get(0)));
	}

	@Test
	public void globalEdgeAttributeAdHocNodes() {
		Graph graph = interpreter
				.interpret(
						parse(DotTestGraphs.GLOBAL_EDGE_LABEL_AD_HOC_NODES))
				.get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("TEXT", DotAttributes.getLabel(graph.getEdges() //$NON-NLS-1$
				.get(0)));
	}

	@Test
	public void headerCommentGraph() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.HEADER_COMMENT)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(2, graph.getNodes().size());
		Assert.assertEquals(1, graph.getEdges().size());
	}

	@Test
	public void nodesBeforeEdges() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.NODES_BEFORE_EDGES)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getEdges().size());
	}

	@Test
	public void nodesAfterEdges() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.NODES_AFTER_EDGES)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getEdges().size());
		Assert.assertEquals("node",
				DotAttributes.getLabel(graph.getNodes().get(0)));
	}

	@Test
	public void useInterpreterTwice() {
		String dot = DotTestGraphs.NODES_AFTER_EDGES;
		Graph graph = interpreter.interpret(parse(dot)).get(0);
		graph = interpreter.interpret(parse(dot)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getEdges().size());
	}

	@Test
	public void idsWithQuotes() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.IDS_WITH_QUOTES)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		List<Node> list = graph.getNodes();
		Assert.assertEquals("node 1", //$NON-NLS-1$
				DotAttributes._getName(list.get(0)));
		Assert.assertEquals("node 2", //$NON-NLS-1$
				DotAttributes._getName(list.get(1)));
	}

	@Test
	public void escapedQuotes() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.ESCAPED_QUOTES_LABEL)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("node \"1\"", //$NON-NLS-1$
				DotAttributes.getLabel(graph.getNodes().get(0)));
	}

	@Test
	public void fullyQuoted() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.FULLY_QUOTED_IDS)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(2, graph.getNodes().size());
		Assert.assertEquals(1, graph.getEdges().size());
		List<Node> list = graph.getNodes();
		Assert.assertEquals("n1", //$NON-NLS-1$
				DotAttributes._getName(list.get(0)));
		Assert.assertEquals("n2", //$NON-NLS-1$
				DotAttributes._getName(list.get(1)));
	}

	@Test
	public void labelsWithQuotes() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.QUOTED_LABELS)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		List<Node> list = graph.getNodes();
		Assert.assertEquals("node 1", //$NON-NLS-1$
				DotAttributes.getLabel(list.get(0)));
		Assert.assertEquals("node 2", //$NON-NLS-1$
				DotAttributes.getLabel(list.get(1)));
		Assert.assertEquals("edge 1",
				DotAttributes.getLabel(graph.getEdges().get(0)));
	}

	@Test
	public void newLinesInLabels() {
		Graph graph = interpreter
				.interpret(parse(DotTestGraphs.NEW_LINES_IN_LABELS)).get(0);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("node" + System.lineSeparator() + "1", //$NON-NLS-1$
				DotAttributes.getLabel(graph.getNodes().get(0)));
	}

	@Test
	public void multiEdgeStatements() {
		Graph graph = new DotImport()
				.importDot(DotTestGraphs.MULTI_EDGE_STATEMENTS);
		assertEquals(4, graph.getNodes().size());
		assertEquals(3, graph.getEdges().size());
		/* Each node should be connected to one other, the previous node: */
		List<Node> list = graph.getNodes();
		assertEquals(1, getSourceConnections(list.get(1), graph).size());
		assertEquals(1, getSourceConnections(list.get(1), graph).size());
		assertEquals(1, getSourceConnections(list.get(1), graph).size());
	}

	private List<Edge> getSourceConnections(Node node, Graph graph) {
		List<Edge> result = new ArrayList<>();
		List<Edge> edges = graph.getEdges();
		for (Edge edge : edges)
			if (edge.getTarget().equals(node))
				result.add(edge);
		return result;
	}

	@Test
	/* see http://www.graphviz.org/doc/info/attrs.html#d:style */
	public void edgeStyleInvis() {
		Graph graph = new DotImport()
				.importDot(DotTestGraphs.EDGE_STYLE_INVIS);
		assertEquals(2, graph.getNodes().size());
		assertEquals(1, graph.getEdges().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void faultyLayout() {
		interpreter.interpret(parse("graph Sample{graph[layout=cool];1;}")); //$NON-NLS-1$
	}

	private DotAst parse(String dot) {
		return (DotAst) dotParser.parse(new StringReader(dot))
				.getRootASTElement();
	}
}
