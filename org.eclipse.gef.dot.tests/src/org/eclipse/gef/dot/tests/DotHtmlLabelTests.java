/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Tamas Miklossy   (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.dot.internal.language.DotHtmlLabelInjectorProvider;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlContent;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlLabel;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlTag;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.eclipse.xtext.validation.Issue;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

@RunWith(XtextRunner.class)
@InjectWith(DotHtmlLabelInjectorProvider.class)
public class DotHtmlLabelTests {

	@Inject
	private ParseHelper<HtmlLabel> parseHelper;

	@Inject
	private ValidationTestHelper validationTestHelper;

	@Test(timeout = 2000)
	public void test_tag_with_attribute() {
		parse("<BR ALIGN=\"LEFT\"/>");
	}

	@Test
	public void test_font_tag_with_point_size_attribute() {
		parse("<FONT POINT-SIZE=\"24.0\">line3</FONT>");
	}

	@Test
	public void test_nesting() throws Throwable {
		String text = "<table>\n" + "<tr><td>first</td></tr>\n"
				+ "<tr><td><table><tr><td><b>second</b></td></tr></table></td></tr>\n"
				+ "</table>";
		HtmlLabel htmlLabel = parse(text);
		EList<HtmlContent> parts = htmlLabel.getParts();
		assertEquals(1, parts.size());
		// check base table
		HtmlTag baseTable = parts.get(0).getTag();
		assertNotNull(baseTable);
		EList<HtmlContent> baseTableChildren = baseTable.getChildren();
		assertEquals(5, baseTableChildren.size());
		HtmlTag baseTr1 = baseTableChildren.get(1).getTag();
		assertNotNull(baseTr1);
		assertEquals("tr", baseTr1.getName());
		assertEquals(baseTable, baseTr1.eContainer().eContainer());
		assertEquals("first", baseTr1.getChildren().get(0).getTag()
				.getChildren().get(0).getText());
		HtmlTag baseTr2 = baseTableChildren.get(3).getTag();
		assertNotNull(baseTr2);
		assertEquals("tr", baseTr2.getName());
		assertEquals(baseTable, baseTr2.eContainer().eContainer());
		// check nested table
		HtmlTag nestedTable = baseTr2.getChildren().get(0).getTag()
				.getChildren().get(0).getTag();
		assertEquals("table", nestedTable.getName());
		assertEquals("second",
				nestedTable.getChildren().get(0).getTag().getChildren().get(0)
						.getTag().getChildren().get(0).getTag().getChildren()
						.get(0).getText());
	}

	@Test
	public void test_tag_wrongly_closed() throws Throwable {
		String text = "<test>string</B>";
		// parsing the string fails with one error and one warning:
		// 1) </B> refers to missing tag <B>
		// 2) <test> tag is not supported
		List<Issue> issues = issues(text);
		assertEquals(2, issues.size());
		Map<Severity, Issue> severityToIssue = new IdentityHashMap<>();
		for (Issue issue : issues) {
			severityToIssue.put(issue.getSeverity(), issue);
		}
		assertEquals(2, severityToIssue.keySet().size());
		assertTrue(severityToIssue.containsKey(Severity.ERROR));
		assertTrue(severityToIssue.containsKey(Severity.WARNING));
		assertTrue(severityToIssue.get(Severity.ERROR).getMessage().contains(
				"Tag '<test>' is not closed (expected '</test>' but got '</B>')."));
		assertTrue(severityToIssue.get(Severity.WARNING).getMessage()
				.contains("Tag '<test>' is not supported."));
	}

	@Test
	public void test_tag_case_insensitivity() throws Throwable {
		String text = "<b>string</B>";
		parse(text);
	}

	private List<Issue> issues(String text) {
		try {
			HtmlLabel ast = parseHelper.parse(text);
			assertNotNull(ast);
			List<Issue> issues = validationTestHelper.validate(ast);
			return issues;
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		return Collections.emptyList();
	}

	private HtmlLabel parse(String text) {
		try {
			HtmlLabel ast = parseHelper.parse(text);
			assertNotNull(ast);
			validationTestHelper.assertNoErrors(ast);
			return ast;
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		return null;
	}
}
