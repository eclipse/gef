/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy   (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.eclipse.gef.dot.internal.language.DotStyleInjectorProvider;
import org.eclipse.gef.dot.internal.language.style.Style;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

@RunWith(XtextRunner.class)
@InjectWith(DotStyleInjectorProvider.class)
public class DotStyleTests {

	@Inject
	private ParseHelper<Style> parseHelper;

	@Inject
	private ValidationTestHelper validationTestHelper;

	@Test
	public void test_valid_style() {
		parse("bold");
	}

	private Style parse(String text) {
		try {
			Style ast = parseHelper.parse(text);
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
