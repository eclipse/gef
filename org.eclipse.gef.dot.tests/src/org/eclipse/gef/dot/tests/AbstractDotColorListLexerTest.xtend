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

abstract class AbstractDotColorListLexerTest extends AbstractLexerTest {

	@Test def one_color_value_with_weight() {
		"#3030FF;1".assertLexing('''
			'#' '#'
			RULE_HEXADECIMAL_DIGIT '3'
			RULE_HEXADECIMAL_DIGIT '0'
			RULE_HEXADECIMAL_DIGIT '3'
			RULE_HEXADECIMAL_DIGIT '0'
			RULE_HEXADECIMAL_DIGIT 'F'
			RULE_HEXADECIMAL_DIGIT 'F'
			';' ';'
			RULE_COLOR_NUMBER '1'
		''')
	}

}