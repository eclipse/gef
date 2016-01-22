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
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef4.common.beans.value.ObservableMultisetValue;
import org.eclipse.gef4.common.collections.MultisetChangeListener;
import org.eclipse.gef4.common.collections.MultisetChangeListenerHelper;
import org.eclipse.gef4.common.collections.ObservableMultiset;

import com.google.common.collect.HashMultiset;

import javafx.beans.value.ChangeListener;

/**
 * A utility class to support change notifications for an
 * {@link ObservableMultiset}.
 *
 * @author anyssen
 * @param <E>
 *            The element type of the {@link ObservableMultiset}.
 *
 */
public class MultisetExpressionHelper<E>
		extends MultisetChangeListenerHelper<E> {

	private List<ChangeListener<? super ObservableMultiset<E>>> changeListeners = null;

	private ObservableMultisetValue<E> observableValue = null;
	private ObservableMultiset<E> currentValue = null;

	private boolean lockChangeListeners;

	/**
	 * Constructs a new {@link MultisetExpressionHelper} for the given source
	 * {@link ObservableMultisetValue}.
	 *
	 * @param observableValue
	 *            The observableValue {@link ObservableMultiset}, which is used
	 *            in change notifications.
	 */
	public MultisetExpressionHelper(
			ObservableMultisetValue<E> observableValue) {
		super(observableValue);
		this.observableValue = observableValue;
		this.currentValue = observableValue.getValue();
	}

	/**
	 * Adds a new {@link ChangeListener} to this
	 * {@link MultisetExpressionHelper}. If the same listener is added more than
	 * once, it will be registered more than once and will receive multiple
	 * change events.
	 *
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(
			ChangeListener<? super ObservableMultiset<E>> listener) {
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
	 * {@link MultisetChangeListener MultisetChangeListeners}.
	 *
	 */
	public void fireValueChangedEvent() {
		final ObservableMultiset<E> oldValue = currentValue;
		currentValue = observableValue.getValue();
		notifyListeners(oldValue, currentValue);
	}

	/**
	 * Fires notifications to all attached
	 * {@link javafx.beans.InvalidationListener InvalidationListeners},
	 * {@link javafx.beans.value.ChangeListener ChangeListeners}, and
	 * {@link MultisetChangeListener MultisetChangeListeners}.
	 *
	 * @param change
	 *            The change that needs to be propagated.
	 */
	@Override
	public void fireValueChangedEvent(
			MultisetChangeListener.Change<? extends E> change) {
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
					for (ChangeListener<? super ObservableMultiset<E>> l : changeListeners) {
						l.changed(observableValue, currentValue, currentValue);
					}
				} finally {
					lockChangeListeners = false;
				}
			}
			notifyMultisetListeners(
					new AtomicChange<>(observableValue, change));
		}
	}

	private void notifyListeners(ObservableMultiset<E> oldValue,
			ObservableMultiset<E> currentValue) {
		notifyInvalidationListeners();
		if (currentValue != oldValue) {
			if (changeListeners != null) {
				try {
					lockChangeListeners = true;
					for (ChangeListener<? super ObservableMultiset<E>> l : changeListeners) {
						l.changed(observableValue, oldValue, currentValue);
					}
				} finally {
					lockChangeListeners = false;
				}
			}
			notifyMultisetListeners(oldValue, currentValue);
		}
	}

	private void notifyMultisetListeners(ObservableMultiset<E> oldValue,
			ObservableMultiset<E> currentValue) {
		if (currentValue == null) {
			List<ElementarySubChange<E>> elementaryChanges = new ArrayList<>();
			// all entries have been removed
			for (E e : oldValue.elementSet()) {
				elementaryChanges.add(
						new ElementarySubChange<>(e, oldValue.count(e), 0));
			}
			notifyMultisetListeners(
					new MultisetChangeListenerHelper.AtomicChange<>(getSource(),
							HashMultiset.<E> create(oldValue),
							elementaryChanges));
		} else if (oldValue == null) {
			List<ElementarySubChange<E>> elementaryChanges = new ArrayList<>();
			// all entries have been added
			for (E e : currentValue.elementSet()) {
				elementaryChanges.add(
						new ElementarySubChange<>(e, 0, currentValue.count(e)));
			}
			notifyMultisetListeners(
					new MultisetChangeListenerHelper.AtomicChange<>(getSource(),
							HashMultiset.<E> create(), elementaryChanges));
		} else {
			List<ElementarySubChange<E>> elementaryChanges = new ArrayList<>();
			// compute changed/removed values
			for (E e : oldValue.elementSet()) {
				if (currentValue.contains(e)) {
					// only count changed
					if (oldValue.count(e) > currentValue.count(e)) {
						// occurrences removed
						elementaryChanges.add(new ElementarySubChange<>(e,
								oldValue.count(e) - currentValue.count(e), 0));
					} else if (currentValue.count(e) > oldValue.count(e)) {
						// occurrences added
						elementaryChanges.add(new ElementarySubChange<>(e, 0,
								currentValue.count(e) - oldValue.count(e)));
					}
				} else {
					// removed
					elementaryChanges.add(
							new ElementarySubChange<>(e, oldValue.count(e), 0));
				}
			}
			for (E e : currentValue.elementSet()) {
				if (!oldValue.contains(e)) {
					// added
					elementaryChanges.add(new ElementarySubChange<>(e, 0,
							currentValue.count(e)));
				}
			}
			notifyMultisetListeners(
					new MultisetChangeListenerHelper.AtomicChange<>(getSource(),
							HashMultiset.<E> create(oldValue),
							elementaryChanges));
		}
	}

	/**
	 * Removes the given {@link ChangeListener} from this
	 * {@link MultisetChangeListener}. If its was registered more than once,
	 * removes one occurrence.
	 *
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(
			ChangeListener<? super ObservableMultiset<E>> listener) {
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
		for (Iterator<ChangeListener<? super ObservableMultiset<E>>> iterator = changeListeners
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
