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
package org.eclipse.gef.common.beans.binding;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.common.collections.ObservableSetMultimap;
import org.eclipse.gef.common.collections.SetMultimapChangeListener;

import com.google.common.collect.SetMultimap;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.beans.binding.ListBinding;
import javafx.beans.binding.MapBinding;
import javafx.beans.binding.SetBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * An abstract base class that provides functionality needed to realize a
 * {@link Binding} on an {@link ObservableSetMultimap}.
 * <p>
 * This class provides identical functionality for {@link SetMultimap} as
 * {@link MapBinding} for {@link Map}, {@link SetBinding} for {@link Set}, or
 * {@link ListBinding} for {@link List}.
 *
 * @author anyssen
 *
 * @param <K>
 *            The key type of the {@link ObservableSetMultimap}.
 * @param <V>
 *            The value type of the {@link ObservableSetMultimap}.
 *
 */
public abstract class SetMultimapBinding<K, V>
		extends SetMultimapExpression<K, V>
		implements Binding<ObservableSetMultimap<K, V>> {

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
			return SetMultimapBinding.this;
		}

		@Override
		public String getName() {
			return "empty";
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
			return SetMultimapBinding.this;
		}

		@Override
		public String getName() {
			return "size";
		}
	}

	private SetMultimapExpressionHelper<K, V> helper = null;

	private SetMultimapChangeListener<K, V> invalidatingValueObserver = new SetMultimapChangeListener<K, V>() {

		@Override
		public void onChanged(
				org.eclipse.gef.common.collections.SetMultimapChangeListener.Change<? extends K, ? extends V> change) {
			invalidateProperties();
			invalidated();
			fireValueChangedEvent(change);
		}
	};
	private ObservableSetMultimap<K, V> value = null;

	private InvalidationListener invalidatingDependenciesObserver = new InvalidationListener() {

		@Override
		public void invalidated(Observable observable) {
			invalidate();
		}
	};
	private ObservableList<Observable> dependencies = null;

	private boolean valid = false;

	private EmptyProperty emptyProperty = null;
	private SizeProperty sizeProperty = null;

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

	/**
	 * Start observing the given dependencies for changes. If the value of one
	 * of the dependencies changes, the binding is marked as invalid.
	 *
	 * @param dependencies
	 *            The dependencies to observe.
	 */
	protected void bind(Observable... dependencies) {
		if ((dependencies != null) && (dependencies.length > 0)) {
			if (this.dependencies == null) {
				this.dependencies = FXCollections.observableArrayList();
			}
			for (final Observable d : dependencies) {
				if (d != null) {
					this.dependencies.add(d);
					d.addListener(invalidatingDependenciesObserver);
				}
			}
		}
	}

	/**
	 * Computes the current value of this {@link SetMultimapBinding}.
	 *
	 * @return The current value of this {@link SetMultimapBinding}.
	 */
	protected abstract ObservableSetMultimap<K, V> computeValue();

	@Override
	public void dispose() {
		if (dependencies != null) {
			unbind(dependencies.toArray(new Observable[] {}));
		}
		invalidatingDependenciesObserver = null;
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

	@Override
	public ObservableSetMultimap<K, V> get() {
		if (!valid) {
			value = computeValue();
			valid = true;
			if (value != null) {
				value.addListener(invalidatingValueObserver);
			}
		}
		return value;
	}

	@Override
	public ObservableList<?> getDependencies() {
		if (dependencies == null) {
			return FXCollections.emptyObservableList();
		}
		return FXCollections.unmodifiableObservableList(dependencies);
	}

	@Override
	public void invalidate() {
		if (valid) {
			if (value != null) {
				value.removeListener(invalidatingValueObserver);
			}
			valid = false;
			invalidateProperties();
			invalidated();
			fireValueChangedEvent();
		}
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
	public boolean isValid() {
		return valid;
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
		if (sizeProperty == null) {
			sizeProperty = new SizeProperty();
		}
		return sizeProperty;
	}

	/**
	 * Stops observing the dependencies for changes. The binding will no longer
	 * be marked as invalid if one of the dependencies changes.
	 *
	 * @param dependencies
	 *            The dependencies to stop observing.
	 */
	protected void unbind(Observable... dependencies) {
		if (this.dependencies != null) {
			for (final Observable d : dependencies) {
				if (d != null) {
					this.dependencies.remove(d);
					d.removeListener(invalidatingDependenciesObserver);
				}
			}
			if (this.dependencies.size() == 0) {
				this.dependencies = null;
			}
		}
	}
}
