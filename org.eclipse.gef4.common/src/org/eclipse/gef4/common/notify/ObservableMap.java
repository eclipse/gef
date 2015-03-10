/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.common.notify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ForwardingMap;

/**
 * An {@link ObservableMap} maintains a list of {@link IMapObserver observers}
 * which are notified whenever the map changes.
 * 
 * @author wienand
 *
 * @param <K>
 *            The type of the map's keys.
 * @param <V>
 *            The type of the map's values.
 */
public class ObservableMap<K, V> extends ForwardingMap<K, V> {

	private List<IMapObserver<K, V>> observers = new ArrayList<IMapObserver<K, V>>();
	private Map<K, V> backingMap = new HashMap<K, V>();

	/**
	 * Adds the given {@link IMapObserver} to the list of observers, which are
	 * notified on changes.
	 * 
	 * @param mapObserver
	 *            The {@link IMapObserver} to add.
	 */
	public void addMapObserver(IMapObserver<K, V> mapObserver) {
		observers.add(mapObserver);
	}

	@Override
	public void clear() {
		if (!isEmpty()) {
			Map<K, V> old = getBackingMapCopy();
			super.clear();
			notifyChanged(old);
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
	protected Map<K, V> getBackingMapCopy() {
		return new HashMap<K, V>(backingMap);
	}

	/**
	 * Notifies all observers that this map changed.
	 * 
	 * @param old
	 *            A copy of the map before the change.
	 */
	protected void notifyChanged(Map<K, V> old) {
		for (IMapObserver<K, V> observer : observers) {
			observer.afterChange(this, old);
		}
	}

	@Override
	public V put(K key, V value) {
		V previousValue = get(key);
		if (value != previousValue) {
			Map<K, V> old = getBackingMapCopy();
			super.put(key, value);
			notifyChanged(old);
			return previousValue;
		}
		return previousValue;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		Map<K, V> old = getBackingMapCopy();
		super.putAll(map);
		if (!equals(old)) {
			notifyChanged(old);
		}
	}

	@Override
	public V remove(Object key) {
		if (containsKey(key)) {
			Map<K, V> old = getBackingMapCopy();
			V previousValue = super.remove(key);
			notifyChanged(old);
			return previousValue;
		}
		return null;
	}

	/**
	 * Removes the given {@link IMapObserver} from the list of observers, which
	 * are notified on changes.
	 * 
	 * @param mapObserver
	 *            The {@link IMapObserver} to remove.
	 */
	public void removeMapObserver(IMapObserver<K, V> mapObserver) {
		observers.remove(mapObserver);
	}

}
