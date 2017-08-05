/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial implementation (bug #477980)		
 *                                - Add support for polygon-based node shapes (bug #441352)
 *
 *******************************************************************************/

package org.eclipse.gef.dot.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.DotFileUtils;
import org.eclipse.gef.dot.internal.language.DotInjectorProvider;
import org.eclipse.gef.dot.internal.language.dot.DotAst;
import org.eclipse.gef.dot.internal.language.dot.DotPackage;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

@RunWith(XtextRunner.class)
@InjectWith(DotInjectorProvider.class)
public class DotValidatorTests {

	@Inject
	ParseHelper<DotAst> parserHelper;

	@Inject
	ValidationTestHelper validationTestHelper;

	@Test
	public void testSingleArrowShapes() {
		DotAst dotAst = parse("arrowshapes_single.dot");
		validationTestHelper.assertNoIssues(dotAst);
	}

	@Test
	public void testMultipleArrowShapes() {
		DotAst dotAst = parse("arrowshapes_multiple.dot");
		validationTestHelper.assertNoIssues(dotAst);
	}

	@Test
	public void testDeprecatedArrowType() throws Exception {
		DotAst dotAst = parse("arrowshapes_deprecated.dot");

		assertArrowTypeWarning(dotAst,
				"The arrowType value 'ediamond' is not semantically correct: The shape 'ediamond' is deprecated.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'open' is not semantically correct: The shape 'open' is deprecated.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'halfopen' is not semantically correct: The shape 'halfopen' is deprecated.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'empty' is not semantically correct: The shape 'empty' is deprecated.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'invempty' is not semantically correct: The shape 'invempty' is deprecated.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'ediamondinvempty' is not semantically correct: The shape 'ediamond' is deprecated.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'ediamondinvempty' is not semantically correct: The shape 'invempty' is deprecated.");

		int lineDelimiterLength = System.getProperty("line.separator").length();
		assertArrowTypeWarning(dotAst, 1311 + 28 * lineDelimiterLength, 4,
				"The arrowType value 'openbox' is not semantically correct: The shape 'open' is deprecated.");

		// verify that these are the only reported issues
		Assert.assertEquals(8, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testDeprecatedStyle() throws Exception {
		DotAst dotAst = parserHelper.parse(DotTestGraphs.DEPRECATED_STYLES);

		int lineDelimiterLength = System.getProperty("line.separator").length();

		validationTestHelper.assertWarning(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.STYLE__GCNE,
				113 + 5 * lineDelimiterLength, 12,
				"The style value 'setlinewidth(1)' is not semantically correct: The usage of setlinewidth is deprecated, use the penwidth attribute instead.");

		validationTestHelper.assertWarning(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.STYLE__GCNE,
				140 + 6 * lineDelimiterLength, 12,
				"The style value 'setlinewidth(2)' is not semantically correct: The usage of setlinewidth is deprecated, use the penwidth attribute instead.");

		validationTestHelper.assertWarning(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.STYLE__GCNE,
				170 + 7 * lineDelimiterLength, 12,
				"The style value 'setlinewidth(3)' is not semantically correct: The usage of setlinewidth is deprecated, use the penwidth attribute instead.");

		validationTestHelper.assertWarning(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.STYLE__GCNE,
				222 + 10 * lineDelimiterLength, 12,
				"The style value 'dashed, setlinewidth(4)' is not semantically correct: The usage of setlinewidth is deprecated, use the penwidth attribute instead.");

		validationTestHelper.assertWarning(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.STYLE__GCNE,
				249 + 11 * lineDelimiterLength, 12,
				"The style value 'setlinewidth(5), dotted' is not semantically correct: The usage of setlinewidth is deprecated, use the penwidth attribute instead.");

		// verify that these are the only reported issues
		Assert.assertEquals(5, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testArrowshapesDirectionBoth() {
		DotAst dotAst = parse("arrowshapes_direction_both.dot");
		validationTestHelper.assertNoIssues(dotAst);
	}

	@Test
	public void testArrowShapesInvalidModifiers() throws Exception {
		registerEscStringPackage();

		DotAst dotAst = parse("arrowshapes_invalid_modifiers.dot");

		assertArrowTypeWarning(dotAst,
				"The arrowType value 'ocrow' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'crow'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'olcrow' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'crow'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'orcrow' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'crow'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'rdot' is not semantically correct: The side modifier 'r' may not be combined with primitive shape 'dot'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'ldot' is not semantically correct: The side modifier 'l' may not be combined with primitive shape 'dot'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'oldot' is not semantically correct: The side modifier 'l' may not be combined with primitive shape 'dot'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'ordot' is not semantically correct: The side modifier 'r' may not be combined with primitive shape 'dot'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'lnone' is not semantically correct: The side modifier 'l' may not be combined with primitive shape 'none'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'rnone' is not semantically correct: The side modifier 'r' may not be combined with primitive shape 'none'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'onone' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'none'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'olnone' is not semantically correct: The side modifier 'l' may not be combined with primitive shape 'none'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'olnone' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'none'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'ornone' is not semantically correct: The side modifier 'r' may not be combined with primitive shape 'none'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'ornone' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'none'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'otee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'tee'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'oltee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'tee'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'ortee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'tee'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'ovee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'vee'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'olvee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'vee'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'orvee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'vee'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'ocurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'curve'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'olcurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'curve'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'orcurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'curve'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'oicurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'icurve'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'olicurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'icurve'.");
		assertArrowTypeWarning(dotAst,
				"The arrowType value 'oricurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'icurve'.");

		// verify that these are the only reported issues
		Assert.assertEquals(26, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testWrongArrowType() throws Exception {
		registerArrowTypePackage();

		String text = "digraph testGraph { 1->2[arrowhead=fooBar arrowtail=fooBar2] }";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.ARROWHEAD__E,
				35, 6,
				"The value 'fooBar' is not a syntactically correct arrowType: No viable alternative at character 'f'. No viable alternative at input 'o'. No viable alternative at character 'B'. No viable alternative at character 'a'. No viable alternative at input '<EOF>'.");

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.ARROWTAIL__E,
				52, 7,
				"The value 'fooBar2' is not a syntactically correct arrowType: No viable alternative at character 'f'. No viable alternative at input 'o'. No viable alternative at character 'B'. No viable alternative at character 'a'. No viable alternative at character '2'.");

		// verify that these are the only reported issues
		Assert.assertEquals(2, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testWrongEdgeDirection() throws Exception {
		String text = "digraph testGraph { 1->2[dir=foo] }";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.DIR__E, 29,
				3,
				"The value 'foo' is not a syntactically correct dirType: Value has to be one of 'forward', 'back', 'both', 'none'.");

		// verify that it is the only reported issue
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testWrongEdgeArrowSize() throws Exception {
		String text = "digraph testGraph { 1->2[arrowsize=foo] 3->4[arrowsize=\"-2.0\"]}";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.ARROWSIZE__E,
				"The value 'foo' is not a syntactically correct double: For input string: \"foo\".");

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.ARROWSIZE__E,
				"The double value '-2.0' is not semantically correct: Value may not be smaller than 0.0.");

		// verify that these are the only reported issues
		Assert.assertEquals(2, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testNoneIsTheLastArrowShape() throws Exception {
		String text = "digraph { 1->2[arrowhead=boxnone] }";

		DotAst dotAst = parserHelper.parse(text);

		assertArrowTypeWarning(dotAst,
				"The arrowType value 'boxnone' is not semantically correct: The shape 'none' may not be the last shape.");

		// verify that this is the only reported issue
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testWrongGraphBackgroundColor() throws Exception {
		registerColorPackage();
		registerColorListPackage();

		String text = "graph { bgcolor=grsy }";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.BGCOLOR__GC,
				"The colorList value 'grsy' is not semantically correct: The 'grsy' color is not valid within the 'x11' color scheme.");

		// verify that this is the only reported issues
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testGraphBackgroundColorDoesNotCorrespondToLocalColorScheme()
			throws Exception {
		registerColorPackage();
		registerColorListPackage();

		String text = "graph { colorscheme=brbg10 bgcolor=blue}";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.BGCOLOR__GC,
				"The colorList value 'blue' is not semantically correct: The 'blue' color is not valid within the 'brbg10' color scheme.");

		// verify that this is the only reported issues
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testGraphBackgroundColorDoesNotCorrespondToGlobalColorScheme()
			throws Exception {
		registerColorPackage();
		registerColorListPackage();

		String text = "graph { graph[colorscheme=brbg10] bgcolor=blue}";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.BGCOLOR__GC,
				"The colorList value 'blue' is not semantically correct: The 'blue' color is not valid within the 'brbg10' color scheme.");

		// verify that this is the only reported issues
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testWrongNodeColor() throws Exception {
		registerColorPackage();

		String text = "graph { 1[color=\"#fffff\"]}";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.COLOR__CNE,
				"The value '#fffff' is not a syntactically correct color: Mismatched input '<EOF>' expecting RULE_HEXADECIMAL_DIGIT.");

		// verify that this is the only reported issues
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testNodeColorDoesNotCorrespondToLocalColorScheme()
			throws Exception {
		registerColorPackage();

		String text = "graph { 1[colorscheme=brbg10 color=blue]}";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.COLOR__CNE,
				"The color value 'blue' is not semantically correct: The 'blue' color is not valid within the 'brbg10' color scheme.");

		// verify that this is the only reported issues
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testNodeColorDoesNotCorrespondToGlobalColorScheme()
			throws Exception {
		registerColorPackage();

		String text = "graph { node[colorscheme=brbg10] 1[color=blue]}";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.COLOR__CNE,
				"The color value 'blue' is not semantically correct: The 'blue' color is not valid within the 'brbg10' color scheme.");

		// verify that this is the only reported issues
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testWrongEdgeFillColor() throws Exception {
		registerColorPackage();

		String text = "digraph { 1->2[fillcolor=\"#fffff\"]}";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(),
				DotAttributes.FILLCOLOR__CNE,
				"The value '#fffff' is not a syntactically correct color: Mismatched input '<EOF>' expecting RULE_HEXADECIMAL_DIGIT.");

		// verify that this is the only reported issues
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testEdgeFillColorDoesNotCorrespondToLocalColorScheme()
			throws Exception {
		registerColorPackage();

		String text = "digraph { 1->2[colorscheme=brbg10 fillcolor=white]}";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(),
				DotAttributes.FILLCOLOR__CNE,
				"The color value 'white' is not semantically correct: The 'white' color is not valid within the 'brbg10' color scheme.");

		// verify that this is the only reported issues
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testEdgeFillColorDoesNotCorrespondToGlobalColorScheme()
			throws Exception {
		registerColorPackage();

		String text = "digraph { edge[colorscheme=brbg10] 1->2[fillcolor=red]}";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(),
				DotAttributes.FILLCOLOR__CNE,
				"The color value 'red' is not semantically correct: The 'red' color is not valid within the 'brbg10' color scheme.");

		// verify that this is the only reported issues
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testWrongNodeDistortion() throws Exception {
		String text = "graph { 1[distortion=foo] 2[distortion=\"-100.0001\"]}";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(),
				DotAttributes.DISTORTION__N,
				"The value 'foo' is not a syntactically correct double: For input string: \"foo\".");

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(),
				DotAttributes.DISTORTION__N,
				"The double value '-100.0001' is not semantically correct: Value may not be smaller than -100.0.");

		// verify that these are the only reported issues
		Assert.assertEquals(2, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testWrongNodeShape() throws Exception {
		String text = "graph { 1[shape=foo] }";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.SHAPE__N,
				"The value 'foo' is not a syntactically correct shape: No viable alternative at input 'foo'.");

		// verify that this is the only reported issue
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testWrongNodeSides() throws Exception {
		String text = "graph { 1[sides=foo] 2[sides=\"-1\"]}";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.SIDES__N,
				"The value 'foo' is not a syntactically correct int: For input string: \"foo\".");

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.SIDES__N,
				"The int value '-1' is not semantically correct: Value may not be smaller than 0.");

		// verify that these are the only reported issues
		Assert.assertEquals(2, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testWrongNodeSkew() throws Exception {
		String text = "graph { 1[skew=foo] 2[skew=\"-100.1\"]}";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.SKEW__N,
				"The value 'foo' is not a syntactically correct double: For input string: \"foo\".");

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.SKEW__N,
				"The double value '-100.1' is not semantically correct: Value may not be smaller than -100.0.");

		// verify that these are the only reported issues
		Assert.assertEquals(2, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testDirectedGraphWithNonDirectedEdge() throws Exception {
		String text = "digraph {1--2}";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getEdgeRhsNode(), null,
				"EdgeOp '--' may only be used in undirected graphs.");

		// verify that this is the only reported issue
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testNonDirectedGraphWithDirectedEdge() throws Exception {
		String text = "graph {1->2}";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getEdgeRhsNode(), null,
				"EdgeOp '->' may only be used in directed graphs.");

		// verify that this is the only reported issue
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testInvalidCombinationOfNodeShapeAndStyle() throws Exception {
		registerShapePackage();
		registerStylePackage();

		/*
		 * The 'striped' node style is only supported with clusters and
		 * rectangularly-shaped nodes('box', 'rect', 'rectangle' and 'square').
		 */

		String text = "graph {1[shape=ellipse style=striped]}";

		DotAst dotAst = parserHelper.parse(text);

		String expectedErrorMessage = "The style 'striped' is only supported with clusters and rectangularly-shaped nodes, such as 'box', 'rect', 'rectangle', 'square'.";

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), null,
				expectedErrorMessage);

		// verify that this is the only reported issue
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());

		text = "graph {1[style=striped shape=ellipse]}";

		dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), null,
				expectedErrorMessage);

		// verify that this is the only reported issue
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());

		text = "graph {node[style=striped shape=ellipse]}";

		dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), null,
				expectedErrorMessage);

		// verify that this is the only reported issue
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());

		text = "graph {1[style=striped]}";

		dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), null,
				expectedErrorMessage);

		// verify that this is the only reported issue
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());

		// TODO: implement test case
		// text = "graph {node[shape=ellipse] 1[style=striped]}";
	}

	@Test
	public void testInvalidHtmlLikeLabelParserProblem() {
		registerHtmlLabelPackage();

		String text = "graph {1[label = <<BR/><FONT>>]}";
		String errorProneText = "<<BR/><FONT>>";
		String errorMessage = "The value '<BR/><FONT>' is not a syntactically correct htmlLabel: Mismatched input '<EOF>' expecting RULE_TAG_START_CLOSE.";
		assertHtmlLikeLabelError(text, errorProneText, errorMessage);
	}

	@Test
	public void testInvalidHtmlLikeLabelTagIsNotClosedProperly() {
		String text = "graph {1[label = <<BR/><FONT/>>]}";
		String errorProneText = "FONT";
		String errorMessage = "The htmlLabel value '<BR/><FONT/>' is not semantically correct: Tag '<FONT/>' cannot be self closing.";
		assertHtmlLikeLabelError(text, errorProneText, errorMessage);
	}

	@Test
	public void testInvalidHtmlLikeLabelTagCannotBeSelfClosing() {
		String text = "graph {1[label = <  <FONT></foo>  >]}";
		String errorProneText = "foo";
		String errorMessage = "The htmlLabel value '  <FONT></foo>  ' is not semantically correct: Tag '<FONT>' is not closed (expected '</FONT>' but got '</foo>').";
		assertHtmlLikeLabelError(text, errorProneText, errorMessage);
	}

	@Test
	public void testInvalidHtmlLikeLabelStringLiteralIsNotAllowed()
			throws Exception {
		String text = "graph {1[label = <  <BR>string</BR>  >]}";
		String errorProneText = "BR";
		String errorMessage = "The htmlLabel value '  <BR>string</BR>  ' is not semantically correct: Tag '<BR>' cannot contain a string literal.";
		assertHtmlLikeLabelError(text, errorProneText, errorMessage);
	}

	@Test
	public void testInvalidHtmlLikeLabelUnsupportedTag() {
		String text = "graph {1[label = <  <test>string</test>  >]}";
		String errorProneText = "test";
		String errorMessage = "The htmlLabel value '  <test>string</test>  ' is not semantically correct: Tag '<test>' is not supported.";
		assertHtmlLikeLabelError(text, errorProneText, errorMessage);
	}

	@Test
	public void testInvalidHtmlLikeLabelInvalidParentTag() {
		String text = "graph {1[label = <  <tr></tr>  >]}";
		String errorProneText = "tr";
		String errorMessage = "The htmlLabel value '  <tr></tr>  ' is not semantically correct: Tag '<tr>' is not allowed inside '<ROOT>', but only inside '<TABLE>'.";
		assertHtmlLikeLabelError(text, errorProneText, errorMessage);
	}

	@Test
	public void testInvalidHtmlLikeLabelInvalidAttribute() {
		String text = "graph {1[label = <  <table foo=\"bar\"></table>  >]}";
		String errorProneText = "foo";
		String errorMessage = "The htmlLabel value '  <table foo=\"bar\"></table>  ' is not semantically correct: Attribute 'foo' is not allowed inside '<table>'.";
		assertHtmlLikeLabelError(text, errorProneText, errorMessage);
	}

	@Test
	public void testInvalidHtmlLikeLabelInvalidAttributeValue() {
		String text = "graph {1[label = <  <table align=\"foo\"></table>  >]}";
		String errorProneText = "\"foo\"";
		String errorMessage = "The htmlLabel value '  <table align=\"foo\"></table>  ' is not semantically correct: The value 'foo' is not a correct align: Value has to be one of 'CENTER', 'LEFT', 'RIGHT'.";
		assertHtmlLikeLabelError(text, errorProneText, errorMessage);
	}

	@Test
	public void testInvalidNodeStyle() {
		String text = "graph {1[style=\"dashed, setlinewidth(4)\"]}";
		String errorProneText = "setlinewidth";
		String message = "The style value 'dashed, setlinewidth(4)' is not semantically correct: The usage of setlinewidth is deprecated, use the penwidth attribute instead.";
		assertStyleWarning(text, errorProneText, message);

		text = "graph {1[style=\"dashed, foo\"]}";
		errorProneText = "foo";
		message = "The style value 'dashed, foo' is not semantically correct: Value should be one of 'bold', 'dashed', 'diagonals', 'dotted', 'filled', 'invis', 'radial', 'rounded', 'solid', 'striped', 'wedged'.";
		assertStyleError(text, errorProneText, message);
	}

	@Test
	public void testInvalidEdgeStyle() throws Exception {
		String text = "graph {1--2[style=\"dashed, setlinewidth(4)\"]}";
		String errorProneText = "setlinewidth";
		String message = "The style value 'dashed, setlinewidth(4)' is not semantically correct: The usage of setlinewidth is deprecated, use the penwidth attribute instead.";
		assertStyleWarning(text, errorProneText, message);

		text = "graph {1--2[style=\"dashed, foo\"]}";
		errorProneText = "foo";
		message = "The style value 'dashed, foo' is not semantically correct: Value should be one of 'bold', 'dashed', 'dotted', 'invis', 'solid', 'tapered'.";
		assertStyleError(text, errorProneText, message);
	}

	@Test
	public void testInvalidSubgraphRankAttribute() throws Exception {
		String text = "graph{subgraph{rank=foo}}";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.RANK__S,
				"The value 'foo' is not a syntactically correct rankType: Value has to be one of 'same', 'min', 'source', 'max', 'sink'.");

		// verify that this is the only reported issue
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());
	}

	private DotAst parse(String fileName) {
		DotAst dotAst = null;
		String fileContents = DotFileUtils
				.read(new File(DotTestUtils.RESOURCES_TESTS + fileName));
		try {
			dotAst = parserHelper.parse(fileContents);
			assertNotNull(dotAst);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		return dotAst;
	}

	private void assertArrowTypeWarning(DotAst dotAst, String warningMessage) {
		validationTestHelper.assertWarning(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.ARROWHEAD__E,
				warningMessage);
	}

	private void assertArrowTypeWarning(DotAst dotAst, int offset, int length,
			String warningMessage) {
		validationTestHelper.assertWarning(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.ARROWHEAD__E,
				offset, length, warningMessage);
	}

	private void assertHtmlLikeLabelError(String text, String errorProneText,
			String errorMessage) {
		DotAst dotAst = null;
		try {
			dotAst = parserHelper.parse(text);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(dotAst);
		int offset = text.indexOf(errorProneText);
		int length = errorProneText.length();

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.LABEL__GCNE,
				offset, length, errorMessage);
	}

	private void assertStyleWarning(String text, String errorProneText,
			String warningMessage) {
		DotAst dotAst = null;
		try {
			dotAst = parserHelper.parse(text);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(dotAst);
		int offset = text.indexOf(errorProneText);
		int length = errorProneText.length();

		validationTestHelper.assertWarning(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.STYLE__GCNE,
				offset, length, warningMessage);
	}

	private void assertStyleError(String text, String errorProneText,
			String errorMessage) {
		DotAst dotAst = null;
		try {
			dotAst = parserHelper.parse(text);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(dotAst);
		int offset = text.indexOf(errorProneText);
		int length = errorProneText.length();

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.STYLE__GCNE,
				offset, length, errorMessage);
	}

	// TODO: check why these extra EMF Package registrations are necessary
	private void registerArrowTypePackage() {
		if (!EPackage.Registry.INSTANCE.containsKey(
				org.eclipse.gef.dot.internal.language.arrowtype.ArrowtypePackage.eNS_URI)) {
			EPackage.Registry.INSTANCE.put(
					org.eclipse.gef.dot.internal.language.arrowtype.ArrowtypePackage.eNS_URI,
					org.eclipse.gef.dot.internal.language.arrowtype.ArrowtypePackage.eINSTANCE);
		}
	}

	private void registerColorPackage() {
		if (!EPackage.Registry.INSTANCE.containsKey(
				org.eclipse.gef.dot.internal.language.color.ColorPackage.eNS_URI)) {
			EPackage.Registry.INSTANCE.put(
					org.eclipse.gef.dot.internal.language.color.ColorPackage.eNS_URI,
					org.eclipse.gef.dot.internal.language.color.ColorPackage.eINSTANCE);
		}
	}

	private void registerColorListPackage() {
		if (!EPackage.Registry.INSTANCE.containsKey(
				org.eclipse.gef.dot.internal.language.colorlist.ColorlistPackage.eNS_URI)) {
			EPackage.Registry.INSTANCE.put(
					org.eclipse.gef.dot.internal.language.colorlist.ColorlistPackage.eNS_URI,
					org.eclipse.gef.dot.internal.language.colorlist.ColorlistPackage.eINSTANCE);
		}
	}

	private void registerEscStringPackage() {
		if (!EPackage.Registry.INSTANCE.containsKey(
				org.eclipse.gef.dot.internal.language.escstring.EscstringPackage.eNS_URI)) {
			EPackage.Registry.INSTANCE.put(
					org.eclipse.gef.dot.internal.language.escstring.EscstringPackage.eNS_URI,
					org.eclipse.gef.dot.internal.language.escstring.EscstringPackage.eINSTANCE);
		}
	}

	private void registerHtmlLabelPackage() {
		if (!EPackage.Registry.INSTANCE.containsKey(
				org.eclipse.gef.dot.internal.language.htmllabel.HtmllabelPackage.eNS_URI)) {
			EPackage.Registry.INSTANCE.put(
					org.eclipse.gef.dot.internal.language.htmllabel.HtmllabelPackage.eNS_URI,
					org.eclipse.gef.dot.internal.language.htmllabel.HtmllabelPackage.eINSTANCE);
		}
	}

	private void registerShapePackage() {
		if (!EPackage.Registry.INSTANCE.containsKey(
				org.eclipse.gef.dot.internal.language.shape.ShapePackage.eNS_URI)) {
			EPackage.Registry.INSTANCE.put(
					org.eclipse.gef.dot.internal.language.shape.ShapePackage.eNS_URI,
					org.eclipse.gef.dot.internal.language.shape.ShapePackage.eINSTANCE);
		}
	}

	private void registerStylePackage() {
		if (!EPackage.Registry.INSTANCE.containsKey(
				org.eclipse.gef.dot.internal.language.style.StylePackage.eNS_URI)) {
			EPackage.Registry.INSTANCE.put(
					org.eclipse.gef.dot.internal.language.style.StylePackage.eNS_URI,
					org.eclipse.gef.dot.internal.language.style.StylePackage.eINSTANCE);
		}
	}
}