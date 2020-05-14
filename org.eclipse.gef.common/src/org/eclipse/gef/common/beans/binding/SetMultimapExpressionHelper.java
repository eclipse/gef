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
package org.eclipse.gef.common.beans.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.common.beans.value.ObservableSetMultimapValue;
import org.eclipse.gef.common.collections.ObservableSetMultimap;
import org.eclipse.gef.common.collections.SetMultimapChangeListener;
import org.eclipse.gef.common.collections.SetMultimapListenerHelper;

import com.google.common.collect.HashMultimap;
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
		extends SetMultimapListenerHelper<K, V> {

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
	 * {@link javafx.beans.InvalidationListener InvalidationListeners} and
	 * {@link SetMultimapChangeListener SetMultimapChangeListeners}.
	 *
	 * @param change
	 *            The change that needs to be propagated.
	 */
	@Override
	public void fireValueChangedEvent(
			SetMultimapChangeListener.Change<? extends K, ? extends V> change) {
		if (change != null) {
			notifyInvalidationListeners();
			// XXX: We do not notify change listeners here, as the identity of
			// the observed value did not change (see
			// https://bugs.openjdk.java.net/browse/JDK-8089169)
			notifySetMultimapChangeListeners(
					new AtomicChange<>(observableValue, change));
		}
	}

	private void notifyListeners(ObservableSetMultimap<K, V> oldValue,
			ObservableSetMultimap<K, V> currentValue) {
		if (currentValue != oldValue) {
			notifyInvalidationListeners();
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
			if (oldValue == null || !oldValue.equals(currentValue)) {
				notifySetMultimapListeners(oldValue, currentValue);
			}
		}
	}

	private void notifySetMultimapListeners(
			ObservableSetMultimap<K, V> oldValue,
			ObservableSetMultimap<K, V> currentValue) {
		if (currentValue == null) {
			List<ElementarySubChange<K, V>> elementaryChanges = new ArrayList<>();
			// all entries have been removed
			for (Map.Entry<K, Set<V>> entries : Multimaps.asMap(oldValue)
					.entrySet()) {
				elementaryChanges.add(new ElementarySubChange<>(
						entries.getKey(), entries.getValue(),
						Collections.<V> emptySet()));
			}
			notifySetMultimapChangeListeners(
					new SetMultimapListenerHelper.AtomicChange<>(getSource(),
							HashMultimap.<K, V> create(oldValue),
							elementaryChanges));
		} else if (oldValue == null) {
			List<ElementarySubChange<K, V>> elementaryChanges = new ArrayList<>();
			// all entries have been added
			for (Map.Entry<K, Set<V>> entries : Multimaps.asMap(currentValue)
					.entrySet()) {
				elementaryChanges.add(new ElementarySubChange<>(
						entries.getKey(), Collections.<V> emptySet(),
						entries.getValue()));
			}
			notifySetMultimapChangeListeners(
					new SetMultimapListenerHelper.AtomicChange<>(getSource(),
							HashMultimap.<K, V> create(), elementaryChanges));
		} else {
			List<ElementarySubChange<K, V>> elementaryChanges = new ArrayList<>();
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
						elementaryChanges.add(new ElementarySubChange<>(
								entries.getKey(), removedValues, addedValues));
					}
				} else {
					elementaryChanges
							.add(new ElementarySubChange<>(entries.getKey(),
									oldValues, Collections.<V> emptySet()));
				}
			}
			// compute added values
			for (Map.Entry<K, Set<V>> entries : Multimaps.asMap(currentValue)
					.entrySet()) {
				K key = entries.getKey();
				if (!oldValue.containsKey(key)) {
					elementaryChanges.add(new ElementarySubChange<>(key,
							Collections.<V> emptySet(), entries.getValue()));
				}
			}
			notifySetMultimapChangeListeners(
					new SetMultimapListenerHelper.AtomicChange<>(getSource(),
							HashMultimap.<K, V> create(oldValue),
							elementaryChanges));
		}
	}

	/**
	 * Removes the given {@link ChangeListener} from this
	 * {@link SetMultimapChangeListener}. If it was registered more than once,
	 * removes only one occurrence.
	 *
	 * @param listener
	 *            The {@link ChangeListener} to remove.
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
