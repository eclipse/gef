/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG)     - initial API and implementation (bug #532244)
 *    Zoey Gerrit Prigge (itemis AG) - added HTML_STRING_PARTITION type (bug #532244)
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.xtext.ui.editor.model.TerminalsTokenTypeToPartitionMapper;

public class DotTerminalsTokenTypeToPartitionMapper
		extends TerminalsTokenTypeToPartitionMapper {

	public static final String HTML_STRING_PARTITION = "__html_string"; //$NON-NLS-1$

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
		/**
		 * Html strings ('RULE_HTML_STRING') in dot use a specific syntax, hence
		 * for double clicking support, we need to implement a custom double
		 * click strategy using the HTML_STRING_PARTITION.
		 */
		case "RULE_HTML_STRING": //$NON-NLS-1$
			return HTML_STRING_PARTITION;
		default:
			return super.calculateId(tokenName, tokenType);
		}
	}

	@Override
	public String[] getSupportedPartitionTypes() {
		List<String> supportedTypes = new ArrayList<>(
				Arrays.asList(super.getSupportedPartitionTypes()));
		supportedTypes.add(HTML_STRING_PARTITION);
		return supportedTypes.toArray(new String[supportedTypes.size()]);
	}
}
