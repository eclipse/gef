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

import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.common.activate.IActivatable;

/**
 * Support class to manage adapters for an {@link IAdaptable}. If the
 * {@link IAdaptable} is also {@link IActivatable}, it will ensure adapters are
 * activated/deactivated upon registration dependent on the active state of the
 * adaptable.
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

	private A adaptable;
	private PropertyChangeSupport pcs;
	private Map<AdapterKey<?>, Object> adapters;

	public AdaptableSupport(A adaptable, PropertyChangeSupport pcs) {
		this.adaptable = adaptable;
		this.pcs = pcs;
	}

	@SuppressWarnings("unchecked")
	public <T> T getAdapter(AdapterKey<? super T> key) {
		if (adapters == null) {
			return null;
		}
		return (T) adapters.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> void setAdapter(AdapterKey<T> key, T adapter) {
		if (adapters == null) {
			adapters = new HashMap<AdapterKey<?>, Object>();
		}

		// deactivate already registered adapters, if adaptable is IActivatable
		// and currently active
		if (adaptable instanceof IActivatable
				&& ((IActivatable) adaptable).isActive()) {
			deactivateAdapters();
		}

		adapters.put(key, adapter);
		if (adapter instanceof IAdaptable.Bound) {
			((IAdaptable.Bound<A>) adapter).setAdaptable(adaptable);
		}

		// activate all adapters, if adaptable is IActivatable and currently
		// active
		if (adaptable instanceof IActivatable
				&& ((IActivatable) adaptable).isActive()) {
			activateAdapters();
		}

		// TODO: fire property change...
	}

	/**
	 * Registers the given adapters under the provided keys. Note, that only
	 * those adapters are registered, for which no key is already existent in
	 * case <i>overwrite</i> is set to <code>false</code>.
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
	public <T> T unsetAdapter(AdapterKey<T> key) {
		if (adapters == null || !adapters.containsKey(key)) {
			throw new IllegalArgumentException("Given key is not registered.");
		}

		// deactivate already registered adapters, if adaptable is IActivatable
		// and currently active
		if (adaptable instanceof IActivatable
				&& ((IActivatable) adaptable).isActive()) {
			deactivateAdapters();
		}

		Object adapter = adapters.remove(key);
		if (adapter != null) {
			if (adapter instanceof IAdaptable.Bound) {
				((IAdaptable.Bound<A>) adapter).setAdaptable(null);
			}
		}

		// re-activate remaining adapters, if adaptable is IActivatable
		// and currently active
		if (adaptable instanceof IActivatable
				&& ((IActivatable) adaptable).isActive()) {
			activateAdapters();
		}

		if (adapters.size() == 0) {
			adapters = null;
		}
		return (T) adapter;
	}

	public Map<AdapterKey<?>, Object> getAdapters() {
		if (adapters == null) {
			return Collections.emptyMap();
		}
		return adapters;
	}

	@SuppressWarnings("unchecked")
	public <T> T getAdapter(Class<? super T> classKey) {
		Map<AdapterKey<? extends Object>, Object> adaptersForClassKey = getAdapters(classKey);
		// if we have only one adapter for the given class key, return this one
		if (adaptersForClassKey.size() == 1) {
			return (T) adaptersForClassKey.values().iterator().next();
		}
		// if we have more than one, retrieve the one with the default role
		return this.<T>getAdapter(AdapterKey.get(classKey, AdapterKey.DEFAULT_ROLE));
	}

	@SuppressWarnings("unchecked")
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(Class<?> classKey) {
		if (adapters == null) {
			return Collections.emptyMap();
		}
		Map<AdapterKey<? extends T>, T> typeSafeAdapters = new HashMap<AdapterKey<? extends T>, T>();
		if (adapters != null) {
			for (AdapterKey<?> key : adapters.keySet()) {
				if (classKey.isAssignableFrom(key.getKey())) {
					typeSafeAdapters.put((AdapterKey<? extends T>) key,
							(T) adapters.get(key));
				}
			}
		}
		return typeSafeAdapters;
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

}
