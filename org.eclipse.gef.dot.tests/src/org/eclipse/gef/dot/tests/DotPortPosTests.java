/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Gerrit Prigge (itemis AG) - initial API and implementation (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.gef.dot.internal.language.DotPortPosInjectorProvider;
import org.eclipse.gef.dot.internal.language.portpos.PortPos;
import org.eclipse.gef.dot.internal.language.portpos.PortposPackage;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

@RunWith(XtextRunner.class)
@InjectWith(DotPortPosInjectorProvider.class)
public class DotPortPosTests {

	@Inject
	private ParseHelper<PortPos> parseHelper;

	@Inject
	private ValidationTestHelper validationTestHelper;

	@Test
	public void compassPointAsName() {
		String text = "w:sw";
		assertNoErrors(text);
	}

	public void noCompassPoint() {
		String text = "hello";
		assertNoErrors(text);
	}

	public void justCompassPoint() {
		String text = "ne";
		assertNoErrors(text);
	}

	@Test
	public void testTwoColons() {
		String text = "port:w:w";
		String errorProneText = "':'";
		assertSyntaxError(PortposPackage.eINSTANCE.getPortPos(), text,
				errorProneText);
	}

	@Test
	public void testInvalidCompassPoint() {
		String text = "king:r";
		String errorProneText = "'r'";
		assertSyntaxError(PortposPackage.eINSTANCE.getPortPos(), text,
				errorProneText);
	}

	private void assertSyntaxError(EClass objectType, String text,
			String errorProneText) {
		PortPos ast = null;
		try {
			ast = parseHelper.parse(text);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(ast);
		validationTestHelper.assertError(ast, objectType,
				"org.eclipse.xtext.diagnostics.Diagnostic.Syntax",
				errorProneText);

		// verify that this is the only reported issue
		Assert.assertEquals(1, validationTestHelper.validate(ast).size());
	}

	private void assertNoErrors(String text) {
		try {
			validationTestHelper.assertNoErrors(parseHelper.parse(text));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
