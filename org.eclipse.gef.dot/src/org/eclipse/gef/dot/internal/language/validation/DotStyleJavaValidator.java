/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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
import java.util.TreeSet;

import org.eclipse.gef.dot.internal.DotAttributes.Context;
import org.eclipse.gef.dot.internal.language.style.EdgeStyle;
import org.eclipse.gef.dot.internal.language.style.NodeStyle;
import org.eclipse.gef.dot.internal.language.style.StyleItem;
import org.eclipse.gef.dot.internal.language.style.StylePackage;
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
		Context attributeContext = getAttributeContext();
		if (Context.NODE.equals(attributeContext)) {
			for (Object validValue : NodeStyle.values()) {
				if (validValue.toString().equals(styleItem.getName())) {
					return;
				}
			}
			// check each style item with the corresponding parser
			error("Value should be one of "
					+ getFormattedValues(NodeStyle.values()) + ".",
					StylePackage.Literals.STYLE__STYLE_ITEMS);
		} else if (Context.EDGE.equals(attributeContext)) {
			for (Object validValue : EdgeStyle.values()) {
				if (validValue.toString().equals(styleItem.getName())) {
					return;
				}
			}
			// check each style item with the corresponding parser
			error("Value should be one of "
					+ getFormattedValues(EdgeStyle.values()) + ".",
					StylePackage.Literals.STYLE__STYLE_ITEMS);
		}
	}

	private Context getAttributeContext() {
		// XXX: This context information is provided by the EObjectValidator
		Context attributeContext = (Context) getContext()
				.get(Context.class.getName());
		if (attributeContext == null) {
			throw new IllegalStateException("Attribute context not specified.");
		}
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