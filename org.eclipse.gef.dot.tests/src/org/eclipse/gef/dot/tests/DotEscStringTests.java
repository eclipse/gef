/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.dot.internal.language.DotEscStringInjectorProvider;
import org.eclipse.gef.dot.internal.language.escstring.EscString;
import org.eclipse.gef.dot.internal.language.escstring.Justification;
import org.eclipse.gef.dot.internal.language.escstring.JustifiedText;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

@RunWith(XtextRunner.class)
@InjectWith(DotEscStringInjectorProvider.class)
public class DotEscStringTests {

	@Inject
	private ParseHelper<EscString> parseHelper;

	@Inject
	private ValidationTestHelper validationTestHelper;

	@Test
	public void test_empty() throws Throwable {
		String text = "";
		EscString ast = parse(text);
		EList<JustifiedText> segments = ast.getLines();
		assertEquals(0, segments.size());
	}

	@Test
	public void test_text_with_escape_sequences() throws Throwable {
		String text = "Some text containing \\arbitrary \\escape \\sequences.";
		EscString ast = parse(text);
		EList<JustifiedText> segments = ast.getLines();
		// check if the text was parsed as a single segment
		assertEquals(1, segments.size());
		// check default justification (centered)
		assertEquals(Justification.CENTERED,
				segments.get(0).getJustification());
		// check if the whole text was parsed
		assertEquals(text, segments.get(0).getText());
	}

	@Test
	public void test_justifications() throws Throwable {
		String text = "center-justified\\nleft-justified\\lright-justified\\rdefault-justified";
		EscString ast = parse(text);
		EList<JustifiedText> segments = ast.getLines();
		// check if parsed as four segments
		assertEquals(4, segments.size());
		// check individual segments
		assertEquals("center-justified", segments.get(0).getText());
		assertEquals(Justification.CENTERED,
				segments.get(0).getJustification());
		assertEquals("left-justified", segments.get(1).getText());
		assertEquals(Justification.LEFT, segments.get(1).getJustification());
		assertEquals("right-justified", segments.get(2).getText());
		assertEquals(Justification.RIGHT, segments.get(2).getJustification());
		assertEquals("default-justified", segments.get(3).getText());
		assertEquals(Justification.CENTERED,
				segments.get(3).getJustification());
	}

	private EscString parse(String text) {
		try {
			EscString ast = parseHelper.parse(text);
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
