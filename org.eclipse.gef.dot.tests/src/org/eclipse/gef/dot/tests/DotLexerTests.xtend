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

import javax.inject.Inject
import javax.inject.Named
import org.eclipse.gef.dot.internal.language.DotInjectorProvider
import org.eclipse.gef.dot.internal.language.parser.antlr.lexer.CustomInternalDotLexer
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.parser.antlr.Lexer
import org.eclipse.xtext.parser.antlr.LexerBindings
import org.junit.Test
import org.junit.runner.RunWith

import static extension org.eclipse.gef.dot.tests.DotTestUtils.lex
import static extension org.junit.Assert.*

/**
 * Test cases for the {@link CustomInternalDotLexer} class.
 */
@RunWith(XtextRunner)
@InjectWith(DotInjectorProvider)
class DotLexerTests {

	@Inject @Named(LexerBindings.RUNTIME) Lexer lexer

	@Test
	def void testLexing_mSubgraph(){
		"subgraph".assertLexing("Subgraph 'subgraph'")
		"Subgraph".assertLexing("Subgraph 'Subgraph'")
		"SUBGRAPH".assertLexing("Subgraph 'SUBGRAPH'")
	}
	
	@Test
	def void testLexing_mDigraph(){
		"digraph".assertLexing("Digraph 'digraph'")
		"Digraph".assertLexing("Digraph 'Digraph'")
		"DIGRAPH".assertLexing("Digraph 'DIGRAPH'")
	}

	@Test
	def void testLexing_mStrict(){
		"strict".assertLexing("Strict 'strict'")
		"Strict".assertLexing("Strict 'Strict'")
		"STRICT".assertLexing("Strict 'STRICT'")
	}

	@Test
	def void testLexing_mGraph(){
		"graph".assertLexing("Graph 'graph'")
		"Graph".assertLexing("Graph 'Graph'")
		"GRAPH".assertLexing("Graph 'GRAPH'")
	}
	
	@Test
	def void testLexing_mEdge(){
		"edge".assertLexing("Edge 'edge'")
		"Edge".assertLexing("Edge 'Edge'")
		"EDGE".assertLexing("Edge 'EDGE'")
	}
	
	@Test
	def void testLexing_mNode(){
		"node".assertLexing("Node 'node'")
		"Node".assertLexing("Node 'Node'")
		"NODE".assertLexing("Node 'NODE'")
	}
	
	@Test
	def void testLexing_mHyphenMinusHyphenMinus(){
		"--".assertLexing("HyphenMinusHyphenMinus '--'")
	}
	
	@Test
	def void testLexing_mHyphenMinusGreaterThanSign(){
		"->".assertLexing("HyphenMinusGreaterThanSign '->'")
	}
	
	@Test
	def void testLexing_mComma(){
		",".assertLexing("Comma ','")
	}
	
	@Test
	def void testLexing_mColon(){
		":".assertLexing("Colon ':'")
	}
	
	@Test
	def void testLexing_mSemicolon(){
		";".assertLexing("Semicolon ';'")
	}
	
	@Test
	def void testLexing_mEqualsSign(){
		"=".assertLexing("EqualsSign '='")
	}
	
	@Test
	def void testLexing_mLeftSquareBracket(){
		"[".assertLexing("LeftSquareBracket '['")
	}
	
	@Test
	def void testLexing_mRightSquareBracket(){
		"]".assertLexing("RightSquareBracket ']'")
	}
	
	@Test
	def void testLexing_mLeftCurlyBracket(){
		"{".assertLexing("LeftCurlyBracket '{'")
	}
	
	@Test
	def void testLexing_mRightCurlyBracket(){
		"}".assertLexing("RightCurlyBracket '}'")
	}
	
	@Test
	def void testLexing_mRULE_NUMERAL(){
		"0"		.assertLexing("RULE_NUMERAL '0'"	)
		"1"		.assertLexing("RULE_NUMERAL '1'"	)
		"-1"	.assertLexing("RULE_NUMERAL '-1'"	)
		"0.5"	.assertLexing("RULE_NUMERAL '0.5'"	)
		"-0.5"	.assertLexing("RULE_NUMERAL '-0.5'"	)
		".6"	.assertLexing("RULE_NUMERAL '.6'"	)
		"12"	.assertLexing("RULE_NUMERAL '12'"	)
	}
	
	@Test
	def void testLexing_mRULE_COMPASS_PT(){
		"n"	.assertLexing("RULE_COMPASS_PT 'n'"	)
		"ne".assertLexing("RULE_COMPASS_PT 'ne'")
		"e"	.assertLexing("RULE_COMPASS_PT 'e'"	)
		"se".assertLexing("RULE_COMPASS_PT 'se'")
		"s"	.assertLexing("RULE_COMPASS_PT 's'"	)
		"sw".assertLexing("RULE_COMPASS_PT 'sw'")
		"w"	.assertLexing("RULE_COMPASS_PT 'w'"	)
		"nw".assertLexing("RULE_COMPASS_PT 'nw'")
		"c"	.assertLexing("RULE_COMPASS_PT 'c'"	)
		"_"	.assertLexing("RULE_COMPASS_PT '_'"	)
	}
	
