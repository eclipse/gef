/*******************************************************************************
 * Copyright (c) 2009, 2018 itemis AG and others.
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
 *                     - conversion from Java to Xtend
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import java.io.File
import java.util.Scanner
import org.eclipse.gef.dot.internal.DotExport
import org.eclipse.gef.graph.Graph
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.eclipse.gef.dot.tests.DotTestUtils.RESOURCES_TESTS

import static extension org.eclipse.gef.dot.internal.DotFileUtils.read
import static extension org.junit.Assert.*

/**
 * Tests for the {@link DotExport} class.
 * 
 * @author Fabian Steeg (fsteeg)
 */
class DotExportTests {

	@Rule 
	public val outputFolder = new TemporaryFolder

	extension DotExport = new DotExport

	@Test def simpleGraph() {
		DotTestUtils.getSimpleGraph.assertExportedTo("simple_graph.dot")
	}

	@Test def directedGraph() {
		DotTestUtils.getSimpleDiGraph.assertExportedTo("simple_digraph.dot")
	}

	@Test def labeledGraph() {
		DotTestUtils.getLabeledGraph.assertExportedTo("labeled_graph.dot")
	}

	@Test def styledGraph() {
		DotTestUtils.getStyledGraph.assertExportedTo("styled_graph.dot")
	}

	@Test def clusteredGraph() {
		DotTestUtils.getClusteredGraph.assertExportedTo("clustered_graph.dot")
	}

	private def assertExportedTo(Graph graph, String expectedFileName) {
		val expected = expectedFileName.file.content
		
		graph.exportDot.assertResult(expected)
		graph.exportDotToFile.assertResult(expected)
	}

	private def assertResult(String actual, String expected) {
		actual.hasNoBlankLines
		expected.assertEquals(actual)
	}

	private def exportDotToFile(Graph graph) {
		val outputFile = outputFolder.newFile
		graph.exportDot(outputFile.path)
		assertTrue(
			'''Generated file «outputFile.name» must exist!''', // $NON-NLS-1$
			outputFile.exists)
		outputFile.content
	}

	private def hasNoBlankLines(String dot) {
		val scanner = new Scanner(dot)
		while (scanner.hasNextLine) {
			if (scanner.nextLine.trim.isEmpty) {
				"Resulting DOT should contain no blank lines!".fail // $NON-NLS-1$
			}
		}
		scanner.close
	}

	private def file(String fileName) {
		new File(RESOURCES_TESTS + fileName)
	}

	private def content(File file) {
		file.read
	}
}
