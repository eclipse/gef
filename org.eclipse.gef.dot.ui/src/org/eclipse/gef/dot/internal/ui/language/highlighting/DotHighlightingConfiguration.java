/*******************************************************************************
 * Copyright (c) 2014, 2020 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Tamas Miklossy  (itemis AG) - improve support for html-label highlighting
 *     Zoey Prigge     (itemis AG) - implement deprecation strikethrough highlighting (bug #552993)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.highlighting;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

public class DotHighlightingConfiguration
		extends DefaultHighlightingConfiguration {

	public static final String GRAPH_NAME_ID = "graph_name"; //$NON-NLS-1$
	public static final String NODE_NAME_ID = "node_name"; //$NON-NLS-1$
	public static final String PORT_NAME_ID = "port_name"; //$NON-NLS-1$
	public static final String ATTRIBUTE_NAME_ID = "attribute_name"; //$NON-NLS-1$

	public static final String EDGE_OP_ID = "edge_op"; //$NON-NLS-1$

	public static final String QUOTED_STRING_ID = "quoted_string"; //$NON-NLS-1$
	public static final String NUMERAL_ID = "numeral"; //$NON-NLS-1$

	public static final String HTML_TAG = "html_tag"; //$NON-NLS-1$
	public static final String HTML_ATTRIBUTE_NAME = "html_attribute_name"; //$NON-NLS-1$
	public static final String HTML_ATTRIBUTE_EQUAL_SIGN = "html_attribute_equal_sign"; //$NON-NLS-1$
	public static final String HTML_ATTRIBUTE_VALUE = "html_attribute_value"; //$NON-NLS-1$
	public static final String HTML_CONTENT = "html_content"; //$NON-NLS-1$
	public static final String HTML_COMMENT = "html_comment"; //$NON-NLS-1$

	public static final String DEPRECATED_ATTRIBUTE_VALUE = "deprecated_attribute_value"; //$NON-NLS-1$
	public static final String QUOTED_SUFFIX = "_quoted"; //$NON-NLS-1$
	public static final String DEPRECATED_ATTRIBUTE_VALUE_QUOTED = DEPRECATED_ATTRIBUTE_VALUE
			+ QUOTED_SUFFIX;

	@Override
	public void configure(IHighlightingConfigurationAcceptor acceptor) {
		// semantic highlighting
		acceptor.acceptDefaultHighlighting(GRAPH_NAME_ID, "Graph Id", //$NON-NLS-1$
				graphIdTextStyle());
		acceptor.acceptDefaultHighlighting(NODE_NAME_ID, "Node Id", //$NON-NLS-1$
				nodeIdTextStyle());
		acceptor.acceptDefaultHighlighting(PORT_NAME_ID, "Port Id", //$NON-NLS-1$
				portIdTextStyle());
		acceptor.acceptDefaultHighlighting(ATTRIBUTE_NAME_ID, "Attribute Id", //$NON-NLS-1$
				attributeIdTextStyle());
		acceptor.acceptDefaultHighlighting(EDGE_OP_ID, "Edge Op", //$NON-NLS-1$
				edgeOpTextStyle());

		acceptor.acceptDefaultHighlighting(DEFAULT_ID, "Default", //$NON-NLS-1$
				defaultTextStyle());
		acceptor.acceptDefaultHighlighting(KEYWORD_ID, "Keyword", //$NON-NLS-1$
				keywordTextStyle());

		// lexical highlighting
		acceptor.acceptDefaultHighlighting(NUMERAL_ID, "Numeral", //$NON-NLS-1$
				numberTextStyle());
		acceptor.acceptDefaultHighlighting(QUOTED_STRING_ID, "Quoted String", //$NON-NLS-1$
				quotedStringTextStyle());
		acceptor.acceptDefaultHighlighting(STRING_ID, "(Unquoted) String", //$NON-NLS-1$
				stringTextStyle());
		acceptor.acceptDefaultHighlighting(COMMENT_ID, "Comment", //$NON-NLS-1$
				commentTextStyle());

		acceptor.acceptDefaultHighlighting(PUNCTUATION_ID,
				"Punctuation Character", punctuationTextStyle()); //$NON-NLS-1$

		acceptor.acceptDefaultHighlighting(INVALID_TOKEN_ID, "Invalid Symbol", //$NON-NLS-1$
				errorTextStyle());

		// html-like label sub-grammar highlighting
		acceptor.acceptDefaultHighlighting(HTML_TAG, "Html Tag", //$NON-NLS-1$
				htmlTagStyle());
		acceptor.acceptDefaultHighlighting(HTML_ATTRIBUTE_NAME,
				"Html Attribute Name", //$NON-NLS-1$
				htmlAttributeNameStyle());
		acceptor.acceptDefaultHighlighting(HTML_ATTRIBUTE_EQUAL_SIGN,
				"Html Attribute Equal Sign", //$NON-NLS-1$
				htmlAttributeEqualSignStyle());
		acceptor.acceptDefaultHighlighting(HTML_ATTRIBUTE_VALUE,
				"Html Attribute Value", //$NON-NLS-1$
				htmlAttributeValueStyle());
		acceptor.acceptDefaultHighlighting(HTML_CONTENT, "Html Content", //$NON-NLS-1$
				htmlContentStyle());
		acceptor.acceptDefaultHighlighting(HTML_COMMENT, "Html Comment", //$NON-NLS-1$
				htmlCommentStyle());

		// deprecated highlighting
		acceptor.acceptDefaultHighlighting(DEPRECATED_ATTRIBUTE_VALUE,
				"Deprecated Attribute Value", //$NON-NLS-1$
				deprecatedStyle());
		acceptor.acceptDefaultHighlighting(DEPRECATED_ATTRIBUTE_VALUE_QUOTED,
				"Deprecated Attribute Value Quoted", //$NON-NLS-1$
				deprecatedQuotedStyle());
	}

	public TextStyle graphIdTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy(); // black
		return textStyle;
	}

	public TextStyle nodeIdTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy(); // black
		return textStyle;
	}

	public TextStyle attributeIdTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(0, 76, 153)); // dark blue
		return textStyle;
	}

	public TextStyle portIdTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(0, 153, 76)); // light green
		return textStyle;
	}

	public TextStyle edgeOpTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(0, 153, 0)); // light green
		return textStyle;
	}

	@Override
	public TextStyle stringTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(153, 76, 0)); // brown
		return textStyle;
	}

	public TextStyle quotedStringTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(255, 0, 0)); // red
		return textStyle;
	}

	@Override
	public TextStyle keywordTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy(); // black
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
	}

	private TextStyle htmlTagStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(63, 127, 127)); // turquoise green
		return textStyle;
	}

	private TextStyle htmlAttributeNameStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(127, 0, 127)); // purple
		return textStyle;
	}

	private TextStyle htmlAttributeEqualSignStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(0, 0, 0)); // black
		return textStyle;
	}

	private TextStyle htmlAttributeValueStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(42, 0, 255)); // blue
		textStyle.setStyle(SWT.ITALIC);
		return textStyle;
	}

	private TextStyle htmlContentStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(0, 0, 0)); // black
		return textStyle;
	}

	private TextStyle htmlCommentStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(63, 95, 191)); // turquoise blue
		return textStyle;
	}

	private TextStyle deprecatedStyle() {
		TextStyle textStyle = stringTextStyle().copy();
		textStyle.setStyle(TextAttribute.STRIKETHROUGH);
		return textStyle;
	}

	private TextStyle deprecatedQuotedStyle() {
		TextStyle textStyle = quotedStringTextStyle().copy();
		textStyle.setStyle(TextAttribute.STRIKETHROUGH);
		return textStyle;
	}

}
