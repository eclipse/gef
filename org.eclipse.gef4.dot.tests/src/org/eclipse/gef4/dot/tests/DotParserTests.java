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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.eclipse.gef4.dot.internal.parser.DotInjectorProvider;
import org.eclipse.gef4.dot.internal.parser.dot.DotAst;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

@RunWith(XtextRunner.class)
@InjectWith(DotInjectorProvider.class)
public class DotParserTests {

	@Inject
	private ParseHelper<DotAst> parserHelper;

	@Inject
	private ValidationTestHelper validationTestHelper;

	@Test
	@Ignore
	// TODO: Enable when remaining problems of #477980 have been fixed
	public void testArrowShapesSingle() {
		testFile("arrowshapes_single.dot");
	}

	@Test
	@Ignore
	// TODO: Enable when remaining problems of #477980 have been fixed
	public void testArrowShapesMultiple() {
		testFile("arrowshapes_multiple.dot");
	}

	@Test
	public void testArrowShapesDeprecated() {
		testFile("arrowshapes_deprecated.dot");
	}

	private void testFile(String path) {
		String newline = System.getProperty("line.separator");
		String content = "";
		try {
			Scanner scanner = new Scanner(
					new File(DotTestUtils.RESOURCES_TESTS + path));
			while (scanner.hasNextLine()) {
				content += scanner.nextLine() + newline;
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			DotAst dotAst = parserHelper.parse(content);
			assertNotNull(dotAst);
			validationTestHelper.assertNoErrors(dotAst);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
