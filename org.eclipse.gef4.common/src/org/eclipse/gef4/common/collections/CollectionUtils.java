/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.common.collections;

import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.collections.ObservableListWrapper;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * The {@link CollectionUtils} contains a method to compute the old value of an
 * {@link ObservableList} for a given
 * {@link javafx.collections.ListChangeListener.Change} event. For details, see
 * {@link #getPreviousContents(javafx.collections.ListChangeListener.Change)}.
 *
 * @author mwienand
 *
 */
public class CollectionUtils {

	private static class ElementaryListChange<E> {

		public static <E> ElementaryListChange<E> createAddRemove(
				List<E> removed, int from, int to) {
			return new ElementaryListChange<>(false, true, removed, from, to,
					null);
		}

		public static <E> ElementaryListChange<E> createPermutation(int from,
				int to, List<Integer> perm) {
			return new ElementaryListChange<>(true, false, null, from, to,
					perm);
		}

		public boolean isPerm;
		public boolean isAddRemove;
		public int from;
		public int to;
		public List<E> removed;
		public List<Integer> permutation;

		private ElementaryListChange(boolean p, boolean a, List<E> removed,
				int from, int to, List<Integer> perm) {
			this.from = from;
			this.to = to;
			isPerm = p;
			isAddRemove = a;
			if (removed != null) {
				this.removed = new ArrayList<>(removed);
			}
			if (perm != null) {
				permutation = new ArrayList<>(perm);
			}
		}

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
		// reset change
		change.reset();

		ObservableList<E> currentList = change.getList();
		ObservableListWrapper<E> previousList = new ObservableListWrapper<>(
				new ArrayList<>(currentList));

		// walk over elementary changes and record them in a list
		List<ElementaryListChange<E>> changes = new ArrayList<>();

		while (change.next()) {
			if (change.wasPermutated()) {
				// find permutation
				List<Integer> permutation = new ArrayList<>();
				for (int oldIndex = change.getFrom(); oldIndex < change
						.getTo(); oldIndex++) {
					int newIndex = change.getPermutation(oldIndex);
					permutation.add(newIndex);
				}
				// record change
				changes.add(ElementaryListChange.<E> createPermutation(
						change.getFrom(), change.getTo(), permutation));
			} else if (change.wasAdded() || change.wasRemoved()) {
				// record change
				changes.add(ElementaryListChange.<E> createAddRemove(
						change.getRemoved(), change.getFrom(), change.getTo()));
			} else if (change.wasUpdated()) {
				// nothing to do
			}
		}

		// undo the changes in reverse order
		for (int i = changes.size() - 1; i >= 0; i--) {
			ElementaryListChange<E> c = changes.get(i);

			if (c.isAddRemove) {
				// remove added elements
				for (int j = c.to - 1; j >= c.from; j--) {
					previousList.remove(j);
				}
				// add removed elements
				if (c.removed != null) {
					previousList.addAll(c.from, c.removed);
				}
			} else if (c.isPerm) {
				// create sub list with old permutation
				List<E> subList = new ArrayList<>(c.to - c.from);
				for (int j = c.from; j < c.to; j++) {
					int k = c.permutation.get(j - c.from);
					subList.add(currentList.get(k));
				}
				// insert sub list at correct position
				previousList.remove(c.from, c.to);
				previousList.addAll(c.from, subList);
			}
		}

		// reset change
		change.reset();

		return previousList;
	}

}
