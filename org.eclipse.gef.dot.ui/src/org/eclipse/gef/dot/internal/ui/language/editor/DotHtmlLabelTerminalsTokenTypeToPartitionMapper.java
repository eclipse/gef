/*******************************************************************************
 * Copyright (c) 2017, 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG)     - initial API and implementation
 *    Zoey Gerrit Prigge (itemis AG) - add TEXT_PARTITION type (bug #532244)
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.xtext.ui.editor.model.TerminalsTokenTypeToPartitionMapper;

public class DotHtmlLabelTerminalsTokenTypeToPartitionMapper
		extends TerminalsTokenTypeToPartitionMapper {

	public static final String TEXT_PARTITION = "__html_text"; //$NON-NLS-1$

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
		case "RULE_TEXT": //$NON-NLS-1$
			/**
			 * assign the TEXT_PARTITION to the TEXT rule, to assign double
			 * click strategy manually as double click text selection does not
			 * work properly otherwise.
			 */
			return TEXT_PARTITION;
		default:
			return super.calculateId(tokenName, tokenType);
		}
	}

	@Override
	public String[] getSupportedPartitionTypes() {
		List<String> supportedTypes = new ArrayList<>(
				Arrays.asList(super.getSupportedPartitionTypes()));
		supportedTypes.add(TEXT_PARTITION);
		return supportedTypes.toArray(new String[supportedTypes.size()]);
	}
}
