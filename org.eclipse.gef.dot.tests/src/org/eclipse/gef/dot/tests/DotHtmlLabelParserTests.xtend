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
import org.eclipse.gef.dot.internal.language.DotHtmlLabelInjectorProvider
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlLabel
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlTag
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.junit.Assert.assertEquals
import static extension org.junit.Assert.assertNotNull

@RunWith(XtextRunner)
@InjectWith(DotHtmlLabelInjectorProvider)
class DotHtmlLabelParserTests {

	@Inject extension ParseHelper<HtmlLabel>
	@Inject extension ValidationTestHelper

	@Test
	def test_tag_case_insensitivity() {
		DotTestHtmlLabels.MIXED_LOWER_CASE_AND_UPPER_CASE.verify
	}

	@Test
	def test_comment() {
		DotTestHtmlLabels.COMMENT.verify
	}

	@Test
	def test_comment_with_hyphen() {
		DotTestHtmlLabels.COMMENT_WITH_HYPHEN.verify
	}

	@Test
	def test_comment_with_nested_tags() {
		DotTestHtmlLabels.COMMENT_WITH_NESTED_TAG.verify
	}

	@Test
	def test_comment_with_open_tag() {
		DotTestHtmlLabels.COMMENT_WITH_OPEN_TAG.verify
	}

	@Test
	def test_comment_with_close_tag() {
		DotTestHtmlLabels.COMMENT_WITH_CLOSE_TAG.verify
	}

	@Test
	def test_comment_within_table_tag() {
		DotTestHtmlLabels.COMMENT_WITHIN_TABLE_TAG.verify
	}

	@Test
	def test_comment_within_text() {
		DotTestHtmlLabels.COMMENT_WITHIN_TEXT.verify
	}

	@Test(timeout = 2000)
	def test_tag_with_single_quoted_attribute_value() {
		DotTestHtmlLabels.TAG_WITH_SINGLE_QUTOED_ATTRIBUTE_VALUE.verify
	}

	@Test(timeout = 2000)
	def test_tag_with_double_quoted_attribute_value() {
		DotTestHtmlLabels.TAG_WITH_DOUBLE_QUOTED_ATTRIBUTE_VALUE.verify
	}

	@Test
	def test_font_tag_with_point_size_attribute() {
		DotTestHtmlLabels.FONT_TAG_WITH_POINT_SIZE_ATTRIBUTE.verify
	}

	@Test
	def test_font_tag_contains_table_tag() {
		DotTestHtmlLabels.FONT_TAG_CONTAINS_TABLE_TAG.verify
	}

	@Test
	def test_nesting() {
		DotTestHtmlLabels.NESTED_TAGS2.verify
	}

	@Test
	def test_self_closing_tags() {
		DotTestHtmlLabels.SELF_CLOSING_TAGS.verify
	}

	@Test
	def test_quotes_in_html_text() {
		DotTestHtmlLabels.QUOTES_IN_HTML_TEXT.verify
	}

	@Test
	def test_valid_attribute_value_of_tag_BR_ALIGN() {
		'''<BR ALIGN="CENTER"/>'''.verify
		'''<BR ALIGN="LEFT"/>'''.verify
		'''<BR ALIGN="RIGHT"/>'''.verify
		'''<BR ALIGN="center"/>'''.verify
		'''<BR ALIGN="left"/>'''.verify
		'''<BR ALIGN="right"/>'''.verify
	}

	@Test
	def void test_valid_attribute_value_of_tag_FONT_COLOR() {
		// TODO implement
	}

	@Test
	def void test_valid_attribute_value_of_tag_FONT_FACE() {
		// TODO implement
	}

	@Test
	def void test_valid_attribute_value_of_tag_FONT_POINTSIZE() {
		// TODO implement
	}

	@Test
	def test_valid_attribute_value_of_tag_IMG_SCALE() {
		'''<TABLE><TR><TD><IMG SCALE="FALSE"/></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD><IMG SCALE="TRUE"/></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD><IMG SCALE="WIDTH"/></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD><IMG SCALE="HEIGHT"/></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD><IMG SCALE="BOTH"/></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD><IMG SCALE="false"/></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD><IMG SCALE="true"/></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD><IMG SCALE="width"/></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD><IMG SCALE="height"/></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD><IMG SCALE="both"/></TD></TR></TABLE>'''.verify
	}

