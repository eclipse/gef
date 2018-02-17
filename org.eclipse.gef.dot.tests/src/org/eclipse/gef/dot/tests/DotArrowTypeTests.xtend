/*******************************************************************************
 * Copyright (c) 2017, 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EClass
import org.eclipse.gef.dot.internal.language.DotArrowTypeInjectorProvider
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowType
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowtypePackage
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotArrowTypeInjectorProvider)
class DotArrowTypeTests {
	
	@Inject extension ParseHelper<ArrowType>
	@Inject extension ValidationTestHelper

	@Test def void testInvalidOpenModifier() {
		"ocrow".assertArrowShapeWarning(
			"o",
			"The open modifier 'o' may not be combined with primitive shape 'crow'."
		)
		
		"lteeveeocrowdot".assertArrowShapeWarning(
			"o",
			"The open modifier 'o' may not be combined with primitive shape 'crow'."
		)
	}

	@Test def void testInvalidSideModifier() {
		"rdot".assertArrowShapeWarning(
			"r",
			"The side modifier 'r' may not be combined with primitive shape 'dot'."
		)
		
		"lteeveerdotbox".assertArrowShapeWarning(
			"r",
			"The side modifier 'r' may not be combined with primitive shape 'dot'."
		)
	}

	@Test def void testDeprecatedArrowShape() {
		"openbox".assertDeprecatedArrowShapeWarning(
			"open",
			"The shape 'open' is deprecated."
		)
		
		"lteeveeopenbox".assertDeprecatedArrowShapeWarning(
			"open",
			"The shape 'open' is deprecated."
		)
	}

	@Test def void testNoneIsTheLastArrowShape() {
		"boxnone".assertArrowShapeWarning(
			"none",
			"The shape 'none' may not be the last shape."
		)
		
		"boxdotveenone".assertArrowShapeWarning(
			"none",
			"The shape 'none' may not be the last shape."
		)
	}

	@Test def void testEmptyArrowType() {
		"".parseArrowType
	}

	def private void assertArrowShapeWarning(String text, String errorProneText, String warningMessage) {
		text.assertArrowTypeWarning(errorProneText, warningMessage, ArrowtypePackage.eINSTANCE.arrowShape)
	}

	def private void assertDeprecatedArrowShapeWarning(String text, String errorProneText, String warningMessage) {
		text.assertArrowTypeWarning(errorProneText, warningMessage,	ArrowtypePackage.eINSTANCE.deprecatedArrowShape)
	}

	def private void assertArrowTypeWarning(String text, String errorProneText, String warningMessage, EClass objectType) {
		val ast = text.parseArrowType
		val offset = text.indexOf(errorProneText)
		val length = errorProneText.length
		ast.assertWarning(objectType, null, offset, length, warningMessage)
		
		// verify that this is the only reported issue
		1.assertEquals(ast.validate.size)
	}
	
	def private ArrowType parseArrowType(String modelAsText){
		var ArrowType ast = null
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
