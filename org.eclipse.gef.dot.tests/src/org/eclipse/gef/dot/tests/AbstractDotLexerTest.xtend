/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
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

import org.junit.Test

abstract class AbstractDotLexerTest extends AbstractLexerTest {

	@Test def lexing_mSubgraph() {
		"subgraph".assertLexing("'subgraph' 'subgraph'")
		"Subgraph".assertLexing("'subgraph' 'Subgraph'")
		"SUBGRAPH".assertLexing("'subgraph' 'SUBGRAPH'")
	}

	@Test def lexing_mDigraph() {
		"digraph".assertLexing("'digraph' 'digraph'")
		"Digraph".assertLexing("'digraph' 'Digraph'")
		"DIGRAPH".assertLexing("'digraph' 'DIGRAPH'")
	}

	@Test def lexing_mStrict() {
		"strict".assertLexing("'strict' 'strict'")
		"Strict".assertLexing("'strict' 'Strict'")
		"STRICT".assertLexing("'strict' 'STRICT'")
	}

	@Test def lexing_mGraph() {
		"graph".assertLexing("'graph' 'graph'")
		"Graph".assertLexing("'graph' 'Graph'")
		"GRAPH".assertLexing("'graph' 'GRAPH'")
	}

	@Test def lexing_mEdge() {
		"edge".assertLexing("'edge' 'edge'")
		"Edge".assertLexing("'edge' 'Edge'")
		"EDGE".assertLexing("'edge' 'EDGE'")
	}

	@Test def lexing_mNode() {
		"node".assertLexing("'node' 'node'")
		"Node".assertLexing("'node' 'Node'")
		"NODE".assertLexing("'node' 'NODE'")
	}

	@Test def lexing_mHyphenMinusHyphenMinus() {
		"--".assertLexing("'--' '--'")
	}

	@Test def lexing_mHyphenMinusGreaterThanSign() {
		"->".assertLexing("'->' '->'")
	}

	@Test def lexing_mComma() {
		",".assertLexing("',' ','")
	}

	@Test def lexing_mColon() {
		":".assertLexing("':' ':'")
	}

	@Test def lexing_mSemicolon() {
		";".assertLexing("';' ';'")
	}

	@Test def lexing_mEqualsSign() {
		"=".assertLexing("'=' '='")
	}

	@Test def lexing_mLeftSquareBracket() {
		"[".assertLexing("'[' '['")
	}

	@Test def lexing_mRightSquareBracket() {
		"]".assertLexing("']' ']'")
	}

	@Test def lexing_mLeftCurlyBracket() {
		"{".assertLexing("'{' '{'")
	}

	@Test def lexing_mRightCurlyBracket() {
		"}".assertLexing("'}' '}'")
	}

	@Test def lexing_mRULE_NUMERAL() {
		"0"		.assertLexing("RULE_NUMERAL '0'"	)
		"1"		.assertLexing("RULE_NUMERAL '1'"	)
		"-1"	.assertLexing("RULE_NUMERAL '-1'"	)
		"0.5"	.assertLexing("RULE_NUMERAL '0.5'"	)
		"-0.5"	.assertLexing("RULE_NUMERAL '-0.5'"	)
		".6"	.assertLexing("RULE_NUMERAL '.6'"	)
		"12"	.assertLexing("RULE_NUMERAL '12'"	)
	}

	@Test def lexing_mRULE_COMPASS_PT() {
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

	@Test def lexing_mRULE_STRING() {
		"foo"	.assertLexing("RULE_STRING 'foo'"	)
		"ä"		.assertLexing("RULE_STRING 'ä'"		)
		"ß"		.assertLexing("RULE_STRING 'ß'"		)
		"Foo"	.assertLexing("RULE_STRING 'Foo'"	)
		"FOO"	.assertLexing("RULE_STRING 'FOO'"	)
		"_foo"	.assertLexing("RULE_STRING '_foo'"	)
		"foo1"	.assertLexing("RULE_STRING 'foo1'"	)
	}

	@Test def lexing_mRULE_QUOTED_STRING() {
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

	@Test def lexing_mRULE_HTML_STRING() {
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

	@Test def lexing_mRULE_ML_COMMENT() {
		'''/*
		 	* This is a C++-style multi line comment.
		 	*/'''.assertLexing('''
		 	RULE_ML_COMMENT '/*
		 			 	* This is a C++-style multi line comment.
		 			 	*/'
		 ''')
	}

	@Test def lexing_mRULE_SL_COMMENT() {
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
			RULE_SL_COMMENT '# This is considered as a line output from C-preprocessor and discarded.'
		''')
	}
}