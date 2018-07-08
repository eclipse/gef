/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Gerrit Prigge (itemis AG) - initial API and implementation (bug #461506)
 *     Tamas Miklossy     (itemis AG) - conversion from Java to Xtend
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.gef.dot.internal.language.DotPortPosInjectorProvider
import org.eclipse.gef.dot.internal.language.portpos.PortPos
import org.eclipse.gef.dot.internal.language.portpos.PortposPackage
import org.eclipse.xtext.diagnostics.Diagnostic
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotPortPosInjectorProvider)
class DotPortPosTests {

	@Inject extension ParseHelper<PortPos>
	@Inject extension ValidationTestHelper

	@Test def compassPointAsName() {
		"w:sw".hasNoErrors
	}

	@Test def noCompassPoint() {
		"hello".hasNoErrors
	}

	@Test def justCompassPoint() {
		"ne".hasNoErrors
	}

	@Test def testTwoColons() {
		"port:w:w".hasOneSyntaxErrorOn("':'")
	}

	@Test def testInvalidCompassPoint() {
		"king:r".hasOneSyntaxErrorOn("'r'")
	}

	private def hasNoErrors(String text) {
		val ast = text.parse
		ast.assertNotNull
		ast.assertNoErrors
	}

	private def hasOneSyntaxErrorOn(String text, String errorProneText) {
		val ast = text.parse
		ast.assertNotNull
		ast.assertError(PortposPackage.eINSTANCE.portPos, Diagnostic.SYNTAX_DIAGNOSTIC, errorProneText)

		// verify that this is the only reported issue
		1.assertEquals(ast.validate.size)
	}
}
