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
package org.eclipse.gef.common.collections;

import java.util.Set;

import com.google.common.collect.SetMultimap;

/**
 * A {@link SetMultimapChangeListener} is the notification target for changes
 * related to an {@link ObservableSetMultimap}.
 *
 * @param <K>
 *            The key type of the {@link ObservableSetMultimap}.
 * @param <V>
 *            The value type of the {@link ObservableSetMultimap}.
 *
 * @author anyssen
 *
 */
public interface SetMultimapChangeListener<K, V> {

	/**
	 * Represents an elementary change done to an {@link ObservableSetMultimap},
	 * related to a single key but to potentially multiple values.
	 *
	 * @param <K>
	 *            The key type of the {@link ObservableSetMultimap}.
	 * @param <V>
	 *            The value type of the {@link ObservableSetMultimap}.
	 */
	public static abstract class Change<K, V> {

		private final ObservableSetMultimap<K, V> source;

		/**
		 * Creates a new (atomic) change associated with the given source
		 * {@link ObservableSetMultimap}.
		 *
		 * @param source
		 *            The source of the change.
		 */
		public Change(ObservableSetMultimap<K, V> source) {
			this.source = source;
		}

		/**
		 * The key associated with the current elementary sub-change.
		 * <p>
		 * This method depends on the state of the internal cursor that can be
		 * manipulated via {@link #next()} and {@link #reset()}.
		 *
		 * @return The key for which values were added or removed.
		 */
		public abstract K getKey();

		/**
		 * Returns an unmodifiable {@link SetMultimap} that contains the
		 * previous contents of the source {@link ObservableSetMultimap} before
		 * the atomic change was applied.
		 * <p>
		 * This method does not depend on the state of the internal cursor, may
		 * thus be accessed independent on which elementary sub-change is
		 * currently processed.
		 *
		 * @return An unmodifiable {@link SetMultimap} representing the contents
		 *         of the {@link ObservableSetMultimap} before the change.
		 */
		public abstract SetMultimap<K, V> getPreviousContents();

		/**
		 * The source {@link ObservableSetMultimap} this (atomic) change is
		 * associated with.
		 * <p>
		 * This method does not depend on the state of the internal cursor, may
		 * thus be accessed independent on which elementary sub-change is
		 * currently processed.
		 *
		 * @return The source {@link ObservableSetMultimap}.
		 */
		public ObservableSetMultimap<K, V> getSetMultimap() {
			return source;
		}

		/**
		 * Retrieves the values that were added for the given key in the current
		 * elementary sub-change.
		 * <p>
		 * This method depends on the state of the internal cursor that can be
		 * manipulated via {@link #next()} and {@link #reset()}.
		 *
		 * @return The values that have become associated with the key.
		 */
		public abstract Set<V> getValuesAdded();

		/**
		 * Retrieves the values that were removed for the given key in the
		 * current elementary sub-change.
		 * <p>
		 * This method depends on the state of the internal cursor that can be
		 * manipulated via {@link #next()} and {@link #reset()}.
		 *
		 * @return The values previously associated with the key.
		 */
		public abstract Set<V> getValuesRemoved();

		/**
		 * Places the internal cursor on the next elementary sub-change, so that
		 * it be processed using {@link #getKey()}, {@link #getValuesAdded()},
		 * and {@link #getValuesRemoved()}, {@link #wasAdded()}, and
		 * {@link #wasRemoved()}. This method has to be called initially to have
		 * the internal cursor point to the first elementary sub-change that is
		 * comprised.
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
		 * Indicates whether elements were added to the
		 * {@link ObservableSetMultimap} during this elementary sub-change.
		 * <p>
		 * This method depends on the state of the internal cursor that can be
		 * manipulated via {@link #next()} and {@link #reset()}.
		 *
		 * @return <code>true</code> if new values or (key-value) entries were
		 *         added to the {@link ObservableSetMultimap},
		 *         <code>false</code> otherwise.
		 */
		public abstract boolean wasAdded();

		/**
		 * Indicates whether elements were removed from the
		 * {@link ObservableSetMultimap} in the current elementary sub-change.
		 * <p>
		 * This method depends on the state of the internal cursor that can be
		 * manipulated via {@link #next()} and {@link #reset()}.
		 *
		 * @return <code>true</code> if old values or (key-value) entries were
		 *         removed from the {@link ObservableSetMultimap},
		 *         <code>false</code> otherwise.
		 */
		public abstract boolean wasRemoved();
	}

	/**
	 * Called after a change has been made to an {@link ObservableSetMultimap}.
	 * This method is called whenever a change to a single key is performed. In
	 * case of complex operation like {@link ObservableSetMultimap#clear()} or
	 * {@link ObservableSetMultimap#putAll(Object, Iterable)} multiple
	 * invocations will occur.
	 *
	 * @param change
	 *            A {@link Change} object representing the changes that were
	 *            performed on the source {@link ObservableSetMultimap}.
	 */
	void onChanged(Change<? extends K, ? extends V> change);
}
