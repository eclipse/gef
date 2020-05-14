/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
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
package org.eclipse.gef.common.adapt;

import java.util.Map;

import com.google.common.reflect.TypeToken;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableMap;

/**
 * An {@link IAdaptable} allows to register and retrieve adapters under a given
 * {@link AdapterKey}, which combines a {@link TypeToken}-based type key and a
 * {@link String}-based role.
 * <p>
 * The combination of a type key with a role (by means of an {@link AdapterKey})
 * allows to register multiple adapters with the same type under different
 * roles. If there is only one adapter for specific type, it can easily be
 * registered and retrieved without specifying a role, or by using the 'default'
 * role ({@link AdapterKey#DEFAULT_ROLE}).
 * <p>
 * Using a {@link TypeToken}-based type key instead of a simple {@link Class}
 * -based type key, an {@link IAdaptable} allows to register and retrieve
 * adapters also via parameterized types (e.g. by using
 * <code>new TypeToken&lt;Provider&lt;IGeometry&gt;&gt;(){}</code> as a type
 * key). For convenience, non-parameterized types can also be registered and
 * retrieved via a raw {@link Class} type key (a {@link TypeToken} will
 * internally be computed for it using {@link TypeToken#of(Class)}).
 * <p>
 * If a to be registered adapter implements the {@link Bound} interface, it is
 * expected that the {@link IAdaptable}, on which the adapter is registered,
 * binds itself to the adapter via {@link Bound#setAdaptable(IAdaptable)} during
 * registration, and accordingly unbinds itself from the adapter
 * (setAdaptable(null)) during un-registration.
 * <p>
 * Any client implementing this interface may internally use an
 * {@link AdaptableSupport} as a delegate to realize the required functionality.
 *
 * @author anyssen
 */
public interface IAdaptable {

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
		 * Default implementation of {@link Bound} that manages a
		 * {@link ReadOnlyObjectProperty} for the {@link IAdaptable}.
		 *
		 * @param <T>
		 *            The type of {@link IAdaptable} which this class is bound
		 *            to.
		 */
		public static class Impl<T extends IAdaptable> implements Bound<T> {
			private ReadOnlyObjectWrapper<T> adaptableProperty = new ReadOnlyObjectWrapper<>();

			@Override
			public ReadOnlyObjectProperty<T> adaptableProperty() {
				return adaptableProperty;
			}

			@Override
			public T getAdaptable() {
				return adaptableProperty.get();
			}

			@Override
			public void setAdaptable(T adaptable) {
				adaptableProperty.set(adaptable);
			}
		}

