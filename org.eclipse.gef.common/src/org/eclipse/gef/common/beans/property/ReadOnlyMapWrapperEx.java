/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.common.beans.property;

import org.eclipse.gef.common.beans.binding.MapExpressionHelperEx;

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
 * A replacement for {@link ReadOnlyMapWrapper} to fix the following JavaFX
 * issues:
 * <ul>
 * <li>All listeners were removed when removing one
 * (https://bugs.openjdk.java.net/browse/JDK-8136465): fixed by keeping track of
 * all listeners and ensuring that remaining listeners are re-added when a
 * listener is removed.</li>
 * <li>Change notifications are fired even when the observed value did not
 * change.(https://bugs.openjdk.java.net/browse/JDK-8089169)</li>
 * <li>Bidirectional binding not working
 * (https://bugs.openjdk.java.net/browse/JDK-8089557): fixed by not forwarding
 * listeners to the nested read-only property but rather keeping the lists of
 * listeners distinct.</li>
 * </ul>
 *
 * @author mwienand
 * @author anyssen
 *
 * @param <K>
 *            The key type of the wrapped {@link ObservableMap}.
 * @param <V>
 *            The value type of the wrapped {@link ObservableMap}.
 */
public class ReadOnlyMapWrapperEx<K, V> extends ReadOnlyMapWrapper<K, V> {

	private class ReadOnlyPropertyImplEx extends ReadOnlyMapProperty<K, V> {

		private MapExpressionHelperEx<K, V> helper = null;

		@Override
		public void addListener(
				ChangeListener<? super ObservableMap<K, V>> listener) {
			if (helper == null) {
				helper = new MapExpressionHelperEx<>(this);
			}
			helper.addListener(listener);
		}

		@Override
		public void addListener(InvalidationListener listener) {
			if (helper == null) {
				helper = new MapExpressionHelperEx<>(this);
			}
			helper.addListener(listener);
		}

		@Override
		public void addListener(
				MapChangeListener<? super K, ? super V> listener) {
			if (helper == null) {
				helper = new MapExpressionHelperEx<>(this);
			}
			helper.addListener(listener);
		}

		@Override
		public ReadOnlyBooleanProperty emptyProperty() {
			return ReadOnlyMapWrapperEx.this.emptyProperty();
		}

		private void fireValueChangedEvent() {
			if (helper != null) {
				helper.fireValueChangedEvent();
			}
		}

		private void fireValueChangedEvent(
				Change<? extends K, ? extends V> change) {
			if (helper != null) {
				helper.fireValueChangedEvent(change);
			}
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
				MapChangeListener<? super K, ? super V> listener) {
			if (helper != null) {
				helper.removeListener(listener);
			}
		}

		@Override
		public ReadOnlyIntegerProperty sizeProperty() {
			return ReadOnlyMapWrapperEx.this.sizeProperty();
		}
	}

	private MapExpressionHelperEx<K, V> helper = null;
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
		// don't delegate to read-only property (fix for
		// https://bugs.openjdk.java.net/browse/JDK-8089557)
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
		// don't delegate to read-only property (fix for
		// https://bugs.openjdk.java.net/browse/JDK-8089557)
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
		// don't delegate to read-only property (fix for
		// https://bugs.openjdk.java.net/browse/JDK-8089557)
		if (helper == null) {
			helper = new MapExpressionHelperEx<>(this);
		}
		helper.addListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fireValueChangedEvent() {
		if (helper != null) {
			helper.fireValueChangedEvent();
		}
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
		if (helper != null) {
			helper.fireValueChangedEvent(change);
		}
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
		// don't delegate to read-only property (fix for
		// https://bugs.openjdk.java.net/browse/JDK-8089557)
		if (helper != null) {
			helper.removeListener(listener);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(InvalidationListener listener) {
		// don't delegate to read-only property (fix for
		// https://bugs.openjdk.java.net/browse/JDK-8089557)
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
		// don't delegate to read-only property (fix for
		// https://bugs.openjdk.java.net/browse/JDK-8089557)
		if (helper != null) {
			helper.removeListener(listener);
		}
	}

}
