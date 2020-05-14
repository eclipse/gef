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
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.common.collections.MultisetChangeListener.Change;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import javafx.beans.InvalidationListener;

/**
 * A utility class to support change notifications for an
 * {@link ObservableMultiset}.
 *
 * @author anyssen
 *
 * @param <E>
 *            The element type of the {@link ObservableMultiset}.
 *
 */
public class MultisetListenerHelper<E> {

	/**
	 * A simple implementation of an {@link MultisetChangeListener.Change}.
	 *
	 * @author anyssen
	 *
	 * @param <E>
	 *            The element type of the source {@link ObservableMultiset}.
	 */
	public static class AtomicChange<E>
			extends MultisetChangeListener.Change<E> {

		private int cursor = -1;
		private ElementarySubChange<E>[] elementarySubChanges;
		private Multiset<E> previousContents;

		/**
		 * Creates a new {@link MultisetListenerHelper.AtomicChange} that
		 * represents a change comprising a single elementary sub-change.
		 *
		 * @param source
		 *            The source {@link ObservableMultiset} from which the
		 *            change originated.
		 * @param previousContents
		 *            The previous contents of the {@link ObservableMultiset}
		 *            before the change was applied.
		 * @param elementarySubChange
		 *            The elementary sub-change that has been applied.
		 */
		@SuppressWarnings("unchecked")
		public AtomicChange(ObservableMultiset<E> source,
				Multiset<E> previousContents,
				ElementarySubChange<E> elementarySubChange) {
			super(source);
			this.previousContents = previousContents;
			this.elementarySubChanges = new ElementarySubChange[] {
					elementarySubChange };
		}

		/**
		 * Creates a new {@link MultisetListenerHelper.AtomicChange} that
		 * represents a change comprising multiple elementary sub-changesO.
		 *
		 * @param source
		 *            The source {@link ObservableMultiset} from which the
		 *            change originated.
		 * @param previousContents
		 *            The previous contents of the {@link ObservableMultiset}
		 *            before the change was applied.
		 * @param elementarySubChanges
		 *            The elementary sub-changes that have been applied as part
		 *            of this change.
		 */
		@SuppressWarnings("unchecked")
		public AtomicChange(ObservableMultiset<E> source,
				Multiset<E> previousContents,
				List<ElementarySubChange<E>> elementarySubChanges) {
			super(source);
			this.previousContents = previousContents;
			this.elementarySubChanges = elementarySubChanges
					.toArray(new ElementarySubChange[] {});
		}

		/**
		 * Creates a new {@link MultisetListenerHelper.AtomicChange} for the
		 * passed in source, based on the data provided in the passed-in change.
		 * <p>
		 * This is basically used to allow properties wrapping an
		 * {@link ObservableMultiset} to re-fire change events of their wrapped
		 * {@link ObservableMultiset} with themselves as source.
		 *
		 * @param source
		 *            The new source {@link ObservableMultiset}.
		 * @param change
		 *            The change to infer a new change from. It is expected that
		 *            the change is in initial state. In either case it will be
		 *            reset to initial state.
		 */
		@SuppressWarnings("unchecked")
		public AtomicChange(ObservableMultiset<E> source,
				MultisetChangeListener.Change<? extends E> change) {
			super(source);

			// copy previous contents
			this.previousContents = HashMultiset
					.create(change.getPreviousContents());

			// retrieve elementary sub-changes by iterating them
			// TODO: we could introduce an initialized field inside Change
			// already, so we could check the passed in change is not already
			// initialized
			List<ElementarySubChange<E>> elementarySubChanges = new ArrayList<>();
			while (change.next()) {
				elementarySubChanges
						.add(new ElementarySubChange<E>(change.getElement(),
								change.getRemoveCount(), change.getAddCount()));
			}
			change.reset();
			this.elementarySubChanges = elementarySubChanges
					.toArray(new ElementarySubChange[] {});
		}

		private void checkCursor() {
			String methodName = Thread.currentThread().getStackTrace()[2]
					.getMethodName();
			if (cursor == -1) {
				throw new IllegalStateException("Need to call next() before "
						+ methodName + "() can be called.");
			} else if (cursor >= elementarySubChanges.length) {
				throw new IllegalStateException("May only call " + methodName
						+ "() if next() returned true.");
			}
		}

		@Override
		public int getAddCount() {
			checkCursor();
			return elementarySubChanges[cursor].getAddCount();
		}

		@Override
		public E getElement() {
			checkCursor();
			return elementarySubChanges[cursor].getElement();
		}

		@Override
		public Multiset<E> getPreviousContents() {
			return Multisets.unmodifiableMultiset(previousContents);
		}

		@Override
		public int getRemoveCount() {
			checkCursor();
			return elementarySubChanges[cursor].getRemoveCount();
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
	}

	/**
	 * An elementary change related to a single element of a {@link Multiset}.
	 *
	 * @param <E>
	 *            The element type of the {@link ObservableMultiset}.
	 */
	public static class ElementarySubChange<E> {

		private int addCount;
		private E element;
		private int removeCount;

		/**
		 * Constructs a new elementary sub-change with the given values.
		 *
		 * @param element
		 *            The element that was added or removed.
		 * @param removeCount
		 *            The number of occurrences that were removed.
		 * @param addCount
		 *            The number of occurrences that were added.
		 */
		public ElementarySubChange(E element, int removeCount, int addCount) {
			this.element = element;
			this.removeCount = removeCount;
			this.addCount = addCount;
		}

		/**
		 * Returns the number of occurrences that have been added for the
		 * respective element as part of this elementary sub-change.
		 *
		 * @return The number of added occurrences.
		 */
		public int getAddCount() {
			return addCount;
		}

		/**
		 * Returns the element that has been altered by this elementary
		 * sub-change.
		 *
		 * @return The changed element.
		 */
		public E getElement() {
			return element;
		}

		/**
		 * Returns the number of occurrences that have been removed for the
		 * respective element as part of this elementary sub-change.
		 *
		 * @return The number of removed occurrences.
		 */
		public int getRemoveCount() {
			return removeCount;
		}

		@Override
		public String toString() {
			if (addCount > 0) {
				return "Added " + addCount + " occurrences of " + element + ".";
			} else {
				return "Removed " + removeCount + " occurrences of " + element
						+ ".";
			}
		}
	}

	private List<InvalidationListener> invalidationListeners = null;
	private boolean lockInvalidationListeners;
	private boolean lockMultisetChangeListeners;
	private List<MultisetChangeListener<? super E>> multisetChangeListeners = null;
	private ObservableMultiset<E> source;

	/**
	 * Constructs a new {@link MultisetListenerHelper} for the given source
	 * {@link ObservableMultiset}.
	 *
	 * @param source
	 *            The {@link ObservableMultiset} to use as source in change
	 *            notifications.
	 */
	public MultisetListenerHelper(ObservableMultiset<E> source) {
		this.source = source;
	}

	/**
	 * Adds a new {@link InvalidationListener} to this
	 * {@link MultisetListenerHelper}. If the same listener is added more than
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
	 * Adds a new {@link SetMultimapChangeListener} to this
	 * {@link MultisetListenerHelper}. If the same listener is added more than
	 * once, it will be registered more than once and will receive multiple
	 * change events.
	 *
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(MultisetChangeListener<? super E> listener) {
		if (multisetChangeListeners == null) {
			multisetChangeListeners = new ArrayList<>();
		}
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockMultisetChangeListeners) {
			multisetChangeListeners = new ArrayList<>(multisetChangeListeners);
		}
		multisetChangeListeners.add(listener);
	}

	/**
	 * Notifies all attached {@link InvalidationListener}s and
	 * {@link MultisetChangeListener}s about the change.
	 *
	 * @param change
	 *            The change to notify listeners about.
	 */
	public void fireValueChangedEvent(
			MultisetChangeListener.Change<? extends E> change) {
		notifyInvalidationListeners();
		if (change != null) {
			notifyMultisetChangeListeners(change);
		}
	}

	/**
	 * Returns the source {@link ObservableMultiset} this
	 * {@link MultisetListenerHelper} is bound to, which is used in change
	 * notifications.
	 *
	 * @return The source {@link ObservableMultiset}.
	 */
	protected ObservableMultiset<E> getSource() {
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
	 * Notifies the attached {@link MultisetChangeListener}s about the related
	 * change.
	 *
	 * @param change
	 *            The applied change.
	 */
	protected void notifyMultisetChangeListeners(Change<? extends E> change) {
		if (multisetChangeListeners != null) {
			try {
				lockMultisetChangeListeners = true;
				for (MultisetChangeListener<? super E> l : multisetChangeListeners) {
					change.reset();
					try {
						l.onChanged(change);
					} catch (Exception e) {
						Thread.currentThread().getUncaughtExceptionHandler()
								.uncaughtException(Thread.currentThread(), e);
					}
				}
			} finally {
				lockMultisetChangeListeners = false;
			}
		}
	}

	/**
	 * Removes the given {@link InvalidationListener} from this
	 * {@link MultisetListenerHelper}. If its was registered more than once,
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
	 * Removes the given {@link MultisetChangeListener} from this
	 * {@link MultisetListenerHelper}. If its was registered more than once,
	 * removes one occurrence.
	 *
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(MultisetChangeListener<? super E> listener) {
		if (multisetChangeListeners == null) {
			return;
		}

		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockMultisetChangeListeners) {
			multisetChangeListeners = new ArrayList<>(multisetChangeListeners);
		}
		// XXX: We have to ignore the hash code when removing listeners, as
		// otherwise unbinding will be broken (JavaFX bindings violate the
		// contract between equals() and hashCode(): JI-9028554); remove() may
		// thus not be used.
		for (Iterator<MultisetChangeListener<? super E>> iterator = multisetChangeListeners
				.iterator(); iterator.hasNext();) {
			if (iterator.next().equals(listener)) {
				iterator.remove();
				break;
			}
		}
		if (multisetChangeListeners.isEmpty()) {
			multisetChangeListeners = null;
		}
	}
}
