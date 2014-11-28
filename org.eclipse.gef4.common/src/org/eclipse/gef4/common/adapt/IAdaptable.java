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
 * {@link Class} or {@link TypeToken} key and a {@link String}-based role.
 * Thereby, adapters of the same type can be registered at an {@link IAdaptable}
 * under different roles.
 * 
 * For convenience, an {@link IAdaptable} supports the registration and
 * retrieval of 'default' adapters by using a simple {@link Class} or
 * {@link TypeToken} ) key instead of an {@link AdapterKey}. This is identical
 * to the use of an {@link AdapterKey} with the given {@link Class} or
 * {@link TypeToken} and the default role ({@link AdapterKey#DEFAULT_ROLE}).
 * 
 * If a to be registered adapter implements the {@link Bound} interface, it is
 * expected that the {@link IAdaptable} on which the adapter is registered binds
 * itself to the adapter via {@link Bound#setAdaptable(IAdaptable)} within
 * {@link #setAdapter(AdapterKey, Object)}, and accordingly unbinds itself from
 * the adapter (setAdaptable(null)) within {@link #unsetAdapter(AdapterKey)}.
 * 
 * Any client implementing this interface may use an {@link AdaptableSupport} as
 * a delegate.
 * 
 * @author anyssen
 */
public interface IAdaptable extends IPropertyChangeNotifier {

	/**
	 * To be implemented by an adapter to indicate that it intends to be bounded
	 * to the respective {@link IAdaptable} it is registered at.
	 * 
	 * @param <A>
	 *            The type of {@link IAdaptable} this {@link Bound} may be bound
	 *            to.
	 */
	public static interface Bound<A extends IAdaptable> {

		/**
		 * Returns the {@link IAdaptable} this {@link IAdaptable.Bound} is
		 * currently bound to, or <code>null</code> if this
		 * {@link IAdaptable.Bound} is currently not bound to an
		 * {@link IAdaptable}.
		 * 
		 * @return The {@link IAdaptable} this {@link IAdaptable.Bound} is
		 *         currently bound to, or <code>null</code> if this
		 *         {@link IAdaptable.Bound} is currently not bound to an
		 *         {@link IAdaptable}.
		 */
		public A getAdaptable();

		/**
		 * Called by the {@link IAdaptable} this {@link IAdaptable.Bound} is
		 * registered at or unregistered from, in order to establish or remove a
		 * back-reference to itself.
		 * 
		 * @param adaptable
		 *            The {@link IAdaptable} this {@link IAdaptable.Bound} is
		 *            bound to or <code>null</code> to unbind this
		 *            {@link IAdaptable.Bound}.
		 */
		void setAdaptable(A adaptable);
	}

	/**
	 * A key used as {@link PropertyChangeEvent#getPropertyName()} when
	 * notifying about registering/unregistering of adapters.
	 */
	public static final String ADAPTERS_PROPERTY = "adapters";

	/**
	 * Returns an adapter for the given {@link AdapterKey} if one can
	 * unambiguously be retrieved. That is, if there is only a single adapter
	 * that 'matches' the given {@link AdapterKey}, this adapter is returned.
	 * 
	 * An adapter 'matching' the {@link AdapterKey} is an adapter, which is
	 * registered with an {@link AdapterKey}, whose {@link TypeToken} key (
	 * {@link AdapterKey#getKey()}) refers to the same type or a sub-type of the
	 * given {@link AdapterKey}'s {@link TypeToken} key (@see
	 * {@link Class#isAssignableFrom(Class)} and whose role (
	 * {@link AdapterKey#getRole()})) equals the role of the given
	 * {@link AdapterKey}'s role.
	 * 
	 * If there is more than one adapter that 'matches' the given
	 * {@link AdapterKey} or none can be retrieved, <code>null</code> will be
	 * returned.
	 * 
	 * @param key
	 *            The {@link AdapterKey} used to retrieve a registered adapter.
	 * @return The unambiguously retrievable adapter for the given
	 *         {@link AdapterKey} or <code>null</code> if none could be
	 *         retrieved.
	 */
	public <T> T getAdapter(AdapterKey<? super T> key);

	/**
	 * Returns an adapter for the given {@link Class} key if one can
	 * unambiguously be retrieved. That is, if there is only a single adapter
	 * that 'matches' the given {@link Class} key, this adapter is returned,
	 * ignoring the role under which it is registered (see
	 * {@link AdapterKey#getRole()}).
	 * 
	 * An adapter 'matching' the {@link Class} key is an adapter, which is
	 * registered with an {@link AdapterKey}, whose key (
	 * {@link AdapterKey#getKey()}) refers to the same type or a sub-type of the
	 * given {@link Class} key (see {@link Class#isAssignableFrom(Class)}.
	 * 
	 * If there is more than one adapter that 'matches' the given {@link Class}
	 * key, it will return the single adapter that is registered for the default
	 * role ({@link AdapterKey#DEFAULT_ROLE}), if there is a single adapter for
	 * which this holds. Otherwise it will return <code>null</code>.
	 * 
	 * @param key
	 *            The {@link Class} key used to retrieve a registered adapter.
	 * @return The unambiguously retrievable adapter for the given {@link Class}
	 *         key or <code>null</code> if none could be retrieved.
	 */
	public <T> T getAdapter(Class<? super T> key);

	/**
	 * Returns an adapter for the given {@link TypeToken} key if one can
	 * unambiguously be retrieved. That is, if there is only a single adapter
	 * that 'matches' the given {@link TypeToken} key, this adapter is returned,
	 * ignoring the role under which it is registered (see
	 * {@link AdapterKey#getRole()}).
	 * 
	 * An adapter 'matching' the {@link TypeToken} key is an adapter, which is
	 * registered with an {@link AdapterKey}, whose key (
	 * {@link AdapterKey#getKey()}) refers to the same type or a sub-type of the
	 * given type key (see {@link TypeToken#isAssignableFrom(TypeToken)}.
	 * 
	 * If there is more than one adapter that 'matches' the given
	 * {@link TypeToken} key, it will return the single adapter that is
	 * registered for the default role ({@link AdapterKey#DEFAULT_ROLE}), if
	 * there is a single adapter for which this holds. Otherwise it will return
	 * <code>null</code>.
	 * 
	 * @param key
	 *            The {@link TypeToken} key used to retrieve a registered
	 *            adapter.
	 * @return The unambiguously retrievable adapter for the given
	 *         {@link TypeToken} key or <code>null</code> if none could be
	 *         retrieved.
	 */
	public <T> T getAdapter(TypeToken<? super T> key);

	/**
	 * Returns all adapters 'matching' the given {@link Class} key, i.e. all
	 * adapters whose {@link AdapterKey}'s {@link TypeToken} key
	 * {@link AdapterKey#getKey()}) refers to the same or a sub-class or
	 * sub-interface of the given {@link Class} key.
	 * 
	 * @param key
	 *            The {@link Class} key to retrieve adapters for.
	 * @return A {@link Map} containing all those adapters registered at this
	 *         {@link IAdaptable}, whose {@link AdapterKey}'s {@link TypeToken}
	 *         key ({@link AdapterKey#getKey()}) refers to the same or a
	 *         sub-class or sub-interface of the given {@link Class} key,
	 *         qualified by their respective {@link AdapterKey}s.
	 * 
	 * @see #getAdapters(TypeToken)
	 */
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(Class<? super T> key);

	/**
	 * Returns all adapters 'matching' the given {@link TypeToken} key, i.e. all
	 * adapters whose {@link AdapterKey}'s {@link TypeToken} key
	 * {@link AdapterKey#getKey()}) refers to the same or a sub-type or of the
	 * given {@link TypeToken} key.
	 * 
	 * @param key
	 *            The {@link TypeToken} key to retrieve adapters for.
	 * @return A {@link Map} containing all those adapters registered at this
	 *         {@link IAdaptable}, whose {@link AdapterKey}'s {@link TypeToken}
	 *         key ({@link AdapterKey#getKey()}) refers to the same or a
	 *         sub-type of the given {@link TypeToken} key, qualified by their
	 *         respective {@link AdapterKey}s.
	 * 
	 * @see #getAdapter(TypeToken)
	 */
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			TypeToken<? super T> key);

	/**
	 * Registers the given adapter under the given {@link AdapterKey}. The
	 * adapter has to be 'matching' the {@link AdapterKey}, i.e. it has to be
	 * compliant to the {@link AdapterKey}'s {@link TypeToken} key (
	 * {@link AdapterKey#getKey()}).
	 * 
	 * If the given adapter implements {@link IAdaptable.Bound}, the adapter
	 * will obtain a back-reference to this {@link IAdaptable} via its
	 * {@link IAdaptable.Bound#setAdaptable(IAdaptable)} method.
	 * 
	 * @param key
	 *            The {@link AdapterKey} under which to register the given
	 *            adapter.
	 * @param adapter
	 *            The adapter to register under the given {@link AdapterKey}.
	 */
	public <T> void setAdapter(AdapterKey<? super T> key, T adapter);

	/**
	 * Registers the given adapter under an {@link AdapterKey}, which will use a
	 * {@link TypeToken} representing the given {@link Class} key, i.e. using
	 * {@link TypeToken#of(Class)}, as well as the default role (see
	 * {@link AdapterKey#DEFAULT_ROLE}.
	 * 
	 * If the given adapter implements {@link IAdaptable.Bound}, the adapter
	 * will obtain a back-reference to this {@link IAdaptable} via its
	 * {@link IAdaptable.Bound#setAdaptable(IAdaptable)} method.
	 * 
	 * @param key
	 *            The {@link Class} under which to register the given adapter.
	 * @param adapter
	 *            The adapter to register under the given {@link Class} key.
	 * 
	 * @see #setAdapter(AdapterKey, Object)
	 */
	public <T> void setAdapter(Class<? super T> key, T adapter);

	/**
	 * Registers the given adapter under an {@link AdapterKey}, which will use
	 * the given {@link TypeToken} key as well as the default role (see
	 * {@link AdapterKey#DEFAULT_ROLE}.
	 * 
	 * If the given adapter implements {@link IAdaptable.Bound}, the adapter
	 * will obtain a back-reference to this {@link IAdaptable} via its
	 * {@link IAdaptable.Bound#setAdaptable(IAdaptable)} method.
	 * 
	 * @param key
	 *            The {@link TypeToken} under which to register the given
	 *            adapter.
	 * @param adapter
	 *            The adapter to register under the given {@link TypeToken} key.
	 * 
	 * @see #setAdapter(AdapterKey, Object)
	 */
	public <T> void setAdapter(TypeToken<? super T> key, T adapter);

	/**
	 * Unregisters the adapter registered under the exact {@link AdapterKey}
	 * given, returning it for convenience.
	 * 
	 * If the given adapter implements {@link IAdaptable.Bound}, the
	 * back-reference to this {@link IAdaptable} will be removed via its
	 * {@link IAdaptable.Bound#setAdaptable(IAdaptable)} method, passing over
	 * <code>null</code>.
	 * 
	 * @param key
	 *            The {@link AdapterKey} for which to remove a registered
	 *            adapter.
	 * @return The adapter, which has been removed.
	 */
	public <T> T unsetAdapter(AdapterKey<? super T> key);
}
