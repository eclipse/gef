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

import static org.eclipse.gef4.dot.tests.DotTestUtils.RESOURCES_TESTS;

import java.io.File;

import org.eclipse.gef4.dot.internal.DotAttributes;
import org.eclipse.gef4.dot.internal.DotImport;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link DotImport} class.
 * 
 * @author Fabian Steeg (fsteeg)
 */
// TODO: this could be combined with the DotInterpreterTests, similar
// as DotExportTests and DotTemplateTests
public final class DotImportTests {

	private Graph testFileImport(final File dotFile) {
		Assert.assertTrue("DOT input file must exist: " + dotFile, //$NON-NLS-1$
				dotFile.exists());
		Graph graph = new DotImport().importDot(dotFile);
		Assert.assertNotNull("Resulting graph must not be null", graph); //$NON-NLS-1$
		return graph;
	}

	/**
	 * Test valid graphs can be imported without exceptions.
	 */
	@Test
	public void sampleGraphsFileImport() {
		// simple graphs
		Graph graph = testFileImport(
				new File(RESOURCES_TESTS + "simple_graph.dot")); //$NON-NLS-1$
		Assert.assertEquals(DotTestUtils.getSimpleGraph().toString(),
				graph.toString());

		graph = testFileImport(
				new File(RESOURCES_TESTS + "simple_digraph.dot")); //$NON-NLS-1$
		Assert.assertEquals(DotTestUtils.getSimpleDiGraph().toString(),
				graph.toString());

		graph = testFileImport(new File(RESOURCES_TESTS + "labeled_graph.dot")); //$NON-NLS-1$
		Assert.assertEquals(DotTestUtils.getLabeledGraph().toString(),
				graph.toString());

		graph = testFileImport(new File(RESOURCES_TESTS + "styled_graph.dot")); //$NON-NLS-1$
		Assert.assertEquals(DotTestUtils.getStyledGraph().toString(),
				graph.toString());

		// test import succeeds without exceptions
		testFileImport(new File(RESOURCES_TESTS + "sample_input.dot")); //$NON-NLS-1$
		testFileImport(new File(RESOURCES_TESTS + "basic_directed_graph.dot")); //$NON-NLS-1$
		testFileImport(new File(RESOURCES_TESTS + "global_node_graph.dot")); //$NON-NLS-1$
		testFileImport(new File(RESOURCES_TESTS + "global_edge_graph.dot")); //$NON-NLS-1$
		testFileImport(new File(RESOURCES_TESTS + "attributes_graph.dot")); //$NON-NLS-1$
		testFileImport(new File(RESOURCES_TESTS + "node_groups.dot")); //$NON-NLS-1$
		testFileImport(new File(RESOURCES_TESTS + "id_matches_keyword.dot")); //$NON-NLS-1$
		testFileImport(new File(RESOURCES_TESTS + "layout_tree_graph.dot")); //$NON-NLS-1$
		testFileImport(new File(RESOURCES_TESTS + "layout_spring_graph.dot")); //$NON-NLS-1$
		testFileImport(new File(RESOURCES_TESTS + "layout_radial_graph.dot")); //$NON-NLS-1$
		testFileImport(new File(RESOURCES_TESTS + "layout_grid_graph.dot")); //$NON-NLS-1$
	}

