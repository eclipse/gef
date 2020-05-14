/******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
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
package org.eclipse.gef.common.beans.binding;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.common.beans.value.ObservableMultisetValue;
import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.common.collections.ObservableMultiset;

import com.google.common.collect.Multiset;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ListExpression;
import javafx.beans.binding.MapExpression;
import javafx.beans.binding.SetExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;

/**
 * A {@code SetMultimapExpression} is a {@link ObservableMultisetValue} plus
 * additional convenience methods to generate bindings.
 * <p>
 * This class provides identical functionality for {@link Multiset} as
 * {@link MapExpression} for {@link Map}, {@link SetExpression} for {@link Set},
 * or {@link ListExpression} for {@link List}.
 *
 * @author anyssen
 *
 * @param <E>
 *            The element type of the {@link ObservableMultiset}.
 */
public abstract class MultisetExpression<E>
		implements ObservableMultisetValue<E> {

	private static final class MultisetBindingImpl<E>
			extends MultisetBinding<E> {

		private ObservableMultisetValue<E> multisetValue = null;

		public MultisetBindingImpl(ObservableMultisetValue<E> multisetValue) {
			this.multisetValue = multisetValue;
			bind(multisetValue);
		}

		@Override
		protected ObservableMultiset<E> computeValue() {
			return multisetValue.get();
		}
	}

	/**
	 * Returns a {@code MultisetExpression} that wraps an
	 * {@link ObservableMultisetValue}. If the {@code ObservableMultisetValue}
	 * is already a {@code MultisetExpression}, it will be returned. Otherwise a
	 * new concrete {@link MultisetBinding} is created that is bound to the
	 * {@code ObservableMultisetValue}.
	 *
	 * @param <E>
	 *            The element type of the {@link MultisetExpression}.
	 *
	 * @param multisetValue
	 *            The {@code ObservableMultisetValue} for which to return a
	 *            {@link MultisetExpression}.
	 * @return The passed in {@link ObservableMultisetValue} if its already a
	 *         {@link MultisetExpression}, or a newly created
	 *         {@link MultisetBinding} for it.
	 */
	public static <E> MultisetExpression<E> multisetExpression(
			final ObservableMultisetValue<E> multisetValue) {
		if (multisetValue == null) {
			throw new IllegalArgumentException(
					"multisetValue may not be null.");
		}
		if (multisetValue instanceof MultisetExpression) {
			return (MultisetExpression<E>) multisetValue;
		}
		return new MultisetBindingImpl<>(multisetValue);
	}

	private final ObservableMultiset<E> EMPTY_MULTISET = CollectionUtils
			.emptyMultiset();

	@Override
	public boolean add(E element) {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.add(element)
				: multiset.add(element);
	}

	@Override
	public int add(E element, int occurrences) {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.add(element, occurrences)
				: multiset.add(element, occurrences);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.addAll(c)
				: multiset.addAll(c);
	}

	/**
	 * Creates a {@link StringBinding} that holds the value of the
	 * {@link MultisetExpression} turned into a {@link String}. If the value of
	 * this {@code SetMultimapExpression} changes, the value of the
	 * {@link StringBinding} will be updated automatically.
	 *
	 * @return A new {@code StringBinding}.
	 */
	public StringBinding asString() {
		return (StringBinding) Bindings.convert(this);
	}

	@Override
	public void clear() {
		final Multiset<E> multiset = get();
		if (multiset == null) {
			EMPTY_MULTISET.clear();
		} else {
			multiset.clear();
		}
	}

	@Override
	public boolean contains(Object element) {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.contains(element)
				: multiset.contains(element);
	}

	@Override
	public boolean containsAll(Collection<?> elements) {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.containsAll(elements)
				: multiset.containsAll(elements);
	}

	@Override
	public int count(Object element) {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.count(element)
				: multiset.count(element);
	}

	@Override
	public Set<E> elementSet() {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.elementSet()
				: multiset.elementSet();
	}

	/**
	 * A boolean property that reflects whether the {@link Multiset} is empty.
	 *
	 * @return A read-only property.
	 *
	 */
	public abstract ReadOnlyBooleanProperty emptyProperty();

	@Override
	public Set<com.google.common.collect.Multiset.Entry<E>> entrySet() {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.entrySet()
				: multiset.entrySet();
	}

	@Override
	public ObservableMultiset<E> getValue() {
		return get();
	}

	@Override
	public boolean isEmpty() {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.isEmpty()
				: multiset.isEmpty();
	}

	/**
	 * Creates a new {@link BooleanBinding} that indicates whether this
	 * {@link ObservableMultiset} is equal to the passed in
	 * {@link ObservableMultiset}.
	 *
	 * @param other
	 *            The {@link ObservableMultiset} to compare this
	 *            {@link ObservableMultiset} to.
	 * @return A new {@code BooleanBinding}.
	 */
	public BooleanBinding isEqualTo(final ObservableMultiset<?> other) {
		return Bindings.equal(this, other);
	}

	/**
	 * Creates a new {@link BooleanBinding} that indicates whether this
	 * {@link ObservableMultiset} is not equal to the passed in
	 * {@link ObservableMultiset}.
	 *
	 * @param other
	 *            The {@link ObservableMultiset} to compare this
	 *            {@link ObservableMultiset} to.
	 * @return A new {@code BooleanBinding}.
	 */
	public BooleanBinding isNotEqualTo(final ObservableMultiset<?> other) {
		return Bindings.notEqual(this, other);
	}

	/**
	 * Creates a new {@link BooleanBinding} that indicates if the wrapped
	 * {@link ObservableMultiset} is not <code>null</code>.
	 *
	 * @return A new {@code BooleanBinding}.
	 */
	public BooleanBinding isNotNull() {
		return Bindings.isNotNull(this);
	}

	/**
	 * Creates a new {@link BooleanBinding} that indicates if the wrapped
	 * {@link ObservableMultiset} is <code>null</code>.
	 *
	 * @return A new {@code BooleanBinding}.
	 */
	public BooleanBinding isNull() {
		return Bindings.isNull(this);
	}

	@Override
	public Iterator<E> iterator() {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.iterator()
				: multiset.iterator();
	}

	@Override
	public boolean remove(Object element) {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.remove(element)
				: multiset.remove(element);
	}

	@Override
	public int remove(Object element, int occurrences) {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.remove(element, occurrences)
				: multiset.remove(element, occurrences);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.removeAll(c)
				: multiset.removeAll(c);
	}

	@Override
	public boolean replaceAll(Multiset<? extends E> multiset) {
		final ObservableMultiset<E> delegate = get();
		return (delegate == null) ? EMPTY_MULTISET.replaceAll(multiset)
				: delegate.replaceAll(multiset);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.retainAll(c)
				: multiset.retainAll(c);
	}

	@Override
	public int setCount(E element, int count) {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.setCount(element, count)
				: multiset.setCount(element, count);
	}

	@Override
	public boolean setCount(E element, int oldCount, int newCount) {
		final Multiset<E> multiset = get();
		return (multiset == null)
				? EMPTY_MULTISET.setCount(element, oldCount, newCount)
				: multiset.setCount(element, oldCount, newCount);
	}

	@Override
	public int size() {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.size() : multiset.size();
	}

	/**
	 * An integer property that represents the size of the {@link Multiset}.
	 *
	 * @return A read-only property.
	 */
	public abstract ReadOnlyIntegerProperty sizeProperty();

	@Override
	public Object[] toArray() {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.toArray()
				: multiset.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		final Multiset<E> multiset = get();
		return (multiset == null) ? EMPTY_MULTISET.toArray(a)
				: multiset.toArray(a);
	}

}
