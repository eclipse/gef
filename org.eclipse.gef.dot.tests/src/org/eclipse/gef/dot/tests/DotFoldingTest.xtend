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
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.gef.dot.internal.ui.language.DotActivator
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionProvider
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils.getDocument
import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotFoldingTest {

	@Inject extension IFoldingRegionProvider

	@Test def comments() {
		DotTestGraphs.EMPTY_WITH_COMMENTS.assertFoldingRegions(1, 10, 4, 7)
	}

	@Test def graph_with_one_node() {
		DotTestGraphs.ONE_NODE.assertFoldingRegions(1, 3)
	}

	@Test def clusters() {
		DotTestGraphs.CLUSTERS.assertFoldingRegions(1, 21, 2, 6, 7, 18)
	}

	@Test def graph_label_HTML_like_01() {
		DotTestGraphs.GRAPH_LABEL_HTML_LIKE(DotTestHtmlLabels.FONT_TAG_WITH_POINT_SIZE_ATTRIBUTE).
		assertFoldingRegions(1, 8, 2, 7, 4, 6)
	}

	@Test def graph_label_HTML_like_02() {
		DotTestGraphs.GRAPH_LABEL_HTML_LIKE(DotTestHtmlLabels.FONT_TAG_CONTAINS_TABLE_TAG).
		assertFoldingRegions(1, 15, 2, 14, 7, 13, 8, 12, 9, 11, 4, 6)
	}

	@Test def attribute_list_01() {
		'''
			graph {
				node[
				]
			}
		'''.assertFoldingRegions(1, 4, 2, 3)
	}

	@Test def attribute_list_02() {
		'''
			graph {
				node
				[
				]
			}
		'''.assertFoldingRegions(1, 5, 2, 4, 3, 4)
	}

	@Test def attribute_list_03() {
		DotTestGraphs.COLORLIST_COLOR_E.
		assertFoldingRegions(1, 5, 2, 4)
	}

	@Test def attribute_list_04() {
		DotTestGraphs.COLORLIST_FILLCOLOR_N.
		assertFoldingRegions(1, 6, 2, 5)
	}

	@Test def incomplete_attribute_statement() {
		'''graph {1[color= ]}'''.assertFoldingRegions
	}

	@Test def incomplete_attribute_statement_with_line_breaks() {
		'''
			graph {
				1[color=]
			}
		'''.assertFoldingRegions(1, 3)
	}

	private def assertFoldingRegions(CharSequence text, int... expectedFoldingRegions) {
		val injector = DotActivator.instance.getInjector(DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOT)
		val document = injector.getDocument(text.toString)
		document.assertNotNull

		val actualFoldingRegions = document.foldingRegions
		assertEquals("The number of expected folding regions does not match to the number of actual folding regions",
			expectedFoldingRegions.length / 2, actualFoldingRegions.size)

		var i = 0
		for (actualFoldingRegion: actualFoldingRegions) {
			val actualStartLine = document.getLineOfOffset(actualFoldingRegion.offset) + 1 // line numbering should start by 1
			val expectedStartLine = expectedFoldingRegions.get(i)
			assertEquals("The start line does not match:", expectedStartLine, actualStartLine)

			val actualEndLine = document.getLineOfOffset(actualFoldingRegion.offset + actualFoldingRegion.length)
			val expectedEndLine = expectedFoldingRegions.get(i + 1)
			assertEquals("The end line does not match:", expectedEndLine, actualEndLine)

			i += 2
		}
	}
}
