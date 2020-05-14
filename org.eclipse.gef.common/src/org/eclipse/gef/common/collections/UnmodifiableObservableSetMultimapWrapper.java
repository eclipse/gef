/******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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
package org.eclipse.gef.common.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.SetMultimap;

import javafx.beans.InvalidationListener;

/**
 * An unmodifiable {@link ObservableSetMultimap}, wrapping an
 * {@link ObservableSetMultimap}.
 *
 * @author anyssen
 *
 * @param <K>
 *            The key type of the {@link ObservableSetMultimap}.
 * @param <V>
 *            The value type of the {@link ObservableSetMultimap}.
 */
class UnmodifiableObservableSetMultimapWrapper<K, V>
		implements ObservableSetMultimap<K, V> {

	private ObservableSetMultimap<K, V> observableSetMultimap;

	/**
	 * Creates a new {@link UnmodifiableObservableSetMultimapWrapper} for the
	 * given {@link ObservableSetMultimap}.
	 *
	 * @param observableSetMultimap
	 *            The {@link ObservableSetMultimap} to wrap.
	 */
	public UnmodifiableObservableSetMultimapWrapper(
			ObservableSetMultimap<K, V> observableSetMultimap) {
		this.observableSetMultimap = observableSetMultimap;
	}

	@Override
	public void addListener(InvalidationListener listener) {
		observableSetMultimap.addListener(listener);
	}

	@Override
	public void addListener(
			SetMultimapChangeListener<? super K, ? super V> listener) {
		observableSetMultimap.addListener(listener);

	}

	@Override
	public Map<K, Collection<V>> asMap() {
		return Collections.unmodifiableMap(observableSetMultimap.asMap());
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsEntry(Object key, Object value) {
		return observableSetMultimap.containsEntry(key, value);
	}

	@Override
	public boolean containsKey(Object key) {
		return observableSetMultimap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return observableSetMultimap.containsValue(value);
	}

	@Override
	public Set<Entry<K, V>> entries() {
		return Collections.unmodifiableSet(observableSetMultimap.entries());
	}

	@Override
	public boolean equals(Object obj) {
		return observableSetMultimap.equals(obj);
	}

	@Override
	public Set<V> get(K key) {
		return Collections.unmodifiableSet(observableSetMultimap.get(key));
	};

	@Override
	public int hashCode() {
		return observableSetMultimap.hashCode();
	}

	@Override
	public boolean isEmpty() {
		return observableSetMultimap.isEmpty();
	}

	@Override
	public Multiset<K> keys() {
		return Multisets.unmodifiableMultiset(observableSetMultimap.keys());
	}

	@Override
	public Set<K> keySet() {
		return Collections.unmodifiableSet(observableSetMultimap.keySet());
	}

	@Override
	public boolean put(K key, V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean putAll(K key, Iterable<? extends V> values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<V> removeAll(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		observableSetMultimap.removeListener(listener);

	}

	@Override
	public void removeListener(
			SetMultimapChangeListener<? super K, ? super V> listener) {
		observableSetMultimap.removeListener(listener);
	}

	@Override
	public boolean replaceAll(
			SetMultimap<? extends K, ? extends V> setMultimap) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<V> replaceValues(K key, Iterable<? extends V> values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return observableSetMultimap.size();
	}

	@Override
	public Collection<V> values() {
		return Collections
				.unmodifiableCollection(observableSetMultimap.values());
	}

}
