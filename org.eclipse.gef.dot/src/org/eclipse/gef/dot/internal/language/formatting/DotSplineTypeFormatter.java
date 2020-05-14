/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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

import org.eclipse.gef.dot.internal.language.services.DotSplineTypeGrammarAccess;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;

import com.google.inject.Inject;

/**
 * This class contains custom formatting declarations.
 */
public class DotSplineTypeFormatter extends AbstractDeclarativeFormatter {

	@Inject
	private DotSplineTypeGrammarAccess grammarAccess;

	@Override
	protected void configureFormatting(FormattingConfig c) {
		for (Keyword keyword : grammarAccess.findKeywords(",")) {
			c.setNoSpace().around(keyword);
		}
	}
}