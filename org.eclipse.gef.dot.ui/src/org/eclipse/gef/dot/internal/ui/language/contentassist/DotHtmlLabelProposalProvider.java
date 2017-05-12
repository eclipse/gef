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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
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
			parentName = ROOT_TAG_KEY;
		}

		for (String tagName : validTags.get(parentName)) {
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
		String[] selfClosingIsNotAllowed = { "B", "FONT", "I", "O", "S", "SUB",
				"SUP", "TABLE", "TD", "TR", "U" };

		StringBuilder sb = new StringBuilder();

		if (Arrays.binarySearch(selfClosingIsNotAllowed,
				tagName.toUpperCase()) >= 0) {
			sb.append("<");
			sb.append(tagName);
			sb.append(">");
			sb.append("</");
			sb.append(tagName);
			sb.append(">");
		} else {
			sb.append("<");
			sb.append(tagName);
			sb.append("/>");
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

	private static final String ROOT_TAG_KEY = "ROOT";
	private static final Set<String> ALL_TAGS = new HashSet<>();
	private static final Map<String, Set<String>> validTags = new HashMap<>();
	private static final Map<String, Set<String>> allowedParents = new HashMap<>();
	private static final Map<String, Set<String>> validAttributes = new HashMap<>();

	static {
		validTags(ROOT_TAG_KEY, // allowed top-level tags
				"BR", "FONT", "I", "B", "U", "O", "SUB", "SUP", "S", "TABLE");

		validTags("FONT", // allowed tags between <FONT> and </FONT>
				"TABLE", "BR", "FONT", "I", "B", "U", "O", "SUB", "SUP", "S");

		validTags("I", // allowed tags between <I> and </I>
				"BR", "FONT", "I", "B", "U", "O", "SUB", "SUP", "S");

		validTags("B", // allowed tags between <B> and </B>
				"BR", "FONT", "I", "B", "U", "O", "SUB", "SUP", "S");

		validTags("U", // allowed tags between <U> and </U>
				"BR", "FONT", "I", "B", "U", "O", "SUB", "SUP", "S");

		validTags("O", // allowed tags between <O> and </O>
				"BR", "FONT", "I", "B", "U", "O", "SUB", "SUP", "S");

		validTags("SUB", // allowed tags between <SUB> and </SUB>
				"BR", "FONT", "I", "B", "U", "O", "SUB", "SUP", "S");

		validTags("SUP", // allowed tags between <SUP> and </SUP>
				"BR", "FONT", "I", "B", "U", "O", "SUB", "SUP", "S");

		validTags("S", // allowed tags between <S> and </S>
				"BR", "FONT", "I", "B", "U", "O", "SUB", "SUP", "S");

		validTags("TABLE", // allowed tags between <TABLE> and </TABLE>
				"HR", "TR");

		validTags("TR", // allowed tags between <TR> and </TR>
				"VR", "TD");

		validTags("TD", // allowed tags between <TD> and </TD>
				"IMG", "BR", "FONT", "I", "B", "U", "O", "SUB", "SUP", "S",
				"TABLE");

		// find all tags
		for (Set<String> ts : validTags.values()) {
			ALL_TAGS.addAll(ts);
		}

		// compute allowed parents for each tag
		for (String tag : ALL_TAGS) {
			allowedParents.put(tag, new HashSet<>());
		}
		for (String parent : validTags.keySet()) {
			for (String tag : validTags.get(parent)) {
				allowedParents.get(tag).add(parent);
			}
		}

		// specify tags that can have attributes
		for (String t : new String[] { "TABLE", "TD", "FONT", "BR", "IMG" }) {
			validAttributes.put(t, new HashSet<>());
		}
		// add allowed attributes
		validAttributes("TABLE", // allowed <TABLE> tag attributes
				"ALIGN", "BGCOLOR", "BORDER", "CELLBORDER", "CELLPADDING",
				"CELLSPACING", "COLOR", "COLUMNS", "FIXEDSIZE", "GRADIENTANGLE",
				"HEIGHT", "HREF", "ID", "PORT", "ROWS", "SIDES", "STYLE",
				"TARGET", "TITLE", "TOOLTIP", "VALIGN", "WIDTH");

		validAttributes("TD", // allowed <TD> tag attributes
				"ALIGN", "BALIGN", "BGCOLOR", "BORDER", "CELLPADDING",
				"CELLSPACING", "COLOR", "COLSPAN", "FIXEDSIZE", "GRADIENTANGLE",
				"HEIGHT", "HREF", "ID", "PORT", "ROWSPAN", "SIDES", "STYLE",
				"TARGET", "TITLE", "TOOLTIP", "VALIGN", "WIDTH");

		validAttributes("FONT", // allowed <FONT> tag attributes
				"COLOR", "FACE", "POINT-SIZE");

		validAttributes("BR", // allowed <BR> tag attributes
				"ALIGN");

		validAttributes("IMG", // allowed <IMG> tag attributes
				"SCALE", "SRC");
	}

	/**
	 * Specify the valid child tags of a certain html tag.
	 * 
	 * @param tag
	 *            the parent tag to which valid child tags should be specified.
	 * @param childTags
	 *            the list of child tags that are valid within the parent tag.
	 */
	private static void validTags(String tag, String... childTags) {
		validTags.put(tag, new HashSet<String>(Arrays.asList(childTags)));
	}

	/**
	 * Specify the valid attributes of a certain html tag.
	 * 
	 * @param tag
	 *            the tag to which valid attributes should be specified.
	 * @param attributes
	 *            the list of attributes that are valid within the tag.
	 */
	private static void validAttributes(String tag, String... attributes) {
		validAttributes.get(tag).addAll(Arrays.asList(attributes));
	}

}
