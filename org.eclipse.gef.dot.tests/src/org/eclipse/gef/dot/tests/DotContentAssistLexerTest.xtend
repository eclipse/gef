/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
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
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import com.google.inject.name.Named
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.LexerUIBindings
import org.eclipse.xtext.ide.editor.contentassist.antlr.internal.Lexer
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotContentAssistLexerTest extends AbstractDotLexerTest {

	@Inject @Named(LexerUIBindings.CONTENT_ASSIST) Lexer lexer

	override lexer() {
		lexer
	}

}