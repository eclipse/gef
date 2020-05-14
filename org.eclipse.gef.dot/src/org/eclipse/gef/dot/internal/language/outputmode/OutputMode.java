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
package org.eclipse.gef.dot.internal.language.outputmode;

/**
 * Enum representing DOT outputMode.
 *
 * @author miklossy
 *
 */
public enum OutputMode {

	/**
	 * This value specifies 'breadthfirst' output mode.
	 */
	BREADTHFIRST("breadthfirst"),

	/**
	 * This value specifies 'nodesfirst' output mode.
	 */
	NODESFIRST("nodesfirst"),

	/**
	 * This value specifies 'edgesfirst' output mode.
	 */
	EDGEFIRST("edgesfirst");

	private final String literalValue;

	private OutputMode(String literalValue) {
		this.literalValue = literalValue;
	}

	@Override
	public String toString() {
		return this.literalValue;
	}
}
