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

import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.gef.common.beans.property.ReadOnlyMapWrapperEx;
import org.eclipse.gef.common.dispose.IDisposable;
import org.eclipse.gef.common.reflect.Types;

import com.google.common.reflect.TypeToken;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * A support class to manage adapters for a source {@link IAdaptable}. It offers
 * all methods defined by {@link IAdaptable}, while not formally implementing
 * the interface, and can thus be used by a source {@link IAdaptable} as a
 * delegate.
 *
 * @author anyssen
 *
 * @param <A>
 *            The type of {@link IAdaptable} supported by this class. If
 *            passed-in adapters implement the {@link IAdaptable.Bound}
 *            interface, the generic type parameter of {@link IAdaptable.Bound}
 *            has to match this one.
 */
public class AdaptableSupport<A extends IAdaptable> implements IDisposable {

	// XXX: We keep a sorted map of adapters to have a deterministic order
	private ObservableMap<AdapterKey<?>, Object> adapters = FXCollections
			.observableMap(new TreeMap<AdapterKey<?>, Object>());
	// XXX: We keep a reverse map to speed up key retrieval
	private Map<Object, AdapterKey<?>> keys = new HashMap<>();
	private ObservableMap<AdapterKey<?>, Object> adaptersUnmodifiable;
	private ReadOnlyMapWrapperEx<AdapterKey<?>, Object> adaptersUnmodifiableProperty;
	private A source;

	/**
	 * Creates a new {@link AdaptableSupport} for the given source
	 * {@link IAdaptable} and a related {@link PropertyChangeSupport}.
	 *
	 * @param source
	 *            The {@link IAdaptable} that encloses the to be created
	 *            {@link AdaptableSupport}, delegating calls to it. May not be
	 *            <code>null</code>
	 */
	public AdaptableSupport(A source) {
		if (source == null) {
			throw new IllegalArgumentException("source may not be null.");
		}
		this.source = source;
	}

	/**
	 * Returns a read-only map property, containing the adapters mapped to their
	 * keys.
	 *
	 * @return A read-only map property.
	 */
	// TODO: renamed to adaptersUnmodifiableProperty
	public ReadOnlyMapProperty<AdapterKey<?>, Object> adaptersProperty() {
		if (adaptersUnmodifiableProperty == null) {
			adaptersUnmodifiableProperty = new ReadOnlyMapWrapperEx<>(source,
					IAdaptable.ADAPTERS_PROPERTY, getAdapters());
		}
		return adaptersUnmodifiableProperty.getReadOnlyProperty();
	}

	/**
	 * Disposes this {@link AdaptableSupport}, which will unregister all
	 * currently registered adapters, unbind them from their source
	 * {@link IAdaptable} (in case they are {@link IAdaptable.Bound}), and
	 * dispose them (if they are {@link IDisposable}).
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void dispose() {
		Map<AdapterKey<?>, Object> oldAdapters = new HashMap<>(adapters);
		for (AdapterKey<?> key : oldAdapters.keySet()) {
			Object adapter = adapters.remove(key);
			if (adapter != null) {
				// unbind adapter (if its bound)
				if (adapter instanceof IAdaptable.Bound) {
					((IAdaptable.Bound<A>) adapter).setAdaptable(null);
				}
			}

			// dispose adapter (if its disposable)
			if (adapter instanceof IDisposable) {
				((IDisposable) adapter).dispose();
			}
		}

		adapters.clear();
		source = null;
	}

	/**
	 * Returns an adapter for the given {@link AdapterKey} if one can
	 * unambiguously be retrieved, i.e. if there is only a single adapter
	 * registered under a key that 'matches' the given {@link AdapterKey}.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param key
	 *            The {@link AdapterKey} used to retrieve a registered adapter.
	 * @return The unambiguously retrievable adapter for the given
	 *         {@link AdapterKey} or <code>null</code> if none could be
	 *         retrieved.
	 *
	 * @see IAdaptable#getAdapter(AdapterKey)
	 */
	public <T> T getAdapter(AdapterKey<T> key) {
		if (adapters.isEmpty()) {
			return null;
		}

		// see if we can unambiguously retrieve a matching adapter
		Map<AdapterKey<? extends T>, T> adaptersForTypeKey = getAdapters(
				key.getKey(), key.getRole());

		// an adapter instance may be registered under different keys
		int adapterCount = new HashSet<>(adaptersForTypeKey.values()).size();
		if (adapterCount == 1) {
			return adaptersForTypeKey.values().iterator().next();
		}

		return null;
	}