	/**
	 * Test error handling for invalid graph.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void invalidGraphFileImport() {
		new DotImport().importDot("graph Sample{");
	}

	@Test
	public void edge_arrowhead() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__DIGRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes._NAME__GNE, "1->2") //$NON-NLS-1$
				.attr(DotAttributes.ARROWHEAD__E, "crow") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes._NAME__GNE, "3->4") //$NON-NLS-1$
				.attr(DotAttributes.ARROWHEAD__E, "crow") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWHEAD_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__DIGRAPH);
		DotAttributes.setArrowHead(e1, "diamond");
		DotAttributes.setArrowHead(e2, "dot");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWHEAD_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__DIGRAPH);
		DotAttributes.setArrowHead(e1, "vee");
		DotAttributes.setArrowHead(e2, "tee");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWHEAD_OVERRIDE);
	}

	@Test
	public void edge_arrowsize() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__DIGRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes._NAME__GNE, "1->2") //$NON-NLS-1$
				.attr(DotAttributes.ARROWSIZE__E, "1.5") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes._NAME__GNE, "3->4") //$NON-NLS-1$
				.attr(DotAttributes.ARROWSIZE__E, "1.5") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWSIZE_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__DIGRAPH);
		DotAttributes.setArrowSize(e1, "2.0");
		DotAttributes.setArrowSize(e2, "2.1");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWSIZE_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__DIGRAPH);
		DotAttributes.setArrowSize(e1, "2.3");
		DotAttributes.setArrowSize(e2, "2.2");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWSIZE_OVERRIDE);
	}

	@Test
	public void edge_arrowtail() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__DIGRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes._NAME__GNE, "1->2") //$NON-NLS-1$
				.attr(DotAttributes.ARROWTAIL__E, "box") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes._NAME__GNE, "3->4") //$NON-NLS-1$
				.attr(DotAttributes.ARROWTAIL__E, "box") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWTAIL_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__DIGRAPH);
		DotAttributes.setArrowTail(e1, "lbox");
		DotAttributes.setArrowTail(e2, "rbox");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWTAIL_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__DIGRAPH);
		DotAttributes.setArrowTail(e1, "olbox");
		DotAttributes.setArrowTail(e2, "obox");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ARROWTAIL_OVERRIDE);
	}

	@Test
	public void edge_dir() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__DIGRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes._NAME__GNE, "1->2") //$NON-NLS-1$
				.attr(DotAttributes.DIR__E, "forward") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes._NAME__GNE, "3->4") //$NON-NLS-1$
				.attr(DotAttributes.DIR__E, "forward") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_DIR_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__DIGRAPH);
		DotAttributes.setDir(e1, "forward");
		DotAttributes.setDir(e2, "back");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_DIR_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__DIGRAPH);
		DotAttributes.setDir(e1, "both");
		DotAttributes.setDir(e2, "back");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_DIR_OVERRIDE);
	}

	@Test
	public void edge_headlabel() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes._NAME__GNE, "1--2") //$NON-NLS-1$
				.attr(DotAttributes.HEADLABEL__E, "EdgeHeadLabel1") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes._NAME__GNE, "3--4") //$NON-NLS-1$
				.attr(DotAttributes.HEADLABEL__E, "EdgeHeadLabel1") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_HEADLABEL_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setHeadLabel(e1, "EdgeHeadLabel2");
		DotAttributes.setHeadLabel(e2, "EdgeHeadLabel3");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_HEADLABEL_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setHeadLabel(e1, "EdgeHeadLabel5");
		DotAttributes.setHeadLabel(e2, "EdgeHeadLabel4");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_HEADLABEL_OVERRIDE);
	}

	@Test
	public void edge_headlp() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes._NAME__GNE, "1--2") //$NON-NLS-1$
				.attr(DotAttributes.HEAD_LP__E, "2.2,3.3") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes._NAME__GNE, "3--4") //$NON-NLS-1$
				.attr(DotAttributes.HEAD_LP__E, "-2.2,-3.3") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_HEAD_LP_LOCAL);
	}

	@Test
	public void edge_id() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes._NAME__GNE, "1--2") //$NON-NLS-1$
				.attr(DotAttributes.ID__GNE, "edgeID2") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes._NAME__GNE, "3--4") //$NON-NLS-1$
				.attr(DotAttributes.ID__GNE, "edgeID3") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_ID_LOCAL);
	}

	@Test
	public void edge_label() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes._NAME__GNE, "1--2") //$NON-NLS-1$
				.attr(DotAttributes.LABEL__GNE, "Edge1") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes._NAME__GNE, "3--4") //$NON-NLS-1$
				.attr(DotAttributes.LABEL__GNE, "Edge1") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_LABEL_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setLabel(e1, "Edge1");
		DotAttributes.setLabel(e2, "Edge2");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_LABEL_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setLabel(e1, "Edge4");
		DotAttributes.setLabel(e2, "Edge3");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_LABEL_OVERRIDE);
	}

	@Test
	public void edge_lp() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes._NAME__GNE, "1--2") //$NON-NLS-1$
				.attr(DotAttributes.LP__GE, "0.3,0.4") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes._NAME__GNE, "3--4") //$NON-NLS-1$
				.attr(DotAttributes.LP__GE, "0.5,0.6") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_LP_LOCAL);
	}

	@Test
	public void edge_pos() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes._NAME__GNE, "1--2") //$NON-NLS-1$
				.attr(DotAttributes.POS__NE, "0.0,0.0 1.0,1.0 2.0,2.0 3.0,3.0") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes._NAME__GNE, "3--4") //$NON-NLS-1$
				.attr(DotAttributes.POS__NE, "4.0,4.0 5.0,5.0 6.0,6.0 7.0,7.0") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_POS_LOCAL);
	}

	@Test
	public void edge_style() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes._NAME__GNE, "1--2") //$NON-NLS-1$
				.attr(DotAttributes.STYLE__E, "dashed") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes._NAME__GNE, "3--4") //$NON-NLS-1$
				.attr(DotAttributes.STYLE__E, "dashed") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_STYLE_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setStyle(e1, "dashed");
		DotAttributes.setStyle(e2, "dotted");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_STYLE_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setStyle(e1, "bold, dotted");
		DotAttributes.setStyle(e2, "bold");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_STYLE_OVERRIDE);
	}

	@Test
	public void edge_taillabel() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes._NAME__GNE, "1--2") //$NON-NLS-1$
				.attr(DotAttributes.TAILLABEL__E, "EdgeTailLabel1") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes._NAME__GNE, "3--4") //$NON-NLS-1$
				.attr(DotAttributes.TAILLABEL__E, "EdgeTailLabel1") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_TAILLABEL_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setTailLabel(e1, "EdgeTailLabel2");
		DotAttributes.setTailLabel(e2, "EdgeTailLabel3");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_TAILLABEL_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setTailLabel(e1, "EdgeTailLabel5");
		DotAttributes.setTailLabel(e2, "EdgeTailLabel4");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_TAILLABEL_OVERRIDE);
	}

	@Test
	public void edge_taillp() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes._NAME__GNE, "1--2") //$NON-NLS-1$
				.attr(DotAttributes.TAIL_LP__E, "-4.5,-6.7") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes._NAME__GNE, "3--4") //$NON-NLS-1$
				.attr(DotAttributes.TAIL_LP__E, "-8.9,-10.11") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_TAIL_LP_LOCAL);
	}

	@Test
	public void edge_xlabel() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes._NAME__GNE, "1--2") //$NON-NLS-1$
				.attr(DotAttributes.XLABEL__NE, "EdgeExternalLabel1") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes._NAME__GNE, "3--4") //$NON-NLS-1$
				.attr(DotAttributes.XLABEL__NE, "EdgeExternalLabel1") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_XLABEL_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setXLabel(e1, "EdgeExternalLabel2");
		DotAttributes.setXLabel(e2, "EdgeExternalLabel3");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_XLABEL_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setXLabel(e1, "EdgeExternalLabel5");
		DotAttributes.setXLabel(e2, "EdgeExternalLabel4");
		expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_XLABEL_OVERRIDE);
	}

	@Test
	public void edge_xlp() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node[] nodes = createNodes();
		Edge e1 = new Edge.Builder(nodes[0], nodes[1])
				.attr(DotAttributes._NAME__GNE, "1--2") //$NON-NLS-1$
				.attr(DotAttributes.XLP__NE, ".3,.4") //$NON-NLS-1$
				.buildEdge();
		Edge e2 = new Edge.Builder(nodes[2], nodes[3])
				.attr(DotAttributes._NAME__GNE, "3--4") //$NON-NLS-1$
				.attr(DotAttributes.XLP__NE, ".5,.6") //$NON-NLS-1$
				.buildEdge();
		Graph expected = graph.nodes(nodes).edges(e1, e2).build();
		testStringImport(expected, DotTestGraphs.EDGE_XLP_LOCAL);
	}

	@Test
	public void node_distortion() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.attr(DotAttributes.DISTORTION__N, "1.1").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.attr(DotAttributes.DISTORTION__N, "1.1").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_DISTORTION_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setDistortion(n1, "1.2");
		DotAttributes.setDistortion(n2, "1.3");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_DISTORTION_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setDistortion(n1, "1.5");
		DotAttributes.setDistortion(n2, "1.4");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_DISTORTION_OVERRIDE);
	}

	@Test
	public void node_fixedsize() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.attr(DotAttributes.FIXEDSIZE__N, "true").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.attr(DotAttributes.FIXEDSIZE__N, "true").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_FIXEDSIZE_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setFixedSizeParsed(n1, true);
		DotAttributes.setFixedSizeParsed(n2, false);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_FIXEDSIZE_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setFixedSizeParsed(n1, false);
		DotAttributes.setFixedSizeParsed(n2, true);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_FIXEDSIZE_OVERRIDE);
	}

	@Test
	public void node_height() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.attr(DotAttributes.HEIGHT__N, "1.2").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.attr(DotAttributes.HEIGHT__N, "1.2").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_HEIGHT_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setHeightParsed(n1, 3.4);
		DotAttributes.setHeightParsed(n2, 5.6);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_HEIGHT_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setHeightParsed(n1, 9.11);
		DotAttributes.setHeightParsed(n2, 7.8);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_HEIGHT_OVERRIDE);
	}

	@Test
	public void node_id() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.attr(DotAttributes.ID__GNE, "NodeID1").buildNode(); //$NON-NLS-1$ .buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.attr(DotAttributes.ID__GNE, "NodeID2").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_ID_LOCAL);
	}

	@Test
	public void node_label() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.attr(DotAttributes.LABEL__GNE, "Node1").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.attr(DotAttributes.LABEL__GNE, "Node1").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_LABEL_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setLabel(n1, "Node1");
		DotAttributes.setLabel(n2, "Node2");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_LABEL_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setLabel(n1, "Node4");
		DotAttributes.setLabel(n2, "Node3");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_LABEL_OVERRIDE);
	}

	@Test
	public void node_pos() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.attr(DotAttributes.POS__NE, ".1,.2!").buildNode(); //$NON-NLS-1$ .buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.attr(DotAttributes.POS__NE, "-0.1,-2.3!").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_POS_LOCAL);
	}

	@Test
	public void node_shape() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.attr(DotAttributes.SHAPE__N, "box").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.attr(DotAttributes.SHAPE__N, "box").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SHAPE_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setShape(n1, "oval");
		DotAttributes.setShape(n2, "house");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SHAPE_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setShape(n1, "circle");
		DotAttributes.setShape(n2, "pentagon");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SHAPE_OVERRIDE);
	}

	@Test
	public void node_sides() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.attr(DotAttributes.SIDES__N, "3").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.attr(DotAttributes.SIDES__N, "3").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SIDES_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setSidesParsed(n1, 4);
		DotAttributes.setSidesParsed(n2, 5);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SIDES_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setSidesParsed(n1, 7);
		DotAttributes.setSidesParsed(n2, 6);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SIDES_OVERRIDE);
	}

	@Test
	public void node_skew() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.attr(DotAttributes.SKEW__N, "1.2").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.attr(DotAttributes.SKEW__N, "1.2").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SKEW_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setSkewParsed(n1, 3.4);
		DotAttributes.setSkewParsed(n2, 5.6);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SKEW_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setSkewParsed(n1, -7.8);
		DotAttributes.setSkewParsed(n2, 7.8);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_SKEW_OVERRIDE);
	}

	@Test
	public void node_style() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.attr(DotAttributes.STYLE__E, "solid, dashed").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.attr(DotAttributes.STYLE__E, "solid, dashed").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_STYLE_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setStyle(n1, "bold");
		DotAttributes.setStyle(n2, "dotted");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_STYLE_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setStyle(n1, "rounded");
		DotAttributes.setStyle(n2, "bold, filled");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_STYLE_OVERRIDE);
	}

	@Test
	public void node_width() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.attr(DotAttributes.WIDTH__N, "1.2").buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.attr(DotAttributes.WIDTH__N, "1.2").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_WIDTH_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setWidthParsed(n1, 3.4);
		DotAttributes.setWidthParsed(n2, 5.6);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_WIDTH_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setWidthParsed(n1, 9.11);
		DotAttributes.setWidthParsed(n2, 7.8);
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_WIDTH_OVERRIDE);
	}

	@Test
	public void node_xlabel() {
		// test global attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.attr(DotAttributes.XLABEL__NE, "NodeExternalLabel1")
				.buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.attr(DotAttributes.XLABEL__NE, "NodeExternalLabel1")
				.buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_XLABEL_GLOBAL);

		// test local attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setXLabel(n1, "NodeExternalLabel2");
		DotAttributes.setXLabel(n2, "NodeExternalLabel3");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_XLABEL_LOCAL);

		// test override attribute
		graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		DotAttributes.setXLabel(n1, "NodeExternalLabel5");
		DotAttributes.setXLabel(n2, "NodeExternalLabel4");
		expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_XLABEL_OVERRIDE);
	}

	@Test
	public void node_xlp() {
		// no global/override attribute tests, since they do not make sense
		// test local attribute
		Graph.Builder graph = new Graph.Builder().attr(DotAttributes._TYPE__G,
				DotAttributes._TYPE__G__GRAPH);
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.attr(DotAttributes.XLP__NE, "-0.3,-0.4").buildNode(); //$NON-NLS-1$ .buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.attr(DotAttributes.XLP__NE, "-1.5,-1.6").buildNode();
		Graph expected = graph.nodes(n1, n2).build();
		testStringImport(expected, DotTestGraphs.NODE_XLP_LOCAL);
	}

	private Node[] createNodes() {
		Node n1 = new Node.Builder().attr(DotAttributes._NAME__GNE, "1") //$NON-NLS-1$
				.buildNode();
		Node n2 = new Node.Builder().attr(DotAttributes._NAME__GNE, "2") //$NON-NLS-1$
				.buildNode();
		Node n3 = new Node.Builder().attr(DotAttributes._NAME__GNE, "3") //$NON-NLS-1$
				.buildNode();
		Node n4 = new Node.Builder().attr(DotAttributes._NAME__GNE, "4") //$NON-NLS-1$
				.buildNode();
		return new Node[] { n1, n2, n3, n4 };
	}

	private void testStringImport(Graph expected, String dot) {
		Graph graph = new DotImport().importDot(dot);
		Assert.assertNotNull("Resulting graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(expected.toString(), graph.toString());
	}
}
