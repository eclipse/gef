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
package org.eclipse.gef.common.beans.property;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.common.beans.binding.MultisetExpressionHelper;
import org.eclipse.gef.common.collections.MultisetChangeListener;
import org.eclipse.gef.common.collections.ObservableMultiset;

import com.google.common.collect.Multiset;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ListPropertyBase;
import javafx.beans.property.MapPropertyBase;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.property.SetPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Abstract base class for implementing a {@link Property} wrapping an
 * {@link ObservableMultiset}.
 * <p>
 * This class provides identical functionality for {@link Multiset} as
 * {@link MapPropertyBase} for {@link Map}, {@link SetPropertyBase} for
 * {@link Set}, or {@link ListPropertyBase} for {@link List}.
 *
 * @author anyssen
 *
 * @param <E>
 *            The element type of the wrapped {@link ObservableMultiset}.
 */
public abstract class MultisetPropertyBase<E> extends MultisetProperty<E> {

	private class EmptyProperty extends ReadOnlyBooleanPropertyBase {

		@Override
		protected void fireValueChangedEvent() {
			super.fireValueChangedEvent();
		}

		@Override
		public boolean get() {
			return isEmpty();
		}

		@Override
		public Object getBean() {
			return MultisetPropertyBase.this;
		}

		@Override
		public String getName() {
			return "empty";
		}
	}

	private static class InvalidatingObserver<E>
			implements InvalidationListener {

		private WeakReference<MultisetPropertyBase<E>> multisetPropertyRef;

		public InvalidatingObserver(
				MultisetPropertyBase<E> setMultimapProperty) {
			this.multisetPropertyRef = new WeakReference<>(setMultimapProperty);
		}

		@Override
		public void invalidated(Observable observable) {
			MultisetPropertyBase<E> multisetProperty = multisetPropertyRef
					.get();
			if (multisetProperty == null) {
				observable.removeListener(this);
			} else {
				multisetProperty.markInvalid(multisetProperty.value);
			}
		}
	}

	private class SizeProperty extends ReadOnlyIntegerPropertyBase {
		@Override
		protected void fireValueChangedEvent() {
			super.fireValueChangedEvent();
		}

		@Override
		public int get() {
			return size();
		}

		@Override
		public Object getBean() {
			return MultisetPropertyBase.this;
		}

		@Override
		public String getName() {
			return "size";
		}
	}

	private final MultisetChangeListener<E> invalidatingValueObserver = new MultisetChangeListener<E>() {

		@Override
		public void onChanged(
				MultisetChangeListener.Change<? extends E> change) {
			invalidateProperties();
			invalidated();
			fireValueChangedEvent(change);
		}
	};

	private MultisetExpressionHelper<E> helper = null;
	private ObservableMultiset<E> value = null;
	private boolean valid = true;

	private SizeProperty sizeProperty = null;
	private EmptyProperty emptyProperty = null;

	// the observed value this property is bound to
	private ObservableValue<? extends ObservableMultiset<E>> observedValue = null;
	private InvalidationListener invalidatingObservedValueObserver = null;

	/**
	 * Creates a new {@link MultisetPropertyBase} with no initial value.
	 */
	public MultisetPropertyBase() {
	}

	/**
	 * Creates a new {@link MultisetPropertyBase} with the given
	 * {@link ObservableMultiset} as initial value.
	 *
	 * @param initialValue
	 *            The initial value of the to be created
	 *            {@link MultisetPropertyBase}.
	 */
	public MultisetPropertyBase(ObservableMultiset<E> initialValue) {
		this.value = initialValue;
		if (initialValue != null) {
			initialValue.addListener(invalidatingValueObserver);
		}
	}

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
	protected void appendValueToString(StringBuilder result) {
		if (isBound()) {
			result.append("bound, ");
			if (valid) {
				result.append("value: " + get());
			} else {
				result.append("invalid");
			}
		} else {
			result.append("value: " + get());
		}
	}

	@Override
	public void bind(
			final ObservableValue<? extends ObservableMultiset<E>> observedValue) {
		if (observedValue == null) {
			// While according to GEF conventions we would rather throw an
			// IllegalArgumentException here, JavaFX seems to request an NPE.
			throw new NullPointerException("Cannot bind to null.");
		}
		if (!observedValue.equals(this.observedValue)) {
			unbind();
			this.observedValue = observedValue;
			if (invalidatingObservedValueObserver == null) {
				invalidatingObservedValueObserver = new InvalidatingObserver<>(
						this);
			}
			observedValue.addListener(invalidatingObservedValueObserver);
			markInvalid(value);
		}
	}

	@Override
	public ReadOnlyBooleanProperty emptyProperty() {
		if (emptyProperty == null) {
			emptyProperty = new EmptyProperty();
		}
		return emptyProperty;
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

	@Override
	public ObservableMultiset<E> get() {
		if (!valid) {
			value = observedValue == null ? value : observedValue.getValue();
			valid = true;
			if (value != null) {
				value.addListener(invalidatingValueObserver);
			}
		}
		return value;
	}

	/**
	 * Can be overwritten by subclasses to receive invalidation notifications.
	 * Does nothing by default.
	 */
	protected void invalidated() {
	}

	private void invalidateProperties() {
		if (sizeProperty != null) {
			sizeProperty.fireValueChangedEvent();
		}
		if (emptyProperty != null) {
			emptyProperty.fireValueChangedEvent();
		}
	}

	@Override
	public boolean isBound() {
		return observedValue != null;
	}

	private void markInvalid(ObservableMultiset<E> oldValue) {
		if (valid) {
			if (oldValue != null) {
				oldValue.removeListener(invalidatingValueObserver);
			}
			valid = false;
			invalidateProperties();
			invalidated();
			fireValueChangedEvent();
		}
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
	public void set(ObservableMultiset<E> newValue) {
		if (isBound()) {
			throw new IllegalArgumentException("A bound value cannot be set.");
		}
		if (value != newValue) {
			final ObservableMultiset<E> oldValue = value;
			value = newValue;
			markInvalid(oldValue);
		}
	}

	@Override
	public ReadOnlyIntegerProperty sizeProperty() {
		if (sizeProperty == null) {
			sizeProperty = new SizeProperty();
		}
		return sizeProperty;
	}

	@Override
	public void unbind() {
		if (observedValue != null) {
			value = observedValue.getValue();
			observedValue.removeListener(invalidatingObservedValueObserver);
			invalidatingObservedValueObserver = null;
			observedValue = null;
		}
	}

}
