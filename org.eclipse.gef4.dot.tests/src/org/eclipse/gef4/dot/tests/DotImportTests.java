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
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef4.dot.internal.DotAttributes;
import org.eclipse.gef4.dot.internal.DotImport;
import org.eclipse.gef4.dot.internal.parser.rankdir.Rankdir;
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
// TODO: this could be combined with the DotInterpreterTests, similar
// as DotExportTests and DotTemplateTests
public final class DotImportTests {

	static Graph importFrom(final File dotFile) {
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
	public void testFileImport() {
		// simple graphs
		Graph graph = importFrom(
				new File(RESOURCES_TESTS + "simple_graph.dot")); //$NON-NLS-1$
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

		// test import succeeds without exceptions
		importFrom(new File(RESOURCES_TESTS + "sample_input.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "basic_directed_graph.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "global_node_graph.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "global_edge_graph.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "attributes_graph.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "node_groups.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "id_matches_keyword.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "layout_tree_graph.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "layout_spring_graph.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "layout_radial_graph.dot")); //$NON-NLS-1$
		importFrom(new File(RESOURCES_TESTS + "layout_grid_graph.dot")); //$NON-NLS-1$
	}

	/**
	 * Test error handling for invalid graph.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFaultyGraphFileImport() {
		new DotImport().importDot("graph Sample{");
	}

	@Test
	public void stringImport() {
		Graph graph = new DotImport()
				.importDot("digraph{subgraph {1->2}; subgraph {1->3}}");
		assertEquals("Non-cluster subgraphs should be ignored in rendering", 3,
				graph.getNodes().size());
		assertEquals(2, graph.getEdges().size());
	}

}
