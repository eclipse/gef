/*******************************************************************************
 * Copyright (c) 2009, 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Fabian Steeg    - initial API and implementation (bug #277380)
 *     Tamas Miklossy  - usage of platform specific line separators (bug #490118)
 *                     - minor refactorings
 *                     - conversion from Java to Xtend
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import java.util.Scanner
import org.eclipse.gef.dot.internal.DotExport
import org.eclipse.gef.graph.Graph
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static extension org.eclipse.gef.dot.internal.DotFileUtils.read
import static extension org.eclipse.gef.dot.tests.DotTestUtils.content
import static extension org.junit.Assert.*

/**
 * Tests for the {@link DotExport} class.
 *
 * @author Fabian Steeg (fsteeg)
 */
class DotExportTest {

	@Rule public val outputFolder = new TemporaryFolder

	extension DotExport = new DotExport

	@Test def simple_graph() {
		DotTestUtils.getSimpleGraph.assertExportedTo("simple_graph.dot")
	}

	@Test def directed_graph() {
		DotTestUtils.getSimpleDiGraph.assertExportedTo("simple_digraph.dot")
	}

	@Test def labeled_graph() {
		DotTestUtils.getLabeledGraph.assertExportedTo("labeled_graph.dot")
	}

	@Test def styled_graph() {
		DotTestUtils.getStyledGraph.assertExportedTo("styled_graph.dot")
	}

	@Test def clustered_graph() {
		DotTestUtils.getClusteredGraph.assertExportedTo("clustered_graph.dot")
	}

	private def assertExportedTo(Graph graph, String expectedFileName) {
		val expected = expectedFileName.content.removeMultiLineComments

		graph.exportDot.assertResult(expected)
		graph.exportDotToFile.assertResult(expected)
	}

	private def removeMultiLineComments(String text) {
		// Regex taken from https://blog.ostermiller.org/find-comment
		text.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)","").
		// Remove leading white spaces
		replaceAll("^\\s+", "")
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
		outputFile.read
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
}
