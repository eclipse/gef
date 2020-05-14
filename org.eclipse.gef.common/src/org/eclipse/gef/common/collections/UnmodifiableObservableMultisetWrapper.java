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
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Iterators;
import com.google.common.collect.Multiset;

import javafx.beans.InvalidationListener;

/**
 * An unmodifiable {@link ObservableSetMultimap}, wrapping an
 * {@link ObservableMultiset}.
 *
 * @author anyssen
 *
 * @param <E>
 *            The element type of the {@link ObservableMultiset}.
 */
class UnmodifiableObservableMultisetWrapper<E>
		implements ObservableMultiset<E> {

	private ObservableMultiset<E> observableMultiset;

	/**
	 * Creates a new {@link UnmodifiableObservableSetMultimapWrapper} for the
	 * given {@link ObservableMultiset}.
	 *
	 * @param observableMultiset
	 *            The {@link ObservableMultiset} to wrap.
	 */
	public UnmodifiableObservableMultisetWrapper(
			ObservableMultiset<E> observableMultiset) {
		this.observableMultiset = observableMultiset;
	}

	@Override
	public boolean add(E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int add(E element, int occurrences) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addListener(InvalidationListener listener) {
		observableMultiset.addListener(listener);
	}

	@Override
	public void addListener(MultisetChangeListener<? super E> listener) {
		observableMultiset.addListener(listener);
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object element) {
		return observableMultiset.contains(element);
	}

	@Override
	public boolean containsAll(Collection<?> elements) {
		return observableMultiset.containsAll(elements);
	}

	@Override
	public int count(Object element) {
		return observableMultiset.count(element);
	}

	@Override
	public Set<E> elementSet() {
		return Collections.unmodifiableSet(observableMultiset.elementSet());
	}

	@Override
	public Set<Multiset.Entry<E>> entrySet() {
		return Collections.unmodifiableSet(observableMultiset.entrySet());
	}

	@Override
	public boolean equals(Object obj) {
		return observableMultiset.equals(obj);
	}

	@Override
	public int hashCode() {
		return observableMultiset.hashCode();
	};

	@Override
	public boolean isEmpty() {
		return observableMultiset.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return Iterators.unmodifiableIterator(observableMultiset.iterator());
	}

	@Override
	public boolean remove(Object element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int remove(Object element, int occurrences) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		observableMultiset.removeListener(listener);
	}

	@Override
	public void removeListener(MultisetChangeListener<? super E> listener) {
		observableMultiset.removeListener(listener);
	}

	@Override
	public boolean replaceAll(Multiset<? extends E> multiset) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int setCount(E element, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean setCount(E element, int oldCount, int newCount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return observableMultiset.size();
	}

	@Override
	public Object[] toArray() {
		return observableMultiset.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return observableMultiset.toArray(a);
	}

}
