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
package org.eclipse.gef.dot.internal.language.rankdir;

/**
 * Enum representing DOT rankdir.
 *
 * @author anyssen
 *
 */
public enum Rankdir {

	/**
	 * This value specifies 'TB' (top-bottom) rankdir.
	 */
	TB("TB"),

	/**
	 * This value specifies 'LR' (left-right) rankdir.
	 */
	LR("LR"),

	/**
	 * This value specifies 'BT' (bottom-top) rankdir.
	 */
	BT("BT"),

	/**
	 * This value specifies 'RL' (right-left) rankdir.
	 */
	RL("RL");

	private final String literalValue;

	private Rankdir(String literalValue) {
		this.literalValue = literalValue;
	}

	@Override
	public String toString() {
		return this.literalValue;
	}
}
