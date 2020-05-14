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

import com.google.common.collect.Multiset;

/**
 * A {@link MultisetChangeListener} is the notification target for changes
 * related to an {@link ObservableMultiset}.
 * 
 * @param <E>
 *            The element type of the {@link ObservableMultiset}.
 * 
 * @author anyssen
 * 
 */
public interface MultisetChangeListener<E> {

	/**
	 * Represents an atomic change done to an {@link ObservableMultiset}, i.e. a
	 * change resulting from a single method invocation on an
	 * {@link ObservableMultiset}.
	 * <p>
	 * The change may internally consist of several elementary sub-changes,
	 * which are related to changes of a single element. That is, a call to
	 * {@link ObservableMultiset#add(Object)} or
	 * {@link ObservableMultiset#setCount(Object, int)} will lead to an atomic
	 * change that comprises only a single elementary sub-change, while a call
	 * to {@link ObservableMultiset#clear()} will potentially lead to several
	 * elementary sub-changes (one for each element contained in the Multiset).
	 * <p>
	 * The comprised elementary sub-changes need to be navigated using
	 * {@link #next()}, the relevant information can then be retrieved via
	 * {@link #getElement()}, {@link #getAddCount()}, and
	 * {@link #getRemoveCount()} (for the current elementary sub-change the
	 * internal cursor currently points at). Initially, the internal cursor is
	 * set to point before the first elementary sub-change, so that an initial
	 * call to {@link #next()} is required to access the first elementary
	 * sub-change, while {@link #reset()} can be used to reset the cursor to
	 * this initial state before the first elementary sub-change.
	 * <p>
	 * The {@link #getMultiset()} returns the source {@link ObservableMultiset}
	 * that was changed (in the state after the atomic change was applied). The
	 * previous contents of the source {@link ObservableMultiset} (in the state
	 * before the atomic change was applied) can be obtained via
	 * {@link #getPreviousContents()}. Both are independent of the state of the
	 * internal cursor and may be accessed at any time.
	 *
	 * @param <E>
	 *            The element type of the {@link ObservableMultiset}.
	 */
	public static abstract class Change<E> {

		private final ObservableMultiset<E> source;

		/**
		 * Creates a new (atomic) change associated with the given source
		 * {@link ObservableMultiset}.
		 * 
		 * @param source
		 *            The source of the change.
		 */
		public Change(ObservableMultiset<E> source) {
			this.source = source;
		}

		/**
		 * Places the internal cursor on the next elementary sub-change, so that
		 * it be processed using {@link #getElement()}, {@link #getAddCount()},
		 * and {@link #getRemoveCount()}. This method has to be called initially
		 * to have the internal cursor point to the first elementary sub-change
		 * that is comprised.
		 * 
		 * @return <code>true</code> if the internal cursor could be switched to
		 *         the next elementary sub-change, <code>false</code> if the
		 *         current elementary sub-change was the last change that is
		 *         comprised.
		 */
		public abstract boolean next();

		/**
		 * Reset the internal cursor to the initial state, so that the first
		 * elementary sub-change can be accessed by calling {@link #next()}.
		 */
		public abstract void reset();

		/**
		 * The source {@link ObservableMultiset} this atomic change is
		 * associated with.
		 * <p>
		 * This method does not depend on the state of the internal cursor, may
		 * thus be accessed independent on which elementary sub-change is
		 * currently processed.
		 * 
		 * @return The source {@link ObservableMultiset}.
		 */
		public ObservableMultiset<E> getMultiset() {
			return source;
		}

		/**
		 * Returns an unmodifiable {@link Multiset} that contains the previous
		 * contents of the source {@link ObservableMultiset} before the atomic
		 * change was applied.
		 * <p>
		 * This method does not depend on the state of the internal cursor, may
		 * thus be accessed independent on which elementary sub-change is
		 * currently processed.
		 * 
		 * @return An unmodifiable {@link Multiset} representing the contents of
		 *         the {@link ObservableMultiset} before the change.
		 */
		public abstract Multiset<E> getPreviousContents();

		/**
		 * Returns how often an element has been added in the current elementary
		 * sub-change, if one has been added.
		 * <p>
		 * This method depends on the state of the internal cursor that can be
		 * manipulated via {@link #next()} and {@link #reset()}.
		 * 
		 * @return The number of occurrences that have been added.
		 */
		public abstract int getAddCount();

		/**
		 * Returns how often an element has been removed in the current
		 * elementary sub-change, if one has been removed.
		 * <p>
		 * This method depends on the state of the internal cursor that can be
		 * manipulated via {@link #next()} and {@link #reset()}.
		 * 
		 * @return The number of occurrences that have been removed.
		 */
		public abstract int getRemoveCount();

		/**
		 * Retrieves the element that was altered in the current elementary
		 * sub-change.
		 * <p>
		 * This method depends on the state of the internal cursor that can be
		 * manipulated via {@link #next()} and {@link #reset()}.
		 * 
		 * @return The added element in case an element was added.
		 */
		public abstract E getElement();
	}

	/**
	 * Called after an atomic change has been made to an
	 * {@link ObservableMultiset}. Each call to a modifying method of
	 * {@link ObservableMultiset} will lead to exactly one invocation, which may
	 * internally comprise several elementary sub-changes.
	 *
	 * @param change
	 *            A {@link Change} object representing an atomic change that was
	 *            performed on the source {@link ObservableSetMultimap}.
	 */
	void onChanged(Change<? extends E> change);
}
