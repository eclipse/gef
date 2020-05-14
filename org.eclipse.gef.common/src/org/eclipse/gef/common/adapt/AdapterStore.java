/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.common.adapt;

import java.util.Map;

import org.eclipse.gef.common.adapt.inject.InjectAdapters;

import com.google.common.reflect.TypeToken;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.collections.ObservableMap;

/**
 * An {@link AdapterStore} is a basic {@link IAdaptable} implementation that can
 * be used standalone.
 *
 * @author anyssen
 */
public class AdapterStore implements IAdaptable {

	private AdaptableSupport<AdapterStore> ads = new AdaptableSupport<>(this);

	/**
	 * Creates a new {@link AdapterStore} with no initial adapters.
	 */
	public AdapterStore() {
	}

	/**
	 * Creates a new AdapterStore with the single given initial adapter, using
	 * the 'default' role.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param adapter
	 *            The adapter to be registered.
	 *
	 */
	public <T> AdapterStore(T adapter) {
		setAdapter(adapter, AdapterKey.DEFAULT_ROLE);
	}

	/**
	 * Creates a new AdapterStore with the single given initial adapter, using
	 * the 'default' role.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param adapterType
	 *            The runtime type of the adapter to be registered.
	 * @param adapter
	 *            The adapter to be registered.
	 *
	 */
	public <T> AdapterStore(TypeToken<T> adapterType, T adapter) {
		setAdapter(adapterType, adapter, AdapterKey.DEFAULT_ROLE);
	}

	/**
	 * Creates a new AdapterStore with the single given initial adapter.
	 *
	 * @param <T>
	 *            The adapter type.
	 * @param adapterType
	 *            The runtime type of the adapter to be registered.
	 * @param adapter
	 *            The adapter to be registered.
	 * @param role
	 *            The role under which the adapter is to be registered.
	 */
	public <T> AdapterStore(TypeToken<T> adapterType, T adapter, String role) {
		setAdapter(adapterType, adapter, role);
	}

	@Override
	public ReadOnlyMapProperty<AdapterKey<?>, Object> adaptersProperty() {
		return ads.adaptersProperty();
	}

	/**
	 * Removes all registered adapters from this {@link AdapterStore}.
	 */
	public void clear() {
		for (Object adapter : ads.getAdapters().values()) {
			ads.unsetAdapter(adapter);
		}
	}

	@Override
	public <T> T getAdapter(AdapterKey<T> key) {
		return ads.getAdapter(key);
	}

	@Override
	public <T> T getAdapter(Class<T> key) {
		return ads.getAdapter(key);
	}

	@Override
	public <T> T getAdapter(TypeToken<T> key) {
		return ads.getAdapter(key);
	}

	@Override
	public <T> AdapterKey<T> getAdapterKey(T adapter) {
		return ads.getAdapterKey(adapter);
	}

	@Override
	public ObservableMap<AdapterKey<?>, Object> getAdapters() {
		return ads.getAdapters();
	}

	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			Class<? super T> key) {
		return ads.getAdapters(key);
	}

	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			TypeToken<? super T> key) {
		return ads.getAdapters(key);
	}

	@Override
	public <T> void setAdapter(T adapter) {
		ads.setAdapter(adapter);
	}

	@Override
	public <T> void setAdapter(T adapter, String role) {
		ads.setAdapter(adapter, role);
	}

	@Override
	public <T> void setAdapter(TypeToken<T> adapterType, T adapter) {
		ads.setAdapter(adapterType, adapter);
	}

	@InjectAdapters
	@Override
	public <T> void setAdapter(TypeToken<T> adapterType, T adapter,
			String role) {
		ads.setAdapter(adapterType, adapter, role);
	}

	@Override
	public <T> void unsetAdapter(T adapter) {
		ads.unsetAdapter(adapter);
	}

}