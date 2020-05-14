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
import org.eclipse.gef.common.collections.MultisetChangeListener.Change;

import com.google.common.collect.Multiset;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.value.ChangeListener;

/**
 * A {@link ReadOnlyMultisetWrapper} is a writable {@link Property} wrapping an
 * {@link ObservableMultiset}, which provides an additional read-only
 * {@link Property} (based on a concrete, non-exposed implementation of
 * {@link ReadOnlyMultisetProperty}), whose value is synchronized with the value
 * of this {@link ReadOnlyMapWrapper}.
 * <p>
 * This class provides identical functionality for {@link Multiset} as
 * {@link ReadOnlyMapWrapper} for {@link Map}, {@link ReadOnlySetWrapper} for
 * {@link Set}, or {@link ReadOnlyListWrapper} for {@link List}.
 * 
 * @author anyssen
 * @param <E>
 *            The element type of the wrapped {@link ObservableMultiset}.
 *
 */
public class ReadOnlyMultisetWrapper<E> extends SimpleMultisetProperty<E> {

	private class ReadOnlyPropertyImpl extends ReadOnlyMultisetProperty<E> {

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
		public void addListener(InvalidationListener listener) {
			if (helper == null) {
				helper = new MultisetExpressionHelper<>(this);
			}
			helper.addListener(listener);
		}

		@Override
		public void addListener(MultisetChangeListener<? super E> listener) {
			if (helper == null) {
				helper = new MultisetExpressionHelper<>(this);
			}
			helper.addListener(listener);
		}

		@Override
		public ReadOnlyBooleanProperty emptyProperty() {
			return ReadOnlyMultisetWrapper.this.emptyProperty();
		}

		private void fireValueChangedEvent() {
			if (helper == null) {
				helper = new MultisetExpressionHelper<>(this);
			}
			helper.fireValueChangedEvent();
		}

		private void fireValueChangedEvent(Change<? extends E> change) {
			if (helper == null) {
				helper = new MultisetExpressionHelper<>(this);
			}
			helper.fireValueChangedEvent(change);
		}

		@Override
		public ObservableMultiset<E> get() {
			return ReadOnlyMultisetWrapper.this.get();
		}

		@Override
		public Object getBean() {
			return ReadOnlyMultisetWrapper.this.getBean();
		}

		@Override
		public String getName() {
			return ReadOnlyMultisetWrapper.this.getName();
		}

		@Override
		public void removeListener(
				ChangeListener<? super ObservableMultiset<E>> listener) {
			if (helper != null) {
				helper.removeListener(listener);
			}
		}

		@Override
		public void removeListener(InvalidationListener listener) {
			if (helper != null) {
				helper.removeListener(listener);
			}
		}

		@Override
		public void removeListener(MultisetChangeListener<? super E> listener) {
			if (helper != null) {
				helper.removeListener(listener);
			}
		}

		@Override
		public ReadOnlyIntegerProperty sizeProperty() {
			return ReadOnlyMultisetWrapper.this.sizeProperty();
		}
	}

	private ReadOnlyPropertyImpl readOnlyProperty = null;

	/**
	 * Constructs a new unnamed {@link ReadOnlyMultisetWrapper}.
	 */
	public ReadOnlyMultisetWrapper() {
		super();
	}

	/**
	 * Constructs a new {@link ReadOnlyMultisetWrapper} for the given bean and
	 * with the given name.
	 * 
	 * @param bean
	 *            The bean the to be created {@link ReadOnlyMultisetWrapper} is
	 *            to be related to.
	 * @param name
	 *            The name of the to be created {@link ReadOnlyMultisetWrapper}
	 */
	public ReadOnlyMultisetWrapper(Object bean, String name) {
		super(bean, name);
	}

	/**
	 * Constructs a new {@link ReadOnlyMultisetWrapper} for the given bean and
	 * with the given name and initial value.
	 * 
	 * @param bean
	 *            The bean the to be created {@link ReadOnlyMultisetWrapper} is
	 *            to be related to.
	 * @param name
	 *            The name of the to be created {@link ReadOnlyMultisetWrapper}
	 * @param initialValue
	 *            The initial value for the to be created
	 *            {@link ReadOnlyMultisetWrapper}.
	 */
	public ReadOnlyMultisetWrapper(Object bean, String name,
			ObservableMultiset<E> initialValue) {
		super(bean, name, initialValue);
	}

	/**
	 * Constructs a new unnamed {@link ReadOnlyMultisetWrapper} with the given
	 * initial value.
	 * 
	 * @param initialValue
	 *            The initial value for the to be created
	 *            {@link ReadOnlyMultisetWrapper}.
	 */
	public ReadOnlyMultisetWrapper(ObservableMultiset<E> initialValue) {
		super(initialValue);
	}

	@Override
	protected void fireValueChangedEvent() {
		super.fireValueChangedEvent();
		if (readOnlyProperty != null) {
			readOnlyProperty.fireValueChangedEvent();
		}
	}

	@Override
	protected void fireValueChangedEvent(Change<? extends E> change) {
		super.fireValueChangedEvent(change);
		if (readOnlyProperty != null) {
			readOnlyProperty.fireValueChangedEvent(change);
		}
	}

	/**
	 * Returns the read-only {@link Property}, whose value is synchronized with
	 * this {@link ReadOnlyMultisetWrapper}.
	 *
	 * @return The {@link ReadOnlyMultisetProperty} that is synchronized with
	 *         this {@link ReadOnlyMultisetWrapper}.
	 */
	public ReadOnlyMultisetProperty<E> getReadOnlyProperty() {
		if (readOnlyProperty == null) {
			readOnlyProperty = new ReadOnlyPropertyImpl();
		}
		return readOnlyProperty;
	}
}
