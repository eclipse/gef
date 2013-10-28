/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swtfx.examples;

import org.eclipse.gef4.swtfx.IParent;

public interface IExample {

	/**
	 * Called once during GUI initialization.
	 * 
	 * @param c
	 */
	void addUi(IParent root);

	/**
	 * Called once during GUI initialization.
	 * 
	 * @return
	 */
	int getHeight();

	/**
	 * Called once during GUI initialization.
	 * 
	 * @return
	 */
	String getTitle();

	/**
	 * Called once during GUI initialization.
	 * 
	 * @return
	 */
	int getWidth();

}
