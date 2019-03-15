/*******************************************************************************
 * Copyright (c) 2017, 2018 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import java.util.List
import org.eclipse.core.resources.IFile
import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyleRange
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.widgets.Display
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.ui.AbstractEditorTest
import org.eclipse.xtext.ui.editor.XtextEditorInfo
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.dot.tests.DotTestUtils.createTestFile

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotHighlightingTests extends AbstractEditorTest {

	@Inject XtextEditorInfo editorInfo

	override setUp() {
		super.setUp
		DotTestUtils.createTestProjectWithXtextNature
	}

	override protected getEditorId() {
		editorInfo.getEditorId
	}

	// lexical highlighting test cases
	@Test def numbers() {
		DotTestGraphs.EDGE_ARROWSIZE_GLOBAL.assertHighlighting("1.5", SWT.NORMAL, 125, 125, 125)
	}

	@Test def quoted_attribute_value() {
		 DotTestGraphs.QUOTED_LABELS.assertHighlighting("node 1", SWT.NORMAL, 255, 0, 0)
	}

	@Test def unquoted_attribute_value() {
		DotTestGraphs.GRAPH_LAYOUT_DOT.assertHighlighting("dot", SWT.NORMAL, 153, 76, 0)
	}

	@Test def void compass_pt() {
		DotTestGraphs.PORTS => [
			assertHighlighting("ne", SWT.NORMAL, 153, 76, 0)
			assertHighlighting("_", SWT.NORMAL, 153, 76, 0)
		]
	}

	@Test def void comments() {
		DotTestGraphs.EMPTY_WITH_COMMENTS => [
			assertHighlighting("// This is a C++-style single line comment.", SWT.NORMAL, 63, 127, 95)
			assertHighlighting("/*", SWT.NORMAL, 63, 127, 95)
			assertHighlighting("* This is a C++-style", SWT.NORMAL, 63, 127, 95)
			assertHighlighting("* multi line comment.", SWT.NORMAL, 63, 127, 95)
			assertHighlighting("*/", SWT.NORMAL, 63, 127, 95)
			assertHighlighting("# This is considered as a line output from C-preprocessor and discarded.", SWT.NORMAL, 63, 127, 95)
		]
	}

	@Test def void keywords() {
		DotTestGraphs.KEYWORDS => [
			assertHighlighting("strict", SWT.BOLD, 0, 0, 0)
			assertHighlighting("digraph", SWT.BOLD, 0, 0, 0)
			assertHighlighting("\tgraph", SWT.BOLD, 0, 0, 0)
			assertHighlighting("node", SWT.BOLD, 0, 0, 0)
			assertHighlighting("edge", SWT.BOLD, 0, 0, 0)
			assertHighlighting("subgraph", SWT.BOLD, 0, 0, 0)
		]
	}

	// semantic highlighting test cases
	@Test def graph_name() {
		DotTestGraphs.GLOBAL_EDGE_NODE_COLORSCHEME.assertHighlighting("G", SWT.NORMAL, 0, 0, 0)
	}

	@Test def node_name() {
		DotTestGraphs.ONE_NODE.assertHighlighting("1", SWT.NORMAL, 0, 0, 0)
	}

	@Test def void port() {
		DotTestGraphs.PORTS => [
			assertHighlighting("portID", SWT.NORMAL, 0, 153, 76)
			assertHighlighting("portID2", SWT.NORMAL, 0, 153, 76)
		]
	}

	@Test def attribute_name() {
		DotTestGraphs.GRAPH_LAYOUT_DOT.assertHighlighting("layout", SWT.NORMAL, 0, 76, 153)
	}

	@Test def edge_operator_directed() {
		DotTestGraphs.ONE_DIRECTED_EDGE.assertHighlighting("->", SWT.NORMAL, 0, 153, 0)
	}

	@Test def edge_operator_undirected() {
		DotTestGraphs.ONE_EDGE.assertHighlighting("--", SWT.NORMAL, 0, 153, 0)
	}

	@Test def void html_label() {
		DotTestGraphs.NODE_LABEL_HTML_LIKE(DotTestHtmlLabels.FONT_TAG_CONTAINS_TABLE_TAG) => [
			
			// test highlighting of Html tag
			assertHighlighting("<", SWT.NORMAL, 63, 127, 127)
			assertHighlighting("<font", SWT.NORMAL, 63, 127, 127)
			assertHighlighting("<table>", SWT.NORMAL, 63, 127, 127)
			assertHighlighting("<tr>", SWT.NORMAL, 63, 127, 127)
			assertHighlighting("<td>", SWT.NORMAL, 63, 127, 127)
			assertHighlighting("</td>", SWT.NORMAL, 63, 127, 127)
			assertHighlighting("</tr>", SWT.NORMAL, 63, 127, 127)
			assertHighlighting("</table>", SWT.NORMAL, 63, 127, 127)
			assertHighlighting("</font>", SWT.NORMAL, 63, 127, 127)
			
			// test highlighting of Html attribute name
			assertHighlighting("color", SWT.NORMAL, 127, 0, 127)
			
			// test highlighting of Html attribute equal sign
			assertHighlighting("=", SWT.NORMAL, 0, 0, 0)
			
			// test highlighting of Html attribute value
			assertHighlighting('"green"', SWT.ITALIC, 42, 0, 255)
			
			// test highlighting of Html content
			assertHighlighting("text", SWT.NORMAL, 0, 0, 0)
			
			// test highlighting of Html comment
			assertHighlighting("<!--", SWT.NORMAL, 63, 95, 191)
			assertHighlighting("Html label with custom font", SWT.NORMAL, 63, 95, 191)
			assertHighlighting("-->", SWT.NORMAL, 63, 95, 191)
		]
	}

	@Test def incomplete_html_label() {
		// test highlighting of Html tag
		DotTestGraphs.INCOMPLETE_HTML_LIKE_LABEL.assertHighlighting("<", SWT.NORMAL, 63, 127, 127)
	}

	private def assertHighlighting(CharSequence it, String text, int fontStyle, int red, int green, int blue) {
		// given
		dslFile
		// when
		.openInEditor
		// then
		.text(text).isHighlightedIn(fontStyle, red, green, blue)
	}

	private def dslFile(CharSequence it) {
		toString.createTestFile
	}

	private def openInEditor(IFile dslFile) {
		val editor = dslFile.openEditor
		
		/*
		 * wait for the Xtext framework HighlightingPresenter.updatePresentation()
		 * to apply the semantic highlighting executed asynchronously
		 */
		waitForEventProcessing

		editor.internalSourceViewer.textWidget
	}

	private def text(StyledText styledText, String text) {
		val List<Pair<String, StyleRange>> elements = newArrayList
		
		val content = styledText.text
		val offset = content.indexOf(text)
		assertNotEquals('''Cannot locate '«text»' in «content»''', -1, offset)
		
		for (var i = 0; i < text.length; i++) {
			val currentPosition = offset + i
			val character = styledText.getTextRange(currentPosition, 1)
			val styleRange = styledText.getStyleRangeAtOffset(currentPosition)
			elements += character -> styleRange
		}
		elements
	}

	private def isHighlightedIn(List<Pair<String, StyleRange>> elements, int fontStyle, int red, int green, int blue) {
		val expectedColor = new Color(null, red, green, blue)
		
		for(element : elements) {
			val character = element.key
			val styleRange = element.value
			// skipping the whitespace characters
			if (character != " " && character != "\t") {
				styleRange => [
					hasFontStyle(character, fontStyle)
					hasForegroundColor(character, expectedColor)
				]
			}
		}
	}

	private def hasFontStyle(StyleRange it, String character, int expected) {
		val actual = fontStyle
		assertEquals('''Expected font style does not correspond to the actual font style on character «character»''',
			expected, actual)
	}

	private def hasForegroundColor(StyleRange it, String character, Color expected) {
		val actual = foreground ?: new Color(null, 0, 0, 0) // the default color is black 
		assertEquals('''Expected foreground color does not correspond to the actual foreground color on character «character»''',
			expected, actual)
	}

	private def waitForEventProcessing() {
		while (Display.^default.readAndDispatch) { }
	}
}
