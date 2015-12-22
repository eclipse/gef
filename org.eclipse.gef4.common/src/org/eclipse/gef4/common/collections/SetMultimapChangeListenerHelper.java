/******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.gef4.common.collections.SetMultimapChangeListener.Change;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

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
public class SetMultimapChangeListenerHelper<K, V> {

	/**
	 * A simple implementation of
	 * {@link org.eclipse.gef4.common.collections.SetMultimapChangeListener.Change}
	 * .
	 * 
	 * @author anyssen
	 *
	 * @param <K>
	 *            The key type of the {@link ObservableSetMultimap}.
	 * @param <V>
	 *            The value type of the {@link ObservableSetMultimap}.
	 */
	public static class SimpleChange<K, V>
			extends SetMultimapChangeListener.Change<K, V> {

		private K key = null;
		private Set<V> removedValues;
		private Set<V> addedValues;

		/**
		 * Constructs a new
		 * {@link org.eclipse.gef4.common.collections.SetMultimapChangeListenerHelper.SimpleChange}
		 * with the given values.
		 * 
		 * @param source
		 *            The source {@link ObservableSetMultimap} from which the
		 *            change resulted.
		 * @param key
		 *            The key to which the change is related.
		 * @param removedValues
		 *            The values removed by the change.
		 * @param addedValues
		 *            The values added by the change.
		 */
		public SimpleChange(ObservableSetMultimap<K, V> source, K key,
				Set<V> removedValues, Set<V> addedValues) {
			super(source);
			this.key = key;
			this.removedValues = new HashSet<>(removedValues);
			this.addedValues = new HashSet<>(addedValues);
		}

		/**
		 * Constructs a new
		 * {@link org.eclipse.gef4.common.collections.SetMultimapChangeListenerHelper.SimpleChange}
		 * based on the values of the passed in {@link Change}.
		 * 
		 * @param source
		 *            The source {@link ObservableSetMultimap} from which the
		 *            change resulted.
		 * @param change
		 *            The {@link Change} which provides the changed values.
		 */
		public SimpleChange(ObservableSetMultimap<K, V> source,
				SetMultimapChangeListener.Change<? extends K, ? extends V> change) {
			this(source, change.getKey(),
					new HashSet<>(change.getValuesRemoved()),
					new HashSet<>(change.getValuesAdded()));
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public Set<V> getValuesAdded() {
			return addedValues;
		}

		@Override
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

		@Override
		public boolean wasAdded() {
			return !addedValues.isEmpty();
		}

		@Override
		public boolean wasRemoved() {
			return !removedValues.isEmpty();
		}
	}

	private Multiset<InvalidationListener> invalidationListeners = null;
	private Multiset<SetMultimapChangeListener<? super K, ? super V>> setMultimapChangeListeners = null;
	private ObservableSetMultimap<K, V> source;
	private boolean lockInvalidationListeners;
	private boolean lockSetMultimapChangeListeners;

	/**
	 * Constructs a new {@link SetMultimapChangeListener} for the given source
	 * {@link ObservableSetMultimap}.
	 * 
	 * @param source
	 *            The {@link ObservableSetMultimap} to use as source in change
	 *            notifications.
	 */
	public SetMultimapChangeListenerHelper(ObservableSetMultimap<K, V> source) {
		this.source = source;
	}

	/**
	 * Returns the source {@link ObservableSetMultimap} this
	 * {@link SetMultimapChangeListenerHelper} is bound to, which is used in
	 * change notifications.
	 * 
	 * @return The source {@link ObservableSetMultimap}.
	 */
	protected ObservableSetMultimap<K, V> getSource() {
		return source;
	}

	/**
	 * Adds a new {@link InvalidationListener} to this
	 * {@link SetMultimapChangeListenerHelper}. If the same listener is added
	 * more than once, it will be registered more than once and will receive
	 * multiple change events.
	 * 
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(InvalidationListener listener) {
		if (invalidationListeners == null) {
			invalidationListeners = HashMultiset.create();
		}
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockInvalidationListeners) {
			invalidationListeners = HashMultiset.create(invalidationListeners);
		}
		invalidationListeners.add(listener);
	}

	/**
	 * Adds a new {@link SetMultimapChangeListener} to this
	 * {@link SetMultimapChangeListenerHelper}. If the same listener is added
	 * more than once, it will be registered more than once and will receive
	 * multiple change events.
	 * 
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(
			SetMultimapChangeListener<? super K, ? super V> listener) {
		if (setMultimapChangeListeners == null) {
			setMultimapChangeListeners = HashMultiset.create();
		}
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockSetMultimapChangeListeners) {
			setMultimapChangeListeners = HashMultiset
					.create(setMultimapChangeListeners);
		}
		setMultimapChangeListeners.add(listener);
	}

	/**
	 * Notifies all registered {@link InvalidationListener}s.
	 */
	protected void notifyInvalidationListeners() {
		if (invalidationListeners != null) {
			try {
				lockInvalidationListeners = true;
				for (InvalidationListener l : invalidationListeners) {
					l.invalidated(source);
				}
			} finally {
				lockInvalidationListeners = false;
			}
		}
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
			notifySetMultimapListeners(change);
		}
	}

	/**
	 * Notifies the attached {@link SetMultimapChangeListener}s about the
	 * related change.
	 * 
	 * @param change
	 *            The applied change.
	 */
	protected void notifySetMultimapListeners(
			Change<? extends K, ? extends V> change) {
		if (setMultimapChangeListeners != null) {
			try {
				lockSetMultimapChangeListeners = true;
				for (SetMultimapChangeListener<? super K, ? super V> l : setMultimapChangeListeners) {
					l.onChanged(change);
				}
			} finally {
				lockSetMultimapChangeListeners = false;
			}
		}
	}

	/**
	 * Removes the given {@link InvalidationListener} from this
	 * {@link SetMultimapChangeListenerHelper}. If its was registered more than
	 * once, removes one occurrence.
	 * 
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(InvalidationListener listener) {
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockInvalidationListeners) {
			invalidationListeners = HashMultiset.create(invalidationListeners);
		}

		// XXX: We have to ignore the hash code when removing listeners, as
		// otherwise unbinding will be broken (JavaFX bindings violate the
		// contract between equals() and hashCode()); remove() may thus not be
		// used.
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
	 * {@link SetMultimapChangeListenerHelper}. If its was registered more than
	 * once, removes one occurrence.
	 * 
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(
			SetMultimapChangeListener<? super K, ? super V> listener) {
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockSetMultimapChangeListeners) {
			setMultimapChangeListeners = HashMultiset
					.create(setMultimapChangeListeners);
		}
		// XXX: We have to ignore the hash code when removing listeners, as
		// otherwise unbinding will be broken (JavaFX bindings violate the
		// contract between equals() and hashCode()); remove() may thus not be
		// used.
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
