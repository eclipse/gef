/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.internal.parser.ui.syntaxcoloring;

import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultAntlrTokenToAttributeIdMapper;

/**
 * A semantic highlighter that takes care of handling DOT lexer tokens properly.
 * 
 * @author anyssen
 *
 */
public class DotAntlrTokenToAttributeIdMapper
		extends DefaultAntlrTokenToAttributeIdMapper {

	@Override
	protected String calculateId(String tokenName, int tokenType) {
		// ensure CompassPt constants are lexically highlighted as STRING tokens
		if ("RULE_STRING".equals(tokenName) //$NON-NLS-1$
				|| "RULE_COMPASS_PT".equals(tokenName)) { //$NON-NLS-1$
			return DotHighlightingConfiguration.STRING_ID;
		} else if ("RULE_NUMERAL".equals(tokenName)) { //$NON-NLS-1$
			return DotHighlightingConfiguration.NUMERAL_ID;
		} else if ("RULE_QUOTED_STRING".equals(tokenName)) { //$NON-NLS-1$
			return DotHighlightingConfiguration.QUOTED_STRING_ID;
		} else if ("RULE_ML_COMMENT".equals(tokenName) //$NON-NLS-1$
				|| "RULE_SL_COMMENT".equals(tokenName)) { //$NON-NLS-1$
			return DotHighlightingConfiguration.COMMENT_ID;
		}
		return super.calculateId(tokenName, tokenType);
	}
}
