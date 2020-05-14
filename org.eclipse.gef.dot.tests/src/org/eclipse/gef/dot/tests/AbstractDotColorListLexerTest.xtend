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