/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.dot.tests;

import static org.eclipse.gef4.dot.tests.DotTestUtils.RESOURCES_TESTS;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef4.dot.DotImport;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link DotImport} class.
 * 
 * @author Fabian Steeg (fsteeg)
 */
// TODO: this could be combined with the GraphCreatorInterpreterTests, similar
// as DotExportTests and DotTemplateTests
public final class DotImportTests {

	static Graph importFrom(final File dotFile) {
		Assert.assertTrue("DOT input file must exist: " + dotFile, //$NON-NLS-1$
				dotFile.exists());
		Graph graph = new DotImport(dotFile).newGraphInstance();
		Assert.assertNotNull("Resulting graph must not be null", graph); //$NON-NLS-1$
		return graph;
	}

	/**
	 * Test valid graphs can be imported without exceptions.
	 */
	@Test
	public void testBasicFileImport() {
		// simple graphs
		Graph graph = importFrom(new File(RESOURCES_TESTS + "simple_graph.dot")); //$NON-NLS-1$
		Assert.assertEquals(DotTestUtils.getSimpleGraph().toString(),
				graph.toString());

		graph = importFrom(new File(RESOURCES_TESTS + "simple_digraph.dot")); //$NON-NLS-1$		
		Assert.assertEquals(DotTestUtils.getSimpleDiGraph().toString(),
				graph.toString());

		graph = importFrom(new File(RESOURCES_TESTS + "labeled_graph.dot")); //$NON-NLS-1$
		Assert.assertEquals(DotTestUtils.getLabeledGraph().toString(),
				graph.toString());

		graph = importFrom(new File(RESOURCES_TESTS + "styled_graph.dot")); //$NON-NLS-1$
		Assert.assertEquals(DotTestUtils.getStyledGraph().toString(),
				graph.toString());

		graph = importFrom(new File(RESOURCES_TESTS + "sample_input.dot")); //$NON-NLS-1$
		// TODO: Assert.assertEquals(DotTestUtils.getSampleGraph().toString(),
		// graph.toString());

		importFrom(new File(RESOURCES_TESTS + "basic_directed_graph.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "global_node_graph.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "global_edge_graph.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "attributes_graph.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "node_groups.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "id_matches_keyword.dot")); //$NON-NLS-1$

		// graph having layout attribute
		importFrom(new File(RESOURCES_TESTS + "layout_tree_graph.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "layout_spring_graph.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "layout_radial_graph.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "layout_grid_graph.dot")); //$NON-NLS-1$

		// graphs with animations
		importFrom(new File(RESOURCES_TESTS
				+ "experimental_animation_simple.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS
				+ "experimental_animation_bintree.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS
				+ "experimental_animation_layout.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "experimental_animation_full.dot")); //$NON-NLS-1$
	}

	/**
	 * Test error handling for invalid graph.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFaultyGraphBasicImport() {
		new DotImport("graph Sample{").newGraphInstance();
	}

	@Test
	public void importNonExistingNodesIntoGraph() {
		// simple graph
		Graph.Builder graph = new Graph.Builder();
		graph.attr(Graph.Attr.Key.LAYOUT.toString(), new TreeLayoutAlgorithm());
		Assert.assertEquals(0, graph.build().getNodes().size());
		Assert.assertEquals(0, graph.build().getEdges().size());
		new DotImport("1->2").into(graph); //$NON-NLS-1$
		assertNodesEdgesCount(2, 1, graph);
		new DotImport("3;4").into(graph); //$NON-NLS-1$
		assertNodesEdgesCount(4, 1, graph);
		new DotImport("5->6").into(graph); //$NON-NLS-1$
		assertNodesEdgesCount(6, 2, graph);

		// sample usage
		graph = new Graph.Builder(); // or from DOT, see below
		new DotImport("node[label=zested]; 1->2; 1->3").into(graph); //$NON-NLS-1$
		assertNodesEdgesCount(3, 2, graph);
		new DotImport("edge[style=dashed]; 2->4; 3->5").into(graph); //$NON-NLS-1$
		assertNodesEdgesCount(5, 4, graph);
	}

	@Test
	public void importExistingNodesIntoGraph() {
		Graph.Builder graph = new Graph.Builder();
		new DotImport("digraph{1->2}").into(graph);
		assertNodesEdgesCount(2, 1, graph);
		new DotImport("1->3").into(graph); // should reuse node 1 from above
		assertNodesEdgesCount(3, 2, graph);
		new DotImport("3->4").into(graph); // should reuse node 3 from above
		assertNodesEdgesCount(4, 3, graph);

		// labeled
		graph = new Graph.Builder();
		new DotImport("digraph{1[label=one];2[label=two]}").into(graph);
		assertNodesEdgesCount(2, 0, graph);
		new DotImport("1->2").into(graph); // reuse nodes 1 and 2 from above
		assertNodesEdgesCount(2, 1, graph);
		new DotImport("1->3").into(graph); // reuse node 1 from above
		assertNodesEdgesCount(3, 2, graph);
	}

	@Test
	public void importUndirectedEdgeDotSyntaxIntoGraph() {
		Graph.Builder graph = new Graph.Builder();
		new DotImport("1--2").into(graph); // reuse nodes 1 and 2 from above
		assertNodesEdgesCount(2, 1, graph);
		new DotImport("1--3").into(graph); // reuse node 1 from above
		assertNodesEdgesCount(3, 2, graph);
	}

	@Test
	public void importLayoutAlgorithmIntoGraph() {
		Graph.Builder graph = new Graph.Builder();
		new DotImport("rankdir=LR").into(graph);
		Assert.assertEquals(TreeLayoutAlgorithm.class, graph.build().getAttrs()
				.get(Graph.Attr.Key.LAYOUT.toString()).getClass());
		Assert.assertEquals(TreeLayoutAlgorithm.LEFT_RIGHT,
				((TreeLayoutAlgorithm) (graph.build().getAttrs()
						.get(Graph.Attr.Key.LAYOUT.toString()))).getDirection());
		new DotImport("rankdir=TD").into(graph);
		Assert.assertEquals(TreeLayoutAlgorithm.class, graph.build().getAttrs()
				.get(Graph.Attr.Key.LAYOUT.toString()).getClass());
		Assert.assertEquals(TreeLayoutAlgorithm.TOP_DOWN,
				((TreeLayoutAlgorithm) (graph.build().getAttrs()
						.get(Graph.Attr.Key.LAYOUT.toString()))).getDirection());
	}

	@Test
	public void importStyledEdgeIntoGraph() {
		Graph.Builder graph = new Graph.Builder();
		Assert.assertNull(graph.build().getAttrs()
				.get(Graph.Attr.Key.EDGE_STYLE.toString()));
		assertNodesEdgesCount(0, 0, graph);
		new DotImport("1->2[style=dashed label=dashed]").into(graph);
		assertNodesEdgesCount(2, 1, graph);
		Iterator<Edge> iterator = graph.build().getEdges().iterator();
		Edge edge = iterator.next();
		Assert.assertEquals(Graph.Attr.Value.LINE_DASH,
				edge.getAttrs().get(Graph.Attr.Key.EDGE_STYLE.toString()));
		Assert.assertEquals("dashed",
				edge.getAttrs().get(Graph.Attr.Key.LABEL.toString()));
		new DotImport("2->3[style=dotted label=dotted]").into(graph);
		assertNodesEdgesCount(3, 2, graph);
		iterator = graph.build().getEdges().iterator();
		iterator.next();
		edge = iterator.next();
		Assert.assertEquals(Graph.Attr.Value.LINE_DOT,
				edge.getAttrs().get(Graph.Attr.Key.EDGE_STYLE.toString()));
		Assert.assertEquals("dotted",
				edge.getAttrs().get(Graph.Attr.Key.LABEL.toString()));
	}

	@Test
	public void importStyledNodeIntoGraph() {
		Graph.Builder graph = new Graph.Builder();
		Assert.assertNull(graph.build().getAttrs()
				.get(Graph.Attr.Key.EDGE_STYLE.toString()));
		assertNodesEdgesCount(0, 0, graph);
		new DotImport("1[label=one]").into(graph);
		assertNodesEdgesCount(1, 0, graph);
		List<Node> list = graph.build().getNodes();
		Assert.assertEquals("one",
				list.get(0).getAttrs().get(Graph.Attr.Key.LABEL.toString()));
		new DotImport("2[label=two]; 3[label=three]").into(graph);
		assertNodesEdgesCount(3, 0, graph);
		list = graph.build().getNodes();
		Assert.assertEquals("two",
				list.get(1).getAttrs().get(Graph.Attr.Key.LABEL.toString()));
		Assert.assertEquals("three",
				list.get(2).getAttrs().get(Graph.Attr.Key.LABEL.toString()));
	}

	private void assertNodesEdgesCount(int n, int e, Graph.Builder builder) {
		Graph graph = builder.build();
		Assert.assertEquals(n, graph.getNodes().size());
		Assert.assertEquals(e, graph.getEdges().size());
	}

	@Test
	public void importSimpleToNewGraph() {
		/* The DOT input, can be given as a String, File or IFile: */
		DotImport dotImport = new DotImport("digraph Simple { 1;2; 1->2 }"); //$NON-NLS-1$
		/* Create a Zest graph instance: */
		dotImport.newGraphInstance();

		DotImport importer = new DotImport("digraph Sample{1;2;1->2}"); //$NON-NLS-1$
		Graph graph = importer.newGraphInstance();
		Assert.assertNotNull("Created graph must not be null", graph); //$NON-NLS-1$
		Assert.assertEquals(Graph.Attr.Value.GRAPH_DIRECTED, graph.getAttrs()
				.get(Graph.Attr.Key.GRAPH_TYPE.toString()));
	}

	@Test
	public void importUndeclaredNodesToNewGraph() {
		Graph graph = new DotImport("digraph{1->2;1->3}").newGraphInstance();
		Assert.assertEquals(3, graph.getNodes().size());
		Assert.assertEquals(2, graph.getEdges().size());
	}

	@Test
	public void importSubgraphsToNewGraph() {
		DotImport dotImport = new DotImport(
				"digraph{subgraph {1->2}; subgraph {1->3}}");
		Graph graph = dotImport.newGraphInstance();
		assertEquals("Non-cluster subgraphs should be ignored in rendering", 3,
				graph.getNodes().size());
		assertEquals(2, graph.getEdges().size());
	}

}
