/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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
