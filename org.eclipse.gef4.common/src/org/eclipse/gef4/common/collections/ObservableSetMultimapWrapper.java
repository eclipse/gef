/******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.common.collections;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef4.common.collections.SetMultimapChangeListenerHelper.SimpleChange;

import com.google.common.collect.ForwardingSetMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import javafx.beans.InvalidationListener;

/**
 * An {@link ObservableSetMultimapWrapper} is an {@link ObservableSetMultimap}
 * that wraps an underlying {@link SetMultimap}.
 * 
 * @param <K>
 *            The key type of the {@link SetMultimap}.
 * @param <V>
 *            The value type of the {@link SetMultimap}.
 *
 * @author anyssen
 */
public class ObservableSetMultimapWrapper<K, V> extends
		ForwardingSetMultimap<K, V> implements ObservableSetMultimap<K, V> {

	private SetMultimap<K, V> backingSetMultiMap;
	private SetMultimapChangeListenerHelper<K, V> helper = new SetMultimapChangeListenerHelper<>(
			this);

	/**
	 * Creates a new {@link ObservableSetMultimap} wrapping the given
	 * {@link SetMultimap}.
	 * 
	 * @param setMultimap
	 *            The {@link SetMultimap} to wrap into the newly created
	 *            {@link ObservableSetMultimapWrapper}.
	 */
	public ObservableSetMultimapWrapper(SetMultimap<K, V> setMultimap) {
		this.backingSetMultiMap = setMultimap;
	}

	@Override
	public void addListener(InvalidationListener listener) {
		helper.addListener(listener);
	}

	@Override
	public void addListener(
			SetMultimapChangeListener<? super K, ? super V> listener) {
		helper.addListener(listener);
	}

	@Override
	public void clear() {
		SetMultimap<K, V> removedValues = HashMultimap.create();
		for (K key : keys()) {
			removedValues.putAll(key, new HashSet<>(get(key)));
		}
		super.clear();
		for (K key : removedValues.keySet()) {
			// this causes multiple change notifications, as an elementary
			// change is related to a single key only
			helper.fireValueChangedEvent(new SimpleChange<>(this, key,
					removedValues.get(key), Collections.<V> emptySet()));
		}
	}

	@Override
	protected SetMultimap<K, V> delegate() {
		return backingSetMultiMap;
	}

	@Override
	public boolean put(K key, V value) {
		if (super.put(key, value)) {
			helper.fireValueChangedEvent(new SimpleChange<>(this, key,
					Collections.<V> emptySet(), Collections.singleton(value)));
			return true;
		}
		return false;
	}

	@Override
	public boolean putAll(K key, Iterable<? extends V> values) {
		Set<V> oldValues = new HashSet<>(get(key));
		if (super.putAll(key, values)) {
			Set<V> removedValues = new HashSet<>(oldValues);
			removedValues.removeAll(get(key));
			Set<V> addedValues = new HashSet<>(get(key));
			addedValues.removeAll(oldValues);
			helper.fireValueChangedEvent(
					new SimpleChange<>(this, key, removedValues, addedValues));
			return true;
		}
		return false;
	}

	@Override
	public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
		SetMultimap<K, V> oldValues = HashMultimap.create();
		for (K key : multimap.keys()) {
			oldValues.putAll(key, new HashSet<>(get(key)));
		}
		if (super.putAll(multimap)) {
			for (K key : multimap.keySet()) {
				// this causes multiple change notifications, as an elementary
				// change is related to a single key only
				Set<V> removedValues = new HashSet<>(oldValues.get(key));
				removedValues.removeAll(get(key));
				Set<V> addedValues = new HashSet<>(get(key));
				addedValues.removeAll(oldValues.get(key));
				helper.fireValueChangedEvent(new SimpleChange<>(this, key,
						removedValues, addedValues));
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object key, Object value) {
		if (super.remove(key, value)) {
			// XXX: If the key or value are not of matching type, the super call
			// should not have an effect; as such, the cast should be safe here.
			helper.fireValueChangedEvent(new SimpleChange<>(this, (K) key,
					Collections.singleton((V) value),
					Collections.<V> emptySet()));
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<V> removeAll(Object key) {
		Set<V> oldValues = super.removeAll(key);
		if (!oldValues.isEmpty()) {
			// XXX: If values could be removed, the key should have the
			// appropriate type. As such the cast here should be safe.
			helper.fireValueChangedEvent(new SimpleChange<>(this, (K) key,
					oldValues, Collections.<V> emptySet()));
		}
		return oldValues;
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		helper.removeListener(listener);
	}

	@Override
	public void removeListener(
			SetMultimapChangeListener<? super K, ? super V> listener) {
		helper.removeListener(listener);
	}

	@Override
	public boolean replaceAll(
			SetMultimap<? extends K, ? extends V> setMultimap) {
		MapDifference<K, Set<? extends V>> difference = Maps.difference(
				Multimaps.asMap(delegate()), Multimaps.asMap(setMultimap));
		super.clear();
		super.putAll(setMultimap);
		// removed keys
		Map<K, Set<? extends V>> removedEntries = difference
				.entriesOnlyOnLeft();
		for (K key : removedEntries.keySet()) {
			helper.fireValueChangedEvent(new SimpleChange<>(this, key,
					new HashSet<>(removedEntries.get(key)),
					Collections.<V> emptySet()));
		}
		// added entries
		Map<K, Set<? extends V>> addedEntries = difference.entriesOnlyOnRight();
		for (K key : addedEntries.keySet()) {
			helper.fireValueChangedEvent(
					new SimpleChange<>(this, key, Collections.<V> emptySet(),
							new HashSet<>(addedEntries.get(key))));
		}
		// changed entries
		Map<K, ValueDifference<Set<? extends V>>> changedEntries = difference
				.entriesDiffering();
		for (K key : changedEntries.keySet()) {
			helper.fireValueChangedEvent(new SimpleChange<>(this, key,
					new HashSet<>(changedEntries.get(key).leftValue()),
					new HashSet<>(changedEntries.get(key).rightValue())));
		}
		return !difference.areEqual();
	}

	@Override
	public Set<V> replaceValues(K key, Iterable<? extends V> values) {
		Set<V> replacedValues = super.replaceValues(key, values);
		if (!replacedValues.isEmpty()) {
			helper.fireValueChangedEvent(new SimpleChange<>(this, key,
					replacedValues, Sets.newHashSet(values)));
		}
		return replacedValues;
	}
}
