/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
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

import org.eclipse.gef.common.beans.binding.ListExpressionHelperEx;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

/**
 * A replacement for {@link ReadOnlyListWrapper} to fix the following JavaFX
 * issues:
 * <ul>
 * <li>Change notifications are fired even when the observed value did not
 * change.(https://bugs.openjdk.java.net/browse/JDK-8089169)</li>
 * <li>Bidirectional binding not working
 * (https://bugs.openjdk.java.net/browse/JDK-8089557): fixed by not forwarding
 * listeners to the nested read-only property but rather keeping the lists of
 * listeners distinct.</li>
 * </ul>
 *
 * @author anyssen
 *
 * @param <E>
 *            The element type of the wrapped {@link ObservableList}.
 */
public class ReadOnlyListWrapperEx<E> extends ReadOnlyListWrapper<E> {

	private class ReadOnlyPropertyImpl extends ReadOnlyListProperty<E> {

		private ListExpressionHelperEx<E> helper = null;

		@Override
		public void addListener(
				ChangeListener<? super ObservableList<E>> listener) {
			if (helper == null) {
				helper = new ListExpressionHelperEx<>(this);
			}
			helper.addListener(listener);
		}

		@Override
		public void addListener(InvalidationListener listener) {
			if (helper == null) {
				helper = new ListExpressionHelperEx<>(this);
			}
			helper.addListener(listener);
		}

		@Override
		public void addListener(ListChangeListener<? super E> listener) {
			if (helper == null) {
				helper = new ListExpressionHelperEx<>(this);
			}
			helper.addListener(listener);
		}

		@Override
		public ReadOnlyBooleanProperty emptyProperty() {
			return ReadOnlyListWrapperEx.this.emptyProperty();
		}

		private void fireValueChangedEvent() {
			if (helper == null) {
				helper = new ListExpressionHelperEx<>(this);
			}
			helper.fireValueChangedEvent();
		}

		private void fireValueChangedEvent(Change<? extends E> change) {
			if (helper == null) {
				helper = new ListExpressionHelperEx<>(this);
			}
			helper.fireValueChangedEvent(change);
		}

		@Override
		public ObservableList<E> get() {
			return ReadOnlyListWrapperEx.this.get();
		}

		@Override
		public Object getBean() {
			return ReadOnlyListWrapperEx.this.getBean();
		}

		@Override
		public String getName() {
			return ReadOnlyListWrapperEx.this.getName();
		}

		@Override
		public void removeListener(
				ChangeListener<? super ObservableList<E>> listener) {
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
		public void removeListener(ListChangeListener<? super E> listener) {
			if (helper != null) {
				helper.removeListener(listener);
			}
		}

		@Override
		public ReadOnlyIntegerProperty sizeProperty() {
			return ReadOnlyListWrapperEx.this.sizeProperty();
		}
	}

	private ReadOnlyPropertyImpl readOnlyProperty = null;

	private ListExpressionHelperEx<E> helper = null;

	/**
	 * Creates a new unnamed {@link ReadOnlyListWrapperEx}.
	 */
	public ReadOnlyListWrapperEx() {
		super();
	}

	/**
	 * Creates a new named {@link ReadOnlyListWrapperEx} related to the given
	 * bean.
	 *
	 * @param bean
	 *            The bean to relate the to be created
	 *            {@link ReadOnlyListWrapperEx} to.
	 * @param name
	 *            The name for the to be created {@link ReadOnlyListWrapperEx}.
	 */
	public ReadOnlyListWrapperEx(Object bean, String name) {
		super(bean, name);
	}

	/**
	 * Creates a new named {@link ReadOnlyListWrapperEx}, related to the given
	 * bean and provided with the initial value.
	 *
	 * @param bean
	 *            The bean to relate the to be created
	 *            {@link ReadOnlyListWrapperEx} to.
	 * @param name
	 *            The name for the to be created {@link ReadOnlyListWrapperEx}.
	 * @param initialValue
	 *            The initial value of the to be created
	 *            {@link ReadOnlyListWrapperEx}.
	 */
	public ReadOnlyListWrapperEx(Object bean, String name,
			ObservableList<E> initialValue) {
		super(bean, name, initialValue);
	}

	/**
	 * Creates a new unnamed {@link ReadOnlyListWrapperEx} with the given
	 * initial value.
	 *
	 * @param initialValue
	 *            The initial value of the to be created
	 *            {@link ReadOnlyListWrapperEx}.
	 */
	public ReadOnlyListWrapperEx(ObservableList<E> initialValue) {
		super(initialValue);
	}

	@Override
	public void addListener(
			ChangeListener<? super ObservableList<E>> listener) {
		if (helper == null) {
			helper = new ListExpressionHelperEx<>(this);
		}
		helper.addListener(listener);
	}

	@Override
	public void addListener(InvalidationListener listener) {
		if (helper == null) {
			helper = new ListExpressionHelperEx<>(this);
		}
		helper.addListener(listener);
	}

	@Override
	public void addListener(ListChangeListener<? super E> listener) {
		if (helper == null) {
			helper = new ListExpressionHelperEx<>(this);
		}
		helper.addListener(listener);
	}

	@Override
	protected void fireValueChangedEvent() {
		if (helper != null) {
			helper.fireValueChangedEvent();
		}
		if (readOnlyProperty != null) {
			readOnlyProperty.fireValueChangedEvent();
		}
	}

	@Override
	protected void fireValueChangedEvent(
			ListChangeListener.Change<? extends E> change) {
		if (helper != null) {
			helper.fireValueChangedEvent(change);
		}
		if (readOnlyProperty != null) {
			readOnlyProperty.fireValueChangedEvent(change);
		}
	}

	@Override
	public ReadOnlyListProperty<E> getReadOnlyProperty() {
		if (readOnlyProperty == null) {
			readOnlyProperty = new ReadOnlyPropertyImpl();
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

	@Override
	public void removeListener(
			ChangeListener<? super ObservableList<E>> listener) {
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
	public void removeListener(ListChangeListener<? super E> listener) {
		if (helper != null) {
			helper.removeListener(listener);
		}
	}

	// TODO: overwrite sort(Comparator) and replaceAll(UnaryOperator) as well,
	// as soon as we drop JavaSE-1.7 support.
}
