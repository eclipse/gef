/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.common.adapt.inject;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.IAdaptable;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

/**
 * A utility class to obtain a {@link MapBinder}, via which adapter (map)
 * bindings can be specified in a {@link Module}.
 *
 * @author anyssen
 *
 */
public class AdapterMaps {

	/**
	 * Creates a {@link AdapterMap} annotation with the given {@code type} .
	 *
	 * @param type
	 *            The type of the {@link AdapterMap} to be created.
	 * @return A new {@link AdapterMapImpl} for the given type.
	 */
	private static AdapterMap get(Class<? extends IAdaptable> type) {
		return new AdapterMapImpl(type);
	}

	/**
	 * Creates a {@link AdapterMap} annotation with the given {@code type} and
	 * role.
	 *
	 * @param type
	 *            The type of the {@link AdapterMap} to be created.
	 * @return A new {@link AdapterMapImpl} for the given type.
	 */
	private static AdapterMap get(Class<? extends IAdaptable> type,
			String role) {
		return new AdapterMapImpl(type, role);
	}

	/**
	 * Returns a {@link MapBinder}, which is bound to an {@link AdapterMap}
	 * annotation of the given type, and can thus be used to specify adapter
	 * that are to injected into {@link IAdaptable}s of the respective type.
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
		MapBinder<AdapterKey<?>, Object> adapterMapBinder = MapBinder
				.newMapBinder(binder, new TypeLiteral<AdapterKey<?>>() {
				}, new TypeLiteral<Object>() {
				}, AdapterMaps.get(type));
		return adapterMapBinder;
	}

	/**
	 * Returns a {@link MapBinder}, which is bound to an {@link AdapterMap}
	 * annotation of the given type and role, and can thus be used to specify
	 * adapter that are to injected into {@link IAdaptable}s of the respective
	 * type and role.
	 *
	 * @param binder
	 *            The {@link Binder} used to create a new {@link MapBinder}.
	 * @param type
	 *            The type to be used as type of the {@link AdapterMap}.
	 * @param role
	 *            The role of the adaptable to bind values to.
	 * @return A new {@link MapBinder} used to define adapter map bindings for
	 *         the given type (and all sub-types).
	 */
	public static MapBinder<AdapterKey<?>, Object> getAdapterMapBinder(
			Binder binder, Class<? extends IAdaptable> type, String role) {
		MapBinder<AdapterKey<?>, Object> adapterMapBinder = MapBinder
				.newMapBinder(binder, new TypeLiteral<AdapterKey<?>>() {
				}, new TypeLiteral<Object>() {
				}, AdapterMaps.get(type, role));
		return adapterMapBinder;
	}

	private AdapterMaps() {
		// should not be invoked by clients
	}
}
