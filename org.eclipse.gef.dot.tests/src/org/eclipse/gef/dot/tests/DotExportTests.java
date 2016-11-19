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

	@BeforeClass
	public static void wipe() {
		DotTestUtils.wipeOutput(DotExportTests.OUTPUT, ".dot"); //$NON-NLS-1$
		if (!DotExportTests.OUTPUT.exists()) {
			DotExportTests.OUTPUT.mkdirs();
		}
	}

	protected void testDotGeneration(final Graph graph, String fileName) {
		String dot = new DotExport().exportDot(graph);
		String fileContents = DotFileUtils
				.read(new File(RESOURCES_TESTS + fileName));
		assertEquals(fileContents, dot);

		/* DotExport adds stripping of blank lines and file output: */
		DotExport dotExport = new DotExport();
		String dotString = dotExport.exportDot(graph);
		assertNoBlankLines(dotString);

		dotExport.exportDot(graph, new File(OUTPUT, fileName).getPath());
		Assert.assertTrue(
				"Generated file " + new File(OUTPUT, fileName).getName() //$NON-NLS-1$
						+ " must exist!",
				new File(OUTPUT, fileName).exists());
		String dotRead = DotFileUtils.read(new File(OUTPUT, fileName));
		Assert.assertEquals("File output and String output should be equal;", //$NON-NLS-1$
				dot, dotRead);
	}

	@Test
	public void simpleGraph() {
		testDotGeneration(DotTestUtils.getSimpleGraph(), "simple_graph.dot");
	}

	@Test
	public void directedGraph() {
		testDotGeneration(DotTestUtils.getSimpleDiGraph(),
				"simple_digraph.dot");
	}

	@Test
	public void labeledGraph() {
		testDotGeneration(DotTestUtils.getLabeledGraph(), "labeled_graph.dot");
	}

	@Test
	public void styledGraph() {
		testDotGeneration(DotTestUtils.getStyledGraph(), "styled_graph.dot");
	}

	/** Test setting layout algorithms. */
	@Test
	public void layoutToGraphvizLayoutMapping() {
		Graph.Builder graph = new Graph.Builder();
		graph.attr(DotAttributes._NAME__GNE, "LayoutMapping")
				.attr(DotAttributes._TYPE__G, DotAttributes._TYPE__G__DIGRAPH)
				.attr(DotAttributes.LAYOUT__G, Layout.DOT.toString());
		assertTrue("'dot'", new DotExport().exportDot(graph.build())
				.contains("layout=\"dot\""));
		graph.attr(DotAttributes.LAYOUT__G, Layout.TWOPI.toString());
		assertTrue("'twopi'", new DotExport().exportDot(graph.build())
				.contains("layout=\"twopi\""));
		graph.attr(DotAttributes.LAYOUT__G, Layout.OSAGE.toString());
		assertTrue("'osage'", new DotExport().exportDot(graph.build())
				.contains("layout=\"osage\""));
		graph.attr(DotAttributes.LAYOUT__G, Layout.FDP.toString());
		assertTrue("'fdp'", new DotExport().exportDot(graph.build())
				.contains("layout=\"fdp\""));
		graph.attr(DotAttributes.LAYOUT__G, Layout.SFDP.toString());
		assertTrue("'sfdp'", new DotExport().exportDot(graph.build())
				.contains("layout=\"sfdp\""));
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
