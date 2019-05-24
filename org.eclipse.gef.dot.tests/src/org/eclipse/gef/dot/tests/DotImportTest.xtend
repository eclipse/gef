/*******************************************************************************
 * Copyright (c) 2009, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg                   - initial API and implementation (see bug #277380)
 *     Tamas Miklossy  (itemis AG)    - implement additional test cases (bug #493136)
 *                                    - merge DotInterpreter into DotImport (bug #491261)
 *     Zoey Gerrit Prigge (itemis AG) - implement additional dot attributes (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import java.io.File
import org.eclipse.gef.dot.internal.DotImport
import org.eclipse.gef.dot.internal.language.DotInjectorProvider
import org.eclipse.gef.dot.internal.language.dot.GraphType
import org.eclipse.gef.dot.internal.language.layout.Layout
import org.eclipse.gef.dot.internal.language.rankdir.Rankdir
import org.eclipse.gef.dot.internal.language.terminals.ID
import org.eclipse.gef.dot.internal.language.terminals.ID.Type
import org.eclipse.gef.graph.Edge
import org.eclipse.gef.graph.Graph
import org.eclipse.gef.graph.Node
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.junit.Assert
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.dot.internal.DotAttributes.*
import static extension org.eclipse.gef.dot.tests.DotTestUtils.file
import static extension org.junit.Assert.assertEquals
import static extension org.junit.Assert.assertNotNull

/** 
 * Tests for the {@link DotImport} class.
 * @author Fabian Steeg (fsteeg)
 */
@RunWith(XtextRunner)
@InjectWith(DotInjectorProvider)
class DotImportTest {

	@Rule public val rule = new DotSubgrammarPackagesRegistrationRule

	val dotImport = new DotImport
	val prettyPrinter = new DotGraphPrettyPrinter

	/**
	 * Test valid graphs can be imported without exceptions.
	 */
	@Test def void sample_graphs_file_import() {
		"simple_graph.dot".assertFileImportedTo(DotTestUtils.simpleGraph)
		"simple_digraph.dot".assertFileImportedTo(DotTestUtils.simpleDiGraph)
		"labeled_graph.dot".assertFileImportedTo(DotTestUtils.labeledGraph)
		"styled_graph.dot".assertFileImportedTo(DotTestUtils.styledGraph)
		
		DotTestGraphs.GLOBAL_EDGE_NODE_COLORSCHEME.importString
	}

	/**
	 * Test error handling for invalid graph.
	 */
	@Test(expected=IllegalArgumentException) def void invalid_graph_file_import() {
		'''
			graph Sample {
		'''.importString
	}

	@Test(expected=IllegalArgumentException) def void faulty_layout() {
		'''
			graph Sample {
				graph[layout=cool]
				1
			}
		'''.importString
	}

	@Test def digraph_type() {
		val graph = DotTestGraphs.TWO_NODES_ONE_DIRECTED_EDGE.importString
		GraphType.DIGRAPH.assertEquals(graph._getType)
	}

	@Test def graphType() {
		val graph = DotTestGraphs.TWO_NODES_ONE_EDGE.importString
		GraphType.GRAPH.assertEquals(graph._getType)
	}

	@Test def node_default_label() {
		val graph = DotTestGraphs.ONE_NODE.importString
		"1".assertEquals(graph.nodes.head._getName)
	}

	@Test def node_count() {
		val graph = DotTestGraphs.TWO_NODES.importString
		2.assertEquals(graph.nodes.size)
	}

	@Test def edge_count() {
		val graph = DotTestGraphs.TWO_NODES_AND_THREE_EDGES.importString
		3.assertEquals(graph.edges.size)
	}

