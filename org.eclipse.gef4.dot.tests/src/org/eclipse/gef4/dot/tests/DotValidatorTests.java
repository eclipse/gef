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
 *
 *******************************************************************************/

package org.eclipse.gef4.dot.tests;

import org.eclipse.gef4.dot.internal.parser.DotInjectorProvider;
import org.eclipse.gef4.dot.internal.parser.dot.DotAst;
import org.eclipse.gef4.dot.internal.parser.dot.DotPackage;
import org.eclipse.gef4.dot.internal.parser.validation.DotJavaValidator;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
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
	public void testWrongArrowType() throws Exception {
		String text = "digraph testGraph { 1->2[arrowhead=fooBar] }";

		DotAst dotAst = parserHelper.parse(text);

		validationTestHelper.assertError(dotAst,
				DotPackage.eINSTANCE.getAttribute(),
				DotJavaValidator.ATTRIBUTE__INVALID_VALUE__ARROW_TYPE, 35, 6,
				"The value 'fooBar' is not a syntactically correct arrow type: No viable alternative at character 'f'. No viable alternative at input 'o'. No viable alternative at character 'B'. No viable alternative at character 'a'. No viable alternative at input '<EOF>'.");
	}
}
