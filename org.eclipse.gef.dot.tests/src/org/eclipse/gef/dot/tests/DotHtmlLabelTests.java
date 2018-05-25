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
package org.eclipse.gef.dot.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.gef.dot.internal.language.DotHtmlLabelInjectorProvider;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlContent;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlLabel;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlTag;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmllabelPackage;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

@RunWith(XtextRunner.class)
@InjectWith(DotHtmlLabelInjectorProvider.class)
public class DotHtmlLabelTests {

	@Inject
	private ParseHelper<HtmlLabel> parseHelper;

	@Inject
	private ValidationTestHelper validationTestHelper;

	/*
	 ************************************************************************************************************
	 * Test cases for valid DOT Html like labels
	 ************************************************************************************************************
	 */
	@Test
	public void test_tag_case_insensitivity() {
		parse(DotTestHtmlLabels.MIXED_LOWER_CASE_AND_UPPER_CASE);
	}

	@Test
	public void test_comment() {
		parse(DotTestHtmlLabels.COMMENT);
	}

	@Test
	public void test_comment_with_hyphen() {
		parse(DotTestHtmlLabels.COMMENT_WITH_HYPHEN);
	}

	@Test
	public void test_comment_with_nested_tags() {
		parse(DotTestHtmlLabels.COMMENT_WITH_NESTED_TAG);
	}

	@Test
	public void test_comment_with_open_tag() {
		parse(DotTestHtmlLabels.COMMENT_WITH_OPEN_TAG);
	}

	@Test
	public void test_comment_with_close_tag() {
		parse(DotTestHtmlLabels.COMMENT_WITH_CLOSE_TAG);
	}

	@Test
	public void test_comment_within_table_tag() {
		parse(DotTestHtmlLabels.COMMENT_WITHIN_TABLE_TAG);
	}

	@Test
	public void test_comment_within_text() {
		parse(DotTestHtmlLabels.COMMENT_WITHIN_TEXT);
	}

	@Test(timeout = 2000)
	public void test_tag_with_single_quoted_attribute_value() {
		parse(DotTestHtmlLabels.TAG_WITH_SINGLE_QUTOED_ATTRIBUTE_VALUE);
	}

	@Test(timeout = 2000)
	public void test_tag_with_double_quoted_attribute_value() {
		parse(DotTestHtmlLabels.TAG_WITH_DOUBLE_QUOTED_ATTRIBUTE_VALUE);
	}

	@Test
	public void test_font_tag_with_point_size_attribute() {
		parse(DotTestHtmlLabels.FONT_TAG_WITH_POINT_SIZE_ATTRIBUTE);
	}

	@Test
	public void test_font_tag_contains_table_tag() {
		parse(DotTestHtmlLabels.FONT_TAG_CONTAINS_TABLE_TAG);
	}

	@Test
	public void test_nesting() {
		HtmlLabel htmlLabel = parse(DotTestHtmlLabels.NESTED_TAGS);

		EList<HtmlContent> parts = htmlLabel.getParts();
		assertEquals(1, parts.size());
		// check base table
		HtmlTag baseTable = parts.get(0).getTag();
		assertNotNull(baseTable);
		EList<HtmlContent> baseTableChildren = baseTable.getChildren();
		assertEquals(5, baseTableChildren.size());
		HtmlTag baseTr1 = baseTableChildren.get(1).getTag();
		assertNotNull(baseTr1);
		assertEquals("tr", baseTr1.getName());
		assertEquals(baseTable, baseTr1.eContainer().eContainer());
		assertEquals("first", baseTr1.getChildren().get(0).getTag()
				.getChildren().get(0).getText());
		HtmlTag baseTr2 = baseTableChildren.get(3).getTag();
		assertNotNull(baseTr2);
		assertEquals("tr", baseTr2.getName());
		assertEquals(baseTable, baseTr2.eContainer().eContainer());
		// check nested table
		HtmlTag nestedTable = baseTr2.getChildren().get(0).getTag()
				.getChildren().get(0).getTag();
		assertEquals("table", nestedTable.getName());
		assertEquals("second",
				nestedTable.getChildren().get(0).getTag().getChildren().get(0)
						.getTag().getChildren().get(0).getTag().getChildren()
						.get(0).getText());
	}

	@Test
	public void test_nesting2() {
		parse(DotTestHtmlLabels.NESTED_TAGS2);
	}

	@Test
	public void test_self_closing_tags() {
		parse(DotTestHtmlLabels.SELF_CLOSING_TAGS);
	}

	@Test
	public void test_quotes_in_html_text() {
		parse(DotTestHtmlLabels.QUOTES_IN_HTML_TEXT);
	}

