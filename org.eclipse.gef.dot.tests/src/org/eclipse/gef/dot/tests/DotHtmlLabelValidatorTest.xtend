/*******************************************************************************
 * Copyright (c) 2017, 2018 itemis AG and others.
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
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import org.eclipse.emf.ecore.EClass
import org.eclipse.gef.dot.internal.language.DotHtmlLabelInjectorProvider
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlLabel
import org.eclipse.xtext.diagnostics.Diagnostic
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static org.eclipse.gef.dot.internal.language.htmllabel.HtmllabelPackage.Literals.*

import static extension org.junit.Assert.assertEquals

@RunWith(XtextRunner)
@InjectWith(DotHtmlLabelInjectorProvider)
class DotHtmlLabelValidatorTest {

	@Inject extension ParseHelper<HtmlLabel>
	@Inject extension ValidationTestHelper

	@Test(timeout=2000) def void incomplete_tag01() {
		'''<TABLE</TABLE>'''.parse
	}

	@Test(timeout=2000) def void incomplete_tag02() {
		'''<T</TABLE>'''.parse
	}

	@Test def invalid_comment() {
		// HTML comments are not allowed inside a tag
		val htmlLabel = '''<B <!--HTML comment--> >string</B>'''.parse.assertNumberOfIssues(8)
		htmlLabel.assertError(HTML_TAG, Diagnostic.SYNTAX_DIAGNOSTIC, "no viable alternative at input '<'")
		htmlLabel.assertError(HTML_TAG, Diagnostic.SYNTAX_DIAGNOSTIC, "no viable alternative at character '!'")
		htmlLabel.assertError(HTML_TAG, Diagnostic.SYNTAX_DIAGNOSTIC, "no viable alternative at character '-'")
		htmlLabel.assertError(HTML_TAG, Diagnostic.SYNTAX_DIAGNOSTIC, "no viable alternative at character '-'")
		htmlLabel.assertError(HTML_TAG, Diagnostic.SYNTAX_DIAGNOSTIC, "mismatched input '>' expecting RULE_ASSIGN")
		htmlLabel.assertHtmlTagError("Tag '<HTML>' is not closed (expected '</HTML>' but got '</B>').")
		htmlLabel.assertHtmlTagError("Tag '<HTML>' is not supported.")
		htmlLabel.assertHtmlAttributeError("Attribute 'comment--' is not allowed inside '<HTML>'.")
	}

	@Test def tag_wrongly_closed() {
		val htmlLabel = '''<test>string</B>'''.parse.assertNumberOfIssues(2)
		htmlLabel.assertHtmlTagError("Tag '<test>' is not closed (expected '</test>' but got '</B>').")
		htmlLabel.assertHtmlTagError("Tag '<test>' is not supported.")
	}

	@Test def unknown_parent() {
		val htmlLabel = '''<foo><tr></tr></foo>'''.parse.assertNumberOfIssues(2)
		htmlLabel.assertHtmlTagError("Tag '<foo>' is not supported.")
		htmlLabel.assertHtmlTagError("Tag '<tr>' is not allowed inside '<foo>', but only inside '<TABLE>'.")
	}

	@Test def invalid_parent1() {
		'''<tr></tr>'''.parse.assertNumberOfIssues(1).
		assertHtmlTagError("Tag '<tr>' is not allowed inside '<ROOT>', but only inside '<TABLE>'.")
	}

	@Test def invalid_parent2() {
		'''<table><U></U></table>'''.parse.assertNumberOfIssues(1).
		assertHtmlTagError("Tag '<U>' is not allowed inside '<table>', but only inside '<TD>', '<SUB>', '<B>', '<S>', '<ROOT>', '<U>', '<I>', '<FONT>', '<O>', '<SUP>'.")
	}

	@Test def invalid_attribute_in_valid_tag() {
		'''<table foo="bar"></table>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("Attribute 'foo' is not allowed inside '<table>'.")
	}

	@Test def invalid_attribute_in_invalid_tag() {
		val htmlLabel = '''<foo bar="baz"></foo>'''.parse.assertNumberOfIssues(2)
		htmlLabel.assertHtmlTagError("Tag '<foo>' is not supported.")
		htmlLabel.assertHtmlAttributeError("Attribute 'bar' is not allowed inside '<foo>'.")
	}

	@Test def string_literal_is_not_allowed() {
		'''<BR>string</BR>'''.parse.assertNumberOfIssues(1).assertStringLiteralError
		'''<TABLE>string</TABLE>'''.parse.assertNumberOfIssues(1).assertStringLiteralError
		'''<TABLE><HR>string</HR></TABLE>'''.parse.assertNumberOfIssues(1).assertStringLiteralError("HR")
		'''<TABLE><TR>string</TR></TABLE>'''.parse.assertNumberOfIssues(1).assertStringLiteralError("TR")
		'''<TABLE><TR><VR>string</VR></TR></TABLE>'''.parse.assertNumberOfIssues(1).assertStringLiteralError("VR")
		'''<TABLE><TR><TD><IMG>string</IMG></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).assertStringLiteralError("IMG")
	}

	@Test def invalid_siblings() {
		// The graphviz DOT HTML-Like Label grammar does not allow text and
		// table or multiple tables on the same (root or nested) level.

		// testDataList[][0]: html label containing invalid siblings
		// testDataList[][1]: object type 1 to be marked as error prone
		// testDataList[][2]: object type 2 be marked as error prone
		// ...
		val testDataList = #[
			// root level
			#['''<TABLE></TABLE><B></B>''', HTML_TAG, HTML_TAG],
			#['''<TABLE></TABLE><BR></BR>''', HTML_TAG, HTML_TAG],
			#['''<TABLE></TABLE><FONT></FONT>''', HTML_TAG, HTML_TAG],
			#['''<TABLE></TABLE><I></I>''', HTML_TAG, HTML_TAG],
			#['''<TABLE></TABLE><O></O>''', HTML_TAG, HTML_TAG],
			#['''<TABLE></TABLE><S></S>''', HTML_TAG, HTML_TAG],
			#['''<TABLE></TABLE><SUB></SUB>''', HTML_TAG, HTML_TAG],
			#['''<TABLE></TABLE><SUP></SUP>''', HTML_TAG, HTML_TAG],
			#['''<TABLE></TABLE><TABLE></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE></TABLE><U></U>''', HTML_TAG, HTML_TAG],
			#['''<TABLE></TABLE>text''', HTML_TAG, HTML_CONTENT],
			#['''<B></B><TABLE></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<BR></BR><TABLE></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<FONT></FONT><TABLE></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<I></I><TABLE></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<O></O><TABLE></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<S></S><TABLE></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<SUB></SUB><TABLE></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<SUP></SUP><TABLE></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<U></U><TABLE></TABLE>''', HTML_TAG, HTML_TAG],
			#['''text<TABLE></TABLE>''', HTML_TAG, HTML_CONTENT],
			#['''<TABLE></TABLE>text<TABLE></TABLE>''', HTML_TAG, HTML_CONTENT, HTML_TAG],

			// nested level
			#['''<TABLE><TR><TD><TABLE></TABLE><B></B></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><TABLE></TABLE><BR></BR></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><TABLE></TABLE><FONT></FONT></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><TABLE></TABLE><I></I></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><TABLE></TABLE><O></O></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><TABLE></TABLE><S></S></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><TABLE></TABLE><SUB></SUB></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><TABLE></TABLE><SUP></SUP></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><TABLE></TABLE><TABLE></TABLE></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><TABLE></TABLE><U></U></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><TABLE></TABLE>text</TD></TR></TABLE>''', HTML_TAG, HTML_CONTENT],
			#['''<TABLE><TR><TD><B></B><TABLE></TABLE></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><BR></BR><TABLE></TABLE></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><FONT></FONT><TABLE></TABLE></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><I></I><TABLE></TABLE></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><O></O><TABLE></TABLE></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><S></S><TABLE></TABLE></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><SUB></SUB><TABLE></TABLE></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><SUP></SUP><TABLE></TABLE></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><U></U><TABLE></TABLE></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD>text<TABLE></TABLE></TD></TR></TABLE>''', HTML_TAG, HTML_CONTENT],
			#['''<TABLE><TR><TD><IMG/><B></B></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><IMG/><BR></BR></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><IMG/><FONT></FONT></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><IMG/><I></I></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><IMG/><O></O></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><IMG/><S></S></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><IMG/><SUB></SUB></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><IMG/><SUP></SUP></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><IMG/><TABLE></TABLE></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><IMG/><U></U></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><IMG/>text</TD></TR></TABLE>''', HTML_TAG, HTML_CONTENT],
			#['''<TABLE><TR><TD><IMG/><IMG/></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><B></B><IMG/></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><BR></BR><IMG/></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><FONT></FONT><IMG/></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><I></I><IMG/></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><O></O><IMG/></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><S></S><IMG/></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><SUB></SUB><IMG/></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><SUP></SUP><IMG/></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD><U></U><IMG/></TD></TR></TABLE>''', HTML_TAG, HTML_TAG],
			#['''<TABLE><TR><TD>text<IMG/></TD></TR></TABLE>''', HTML_TAG, HTML_CONTENT],
			#['''<TABLE><TR><TD><TABLE></TABLE>text<TABLE></TABLE></TD></TR></TABLE>''', HTML_TAG, HTML_CONTENT, HTML_TAG]
		]

		for (testData : testDataList) {
			val htmlLabelText = testData.get(0).toString
			val htmlLabel = htmlLabelText.parse

			val numberOfErrorProneText = testData.length - 1
			htmlLabel.assertNumberOfIssues(numberOfErrorProneText)

			for (var i = 0; i < numberOfErrorProneText; i++) {
				val objectType = testData.get(i + 1) as EClass
				htmlLabel.assertError(objectType, null,	"Invalid siblings.")
			}
		}
	}

	@Test def self_closing_is_not_allowed() {
		'''<FONT/>'''.parse.assertNumberOfIssues(1).assertSelfClosingTagError
		'''<I/>'''.parse.assertNumberOfIssues(1).assertSelfClosingTagError
		'''<B/>'''.parse.assertNumberOfIssues(1).assertSelfClosingTagError
		'''<U/>'''.parse.assertNumberOfIssues(1).assertSelfClosingTagError
		'''<O/>'''.parse.assertNumberOfIssues(1).assertSelfClosingTagError
		'''<SUB/>'''.parse.assertNumberOfIssues(1).assertSelfClosingTagError
		'''<SUP/>'''.parse.assertNumberOfIssues(1).assertSelfClosingTagError
		'''<S/>'''.parse.assertNumberOfIssues(1).assertSelfClosingTagError
		'''<TABLE/>'''.parse.assertNumberOfIssues(1).assertSelfClosingTagError
		'''<TABLE><TR/></TABLE>'''.parse.assertNumberOfIssues(1).assertSelfClosingTagError("TR")
		'''<TABLE><TR><TD/></TR></TABLE>'''.parse.assertNumberOfIssues(1).assertSelfClosingTagError("TD")
	}

	@Test def invalid_attribute_value_of_tag_BR_ALIGN() {
		'''<BR ALIGN=""/>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct ALIGN: Value has to be one of 'CENTER', 'LEFT', 'RIGHT'.")

		'''<BR ALIGN="foo"/>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct ALIGN: Value has to be one of 'CENTER', 'LEFT', 'RIGHT'.")
	}

	@Test def void invalid_attribute_value_of_tag_FONT_COLOR() {
		// TODO implement
	}

	@Test def void invalid_attribute_value_of_tag_FONT_FACE() {
		// TODO implement
	}

	@Test def void invalid_attribute_value_of_tag_FONT_POINTSIZE() {
		// TODO implement
	}

	@Test def invalid_attribute_value_of_tag_IMG_SCALE() {
		'''<TABLE><TR><TD><IMG SCALE=""/></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct SCALE: Value has to be one of 'FALSE', 'TRUE', 'WIDTH', 'HEIGHT', 'BOTH'.")
		'''<TABLE><TR><TD><IMG SCALE="foo"/></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct SCALE: Value has to be one of 'FALSE', 'TRUE', 'WIDTH', 'HEIGHT', 'BOTH'.")
	}

	@Test def void invalid_attribute_value_of_tag_IMG_SRC() {
		// TODO implement
	}

	@Test def invalid_attribute_value_of_tag_TABLE_ALIGN() {
		'''<TABLE ALIGN=""></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct ALIGN: Value has to be one of 'CENTER', 'LEFT', 'RIGHT'.")
		
		'''<TABLE ALIGN="foo"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct ALIGN: Value has to be one of 'CENTER', 'LEFT', 'RIGHT'.")
	}

	@Test def void invalid_attribute_value_of_tag_TABLE_BGCOLOR() {
		// TODO implement
	}

	@Test def invalid_attribute_value_of_tag_TABLE_BORDER() {
		'''<TABLE BORDER=""></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct BORDER: Value has to be between 0 and 255.")

		'''<TABLE BORDER="foo"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct BORDER: Value has to be between 0 and 255.")

		'''<TABLE BORDER="-2"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-2' is not a correct BORDER: Value has to be between 0 and 255.")

		'''<TABLE BORDER="-1"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-1' is not a correct BORDER: Value has to be between 0 and 255.")

		'''<TABLE BORDER="256"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '256' is not a correct BORDER: Value has to be between 0 and 255.")

		'''<TABLE BORDER="257"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '257' is not a correct BORDER: Value has to be between 0 and 255.")
	}

	@Test def invalid_attribute_value_of_tag_TABLE_CELLBORDER() {
		'''<TABLE CELLBORDER=""></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct CELLBORDER: Value has to be between 0 and 127.")

		'''<TABLE CELLBORDER="foo"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct CELLBORDER: Value has to be between 0 and 127.")

		'''<TABLE CELLBORDER="-2"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-2' is not a correct CELLBORDER: Value has to be between 0 and 127.")

		'''<TABLE CELLBORDER="-1"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-1' is not a correct CELLBORDER: Value has to be between 0 and 127.")

		'''<TABLE CELLBORDER="128"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '128' is not a correct CELLBORDER: Value has to be between 0 and 127.")

		'''<TABLE CELLBORDER="129"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '129' is not a correct CELLBORDER: Value has to be between 0 and 127.")
	}

	@Test def invalid_attribute_value_of_tag_TABLE_CELLPADDING() {
		'''<TABLE CELLPADDING=""></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct CELLPADDING: Value has to be between 0 and 255.")

		'''<TABLE CELLPADDING="foo"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct CELLPADDING: Value has to be between 0 and 255.")

		'''<TABLE CELLPADDING="-2"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-2' is not a correct CELLPADDING: Value has to be between 0 and 255.")

		'''<TABLE CELLPADDING="-1"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-1' is not a correct CELLPADDING: Value has to be between 0 and 255.")

		'''<TABLE CELLPADDING="256"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '256' is not a correct CELLPADDING: Value has to be between 0 and 255.")

		'''<TABLE CELLPADDING="257"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '257' is not a correct CELLPADDING: Value has to be between 0 and 255.")
	}

	@Test def invalid_attribute_value_of_tag_TABLE_CELLSPACING() {
		'''<TABLE CELLSPACING=""></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct CELLSPACING: Value has to be between 0 and 127.")

		'''<TABLE CELLSPACING="foo"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct CELLSPACING: Value has to be between 0 and 127.")

		'''<TABLE CELLSPACING="-2"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-2' is not a correct CELLSPACING: Value has to be between 0 and 127.")

		'''<TABLE CELLSPACING="-1"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-1' is not a correct CELLSPACING: Value has to be between 0 and 127.")

		'''<TABLE CELLSPACING="128"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '128' is not a correct CELLSPACING: Value has to be between 0 and 127.")

		'''<TABLE CELLSPACING="129"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '129' is not a correct CELLSPACING: Value has to be between 0 and 127.")
	}

	@Test def void invalid_attribute_value_of_tag_TABLE_COLOR() {
		// TODO implement
	}

	@Test def invalid_attribute_value_of_tag_TABLE_COLUMNS() {
		'''<TABLE ROWS=""></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct ROWS: Value has to be '*'.")

		'''<TABLE ROWS="foo"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct ROWS: Value has to be '*'.")
	}

	@Test def invalid_attribute_value_of_tag_TABLE_FIXEDSIZE() {
		'''<TABLE FIXEDSIZE=""></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct FIXEDSIZE: Value has to be one of 'FALSE', 'TRUE'.")

		'''<TABLE FIXEDSIZE="foo"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct FIXEDSIZE: Value has to be one of 'FALSE', 'TRUE'.")
	}

	@Test def void invalid_attribute_value_of_tag_TABLE_GRADIENTANGLE() {
		// TODO implement
	}

	@Test def invalid_attribute_value_of_tag_TABLE_HEIGHT() {
		'''<TABLE HEIGHT=""></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct HEIGHT: Value has to be between 0 and 65535.")

		'''<TABLE HEIGHT="foo"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct HEIGHT: Value has to be between 0 and 65535.")

		'''<TABLE HEIGHT="-2"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-2' is not a correct HEIGHT: Value has to be between 0 and 65535.")

		'''<TABLE HEIGHT="-1"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-1' is not a correct HEIGHT: Value has to be between 0 and 65535.")

		'''<TABLE HEIGHT="65536"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '65536' is not a correct HEIGHT: Value has to be between 0 and 65535.")

		'''<TABLE HEIGHT="65537"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '65537' is not a correct HEIGHT: Value has to be between 0 and 65535.")
	}

	@Test def void invalid_attribute_value_of_tag_TABLE_HREF() {
		// TODO implement
	}

	@Test def void invalid_attribute_value_of_tag_TABLE_ID() {
		// TODO implement
	}

	@Test def void invalid_attribute_value_of_tag_TABLE_PORT() {
		// TODO implement
	}

	@Test def invalid_attribute_value_of_tag_TABLE_ROWS() {
		'''<TABLE ROWS=""></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct ROWS: Value has to be '*'.")
		
		'''<TABLE ROWS="foo"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct ROWS: Value has to be '*'.")
	}

	@Test def invalid_attribute_value_of_tag_TABLE_SIDES() {
		'''<TABLE SIDES=""></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct SIDES: Value has to contain only the 'L', 'T', 'R', 'B' characters.")
		
		'''<TABLE SIDES="foo"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct SIDES: Value has to contain only the 'L', 'T', 'R', 'B' characters.")
	}

	@Test def void invalid_attribute_value_of_tag_TABLE_STYLE() {
		// TODO implement
	}

	@Test def void invalid_attribute_value_of_tag_TABLE_TARGET() {
		// TODO implement
	}

	@Test def void invalid_attribute_value_of_tag_TABLE_TITLE() {
		// TODO implement
	}

	@Test def void invalid_attribute_value_of_tag_TABLE_TOOLTIP() {
		// TODO implement
	}

	@Test def invalid_attribute_value_of_tag_TABLE_VALIGN() {
		'''<TABLE VALIGN=""></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct VALIGN: Value has to be one of 'MIDDLE', 'BOTTOM', 'TOP'.")

		'''<TABLE VALIGN="foo"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct VALIGN: Value has to be one of 'MIDDLE', 'BOTTOM', 'TOP'.")
	}

	@Test def invalid_attribute_value_of_tag_TABLE_WIDTH() {
		'''<TABLE WIDTH=""></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct WIDTH: Value has to be between 0 and 65535.")

		'''<TABLE WIDTH="foo"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct WIDTH: Value has to be between 0 and 65535.")

		'''<TABLE WIDTH="-2"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-2' is not a correct WIDTH: Value has to be between 0 and 65535.")

		'''<TABLE WIDTH="-1"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-1' is not a correct WIDTH: Value has to be between 0 and 65535.")

		'''<TABLE WIDTH="65536"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '65536' is not a correct WIDTH: Value has to be between 0 and 65535.")

		'''<TABLE WIDTH="65537"></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '65537' is not a correct WIDTH: Value has to be between 0 and 65535.")
	}

	@Test def invalid_attribute_value_of_tag_TD_ALIGN() {
		'''<TABLE><TR><TD ALIGN=""></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct ALIGN: Value has to be one of 'CENTER', 'LEFT', 'RIGHT', 'TEXT'.")

		'''<TABLE><TR><TD ALIGN="foo"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct ALIGN: Value has to be one of 'CENTER', 'LEFT', 'RIGHT', 'TEXT'.")
	}

	@Test def invalid_attribute_value_of_tag_TD_BALIGN() {
		'''<TABLE><TR><TD BALIGN=""></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct BALIGN: Value has to be one of 'CENTER', 'LEFT', 'RIGHT'.")

		'''<TABLE><TR><TD BALIGN="foo"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct BALIGN: Value has to be one of 'CENTER', 'LEFT', 'RIGHT'.")
	}

	@Test def void invalid_attribute_value_of_tag_TD_BGCOLOR() {
		// TODO implement
	}

	@Test def invalid_attribute_value_of_tag_TD_BORDER() {
		'''<TABLE><TR><TD BORDER=""></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct BORDER: Value has to be between 0 and 255.")

		'''<TABLE><TR><TD BORDER="foo"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct BORDER: Value has to be between 0 and 255.")

		'''<TABLE><TR><TD BORDER="-2"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-2' is not a correct BORDER: Value has to be between 0 and 255.")

		'''<TABLE><TR><TD BORDER="-1"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-1' is not a correct BORDER: Value has to be between 0 and 255.")

		'''<TABLE><TR><TD BORDER="256"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '256' is not a correct BORDER: Value has to be between 0 and 255.")

		'''<TABLE><TR><TD BORDER="257"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '257' is not a correct BORDER: Value has to be between 0 and 255.")
	}

	@Test def invalid_attribute_value_of_tag_TD_CELLPADDING() {
		'''<TABLE><TR><TD CELLPADDING=""></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct CELLPADDING: Value has to be between 0 and 255.")

		'''<TABLE><TR><TD CELLPADDING="foo"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct CELLPADDING: Value has to be between 0 and 255.")

		'''<TABLE><TR><TD CELLPADDING="-2"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-2' is not a correct CELLPADDING: Value has to be between 0 and 255.")

		'''<TABLE><TR><TD CELLPADDING="-1"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-1' is not a correct CELLPADDING: Value has to be between 0 and 255.")

		'''<TABLE><TR><TD CELLPADDING="256"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '256' is not a correct CELLPADDING: Value has to be between 0 and 255.")

		'''<TABLE><TR><TD CELLPADDING="257"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '257' is not a correct CELLPADDING: Value has to be between 0 and 255.")
	}

	@Test def invalid_attribute_value_of_tag_TD_CELLSPACING() {
		'''<TABLE><TR><TD CELLSPACING=""></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct CELLSPACING: Value has to be between 0 and 127.")

		'''<TABLE><TR><TD CELLSPACING="foo"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct CELLSPACING: Value has to be between 0 and 127.")

		'''<TABLE><TR><TD CELLSPACING="-2"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-2' is not a correct CELLSPACING: Value has to be between 0 and 127.")

		'''<TABLE><TR><TD CELLSPACING="-1"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-1' is not a correct CELLSPACING: Value has to be between 0 and 127.")

		'''<TABLE><TR><TD CELLSPACING="128"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '128' is not a correct CELLSPACING: Value has to be between 0 and 127.")

		'''<TABLE><TR><TD CELLSPACING="129"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '129' is not a correct CELLSPACING: Value has to be between 0 and 127.")
	}

	@Test def void invalid_attribute_value_of_tag_TD_COLOR() {
		// TODO implement
	}

	@Test def invalid_attribute_value_of_tag_TD_COLSPAN() {
		'''<TABLE><TR><TD COLSPAN=""></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct COLSPAN: Value has to be between 1 and 65535.")

		'''<TABLE><TR><TD COLSPAN="foo"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct COLSPAN: Value has to be between 1 and 65535.")

		'''<TABLE><TR><TD COLSPAN="-1"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-1' is not a correct COLSPAN: Value has to be between 1 and 65535.")

		'''<TABLE><TR><TD COLSPAN="0"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '0' is not a correct COLSPAN: Value has to be between 1 and 65535.")

		'''<TABLE><TR><TD COLSPAN="65536"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '65536' is not a correct COLSPAN: Value has to be between 1 and 65535.")

		'''<TABLE><TR><TD COLSPAN="65537"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '65537' is not a correct COLSPAN: Value has to be between 1 and 65535.")
	}

	@Test def invalid_attribute_value_of_tag_TD_FIXEDSIZE() {
		'''<TABLE><TR><TD FIXEDSIZE=""></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct FIXEDSIZE: Value has to be one of 'FALSE', 'TRUE'.")

		'''<TABLE><TR><TD FIXEDSIZE="foo"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct FIXEDSIZE: Value has to be one of 'FALSE', 'TRUE'")
	}

	@Test def void invalid_attribute_value_of_tag_TD_GRADIENTANGLE() {
		// TODO implement
	}

	@Test def invalid_attribute_value_of_tag_TD_HEIGHT() {
		'''<TABLE><TR><TD HEIGHT=""></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct HEIGHT: Value has to be between 0 and 65535.")

		'''<TABLE><TR><TD HEIGHT="foo"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct HEIGHT: Value has to be between 0 and 65535.")

		'''<TABLE><TR><TD HEIGHT="-2"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-2' is not a correct HEIGHT: Value has to be between 0 and 65535.")

		'''<TABLE><TR><TD HEIGHT="-1"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-1' is not a correct HEIGHT: Value has to be between 0 and 65535.")

		'''<TABLE><TR><TD HEIGHT="65536"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '65536' is not a correct HEIGHT: Value has to be between 0 and 65535.")

		'''<TABLE><TR><TD HEIGHT="65537"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '65537' is not a correct HEIGHT: Value has to be between 0 and 65535.")
	}

	@Test def void invalid_attribute_value_of_tag_TD_HREF() {
		// TODO implement
	}

	@Test def void invalid_attribute_value_of_tag_TD_ID() {
		// TODO implement
	}

	@Test def void invalid_attribute_value_of_tag_TD_PORT() {
		// TODO implement
	}

	@Test def invalid_attribute_value_of_tag_TD_ROWSPAN() {
		'''<TABLE><TR><TD ROWSPAN=""></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct ROWSPAN: Value has to be between 1 and 65535.")

		'''<TABLE><TR><TD ROWSPAN="foo"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct ROWSPAN: Value has to be between 1 and 65535.")

		'''<TABLE><TR><TD ROWSPAN="-1"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-1' is not a correct ROWSPAN: Value has to be between 1 and 65535.")

		'''<TABLE><TR><TD ROWSPAN="0"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '0' is not a correct ROWSPAN: Value has to be between 1 and 65535.")

		'''<TABLE><TR><TD ROWSPAN="65536"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '65536' is not a correct ROWSPAN: Value has to be between 1 and 65535.")

		'''<TABLE><TR><TD ROWSPAN="65537"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '65537' is not a correct ROWSPAN: Value has to be between 1 and 65535.")
	}

	@Test def invalid_attribute_value_of_tag_TD_SIDES() {
		'''<TABLE><TR><TD SIDES=""></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct SIDES: Value has to contain only the 'L', 'T', 'R', 'B' characters.")

		'''<TABLE><TR><TD SIDES="foo"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct SIDES: Value has to contain only the 'L', 'T', 'R', 'B' characters.")
	}

	@Test def void invalid_attribute_value_of_tag_TD_STYLE() {
		// TODO implement
	}

	@Test def void invalid_attribute_value_of_tag_TD_TARGET() {
		// TODO implement
	}

	@Test def void invalid_attribute_value_of_tag_TD_TITLE() {
		// TODO implement
	}

	@Test def void invalid_attribute_value_of_tag_TD_TOOLTIP() {
		// TODO implement
	}

	@Test def invalid_attribute_value_of_tag_TD_VALIGN() {
		'''<TABLE><TR><TD VALIGN=""></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct VALIGN: Value has to be one of 'MIDDLE', 'BOTTOM', 'TOP'.")

		'''<TABLE><TR><TD VALIGN="foo"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct VALIGN: Value has to be one of 'MIDDLE', 'BOTTOM', 'TOP'.")
	}

	@Test def invalid_attribute_value_of_tag_TD_WIDTH() {
		'''<TABLE><TR><TD WIDTH=""></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '' is not a correct WIDTH: Value has to be between 0 and 65535.")

		'''<TABLE><TR><TD WIDTH="foo"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value 'foo' is not a correct WIDTH: Value has to be between 0 and 65535.")

		'''<TABLE><TR><TD WIDTH="-2"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-2' is not a correct WIDTH: Value has to be between 0 and 65535.")

		'''<TABLE><TR><TD WIDTH="-1"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '-1' is not a correct WIDTH: Value has to be between 0 and 65535.")

		'''<TABLE><TR><TD WIDTH="65536"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '65536' is not a correct WIDTH: Value has to be between 0 and 65535.")

		'''<TABLE><TR><TD WIDTH="65537"></TD></TR></TABLE>'''.parse.assertNumberOfIssues(1).
		assertHtmlAttributeError("The value '65537' is not a correct WIDTH: Value has to be between 0 and 65535.")
	}

	private def assertStringLiteralError(HtmlLabel htmlLabel, String... text) {
		val tagName = if(text.length > 0) text.get(0) else htmlLabel.parts.head.tag.name
		htmlLabel.assertHtmlTagError('''Tag '<«tagName»>' cannot contain a string literal.''')
	}

	private def assertSelfClosingTagError(HtmlLabel htmlLabel, String... text) {
		val tagName = if(text.length > 0) text.get(0) else htmlLabel.parts.head.tag.name
		htmlLabel.assertHtmlTagError('''Tag '<«tagName»/>' cannot be self closing.''')
	}

	private def assertHtmlTagError(HtmlLabel htmlLabel, String message) {
		htmlLabel.assertError(HTML_TAG, null, message)
	}

	private def assertHtmlAttributeError(HtmlLabel htmlLabel, String message) {
		htmlLabel.assertError(HTML_ATTR, null, message)
	}

	private def assertNumberOfIssues(HtmlLabel htmlLabel, int expectedNumberOfIssues) {
		expectedNumberOfIssues.assertEquals(htmlLabel.validate.size)
		htmlLabel
	}
}
