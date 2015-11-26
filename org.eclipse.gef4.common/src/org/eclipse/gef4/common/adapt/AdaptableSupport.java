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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.gef4.common.activate.ActivatableSupport;
import org.eclipse.gef4.common.activate.IActivatable;
import org.eclipse.gef4.common.dispose.IDisposable;

import com.google.common.reflect.TypeToken;

/**
 * A support class to manage adapters for a source {@link IAdaptable}. It offers
 * all methods defined by {@link IAdaptable}, while not formally implementing
 * the interface, and can thus be used by a source {@link IAdaptable} as a
 * delegate.
 * <p>
 * In addition to the source {@link IAdaptable} a {@link PropertyChangeSupport}
 * is expected to be passe in during construction. It will be used to fire
 * {@link PropertyChangeEvent}s whenever an adapter is set (
 * {@link #setAdapter(AdapterKey, TypeToken, Object)}) or unset (
 * {@link #unsetAdapter(Object)}). {@link IAdaptable#ADAPTERS_PROPERTY} will be
 * used as the property name within all change events.
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

	private A source;
	private PropertyChangeSupport pcs;
	private Map<AdapterKey<?>, Object> adapters;
	private Map<Object, TypeToken<?>> adapterTypes;

	/**
	 * Creates a new {@link AdaptableSupport} for the given source
	 * {@link IAdaptable} and a related {@link PropertyChangeSupport}.
	 * 
	 * @param source
	 *            The {@link IAdaptable} that encloses the to be created
	 *            {@link AdaptableSupport}, delegating calls to it. May not be
	 *            <code>null</code>
	 * @param pcs
	 *            An {@link PropertyChangeSupport}, which will be used to fire
	 *            {@link PropertyChangeEvent}'s whenever adapters are set or
	 *            unset. May not be <code>null</code>
	 */
	public AdaptableSupport(A source, PropertyChangeSupport pcs) {
		if (source == null) {
			throw new IllegalArgumentException("source may not be null.");
		}
		if (pcs == null) {
			throw new IllegalArgumentException("pcs may not be null.");
		}
		this.source = source;
		this.pcs = pcs;
	}

	private void activateAdapters() {
		for (IActivatable adapter : this
				.<IActivatable> getAdapters(IActivatable.class).values()) {
			adapter.activate();
		}
	}

	private void deactivateAdapters() {
		for (IActivatable adapter : this
				.<IActivatable> getAdapters(IActivatable.class).values()) {
			adapter.deactivate();
		}
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
	public <T> T getAdapter(AdapterKey<? super T> key) {
		if (adapters == null) {
			return null;
		}

		// see if we can unambiguously retrieve a matching adapter
		Map<AdapterKey<? extends T>, T> adaptersForTypeKey = getAdapters(
				key.getKey(), key.getRole());

		// an adapter instance may be registered under different keys
		int adapterCount = new HashSet<T>(adaptersForTypeKey.values()).size();
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
	public <T> T getAdapter(Class<? super T> key) {
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
	public <T> T getAdapter(TypeToken<? super T> key) {
		// if we have only one adapter (instance) for the given type key
		// (disregarding the
		// role), return this one
		Map<AdapterKey<? extends T>, T> adaptersForTypeKey = getAdapters(key,
				null);

		// an adapter instance may be registered under different keys
		int adapterCount = new HashSet<T>(adaptersForTypeKey.values()).size();
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
	 * @return A map containing the registered adapters under their
	 *         {@link AdapterKey}s as a copy.
	 */
	protected Map<AdapterKey<?>, Object> getAdapters() {
		if (adapters == null) {
			return Collections.emptyMap();
		}
		return new HashMap<AdapterKey<?>, Object>(adapters);
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
		if (adapters == null) {
			return Collections.emptyMap();
		}
		Map<AdapterKey<? extends T>, T> typeSafeAdapters = new HashMap<AdapterKey<? extends T>, T>();
		if (adapters != null) {
			for (AdapterKey<?> k : adapters.keySet()) {
				if (key.isAssignableFrom(k.getKey())) {
					// check type compliance...
					typeSafeAdapters.put((AdapterKey<? extends T>) k,
							(T) adapters.get(k));
				}
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
		if (adapters == null) {
			return Collections.emptyMap();
		}
		Map<AdapterKey<? extends T>, T> typeSafeAdapters = new HashMap<AdapterKey<? extends T>, T>();
		if (adapters != null) {
			for (AdapterKey<?> k : adapters.keySet()) {
				if (k.getKey().isAssignableFrom(typeKey)
						&& typeKey.isAssignableFrom(
								adapterTypes.get(adapters.get(k)))
						&& (role == null || k.getRole().equals(role))) {
					// check type compliance...
					typeSafeAdapters.put((AdapterKey<? extends T>) k,
							(T) adapters.get(k));
				}
			}
		}
		return typeSafeAdapters;
	}

	/**
	 * Registers the given adapter under an {@link AdapterKey}, which will use a
	 * {@link TypeToken} representing the given {@link Class} key, i.e. using
	 * {@link TypeToken#of(Class)}, as well as the default role (see
	 * {@link AdapterKey#DEFAULT_ROLE}.
	 * 
	 * @param <T>
	 *            The adapter type.
	 * @param key
	 *            The {@link Class} under which to register the given adapter.
	 * @param adapter
	 *            The adapter to register under the given {@link Class} key.
	 * 
	 * @see IAdaptable#setAdapter(Class, Object)
	 */
	@SuppressWarnings("unchecked")
	public <T> void setAdapter(Class<T> key, T adapter) {
		if (adapter.getClass().getTypeParameters().length > 0) {
			throw new IllegalArgumentException("Adapter " + adapter
					+ " needs to be registered with runtime type information, because its not a raw type and its parameter type cannot be inferred.");
		}
		setAdapter(TypeToken.of(key), TypeToken.of((Class<T>) adapter.getClass()), adapter);
	}

	/**
	 * Registers the given adapter under an {@link AdapterKey}, which will use
	 * the given {@link TypeToken} key as well as the default role (see
	 * {@link AdapterKey#DEFAULT_ROLE}.
	 * 
	 * @param <T>
	 *            The adapter type.
	 * @param key
	 *            The {@link TypeToken} under which to register the given
	 *            adapter.
	 * @param adapterType
	 *            The runtime type of the given adapter.
	 * @param adapter
	 *            The adapter to register under the given {@link TypeToken} key.
	 * 
	 * @see IAdaptable#setAdapter(TypeToken, TypeToken, Object)
	 */
	public <T> void setAdapter(TypeToken<? super T> key,
			TypeToken<T> adapterType, T adapter) {
		setAdapter(AdapterKey.get(key, AdapterKey.DEFAULT_ROLE), adapterType,
				adapter);
	}

	/**
	 * Registers the given adapter under the given {@link AdapterKey}. The
	 * adapter has to be compliant to the {@link AdapterKey}, i.e. it has to be
	 * of the same type or a sub-type of the {@link AdapterKey}'s type key (
	 * {@link AdapterKey#getKey()}).
	 * 
	 * @param <T>
	 *            The adapter type.
	 * @param key
	 *            The {@link AdapterKey} under which to register the given
	 *            adapter.
	 * @param adapterType
	 *            The runtime type of the to be registered adapter.
	 * @param adapter
	 *            The adapter to register under the given {@link AdapterKey}.
	 * 
	 * @see IAdaptable#setAdapter(AdapterKey, TypeToken, Object)
	 */
	@SuppressWarnings("unchecked")
	public <T> void setAdapter(AdapterKey<? super T> key,
			TypeToken<T> adapterType, T adapter) {
		if (!key.getKey().isAssignableFrom(adapterType)) {
			throw new IllegalArgumentException("Key type "
					+ key.getKey().getType().getClass().getSimpleName()
					+ " is not assignable from adapter type "
					+ adapterType.getType().getClass().getSimpleName());
		}

		if (adapters == null) {
			adapters = new HashMap<AdapterKey<?>, Object>();
		}
		if (adapterTypes == null) {
			adapterTypes = new HashMap<Object, TypeToken<?>>();
		}

		// deactivate already registered adapters, if adaptable is IActivatable
		// and currently active
		if (source instanceof IActivatable
				&& ((IActivatable) source).isActive()) {
			deactivateAdapters();
		}

		Map<AdapterKey<?>, Object> oldAdapters = new HashMap<AdapterKey<?>, Object>(
				adapters);

		adapters.put(key, adapter);
		if (adapterTypes.containsKey(adapter)) {
			TypeToken<?> registeredAdapterType = adapterTypes.get(adapter);
			if (registeredAdapterType.isAssignableFrom(adapterType)) {
				adapterTypes.put(adapter, adapterType);
			}
		} else {
			adapterTypes.put(adapter, adapterType);
		}

		if (adapter instanceof IAdaptable.Bound) {
			((IAdaptable.Bound<A>) adapter).setAdaptable(source);
		}

		// activate all adapters, if adaptable is IActivatable and currently
		// active
		if (source instanceof IActivatable
				&& ((IActivatable) source).isActive()) {
			activateAdapters();
		}

		pcs.firePropertyChange(IAdaptable.ADAPTERS_PROPERTY, oldAdapters,
				new HashMap<AdapterKey<?>, Object>(adapters));
	}

	/**
	 * Registers the given adapter under the given {@link AdapterKey}. The
	 * adapter has to be compliant to the {@link AdapterKey}, i.e. it has to be
	 * of the same type or a sub-type of the {@link AdapterKey}'s type key (
	 * {@link AdapterKey#getKey()}).
	 * 
	 * @param <T>
	 *            The adapter type.
	 * @param key
	 *            The {@link AdapterKey} under which to register the given
	 *            adapter.
	 * @param adapter
	 *            The adapter to register under the given {@link AdapterKey}.
	 * 
	 * @see IAdaptable#setAdapter(AdapterKey, Object)
	 */
	@SuppressWarnings("unchecked")
	public <T> void setAdapter(AdapterKey<T> key, T adapter) {
		if (adapter.getClass().getTypeParameters().length > 0) {
			throw new IllegalArgumentException("Adapter " + adapter
					+ " needs to be registered with runtime type information, because its not a raw type and its parameter type cannot be inferred.");
		}
		setAdapter(key, TypeToken.of((Class<T>) adapter.getClass()), adapter);
	}

	/**
	 * Unregisters the adapter, returning it for convenience.
	 * 
	 * @param <T>
	 *            The adapter type.
	 * @param adapter
	 *            The adapter to unregister.
	 * @see IAdaptable#unsetAdapter(AdapterKey)
	 */
	@SuppressWarnings("unchecked")
	public <T> void unsetAdapter(T adapter) {
		if (adapters == null || !adapters.containsValue(adapter)) {
			throw new IllegalArgumentException("Given key is not registered.");
		}

		// deactivate already registered adapters, if adaptable is IActivatable
		// and currently active
		if (source instanceof IActivatable
				&& ((IActivatable) source).isActive()) {
			deactivateAdapters();
		}

		Map<AdapterKey<?>, Object> oldAdapters = new HashMap<AdapterKey<?>, Object>(
				adapters);

		// process all keys and remove those pointing to the given adapter
		for (AdapterKey<?> key : adapters.keySet()) {
			if (adapters.get(key) == adapter) {
				adapters.remove(key);
			}
		}
		adapterTypes.remove(adapter);

		if (adapter instanceof IAdaptable.Bound) {
			((IAdaptable.Bound<A>) adapter).setAdaptable(null);
		}

		// re-activate remaining adapters, if adaptable is IActivatable
		// and currently active
		if (source instanceof IActivatable
				&& ((IActivatable) source).isActive()) {
			activateAdapters();
		}

		pcs.firePropertyChange(IAdaptable.ADAPTERS_PROPERTY, oldAdapters,
				new HashMap<AdapterKey<?>, Object>(adapters));

		if (adapters.size() == 0) {
			adapters = null;
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
	@SuppressWarnings("unchecked")
	public void dispose() {
		// deactivate already registered adapters, if adaptable is IActivatable
		// and currently active (thus adapters are also active)
		if (source instanceof IActivatable
				&& ((IActivatable) source).isActive()) {
			throw new IllegalStateException(
					"source needs to be deactivated before disposing this AdaptableSupport.");
		}

		Map<AdapterKey<?>, Object> oldAdapters = new HashMap<AdapterKey<?>, Object>(
				adapters);

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
		adapters = null;
	}

}
