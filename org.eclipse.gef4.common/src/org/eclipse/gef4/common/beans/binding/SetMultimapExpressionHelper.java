/******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.common.beans.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef4.common.beans.value.ObservableSetMultimapValue;
import org.eclipse.gef4.common.collections.ObservableSetMultimap;
import org.eclipse.gef4.common.collections.SetMultimapChangeListener;
import org.eclipse.gef4.common.collections.SetMultimapChangeListenerHelper;

import com.google.common.collect.Multimaps;

import javafx.beans.value.ChangeListener;

/**
 * A utility class to support change notifications for an
 * {@link ObservableSetMultimap}.
 *
 * @author anyssen
 * @param <K>
 *            The key type of the {@link ObservableSetMultimap}.
 * @param <V>
 *            The value type of the {@link ObservableSetMultimap}.
 *
 */
public class SetMultimapExpressionHelper<K, V>
		extends SetMultimapChangeListenerHelper<K, V> {

	private List<ChangeListener<? super ObservableSetMultimap<K, V>>> changeListeners = null;

	private ObservableSetMultimapValue<K, V> observableValue = null;
	private ObservableSetMultimap<K, V> currentValue = null;

	private boolean lockChangeListeners;

	/**
	 * Constructs a new {@link SetMultimapExpressionHelper} for the given source
	 * {@link ObservableSetMultimapValue}.
	 *
	 * @param observableValue
	 *            The observableValue {@link ObservableSetMultimap}, which is
	 *            used in change notifications.
	 */
	public SetMultimapExpressionHelper(
			ObservableSetMultimapValue<K, V> observableValue) {
		super(observableValue);
		this.observableValue = observableValue;
		this.currentValue = observableValue.getValue();
	}

	/**
	 * Adds a new {@link ChangeListener} to this
	 * {@link SetMultimapExpressionHelper}. If the same listener is added more
	 * than once, it will be registered more than once and will receive multiple
	 * change events.
	 *
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(
			ChangeListener<? super ObservableSetMultimap<K, V>> listener) {
		if (changeListeners == null) {
			changeListeners = new ArrayList<>();
		}
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockChangeListeners) {
			changeListeners = new ArrayList<>(changeListeners);
		}
		changeListeners.add(listener);
	}

	/**
	 * Fires notifications to all attached
	 * {@link javafx.beans.InvalidationListener InvalidationListeners},
	 * {@link javafx.beans.value.ChangeListener ChangeListeners}, and
	 * {@link SetMultimapChangeListener SetMultimapChangeListeners}.
	 *
	 */
	public void fireValueChangedEvent() {
		final ObservableSetMultimap<K, V> oldValue = currentValue;
		currentValue = observableValue.getValue();
		notifyListeners(oldValue, currentValue);
	}

	/**
	 * Fires notifications to all attached
	 * {@link javafx.beans.InvalidationListener InvalidationListeners},
	 * {@link javafx.beans.value.ChangeListener ChangeListeners}, and
	 * {@link SetMultimapChangeListener SetMultimapChangeListeners}.
	 *
	 * @param change
	 *            The change that needs to be propagated.
	 */
	@Override
	public void fireValueChangedEvent(
			SetMultimapChangeListener.Change<? extends K, ? extends V> change) {
		notifyInvalidationListeners();
		if (change != null) {
			// FIXME: For ListPropertyBase, firing change events in case the
			// underlying observable list changes is reported as a bug, see
			// https://bugs.openjdk.java.net/browse/JDK-8089169; While it would
			// be cleaner, this corresponds to the current behavior of other
			// JavaFX collection-related properties.
			if (changeListeners != null) {
				try {
					lockChangeListeners = true;
					for (ChangeListener<? super ObservableSetMultimap<K, V>> l : changeListeners) {
						l.changed(observableValue, currentValue, currentValue);
					}
				} finally {
					lockChangeListeners = false;
				}
			}
			notifySetMultimapListeners(
					new SimpleChange<>(observableValue, change));
		}
	}

	private void notifyListeners(ObservableSetMultimap<K, V> oldValue,
			ObservableSetMultimap<K, V> currentValue) {
		notifyInvalidationListeners();
		if (currentValue != oldValue) {
			if (changeListeners != null) {
				try {
					lockChangeListeners = true;
					for (ChangeListener<? super ObservableSetMultimap<K, V>> l : changeListeners) {
						l.changed(observableValue, oldValue, currentValue);
					}
				} finally {
					lockChangeListeners = false;
				}
			}
			notifySetMultimapListeners(oldValue, currentValue);
		}
	}

	private void notifySetMultimapListeners(K key, Set<V> removedValues,
			Set<V> addedValues) {
		notifySetMultimapListeners(new SimpleChange<>(getSource(), key,
				removedValues, addedValues));
	}

	private void notifySetMultimapListeners(
			ObservableSetMultimap<K, V> oldValue,
			ObservableSetMultimap<K, V> currentValue) {
		if (currentValue == null) {
			// all entries have been removed
			for (Map.Entry<K, Set<V>> entries : Multimaps.asMap(oldValue)
					.entrySet()) {
				notifySetMultimapListeners(entries.getKey(), entries.getValue(),
						Collections.<V> emptySet());
			}
		} else if (oldValue == null) {
			// all entries have been added
			for (Map.Entry<K, Set<V>> entries : Multimaps.asMap(currentValue)
					.entrySet()) {
				notifySetMultimapListeners(entries.getKey(),
						Collections.<V> emptySet(), entries.getValue());
			}
		} else {
			// compute changed/removed values
			for (Map.Entry<K, Set<V>> entries : Multimaps.asMap(oldValue)
					.entrySet()) {
				K key = entries.getKey();
				Set<V> oldValues = entries.getValue();
				if (currentValue.containsKey(key)) {
					Set<V> newValues = currentValue.get(key);
					// compute add/removed values
					Set<V> addedValues = new HashSet<>(newValues);
					addedValues.removeAll(oldValues);
					Set<V> removedValues = new HashSet<>(oldValues);
					removedValues.removeAll(newValues);
					if (!addedValues.isEmpty() || !removedValues.isEmpty()) {
						notifySetMultimapListeners(entries.getKey(),
								removedValues, addedValues);
					}
				} else {
					notifySetMultimapListeners(entries.getKey(), oldValues,
							Collections.<V> emptySet());
				}
			}
			// compute added values
			for (Map.Entry<K, Set<V>> entries : Multimaps.asMap(currentValue)
					.entrySet()) {
				K key = entries.getKey();
				if (!oldValue.containsKey(key)) {
					notifySetMultimapListeners(key, Collections.<V> emptySet(),
							entries.getValue());
				}
			}
		}
	}

	/**
	 * Removes the given {@link ChangeListener} from this
	 * {@link SetMultimapChangeListener}. If its was registered more than once,
	 * removes one occurrence.
	 *
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(
			ChangeListener<? super ObservableSetMultimap<K, V>> listener) {
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockChangeListeners) {
			changeListeners = new ArrayList<>(changeListeners);
		}
		// XXX: We have to ignore the hash code when removing listeners, as
		// otherwise unbinding will be broken (JavaFX bindings violate the
		// contract between equals() and hashCode()); remove() may thus not be
		// used.
		for (Iterator<ChangeListener<? super ObservableSetMultimap<K, V>>> iterator = changeListeners
				.iterator(); iterator.hasNext();) {
			if (iterator.next().equals(listener)) {
				iterator.remove();
				break;
			}
		}
		if (changeListeners.isEmpty()) {
			changeListeners = null;
		}
	}
}
