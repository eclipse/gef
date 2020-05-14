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

import com.google.common.collect.SetMultimap;

import javafx.beans.Observable;

/**
 * An {@link ObservableSetMultimap} is a specific {@link SetMultimap} that
 * allows observers to track changes by registering
 * {@link SetMultimapChangeListener SetMultimapChangeListeners}.
 * 
 * @author anyssen
 *
 * @param <K>
 *            The key type of the {@link ObservableSetMultimap}.
 * @param <V>
 *            The value type of the {@link ObservableSetMultimap}.
 */
public interface ObservableSetMultimap<K, V>
		extends SetMultimap<K, V>, Observable {

	/**
	 * Replaces all the contents of the {@link ObservableSetMultimap} with the
	 * contents provided by the given {@link SetMultimap}.
	 * 
	 * @param setMultimap
	 *            The {@link SetMultimap} whose values should be used to replace
	 *            those of this {@link ObservableSetMultimap}.
	 * @return Whether this map was changed through the replace operation.
	 */
	boolean replaceAll(SetMultimap<? extends K, ? extends V> setMultimap);

	/**
	 * Adds a {@link SetMultimapChangeListener} to this
	 * {@link ObservableSetMultimap}. If the same listener is registered more
	 * than once, it will be notified more than once.
	 * 
	 * @param listener
	 *            The {@link SetMultimapChangeListener} to add.
	 */
	public void addListener(
			SetMultimapChangeListener<? super K, ? super V> listener);

	/**
	 * Removes a {@link SetMultimapChangeListener} from this
	 * {@link ObservableSetMultimap}. Will do nothing if the listener was not
	 * attached to this {@link ObservableSetMultimap}. If it was added more than
	 * once, then only the first occurrence will be removed.
	 * 
	 * @param listener
	 *            The {@link SetMultimapChangeListener} to remove.
	 */
	public void removeListener(
			SetMultimapChangeListener<? super K, ? super V> listener);
}
