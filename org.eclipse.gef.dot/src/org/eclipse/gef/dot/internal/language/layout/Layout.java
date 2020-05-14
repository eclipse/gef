/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.layout;

/**
 * Enum representing DOT layout.
 */
public enum Layout {

	/**
	 * This value specifies that the "circo" layout algorithm is to be used for
	 * laying out the graph.
	 */
	CIRCO("circo"),

	/**
	 * This value specifies that the "dot" layout algorithm is to be used for
	 * laying out the graph.
	 */
	DOT("dot"),

	/**
	 * This value specifies that the "fdp" layout algorithm is to be used for
	 * laying out the graph.
	 */
	FDP("fdp"),

	/**
	 * This value specifies that the "neato" layout algorithm is to be used for
	 * laying out the graph.
	 */
	NEATO("neato"),

	/**
	 * This value specifies that the "osage" layout algorithm is to be used for
	 * laying out the graph.
	 */
	OSAGE("osage"),

	/**
	 * This value specifies that the "sfdp" layout algorithm is to be used for
	 * laying out the graph.
	 */
	SFDP("sfdp"),

	/**
	 * This value specifies that the "twopi" layout algorithm is to be used for
	 * laying out the graph.
	 */
	TWOPI("twopi");

	/**
	 * Returns the '<em><b>Layout</b></em>' literal with the specified literal
	 * value.
	 *
	 * @param literal
	 *            the literal.
	 * @return the matching enum or <code>null</code>.
	 */
	public static Layout get(String literal) {
		for (Layout result : values()) {
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	private final String literalValue;

	private Layout(String literalValue) {
		this.literalValue = literalValue;
	}

	@Override
	public String toString() {
		return this.literalValue;
	}
}
