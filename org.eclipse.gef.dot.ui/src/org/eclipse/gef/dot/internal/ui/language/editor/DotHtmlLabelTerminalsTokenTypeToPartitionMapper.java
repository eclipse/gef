/*******************************************************************************
 * Copyright (c) 2017, 2018 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.editor;

import org.eclipse.xtext.ui.editor.model.TerminalsTokenTypeToPartitionMapper;

public class DotHtmlLabelTerminalsTokenTypeToPartitionMapper
		extends TerminalsTokenTypeToPartitionMapper {

	@Override
	protected String calculateId(String tokenName, int tokenType) {
		switch (tokenName) {
		case "RULE_HTML_COMMENT": //$NON-NLS-1$
			/**
			 * assign the COMMENT_PARTITION to the HTML_COMMENT rule, otherwise,
			 * the multi-line comment folding does not work
			 */
			return COMMENT_PARTITION;
		case "RULE_ATTR_VALUE": //$NON-NLS-1$
			/**
			 * assign the STRING_LITERAL_PARTITION to the ATTR_VALUE rule,
			 * otherwise, the double click text selection does not work properly
			 */
			return STRING_LITERAL_PARTITION;
		default:
			return super.calculateId(tokenName, tokenType);
		}
	}
}
