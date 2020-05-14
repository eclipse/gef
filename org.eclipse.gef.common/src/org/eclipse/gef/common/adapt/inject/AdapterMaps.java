/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.common.adapt.inject;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.common.adapt.inject.AdapterMap.BoundAdapter;
import org.eclipse.gef.common.reflect.Types;

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
	 * context restriction.
	 *
	 * @param type
	 *            The type of the {@link AdapterMap} to be created.
	 * @return A new {@link AdapterMapImpl} for the given type.
	 */
	private static AdapterMap get(Class<? extends IAdaptable> type,
			AdapterKey<?>... context) {
		// convert keys into annotations (which are needed because the
		// AdapterMap annotation may not use AdapterKey type directly
		BoundAdapter[] ce = new BoundAdapter[context.length];
		for (int i = 0; i < context.length; i++) {
			ce[i] = new AdapterMapImpl.BoundAdapterImpl(
					Types.serialize(context[i].getKey()), context[i].getRole());
		}
		return new AdapterMapImpl(type, ce);
	}

	/**
	 * Returns a {@link MapBinder}, which can be used to define adapter bindings
	 * for an {@link IAdaptable}s of the given type.
	 *
	 * @param binder
	 *            The {@link Binder} used to create a new {@link MapBinder}.
	 * @param adaptableType
	 *            The type to be used as type of the {@link AdapterMap}.
	 * @return A new {@link MapBinder} used to define adapter map bindings for
	 *         the given type (and all sub-types).
	 */
	public static MapBinder<AdapterKey<?>, Object> getAdapterMapBinder(
			Binder binder, Class<? extends IAdaptable> adaptableType) {
		MapBinder<AdapterKey<?>, Object> adapterMapBinder = MapBinder
				.newMapBinder(binder, new TypeLiteral<AdapterKey<?>>() {
				}, new TypeLiteral<Object>() {
				}, AdapterMaps.get(adaptableType));
		return adapterMapBinder;
	}

	/**
	 * Returns a {@link MapBinder}, which can be used to define adapter bindings
	 * for an {@link IAdaptable}s of the given type, restricting it further to
	 * those {@link IAdaptable}s that are themselves
	 * {@link org.eclipse.gef.common.adapt.IAdaptable.Bound adapted} to another
	 * {@link IAdaptable} with the specified role.
	 *
	 * @param binder
	 *            The {@link Binder} used to create a new {@link MapBinder}.
	 * @param adaptableType
	 *            The type to be used as type of the {@link AdapterMap}.
	 * @param adaptableContext
	 *            A specification of the context the adaptable, into which
	 *            adapters are to be injected, has to provide. If specified the
	 *            injection will be restricted to {@link IAdaptable}s with a
	 *            compatible context only. The context of an adaptable is
	 *            compatible when respective context elements are visited in the
	 *            given order when walking the adaptable-adapter chain,
	 *            beginning with the adaptable in which to inject. The actual
	 *            chain may contain additional elements, that do not correspond
	 *            to context element, in between (which are ignored), but it has
	 *            to contain the specified context elements in the given order.
	 * @return A new {@link MapBinder} used to define adapter map bindings for
	 *         the given type (and all sub-types).
	 */
	public static MapBinder<AdapterKey<?>, Object> getAdapterMapBinder(
			Binder binder, Class<? extends IAdaptable> adaptableType,
			AdapterKey<?>... adaptableContext) {
		if (!IAdaptable.Bound.class.isAssignableFrom(adaptableType)) {
			throw new IllegalArgumentException(
					"In order to restrict adapter map bindings with a role, the IAdaptable has to be IAdaptable.Bound (with the role).");
		}
		MapBinder<AdapterKey<?>, Object> adapterMapBinder = MapBinder
				.newMapBinder(binder, new TypeLiteral<AdapterKey<?>>() {
				}, new TypeLiteral<Object>() {
				}, AdapterMaps.get(adaptableType, adaptableContext));
		return adapterMapBinder;
	}

	private AdapterMaps() {
		// should not be invoked by clients
	}
}
