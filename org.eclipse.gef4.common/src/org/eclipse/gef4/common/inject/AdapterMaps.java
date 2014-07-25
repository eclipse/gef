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
package org.eclipse.gef4.common.inject;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.IAdaptable;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

/**
 * A utility class to support working with {@link AdapterMap} annotations.
 * 
 * @see AdapterMap
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
	public static AdapterMap typed(Class<? extends IAdaptable> type) {
		return new AdapterMapImpl(type);
	}

	/**
	 * Returns a {@link MapBinder}, which is bound to an {@link AdapterMap}
	 * annotation of the given type.
	 * 
	 * @param binder
	 *            The {@link Binder} used to create a new {@link MapBinder}.
	 * @param type
	 *            The type to be used as type of the {@link AdapterMap}.
	 * @return A new {@link MapBinder} used to define adapter map bindings for
	 *         the given type (and all sub-types).
	 */
	public static MapBinder<AdapterKey<?>, Object> getAdapterMapBinder(
			Binder binder, Class<? extends IAdaptable> type) {
		return MapBinder.newMapBinder(binder, new TypeLiteral<AdapterKey<?>>() {
		}, new TypeLiteral<Object>() {
		}, AdapterMaps.typed(type));
	}
}
