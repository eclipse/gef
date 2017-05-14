/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.contentassist;

import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.htmllabel.DotHtmlLabelHelper;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlTag;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

/**
 * See
 * https://www.eclipse.org/Xtext/documentation/304_ide_concepts.html#content-assist
 * on how to customize the content assistant.
 */
public class DotHtmlLabelProposalProvider extends
		org.eclipse.gef.dot.internal.ui.language.contentassist.AbstractDotHtmlLabelProposalProvider {

	@Override
	public void complete_HtmlTag(EObject model, RuleCall ruleCall,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {

		String parentName = null;

		if (model instanceof HtmlTag) {
			parentName = ((HtmlTag) model).getName();
		} else {
			parentName = DotHtmlLabelHelper.getRootTagKey();
		}

		for (String tagName : DotHtmlLabelHelper.getValidTags()
				.get(parentName)) {
			String proposal = calculateProposalString(tagName);
			String displayString = proposal;
			Image image = null;

			ICompletionProposal completionProposal = createCompletionProposal(
					proposal, displayString, image, context);

			// place the cursor between the opening and the closing html tag
			// after the proposal has been applied
			if (completionProposal instanceof ConfigurableCompletionProposal) {
				ConfigurableCompletionProposal configurableCompletionProposal = (ConfigurableCompletionProposal) completionProposal;
				int cursorPosition = calculateCursorPosition(displayString);
				configurableCompletionProposal
						.setCursorPosition(cursorPosition);
				acceptor.accept(configurableCompletionProposal);
			}

		}
	}

	@Override
	public void completeHtmlAttr_Name(EObject model, Assignment assignment,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		super.completeHtmlAttr_Name(model, assignment, context, acceptor);

		if (model instanceof HtmlTag) {
			HtmlTag htmlTag = (HtmlTag) model;
			String htmlTagName = htmlTag.getName();
			Map<String, Set<String>> validAttributes = DotHtmlLabelHelper
					.getValidAttributes();
			if (validAttributes.containsKey(htmlTagName)) {
				Set<String> validAttributeNames = validAttributes
						.get(htmlTagName);
				for (String validAttributeName : validAttributeNames) {
					acceptor.accept(createCompletionProposal(validAttributeName,
							context));
				}
			}
		}
	}

	/**
	 * Calculates the proposal string to a given tag. Proposes self-closing tags
	 * whenever possible.
	 * 
	 * @param tagName
	 * @return
	 */
	private String calculateProposalString(String tagName) {
		StringBuilder sb = new StringBuilder();

		if (DotHtmlLabelHelper.getNonSelfClosingTags()
				.contains(tagName.toUpperCase())) {
			sb.append("<"); //$NON-NLS-1$
			sb.append(tagName);
			sb.append(">"); //$NON-NLS-1$
			sb.append("</"); //$NON-NLS-1$
			sb.append(tagName);
			sb.append(">"); //$NON-NLS-1$
		} else {
			sb.append("<"); //$NON-NLS-1$
			sb.append(tagName);
			sb.append("/>"); //$NON-NLS-1$
		}

		return sb.toString();
	}

	/**
	 * Calculates the cursor position where the cursor has to be placed after
	 * the given proposal has been applied.
	 * 
	 * @param htmlTagText
	 *            the htmlTagText representing the proposal
	 * @return the proper cursor position
	 */
	private int calculateCursorPosition(String htmlTagText) {
		// in case of a self-closing tag, place the cursor immediately before
		// the "/>" symbol
		if (htmlTagText.contains("/>")) { //$NON-NLS-1$
			return htmlTagText.indexOf("/>"); //$NON-NLS-1$

			// in case of a non self-closing tag, place the cursor between the
			// ">" and "<" symbols
		} else {
			return htmlTagText.indexOf("><") + 1; //$NON-NLS-1$
		}
	}
}
