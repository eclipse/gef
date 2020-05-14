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
