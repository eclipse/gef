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
package org.eclipse.gef.dot.internal.language.splines;

/**
 * Enum representing DOT splines.
 */
public enum Splines {

	/**
	 * This value indicates that 'compound' are to be used.
	 */
	COMPOUND("compound"),

	/**
	 * This value indicates that the edges should be drawn as curved arcs.
	 */
	CURVED("curved"),

	/**
	 * This value indicates that no edges are to be drawn. This is a synonym of
	 * {@link #NONE}
	 */
	EMPTY(""),

	/**
	 * This value indicates that lines are to be used. This is a synonym of
	 * {@link #LINE}
	 */
	FALSE("false"),

	/**
	 * This value indicates that lines are to be used. This is a synonym of
	 * {@link #FALSE}
	 */
	LINE("line"),

	/**
	 * This value indicates that no edges are to be drawn. This is a synonym of
	 * {@link #EMPTY}
	 */
	NONE("none"),

	/**
	 * This value indicates that orthogonal polylines are to be used.
	 */
	ORTHO("ortho"),

	/**
	 * This value indicates that straight polylines are to be used.
	 */
	POLYLINE("polyline"),

	/**
	 * This value indicates that splines are to be used. This is a synonym of
	 * {@link #TRUE}
	 */
	SPLINE("spline"),

	/**
	 * This value indicates that splines are to be used. This is a synonym of
	 * {@link #SPLINE}
	 */
	TRUE("true");

	private final String literalValue;

	private Splines(String literalValue) {
		this.literalValue = literalValue;
	}

	/**
	 * Returns the '<em><b>Splines</b></em>' literal with the specified literal
	 * value.
	 *
	 * @param literal
	 *            the literal.
	 * @return the matching enum or <code>null</code>.
	 */
	public static Splines get(String literal) {
		for (Splines result : values()) {
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
