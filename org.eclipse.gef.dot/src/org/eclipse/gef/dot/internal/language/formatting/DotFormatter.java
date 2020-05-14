/*******************************************************************************
 * Copyright (c) 2009, 2018 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation (see bug #277380)
 *     Tamas Miklossy (itemis AG) - formatter improvements (bug #532216)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.formatting;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.services.DotGrammarAccess;
import org.eclipse.gef.dot.internal.language.services.DotGrammarAccess.DotGraphElements;
import org.eclipse.gef.dot.internal.language.services.DotGrammarAccess.SubgraphElements;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.formatting.IElementMatcherProvider.IElementMatcher;
import org.eclipse.xtext.formatting.IIndentationInformation;
import org.eclipse.xtext.formatting.ILineSeparatorInformation;
import org.eclipse.xtext.formatting.IWhitespaceInformationProvider;
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.AbstractFormattingConfig.ElementPattern;
import org.eclipse.xtext.formatting.impl.FormattingConfig;
import org.eclipse.xtext.formatting.impl.FormattingConfigBasedStream;
import org.eclipse.xtext.parsetree.reconstr.IHiddenTokenHelper;
import org.eclipse.xtext.parsetree.reconstr.ITokenStream;

import com.google.inject.Inject;

/**
 * This class contains custom formatting declarations.
 */
public class DotFormatter extends AbstractDeclarativeFormatter {

	@Inject
	private DotGrammarAccess grammarAccess;

	@Override
	protected void configureFormatting(FormattingConfig c) {
		// auto line wrap lines longer than 120 characters
		c.setAutoLinewrap(120);

		configureKeywordFormatting(c);
		configureGraphFormatting(c);
		configureSubgraphFormatting(c);
		configureCommentsFormatting(c);
	}

	private void configureKeywordFormatting(FormattingConfig c) {
		// no space before ';'
		for (Keyword keyword : grammar.findKeywords(";")) {
			c.setNoSpace().before(keyword);
		}

		// no space before ','
		for (Keyword keyword : grammar.findKeywords(",")) {
			c.setNoSpace().before(keyword);
		}

		// no space before '['
		for (Keyword keyword : grammar.findKeywords("[")) {
			c.setNoSpace().before(keyword);
		}

		// no space before and after '='
		for (Keyword keyword : grammar.findKeywords("=")) {
			c.setNoSpace().around(keyword);
		}
	}

	private void configureGraphFormatting(FormattingConfig c) {
		DotGraphElements graph = grammarAccess.getDotGraphAccess();

		// newline after {
		c.setLinewrap(1, 1, 2).after(graph.getLeftCurlyBracketKeyword_3());

		// newline before }
		c.setLinewrap().before(graph.getRightCurlyBracketKeyword_5());

		// indentation between { }
		c.setIndentation(graph.getLeftCurlyBracketKeyword_3(),
				graph.getRightCurlyBracketKeyword_5());

		// newline after the graph's statements
		c.setLinewrap(1, 1, 2).after(graph.getStmtsAssignment_4());
	}

	private void configureSubgraphFormatting(FormattingConfig c) {
		SubgraphElements subgraph = grammarAccess.getSubgraphAccess();

		// newline after {
		c.setLinewrap(1, 1, 2).after(subgraph.getLeftCurlyBracketKeyword_2());

		// newline before }
		c.setLinewrap().before(subgraph.getRightCurlyBracketKeyword_4());

		// indentation between { }
		c.setIndentation(subgraph.getLeftCurlyBracketKeyword_2(),
				subgraph.getRightCurlyBracketKeyword_4());

		// newline after the subgraph's statements
		c.setLinewrap(1, 1, 2).after(subgraph.getStmtsAssignment_3());

		// an empty line before the subgraph itself
		c.setLinewrap(2, 2, 2).before(subgraph.getRule());
	}

	private void configureCommentsFormatting(FormattingConfig c) {
		// It's usually a good idea to activate the following three statements.
		// They will add and preserve newlines around comments
		c.setLinewrap(0, 1, 2).before(grammarAccess.getSL_COMMENTRule());
		c.setLinewrap(0, 1, 2).before(grammarAccess.getML_COMMENTRule());
		c.setLinewrap(0, 1, 1).after(grammarAccess.getML_COMMENTRule());
	}

	/**
	 * The following customizations are needed to be able to create a customized
	 * DotFormattingConfigBasedStream
	 */

	@Inject
	private IHiddenTokenHelper hiddenTokenHelper;

	@Inject
	private IWhitespaceInformationProvider whitespaceInformationProvider;

	private URI contextResourceURI;

	protected IIndentationInformation getIndentInfo() {
		return whitespaceInformationProvider
				.getIndentationInformation(contextResourceURI);
	}

	protected ILineSeparatorInformation getLineSeparatorInfo() {
		return whitespaceInformationProvider
				.getLineSeparatorInformation(contextResourceURI);
	}

	public ITokenStream createFormatterStream(EObject context, String indent,
			ITokenStream out, boolean preserveWhitespaces) {
		if (context != null && context.eResource() != null
				&& context.eResource().getURI() != null) {
			contextResourceURI = EcoreUtil2
					.getPlatformResourceOrNormalizedURI(context).trimFragment();
		}
		return new DotFormattingConfigBasedStream(out, indent, getConfig(),
				createMatcher(), hiddenTokenHelper, preserveWhitespaces);
	}

	/**
	 * The DotFormattingconfigBasedStream extends the
	 * formattingConfigBasedStream API by defining additional methods and making
	 * inherited methods visible.
	 */
	class DotFormattingConfigBasedStream extends FormattingConfigBasedStream {

		/**
		 * Instantiates a formatting configuration based stream
		 */
		DotFormattingConfigBasedStream(ITokenStream out,
				String initialIndentation, FormattingConfig cfg,
				IElementMatcher<ElementPattern> matcher,
				IHiddenTokenHelper hiddenTokenHelper, boolean preserveSpaces) {
			super(out, initialIndentation, cfg, matcher, hiddenTokenHelper,
					preserveSpaces);
		}

		@Override
		public void addLineEntry(EObject grammarElement, String value,
				boolean isHidden) throws IOException {
			super.addLineEntry(grammarElement, value, isHidden);
		}

		/**
		 * Adds a new line to the stream
		 *
		 * @throws IOException
		 *             exception may occur from the Xtext framework
		 */
		void addNewLine() throws IOException {
			addLineEntry(null, getLineSeparator(), false);
		}
	}
}
