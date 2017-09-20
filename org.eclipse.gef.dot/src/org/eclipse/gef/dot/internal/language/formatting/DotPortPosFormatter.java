/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Gerrit Prigge (itemis AG) - initial API and implementation (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.formatting;

import org.eclipse.gef.dot.internal.language.services.DotPortPosGrammarAccess;
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;

import com.google.inject.Inject;

/**
 * This class contains custom formatting declarations.
 * 
 * See
 * https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#formatting
 * on how and when to use it.
 * 
 * Also see {@link org.eclipse.xtext.xtext.XtextFormatter} as an example
 */
public class DotPortPosFormatter extends AbstractDeclarativeFormatter {

	@Inject
	private DotPortPosGrammarAccess grammarAccess;

	@Override
	protected void configureFormatting(FormattingConfig c) {
		// It's usually a good idea to activate the following three statements.
		// They will add and preserve newlines around comments
		// c.setLinewrap(0, 1, 2).before(grammarAccess.getSL_COMMENTRule());
		// c.setLinewrap(0, 1, 2).before(grammarAccess.getML_COMMENTRule());
		// c.setLinewrap(0, 1, 1).after(grammarAccess.getML_COMMENTRule());
	}
}
