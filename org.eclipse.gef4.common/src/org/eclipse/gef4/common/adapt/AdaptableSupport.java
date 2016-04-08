/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import java.beans.PropertyChangeSupport;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.gef4.common.activate.ActivatableSupport;
import org.eclipse.gef4.common.activate.IActivatable;
import org.eclipse.gef4.common.beans.property.ReadOnlyMapWrapperEx;
import org.eclipse.gef4.common.dispose.IDisposable;

import com.google.common.reflect.TypeToken;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * A support class to manage adapters for a source {@link IAdaptable}. It offers
 * all methods defined by {@link IAdaptable}, while not formally implementing
 * the interface, and can thus be used by a source {@link IAdaptable} as a
 * delegate.
 * <p>
 * If the {@link IAdaptable} is also {@link IActivatable}, it will ensure
 * adapters are activated/deactivated when being set/unset dependent on the
 * active state of the adaptable at that moment. However, the
 * {@link AdaptableSupport} will not register a listener for the active state of
 * the source {@link IAdaptable}, so changes to its active state will not result
 * in state changes of the registered adapters. For this purpose, an
 * {@link ActivatableSupport} may be used by the source {@link IAdaptable} as a
 * second delegate.
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

	// XXX: We keep a sorted map of adapters (so activation/deactivation is in
	// deterministic order)
	private ObservableMap<AdapterKey<?>, Object> adapters = FXCollections
			.observableMap(new TreeMap<AdapterKey<?>, Object>());
	private ObservableMap<AdapterKey<?>, Object> adaptersUnmodifiable = FXCollections
			.unmodifiableObservableMap(adapters);
	private ReadOnlyMapWrapperEx<AdapterKey<?>, Object> adaptersUnmodifiableProperty = null;
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
		this.adaptersUnmodifiableProperty = new ReadOnlyMapWrapperEx<>(source,
				IAdaptable.ADAPTERS_PROPERTY, adaptersUnmodifiable);
	}

	private void activateAdapters() {
		for (IActivatable adapter : this
				.<IActivatable> getAdapters(IActivatable.class).values()) {
			adapter.activate();
		}
	}

	/**
	 * Returns a read-only map property, containing the adapters mapped to their
	 * keys.
	 *
	 * @return A read-only map property.
	 */
	public ReadOnlyMapProperty<AdapterKey<?>, Object> adaptersProperty() {
		return adaptersUnmodifiableProperty.getReadOnlyProperty();
	}

	private void deactivateAdapters() {
		for (IActivatable adapter : this
				.<IActivatable> getAdapters(IActivatable.class).values()) {
			adapter.deactivate();
		}
	}

	/**
	 * Disposes this {@link AdaptableSupport}, which will unregister all
	 * currently registered adapters, unbind them from their source
	 * {@link IAdaptable} (in case they are {@link IAdaptable.Bound}), and
	 * dispose them (if they are {@link IDisposable}). No notification will be
	 * fired to notify listeners about the unregistering of adapters. It is
	 * expected that in case the source {@link IAdaptable} is
	 * {@link IActivatable}, it is deactivated before disposing this
	 * {@link AdaptableSupport}.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void dispose() {
		// deactivate already registered adapters, if adaptable is
		// IActivatable
		// and currently active (thus adapters are also active)
		if (source instanceof IActivatable
				&& ((IActivatable) source).isActive()) {
			throw new IllegalStateException(
					"source needs to be deactivated before disposing this AdaptableSupport.");
		}

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
	 * Retrieves all registered adapters, mapped to the respective
	 * {@link AdapterKey}s they are registered.
	 *
	 * @return An unmodifiable observable map containing the registered adapters
	 *         under their {@link AdapterKey}s as a copy.
	 */
	public ObservableMap<AdapterKey<?>, Object> getAdapters() {
		return adaptersUnmodifiable;
	}

	/**
	 * Returns all adapters 'matching' the given {@link Class} key, i.e. all
	 * adapters whose {@link AdapterKey}'s {@link TypeToken} key
	 * {@link AdapterKey#getKey()}) refers to the same or a sub-type of the
	 * given {@link Class} key (see {@link TypeToken#isAssignableFrom(Type)}).
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
	 * given {@link TypeToken} key (see
	 * {@link TypeToken#isAssignableFrom(TypeToken)}).
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
			if (key.isAssignableFrom(k.getKey())) {
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
				if (typeKey.isAssignableFrom(k.getKey())) {
					typeSafeAdapters.put((AdapterKey<? extends T>) k,
							(T) adapters.get(k));

				}
			}
		}
		return typeSafeAdapters;
	}

	private <T> boolean isRawType(TypeToken<? super T> typeKey) {
		return !(typeKey.getType() instanceof ParameterizedType);
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
		TypeToken<T> actualType = TypeToken.of((Class<T>) adapter.getClass());
		if (!isRawType(actualType)) {
			throw new IllegalArgumentException("Adapter " + adapter
					+ " has no raw type, thus needs to be registered with a type key reflecting its actual type");
		}
		setAdapter(actualType, adapter);
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
		// TODO: these checks do not seem to be valid...
		TypeToken<T> actualType = TypeToken.of((Class<T>) adapter.getClass());
		if (!isRawType(actualType)) {
			// check that all parameters are bound to actual types
			for (Type argument : ((ParameterizedType) actualType.getType())
					.getActualTypeArguments()) {
				if (argument instanceof TypeVariable) {
					throw new IllegalArgumentException("Adapter " + adapter
							+ " has no raw type, thus needs to be registered with a type key reflecting its actual type");
				}
			}
		}

		setAdapter(actualType, adapter, role);
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
		// TODO: check if we can make that even better (now we restrict the
		// check to raw types)
		if (!adapterType.getRawType().isAssignableFrom(adapter.getClass())
				|| !adapter.getClass()
						.isAssignableFrom(adapterType.getRawType())) {
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

		// deactivate already registered adapters, if adaptable is
		// IActivatable
		// and currently active
		if (source instanceof IActivatable
				&& ((IActivatable) source).isActive()) {
			deactivateAdapters();
		}

		adapters.put(key, adapter);
		if (adapter instanceof IAdaptable.Bound) {
			((IAdaptable.Bound<A>) adapter).setAdaptable(source);
		}

		// activate all adapters, if adaptable is IActivatable and
		// currently
		// active
		if (source instanceof IActivatable
				&& ((IActivatable) source).isActive()) {
			activateAdapters();
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

		// deactivate already registered adapters, if adaptable is
		// IActivatable
		// and currently active
		if (source instanceof IActivatable
				&& ((IActivatable) source).isActive()) {
			deactivateAdapters();
		}

		// process all keys and remove those pointing to the given adapter
		for (AdapterKey<?> key : adapters.keySet()) {
			if (adapters.get(key) == adapter) {
				adapters.remove(key);
			}
		}

		if (adapter instanceof IAdaptable.Bound) {
			((IAdaptable.Bound<A>) adapter).setAdaptable(null);
		}

		// re-activate remaining adapters, if adaptable is IActivatable
		// and currently active
		if (source instanceof IActivatable
				&& ((IActivatable) source).isActive()) {
			activateAdapters();
		}
	}

}
