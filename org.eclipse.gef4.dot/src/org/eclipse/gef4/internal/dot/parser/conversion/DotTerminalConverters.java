/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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
package org.eclipse.gef4.internal.dot.parser.conversion;

import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.impl.AbstractDeclarativeValueConverterService;
import org.eclipse.xtext.conversion.impl.IDValueConverter;
import org.eclipse.xtext.conversion.impl.STRINGValueConverter;

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
	private STRINGValueConverter quotedStringValueConverter;

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
		return value.isEmpty() || value.matches(".*\\s.*");
	}

	/**
	 * Turns the given {@link String} value into a quoted string, if it is not
	 * already quoted.
	 * 
	 * @param value
	 *            The {@link String} value to quote if needed.
	 * @return A quoted string that starts and ends with a quotation mark and
	 *         contains the given {@link String} value in between, or the
	 *         unmodified value, if it is already quoted.
	 */
	public static String quote(String value) {
		if (!isQuoted(value)) {
			return "\"" + value + "\"";
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
	public static boolean isQuoted(String value) {
		return value.startsWith("\"") && value.endsWith("\"");
	}

	/**
	 * Turns the given {@link String} value into an unquoted string, if it is
	 * quoted, i.e. removes the first and last character if the string starts
	 * and ends with a quotation mark.
	 * 
	 * @param value
	 *            The {@link String} value to unquote if needed.
	 * @return An unquoted string, where the first and last characters
	 *         (quotation marks) have been removed, or the unmodified value, if
	 *         it is not quoted.
	 */
	public static String unquote(String value) {
		if (isQuoted(value)) {
			return value.substring(1, value.length() - 1);
		}
		return value;
	}
}