	/**
	 * Returns an adapter for the given {@link Class} key if one can
	 * unambiguously be retrieved. That is, if there is only a single adapter
	 * that 'matches' the given {@link Class} key, this adapter is returned,
	 * ignoring the role under which it is registered (see
	 * {@link AdapterKey#getRole()}).
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param key
	 *            The {@link Class} key used to retrieve a registered adapter.
	 * @return The unambiguously retrievable adapter for the given {@link Class}
	 *         key or <code>null</code> if none could be retrieved.
	 *
	 * @see IAdaptable#getAdapter(Class)
	 */
	public <T> T getAdapter(Class<T> key) {
		return this.<T> getAdapter(TypeToken.of(key));
	}

	/**
	 * Returns an adapter for the given {@link TypeToken} key if one can
	 * unambiguously be retrieved. That is, if there is only a single adapter
	 * that 'matches' the given {@link TypeToken} key, this adapter is returned,
	 * ignoring the role under which it is registered (see
	 * {@link AdapterKey#getRole()}).
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param key
	 *            The {@link TypeToken} key used to retrieve a registered
	 *            adapter.
	 * @return The unambiguously retrievable adapter for the given
	 *         {@link TypeToken} key or <code>null</code> if none could be
	 *         retrieved.
	 *
	 * @see IAdaptable#getAdapter(TypeToken)
	 */
	public <T> T getAdapter(TypeToken<T> key) {
		// if we have only one adapter (instance) for the given type key
		// (disregarding the
		// role), return this one
		Map<AdapterKey<? extends T>, T> adaptersForTypeKey = getAdapters(key,
				null);

		// an adapter instance may be registered under different keys
		int adapterCount = new HashSet<>(adaptersForTypeKey.values()).size();
		if (adapterCount == 1) {
			return adaptersForTypeKey.values().iterator().next();
		}

		if (adapterCount > 1) {
			// if we have more than one adapter instance, try to retrieve one
			// unambiguously by using the default role
			return getAdapter(AdapterKey.get(key, AdapterKey.DEFAULT_ROLE));
		}

		return null;
	}

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
	@SuppressWarnings("unchecked")
	public <T> AdapterKey<T> getAdapterKey(T adapter) {
		return (AdapterKey<T>) keys.get(adapter);
	}

