/*******************************************************************************
 * Copyright (c) 2019, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation (bug #513196)
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlLabel
import org.eclipse.gef.dot.tests.ui.DotHtmlLabelUiInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.eclipse.xtext.ui.testing.AbstractQuickfixTest
import org.junit.Test
import org.junit.runner.RunWith

import static org.eclipse.gef.dot.internal.language.validation.DotHtmlLabelValidator.HTML_ATTRIBUTE_INVALID_ATTRIBUTE_NAME
import static org.eclipse.gef.dot.internal.language.validation.DotHtmlLabelValidator.HTML_TAG_IS_NOT_PROPERLY_CLOSED
import org.junit.Ignore

@RunWith(XtextRunner)
@InjectWith(DotHtmlLabelUiInjectorProvider)
@Ignore
class DotHtmlLabelQuickfixTest extends AbstractQuickfixTest {

	@Inject extension ParseHelper<HtmlLabel>
	@Inject extension ValidationTestHelper

	@Test def fix_html_tag_is_not_properly_closed() {
		'''<I>text</B>'''.testQuickfixesOn(HTML_TAG_IS_NOT_PROPERLY_CLOSED,
			new Quickfix("Change the opening tag to 'B'.", "Change the opening tag from 'I' to 'B'.", '''<B>text</B>'''),
			new Quickfix("Change the closing tag to 'I'.", "Change the closing tag from 'B' to 'I'.", '''<I>text</I>''')
		)
	}

	@Test def fix_html_attribute_invalid_attribute_name() {
		'''<FONT FOO=""></FONT>'''.testQuickfixesOn(HTML_ATTRIBUTE_INVALID_ATTRIBUTE_NAME,
			new Quickfix("Change to 'COLOR'.", "Change 'FOO' to 'COLOR'.", '''<FONT COLOR=""></FONT>'''),
			new Quickfix("Change to 'FACE'.", "Change 'FOO' to 'FACE'.", '''<FONT FACE=""></FONT>'''),
			new Quickfix("Change to 'POINT-SIZE'.", "Change 'FOO' to 'POINT-SIZE'.", '''<FONT POINT-SIZE=""></FONT>''')
		)
	}

	override testQuickfixesOn(CharSequence it, String issueCode, Quickfix... expected) {
		val issue = getValidationIssue(issueCode)
		val actualIssueResolutions = issueResolutionProvider.getResolutions(issue);
		assertEquals("The number of quickfixes does not match!", expected.size, actualIssueResolutions.size)
		for (i : 0..< actualIssueResolutions.size) {
			val actualIssueResolution = actualIssueResolutions.get(i)
			val expectedIssueResolution = expected.get(i)
			expectedIssueResolution.label.assertEquals(actualIssueResolution.label)
			expectedIssueResolution.description.assertEquals(actualIssueResolution.getDescription)
			expectedIssueResolution.result.assertIssueResolutionResult(actualIssueResolution, toString)
		}
	}

	private def getValidationIssue(CharSequence it, String issueCode) {
		val issues = parse.validate
		val issueCandidates = issues.filter[code == issueCode]
		assertEquals("There should be one '" + issueCode + "' validation issue!", 1, issueCandidates.size)
		issueCandidates.head
	}
}
