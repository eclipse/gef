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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.gef4.common.collections.MultisetChangeListenerHelper.SimpleChange;

import com.google.common.collect.ForwardingMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import javafx.beans.InvalidationListener;

/**
 * An {@link ObservableMultisetWrapper} is an {@link ObservableMultiset} that
 * wraps an underlying {@link Multiset}.
 * 
 * @param <E>
 *            The element type of the {@link Multiset}.
 *
 * @author anyssen
 */
public class ObservableMultisetWrapper<E> extends ForwardingMultiset<E>
		implements ObservableMultiset<E> {

	private MultisetChangeListenerHelper<E> helper = new MultisetChangeListenerHelper<>(
			this);
	private Multiset<E> backingMultiset;

	/**
	 * Creates a new {@link ObservableMultiset} wrapping the given
	 * {@link Multiset}.
	 * 
	 * @param setMultimap
	 *            The {@link Multiset} to wrap into the newly created
	 *            {@link ObservableMultisetWrapper}.
	 */
	public ObservableMultisetWrapper(Multiset<E> setMultimap) {
		this.backingMultiset = setMultimap;
	}

	@Override
	public void addListener(InvalidationListener listener) {
		helper.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		helper.removeListener(listener);
	}

	@Override
	public void addListener(MultisetChangeListener<? super E> listener) {
		helper.addListener(listener);
	}

	@Override
	public void removeListener(MultisetChangeListener<? super E> listener) {
		helper.removeListener(listener);
	}

	@Override
	protected Multiset<E> delegate() {
		return backingMultiset;
	}

	@Override
	public int add(E element, int occurrences) {
		int countBefore = super.add(element, occurrences);
		if (count(element) > countBefore) {
			// only fire change if occurrences have really been added.
			helper.fireValueChangedEvent(
					new MultisetChangeListenerHelper.SimpleChange<>(this,
							element, 0, count(element) - countBefore));
		}
		return countBefore;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int remove(Object element, int occurrences) {
		int countBefore = super.remove(element, occurrences);
		if (countBefore > count(element)) {
			// if the element has been removed, the cast to E should be safe
			// here; we may actually remove fewer then the specified
			// occurrences, thus we have to compute how many have actually be
			// removed.
			helper.fireValueChangedEvent(
					new MultisetChangeListenerHelper.SimpleChange<>(this,
							(E) element, countBefore - count(element), 0));
		}
		return countBefore;
	}

	@Override
	public int setCount(E element, int count) {
		int countBefore = super.setCount(element, count);
		if (count(element) > countBefore) {
			helper.fireValueChangedEvent(
					new MultisetChangeListenerHelper.SimpleChange<>(this,
							(E) element, 0, count(element) - countBefore));
		} else if (count(element) < countBefore) {
			helper.fireValueChangedEvent(
					new MultisetChangeListenerHelper.SimpleChange<>(this,
							(E) element, countBefore - count(element), 0));
		}
		return countBefore;
	}

	@Override
	public boolean setCount(E element, int oldCount, int newCount) {
		boolean changed = super.setCount(element, oldCount, newCount);
		// if changed it means that the oldCound was matched and that now we
		// have the new count
		if (changed) {
			if (newCount > oldCount) {
				helper.fireValueChangedEvent(
						new MultisetChangeListenerHelper.SimpleChange<>(this,
								(E) element, 0, newCount - oldCount));
			} else if (oldCount > newCount) {
				helper.fireValueChangedEvent(
						new MultisetChangeListenerHelper.SimpleChange<>(this,
								(E) element, oldCount - newCount, 0));
			}
		}
		return changed;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean removeAll(Collection<?> collection) {
		Map<E, Integer> countsBefore = new HashMap<>();
		for (E e : elementSet()) {
			countsBefore.put(e, count(e));
		}
		boolean changed = super.removeAll(collection);
		if (changed) {
			// collection may contain element multiple times; as we only want to
			// notify once per element, we have to iterate over the set of
			// unique elements
			for (Object e : new HashSet<>(collection)) {
				if (countsBefore.containsKey(e)) {
					// if the element was contained, its safe to cast to E in
					// the following
					if (countsBefore.get(e) > count(e)) {
						helper.fireValueChangedEvent(
								new MultisetChangeListenerHelper.SimpleChange<>(
										this, (E) e, countsBefore.get(e), 0));
					}
				}
			}
		}
		return changed;
	}

	@Override
	public boolean add(E element) {
		boolean changed = super.add(element);
		if (changed) {
			helper.fireValueChangedEvent(
					new MultisetChangeListenerHelper.SimpleChange<>(this,
							element, 0, 1));
		}
		return changed;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object object) {
		boolean changed = super.remove(object);
		if (changed) {
			// if remove was successful, the cast to E should be safe.
			helper.fireValueChangedEvent(
					new MultisetChangeListenerHelper.SimpleChange<>(this,
							(E) object, 1, 0));
		}
		return changed;
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		Map<E, Integer> countsBefore = new HashMap<>();
		for (E e : elementSet()) {
			countsBefore.put(e, count(e));
		}
		boolean changed = super.addAll(collection);
		if (changed) {
			// collection may contain element multiple times; as we only want to
			// notify once per element, we have to iterate over the set of
			// unique elements
			for (E e : new HashSet<>(collection)) {
				if (countsBefore.containsKey(e)) {
					// already contained
					if (count(e) > countsBefore.get(e)) {
						helper.fireValueChangedEvent(
								new MultisetChangeListenerHelper.SimpleChange<>(
										this, e, 0,
										count(e) - countsBefore.get(e)));
					}
				} else {
					// newly added
					helper.fireValueChangedEvent(
							new MultisetChangeListenerHelper.SimpleChange<>(
									this, e, 0, count(e)));
				}
			}
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		Map<E, Integer> countsBefore = new HashMap<>();
		for (E e : elementSet()) {
			countsBefore.put(e, count(e));
		}
		boolean changed = super.retainAll(collection);
		if (changed) {
			// collection may contain element multiple times; as we only want to
			// notify once per element, we have to iterate over the set of
			// unique elements
			for (E e : countsBefore.keySet()) {
				if (!collection.contains(e)) {
					helper.fireValueChangedEvent(
							new MultisetChangeListenerHelper.SimpleChange<>(
									this, e, countsBefore.get(e), 0));
				}
			}
		}
		return changed;
	}

	@Override
	public boolean replaceAll(Multiset<? extends E> multiset) {
		Multiset<E> addedElements = Multisets.difference(delegate(), multiset);
		Multiset<? extends E> removedElements = Multisets.difference(multiset,
				delegate());
		super.clear();
		super.addAll(multiset);

		// removed elements
		for (E e : removedElements.elementSet()) {
			helper.fireValueChangedEvent(
					new SimpleChange<>(this, e, removedElements.count(e), 0));
		}
		// added entries
		for (E e : addedElements.elementSet()) {
			helper.fireValueChangedEvent(
					new SimpleChange<>(this, e, 0, addedElements.count(e)));
		}
		return !addedElements.isEmpty() || !removedElements.isEmpty();
	}

	@Override
	public void clear() {
		Map<E, Integer> countsBefore = new HashMap<>();
		for (E e : elementSet()) {
			countsBefore.put(e, count(e));
		}
		super.clear();
		for (E e : countsBefore.keySet()) {
			helper.fireValueChangedEvent(
					new MultisetChangeListenerHelper.SimpleChange<>(this, e,
							countsBefore.get(e), 0));
		}
	}

}
