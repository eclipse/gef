/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.internal.properties;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 * The {@link ReadOnlyMapWrapperEx} extends the {@link ReadOnlyMapWrapper} to
 * fix JavaFX bug https://bugs.openjdk.java.net/browse/JDK-8136465, i.e. it
 * keeps track of all listeners and ensures that remaining listeners are
 * re-added when a listener is removed.
 *
 * @author mwienand
 *
 * @param <K>
 *            Type parameter specifying the key type.
 * @param <V>
 *            Type parameter specifying the value type.
 */
public class ReadOnlyMapWrapperEx<K, V> extends ReadOnlyMapWrapper<K, V> {

	private List<ChangeListener<? super ObservableMap<K, V>>> changeListeners = new ArrayList<ChangeListener<? super ObservableMap<K, V>>>();
	private List<MapChangeListener<? super K, ? super V>> mapChangeListeners = new ArrayList<MapChangeListener<? super K, ? super V>>();
	private List<InvalidationListener> invalidationListeners = new ArrayList<InvalidationListener>();

	/**
	 * Constructs a new {@link ReadOnlyMapWrapperEx} that wraps the given
	 * {@link ObservableMap}.
	 *
	 * @param observableHashMap
	 *            The {@link ObservableMap} that is wrapped by this
	 *            {@link ReadOnlyMapWrapperEx}.
	 */
	public ReadOnlyMapWrapperEx(ObservableMap<K, V> observableHashMap) {
		super(observableHashMap);
	}

	@Override
	public void addListener(
			ChangeListener<? super ObservableMap<K, V>> listener) {
		// keep track of all registered change listeners
		changeListeners.add(listener);
		super.addListener(listener);
	}

	@Override
	public void addListener(InvalidationListener listener) {
		// keep track of all registered invalidation listeners
		invalidationListeners.add(listener);
		super.addListener(listener);
	}

	@Override
	public void addListener(MapChangeListener<? super K, ? super V> listener) {
		// keep track of all registered map change listeners
		mapChangeListeners.add(listener);
		super.addListener(listener);
	}

	@Override
	public void removeListener(
			ChangeListener<? super ObservableMap<K, V>> listener) {
		super.removeListener(listener);
		changeListeners.remove(listener);

		// IMPORTANT: Due to the JavaFX bug
		// https://bugs.openjdk.java.net/browse/JDK-8136465, which leads to a
		// removal of all listeners when a single listener is removed, we have
		// to re-add all remaining listeners. However, since the current JavaFX
		// version might not contain the bug, we have to remove all remaining
		// listeners first, so that they are not notified twice.
		for (ChangeListener<? super ObservableMap<K, V>> l : changeListeners) {
			super.removeListener(l);
		}
		for (ChangeListener<? super ObservableMap<K, V>> l : changeListeners) {
			super.addListener(l);
		}
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		super.removeListener(listener);
		invalidationListeners.remove(listener);

		// IMPORTANT: Due to the JavaFX bug
		// https://bugs.openjdk.java.net/browse/JDK-8136465, which leads to a
		// removal of all listeners when a single listener is removed, we have
		// to re-add all remaining listeners. However, since the current JavaFX
		// version might not contain the bug, we have to remove all remaining
		// listeners first, so that they are not notified twice.
		for (InvalidationListener l : invalidationListeners) {
			super.removeListener(l);
		}
		for (InvalidationListener l : invalidationListeners) {
			super.addListener(l);
		}
	}

	@Override
	public void removeListener(
			MapChangeListener<? super K, ? super V> listener) {
		super.removeListener(listener);
		mapChangeListeners.remove(listener);

		// IMPORTANT: Due to the JavaFX bug
		// https://bugs.openjdk.java.net/browse/JDK-8136465, which leads to a
		// removal of all listeners when a single listener is removed, we have
		// to re-add all remaining listeners. However, since the current JavaFX
		// version might not contain the bug, we have to remove all remaining
		// listeners first, so that they are not notified twice.
		for (MapChangeListener<? super K, ? super V> l : mapChangeListeners) {
			super.removeListener(l);
		}
		for (MapChangeListener<? super K, ? super V> l : mapChangeListeners) {
			super.addListener(l);
		}
	}

}