	@Test
	def void testLexing_mRULE_STRING(){
		"foo"	.assertLexing("RULE_STRING 'foo'"	)
		"ä"		.assertLexing("RULE_STRING 'ä'"		)
		"ß"		.assertLexing("RULE_STRING 'ß'"		)
		"Foo"	.assertLexing("RULE_STRING 'Foo'"	)
		"FOO"	.assertLexing("RULE_STRING 'FOO'"	)
		"_foo"	.assertLexing("RULE_STRING '_foo'"	)
		"foo1"	.assertLexing("RULE_STRING 'foo1'"	)
	}
	
	@Test
	def void testLexing_mRULE_QUOTED_STRING(){
		'''""'''.assertLexing('''
			RULE_QUOTED_STRING '""'
		''')

		'''"foo"'''.assertLexing('''
			RULE_QUOTED_STRING '"foo"'
		''')
		
		'''"foo\"bar\"baz"'''.assertLexing('''
			RULE_QUOTED_STRING '"foo\"bar\"baz"'
		''')
		
		'''"foo\bar\baz"'''.assertLexing('''
			RULE_QUOTED_STRING '"foo\bar\baz"'
		''')
		
		'''"!"'''.assertLexing('''
			RULE_QUOTED_STRING '"!"'
		''')
		
		'''"\\"!\\"'''.assertLexing('''
			RULE_QUOTED_STRING '"\\"!\\"'
		''')
	}
	
	@Test
	def void testLexing_mRULE_HTML_STRING(){
		'''<>'''					.assertLexing("RULE_HTML_STRING '<>'"						)
		'''<	>'''				.assertLexing("RULE_HTML_STRING '<	>'"						)
		'''<<B>Bold Label</B>>'''	.assertLexing("RULE_HTML_STRING '<<B>Bold Label</B>>'"		)
		'''<<B>Bold Label</B>>'''	.assertLexing("RULE_HTML_STRING '<<B>Bold Label</B>>'"		)
		'''<<BR ALIGN="LEFT"/>>'''	.assertLexing("RULE_HTML_STRING '<<BR ALIGN=\"LEFT\"/>>'"	)
		'''<<BR ALIGN='LEFT'/>>'''	.assertLexing("RULE_HTML_STRING '<<BR ALIGN=\'LEFT\'/>>'"	)
		'''<
				<font color="green">
					<table>
						<tr>
							<td>text</td>
						</tr>
					</table>
				</font>
			>'''
		.assertLexing('''
			RULE_HTML_STRING '<
							<font color="green">
								<table>
									<tr>
										<td>text</td>
									</tr>
								</table>
							</font>
						>'
		''')
		
		'''< <!-- This is a bold label --> <B>Bold Label</B>>'''
		.assertLexing(
		'''RULE_HTML_STRING '< <!-- This is a bold label --> <B>Bold Label</B>>'
		''')
	
	}
	
	@Test
	def void testLexing_mRULE_ML_COMMENT(){
		'''/*
		 	* This is a C++-style multi line comment.
		 	*/'''.assertLexing('''
		 	RULE_ML_COMMENT '/*
		 			 	* This is a C++-style multi line comment.
		 			 	*/'
		 ''')
			
	}
	
	@Test
	def void testLexing_mRULE_SL_COMMENT(){
		'''// This is a C++-style single line comment.'''
		.assertLexing('''
			RULE_SL_COMMENT '// This is a C++-style single line comment.'
		''')
		
		'''# This is considered as a line output from C-preprocessor and discarded.'''
		.assertLexing('''
			RULE_SL_COMMENT '# This is considered as a line output from C-preprocessor and discarded.'
		''')
		
		'''
			# This is considered as a line output from C-preprocessor and discarded.
		'''
		.assertLexing('''
			RULE_SL_COMMENT '# This is considered as a line output from C-preprocessor and discarded.
			'
		''')
	}
	
	@Test
	def void testLexing_mRULE_WS(){
		" "		.assertLexing("RULE_WS ' '"		)
		"	"	.assertLexing("RULE_WS '	'"	)
		'''

		'''.assertLexing('''
		RULE_WS '
		'
		''')
	}

	private def assertLexing(CharSequence modelAsText, CharSequence expected) {
		val antlrTokenFilePath = "../org.eclipse.gef.dot/src-gen/org/eclipse/gef/dot/internal/language/parser/antlr/lexer/CustomInternalDotLexer.tokens";
		val actual = lexer.lex(antlrTokenFilePath, modelAsText)
		expected.toString.trim.assertEquals(actual.toString.trim)
	}
}