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
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.internal;

import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.binding.MapExpressionHelper;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableMap;

/**
 * The {@link ReadOnlyMapWrapperEx} extends the {@link ReadOnlyMapWrapper} to
 * fix JavaFX bug https://bugs.openjdk.java.net/browse/JDK-8136465, i.e. it
 * keeps track of all listeners and ensures that remaining listeners are
 * re-added when a listener is removed.
 *
 * @author mwienand
 * @author anyssen
 *
 * @param <K>
 *            Type parameter specifying the key type.
 * @param <V>
 *            Type parameter specifying the value type.
 */
public class ReadOnlyMapWrapperEx<K, V> extends ReadOnlyMapWrapper<K, V> {

	private class ReadOnlyPropertyImplEx extends ReadOnlyMapProperty<K, V> {

		private List<ChangeListener<? super ObservableMap<K, V>>> changeListeners = new ArrayList<>();
		private List<MapChangeListener<? super K, ? super V>> mapChangeListeners = new ArrayList<>();
		private List<InvalidationListener> invalidationListeners = new ArrayList<>();

		private MapExpressionHelper<K, V> helper = null;

		@Override
		public void addListener(
				ChangeListener<? super ObservableMap<K, V>> listener) {
			// keep track of all registered change listeners
			changeListeners.add(listener);
			helper = MapExpressionHelper.addListener(helper, this, listener);
		}

		@Override
		public void addListener(InvalidationListener listener) {
			// keep track of all registered change listeners
			invalidationListeners.add(listener);
			helper = MapExpressionHelper.addListener(helper, this, listener);
		}

		@Override
		public void addListener(
				MapChangeListener<? super K, ? super V> listener) {
			// keep track of all registered change listeners
			mapChangeListeners.add(listener);
			helper = MapExpressionHelper.addListener(helper, this, listener);
		}

		@Override
		public ReadOnlyBooleanProperty emptyProperty() {
			return ReadOnlyMapWrapperEx.this.emptyProperty();
		}

		private void fireValueChangedEvent() {
			MapExpressionHelper.fireValueChangedEvent(helper);
		}

		private void fireValueChangedEvent(
				Change<? extends K, ? extends V> change) {
			MapExpressionHelper.fireValueChangedEvent(helper, change);
		}

		@Override
		public ObservableMap<K, V> get() {
			return ReadOnlyMapWrapperEx.this.get();
		}

		@Override
		public Object getBean() {
			return ReadOnlyMapWrapperEx.this.getBean();
		}

		@Override
		public String getName() {
			return ReadOnlyMapWrapperEx.this.getName();
		}

		@Override
		public void removeListener(
				ChangeListener<? super ObservableMap<K, V>> listener) {
			// XXX: Due to https://bugs.openjdk.java.net/browse/JDK-8136465,
			// which leads to a removal of all listeners when a single listener
			// is removed, we have to re-add all remaining listeners. However,
			// since the current JavaFX version might not contain the bug, we
			// have to remove all remaining listeners first, so that they are
			// not notified twice.
			for (ChangeListener<? super ObservableMap<K, V>> l : changeListeners) {
				helper = MapExpressionHelper.removeListener(helper, l);
			}
			changeListeners.remove(listener);
			for (ChangeListener<? super ObservableMap<K, V>> l : changeListeners) {
				helper = MapExpressionHelper.addListener(helper, this, l);
			}
		}

		@Override
		public void removeListener(InvalidationListener listener) {
			// XXX: Due to https://bugs.openjdk.java.net/browse/JDK-8136465,
			// which leads to a removal of all listeners when a single listener
			// is removed, we have to re-add all remaining listeners. However,
			// since the current JavaFX version might not contain the bug, we
			// have to remove all remaining listeners first, so that they are
			// not notified twice.
			for (InvalidationListener l : invalidationListeners) {
				helper = MapExpressionHelper.removeListener(helper, l);
			}
			invalidationListeners.remove(listener);
			for (InvalidationListener l : invalidationListeners) {
				helper = MapExpressionHelper.addListener(helper, this, l);
			}
		}

