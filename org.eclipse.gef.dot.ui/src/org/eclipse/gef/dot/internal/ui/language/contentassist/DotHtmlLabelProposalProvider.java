/*******************************************************************************
 * Copyright (c) 2017, 2020 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Tamas Miklossy   (itemis AG) - initial API and implementation
 *     Zoey Prigge      (itemis AG) - Add ca support for html color attrs (bug #553575)
 *     
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.contentassist;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.htmllabel.DotHtmlLabelHelper;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlAttr;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlContent;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlLabel;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlTag;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmllabelFactory;
import org.eclipse.gef.dot.internal.ui.language.DotActivator;
import org.eclipse.gef.dot.internal.ui.language.editor.DotEditorUtils;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.ui.IImageHelper;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

import com.google.inject.Inject;

/**
 * See
 * https://www.eclipse.org/Xtext/documentation/304_ide_concepts.html#content-assist
 * on how to customize the content assistant.
 */
public class DotHtmlLabelProposalProvider extends
		org.eclipse.gef.dot.internal.ui.language.contentassist.AbstractDotHtmlLabelProposalProvider {

	@Inject
	private IImageHelper imageHelper;

	@Override
	public void complete_HtmlTag(EObject model, RuleCall ruleCall,
			ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {

		String parentName = null;
		List<HtmlContent> siblings = new ArrayList<>();

		if (model instanceof HtmlTag) {
			HtmlTag tag = (HtmlTag) model;
			parentName = tag.getName();
			siblings = tag.getChildren();
		} else {
			parentName = DotHtmlLabelHelper.getRootTagKey();
			if (model instanceof HtmlLabel) {
				siblings = ((HtmlLabel) model).getParts();
			}
			if (model instanceof HtmlContent) {
				siblings.add((HtmlContent) model);
			}
		}

		Image image = imageHelper.getImage("html_tag.png"); //$NON-NLS-1$
		for (String tagName : DotHtmlLabelHelper.getValidTags()
				.get(parentName)) {
			if (isValidSibling(tagName, siblings)) {

				String proposal = calculateProposalString(tagName);
				String format = "%s: Tag"; //$NON-NLS-1$
				StyledString displayString = DotEditorUtils.style(format,
						proposal);

				ICompletionProposal completionProposal = createCompletionProposal(
						proposal, displayString, image, context);

				if (completionProposal instanceof ConfigurableCompletionProposal) {
					ConfigurableCompletionProposal configurableCompletionProposal = (ConfigurableCompletionProposal) completionProposal;
					int cursorPosition = calculateCursorPosition(proposal);
					String tagDescription = DotHtmlLabelHelper
							.getTagDescription(tagName);
					// place the cursor between the opening and the closing html
					// tag
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
				Image image = imageHelper.getImage("attribute.png"); //$NON-NLS-1$
				Set<String> validAttributeNames = validAttributes
						.get(htmlTagName);
				for (String validAttributeName : validAttributeNames) {
					String proposal = validAttributeName;
					String format = "%s: Attribute"; //$NON-NLS-1$
					StyledString displayString = DotEditorUtils.style(format,
							proposal);

					ICompletionProposal completionProposal = createCompletionProposal(
							proposal, displayString, image, context);

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
			switch (htmlAttr.getName().toLowerCase(Locale.ENGLISH)) {
			case "bgcolor": //$NON-NLS-1$
				proposeHtmlBgColorAttributeValues(context, acceptor);
				break;
			case "color": //$NON-NLS-1$
				proposeHtmlColorAttributeValues(context, acceptor);
				break;
			default:
				proposeHtmlAttributeValues(htmlTag.getName(),
						htmlAttr.getName(), context, acceptor);
				break;
			}
		}
	}

	@Override
	protected boolean isValidProposal(String proposal, String prefix,
			ContentAssistContext context) {
		if (prefix == null) {
			return false;
		}
		// consider a single quote / double quote as a valid prefix for the
		// attribute values
		if (context.getCurrentModel() instanceof HtmlAttr
				&& (prefix.startsWith("\"") || prefix.startsWith("'"))) { //$NON-NLS-1$ //$NON-NLS-2$
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

	/**
	 * Checks if the html tag is a valid sibling to determine if it should be
	 * offered as proposal
	 * 
	 * @param tagName
	 *            the name of the html tag
	 * @param siblings
	 *            the list of siblings
	 * @return true if the html tag represented by the tagName is valid
	 *         considering the siblings, false otherwise
	 */
	private boolean isValidSibling(String tagName, List<HtmlContent> siblings) {
		// create a new sibling with the html tag 'tagName'
		HtmlContent newSibling = HtmllabelFactory.eINSTANCE.createHtmlContent();
		HtmlTag htmlTag = HtmllabelFactory.eINSTANCE.createHtmlTag();
		htmlTag.setName(tagName);
		newSibling.setTag(htmlTag);

		// add the newly created sibling into the siblings list and verify the
		// extended siblings list
		List<HtmlContent> extendedSiblings = new ArrayList<>(siblings);
		extendedSiblings.add(newSibling);
		return DotHtmlLabelHelper.isValidSiblings(extendedSiblings);
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

	private void proposeHtmlBgColorAttributeValues(ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		INode currentNode = context.getCurrentNode();
		String fullText = currentNode.getText();
		String text = fullText;
		int beginReplacementOffset = currentNode.getOffset();

		if (context.getPrefix().contains(":")) { //$NON-NLS-1$
			int colonOffset = fullText.indexOf(':') + 1;
			text = fullText.substring(colonOffset);
			beginReplacementOffset += colonOffset;
		} else {
			beginReplacementOffset += beginsWithQuote(text) ? 1 : 0;
		}
		proposeHtmlColorAttributeValues(context, acceptor,
				text.replaceAll("['\"]", ""), //$NON-NLS-1$ //$NON-NLS-2$
				beginReplacementOffset, context.getOffset());
		if (!fullText.contains(":")) { //$NON-NLS-1$
			acceptor.accept(new ConfigurableCompletionProposal(":", //$NON-NLS-1$
					context.getOffset(), 0, 1));
		}
	}

	private void proposeHtmlColorAttributeValues(ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		INode currentNode = context.getCurrentNode();
		String text = currentNode.getText();
		proposeHtmlColorAttributeValues(context, acceptor,
				text.replaceAll("['\"]", ""), //$NON-NLS-1$ //$NON-NLS-2$
				currentNode.getOffset() + (beginsWithQuote(text) ? 1 : 0),
				context.getOffset());
	}

	private void proposeHtmlColorAttributeValues(ContentAssistContext context,
			ICompletionProposalAcceptor acceptor, String text,
			int beginReplacementOffset, int contextOffset) {
		String subgrammarName = DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTCOLOR;

		List<ConfigurableCompletionProposal> configurableCompletionProposals = new DotProposalProviderDelegator(
				subgrammarName).computeConfigurableCompletionProposals(text,
						contextOffset - beginReplacementOffset);

		for (ConfigurableCompletionProposal configurableCompletionProposal : configurableCompletionProposals) {
			// adapt the replacement offset determined within the
			// sub-grammar context to be valid within the context of the
			// original text
			configurableCompletionProposal.setReplacementOffset(
					beginReplacementOffset + configurableCompletionProposal
							.getReplacementOffset());
			acceptor.accept(configurableCompletionProposal);
		}
	}

	private boolean beginsWithQuote(String baseText) {
		if (baseText.length() == 0) {
			return false;
		}
		char first = baseText.charAt(0);
		return first == '\'' || first == '"';
	}

	private void proposeHtmlAttributeValues(ContentAssistContext context,
			ICompletionProposalAcceptor acceptor, String... proposals) {
		for (String proposal : proposals) {
			ICompletionProposal completionProposal = createCompletionProposal(
					proposal, context);
			String text = context.getCurrentNode().getText();
			if ((text.startsWith("\"") || text.startsWith("'"))//$NON-NLS-1$ //$NON-NLS-2$
					&& completionProposal instanceof ConfigurableCompletionProposal) {
				// ensure that the single quote / double quote at the beginning
				// of an attribute
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
