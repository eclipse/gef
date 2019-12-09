/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #321775)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import com.google.inject.name.Named
import org.eclipse.gef.dot.tests.DotHtmlLabelInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.parser.antlr.Lexer
import org.eclipse.xtext.parser.antlr.LexerBindings
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(DotHtmlLabelInjectorProvider)
class DotHtmlLabelLexerTest extends AbstractDotHtmlLabelLexerTest {

	@Inject @Named(LexerBindings.RUNTIME) Lexer lexer

	override lexer() {
		lexer
	}

}