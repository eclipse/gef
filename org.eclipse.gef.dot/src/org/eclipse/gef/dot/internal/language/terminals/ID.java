/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
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
package org.eclipse.gef.dot.internal.language.terminals;

import java.util.regex.Pattern;

/**
 * A representation of a Dot ID according to the Graphviz DOT grammar.
 *
 * @author anyssen
 */
public class ID {

	// ('a'..'z' | 'A'..'Z' | '\u0080'..'\u00FF' | '_') ('a'..'z' | 'A'..'Z' |
	// '\u0080'..'\u00FF' | '_' | '0'..'9')*
	private static final Pattern STRING_PATTERN = Pattern
			.compile("[a-zA-Z\\u0080-\\u00FF_][a-zA-Z\\u0080-\\u00FF_0-9]*");

	// ('-')? ('.' ('0'..'9')+) | ('0'..'9')+ ('.'('0'..'9')*)?;
	private static final Pattern NUMERAL_PATTERN = Pattern
			.compile("-?(\\.\\d+)|(\\d+(\\.\\d*)?)");

	private String string;
	private Type type;

	/**
	 * The terminal type of the Dot ID according to the DOT grammar.
	 */
	public enum Type {

		/**
		 * A numeral ID that complies with the NUMERAL terminal rule:
		 *
		 * <pre>
		* terminal NUMERAL:
		* ('-')? ('.' ('0'..'9')+) | ('0'..'9')+ ('.' ('0'..'9')*)?;
		 * </pre>
		 */
		NUMERAL,
		/**
		 * A string ID that complies with the STRING terminal rule:
		 *
		 * <pre>
		* terminal STRING:
		* ('a'..'z' | 'A'..'Z' | '\u0080'..'\u00FF' | '_') ('a'..'z' | 'A'..'Z' | '\u0080'..'\u00FF' | '_' | '0'..'9')*;
		 * </pre>
		 */
		STRING,
		/**
		 * A quoted string ID that complies to the QUOTED_STRING terminal rule:
		 *
		 * <pre>
		* terminal QUOTED_STRING:
		* ('"' ('\\' ('b' | 't' | 'n' | 'f' | 'r' | 'u' | '"' | "'" | '\\') | !('\\' | '"'))* '"');
		 * </pre>
		 */
		QUOTED_STRING,
		/**
		 * A html string ID that complies to the HTML_STRING terminal rule (and
		 * its fragments):
		 *
		 * <pre>
		 * HTML_STRING : '<' HTML_CONTENT* '>' ;
		 * fragment HTML_CONTENT : (HTML_TAG | HTML_PCDATA) ;
		 * fragment HTML_TAG : HTML_TAG_START_OPEN HTML_TAG_DATA ( HTML_TAG_EMPTY_CLOSE | HTML_TAG_CLOSE (HTML_CONTENT)* HTML_TAG_END_OPEN HTML_TAG_DATA HTML_TAG_CLOSE);
		 * fragment HTML_TAG_START_OPEN : { !tagMode }?=> '<' { tagMode = true; };
		 * fragment HTML_TAG_END_OPEN : { !tagMode }?=> '<''/' { tagMode = true; };
		 * fragment HTML_TAG_CLOSE : { tagMode }?=> '>' { tagMode = false; } ;
		 * fragment HTML_TAG_EMPTY_CLOSE : { tagMode }?=> '/''>' { tagMode = false; } ;
		 * fragment HTML_TAG_DATA : { tagMode }?=>  ~('/') ({ input.LA(1) != '>' && (input.LA(1) != '/' || input.LA(2) != '>')}?=> ~('>'))*;
		 * fragment HTML_PCDATA : { !tagMode }?=> (~('<'|'>'))+ ;
		 * </pre>
		 */
		HTML_STRING
	};

	private ID(String string, Type type) {
		this.string = string;
		this.type = type;
	}

	/**
	 * Constructs a new ID of the given type from the given (encoded) raw string
	 * value.
	 *
	 * @param string
	 *            The (encoded) raw string to create an ID for. Maybe
	 *            <code>null</code>.
	 * @param type
	 *            The type of the ID.
	 * @return A new ID, or <code>null</code> if the given raw string was
	 *         <code>null</code>.
	 */
	public static ID fromString(String string, Type type) {
		if (string == null) {
			return null;
		}
		return new ID(string, type);
	}

