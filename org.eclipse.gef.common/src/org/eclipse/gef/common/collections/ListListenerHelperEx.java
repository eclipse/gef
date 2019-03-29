/******************************************************************************
 * Copyright (c) 2016, 2019 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - remove checkCursor()
 *
 *******************************************************************************/
package org.eclipse.gef.common.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

/**
 * A utility class to support change notifications for an {@link ObservableList}
 * , replacing the JavaFX-internal {@code ListChangeListener} helper class.
 *
 * @author anyssen
 *
 * @param <E>
 *            The element type of the {@link ObservableList}.
 *
 */
public class ListListenerHelperEx<E> {

	/**
	 * A simple implementation of an
	 * {@link javafx.collections.ListChangeListener.Change}.
	 *
	 * @author anyssen
	 *
	 * @param <E>
	 *            The element type of the source {@link ObservableList}.
	 */
	public static class AtomicChange<E> extends ListChangeListener.Change<E> {

		private int cursor = -1;
		private ElementarySubChange<E>[] elementarySubChanges;
		private List<E> previousContents;

		/**
		 * Creates a new {@link ListListenerHelperEx.AtomicChange} that
		 * represents a change comprising a single elementary sub-change.
		 *
		 * @param source
		 *            The source {@link ObservableList} from which the change
		 *            originated.
		 * @param previousContents
		 *            The previous contents of the {@link ObservableList} before
		 *            the change was applied.
		 * @param elementarySubChange
		 *            The elementary sub-change that has been applied.
		 */
		@SuppressWarnings("unchecked")
		public AtomicChange(ObservableList<E> source, List<E> previousContents,
				ElementarySubChange<E> elementarySubChange) {
			super(source);
			this.previousContents = previousContents;
			this.elementarySubChanges = new ElementarySubChange[] {
					elementarySubChange };
		}

		/**
		 * Creates a new {@link ListListenerHelperEx.AtomicChange} that
		 * represents a change comprising multiple elementary sub-changesO.
		 *
		 * @param source
		 *            The source {@link ObservableList} from which the change
		 *            originated.
		 * @param previousContents
		 *            The previous contents of the {@link ObservableList} before
		 *            the change was applied.
		 * @param elementarySubChanges
		 *            The elementary sub-changes that have been applied as part
		 *            of this change.
		 */
		@SuppressWarnings("unchecked")
		public AtomicChange(ObservableList<E> source, List<E> previousContents,
				List<ElementarySubChange<E>> elementarySubChanges) {
			super(source);
			this.previousContents = previousContents;
			this.elementarySubChanges = elementarySubChanges
					.toArray(new ElementarySubChange[] {});
		}

		/**
		 * Creates a new {@link ListListenerHelperEx.AtomicChange} for the
		 * passed in source, based on the data provided in the passed-in change.
		 * <p>
		 * This is basically used to allow properties wrapping an
		 * {@link ObservableList} to re-fire change events of their wrapped
		 * {@link ObservableList} with themselves as source.
		 *
		 * @param source
		 *            The new source {@link ObservableList}.
		 * @param change
		 *            The change to infer a new change from. It is expected that
		 *            the change is in initial state. In either case it will be
		 *            reset to initial state.
		 */
		@SuppressWarnings("unchecked")
		public AtomicChange(ObservableList<E> source,
				ListChangeListener.Change<? extends E> change) {
			super(source);

			// copy previous contents
			this.previousContents = new ArrayList<>(
					CollectionUtils.getPreviousContents(change));

			// retrieve elementary sub-changes by iterating them
			// TODO: we could introduce an initialized field inside Change
			// already, so we could check the passed in change is not already
			// initialized
			List<ElementarySubChange<E>> elementarySubChanges = getElementaryChanges(
					change);
			this.elementarySubChanges = elementarySubChanges
					.toArray(new ElementarySubChange[] {});
		}

		@Override
		public int getAddedSize() {
			return super.getAddedSize();
		}

		@Override
		public List<E> getAddedSubList() {
			return elementarySubChanges[cursor].getAdded();
		}

		@Override
		public int getFrom() {
			return elementarySubChanges[cursor].getFrom();
		}

		@Override
		public int[] getPermutation() {
			return elementarySubChanges[cursor].getPermutation();
		}

		@Override
		public int getPermutation(int i) {
			return super.getPermutation(i);
		}

		/**
		 * Returns the previous contents of the observable list before the
		 * change was applied.
		 *
		 * @return An unmodifiable list containing the previous contents of the
		 *         list.
		 */
		public List<E> getPreviousContents() {
			return Collections.unmodifiableList(previousContents);
		}

		@Override
		public List<E> getRemoved() {
			return elementarySubChanges[cursor].getRemoved();
		}

		@Override
		public int getRemovedSize() {
			return super.getRemovedSize();
		}