	@Test
	def void test_valid_attribute_value_of_tag_IMG_SRC() {
		// TODO implement
	}

	@Test
	def void test_valid_attribute_value_of_tag_TABLE_ALIGN() {
		'''<TABLE ALIGN="CENTER"></TABLE>'''.verify
		'''<TABLE ALIGN="LEFT"></TABLE>'''.verify
		'''<TABLE ALIGN="RIGHT"></TABLE>'''.verify
		'''<TABLE ALIGN="center"></TABLE>'''.verify
		'''<TABLE ALIGN="left"></TABLE>'''.verify
		'''<TABLE ALIGN="right"></TABLE>'''.verify
	}

	@Test
	def void test_valid_attribute_value_of_tag_TABLE_BGCOLOR() {
		// TODO implement
	}

	@Test
	def test_valid_attribute_value_of_tag_TABLE_BORDER() {
		'''<TABLE BORDER="0"></TABLE>'''.verify
		'''<TABLE BORDER="1"></TABLE>'''.verify
		'''<TABLE BORDER="254"></TABLE>'''.verify
		'''<TABLE BORDER="255"></TABLE>'''.verify
	}

	@Test
	def test_valid_attribute_value_of_tag_TABLE_CELLBORDER() {
		'''<TABLE CELLBORDER="0"></TABLE>'''.verify
		'''<TABLE CELLBORDER="1"></TABLE>'''.verify
		'''<TABLE CELLBORDER="126"></TABLE>'''.verify
		'''<TABLE CELLBORDER="127"></TABLE>'''.verify
	}

	@Test
	def test_valid_attribute_value_of_tag_TABLE_CELLPADDING() {
		'''<TABLE CELLPADDING="0"></TABLE>'''.verify
		'''<TABLE CELLPADDING="1"></TABLE>'''.verify
		'''<TABLE CELLPADDING="254"></TABLE>'''.verify
		'''<TABLE CELLPADDING="255"></TABLE>'''.verify
	}

	@Test
	def test_valid_attribute_value_of_tag_TABLE_CELLSPACING() {
		'''<TABLE CELLSPACING="0"></TABLE>'''.verify
		'''<TABLE CELLSPACING="1"></TABLE>'''.verify
		'''<TABLE CELLSPACING="126"></TABLE>'''.verify
		'''<TABLE CELLSPACING="127"></TABLE>'''.verify
	}

	@Test
	def void test_valid_attribute_value_of_tag_TABLE_COLOR() {
		// TODO implement
	}

	@Test
	def void test_valid_attribute_value_of_tag_TABLE_COLUMNS() {
		// TODO implement
	}

	@Test
	def test_valid_attribute_value_of_tag_TABLE_FIXEDSIZE() {
		'''<TABLE FIXEDSIZE="FALSE"></TABLE>'''.verify
		'''<TABLE FIXEDSIZE="TRUE"></TABLE>'''.verify
		'''<TABLE FIXEDSIZE="false"></TABLE>'''.verify
		'''<TABLE FIXEDSIZE="true"></TABLE>'''.verify
	}

	@Test
	def void test_valid_attribute_value_of_tag_TABLE_GRADIENTANGLE() {
		// TODO implement
	}

	@Test
	def test_valid_attribute_value_of_tag_TABLE_HEIGHT() {
		'''<TABLE HEIGHT="0"></TABLE>'''.verify
		'''<TABLE HEIGHT="1"></TABLE>'''.verify
		'''<TABLE HEIGHT="65534"></TABLE>'''.verify
		'''<TABLE HEIGHT="65535"></TABLE>'''.verify
	}

	@Test
	def void test_valid_attribute_value_of_tag_TABLE_HREF() {
		// TODO implement
	}

	@Test
	def void test_valid_attribute_value_of_tag_TABLE_ID() {
		// TODO implement
	}

	@Test
	def void test_valid_attribute_value_of_tag_TABLE_PORT() {
		// TODO implement
	}

	@Test
	def test_valid_attribute_value_of_tag_TABLE_ROWS() {
		'''<TABLE ROWS="*"></TABLE>'''.verify
	}

