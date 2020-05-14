/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.common.beans.binding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.common.collections.MapListenerHelperEx;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.MapExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableMapValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 * A utility class to support notifications for an {@link MapExpression}.
 *
 * @author anyssen
 *
 * @param <K>
 *            The key type of the {@link MapExpression}.
 * @param <V>
 *            The value type of the {@link MapExpression}.
 */
public class MapExpressionHelperEx<K, V> extends MapListenerHelperEx<K, V> {

	private List<ChangeListener<? super ObservableMap<K, V>>> changeListeners = null;
	private ObservableMapValue<K, V> observableValue = null;
	private ObservableMap<K, V> currentValue = null;

	private boolean lockChangeListeners;

	/**
	 * Constructs a new {@link MapExpressionHelperEx} for the given source
	 * {@link ObservableMapValue}.
	 *
	 * @param observableValue
	 *            The observableValue {@link ObservableMap}, which is used in
	 *            change notifications.
	 */
	public MapExpressionHelperEx(ObservableMapValue<K, V> observableValue) {
		super(observableValue);
		this.observableValue = observableValue;
		this.currentValue = observableValue.getValue();
	}

	/**
	 * Adds a new {@link ChangeListener} to this {@link MapExpressionHelperEx}.
	 * If the same listener is added more than once, it will be registered more
	 * than once and will receive multiple change events.
	 *
	 * @param listener
	 *            The {@link ChangeListener} to add.
	 */
	public void addListener(
			ChangeListener<? super ObservableMap<K, V>> listener) {
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
	 * {@link MapChangeListener MapChangeListeners}.
	 *
	 */
	public void fireValueChangedEvent() {
		final ObservableMap<K, V> oldValue = currentValue;
		currentValue = observableValue.getValue();
		notifyListeners(oldValue, currentValue);
	}

	/**
	 * Fires notifications to all attached {@link InvalidationListener
	 * InvalidationListeners}, and {@link MapChangeListener MapChangeListeners}.
	 *
	 * @param change
	 *            The change that needs to be propagated.
	 */
	@Override
	public void fireValueChangedEvent(
			MapChangeListener.Change<? extends K, ? extends V> change) {
		if (change != null) {
			notifyInvalidationListeners();
			// XXX: We do not notify change listeners here, as the identity of
			// the observed value did not change (see
			// https://bugs.openjdk.java.net/browse/JDK-8089169)
			notifyMapChangeListeners(
					new AtomicChange<>(observableValue, change));
		}
	}

	private void notifyListeners(ObservableMap<K, V> oldValue,
			ObservableMap<K, V> currentValue) {
		if (currentValue != oldValue) {
			notifyInvalidationListeners();
			if (changeListeners != null) {
				try {
					lockChangeListeners = true;
					for (ChangeListener<? super ObservableMap<K, V>> l : changeListeners) {
						l.changed(observableValue, oldValue, currentValue);
					}
				} finally {
					lockChangeListeners = false;
				}
			}
			if (oldValue == null || !oldValue.equals(currentValue)) {
				notifyMapListeners(oldValue, currentValue);
			}
		}
	}

	private void notifyMapListeners(ObservableMap<K, V> oldValue,
			ObservableMap<K, V> currentValue) {
		if (currentValue == null) {
			for (K key : oldValue.keySet()) {
				notifyMapChangeListeners(new MapListenerHelperEx.AtomicChange<>(
						getSource(), key, oldValue.get(key), null));
			}
		} else if (oldValue == null) {
			for (K key : currentValue.keySet()) {
				notifyMapChangeListeners(new MapListenerHelperEx.AtomicChange<>(
						getSource(), key, null, currentValue.get(key)));
			}
		} else {
			for (K key : oldValue.keySet()) {
				if (!currentValue.containsKey(key)) {
					// removed values
					notifyMapChangeListeners(
							new MapListenerHelperEx.AtomicChange<>(getSource(),
									key, oldValue.get(key), null));
				}
			}
			for (K key : currentValue.keySet()) {
				if (currentValue.get(key) == null ? oldValue.get(key) != null
						: !currentValue.get(key).equals(oldValue.get(key))) {
					// added/changed values
					notifyMapChangeListeners(
							new MapListenerHelperEx.AtomicChange<>(getSource(),
									key, oldValue.get(key),
									currentValue.get(key)));
				}
			}
		}
	}

	/**
	 * Removes the given {@link ChangeListener} from this
	 * {@link MapChangeListener}. If it was registered more than once, removes
	 * only one occurrence.
	 *
	 * @param listener
	 *            The {@link ChangeListener} to remove.
	 */
	public void removeListener(
			ChangeListener<? super ObservableMap<K, V>> listener) {
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
		for (Iterator<ChangeListener<? super ObservableMap<K, V>>> iterator = changeListeners
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
