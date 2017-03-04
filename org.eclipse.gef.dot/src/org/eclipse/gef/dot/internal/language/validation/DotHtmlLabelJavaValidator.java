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
 *     Tamas Miklossy   (itemis AG) - minor refactorings
 *     
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.validation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlAttr;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlContent;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlTag;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmllabelPackage;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.validation.Check;

/**
 * This class contains custom validation rules.
 *
 * See
 * https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
public class DotHtmlLabelJavaValidator extends
		org.eclipse.gef.dot.internal.language.validation.AbstractDotHtmlLabelJavaValidator {

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

	/**
	 * Checks if the given {@link HtmlTag} is properly closed. Generates errors
	 * if the html's open tag does not correspond to its close tag.
	 * 
	 * @param tag
	 *            The {@link HtmlTag} to check.
	 */
	@Check
	public void checkTagIsClosed(HtmlTag tag) {
		if (!tag.getName().toUpperCase()
				.equals(tag.getCloseName().toUpperCase())) {
			reportRangeBasedError(
					"Tag '<" + tag.getName() + ">' is not closed (expected '</"
							+ tag.getName() + ">' but got '</"
							+ tag.getCloseName() + ">').",
					tag, HtmllabelPackage.Literals.HTML_TAG__CLOSE_NAME);
		}
	}

	/**
	 * Checks if the given {@link HtmlTag} is properly closed. Generates errors
	 * if the html tag is self-closed where self-closing is not allowed.
	 * 
	 * @param tag
	 *            The {@link HtmlTag} to check.
	 */
	@Check
	public void checkSelfClosingTagIsAllowed(HtmlTag tag) {

		String[] selfClosingIsNotAllowed = { "B", "FONT", "I", "O", "S", "SUB",
				"SUP", "TABLE", "TD", "TR", "U" };

		String tagNameUpperCase = tag.getName().toUpperCase();

		if (tag.isSelfClosing() && Arrays.binarySearch(selfClosingIsNotAllowed,
				tagNameUpperCase) >= 0) {
			reportRangeBasedError(
					"Tag '<" + tag.getName() + "/>' cannot be self closing.",
					tag, HtmllabelPackage.Literals.HTML_TAG__NAME);
		}
	}

	/**
	 * Checks if a string literal is allowed in the given {@link HtmlTag}.
	 * Generates errors if the html tag is not allowed to contain a string
	 * literal.
	 * 
	 * @param tag
	 *            The {@link HtmlTag} to check.
	 */
	@Check
	public void checkStringLiteralIsAllowed(HtmlTag tag) {

		String[] stringLiteralIsNotAllowed = { "BR", "HR", "IMG", "TABLE", "TR",
				"VR" };

		String tagNameUpperCase = tag.getName().toUpperCase();

		if (Arrays.binarySearch(stringLiteralIsNotAllowed,
				tagNameUpperCase) >= 0) {

			for (HtmlContent child : tag.getChildren()) {
				// TODO: verify why white spaces is stored as text
				String text = child.getText();
				if (text != null && !text.trim().isEmpty()) {
					reportRangeBasedError(
							"Tag '<" + tag.getName()
									+ ">' cannot contain a string literal.",
							tag, HtmllabelPackage.Literals.HTML_TAG__NAME);
				}
			}
		}
	}

	/**
	 * Checks if the given {@link HtmlTag} is valid w.r.t. its parent (not all
	 * tags are allowed on all nesting levels). Generates errors when the given
	 * {@link HtmlTag} is not supported by Graphviz w.r.t. its parent.
	 * 
	 * @param tag
	 *            The {@link HtmlTag} to check.
	 */
	@Check
	public void checkTagNameIsValid(HtmlTag tag) {
		String tagName = tag.getName();
		if (!ALL_TAGS.contains(tagName.toUpperCase())) {
			reportRangeBasedError("Tag '<" + tagName + ">' is not supported.",
					tag, HtmllabelPackage.Literals.HTML_TAG__NAME);
		} else {
			// find parent tag
			EObject container = tag.eContainer().eContainer();
			HtmlTag parent = null;
			if (container instanceof HtmlTag) {
				parent = (HtmlTag) container;
			}

			// check if tag allowed inside parent or "root" if we could not find
			// a parent
			String parentName = parent == null ? ROOT_TAG_KEY
					: parent.getName();
			if (!validTags.containsKey(parentName.toUpperCase())
					|| !validTags.get(parentName.toUpperCase())
							.contains(tagName.toUpperCase())) {
				reportRangeBasedError(
						"Tag '<" + tagName + ">' is not allowed inside '<"
								+ parentName + ">', but only inside '<"
								+ String.join(">', '<",
										allowedParents
												.get(tagName.toUpperCase()))
								+ ">'.",
						tag, HtmllabelPackage.Literals.HTML_TAG__NAME);
			}
		}
	}

	/**
	 * Checks if the given {@link HtmlAttr} is valid w.r.t. its tag (only
	 * certain attributes are supported by the individual tags). Generates
	 * errors if the {@link HtmlAttr} is not supported by Graphviz w.r.t. its
	 * tag.
	 * 
	 * @param attr
	 *            The {@link HtmlAttr} to check.
	 */
	@Check
	public void checkAttributeNameIsValid(HtmlAttr attr) {
		String attrName = attr.getName();
		EObject container = attr.eContainer();
		if (container instanceof HtmlTag) {
			HtmlTag tag = (HtmlTag) container;
			String tagName = tag.getName();
			if (!validAttributes.containsKey(tagName.toUpperCase())
					|| !validAttributes.get(tagName.toUpperCase())
							.contains(attrName.toUpperCase())) {
				reportRangeBasedError(
						"Attribute '" + attrName + "' is not allowed inside '<"
								+ tagName + ">'.",
						attr, HtmllabelPackage.Literals.HTML_ATTR__NAME);
			}
		}
	}

	private void reportRangeBasedError(String message, EObject object,
			EStructuralFeature feature) {

		List<INode> nodes = NodeModelUtils.findNodesForFeature(object, feature);

		if (nodes.size() != 1) {
			throw new IllegalStateException(
					"Exact 1 node is expected for the feature, but got "
							+ nodes.size() + " node(s).");
		}

		INode node = nodes.get(0);
		int offset = node.getTotalOffset();
		int length = node.getLength();

		String code = null;
		String[] issueData = null;
		getMessageAcceptor().acceptError(message, object, offset, length, code,
				issueData);
	}
}
