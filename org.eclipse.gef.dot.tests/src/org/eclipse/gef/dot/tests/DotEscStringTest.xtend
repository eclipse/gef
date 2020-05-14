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
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Tamas Miklossy   (itemis AG) - conversion from Java to Xtend
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.gef.dot.internal.language.escstring.EscString
import org.eclipse.gef.dot.internal.language.escstring.Justification
import org.eclipse.gef.dot.internal.language.escstring.JustifiedText
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotEscStringInjectorProvider)
class DotEscStringTest {

	@Inject extension ParseHelper<EscString>
	@Inject extension ValidationTestHelper

	@Test def test_empty() {
		val ast = "".parseEscString
		0.assertEquals(ast.lines.size)
	}

	@Test def test_text_with_escape_sequences() {
		val text = "Some text containing \\arbitrary \\escape \\sequences."
		val lines = text.parseEscString.lines

		// check if the text was parsed as a single line
		1.assertEquals(lines.size)

		// check if the whole text was parsed with the default justification (centered)
		lines.get(0).assertLineProperties(text, Justification.CENTERED)
	}

	@Test def test_justifications() {
		val lines = "center-justified\\nleft-justified\\lright-justified\\rdefault-justified".parseEscString.lines

		// check if parsed as four lines
		4.assertEquals(lines.size)

		// check individual lines
		lines.get(0).assertLineProperties("center-justified", Justification.CENTERED)
		lines.get(1).assertLineProperties("left-justified", Justification.LEFT)
		lines.get(2).assertLineProperties("right-justified", Justification.RIGHT)
		lines.get(3).assertLineProperties("default-justified", Justification.CENTERED)
	}

	private def assertLineProperties(JustifiedText line, String expectedText, Justification expectedJustification) {
		expectedText.assertEquals(line.text)
		expectedJustification.assertEquals(line.justification)
	}

	private def parseEscString(String modelAsText) {
		var EscString ast = null
		try {
			ast = modelAsText.parse
		} catch (Exception e) {
			e.printStackTrace
			fail
		}

		ast.assertNotNull
		ast.assertNoErrors
		ast
	}
}
