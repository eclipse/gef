/*******************************************************************************
 * Copyright (c) 2017, 2018 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Zoey Gerrit Prigge  - initial API and implementation (bug #454629)
 *     Tamas Miklossy (itemis AG) - minor refactorings
 *    
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EObject
import org.eclipse.gef.dot.internal.language.DotRecordLabelInjectorProvider
import org.eclipse.gef.dot.internal.language.recordlabel.Field
import org.eclipse.gef.dot.internal.language.recordlabel.RLabel
import org.eclipse.gef.dot.internal.language.recordlabel.RecordlabelFactory
import org.eclipse.gef.dot.internal.language.validation.DotRecordLabelJavaValidator
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static org.eclipse.gef.dot.internal.language.recordlabel.RecordlabelPackage.Literals.*
import static org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotRecordLabelInjectorProvider)
class DotRecordLabelTests {

	@Inject extension ParseHelper<RLabel>
	@Inject extension ValidationTestHelper

	// good Syntax
	@Test def emptyString() {
		''''''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField(null))
		)
	}

	@Test def singleLetter() {
		'''F'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField("F"))
		)
	}

	@Test def specialSign() {
		'''§'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField("§"))
		)
	}

	@Test def word() {
		'''Hello'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField("Hello"))
		)
	}

	@Test def escapedCharacter() {
		'''Please\ read\ §146'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField('''Please\ read\ §146'''))
		)
	}

	@Test def escapedBraceInText() {
		'''Ple\}se146read'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField('''Ple\}se146read'''))
		)
	}

	@Test def escapedBackslash() {
		'''\\'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField('''\\'''))
		)
	}

	@Test def whiteSpaceBetweenLetters() {
		'''k D'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField('''k D'''))
		)
	}

	@Test def separatorSign() {
		'''abc|def'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField("abc"),
				fieldIDinField("def")
			)
		)
	}

	@Test def threeFields() {
		'''abc | def | gh4i'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField("abc"),
				fieldIDinField("def"),
				fieldIDinField("gh4i")
			)
		)
	}

	@Test def simpleFourFields() {
		'''A | B | C | D'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField("A"), fieldIDinField("B"), fieldIDinField("C"), fieldIDinField("D")))
	}

	@Test def emptyRotatedLabel() {
		'''{}'''.hasNoErrors.assertTreeEquals(
			rlabel(rotationWrapper(rlabel(
				fieldIDinField(null)
			)))
		)
	}

	@Test def simpleRotation() {
		'''{ Hi }'''.hasNoErrors.assertTreeEquals(
			rlabel(rotationWrapper(rlabel(
				fieldIDinField("Hi")
			)))
		)
	}

	@Test def rotatedFourFieldsLabel() {
		'''{ Hi | This | Is | Awesome }'''.hasNoErrors.assertTreeEquals(
			rlabel(rotationWrapper(rlabel(
				fieldIDinField("Hi"),
				fieldIDinField("This"),
				fieldIDinField("Is"),
				fieldIDinField("Awesome")
			)))
		)
	}

	@Test def rotatedMoreComplexLabel() {
		'''Hi | {Test | Section 2 } | xyz'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField("Hi"),
				rotationWrapper(rlabel(
					fieldIDinField("Test"),
					fieldIDinField("Section 2")
				)),
				fieldIDinField("xyz")
			)
		)
	}

	@Test def fieldId() {
		'''<fgh> someField'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField("fgh", "someField"))
		)
	}

	@Test def emptyPortname() {
		'''<>'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField("", null)
			)
		)
	}

	@Test def emptyPortnameWithText() {
		'''<> kids'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField("", "kids")
			)
		)
	}

	@Test def namedPort() {
		'''<Label>'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField("Label", null))
		)
	}

	@Test def portInHField() {
		'''{<Label>}'''.hasNoErrors.assertTreeEquals(
			rlabel(rotationWrapper(
				rlabel(fieldIDinField("Label", null))
			))
		)
	}

	@Test def portInHFieldWithText() {
		'''{<Label> Coolstuff!}'''.hasNoErrors.assertTreeEquals(
			rlabel(rotationWrapper(
				rlabel(fieldIDinField("Label", "Coolstuff!"))
			))
		)
	}

	@Test def portWithEscapedCharInName() {
		'''<some_weans\{>'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField('''some_weans\{''', null)
			)
		)
	}

	// complex Parse Tests
	@Test def parseTreeSimple() {
		'''hello word | <port> cool stuff going on '''.assertTreeEquals(rlabel(
			fieldIDinField("hello word"),
			fieldIDinField("port", "cool stuff going on")
		))
	}

	@Test def parseTreeComplex() {
		'''
		hello word | cool stuff going on | { <free> free beer here |
		wine there } | sad it's just a test'''.assertTreeEquals(
			rlabel(fieldIDinField("hello word"), fieldIDinField("cool stuff going on"), rotationWrapper(
				rlabel(
					fieldIDinField("free", "free beer here"),
					fieldIDinField("wine there")
				)
			), fieldIDinField("sad it's just a test"))
		)
	}

	@Test def documentationExampleLine1() {
		'''<f0> left|<f1> mid&#92; dle|<f2> right'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField("f0", "left"),
				fieldIDinField("f1", "mid&#92; dle"),
				fieldIDinField("f2", "right")
			)
		)
	}

	@Test def documentationExampleLine3() {
		'''hello&#92;nworld |{ b |{c|<here> d|e}| f}| g | h'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField("hello&#92;nworld"),
				rotationWrapper(rlabel(
					fieldIDinField("b"),
					rotationWrapper(rlabel(
						fieldIDinField("c"),
						fieldIDinField("here", "d"),
						fieldIDinField("e")
					)),
					fieldIDinField("f")
				)),
				fieldIDinField("g"),
				fieldIDinField("h")
			)
		)
	}

	@Test def complexExampleLineBreak() {
		'''
		hello&#92;nworld |{ b |{c|<here>
		 d
		 |e}| f}|
		g | h'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField("hello&#92;nworld"),
				rotationWrapper(rlabel(
					fieldIDinField("b"),
					rotationWrapper(rlabel(
						fieldIDinField("c"),
						fieldIDinField("here", "d"),
						fieldIDinField("e")
					)),
					fieldIDinField("f")
				)),
				fieldIDinField("g"),
				fieldIDinField("h")
			)
		)
	}

	@Test def complexLineBreakInString() {
		'''
		hello
		world |{ b |{c|<here>
		 d|e}| f}|
		g | h'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField('''hello world'''), //this deviates from graphviz rendering
//				fieldID(''' // this should be the assertion, if rendering identical to graphviz
//				hello
//				world'''),
				rotationWrapper(rlabel(
					fieldIDinField("b"),
					rotationWrapper(rlabel(
						fieldIDinField("c"),
						fieldIDinField("here", "d"),
						fieldIDinField("e")
					)),
					fieldIDinField("f")
				)),
				fieldIDinField("g"),
				fieldIDinField("h")
			)
		)
	}

	@Test def complexExampleUsingSpecialSignsRotated() {
		'''{Animal|+ name : string\l+ age : int\l|+ die() : void\l}'''.hasNoErrors.assertTreeEquals(
			rlabel(rotationWrapper(rlabel(
				fieldIDinField("Animal"),
				fieldIDinField('''+ name : string\l+ age : int\l'''),
				fieldIDinField('''+ die() : void\l''')
			)))
		)

	}

	@Test def fieldIDsWithNoEntry() {
		'''<f0> (nil)| | |-1'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField("f0", "(nil)"),
				fieldIDinField(null),
				fieldIDinField(null),
				fieldIDinField("-1")
			)
		)
	}

	// bad Syntax
	@Test def void singleClosePortFails() { '''>'''.hasSyntaxErrorOnLabel(">") }

	@Test def void singleCloseBraceFails() { '''}'''.hasSyntaxErrorOnLabel("}") }

	@Test def void missingOpenBraceFails() { '''}asas'''.hasSyntaxErrorOnLabel("}") }

	@Test def void escapedOpeningBraceFails() { '''\{ Hello }'''.hasSyntaxErrorOnLabel("}") }

	@Test def void escapedClosingBraceFails() { '''{ Hello \}'''.hasSyntaxErrorOnFieldIDinField("<EOF>") }

	@Test def void escapedOpeningPortFails() { '''\< Hello >'''.hasSyntaxErrorOnLabel(">") }

	@Test def void escapedClosingPortFails() { '''< Hello \>'''.hasSyntaxErrorOnPort("<EOF>") }

	@Test def void missingClosingPortFails() { '''< Hello'''.hasSyntaxErrorOnPort("<EOF>") }

	@Test def void portWithBraceFails() { '''< Hello }>'''.hasSyntaxErrorOnPort(">") }

	@Test def void braceUnclosedFirstFieldFails() { '''{ Hello | MoreHi'''.hasSyntaxErrorOnFieldIDinField("<EOF>") }

	@Test def void braceUnclosedSecondFieldFails() { '''hello|{ hslnh'''.hasSyntaxErrorOnFieldIDinField("<EOF>") }

	@Test def void wrongPosLabelFails() { '''sdsdsdsd<>'''.hasSyntaxErrorOnLabel("<") }

	@Test def void bracesInFieldFail() { '''This{Is}Illegal'''.hasSyntaxErrorOnLabel("{") }

	@Test def void bracesInMiddleFail() { '''This{Is}Illegal'''.hasSyntaxErrorOnLabel("{") }

	@Test def void bracesAfterPortNameFail() { '''<Port1>{Stuff}'''.hasSyntaxErrorOnLabel("{") }

	@Test def void complexBracesMistaken() { '''<f0> left|{ middle|<f2> right} boo'''.hasSyntaxErrorOnLabel("boo") }

	@Test def void missingABraceMiddle() {
		'''
		hello word | cool stuff going on | { <free> free beer here |
		<expensive wine there } | sad its just a test'''.hasSyntaxErrorOnRotationWrapper(">")
	}

	// validation tests
	@Test def void sameNamePortsSameLevel() {
		'''<here>|<here>'''.hasValidationErrorOnFieldIDinField(DotRecordLabelJavaValidator.PORT_NAME_DUPLICATE)
	}

	@Test def void sameNamePortsDifferentLevel() {
		'''a | <b> c | { <d> f | <b> f } | x'''.hasValidationErrorOnFieldID(
			DotRecordLabelJavaValidator.PORT_NAME_DUPLICATE,
			5,
			1
		).hasValidationErrorOnFieldID(
			DotRecordLabelJavaValidator.PORT_NAME_DUPLICATE,
			23,
			1
		)
	}

	@Test def void twoEmptyPortNamesNoError() {
		'''<> a | <> b'''.hasNoErrors
	}

	@Test def void emptyPortNameWarning() {
		'''<>'''.parse.assertWarning(
			FIELD_ID,
			DotRecordLabelJavaValidator.PORT_NAME_NOT_SET
		)
	}

	@Test def complexEmptyPortNameWarning() {
		'''a | <b> c | { <d> f | <> f } | x'''.parse.assertWarning(
			FIELD_ID,
			DotRecordLabelJavaValidator.PORT_NAME_NOT_SET
		)
	}

	@Test def noWhitespaceWarning() {
		'''a | <b> coolstuff | { <d> f\ kinds | <f> f\nbut } | x'''.hasNoIssues
	}

	private def hasValidationErrorOnFieldID(CharSequence content, String error, int offset, int length) {
		content.parse.assertError(FIELD_ID, error, offset, length)
		content
	}

	private def hasValidationErrorOnFieldIDinField(CharSequence content, String error) {
		content.parse.assertError(FIELD_ID, error)
		content
	}

	private def hasNoIssues(CharSequence sequence) {
		sequence.parse.assertNoIssues
	}

	private def hasNoErrors(CharSequence sequence) {
		sequence.parse.assertNoErrors
		sequence
	}

	private def hasSyntaxErrorOnLabel(CharSequence content, String character) {
		content.hasSyntaxError(RLABEL, "'" + character + "'")
	}

	private def hasSyntaxErrorOnRotationWrapper(CharSequence content, String character) {
		content.hasSyntaxError(FIELD, "'" + character + "'")
	}

	private def hasSyntaxErrorOnFieldIDinField(CharSequence content, String character) {
		content.hasSyntaxError(FIELD_ID, "'" + character + "'")
	}

	private def hasSyntaxErrorOnPort(CharSequence content, String character) {
		content.hasSyntaxError(FIELD_ID, "'" + character + "'")
	}

	private def hasSyntaxError(CharSequence content, EClass eClass, String message) {
		content.parse.assertError(eClass, "org.eclipse.xtext.diagnostics.Diagnostic.Syntax", message)
		content
	}

	private def void assertTreeEquals(CharSequence sequenceForParsing, EObject expected) {
		sequenceForParsing.parse.assertTreeEquals(expected)
	}

	private def EObject assertTreeEquals(EObject actual, EObject expected) {
		assertEquals("Objects of different classtype ", expected.eClass, actual.eClass)
		for (attribute : expected.eClass.EAllAttributes) {
			assertEquals("Attribute " + attribute.name + " of class " + expected.eClass.name, expected.eGet(attribute),
				actual.eGet(attribute))
		}
		assertEquals("Number of Child Nodes", expected.eContents.size, actual.eContents.size)
		for (var i = 0; i < expected.eContents.size; i++) {
			actual.eContents.get(i).assertTreeEquals(expected.eContents.get(i))
		}
		actual
	}

	private def rlabel(Field... fields) {
		val label = RecordlabelFactory.eINSTANCE.createRLabel
		label.fields.addAll(fields)
		label
	}

	private def fieldIDinField(String port, String name) {
		val fieldID = RecordlabelFactory.eINSTANCE.createFieldID
		fieldID.name = name
		if (port !== null) {
			fieldID.portNamed = true
			if (port.length > 0)
				fieldID.port = port
		}
		val field = RecordlabelFactory.eINSTANCE.createField
		field.fieldID = fieldID
		field
	}

	private def fieldIDinField(String name) {
		fieldIDinField(null, name)
	}

	private def rotationWrapper(RLabel label) {
		val wrapper = RecordlabelFactory.eINSTANCE.createField
		wrapper.label = label
		wrapper
	}
}
