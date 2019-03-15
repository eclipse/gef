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

import org.junit.Test

abstract class AbstractDotHtmlLabelLexerTest extends AbstractLexerTest {

	@Test def lexing01() {
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
		''')
	}

	@Test def lexing02() {
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
		''')
	}

	@Test def lexing03() {
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
		''')
	}

	@Test def lexing04() {
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
		''')
	}

	@Test def lexing05() {
		'''"text"'''
		.assertLexing('''
			RULE_TEXT '"text"'
		''')
	}

	@Test def lexing06() {
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

	@Test def lexing07() {
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

	@Test def lexing08() {
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

	@Test def lexing09() {
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

	@Test def lexing10() {
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
}