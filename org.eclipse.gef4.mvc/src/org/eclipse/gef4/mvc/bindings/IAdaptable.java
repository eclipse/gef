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

import java.util.Map;

/**
 * An {@link IAdaptable} allows to register, unregister, and retrieve
 * (registered) adapters under a given {@link AdapterKey}. If a to be registered
 * adapter implements the {@link Bound} interface, it is expected that the
 * {@link IAdaptable} on which the adapter is registered binds itself to the
 * adapter via {@link Bound#setAdaptable(IAdaptable)} within
 * {@link #setAdapter(AdapterKey, Object)}, and accordingly unbinds itself from
 * the adapter (setAdaptable(null)) within {@link #unsetAdapter(AdapterKey)}.
 * 
 * @author anyssen
 * 
 */
public interface IAdaptable {

	/**
	 * Returns an adapter for the given class key if one can unambiguously
	 * retrieved. That is, if there is only a single adapter that matches the
	 * given class key, this adapter is returned, ignoring the role under which
	 * it is registered (see {@link AdapterKey#getRole()} ). If there is more
	 * than one adapter that matches the given class key, it will return the
	 * single adapter that is registered for the default role (
	 * {@link AdapterKey#DEFAULT_ROLE}), if there is a single adapter for which
	 * this applies. Otherwise it will return <code>null</code>. In all cases,
	 * an adapter 'matching' the class key is an adapter, which is registered
	 * with an {@link AdapterKey}, whose key ( {@link AdapterKey#getKey()})
	 * refers to a sub-class or a sub-interface of the given class key
	 * 
	 * @param classKey
	 *            The {@link Class} key used to retrieve a registered adapter.
	 * @return The unambiguously retrievable adapter for the given {@link Class}
	 *         key or <code>null</code> if none could be retrieved.
	 */
	public <T> T getAdapter(Class<T> classKey);

	// public <T> T getAdapter(Class<T> classKey, String qualifier);

	// public <T> Map<AdapterKey<T>, T> getAdapters();

	/**
	 * Returns all adapters being registered for the given class key, i.e. all
	 * adapters whose {@link AdapterKey}'s key {@link AdapterKey#getKey()})
	 * either is the same or a sub-class or sub-interface of the given classKey.
	 * 
	 * @param classKey
	 *            The classKey used to retrieve registered adapters.
	 * @return A {@link Map} containing all those adapters registered at this
	 *         {@link IAdaptable}, whose {@link AdapterKey}'s key (
	 *         {@link AdapterKey#getKey()}) is a sub-class or sub-interface of
	 *         the given classKey, qualified by their respective
	 *         {@link AdapterKey}s.
	 */
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(Class<?> classKey);

	public <T> T getAdapter(AdapterKey<T> key);

	public <T> void setAdapter(AdapterKey<T> key, T adapter);

	public <T> T unsetAdapter(AdapterKey<T> key);

	/**
	 * To be implemented by an adapter to indicate that it intends to be bounded
	 * to the respective {@link IAdaptable} it is registered at.
	 * 
	 * @param <A>
	 *            The type of {@link IAdaptable} this {@link Bound} may be bound
	 *            to.
	 */
	public static interface Bound<A extends IAdaptable> {
		public A getAdaptable();

		void setAdaptable(A adaptable);
	}
}
