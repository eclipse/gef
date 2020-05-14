/*******************************************************************************
 * Copyright (c) 2016, 2020 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Tamas Miklossy  (itemis AG) - implement additional validation rules
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.dot.internal.DotAttributes.Context;
import org.eclipse.gef.dot.internal.language.style.ClusterStyle;
import org.eclipse.gef.dot.internal.language.style.EdgeStyle;
import org.eclipse.gef.dot.internal.language.style.NodeStyle;
import org.eclipse.gef.dot.internal.language.style.Style;
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
 *
 */
public class DotStyleValidator extends AbstractDotStyleValidator {

	/**
	 * Issue code for a deprecated style item.
	 */
	public static final String DEPRECATED_STYLE_ITEM = "deprecated_style_item";

	/**
	 * Issue code for a duplicated style item.
	 */
	public static final String DUPLICATED_STYLE_ITEM = "duplicated_style_item";

	/**
	 * Issue code for an invalid style item.
	 */
	public static final String INVALID_STYLE_ITEM = "invalid_style_item";

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
		String name = styleItem.getName();
		if (name.equals("setlinewidth")) {
			return;
		}

		Context attributeContext = getAttributeContext();

		switch (attributeContext) {
		case GRAPH:
		case CLUSTER:
			validateStyleItem(styleItem, ClusterStyle.VALUES, attributeContext);
			break;
		case NODE:
			validateStyleItem(styleItem, NodeStyle.VALUES, attributeContext);
			break;
		case EDGE:
			validateStyleItem(styleItem, EdgeStyle.VALUES, attributeContext);
			break;
		default:
			// do nothing if the DOT attribute context cannot be determined. In
			// such
			// cases this validation rule should have no effect.
			break;
		}
	}

	private void validateStyleItem(StyleItem styleItem, List<?> validValues,
			Context attributeContext) {
		for (Object validValue : validValues) {
			if (validValue.toString().equals(styleItem.getName())) {
				return;
			}
		}
		// check each style item with the corresponding parser
		reportRangeBaseError(
				INVALID_STYLE_ITEM, "Value should be one of "
						+ getFormattedValues(validValues) + ".",
				styleItem, attributeContext);
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
			reportRangeBasedWarning(DEPRECATED_STYLE_ITEM,
					"The usage of setlinewidth is deprecated, use the penwidth attribute instead.",
					styleItem);
		}
	}

	/**
	 * Validates that the used {@link Style} does not contains duplicates.
	 * Generates warnings in case of the usage of duplicated style items.
	 *
	 * @param style
	 *            The {@link Style} to check.
	 */
	@Check
	public void checkDuplicatedStyleItem(Style style) {
		Set<String> definedStyles = new HashSet<>();

		EList<StyleItem> styleItems = style.getStyleItems();
		// iterate backwards as the last styleItem value will be used
		for (int i = styleItems.size() - 1; i >= 0; i--) {
			StyleItem styleItem = styleItems.get(i);
			String name = styleItem.getName();
			if (!definedStyles.add(name)) {
				reportRangeBasedWarning(DUPLICATED_STYLE_ITEM,
						"The style value '" + name + "' is duplicated.",
						styleItem);
			}
		}
	}

	private void reportRangeBasedWarning(String issueCode, String message,
			StyleItem styleItem) {

		List<INode> nodes = NodeModelUtils.findNodesForFeature(styleItem,
				StylePackage.Literals.STYLE_ITEM__NAME);

		if (nodes.size() != 1) {
			throw new IllegalStateException(
					"Exact 1 node is expected for the feature, but got "
							+ nodes.size() + " node(s).");
		}

		INode node = nodes.get(0);
		int offset = node.getTotalOffset();
		int length = node.getLength();

		// the issueData will be evaluated by the quickfixes
		List<String> issueData = new ArrayList<>();
		issueData.add(issueCode);
		issueData.add(styleItem.getName());
		issueData.addAll(styleItem.getArgs());

		getMessageAcceptor().acceptWarning(message, styleItem, offset, length,
				issueCode, issueData.toArray(new String[0]));
	}

	private void reportRangeBaseError(String issueCode, String message,
			StyleItem styleItem, Context attributeContext) {

		List<INode> nodes = NodeModelUtils.findNodesForFeature(styleItem,
				StylePackage.Literals.STYLE_ITEM__NAME);

		if (nodes.size() != 1) {
			throw new IllegalStateException(
					"Exact 1 node is expected for the feature, but got "
							+ nodes.size() + " node(s).");
		}

		INode node = nodes.get(0);
		int offset = node.getTotalOffset();
		int length = node.getLength();

		// the issueData will be evaluated by the quickfixes
		String[] issueData = { issueCode, styleItem.getName(),
				attributeContext.toString() };
		getMessageAcceptor().acceptError(message, styleItem, offset, length,
				issueCode, issueData);
	}

	private Context getAttributeContext() {
		// XXX: This context information is provided by the EObjectValidator
		Context attributeContext = (Context) getContext()
				.get(Context.class.getName());
		return attributeContext;
	}

	private String getFormattedValues(List<?> values) {
		StringBuilder sb = new StringBuilder();
		for (Object value : new TreeSet<>(values)) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append("'" + value + "'");
		}
		return sb.toString();
	}

}
