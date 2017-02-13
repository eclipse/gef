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
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef.dot.internal.language.DotHtmlLabelInjectorProvider;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlLabel;
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

	@Test
	public void test_nesting() throws Throwable {
		String text = "<table>\n" + "<tr><td>first</td></tr>\n"
				+ "<tr><td><table><tr><td><b>second</b></td></tr></table></td></tr>\n"
				+ "</table>";
		List<Issue> errors = errors(text);
		assertEquals(2, errors.size());
		assertTrue(errors.get(0).getMessage()
				.contains("Couldn't resolve reference to HtmlTag 'B'."));
		assertTrue(errors.get(1).getMessage()
				.contains("The test tag is not supported."));
	}

	@Test
	public void test_tag_wrongly_closed() throws Throwable {
		String text = "<test>string</B>";
		// parsing the string fails with one error and one warning:
		// 1) </B> refers to missing tag <B>
		// 2) <test> tag is not supported
		List<Issue> errors = errors(text);
		assertEquals(2, errors.size());
		assertEquals(Severity.ERROR, errors.get(0).getSeverity());
		assertTrue(errors.get(0).getMessage()
				.contains("Couldn't resolve reference to HtmlTag 'B'."));
		assertEquals(Severity.WARNING, errors.get(1).getSeverity());
		assertTrue(errors.get(1).getMessage()
				.contains("The test tag is not supported."));
	}

	@Test
	public void test_tag_case_insensitivity() throws Throwable {
		String text = "<b>string</B>";
		parse(text);
	}

	private List<Issue> errors(String text) {
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
