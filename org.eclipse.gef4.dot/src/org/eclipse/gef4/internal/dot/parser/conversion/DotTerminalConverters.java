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
	private IDValueConverter stringValueConverter;

	@Inject
	private IDValueConverter numeralValueConverter;

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
}
