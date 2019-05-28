/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation (bug #513196)
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.htmllabel;

import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;
import org.eclipse.xtext.conversion.impl.IDValueConverter;

/**
 * A custom {@link IDValueConverter} is needed to be able to serialize ID values
 * properly. Use case: wenn changing the invalid name of tag by a quickfix, the
 * new value 'b' is considered as TEXT according to the custom lexer (in that
 * case the tagMode is false, and the erminal rule ID only works if tagMode is
 * true).
 */
public class DotHtmlLabelIDValueConverter extends IDValueConverter {

	@Override
	protected void assertTokens(String value, TokenSource tokenSource,
			String escapedString) {
		if (tokenSource == null)
			return;
		Token token = tokenSource.nextToken();

		// customization start
		if ("ID".equals(getRuleName()) && "TEXT".equals(getRuleName(token))) {
			return;
		}
		// customization end

		if (!escapedString.equals(token.getText())) {
			throw createTokenContentMismatchException(value, escapedString,
					token);
		}
		if (!getRuleName().toUpperCase().equals(getRuleName(token))) {
			throw createTokenTypeMismatchException(value, escapedString, token);
		}
		String reparsedValue = toValue(token.getText(), null);
		if (value != reparsedValue && !value.equals(reparsedValue)) {
			throw createTokenContentMismatchException(value, escapedString,
					token);
		}
	}

	@Override
	public String toString(String value) {
		return value;
	}
}
