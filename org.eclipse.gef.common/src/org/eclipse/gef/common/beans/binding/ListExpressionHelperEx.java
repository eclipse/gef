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
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.common.collections.ListListenerHelperEx;

import javafx.beans.binding.ListExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableListValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * A utility class to support notifications for an {@link ListExpression}.
 *
 * @author anyssen
 *
 * @param <E>
 *            The element type of the {@link ListExpression}.
 *
 */
public class ListExpressionHelperEx<E> extends ListListenerHelperEx<E> {

	private List<ChangeListener<? super ObservableList<E>>> changeListeners = null;
	private ObservableListValue<E> observableValue = null;
	private ObservableList<E> currentValue = null;

	private boolean lockChangeListeners;

	/**
	 * Constructs a new {@link ListExpressionHelperEx} for the given source
	 * {@link ObservableListValue}.
	 *
	 * @param observableValue
	 *            The observableValue {@link ObservableList}, which is used in
	 *            change notifications.
	 */
	public ListExpressionHelperEx(ObservableListValue<E> observableValue) {
		super(observableValue);
		this.observableValue = observableValue;
		this.currentValue = observableValue.getValue();
	}

	/**
	 * Adds a new {@link ChangeListener} to this {@link ListExpressionHelperEx}.
	 * If the same listener is added more than once, it will be registered more
	 * than once and will receive multiple change events.
	 *
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(
			ChangeListener<? super ObservableList<E>> listener) {
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
	 * {@link ListChangeListener ListChangeListeners}.
	 *
	 */
	public void fireValueChangedEvent() {
		final ObservableList<E> oldValue = currentValue;
		currentValue = observableValue.getValue();
		notifyListeners(oldValue, currentValue);
	}

	/**
	 * Fires notifications to all attached
	 * {@link javafx.beans.InvalidationListener InvalidationListeners}, and
	 * {@link ListChangeListener ListChangeListeners}.
	 *
	 * @param change
	 *            The change that needs to be propagated.
	 */
	@Override
	public void fireValueChangedEvent(
			ListChangeListener.Change<? extends E> change) {
		if (change != null) {
			notifyInvalidationListeners();
			// XXX: We do not notify change listeners here, as the identity of
			// the observed value did not change (see
			// https://bugs.openjdk.java.net/browse/JDK-8089169)
			notifyListChangeListeners(
					new AtomicChange<>(observableValue, change));
		}
	}

	private void notifyListeners(ObservableList<E> oldValue,
			ObservableList<E> currentValue) {
		if (currentValue != oldValue) {
			notifyInvalidationListeners();
			if (changeListeners != null) {
				try {
					lockChangeListeners = true;
					for (ChangeListener<? super ObservableList<E>> l : changeListeners) {
						l.changed(observableValue, oldValue, currentValue);
					}
				} finally {
					lockChangeListeners = false;
				}
			}
			if (oldValue == null || !oldValue.equals(currentValue)) {
				notifyListListeners(oldValue, currentValue);
			}
		}
	}

	private void notifyListListeners(ObservableList<E> oldValue,
			ObservableList<E> currentValue) {
		if (currentValue == null) {
			notifyListChangeListeners(new ListListenerHelperEx.AtomicChange<>(
					getSource(), new ArrayList<>(oldValue),
					ElementarySubChange.removed(oldValue, 0, 0)));
		} else if (oldValue == null) {
			notifyListChangeListeners(new ListListenerHelperEx.AtomicChange<>(
					getSource(), Collections.<E> emptyList(),
					ElementarySubChange.added(currentValue, 0,
							currentValue.size())));
		} else {
			notifyListChangeListeners(new ListListenerHelperEx.AtomicChange<>(
					getSource(), new ArrayList<>(oldValue),
					ElementarySubChange.replaced(oldValue, currentValue, 0,
							currentValue.size())));
		}
	}

	/**
	 * Removes the given {@link ChangeListener} from this
	 * {@link ListChangeListener}. If it was registered more than once, removes
	 * only one occurrence.
	 *
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(
			ChangeListener<? super ObservableList<E>> listener) {
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
		for (Iterator<ChangeListener<? super ObservableList<E>>> iterator = changeListeners
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
