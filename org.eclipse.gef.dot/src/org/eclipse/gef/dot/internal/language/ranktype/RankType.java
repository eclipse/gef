/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.ranktype;

/**
 * Enum representing DOT rankType.
 */
public enum RankType {

	/**
	 * This value indicates that 'same' is to be used.
	 */
	SAME("same"),

	/**
	 * This value indicates that 'min' is to be used.
	 */
	MIN("min"),

	/**
	 * This value indicates that 'source' is to be used.
	 */
	SOURCE("source"),

	/**
	 * This value indicates that 'max' is to be used.
	 */
	MAX("max"),

	/**
	 * This value indicates that 'sink' is to be used.
	 */
	SINK("sink");

	private final String literalValue;

	private RankType(String literalValue) {
		this.literalValue = literalValue;
	}

	/**
	 * Returns the '<em><b>rankType</b></em>' literal with the specified literal
	 * value.
	 *
	 * @param literal
	 *            the literal.
	 * @return the matching enum or <code>null</code>.
	 */
	public static RankType get(String literal) {
		for (RankType result : values()) {
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return this.literalValue;
	}
}