		@Override
		public void removeListener(
				MapChangeListener<? super K, ? super V> listener) {
			// XXX: Due to https://bugs.openjdk.java.net/browse/JDK-8136465,
			// which leads to a removal of all listeners when a single listener
			// is removed, we have to re-add all remaining listeners. However,
			// since the current JavaFX version might not contain the bug, we
			// have to remove all remaining listeners first, so that they are
			// not notified twice.
			for (MapChangeListener<? super K, ? super V> l : mapChangeListeners) {
				helper = MapExpressionHelper.removeListener(helper, l);
			}
			mapChangeListeners.remove(listener);
			for (MapChangeListener<? super K, ? super V> l : mapChangeListeners) {
				helper = MapExpressionHelper.addListener(helper, this, l);
			}
		}

		@Override
		public ReadOnlyIntegerProperty sizeProperty() {
			return ReadOnlyMapWrapperEx.this.sizeProperty();
		}
	}

	private ReadOnlyPropertyImplEx readOnlyProperty;

	/**
	 * The constructor of {@code ReadOnlyMapWrapperEx}
	 */
	public ReadOnlyMapWrapperEx() {
	}

	/**
	 * The constructor of {@code ReadOnlyMapWrapperEx}
	 *
	 * @param bean
	 *            the bean of this {@code ReadOnlyMapWrapperEx}
	 * @param name
	 *            the name of this {@code ReadOnlyMapWrapperEx}
	 */
	public ReadOnlyMapWrapperEx(Object bean, String name) {
		super(bean, name);
	}

	/**
	 * The constructor of {@code ReadOnlyMapWrapperEx}
	 *
	 * @param bean
	 *            the bean of this {@code ReadOnlyMapWrapperEx}
	 * @param name
	 *            the name of this {@code ReadOnlyMapWrapperEx}
	 * @param initialValue
	 *            the initial value of the wrapped value
	 */
	public ReadOnlyMapWrapperEx(Object bean, String name,
			ObservableMap<K, V> initialValue) {
		super(bean, name, initialValue);
	}

	/**
	 * The constructor of {@code ReadOnlyMapWrapperEx}
	 *
	 * @param initialValue
	 *            the initial value of the wrapped value
	 */
	public ReadOnlyMapWrapperEx(ObservableMap<K, V> initialValue) {
		super(initialValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(
			ChangeListener<? super ObservableMap<K, V>> listener) {
		getReadOnlyProperty().addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(InvalidationListener listener) {
		getReadOnlyProperty().addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(MapChangeListener<? super K, ? super V> listener) {
		getReadOnlyProperty().addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fireValueChangedEvent() {
		if (readOnlyProperty != null) {
			readOnlyProperty.fireValueChangedEvent();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fireValueChangedEvent(
			Change<? extends K, ? extends V> change) {
		if (readOnlyProperty != null) {
			readOnlyProperty.fireValueChangedEvent(change);
		}
	}

	/**
	 * Returns the readonly property, that is synchronized with this
	 * {@code ReadOnlyMapWrapper}.
	 *
	 * @return the readonly property
	 */
	@Override
	public ReadOnlyMapProperty<K, V> getReadOnlyProperty() {
		if (readOnlyProperty == null) {
			readOnlyProperty = new ReadOnlyPropertyImplEx();
		}
		return readOnlyProperty;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(
			ChangeListener<? super ObservableMap<K, V>> listener) {
		if (readOnlyProperty != null) {
			readOnlyProperty.removeListener(listener);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(InvalidationListener listener) {
		if (readOnlyProperty != null) {
			readOnlyProperty.removeListener(listener);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(
			MapChangeListener<? super K, ? super V> listener) {
		if (readOnlyProperty != null) {
			readOnlyProperty.removeListener(listener);
		}
	}

}