	@Test def layout_dot() {
		val graph = new Graph.Builder().
			attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH).
			attr([p1,p2|p1.layoutParsed=p2], Layout.DOT)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).buildEdge
		val e2 = new Edge.Builder(nodes.get(1), nodes.get(2)).buildEdge
		val e3 = new Edge.Builder(nodes.get(1), nodes.get(3)).buildEdge
		val expected = graph.nodes(nodes).edges(e1, e2, e3).build
		
		DotTestGraphs.GRAPH_LAYOUT_DOT.assertImportedTo(expected)
	}

	@Test def layout_fdp() {
		val graph = new Graph.Builder().
			attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH).
			attr([p1,p2|p1.layoutParsed=p2], Layout.FDP)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).buildEdge
		val e2 = new Edge.Builder(nodes.get(1), nodes.get(2)).buildEdge
		val e3 = new Edge.Builder(nodes.get(1), nodes.get(3)).buildEdge
		val expected = graph.nodes(nodes).edges(e1, e2, e3).build
		
		DotTestGraphs.GRAPH_LAYOUT_FDP.assertImportedTo(expected)
	}

	@Test def layout_osage() {
		val graph = new Graph.Builder().
			attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH).
			attr([p1,p2|p1.layoutParsed=p2], Layout.OSAGE)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).buildEdge
		val e2 = new Edge.Builder(nodes.get(1), nodes.get(2)).buildEdge
		val e3 = new Edge.Builder(nodes.get(1), nodes.get(3)).buildEdge
		val expected = graph.nodes(nodes).edges(e1, e2, e3).build
		
		DotTestGraphs.GRAPH_LAYOUT_OSAGE.assertImportedTo(expected)
	}

	@Test def layout_twopi() {
		val graph = new Graph.Builder().
			attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH).
			attr([p1,p2|p1.layoutParsed=p2], Layout.TWOPI)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).buildEdge
		val e2 = new Edge.Builder(nodes.get(1), nodes.get(2)).buildEdge
		val e3 = new Edge.Builder(nodes.get(1), nodes.get(3)).buildEdge
		val expected = graph.nodes(nodes).edges(e1, e2, e3).build
		
		DotTestGraphs.GRAPH_LAYOUT_TWOPI.assertImportedTo(expected)
	}

	@Test def layout_dot_horizontal() {
		val graph = DotTestGraphs.GRAPH_LAYOUT_DOT_HORIZONTAL.importString
		Layout.DOT.toString.assertEquals(graph.layout)
		Rankdir.LR.assertEquals(graph.rankdirParsed)
	}

	@Test def layout_horizontal_tree_via_attribute() {
		val graph = DotTestGraphs.GRAPH_RANKDIR_LR.importString
		Rankdir.LR.assertEquals(graph.rankdirParsed)
	}

	@Test def global_node_attribute_adhoc_nodes() {
		val graph = DotTestGraphs.GLOBAL_NODE_LABEL_AD_HOC_NODES.importString
		"TEXT".assertEquals(graph.nodes.head.label)
	}

	@Test def global_edge_attribute_adhoc_nodes() {
		val graph = DotTestGraphs.GLOBAL_EDGE_LABEL_AD_HOC_NODES.importString
		"TEXT".assertEquals(graph.edges.head.label)
	}

	@Test def header_comment_graph() {
		val graph = DotTestGraphs.HEADER_COMMENT.importString
		2.assertEquals(graph.nodes.size)
		1.assertEquals(graph.edges.size)
	}

	@Test def nodes_before_edges() {
		val graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).buildEdge
		val e2 = new Edge.Builder(nodes.get(1), nodes.get(2)).buildEdge
		val e3 = new Edge.Builder(nodes.get(1), nodes.get(3)).buildEdge
		val expected = graph.nodes(nodes).edges(e1, e2, e3).build
		
		DotTestGraphs.NODES_BEFORE_EDGES.assertImportedTo(expected)
	}

	@Test def nodes_before_edges_with_attributes() {
		val graph = new Graph.Builder().
			attr([p1,p2|p1._setName(p2)], "AttributesGraph").
			attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH).
			attr([p1,p2|p1.rankdirParsed=p2], Rankdir.LR).
			attr([p1,p2|p1.label=p2], "Left-to-Right")
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).buildEdge
		val e2 = new Edge.Builder(nodes.get(0), nodes.get(2)).buildEdge
		val e3 = new Edge.Builder(nodes.get(1), nodes.get(3)).buildEdge
		val expected = graph.nodes(nodes).edges(e1, e2, e3).build
		
		DotTestGraphs.NODES_BEFORE_EDGES_WITH_ATTRIBUTES.assertImportedTo(expected)
	}

	@Test def directed_styled_graph() {
		val graph = new Graph.Builder().
			attr([p1,p2|p1._setName(p2)], "DirectedStyledGraph").
			attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).
			attr([p1,p2|p1.label=p2], "Edge").
			attr([p1,p2|p1.style=p2], "dashed").buildEdge
		val e2 = new Edge.Builder(nodes.get(1), nodes.get(2)).
			attr([p1,p2|p1.style=p2], "dotted").buildEdge
		e2.labelRaw = ID.fromValue("Dotted", Type.QUOTED_STRING)
		val e3 = new Edge.Builder(nodes.get(1), nodes.get(3)).
			attr([p1,p2|p1.label=p2], "Edge").
			attr([p1,p2|p1.style=p2], "dashed").buildEdge
		val expected = graph.nodes(nodes).edges(e1, e2, e3).build
		
		DotTestGraphs.DIRECTED_STYLED_GRAPH.assertImportedTo(expected)
	}

	@Test def nodes_after_edges() {
		val graph = new Graph.Builder().
			attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		nodes.head.label="node"
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).buildEdge
		val e2 = new Edge.Builder(nodes.get(1), nodes.get(2)).buildEdge
		val e3 = new Edge.Builder(nodes.get(1), nodes.get(3)).buildEdge
		val expected = graph.nodes(nodes).edges(e1, e2, e3).build
		
		DotTestGraphs.NODES_AFTER_EDGES.assertImportedTo(expected)
	}

	@Test def use_dot_importer_twice() {
		val dot = DotTestGraphs.NODES_AFTER_EDGES
		var graph = dot.importString
		graph = dot.importString
		
		4.assertEquals(graph.nodes.size)
		3.assertEquals(graph.edges.size)
	}

	@Test def ids_with_quotes() {
		val graph = DotTestGraphs.IDS_WITH_QUOTES.importString
		val nodes = graph.nodes
		"node 1".assertEquals(nodes.get(0)._getName)
		"node 2".assertEquals(nodes.get(1)._getName)
	}

	@Test def escaped_quotes() {
		val graph = DotTestGraphs.ESCAPED_QUOTES_LABEL.importString
		'node "1"'.assertEquals(graph.nodes.head.label)
	}

	@Test def multiline_quoted_id() {
		val graph = DotTestGraphs.MULTILINE_QUOTED_IDS.importString
		"node 1".assertEquals(graph.nodes.head.label)
	}

	@Test def fully_quoted() {
		val graph = DotTestGraphs.FULLY_QUOTED_IDS.importString
		2.assertEquals(graph.nodes.size)
		1.assertEquals(graph.edges.size)
		
		val nodes = graph.nodes
		"n1".assertEquals(nodes.get(0)._getName)
		"n2".assertEquals(nodes.get(1)._getName)
	}

	@Test def labels_with_quotes() {
		val graph = DotTestGraphs.QUOTED_LABELS.importString
		val nodes = graph.nodes
		"node 1".assertEquals(nodes.get(0).label)
		"node 2".assertEquals(nodes.get(1).label)
		"edge 1".assertEquals(graph.edges.head.label)
	}

	@Test def labels_with_quotes2() {
		val graph = new Graph.Builder().
			attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		nodes.get(0).label = "node"
		nodes.get(0).xlabel = "Node"
		nodes.get(1).label = "foo bar"
		nodes.get(2).label = "foo"
		nodes.get(3).labelRaw = ID.fromValue("foo", Type.QUOTED_STRING)
		val expected = graph.nodes(nodes).build
		
		DotTestGraphs.QUOTED_LABELS2.assertImportedTo(expected)
	}

	@Test def newlines_in_labels() {
		val graph = DotTestGraphs.NEW_LINES_IN_LABELS.importString
		'''node«System.lineSeparator»1'''.toString.assertEquals(graph.nodes.head.label)
	}

	@Test def multi_edge_statements() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.arrowhead=p2], "ornormal").buildEdge
		val e2 = new Edge.Builder(nodes.get(1), nodes.get(2)).attr([p1,p2|p1.arrowhead=p2], "ornormal").buildEdge
		val e3 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.arrowhead=p2], "ornormal").buildEdge
		val e4 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.arrowhead=p2], "ornormal").buildEdge
		val e5 = new Edge.Builder(nodes.get(1), nodes.get(2)).attr([p1,p2|p1.arrowhead=p2], "ornormal").buildEdge
		val e6 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.arrowhead=p2], "ornormal").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2, e3, e4, e5, e6).build
		
		DotTestGraphs.MULTI_EDGE_STATEMENTS_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		e4.arrowhead = "olnormal"
		e5.arrowhead = "olnormal"
		e6.arrowhead = "olnormal"
		expected = graph.nodes(nodes).edges(e1, e2, e3, e4, e5, e6).build
		
		DotTestGraphs.MULTI_EDGE_STATEMENTS_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		DotTestGraphs.MULTI_EDGE_STATEMENTS_OVERRIDE.assertImportedTo(expected)
	}

	@Test def compass_points_as_node_names() {
		val graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "n").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "ne").buildNode
		val n3 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "e").buildNode
		val n4 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "se").buildNode
		val n5 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "s").buildNode
		val n6 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "sw").buildNode
		val n7 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "w").buildNode
		val n8 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "nw").buildNode
		val n9 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "c").buildNode
		val n10 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "_").buildNode
		val expected = graph.nodes(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10).build
		
		DotTestGraphs.COMPASS_POINTS_AS_NODE_NAMES.assertImportedTo(expected)
	}

	@Ignore @Test def node_groups() {
		// TODO: implement as soon as the EdgeStmtNode is properly imported
		val graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").buildNode
		val n3 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "3").buildNode
		val n4 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "foo").
			attr([p1,p2|p1.shape=p2], "box").buildNode
		val n5 = new Node.Builder().
			attr([p1,p2|p1._setName(p2)], "bar").
			attr([p1,p2|p1.shape=p2], "box").buildNode
		val n6 = new Node.Builder().
			attr([p1,p2|p1._setName(p2)], "baz").
			attr([p1,p2|p1.shape=p2], "box").buildNode
		val expected = graph.nodes(n1, n2, n3, n4, n5, n6).build
		
		DotTestGraphs.NODE_GROUPS.assertImportedTo(expected)
	}

	@Test def edge_style_invis() {
		val graph = DotTestGraphs.EDGE_STYLE_INVIS.importString
		2.assertEquals(graph.nodes.size)
		1.assertEquals(graph.edges.size)
	}

	@Test def edge_arrowhead() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.arrowhead=p2], "crow").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.arrowhead=p2], "crow").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_ARROWHEAD_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		e1.arrowhead = "diamond"
		e2.arrowhead = "dot"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_ARROWHEAD_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		e1.arrowhead = "vee"
		e2.arrowhead = "tee"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_ARROWHEAD_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_arrowsize() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.arrowsize=p2], "1.5").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.arrowsize=p2], "1.5").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_ARROWSIZE_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		e1.arrowsize = "2.0"
		e2.arrowsize = "2.1"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_ARROWSIZE_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		e1.arrowsize = "2.3"
		e2.arrowsize = "2.2"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_ARROWSIZE_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_arrowtail() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.arrowtail=p2], "box").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.arrowtail=p2], "box").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_ARROWTAIL_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		e1.arrowtail = "lbox"
		e2.arrowtail = "rbox"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_ARROWTAIL_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		e1.arrowtail = "olbox"
		e2.arrowtail = "obox"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_ARROWTAIL_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_color() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.color=p2], "0.000 0.000 1.000").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.color=p2], "0.000 0.000 1.000").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_COLOR_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.color = "0.000 0.000 1.000"
		e2.color = "white"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_COLOR_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.color = "white"
		e2.color = "0.000 0.000 1.000"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_COLOR_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_colorscheme() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.colorscheme=p2], "accent3").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.colorscheme=p2], "accent3").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_COLORSCHEME_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.colorscheme = "accent3"
		e2.colorscheme = "accent4"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_COLORSCHEME_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.colorscheme = "accent4"
		e2.colorscheme = "accent3"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_COLORSCHEME_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_dir() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.dir=p2], "forward").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.dir=p2], "forward").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_DIR_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		e1.dir = "forward"
		e2.dir = "back"
		expected = graph.nodes(nodes).edges(e1, e2).build
		DotTestGraphs.EDGE_DIR_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		e1.dir = "both"
		e2.dir = "back"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_DIR_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_edgetooltip() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.edgetooltip=p2], "a").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.edgetooltip=p2], "a").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_EDGETOOLTIP_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.edgetooltip = "b"
		e2.edgetooltip = "c"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_EDGETOOLTIP_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.edgetooltip = "e"
		e2.edgetooltip = "d"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_EDGETOOLTIP_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_fillcolor() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.fillcolor=p2], "0.000 0.000 0.000").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.fillcolor=p2], "0.000 0.000 0.000").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_FILLCOLOR_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.fillcolor = "0.000 0.000 0.000"
		e2.fillcolor = "black"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_FILLCOLOR_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.fillcolor = "black"
		e2.fillcolor = "0.000 0.000 0.000"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_FILLCOLOR_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_fontcolor() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.fontcolor=p2], "0.000 1.000 1.000").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.fontcolor=p2], "0.000 1.000 1.000").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_FONTCOLOR_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.fontcolor = "0.000 1.000 1.000"
		e2.fontcolor = "red"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_FONTCOLOR_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.fontcolor = "red"
		e2.fontcolor = "0.000 1.000 1.000"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_FONTCOLOR_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_fontname() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.fontname=p2], "Font1").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.fontname=p2], "Font1").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_FONTNAME_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.fontname = "Font1"
		e2.fontname = "Font2"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_FONTNAME_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.fontname = "Font3"
		e2.fontname = "Font4"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_FONTNAME_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_fontsize() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.fontsize=p2], "1.1").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.fontsize=p2], "1.1").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_FONTSIZE_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.fontsize = "1.1"
		e2.fontsize = "1.2"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_FONTSIZE_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.fontsize = "1.3"
		e2.fontsize = "1.4"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_FONTSIZE_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_headlabel() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.headlabel=p2], "EdgeHeadLabel1").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.headlabel=p2], "EdgeHeadLabel1").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_HEADLABEL_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.headlabel = "EdgeHeadLabel2"
		e2.headlabel = "EdgeHeadLabel3"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_HEADLABEL_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.headlabel = "EdgeHeadLabel5"
		e2.headlabel = "EdgeHeadLabel4"
		expected = graph.nodes(nodes).edges(e1, e2).build
		DotTestGraphs.EDGE_HEADLABEL_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_headlp() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		val graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.headLp=p2], "2.2,3.3").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.headLp=p2], "-2.2,-3.3").buildEdge
		val expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_HEAD_LP_LOCAL.assertImportedTo(expected)
	}

	@Test def edge_headport() {
		
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.headport=p2], "port5:nw").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.headport=p2], "port5:nw").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_HEADPORT_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.headport = "port1:w"
		e2.headport = "port2:e"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_HEADPORT_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.headport = "port1:w"
		e2.headport = "port5:nw"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_HEADPORT_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_headtooltip() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.headtooltip=p2], "a").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.headtooltip=p2], "a").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_HEADTOOLTIP_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		e1.headtooltip = "b"
		e2.headtooltip = "c"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_HEADTOOLTIP_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		e1.headtooltip = "e"
		e2.headtooltip = "d"
		expected = graph.nodes(nodes).edges(e1, e2).build

		DotTestGraphs.EDGE_HEADTOOLTIP_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_id() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		val graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.id=p2], "edgeID2").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.id=p2], "edgeID3").buildEdge
		val expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_ID_LOCAL.assertImportedTo(expected)
	}

	@Test def edge_label() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.label=p2], "Edge1").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.label=p2], "Edge1").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_LABEL_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.label = "Edge1"
		e2.label = "Edge2"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_LABEL_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.label = "Edge4"
		e2.label = "Edge3"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_LABEL_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_labelfontcolor() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.labelfontcolor=p2],"0.482 0.714 0.878").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.labelfontcolor=p2],"0.482 0.714 0.878").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_LABELFONTCOLOR_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.labelfontcolor = "0.482 0.714 0.878"
		e2.labelfontcolor = "turquoise"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_LABELFONTCOLOR_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.labelfontcolor = "turquoise"
		e2.labelfontcolor = "0.482 0.714 0.878"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_LABELFONTCOLOR_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_labelfontname() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.labelfontname=p2], "Font1").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.labelfontname=p2], "Font1").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_LABELFONTNAME_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.labelfontname = "Font1"
		e2.labelfontname = "Font2"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_LABELFONTNAME_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.labelfontname = "Font3"
		e2.labelfontname = "Font4"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_LABELFONTNAME_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_labelfontsize() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.labelfontsize=p2], "1.1").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.labelfontsize=p2], "1.1").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_LABELFONTSIZE_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.labelfontsize = "1.1"
		e2.labelfontsize = "1.2"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_LABELFONTSIZE_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.labelfontsize = "1.3"
		e2.labelfontsize = "1.4"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_LABELFONTSIZE_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_labeltooltip() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.labeltooltip=p2], "a").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.labeltooltip=p2], "a").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_LABELTOOLTIP_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.labeltooltip = "b"
		e2.labeltooltip = "c"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_LABELTOOLTIP_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.labeltooltip = "e"
		e2.labeltooltip = "d"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_LABELTOOLTIP_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_lp() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		val graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.lp=p2], "0.3,0.4").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.lp=p2], "0.5,0.6").buildEdge
		val expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_LP_LOCAL.assertImportedTo(expected)
	}

	@Test def edge_pos() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		val graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.pos=p2], "0.0,0.0 1.0,1.0 2.0,2.0 3.0,3.0").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.pos=p2], "4.0,4.0 5.0,5.0 6.0,6.0 7.0,7.0").buildEdge
		val expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_POS_LOCAL.assertImportedTo(expected)
	}

	@Test def edge_penwidth() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.penwidth=p2], "1.5").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.penwidth=p2], "1.5").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_PENWIDTH_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.penwidth = "2.5"
		e2.penwidth = "3.0"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_PENWIDTH_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.penwidth = "3.5"
		e2.penwidth = "4.0"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_PENWIDTH_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_style() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.style=p2], "dashed").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.style=p2], "dashed").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_STYLE_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.style = "dashed"
		e2.style = "dotted"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_STYLE_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.style = "bold, dotted"
		e2.style = "bold"
		expected = graph.nodes(nodes).edges(e1, e2).build
		DotTestGraphs.EDGE_STYLE_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_taillabel() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.taillabel=p2], "EdgeTailLabel1").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.taillabel=p2], "EdgeTailLabel1").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_TAILLABEL_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.taillabel = "EdgeTailLabel2"
		e2.taillabel = "EdgeTailLabel3"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_TAILLABEL_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.taillabel = "EdgeTailLabel5"
		e2.taillabel = "EdgeTailLabel4"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_TAILLABEL_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_taillp() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		val graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.tailLp=p2], "-4.5,-6.7").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.tailLp=p2], "-8.9,-10.11").buildEdge
		val expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_TAIL_LP_LOCAL.assertImportedTo(expected)
	}

	@Test def edge_tailport() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.tailport=p2], "port5:nw").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.tailport=p2], "port5:nw").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_TAILPORT_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.tailport = "port1:w"
		e2.tailport = "port2:e"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_TAILPORT_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.tailport = "port1:w"
		e2.tailport = "port5:nw"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_TAILPORT_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_tailtooltip() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.tailtooltip=p2], "a").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.tailtooltip=p2], "a").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_TAILTOOLTIP_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		e1.tailtooltip = "b"
		e2.tailtooltip = "c"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_TAILTOOLTIP_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		e1.tailtooltip = "e"
		e2.tailtooltip = "d"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_TAILTOOLTIP_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_tooltip() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.tooltip=p2], "a").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.tooltip=p2], "a").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_TOOLTIP_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.tooltip = "b"
		e2.tooltip = "c"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_TOOLTIP_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.tooltip = "e"
		e2.tooltip = "d"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_TOOLTIP_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_xlabel() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.xlabel=p2], "EdgeExternalLabel1").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.xlabel=p2], "EdgeExternalLabel1").buildEdge
		var expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_XLABEL_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.xlabel = "EdgeExternalLabel2"
		e2.xlabel = "EdgeExternalLabel3"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_XLABEL_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		e1.xlabel = "EdgeExternalLabel5"
		e2.xlabel = "EdgeExternalLabel4"
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_XLABEL_OVERRIDE.assertImportedTo(expected)
	}

	@Test def edge_xlp() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		val graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val nodes = createNodes
		val e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).attr([p1,p2|p1.xlp=p2], ".3,.4").buildEdge
		val e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).attr([p1,p2|p1.xlp=p2], ".5,.6").buildEdge
		val expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.EDGE_XLP_LOCAL.assertImportedTo(expected)
	}

	@Test def graph_bgcolor() {
		// test local attribute
		val graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH).
			attr([p1,p2|p1.bgcolor=p2], "gray")
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		val expected = graph.nodes(n1).build
		
		DotTestGraphs.GRAPH_BGCOLOR_LOCAL.assertImportedTo(expected)
	}

	@Test def graph_fontcolor() {
		// test global attribute
		val graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH).
			attr([p1,p2|p1.fontcolor=p2], "aquamarine")
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		val expected = graph.nodes(n1).build
		
		DotTestGraphs.GRAPH_FONTCOLOR_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		expected.fontcolor = "red"
		
		DotTestGraphs.GRAPH_FONTCOLOR_LOCAL.assertImportedTo(expected)
	}

	@Test def node_color() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.color=p2], "#ffffff").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.color=p2], "#ffffff").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_COLOR_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.color = "#ff0000"
		n2.color = "#00ffff"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_COLOR_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.color = "#00ff00"
		n2.color = "#ff0000"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_COLOR_OVERRIDE.assertImportedTo(expected)
	}

	@Test def node_colorscheme() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setNameRaw=p2], ID.fromValue("1", Type.STRING)).
			attr([p1,p2|p1.colorscheme=p2], "accent5").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setNameRaw=p2], ID.fromValue("2", Type.STRING)).
			attr([p1,p2|p1.colorscheme=p2], "accent5").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_COLORSCHEME_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.colorscheme = "accent5"
		n2.colorscheme = "accent6"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_COLORSCHEME_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.colorscheme = "accent6"
		n2.colorscheme = "accent5"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_COLORSCHEME_OVERRIDE.assertImportedTo(expected)
	}

	@Test def node_distortion() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.distortion=p2], "1.1").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.distortion=p2], "1.1").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_DISTORTION_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.distortion = "1.2"
		n2.distortion = "1.3"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_DISTORTION_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.distortion = "1.5"
		n2.distortion = "1.4"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_DISTORTION_OVERRIDE.assertImportedTo(expected)
	}

	@Test def node_fillcolor() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.fillcolor=p2], "0.3 .8 .7").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.fillcolor=p2], "0.3 .8 .7").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_FILLCOLOR_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.fillcolor = "0.3 .8 .7"
		n2.fillcolor = "/bugn9/7"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_FILLCOLOR_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.fillcolor = "/bugn9/7"
		n2.fillcolor = "0.3 .8 .7"
		expected = graph.nodes(n1, n2).build
		DotTestGraphs.NODE_FILLCOLOR_OVERRIDE.assertImportedTo(expected)
	}

	@Test def node_fixedsize() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr(FIXEDSIZE__N, "true").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr(FIXEDSIZE__N, "true").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_FIXEDSIZE_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.fixedsizeParsed = true
		n2.fixedsizeParsed = false
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_FIXEDSIZE_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.fixedsizeParsed = false
		n2.fixedsizeParsed = true
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_FIXEDSIZE_OVERRIDE.assertImportedTo(expected)
	}

	@Test def node_fontcolor() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.fontcolor=p2], "0.3, .8, .7").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.fontcolor=p2], "0.3, .8, .7").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_FONTCOLOR_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.fontcolor = "0.3, .8, .7"
		n2.fontcolor = "/brbg11/10"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_FONTCOLOR_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.fontcolor = "/brbg11/10"
		n2.fontcolor = "0.3, .8, .7"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_FONTCOLOR_OVERRIDE.assertImportedTo(expected)
	}

	@Test def node_fontname() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.fontname=p2], "Font1").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.fontname=p2], "Font1").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_FONTNAME_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.fontname = "Font1"
		n2.fontname = "Font2"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_FONTNAME_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.fontname = "Font3"
		n2.fontname = "Font4"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_FONTNAME_OVERRIDE.assertImportedTo(expected)
	}

	@Test def node_fontsize() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.fontsize=p2], "1.1").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.fontsize=p2], "1.1").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_FONTSIZE_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.fontsize = "1.1"
		n2.fontsize = "1.2"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_FONTSIZE_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.fontsize = "1.3"
		n2.fontsize = "1.4"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_FONTSIZE_OVERRIDE.assertImportedTo(expected)
	}

	@Test def node_height() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.height=p2], "1.2").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.height=p2], "1.2").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_HEIGHT_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.heightParsed = 3.4
		n2.heightParsed = 5.6
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_HEIGHT_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.heightParsed = 9.11
		n2.heightParsed = 7.8
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_HEIGHT_OVERRIDE.assertImportedTo(expected)
	}

	@Test def node_id() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		val graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.id=p2], "NodeID1").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.id=p2], "NodeID2").buildNode
		val expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_ID_LOCAL.assertImportedTo(expected)
	}

	@Test def node_label() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.label=p2], "Node1").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.label=p2], "Node1").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_LABEL_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.label = "Node1"
		n2.label = "Node2"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_LABEL_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.label = "Gültig"
		n2.label = "Käse"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_LABEL_OVERRIDE.assertImportedTo(expected)
		
		// test override attribute2
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val e = new Edge.Builder(n1, n2).buildEdge
		expected = graph.nodes(n1, n2).edges(e).build
		
		DotTestGraphs.NODE_LABEL_OVERRIDE2.assertImportedTo(expected)
		
		// test override attribute3
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		var nodes = createNodes
		nodes.get(1).label = "Node1"
		nodes.get(2).label = "Node2"
		nodes.get(3).label = "Node3"
		var e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).buildEdge
		var e2 = new Edge.Builder(nodes.get(2), nodes.get(3)).buildEdge
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.NODE_LABEL_OVERRIDE3.assertImportedTo(expected)
		
		// test override attribute4
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.DIGRAPH)
		nodes = createNodes
		nodes.get(0).label = "Node"
		nodes.get(1).label = "Node"
		nodes.get(2).labelRaw = ID.fromValue("Leaf", Type.QUOTED_STRING)
		nodes.get(3).label = "Node"
		e1 = new Edge.Builder(nodes.get(0), nodes.get(1)).buildEdge
		e2 = new Edge.Builder(nodes.get(1), nodes.get(2)).buildEdge
		expected = graph.nodes(nodes).edges(e1, e2).build
		
		DotTestGraphs.NODE_LABEL_OVERRIDE4.assertImportedTo(expected)
	}

	@Test def node_penwidth() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.penwidth=p2], "1.5").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.penwidth=p2], "1.5").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_PENWIDTH_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.penwidth = "2.5"
		n2.penwidth = "3.0"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_PENWIDTH_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.penwidth = "3.5"
		n2.penwidth = "4.0"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_PENWIDTH_OVERRIDE.assertImportedTo(expected)
	}

	@Test def node_pos() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		val graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.pos=p2], ".1,.2!").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.pos=p2], "-0.1,-2.3!").buildNode
		val expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_POS_LOCAL.assertImportedTo(expected)
	}

	@Test def node_shape() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.shape=p2], "box").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.shape=p2], "box").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_SHAPE_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.shape = "oval"
		n2.shape = "house"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_SHAPE_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.shape = "circle"
		n2.shape = "pentagon"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_SHAPE_OVERRIDE.assertImportedTo(expected)
	}

	@Test def node_sides() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.sides=p2], "3").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.sides=p2], "3").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_SIDES_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.sidesParsed = 4
		n2.sidesParsed = 5
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_SIDES_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.sidesParsed = 7
		n2.sidesParsed = 6
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_SIDES_OVERRIDE.assertImportedTo(expected)
	}

	@Test def node_skew() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.skew=p2], "1.2").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.skew=p2], "1.2").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_SKEW_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.skewParsed = 3.4
		n2.skewParsed = 5.6
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_SKEW_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.skewParsed = -7.8
		n2.skewParsed = 7.8
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_SKEW_OVERRIDE.assertImportedTo(expected)
	}

	@Test def node_style() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.style=p2], "solid, dashed").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.style=p2], "solid, dashed").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_STYLE_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.style = "bold"
		n2.style = "dotted"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_STYLE_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.style = "rounded"
		n2.style = "bold, filled"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_STYLE_OVERRIDE.assertImportedTo(expected)
	}

	@Test def node_tooltip() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.tooltip=p2], "a").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.tooltip=p2], "a").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_TOOLTIP_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.tooltip = "b"
		n2.tooltip = "c"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_TOOLTIP_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.tooltip = "e"
		n2.tooltip = "d"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_TOOLTIP_OVERRIDE.assertImportedTo(expected)
	}

	@Test def node_width() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.width=p2], "1.2").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.width=p2], "1.2").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_WIDTH_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.widthParsed = 3.4
		n2.widthParsed = 5.6
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_WIDTH_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.widthParsed = 9.11
		n2.widthParsed = 7.8
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_WIDTH_OVERRIDE.assertImportedTo(expected)
	}

	@Test def node_xlabel() {
		// test global attribute
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.xlabel=p2], "NodeExternalLabel1").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.xlabel=p2], "NodeExternalLabel1").buildNode
		var expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_XLABEL_GLOBAL.assertImportedTo(expected)
		
		// test local attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.xlabel = "NodeExternalLabel2"
		n2.xlabel = "NodeExternalLabel3"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_XLABEL_LOCAL.assertImportedTo(expected)
		
		// test override attribute
		graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		n1.xlabel = "NodeExternalLabel5"
		n2.xlabel = "NodeExternalLabel4"
		expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_XLABEL_OVERRIDE.assertImportedTo(expected)
	}

	@Test def node_xlp() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		val graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		val n1 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").attr([p1,p2|p1.xlp=p2], "-0.3,-0.4").buildNode
		val n2 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").attr([p1,p2|p1.xlp=p2], "-1.5,-1.6").buildNode
		val expected = graph.nodes(n1, n2).build
		
		DotTestGraphs.NODE_XLP_LOCAL.assertImportedTo(expected)
	}

	@Test def cluster_bgcolor() {
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		var nestedGraph = new Graph.Builder().attr([p1,p2|p1._setName(p2)], "clusterName").attr([p1,p2|p1.bgcolor=p2], "red")
		
		val n1 = new Node.Builder().buildNode
		val n11 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		
		n1.nestedGraph = nestedGraph.nodes(n11).build
		var expected = graph.nodes(n1).build
		
		DotTestGraphs.CLUSTER_BGCOLOR.assertImportedTo(expected)
	}

	@Test def cluster_color() {
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		var nestedGraph = new Graph.Builder().attr([p1,p2|p1._setName(p2)], "clusterName").attr([p1,p2|p1.color=p2], "red")
		
		val n1 = new Node.Builder().buildNode
		val n11 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		
		n1.nestedGraph = nestedGraph.nodes(n11).build
		var expected = graph.nodes(n1).build
		
		DotTestGraphs.CLUSTER_COLOR.assertImportedTo(expected)
	}

	@Test def cluster_colorscheme() {
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		var nestedGraph = new Graph.Builder().attr([p1,p2|p1._setName(p2)], "clusterName").attr([p1,p2|p1.colorscheme=p2], "svg")
		
		val n1 = new Node.Builder().buildNode
		val n11 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		
		n1.nestedGraph = nestedGraph.nodes(n11).build
		var expected = graph.nodes(n1).build
		
		DotTestGraphs.CLUSTER_COLORSCHEME.assertImportedTo(expected)
	}

	@Test def cluster_fillcolor() {
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		var nestedGraph = new Graph.Builder().attr([p1,p2|p1._setName(p2)], "clusterName").attr([p1,p2|p1.fillcolor=p2], "red")
		
		val n1 = new Node.Builder().buildNode
		val n11 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		
		n1.nestedGraph = nestedGraph.nodes(n11).build
		var expected = graph.nodes(n1).build
		
		DotTestGraphs.CLUSTER_FILLCOLOR.assertImportedTo(expected)
	}

	@Test def cluster_fontcolor() {
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		var nestedGraph = new Graph.Builder().attr([p1,p2|p1._setName(p2)], "clusterName").attr([p1,p2|p1.fontcolor=p2], "red")
		
		val n1 = new Node.Builder().buildNode
		val n11 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		
		n1.nestedGraph = nestedGraph.nodes(n11).build
		var expected = graph.nodes(n1).build
		
		DotTestGraphs.CLUSTER_FONTCOLOR.assertImportedTo(expected)
	}

	@Test def cluster_fontname() {
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		var nestedGraph = new Graph.Builder().attr([p1,p2|p1._setName(p2)], "clusterName").attr([p1,p2|p1.fontname=p2], "Helvetica")
		
		val n1 = new Node.Builder().buildNode
		val n11 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		
		n1.nestedGraph = nestedGraph.nodes(n11).build
		var expected = graph.nodes(n1).build
		
		DotTestGraphs.CLUSTER_FONTNAME.assertImportedTo(expected)
	}

	@Test def cluster_fontsize() {
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		var nestedGraph = new Graph.Builder().attr([p1,p2|p1._setName(p2)], "clusterName").attr([p1,p2|p1.fontsize=p2], "2")
		
		val n1 = new Node.Builder().buildNode
		val n11 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		
		n1.nestedGraph = nestedGraph.nodes(n11).build
		var expected = graph.nodes(n1).build
		
		DotTestGraphs.CLUSTER_FONTSIZE.assertImportedTo(expected)
	}

	@Test def cluster_id() {
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		var nestedGraph = new Graph.Builder().attr([p1,p2|p1._setName(p2)], "clusterName").attr([p1,p2|p1.id=p2], "FOO")
		
		val n1 = new Node.Builder().buildNode
		val n11 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		
		n1.nestedGraph = nestedGraph.nodes(n11).build
		var expected = graph.nodes(n1).build
		
		DotTestGraphs.CLUSTER_ID.assertImportedTo(expected)
	}

	@Test def cluster_label() {
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		var nestedGraph = new Graph.Builder().attr([p1,p2|p1._setName(p2)], "clusterName").attr([p1,p2|p1.label=p2], "foo")
		
		val n1 = new Node.Builder().buildNode
		val n11 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		
		n1.nestedGraph = nestedGraph.nodes(n11).build
		var expected = graph.nodes(n1).build
		
		DotTestGraphs.CLUSTER_LABEL.assertImportedTo(expected)
	}

	@Test def cluster_lp() {
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		var nestedGraph = new Graph.Builder().attr([p1,p2|p1._setName(p2)], "clusterName").attr([p1,p2|p1.lp=p2], "-4.5,-6.7")
		
		val n1 = new Node.Builder().buildNode
		val n11 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		
		n1.nestedGraph = nestedGraph.nodes(n11).build
		var expected = graph.nodes(n1).build
		
		DotTestGraphs.CLUSTER_LP.assertImportedTo(expected)
	}

	@Test def cluster_penwidth() {
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		var nestedGraph = new Graph.Builder().attr([p1,p2|p1._setName(p2)], "clusterName").attr([p1,p2|p1.penwidth=p2], "2")
		
		val n1 = new Node.Builder().buildNode
		val n11 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		
		n1.nestedGraph = nestedGraph.nodes(n11).build
		var expected = graph.nodes(n1).build
		
		DotTestGraphs.CLUSTER_PENWIDTH.assertImportedTo(expected)
	}

	@Test def cluster_style() {
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		var nestedGraph = new Graph.Builder().attr([p1,p2|p1._setName(p2)], "clusterName").attr([p1,p2|p1.style=p2], "dashed")
		
		val n1 = new Node.Builder().buildNode
		val n11 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		
		n1.nestedGraph = nestedGraph.nodes(n11).build
		var expected = graph.nodes(n1).build
		
		DotTestGraphs.CLUSTER_STYLE.assertImportedTo(expected)
	}

	@Test def cluster_tooltip() {
		var graph = new Graph.Builder().attr([p1,p2|p1._setType(p2)], GraphType.GRAPH)
		var nestedGraph = new Graph.Builder().attr([p1,p2|p1._setName(p2)], "clusterName").attr([p1,p2|p1.tooltip=p2], "foo")
		
		val n1 = new Node.Builder().buildNode
		val n11 = new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode
		
		n1.nestedGraph = nestedGraph.nodes(n11).build
		var expected = graph.nodes(n1).build
		
		DotTestGraphs.CLUSTER_TOOLTIP.assertImportedTo(expected)
	}

	@Test def clusters() {
		// test cluster subgraph
		var graph = DotTestGraphs.CLUSTERS.importString
		GraphType.DIGRAPH.assertEquals(graph._getType)
		
		// two clusters
		2.assertEquals(graph.nodes.size)
		val cluster1 = graph.nodes.get(0)
		cluster1.getNestedGraph.assertNotNull
		"cluster1".assertEquals(cluster1.nestedGraph._getName)
		cluster1.assertEquals(cluster1.nestedGraph.nestingNode)
		
		// two nested nodes and one nested edge (between these nodes) in small cluster
		2.assertEquals(cluster1.nestedGraph.nodes.size)
		1.assertEquals(cluster1.nestedGraph.edges.size)
		
		val cluster2 = graph.nodes.get(1)
		cluster2.getNestedGraph.assertNotNull
		"cluster2".assertEquals(cluster2.nestedGraph._getName)
		
		// five nested nodes and five nested edges (between these nodes) in big cluster
		5.assertEquals(cluster2.nestedGraph.nodes.size)
		5.assertEquals(cluster2.nestedGraph.edges.size)
		2.assertEquals(graph.getEdges.size)
		
		val e1 = graph.edges.get(0)
		"b".assertEquals(e1.source._getName)
		"q".assertEquals(e1.target._getName)
		
		val e2 = graph.getEdges.get(1)
		"t".assertEquals(e2.source._getName)
		"a".assertEquals(e2.target._getName)
		
		// ensure DotImport can be used multiple times in succession
		graph = DotTestGraphs.CLUSTERS.importString
		2.assertEquals(graph.nodes.size)
	}

	@Test def cluster_merge() {
		val graph = DotTestGraphs.CLUSTER_MERGE.importString
		graph.assertNotNull
		GraphType.DIGRAPH.assertEquals(graph._getType)
		// one (merged) cluster
		1.assertEquals(graph.nodes.size)
		val cluster1 = graph.nodes.head
		cluster1.nestedGraph.assertNotNull
		4.assertEquals(cluster1.nestedGraph.nodes.size)
		2.assertEquals(cluster1.nestedGraph.edges.size)
		2.assertEquals(graph.edges.size)
	}

	@Test def subgraph_scoping() {
		/*
		 * Input:
		 * node [shape="hexagon", style="filled", fillcolor="blue"]
		 * { node [shape="box"] a b }
		 * { node [fillcolor="red"] b c }
		 */
		val graph = DotTestGraphs.CLUSTER_SCOPE.importString
		/*
		 * Expected result:
		 * a [shape="box", style="filled", fillcolor="blue"]
		 * b [shape="box", style="filled", fillcolor="blue"]
		 * c [shape="hexagon", style="filled", fillcolor="red"]
		 */
		2.assertEquals(graph.nodes.size)
		val subgraph1 = graph.nodes.get(0)
		val subgraph2 = graph.nodes.get(1)
		2.assertEquals(subgraph1.nestedGraph.nodes.size)
		1.assertEquals(subgraph2.nestedGraph.nodes.size)
		
		val a = subgraph1.nestedGraph.nodes.get(0)
		"a".assertEquals(a._getName)
		"box".assertEquals(a.shape)
		"filled".assertEquals(a.style)
		"blue".assertEquals(a.fillcolor)

		// b is defined in the first subgraph, so it should be contained there
		val b = subgraph1.nestedGraph.nodes.get(1)
		"b".assertEquals(b._getName)
		"box".assertEquals(b.shape)
		"filled".assertEquals(b.style)
		"blue".assertEquals(b.fillcolor)
		
		val c = subgraph2.nestedGraph.nodes.get(0)
		"c".assertEquals(c._getName)
		"hexagon".assertEquals(c.shape)
		"filled".assertEquals(c.style)
		"red".assertEquals(c.fillcolor)
	}

	private def Node[] createNodes() {
		#[
			new Node.Builder().attr([p1,p2|p1._setName(p2)], "1").buildNode,
			new Node.Builder().attr([p1,p2|p1._setName(p2)], "2").buildNode,
			new Node.Builder().attr([p1,p2|p1._setName(p2)], "3").buildNode,
			new Node.Builder().attr([p1,p2|p1._setName(p2)], "4").buildNode
		]
	}

	private def assertImportedTo(CharSequence dot, Graph expected) {
		val graphs = dotImport.importDot(dot.toString)
		1.assertEquals(graphs.size)
		graphs.head.assertGraph(expected)
	}

	private def assertFileImportedTo(String fileName, Graph expected) {
		val actual = fileName.file.importFile
		actual.assertGraph(expected)
	}

	private def assertGraph(Graph actual, Graph expected) {
		val actualFormattedText = prettyPrinter.prettyPrint(actual)
		val expectedFormattedText = prettyPrinter.prettyPrint(expected)
		expectedFormattedText.assertEquals(actualFormattedText)
	}

	// TODO: Generalize to multiple graphs
	private def importFile(File dotFile) {
		Assert.assertTrue("DOT input file" + dotFile + " does not exist:", dotFile.exists)
		val graphs = dotImport.importDot(dotFile)
		Assert.assertFalse("Created graph does not exist!", graphs.isEmpty)
		graphs.head
	}

	// TODO: Generalize to multiple graphs
	private def importString(CharSequence dotString) {
		val graphs = dotImport.importDot(dotString.toString)
		Assert.assertFalse("Created graph does not exist!", graphs.isEmpty)
		graphs.head
	}
}
