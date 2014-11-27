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
package org.eclipse.gef4.common.adapt;

import java.beans.PropertyChangeEvent;
import java.util.Map;

import org.eclipse.gef4.common.notify.IPropertyChangeNotifier;

import com.google.common.reflect.TypeToken;

/**
 * An {@link IAdaptable} allows to register (as well as unregister) and retrieve
 * (registered) adapters under a given {@link AdapterKey}, that is under a
 * {@link Class} or {@link TypeToken} key in combination with a {@link String}
 * -based role. Thereby, adapters of the same type can be registered at an
 * {@link IAdaptable} under different roles.
 * 
 * For convenience, an {@link IAdaptable} supports the registration and
 * retrieval of 'default' adapters by using a simple {@link Class} or
 * {@link TypeToken} ) key instead of an {@link AdapterKey}. This is identical
 * to the use of an {@link AdapterKey} with the given {@link Class} or
 * {@link TypeToken} and the default role ( {@link AdapterKey#DEFAULT_ROLE}).
 * 
 * If a to be registered adapter implements the {@link Bound} interface, it is
 * expected that the {@link IAdaptable} on which the adapter is registered binds
 * itself to the adapter via {@link Bound#setAdaptable(IAdaptable)} within
 * {@link #setAdapter(AdapterKey, Object)}, and accordingly unbinds itself from
 * the adapter (setAdaptable(null)) within {@link #unsetAdapter(AdapterKey)}.
 * 
 * @author anyssen
 * 
 */
public interface IAdaptable extends IPropertyChangeNotifier {

	/**
	 * A key used as {@link PropertyChangeEvent#getPropertyName()} when
	 * notifying about registering/unregistering of adapters.
	 */
	public static final String ADAPTERS_PROPERTY = "adapters";

	/**
	 * Returns an adapter for the given {@link TypeToken} key if one can
	 * unambiguously retrieved. That is, if there is only a single adapter that
	 * matches the given {@link TypeToken} key, this adapter is returned,
	 * ignoring the role under which it is registered (see
	 * {@link AdapterKey#getRole()}).
	 * 
	 * An adapter 'matching' the {@link TypeToken} key is an adapter, which is
	 * registered with an {@link AdapterKey}, whose key (
	 * {@link AdapterKey#getKey()}) refers to the type or a sub-type of the
	 * given type key (@see {@link TypeToken#isAssignableFrom(TypeToken)}.
	 * 
	 * If there is more than one adapter that matches the given type key, it
	 * will return the single adapter that is registered for the default role (
	 * {@link AdapterKey#DEFAULT_ROLE}), if there is a single adapter for which
	 * this applies, not taking into consideration which is the 'best matching'
	 * {@link TypeToken} key. Otherwise it will return <code>null</code>.
	 * 
	 * @param key
	 *            The {@link TypeToken} key used to retrieve a registered
	 *            adapter.
	 * @return The unambiguously retrievable adapter for the given
	 *         {@link TypeToken} key or <code>null</code> if none could be
	 *         retrieved.
	 */
	public <T> T getAdapter(TypeToken<? super T> key);

	public <T> T getAdapter(Class<? super T> key);

	/**
	 * Returns all adapters being registered for the given class key, i.e. all
	 * adapters whose {@link AdapterKey}'s key {@link AdapterKey#getKey()})
	 * either is the same or a sub-class or sub-interface of the given classKey.
	 * 
	 * @param key
	 *            The classKey used to retrieve registered adapters.
	 * @return A {@link Map} containing all those adapters registered at this
	 *         {@link IAdaptable}, whose {@link AdapterKey}'s key (
	 *         {@link AdapterKey#getKey()}) is a sub-class or sub-interface of
	 *         the given classKey, qualified by their respective
	 *         {@link AdapterKey}s.
	 */
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(Class<? super T> key);

	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			TypeToken<? super T> key);

	public <T> T getAdapter(AdapterKey<? super T> key);

	public <T> void setAdapter(AdapterKey<? super T> key, T adapter);

	// shortcut for using an type token key with the given class and the default
	// role
	public <T> void setAdapter(TypeToken<? super T> key, T adapter);

	// shortcut for using an adapter key with the given class and the default
	// role
	public <T> void setAdapter(Class<? super T> key, T adapter);

	public <T> T unsetAdapter(AdapterKey<? super T> key);

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
