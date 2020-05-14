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
package org.eclipse.gef.common.beans.property;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.common.beans.binding.SetMultimapExpressionHelper;
import org.eclipse.gef.common.collections.ObservableSetMultimap;
import org.eclipse.gef.common.collections.SetMultimapChangeListener;

import com.google.common.collect.SetMultimap;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListPropertyBase;
import javafx.beans.property.ReadOnlyMapPropertyBase;
import javafx.beans.property.ReadOnlySetPropertyBase;
import javafx.beans.value.ChangeListener;

/**
 * Abstract base class for implementing a read-only {@link Property} wrapping an
 * {@link ObservableSetMultimap}.
 * <p>
 * This class provides identical functionality for {@link SetMultimap} as
 * {@link ReadOnlyMapPropertyBase} for {@link Map},
 * {@link ReadOnlySetPropertyBase} for {@link Set}, or
 * {@link ReadOnlyListPropertyBase} for {@link List}.
 * 
 * @author anyssen
 *
 * @param <K>
 *            The key type of the {@link ObservableSetMultimap}.
 * @param <V>
 *            The value type of the {@link ObservableSetMultimap}.
 */
public abstract class ReadOnlySetMultimapPropertyBase<K, V>
		extends ReadOnlySetMultimapProperty<K, V> {

	private SetMultimapExpressionHelper<K, V> helper = null;

	@Override
	public void addListener(
			ChangeListener<? super ObservableSetMultimap<K, V>> listener) {
		if (helper == null) {
			helper = new SetMultimapExpressionHelper<>(this);
		}
		helper.addListener(listener);
	}

	@Override
	public void removeListener(
			ChangeListener<? super ObservableSetMultimap<K, V>> listener) {
		if (helper != null) {
			helper.removeListener(listener);
		}
	}

	@Override
	public void addListener(InvalidationListener listener) {
		if (helper == null) {
			helper = new SetMultimapExpressionHelper<>(this);
		}
		helper.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		if (helper != null) {
			helper.removeListener(listener);
		}
	}

	@Override
	public void addListener(
			SetMultimapChangeListener<? super K, ? super V> listener) {
		if (helper == null) {
			helper = new SetMultimapExpressionHelper<>(this);
		}
		helper.addListener(listener);
	}

	@Override
	public void removeListener(
			SetMultimapChangeListener<? super K, ? super V> listener) {
		if (helper != null) {
			helper.removeListener(listener);
		}
	}

	/**
	 * Fires notifications to all attached
	 * {@link javafx.beans.InvalidationListener InvalidationListeners},
	 * {@link javafx.beans.value.ChangeListener ChangeListeners}, and
	 * {@link SetMultimapChangeListener SetMultimapChangeListeners}.
	 * 
	 */
	protected void fireValueChangedEvent() {
		if (helper != null) {
			helper.fireValueChangedEvent();
		}
	}

	/**
	 * Fires notifications to all attached
	 * {@link javafx.beans.InvalidationListener InvalidationListeners},
	 * {@link javafx.beans.value.ChangeListener ChangeListeners}, and
	 * {@link SetMultimapChangeListener SetMultimapChangeListeners}.
	 *
	 * @param change
	 *            the change that needs to be propagated
	 */
	protected void fireValueChangedEvent(
			SetMultimapChangeListener.Change<? extends K, ? extends V> change) {
		if (helper != null) {
			helper.fireValueChangedEvent(change);
		}
	}
}
