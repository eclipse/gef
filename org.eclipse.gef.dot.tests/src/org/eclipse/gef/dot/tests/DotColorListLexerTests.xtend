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

import javax.inject.Inject
import javax.inject.Named
import org.eclipse.gef.dot.internal.language.DotColorListInjectorProvider
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.parser.antlr.Lexer
import org.eclipse.xtext.parser.antlr.LexerBindings
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(DotColorListInjectorProvider)
class DotColorListLexerTests extends AbstractDotColorListLexerTest {

	@Inject @Named(LexerBindings.RUNTIME) Lexer lexer

	override lexer() {
		lexer
	}

}