/******************************************************************************
 * Copyright (c) 2011, 2016 Stephan Schwiebert and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stephan Schwiebert - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.cloudio.internal.ui.data;

/**
 * 
 * @author sschwieb
 *
 */
public class Type {

	private String string;
	private int occurrences;

	public Type(String string, int occurrences) {
		this.string = string;
		this.occurrences = occurrences;
	}

	public String getString() {
		return string;
	}

	public int getOccurrences() {
		return occurrences;
	}

}
