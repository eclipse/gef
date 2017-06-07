/******************************************************************************
 * Copyright (c) 2011, 2016 Stephan Schwiebert and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
