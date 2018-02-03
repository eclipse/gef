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
package org.eclipse.gef.dot.tests;

import org.eclipse.gef.dot.internal.ui.language.internal.DotActivator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xtext.junit4.ui.AbstractEditorTest;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.XtextSourceViewer;
import org.junit.Test;

@SuppressWarnings("restriction")
public class DotHighlightingTests extends AbstractEditorTest {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		DotTestUtils.createTestProjectWithXtextNature();
	}

	@Override
	protected String getEditorId() {
		return DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOT;
	}

	// lexical highlighting test cases
	@Test
	public void numbers() {
		String text = DotTestGraphs.EDGE_ARROWSIZE_GLOBAL;
		StyledText textWidget = getTextWidget(text);
		test(textWidget, "1.5", SWT.NORMAL, 125, 125, 125);
	}

	@Test
	public void quotedAttributeValue() {
		String text = DotTestGraphs.QUOTED_LABELS;
		StyledText textWidget = getTextWidget(text);
		test(textWidget, "node 1", SWT.NORMAL, 255, 0, 0);
	}

	@Test
	public void unquotedAttributeValue() {
		String text = DotTestGraphs.GRAPH_LAYOUT_DOT;
		StyledText textWidget = getTextWidget(text);
		test(textWidget, "dot", SWT.NORMAL, 153, 76, 0);
	}

	@Test
	public void compassPt() {
		String text = DotTestGraphs.PORTS;
		StyledText textWidget = getTextWidget(text);
		test(textWidget, "ne", SWT.NORMAL, 153, 76, 0);
		test(textWidget, "_", SWT.NORMAL, 153, 76, 0);
	}

	@Test
	public void comments() {
		String text = DotTestGraphs.EMPTY_WITH_COMMENTS;
		StyledText textWidget = getTextWidget(text);
		test(textWidget, "// This is a C++-style single line comment.",
				SWT.NORMAL, 63, 127, 95);
		test(textWidget, "/*", SWT.NORMAL, 63, 127, 95);
		test(textWidget, "* This is a C++-style", SWT.NORMAL, 63, 127, 95);
		test(textWidget, "* multi line comment.", SWT.NORMAL, 63, 127, 95);
		test(textWidget, "*/", SWT.NORMAL, 63, 127, 95);
		test(textWidget,
				"# This is considered as a line output from C-preprocessor and discarded.",
				SWT.NORMAL, 63, 127, 95);
	}

	@Test
	public void keywords() {
		String text = DotTestGraphs.KEYWORDS;
		StyledText textWidget = getTextWidget(text);
		test(textWidget, "strict", SWT.BOLD, 0, 0, 0);
		test(textWidget, "digraph", SWT.BOLD, 0, 0, 0);
		test(textWidget, "\tgraph", SWT.BOLD, 0, 0, 0);
		test(textWidget, "node", SWT.BOLD, 0, 0, 0);
		test(textWidget, "edge", SWT.BOLD, 0, 0, 0);
		test(textWidget, "subgraph", SWT.BOLD, 0, 0, 0);
	}

	// semantic highlighting test cases
	@Test
	public void graphName() {
		String text = DotTestGraphs.EXTRACTED_01;
		StyledText textWidget = getTextWidget(text);
		test(textWidget, "name", SWT.NORMAL, 0, 0, 0);
	}

	@Test
	public void nodeName() {
		String text = DotTestGraphs.ONE_NODE;
		StyledText textWidget = getTextWidget(text);
		test(textWidget, "1", SWT.NORMAL, 0, 0, 0);
	}

	@Test
	public void port() {
		String text = DotTestGraphs.PORTS;
		StyledText textWidget = getTextWidget(text);
		test(textWidget, "portID", SWT.NORMAL, 0, 153, 76);
		test(textWidget, "portID2", SWT.NORMAL, 0, 153, 76);
	}

	@Test
	public void attributeName() {
		String text = DotTestGraphs.GRAPH_LAYOUT_DOT;
		StyledText textWidget = getTextWidget(text);
		test(textWidget, "layout", SWT.NORMAL, 0, 76, 153);
	}

	@Test
	public void edgeOperatorDirected() {
		String text = DotTestGraphs.ONE_DIRECTED_EDGE;
		StyledText textWidget = getTextWidget(text);
		test(textWidget, "->", SWT.NORMAL, 0, 153, 0);
	}

	@Test
	public void edgeOperatorUnDirected() {
		String text = DotTestGraphs.ONE_EDGE;
		StyledText textWidget = getTextWidget(text);
		test(textWidget, "--", SWT.NORMAL, 0, 153, 0);
	}

	@Test
	public void htmlLabel() {
		String text = DotTestGraphs
				.NODE_LABEL_HTML_LIKE(
						DotTestHtmlLabels.FONT_TAG_CONTAINS_TABLE_TAG)
				.toString();
		StyledText textWidget = getTextWidget(text);

		// test highlighting of Html tag
		test(textWidget, "<", SWT.NORMAL, 63, 127, 127);
		test(textWidget, "<font", SWT.NORMAL, 63, 127, 127);
		test(textWidget, "<table>", SWT.NORMAL, 63, 127, 127);
		test(textWidget, "<tr>", SWT.NORMAL, 63, 127, 127);
		test(textWidget, "<td>", SWT.NORMAL, 63, 127, 127);
		test(textWidget, "</td>", SWT.NORMAL, 63, 127, 127);
		test(textWidget, "</tr>", SWT.NORMAL, 63, 127, 127);
		test(textWidget, "</table>", SWT.NORMAL, 63, 127, 127);
		test(textWidget, "</font>", SWT.NORMAL, 63, 127, 127);

		// test highlighting of Html attribute name
		test(textWidget, "color", SWT.NORMAL, 127, 0, 127);

		// test highlighting of Html attribute equal sign
		test(textWidget, "=", SWT.NORMAL, 0, 0, 0);

		// test highlighting of Html attribute value
		test(textWidget, "\"green\"", SWT.ITALIC, 42, 0, 255);

		// test highlighting of Html content
		test(textWidget, "text", SWT.NORMAL, 0, 0, 0);

		// test highlighting of Html comment
		test(textWidget, "<!--", SWT.NORMAL, 63, 95, 191);
		test(textWidget, "Html label with custom font", SWT.NORMAL, 63, 95,
				191);
		test(textWidget, "-->", SWT.NORMAL, 63, 95, 191);
	}

	private StyledText getTextWidget(String content) {
		XtextEditor editor = null;
		try {
			editor = openEditor(DotTestUtils.createTestFile(content));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		XtextSourceViewer xtextSourceViewer = (XtextSourceViewer) editor
				.getInternalSourceViewer();

		Display display = xtextSourceViewer.getControl().getDisplay();
		while (display.readAndDispatch()) {
			// wait for the Xtext framework
			// HighlightingPresenter.updatePresentation() to apply the semantic
			// highlighting executed asynchronously
		}

		return xtextSourceViewer.getTextWidget();
	}

	private void test(StyledText textWidget, String subString,
			int expectedFontStyle, int expectedR, int expectedG,
			int expectedB) {

		String content = textWidget.getText();
		int startPosition = content.indexOf(subString);
		for (int i = 0; i < subString.length(); i++) {
			int currentPosition = startPosition + i;
			StyleRange styleRange = textWidget
					.getStyleRangeAtOffset(currentPosition);

			String character = textWidget.getContent()
					.getTextRange(currentPosition, 1);

			// skipping the whitespace characters
			if (character.equals(" ") || character.equals("\t")) {
				continue;
			}

			assertEquals(
					"Expected font style does not correspond to the actual font style on character "
							+ character,
					expectedFontStyle, styleRange.fontStyle);

			assertEquals(
					"Expected foreground color does not correspond to the actual foreground color on character "
							+ character,
					new Color(null, expectedR, expectedG, expectedB),
					getActualColor(styleRange.foreground));
		}
	}

	private Color getActualColor(Color color) {
		// the default color is black
		return color == null ? new Color(null, 0, 0, 0) : color;
	}

}
