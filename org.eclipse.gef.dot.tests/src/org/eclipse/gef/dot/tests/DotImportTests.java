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
 *     Tamas Miklossy  (itemis AG) - implement additional test cases (bug #493136)
 *                                 - merge DotInterpreter into DotImport (bug #491261)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import static org.eclipse.gef.dot.tests.DotTestUtils.RESOURCES_TESTS;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.DotImport;
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider;
import org.eclipse.gef.dot.internal.language.dot.GraphType;
import org.eclipse.gef.dot.internal.language.layout.Layout;
import org.eclipse.gef.dot.internal.language.rankdir.Rankdir;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.gef.dot.internal.language.terminals.ID.Type;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for the {@link DotImport} class.
 * 
 * @author Fabian Steeg (fsteeg)
 */
@RunWith(XtextRunner.class)
@InjectWith(DotUiInjectorProvider.class)
public final class DotImportTests {

	private final DotImport dotImport = new DotImport();

	/**
	 * Test valid graphs can be imported without exceptions.
	 */
	@Test
	public void sampleGraphsFileImport() {
		// simple graphs
		Graph graph = importFile(
				new File(RESOURCES_TESTS + "simple_graph.dot")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(DotTestUtils.getSimpleGraph().toString(),
				graph.toString());

		graph = importFile(new File(RESOURCES_TESTS + "simple_digraph.dot")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(DotTestUtils.getSimpleDiGraph().toString(),
				graph.toString());

		graph = importFile(new File(RESOURCES_TESTS + "labeled_graph.dot")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(DotTestUtils.getLabeledGraph().toString(),
				graph.toString());

		graph = importFile(new File(RESOURCES_TESTS + "styled_graph.dot")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(DotTestUtils.getStyledGraph().toString(),
				graph.toString());

		// test import succeeds without exceptions
		graph = importFile(new File(RESOURCES_TESTS + "sample_input.dot")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$

		graph = importFile(
				new File(RESOURCES_TESTS + "basic_directed_graph.dot")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$

		graph = importFile(new File(RESOURCES_TESTS + "global_node_graph.dot")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$

		graph = importFile(new File(RESOURCES_TESTS + "global_edge_graph.dot")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$

		graph = importFile(new File(RESOURCES_TESTS + "attributes_graph.dot")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$

		graph = importFile(new File(RESOURCES_TESTS + "node_groups.dot")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$

		graph = importFile(
				new File(RESOURCES_TESTS + "id_matches_keyword.dot")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$

		graph = importFile(new File(RESOURCES_TESTS + "layout_tree_graph.dot")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$

		graph = importFile(
				new File(RESOURCES_TESTS + "layout_spring_graph.dot")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$

		graph = importFile(
				new File(RESOURCES_TESTS + "layout_radial_graph.dot")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$

		graph = importFile(new File(RESOURCES_TESTS + "layout_grid_graph.dot")); //$NON-NLS-1$
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
	}

	/**
	 * Test error handling for invalid graph.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void invalidGraphFileImport() {
		importString("graph Sample{");
	}

	@Test(expected = IllegalArgumentException.class)
	public void faultyLayout() {
		importString("graph Sample{graph[layout=cool];1;}"); //$NON-NLS-1$
	}

	@Test
	public void digraphType() {
		Graph graph = importString(DotTestGraphs.TWO_NODES_ONE_DIRECTED_EDGE);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(GraphType.DIGRAPH, DotAttributes._getType(graph));
	}

	@Test
	public void graphType() {
		Graph graph = importString(DotTestGraphs.TWO_NODES_ONE_EDGE);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(GraphType.GRAPH, DotAttributes._getType(graph));
	}

	@Test
	public void nodeDefaultLabel() {
		Graph graph = importString(DotTestGraphs.ONE_NODE);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("1", //$NON-NLS-1$
				DotAttributes._getName(graph.getNodes().get(0)));
	}

	@Test
	public void nodeCount() {
		Graph graph = importString(DotTestGraphs.TWO_NODES);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(2, graph.getNodes().size());
	}

	@Test
	public void edgeCount() {
		Graph graph = importString(DotTestGraphs.TWO_NODES_AND_THREE_EDGES);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(3, graph.getEdges().size());
	}

	@Test
	public void layoutSpring() {
		Graph graph = importString(DotTestGraphs.GRAPH_LAYOUT_FDP);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(Layout.FDP.toString(),
				DotAttributes.getLayout(graph));
	}

	@Test
	public void layoutGrid() {
		Graph graph = importString(DotTestGraphs.GRAPH_LAYOUT_OSAGE);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(Layout.OSAGE.toString(),
				DotAttributes.getLayout(graph));
	}

	@Test
	public void layoutRadial() {
		Graph graph = importString(DotTestGraphs.GRAPH_LAYOUT_TWOPI);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(Layout.TWOPI.toString(),
				DotAttributes.getLayout(graph));
	}

	@Test
	public void layoutTree() {
		Graph graph = importString(DotTestGraphs.GRAPH_LAYOUT_DOT);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(Layout.DOT.toString(),
				DotAttributes.getLayout(graph));
	}

	@Test
	public void layoutHorizontalTreeViaLayout() {
		Graph graph = importString(DotTestGraphs.GRAPH_LAYOUT_DOT_HORIZONTAL);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(Layout.DOT.toString(),
				DotAttributes.getLayout(graph));
		Assert.assertEquals(Rankdir.LR, DotAttributes.getRankdirParsed(graph));
	}

	@Test
	public void layoutHorizontalTreeViaAttribute() {
		Graph graph = importString(DotTestGraphs.GRAPH_RANKDIR_LR);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(Rankdir.LR, DotAttributes.getRankdirParsed(graph));
	}

	@Test
	public void globalNodeAttributeAdHocNodes() {
		Graph graph = importString(
				DotTestGraphs.GLOBAL_NODE_LABEL_AD_HOC_NODES);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("TEXT", //$NON-NLS-1$
				DotAttributes.getLabel(graph.getNodes().get(0)));
	}

	@Test
	public void globalEdgeAttributeAdHocNodes() {
		Graph graph = importString(
				DotTestGraphs.GLOBAL_EDGE_LABEL_AD_HOC_NODES);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("TEXT", DotAttributes.getLabel(graph.getEdges() //$NON-NLS-1$
				.get(0)));
	}

	@Test
	public void headerCommentGraph() {
		Graph graph = importString(DotTestGraphs.HEADER_COMMENT);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(2, graph.getNodes().size());
		Assert.assertEquals(1, graph.getEdges().size());
	}

	@Test
	public void nodesBeforeEdges() {
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1]).buildEdge();
		Edge e2 = new Edge.Builder(nodes[1], nodes[2]).buildEdge();
		Edge e3 = new Edge.Builder(nodes[1], nodes[3]).buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2, e3).build();
		testStringImport(expected, DotTestGraphs.NODES_BEFORE_EDGES);
	}

	@Test
	public void nodesAfterEdges() {
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		DotAttributes.setLabel(nodes[0], "node");
		Edge e1 = new Edge.Builder(nodes[0], nodes[1]).buildEdge();
		Edge e2 = new Edge.Builder(nodes[1], nodes[2]).buildEdge();
		Edge e3 = new Edge.Builder(nodes[1], nodes[3]).buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2, e3).build();
		testStringImport(expected, DotTestGraphs.NODES_AFTER_EDGES);
	}

	@Test
	public void useDotImporterTwice() {
		String dot = DotTestGraphs.NODES_AFTER_EDGES;
		Graph graph = importString(dot);
		graph = importString(dot);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(4, graph.getNodes().size());
		Assert.assertEquals(3, graph.getEdges().size());
	}

	@Test
	public void idsWithQuotes() {
		Graph graph = importString(DotTestGraphs.IDS_WITH_QUOTES);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		List<Node> list = graph.getNodes();
		Assert.assertEquals("node 1", //$NON-NLS-1$
				DotAttributes._getName(list.get(0)));
		Assert.assertEquals("node 2", //$NON-NLS-1$
				DotAttributes._getName(list.get(1)));
	}

	@Test
	public void escapedQuotes() {
		Graph graph = importString(DotTestGraphs.ESCAPED_QUOTES_LABEL);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("node \"1\"", //$NON-NLS-1$
				DotAttributes.getLabel(graph.getNodes().get(0)));
	}

	@Test
	public void multilineQuotedId() {
		Graph graph = importString(DotTestGraphs.MULTILINE_QUOTED_IDS);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("node 1", //$NON-NLS-1$
				DotAttributes.getLabel(graph.getNodes().get(0)));
	}

	@Test
	public void fullyQuoted() {
		Graph graph = importString(DotTestGraphs.FULLY_QUOTED_IDS);
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
		Graph graph = importString(DotTestGraphs.QUOTED_LABELS);
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
		Graph graph = importString(DotTestGraphs.NEW_LINES_IN_LABELS);
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals("node" + System.lineSeparator() + "1", //$NON-NLS-1$
				DotAttributes.getLabel(graph.getNodes().get(0)));
	}

	@Test
	public void multiEdgeStatements() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.DIGRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setArrowhead, "ornormal") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[1], nodes[2])
				.attr(DotAttributes::setArrowhead, "ornormal") //$NON-NLS-1$
				.buildEdge();
		Edge e3 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setArrowhead, "ornormal") //$NON-NLS-1$
				.buildEdge();
		Edge e4 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setArrowhead, "ornormal") //$NON-NLS-1$
				.buildEdge();
		Edge e5 = new Edge.Builder(nodes[1], nodes[2])
				.attr(DotAttributes::setArrowhead, "ornormal") //$NON-NLS-1$
				.buildEdge();
		Edge e6 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setArrowhead, "ornormal") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2, e3, e4, e5, e6)
				.build();
		testStringImport(expected, DotTestGraphs.MULTI_EDGE_STATEMENTS_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.DIGRAPH);
		DotAttributes.setArrowhead(e4, "olnormal");
		DotAttributes.setArrowhead(e5, "olnormal");
		DotAttributes.setArrowhead(e6, "olnormal");
		expected = graph.nodes(nodes).edges(e1, e2, e3, e4, e5, e6).build();
		testStringImport(expected, DotTestGraphs.MULTI_EDGE_STATEMENTS_LOCAL);

		// test override attribute
		testStringImport(expected,
				DotTestGraphs.MULTI_EDGE_STATEMENTS_OVERRIDE);
	}

	@Test
	public void edgeStyleInvis() {
		Graph graph = importString(DotTestGraphs.EDGE_STYLE_INVIS);
		assertEquals(2, graph.getNodes().size());
		assertEquals(1, graph.getEdges().size());
	}

	@Test
	public void edge_arrowhead() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.DIGRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setArrowhead, "crow") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setArrowhead, "crow") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWHEAD_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.DIGRAPH);
		DotAttributes.setArrowhead(e1, "diamond");
		DotAttributes.setArrowhead(e2, "dot");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWHEAD_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.DIGRAPH);
		DotAttributes.setArrowhead(e1, "vee");
		DotAttributes.setArrowhead(e2, "tee");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWHEAD_OVERRIDE);
	}

	@Test
	public void edge_arrowsize() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.DIGRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setArrowsize, "1.5") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setArrowsize, "1.5") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWSIZE_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.DIGRAPH);
		DotAttributes.setArrowsize(e1, "2.0");
		DotAttributes.setArrowsize(e2, "2.1");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWSIZE_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.DIGRAPH);
		DotAttributes.setArrowsize(e1, "2.3");
		DotAttributes.setArrowsize(e2, "2.2");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWSIZE_OVERRIDE);
	}

	@Test
	public void edge_arrowtail() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.DIGRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setArrowtail, "box") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setArrowtail, "box") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWTAIL_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.DIGRAPH);
		DotAttributes.setArrowtail(e1, "lbox");
		DotAttributes.setArrowtail(e2, "rbox");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWTAIL_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.DIGRAPH);
		DotAttributes.setArrowtail(e1, "olbox");
		DotAttributes.setArrowtail(e2, "obox");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWTAIL_OVERRIDE);
	}

	@Test
	public void edge_color() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setColor, "0.000 0.000 1.000") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setColor, "0.000 0.000 1.000") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_COLOR_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setColor(e1, "0.000 0.000 1.000");
		DotAttributes.setColor(e2, "white");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_COLOR_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setColor(e1, "white");
		DotAttributes.setColor(e2, "0.000 0.000 1.000");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_COLOR_OVERRIDE);
	}

	@Test
	public void edge_colorscheme() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setColorscheme, "accent3") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setColorscheme, "accent3") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_COLORSCHEME_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setColorscheme(e1, "accent3");
		DotAttributes.setColorscheme(e2, "accent4");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_COLORSCHEME_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setColorscheme(e1, "accent4");
		DotAttributes.setColorscheme(e2, "accent3");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_COLORSCHEME_OVERRIDE);
	}

	@Test
	public void edge_dir() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.DIGRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setDir, "forward") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setDir, "forward") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_DIR_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.DIGRAPH);
		DotAttributes.setDir(e1, "forward");
		DotAttributes.setDir(e2, "back");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_DIR_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.DIGRAPH);
		DotAttributes.setDir(e1, "both");
		DotAttributes.setDir(e2, "back");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_DIR_OVERRIDE);
	}

	@Test
	public void edge_fillcolor() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setFillcolor, "0.000 0.000 0.000") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setFillcolor, "0.000 0.000 0.000") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_FILLCOLOR_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setFillcolor(e1, "0.000 0.000 0.000");
		DotAttributes.setFillcolor(e2, "black");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_FILLCOLOR_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setFillcolor(e1, "black");
		DotAttributes.setFillcolor(e2, "0.000 0.000 0.000");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_FILLCOLOR_OVERRIDE);
	}

	@Test
	public void edge_fontcolor() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setFontcolor, "0.000 1.000 1.000") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setFontcolor, "0.000 1.000 1.000") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_FONTCOLOR_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setFontcolor(e1, "0.000 1.000 1.000");
		DotAttributes.setFontcolor(e2, "red");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_FONTCOLOR_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setFontcolor(e1, "red");
		DotAttributes.setFontcolor(e2, "0.000 1.000 1.000");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_FONTCOLOR_OVERRIDE);
	}

	@Test
	public void edge_headlabel() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setHeadlabel, "EdgeHeadLabel1") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setHeadlabel, "EdgeHeadLabel1") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_HEADLABEL_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setHeadlabel(e1, "EdgeHeadLabel2");
		DotAttributes.setHeadlabel(e2, "EdgeHeadLabel3");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_HEADLABEL_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setHeadlabel(e1, "EdgeHeadLabel5");
		DotAttributes.setHeadlabel(e2, "EdgeHeadLabel4");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_HEADLABEL_OVERRIDE);
	}

	@Test
	public void edge_headlp() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setHeadLp, "2.2,3.3") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setHeadLp, "-2.2,-3.3") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_HEAD_LP_LOCAL);
	}

	@Test
	public void edge_id() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setId, "edgeID2") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setId, "edgeID3") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ID_LOCAL);
	}

	@Test
	public void edge_label() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setLabel, "Edge1") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setLabel, "Edge1") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_LABEL_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setLabel(e1, "Edge1");
		DotAttributes.setLabel(e2, "Edge2");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_LABEL_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setLabel(e1, "Edge4");
		DotAttributes.setLabel(e2, "Edge3");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_LABEL_OVERRIDE);
	}

	@Test
	public void edge_labelfontcolor() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setLabelfontcolor, "0.482 0.714 0.878") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setLabelfontcolor, "0.482 0.714 0.878") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_LABELFONTCOLOR_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setLabelfontcolor(e1, "0.482 0.714 0.878");
		DotAttributes.setLabelfontcolor(e2, "turquoise");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_LABELFONTCOLOR_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setLabelfontcolor(e1, "turquoise");
		DotAttributes.setLabelfontcolor(e2, "0.482 0.714 0.878");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_LABELFONTCOLOR_OVERRIDE);
	}

	@Test
	public void edge_lp() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setLp, "0.3,0.4") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setLp, "0.5,0.6") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_LP_LOCAL);
	}

	@Test
	public void edge_pos() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setPos, "0.0,0.0 1.0,1.0 2.0,2.0 3.0,3.0") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setPos, "4.0,4.0 5.0,5.0 6.0,6.0 7.0,7.0") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_POS_LOCAL);
	}

	@Test
	public void edge_style() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setStyle, "dashed") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setStyle, "dashed") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_STYLE_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setStyle(e1, "dashed");
		DotAttributes.setStyle(e2, "dotted");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_STYLE_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setStyle(e1, "bold, dotted");
		DotAttributes.setStyle(e2, "bold");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_STYLE_OVERRIDE);
	}

	@Test
	public void edge_taillabel() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setTaillabel, "EdgeTailLabel1") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setTaillabel, "EdgeTailLabel1") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_TAILLABEL_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setTaillabel(e1, "EdgeTailLabel2");
		DotAttributes.setTaillabel(e2, "EdgeTailLabel3");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_TAILLABEL_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setTaillabel(e1, "EdgeTailLabel5");
		DotAttributes.setTaillabel(e2, "EdgeTailLabel4");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_TAILLABEL_OVERRIDE);
	}

	@Test
	public void edge_taillp() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setTailLp, "-4.5,-6.7") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setTailLp, "-8.9,-10.11") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_TAIL_LP_LOCAL);
	}

	@Test
	public void edge_xlabel() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setXlabel, "EdgeExternalLabel1") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setXlabel, "EdgeExternalLabel1") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_XLABEL_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setXlabel(e1, "EdgeExternalLabel2");
		DotAttributes.setXlabel(e2, "EdgeExternalLabel3");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_XLABEL_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setXlabel(e1, "EdgeExternalLabel5");
		DotAttributes.setXlabel(e2, "EdgeExternalLabel4");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_XLABEL_OVERRIDE);
	}

	@Test
	public void edge_xlp() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes::setXlp, ".3,.4") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes::setXlp, ".5,.6") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_XLP_LOCAL);
	}

	@Test
	public void graph_bgcolor() {
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		graph.attr(DotAttributes::setBgcolor, "gray");
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1")
				.buildNode();
		Graph expected = graph.nodes(n1).build();
		testStringImport(expected, DotTestGraphs.GRAPH_BGCOLOR_LOCAL);
	}

	@Test
	public void graph_fontcolor() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		graph.attr(DotAttributes::setFontcolor, "aquamarine");
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1")
				.buildNode();
		Graph expected = graph.nodes(n1).build();
		testStringImport(expected, DotTestGraphs.GRAPH_FONTCOLOR_GLOBAL);

		// test local attribute
		DotAttributes.setFontcolor(expected, "red");
		testStringImport(expected, DotTestGraphs.GRAPH_FONTCOLOR_LOCAL);
	}

	@Test
	public void node_color() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1")
				// $NON-NLS-1$
				.attr(DotAttributes::setColor, "#ffffff").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2")
				// $NON-NLS-1$
				.attr(DotAttributes::setColor, "#ffffff").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_COLOR_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setColor(n1, "#ff0000");
		DotAttributes.setColor(n2, "#00ffff");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_COLOR_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setColor(n1, "#00ff00");
		DotAttributes.setColor(n2, "#ff0000");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_COLOR_OVERRIDE);
	}

	@Test
	public void node_colorscheme() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node n1 = new Node.Builder()
				.attr(DotAttributes::_setNameRaw,
						ID.fromValue("1", Type.STRING))
				// $NON-NLS-1$
				.attr(DotAttributes::setColorscheme, "accent5").buildNode();
		Node n2 = new Node.Builder()
				.attr(DotAttributes::_setNameRaw,
						ID.fromValue("2", Type.STRING))
				// $NON-NLS-1$
				.attr(DotAttributes::setColorscheme, "accent5").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_COLORSCHEME_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setColorscheme(n1, "accent5");
		DotAttributes.setColorscheme(n2, "accent6");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_COLORSCHEME_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setColorscheme(n1, "accent6");
		DotAttributes.setColorscheme(n2, "accent5");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_COLORSCHEME_OVERRIDE);
	}

	@Test
	public void node_distortion() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.attr(DotAttributes::setDistortion, "1.1").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.attr(DotAttributes::setDistortion, "1.1").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_DISTORTION_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setDistortion(n1, "1.2");
		DotAttributes.setDistortion(n2, "1.3");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_DISTORTION_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setDistortion(n1, "1.5");
		DotAttributes.setDistortion(n2, "1.4");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_DISTORTION_OVERRIDE);
	}

	@Test
	public void node_fillcolor() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1")
				// $NON-NLS-1$
				.attr(DotAttributes::setFillcolor, "0.3 .8 .7").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2")
				// $NON-NLS-1$
				.attr(DotAttributes::setFillcolor, "0.3 .8 .7").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_FILLCOLOR_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setFillcolor(n1, "0.3 .8 .7");
		DotAttributes.setFillcolor(n2, "/bugn9/7");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_FILLCOLOR_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setFillcolor(n1, "/bugn9/7");
		DotAttributes.setFillcolor(n2, "0.3 .8 .7");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_FILLCOLOR_OVERRIDE);
	}

	@Test
	public void node_fixedsize() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.attr(DotAttributes.FIXEDSIZE__N, "true").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.attr(DotAttributes.FIXEDSIZE__N, "true").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_FIXEDSIZE_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setFixedsizeParsed(n1, true);
		DotAttributes.setFixedsizeParsed(n2, false);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_FIXEDSIZE_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setFixedsizeParsed(n1, false);
		DotAttributes.setFixedsizeParsed(n2, true);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_FIXEDSIZE_OVERRIDE);
	}

	@Test
	public void node_fontcolor() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1")
				// $NON-NLS-1$
				.attr(DotAttributes::setFontcolor, "0.3, .8, .7").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2")
				// $NON-NLS-1$
				.attr(DotAttributes::setFontcolor, "0.3, .8, .7").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_FONTCOLOR_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setFontcolor(n1, "0.3, .8, .7");
		DotAttributes.setFontcolor(n2, "/brbg11/10");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_FONTCOLOR_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setFontcolor(n1, "/brbg11/10");
		DotAttributes.setFontcolor(n2, "0.3, .8, .7");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_FONTCOLOR_OVERRIDE);
	}

	@Test
	public void node_height() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.attr(DotAttributes::setHeight, "1.2").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.attr(DotAttributes::setHeight, "1.2").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_HEIGHT_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setHeightParsed(n1, 3.4);
		DotAttributes.setHeightParsed(n2, 5.6);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_HEIGHT_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setHeightParsed(n1, 9.11);
		DotAttributes.setHeightParsed(n2, 7.8);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_HEIGHT_OVERRIDE);
	}

	@Test
	public void node_id() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.attr(DotAttributes::setId, "NodeID1").buildNode(); //$NON-NLS-1$ .buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.attr(DotAttributes::setId, "NodeID2").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_ID_LOCAL);
	}

	@Test
	public void node_label() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.attr(DotAttributes::setLabel, "Node1").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.attr(DotAttributes::setLabel, "Node1").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_LABEL_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setLabel(n1, "Node1");
		DotAttributes.setLabel(n2, "Node2");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_LABEL_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setLabel(n1, "Node4");
		DotAttributes.setLabel(n2, "Node3");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_LABEL_OVERRIDE);

		// test override attribute2
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Edge e = new Edge.Builder(n1, n2).buildEdge();
		expected = graph.nodes(n1, n2).edges(e).build();
		testStringImport(expected, DotTestGraphs.NODE_LABEL_OVERRIDE2);

		// test override attribute3
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node[] nodes = createNodes();
		DotAttributes.setLabel(nodes[1], "Node1");
		DotAttributes.setLabel(nodes[2], "Node2");
		DotAttributes.setLabel(nodes[3], "Node3");
		Edge e1 = new Edge.Builder(nodes[0], nodes[1]).buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3]).buildEdge();
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.NODE_LABEL_OVERRIDE3);
	}

	@Test
	public void node_pos() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.attr(DotAttributes::setPos, ".1,.2!").buildNode(); //$NON-NLS-1$ .buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.attr(DotAttributes::setPos, "-0.1,-2.3!").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_POS_LOCAL);
	}

	@Test
	public void node_shape() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.attr(DotAttributes::setShape, "box").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.attr(DotAttributes::setShape, "box").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SHAPE_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setShape(n1, "oval");
		DotAttributes.setShape(n2, "house");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SHAPE_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setShape(n1, "circle");
		DotAttributes.setShape(n2, "pentagon");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SHAPE_OVERRIDE);
	}

	@Test
	public void node_sides() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.attr(DotAttributes::setSides, "3").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.attr(DotAttributes::setSides, "3").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SIDES_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setSidesParsed(n1, 4);
		DotAttributes.setSidesParsed(n2, 5);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SIDES_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setSidesParsed(n1, 7);
		DotAttributes.setSidesParsed(n2, 6);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SIDES_OVERRIDE);
	}

	@Test
	public void node_skew() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.attr(DotAttributes::setSkew, "1.2").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.attr(DotAttributes::setSkew, "1.2").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SKEW_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setSkewParsed(n1, 3.4);
		DotAttributes.setSkewParsed(n2, 5.6);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SKEW_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setSkewParsed(n1, -7.8);
		DotAttributes.setSkewParsed(n2, 7.8);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SKEW_OVERRIDE);
	}

	@Test
	public void node_style() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.attr(DotAttributes::setStyle, "solid, dashed").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.attr(DotAttributes::setStyle, "solid, dashed").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_STYLE_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setStyle(n1, "bold");
		DotAttributes.setStyle(n2, "dotted");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_STYLE_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setStyle(n1, "rounded");
		DotAttributes.setStyle(n2, "bold, filled");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_STYLE_OVERRIDE);
	}

	@Test
	public void node_width() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.attr(DotAttributes::setWidth, "1.2").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.attr(DotAttributes::setWidth, "1.2").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_WIDTH_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setWidthParsed(n1, 3.4);
		DotAttributes.setWidthParsed(n2, 5.6);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_WIDTH_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setWidthParsed(n1, 9.11);
		DotAttributes.setWidthParsed(n2, 7.8);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_WIDTH_OVERRIDE);
	}

	@Test
	public void node_xlabel() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.attr(DotAttributes::setXlabel, "NodeExternalLabel1")
				.buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.attr(DotAttributes::setXlabel, "NodeExternalLabel1")
				.buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_XLABEL_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setXlabel(n1, "NodeExternalLabel2");
		DotAttributes.setXlabel(n2, "NodeExternalLabel3");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_XLABEL_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		DotAttributes.setXlabel(n1, "NodeExternalLabel5");
		DotAttributes.setXlabel(n2, "NodeExternalLabel4");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_XLABEL_OVERRIDE);
	}

	@Test
	public void node_xlp() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes::_setType,
				GraphType.GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.attr(DotAttributes::setXlp, "-0.3,-0.4").buildNode(); //$NON-NLS-1$ .buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.attr(DotAttributes::setXlp, "-1.5,-1.6").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_XLP_LOCAL);
	}

	private Node[] createNodes() {
		Node n1 = new Node.Builder().attr(DotAttributes::_setName, "1") //$NON-NLS-1$
				.buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes::_setName, "2") //$NON-NLS-1$
				.buildNode();
		Node n3 = new Node.Builder().attr(DotAttributes::_setName, "3") //$NON-NLS-1$
				.buildNode();
		Node n4 = new Node.Builder().attr(DotAttributes::_setName, "4") //$NON-NLS-1$
				.buildNode();
		return new Node[] { n1, n2, n3, n4 };
	}

	// TODO: Generalize to multiple graphs
	private Graph importFile(final File dotFile) {
		Assert.assertTrue("DOT input file must exist: " + dotFile, //$NON-NLS-1$
				dotFile.exists());
		List<Graph> graphs = dotImport.importDot(dotFile);
		return graphs.isEmpty() ? null : graphs.get(0);
	}

	// TODO: Generalize to multiple graphs
	private Graph importString(final String dotString) {
		List<Graph> graphs = dotImport.importDot(dotString);
		return graphs.isEmpty() ? null : graphs.get(0);
	}

	private void testStringImport(Graph expected, String dot) {
		List<Graph> graphs = dotImport.importDot(dot);
		Assert.assertEquals("Expected one graph", 1, graphs.size()); //$NON-NLS-1$
		Assert.assertEquals(expected.toString(), graphs.get(0).toString());
	}
}
