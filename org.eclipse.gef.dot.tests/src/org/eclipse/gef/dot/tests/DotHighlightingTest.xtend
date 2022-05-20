/*******************************************************************************
 * Copyright (c) 2017, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *    Zoey Prigge    (itemis AG) - strikethrough/deprecation (bug #552993)
 *    Christoph Läubrich - compatibility with later xtend version (https://github.com/eclipse/gef/issues/88)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import org.eclipse.core.resources.IFile
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyleRange
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.widgets.Display
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.testing.AbstractHighlightingTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotHighlightingTest extends AbstractHighlightingTest {

	// lexical highlighting test cases
	@Test def numbers() {
		DotTestGraphs.EDGE_ARROWSIZE_GLOBAL.testHighlighting("1.5", SWT.NORMAL, 125, 125, 125)
	}

	@Test def quoted_attribute_value() {
		 DotTestGraphs.QUOTED_LABELS.testHighlighting("node 1", SWT.NORMAL, 255, 0, 0)
	}

	@Test def unquoted_attribute_value() {
		DotTestGraphs.GRAPH_LAYOUT_DOT.testHighlighting("dot", SWT.NORMAL, 153, 76, 0)
	}

	@Test def void compass_pt() {
		DotTestGraphs.PORTS => [
			testHighlighting("ne", SWT.NORMAL, 153, 76, 0)
			testHighlighting("_", SWT.NORMAL, 153, 76, 0)
		]
	}

	@Test def void comments() {
		DotTestGraphs.EMPTY_WITH_COMMENTS => [
			testHighlighting("// This is a C++-style single line comment.", SWT.NORMAL, 63, 127, 95)
			testHighlighting("/*", SWT.NORMAL, 63, 127, 95)
			testHighlighting("* This is a C++-style", SWT.NORMAL, 63, 127, 95)
			testHighlighting("* multi line comment.", SWT.NORMAL, 63, 127, 95)
			testHighlighting("*/", SWT.NORMAL, 63, 127, 95)
			testHighlighting("# This is considered as a line output from C-preprocessor and discarded.", SWT.NORMAL, 63, 127, 95)
		]
	}

	@Test def void keywords() {
		DotTestGraphs.KEYWORDS => [
			testHighlighting("strict", SWT.BOLD, 0, 0, 0)
			testHighlighting("digraph", SWT.BOLD, 0, 0, 0)
			testHighlighting("\tgraph", SWT.BOLD, 0, 0, 0)
			testHighlighting("node", SWT.BOLD, 0, 0, 0)
			testHighlighting("edge", SWT.BOLD, 0, 0, 0)
			testHighlighting("subgraph", SWT.BOLD, 0, 0, 0)
		]
	}

	// semantic highlighting test cases
	@Test def graph_name() {
		DotTestGraphs.GLOBAL_EDGE_NODE_COLORSCHEME.testHighlighting("G", SWT.NORMAL, 0, 0, 0)
	}

	@Test def node_name() {
		DotTestGraphs.ONE_NODE.testHighlighting("1", SWT.NORMAL, 0, 0, 0)
	}

	@Test def void port() {
		DotTestGraphs.PORTS => [
			testHighlighting("portID", SWT.NORMAL, 0, 153, 76)
			testHighlighting("portID2", SWT.NORMAL, 0, 153, 76)
		]
	}

	@Test def attribute_name() {
		DotTestGraphs.GRAPH_LAYOUT_DOT.testHighlighting("layout", SWT.NORMAL, 0, 76, 153)
	}

	@Test def edge_operator_directed() {
		DotTestGraphs.ONE_DIRECTED_EDGE.testHighlighting("->", SWT.NORMAL, 0, 153, 0)
	}

	@Test def edge_operator_undirected() {
		DotTestGraphs.ONE_EDGE.testHighlighting("--", SWT.NORMAL, 0, 153, 0)
	}

	@Test def void html_label() {
		DotTestGraphs.NODE_LABEL_HTML_LIKE(DotTestHtmlLabels.FONT_TAG_CONTAINS_TABLE_TAG) => [

			// test highlighting of Html tag
			testHighlighting("<", SWT.NORMAL, 63, 127, 127)
			testHighlighting("<font", SWT.NORMAL, 63, 127, 127)
			testHighlighting("<table>", SWT.NORMAL, 63, 127, 127)
			testHighlighting("<tr>", SWT.NORMAL, 63, 127, 127)
			testHighlighting("<td>", SWT.NORMAL, 63, 127, 127)
			testHighlighting("</td>", SWT.NORMAL, 63, 127, 127)
			testHighlighting("</tr>", SWT.NORMAL, 63, 127, 127)
			testHighlighting("</table>", SWT.NORMAL, 63, 127, 127)
			testHighlighting("</font>", SWT.NORMAL, 63, 127, 127)

			// test highlighting of Html attribute name
			testHighlighting("color", SWT.NORMAL, 127, 0, 127)

			// test highlighting of Html attribute equal sign
			testHighlighting("=", SWT.NORMAL, 0, 0, 0)

			// test highlighting of Html attribute value
			testHighlighting('"green"', SWT.ITALIC, 42, 0, 255)

			// test highlighting of Html content
			testHighlighting("text", SWT.NORMAL, 0, 0, 0)

			// test highlighting of Html comment
			testHighlighting("<!--", SWT.NORMAL, 63, 95, 191)
			testHighlighting("Html label with custom font", SWT.NORMAL, 63, 95, 191)
			testHighlighting("-->", SWT.NORMAL, 63, 95, 191)
		]
	}

	@Test def incomplete_html_label() {
		// test highlighting of Html tag
		DotTestGraphs.INCOMPLETE_HTML_LIKE_LABEL.testHighlighting("<", SWT.NORMAL, 63, 127, 127)
	}

	@Test def void deprecated_arrow_type() {
		DotTestGraphs.DEPRECATED_ARROWTYPES => [
			// test unquoted deprecated highlighting
			testHighlighting("ediamond", SWT.NORMAL, 153, 76, 0, true)
			// test quoted deprecated highlighting
			testHighlighting("open", SWT.NORMAL, 255, 0, 0, true)
		]
	}

	private def testHighlighting(CharSequence it, String text, int fontStyle, int red, int green, int blue, boolean strikeout) {
	//TODO: Adapt to XText 2.22 API changes	
		// given
		dslFile(projectName, fileName, fileExtension, it)
		// when
		.openInEditor
		// then
		.testHighlighting(text, fontStyle, red, green, blue, strikeout)
	}

	private def testHighlighting(StyledText styledText, String text, int fontStyle,
		int foregroundR, int foregroundG, int foregroundB, boolean expectedStrikeout) {

		super.testHighlighting(styledText, text, fontStyle, foregroundR, foregroundG, foregroundB, 255, 255, 255)

		val content = styledText.text
		val offset = content.indexOf(text)
		assertNotEquals('''Cannot locate '«text»' in «content»''', -1, offset)

		for (var i = 0; i < text.length; i++) {
			val currentPosition = offset + i
			val character = styledText.getTextRange(currentPosition, 1)
			val styleRange = styledText.getStyleRangeAtOffset(currentPosition)
			if (character.isRelevant) {
				styleRange => [
					assertStrikedout(character, expectedStrikeout)
				]
			}
		}
	}

	private def assertStrikedout(StyleRange it, String character, boolean expected) {
		val actual = strikeout
		assertEquals('''Expected strikeout does not correspond to the actual strikeout on character «character»''',
			expected, actual)
	}

	/**
	 * This derived class includes the fix
	 * https://github.com/eclipse/xtext-eclipse/commit/9c63ca2ed24050d4fa9fba431f7442f0e903925a
	 * available from Xtext Version 2.18 (Eclipse 2019-06).
	 * TODO: remove this workaround as soon as Eclipse 2019-06 will be the minimum platform supported by GEF DOT.
	 */
	protected override openInEditor(IFile dslFile) {
		val editor = dslFile.openEditor

		waitForEventProcessingWorkaround

		editor.internalSourceViewer.textWidget
	}

	protected def waitForEventProcessingWorkaround() {
		while (Display.^default.readAndDispatch) { }
	}
}
