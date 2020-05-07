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
 *     Tamas Miklossy   (itemis AG) - implement additional validation checks (bug #321775)
 *     Zoey Prigge      (itemis AG) - implement duplicate attribute name error (bug #549410)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.validation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gef.dot.internal.language.htmllabel.DotHtmlLabelHelper;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlAttr;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlContent;
import org.eclipse.gef.dot.internal.language.htmllabel.HtmlLabel;
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
public class DotHtmlLabelValidator extends AbstractDotHtmlLabelValidator {

	/**
	 * Issue code for a non-properly-closed html tag.
	 */
	public static final String HTML_TAG_IS_NOT_PROPERLY_CLOSED = "html_tag_is_not_properly_closed";

	/**
	 * Issue code for a html tag where self-closing is not allowed.
	 */
	public static final String HTML_TAG_SELF_CLOSING_IS_NOT_ALLOWED = "html_tag_self_closing_is_not_allowed";

	/**
	 * Issue code for an invalid html tag name.
	 */
	public static final String HTML_TAG_INVALID_TAG_NAME = "html_tag_invalid_tag_name";

	/**
	 * Issue code for an invalid html attribute name.
	 */
	public static final String HTML_ATTRIBUTE_INVALID_ATTRIBUTE_NAME = "html_attribute_invalid_attribute_name";

	/**
	 * Issue code for an invalid html attribute value.
	 */
	public static final String HTML_ATTRIBUTE_INVALID_ATTRIBUTE_VALUE = "html_attribute_invalid_attribute_value";

	/**
	 * Issue code for an invalid html attribute value.
	 */
	public static final String HTML_ATTRIBUTE_DUPLICATE_ATTRIBUTE_NAME = "html_attribute_duplicate_attribute_name";

	/**
	 * Checks if the given {@link HtmlLabel}'s parts are valid siblings to each
	 * other. Generates errors if the label's parts contains invalid siblings.
	 *
	 * @param label
	 *            The {@link HtmlLabel} of that's parts are to be checked.
	 */
	@Check
	public void checkHtmlLabelPartsAreValidSiblings(HtmlLabel label) {
		checkSiblingsAreValid(label.getParts());
	}

	/**
	 * Checks if the given {@link HtmlTag}'s children are valid siblings to each
	 * other. Generates errors if the tag's children contains invalid siblings.
	 *
	 * @param tag
	 *            The {@link HtmlTag} of that's children are to be checked.
	 */
	@Check
	public void checkHtmlTagChildrenAreValidSiblings(HtmlTag tag) {
		checkSiblingsAreValid(tag.getChildren());
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
		if (!tag.isSelfClosing() && !tag.getName().toUpperCase()
				.equals(tag.getCloseName().toUpperCase())) {
			reportRangeBasedError(HTML_TAG_IS_NOT_PROPERLY_CLOSED,
					"Tag '<" + tag.getName() + ">' is not closed (expected '</"
							+ tag.getName() + ">' but got '</"
							+ tag.getCloseName() + ">').",
					tag, HtmllabelPackage.Literals.HTML_TAG__CLOSE_NAME,
					new String[] { tag.getName(), tag.getCloseName() });
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

		String tagNameUpperCase = tag.getName().toUpperCase();

		if (tag.isSelfClosing() && DotHtmlLabelHelper.getNonSelfClosingTags()
				.contains(tagNameUpperCase)) {
			reportRangeBasedError(HTML_TAG_SELF_CLOSING_IS_NOT_ALLOWED,
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
					reportRangeBasedError(null,
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
		if (!DotHtmlLabelHelper.getAllTags().contains(tagName.toUpperCase())) {
			reportRangeBasedError(HTML_TAG_INVALID_TAG_NAME,
					"Tag '<" + tagName + ">' is not supported.", tag,
					HtmllabelPackage.Literals.HTML_TAG__NAME);
		} else {
			// find parent tag
			EObject container = tag.eContainer().eContainer();
			HtmlTag parent = null;
			if (container instanceof HtmlTag) {
				parent = (HtmlTag) container;
			}

			// check if tag allowed inside parent or "root" if we could not find
			// a parent
			String parentName = parent == null
					? DotHtmlLabelHelper.getRootTagKey()
					: parent.getName();
			Map<String, Set<String>> validTags = DotHtmlLabelHelper
					.getValidTags();
			if (!validTags.containsKey(parentName.toUpperCase())
					|| !validTags.get(parentName.toUpperCase())
							.contains(tagName.toUpperCase())) {
				reportRangeBasedError(HTML_TAG_INVALID_TAG_NAME,
						"Tag '<" + tagName + ">' is not allowed inside '<"
								+ parentName + ">', but only inside '<"
								+ String.join(">', '<",
										DotHtmlLabelHelper.getAllowedParents()
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
			Map<String, Set<String>> validAttributes = DotHtmlLabelHelper
					.getValidAttributes();
			if (!validAttributes.containsKey(tagName.toUpperCase())
					|| !validAttributes.get(tagName.toUpperCase())
							.contains(attrName.toUpperCase())) {
				reportRangeBasedError(HTML_ATTRIBUTE_INVALID_ATTRIBUTE_NAME,
						"Attribute '" + attrName + "' is not allowed inside '<"
								+ tagName + ">'.",
						attr, HtmllabelPackage.Literals.HTML_ATTR__NAME);
			}
		}
	}

	/**
	 * Checks if the value of a given {@link HtmlAttr} is valid. Generates
	 * errors if the value of a given {@link HtmlAttr} is not supported by
	 * Graphviz.
	 *
	 * @param attr
	 *            The {@link HtmlAttr} of that's attribute value is to check.
	 */
	@Check
	public void checkAttributeValueIsValid(HtmlAttr attr) {
		String htmlAttributeName = attr.getName();
		// trim the leading and trailing (single or double) quotes if necessary
		String htmlAttributeValue = removeQuotes(attr.getValue());
		EObject container = attr.eContainer();
		if (container instanceof HtmlTag) {
			HtmlTag tag = (HtmlTag) container;
			String htmlTagName = tag.getName();
			String message = getAttributeValueErrorMessage(htmlTagName,
					htmlAttributeName, htmlAttributeValue);
			if (message != null) {
				reportRangeBasedError(HTML_ATTRIBUTE_INVALID_ATTRIBUTE_VALUE,
						"The value '" + htmlAttributeValue
								+ "' is not a correct " + htmlAttributeName
								+ ": " + message,
						attr, HtmllabelPackage.Literals.HTML_ATTR__VALUE);
			}
		}
	}

	/**
	 * Checks if a given {@link HtmlTag} has {@link HtmlAttr}s of the same name.
	 * Generates errors if multiple {@link HtmlAttr}s have the same name as this
	 * is not supported by Graphviz.
	 *
	 * @param tag
	 *            The {@link HtmlTag} being checked.
	 */
	@Check
	public void checkAttributeNameIsNotDuplicate(HtmlTag tag) {
		Map<String, HtmlAttr> definedAttributes = new HashMap<>();
		Set<HtmlAttr> duplicateAttrSet = new HashSet<>();

		for (HtmlAttr attr : tag.getAttributes()) {
			String attrNameLower = attr.getName().toLowerCase(Locale.ENGLISH);
			if (definedAttributes.putIfAbsent(attrNameLower, attr) != null) {
				duplicateAttrSet.add(attr);
				duplicateAttrSet.add(definedAttributes.get(attrNameLower));
			}
		}

		for (HtmlAttr attr : duplicateAttrSet) {
			reportRangeBasedError(HTML_ATTRIBUTE_DUPLICATE_ATTRIBUTE_NAME,
					"The attribute '" + attr.getName()
							+ "' is defined more than once.",
					attr, HtmllabelPackage.Literals.HTML_ATTR__NAME);
		}
	}

	private void checkSiblingsAreValid(List<HtmlContent> siblings) {
		if (!DotHtmlLabelHelper.isValidSiblings(siblings)) {
			for (HtmlContent htmlText : siblings) {
				if (htmlText.getTag() != null) {
					// if the htmlContent has a tag, mark the tag name as error
					// prone text
					reportRangeBasedError(null, "Invalid siblings.",
							htmlText.getTag(),
							HtmllabelPackage.Literals.HTML_TAG__NAME);
				} else {
					// otherwise, mark the text as error prone text
					reportRangeBasedError(null, "Invalid siblings.", htmlText,
							HtmllabelPackage.Literals.HTML_CONTENT__TEXT);
				}
			}
		}
	}

	private String removeQuotes(String value) {
		if (value.startsWith("\"") || value.startsWith("'")) {
			value = value.substring(1);
		}
		if (value.endsWith("\"") || value.endsWith("'")) {
			value = value.substring(0, value.length() - 1);
		}
		return value;
	}

	/**
	 * Determines whether the given html attribute value is valid or not.
	 *
	 * @param htmlTagName
	 *            The html tag name
	 * @param htmlAttributeName
	 *            The html attribute name
	 * @param htmlAttributeValue
	 *            The html attribute value
	 * @return Null if the html attribute is valid, the error message otherwise.
	 */
	private String getAttributeValueErrorMessage(String htmlTagName,
			String htmlAttributeName, String htmlAttributeValue) {
		if ("BR".equalsIgnoreCase(htmlTagName)) { //$NON-NLS-1$
			switch (htmlAttributeName.toUpperCase()) {
			case "ALIGN": //$NON-NLS-1$
				return getEnumAttributeValueErrorMessage(htmlAttributeValue,
						"CENTER", "LEFT", //$NON-NLS-1$
						"RIGHT");
			default:
				break;
			}
		}

		if ("IMG".equalsIgnoreCase(htmlTagName)) { //$NON-NLS-1$
			switch (htmlAttributeName.toUpperCase()) {
			case "SCALE": //$NON-NLS-1$
				return getEnumAttributeValueErrorMessage(htmlAttributeValue,
						"FALSE", "TRUE", //$NON-NLS-1$ //$NON-NLS-2$
						"WIDTH", "HEIGHT", "BOTH"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			default:
				break;
			}
		}

		if ("TABLE".equalsIgnoreCase(htmlTagName)) { //$NON-NLS-1$
			switch (htmlAttributeName.toUpperCase()) {
			case "ALIGN": //$NON-NLS-1$
				return getEnumAttributeValueErrorMessage(htmlAttributeValue,
						"CENTER", "LEFT", //$NON-NLS-1$ //$NON-NLS-2$
						"RIGHT"); //$NON-NLS-1$
			case "BORDER":
				return getNumberAttributeValueErrorMessage(htmlAttributeValue,
						0, 255);
			case "CELLBORDER":
				return getNumberAttributeValueErrorMessage(htmlAttributeValue,
						0, 127);
			case "CELLPADDING":
				return getNumberAttributeValueErrorMessage(htmlAttributeValue,
						0, 255);
			case "CELLSPACING":
				return getNumberAttributeValueErrorMessage(htmlAttributeValue,
						0, 127);
			case "COLUMNS":
			case "ROWS":
				return getEnumAttributeValueErrorMessage(htmlAttributeValue,
						"*");
			case "FIXEDSIZE": //$NON-NLS-1$
				return getEnumAttributeValueErrorMessage(htmlAttributeValue,
						"FALSE", "TRUE"); //$NON-NLS-1$ //$NON-NLS-2$
			case "HEIGHT":
			case "WIDTH":
				return getNumberAttributeValueErrorMessage(htmlAttributeValue,
						0, 65535);
			case "SIDES": //$NON-NLS-1$
				return getSidesAttributeValueErrorMessage(htmlAttributeValue);
			case "VALIGN": //$NON-NLS-1$
				return getEnumAttributeValueErrorMessage(htmlAttributeValue,
						"MIDDLE", "BOTTOM", //$NON-NLS-1$ //$NON-NLS-2$
						"TOP");
			default:
				break;
			}
		}

		if ("TD".equalsIgnoreCase(htmlTagName)) { //$NON-NLS-1$
			switch (htmlAttributeName.toUpperCase()) {
			case "ALIGN": //$NON-NLS-1$
				return getEnumAttributeValueErrorMessage(htmlAttributeValue,
						"CENTER", "LEFT", //$NON-NLS-1$ //$NON-NLS-2$
						"RIGHT", "TEXT"); //$NON-NLS-1$ //$NON-NLS-2$
			case "BALIGN": //$NON-NLS-1$
				return getEnumAttributeValueErrorMessage(htmlAttributeValue,
						"CENTER", "LEFT", //$NON-NLS-1$ //$NON-NLS-2$
						"RIGHT"); //$NON-NLS-1$
			case "BORDER":
				return getNumberAttributeValueErrorMessage(htmlAttributeValue,
						0, 255);
			case "CELLPADDING":
				return getNumberAttributeValueErrorMessage(htmlAttributeValue,
						0, 255);
			case "CELLSPACING":
				return getNumberAttributeValueErrorMessage(htmlAttributeValue,
						0, 127);
			case "COLSPAN":
			case "ROWSPAN":
				return getNumberAttributeValueErrorMessage(htmlAttributeValue,
						1, 65535);
			case "FIXEDSIZE": //$NON-NLS-1$
				return getEnumAttributeValueErrorMessage(htmlAttributeValue,
						"FALSE", "TRUE"); //$NON-NLS-1$ //$NON-NLS-2$
			case "HEIGHT":
			case "WIDTH":
				return getNumberAttributeValueErrorMessage(htmlAttributeValue,
						0, 65535);
			case "SIDES": //$NON-NLS-1$
				return getSidesAttributeValueErrorMessage(htmlAttributeValue);
			case "VALIGN": //$NON-NLS-1$
				return getEnumAttributeValueErrorMessage(htmlAttributeValue,
						"MIDDLE", //$NON-NLS-1$
						"BOTTOM", "TOP"); //$NON-NLS-1$ //$NON-NLS-2$
			default:
				break;
			}
		}

		// html attribute values, that cannot be verified, are considered as
		// valid.
		return null;
	}

	private String getEnumAttributeValueErrorMessage(String currentValue,
			String... allowedValues) {
		List<String> allowedValuesList = Arrays.asList(allowedValues);

		if (allowedValuesList.contains(currentValue.toUpperCase())) {
			return null;
		}

		String formattedAllowedValues = allowedValuesList.stream()
				.map(e -> "'" + e + "'").collect(Collectors.joining(", "));

		return "Value has to be " + (allowedValues.length > 1 ? "one of " : "")
				+ formattedAllowedValues + ".";
	}

	private String getNumberAttributeValueErrorMessage(String currentValue,
			int minimum, int maximum) {
		boolean isValid = true;

		try {
			int currentValueParsed = Integer.parseInt(currentValue);
			isValid = minimum <= currentValueParsed
					&& currentValueParsed <= maximum;
		} catch (NumberFormatException e) {
			isValid = false;
		}

		if (isValid) {
			return null;
		} else {
			return String.format("Value has to be between %1$d and %2$d.",
					minimum, maximum);
		}
	}

	private String getSidesAttributeValueErrorMessage(
			String htmlAttributeValue) {
		if (htmlAttributeValue.isEmpty()) {
			return "Value has to contain only the 'L', 'T', 'R', 'B' characters.";
		}

		for (int i = 0; i < htmlAttributeValue.length(); i++) {
			String subString = Character.toString(htmlAttributeValue.charAt(i))
					.toUpperCase();
			if (!"LTRB".contains(subString)) {
				return "Value has to contain only the 'L', 'T', 'R', 'B' characters.";
			}
		}
		return null;
	}

	private void reportRangeBasedError(String issueCode, String message,
			EObject object, EStructuralFeature feature) {
		reportRangeBasedError(issueCode, message, object, feature, null);
	}

	private void reportRangeBasedError(String issueCode, String message,
			EObject object, EStructuralFeature feature, String[] issueData) {

		List<INode> nodes = NodeModelUtils.findNodesForFeature(object, feature);

		if (nodes.size() != 1) {
			throw new IllegalStateException(
					"Exact 1 node is expected for the feature, but got "
							+ nodes.size() + " node(s).");
		}

		INode node = nodes.get(0);
		int offset = node.getTotalOffset();
		int length = node.getLength();

		getMessageAcceptor().acceptError(message, object, offset, length,
				issueCode, issueData);
	}

}
