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


public class AdapterMaps {

	private AdapterMaps() {
	}

	/**
	 * Creates a {@link AdapterMap} annotation with the given {@code type}
	 * .
	 */
	public static AdapterMap typed(Class<?> type) {
		return new AdapterMapImpl(type);
	}
}