		@Override
		public int getTo() {
			return elementarySubChanges[cursor].getTo();
		}

		@Override
		public boolean next() {
			cursor++;
			return cursor < elementarySubChanges.length;
		}

		@Override
		public void reset() {
			cursor = -1;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < elementarySubChanges.length; i++) {
				sb.append(elementarySubChanges[i].toString());
				if (i < elementarySubChanges.length - 1) {
					sb.append(" ");
				}
			}
			return sb.toString();
		}

		@Override
		public boolean wasAdded() {
			return !getAddedSubList().isEmpty();
		}

		@Override
		public boolean wasPermutated() {
			return super.wasPermutated();
		}

		@Override
		public boolean wasRemoved() {
			return super.wasRemoved();
		}

		@Override
		public boolean wasReplaced() {
			return super.wasReplaced();
		}

		@Override
		public boolean wasUpdated() {
			return super.wasUpdated();
		}
	}

	/**
	 * An abstract elementary change of an {@link ObservableList}
	 *
	 * @param <E>
	 *            The element type of the list.
	 */
	public static class ElementarySubChange<E> {

		/**
		 * The kind of change that is performed to the {@link ObservableList}.
		 *
		 * @author anyssen
		 *
		 */
		public enum Kind {
			/**
			 * Addition of elements.
			 */
			ADD,
			/**
			 * Removal of elements.
			 */
			REMOVE,
			/**
			 * Replacement of elements.
			 */
			REPLACE,
			/**
			 * Permutation of elements.
			 */
			PERMUTATE
		}

		/**
		 * Creates a new
		 * {@link org.eclipse.gef.common.collections.ListListenerHelperEx.ElementarySubChange}
		 * representing an addition.
		 *
		 * @param <E>
		 *            The element type of the {@link ObservableList}.
		 *
		 * @param from
		 *            The start index of the change.
		 * @param to
		 *            The end index of the change.
		 * @param added
		 *            The elements that were added during this change.
		 * @return An
		 *         {@link org.eclipse.gef.common.collections.ListListenerHelperEx.ElementarySubChange}
		 *         representing the change.
		 */
		public static <E> ElementarySubChange<E> added(List<? extends E> added,
				int from, int to) {
			return new ElementarySubChange<>(Kind.ADD, from, to, null, added,
					null);
		}

		/**
		 * Creates a new
		 * {@link org.eclipse.gef.common.collections.ListListenerHelperEx.ElementarySubChange}
		 * representing a permutation.
		 *
		 * @param <E>
		 *            The element type of the {@link ObservableList}.
		 * @param permutation
		 *            A mapping of prior indexes to current ones.
		 *
		 * @param from
		 *            The start index of the change.
		 * @param to
		 *            The end index of the change.
		 * @return An
		 *         {@link org.eclipse.gef.common.collections.ListListenerHelperEx.ElementarySubChange}
		 *         representing the change.
		 */
		public static <E> ElementarySubChange<E> permutated(int[] permutation,
				int from, int to) {
			return new ElementarySubChange<>(Kind.PERMUTATE, from, to, null,
					null, permutation);
		}

		/**
		 * Creates a new
		 * {@link org.eclipse.gef.common.collections.ListListenerHelperEx.ElementarySubChange}
		 * representing a removal.
		 *
		 * @param <E>
		 *            The element type of the {@link ObservableList}.
		 *
		 * @param from
		 *            The start index of the change.
		 * @param to
		 *            The end index of the change.
		 * @param removed
		 *            The elements that were removed during this change.
		 * @return An
		 *         {@link org.eclipse.gef.common.collections.ListListenerHelperEx.ElementarySubChange}
		 *         representing the change.
		 */
		public static <E> ElementarySubChange<E> removed(
				List<? extends E> removed, int from, int to) {
			return new ElementarySubChange<>(Kind.REMOVE, from, to, removed,
					null, null);
		}

		/**
		 * Creates a new
		 * {@link org.eclipse.gef.common.collections.ListListenerHelperEx.ElementarySubChange}
		 * representing a replacement.
		 *
		 * @param <E>
		 *            The element type of the {@link ObservableList}.
		 * @param removed
		 *            The elements that were removed.
		 *
		 * @param from
		 *            The start index of the change.
		 * @param to
		 *            The end index of the change.
		 * @param added
		 *            The elements that were added during this change.
		 * @return An
		 *         {@link org.eclipse.gef.common.collections.ListListenerHelperEx.ElementarySubChange}
		 *         representing the change.
		 */
		public static <E> ElementarySubChange<E> replaced(
				List<? extends E> removed, List<? extends E> added, int from,
				int to) {
			return new ElementarySubChange<>(Kind.REPLACE, from, to, removed,
					added, null);
		}

		private Kind kind;
		private List<E> removed;
		private List<E> added;
		private int from;
		private int to;

		private int[] permutation;

		/**
		 * Creates a new
		 * {@link org.eclipse.gef.common.collections.ListListenerHelperEx.ElementarySubChange}
		 * .
		 *
		 * @param kind
		 *            The kind of change.
		 *
		 * @param from
		 *            The start index of the change.
		 * @param to
		 *            The end index of the change.
		 * @param removed
		 *            The elements that were removed.
		 * @param added
		 *            The elements that were added.
		 * @param permutation
		 *            A mapping of prior indexes to current ones.
		 */
		public ElementarySubChange(Kind kind, int from, int to,
				List<? extends E> removed, List<? extends E> added,
				int[] permutation) {
			this.kind = kind;
			this.from = from;
			this.to = to;
			if (removed != null) {
				this.removed = new ArrayList<>(removed);
			}
			if (added != null) {
				this.added = new ArrayList<>(added);
			}
			if (permutation != null) {
				this.permutation = permutation;
			}
		}

		/**
		 * Returns the elements that were added by this change.
		 *
		 * @return The added elements.
		 */
		public List<E> getAdded() {
			if (added == null) {
				return Collections.emptyList();
			}
			return added;
		}

		/**
		 * Returns the index at which elements were added/removed/re-ordered.
		 *
		 * @return The start index.
		 */
		public int getFrom() {
			return from;
		}

		/**
		 * Returns the kind of change.
		 *
		 * @return The change kind.
		 */
		public Kind getKind() {
			return kind;
		}

		/**
		 * Returns a mapping of previous indexes to current ones
		 *
		 * @return An integer array representing a mapping of previous indexes
		 *         to current indexes.
		 */
		public int[] getPermutation() {
			if (permutation == null) {
				return new int[] {};
			}
			return permutation;
		}

		/**
		 * Returns the elements that were removed by this change.
		 *
		 * @return The removed elements.
		 */
		public List<E> getRemoved() {
			if (removed == null) {
				return Collections.emptyList();
			}
			return removed;
		}

		/**
		 * Returns the index up to which (excluding) elements were
		 * added/removed/re-ordered.
		 *
		 * @return The end index.
		 */
		public int getTo() {
			return to;
		}

		@Override
		public String toString() {
			if (Kind.REPLACE.equals(kind)) {
				return "Replaced" + getRemoved() + " by " + getAdded() + " at "
						+ getFrom() + ".";
			} else if (Kind.ADD.equals(kind)) {
				return "Added" + getAdded() + " at " + getFrom() + ".";
			} else if (Kind.REMOVE.equals(kind)) {
				return "Removed" + getRemoved() + " at " + getFrom() + ".";
			} else if (Kind.PERMUTATE.equals(kind)) {
				return "Permutated by " + Arrays.toString(getPermutation())
						+ ".";
			}
			return super.toString();
		}
	}

	/**
	 * Infers the elementary changes constituting the change of the
	 * {@link ObservableList}.
	 *
	 * @param <E>
	 *            The element type of the {@link ObservableList} that was
	 *            changed.
	 * @param change
	 *            The (atomic) change to infer elementary changes from.
	 * @return A list of elementary changes.
	 */
	protected static <E> List<ElementarySubChange<E>> getElementaryChanges(
			ListChangeListener.Change<? extends E> change) {
		List<ElementarySubChange<E>> elementarySubChanges = new ArrayList<>();
		while (change.next()) {
			if (change.wasReplaced()) {
				elementarySubChanges.add(ElementarySubChange.replaced(
						change.getRemoved(), change.getAddedSubList(),
						change.getFrom(), change.getTo()));
			} else if (change.wasRemoved()) {
				elementarySubChanges.add(ElementarySubChange.removed(
						change.getRemoved(), change.getFrom(), change.getTo()));
			} else if (change.wasAdded()) {
				elementarySubChanges.add(ElementarySubChange.added(
						new ArrayList<>(change.getAddedSubList()),
						change.getFrom(), change.getTo()));
			} else if (change.wasPermutated()) {
				// find permutation
				int[] permutation = CollectionUtils.getPermutation(change);
				elementarySubChanges.add(ElementarySubChange.<E> permutated(
						permutation, change.getFrom(), change.getTo()));
			}
		}
		change.reset();
		return elementarySubChanges;
	}

	private List<InvalidationListener> invalidationListeners = null;
	private boolean lockInvalidationListeners;
	private boolean lockListChangeListeners;
	private List<ListChangeListener<? super E>> listChangeListeners = null;
	private ObservableList<E> source;

	/**
	 * Constructs a new {@link ListListenerHelperEx} for the given source
	 * {@link ObservableList}.
	 *
	 * @param source
	 *            The {@link ObservableList} to use as source in change
	 *            notifications.
	 */
	public ListListenerHelperEx(ObservableList<E> source) {
		this.source = source;
	}

	/**
	 * Adds a new {@link InvalidationListener} to this
	 * {@link ListListenerHelperEx}. If the same listener is added more than
	 * once, it will be registered more than once and will receive multiple
	 * change events.
	 *
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(InvalidationListener listener) {
		if (invalidationListeners == null) {
			invalidationListeners = new ArrayList<>();
		}
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockInvalidationListeners) {
			invalidationListeners = new ArrayList<>(invalidationListeners);
		}
		invalidationListeners.add(listener);
	}

	/**
	 * Adds a new {@link ListChangeListener} to this
	 * {@link ListListenerHelperEx}. If the same listener is added more than
	 * once, it will be registered more than once and will receive multiple
	 * change events.
	 *
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(ListChangeListener<? super E> listener) {
		if (listChangeListeners == null) {
			listChangeListeners = new ArrayList<>();
		}
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockListChangeListeners) {
			listChangeListeners = new ArrayList<>(listChangeListeners);
		}
		listChangeListeners.add(listener);
	}

	/**
	 * Notifies all attached {@link InvalidationListener}s and
	 * {@link ListChangeListener}s about the change.
	 *
	 * @param change
	 *            The change to notify listeners about.
	 */
	public void fireValueChangedEvent(
			ListChangeListener.Change<? extends E> change) {
		notifyInvalidationListeners();
		if (change != null) {
			notifyListChangeListeners(change);
		}
	}

	/**
	 * Returns the source {@link ObservableList} this
	 * {@link ListListenerHelperEx} is bound to, which is used in change
	 * notifications.
	 *
	 * @return The source {@link ObservableList}.
	 */
	protected ObservableList<E> getSource() {
		return source;
	}

	/**
	 * Notifies all registered {@link InvalidationListener}s.
	 */
	protected void notifyInvalidationListeners() {
		if (invalidationListeners != null) {
			try {
				lockInvalidationListeners = true;
				for (InvalidationListener l : invalidationListeners) {
					try {
						l.invalidated(source);
					} catch (Exception e) {
						Thread.currentThread().getUncaughtExceptionHandler()
								.uncaughtException(Thread.currentThread(), e);
					}
				}
			} finally {
				lockInvalidationListeners = false;
			}
		}
	}

	/**
	 * Notifies the attached {@link ListChangeListener}s about the related
	 * change.
	 *
	 * @param change
	 *            The applied change.
	 */
	protected void notifyListChangeListeners(Change<? extends E> change) {
		if (listChangeListeners != null) {
			try {
				lockListChangeListeners = true;
				for (ListChangeListener<? super E> l : listChangeListeners) {
					change.reset();
					try {
						l.onChanged(change);
					} catch (Exception e) {
						Thread.currentThread().getUncaughtExceptionHandler()
								.uncaughtException(Thread.currentThread(), e);
					}
				}
			} finally {
				lockListChangeListeners = false;
			}
		}
	}

	/**
	 * Removes the given {@link InvalidationListener} from this
	 * {@link ListListenerHelperEx}. If its was registered more than once,
	 * removes one occurrence.
	 *
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(InvalidationListener listener) {
		if (invalidationListeners == null) {
			return;
		}

		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockInvalidationListeners) {
			invalidationListeners = new ArrayList<>(invalidationListeners);
		}

		// XXX: We have to ignore the hash code when removing listeners, as
		// otherwise unbinding will be broken (JavaFX bindings violate the
		// contract between equals() and hashCode(): JI-9028554); remove() may
		// thus not be used.
		for (Iterator<InvalidationListener> iterator = invalidationListeners
				.iterator(); iterator.hasNext();) {
			if (iterator.next().equals(listener)) {
				iterator.remove();
				break;
			}
		}
		if (invalidationListeners.isEmpty()) {
			invalidationListeners = null;
		}
	}

	/**
	 * Removes the given {@link ListChangeListener} from this
	 * {@link ListListenerHelperEx}. If its was registered more than once,
	 * removes one occurrence.
	 *
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(ListChangeListener<? super E> listener) {
		if (listChangeListeners == null) {
			return;
		}

		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockListChangeListeners) {
			listChangeListeners = new ArrayList<>(listChangeListeners);
		}
		// XXX: We have to ignore the hash code when removing listeners, as
		// otherwise unbinding will be broken (JavaFX bindings violate the
		// contract between equals() and hashCode(): JI-9028554); remove() may
		// thus not be used.
		for (Iterator<ListChangeListener<? super E>> iterator = listChangeListeners
				.iterator(); iterator.hasNext();) {
			if (iterator.next().equals(listener)) {
				iterator.remove();
				break;
			}
		}
		if (listChangeListeners.isEmpty()) {
			listChangeListeners = null;
		}
	}
}
