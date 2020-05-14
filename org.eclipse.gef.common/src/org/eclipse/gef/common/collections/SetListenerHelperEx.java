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

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.SetChangeListener.Change;

/**
 * A utility class to support change notifications for an {@link ObservableSet}
 * , replacing the JavaFX-internal {@code SetChangeListener} helper class.
 *
 * @author anyssen
 *
 * @param <E>
 *            The element type of the {@link ObservableSet}.
 */
public class SetListenerHelperEx<E> {

	/**
	 * A simple implementation of an
	 * {@link javafx.collections.SetChangeListener.Change}.
	 *
	 * @author anyssen
	 *
	 * @param <E>
	 *            The element type of the source {@link ObservableSet}.
	 *
	 */
	public static class AtomicChange<E> extends SetChangeListener.Change<E> {

		private E removedElement;
		private E addedElement;

		/**
		 * Creates a new {@link SetListenerHelperEx.AtomicChange} that
		 * represents a change comprising a single elementary sub-change.
		 *
		 * @param source
		 *            The source {@link ObservableSet} from which the change
		 *            originated.
		 * @param removedElement
		 *            The element that was removed by this change or
		 *            <code>null</code> if no value was removed.
		 * @param addedElement
		 *            The element that was added by this change or
		 *            <code>null</code> if no value was added.
		 */
		public AtomicChange(ObservableSet<E> source, E removedElement,
				E addedElement) {
			super(source);
			this.removedElement = removedElement;
			this.addedElement = addedElement;
		}

		/**
		 * Creates a new {@link SetListenerHelperEx.AtomicChange} for the passed
		 * in source, based on the data provided in the passed-in change.
		 * <p>
		 * This is basically used to allow properties wrapping an
		 * {@link ObservableSet} to re-fire change events of their wrapped
		 * {@link ObservableSet} with themselves as source.
		 *
		 * @param source
		 *            The new source {@link ObservableSet}.
		 * @param change
		 *            The change to infer a new change from. It is expected that
		 *            the change is in initial state. In either case it will be
		 *            reset to initial state.
		 */
		public AtomicChange(ObservableSet<E> source,
				SetChangeListener.Change<? extends E> change) {
			super(source);

			this.addedElement = change.getElementAdded();
			this.removedElement = change.getElementRemoved();
		}

		@Override
		public E getElementAdded() {
			return addedElement;
		}

		@Override
		public E getElementRemoved() {
			return removedElement;
		}

		@Override
		public String toString() {
			if (wasAdded()) {
				return "Added " + addedElement + ".";
			} else {
				return "Removed " + removedElement + ".";
			}
		}

		@Override
		public boolean wasAdded() {
			return addedElement != null;
		}

		@Override
		public boolean wasRemoved() {
			return removedElement != null;
		}
	}

	private List<InvalidationListener> invalidationListeners = null;
	private boolean lockInvalidationListeners;
	private boolean lockSetChangeListeners;
	private List<SetChangeListener<? super E>> setChangeListeners = null;
	private ObservableSet<E> source;

	/**
	 * Constructs a new {@link SetListenerHelperEx} for the given source
	 * {@link ObservableSet}.
	 *
	 * @param source
	 *            The {@link ObservableSet} to use as source in change
	 *            notifications.
	 */
	public SetListenerHelperEx(ObservableSet<E> source) {
		this.source = source;
	}

	/**
	 * Adds a new {@link InvalidationListener} to this
	 * {@link SetListenerHelperEx}. If the same listener is added more than
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
	 * Adds a new {@link SetChangeListener} to this {@link SetListenerHelperEx}.
	 * If the same listener is added more than once, it will be registered more
	 * than once and will receive multiple change events.
	 *
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(SetChangeListener<? super E> listener) {
		if (setChangeListeners == null) {
			setChangeListeners = new ArrayList<>();
		}
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockSetChangeListeners) {
			setChangeListeners = new ArrayList<>(setChangeListeners);
		}
		setChangeListeners.add(listener);
	}

	/**
	 * Notifies all attached {@link InvalidationListener}s and
	 * {@link SetChangeListener}s about the change.
	 *
	 * @param change
	 *            The change to notify listeners about.
	 */
	public void fireValueChangedEvent(
			SetChangeListener.Change<? extends E> change) {
		notifyInvalidationListeners();
		if (change != null) {
			notifySetChangeListeners(change);
		}
	}

	/**
	 * Returns the source {@link ObservableSet} this {@link SetListenerHelperEx}
	 * is bound to, which is used in change notifications.
	 *
	 * @return The source {@link ObservableSet}.
	 */
	protected ObservableSet<E> getSource() {
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
	 * Notifies the attached {@link SetChangeListener}s about the related
	 * change.
	 *
	 * @param change
	 *            The applied change.
	 */
	protected void notifySetChangeListeners(Change<? extends E> change) {
		if (setChangeListeners != null) {
			try {
				lockSetChangeListeners = true;
				for (SetChangeListener<? super E> l : setChangeListeners) {
					try {
						l.onChanged(change);
					} catch (Exception e) {
						Thread.currentThread().getUncaughtExceptionHandler()
								.uncaughtException(Thread.currentThread(), e);
					}
				}
			} finally {
				lockSetChangeListeners = false;
			}
		}
	}

	/**
	 * Removes the given {@link InvalidationListener} from this
	 * {@link SetListenerHelperEx}. If its was registered more than once,
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
	 * Removes the given {@link SetChangeListener} from this
	 * {@link SetListenerHelperEx}. If its was registered more than once,
	 * removes one occurrence.
	 *
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(SetChangeListener<? super E> listener) {
		if (setChangeListeners == null) {
			return;
		}

		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockSetChangeListeners) {
			setChangeListeners = new ArrayList<>(setChangeListeners);
		}
		// XXX: We have to ignore the hash code when removing listeners, as
		// otherwise unbinding will be broken (JavaFX bindings violate the
		// contract between equals() and hashCode(): JI-9028554); remove() may
		// thus not be used.
		for (Iterator<SetChangeListener<? super E>> iterator = setChangeListeners
				.iterator(); iterator.hasNext();) {
			if (iterator.next().equals(listener)) {
				iterator.remove();
				break;
			}
		}
		if (setChangeListeners.isEmpty()) {
			setChangeListeners = null;
		}
	}
}
