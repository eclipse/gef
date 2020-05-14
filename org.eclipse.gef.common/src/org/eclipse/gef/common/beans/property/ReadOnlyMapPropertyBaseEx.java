/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.common.beans.property;

import org.eclipse.gef.common.beans.binding.MapExpressionHelperEx;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyMapPropertyBase;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 * A replacement for {@link ReadOnlyMapWrapper} to fix the following JavaFX
 * issues:
 * <ul>
 * <li>All listeners were removed when removing one
 * (https://bugs.openjdk.java.net/browse/JDK-8136465): fixed by keeping track of
 * all listeners and ensuring that remaining listeners are re-added when a
 * listener is removed.</li>
 * <li>Change notifications are fired even when the observed value did not
 * change.(https://bugs.openjdk.java.net/browse/JDK-8089169)</li>
 * </ul>
 *
 * @author anyssen
 *
 * @param <K>
 *            The key type of the wrapped {@link ObservableMap}.
 * @param <V>
 *            The value type of the wrapped {@link ObservableMap}.
 */
public abstract class ReadOnlyMapPropertyBaseEx<K, V>
		extends ReadOnlyMapPropertyBase<K, V> {

	private MapExpressionHelperEx<K, V> helper = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(
			ChangeListener<? super ObservableMap<K, V>> listener) {
		if (helper == null) {
			helper = new MapExpressionHelperEx<>(this);
		}
		helper.addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(InvalidationListener listener) {
		if (helper == null) {
			helper = new MapExpressionHelperEx<>(this);
		}
		helper.addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(MapChangeListener<? super K, ? super V> listener) {
		if (helper == null) {
			helper = new MapExpressionHelperEx<>(this);
		}
		helper.addListener(listener);
	}

	@Override
	protected void fireValueChangedEvent() {
		if (helper != null) {
			helper.fireValueChangedEvent();
		}
	}

	@Override
	protected void fireValueChangedEvent(
			MapChangeListener.Change<? extends K, ? extends V> change) {
		if (helper != null) {
			helper.fireValueChangedEvent(change);
		}
	}

	@Override
	public int hashCode() {
		// XXX: As we rely on equality to remove a binding again, we have to
		// ensure the hash code is the same for a pair of given properties.
		// We fall back to the very easiest case here (and use a constant).
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(
			ChangeListener<? super ObservableMap<K, V>> listener) {
		if (helper != null) {
			helper.removeListener(listener);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(InvalidationListener listener) {
		if (helper != null) {
			helper.removeListener(listener);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(
			MapChangeListener<? super K, ? super V> listener) {
		if (helper != null) {
			helper.removeListener(listener);
		}
	}
}