	@Test
	def test_valid_attribute_value_of_tag_TABLE_SIDES() {
		'''<TABLE SIDES="L"></TABLE>'''.verify
		'''<TABLE SIDES="T"></TABLE>'''.verify
		'''<TABLE SIDES="R"></TABLE>'''.verify
		'''<TABLE SIDES="B"></TABLE>'''.verify
		'''<TABLE SIDES="LT"></TABLE>'''.verify
		'''<TABLE SIDES="LR"></TABLE>'''.verify
		'''<TABLE SIDES="LB"></TABLE>'''.verify
		'''<TABLE SIDES="TR"></TABLE>'''.verify
		'''<TABLE SIDES="TB"></TABLE>'''.verify
		'''<TABLE SIDES="RB"></TABLE>'''.verify
		'''<TABLE SIDES="LTR"></TABLE>'''.verify
		'''<TABLE SIDES="TRB"></TABLE>'''.verify
		'''<TABLE SIDES="LRB"></TABLE>'''.verify
		'''<TABLE SIDES="LTB"></TABLE>'''.verify
		'''<TABLE SIDES="LTRB"></TABLE>'''.verify
	}

	@Test
	def void test_valid_attribute_value_of_tag_TABLE_STYLE() {
		// TODO implement
	}

	@Test
	def void test_valid_attribute_value_of_tag_TABLE_TARGET() {
		// TODO implement
	}

	@Test
	def void test_valid_attribute_value_of_tag_TABLE_TITLE() {
		// TODO implement
	}

	@Test
	def void test_valid_attribute_value_of_tag_TABLE_TOOLTIP() {
		// TODO implement
	}

	@Test
	def test_valid_attribute_value_of_tag_TABLE_VALIGN() {
		'''<TABLE VALIGN="MIDDLE"></TABLE>'''.verify
		'''<TABLE VALIGN="BOTTOM"></TABLE>'''.verify
		'''<TABLE VALIGN="TOP"></TABLE>'''.verify
		'''<TABLE VALIGN="middle"></TABLE>'''.verify
		'''<TABLE VALIGN="top"></TABLE>'''.verify
		'''<TABLE VALIGN="bottom"></TABLE>'''.verify
	}

	@Test
	def test_valid_attribute_value_of_tag_TABLE_WIDTH() {
		'''<TABLE WIDTH="0"></TABLE>'''.verify
		'''<TABLE WIDTH="1"></TABLE>'''.verify
		'''<TABLE WIDTH="65534"></TABLE>'''.verify
		'''<TABLE WIDTH="65535"></TABLE>'''.verify
	}

	@Test
	def test_valid_attribute_value_of_tag_TD_ALIGN() {
		'''<TABLE><TR><TD ALIGN="CENTER"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD ALIGN="LEFT"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD ALIGN="RIGHT"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD ALIGN="TEXT"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD ALIGN="center"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD ALIGN="left"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD ALIGN="right"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD ALIGN="text"></TD></TR></TABLE>'''.verify
	}

	@Test
	def test_valid_attribute_value_of_tag_TD_BALIGN() {
		'''<TABLE><TR><TD BALIGN="CENTER"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD BALIGN="LEFT"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD BALIGN="RIGHT"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD BALIGN="center"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD BALIGN="left"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD BALIGN="right"></TD></TR></TABLE>'''.verify
	}

	@Test
	def void test_valid_attribute_value_of_tag_TD_BGCOLOR() {
		// TODO implement
	}

	@Test
	def test_valid_attribute_value_of_tag_TD_BORDER() {
		'''<TABLE><TR><TD BORDER="0"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD BORDER="1"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD BORDER="254"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD BORDER="255"></TD></TR></TABLE>'''.verify
	}

	@Test
	def test_valid_attribute_value_of_tag_TD_CELLPADDING() {
		'''<TABLE><TR><TD CELLPADDING="0"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD CELLPADDING="1"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD CELLPADDING="254"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD CELLPADDING="255"></TD></TR></TABLE>'''.verify
	}

	@Test
	def test_valid_attribute_value_of_tag_TD_CELLSPACING() {
		'''<TABLE><TR><TD CELLSPACING="0"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD CELLSPACING="1"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD CELLSPACING="126"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD CELLSPACING="127"></TD></TR></TABLE>'''.verify
	}

	@Test
	def void test_valid_attribute_value_of_tag_TD_COLOR() {
		// TODO implement
	}

	@Test
	def test_valid_attribute_value_of_tag_TD_COLSPAN() {
		'''<TABLE><TR><TD COLSPAN="1"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD COLSPAN="2"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD COLSPAN="65534"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD COLSPAN="65535"></TD></TR></TABLE>'''.verify
	}

