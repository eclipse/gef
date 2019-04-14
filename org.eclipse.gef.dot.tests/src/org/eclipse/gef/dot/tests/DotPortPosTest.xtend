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
class DotPortPosTest {

	@Inject extension ParseHelper<PortPos>
	@Inject extension ValidationTestHelper

	@Test def compass_point_as_name() {
		"w:sw".hasNoErrors
	}

	@Test def no_compass_point() {
		"hello".hasNoErrors
	}

	@Test def just_compass_point() {
		"ne".hasNoErrors
	}

	@Test def two_colons() {
		"port:w:w".hasOneSyntaxErrorOn("':'")
	}

	@Test def invalid_compass_point() {
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
