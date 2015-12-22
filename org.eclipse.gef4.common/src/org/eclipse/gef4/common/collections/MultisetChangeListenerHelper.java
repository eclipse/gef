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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef4.common.collections.MultisetChangeListener.Change;

import javafx.beans.InvalidationListener;

/**
 * A utility class to support change notifications for an
 * {@link ObservableMultiset}.
 * 
 * @author anyssen
 * 
 * @param <E>
 *            The element type of the {@link ObservableMultiset}.
 *
 */
public class MultisetChangeListenerHelper<E> {

	/**
	 * A simple implementation of
	 * {@link org.eclipse.gef4.common.collections.MultisetChangeListener.Change}
	 * .
	 * 
	 * @author anyssen
	 *
	 * @param <E>
	 *            The elemenet type of the {@link ObservableMultiset}.
	 */
	public static class SimpleChange<E>
			extends MultisetChangeListener.Change<E> {

		private E element;
		private int removeCount;
		private int addCount;

		/**
		 * Constructs a new
		 * {@link org.eclipse.gef4.common.collections.MultisetChangeListenerHelper.SimpleChange}
		 * with the given values.
		 * 
		 * @param source
		 *            The source {@link ObservableSetMultimap} from which the
		 *            change resulted.
		 * @param element
		 *            The element that was added or removed.
		 * @param removeCount
		 *            The number of occurrences that were removed.
		 * @param addCount
		 *            The number of occurrences that were added.
		 */
		public SimpleChange(ObservableMultiset<E> source, E element,
				int removeCount, int addCount) {
			super(source);
			this.element = element;
			this.removeCount = removeCount;
			this.addCount = addCount;
		}

		/**
		 * Constructs a new
		 * {@link org.eclipse.gef4.common.collections.MultisetChangeListenerHelper.SimpleChange}
		 * based on the values of the passed in {@link Change}.
		 * 
		 * @param source
		 *            The source {@link ObservableMultiset} from which the
		 *            change resulted.
		 * @param change
		 *            The {@link Change} which provides the changed values.
		 */
		public SimpleChange(ObservableMultiset<E> source,
				MultisetChangeListener.Change<? extends E> change) {
			this(source, change.getElement(), change.getRemoveCount(),
					change.getAddCount());
		}

		@Override
		public String toString() {
			if (addCount > 0) {
				return "Added " + element + " " + addCount + " times.";
			} else {
				return "Removed " + element + " " + removeCount + " times.";
			}
		}

		@Override
		public int getAddCount() {
			return addCount;
		}

		@Override
		public int getRemoveCount() {
			return removeCount;
		}

		@Override
		public E getElement() {
			return element;
		}

	}

	private List<InvalidationListener> invalidationListeners = null;
	private List<MultisetChangeListener<? super E>> multisetChangeListeners = null;
	private ObservableMultiset<E> source;
	private boolean lockInvalidationListeners;
	private boolean lockMultisetChangeListeners;

	/**
	 * Constructs a new {@link MultisetChangeListener} for the given source
	 * {@link ObservableMultiset}.
	 * 
	 * @param source
	 *            The {@link ObservableMultiset} to use as source in change
	 *            notifications.
	 */
	public MultisetChangeListenerHelper(ObservableMultiset<E> source) {
		this.source = source;
	}

	/**
	 * Returns the source {@link ObservableMultiset} this
	 * {@link MultisetChangeListenerHelper} is bound to, which is used in change
	 * notifications.
	 * 
	 * @return The source {@link ObservableMultiset}.
	 */
	protected ObservableMultiset<E> getSource() {
		return source;
	}

	/**
	 * Adds a new {@link InvalidationListener} to this
	 * {@link MultisetChangeListenerHelper}. If the same listener is added more
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
	 * {@link MultisetChangeListenerHelper}. If the same listener is added more
	 * than once, it will be registered more than once and will receive multiple
	 * change events.
	 * 
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(MultisetChangeListener<? super E> listener) {
		if (multisetChangeListeners == null) {
			multisetChangeListeners = new ArrayList<>();
		}
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockMultisetChangeListeners) {
			multisetChangeListeners = new ArrayList<>(multisetChangeListeners);
		}
		multisetChangeListeners.add(listener);
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
	 * {@link MultisetChangeListener}s about the change.
	 * 
	 * @param change
	 *            The change to notify listeners about.
	 */
	public void fireValueChangedEvent(
			MultisetChangeListener.Change<? extends E> change) {
		notifyInvalidationListeners();
		if (change != null) {
			notifyMultisetListeners(change);
		}
	}

	/**
	 * Notifies the attached {@link MultisetChangeListener}s about the related
	 * change.
	 * 
	 * @param change
	 *            The applied change.
	 */
	protected void notifyMultisetListeners(Change<? extends E> change) {
		if (multisetChangeListeners != null) {
			try {
				lockMultisetChangeListeners = true;
				for (MultisetChangeListener<? super E> l : multisetChangeListeners) {
					l.onChanged(change);
				}
			} finally {
				lockMultisetChangeListeners = false;
			}
		}
	}

	/**
	 * Removes the given {@link InvalidationListener} from this
	 * {@link MultisetChangeListenerHelper}. If its was registered more than
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
			invalidationListeners = new ArrayList<>(invalidationListeners);
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
	 * Removes the given {@link MultisetChangeListener} from this
	 * {@link MultisetChangeListenerHelper}. If its was registered more than
	 * once, removes one occurrence.
	 * 
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(MultisetChangeListener<? super E> listener) {
		// XXX: Prevent ConcurrentModificationExceptions (in case listeners are
		// added during notifications); as we only create a new multi-set in the
		// locked case, memory should not be waisted.
		if (lockMultisetChangeListeners) {
			multisetChangeListeners = new ArrayList<>(multisetChangeListeners);
		}
		// XXX: We have to ignore the hash code when removing listeners, as
		// otherwise unbinding will be broken (JavaFX bindings violate the
		// contract between equals() and hashCode()); remove() may thus not be
		// used.
		for (Iterator<MultisetChangeListener<? super E>> iterator = multisetChangeListeners
				.iterator(); iterator.hasNext();) {
			if (iterator.next().equals(listener)) {
				iterator.remove();
				break;
			}
		}
		if (multisetChangeListeners.isEmpty()) {
			multisetChangeListeners = null;
		}
	}
}
