/******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.common.beans.binding;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.gef4.common.beans.value.ObservableSetMultimapValue;
import org.eclipse.gef4.common.collections.ObservableSetMultimap;
import org.eclipse.gef4.common.collections.ObservableSetMultimapWrapper;
import org.eclipse.gef4.common.collections.UnmodifiableObservableSetMultimapWrapper;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ListExpression;
import javafx.beans.binding.MapExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.SetExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ObservableValue;

/**
 * A {@code SetMultimapExpression} is a {@link ObservableSetMultimapValue} plus
 * additional convenience methods to generate bindings.
 * <p>
 * This class provides identical functionality for {@link SetMultimap} as
 * {@link MapExpression} for {@link Map}, {@link SetExpression} for {@link Set},
 * or {@link ListExpression} for {@link List}.
 * 
 * @author anyssen
 *
 * @param <K>
 *            The key type of the {@link ObservableSetMultimap}.
 * @param <V>
 *            The value type of the {@link ObservableSetMultimap}.
 */
public abstract class SetMultimapExpression<K, V>
		implements ObservableSetMultimapValue<K, V> {

	private static final class SetMultimapBindingImpl<K, V>
			extends SetMultimapBinding<K, V> {

		private ObservableSetMultimapValue<K, V> setMultimapValue = null;

		public SetMultimapBindingImpl(
				ObservableSetMultimapValue<K, V> setMultimapValue) {
			this.setMultimapValue = setMultimapValue;
			bind(setMultimapValue);
		}

		@Override
		protected ObservableSetMultimap<K, V> computeValue() {
			return setMultimapValue.get();
		}
	}

	private final ObservableSetMultimap<K, V> EMPTY_SETMULTIMAP = new UnmodifiableObservableSetMultimapWrapper<>(
			new ObservableSetMultimapWrapper<>(HashMultimap.<K, V> create()));

	@Override
	public ObservableSetMultimap<K, V> getValue() {
		return get();
	}

	@Override
	public Set<V> get(K key) {
		final SetMultimap<K, V> setMultimap = get();
		return (setMultimap == null) ? EMPTY_SETMULTIMAP.get(key)
				: setMultimap.get(key);
	}

	@Override
	public Set<V> removeAll(Object key) {
		final SetMultimap<K, V> setMultimap = get();
		return (setMultimap == null) ? EMPTY_SETMULTIMAP.removeAll(key)
				: setMultimap.removeAll(key);
	}

	@Override
	public Set<V> replaceValues(K key, Iterable<? extends V> values) {
		final SetMultimap<K, V> setMultimap = get();
		return (setMultimap == null)
				? EMPTY_SETMULTIMAP.replaceValues(key, values)
				: setMultimap.replaceValues(key, values);
	}

	@Override
	public boolean replaceAll(
			SetMultimap<? extends K, ? extends V> setMultimap) {
		final ObservableSetMultimap<K, V> delegate = get();
		return (delegate == null) ? EMPTY_SETMULTIMAP.replaceAll(setMultimap)
				: delegate.replaceAll(setMultimap);
	}

	@Override
	public Set<Entry<K, V>> entries() {
		final SetMultimap<K, V> setMultimap = get();
		return (setMultimap == null) ? EMPTY_SETMULTIMAP.entries()
				: setMultimap.entries();
	}

	@Override
	public Map<K, Collection<V>> asMap() {
		final SetMultimap<K, V> setMultimap = get();
		return (setMultimap == null) ? EMPTY_SETMULTIMAP.asMap()
				: setMultimap.asMap();
	}

	@Override
	public int size() {
		final SetMultimap<K, V> setMultimap = get();
		return (setMultimap == null) ? EMPTY_SETMULTIMAP.size()
				: setMultimap.size();
	}

	@Override
	public boolean isEmpty() {
		final SetMultimap<K, V> setMultimap = get();
		return (setMultimap == null) ? EMPTY_SETMULTIMAP.isEmpty()
				: setMultimap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		final SetMultimap<K, V> setMultimap = get();
		return (setMultimap == null) ? EMPTY_SETMULTIMAP.containsKey(key)
				: setMultimap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		final SetMultimap<K, V> setMultimap = get();
		return (setMultimap == null) ? EMPTY_SETMULTIMAP.containsValue(value)
				: setMultimap.containsValue(value);
	}

	@Override
	public boolean containsEntry(Object key, Object value) {
		final SetMultimap<K, V> setMultimap = get();
		return (setMultimap == null)
				? EMPTY_SETMULTIMAP.containsEntry(key, value)
				: setMultimap.containsEntry(key, value);
	}

	@Override
	public boolean put(K key, V value) {
		final SetMultimap<K, V> setMultimap = get();
		return (setMultimap == null) ? EMPTY_SETMULTIMAP.put(key, value)
				: setMultimap.put(key, value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		final SetMultimap<K, V> setMultimap = get();
		return (setMultimap == null) ? EMPTY_SETMULTIMAP.remove(key, value)
				: setMultimap.remove(key, value);
	}

	@Override
	public boolean putAll(K key, Iterable<? extends V> values) {
		final SetMultimap<K, V> setMultimap = get();
		return (setMultimap == null) ? EMPTY_SETMULTIMAP.putAll(key, values)
				: setMultimap.putAll(key, values);
	}

	@Override
	public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
		final SetMultimap<K, V> setMultimap = get();
		return (setMultimap == null) ? EMPTY_SETMULTIMAP.putAll(multimap)
				: setMultimap.putAll(multimap);
	}

	@Override
	public void clear() {
		final SetMultimap<K, V> setMultimap = get();
		if (setMultimap == null) {
			EMPTY_SETMULTIMAP.clear();
		} else {
			setMultimap.clear();
		}
	}

	@Override
	public Set<K> keySet() {
		final SetMultimap<K, V> setMultimap = get();
		return (setMultimap == null) ? EMPTY_SETMULTIMAP.keySet()
				: setMultimap.keySet();
	}

	@Override
	public Multiset<K> keys() {
		final SetMultimap<K, V> setMultimap = get();
		return (setMultimap == null) ? EMPTY_SETMULTIMAP.keys()
				: setMultimap.keys();
	}

	@Override
	public Collection<V> values() {
		final SetMultimap<K, V> setMultimap = get();
		return (setMultimap == null) ? EMPTY_SETMULTIMAP.values()
				: setMultimap.values();
	}

	/**
	 * An integer property that represents the size of the {@link SetMultimap}.
	 * 
	 * @return A read-only property.
	 */
	public abstract ReadOnlyIntegerProperty sizeProperty();

	/**
	 * A boolean property that reflects whether the {@link SetMultimap} is
	 * empty.
	 * 
	 * @return A read-only property.
	 * 
	 */
	public abstract ReadOnlyBooleanProperty emptyProperty();

	/**
	 * Creates a new {@link BooleanBinding} that indicates whether this
	 * {@link ObservableSetMultimap} is equal to the passed in
	 * {@link ObservableSetMultimap}.
	 *
	 * @param other
	 *            The {@link ObservableSetMultimap} to compare this
	 *            {@link ObservableSetMultimap} to.
	 * @return A new {@code BooleanBinding}.
	 */
	public BooleanBinding isEqualTo(final ObservableSetMultimap<?, ?> other) {
		return Bindings.equal(this, other);
	}

	/**
	 * Creates a new {@link BooleanBinding} that indicates whether this
	 * {@link ObservableSetMultimap} is not equal to the passed in
	 * {@link ObservableSetMultimap}.
	 *
	 * @param other
	 *            The {@link ObservableSetMultimap} to compare this
	 *            {@link ObservableSetMultimap} to.
	 * @return A new {@code BooleanBinding}.
	 */
	public BooleanBinding isNotEqualTo(
			final ObservableSetMultimap<?, ?> other) {
		return Bindings.notEqual(this, other);
	}

	/**
	 * Creates a new {@link BooleanBinding} that indicates if the wrapped
	 * {@link ObservableSetMultimap} is <code>null</code>.
	 *
	 * @return A new {@code BooleanBinding}.
	 */
	public BooleanBinding isNull() {
		return Bindings.isNull(this);
	}

	/**
	 * Creates a new {@link BooleanBinding} that indicates if the wrapped
	 * {@link ObservableSetMultimap} is not <code>null</code>.
	 *
	 * @return A new {@code BooleanBinding}.
	 */
	public BooleanBinding isNotNull() {
		return Bindings.isNotNull(this);
	}

	/**
	 * Creates a {@link StringBinding} that holds the value of the
	 * {@link SetMultimapExpression} turned into a {@link String}. If the value
	 * of this {@code SetMultimapExpression} changes, the value of the
	 * {@link StringBinding} will be updated automatically.
	 *
	 * @return A new {@code StringBinding}.
	 */
	public StringBinding asString() {
		return (StringBinding) Bindings.convert(this);
	}

	/**
	 * Creates a new {@link ObjectBinding} that contains the values that are
	 * mapped to the specified key.
	 *
	 * @param key
	 *            the key of the mapping
	 * @return A new {@code ObjectBinding}.
	 */
	public ObjectBinding<Set<V>> valuesAt(final K key) {
		return Bindings2.valuesAt(this, key);
	}

	/**
	 * Creates a new {@link ObjectBinding} that contains the values that are
	 * mapped to the specified key.
	 *
	 * @param key
	 *            The key of the mapping.
	 * @return The {@code ObjectBinding}.
	 */
	public ObjectBinding<Set<V>> valuesAt(final ObservableValue<K> key) {
		return Bindings2.valuesAt(this, key);
	}

	/**
	 * Returns a {@code SetMultimapExpression} that wraps an
	 * {@link ObservableSetMultimapValue}. If the
	 * {@code ObservableSetMultimapValue} is already a
	 * {@code SetMultimapExpression}, it will be returned. Otherwise a new
	 * concrete {@link SetMultimapBinding} is created that is bound to the
	 * {@code ObservableSetMultimapValue}.
	 * 
	 * @param <K>
	 *            The key type of the {@link SetMultimapExpression}.
	 * @param <V>
	 *            The value type of the {@link SetMultimapExpression}.
	 *
	 * @param setMultimapValue
	 *            The {@code ObservableSetMultimapValue} for which to return a
	 *            {@link SetMultimapExpression}.
	 * @return The passed in {@link ObservableSetMultimapValue} if its already a
	 *         {@link SetMultimapExpression}, or a newly created
	 *         {@link SetMultimapBinding} for it.
	 */
	public static <K, V> SetMultimapExpression<K, V> setMultimapExpression(
			final ObservableSetMultimapValue<K, V> setMultimapValue) {
		if (setMultimapValue == null) {
			throw new IllegalArgumentException(
					"setMultimapValue may not be null.");
		}
		if (setMultimapValue instanceof SetMultimapExpression) {
			return (SetMultimapExpression<K, V>) setMultimapValue;
		}
		return new SetMultimapBindingImpl<>(setMultimapValue);
	}

}
