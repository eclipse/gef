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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.gef.dot.internal.language.DotUiInjectorProvider;
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.ui.editor.folding.FoldedPosition;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionProvider;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Injector;

@RunWith(XtextRunner.class)
@InjectWith(DotUiInjectorProvider.class)
public class DotFoldingTests {

	@Inject
	private Injector injector;

	@Inject
	private IFoldingRegionProvider foldingRegionProvider;

	@Test
	public void testComments() {
		testString(DotTestGraphs.EMPTY_WITH_COMMENTS, 1, 10, 4, 7);
	}

	@Test
	public void testGraphWithOneNode() {
		testString(DotTestGraphs.ONE_NODE, 1, 3);
	}

	@Test
	public void testClusters() {
		testString(DotTestGraphs.CLUSTERS, 1, 21, 2, 6, 7, 18);
	}

	@Test
	public void testGraphLabelHTMLLike1() {
		String text = DotTestGraphs
				.GRAPH_LABEL_HTML_LIKE(
						DotTestHtmlLabels.FONT_TAG_WITH_POINT_SIZE_ATTRIBUTE)
				.toString();
		testString(text, 1, 8, 2, 7, 4, 6);
	}

	@Test
	public void testGraphLabelHTMLLike2() {
		String text = DotTestGraphs
				.GRAPH_LABEL_HTML_LIKE(
						DotTestHtmlLabels.FONT_TAG_CONTAINS_TABLE_TAG)
				.toString();
		testString(text, 1, 15, 2, 14, 7, 13, 8, 12, 9, 11, 4, 6);
	}

	@Test
	public void testIncompleteAttributeStatement() {
		String text = "graph {1[color= ]}";
		testString(text);
	}

	@Test
	public void testIncompleteAttributeStatementWithLineBreaks() {
		String text = "graph {\r\n1[color=]\r\n}";
		testString(text, 1, 2);
	}

	private void testString(String text, int... expectedLineNumbers) {
		IXtextDocument xtextDocument = null;

		try {
			xtextDocument = DotEditorUtils.getDocument(injector, text);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertNotNull(xtextDocument);
		Collection<FoldedPosition> actualFoldingRegions = foldingRegionProvider
				.getFoldingRegions(xtextDocument);

		assertFoldingRegions(xtextDocument, expectedLineNumbers,
				actualFoldingRegions);
	}

	private void assertFoldingRegions(IDocument document,
			int[] expectedFoldingRegions,
			Collection<FoldedPosition> actualFoldingRegions) {
		assertEquals(
				"The number of expected folding regions does not match to the number of actual folding regions",
				expectedFoldingRegions.length / 2, actualFoldingRegions.size());

		int i = 0;
		for (Iterator<FoldedPosition> iterator = actualFoldingRegions
				.iterator(); iterator.hasNext(); i += 2) {
			FoldedPosition foldedPosition = iterator.next();
			// convert the calculated folding regions offset and length to start
			// line and end line
			int startLine = -1;
			int endLine = -1;
			try {
				startLine = document.getLineOfOffset(foldedPosition.getOffset())
						+ 1; // line numbering should start by 1
				endLine = document.getLineOfOffset(foldedPosition.getOffset()
						+ foldedPosition.getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}

			assertEquals("The start line does not match:",
					expectedFoldingRegions[i], startLine);
			assertEquals("The end line does not match:",
					expectedFoldingRegions[i + 1], endLine);
		}
	}
}
