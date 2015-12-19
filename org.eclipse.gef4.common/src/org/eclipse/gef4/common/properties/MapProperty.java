/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Alexander Nyßen, Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.common.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ForwardingMap;

/**
 * A {@link MapProperty} is a map that supports notification of
 * {@link PropertyChangeListener}s. Will fire 'simple'
 * {@link PropertyChangeEvent}s in case of calls to {@link #clear()} or
 * {@link #putAll(Map)}, using a {@link Map} for new and old values. Will fire
 * {@link KeyedPropertyChangeEvent}s in case of calls to
 * {@link #put(Object, Object)} and {@link #remove(Object)}.
 * 
 * @param <K>
 *            The type of the map's keys.
 * @param <V>
 *            The type of the map's values.
 * 
 * @author anyssen
 * @author mwienand
 */
public class MapProperty<K, V> extends ForwardingMap<K, V>
		implements IPropertyChangeNotifier {

	private PropertyChangeNotifierSupport pcs = null;
	private Map<K, V> backingMap = new HashMap<>();
	private String propertyName;

	/**
	 * Creates a new {@link MapProperty} with the given name.
	 * 
	 * @param sourceBean
	 *            The source bean to use in property change notifications.
	 * 
	 * @param propertyName
	 *            The name of the {@link MapProperty}, which will be used when
	 *            notifying {@link PropertyChangeListener}s.
	 */
	public MapProperty(Object sourceBean, String propertyName) {
		this.pcs = new PropertyChangeNotifierSupport(sourceBean);
		this.propertyName = propertyName;
	}

	@Override
	public void clear() {
		if (!isEmpty()) {
			Map<K, V> oldValue = delegateCopy();
			super.clear();
			pcs.firePropertyChange(propertyName, oldValue, delegateCopy());
		}
	}

	@Override
	protected Map<K, V> delegate() {
		return backingMap;
	}

	/**
	 * Returns a copy of the backing map. This is used for reporting changes.
	 * 
	 * @return A copy of the backing map.
	 */
	protected Map<K, V> delegateCopy() {
		return new HashMap<>(backingMap);
	}

	@Override
	public V put(K key, V value) {
		V oldValue = super.put(key, value);
		pcs.fireKeyedPropertyChange(propertyName, key, oldValue, value);
		return oldValue;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		Map<K, V> oldValue = delegateCopy();
		super.putAll(map);
		pcs.firePropertyChange(propertyName, oldValue, delegateCopy());
	}

	@Override
	public V remove(Object key) {
		V oldValue = super.remove(key);
		pcs.fireKeyedPropertyChange(propertyName, key, oldValue, null);
		return oldValue;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
}
