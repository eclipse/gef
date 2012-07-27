/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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
package org.eclipse.gef4.graphics;

/**
 * The AbstractWriteProperties class partially implements the
 * {@link IWriteProperties} interface.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractWriteProperties implements IWriteProperties {

	/**
	 * The current anti aliasing setting associated with this
	 * {@link AbstractWriteProperties}.
	 * 
	 * @see IWriteProperties#DEFAULT_ANTIALIASING
	 */
	protected boolean antialiasing = IWriteProperties.DEFAULT_ANTIALIASING;

	public boolean isAntialiasing() {
		return antialiasing;
	}

	public IWriteProperties setAntialiasing(boolean antialiasing) {
		this.antialiasing = antialiasing;
		return this;
	}

}