	@Test
	def test_valid_attribute_value_of_tag_TD_FIXEDSIZE() {
		'''<TABLE><TR><TD FIXEDSIZE="FALSE"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD FIXEDSIZE="TRUE"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD FIXEDSIZE="false"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD FIXEDSIZE="true"></TD></TR></TABLE>'''.verify
	}

	@Test
	def void test_valid_attribute_value_of_tag_TD_GRADIENTANGLE() {
		// TODO implement
	}

	@Test
	def test_valid_attribute_value_of_tag_TD_HEIGHT() {
		'''<TABLE><TR><TD HEIGHT="0"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD HEIGHT="1"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD HEIGHT="65534"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD HEIGHT="65535"></TD></TR></TABLE>'''.verify
	}

	@Test
	def void test_valid_attribute_value_of_tag_TD_HREF() {
		// TODO implement
	}

	@Test
	def void test_valid_attribute_value_of_tag_TD_ID() {
		// TODO implement
	}

	@Test
	def void test_valid_attribute_value_of_tag_TD_PORT() {
		// TODO implement
	}

	@Test
	def test_valid_attribute_value_of_tag_TD_ROWSPAN() {
		'''<TABLE><TR><TD ROWSPAN="1"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD ROWSPAN="2"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD ROWSPAN="65534"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD ROWSPAN="65535"></TD></TR></TABLE>'''.verify
	}

	@Test
	def test_valid_attribute_value_of_tag_TD_SIDES() {
		'''<TABLE><TR><TD SIDES="L"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD SIDES="T"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD SIDES="R"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD SIDES="B"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD SIDES="LT"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD SIDES="LR"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD SIDES="LB"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD SIDES="TR"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD SIDES="TB"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD SIDES="RB"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD SIDES="LTR"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD SIDES="TRB"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD SIDES="LRB"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD SIDES="LTB"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD SIDES="LTRB"></TD></TR></TABLE>'''.verify
	}

	@Test
	def void test_valid_attribute_value_of_tag_TD_STYLE() {
		// TODO implement
	}

	@Test
	def void test_valid_attribute_value_of_tag_TD_TARGET() {
		// TODO implement
	}

	@Test
	def void test_valid_attribute_value_of_tag_TD_TITLE() {
		// TODO implement
	}

	@Test
	def void test_valid_attribute_value_of_tag_TD_TOOLTIP() {
		// TODO implement
	}

	@Test
	def test_valid_attribute_value_of_tag_TD_VALIGN() {
		'''<TABLE><TR><TD VALIGN="MIDDLE"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD VALIGN="BOTTOM"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD VALIGN="TOP"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD VALIGN="middle"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD VALIGN="bottom"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD VALIGN="top"></TD></TR></TABLE>'''.verify
	}

	@Test
	def test_valid_attribute_value_of_tag_TD_WIDTH() {
		'''<TABLE><TR><TD WIDTH="0"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD WIDTH="1"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD WIDTH="65534"></TD></TR></TABLE>'''.verify
		'''<TABLE><TR><TD WIDTH="65535"></TD></TR></TABLE>'''.verify
	}

	@Test
	def test_ast_creation() {
		val parts = DotTestHtmlLabels.NESTED_TAGS.parse.parts
		1.assertEquals(2, parts.size)

		// check base table
		val baseTable = parts.get(0).tag
		baseTable.assertNotNull
		val baseTableChildren = baseTable.children
		5.assertEquals(baseTableChildren.size)

		val baseTr1 = baseTableChildren.get(1).tag
		baseTr1.assertNotNull
		"tr".assertEquals(baseTr1.name)
		baseTable.assertEquals(baseTr1.eContainer.eContainer)
		"first".assertEquals(baseTr1.children.get(0).tag.children.get(0).text)

		val baseTr2 = baseTableChildren.get(3).tag
		baseTr2.assertNotNull
		"tr".assertEquals(baseTr2.name)
		baseTable.assertEquals(baseTr2.eContainer.eContainer)

		// check nested table
		val HtmlTag nestedTable = baseTr2.children.get(0).tag.children.get(0).tag
		"table".assertEquals(nestedTable.name)
		"second".assertEquals(
				nestedTable.children.get(0).tag.getChildren.get(0)
						.tag.children.get(0).tag.children.get(0).text)
	}

	private def verify(CharSequence text) {
		text.toString.verify
	}

	private def verify(String text) {
		text.parse.assertNoErrors
	}
}