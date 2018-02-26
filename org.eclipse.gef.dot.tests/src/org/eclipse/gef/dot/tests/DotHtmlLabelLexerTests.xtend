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
import org.eclipse.gef.dot.internal.language.DotHtmlLabelInjectorProvider
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.parser.antlr.Lexer
import org.eclipse.xtext.parser.antlr.LexerBindings
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.dot.tests.DotTestUtils.lex
import static extension org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(DotHtmlLabelInjectorProvider)
class DotHtmlLabelLexerTests {

	@Inject @Named(LexerBindings.RUNTIME) Lexer lexer

	@Test
	def void testLexing01(){
		'''
			<TABLE>
			</TABLE>
		'''
		.assertLexing('''
			RULE_TAG_START '<'
			RULE_ID 'TABLE'
			RULE_TAG_END '>'
			RULE_TEXT '
			'
			RULE_TAG_START_CLOSE '</'
			RULE_ID 'TABLE'
			RULE_TAG_END '>'
			RULE_TEXT '
			'
		''')
	}

	@Test
	def void testLexing02(){
		'''
			<font>text</font>
		'''
		.assertLexing('''
			RULE_TAG_START '<'
			RULE_ID 'font'
			RULE_TAG_END '>'
			RULE_TEXT 'text'
			RULE_TAG_START_CLOSE '</'
			RULE_ID 'font'
			RULE_TAG_END '>'
			RULE_TEXT '
			'
		''')
	}

	@Test(timeout = 2000)
	def void testLexing03(){
		'''
			<font><text</font>
		'''
		.assertLexing('''
			RULE_TAG_START '<'
			RULE_ID 'font'
			RULE_TAG_END '>'
			RULE_TAG_START '<'
			RULE_ID 'text'
			RULE_TAG_START_CLOSE '</'
			RULE_ID 'font'
			RULE_TAG_END '>'
			RULE_TEXT '
			'
		''')
	}
	
	@Test(timeout = 2000)
	def void testLexing04(){
		'''
		<TABLE BORDER="0" CELLBORDER="1" CELLSPACING="0" bgcolor="blue">
			<TR>
				<TD><font><fonttext2</font></TD>
			</TR>
		</TABLE>
		'''
		.assertLexing('''
			RULE_TAG_START '<'
			RULE_ID 'TABLE'
			RULE_WS ' '
			RULE_ID 'BORDER'
			RULE_ASSIGN '='
			RULE_ATTR_VALUE '"0"'
			RULE_WS ' '
			RULE_ID 'CELLBORDER'
			RULE_ASSIGN '='
			RULE_ATTR_VALUE '"1"'
			RULE_WS ' '
			RULE_ID 'CELLSPACING'
			RULE_ASSIGN '='
			RULE_ATTR_VALUE '"0"'
			RULE_WS ' '
			RULE_ID 'bgcolor'
			RULE_ASSIGN '='
			RULE_ATTR_VALUE '"blue"'
			RULE_TAG_END '>'
			RULE_TEXT '
				'
			RULE_TAG_START '<'
			RULE_ID 'TR'
			RULE_TAG_END '>'
			RULE_TEXT '
					'
			RULE_TAG_START '<'
			RULE_ID 'TD'
			RULE_TAG_END '>'
			RULE_TAG_START '<'
			RULE_ID 'font'
			RULE_TAG_END '>'
			RULE_TAG_START '<'
			RULE_ID 'fonttext2'
			RULE_TAG_START_CLOSE '</'
			RULE_ID 'font'
			RULE_TAG_END '>'
			RULE_TAG_START_CLOSE '</'
			RULE_ID 'TD'
			RULE_TAG_END '>'
			RULE_TEXT '
				'
			RULE_TAG_START_CLOSE '</'
			RULE_ID 'TR'
			RULE_TAG_END '>'
			RULE_TEXT '
			'
			RULE_TAG_START_CLOSE '</'
			RULE_ID 'TABLE'
			RULE_TAG_END '>'
			RULE_TEXT '
			'
		''')
	}

	
	@Test(timeout = 2000)
	def void testLexing06(){
		'''<a b="c"/>'''
		.assertLexing('''
			RULE_TAG_START '<'
			RULE_ID 'a'
			RULE_WS ' '
			RULE_ID 'b'
			RULE_ASSIGN '='
			RULE_ATTR_VALUE '"c"'
			RULE_TAG_END_CLOSE '/>'
		''')
	}

	@Test(timeout = 2000)
	def void testLexing07(){
		'''<BR ALIGN="LEFT"/>'''
		.assertLexing('''
			RULE_TAG_START '<'
			RULE_ID 'BR'
			RULE_WS ' '
			RULE_ID 'ALIGN'
			RULE_ASSIGN '='
			RULE_ATTR_VALUE '"LEFT"'
			RULE_TAG_END_CLOSE '/>'
		''')
	}
	
	@Test(timeout = 2000)
	def void testLexing08() {
		'''<a b="'''
		.assertLexing(
		'''
		RULE_TAG_START '<'
		RULE_ID 'a'
		RULE_WS ' '
		RULE_ID 'b'
		RULE_ASSIGN '='
		0 '"'
		''')
	}
	
	@Test(timeout = 2000)
	def void testLexing09() {
		'''<TABLE ALIGN="'''
		.assertLexing(
		'''
		RULE_TAG_START '<'
		RULE_ID 'TABLE'
		RULE_WS ' '
		RULE_ID 'ALIGN'
		RULE_ASSIGN '='
		0 '"'
		''')
	}
	
	@Test(timeout = 2000)
	def void testLexing10() {
		'''<TABLE ALIGN=""></TABLE>'''
		.assertLexing(
		'''
		RULE_TAG_START '<'
		RULE_ID 'TABLE'
		RULE_WS ' '
		RULE_ID 'ALIGN'
		RULE_ASSIGN '='
		RULE_ATTR_VALUE '""'
		RULE_TAG_END '>'
		RULE_TAG_START_CLOSE '</'
		RULE_ID 'TABLE'
		RULE_TAG_END '>'
		''')
	}

	private def assertLexing(CharSequence modelAsText, CharSequence expected) {
		val tokenFilePath = "../org.eclipse.gef.dot/src-gen/org/eclipse/gef/dot/internal/language/parser/antlr/lexer/CustomInternalDotHtmlLabelLexer.tokens";
		val actual = modelAsText.lex(lexer, tokenFilePath)
		expected.toString.trim.assertEquals(actual.toString.trim)
	}
}