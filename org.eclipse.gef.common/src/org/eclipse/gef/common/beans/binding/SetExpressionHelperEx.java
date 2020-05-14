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

import org.eclipse.gef.common.collections.SetListenerHelperEx;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.SetExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableSetValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

/**
 * A utility class to support notifications for an {@link SetExpression}.
 *
 * @author anyssen
 *
 * @param <E>
 *            The element type of the {@link SetExpression}.
 */
public class SetExpressionHelperEx<E> extends SetListenerHelperEx<E> {

	private List<ChangeListener<? super ObservableSet<E>>> changeListeners = null;
	private ObservableSetValue<E> observableValue = null;
	private ObservableSet<E> currentValue = null;

	private boolean lockChangeListeners;

	/**
	 * Constructs a new {@link SetExpressionHelperEx} for the given source
	 * {@link ObservableSetValue}.
	 *
	 * @param observableValue
	 *            The observableValue {@link ObservableSet}, which is used in
	 *            change notifications.
	 */
	public SetExpressionHelperEx(ObservableSetValue<E> observableValue) {
		super(observableValue);
		this.observableValue = observableValue;
		this.currentValue = observableValue.getValue();
	}

	/**
	 * Adds a new {@link ChangeListener} to this {@link SetExpressionHelperEx}.
	 * If the same listener is added more than once, it will be registered more
	 * than once and will receive multiple change events.
	 *
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(ChangeListener<? super ObservableSet<E>> listener) {
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
	 * {@link SetChangeListener SetChangeListeners}.
	 *
	 */
	public void fireValueChangedEvent() {
		final ObservableSet<E> oldValue = currentValue;
		currentValue = observableValue.getValue();
		notifyListeners(oldValue, currentValue);
	}

	/**
	 * Fires notifications to all attached {@link InvalidationListener
	 * InvalidationListeners}, and {@link SetChangeListener SetChangeListeners}.
	 *
	 * @param change
	 *            The change that needs to be propagated.
	 */
	@Override
	public void fireValueChangedEvent(
			SetChangeListener.Change<? extends E> change) {
		if (change != null) {
			notifyInvalidationListeners();
			// XXX: We do not notify change listeners here, as the identity of
			// the observed value did not change (see
			// https://bugs.openjdk.java.net/browse/JDK-8089169)
			notifySetChangeListeners(
					new AtomicChange<>(observableValue, change));
		}
	}

	private void notifyListeners(ObservableSet<E> oldValue,
			ObservableSet<E> currentValue) {
		if (currentValue != oldValue) {
			notifyInvalidationListeners();
			if (changeListeners != null) {
				try {
					lockChangeListeners = true;
					for (ChangeListener<? super ObservableSet<E>> l : changeListeners) {
						l.changed(observableValue, oldValue, currentValue);
					}
				} finally {
					lockChangeListeners = false;
				}
			}
			if (oldValue == null || !oldValue.equals(currentValue)) {
				notifySetListeners(oldValue, currentValue);
			}
		}
	}

	private void notifySetListeners(ObservableSet<E> oldValue,
			ObservableSet<E> currentValue) {
		if (currentValue == null) {
			for (E e : oldValue) {
				notifySetChangeListeners(new SetListenerHelperEx.AtomicChange<>(
						getSource(), e, null));
			}
		} else if (oldValue == null) {
			for (E e : currentValue) {
				notifySetChangeListeners(new SetListenerHelperEx.AtomicChange<>(
						getSource(), null, e));
			}
		} else {
			for (E e : oldValue) {
				if (!currentValue.contains(e)) {
					// removed values
					notifySetChangeListeners(
							new SetListenerHelperEx.AtomicChange<>(getSource(),
									e, null));
				}
			}
			for (E e : currentValue) {
				if (!oldValue.contains(e)) {
					// added values
					notifySetChangeListeners(
							new SetListenerHelperEx.AtomicChange<>(getSource(),
									null, e));
				}
			}
		}
	}

	/**
	 * Removes the given {@link ChangeListener} from this
	 * {@link SetChangeListener}. If it was registered more than once, removes
	 * only one occurrence.
	 *
	 * @param listener
	 *            The {@link ChangeListener} to remove.
	 */
	public void removeListener(
			ChangeListener<? super ObservableSet<E>> listener) {
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
		for (Iterator<ChangeListener<? super ObservableSet<E>>> iterator = changeListeners
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
