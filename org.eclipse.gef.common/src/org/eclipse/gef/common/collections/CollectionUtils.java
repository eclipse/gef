/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen  (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.common.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.eclipse.gef.common.collections.ListListenerHelperEx.AtomicChange;
import org.eclipse.gef.common.collections.ListListenerHelperEx.ElementarySubChange;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

/**
 * The {@link CollectionUtils} contains a method to compute the old value of an
 * {@link ObservableList} for a given
 * {@link javafx.collections.ListChangeListener.Change} event. For details, see
 * {@link #getPreviousContents(javafx.collections.ListChangeListener.Change)}.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class CollectionUtils {

	/**
	 * Returns an empty, unmodifiable {@link ObservableMultiset}.
	 *
	 * @param <E>
	 *            The element type of the {@link ObservableMultiset}.
	 * @return An empty, unmodifiable {@link ObservableMultiset}.
	 */
	public static <E> ObservableMultiset<E> emptyMultiset() {
		// TODO: use singleton field
		return new UnmodifiableObservableMultisetWrapper<>(
				new ObservableMultisetWrapper<>(HashMultiset.<E> create()));
	}

	/**
	 * Returns an empty, unmodifiable {@link ObservableSetMultimap}.
	 *
	 * @param <K>
	 *            The key type of the {@link ObservableSetMultimap}.
	 * @param <V>
	 *            The value type of the {@link ObservableSetMultimap}.
	 * @return An empty, unmodifiable {@link ObservableSetMultimap}.
	 */
	public static <K, V> ObservableSetMultimap<K, V> emptySetMultimap() {
		// TODO: use singleton field
		return new UnmodifiableObservableSetMultimapWrapper<>(
				new ObservableSetMultimapWrapper<>(
						HashMultimap.<K, V> create()));
	}

	/**
	 * Computes the permutation for the given {@link Change}.
	 *
	 * @param <E>
	 *            The element type of the {@link ObservableList} that was
	 *            changed.
	 * @param change
	 *            The change, for which {@link Change#wasPermutated()} has to
	 *            return <code>true</code>.
	 * @return An integer array mapping previous indexes to current ones.
	 */
	public static <E> int[] getPermutation(
			ListChangeListener.Change<? extends E> change) {
		if (!change.wasPermutated()) {
			throw new IllegalArgumentException(
					"Change is no permutation change.");
		}
		if (change instanceof AtomicChange) {
			return ((AtomicChange<?>) change).getPermutation();
		}
		int[] permutation = new int[change.getTo() - change.getFrom()];
		for (int oldIndex = change.getFrom(); oldIndex < change
				.getTo(); oldIndex++) {
			int newIndex = change.getPermutation(oldIndex);
			permutation[oldIndex] = newIndex;
		}
		return permutation;
	}

	/**
	 * Computes the previous contents of the source {@link ObservableList}
	 * before the given {@link javafx.collections.ListChangeListener.Change} was
	 * applied.
	 *
	 * @param <E>
	 *            The element type of the {@link ObservableList}.
	 * @param change
	 *            The {@link javafx.collections.ListChangeListener.Change} for
	 *            which to compute the previous contents.
	 * @return A newly created {@link List} that resembles the state of the
	 *         source {@link ObservableList} before the change.
	 */
	public static <E> List<E> getPreviousContents(
			ListChangeListener.Change<E> change) {
		if (change instanceof AtomicChange) {
			return ((AtomicChange<E>) change).getPreviousContents();
		}

		ObservableList<E> currentList = change.getList();
		ObservableList<E> previousList = FXCollections
				.observableArrayList(currentList);

		// walk over elementary changes and record them in a list
		change.reset();
		List<ElementarySubChange<E>> changes = ListListenerHelperEx
				.getElementaryChanges(change);

		// undo the changes in reverse order
		for (int i = changes.size() - 1; i >= 0; i--) {
			ElementarySubChange<E> c = changes.get(i);
			int from = c.getFrom();
			int to = c.getTo();
			if (ElementarySubChange.Kind.ADD.equals(c.getKind())
					|| ElementarySubChange.Kind.REPLACE.equals(c.getKind())) {
				// remove added elements
				for (int j = to - 1; j >= from; j--) {
					previousList.remove(j);
				}
			}
			if (ElementarySubChange.Kind.REMOVE.equals(c.getKind())
					|| ElementarySubChange.Kind.REPLACE.equals(c.getKind())) {
				// add removed elements
				List<E> removed = c.getRemoved();
				previousList.addAll(from, removed);
			}
			if (ElementarySubChange.Kind.PERMUTATE.equals(c.getKind())) {
				// create sub list with old permutation
				int[] permutation = c.getPermutation();
				List<E> subList = new ArrayList<>(to - from);
				for (int j = from; j < to; j++) {
					int k = permutation[j - from];
					subList.add(currentList.get(k));
				}
				// insert sub list at correct position
				previousList.remove(from, to);
				previousList.addAll(from, subList);
			}
		}
		return previousList;
	}

	/**
	 * Returns a (modifiable) new {@link ObservableList} wrapping an
	 * {@link ArrayList}.
	 *
	 * Please note that in order to obtain proper change notifications when
	 * sorting the returned {@link ObservableList},
	 * {@link #sort(ObservableList)} or
	 * {@link #sort(ObservableList, Comparator)} have to be used instead of
	 * {@link FXCollections#sort(ObservableList)} and
	 * {@link FXCollections#sort(ObservableList, Comparator)}.
	 *
	 * @param <E>
	 *            The element type of the {@link ObservableList}. The
	 *            {@link List} to wrap.
	 * @return An {@link ObservableList} wrapping the given {@link List}.
	 */
	public static <E> ObservableList<E> observableArrayList() {
		return observableList(new ArrayList<E>());
	}

	/**
	 * Create a new {@link ObservableList} that is backed by an
	 * {@link ArrayList} that contains the contents of the given
	 * {@link Collection}.
	 *
	 * @param <E>
	 *            The element type of the {@link ObservableList}.
	 * @param collection
	 *            The {@link Collection} that provides the initial contents of
	 *            the to be created {@link ObservableList}.
	 * @return A new {@link ObservableList} containing the given contents.
	 */
	public static <E> ObservableList<E> observableArrayList(
			Collection<? extends E> collection) {
		ObservableList<E> list = observableArrayList();
		list.addAll(collection);
		return list;
	}

	/**
	 * Creates a new {@link ObservableList} that contains the given elements.
	 *
	 * @param <E>
	 *            The element type of the {@link ObservableList}.
	 *
	 * @return a newly created observableArrayList
	 * @param elements
	 *            The elements that will be added to the returned
	 *            {@link ObservableList}
	 */
	@SuppressWarnings("unchecked")
	public static <E> ObservableList<E> observableArrayList(E... elements) {
		ObservableList<E> list = observableArrayList();
		list.addAll(elements);
		return list;
	}

	/**
	 * Returns a (modifiable) new {@link ObservableSetMultimap} wrapping a
	 * {@link HashMultimap}.
	 *
	 * @param <K>
	 *            The key type of the {@link ObservableSetMultimap}.
	 * @param <V>
	 *            The value type of the {@link ObservableSetMultimap}
	 * @return An {@link ObservableSetMultimap} wrapping a {@link HashMultimap}.
	 */
	public static <K, V> ObservableSetMultimap<K, V> observableHashMultimap() {
		return observableSetMultimap(HashMultimap.<K, V> create());
	}

	/**
	 * Returns a (modifiable) new {@link ObservableMultiset} wrapping a
	 * {@link HashMultiset}.
	 *
	 * @param <E>
	 *            The element type of the {@link ObservableList}.
	 * @return An {@link ObservableMultiset} wrapping a {@link HashMultiset}.
	 */
	public static <E> ObservableMultiset<E> observableHashMultiset() {
		return observableMultiset(HashMultiset.<E> create());
	}

	/**
	 * Returns a (modifiable) new {@link ObservableList} wrapping the given
	 * {@link List}.
	 *
	 * Please note that in order to obtain proper change notifications when
	 * sorting the returned {@link ObservableList},
	 * {@link #sort(ObservableList)} or
	 * {@link #sort(ObservableList, Comparator)} have to be used instead of
	 * {@link FXCollections#sort(ObservableList)} and
	 * {@link FXCollections#sort(ObservableList, Comparator)}.
	 *
	 * @param <E>
	 *            The element type of the {@link ObservableList}.
	 * @param list
	 *            The {@link List} to wrap.
	 * @return An {@link ObservableList} wrapping the given {@link List}.
	 */
	public static <E> ObservableList<E> observableList(List<E> list) {
		if (list == null) {
			throw new NullPointerException();
		}
		return new ObservableListWrapperEx<>(list);
	}

	/**
	 * Returns a (modifiable) new {@link ObservableMultiset} wrapping the given
	 * {@link List}.
	 *
	 * @param <E>
	 *            The element type of the {@link ObservableList}.
	 * @param multiset
	 *            The {@link Multiset} to wrap.
	 * @return An {@link ObservableMultiset} wrapping the given {@link List}.
	 */
	public static <E> ObservableMultiset<E> observableMultiset(
			Multiset<E> multiset) {
		if (multiset == null) {
			throw new NullPointerException();
		}
		return new ObservableMultisetWrapper<>(multiset);
	}

	/**
	 * Returns a (modifiable) new {@link ObservableSetMultimap} wrapping the
	 * given {@link SetMultimap}.
	 *
	 * @param <K>
	 *            The key type of the {@link ObservableSetMultimap}.
	 * @param <V>
	 *            The value type of the {@link ObservableSetMultimap}
	 * @param setMultimap
	 *            The {@link SetMultimap} to wrap.
	 * @return An {@link ObservableSetMultimap} wrapping the given {@link List}.
	 */
	public static <K, V> ObservableSetMultimap<K, V> observableSetMultimap(
			SetMultimap<K, V> setMultimap) {
		if (setMultimap == null) {
			throw new NullPointerException();
		}
		return new ObservableSetMultimapWrapper<>(setMultimap);
	}

	/**
	 * Sorts the given {@link ObservableList} using the default
	 * {@link Comparator} .
	 *
	 * @param <E>
	 *            The value type of the {@link ObservableList}.
	 * @param observableList
	 *            The {@link ObservableList} to sort.
	 */
	public static <E extends Comparable<? super E>> void sort(
			ObservableList<E> observableList) {
		if (observableList instanceof ObservableListWrapperEx) {
			((ObservableListWrapperEx<? extends E>) observableList).sort();
		} else {
			FXCollections.sort(observableList);
		}
	}

	/**
	 * Sorts the given {@link ObservableList} using the given {@link Comparator}
	 * .
	 *
	 * @param <E>
	 *            The value type of the {@link ObservableList}.
	 * @param observableList
	 *            The {@link ObservableList} to sort.
	 * @param comparator
	 *            The {@link Comparator} to use.
	 */
	public static <E> void sort(ObservableList<E> observableList,
			Comparator<? super E> comparator) {
		if (observableList instanceof ObservableListWrapperEx) {
			((ObservableListWrapperEx<? extends E>) observableList)
					.sort(comparator);
		} else {
			FXCollections.sort(observableList, comparator);
		}
	}

	/**
	 * Returns an unmodifiable {@link ObservableMultiset} wrapping the given
	 * {@link ObservableMultiset}.
	 *
	 * @param <E>
	 *            The element type of the {@link ObservableMultiset}.
	 * @param multiset
	 *            The {@link ObservableMultiset} to wrap.
	 * @return An unmodifiable wrapper around the given
	 *         {@link ObservableMultiset}.
	 */
	public static <E> ObservableMultiset<E> unmodifiableObservableMultiset(
			ObservableMultiset<E> multiset) {
		if (multiset == null) {
			throw new NullPointerException();
		}
		return new UnmodifiableObservableMultisetWrapper<>(multiset);
	}

	/**
	 * Returns an unmodifiable {@link ObservableSetMultimap} wrapping the given
	 * {@link ObservableSetMultimap}.
	 *
	 * @param <K>
	 *            The key type of the {@link ObservableSetMultimap}.
	 * @param <V>
	 *            The value type of the {@link ObservableSetMultimap}.
	 * @param setMultimap
	 *            The {@link ObservableSetMultimap} to wrap.
	 * @return An unmodifiable wrapper around the given
	 *         {@link ObservableSetMultimap}.
	 */
	public static <K, V> ObservableSetMultimap<K, V> unmodifiableObservableSetMultimap(
			ObservableSetMultimap<K, V> setMultimap) {
		if (setMultimap == null) {
			throw new NullPointerException();
		}
		return new UnmodifiableObservableSetMultimapWrapper<>(setMultimap);
	}

}
