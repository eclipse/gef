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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.gef.common.collections.ListListenerHelperEx.ElementarySubChange;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * A replacement for the (internal) observable list wrapper returned by
 * {@link FXCollections#observableList(java.util.List)} to fix the following
 * issues:
 * <ul>
 * <li>Change notifications are fired from sort() and sort(Comparator) even if
 * no change occurred (JI-9029606): fixed by properly guarding the change
 * notification calls within.</li>
 * <li>Invalidation listeners are notified from within clear() (on JavaSE-1.7
 * only), even if no change resulted.</li>
 * <li>Invalidation listeners are notified from within set(int, E),
 * setAll(Collection), and setAll(E...), even if the call has no effect
 * (JI-9029640, JI-9029642).</li>
 * <li>Change objects are not immutable
 * (https://bugs.openjdk.java.net/browse/JDK-8092504): fixed by using
 * {@link ListListenerHelperEx} as a replacement for ListListenerHelper.</li>
 * </ul>
 *
 * @author anyssen
 * @param <E>
 *            The element type of the {@link ObservableList}.
 *
 */
class ObservableListWrapperEx<E> extends ForwardingList<E>
		implements ObservableList<E> {

	private ListListenerHelperEx<E> helper = new ListListenerHelperEx<>(this);
	private List<E> backingList;

	/**
	 * Creates a new {@link ObservableList} wrapping the given {@link List}.
	 *
	 * @param list
	 *            The {@link List} to wrap into the newly created
	 *            {@link ObservableListWrapperEx}.
	 */
	public ObservableListWrapperEx(List<E> list) {
		this.backingList = list;
	}

	@Override
	public boolean add(E element) {
		List<E> previousContents = delegateCopy();
		boolean result = super.add(element);
		if (result) {
			helper.fireValueChangedEvent(
					new ListListenerHelperEx.AtomicChange<>(this,
							previousContents,
							ListListenerHelperEx.ElementarySubChange.added(
									Collections.singletonList(element),
									previousContents.size(),
									previousContents.size() + 1)));
		}
		return result;
	}

	@Override
	public void add(int index, E element) {
		List<E> previousContents = delegateCopy();
		super.add(index, element);
		helper.fireValueChangedEvent(
				new ListListenerHelperEx.AtomicChange<>(this, previousContents,
						ListListenerHelperEx.ElementarySubChange.added(
								Collections.singletonList(element), index,
								index + 1)));
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		List<E> previousContents = delegateCopy();
		boolean result = super.addAll(collection);
		helper.fireValueChangedEvent(new ListListenerHelperEx.AtomicChange<>(
				this, previousContents,
				ListListenerHelperEx.ElementarySubChange.added(
						new ArrayList<>(collection), previousContents.size(),
						previousContents.size() + collection.size())));
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean addAll(E... elements) {
		return addAll(Arrays.asList(elements));
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> elements) {
		List<E> previousContents = delegateCopy();
		boolean result = super.addAll(index, elements);
		helper.fireValueChangedEvent(
				new ListListenerHelperEx.AtomicChange<>(this, previousContents,
						ListListenerHelperEx.ElementarySubChange.added(
								new ArrayList<>(elements), index,
								index + elements.size())));
		return result;
	}

	@Override
	public void addListener(InvalidationListener listener) {
		helper.addListener(listener);
	}

	@Override
	public void addListener(ListChangeListener<? super E> listener) {
		helper.addListener(listener);
	}

	@Override
	public void clear() {
		List<E> previousContents = delegateCopy();
		super.clear();
		if (!previousContents.isEmpty()) {
			helper.fireValueChangedEvent(
					new ListListenerHelperEx.AtomicChange<>(this,
							previousContents,
							ListListenerHelperEx.ElementarySubChange
									.removed(previousContents, 0, 0)));
		}
	}

	@Override
	protected List<E> delegate() {
		return backingList;
	}

	/**
	 * Returns a copy of the delegate {@link List}, which is used for change
	 * notifications.
	 *
	 * @return A copy of the backing {@link List}.
	 */
	protected List<E> delegateCopy() {
		return new ArrayList<>(backingList);
	}

	@Override
	public E remove(int index) {
		List<E> previousContents = delegateCopy();
		E result = super.remove(index);
		helper.fireValueChangedEvent(
				new ListListenerHelperEx.AtomicChange<>(this, previousContents,
						ListListenerHelperEx.ElementarySubChange.removed(
								Collections.singletonList(result), index,
								index)));
		return result;
	}

	@Override
	public void remove(int from, int to) {
		List<E> previousContents = delegateCopy();
		List<E> removed = new ArrayList<>();
		for (int i = to - 1; i >= from; i--) {
			removed.add(0, super.remove(i));
		}
		helper.fireValueChangedEvent(new ListListenerHelperEx.AtomicChange<>(
				this, previousContents, ListListenerHelperEx.ElementarySubChange
						.removed(removed, from, from)));
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object object) {
		List<E> previousContents = delegateCopy();
		if (super.remove(object)) {
			// XXX: if remove was successful, its safe to cast here
			int index = previousContents.indexOf(object);
			helper.fireValueChangedEvent(
					new ListListenerHelperEx.AtomicChange<>(this,
							previousContents,
							ListListenerHelperEx.ElementarySubChange.removed(
									Collections.singletonList((E) object),
									index, index)));
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		List<E> previousContents = delegateCopy();
		if (super.removeAll(collection)) {
			// check which have been removed
			List<ElementarySubChange<E>> elementaryChanges = new ArrayList<>();
			ElementarySubChange<E> currentElementaryChange = null;
			int removeCount = 0;
			for (E e : previousContents) {
				if (collection.contains(e)) {
					// create a new elementary change, if elements are not
					// 'continuous' (ensure that the count of elements that have
					// already been deleted by preceding elementary changes is
					// subtracted from the index)
					if (currentElementaryChange == null
							|| previousContents.indexOf(e)
									- currentElementaryChange.getFrom() > 1) {
						if (currentElementaryChange != null) {
							removeCount += currentElementaryChange.getRemoved()
									.size();
						}
						int index = previousContents.indexOf(e) - removeCount;
						currentElementaryChange = ElementarySubChange.removed(
								Collections.singletonList(e), index, index);
						elementaryChanges.add(currentElementaryChange);
					} else {
						// replace current elementary change (i.e. append the
						// removed element)
						List<E> removed = new ArrayList<>(
								currentElementaryChange.getRemoved());
						removed.add(e);
						int index = currentElementaryChange.getFrom();
						elementaryChanges.remove(currentElementaryChange);
						currentElementaryChange = ElementarySubChange
								.removed(removed, index, index);
						elementaryChanges.add(currentElementaryChange);
					}
				}
			}
			// determine lowest index that was removed (will be used as from and
			// to index)
			helper.fireValueChangedEvent(
					new ListListenerHelperEx.AtomicChange<>(this,
							previousContents, elementaryChanges));
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean removeAll(E... elements) {
		return removeAll(Arrays.asList(elements));
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		helper.removeListener(listener);
	}

	@Override
	public void removeListener(ListChangeListener<? super E> listener) {
		helper.removeListener(listener);
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		List<E> previousContents = delegateCopy();
		if (super.retainAll(collection)) {
			// check which have been removed
			List<ElementarySubChange<E>> elementaryChanges = new ArrayList<>();
			ElementarySubChange<E> currentElementaryChange = null;
			int removeCount = 0;
			for (E e : previousContents) {
				if (!collection.contains(e)) {
					// create a new elementary change, if elements are not
					// 'continuous' (ensure that the count of elements that have
					// already been deleted by preceding elementary changes is
					// subtracted from the index)
					if (currentElementaryChange == null
							|| previousContents.indexOf(e)
									- currentElementaryChange.getFrom() > 1) {
						if (currentElementaryChange != null) {
							removeCount += currentElementaryChange.getRemoved()
									.size();
						}
						int index = previousContents.indexOf(e) - removeCount;
						currentElementaryChange = ElementarySubChange.removed(
								Collections.singletonList(e), index, index);
						elementaryChanges.add(currentElementaryChange);
					} else {
						// replace current elementary change (i.e. append the
						// removed element)
						List<E> removed = new ArrayList<>(
								currentElementaryChange.getRemoved());
						removed.add(e);
						int index = currentElementaryChange.getFrom();
						elementaryChanges.remove(currentElementaryChange);
						currentElementaryChange = ElementarySubChange
								.removed(removed, index, index);
						elementaryChanges.add(currentElementaryChange);
					}
				}
			}
			// determine lowest index that was removed (will be used as from and
			// to index)
			helper.fireValueChangedEvent(
					new ListListenerHelperEx.AtomicChange<>(this,
							previousContents, elementaryChanges));
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean retainAll(E... elements) {
		return retainAll(Arrays.asList(elements));
	}

	@Override
	public E set(int index, E element) {
		List<E> previousContents = delegateCopy();
		if (get(index) != element) {
			E result = super.remove(index);
			super.add(index, element);
			helper.fireValueChangedEvent(
					new ListListenerHelperEx.AtomicChange<>(this,
							previousContents,
							ElementarySubChange.replaced(
									Collections.singletonList(result),
									Collections.singletonList(element), index,
									index + 1)));
			return result;
		}
		return element;
	}

	@Override
	public boolean setAll(Collection<? extends E> collection) {
		List<E> previousContents = delegateCopy();
		if (!previousContents.equals(collection)) {
			delegate().clear();
			delegate().addAll(collection);
			helper.fireValueChangedEvent(
					new ListListenerHelperEx.AtomicChange<>(this,
							previousContents, ElementarySubChange.replaced(
									previousContents, delegate(), 0, size())));
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean setAll(E... elements) {
		return setAll(Arrays.asList(elements));
	}

	/**
	 * Sorts the elements of this {@link ObservableListWrapperEx} using the
	 * default comparator.
	 */
	public void sort() {
		sort(null);
	}

	/**
	 * Sorts the elements of this {@link ObservableListWrapperEx} using the
	 * given {@link Comparator}.
	 *
	 * @param c
	 *            The {@link Comparator} to use.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void sort(Comparator<? super E> c) {
		// TODO: This algorithm is not very elaborated, as computation of
		// permutation indexes is done independent of the sort itself, and we
		// need to iterate over the complete list to compute the previous
		// indexes (so we can properly handle elements with multiple
		// occurrences).
		List<E> previousContents = delegateCopy();
		SetMultimap<E, Integer> previousIndexes = HashMultimap.create();
		for (int i = 0; i < previousContents.size(); i++) {
			previousIndexes.put(previousContents.get(i), i);
		}

		// List.sort(Comparator) was introduced in 1.8; we use list iterator
		// directly here, so we stay compatible with 1.7
		// TODO: change to using List.sort(Comparator) when dropping support for
		// JavaSE-1.7.
		Object[] a = delegate().toArray();
		int[] permutation = new int[a.length];
		Arrays.sort(a, (Comparator) c);
		ListIterator<E> iterator = delegate().listIterator();
		// keep track if list was actually changed
		boolean changed = false;
		for (int i = 0; i < a.length; i++) {
			E current = iterator.next();
			if (current != a[i]) {
				changed = true;
				iterator.set((E) a[i]);
			}
			// build-up permutation (for change notification)
			Iterator<Integer> previousIndexIterator = previousIndexes
					.get((E) a[i]).iterator();
			permutation[previousIndexIterator.next()] = i;
			previousIndexIterator.remove();
		}
		if (changed) {
			helper.fireValueChangedEvent(
					new ListListenerHelperEx.AtomicChange<>(this,
							previousContents,
							ListListenerHelperEx.ElementarySubChange
									.<E> permutated(permutation, 0, a.length)));
		}
	}

	// TODO: overwrite replaceAll(UnaryOperator) as well, as soon as we drop
	// Java 7 support.
}
