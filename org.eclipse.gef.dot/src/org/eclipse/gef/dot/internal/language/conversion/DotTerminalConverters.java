/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.conversion;

import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractDeclarativeValueConverterService;
import org.eclipse.xtext.conversion.impl.IDValueConverter;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;

import com.google.inject.Inject;

/**
 * A terminal converter for Dot.
 * 
 * @author anyssen
 *
 */
public class DotTerminalConverters
		extends AbstractDeclarativeValueConverterService {

	@Inject
	private IDValueConverter compassPtValueConverter;

	@Inject
	private IDValueConverter numeralValueConverter;

	@Inject
	private DotStringValueConverter stringValueConverter;

	@Inject
	private DotQuotedStringValueConverter quotedStringValueConverter;

	/**
	 * A {@link ValueConverter} for Dot "STRING" terminals:
	 * 
	 * <pre>
	 * terminal STRING: 
	 * ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*;
	 * </pre>
	 * 
	 * @return The {@link String} value converted from a "STRING" terminal.
	 */
	@ValueConverter(rule = "STRING")
	public IValueConverter<String> STRING() {
		return stringValueConverter;
	}

	@Override
	public Object toValue(String string, String lexerRule, INode node)
			throws ValueConverterException {
		if ("ID".equals(lexerRule)) {
			for (ILeafNode leaf : node.getLeafNodes()) {
				Object grammarElement = leaf.getGrammarElement();
				if (grammarElement instanceof RuleCall) {
					RuleCall lexerRuleCall = (RuleCall) grammarElement;
					AbstractRule nestedLexerRule = lexerRuleCall.getRule();
					return super.toValue(string, nestedLexerRule.getName(),
							node);
				}
			}
		}
		return super.toValue(string, lexerRule, node);
	}

	@Override
	public String toString(Object value, String lexerRule) {
		if (lexerRule.equals("ID")) {
			if (value instanceof String) {
				// use quoted string where needed
				if (needsToBeQuoted((String) value)) {
					return super.toString(value, "QUOTED_STRING");
				}
			}
		}
		return super.toString(value, lexerRule);
	}

	/**
	 * A {@link ValueConverter} for Dot "NUMERAL" terminals:
	 * 
	 * <pre>
	 * terminal NUMERAL:
	 * ('-')? ('.' ('0'..'9')+) | ('0'..'9')+ ('.' ('0'..'9')*)?;
	 * </pre>
	 * 
	 * @return The {@link String} value converted from a "NUMERAL" terminal.
	 */
	@ValueConverter(rule = "NUMERAL")
	public IValueConverter<String> NUMERAL() {
		return numeralValueConverter;
	}

	/**
	 * A {@link ValueConverter} for Dot "NUMERAL" terminals:
	 * 
	 * <pre>
	 * terminal COMPASS_PT:
	 * 'n' | 'ne' | 'e' | 'se' | 's' | 'sw' | 'w' | 'nw' | 'c' | '_';
	 * </pre>
	 * 
	 * @return The {@link String} value converted from a "NUMERAL" terminal.
	 */
	@ValueConverter(rule = "COMPASS_PT")
	public IValueConverter<String> COMPASS_PT() {
		return compassPtValueConverter;
	}

	/**
	 * A {@link ValueConverter} for Dot "QUOTED_STRING" terminals:
	 * 
	 * <pre>
	 * terminal QUOTED_STRING:
	 * ('"' ('\\' ('b' | 't' | 'n' | 'f' | 'r' | 'u' | '"' | "'" | '\\') | !('\\' | '"'))* '"');
	 * </pre>
	 * 
	 * @return The {@link String} value converted from a "QUOTED_STRING"
	 *         terminal.
	 */
	@ValueConverter(rule = "QUOTED_STRING")
	public IValueConverter<String> QUOTED_STRING() {
		return quotedStringValueConverter;
	}

	/**
	 * Tests whether a given {@link String} value should be quoted because it
	 * contains whitespace characters.
	 * 
	 * @param value
	 *            The {@link String} value to test.
	 * @return <code>true</code> if the given value contains whitespace
	 *         characters.
	 */
	// TODO: check for keywords as well, which need to be quoted in addition
	public static boolean needsToBeQuoted(String value) {
		// FIXME: if it contains quotes, it needs to be quoted
		return value.isEmpty() || value.matches(".*\\s.*");
	}

	/**
	 * Turns the given {@link String} value into a quoted string, if it is not
	 * already quoted, i.e. adds quotation marks to start and end, and escapes
	 * any contained quotes.
	 * 
	 * @param value
	 *            The {@link String} value to quote if needed.
	 * @return A quoted string that starts and ends with a quotation mark and
	 *         contains the given {@link String} value in between, or the
	 *         unmodified value, if it is already quoted.
	 */
	public static String quote(String value) {
		if (!isQuoted(value)) {
			return "\"" + value.replaceAll("\"", "\\\\\"") + "\"";
		}
		return value;
	}

	/**
	 * Tests whether the given {@link String} value is quoted, i.e. has
	 * quotation marks as its first and last character respectively.
	 * 
	 * @param value
	 *            The {@link String} value to test.
	 * @return <code>true</code> if the given value starts and ends with a
	 *         quotation mark, <code>false</code> otherwise.
	 */
	protected static boolean isQuoted(String value) {
		return value.startsWith("\"") && value.endsWith("\"");
	}

	/**
	 * Turns the given {@link String} value into an unquoted string, i.e.
	 * removes the first and last character if the string starts and ends with a
	 * quotation mark and transfers all escaped quotes into normal quotes.
	 * 
	 * @param value
	 *            The {@link String} value to unquote if needed.
	 * @return An unquoted string, where the first and last characters
	 *         (quotation marks) have been removed, or the unmodified value, if
	 *         it is not quoted.
	 */
	public static String unquote(String value) {
		if (value == null) {
			return null;
		}
		return value
				/* In DOT, an ID can be quoted... */
				.replaceAll("^\"|\"$", "") //$NON-NLS-1$//$NON-NLS-2$
				/*
				 * ...and may contain escaped quotes (see footnote on
				 * http://www.graphviz.org/doc/info/lang.html)
				 */
				.replaceAll("\\\\\"", "\""); //$NON-NLS-1$//$NON-NLS-2$
	}
}
