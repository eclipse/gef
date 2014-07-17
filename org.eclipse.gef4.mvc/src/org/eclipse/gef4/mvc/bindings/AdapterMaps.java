/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.bindings;

/**
 * A utiliy class to construct a new {@link AdapterMap} annotation for a given
 * type.
 * 
 * @author anyssen
 *
 */
public class AdapterMaps {

	private AdapterMaps() {
		// should not be invoked by clients
	}

	/**
	 * Creates a {@link AdapterMap} annotation with the given {@code type} .
	 * 
	 * @param type
	 *            The type of the {@link AdapterMap} to be created.
	 * @return A new {@link AdapterMapImpl} for the given type.
	 */
	public static AdapterMap typed(Class<?> type) {
		return new AdapterMapImpl(type);
	}
}
