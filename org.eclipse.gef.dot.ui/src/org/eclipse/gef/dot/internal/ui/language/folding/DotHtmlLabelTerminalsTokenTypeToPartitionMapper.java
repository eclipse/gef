/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.folding;

import org.eclipse.xtext.ui.editor.model.TerminalsTokenTypeToPartitionMapper;

public class DotHtmlLabelTerminalsTokenTypeToPartitionMapper
		extends TerminalsTokenTypeToPartitionMapper {

	@Override
	protected String calculateId(String tokenName, int tokenType) {
		// assign the COMMENT_PARTITION to the HTML_COMMENT rule, otherwise, the
		// multi-line comment folding does not work
		if ("RULE_HTML_COMMENT".equals(tokenName)) { //$NON-NLS-1$
			return COMMENT_PARTITION;
		} else {
			return super.calculateId(tokenName, tokenType);
		}
	}
}