	@Test(timeout = 2000)
	public void test_incomplete_tag01() {
		String text = "<TABLE</TABLE>";
		try {
			parseHelper.parse(text);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test(timeout = 2000)
	public void test_incomplete_tag02() {
		String text = "<T</TABLE>";
		try {
			parseHelper.parse(text);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test_valid_attribute_value_of_tag_BR_ALIGN() {
		parse("<BR ALIGN=\"CENTER\"/>");
		parse("<BR ALIGN=\"LEFT\"/>");
		parse("<BR ALIGN=\"RIGHT\"/>");
		parse("<BR ALIGN=\"center\"/>");
		parse("<BR ALIGN=\"left\"/>");
		parse("<BR ALIGN=\"right\"/>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_FONT_COLOR() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_FONT_FACE() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_FONT_POINTSIZE() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_IMG_SCALE() {
		parse("<TABLE><TR><TD><IMG SCALE=\"FALSE\"/></TD></TR></TABLE>");
		parse("<TABLE><TR><TD><IMG SCALE=\"TRUE\"/></TD></TR></TABLE>");
		parse("<TABLE><TR><TD><IMG SCALE=\"WIDTH\"/></TD></TR></TABLE>");
		parse("<TABLE><TR><TD><IMG SCALE=\"HEIGHT\"/></TD></TR></TABLE>");
		parse("<TABLE><TR><TD><IMG SCALE=\"BOTH\"/></TD></TR></TABLE>");
		parse("<TABLE><TR><TD><IMG SCALE=\"false\"/></TD></TR></TABLE>");
		parse("<TABLE><TR><TD><IMG SCALE=\"true\"/></TD></TR></TABLE>");
		parse("<TABLE><TR><TD><IMG SCALE=\"width\"/></TD></TR></TABLE>");
		parse("<TABLE><TR><TD><IMG SCALE=\"height\"/></TD></TR></TABLE>");
		parse("<TABLE><TR><TD><IMG SCALE=\"both\"/></TD></TR></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_IMG_SRC() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_ALIGN() {
		parse("<TABLE ALIGN=\"CENTER\"></TABLE>");
		parse("<TABLE ALIGN=\"LEFT\"></TABLE>");
		parse("<TABLE ALIGN=\"RIGHT\"></TABLE>");
		parse("<TABLE ALIGN=\"center\"></TABLE>");
		parse("<TABLE ALIGN=\"left\"></TABLE>");
		parse("<TABLE ALIGN=\"right\"></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_BGCOLOR() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_BORDER() {
		parse("<TABLE BORDER=\"0\"></TABLE>");
		parse("<TABLE BORDER=\"1\"></TABLE>");
		parse("<TABLE BORDER=\"254\"></TABLE>");
		parse("<TABLE BORDER=\"255\"></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_CELLBORDER() {
		parse("<TABLE CELLBORDER=\"0\"></TABLE>");
		parse("<TABLE CELLBORDER=\"1\"></TABLE>");
		parse("<TABLE CELLBORDER=\"126\"></TABLE>");
		parse("<TABLE CELLBORDER=\"127\"></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_CELLPADDING() {
		parse("<TABLE CELLPADDING=\"0\"></TABLE>");
		parse("<TABLE CELLPADDING=\"1\"></TABLE>");
		parse("<TABLE CELLPADDING=\"254\"></TABLE>");
		parse("<TABLE CELLPADDING=\"255\"></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_CELLSPACING() {
		parse("<TABLE CELLSPACING=\"0\"></TABLE>");
		parse("<TABLE CELLSPACING=\"1\"></TABLE>");
		parse("<TABLE CELLSPACING=\"126\"></TABLE>");
		parse("<TABLE CELLSPACING=\"127\"></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_COLOR() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_COLUMNS() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_FIXEDSIZE() {
		parse("<TABLE FIXEDSIZE=\"FALSE\"></TABLE>");
		parse("<TABLE FIXEDSIZE=\"TRUE\"></TABLE>");
		parse("<TABLE FIXEDSIZE=\"false\"></TABLE>");
		parse("<TABLE FIXEDSIZE=\"true\"></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_GRADIENTANGLE() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_HEIGHT() {
		parse("<TABLE HEIGHT=\"0\"></TABLE>");
		parse("<TABLE HEIGHT=\"1\"></TABLE>");
		parse("<TABLE HEIGHT=\"65534\"></TABLE>");
		parse("<TABLE HEIGHT=\"65535\"></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_HREF() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_ID() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_PORT() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_ROWS() {
		parse("<TABLE ROWS=\"*\"></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_SIDES() {
		parse("<TABLE SIDES=\"L\"></TABLE>");
		parse("<TABLE SIDES=\"T\"></TABLE>");
		parse("<TABLE SIDES=\"R\"></TABLE>");
		parse("<TABLE SIDES=\"B\"></TABLE>");
		parse("<TABLE SIDES=\"LT\"></TABLE>");
		parse("<TABLE SIDES=\"LR\"></TABLE>");
		parse("<TABLE SIDES=\"LB\"></TABLE>");
		parse("<TABLE SIDES=\"TR\"></TABLE>");
		parse("<TABLE SIDES=\"TB\"></TABLE>");
		parse("<TABLE SIDES=\"RB\"></TABLE>");
		parse("<TABLE SIDES=\"LTR\"></TABLE>");
		parse("<TABLE SIDES=\"TRB\"></TABLE>");
		parse("<TABLE SIDES=\"LRB\"></TABLE>");
		parse("<TABLE SIDES=\"LTB\"></TABLE>");
		parse("<TABLE SIDES=\"LTRB\"></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_STYLE() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_TARGET() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_TITLE() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_TOOLTIP() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_VALIGN() {
		parse("<TABLE VALIGN=\"MIDDLE\"></TABLE>");
		parse("<TABLE VALIGN=\"BOTTOM\"></TABLE>");
		parse("<TABLE VALIGN=\"TOP\"></TABLE>");
		parse("<TABLE VALIGN=\"middle\"></TABLE>");
		parse("<TABLE VALIGN=\"top\"></TABLE>");
		parse("<TABLE VALIGN=\"bottom\"></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TABLE_WIDTH() {
		parse("<TABLE WIDTH=\"0\"></TABLE>");
		parse("<TABLE WIDTH=\"1\"></TABLE>");
		parse("<TABLE WIDTH=\"65534\"></TABLE>");
		parse("<TABLE WIDTH=\"65535\"></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_ALIGN() {
		parse("<TABLE><TR><TD ALIGN=\"CENTER\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD ALIGN=\"LEFT\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD ALIGN=\"RIGHT\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD ALIGN=\"TEXT\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD ALIGN=\"center\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD ALIGN=\"left\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD ALIGN=\"right\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD ALIGN=\"text\"></TD></TR></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_BALIGN() {
		parse("<TABLE><TR><TD BALIGN=\"CENTER\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD BALIGN=\"LEFT\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD BALIGN=\"RIGHT\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD BALIGN=\"center\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD BALIGN=\"left\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD BALIGN=\"right\"></TD></TR></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_BGCOLOR() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_BORDER() {
		parse("<TABLE><TR><TD BORDER=\"0\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD BORDER=\"1\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD BORDER=\"254\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD BORDER=\"255\"></TD></TR></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_CELLPADDING() {
		parse("<TABLE><TR><TD CELLPADDING=\"0\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD CELLPADDING=\"1\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD CELLPADDING=\"254\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD CELLPADDING=\"255\"></TD></TR></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_CELLSPACING() {
		parse("<TABLE><TR><TD CELLSPACING=\"0\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD CELLSPACING=\"1\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD CELLSPACING=\"126\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD CELLSPACING=\"127\"></TD></TR></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_COLOR() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_COLSPAN() {
		parse("<TABLE><TR><TD COLSPAN=\"1\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD COLSPAN=\"2\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD COLSPAN=\"65534\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD COLSPAN=\"65535\"></TD></TR></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_FIXEDSIZE() {
		parse("<TABLE><TR><TD FIXEDSIZE=\"FALSE\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD FIXEDSIZE=\"TRUE\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD FIXEDSIZE=\"false\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD FIXEDSIZE=\"true\"></TD></TR></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_GRADIENTANGLE() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_HEIGHT() {
		parse("<TABLE><TR><TD HEIGHT=\"0\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD HEIGHT=\"1\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD HEIGHT=\"65534\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD HEIGHT=\"65535\"></TD></TR></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_HREF() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_ID() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_PORT() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_ROWSPAN() {
		parse("<TABLE><TR><TD ROWSPAN=\"1\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD ROWSPAN=\"2\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD ROWSPAN=\"65534\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD ROWSPAN=\"65535\"></TD></TR></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_SIDES() {
		parse("<TABLE><TR><TD SIDES=\"L\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD SIDES=\"T\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD SIDES=\"R\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD SIDES=\"B\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD SIDES=\"LT\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD SIDES=\"LR\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD SIDES=\"LB\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD SIDES=\"TR\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD SIDES=\"TB\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD SIDES=\"RB\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD SIDES=\"LTR\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD SIDES=\"TRB\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD SIDES=\"LRB\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD SIDES=\"LTB\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD SIDES=\"LTRB\"></TD></TR></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_STYLE() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_TARGET() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_TITLE() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_TOOLTIP() {
		// TODO implement
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_VALIGN() {
		parse("<TABLE><TR><TD VALIGN=\"MIDDLE\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD VALIGN=\"BOTTOM\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD VALIGN=\"TOP\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD VALIGN=\"middle\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD VALIGN=\"bottom\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD VALIGN=\"top\"></TD></TR></TABLE>");
	}

	@Test
	public void test_valid_attribute_value_of_tag_TD_WIDTH() {
		parse("<TABLE><TR><TD WIDTH=\"0\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD WIDTH=\"1\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD WIDTH=\"65534\"></TD></TR></TABLE>");
		parse("<TABLE><TR><TD WIDTH=\"65535\"></TD></TR></TABLE>");
	}

	/*
	 ************************************************************************************************************
	 * Test cases for invalid DOT Html like labels
	 ************************************************************************************************************
	 */

	@Test
	public void test_invalid_comment() throws Exception {
		// HTML comments are not allowed inside a tag
		String text = "<B <!--HTML comment--> >string</B>";

		HtmlLabel htmlLabel = parseHelper.parse(text);

		// verify that there are some reported issues
		assertNumberOfIssues(htmlLabel, 8);
	}

	@Test
	public void test_tag_wrongly_closed() throws Exception {
		String text = "<test>string</B>";

		HtmlLabel htmlLabel = parseHelper.parse(text);

		validationTestHelper.assertError(htmlLabel,
				HtmllabelPackage.eINSTANCE.getHtmlTag(), null,
				"Tag '<test>' is not closed (expected '</test>' but got '</B>').");

		validationTestHelper.assertError(htmlLabel,
				HtmllabelPackage.eINSTANCE.getHtmlTag(), null,
				"Tag '<test>' is not supported.");

		// verify that these are the only reported issues
		assertNumberOfIssues(htmlLabel, 2);
	}

	@Test
	public void test_unknown_parent() throws Exception {
		String text = "<foo><tr></tr></foo>";

		HtmlLabel htmlLabel = parseHelper.parse(text);

		validationTestHelper.assertError(htmlLabel,
				HtmllabelPackage.eINSTANCE.getHtmlTag(), null,
				"Tag '<foo>' is not supported.");

		validationTestHelper.assertError(htmlLabel,
				HtmllabelPackage.eINSTANCE.getHtmlTag(), null,
				"Tag '<tr>' is not allowed inside '<foo>', but only inside '<TABLE>'.");

		// verify that these are the only reported issues
		assertNumberOfIssues(htmlLabel, 2);
	}

	@Test
	public void test_invalid_parent1() throws Exception {
		verifyHtmlTag("<tr></tr>",
				"Tag '<tr>' is not allowed inside '<ROOT>', but only inside '<TABLE>'.");
	}

	@Test
	public void test_invalid_parent2() throws Exception {
		verifyHtmlTag("<table><U></U></table>",
				"Tag '<U>' is not allowed inside '<table>', but only inside '<TD>', '<SUB>', '<B>', '<S>', '<ROOT>', '<U>', '<I>', '<FONT>', '<O>', '<SUP>'.");
	}

	@Test
	public void test_invalid_attribute_in_valid_tag() throws Exception {
		verifyHtmlAttribute("<table foo=\"bar\"></table>",
				"Attribute 'foo' is not allowed inside '<table>'.");
	}

	@Test
	public void test_invalid_attribute_in_invalid_tag() throws Exception {
		String text = "<foo bar=\"baz\"></foo>";

		HtmlLabel htmlLabel = parseHelper.parse(text);

		validationTestHelper.assertError(htmlLabel,
				HtmllabelPackage.eINSTANCE.getHtmlTag(), null,
				"Tag '<foo>' is not supported.");

		validationTestHelper.assertError(htmlLabel,
				HtmllabelPackage.eINSTANCE.getHtmlAttr(), null,
				"Attribute 'bar' is not allowed inside '<foo>'.");

		// verify that these are the only reported issues
		assertNumberOfIssues(htmlLabel, 2);
	}

	@Test
	public void test_string_literal_is_not_allowed() {
		// <BR>string</BR> is not allowed
		testStringLiteral("<BR>string</BR>", "BR");

		// <TABLE>string</TABLE> is not allowed
		testStringLiteral("<TABLE>string</TABLE>", "TABLE");

		// <HR>string</HR> is not allowed
		testStringLiteral("<TABLE><HR>string</HR></TABLE>", "HR");

		// <TR>string</TR> is not allowed
		testStringLiteral("<TABLE><TR>string</TR></TABLE>", "TR");

		// <VR>string</VR> is not allowed
		testStringLiteral("<TABLE><TR><VR>string</VR></TR></TABLE>", "VR");

		// <IMG>string</IMG> is not allowed
		testStringLiteral("<TABLE><TR><TD><IMG>string</IMG></TD></TR></TABLE>",
				"IMG");
	}

	@Test
	public void test_invalid_siblings() throws Exception {
		// The graphviz DOT HTML-Like Label grammar does not allow text and
		// table or multiple tables on the same (root or nested) level.

		EClass htmlTag = HtmllabelPackage.eINSTANCE.getHtmlTag();
		EClass htmlContent = HtmllabelPackage.eINSTANCE.getHtmlContent();

		// testDataList[][0]: html label containing invalid siblings
		// testDataList[][1]: object type 1 to be marked as error prone
		// testDataList[][2]: object type2 be marked as error prone
		// ...
		Object[][] testDataList = {
				// root level
				{ "<TABLE></TABLE><B></B>", htmlTag, htmlTag },
				{ "<TABLE></TABLE><BR></BR>", htmlTag, htmlTag },
				{ "<TABLE></TABLE><FONT></FONT>", htmlTag, htmlTag },
				{ "<TABLE></TABLE><I></I>", htmlTag, htmlTag },
				{ "<TABLE></TABLE><O></O>", htmlTag, htmlTag },
				{ "<TABLE></TABLE><S></S>", htmlTag, htmlTag },
				{ "<TABLE></TABLE><SUB></SUB>", htmlTag, htmlTag },
				{ "<TABLE></TABLE><SUP></SUP>", htmlTag, htmlTag },
				{ "<TABLE></TABLE><TABLE></TABLE>", htmlTag, htmlTag },
				{ "<TABLE></TABLE><U></U>", htmlTag, htmlTag },
				{ "<TABLE></TABLE>text", htmlTag, htmlContent },
				{ "<B></B><TABLE></TABLE>", htmlTag, htmlTag },
				{ "<BR></BR><TABLE></TABLE>", htmlTag, htmlTag },
				{ "<FONT></FONT><TABLE></TABLE>", htmlTag, htmlTag },
				{ "<I></I><TABLE></TABLE>", htmlTag, htmlTag },
				{ "<O></O><TABLE></TABLE>", htmlTag, htmlTag },
				{ "<S></S><TABLE></TABLE>", htmlTag, htmlTag },
				{ "<SUB></SUB><TABLE></TABLE>", htmlTag, htmlTag },
				{ "<SUP></SUP><TABLE></TABLE>", htmlTag, htmlTag },
				{ "<U></U><TABLE></TABLE>", htmlTag, htmlTag },
				{ "text<TABLE></TABLE>", htmlTag, htmlContent },
				{ "<TABLE></TABLE>text<TABLE></TABLE>", htmlTag, htmlContent,
						htmlTag },

				// nested level
				{ "<TABLE><TR><TD><TABLE></TABLE><B></B></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><TABLE></TABLE><BR></BR></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><TABLE></TABLE><FONT></FONT></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><TABLE></TABLE><I></I></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><TABLE></TABLE><O></O></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><TABLE></TABLE><S></S></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><TABLE></TABLE><SUB></SUB></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><TABLE></TABLE><SUP></SUP></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><TABLE></TABLE><TABLE></TABLE></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><TABLE></TABLE><U></U></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><TABLE></TABLE>text</TD></TR></TABLE>",
						htmlTag, htmlContent },
				{ "<TABLE><TR><TD><B></B><TABLE></TABLE></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><BR></BR><TABLE></TABLE></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><FONT></FONT><TABLE></TABLE></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><I></I><TABLE></TABLE></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><O></O><TABLE></TABLE></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><S></S><TABLE></TABLE></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><SUB></SUB><TABLE></TABLE></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><SUP></SUP><TABLE></TABLE></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><U></U><TABLE></TABLE></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD>text<TABLE></TABLE></TD></TR></TABLE>",
						htmlTag, htmlContent },
				{ "<TABLE><TR><TD><IMG/><B></B></TD></TR></TABLE>", htmlTag,
						htmlTag },
				{ "<TABLE><TR><TD><IMG/><BR></BR></TD></TR></TABLE>", htmlTag,
						htmlTag },
				{ "<TABLE><TR><TD><IMG/><FONT></FONT></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><IMG/><I></I></TD></TR></TABLE>", htmlTag,
						htmlTag },
				{ "<TABLE><TR><TD><IMG/><O></O></TD></TR></TABLE>", htmlTag,
						htmlTag },
				{ "<TABLE><TR><TD><IMG/><S></S></TD></TR></TABLE>", htmlTag,
						htmlTag },
				{ "<TABLE><TR><TD><IMG/><SUB></SUB></TD></TR></TABLE>", htmlTag,
						htmlTag },
				{ "<TABLE><TR><TD><IMG/><SUP></SUP></TD></TR></TABLE>", htmlTag,
						htmlTag },
				{ "<TABLE><TR><TD><IMG/><TABLE></TABLE></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><IMG/><U></U></TD></TR></TABLE>", htmlTag,
						htmlTag },
				{ "<TABLE><TR><TD><IMG/>text</TD></TR></TABLE>", htmlTag,
						htmlContent },
				{ "<TABLE><TR><TD><IMG/><IMG/></TD></TR></TABLE>", htmlTag,
						htmlTag },
				{ "<TABLE><TR><TD><B></B><IMG/></TD></TR></TABLE>", htmlTag,
						htmlTag },
				{ "<TABLE><TR><TD><BR></BR><IMG/></TD></TR></TABLE>", htmlTag,
						htmlTag },
				{ "<TABLE><TR><TD><FONT></FONT><IMG/></TD></TR></TABLE>",
						htmlTag, htmlTag },
				{ "<TABLE><TR><TD><I></I><IMG/></TD></TR></TABLE>", htmlTag,
						htmlTag },
				{ "<TABLE><TR><TD><O></O><IMG/></TD></TR></TABLE>", htmlTag,
						htmlTag },
				{ "<TABLE><TR><TD><S></S><IMG/></TD></TR></TABLE>", htmlTag,
						htmlTag },
				{ "<TABLE><TR><TD><SUB></SUB><IMG/></TD></TR></TABLE>", htmlTag,
						htmlTag },
				{ "<TABLE><TR><TD><SUP></SUP><IMG/></TD></TR></TABLE>", htmlTag,
						htmlTag },
				{ "<TABLE><TR><TD><U></U><IMG/></TD></TR></TABLE>", htmlTag,
						htmlTag },
				{ "<TABLE><TR><TD>text<IMG/></TD></TR></TABLE>", htmlTag,
						htmlContent },
				{ "<TABLE><TR><TD><TABLE></TABLE>text<TABLE></TABLE></TD></TR></TABLE>",
						htmlTag, htmlContent, htmlTag } };

		for (Object[] testData : testDataList) {

			String htmlLabelText = (String) testData[0];
			HtmlLabel htmlLabel = parseHelper.parse(htmlLabelText);
			int numberOfErrorProneText = testData.length - 1;

			for (int i = 0; i < numberOfErrorProneText; i++) {
				EClass objectType = (EClass) testData[i + 1];
				validationTestHelper.assertError(htmlLabel, objectType, null,
						"Invalid siblings.");
			}

			// verify that these are the only reported issues
			assertNumberOfIssues(htmlLabel, numberOfErrorProneText);
		}
	}

	@Test
	public void test_self_closing_is_not_allowed() {
		testSelfClosingTag("<FONT/>"); // <FONT/> is not allowed

		testSelfClosingTag("<I/>"); // <I/> is not allowed

		testSelfClosingTag("<B/>"); // <B/> is not allowed

		testSelfClosingTag("<U/>"); // <U/> is not allowed

		testSelfClosingTag("<O/>"); // <O/> is not allowed

		testSelfClosingTag("<SUB/>"); // <SUB/> is not allowed

		testSelfClosingTag("<SUP/>"); // <SUP/> is not allowed

		testSelfClosingTag("<S/>"); // <S/> is not allowed

		testSelfClosingTag("<TABLE/>"); // <TABLE/> is not allowed

		// <TR/> is not allowed
		testSelfClosingTag("<TABLE><TR/></TABLE>", "<TR/>");

		// <TD/> is not allowed
		testSelfClosingTag("<TABLE><TR><TD/></TR></TABLE>", "<TD/>");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_BR_ALIGN() {
		verifyHtmlAttribute("<BR ALIGN=\"\"/>",
				"The value '' is not a correct ALIGN: Value has to be one of 'CENTER', 'LEFT', 'RIGHT'.");
		verifyHtmlAttribute("<BR ALIGN=\"foo\"/>",
				"The value 'foo' is not a correct ALIGN: Value has to be one of 'CENTER', 'LEFT', 'RIGHT'.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_FONT_COLOR() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_FONT_FACE() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_FONT_POINTSIZE() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_IMG_SCALE() {
		verifyHtmlAttribute(
				"<TABLE><TR><TD><IMG SCALE=\"\"/></TD></TR></TABLE>",
				"The value '' is not a correct SCALE: Value has to be one of 'FALSE', 'TRUE', 'WIDTH', 'HEIGHT', 'BOTH'.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD><IMG SCALE=\"foo\"/></TD></TR></TABLE>",
				"The value 'foo' is not a correct SCALE: Value has to be one of 'FALSE', 'TRUE', 'WIDTH', 'HEIGHT', 'BOTH'.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_IMG_SRC() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_ALIGN() {
		verifyHtmlAttribute("<TABLE ALIGN=\"\"></TABLE>",
				"The value '' is not a correct ALIGN: Value has to be one of 'CENTER', 'LEFT', 'RIGHT'.");
		verifyHtmlAttribute("<TABLE ALIGN=\"foo\"></TABLE>",
				"The value 'foo' is not a correct ALIGN: Value has to be one of 'CENTER', 'LEFT', 'RIGHT'.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_BGCOLOR() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_BORDER() {
		verifyHtmlAttribute("<TABLE BORDER=\"\"></TABLE>",
				"The value '' is not a correct BORDER: Value has to be between 0 and 255.");
		verifyHtmlAttribute("<TABLE BORDER=\"foo\"></TABLE>",
				"The value 'foo' is not a correct BORDER: Value has to be between 0 and 255.");
		verifyHtmlAttribute("<TABLE BORDER=\"-2\"></TABLE>",
				"The value '-2' is not a correct BORDER: Value has to be between 0 and 255.");
		verifyHtmlAttribute("<TABLE BORDER=\"-1\"></TABLE>",
				"The value '-1' is not a correct BORDER: Value has to be between 0 and 255.");
		verifyHtmlAttribute("<TABLE BORDER=\"256\"></TABLE>",
				"The value '256' is not a correct BORDER: Value has to be between 0 and 255.");
		verifyHtmlAttribute("<TABLE BORDER=\"257\"></TABLE>",
				"The value '257' is not a correct BORDER: Value has to be between 0 and 255.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_CELLBORDER() {
		verifyHtmlAttribute("<TABLE CELLBORDER=\"\"></TABLE>",
				"The value '' is not a correct CELLBORDER: Value has to be between 0 and 127.");
		verifyHtmlAttribute("<TABLE CELLBORDER=\"foo\"></TABLE>",
				"The value 'foo' is not a correct CELLBORDER: Value has to be between 0 and 127.");
		verifyHtmlAttribute("<TABLE CELLBORDER=\"-2\"></TABLE>",
				"The value '-2' is not a correct CELLBORDER: Value has to be between 0 and 127.");
		verifyHtmlAttribute("<TABLE CELLBORDER=\"-1\"></TABLE>",
				"The value '-1' is not a correct CELLBORDER: Value has to be between 0 and 127.");
		verifyHtmlAttribute("<TABLE CELLBORDER=\"128\"></TABLE>",
				"The value '128' is not a correct CELLBORDER: Value has to be between 0 and 127.");
		verifyHtmlAttribute("<TABLE CELLBORDER=\"129\"></TABLE>",
				"The value '129' is not a correct CELLBORDER: Value has to be between 0 and 127.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_CELLPADDING() {
		verifyHtmlAttribute("<TABLE CELLPADDING=\"\"></TABLE>",
				"The value '' is not a correct CELLPADDING: Value has to be between 0 and 255.");
		verifyHtmlAttribute("<TABLE CELLPADDING=\"foo\"></TABLE>",
				"The value 'foo' is not a correct CELLPADDING: Value has to be between 0 and 255.");
		verifyHtmlAttribute("<TABLE CELLPADDING=\"-2\"></TABLE>",
				"The value '-2' is not a correct CELLPADDING: Value has to be between 0 and 255.");
		verifyHtmlAttribute("<TABLE CELLPADDING=\"-1\"></TABLE>",
				"The value '-1' is not a correct CELLPADDING: Value has to be between 0 and 255.");
		verifyHtmlAttribute("<TABLE CELLPADDING=\"256\"></TABLE>",
				"The value '256' is not a correct CELLPADDING: Value has to be between 0 and 255.");
		verifyHtmlAttribute("<TABLE CELLPADDING=\"257\"></TABLE>",
				"The value '257' is not a correct CELLPADDING: Value has to be between 0 and 255.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_CELLSPACING() {
		verifyHtmlAttribute("<TABLE CELLSPACING=\"\"></TABLE>",
				"The value '' is not a correct CELLSPACING: Value has to be between 0 and 127.");
		verifyHtmlAttribute("<TABLE CELLSPACING=\"foo\"></TABLE>",
				"The value 'foo' is not a correct CELLSPACING: Value has to be between 0 and 127.");
		verifyHtmlAttribute("<TABLE CELLSPACING=\"-2\"></TABLE>",
				"The value '-2' is not a correct CELLSPACING: Value has to be between 0 and 127.");
		verifyHtmlAttribute("<TABLE CELLSPACING=\"-1\"></TABLE>",
				"The value '-1' is not a correct CELLSPACING: Value has to be between 0 and 127.");
		verifyHtmlAttribute("<TABLE CELLSPACING=\"128\"></TABLE>",
				"The value '128' is not a correct CELLSPACING: Value has to be between 0 and 127.");
		verifyHtmlAttribute("<TABLE CELLSPACING=\"129\"></TABLE>",
				"The value '129' is not a correct CELLSPACING: Value has to be between 0 and 127.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_COLOR() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_COLUMNS() {
		verifyHtmlAttribute("<TABLE ROWS=\"\"></TABLE>",
				"The value '' is not a correct ROWS: Value has to be '*'.");
		verifyHtmlAttribute("<TABLE ROWS=\"foo\"></TABLE>",
				"The value 'foo' is not a correct ROWS: Value has to be '*'.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_FIXEDSIZE() {
		verifyHtmlAttribute("<TABLE FIXEDSIZE=\"\"></TABLE>",
				"The value '' is not a correct FIXEDSIZE: Value has to be one of 'FALSE', 'TRUE'.");
		verifyHtmlAttribute("<TABLE FIXEDSIZE=\"foo\"></TABLE>",
				"The value 'foo' is not a correct FIXEDSIZE: Value has to be one of 'FALSE', 'TRUE'.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_GRADIENTANGLE() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_HEIGHT() {
		verifyHtmlAttribute("<TABLE HEIGHT=\"\"></TABLE>",
				"The value '' is not a correct HEIGHT: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE HEIGHT=\"foo\"></TABLE>",
				"The value 'foo' is not a correct HEIGHT: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE HEIGHT=\"-2\"></TABLE>",
				"The value '-2' is not a correct HEIGHT: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE HEIGHT=\"-1\"></TABLE>",
				"The value '-1' is not a correct HEIGHT: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE HEIGHT=\"65536\"></TABLE>",
				"The value '65536' is not a correct HEIGHT: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE HEIGHT=\"65537\"></TABLE>",
				"The value '65537' is not a correct HEIGHT: Value has to be between 0 and 65535.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_HREF() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_ID() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_PORT() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_ROWS() {
		verifyHtmlAttribute("<TABLE ROWS=\"\"></TABLE>",
				"The value '' is not a correct ROWS: Value has to be '*'.");
		verifyHtmlAttribute("<TABLE ROWS=\"foo\"></TABLE>",
				"The value 'foo' is not a correct ROWS: Value has to be '*'.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_SIDES() {
		verifyHtmlAttribute("<TABLE SIDES=\"\"></TABLE>",
				"The value '' is not a correct SIDES: Value has to contain only the 'L', 'T', 'R', 'B' characters.");
		verifyHtmlAttribute("<TABLE SIDES=\"foo\"></TABLE>",
				"The value 'foo' is not a correct SIDES: Value has to contain only the 'L', 'T', 'R', 'B' characters.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_STYLE() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_TARGET() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_TITLE() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_TOOLTIP() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_VALIGN() {
		verifyHtmlAttribute("<TABLE VALIGN=\"\"></TABLE>",
				"The value '' is not a correct VALIGN: Value has to be one of 'MIDDLE', 'BOTTOM', 'TOP'.");
		verifyHtmlAttribute("<TABLE VALIGN=\"foo\"></TABLE>",
				"The value 'foo' is not a correct VALIGN: Value has to be one of 'MIDDLE', 'BOTTOM', 'TOP'.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TABLE_WIDTH() {
		verifyHtmlAttribute("<TABLE WIDTH=\"\"></TABLE>",
				"The value '' is not a correct WIDTH: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE WIDTH=\"foo\"></TABLE>",
				"The value 'foo' is not a correct WIDTH: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE WIDTH=\"-2\"></TABLE>",
				"The value '-2' is not a correct WIDTH: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE WIDTH=\"-1\"></TABLE>",
				"The value '-1' is not a correct WIDTH: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE WIDTH=\"65536\"></TABLE>",
				"The value '65536' is not a correct WIDTH: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE WIDTH=\"65537\"></TABLE>",
				"The value '65537' is not a correct WIDTH: Value has to be between 0 and 65535.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_ALIGN() {
		verifyHtmlAttribute("<TABLE><TR><TD ALIGN=\"\"></TD></TR></TABLE>",
				"The value '' is not a correct ALIGN: Value has to be one of 'CENTER', 'LEFT', 'RIGHT', 'TEXT'.");
		verifyHtmlAttribute("<TABLE><TR><TD ALIGN=\"foo\"></TD></TR></TABLE>",
				"The value 'foo' is not a correct ALIGN: Value has to be one of 'CENTER', 'LEFT', 'RIGHT', 'TEXT'.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_BALIGN() {
		verifyHtmlAttribute("<TABLE><TR><TD BALIGN=\"\"></TD></TR></TABLE>",
				"The value '' is not a correct BALIGN: Value has to be one of 'CENTER', 'LEFT', 'RIGHT'.");
		verifyHtmlAttribute("<TABLE><TR><TD BALIGN=\"foo\"></TD></TR></TABLE>",
				"The value 'foo' is not a correct BALIGN: Value has to be one of 'CENTER', 'LEFT', 'RIGHT'.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_BGCOLOR() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_BORDER() {
		verifyHtmlAttribute("<TABLE><TR><TD BORDER=\"\"></TD></TR></TABLE>",
				"The value '' is not a correct BORDER: Value has to be between 0 and 255.");
		verifyHtmlAttribute("<TABLE><TR><TD BORDER=\"foo\"></TD></TR></TABLE>",
				"The value 'foo' is not a correct BORDER: Value has to be between 0 and 255.");
		verifyHtmlAttribute("<TABLE><TR><TD BORDER=\"-2\"></TD></TR></TABLE>",
				"The value '-2' is not a correct BORDER: Value has to be between 0 and 255.");
		verifyHtmlAttribute("<TABLE><TR><TD BORDER=\"-1\"></TD></TR></TABLE>",
				"The value '-1' is not a correct BORDER: Value has to be between 0 and 255.");
		verifyHtmlAttribute("<TABLE><TR><TD BORDER=\"256\"></TD></TR></TABLE>",
				"The value '256' is not a correct BORDER: Value has to be between 0 and 255.");
		verifyHtmlAttribute("<TABLE><TR><TD BORDER=\"257\"></TD></TR></TABLE>",
				"The value '257' is not a correct BORDER: Value has to be between 0 and 255.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_CELLPADDING() {
		verifyHtmlAttribute(
				"<TABLE><TR><TD CELLPADDING=\"\"></TD></TR></TABLE>",
				"The value '' is not a correct CELLPADDING: Value has to be between 0 and 255.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD CELLPADDING=\"foo\"></TD></TR></TABLE>",
				"The value 'foo' is not a correct CELLPADDING: Value has to be between 0 and 255.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD CELLPADDING=\"-2\"></TD></TR></TABLE>",
				"The value '-2' is not a correct CELLPADDING: Value has to be between 0 and 255.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD CELLPADDING=\"-1\"></TD></TR></TABLE>",
				"The value '-1' is not a correct CELLPADDING: Value has to be between 0 and 255.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD CELLPADDING=\"256\"></TD></TR></TABLE>",
				"The value '256' is not a correct CELLPADDING: Value has to be between 0 and 255.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD CELLPADDING=\"257\"></TD></TR></TABLE>",
				"The value '257' is not a correct CELLPADDING: Value has to be between 0 and 255.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_CELLSPACING() {
		verifyHtmlAttribute(
				"<TABLE><TR><TD CELLSPACING=\"\"></TD></TR></TABLE>",
				"The value '' is not a correct CELLSPACING: Value has to be between 0 and 127.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD CELLSPACING=\"foo\"></TD></TR></TABLE>",
				"The value 'foo' is not a correct CELLSPACING: Value has to be between 0 and 127.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD CELLSPACING=\"-2\"></TD></TR></TABLE>",
				"The value '-2' is not a correct CELLSPACING: Value has to be between 0 and 127.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD CELLSPACING=\"-1\"></TD></TR></TABLE>",
				"The value '-1' is not a correct CELLSPACING: Value has to be between 0 and 127.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD CELLSPACING=\"128\"></TD></TR></TABLE>",
				"The value '128' is not a correct CELLSPACING: Value has to be between 0 and 127.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD CELLSPACING=\"129\"></TD></TR></TABLE>",
				"The value '129' is not a correct CELLSPACING: Value has to be between 0 and 127.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_COLOR() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_COLSPAN() {
		verifyHtmlAttribute("<TABLE><TR><TD COLSPAN=\"\"></TD></TR></TABLE>",
				"The value '' is not a correct COLSPAN: Value has to be between 1 and 65535.");
		verifyHtmlAttribute("<TABLE><TR><TD COLSPAN=\"foo\"></TD></TR></TABLE>",
				"The value 'foo' is not a correct COLSPAN: Value has to be between 1 and 65535.");
		verifyHtmlAttribute("<TABLE><TR><TD COLSPAN=\"-1\"></TD></TR></TABLE>",
				"The value '-1' is not a correct COLSPAN: Value has to be between 1 and 65535.");
		verifyHtmlAttribute("<TABLE><TR><TD COLSPAN=\"0\"></TD></TR></TABLE>",
				"The value '0' is not a correct COLSPAN: Value has to be between 1 and 65535.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD COLSPAN=\"65536\"></TD></TR></TABLE>",
				"The value '65536' is not a correct COLSPAN: Value has to be between 1 and 65535.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD COLSPAN=\"65537\"></TD></TR></TABLE>",
				"The value '65537' is not a correct COLSPAN: Value has to be between 1 and 65535.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_FIXEDSIZE() {
		verifyHtmlAttribute("<TABLE><TR><TD FIXEDSIZE=\"\"></TD></TR></TABLE>",
				"The value '' is not a correct FIXEDSIZE: Value has to be one of 'FALSE', 'TRUE'.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD FIXEDSIZE=\"foo\"></TD></TR></TABLE>",
				"The value 'foo' is not a correct FIXEDSIZE: Value has to be one of 'FALSE', 'TRUE'");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_GRADIENTANGLE() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_HEIGHT() {
		verifyHtmlAttribute("<TABLE><TR><TD HEIGHT=\"\"></TD></TR></TABLE>",
				"The value '' is not a correct HEIGHT: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE><TR><TD HEIGHT=\"foo\"></TD></TR></TABLE>",
				"The value 'foo' is not a correct HEIGHT: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE><TR><TD HEIGHT=\"-2\"></TD></TR></TABLE>",
				"The value '-2' is not a correct HEIGHT: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE><TR><TD HEIGHT=\"-1\"></TD></TR></TABLE>",
				"The value '-1' is not a correct HEIGHT: Value has to be between 0 and 65535.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD HEIGHT=\"65536\"></TD></TR></TABLE>",
				"The value '65536' is not a correct HEIGHT: Value has to be between 0 and 65535.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD HEIGHT=\"65537\"></TD></TR></TABLE>",
				"The value '65537' is not a correct HEIGHT: Value has to be between 0 and 65535.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_HREF() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_ID() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_PORT() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_ROWSPAN() {
		verifyHtmlAttribute("<TABLE><TR><TD ROWSPAN=\"\"></TD></TR></TABLE>",
				"The value '' is not a correct ROWSPAN: Value has to be between 1 and 65535.");
		verifyHtmlAttribute("<TABLE><TR><TD ROWSPAN=\"foo\"></TD></TR></TABLE>",
				"The value 'foo' is not a correct ROWSPAN: Value has to be between 1 and 65535.");
		verifyHtmlAttribute("<TABLE><TR><TD ROWSPAN=\"-1\"></TD></TR></TABLE>",
				"The value '-1' is not a correct ROWSPAN: Value has to be between 1 and 65535.");
		verifyHtmlAttribute("<TABLE><TR><TD ROWSPAN=\"0\"></TD></TR></TABLE>",
				"The value '0' is not a correct ROWSPAN: Value has to be between 1 and 65535.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD ROWSPAN=\"65536\"></TD></TR></TABLE>",
				"The value '65536' is not a correct ROWSPAN: Value has to be between 1 and 65535.");
		verifyHtmlAttribute(
				"<TABLE><TR><TD ROWSPAN=\"65537\"></TD></TR></TABLE>",
				"The value '65537' is not a correct ROWSPAN: Value has to be between 1 and 65535.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_SIDES() {
		verifyHtmlAttribute("<TABLE><TR><TD SIDES=\"\"></TD></TR></TABLE>",
				"The value '' is not a correct SIDES: Value has to contain only the 'L', 'T', 'R', 'B' characters.");
		verifyHtmlAttribute("<TABLE><TR><TD SIDES=\"foo\"></TD></TR></TABLE>",
				"The value 'foo' is not a correct SIDES: Value has to contain only the 'L', 'T', 'R', 'B' characters.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_STYLE() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_TARGET() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_TITLE() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_TOOLTIP() {
		// TODO implement
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_VALIGN() {
		verifyHtmlAttribute("<TABLE><TR><TD VALIGN=\"\"></TD></TR></TABLE>",
				"The value '' is not a correct VALIGN: Value has to be one of 'MIDDLE', 'BOTTOM', 'TOP'.");
		verifyHtmlAttribute("<TABLE><TR><TD VALIGN=\"foo\"></TD></TR></TABLE>",
				"The value 'foo' is not a correct VALIGN: Value has to be one of 'MIDDLE', 'BOTTOM', 'TOP'.");
	}

	@Test
	public void test_invalid_attribute_value_of_tag_TD_WIDTH() {
		verifyHtmlAttribute("<TABLE><TR><TD WIDTH=\"\"></TD></TR></TABLE>",
				"The value '' is not a correct WIDTH: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE><TR><TD WIDTH=\"foo\"></TD></TR></TABLE>",
				"The value 'foo' is not a correct WIDTH: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE><TR><TD WIDTH=\"-2\"></TD></TR></TABLE>",
				"The value '-2' is not a correct WIDTH: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE><TR><TD WIDTH=\"-1\"></TD></TR></TABLE>",
				"The value '-1' is not a correct WIDTH: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE><TR><TD WIDTH=\"65536\"></TD></TR></TABLE>",
				"The value '65536' is not a correct WIDTH: Value has to be between 0 and 65535.");
		verifyHtmlAttribute("<TABLE><TR><TD WIDTH=\"65537\"></TD></TR></TABLE>",
				"The value '65537' is not a correct WIDTH: Value has to be between 0 and 65535.");
	}

	private void testStringLiteral(String... text) {
		String tagName = text.length > 1 ? text[1] : text[0];
		verifyHtmlTag(text[0],
				"Tag '<" + tagName + ">' cannot contain a string literal.");
	}

	private void testSelfClosingTag(String... text) {
		String tagName = text.length > 1 ? text[1] : text[0];
		verifyHtmlTag(text[0], "Tag '" + tagName + "' cannot be self closing.");
	}

	private void verifyHtmlTag(String text, String message) {
		verify(text, message, HtmllabelPackage.eINSTANCE.getHtmlTag());
	}

	private void verifyHtmlAttribute(String text, String message) {
		verify(text, message, HtmllabelPackage.eINSTANCE.getHtmlAttr());
	}

	/*
	 * Verify that parsing the given text raises a validation error with the
	 * given message.
	 */
	private void verify(String text, String message, EClass objectType) {
		HtmlLabel htmlLabel = null;
		try {
			htmlLabel = parseHelper.parse(text);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		validationTestHelper.assertError(htmlLabel, objectType, null, message);

		// verify that this is the only reported issue
		assertNumberOfIssues(htmlLabel, 1);
	}

	private void assertNumberOfIssues(HtmlLabel htmlLabel,
			int expectedNumberOfIssues) {
		assertEquals(expectedNumberOfIssues,
				validationTestHelper.validate(htmlLabel).size());
	}

	private HtmlLabel parse(String text) {
		try {
			HtmlLabel ast = parseHelper.parse(text);
			assertNotNull(ast);
			validationTestHelper.assertNoErrors(ast);
			return ast;
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		return null;
	}
}