	/**
	 * Retrieves all registered adapters, mapped to the respective
	 * {@link AdapterKey}s they are registered.
	 *
	 * @return An unmodifiable observable map containing the registered adapters
	 *         under their {@link AdapterKey}s as a copy.
	 */
	public ObservableMap<AdapterKey<?>, Object> getAdapters() {
		if (adaptersUnmodifiable == null) {
			adaptersUnmodifiable = FXCollections
					.unmodifiableObservableMap(adapters);
		}
		return adaptersUnmodifiable;
	}

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
	 *         {@link AdaptableSupport}, whose {@link AdapterKey}'s
	 *         {@link TypeToken} key ({@link AdapterKey#getKey()}) refers to the
	 *         same or a sub-type of the given {@link Class} key, qualified by
	 *         their respective {@link AdapterKey}s.
	 *
	 * @see IAdaptable#getAdapters(Class)
	 */
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			Class<? super T> key) {
		return getAdapters(TypeToken.of(key));
	}

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
	 *         {@link AdaptableSupport}, whose {@link AdapterKey}'s
	 *         {@link TypeToken} key ({@link AdapterKey#getKey()}) refers to the
	 *         same or a sub-type of the given {@link TypeToken} key, qualified
	 *         by their respective {@link AdapterKey}s.
	 *
	 * @see IAdaptable#getAdapters(TypeToken)
	 */
	@SuppressWarnings("unchecked")
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			TypeToken<? super T> key) {
		if (adapters.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<AdapterKey<? extends T>, T> typeSafeAdapters = new TreeMap<>();
		for (AdapterKey<?> k : adapters.keySet()) {
			if (Types.isAssignable(key, k.getKey())) {
				// check type compliance...
				typeSafeAdapters.put((AdapterKey<? extends T>) k,
						(T) adapters.get(k));
			}
		}
		return typeSafeAdapters;
	}

	@SuppressWarnings("unchecked")
	private <T> Map<AdapterKey<? extends T>, T> getAdapters(
			TypeToken<? super T> typeKey, String role) {
		if (typeKey == null) {
			throw new IllegalArgumentException("typeKey may not be null");
		}
		if (adapters.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<AdapterKey<? extends T>, T> typeSafeAdapters = new TreeMap<>();
		for (AdapterKey<?> k : adapters.keySet()) {
			if (role == null || k.getRole().equals(role)) {
				// return all adapters assignable to the given type
				// key
				if (Types.isAssignable(typeKey, k.getKey())) {
					typeSafeAdapters.put((AdapterKey<? extends T>) k,
							(T) adapters.get(k));
				}
			}
		}
		return typeSafeAdapters;
	}

	/**
	 * Registers the given adapter under the default role (see
	 * {@link AdapterKey#DEFAULT_ROLE}.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param adapter
	 *            The adapter to register under the given {@link Class} key.
	 *
	 * @see IAdaptable#setAdapter(TypeToken, Object)
	 */
	@SuppressWarnings("unchecked")
	public <T> void setAdapter(T adapter) {
		setAdapter(TypeToken.of((Class<T>) adapter.getClass()), adapter);
	}

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
	@SuppressWarnings("unchecked")
	public <T> void setAdapter(T adapter, String role) {
		setAdapter(TypeToken.of((Class<T>) adapter.getClass()), adapter, role);
	}

	/**
	 * Registers the given adapter under an {@link AdapterKey}, which will use
	 * the given {@link TypeToken} key as well as the default role (see
	 * {@link AdapterKey#DEFAULT_ROLE}.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param adapterType
	 *            The {@link TypeToken} reflecting the actual type of the
	 *            adapter.
	 * @param adapter
	 *            The adapter to register.
	 *
	 */
	public <T> void setAdapter(TypeToken<T> adapterType, T adapter) {
		setAdapter(adapterType, adapter, AdapterKey.DEFAULT_ROLE);
	}

	/**
	 * Registers the given adapter under the given {@link AdapterKey}. The
	 * {@link AdapterKey} should provide the actual type of the adapter plus a
	 * role.
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
	@SuppressWarnings("unchecked")
	public <T> void setAdapter(TypeToken<T> adapterType, T adapter,
			String role) {
		// we can only check raw types here because of type erasure
		TypeToken<? extends Object> instanceType = TypeToken
				.of(adapter.getClass());
		// if we have an adapter of an anonymous class, it has to be assignable
		// to the passed in adapter type; otherwise the raw types should be
		// equal (so that the specified adapter type is as 'good' as possible.
		if (instanceType.getRawType().isAnonymousClass() && !adapterType
				.getRawType().isAssignableFrom(instanceType.getRawType())) {
			throw new IllegalArgumentException("The passed in adapter type "
					+ adapterType.getRawType().getSimpleName()
					+ " is not assignable from the runtime (raw) type of the adapter, which is "
					+ instanceType.getRawType().getSimpleName());
		} else if (!instanceType.getRawType().isAnonymousClass()
				&& !instanceType.getRawType()
						.equals(adapterType.getRawType())) {
			throw new IllegalArgumentException("The given adapter type "
					+ adapterType.getType().getClass().getSimpleName()
					+ " does not match the passed in adapter's type "
					+ adapter.getClass().getSimpleName());
		}

		AdapterKey<T> key = AdapterKey.get(adapterType, role);
		if (adapters.containsKey(key)) {
			if (adapters.get(key) != adapter) {
				throw new IllegalArgumentException("A different adapter ("
						+ adapter + ") is already registered with key " + key
						+ " at adaptable " + source);
			} else {
				System.err.println("The adapter " + adapter
						+ " was already registered with key " + key
						+ " at adaptable " + source);
			}
		}

		adapters.put(key, adapter);
		keys.put(adapter, key);

		if (adapter instanceof IAdaptable.Bound) {
			((IAdaptable.Bound<A>) adapter).setAdaptable(source);
		}
	}

	/**
	 * Unregisters the adapter, returning it for convenience.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param adapter
	 *            The adapter to unregister.
	 * @see IAdaptable#unsetAdapter(Object)
	 */
	@SuppressWarnings("unchecked")
	public <T> void unsetAdapter(T adapter) {
		if (!adapters.containsValue(adapter)) {
			throw new IllegalArgumentException(
					"Given adapter is not registered.");
		}

		if (adapter instanceof IAdaptable.Bound) {
			((IAdaptable.Bound<A>) adapter).setAdaptable(null);
		}

		adapters.remove(keys.remove(adapter));
	}

}
