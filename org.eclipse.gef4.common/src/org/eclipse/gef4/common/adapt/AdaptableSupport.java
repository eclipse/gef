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
import java.beans.PropertyChangeSupport;
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
 * {@link #setAdapter(AdapterKey, Object)}) or unset (
 * {@link #unsetAdapter(AdapterKey)}). {@link IAdaptable#ADAPTERS_PROPERTY} will
 * be used as the property name within all change events.
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
public class AdaptableSupport<A extends IAdaptable> {

	private A source;
	private PropertyChangeSupport pcs;
	private Map<AdapterKey<?>, Object> adapters;

	/**
	 * Creates a new {@link AdaptableSupport} for the given source
	 * {@link IAdaptable} and a related {@link PropertyChangeSupport}.
	 * 
	 * @param source
	 *            The {@link IAdaptable} that encloses the to be created
	 *            {@link AdaptableSupport}, delegating calls to it.
	 * @param pcs
	 *            An {@link PropertyChangeSupport}, which will be used to fire
	 *            {@link PropertyChangeEvent}'s whenever adapters are set or
	 *            unset.
	 */
	public AdaptableSupport(A source, PropertyChangeSupport pcs) {
		this.source = source;
		this.pcs = pcs;
	}

	private void activateAdapters() {
		for (IActivatable adapter : this.<IActivatable> getAdapters(
				IActivatable.class).values()) {
			adapter.activate();
		}
	}

	private void deactivateAdapters() {
		for (IActivatable adapter : this.<IActivatable> getAdapters(
				IActivatable.class).values()) {
			adapter.deactivate();
		}
	}

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
			return (T) adaptersForTypeKey.values().iterator().next();
		}

		return null;
	}

	public <T> T getAdapter(Class<? super T> key) {
		return this.<T> getAdapter(TypeToken.of(key));
	}

	@SuppressWarnings("unchecked")
	public <T> T getAdapter(TypeToken<? super T> key) {
		// if we have only one adapter (instance) for the given type key
		// (disregarding the
		// role), return this one
		Map<AdapterKey<? extends T>, T> adaptersForTypeKey = getAdapters(key,
				null);

		// an adapter instance may be registered under different keys
		int adapterCount = new HashSet<T>(adaptersForTypeKey.values()).size();
		if (adapterCount == 1) {
			return (T) adaptersForTypeKey.values().iterator().next();
		}

		if (adapterCount > 1) {
			// if we have more than one adapter instance, try to retrieve one
			// unambiguously by using the default role
			return getAdapter(AdapterKey.get(key, AdapterKey.DEFAULT_ROLE));
		}

		return null;
	}

	public Map<AdapterKey<?>, Object> getAdapters() {
		if (adapters == null) {
			return Collections.emptyMap();
		}
		return adapters;
	}

	public <T> Map<AdapterKey<? extends T>, T> getAdapters(Class<? super T> key) {
		return getAdapters(TypeToken.of(key));
	}

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

	private <T> Map<AdapterKey<? extends T>, T> getAdapters(
			TypeToken<? super T> typeKey, String role) {
		if (adapters == null) {
			return Collections.emptyMap();
		}
		Map<AdapterKey<? extends T>, T> typeSafeAdapters = new HashMap<AdapterKey<? extends T>, T>();
		if (adapters != null) {
			for (AdapterKey<?> k : adapters.keySet()) {
				if ((typeKey == null || typeKey.isAssignableFrom(k.getKey()))
						&& (role == null || k.getRole().equals(role))) {
					// check type compliance...
					typeSafeAdapters.put((AdapterKey<? extends T>) k,
							(T) adapters.get(k));
				}
			}
		}
		return typeSafeAdapters;
	}

	public <T> void setAdapter(Class<? super T> key, T adapter) {
		setAdapter(AdapterKey.get(TypeToken.of(key), AdapterKey.DEFAULT_ROLE),
				adapter);
	}

	public <T> void setAdapter(TypeToken<? super T> key, T adapter) {
		setAdapter(AdapterKey.get(key, AdapterKey.DEFAULT_ROLE), adapter);
	}

	@SuppressWarnings("unchecked")
	public <T> void setAdapter(AdapterKey<? super T> key, T adapter) {
		if (adapters == null) {
			adapters = new HashMap<AdapterKey<?>, Object>();
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
	 * Registers the given adapters under the provided keys by delegating to
	 * {@link #setAdapter(AdapterKey, Object)}. Note, that delegation will only
	 * occur for those adapters, whose key is not already bound to an adapter,
	 * in case <i>overwrite</i> is set to <code>false</code>.
	 * 
	 * @param adaptersWithKeys
	 *            A map of class keys and related adapters to be added via
	 *            {@link #setAdapter(AdapterKey, Object)}.
	 * @param overwrite
	 *            Indicates whether adapters whose keys are already registered
	 *            for another adapter should be ignored. If set to
	 *            <code>true</code> existing entries will be overwritten,
	 *            otherwise, existing entries will be preserved.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setAdapters(Map<AdapterKey<?>, Object> adaptersWithKeys,
			boolean overwrite) {
		for (AdapterKey<?> key : adaptersWithKeys.keySet()) {
			if (!key.getKey().isAssignableFrom(
					adaptersWithKeys.get(key).getClass())) {
				throw new IllegalArgumentException(
						key
								+ " is not a valid key for "
								+ adaptersWithKeys.get(key)
								+ ", as its neither a super interface nor a super class of its type.");
			}
			if (overwrite || !getAdapters().containsKey(key)) {
				setAdapter((AdapterKey) key, (Object) adaptersWithKeys.get(key));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T unsetAdapter(AdapterKey<? super T> key) {
		if (adapters == null || !adapters.containsKey(key)) {
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

		Object adapter = adapters.remove(key);
		if (adapter != null) {
			if (adapter instanceof IAdaptable.Bound) {
				((IAdaptable.Bound<A>) adapter).setAdaptable(null);
			}
		}

		// re-activate remaining adapters, if adaptable is IActivatable
		// and currently active
		if (source instanceof IActivatable
				&& ((IActivatable) source).isActive()) {
			activateAdapters();
		}

		if (adapters.size() == 0) {
			adapters = null;
		}

		pcs.firePropertyChange(IAdaptable.ADAPTERS_PROPERTY, oldAdapters,
				new HashMap<AdapterKey<?>, Object>(adapters));
		return (T) adapter;
	}

	public void dispose() {
		for(AdapterKey<?> key : adapters.keySet()){
			Object adapter = adapters.get(key);
			// dispose adapter if its disposable
			if(adapter instanceof IDisposable){
				((IDisposable) adapter).dispose();
			}
		}
		adapters.clear();
	}

}
