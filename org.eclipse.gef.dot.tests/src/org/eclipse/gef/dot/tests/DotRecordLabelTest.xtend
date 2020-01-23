/*******************************************************************************
 * Copyright (c) 2017, 2020 itemis AG and others.
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
import org.eclipse.gef.dot.internal.language.recordlabel.Field
import org.eclipse.gef.dot.internal.language.recordlabel.RLabel
import org.eclipse.gef.dot.internal.language.recordlabel.RecordlabelFactory
import org.eclipse.gef.dot.internal.language.validation.DotRecordLabelValidator
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static org.eclipse.gef.dot.internal.language.recordlabel.RecordlabelPackage.Literals.*
import static org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotRecordLabelInjectorProvider)
class DotRecordLabelTest {

	@Inject extension ParseHelper<RLabel>
	@Inject extension ValidationTestHelper

	// good syntax
	@Test def empty_string() {
		''''''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField(null))
		)
	}

	@Test def single_letter() {
		'''F'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField("F"))
		)
	}

	@Test def special_sign() {
		'''§'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField("§"))
		)
	}

	@Test def word() {
		'''Hello'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField("Hello"))
		)
	}

	@Test def escaped_character() {
		'''Please\ read\ §146'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField('''Please\ read\ §146'''))
		)
	}

	@Test def escaped_brace_in_text() {
		'''Ple\}se146read'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField('''Ple\}se146read'''))
		)
	}

	@Test def escaped_backslash() {
		'''\\'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField('''\\'''))
		)
	}

	@Test def whitespace_between_letters() {
		'''k D'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField('''k D'''))
		)
	}

	@Test def separator_sign() {
		'''abc|def'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField("abc"),
				fieldIDinField("def")
			)
		)
	}

	@Test def three_fields() {
		'''abc | def | gh4i'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField("abc"),
				fieldIDinField("def"),
				fieldIDinField("gh4i")
			)
		)
	}

	@Test def simple_four_fields() {
		'''A | B | C | D'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField("A"), fieldIDinField("B"), fieldIDinField("C"), fieldIDinField("D")))
	}

	@Test def empty_rotated_label() {
		'''{}'''.hasNoErrors.assertTreeEquals(
			rlabel(rotationWrapper(rlabel(
				fieldIDinField(null)
			)))
		)
	}

	@Test def simple_rotation() {
		'''{ Hi }'''.hasNoErrors.assertTreeEquals(
			rlabel(rotationWrapper(rlabel(
				fieldIDinField("Hi")
			)))
		)
	}

	@Test def rotated_four_fields_label() {
		'''{ Hi | This | Is | Awesome }'''.hasNoErrors.assertTreeEquals(
			rlabel(rotationWrapper(rlabel(
				fieldIDinField("Hi"),
				fieldIDinField("This"),
				fieldIDinField("Is"),
				fieldIDinField("Awesome")
			)))
		)
	}

	@Test def rotated_more_complex_label() {
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

	@Test def field_id() {
		'''<fgh> someField'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField("fgh", "someField"))
		)
	}

	@Test def empty_portname() {
		'''<>'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField("", null)
			)
		)
	}

	@Test def empty_portname_with_text() {
		'''<> kids'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField("", "kids")
			)
		)
	}

	@Test def named_port() {
		'''<Label>'''.hasNoErrors.assertTreeEquals(
			rlabel(fieldIDinField("Label", null))
		)
	}

	@Test def port_in_hfield() {
		'''{<Label>}'''.hasNoErrors.assertTreeEquals(
			rlabel(rotationWrapper(
				rlabel(fieldIDinField("Label", null))
			))
		)
	}

	@Test def port_in_hfield_with_text() {
		'''{<Label> Coolstuff!}'''.hasNoErrors.assertTreeEquals(
			rlabel(rotationWrapper(
				rlabel(fieldIDinField("Label", "Coolstuff!"))
			))
		)
	}

	@Test def port_with_escaped_char_in_name() {
		'''<some_weans\{>'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField('''some_weans\{''', null)
			)
		)
	}

	// complex parse tests
	@Test def parsetree_simple() {
		'''hello word | <port> cool stuff going on '''.assertTreeEquals(rlabel(
			fieldIDinField("hello word"),
			fieldIDinField("port", "cool stuff going on")
		))
	}

	@Test def parsetree_complex() {
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

	@Test def documentation_example_line1() {
		'''<f0> left|<f1> mid&#92; dle|<f2> right'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField("f0", "left"),
				fieldIDinField("f1", "mid&#92; dle"),
				fieldIDinField("f2", "right")
			)
		)
	}

	@Test def documentation_example_line3() {
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

	@Test def complex_example_linebreak() {
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

	@Test def complex_linebreak_in_string() {
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

	@Test def complex_example_using_special_signs_rotated() {
		'''{Animal|+ name : string\l+ age : int\l|+ die() : void\l}'''.hasNoErrors.assertTreeEquals(
			rlabel(rotationWrapper(rlabel(
				fieldIDinField("Animal"),
				fieldIDinField('''+ name : string\l+ age : int\l'''),
				fieldIDinField('''+ die() : void\l''')
			)))
		)

	}

	@Test def field_ids_with_no_entry() {
		'''<f0> (nil)| | |-1'''.hasNoErrors.assertTreeEquals(
			rlabel(
				fieldIDinField("f0", "(nil)"),
				fieldIDinField(null),
				fieldIDinField(null),
				fieldIDinField("-1")
			)
		)
	}

	// bad syntax
	@Test def void single_close_port_fails() { '''>'''.hasSyntaxErrorOnLabel(">") }

	@Test def void single_close_brace_fails() { '''}'''.hasSyntaxErrorOnLabel("}") }

	@Test def void missing_open_brace_fails() { '''}asas'''.hasSyntaxErrorOnLabel("}") }

	@Test def void escaped_opening_brace_fails() { '''\{ Hello }'''.hasSyntaxErrorOnLabel("}") }

	@Test def void escaped_closing_brace_fails() { '''{ Hello \}'''.hasSyntaxErrorOnFieldIDinField("<EOF>") }

	@Test def void escaped_opening_port_fails() { '''\< Hello >'''.hasSyntaxErrorOnLabel(">") }

	@Test def void escaped_closing_port_fails() { '''< Hello \>'''.hasSyntaxErrorOnPort("<EOF>") }

	@Test def void missing_closing_port_fails() { '''< Hello'''.hasSyntaxErrorOnPort("<EOF>") }

	@Test def void port_with_brace_fails() { '''< Hello }>'''.hasSyntaxErrorOnPort(">") }

	@Test def void brace_unclosed_first_field_fails() { '''{ Hello | MoreHi'''.hasSyntaxErrorOnFieldIDinField("<EOF>") }

	@Test def void brace_unclosed_second_field_fails() { '''hello|{ hslnh'''.hasSyntaxErrorOnFieldIDinField("<EOF>") }

	@Test def void wrong_pos_label_fails() { '''sdsdsdsd<>'''.hasSyntaxErrorOnLabel("<") }

	@Test def void braces_in_field_fails() { '''This{Is}Illegal'''.hasSyntaxErrorOnLabel("{") }

	@Test def void braces_in_middle_fails() { '''This{Is}Illegal'''.hasSyntaxErrorOnLabel("{") }

	@Test def void braces_after_portname_fails() { '''<Port1>{Stuff}'''.hasSyntaxErrorOnLabel("{") }

	@Test def void complex_braces_mistaken() { '''<f0> left|{ middle|<f2> right} boo'''.hasSyntaxErrorOnLabel("boo") }

	@Test def void missing_a_brace_middle() {
		'''
		hello word | cool stuff going on | { <free> free beer here |
		<expensive wine there } | sad its just a test'''.hasSyntaxErrorOnRotationWrapper(">")
	}

	// validation tests
	@Test def void same_name_ports_same_level() {
		'''<here>|<here>'''.hasValidationErrorOnFieldIDinField(DotRecordLabelValidator.PORT_NAME_DUPLICATE)
	}

	@Test def void same_name_ports_different_level() {
		'''a | <b> c | { <d> f | <b> f } | x'''.hasValidationErrorOnFieldID(
			DotRecordLabelValidator.PORT_NAME_DUPLICATE,
			5,
			1
		).hasValidationErrorOnFieldID(
			DotRecordLabelValidator.PORT_NAME_DUPLICATE,
			23,
			1
		)
	}

	@Test def void two_empty_portnames_no_error() {
		'''<> a | <> b'''.hasNoErrors
	}

	@Test def void empty_portname_warning() {
		'''<>'''.parse.assertWarning(
			FIELD_ID,
			DotRecordLabelValidator.PORT_NAME_NOT_SET
		)
	}

	@Test def complex_empty_portname_warning() {
		'''a | <b> c | { <d> f | <> f } | x'''.parse.assertWarning(
			FIELD_ID,
			DotRecordLabelValidator.PORT_NAME_NOT_SET
		)
	}

	@Test def no_whitespace_warning() {
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
