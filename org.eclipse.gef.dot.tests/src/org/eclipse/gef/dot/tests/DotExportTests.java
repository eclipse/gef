/*******************************************************************************
 * Copyright (c) 2009, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg    - initial API and implementation (bug #277380)
 *     Tamas Miklossy  - usage of platform specific line separators (bug #490118)
 *                     - minor refactorings 
 *     
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import static org.eclipse.gef.dot.tests.DotTestUtils.RESOURCES_TESTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Scanner;

import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.DotExport;
import org.eclipse.gef.dot.internal.DotFileUtils;
import org.eclipse.gef.dot.internal.language.layout.Layout;
import org.eclipse.gef.graph.Graph;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for the {@link DotExport} class.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class DotExportTests {

	public static final File OUTPUT = new File("output"); //$NON-NLS-1$
	private final DotExport dotExport = new DotExport();

	@BeforeClass
	public static void wipe() {
		DotTestUtils.wipeOutput(DotExportTests.OUTPUT, ".dot"); //$NON-NLS-1$
		if (!DotExportTests.OUTPUT.exists()) {
			DotExportTests.OUTPUT.mkdirs();
		}
	}

	@Test
	public void simpleGraph() {
		testDotExport(DotTestUtils.getSimpleGraph(), "simple_graph.dot");
	}

	@Test
	public void directedGraph() {
		testDotExport(DotTestUtils.getSimpleDiGraph(), "simple_digraph.dot");
	}

	@Test
	public void labeledGraph() {
		testDotExport(DotTestUtils.getLabeledGraph(), "labeled_graph.dot");
	}

	@Test
	public void styledGraph() {
		testDotExport(DotTestUtils.getStyledGraph(), "styled_graph.dot");
	}

	/** Test setting layout algorithms. */
	@Test
	public void layoutToGraphvizLayoutMapping() {
		Graph.Builder graph = new Graph.Builder();
		graph.attr(DotAttributes._NAME__GNE, "LayoutMapping")
				.attr(DotAttributes._TYPE__G, DotAttributes._TYPE__G__DIGRAPH)
				.attr(DotAttributes.LAYOUT__G, Layout.DOT.toString());
		assertTrue("'dot'",
				dotExport.exportDot(graph.build()).contains("layout=\"dot\""));
		graph.attr(DotAttributes.LAYOUT__G, Layout.TWOPI.toString());
		assertTrue("'twopi'", dotExport.exportDot(graph.build())
				.contains("layout=\"twopi\""));
		graph.attr(DotAttributes.LAYOUT__G, Layout.OSAGE.toString());
		assertTrue("'osage'", dotExport.exportDot(graph.build())
				.contains("layout=\"osage\""));
		graph.attr(DotAttributes.LAYOUT__G, Layout.FDP.toString());
		assertTrue("'fdp'",
				dotExport.exportDot(graph.build()).contains("layout=\"fdp\""));
		graph.attr(DotAttributes.LAYOUT__G, Layout.SFDP.toString());
		assertTrue("'sfdp'",
				dotExport.exportDot(graph.build()).contains("layout=\"sfdp\""));
	}

	private void testDotExport(final Graph graph, String fileName) {
		// test exporting the graph into a string
		String dot = dotExport.exportDot(graph);
		String fileContents = DotFileUtils
				.read(new File(RESOURCES_TESTS + fileName));
		assertEquals(fileContents, dot);

		/* verify that there is no blank lines in the exported dot string */
		assertNoBlankLines(dot);

		// test exporting the graph into a file
		File outputFile = new File(OUTPUT, fileName);
		dotExport.exportDot(graph, outputFile.getPath());
		Assert.assertTrue("Generated file " + outputFile.getName() //$NON-NLS-1$
				+ " must exist!", outputFile.exists());
		String dotRead = DotFileUtils.read(outputFile);
		Assert.assertEquals("File output and String output should be equal;", //$NON-NLS-1$
				dot, dotRead);
	}

	private void assertNoBlankLines(final String dot) {
		Scanner scanner = new Scanner(dot);
		while (scanner.hasNextLine()) {
			if (scanner.nextLine().trim().equals("")) { //$NON-NLS-1$
				Assert.fail("Resulting DOT should contain no blank lines;"); //$NON-NLS-1$
			}
		}
		scanner.close();
	}
}
