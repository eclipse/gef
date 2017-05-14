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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gef.dot.internal.language.htmllabel.DotHtmlLabelHelper;
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

		String tagNameUpperCase = tag.getName().toUpperCase();

		if (tag.isSelfClosing() && DotHtmlLabelHelper.getNonSelfClosingTags()
				.contains(tagNameUpperCase)) {
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
		if (!DotHtmlLabelHelper.getAllTags().contains(tagName.toUpperCase())) {
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
			String parentName = parent == null
					? DotHtmlLabelHelper.getRootTagKey() : parent.getName();
			Map<String, Set<String>> validTags = DotHtmlLabelHelper
					.getValidTags();
			if (!validTags.containsKey(parentName.toUpperCase())
					|| !validTags.get(parentName.toUpperCase())
							.contains(tagName.toUpperCase())) {
				reportRangeBasedError(
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
