/*******************************************************************************
 * Copyright (c) 2009, 2017 itemis AG and others.
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

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.eclipse.gef.dot.internal.DotExport;
import org.eclipse.gef.dot.internal.DotFileUtils;
import org.eclipse.gef.graph.Graph;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for the {@link DotExport} class.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class DotExportTests {

	@Rule
	public TemporaryFolder outputFolder = new TemporaryFolder();

	private final DotExport dotExport = new DotExport();

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

	@Test
	public void clusteredGraph() {
		testDotExport(DotTestUtils.getClusteredGraph(), "clustered_graph.dot");
	}

	private void testDotExport(final Graph graph, String fileName) {
		String expected = DotFileUtils
				.read(new File(RESOURCES_TESTS + fileName));

		// test exporting the graph into a string
		String actual = dotExport.exportDot(graph);
		assertEquals(expected, actual);
		assertNoBlankLines(actual);

		// test exporting the graph into a file
		File outputFile = null;
		try {
			outputFile = outputFolder.newFile(fileName);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Cannot create temporary file " + fileName + " "
					+ e.getMessage());
		}

		dotExport.exportDot(graph, outputFile.getPath());
		Assert.assertTrue("Generated file " + outputFile.getName() //$NON-NLS-1$
				+ " must exist!", outputFile.exists());
		actual = DotFileUtils.read(outputFile);
		Assert.assertEquals("File output and String output should be equal;", //$NON-NLS-1$
				expected, actual);
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
