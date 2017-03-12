/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
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

import java.util.Collection;

import org.eclipse.gef.dot.internal.language.DotInjectorProvider;
import org.eclipse.gef.dot.internal.language.dot.DotAst;
import org.eclipse.gef.dot.internal.ui.language.highlighting.DotHighlightingConfiguration;
import org.eclipse.gef.dot.internal.ui.language.highlighting.DotSemanticHighlightingCalculator;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.util.TextRegion;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;

/**
 * The implementation of this class is mainly taken from the
 * org.eclipse.xtend.ide.tests.highlighting.XtendHighlightingCalculatorTest java
 * class.
 * 
 * @author miklossy
 *
 */
@RunWith(XtextRunner.class)
@InjectWith(DotInjectorProvider.class)
public class DotHighlightingCalculatorTests
		implements IHighlightedPositionAcceptor {

	@Inject
	private DotSemanticHighlightingCalculator calculator;

	@Inject
	private ParseHelper<DotAst> parseHelper;

	private Multimap<TextRegion, String> expectedRegions;

	@Before
	public void setUp() {
		expectedRegions = HashMultimap.create();
	}

	@After
	public void tearDown() {
		expectedRegions = null;
		calculator = null;
	}

	// semantic highlighting test cases
	@Test
	public void nullGuardTest() {
		calculator.provideHighlightingFor(null, this);
	}

	@Test
	public void graphName() {
		test(DotTestGraphs.EXTRACTED_01, "name",
				DotHighlightingConfiguration.GRAPH_NAME_ID);
	}

	@Test
	public void nodeName() {
		test(DotTestGraphs.ONE_NODE, "1",
				DotHighlightingConfiguration.NODE_NAME_ID);
	}

	@Test
	public void port() {
		test(DotTestGraphs.PORTS, "portID",
				DotHighlightingConfiguration.PORT_NAME_ID);
		test(DotTestGraphs.PORTS, "portID2",
				DotHighlightingConfiguration.PORT_NAME_ID);
	}

	@Test
	public void attributeName() {
		test(DotTestGraphs.GRAPH_LAYOUT_DOT, "layout",
				DotHighlightingConfiguration.ATTRIBUTE_NAME_ID);
	}

	@Test
	public void edgeOperatorDirected() {
		test(DotTestGraphs.ONE_DIRECTED_EDGE, "->",
				DotHighlightingConfiguration.EDGE_OP_ID);
	}

	@Test
	public void edgeOperatorUnDirected() {
		test(DotTestGraphs.ONE_EDGE, "--",
				DotHighlightingConfiguration.EDGE_OP_ID);
	}

	@Test
	public void htmlLabel() {
		String text = DotTestGraphs
				.NODE_LABEL_HTML_LIKE(
						DotTestHtmlLabels.FONT_TAG_CONTAINS_TABLE_TAG)
				.toString();

		// test highlighting of Html tag
		expect(text, "<", DotHighlightingConfiguration.HTML_TAG);
		expect(text, "table", DotHighlightingConfiguration.HTML_TAG);
		expect(text, "tr", DotHighlightingConfiguration.HTML_TAG);
		expect(text, "td", DotHighlightingConfiguration.HTML_TAG);
		expect(text, "</", DotHighlightingConfiguration.HTML_TAG);

		// test highlighting of Html attribute name
		expect(text, "color", DotHighlightingConfiguration.HTML_ATTRIBUTE_NAME);

		// test highlighting of Html attribute value
		expect(text, "\"green\"",
				DotHighlightingConfiguration.HTML_ATTRIBUTE_VALUE);

		// test highlighting of Html content
		expect(text, "text", DotHighlightingConfiguration.HTML_CONTENT);

		// test highlighting of Html comment
		expect(text,
				"<!--" + System.lineSeparator()
						+ "\t\t\t\t\tHtml label with custom font"
						+ System.lineSeparator() + "\t\t\t\t-->",
				DotHighlightingConfiguration.HTML_COMMENT);

		highlight(text);
	}

	private void test(String text, String substring,
			String expectedHighlightID) {
		expect(text, substring, expectedHighlightID);
		highlight(text);
	}

	private void expect(String text, String substring, String highlightID) {
		int offset = text.indexOf(substring);
		int length = substring.length();
		expectedRegions.put(new TextRegion(offset, length), highlightID);
	}

	private void highlight(String text) {
		DotAst model = null;
		try {
			model = parseHelper.parse(text);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Cannot create Dot Ast.");
		}
		calculator.provideHighlightingFor((XtextResource) model.eResource(),
				this);
		Assert.assertTrue(expectedRegions.toString(),
				expectedRegions.isEmpty());
	}

	@Override
	public void addPosition(int offset, int length, String... id) {
		Assert.assertTrue("length = " + length, length >= 0);
		TextRegion region = new TextRegion(offset, length);
		Assert.assertEquals(1, id.length);
		Collection<String> expectedIds = expectedRegions.get(region);
		if (expectedIds.contains(id[0])) {
			expectedRegions.remove(region, id[0]);
		}
	}

}
