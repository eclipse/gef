/*******************************************************************************
 * Copyright (c) 2016, 2019 itemis AG and others.
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
package org.eclipse.gef.dot.internal.language.formatting;

import org.eclipse.gef.dot.internal.language.services.DotPointGrammarAccess;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;

import com.google.inject.Inject;

/**
 * This class contains custom formatting declarations.
 */
public class DotPointFormatter extends AbstractDeclarativeFormatter {

	@Inject
	private DotPointGrammarAccess grammarAccess;

	@Override
	protected void configureFormatting(FormattingConfig c) {
		for (Keyword keyword : grammarAccess.findKeywords(",", "!")) {
			c.setNoSpace().before(keyword);
		}
	}
}
