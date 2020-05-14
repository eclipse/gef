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
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #532216)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.formatting;

import java.io.IOException;
import java.io.StringReader;

import org.eclipse.gef.dot.internal.language.DotHtmlLabelStandaloneSetup;
import org.eclipse.gef.dot.internal.language.formatting.DotFormatter.DotFormattingConfigBasedStream;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.formatting.IFormatter;
import org.eclipse.xtext.formatting.INodeModelStreamer;
import org.eclipse.xtext.formatting.impl.NodeModelStreamer;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.parsetree.reconstr.ITokenStream;
import org.eclipse.xtext.parsetree.reconstr.impl.TokenStringBuffer;

import com.google.inject.Injector;

/**
 * The DotNodeModelStreamer class is capable of delegating the formatting from
 * the dot host grammar formatter to the dot sub-grammar formatters.
 */
public class DotNodeModelStreamer extends NodeModelStreamer {

	@Override
	protected void writeSemantic(ITokenStream out, ICompositeNode node)
			throws IOException {
		AbstractRule rule = tokenUtil.getTokenRule(node);
		Object val = valueConverter.toValue(tokenUtil.serializeNode(node),
				rule.getName(), node);

		if (val instanceof ID && ((ID) val).getType() == ID.Type.HTML_STRING) {
			writeHTMLStringSemantic(rule,
					(DotFormatter.DotFormattingConfigBasedStream) out, node);
		} else {
			super.writeSemantic(out, node);
		}
	}

	private void writeHTMLStringSemantic(AbstractRule rule,
			DotFormattingConfigBasedStream out, ICompositeNode node)
			throws IOException {
		Injector htmlLabelInjector = new DotHtmlLabelStandaloneSetup()
				.createInjectorAndDoEMFRegistration();
		IFormatter dotHtmlLabelFormatter = htmlLabelInjector
				.getInstance(IFormatter.class);
		ITokenStream htmlLabelOut = new TokenStringBuffer();
		// TODO: calculate initial indentation properly
		ITokenStream fmt = dotHtmlLabelFormatter.createFormatterStream("\t\t",
				htmlLabelOut, false);
		INodeModelStreamer dothtmlLabelNodeModelStreamer = htmlLabelInjector
				.getInstance(INodeModelStreamer.class);

		IParser dotHtmlLabelParser = htmlLabelInjector
				.getInstance(IParser.class);
		// cut off the leading and the trailing white spaces
		String trimmedNodeText = node.getText().trim();
		String htmlLabelText = trimmedNodeText.substring(1,
				trimmedNodeText.length() - 1);
		IParseResult parseResult = dotHtmlLabelParser
				.parse(new StringReader(htmlLabelText));
		ICompositeNode htmlLabelRootNode = parseResult.getRootNode();

		dothtmlLabelNodeModelStreamer.feedTokenStream(fmt, htmlLabelRootNode, 0,
				htmlLabelText.length());
		out.writeSemantic(null, "<");
		out.addNewLine();
		out.addLineEntry(node.getGrammarElement(), htmlLabelOut.toString(),
				false);
		out.addNewLine();
		out.writeSemantic(null, ">");
	}
}
