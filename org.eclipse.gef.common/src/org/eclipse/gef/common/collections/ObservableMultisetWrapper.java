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
package org.eclipse.gef.common.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.gef.common.collections.MultisetListenerHelper.ElementarySubChange;

import com.google.common.collect.ForwardingMultiset;
import com.google.common.collect.HashMultiset;
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
class ObservableMultisetWrapper<E> extends ForwardingMultiset<E>
		implements ObservableMultiset<E> {

	private MultisetListenerHelper<E> helper = new MultisetListenerHelper<>(
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
	public boolean add(E element) {
		Multiset<E> previousContents = delegateCopy();
		boolean changed = super.add(element);
		if (changed) {
			helper.fireValueChangedEvent(
					new MultisetListenerHelper.AtomicChange<>(this,
							previousContents,
							new ElementarySubChange<>(element, 0, 1)));
		}
		return changed;
	}

	@Override
	public int add(E element, int occurrences) {
		Multiset<E> previousContents = delegateCopy();
		int countBefore = super.add(element, occurrences);
		if (count(element) > countBefore) {
			// only fire change if occurrences have really been added.
			helper.fireValueChangedEvent(
					new MultisetListenerHelper.AtomicChange<>(this,
							previousContents, new ElementarySubChange<>(element,
									0, count(element) - countBefore)));
		}
		return countBefore;
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		Multiset<E> previousContents = delegateCopy();
		boolean changed = super.addAll(collection);
		if (changed) {
			List<ElementarySubChange<E>> elementaryChanges = new ArrayList<>();
			// collection may contain element multiple times; as we only want to
			// notify once per element, we have to iterate over the set of
			// unique elements
			for (E e : new HashSet<>(collection)) {
				if (previousContents.contains(e)) {
					// already contained
					if (count(e) > previousContents.count(e)) {
						elementaryChanges.add(new ElementarySubChange<>(e, 0,
								count(e) - previousContents.count(e)));
					}
				} else {
					// newly added
					elementaryChanges
							.add(new ElementarySubChange<>(e, 0, count(e)));
				}
			}
			helper.fireValueChangedEvent(
					new MultisetListenerHelper.AtomicChange<>(this,
							previousContents, elementaryChanges));
		}
		return changed;
	}

	@Override
	public void addListener(InvalidationListener listener) {
		helper.addListener(listener);
	}

	@Override
	public void addListener(MultisetChangeListener<? super E> listener) {
		helper.addListener(listener);
	}

	@Override
	public void clear() {
		Multiset<E> previousContents = delegateCopy();
		super.clear();
		if (!previousContents.isEmpty()) {
			List<ElementarySubChange<E>> elementaryChanges = new ArrayList<>();
			for (E e : previousContents.elementSet()) {
				elementaryChanges.add(new ElementarySubChange<>(e,
						previousContents.count(e), 0));
			}
			helper.fireValueChangedEvent(
					new MultisetListenerHelper.AtomicChange<>(this,
							previousContents, elementaryChanges));
		}
	}

	@Override
	protected Multiset<E> delegate() {
		return backingMultiset;
	}

	/**
	 * Returns a copy of the delegate {@link Multiset}, which is used for change
	 * notifications.
	 *
	 * @return A copy of the backing {@link Multiset}.
	 */
	protected Multiset<E> delegateCopy() {
		return HashMultiset.create(backingMultiset);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object object) {
		Multiset<E> previousContents = delegateCopy();
		boolean changed = super.remove(object);
		if (changed) {
			// if remove was successful, the cast to E should be safe.
			helper.fireValueChangedEvent(
					new MultisetListenerHelper.AtomicChange<>(this,
							previousContents,
							new ElementarySubChange<>((E) object, 1, 0)));
		}
		return changed;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int remove(Object element, int occurrences) {
		Multiset<E> previousContents = delegateCopy();
		int countBefore = super.remove(element, occurrences);
		if (countBefore > count(element)) {
			// if the element has been removed, the cast to E should be safe
			// here; we may actually remove fewer then the specified
			// occurrences, thus we have to compute how many have actually be
			// removed.
			helper.fireValueChangedEvent(
					new MultisetListenerHelper.AtomicChange<>(this,
							previousContents,
							new ElementarySubChange<>((E) element,
									countBefore - count(element), 0)));
		}
		return countBefore;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean removeAll(Collection<?> collection) {
		Multiset<E> previousContents = delegateCopy();
		boolean changed = super.removeAll(collection);
		if (changed) {
			List<ElementarySubChange<E>> elementaryChanges = new ArrayList<>();
			// collection may contain element multiple times; as we only want to
			// notify once per element, we have to iterate over the set of
			// unique elements
			for (Object e : new HashSet<>(collection)) {
				if (previousContents.contains(e)) {
					// if the element was contained, its safe to cast to E in
					// the following
					if (previousContents.count(e) > count(e)) {
						elementaryChanges.add(new ElementarySubChange<>((E) e,
								previousContents.count(e), 0));
					}
				}
			}
			helper.fireValueChangedEvent(
					new MultisetListenerHelper.AtomicChange<>(this,
							previousContents, elementaryChanges));
		}
		return changed;
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		helper.removeListener(listener);
	}

	@Override
	public void removeListener(MultisetChangeListener<? super E> listener) {
		helper.removeListener(listener);
	}

	@Override
	public boolean replaceAll(Multiset<? extends E> multiset) {
		Multiset<E> previousContents = delegateCopy();

		super.clear();
		super.addAll(multiset);

		Multiset<E> removedElements = Multisets.difference(previousContents,
				multiset);
		Multiset<? extends E> addedElements = Multisets.difference(multiset,
				previousContents);
		if (!addedElements.isEmpty() || !removedElements.isEmpty()) {
			List<ElementarySubChange<E>> elementaryChanges = new ArrayList<>();
			// removed / decreased elements
			for (E e : removedElements.elementSet()) {
				elementaryChanges.add(new ElementarySubChange<>(e,
						removedElements.count(e), 0));
			}
			// added / increased entries
			for (E e : addedElements.elementSet()) {
				elementaryChanges.add(new ElementarySubChange<>(e, 0,
						addedElements.count(e)));
			}
			helper.fireValueChangedEvent(
					new MultisetListenerHelper.AtomicChange<>(this,
							previousContents, elementaryChanges));
			return true;
		}
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		Multiset<E> previousContents = delegateCopy();
		boolean changed = super.retainAll(collection);
		if (changed) {
			List<ElementarySubChange<E>> elementaryChanges = new ArrayList<>();
			// collection may contain element multiple times; as we only want to
			// notify once per element, we have to iterate over the set of
			// unique elements
			for (E e : previousContents.elementSet()) {
				if (!collection.contains(e)) {
					elementaryChanges.add(new ElementarySubChange<>(e,
							previousContents.count(e), 0));

				}
			}
			helper.fireValueChangedEvent(
					new MultisetListenerHelper.AtomicChange<>(this,
							previousContents, elementaryChanges));
		}
		return changed;
	}

	@Override
	public int setCount(E element, int count) {
		Multiset<E> previousContents = delegateCopy();
		int countBefore = super.setCount(element, count);
		if (count(element) > countBefore) {
			helper.fireValueChangedEvent(
					new MultisetListenerHelper.AtomicChange<>(this,
							previousContents, new ElementarySubChange<>(element,
									0, count(element) - countBefore)));
		} else if (count(element) < countBefore) {
			helper.fireValueChangedEvent(
					new MultisetListenerHelper.AtomicChange<>(this,
							previousContents, new ElementarySubChange<>(element,
									countBefore - count(element), 0)));
		}
		return countBefore;
	}

	@Override
	public boolean setCount(E element, int oldCount, int newCount) {
		Multiset<E> previousContents = delegateCopy();
		boolean changed = super.setCount(element, oldCount, newCount);
		// if changed it means that the oldCound was matched and that now we
		// have the new count
		if (changed) {
			if (newCount > oldCount) {
				helper.fireValueChangedEvent(
						new MultisetListenerHelper.AtomicChange<>(this,
								previousContents, new ElementarySubChange<>(
										element, 0, newCount - oldCount)));
			} else if (oldCount > newCount) {
				helper.fireValueChangedEvent(
						new MultisetListenerHelper.AtomicChange<>(this,
								previousContents, new ElementarySubChange<>(
										element, oldCount - newCount, 0)));
			}
		}
		return changed;
	}

}
