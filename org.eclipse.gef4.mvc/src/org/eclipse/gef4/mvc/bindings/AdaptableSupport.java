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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.mvc.IActivatable;

/**
 * Support class to manage adapters for an {@link IAdaptable}. If the given
 * sourceAdaptable is an {@link IActivatable}, all {@link IActivatable} adapters
 * will activated/deactivated dependent on the active state of the
 * sourceAdaptable, using the property change mechanism.
 * 
 * @author anyssen
 * 
 * @param <A>
 *            The type of sourceAdaptable supported by this class. If passed-in
 *            adapters implement the {@link IAdaptable.Bound} interface, the
 *            generic type parameter of {@link IAdaptable.Bound} has to match
 *            this one.
 */
public class AdaptableSupport<A extends IAdaptable> implements
		PropertyChangeListener {

	private A sourceAdaptable;
	private Map<AdapterKey<?>, Object> adapters;

	public AdaptableSupport(A sourceAdaptable) {
		this.sourceAdaptable = sourceAdaptable;
		if (sourceAdaptable instanceof IActivatable) {
			((IActivatable) sourceAdaptable).addPropertyChangeListener(this);
		}
	}

	public void dispose() {
		if (sourceAdaptable instanceof IActivatable) {
			((IActivatable) sourceAdaptable).removePropertyChangeListener(this);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getAdapter(AdapterKey<T> key) {
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
		adapters.put(key, adapter);
		if (adapter instanceof IAdaptable.Bound) {
			((IAdaptable.Bound<A>) adapter).setAdaptable(sourceAdaptable);
		}
		if (adapter instanceof IActivatable) {
			if (sourceAdaptable instanceof IActivatable
					&& ((IActivatable) sourceAdaptable).isActive()) {
				((IActivatable) adapter).activate();
			}
		}
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
			if (adapters == null) {
				adapters = new HashMap<AdapterKey<?>, Object>();
			}
			if (!key.getKey().isAssignableFrom(
					adaptersWithKeys.get(key).getClass())) {
				throw new IllegalArgumentException(
						key
								+ " is not a valid key for "
								+ adaptersWithKeys.get(key)
								+ ", as its neither a super interface nor a super class of its type.");
			}
			if (overwrite || !adapters.containsKey(key)) {
				setAdapter((AdapterKey) key, (Object) adaptersWithKeys.get(key));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T unsetAdapter(AdapterKey<T> key) {
		if (adapters == null)
			return null;
		Object adapter = adapters.remove(key);
		if (adapter != null) {
			if (adapter instanceof IActivatable) {
				((IActivatable) adapter).deactivate();
			}
			if (adapter instanceof IAdaptable.Bound) {
				((IAdaptable.Bound<A>) adapter).setAdaptable(null);
			}
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
	public <T> T getAdapter(Class<T> classKey) {
		Map<AdapterKey<? extends Object>, Object> adaptersForClassKey = getAdapters(classKey);
		// if we have only one adapter for the given class key, return this one
		if (adaptersForClassKey.size() == 1) {
			return (T) adaptersForClassKey.get(0);
		}
		// if we have more than one, retrieve the one with the default role
		return getAdapter(AdapterKey.get(classKey, AdapterKey.DEFAULT_ROLE));
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (IActivatable.ACTIVE_PROPERTY.equals(evt.getPropertyName())) {
			for (Object b : getAdapters().values()) {
				if (b instanceof IActivatable) {
					if (((Boolean) evt.getNewValue()).booleanValue()) {
						((IActivatable) b).activate();
					} else {
						((IActivatable) b).deactivate();
					}
				}
			}
		}
	}

}
