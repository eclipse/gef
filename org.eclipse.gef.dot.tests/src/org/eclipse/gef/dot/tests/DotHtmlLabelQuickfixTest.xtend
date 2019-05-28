/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation (bug #513196)
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.emf.common.util.URI
import org.eclipse.gef.dot.internal.language.DotHtmlLabelUiInjectorProvider
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlLabel
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.xtext.ui.editor.model.IXtextDocument
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext
import org.eclipse.xtext.ui.editor.quickfix.IssueResolution
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionProvider
import org.junit.Test
import org.junit.runner.RunWith

import static org.eclipse.gef.dot.internal.language.validation.DotHtmlLabelJavaValidator.HTML_TAG_IS_NOT_PROPERLY_CLOSED

import static extension org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils.getDocument
import static extension org.junit.Assert.assertEquals

@RunWith(XtextRunner)
@InjectWith(DotHtmlLabelUiInjectorProvider)
class DotHtmlLabelQuickfixTest {

	@Inject Injector injector
	@Inject extension ParseHelper<HtmlLabel>
	@Inject extension ValidationTestHelper
	@Inject extension IssueResolutionProvider

	@Test def fix_html_tag_is_not_properly_closed() {
		'''<I>text</B>'''.testQuickfixesOn(HTML_TAG_IS_NOT_PROPERLY_CLOSED,
			new Quickfix("Change the opening tag to 'B'.", "Change the opening tag from 'I' to 'B'.", '''<B>text</B>'''),
			new Quickfix("Change the closing tag to 'I'.", "Change the closing tag from 'B' to 'I'.", '''<I>text</I>''')
		)
	}

	/**
	  * Test that the expected quickfixes are offered on a given validation issue in a given DSL text.
	  * 
	  * @param it The initial DSL text.
	  * @param expected The quickfixes that are expected to be offered on the given <code>issueCode</code>.
	  * Each expected quickfix should be described by the following triple:
	  * <ol>
	  * 	<li>the quickfix label</li>
	  * 	<li>the quickfix description</li>
	  * 	<li>the DSL text after the quickfix application</li>
	  * </ol>
	  */
	private def testQuickfixesOn(CharSequence it, String issueCode, Quickfix... expected) {
		val issue = getValidationIssue(issueCode)
		val actualIssueResolutions = issue.getResolutions
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

	private def assertIssueResolutionResult(String expectedResult, IssueResolution actualIssueResolution, String originalText) {
		/*
		 * manually create an IModificationContext with a XtextDocument and call the
		 * apply method of the actualIssueResolution with that IModificationContext
		 */
		val document = injector.getDocument(originalText)
		val modificationContext = new TestModificationContext
		modificationContext.document = document
		new IssueResolution(actualIssueResolution.label, actualIssueResolution.description,
			actualIssueResolution.image, modificationContext, actualIssueResolution.modification,
			actualIssueResolution.relevance).apply
		val actualResult = document.get
		expectedResult.assertEquals(actualResult)
	}

	private static class TestModificationContext implements IModificationContext {
		IXtextDocument doc

		override getXtextDocument() {
			doc
		}

		override getXtextDocument(URI uri) {
			doc
		}

		def setDocument(IXtextDocument doc) {
			this.doc = doc
		}

	}

	@Data
	private static class Quickfix {
		String label
		String description
		String result
	}
}
