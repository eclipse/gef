/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef4.common.adapt;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;

import com.google.common.reflect.TypeToken;

/**
 * An {@link AdapterStore} is a basic {@link IAdaptable} implementation that can
 * be used standalone.
 * 
 * @author anyssen
 */
public class AdapterStore implements IAdaptable {

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private AdaptableSupport<AdapterStore> as = new AdaptableSupport<AdapterStore>(
			this, pcs);

	/**
	 * Creates a new {@link AdapterStore} with no initial adapters.
	 */
	public AdapterStore() {
	}

	/**
	 * Creates a new AdapterStore with the provided initial adapters.
	 * 
	 * @param adapters
	 *            The adapters to initially add to this {@link AdapterStore}.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AdapterStore(Map<AdapterKey<?>, ?> adapters) {
		for (AdapterKey<?> key : adapters.keySet()) {
			setAdapter((AdapterKey) key, adapters.get(key));
		}
	}

	/**
	 * Creates a new AdapterStore with the single given initial adapter.
	 * 
	 * @param <T>
	 *            The adapter type.
	 * @param key
	 *            The key under which to register the adapter.
	 * @param adapter
	 *            The adapter to be registered.
	 */
	public <T> AdapterStore(AdapterKey<? super T> key, T adapter) {
		setAdapter(key, adapter);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public <T> T getAdapter(AdapterKey<? super T> key) {
		return as.getAdapter(key);
	}

	@Override
	public <T> T getAdapter(Class<? super T> key) {
		return as.getAdapter(key);
	}

	@Override
	public <T> T getAdapter(TypeToken<? super T> key) {
		return as.getAdapter(key);
	}

	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			Class<? super T> key) {
		return as.getAdapters(key);
	}

	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			TypeToken<? super T> key) {
		return as.getAdapters(key);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public <T> void setAdapter(AdapterKey<? super T> key, T adapter) {
		as.setAdapter(key, adapter);
	}

	@Override
	public <T> void setAdapter(Class<? super T> key, T adapter) {
		as.setAdapter(key, adapter);
	}

	@Override
	public <T> void setAdapter(TypeToken<? super T> key, T adapter) {
		as.setAdapter(key, adapter);
	}

	@Override
	public <T> T unsetAdapter(AdapterKey<? super T> key) {
		return as.unsetAdapter(key);
	}
}