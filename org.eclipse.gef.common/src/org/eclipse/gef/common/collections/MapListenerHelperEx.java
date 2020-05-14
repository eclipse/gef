/******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
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

import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableMap;

/**
 * A utility class to support change notifications for an {@link ObservableMap}
 * , replacing the JavaFX-internal {@code MapChangeListener} helper class.
 *
 * @author anyssen
 *
 * @param <K>
 *            The key type of the {@link ObservableMap}.
 * @param <V>
 *            The value type of the {@link ObservableMap}.
 *
 */
public class MapListenerHelperEx<K, V> {

	/**
	 * A simple implementation of an
	 * {@link javafx.collections.MapChangeListener.Change}.
	 *
	 * @author anyssen
	 *
	 * @param <K>
	 *            The key type of the source {@link ObservableMap}.
	 * @param <V>
	 *            The value type of the source {@link ObservableMap}.
	 *
	 */
	public static class AtomicChange<K, V>
			extends MapChangeListener.Change<K, V> {

		private K key;
		private V removedValue;
		private V addedValue;

		/**
		 * Creates a new {@link MapListenerHelperEx.AtomicChange} that
		 * represents a change comprising a single elementary sub-change.
		 *
		 * @param source
		 *            The source {@link ObservableMap} from which the change
		 *            originated.
		 * @param key
		 *            The key to which the change is related.
		 * @param removedValue
		 *            The value that was removed by this change or
		 *            <code>null</code> if no value was removed.
		 * @param addedValue
		 *            The value that was added by this change or
		 *            <code>null</code> if no value was added.
		 */
		public AtomicChange(ObservableMap<K, V> source, K key, V removedValue,
				V addedValue) {
			super(source);
			this.key = key;
			this.removedValue = removedValue;
			this.addedValue = addedValue;
		}

		/**
		 * Creates a new {@link MapListenerHelperEx.AtomicChange} for the passed
		 * in source, based on the data provided in the passed-in change.
		 * <p>
		 * This is basically used to allow properties wrapping an
		 * {@link ObservableMap} to re-fire change events of their wrapped
		 * {@link ObservableMap} with themselves as source.
		 *
		 * @param source
		 *            The new source {@link ObservableMap}.
		 * @param change
		 *            The change to infer a new change from. It is expected that
		 *            the change is in initial state. In either case it will be
		 *            reset to initial state.
		 */
		public AtomicChange(ObservableMap<K, V> source,
				MapChangeListener.Change<? extends K, ? extends V> change) {
			super(source);

			this.key = change.getKey();
			this.addedValue = change.getValueAdded();
			this.removedValue = change.getValueRemoved();
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValueAdded() {
			return addedValue;
		}

		@Override
		public V getValueRemoved() {
			return removedValue;
		}

		@Override
		public String toString() {
			if (wasAdded()) {
				if (wasRemoved()) {
					return "Replaced " + removedValue + " by " + addedValue
							+ " for key " + key + ".";
				}
				return "Added " + addedValue + " for key " + key + ".";
			} else {
				return "Removed " + removedValue + " for key " + key + ".";
			}
		}

		@Override
		public boolean wasAdded() {
			return addedValue != null;
		}

		@Override
		public boolean wasRemoved() {
			return removedValue != null;
		}
	}

	private List<InvalidationListener> invalidationListeners = null;
	private boolean lockInvalidationListeners;
	private boolean lockMapChangeListeners;
	private List<MapChangeListener<? super K, ? super V>> mapChangeListeners = null;
	private ObservableMap<K, V> source;

	/**
	 * Constructs a new {@link MapListenerHelperEx} for the given source
	 * {@link ObservableMap}.
	 *
	 * @param source
	 *            The {@link ObservableMap} to use as source in change
	 *            notifications.
	 */
	public MapListenerHelperEx(ObservableMap<K, V> source) {
		this.source = source;
	}

	/**
	 * Adds a new {@link InvalidationListener} to this
	 * {@link MapListenerHelperEx}. If the same listener is added more than
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
	 * Adds a new {@link MapChangeListener} to this {@link MapListenerHelperEx}.
	 * If the same listener is added more than once, it will be registered more
	 * than once and will receive multiple change events.
	 *
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(MapChangeListener<? super K, ? super V> listener) {
		if (mapChangeListeners == null) {
			mapChangeListeners = new ArrayList<>();
		}
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockMapChangeListeners) {
			mapChangeListeners = new ArrayList<>(mapChangeListeners);
		}
		mapChangeListeners.add(listener);
	}

	/**
	 * Notifies all attached {@link InvalidationListener}s and
	 * {@link MapChangeListener}s about the change.
	 *
	 * @param change
	 *            The change to notify listeners about.
	 */
	public void fireValueChangedEvent(
			MapChangeListener.Change<? extends K, ? extends V> change) {
		notifyInvalidationListeners();
		if (change != null) {
			notifyMapChangeListeners(change);
		}
	}

	/**
	 * Returns the source {@link ObservableMap} this {@link MapListenerHelperEx}
	 * is bound to, which is used in change notifications.
	 *
	 * @return The source {@link ObservableMap}.
	 */
	protected ObservableMap<K, V> getSource() {
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
	 * Notifies the attached {@link MapChangeListener}s about the related
	 * change.
	 *
	 * @param change
	 *            The applied change.
	 */
	protected void notifyMapChangeListeners(
			Change<? extends K, ? extends V> change) {
		if (mapChangeListeners != null && !mapChangeListeners.isEmpty()) {
			// if (lockMapChangeListeners) {
			// throw new IllegalStateException("Re-entrant map change!");
			// }
			try {
				lockMapChangeListeners = true;
				for (MapChangeListener<? super K, ? super V> l : mapChangeListeners) {
					try {
						l.onChanged(change);
					} catch (Exception e) {
						// System.out.println("Exception in listener: " +
						// e.getMessage() + ", cause=" + e.getCause());
						Thread.currentThread().getUncaughtExceptionHandler()
								.uncaughtException(Thread.currentThread(), e);
					}
				}
			} finally {
				lockMapChangeListeners = false;
			}
		}
	}

	/**
	 * Removes the given {@link InvalidationListener} from this
	 * {@link MapListenerHelperEx}. If its was registered more than once,
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
	 * Removes the given {@link MapChangeListener} from this
	 * {@link MapListenerHelperEx}. If its was registered more than once,
	 * removes one occurrence.
	 *
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(
			MapChangeListener<? super K, ? super V> listener) {
		if (mapChangeListeners == null) {
			return;
		}

		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockMapChangeListeners) {
			mapChangeListeners = new ArrayList<>(mapChangeListeners);
		}
		// XXX: We have to ignore the hash code when removing listeners, as
		// otherwise unbinding will be broken (JavaFX bindings violate the
		// contract between equals() and hashCode(): JI-9028554); remove() may
		// thus not be used.
		for (Iterator<MapChangeListener<? super K, ? super V>> iterator = mapChangeListeners
				.iterator(); iterator.hasNext();) {
			if (iterator.next().equals(listener)) {
				iterator.remove();
				break;
			}
		}
		if (mapChangeListeners.isEmpty()) {
			mapChangeListeners = null;
		}
	}
}
