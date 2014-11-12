/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.internal.dot.parser.ui.syntaxcoloring;

import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultAntlrTokenToAttributeIdMapper;

/**
 * A semantic highlighter that takes care of handling DOT lexer tokens properly.
 * 
 * @author anyssen
 *
 */
public class DotAntlrTokenToAttributeIdMapper extends
		DefaultAntlrTokenToAttributeIdMapper {

	@Override
	protected String calculateId(String tokenName, int tokenType) {
		// ensure CompassPt constants are lexically highlighted as STRING tokens
		if ("RULE_STRING".equals(tokenName) || tokenName.equals("'n'")
				|| tokenName.equals("'ne'") || tokenName.equals("'e'")
				|| tokenName.equals("'se'") || tokenName.equals("'s'")
				|| tokenName.equals("'sw'") || tokenName.equals("'w'")
				|| tokenName.equals("'nw'") || tokenName.equals("'c'")
				|| tokenName.equals("'_'")) {
			return DotHighlightingConfiguration.STRING_ID;
		} else if ("RULE_NUMERAL".equals(tokenName)) {
			return DotHighlightingConfiguration.NUMERAL_ID;
		} else if ("RULE_QUOTED_STRING".equals(tokenName)) {
			return DotHighlightingConfiguration.QUOTED_STRING_ID;
		} else if ("RULE_ML_COMMENT".equals(tokenName)
				|| "RULE_SL_COMMENT".equals(tokenName)) {
			return DotHighlightingConfiguration.COMMENT_ID;
		}
		return super.calculateId(tokenName, tokenType);
	}
}
