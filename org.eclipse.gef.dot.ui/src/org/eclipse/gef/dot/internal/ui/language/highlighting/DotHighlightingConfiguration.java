/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Tamas Miklossy  (itemis AG) - minor refactorings
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.highlighting;

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

	public static final String HTML_STRING_ID = "html_string"; //$NON-NLS-1$
	public static final String QUOTED_STRING_ID = "quoted_string"; //$NON-NLS-1$
	public static final String NUMERAL_ID = "numeral"; //$NON-NLS-1$

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
		acceptor.acceptDefaultHighlighting(HTML_STRING_ID, "HTML String", //$NON-NLS-1$
				htmlStringTextStyle());

		acceptor.acceptDefaultHighlighting(COMMENT_ID, "Comment", //$NON-NLS-1$
				commentTextStyle());

		acceptor.acceptDefaultHighlighting(PUNCTUATION_ID,
				"Punctuation Character", punctuationTextStyle()); //$NON-NLS-1$

		acceptor.acceptDefaultHighlighting(INVALID_TOKEN_ID, "Invalid Symbol", //$NON-NLS-1$
				errorTextStyle());
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

	public TextStyle htmlStringTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(153, 76, 0)); // brown
		textStyle.setBackgroundColor(new RGB(220, 220, 220)); // light gray
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

}
