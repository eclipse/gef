/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.formatting;

import org.eclipse.gef.dot.internal.language.services.DotHtmlLabelGrammarAccess;
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;

import com.google.inject.Inject;

/**
 * This class contains custom formatting declarations.
 */
public class DotHtmlLabelFormatter extends AbstractDeclarativeFormatter {

	@Inject
	private DotHtmlLabelGrammarAccess grammarAccess;

	@Override
	protected void configureFormatting(FormattingConfig c) {
		// TODO: preserve newlines around comments
		// c.setLinewrap(0, 1, 2).before(grammarAccess.getHTML_COMMENTRule());
	}
}