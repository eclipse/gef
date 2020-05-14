/*******************************************************************************
 * Copyright (c) 2017, 2018 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Tamas Miklossy   (itemis AG) - formatter improvements (bug #532216)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.formatting;

import org.eclipse.gef.dot.internal.language.services.DotHtmlLabelGrammarAccess;
import org.eclipse.gef.dot.internal.language.services.DotHtmlLabelGrammarAccess.HtmlAttrElements;
import org.eclipse.gef.dot.internal.language.services.DotHtmlLabelGrammarAccess.HtmlTagElements;
import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;

import com.google.inject.Inject;

/**
 * This class contains custom formatting declarations.
 */
public class DotHtmlLabelFormatter extends AbstractDeclarativeFormatter {

	@Inject
	private DotHtmlLabelGrammarAccess grammarAccess;

	@Override
	protected void configureFormatting(FormattingConfig c) {
		configureHtmlTagFormatting(c);
		configureHtmlAttributeFormatting(c);
		configureCommentsFormatting(c);
	}

	private void configureHtmlTagFormatting(FormattingConfig c) {
		HtmlTagElements htmlTag = grammarAccess.getHtmlTagAccess();

		// no space before and after the html tag name
		c.setNoSpace().around(htmlTag.getNameAssignment_1());

		// line break before and after the html tag's child
		c.setLinewrap(1, 1, 1).around(htmlTag.getChildrenAssignment_3_1_1());

		// indentation before and after the html tag's child
		c.setIndentationIncrement()
				.before(htmlTag.getChildrenAssignment_3_1_1());

		// dedentation before and after the html tag's child
		c.setIndentationDecrement()
				.after(htmlTag.getChildrenAssignment_3_1_1());

		// no space before and after the html tag close name
		c.setNoSpace().around(htmlTag.getCloseNameAssignment_3_1_3());

		// no space before the '>' symbol
		c.setNoSpace().before(htmlTag.getTAG_ENDTerminalRuleCall_3_1_0());

		// set a line wrap after the '>' symbol that is part of an end tag
		c.setLinewrap(1, 1, 1)
				.after(htmlTag.getTAG_ENDTerminalRuleCall_3_1_4());

		// set a line wrap after the '>' symbol that is part of a self-closing
		// end tag
		c.setLinewrap(1, 1, 1).after(
				htmlTag.getSelfClosingTAG_END_CLOSETerminalRuleCall_3_0_0());
	}

	private void configureHtmlAttributeFormatting(FormattingConfig c) {
		HtmlAttrElements htmlAttribute = grammarAccess.getHtmlAttrAccess();

		// one space before the html attribute name
		c.setSpace(" ").before(htmlAttribute.getNameAssignment_0());

		// no space after the html attribute name
		c.setNoSpace().after(htmlAttribute.getNameAssignment_0());

		// no space before the html attribute value
		c.setNoSpace().before(htmlAttribute.getValueAssignment_4());
	}

	private void configureCommentsFormatting(FormattingConfig c) {
		// preserve newlines around comments
		c.setLinewrap(0, 1, 2).before(grammarAccess.getHTML_COMMENTRule());
	}
}