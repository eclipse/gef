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
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlAttr;
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

			if (completionProposal instanceof ConfigurableCompletionProposal) {
				ConfigurableCompletionProposal configurableCompletionProposal = (ConfigurableCompletionProposal) completionProposal;
				int cursorPosition = calculateCursorPosition(displayString);
				String tagDescription = DotHtmlLabelHelper
						.getTagDescription(tagName);
				// place the cursor between the opening and the closing html tag
				// after the proposal has been applied
				configurableCompletionProposal
						.setCursorPosition(cursorPosition);
				// add tag description to the proposal
				configurableCompletionProposal
						.setAdditionalProposalInfo(tagDescription);
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
					ICompletionProposal completionProposal = createCompletionProposal(
							validAttributeName, context);

					// insert the ="" symbols after the html attribute name and
					// place the cursor between the two "" symbols
					if (completionProposal instanceof ConfigurableCompletionProposal) {
						ConfigurableCompletionProposal configurableCompletionProposal = (ConfigurableCompletionProposal) completionProposal;
						String replacementString = validAttributeName + "=\"\""; //$NON-NLS-1$
						configurableCompletionProposal
								.setReplacementString(replacementString);
						configurableCompletionProposal.setCursorPosition(
								replacementString.length() - 1);
						acceptor.accept(configurableCompletionProposal);
					}

				}
			}
		}
	}

	@Override
	public void completeHtmlAttr_Value(EObject model, Assignment assignment,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		super.completeHtmlAttr_Value(model, assignment, context, acceptor);
		if (model instanceof HtmlAttr) {
			HtmlAttr htmlAttr = (HtmlAttr) model;
			HtmlTag htmlTag = (HtmlTag) htmlAttr.eContainer();
			proposeHtmlAttributeValues(htmlTag.getName(), htmlAttr.getName(),
					context, acceptor);
		}
	}

	@Override
	protected boolean isValidProposal(String proposal, String prefix,
			ContentAssistContext context) {
		if (prefix == null) {
			return false;
		}
		// consider a double quote as a valid prefix for the attribute values
		if (context.getCurrentModel() instanceof HtmlAttr
				&& prefix.startsWith("\"")) { //$NON-NLS-1$
			prefix = prefix.substring(1);
			if (!context.getMatcher().isCandidateMatchingPrefix(proposal,
					prefix)) {
				return false;
			} else {
				/**
				 * Skip the proposal validation check through the
				 * conflictHelper, otherwise it will report a conflict. The
				 * conflictHelper cannot differentiate between the different
				 * states of the custom lexer (tagMode is on or off).
				 */
				return true;
			}
		}

		return super.isValidProposal(proposal, prefix, context);
	}

	private void proposeHtmlAttributeValues(String htmlTagName,
			String htmlAttributeName, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		if ("BR".equalsIgnoreCase(htmlTagName)) { //$NON-NLS-1$
			switch (htmlAttributeName.toUpperCase()) {
			case "ALIGN": //$NON-NLS-1$
				proposeHtmlAttributeValues(context, acceptor, "CENTER", "LEFT", //$NON-NLS-1$ //$NON-NLS-2$
						"RIGHT"); //$NON-NLS-1$
				break;
			default:
				break;
			}
		}

		if ("IMG".equalsIgnoreCase(htmlTagName)) { //$NON-NLS-1$
			switch (htmlAttributeName.toUpperCase()) {
			case "SCALE": //$NON-NLS-1$
				proposeHtmlAttributeValues(context, acceptor, "FALSE", "TRUE", //$NON-NLS-1$ //$NON-NLS-2$
						"WIDTH", "HEIGHT", "BOTH"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				break;
			default:
				break;
			}
		}

		if ("TABLE".equalsIgnoreCase(htmlTagName)) { //$NON-NLS-1$
			switch (htmlAttributeName.toUpperCase()) {
			case "ALIGN": //$NON-NLS-1$
				proposeHtmlAttributeValues(context, acceptor, "CENTER", "LEFT", //$NON-NLS-1$ //$NON-NLS-2$
						"RIGHT"); //$NON-NLS-1$
				break;
			case "FIXEDSIZE": //$NON-NLS-1$
				proposeHtmlAttributeValues(context, acceptor, "FALSE", "TRUE"); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			case "COLUMNS": //$NON-NLS-1$
			case "ROWS": //$NON-NLS-1$
				proposeHtmlAttributeValues(context, acceptor, "*"); //$NON-NLS-1$
				break;
			case "SIDES": //$NON-NLS-1$
				proposeHtmlAttributeValues(context, acceptor, "L", "T", "R", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						"B", "LT", "LR", "LB", "TR", "TB", "RB", "LTR", "TRB", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
						"LRB", "LTB", "LTRB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				break;
			case "VALIGN": //$NON-NLS-1$
				proposeHtmlAttributeValues(context, acceptor, "MIDDLE", //$NON-NLS-1$
						"BOTTOM", "TOP"); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			default:
				break;
			}
		}

		if ("TD".equalsIgnoreCase(htmlTagName)) { //$NON-NLS-1$
			switch (htmlAttributeName.toUpperCase()) {
			case "ALIGN": //$NON-NLS-1$
				proposeHtmlAttributeValues(context, acceptor, "CENTER", "LEFT", //$NON-NLS-1$ //$NON-NLS-2$
						"RIGHT", "TEXT"); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			case "BALIGN": //$NON-NLS-1$
				proposeHtmlAttributeValues(context, acceptor, "CENTER", "LEFT", //$NON-NLS-1$ //$NON-NLS-2$
						"RIGHT"); //$NON-NLS-1$
				break;
			case "FIXEDSIZE": //$NON-NLS-1$
				proposeHtmlAttributeValues(context, acceptor, "FALSE", "TRUE"); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			case "SIDES": //$NON-NLS-1$
				proposeHtmlAttributeValues(context, acceptor, "L", "T", "R", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						"B", "LT", "LR", "LB", "TR", "TB", "RB", "LTR", "TRB", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
						"LRB", "LTB", "LTRB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				break;
			case "VALIGN": //$NON-NLS-1$
				proposeHtmlAttributeValues(context, acceptor, "MIDDLE", //$NON-NLS-1$
						"BOTTOM", "TOP"); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			default:
				break;
			}
		}
	}

	private void proposeHtmlAttributeValues(ContentAssistContext context,
			ICompletionProposalAcceptor acceptor, String... proposals) {
		for (String proposal : proposals) {
			ICompletionProposal completionProposal = createCompletionProposal(
					proposal, context);
			if (context.getCurrentNode().getText().startsWith("\"") //$NON-NLS-1$
					&& completionProposal instanceof ConfigurableCompletionProposal) {
				// ensure that the double quote at the beginning of an attribute
				// value is not overridden when applying the proposal
				ConfigurableCompletionProposal configurableCompletionProposal = (ConfigurableCompletionProposal) completionProposal;
				configurableCompletionProposal.setReplacementOffset(
						configurableCompletionProposal.getReplacementOffset()
								+ 1);
				configurableCompletionProposal.setReplacementLength(
						configurableCompletionProposal.getReplacementLength()
								- 1);
				configurableCompletionProposal.setReplaceContextLength(
						configurableCompletionProposal.getReplaceContextLength()
								- 1);
			}
			acceptor.accept(completionProposal);
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
