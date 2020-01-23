/*******************************************************************************
 * Copyright (c) 2016, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial implementation (bug #477980)
 *                                - Add support for polygon-based node shapes (bug #441352)
 *     Zoey G. Prigge (itemis AG) - Add support for record-based node shapes (bug #454629)
 *                                - implement additional dot attributes (bug #461506)
 *                                - implement redundant attribute validation (bug #540330)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import java.util.List
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import static org.eclipse.gef.dot.internal.DotAttributes.*
import static org.eclipse.gef.dot.internal.language.dot.DotPackage.Literals.*
import static org.eclipse.gef.dot.internal.language.validation.DotRecordLabelValidator.*
import static org.eclipse.gef.dot.internal.language.validation.DotValidator.*
import static org.eclipse.xtext.diagnostics.Diagnostic.*

import static extension org.eclipse.gef.dot.tests.DotTestUtils.content
import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotInjectorProvider)
class DotValidatorTest {

	@Rule public val rule = new DotSubgrammarPackagesRegistrationRule

	@Inject extension ParseHelper<DotAst>
	@Inject extension ValidationTestHelper

	val l = System.getProperty("line.separator").length

	@Test def arrowshapes_single() {
		"arrowshapes_single.dot".readFile.assertNoIssues
	}

	@Test def arrowshapes_multiple() {
		"arrowshapes_multiple.dot".readFile.assertNoIssues
	}

	@Test def arrowshapes_deprecated() {
		val dotAst = "arrowshapes_deprecated.dot".readFile.assertNumberOfIssues(8)
		dotAst.assertArrowTypeWarning("The arrowType value 'ediamond' is not semantically correct: The shape 'ediamond' is deprecated.")
		dotAst.assertArrowTypeWarning("The arrowType value 'open' is not semantically correct: The shape 'open' is deprecated.")
		dotAst.assertArrowTypeWarning("The arrowType value 'halfopen' is not semantically correct: The shape 'halfopen' is deprecated.")
		dotAst.assertArrowTypeWarning("The arrowType value 'empty' is not semantically correct: The shape 'empty' is deprecated.")
		dotAst.assertArrowTypeWarning("The arrowType value 'invempty' is not semantically correct: The shape 'invempty' is deprecated.")
		dotAst.assertArrowTypeWarning("The arrowType value 'ediamondinvempty' is not semantically correct: The shape 'ediamond' is deprecated.")
		dotAst.assertArrowTypeWarning("The arrowType value 'ediamondinvempty' is not semantically correct: The shape 'invempty' is deprecated.")
		dotAst.assertArrowTypeWarning(1281 + 28 * l, 4, "The arrowType value 'openbox' is not semantically correct: The shape 'open' is deprecated.")
	}

	@Test def deprecated_style() {
		val dotAst = DotTestGraphs.DEPRECATED_STYLES.parse.assertNumberOfIssues(5)
		dotAst.assertWarning(ATTRIBUTE, STYLE__GCNE, 113 + 5 * l, 12, "The style value 'setlinewidth(1)' is not semantically correct: The usage of setlinewidth is deprecated, use the penwidth attribute instead.")
		dotAst.assertWarning(ATTRIBUTE, STYLE__GCNE, 140 + 6 * l, 12, "The style value 'setlinewidth(2)' is not semantically correct: The usage of setlinewidth is deprecated, use the penwidth attribute instead.")
		dotAst.assertWarning(ATTRIBUTE, STYLE__GCNE, 170 + 7 * l, 12, "The style value 'setlinewidth(3)' is not semantically correct: The usage of setlinewidth is deprecated, use the penwidth attribute instead.")
		dotAst.assertWarning(ATTRIBUTE, STYLE__GCNE, 222 + 10 * l, 12,"The style value 'dashed, setlinewidth(4)' is not semantically correct: The usage of setlinewidth is deprecated, use the penwidth attribute instead.")
		dotAst.assertWarning(ATTRIBUTE, STYLE__GCNE, 249 + 11 * l, 12,"The style value 'setlinewidth(5), dotted' is not semantically correct: The usage of setlinewidth is deprecated, use the penwidth attribute instead.")
	}

	@Test def duplicated_style1() {
		'''
			graph {
				1[style="bold, bold"]
			}
		'''.assertStyleWarning("bold", "The style value 'bold, bold' is not semantically correct: The style value 'bold' is duplicated.")
	}

	@Test def duplicated_style2() {
		'''
			graph {
				1--2 [style="dashed, dashed"]
			}
		'''.assertStyleWarning("dashed", "The style value 'dashed, dashed' is not semantically correct: The style value 'dashed' is duplicated.")
	}

	@Test def arrowshapes_direction_both() {
		"arrowshapes_direction_both.dot".readFile.assertNoIssues
	}

	@Test def arrowshapes_invalid_modifiers() {
		val dotAst = "arrowshapes_invalid_modifiers.dot".readFile.assertNumberOfIssues(26)
		dotAst.assertArrowTypeWarning("The arrowType value 'ocrow' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'crow'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'olcrow' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'crow'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'orcrow' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'crow'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'rdot' is not semantically correct: The side modifier 'r' may not be combined with primitive shape 'dot'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'ldot' is not semantically correct: The side modifier 'l' may not be combined with primitive shape 'dot'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'oldot' is not semantically correct: The side modifier 'l' may not be combined with primitive shape 'dot'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'ordot' is not semantically correct: The side modifier 'r' may not be combined with primitive shape 'dot'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'lnone' is not semantically correct: The side modifier 'l' may not be combined with primitive shape 'none'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'rnone' is not semantically correct: The side modifier 'r' may not be combined with primitive shape 'none'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'onone' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'none'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'olnone' is not semantically correct: The side modifier 'l' may not be combined with primitive shape 'none'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'olnone' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'none'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'ornone' is not semantically correct: The side modifier 'r' may not be combined with primitive shape 'none'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'ornone' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'none'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'otee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'tee'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'oltee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'tee'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'ortee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'tee'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'ovee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'vee'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'olvee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'vee'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'orvee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'vee'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'ocurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'curve'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'olcurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'curve'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'orcurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'curve'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'oicurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'icurve'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'olicurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'icurve'.")
		dotAst.assertArrowTypeWarning("The arrowType value 'oricurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'icurve'.")
	}

	@Test def invalid_arrowtype() {
		val dotAst = '''digraph testGraph { 1->2[arrowhead=fooBar arrowtail=fooBar2] }'''.parse.assertNumberOfIssues(2)
		dotAst.assertError(ATTRIBUTE, ARROWHEAD__E, 35, 6, "The value 'fooBar' is not a syntactically correct arrowType: No viable alternative at character 'f'. No viable alternative at input 'o'. No viable alternative at character 'B'. No viable alternative at character 'a'. No viable alternative at input '<EOF>'.")
		dotAst.assertError(ATTRIBUTE, ARROWTAIL__E, 52, 7, "The value 'fooBar2' is not a syntactically correct arrowType: No viable alternative at character 'f'. No viable alternative at input 'o'. No viable alternative at character 'B'. No viable alternative at character 'a'. No viable alternative at character '2'.")
	}

	@Test def invalid_edge_direction() {
		'''digraph testGraph { 1->2[dir=foo] }'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, DIR__E, 29, 3, "The value 'foo' is not a syntactically correct dirType: Value has to be one of 'forward', 'back', 'both', 'none'.")
	}

	@Test def invalid_edge_arrowsize() {
		val dotAst = '''digraph testGraph { 1->2[arrowsize=foo] 3->4[arrowsize="-2.0"]}'''.parse.assertNumberOfIssues(2)
		dotAst.assertError(ATTRIBUTE, ARROWSIZE__E, "The value 'foo' is not a syntactically correct double: For input string: \"foo\".")
		dotAst.assertError(ATTRIBUTE, ARROWSIZE__E, "The double value '-2.0' is not semantically correct: Value may not be smaller than 0.0.")
	}

	@Test def invalid_edge_fontsize() {
		val dotAst = '''graph { 1--2[fontsize=large] 3--4[fontsize=0.3]}'''.parse.assertNumberOfIssues(2)
		dotAst.assertError(ATTRIBUTE, FONTSIZE__GCNE, "The value 'large' is not a syntactically correct double: For input string: \"large\".")
		dotAst.assertError(ATTRIBUTE, FONTSIZE__GCNE, "The double value '0.3' is not semantically correct: Value may not be smaller than 1.0.")
	}

	@Test def invalid_edge_labelfontsize() {
		val dotAst = '''graph { 1--2[labelfontsize=large] 3--4[labelfontsize=0.3]}'''.parse.assertNumberOfIssues(2)
		dotAst.assertError(ATTRIBUTE, LABELFONTSIZE__E, "The value 'large' is not a syntactically correct double: For input string: \"large\".")
		dotAst.assertError(ATTRIBUTE, LABELFONTSIZE__E, "The double value '0.3' is not semantically correct: Value may not be smaller than 1.0.")
	}

	@Test def invalid_edge_penwidth() {
		val dotAst = '''graph { 1--2[penwidth=thin] 3--4[penwidth=-0.9]}'''.parse.assertNumberOfIssues(2)
		dotAst.assertError(ATTRIBUTE, PENWIDTH__CNE, "The value 'thin' is not a syntactically correct double: For input string: \"thin\".")
		dotAst.assertError(ATTRIBUTE, PENWIDTH__CNE, "The double value '-0.9' is not semantically correct: Value may not be smaller than 0.0.")
	}

	@Test def none_is_the_last_arrowshape() {
		'''digraph { 1->2[arrowhead=boxnone] }'''.parse.assertNumberOfIssues(1).
		assertArrowTypeWarning("The arrowType value 'boxnone' is not semantically correct: The shape 'none' may not be the last shape.")
	}

	@Test def invalid_graph_penwidth() {
		val dotAst = '''graph { subgraph clusterName { graph[penwidth=thin] 1 } subgraph clusterOtherName {graph[penwidth=-.5] 2}}'''.parse.assertNumberOfIssues(2)
		dotAst.assertError(ATTRIBUTE, PENWIDTH__CNE, "The value 'thin' is not a syntactically correct double: For input string: \"thin\".")
		dotAst.assertError(ATTRIBUTE, PENWIDTH__CNE, "The double value '-.5' is not semantically correct: Value may not be smaller than 0.0.")
	}

	@Test def invalid_graph_background_color() {
		'''graph { bgcolor=grsy }'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, BGCOLOR__GC, "The colorList value 'grsy' is not semantically correct: The 'grsy' color is not valid within the 'x11' color scheme.")
	}

	@Test def graph_background_color_does_not_correspond_to_local_colorscheme() {
		'''graph { colorscheme=brbg10 bgcolor=blue}'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, BGCOLOR__GC, "The colorList value 'blue' is not semantically correct: The 'blue' color is not valid within the 'brbg10' color scheme.")
	}

	@Test def graph_background_color_does_not_correspond_to_global_colorscheme() {
		'''graph { graph[colorscheme=brbg10] bgcolor=blue}'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, BGCOLOR__GC, "The colorList value 'blue' is not semantically correct: The 'blue' color is not valid within the 'brbg10' color scheme.")
	}

	@Test def invalid_graph_fontsize() {
		'''graph {fontsize=large}'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, FONTSIZE__GCNE, "The value 'large' is not a syntactically correct double: For input string: \"large\".")
		'''graph {fontsize=0.3}'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, FONTSIZE__GCNE, "The double value '0.3' is not semantically correct: Value may not be smaller than 1.0.")
	}

	@Test def invalid_node_color() {
		'''graph { 1[color="#fffff"]}'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, COLOR__CNE, "The value '#fffff' is not a syntactically correct color: Mismatched input '<EOF>' expecting RULE_HEXADECIMAL_DIGIT.")
	}

	@Test def node_color_does_not_correspond_to_local_colorscheme() {
		'''graph { 1[colorscheme=brbg10 color=blue]}'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, COLOR__CNE, "The color value 'blue' is not semantically correct: The 'blue' color is not valid within the 'brbg10' color scheme.")
	}

	@Test def node_color_does_not_correspond_to_global_colorscheme() {
		'''graph { node[colorscheme=brbg10] 1[color=blue]}'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, COLOR__CNE, "The color value 'blue' is not semantically correct: The 'blue' color is not valid within the 'brbg10' color scheme.")
	}

	@Test def invalid_edge_fillcolor() {
		'''digraph { 1->2[fillcolor="#fffff"]}'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, FILLCOLOR__CNE, "The value '#fffff' is not a syntactically correct color: Mismatched input '<EOF>' expecting RULE_HEXADECIMAL_DIGIT.")
	}

	@Test def edge_fillcolor_does_not_correspond_to_local_colorscheme() {
		'''digraph { 1->2[colorscheme=brbg10 fillcolor=white]}'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, FILLCOLOR__CNE, "The color value 'white' is not semantically correct: The 'white' color is not valid within the 'brbg10' color scheme.")
	}

	@Test def edge_fillcolor_does_not_correspond_to_global_colorscheme() {
		'''digraph { edge[colorscheme=brbg10] 1->2[fillcolor=red]}'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, FILLCOLOR__CNE, "The color value 'red' is not semantically correct: The 'red' color is not valid within the 'brbg10' color scheme.")
	}

	@Test def invalid_node_distortion() {
		val dotAst = '''graph { 1[distortion=foo] 2[distortion="-100.0001"]}'''.parse.assertNumberOfIssues(2)
		dotAst.assertError(ATTRIBUTE, DISTORTION__N, "The value 'foo' is not a syntactically correct double: For input string: \"foo\".")
		dotAst.assertError(ATTRIBUTE, DISTORTION__N, "The double value '-100.0001' is not semantically correct: Value may not be smaller than -100.0.")
	}

	@Test def invalid_node_fontsize() {
		val dotAst = '''graph { 1[fontsize=large] 2[fontsize=0.3]}'''.parse.assertNumberOfIssues(2)
		dotAst.assertError(ATTRIBUTE, FONTSIZE__GCNE, "The value 'large' is not a syntactically correct double: For input string: \"large\".")
		dotAst.assertError(ATTRIBUTE, FONTSIZE__GCNE, "The double value '0.3' is not semantically correct: Value may not be smaller than 1.0.")
	}

	@Test def invalid_node_penwidth() {
		val dotAst = '''graph { 1[penwidth=bold] 2[penwidth=-0.3]}'''.parse.assertNumberOfIssues(2)
		dotAst.assertError(ATTRIBUTE, PENWIDTH__CNE, "The value 'bold' is not a syntactically correct double: For input string: \"bold\".")
		dotAst.assertError(ATTRIBUTE, PENWIDTH__CNE, "The double value '-0.3' is not semantically correct: Value may not be smaller than 0.0.")
	}

	@Test def invalid_node_shape() {
		'''graph { 1[shape=foo] }'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, SHAPE__N, "The value 'foo' is not a syntactically correct shape: Extraneous input 'foo' expecting EOF.")
	}

	@Test def invalid_node_sides() {
		val dotAst = "graph { 1[sides=foo] 2[sides=\"-1\"]}".parse.assertNumberOfIssues(2)
		dotAst.assertError(ATTRIBUTE, SIDES__N, "The value 'foo' is not a syntactically correct int: For input string: \"foo\".")
		dotAst.assertError(ATTRIBUTE, SIDES__N, "The int value '-1' is not semantically correct: Value may not be smaller than 0.")
	}

	@Test def invalid_node_skew() {
		val dotAst = "graph { 1[skew=foo] 2[skew=\"-100.1\"]}".parse.assertNumberOfIssues(2)
		dotAst.assertError(ATTRIBUTE, SKEW__N, "The value 'foo' is not a syntactically correct double: For input string: \"foo\".")
		dotAst.assertError(ATTRIBUTE, SKEW__N, "The double value '-100.1' is not semantically correct: Value may not be smaller than -100.0.")
	}

	@Test def directed_graph_with_non_directed_edge() {
		'''digraph {1--2}'''.parse.assertNumberOfIssues(1).
		assertError(EDGE_RHS_NODE, INVALID_EDGE_OPERATOR, "EdgeOp '--' may only be used in undirected graphs.")
	}

	@Test def non_directed_graph_with_directed_edge() {
		'''graph {1->2}'''.parse.assertNumberOfIssues(1).
		assertError(EDGE_RHS_NODE, INVALID_EDGE_OPERATOR, "EdgeOp '->' may only be used in directed graphs.")
	}

	@Test def invalid_combination_of_node_shape_and_style() {
		/*
		 * The 'striped' node style is only supported with clusters and
		 * rectangularly-shaped nodes('box', 'rect', 'rectangle' and 'square').
		 */
		val expectedErrorMessage = "The style 'striped' is only supported with clusters and rectangularly-shaped nodes, such as 'box', 'rect', 'rectangle', 'square'."

		'''graph {1[shape=ellipse style=striped]}'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, null, expectedErrorMessage)

		'''graph {1[style=striped shape=ellipse]}'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, null, expectedErrorMessage)

		'''graph {node[style=striped shape=ellipse]}'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, null, expectedErrorMessage)

		'''graph {1[style=striped]}'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, null, expectedErrorMessage)

		// TODO: implement test case
		// text = "graph {node[shape=ellipse] 1[style=striped]}";
	}

	@Test def invalid_html_like_label_01() {
		'''graph {1[label = <<BR/><FONT>>]}'''.assertHtmlLikeLabelError("<<BR/><FONT>>",
			"The value '<BR/><FONT>' is not a syntactically correct htmlLabel: Mismatched input '<EOF>' expecting RULE_TAG_START_CLOSE.")
	}

	@Test def invalid_html_like_label_02() {
		val text = '''graph {1[label = <<TABLE</TABLE>>]}'''
		val errorProneText = "<<TABLE</TABLE>>]}"
		val errorMessage = "mismatched character '<EOF>' expecting '>'"
		val offset = text.indexOf(errorProneText)
		val length = errorProneText.length
		text.parse.assertNumberOfIssues(1).
		assertError(DOT_AST, SYNTAX_DIAGNOSTIC, offset, length, errorMessage)
	}

	@Test(timeout=2000) def invalid_html_like_label_03() {
		Assert.assertTrue(DotTestGraphs.INCOMPLETE_HTML_LIKE_LABEL.parse.validate.size > 0)
	}

	@Test def invalid_html_like_label_tag_is_not_closed_properly() {
		'''graph {1[label = <<BR/><FONT/>>]}'''.assertHtmlLikeLabelError("FONT",
		"The htmlLabel value '<BR/><FONT/>' is not semantically correct: Tag '<FONT/>' cannot be self closing.")
	}

	@Test def invalid_html_like_label_tag_cannot_be_self_closing() {
		'''graph {1[label = <  <FONT></foo>  >]}'''.assertHtmlLikeLabelError("foo",
		"The htmlLabel value '  <FONT></foo>  ' is not semantically correct: Tag '<FONT>' is not closed (expected '</FONT>' but got '</foo>').")
	}

	@Test def invalid_html_like_label_string_literal_is_not_allowed() {
		'''graph {1[label = <  <BR>string</BR>  >]}'''.assertHtmlLikeLabelError("BR",
		"The htmlLabel value '  <BR>string</BR>  ' is not semantically correct: Tag '<BR>' cannot contain a string literal.")
	}

	@Test def invalid_html_like_label_unsupported_tag() {
		'''graph {1[label = <  <test>string</test>  >]}'''.assertHtmlLikeLabelError("test",
		"The htmlLabel value '  <test>string</test>  ' is not semantically correct: Tag '<test>' is not supported.")
	}

	@Test def invalid_html_like_label_invalid_parent_tag() {
		'''graph {1[label = <  <tr></tr>  >]}'''.assertHtmlLikeLabelError("tr",
		"The htmlLabel value '  <tr></tr>  ' is not semantically correct: Tag '<tr>' is not allowed inside '<ROOT>', but only inside '<TABLE>'.")
	}

	@Test def invalid_html_like_label_invalid_attribute() {
		'''graph {1[label = <  <table foo="bar"></table>  >]}'''.assertHtmlLikeLabelError("foo",
		'''The htmlLabel value '  <table foo="bar"></table>  ' is not semantically correct: Attribute 'foo' is not allowed inside '<table>'.''')
	}

	@Test def invalid_html_like_label_invalid_attribute_value() {
		'''graph {1[label = <  <table align="foo"></table>  >]}'''.assertHtmlLikeLabelError('"foo"',
		'''The htmlLabel value '  <table align="foo"></table>  ' is not semantically correct: The value 'foo' is not a correct align: Value has to be one of 'CENTER', 'LEFT', 'RIGHT'.''')
	}

	@Test def invalid_html_like_label_invalid_siblings() {
		/*
		 * The graphviz DOT HTML-Like Label grammar does not allow text and
		 * table or multiple tables on the same (root or nested) level.
		 */

		/*
		 * testDataList[][0]: html label containing invalid siblings
		 * testDataList[][1]: text1 to be marked as error prone
		 * testDataList[][2]: index to locate the text1 from
		 * testDataList[][3]: text2 to be marked as error prone
		 * testDataList[][4]: index to locate the text2 from
		 * ...
		 */

		val testDataList = #[
			// root level
			#["<table></table><b></b>", "table", "0", "b", "15"],
			#["<table></table><b>text</b>", "table", "0", "b", "15"],
			#["<table></table><br></br>", "table", "0", "br", "0"],
			#["<table></table><font></font>", "table", "0", "font", "0"],
			#["<table></table><font>text</font>", "table", "0", "font", "0"],
			#["<table></table><i></i>", "table", "0", "i", "0"],
			#["<table></table><i>text</i>", "table", "0", "i", "0"],
			#["<table></table><o></o>", "table", "0", "o", "0"],
			#["<table></table><o>text</o>", "table", "0", "o", "0"],
			#["<table></table><s></s>", "table", "0", "s", "0"],
			#["<table></table><s>text</s>", "table", "0", "s", "0"],
			#["<table></table><sub></sub>", "table", "0", "sub", "0"],
			#["<table></table><sub>text</sub>", "table", "0", "sub", "0"],
			#["<table></table><sup></sup>", "table", "0", "sup", "0"],
			#["<table></table><sup>text</sup>", "table", "0", "sup", "0"],
			#["<table></table><table></table>", "table", "0", "table", "15"],
			#["<table></table><u></u>", "table", "0", "u", "0"],
			#["<table></table><u>text</u>", "table", "0", "u", "0"],
			#["<table></table>text", "table", "0", "text", "0"],
			#["<b></b><table></table>", "b", "0", "table", "0"],
			#["<b>text</b><table></table>", "b", "0", "table", "0"],
			#["<br></br><table></table>", "br", "0", "table", "0"],
			#["<font></font><table></table>", "font", "0", "table", "0"],
			#["<font>text</font><table></table>", "font", "0", "table", "0"],
			#["<i></i><table></table>", "i", "0", "table", "0"],
			#["<i>text</i><table></table>", "i", "0", "table", "0"],
			#["<o></o><table></table>", "o", "0", "table", "0"],
			#["<o>text</o><table></table>", "o", "0", "table", "0"],
			#["<s></s><table></table>", "s", "0", "table", "0"],
			#["<s>text</s><table></table>", "s", "0", "table", "0"],
			#["<sub></sub><table></table>", "sub", "0", "table", "0"],
			#["<sub>text</sub><table></table>", "sub", "0", "table", "0"],
			#["<sup></sup><table></table>", "sup", "0", "table", "0"],
			#["<sup>text</sup><table></table>", "sup", "0", "table", "0"],
			#["<u></u><table></table>", "u", "0", "table", "0"],
			#["<u>text</u><table></table>", "u", "0", "table", "0"],
			#["text<table></table>", "text", "0", "table", "0"],
			#["<table></table>text<table></table>", "table", "0", "text", "0", "table", "20"],

			// nested level
			#["<table><tr><td><table></table><b></b></td></tr></table>", "table", "15", "b", "30"],
			#["<table><tr><td><table></table><br></br></td></tr></table>", "table", "15", "br", "15"],
			#["<table><tr><td><table></table><font></font></td></tr></table>", "table", "15", "font", "15"],
			#["<table><tr><td><table></table><i></i></td></tr></table>", "table", "15", "i", "15"],
			#["<table><tr><td><table></table><o></o></td></tr></table>", "table", "15", "o", "15"],
			#["<table><tr><td><table></table><s></s></td></tr></table>", "table", "15", "s", "15"],
			#["<table><tr><td><table></table><sub></sub></td></tr></table>", "table", "15", "sub", "15"],
			#["<table><tr><td><table></table><sup></sup></td></tr></table>", "table", "15", "sup", "15"],
			#["<table><tr><td><table></table><table></table></td></tr></table>", "table", "15", "table", "30"],
			#["<table><tr><td><table></table><u></u></td></tr></table>", "table", "15", "u", "15"],
			#["<table><tr><td><table></table>text</td></tr></table>", "table", "15", "text", "15"],
			#["<table><tr><td><b></b><table></table></td></tr></table>", "b", "15", "table", "15"],
			#["<table><tr><td><br></br><table></table></td></tr></table>", "br", "15", "table", "15"],
			#["<table><tr><td><font></font><table></table></td></tr></table>", "font", "15", "table", "15"],
			#["<table><tr><td><i></i><table></table></td></tr></table>", "i", "15", "table", "15"],
			#["<table><tr><td><o></o><table></table></td></tr></table>", "o", "15", "table", "15"],
			#["<table><tr><td><s></s><table></table></td></tr></table>", "s", "15", "table", "15"],
			#["<table><tr><td><sub></sub><table></table></td></tr></table>", "sub", "15", "table", "15"],
			#["<table><tr><td><sup></sup><table></table></td></tr></table>", "sup", "15", "table", "15"],
			#["<table><tr><td><u></u><table></table></td></tr></table>", "u", "15", "table", "15"],
			#["<table><tr><td>text<table></table></td></tr></table>", "text", "15", "table", "15"],
			#["<table><tr><td><table></table>text<table></table></td></tr></table>", "table", "15", "text", "15", "table", "34"]
		]

		for (testData : testDataList) {
			val htmlLabel = testData.get(0)
			val numberOfErrorProneText = (testData.length - 1) / 2

			val errorProneTextList = newArrayList
			val errorProneTextIndexList = newArrayList
			val errorMessages = newArrayList

			for (var i = 0; i < numberOfErrorProneText; i++) {
				errorProneTextList += testData.get(2 * i + 1)
				errorProneTextIndexList += Integer.valueOf(testData.get(2 * i + 2)) + 18
				errorMessages += '''The htmlLabel value '«htmlLabel»' is not semantically correct: Invalid siblings.'''
			}

			'''graph {1[label = <«htmlLabel»>]}'''.assertHtmlLikeLabelErrors(errorProneTextList, errorProneTextIndexList, errorMessages)
		}
	}

	@Test def invalid_node_style() {
		'''graph {1[style="dashed, setlinewidth(4)"]}'''.assertStyleWarning("setlinewidth",
		'''The style value 'dashed, setlinewidth(4)' is not semantically correct: The usage of setlinewidth is deprecated, use the penwidth attribute instead.''')

		'''graph {1[style="dashed, foo"]}'''.assertStyleError("foo", 
		'''The style value 'dashed, foo' is not semantically correct: Value should be one of 'bold', 'dashed', 'diagonals', 'dotted', 'filled', 'invis', 'radial', 'rounded', 'solid', 'striped', 'wedged'.'''
		)
	}

	@Test def invalid_edge_style() {
		'''graph {1--2[style="dashed, setlinewidth(4)"]}'''.assertStyleWarning("setlinewidth",
		'''The style value 'dashed, setlinewidth(4)' is not semantically correct: The usage of setlinewidth is deprecated, use the penwidth attribute instead.''')

		'''graph {1--2[style="dashed, foo"]}'''.assertStyleError("foo", 
		'''The style value 'dashed, foo' is not semantically correct: Value should be one of 'bold', 'dashed', 'dotted', 'invis', 'solid', 'tapered'.''')
	}

	@Test def invalid_subgraph_rank_attribute() {
		'''graph{subgraph{rank=foo}}'''.parse.assertNumberOfIssues(1).
		assertError(ATTRIBUTE, RANK__S, '''The value 'foo' is not a syntactically correct rankType: Value has to be one of 'same', 'min', 'source', 'max', 'sink'.''')
	}

	@Test def record_shape_node1() {
		"record_shape_node1.dot".readFile.assertNoIssues
	}

	@Test def invalid_port_assigned_same_name_record_label() {
		val dotAst = '''digraph{ node [shape=record]; myNode [label="<here> foo | <here> more foo"]; }'''.parse.assertNumberOfIssues(2)
		dotAst.assertError(ATTRIBUTE, PORT_NAME_DUPLICATE, 46, 4, "The record-based label '<here> foo | <here> more foo' is not semantically correct: Port name not unique: here")
		dotAst.assertError(ATTRIBUTE, PORT_NAME_DUPLICATE, 59, 4, "The record-based label '<here> foo | <here> more foo' is not semantically correct: Port name not unique: here")
	}

	@Test def invalid_port_not_assigned_name_record_label() {
		'''digraph{ node [shape=record]; myNode [label="<> foo | <here> more foo"]; }'''.parse.assertNumberOfIssues(1).
		assertWarning(ATTRIBUTE, PORT_NAME_NOT_SET, 45, 6, "The record-based label '<> foo | <here> more foo' is not semantically correct: Port unnamed: port cannot be referenced")
	}

	@Test def invalid_syntax_error_record_label() {
		'''digraph{ node [shape=record]; myNode [label="<}> foo | <here> more foo"]; }'''.parse.
		assertError(ATTRIBUTE, SYNTAX_DIAGNOSTIC, 46, 1, "The value '<}> foo | <here> more foo' is not a syntactically correct record-based label: extraneous input '}' expecting '>'")
	}

	@Test def invalid_cluster_style() {
		'''
			graph {
				subgraph cluster {
					style = foo
					1
				}
			}
		'''.assertStyleError("foo", "The style value 'foo' is not semantically correct: Value should be one of 'bold', 'dashed', 'dotted', 'filled', 'invis', 'radial', 'rounded', 'solid', 'striped'.")
	}

	@Test def incomplete_model() {
		'''graph{1[c]}'''.parse.
		assertError(ATTR_LIST, SYNTAX_DIAGNOSTIC, 9, 1, "mismatched input ']' expecting '='")
	}

	@Test def redundant_attribute_single() {
		'''graph{1[label="foo", label="faa"]}'''.assertRedundantAttributeWarning('''label="foo"''',
		"Redundant attribute value 'foo' for attribute 'label' is ignored.")
	}

	@Test def redundant_attribute_mixed() {
		'''graph{1[label="foo", style="rounded", label="faa"]}'''.assertRedundantAttributeWarning('''label="foo"''',
			"Redundant attribute value 'foo' for attribute 'label' is ignored."
		)
	}

	@Test def redundant_attribute_multiple() {
		val dotAst = '''graph{1[label="foo", label=fee, label="faa"]}'''.parse.assertNumberOfIssues(2)
		dotAst.assertRedundantAttributeWarning("Redundant attribute value 'foo' for attribute 'label' is ignored.")
		dotAst.assertRedundantAttributeWarning("Redundant attribute value 'fee' for attribute 'label' is ignored.")
	}

	@Test def redundant_attribute_multiple_mixed() {
		val dotAst = '''graph{1[label="foo", xlabel="lorem ipsum", label=fee, label="faa"]}'''.parse.assertNumberOfIssues(2)
		dotAst.assertRedundantAttributeWarning("Redundant attribute value 'foo' for attribute 'label' is ignored.")
		dotAst.assertRedundantAttributeWarning("Redundant attribute value 'fee' for attribute 'label' is ignored.")
	}

	@Test def redundant_attribute_edge() {
		'''graph{1--2[style="dotted", style="dashed"]}'''.assertRedundantAttributeWarning('''style="dotted"''',
			"Redundant attribute value 'dotted' for attribute 'style' is ignored."
		)
	}

	@Test def redundant_attribute_attr_stmt() {
		'''graph{graph[label="dotted", label="dashed"]1}'''.assertRedundantAttributeWarning('''label="dotted"''',
			"Redundant attribute value 'dotted' for attribute 'label' is ignored."
		)
	}

	private def assertArrowTypeWarning(DotAst dotAst, String message) {
		dotAst.assertWarning(ATTRIBUTE, ARROWHEAD__E, message)
	}

	private def assertArrowTypeWarning(DotAst dotAst, int offset, int length, String message) {
		dotAst.assertWarning(ATTRIBUTE, ARROWHEAD__E, offset, length, message)
	}

	private def assertHtmlLikeLabelError(CharSequence text, String errorProneText, String message) {
		val offset = text.toString.indexOf(errorProneText)
		val length = errorProneText.length
		text.parse.assertNumberOfIssues(1).assertError(ATTRIBUTE, LABEL__GCNE, offset, length, message)
	}

	private def assertHtmlLikeLabelErrors(CharSequence text, List<String> errorProneTextList, List<Integer> fromIndexList, List<String> errorMessages) {
		if (errorProneTextList.size != errorMessages.size || errorProneTextList.size != fromIndexList.size) {
			throw new IllegalArgumentException(
				"Expected as much as errorProneTextList as fromIndexList and as errorMessages!")
		}
		for (var i = 0; i < errorProneTextList.length; i++) {
			val errorProneText = errorProneTextList.get(i)
			val errorMessage = errorMessages.get(i)
			val fromIndex = fromIndexList.get(i)
			val offset = text.toString.indexOf(errorProneText, fromIndex)
			if (offset < 0) {
				throw new IllegalArgumentException(''''«errorProneText»' cannot be found in the input string from index «fromIndex»''')
			}
			val length = errorProneText.length

			text.parse
			.assertNumberOfIssues(errorMessages.length)
			.assertError(ATTRIBUTE, LABEL__GCNE, offset, length, errorMessage)
		}
	}

	private def assertStyleWarning(CharSequence text, String errorProneText, String message) {
		val offset = text.toString.indexOf(errorProneText)
		val length = errorProneText.length
		text.parse.assertNumberOfIssues(1).assertWarning(ATTRIBUTE, STYLE__GCNE, offset, length, message)
	}

	private def assertStyleError(CharSequence text, String errorProneText, String message) {
		val offset = text.toString.indexOf(errorProneText)
		val length = errorProneText.length
		text.parse.assertNumberOfIssues(1).assertError(ATTRIBUTE, STYLE__GCNE, offset, length, message)
	}

	private def assertRedundantAttributeWarning(DotAst dotAst, String message) {
		dotAst.assertWarning(ATTRIBUTE, REDUNDANT_ATTRIBUTE, message)
	}

	private def assertRedundantAttributeWarning(CharSequence text, String errorProneText, String message) {
		val offset = text.toString.indexOf(errorProneText)
		val length = errorProneText.length
		text.parse.assertWarning(ATTRIBUTE, REDUNDANT_ATTRIBUTE, offset, length, message)
	}

	private def assertNumberOfIssues(DotAst dotAst, int expectedNumberOfIssues) {
		expectedNumberOfIssues.assertEquals(dotAst.validate.size)
		dotAst
	}

	private def readFile(String fileName) {
		fileName.content.parse
	}

}
