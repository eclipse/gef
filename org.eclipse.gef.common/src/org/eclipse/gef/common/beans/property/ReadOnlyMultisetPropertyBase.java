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

import org.eclipse.gef.common.beans.binding.MultisetExpressionHelper;
import org.eclipse.gef.common.collections.MultisetChangeListener;
import org.eclipse.gef.common.collections.ObservableMultiset;

import com.google.common.collect.Multiset;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListPropertyBase;
import javafx.beans.property.ReadOnlyMapPropertyBase;
import javafx.beans.property.ReadOnlySetPropertyBase;
import javafx.beans.value.ChangeListener;

/**
 * Abstract base class for implementing a read-only {@link Property} wrapping an
 * {@link ObservableMultiset}.
 * <p>
 * This class provides identical functionality for {@link Multiset} as
 * {@link ReadOnlyMapPropertyBase} for {@link Map},
 * {@link ReadOnlySetPropertyBase} for {@link Set}, or
 * {@link ReadOnlyListPropertyBase} for {@link List}.
 * 
 * @author anyssen
 *
 * @param <E>
 *            The element type of the {@link ObservableMultiset}.
 */
public abstract class ReadOnlyMultisetPropertyBase<E>
		extends ReadOnlyMultisetProperty<E> {

	private MultisetExpressionHelper<E> helper = null;

	@Override
	public void addListener(
			ChangeListener<? super ObservableMultiset<E>> listener) {
		if (helper == null) {
			helper = new MultisetExpressionHelper<>(this);
		}
		helper.addListener(listener);
	}

	@Override
	public void removeListener(
			ChangeListener<? super ObservableMultiset<E>> listener) {
		if (helper != null) {
			helper.removeListener(listener);
		}
	}

	@Override
	public void addListener(InvalidationListener listener) {
		if (helper == null) {
			helper = new MultisetExpressionHelper<>(this);
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
	public void addListener(MultisetChangeListener<? super E> listener) {
		if (helper == null) {
			helper = new MultisetExpressionHelper<>(this);
		}
		helper.addListener(listener);
	}

	@Override
	public void removeListener(MultisetChangeListener<? super E> listener) {
		if (helper != null) {
			helper.removeListener(listener);
		}
	}

	/**
	 * Fires notifications to all attached
	 * {@link javafx.beans.InvalidationListener InvalidationListeners},
	 * {@link javafx.beans.value.ChangeListener ChangeListeners}, and
	 * {@link MultisetChangeListener MultisetChangeListeners}.
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
	 * {@link MultisetChangeListener MultisetChangeListeners}.
	 *
	 * @param change
	 *            the change that needs to be propagated
	 */
	protected void fireValueChangedEvent(
			MultisetChangeListener.Change<? extends E> change) {
		if (helper != null) {
			helper.fireValueChangedEvent(change);
		}
	}
}
