/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
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
package org.eclipse.gef.dot.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.gef.dot.internal.language.DotArrowTypeInjectorProvider;
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowType;
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowtypePackage;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

@RunWith(XtextRunner.class)
@InjectWith(DotArrowTypeInjectorProvider.class)
public class DotArrowTypeTests {

	@Inject
	private ParseHelper<ArrowType> parseHelper;

	@Inject
	private ValidationTestHelper validationTestHelper;

	@Test
	public void testInvalidOpenModifier() {
		String text = "ocrow";
		String errorProneText = "o";
		String warningMessage = "The open modifier 'o' may not be combined with primitive shape 'crow'.";
		assertArrowShapeWarning(text, errorProneText, warningMessage);

		text = "lteeveeocrowdot";
		errorProneText = "o";
		warningMessage = "The open modifier 'o' may not be combined with primitive shape 'crow'.";
		assertArrowShapeWarning(text, errorProneText, warningMessage);
	}

	@Test
	public void testInvalidSideModifier() {
		String text = "rdot";
		String errorProneText = "r";
		String warningMessage = "The side modifier 'r' may not be combined with primitive shape 'dot'.";
		assertArrowShapeWarning(text, errorProneText, warningMessage);

		text = "lteeveerdotbox";
		errorProneText = "r";
		warningMessage = "The side modifier 'r' may not be combined with primitive shape 'dot'.";
		assertArrowShapeWarning(text, errorProneText, warningMessage);
	}

	@Test
	public void testDeprecatedArrowShape() {
		String text = "openbox";
		String errorProneText = "open";
		String warningMessage = "The shape 'open' is deprecated.";
		assertDeprecatedArrowShapeWarning(text, errorProneText, warningMessage);

		text = "lteeveeopenbox";
		errorProneText = "open";
		warningMessage = "The shape 'open' is deprecated.";
		assertDeprecatedArrowShapeWarning(text, errorProneText, warningMessage);
	}

	@Test
	public void testNoneIsTheLastArrowShape() {
		String text = "boxnone";
		String errorProneText = "none";
		String warningMessage = "The shape 'none' may not be the last shape.";
		assertArrowShapeWarning(text, errorProneText, warningMessage);

		text = "boxdotveenone";
		errorProneText = "none";
		warningMessage = "The shape 'none' may not be the last shape.";
		assertArrowShapeWarning(text, errorProneText, warningMessage);
	}

	private void assertArrowShapeWarning(String text, String errorProneText,
			String warningMessage) {
		assertArrowTypeWarning(text, errorProneText, warningMessage,
				ArrowtypePackage.eINSTANCE.getArrowShape());
	}

	private void assertDeprecatedArrowShapeWarning(String text,
			String errorProneText, String warningMessage) {
		assertArrowTypeWarning(text, errorProneText, warningMessage,
				ArrowtypePackage.eINSTANCE.getDeprecatedArrowShape());
	}

	private void assertArrowTypeWarning(String text, String errorProneText,
			String warningMessage, EClass objectType) {
		ArrowType ast = null;
		try {
			ast = parseHelper.parse(text);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(ast);
		int offset = text.indexOf(errorProneText);
		int length = errorProneText.length();

		validationTestHelper.assertWarning(ast, objectType, null, offset,
				length, warningMessage);

		// verify that this is the only reported issue
		Assert.assertEquals(1, validationTestHelper.validate(ast).size());
	}
}
