/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.clustermode;

/**
 * Enum representing DOT clusterMode.
 *
 * @author miklossy
 *
 */
public enum ClusterMode {

	/**
	 * This value specifies 'local' cluster mode.
	 */
	LOCAL("local"),

	/**
	 * This value specifies 'global' cluster mode.
	 */
	GLOBAL("global"),

	/**
	 * This value specifies 'none' cluster mode.
	 */
	NONE("none");

	private final String literalValue;

	private ClusterMode(String literalValue) {
		this.literalValue = literalValue;
	}

	@Override
	public String toString() {
		return this.literalValue;
	}
}