		/**
		 * A read-only object property providing the {@link IAdaptable} this
		 * {@link IAdaptable.Bound} is bound to.
		 *
		 * @return A read-only object property.
		 */
		public ReadOnlyObjectProperty<A> adaptableProperty();

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
		 * registered at or unregistered from. When registering an
		 * {@link IAdaptable.Bound}, the {@link IAdaptable} will pass in a
		 * reference to itself, when unregistering an {@link IAdaptable.Bound}
		 * it will pass in <code>null</code>.
		 *
		 * @param adaptable
		 *            The {@link IAdaptable} this {@link IAdaptable.Bound} is
		 *            bound to or <code>null</code> to unbind this
		 *            {@link IAdaptable.Bound}.
		 */
		void setAdaptable(A adaptable);

	}

	/**
	 * The name of the {@link #adaptersProperty() adapters property}.
	 */
	public static final String ADAPTERS_PROPERTY = "adapters";

	/**
	 * Returns an unmodifiable read-only map property that contains the
	 * registered adapters by their keys.
	 *
	 * @return An unmodifiable read-only map property.
	 */
	public ReadOnlyMapProperty<AdapterKey<?>, Object> adaptersProperty();

	/**
	 * Returns an adapter for the given {@link AdapterKey} if one can
	 * unambiguously be retrieved, i.e. if there is only a single adapter that
	 * 'matches' the given {@link AdapterKey}.
	 * <p>
	 * An adapter 'matching' the {@link AdapterKey} is an adapter, which is
	 * registered with an {@link AdapterKey}, whose {@link TypeToken} key (
	 * {@link AdapterKey#getKey()}) refers to the same type or a sub-type of the
	 * given {@link AdapterKey}'s {@link TypeToken} key and whose role (
	 * {@link AdapterKey#getRole()})) equals the role of the given
	 * {@link AdapterKey}'s role.
	 * <p>
	 * If there is more than one adapter that 'matches' the given
	 * {@link AdapterKey}, or there is no one 'matching' it, <code>null</code>
	 * will be returned.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param key
	 *            The {@link AdapterKey} used to retrieve a registered adapter.
	 * @return The unambiguously retrievable adapter for the given
	 *         {@link AdapterKey} or <code>null</code> if none could be
	 *         retrieved.
	 */
	public <T> T getAdapter(AdapterKey<T> key);

	/**
	 * Returns an adapter for the given {@link Class} key if one can
	 * unambiguously be retrieved. That is, if there is only a single adapter
	 * that 'matches' the given {@link Class} key, this adapter is returned,
	 * ignoring the role under which it is registered (see
	 * {@link AdapterKey#getRole()}).
	 * <p>
	 * An adapter 'matching' the {@link Class} key is an adapter, which is
	 * registered with an {@link AdapterKey}, whose key (
	 * {@link AdapterKey#getKey()}) refers to the same type or a sub-type of the
	 * given {@link Class} key.
	 * <p>
	 * If there is more than one adapter that 'matches' the given {@link Class}
	 * key, it will return the single adapter that is registered for the default
	 * role ({@link AdapterKey#DEFAULT_ROLE}), if there is a single adapter for
	 * which this holds. Otherwise it will return <code>null</code>.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param key
	 *            The {@link Class} key used to retrieve a registered adapter.
	 * @return The unambiguously retrievable adapter for the given {@link Class}
	 *         key or <code>null</code> if none could be retrieved.
	 */
	public <T> T getAdapter(Class<T> key);

	/**
	 * Returns an adapter for the given {@link TypeToken} key if one can
	 * unambiguously be retrieved. That is, if there is only a single adapter
	 * that 'matches' the given {@link TypeToken} key, this adapter is returned,
	 * ignoring the role under which it is registered (see
	 * {@link AdapterKey#getRole()}).
	 * <p>
	 * An adapter 'matching' the {@link TypeToken} key is an adapter, which is
	 * registered with an {@link AdapterKey}, whose key (
	 * {@link AdapterKey#getKey()}) refers to the same type or a sub-type of the
	 * given type key.
	 * <p>
	 * If there is more than one adapter that 'matches' the given
	 * {@link TypeToken} key, it will return the single adapter that is
	 * registered for the default role ({@link AdapterKey#DEFAULT_ROLE}), if
	 * there is a single adapter for which this holds. Otherwise it will return
	 * <code>null</code>.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param key
	 *            The {@link TypeToken} key used to retrieve a registered
	 *            adapter.
	 * @return The unambiguously retrievable adapter for the given
	 *         {@link TypeToken} key or <code>null</code> if none could be
	 *         retrieved.
	 */
	public <T> T getAdapter(TypeToken<T> key);

	/**
	 * Returns the key under which the given adapter is bound.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param adapter
	 *            The adapter whose key to retrieve.
	 * @return The {@link AdapterKey} under which the respective adapter is
	 *         bound, or <code>null</code> if the adapter is not registered.
	 */
	public <T> AdapterKey<T> getAdapterKey(T adapter);

	/**
	 * Returns an unmodifiable {@link ObservableMap} that contains the
	 * registered adapters by their keys.
	 *
	 * @return An unmodifiable {@link ObservableMap}.
	 */
	// TODO: rename to getAdaptersUnmodifiable
	public ObservableMap<AdapterKey<?>, Object> getAdapters();

	/**
	 * Returns all adapters 'matching' the given {@link Class} key, i.e. all
	 * adapters whose {@link AdapterKey}'s {@link TypeToken} key
	 * {@link AdapterKey#getKey()}) refers to the same or a sub-type of the
	 * given {@link Class} key.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param key
	 *            The {@link Class} key to retrieve adapters for.
	 * @return A {@link Map} containing all those adapters registered at this
	 *         {@link IAdaptable}, whose {@link AdapterKey}'s {@link TypeToken}
	 *         key ({@link AdapterKey#getKey()}) refers to the same or a
	 *         sub-type of the given {@link Class} key, qualified by their
	 *         respective {@link AdapterKey}s.
	 *
	 * @see #getAdapter(Class)
	 */
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			Class<? super T> key);

	/**
	 * Returns all adapters 'matching' the given {@link TypeToken} key, i.e. all
	 * adapters whose {@link AdapterKey}'s {@link TypeToken} key
	 * {@link AdapterKey#getKey()}) refers to the same or a sub-type or of the
	 * given {@link TypeToken} key.
	 *
	 * @param <T>
	 *            The adapter type.
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
	 * Registers the given adapter under an {@link AdapterKey}, which takes the
	 * given raw type key as well as the 'default' role (see
	 * {@link AdapterKey#DEFAULT_ROLE}. The adapter may afterwards be retrieved
	 * by any type key 'in between' the given key type and actual raw type. If
	 * the actual type of the parameter is not a raw type but a parameterized
	 * type, it is not legitimate to use this method.
	 * <p>
	 * If the given adapter implements {@link IAdaptable.Bound}, the adapter
	 * will obtain a back-reference to this {@link IAdaptable} via its
	 * {@link IAdaptable.Bound#setAdaptable(IAdaptable)} method.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param adapter
	 *            The adapter to register under the given {@link Class} key.
	 *
	 * @see IAdaptable#setAdapter(Object, String)
	 */
	public <T> void setAdapter(T adapter);

	/**
	 * Registers the given adapter under the given role .
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param adapter
	 *            The adapter to register.
	 * @param role
	 *            The role to register this adapter with.
	 *
	 * @see IAdaptable#setAdapter(TypeToken, Object)
	 */
	public <T> void setAdapter(T adapter, String role);

	/**
	 * Registers the given adapter under the 'default' role (see
	 * {@link AdapterKey#DEFAULT_ROLE}.
	 * <p>
	 * If the given adapter implements {@link IAdaptable.Bound}, the adapter
	 * will obtain a back-reference to this {@link IAdaptable} via its
	 * {@link IAdaptable.Bound#setAdaptable(IAdaptable)} method.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param adapterType
	 *            The {@link TypeToken} under which to register the given
	 *            adapter, which should reflect the actual adapter type.
	 * @param adapter
	 *            The adapter to register under the given {@link TypeToken} key.
	 *
	 * @see #setAdapter(TypeToken, Object, String)
	 */
	public <T> void setAdapter(TypeToken<T> adapterType, T adapter);

	/**
	 * Registers the given adapter under the given role.
	 * <p>
	 * If the given adapter implements {@link IAdaptable.Bound}, the adapter
	 * will obtain a back-reference to this {@link IAdaptable} via its
	 * {@link IAdaptable.Bound#setAdaptable(IAdaptable)} method.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param adapterType
	 *            A {@link TypeToken} representing the actual type of the given
	 *            adapter.
	 * @param adapter
	 *            The adapter to register.
	 * @param role
	 *            The role under which to register the adapter.
	 *
	 */
	public <T> void setAdapter(TypeToken<T> adapterType, T adapter,
			String role);

	/**
	 * Unregisters the given adapter under all keys it was registered for.
	 * <p>
	 * If the given adapter implements {@link IAdaptable.Bound}, the
	 * back-reference to this {@link IAdaptable} will be removed via its
	 * {@link IAdaptable.Bound#setAdaptable(IAdaptable)} method, passing over
	 * <code>null</code>.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param adapter
	 *            The adapter which should be removed.
	 */
	public <T> void unsetAdapter(T adapter);
}
