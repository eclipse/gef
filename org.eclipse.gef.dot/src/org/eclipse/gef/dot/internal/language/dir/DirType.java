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
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.dir;

/**
 * Enum representing DOT dirType.
 *
 * @author anyssen
 *
 */
public enum DirType {

	/**
	 * This value specifies 'forward' direction.
	 */
	FORWARD("forward"),

	/**
	 * This value specifies 'back' direction.
	 */
	BACK("back"),

	/**
	 * This value specifies 'both' direction.
	 */
	BOTH("both"),

	/**
	 * This value specifies 'none' direction.
	 */
	NONE("none");

	private final String literalValue;

	private DirType(String literalValue) {
		this.literalValue = literalValue;
	}

	@Override
	public String toString() {
		return this.literalValue;
	}
}
