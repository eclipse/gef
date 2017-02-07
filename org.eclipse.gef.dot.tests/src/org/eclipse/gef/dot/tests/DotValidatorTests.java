/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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
				"The ArrowType value 'ediamond' is not semantically correct: The shape 'ediamond' is deprecated.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'open' is not semantically correct: The shape 'open' is deprecated.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'halfopen' is not semantically correct: The shape 'halfopen' is deprecated.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'empty' is not semantically correct: The shape 'empty' is deprecated.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'invempty' is not semantically correct: The shape 'invempty' is deprecated.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'ediamondinvempty' is not semantically correct: The shape 'ediamond' is deprecated.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'ediamondinvempty' is not semantically correct: The shape 'invempty' is deprecated.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'openbox' is not semantically correct: The shape 'open' is deprecated.");

		// verify that these are the only reported issues
		Assert.assertEquals(8, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testArrowshapesDirectionBoth() {
		DotAst dotAst = parse("arrowshapes_direction_both.dot");
		validationTestHelper.assertNoIssues(dotAst);
	}

	@Test
	public void testArrowShapesInvalidModifiers() throws Exception {
		DotAst dotAst = parse("arrowshapes_invalid_modifiers.dot");

		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'ocrow' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'crow'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'olcrow' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'crow'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'orcrow' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'crow'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'rdot' is not semantically correct: The side modifier 'r' may not be combined with primitive shape 'dot'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'ldot' is not semantically correct: The side modifier 'l' may not be combined with primitive shape 'dot'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'oldot' is not semantically correct: The side modifier 'l' may not be combined with primitive shape 'dot'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'ordot' is not semantically correct: The side modifier 'r' may not be combined with primitive shape 'dot'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'lnone' is not semantically correct: The side modifier 'l' may not be combined with primitive shape 'none'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'rnone' is not semantically correct: The side modifier 'r' may not be combined with primitive shape 'none'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'onone' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'none'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'olnone' is not semantically correct: The side modifier 'l' may not be combined with primitive shape 'none'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'olnone' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'none'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'ornone' is not semantically correct: The side modifier 'r' may not be combined with primitive shape 'none'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'ornone' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'none'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'otee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'tee'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'oltee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'tee'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'ortee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'tee'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'ovee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'vee'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'olvee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'vee'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'orvee' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'vee'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'ocurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'curve'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'olcurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'curve'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'orcurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'curve'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'oicurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'icurve'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'olicurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'icurve'.");
		assertArrowTypeWarning(dotAst,
				"The ArrowType value 'oricurve' is not semantically correct: The open modifier 'o' may not be combined with primitive shape 'icurve'.");

		// verify that these are the only reported issues
		Assert.assertEquals(26, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testWrongArrowType() throws Exception {
		String text = "digraph testGraph { 1->2[arrowhead=fooBar arrowtail=fooBar2] }";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.ARROWHEAD__E,
				35, 6,
				"The value 'fooBar' is not a syntactically correct ArrowType: No viable alternative at character 'f'. No viable alternative at input 'o'. No viable alternative at character 'B'. No viable alternative at character 'a'. No viable alternative at input '<EOF>'.");

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.ARROWTAIL__E,
				52, 7,
				"The value 'fooBar2' is not a syntactically correct ArrowType: No viable alternative at character 'f'. No viable alternative at input 'o'. No viable alternative at character 'B'. No viable alternative at character 'a'. No viable alternative at character '2'.");

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
				"The ArrowType value 'boxnone' is not semantically correct: The shape 'none' may not be the last shape.");

		// verify that this is the only reported issue
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testWrongGraphBackgroundColor() throws Exception {
		String text = "graph { bgcolor=grsy }";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.BGCOLOR__GC,
				"The color value 'grsy' is not semantically correct: The 'grsy' color is not valid within the 'x11' color scheme.");

		// verify that this is the only reported issues
		Assert.assertEquals(1, validationTestHelper.validate(dotAst).size());
	}

	@Test
	public void testWrongNodeColor() throws Exception {
		String text = "graph { 1[color=\"#fffff\"]}";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(), DotAttributes.COLOR__NE,
				"The value '#fffff' is not a syntactically correct color: Mismatched input '<EOF>' expecting RULE_HEXADECIMAL_DIGIT.");

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
				"The double value '-100.0001' is not semantically correct: Value may not be smaller than -100.0");

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
				"The double value '-100.1' is not semantically correct: Value may not be smaller than -100.0");

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
}