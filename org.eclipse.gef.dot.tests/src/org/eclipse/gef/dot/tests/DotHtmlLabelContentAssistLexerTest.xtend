/*******************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import com.google.inject.name.Named
import org.eclipse.gef.dot.tests.ui.DotHtmlLabelUiInjectorProvider
import org.eclipse.xtext.ide.LexerIdeBindings
import org.eclipse.xtext.ide.editor.contentassist.antlr.internal.Lexer
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(DotHtmlLabelUiInjectorProvider)
class DotHtmlLabelContentAssistLexerTest extends AbstractDotHtmlLabelLexerTest {

	@Inject @Named(LexerIdeBindings.CONTENT_ASSIST) Lexer lexer

	override lexer() {
		lexer
	}

}