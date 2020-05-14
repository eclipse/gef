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
import org.eclipse.gef.common.collections.SetMultimapChangeListener.Change;

import com.google.common.collect.SetMultimap;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.value.ChangeListener;

/**
 * A {@link ReadOnlySetMultimapWrapper} is a writable {@link Property} wrapping
 * an {@link ObservableSetMultimap}, which provides an additional read-only
 * {@link Property} (based on a concrete, non-exposed implementation of
 * {@link ReadOnlySetMultimapProperty}), whose value is synchronized with the
 * value of this {@link ReadOnlyMapWrapper}.
 * <p>
 * This class provides identical functionality for {@link SetMultimap} as
 * {@link ReadOnlyMapWrapper} for {@link Map}, {@link ReadOnlySetWrapper} for
 * {@link Set}, or {@link ReadOnlyListWrapper} for {@link List}.
 * 
 * @author anyssen
 * @param <K>
 *            The key type of the wrapped {@link ObservableSetMultimap}.
 * @param <V>
 *            The value type of the wrapped {@link ObservableSetMultimap}.
 *
 */
public class ReadOnlySetMultimapWrapper<K, V>
		extends SimpleSetMultimapProperty<K, V> {

	private class ReadOnlyPropertyImpl
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
		public void addListener(InvalidationListener listener) {
			if (helper == null) {
				helper = new SetMultimapExpressionHelper<>(this);
			}
			helper.addListener(listener);
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
		public ReadOnlyBooleanProperty emptyProperty() {
			return ReadOnlySetMultimapWrapper.this.emptyProperty();
		}

		private void fireValueChangedEvent() {
			if (helper == null) {
				helper = new SetMultimapExpressionHelper<>(this);
			}
			helper.fireValueChangedEvent();
		}

		private void fireValueChangedEvent(
				Change<? extends K, ? extends V> change) {
			if (helper == null) {
				helper = new SetMultimapExpressionHelper<>(this);
			}
			helper.fireValueChangedEvent(change);
		}

		@Override
		public ObservableSetMultimap<K, V> get() {
			return ReadOnlySetMultimapWrapper.this.get();
		}

		@Override
		public Object getBean() {
			return ReadOnlySetMultimapWrapper.this.getBean();
		}

		@Override
		public String getName() {
			return ReadOnlySetMultimapWrapper.this.getName();
		}

		@Override
		public void removeListener(
				ChangeListener<? super ObservableSetMultimap<K, V>> listener) {
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
		public void removeListener(
				SetMultimapChangeListener<? super K, ? super V> listener) {
			if (helper != null) {
				helper.removeListener(listener);
			}
		}

		@Override
		public ReadOnlyIntegerProperty sizeProperty() {
			return ReadOnlySetMultimapWrapper.this.sizeProperty();
		}
	}

	private ReadOnlyPropertyImpl readOnlyProperty = null;

	/**
	 * Constructs a new unnamed {@link ReadOnlySetMultimapWrapper}.
	 */
	public ReadOnlySetMultimapWrapper() {
		super();
	}

	/**
	 * Constructs a new {@link ReadOnlySetMultimapWrapper} for the given bean
	 * and with the given name.
	 * 
	 * @param bean
	 *            The bean the to be created {@link ReadOnlySetMultimapWrapper}
	 *            is to be related to.
	 * @param name
	 *            The name of the to be created
	 *            {@link ReadOnlySetMultimapWrapper}
	 */
	public ReadOnlySetMultimapWrapper(Object bean, String name) {
		super(bean, name);
	}

	/**
	 * Constructs a new {@link ReadOnlySetMultimapWrapper} for the given bean
	 * and with the given name and initial value.
	 * 
	 * @param bean
	 *            The bean the to be created {@link ReadOnlySetMultimapWrapper}
	 *            is to be related to.
	 * @param name
	 *            The name of the to be created
	 *            {@link ReadOnlySetMultimapWrapper}
	 * @param initialValue
	 *            The initial value for the to be created
	 *            {@link ReadOnlySetMultimapWrapper}.
	 */
	public ReadOnlySetMultimapWrapper(Object bean, String name,
			ObservableSetMultimap<K, V> initialValue) {
		super(bean, name, initialValue);
	}

	/**
	 * Constructs a new unnamed {@link ReadOnlySetMultimapWrapper} with the
	 * given initial value.
	 * 
	 * @param initialValue
	 *            The initial value for the to be created
	 *            {@link ReadOnlySetMultimapWrapper}.
	 */
	public ReadOnlySetMultimapWrapper(
			ObservableSetMultimap<K, V> initialValue) {
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
	protected void fireValueChangedEvent(
			Change<? extends K, ? extends V> change) {
		super.fireValueChangedEvent(change);
		if (readOnlyProperty != null) {
			readOnlyProperty.fireValueChangedEvent(change);
		}
	}

	/**
	 * Returns the read-only {@link Property}, whose value is synchronized with
	 * this {@link ReadOnlySetMultimapWrapper}.
	 *
	 * @return The {@link ReadOnlySetMultimapProperty} that is synchronized with
	 *         this {@link ReadOnlySetMultimapWrapper}.
	 */
	public ReadOnlySetMultimapProperty<K, V> getReadOnlyProperty() {
		if (readOnlyProperty == null) {
			readOnlyProperty = new ReadOnlyPropertyImpl();
		}
		return readOnlyProperty;
	}

}
