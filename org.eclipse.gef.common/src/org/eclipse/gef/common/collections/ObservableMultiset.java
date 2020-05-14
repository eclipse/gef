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

import javafx.beans.Observable;

/**
 * An {@link ObservableMultiset} is a specific {@link Multiset} that allows
 * observers to track changes by registering {@link MultisetChangeListener
 * MultisetChangeListeners}.
 * 
 * @author anyssen
 *
 * @param <E>
 *            The element type of the {@link Multiset}.
 */
public interface ObservableMultiset<E> extends Multiset<E>, Observable {

	/**
	 * Adds a {@link MultisetChangeListener} to this {@link ObservableMultiset}.
	 * If the same listener is registered more than once, it will be notified
	 * more than once.
	 * 
	 * @param listener
	 *            The {@link MultisetChangeListener} to add.
	 */
	public void addListener(MultisetChangeListener<? super E> listener);

	/**
	 * Removes a {@link MultisetChangeListener} from this
	 * {@link ObservableMultiset}. Will do nothing if the listener was not
	 * attached to this {@link ObservableMultiset}. If it was added more than
	 * once, then only the first occurrence will be removed.
	 * 
	 * @param listener
	 *            The {@link MultisetChangeListener} to remove.
	 */
	public void removeListener(MultisetChangeListener<? super E> listener);

	/**
	 * Replaces all the contents of the {@link ObservableMultiset} with the
	 * contents provided by the given {@link Multiset}.
	 * 
	 * @param multiset
	 *            The {@link Multiset} whose values should be used to replace
	 *            those of this {@link ObservableMultiset}.
	 * @return Whether this map was changed through the replace operation.
	 */
	boolean replaceAll(Multiset<? extends E> multiset);
}
