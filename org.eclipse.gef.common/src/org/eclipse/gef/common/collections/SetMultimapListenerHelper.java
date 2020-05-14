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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.common.collections.SetMultimapChangeListener.Change;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import javafx.beans.InvalidationListener;

/**
 * A utility class to support change notifications for an
 * {@link ObservableSetMultimap}.
 *
 * @author anyssen
 *
 * @param <K>
 *            The key type of the {@link ObservableSetMultimap}.
 * @param <V>
 *            The value type of the {@link ObservableSetMultimap}.
 *
 */
public class SetMultimapListenerHelper<K, V> {

	/**
	 * A simple implementation of an {@link SetMultimapChangeListener.Change}.
	 *
	 * @author anyssen
	 *
	 * @param <K>
	 *            The key type of the source {@link ObservableSetMultimap}.
	 * @param <V>
	 *            The value type of the source {@link ObservableSetMultimap}.
	 *
	 */
	public static class AtomicChange<K, V>
			extends SetMultimapChangeListener.Change<K, V> {

		private SetMultimap<K, V> previousContents;
		private ElementarySubChange<K, V>[] elementarySubChanges;
		private int cursor = -1;

		/**
		 * Creates a new {@link SetMultimapListenerHelper.AtomicChange} that
		 * represents a change comprising a single elementary sub-change.
		 *
		 * @param source
		 *            The source {@link ObservableSetMultimap} from which the
		 *            change originated.
		 * @param previousContents
		 *            The previous contents of the {@link ObservableSetMultimap}
		 *            before the change was applied.
		 * @param elementarySubChange
		 *            The elementary sub-change that has been applied.
		 */
		@SuppressWarnings("unchecked")
		public AtomicChange(ObservableSetMultimap<K, V> source,
				SetMultimap<K, V> previousContents,
				ElementarySubChange<K, V> elementarySubChange) {
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
		public AtomicChange(ObservableSetMultimap<K, V> source,
				SetMultimap<K, V> previousContents,
				List<ElementarySubChange<K, V>> elementarySubChanges) {
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
		public AtomicChange(ObservableSetMultimap<K, V> source,
				SetMultimapChangeListener.Change<? extends K, ? extends V> change) {
			super(source);

			// copy previous contents
			this.previousContents = HashMultimap
					.create(change.getPreviousContents());
			// retrieve elementary sub-changes by iterating them
			// TODO: we could introduce an initialized field inside Change
			// already, so we could check the passed in change is not already
			// initialized
			List<ElementarySubChange<K, V>> elementarySubChanges = new ArrayList<>();
			while (change.next()) {
				elementarySubChanges.add(new ElementarySubChange<K, V>(
						change.getKey(), change.getValuesRemoved(),
						change.getValuesAdded()));
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
		public K getKey() {
			checkCursor();
			return elementarySubChanges[cursor].getKey();
		}

		@Override
		public SetMultimap<K, V> getPreviousContents() {
			return Multimaps.unmodifiableSetMultimap(previousContents);
		}

		@Override
		public Set<V> getValuesAdded() {
			checkCursor();
			return elementarySubChanges[cursor].getValuesAdded();
		}

		@Override
		public Set<V> getValuesRemoved() {
			checkCursor();
			return elementarySubChanges[cursor].getValuesRemoved();
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
			checkCursor();
			return elementarySubChanges[cursor].wasAdded();
		}

		@Override
		public boolean wasRemoved() {
			checkCursor();
			return elementarySubChanges[cursor].wasRemoved();
		}

	}

	/**
	 * An elementary change related to a single key of a
	 * {@link ObservableSetMultimap}. .
	 *
	 * @author anyssen
	 *
	 * @param <K>
	 *            The key type of the {@link ObservableSetMultimap}.
	 * @param <V>
	 *            The value type of the {@link ObservableSetMultimap}.
	 */
	public static class ElementarySubChange<K, V> {

		private K key = null;
		private Set<V> removedValues;
		private Set<V> addedValues;

		/**
		 * Constructs a new
		 * {@link SetMultimapListenerHelper.ElementarySubChange} with the given
		 * values.
		 *
		 * @param key
		 *            The key to which the change is related.
		 * @param removedValues
		 *            The values removed by the change.
		 * @param addedValues
		 *            The values added by the change.
		 */
		public ElementarySubChange(K key, Set<? extends V> removedValues,
				Set<? extends V> addedValues) {
			this.key = key;
			this.removedValues = new HashSet<>(removedValues);
			this.addedValues = new HashSet<>(addedValues);
		}

		/**
		 * Returns the key that was modified in this elementary sub-change, i.e.
		 * for which values were added or removed.
		 *
		 * @return The key this elementary sub-change is related to.
		 */
		public K getKey() {
			return key;
		}

		/**
		 * Returns the values added by this elementary sub-change.
		 *
		 * @return The values that were added by this elementary sub-change, if
		 *         any. Will return an empty set in case no elements were added.
		 */
		public Set<V> getValuesAdded() {
			return addedValues;
		}

		/**
		 * Returns the values removed by this elementary sub-change.
		 *
		 * @return The values that were removed by this elementary sub-change,
		 *         if any. Will return an empty set in case no elements were
		 *         removed.
		 */
		public Set<V> getValuesRemoved() {
			return removedValues;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			if (wasAdded()) {
				if (wasRemoved()) {
					builder.append("Replaced ").append(removedValues)
							.append(" by ").append(addedValues);
				} else {
					builder.append("Added ").append(addedValues);
				}
			} else {
				builder.append("Removed ").append(removedValues);
			}
			builder.append(" for key ").append(key).append(".");
			return builder.toString();
		}

		/**
		 * Indicates whether values were added by this elementary sub-change.
		 *
		 * @return <code>true</code> if values were added, <code>false</code>
		 *         otherwise.
		 */
		public boolean wasAdded() {
			return !addedValues.isEmpty();
		}

		/**
		 * Indicates whether values were removed by this elementary sub-change.
		 *
		 * @return <code>true</code> if values were removed, <code>false</code>
		 *         otherwise.
		 */
		public boolean wasRemoved() {
			return !removedValues.isEmpty();
		}
	}

	private List<InvalidationListener> invalidationListeners = null;
	private List<SetMultimapChangeListener<? super K, ? super V>> setMultimapChangeListeners = null;
	private ObservableSetMultimap<K, V> source;
	private boolean lockInvalidationListeners;
	private boolean lockSetMultimapChangeListeners;

	/**
	 * Constructs a new {@link SetMultimapListenerHelper} for the given source
	 * {@link ObservableSetMultimap}.
	 *
	 * @param source
	 *            The {@link ObservableSetMultimap} to use as source in change
	 *            notifications.
	 */
	public SetMultimapListenerHelper(ObservableSetMultimap<K, V> source) {
		this.source = source;
	}

	/**
	 * Adds a new {@link InvalidationListener} to this
	 * {@link SetMultimapListenerHelper}. If the same listener is added more
	 * than once, it will be registered more than once and will receive multiple
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
	 * {@link SetMultimapListenerHelper}. If the same listener is added more
	 * than once, it will be registered more than once and will receive multiple
	 * change events.
	 *
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(
			SetMultimapChangeListener<? super K, ? super V> listener) {
		if (setMultimapChangeListeners == null) {
			setMultimapChangeListeners = new ArrayList<>();
		}
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockSetMultimapChangeListeners) {
			setMultimapChangeListeners = new ArrayList<>(
					setMultimapChangeListeners);
		}
		setMultimapChangeListeners.add(listener);
	}

	/**
	 * Notifies all attached {@link InvalidationListener}s and
	 * {@link SetMultimapChangeListener}s about the change.
	 *
	 * @param change
	 *            The change to notify listeners about.
	 */
	public void fireValueChangedEvent(
			SetMultimapChangeListener.Change<? extends K, ? extends V> change) {
		notifyInvalidationListeners();
		if (change != null) {
			notifySetMultimapChangeListeners(change);
		}
	}

	/**
	 * Returns the source {@link ObservableSetMultimap} this
	 * {@link SetMultimapListenerHelper} is bound to, which is used in change
	 * notifications.
	 *
	 * @return The source {@link ObservableSetMultimap}.
	 */
	protected ObservableSetMultimap<K, V> getSource() {
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
	 * Notifies the attached {@link SetMultimapChangeListener}s about the
	 * related change.
	 *
	 * @param change
	 *            The applied change.
	 */
	protected void notifySetMultimapChangeListeners(
			Change<? extends K, ? extends V> change) {
		if (setMultimapChangeListeners != null) {
			try {
				lockSetMultimapChangeListeners = true;
				for (SetMultimapChangeListener<? super K, ? super V> l : setMultimapChangeListeners) {
					change.reset();
					try {
						l.onChanged(change);
					} catch (Exception e) {
						Thread.currentThread().getUncaughtExceptionHandler()
								.uncaughtException(Thread.currentThread(), e);
					}
				}
			} finally {
				lockSetMultimapChangeListeners = false;
			}
		}
	}

	/**
	 * Removes the given {@link InvalidationListener} from this
	 * {@link SetMultimapListenerHelper}. If its was registered more than once,
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
	 * Removes the given {@link SetMultimapChangeListener} from this
	 * {@link SetMultimapListenerHelper}. If its was registered more than once,
	 * removes one occurrence.
	 *
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(
			SetMultimapChangeListener<? super K, ? super V> listener) {
		if (setMultimapChangeListeners == null) {
			return;
		}

		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockSetMultimapChangeListeners) {
			setMultimapChangeListeners = new ArrayList<>(
					setMultimapChangeListeners);
		}
		// XXX: We have to ignore the hash code when removing listeners, as
		// otherwise unbinding will be broken (JavaFX bindings violate the
		// contract between equals() and hashCode(): JI-9028554); remove() may
		// thus not be used.
		for (Iterator<SetMultimapChangeListener<? super K, ? super V>> iterator = setMultimapChangeListeners
				.iterator(); iterator.hasNext();) {
			if (iterator.next().equals(listener)) {
				iterator.remove();
				break;
			}
		}
		if (setMultimapChangeListeners.isEmpty()) {
			setMultimapChangeListeners = null;
		}
	}
}
