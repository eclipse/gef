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
	 * Represents an elementary change done to an {@link ObservableMultiset}.
	 *
	 * @param <E>
	 *            The element type of the {@link ObservableMultiset}.
	 */
	public static abstract class Change<E> {

		private final ObservableMultiset<E> source;

		/**
		 * Creates a new change associated with the given source
		 * {@link ObservableMultiset}.
		 * 
		 * @param source
		 *            The source of the change.
		 */
		public Change(ObservableMultiset<E> source) {
			this.source = source;
		}

		/**
		 * The source {@link ObservableMultiset} this change is associated with.
		 * 
		 * @return The source {@link ObservableMultiset}.
		 */
		public ObservableMultiset<E> getMultiset() {
			return source;
		}

		/**
		 * Returns how often an element has been added, if one has been added.
		 * 
		 * @return The number of occurrences that have been added.
		 */
		public abstract int getAddCount();

		/**
		 * Returns how often an element has been removed, if one has been
		 * removed.
		 * 
		 * @return The number of occurrences that have been removed.
		 */
		public abstract int getRemoveCount();

		/**
		 * Retrieves the element that was added.
		 * 
		 * @return The added element in case an element was added.
		 */
		public abstract E getElement();
	}

	/**
	 * Called after a change has been made to an {@link ObservableMultiset}.
	 * This method is called whenever a change to an element is performed, even
	 * if multiple occurrences have been added/removed. In case of complex
	 * operation like {@link ObservableMultiset#clear()} or
	 * {@link ObservableMultiset#addAll(java.util.Collection)} multiple
	 * invocations will occur.
	 *
	 * @param change
	 *            A {@link Change} object representing the changes that were
	 *            performed on the source {@link ObservableSetMultimap}.
	 */
	void onChanged(Change<? extends E> change);
}