	/**
	 * Constructs a new ID from the given (encoded) raw value, inferring the
	 * 'best matching' type.
	 *
	 * @param string
	 *            The (encoded) raw string to create an ID for. Maybe
	 *            <code>null</code>.
	 * @return A new ID, or <code>null</code> if the given string was
	 *         <code>null</code>.
	 */
	public static ID fromString(String string) {
		if (string == null) {
			return null;
		}

		if (string.isEmpty()) {
			return fromString(string, Type.QUOTED_STRING);
		}

		// check HTML-STRING
		if (string.matches("^<.*>$")) {
			return fromString(string, Type.HTML_STRING);
		}

		// check QUOTED_STRING
		if (string.matches("^\".*\"$")) {
			return fromString(string, Type.QUOTED_STRING);
		}

		// check NUMERAL
		if (NUMERAL_PATTERN.matcher(string).matches()) {
			return fromValue(string, Type.NUMERAL);
		}
		return fromString(string, Type.STRING);
	}

	/**
	 * Constructs a new ID from the given (decoded) value, inferring the 'best
	 * matching' type.
	 *
	 * @param value
	 *            The (decoded) value to create an ID for. Maybe
	 *            <code>null</code>.
	 * @return A new ID, or <code>null</code> if the given value was
	 *         <code>null</code>.
	 */
	public static ID fromValue(String value) {
		if (value == null) {
			return null;
		}

		// quick test
		if (value.isEmpty()) {
			return fromValue(value, Type.QUOTED_STRING);
		}

		// check for NUMERAL:
		if (NUMERAL_PATTERN.matcher(value).matches()) {
			return fromValue(value, Type.NUMERAL);
		}

		// check for STRING:
		if (STRING_PATTERN.matcher(value).matches()) {
			if (!value.equalsIgnoreCase("node")
					&& !value.equalsIgnoreCase("edge")
					&& !value.equalsIgnoreCase("graph")
					&& !value.equalsIgnoreCase("digraph")
					&& !value.equalsIgnoreCase("subgraph")
					&& !value.equalsIgnoreCase("strict")) {
				return fromValue(value, Type.STRING);
			}
		}

		// TODO: decide HTML or QUOTED?
		return fromValue(value, Type.QUOTED_STRING);
	}

	/**
	 * Constructs a new ID of the given type from the given (decoded) value.
	 *
	 * @param value
	 *            The (decoded) value to create an ID for. Maybe
	 *            <code>null</code>.
	 * @param type
	 *            The type of the ID.
	 * @return A new ID, or <code>null</code> if the given value was
	 *         <code>null</code>.
	 */
	public static ID fromValue(String value, Type type) {
		if (value == null) {
			return null;
		}
		return new ID(encode(value, type), type);
	}

	private static String encode(String value, Type type) {
		switch (type) {
		case NUMERAL:
		case STRING:
			return value;
		case QUOTED_STRING:
			return "\"" + value.replaceAll("\"", "\\\\\"") + "\"";
		case HTML_STRING:
			return "<" + value + ">";
		default:
			throw new IllegalArgumentException("Unsupported type " + type);
		}
	}

	private static String decode(String string, Type type) {
		switch (type) {
		case NUMERAL:
		case STRING:
			return string;
		case QUOTED_STRING:
			return string
					/* Un-quote */
					.replaceAll("^\"|\"$", "") //$NON-NLS-1$//$NON-NLS-2$
					/*
					 * 'As another aid for readability, dot allows double-quoted
					 * strings to span multiple physical lines using the
					 * standard C convention of a backslash
					 * immediately.'[footnote on
					 * http://www.graphviz.org/doc/info/lang.html]
					 */
					.replaceAll("\\\\(?:\\r\\n|\\r|\\n)", "")
					/*
					 * Un-escape escaped quotes: 'In quoted strings in DOT, the
					 * only escaped character is double-quote
					 * ("). That is, in quoted strings, the dyad \" is converted to "
					 * ; all other characters are left unchanged. In particular,
					 * \\ remains \\.' [footnote on
					 * http://www.graphviz.org/doc/info/lang.html]
					 */
					.replaceAll("\\\\\"", "\"");//$NON-NLS-1$//$NON-NLS-2$
		case HTML_STRING:
			return string.replaceAll("^<|>$", ""); //$NON-NLS-1$//$NON-NLS-2$
		default:
			throw new IllegalArgumentException("Unsupported type " + type);
		}
	}

	/**
	 * Returns the type of the ID.
	 *
	 * @return The {@link Type} of this ID.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns the (encoded) raw string.
	 *
	 * @return The (encoded) raw string.
	 */
	@Override
	public String toString() {
		return string;
	}

	/**
	 * Returns the (decoded) value.
	 *
	 * @return The (decoded) value.
	 */
	public String toValue() {
		return decode(string, type);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((string == null) ? 0 : string.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ID other = (ID) obj;
		if (string == null) {
			if (other.string != null) {
				return false;
			}
		} else if (!string.equals(other.string)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

}
