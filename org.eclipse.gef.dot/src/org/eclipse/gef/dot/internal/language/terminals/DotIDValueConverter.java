/*******************************************************************************
 * Copyright (c) 2016, 2018 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.terminals;

import org.eclipse.gef.dot.internal.language.terminals.ID.Type;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;

/**
 * A value converter for Dot ID values.
 *
 * @author anyssen
 *
 */
public class DotIDValueConverter implements IValueConverter<ID> {

	@Override
	public ID toValue(String string, INode node)
			throws ValueConverterException {
		if (string == null) {
			return null;
		}
		if (node == null) {
			return ID.fromString(string);
		}

		for (ILeafNode leaf : node.getLeafNodes()) {
			Object grammarElement = leaf.getGrammarElement();
			if (grammarElement instanceof RuleCall) {
				RuleCall lexerRuleCall = (RuleCall) grammarElement;
				AbstractRule nestedLexerRule = lexerRuleCall.getRule();
				String nestedLexerRuleName = nestedLexerRule.getName();
				if ("COMPASS_PT".equals(nestedLexerRuleName)) {
					nestedLexerRuleName = "STRING";
				}
				return ID.fromString(string, Type.valueOf(nestedLexerRuleName));
			}
		}
		throw new IllegalArgumentException("Invalid ID string " + string);
	}

	@Override
	public String toString(ID value) throws ValueConverterException {
		if (value == null) {
			return null;
		}
		return value.toString();
	}

}
