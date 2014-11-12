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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

public class DotHighlightingConfiguration extends
		DefaultHighlightingConfiguration {

	public static final String GRAPH_NAME_ID = "graph_name";
	public static final String NODE_NAME_ID = "node_name";
	public static final String PORT_NAME_ID = "port_name";
	public static final String ATTRIBUTE_NAME_ID = "attribute_name";

	public static final String EDGE_OP_ID = "edge_op";

	public static final String QUOTED_STRING_ID = "quoted_string";
	public static final String NUMERAL_ID = "numeral";

	public void configure(IHighlightingConfigurationAcceptor acceptor) {

		// semantic highlighting
		acceptor.acceptDefaultHighlighting(GRAPH_NAME_ID, "Graph Id",
				graphIdTextStyle());
		acceptor.acceptDefaultHighlighting(NODE_NAME_ID, "Node Id",
				nodeIdTextStyle());
		acceptor.acceptDefaultHighlighting(PORT_NAME_ID, "Port Id",
				portIdTextStyle());
		acceptor.acceptDefaultHighlighting(ATTRIBUTE_NAME_ID, "Attribute Id",
				attributeIdTextStyle());
		acceptor.acceptDefaultHighlighting(EDGE_OP_ID, "Edge Op",
				edgeOpTextStyle());

		acceptor.acceptDefaultHighlighting(DEFAULT_ID, "Default",
				defaultTextStyle());
		acceptor.acceptDefaultHighlighting(KEYWORD_ID, "Keyword",
				keywordTextStyle());

		// lexical hightlighting
		acceptor.acceptDefaultHighlighting(STRING_ID, "(Unquoted) String",
				stringTextStyle());
		acceptor.acceptDefaultHighlighting(NUMERAL_ID, "Numeral",
				numberTextStyle());
		acceptor.acceptDefaultHighlighting(QUOTED_STRING_ID, "Quoted String",
				quotedStringTextStyle());
		acceptor.acceptDefaultHighlighting(COMMENT_ID, "Comment",
				commentTextStyle());

		acceptor.acceptDefaultHighlighting(PUNCTUATION_ID,
				"Punctuation Character", punctuationTextStyle());

		acceptor.acceptDefaultHighlighting(INVALID_TOKEN_ID, "Invalid Symbol",
				errorTextStyle());
	}

	public TextStyle graphIdTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		return textStyle;
	}

	public TextStyle nodeIdTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		return textStyle;
	}

	public TextStyle attributeIdTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		// dark blue
		textStyle.setColor(new RGB(0, 76, 153));
		return textStyle;
	}

	public TextStyle portIdTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(0, 153, 76));
		return textStyle;
	}

	public TextStyle edgeOpTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(0, 153, 0));
		return textStyle;
	}

	@Override
	public TextStyle stringTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(153, 76, 0));
		return textStyle;
	}

	public TextStyle quotedStringTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(255, 0, 0));
		return textStyle;
	}

	@Override
	public TextStyle keywordTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
	}

}
