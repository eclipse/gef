/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.formatting;

import org.eclipse.gef.dot.internal.language.services.DotColorListGrammarAccess;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;

import com.google.inject.Inject;

/**
 * This class contains custom formatting declarations.
 */
public class DotColorListFormatter extends AbstractDeclarativeFormatter {

	@Inject
	private DotColorListGrammarAccess grammarAccess;

	@Override
	protected void configureFormatting(FormattingConfig c) {
		/**
		 * Configure the dot colorList formatter not set an extra white space
		 * after the '#' symbol and after the hex digits. It should produce
		 * '#abcdef' instead of '# ab cd ef'.
		 */
		c.setNoSpace().around(grammarAccess.getHexRule());

		/**
		 * Configure the dot colorList formatter not set an extra white space
		 * before and after the '/' symbol. It should produce '/svg/white'
		 * instead of '/ svg / white'.
		 */
		for (Keyword keyWord : grammarAccess.findKeywords("/")) {
			c.setNoSpace().around(keyWord);
		}
	}
}
