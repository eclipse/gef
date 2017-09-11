/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.validation;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gef.dot.internal.DotAttributes.Context;
import org.eclipse.gef.dot.internal.language.style.EdgeStyle;
import org.eclipse.gef.dot.internal.language.style.NodeStyle;
import org.eclipse.gef.dot.internal.language.style.StyleItem;
import org.eclipse.gef.dot.internal.language.style.StylePackage;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.validation.Check;

/**
 * This class contains custom validation rules.
 *
 * See
 * https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
public class DotStyleJavaValidator extends
		org.eclipse.gef.dot.internal.language.validation.AbstractDotStyleJavaValidator {

	/**
	 * Validates that the used {@link StyleItem}s are applicable in the
	 * respective {@link Context}.
	 *
	 * @param styleItem
	 *            The {@link StyleItem} to check.
	 */
	@Check
	public void checkStyleItemConformsToContext(StyleItem styleItem) {
		// The use of setlinewidth is deprecated, but still valid
		if (styleItem.getName().equals("setlinewidth")) {
			return;
		}

		Context attributeContext = getAttributeContext();
		if (Context.NODE.equals(attributeContext)) {
			for (Object validValue : NodeStyle.values()) {
				if (validValue.toString().equals(styleItem.getName())) {
					return;
				}
			}
			// check each style item with the corresponding parser
			reportRangeBaseError(
					"Value should be one of "
							+ getFormattedValues(NodeStyle.values()) + ".",
					styleItem, StylePackage.Literals.STYLE_ITEM__NAME,
					attributeContext);
		} else if (Context.EDGE.equals(attributeContext)) {
			for (Object validValue : EdgeStyle.values()) {
				if (validValue.toString().equals(styleItem.getName())) {
					return;
				}
			}
			// check each style item with the corresponding parser
			reportRangeBaseError(
					"Value should be one of "
							+ getFormattedValues(EdgeStyle.values()) + ".",
					styleItem, StylePackage.Literals.STYLE_ITEM__NAME,
					attributeContext);
		}
		// do nothing if the DOT attribute context cannot be determined. In such
		// cases this validation rule should have no effect.
	}

	/**
	 * Validates that the used {@link StyleItem}s are not deprecated. Generates
	 * warnings in case of the usage of deprecated style items.
	 *
	 * @param styleItem
	 *            The {@link StyleItem} to check.
	 */
	@Check
	public void checkDeprecatedStyleItem(StyleItem styleItem) {
		if (styleItem.getName().equals("setlinewidth")) {
			reportRangeBasedWarning(
					"The usage of setlinewidth is deprecated, use the penwidth attribute instead.",
					styleItem, StylePackage.Literals.STYLE_ITEM__NAME);
		}
	}

	private void reportRangeBasedWarning(String message, StyleItem styleItem,
			EStructuralFeature feature) {

		List<INode> nodes = NodeModelUtils.findNodesForFeature(styleItem,
				feature);

		if (nodes.size() != 1) {
			throw new IllegalStateException(
					"Exact 1 node is expected for the feature, but got "
							+ nodes.size() + " node(s).");
		}

		INode node = nodes.get(0);
		int offset = node.getTotalOffset();
		int length = node.getLength();

		String code = null;
		// the issueData will be evaluated by the quickfixes
		String[] issueData = { styleItem.getName() };
		getMessageAcceptor().acceptWarning(message, styleItem, offset, length,
				code, issueData);
	}

	private void reportRangeBaseError(String message, StyleItem styleItem,
			EStructuralFeature feature, Context attributeContext) {

		List<INode> nodes = NodeModelUtils.findNodesForFeature(styleItem,
				feature);

		if (nodes.size() != 1) {
			throw new IllegalStateException(
					"Exact 1 node is expected for the feature, but got "
							+ nodes.size() + " node(s).");
		}

		INode node = nodes.get(0);
		int offset = node.getTotalOffset();
		int length = node.getLength();

		String code = null;
		// the issueData will be evaluated by the quickfixes
		String[] issueData = { styleItem.getName(),
				attributeContext.toString() };
		getMessageAcceptor().acceptError(message, styleItem, offset, length,
				code, issueData);
	}

	private Context getAttributeContext() {
		// XXX: This context information is provided by the EObjectValidator
		Context attributeContext = (Context) getContext()
				.get(Context.class.getName());
		return attributeContext;
	}

	private String getFormattedValues(Object[] values) {
		StringBuilder sb = new StringBuilder();
		for (Object value : new TreeSet<>(Arrays.asList(values))) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append("'" + value + "'");
		}
		return sb.toString();
	}
}