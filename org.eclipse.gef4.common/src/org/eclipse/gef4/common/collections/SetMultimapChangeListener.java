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
package org.eclipse.gef4.common.collections;

import java.util.Set;

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
		 * Creates a new change associated with the given source
		 * {@link ObservableSetMultimap}.
		 * 
		 * @param source
		 *            The source of the change.
		 */
		public Change(ObservableSetMultimap<K, V> source) {
			this.source = source;
		}

		/**
		 * The source {@link ObservableSetMultimap} this change is associated
		 * with.
		 * 
		 * @return The source {@link ObservableSetMultimap}.
		 */
		public ObservableSetMultimap<K, V> getSource() {
			return source;
		}

		/**
		 * Indicates whether elements were added to the
		 * {@link ObservableSetMultimap}.
		 * 
		 * @return <code>true</code> if new values or (key-value) entries were
		 *         added to the {@link ObservableSetMultimap},
		 *         <code>false</code> otherwise.
		 */
		public abstract boolean wasAdded();

		/**
		 * Indicates whether elements were removed from the
		 * {@link ObservableSetMultimap}.
		 * 
		 * @return <code>true</code> if old values or (key-value) entries were
		 *         removed from the {@link ObservableSetMultimap},
		 *         <code>false</code> otherwise.
		 */
		public abstract boolean wasRemoved();

		/**
		 * The key associated with the respective change.
		 * 
		 * @return The key for which values were added or removed.
		 */
		public abstract K getKey();

		/**
		 * Retrieves the values that were added for the given key.
		 * 
		 * @return The values that have become associated with the key.
		 */
		public abstract Set<V> getValuesAdded();

		/**
		 * Retrieves the values that were removed for the given key.
		 * 
		 * @return The values previously associated with the key.
		 */
		public abstract Set<V> getValuesRemoved();
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
