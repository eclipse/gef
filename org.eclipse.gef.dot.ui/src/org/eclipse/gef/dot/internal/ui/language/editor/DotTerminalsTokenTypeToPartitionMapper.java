/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation (bug #532244)
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.xtext.ui.editor.model.TerminalsTokenTypeToPartitionMapper;

public class DotTerminalsTokenTypeToPartitionMapper
		extends TerminalsTokenTypeToPartitionMapper {

	@Override
	protected String calculateId(String tokenName, int tokenType) {
		/**
		 * The DOT grammar uses the 'terminal STRING' rule for unquoted text
		 * (that is usually used for quoted text) and uses the 'terminal
		 * QUOTED_STRING' rule for quoted text.
		 * 
		 * With the default TerminalsTokenTypeToPartitionMapper, the double
		 * click text selection does not work as expected. It recognizes the
		 * unquoted text as quoted text (the first and the last letter will be
		 * stripped where they should not be stripped) and does not recognize
		 * the quoted text (the first and the last letter will not be stripped
		 * where they should be stripped).
		 * 
		 * To fix this problem, assign the DEFAULT_CONTENT_TYPE to the 'terminal
		 * STRING' rule (identified by the 'RULE_STRING' token name) and the
		 * STRING_LITERAL_PARTITION to the 'terminal QUOTED_STRING' rule
		 * (identified by the 'RULE_QUOTED_STRING' token name).
		 */
		switch (tokenName) {
		case "RULE_STRING": //$NON-NLS-1$
			return IDocument.DEFAULT_CONTENT_TYPE;
		case "RULE_QUOTED_STRING": //$NON-NLS-1$
			return STRING_LITERAL_PARTITION;
		default:
			return super.calculateId(tokenName, tokenType);
		}
	}
}
