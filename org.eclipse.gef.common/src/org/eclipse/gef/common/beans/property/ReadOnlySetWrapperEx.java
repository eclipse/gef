/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.common.beans.property;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.common.beans.binding.SetExpressionHelperEx;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.SetChangeListener.Change;

/**
 * A replacement for {@link ReadOnlySetWrapper} to fix the following JavaFX
 * issues:
 * <ul>
 * <li>Change notifications are fired even when the observed value did not
 * change.(https://bugs.openjdk.java.net/browse/JDK-8089169)</li>
 * <li>Bidirectional binding not working
 * (https://bugs.openjdk.java.net/browse/JDK-8089557): fixed by not forwarding
 * listeners to the nested read-only property but rather keeping the lists of
 * listeners distinct.</li>
 * <li>No proper implementation of equals() for Java 7, but object equality
 * considered (https://bugs.openjdk.java.net/browse/JDK-8120138): fixed by
 * overwriting equals() and hashCode() and by overwriting
 * {@link #bindBidirectional(Property)} and
 * {@link #unbindBidirectional(Property)}, which relied on the wrong
 * implementation.</li>
 * </ul>
 *
 * @author anyssen
 *
 * @param <E>
 *            The element type of the wrapped {@link ObservableSet}.
 */
public class ReadOnlySetWrapperEx<E> extends ReadOnlySetWrapper<E> {

	private class ReadOnlyPropertyImpl extends ReadOnlySetProperty<E> {

		private SetExpressionHelperEx<E> helper = null;

		@Override
		public void addListener(
				ChangeListener<? super ObservableSet<E>> listener) {
			if (helper == null) {
				helper = new SetExpressionHelperEx<>(this);
			}
			helper.addListener(listener);
		}

		@Override
		public void addListener(InvalidationListener listener) {
			if (helper == null) {
				helper = new SetExpressionHelperEx<>(this);
			}
			helper.addListener(listener);
		}

		@Override
		public void addListener(SetChangeListener<? super E> listener) {
			if (helper == null) {
				helper = new SetExpressionHelperEx<>(this);
			}
			helper.addListener(listener);
		}

		@Override
		public ReadOnlyBooleanProperty emptyProperty() {
			return ReadOnlySetWrapperEx.this.emptyProperty();
		}

		private void fireValueChangedEvent() {
			if (helper == null) {
				helper = new SetExpressionHelperEx<>(this);
			}
			helper.fireValueChangedEvent();
		}

		private void fireValueChangedEvent(Change<? extends E> change) {
			if (helper == null) {
				helper = new SetExpressionHelperEx<>(this);
			}
			helper.fireValueChangedEvent(change);
		}

		@Override
		public ObservableSet<E> get() {
			return ReadOnlySetWrapperEx.this.get();
		}

		@Override
		public Object getBean() {
			return ReadOnlySetWrapperEx.this.getBean();
		}

		@Override
		public String getName() {
			return ReadOnlySetWrapperEx.this.getName();
		}

		@Override
		public void removeListener(
				ChangeListener<? super ObservableSet<E>> listener) {
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
		public void removeListener(SetChangeListener<? super E> listener) {
			if (helper != null) {
				helper.removeListener(listener);
			}
		}

		@Override
		public ReadOnlyIntegerProperty sizeProperty() {
			return ReadOnlySetWrapperEx.this.sizeProperty();
		}
	}

	private ReadOnlyPropertyImpl readOnlyProperty = null;
	private SetExpressionHelperEx<E> helper = null;

	/**
	 * Creates a new unnamed {@link ReadOnlySetWrapperEx}.
	 */
	public ReadOnlySetWrapperEx() {
		super();
	}

	/**
	 * Creates a new named {@link ReadOnlySetWrapperEx} related to the given
	 * bean.
	 *
	 * @param bean
	 *            The bean to relate the to be created
	 *            {@link ReadOnlySetWrapperEx} to.
	 * @param name
	 *            The name for the to be created {@link ReadOnlySetWrapperEx}.
	 */
	public ReadOnlySetWrapperEx(Object bean, String name) {
		super(bean, name);
	}

	/**
	 * Creates a new named {@link ReadOnlySetWrapperEx}, related to the given
	 * bean and provided with the initial value.
	 *
	 * @param bean
	 *            The bean to relate the to be created
	 *            {@link ReadOnlySetWrapperEx} to.
	 * @param name
	 *            The name for the to be created {@link ReadOnlySetWrapperEx}.
	 * @param initialValue
	 *            The initial value of the to be created
	 *            {@link ReadOnlySetWrapperEx}.
	 */
	public ReadOnlySetWrapperEx(Object bean, String name,
			ObservableSet<E> initialValue) {
		super(bean, name, initialValue);
	}

	/**
	 * Creates a new unnamed {@link ReadOnlySetWrapperEx} with the given initial
	 * value.
	 *
	 * @param initialValue
	 *            The initial value of the to be created
	 *            {@link ReadOnlySetWrapperEx}.
	 */
	public ReadOnlySetWrapperEx(ObservableSet<E> initialValue) {
		super(initialValue);
	}

	@Override
	public void addListener(ChangeListener<? super ObservableSet<E>> listener) {
		if (helper == null) {
			helper = new SetExpressionHelperEx<>(this);
		}
		helper.addListener(listener);
	}

	@Override
	public void addListener(InvalidationListener listener) {
		if (helper == null) {
			helper = new SetExpressionHelperEx<>(this);
		}
		helper.addListener(listener);
	}

	@Override
	public void addListener(SetChangeListener<? super E> listener) {
		if (helper == null) {
			helper = new SetExpressionHelperEx<>(this);
		}
		helper.addListener(listener);
	}

	@Override
	public void bindBidirectional(Property<ObservableSet<E>> other) {
		try {
			super.bindBidirectional(other);
		} catch (IllegalArgumentException e) {
			if ("Cannot bind property to itself".equals(e.getMessage())
					&& this != other) {
				// XXX: In JavaSE-1.7 super implementation relies on equals()
				// not on object identity (as in JavaSE-1.8) to infer whether a
				// binding is valid. It thus throws an IllegalArgumentException
				// if two equal properties are passed in, even if they are not
				// identical. We have to ensure they are thus unequal to
				// establish the binding; as our value will be initially
				// overwritten anyway, we may adjust the local value; to reduce
				// noise, we only adjust the local value if necessary.
				// TODO: Remove when dropping support for JavaSE-1.7
				if (other.getValue() == null) {
					if (getValue() == null) {
						// set to value != null
						setValue(FXCollections.observableSet(new HashSet<E>()));
					}
				} else {
					if (getValue().equals(other)) {
						// set to null value
						setValue(null);
					}
				}
				// try again
				super.bindBidirectional(other);
			} else {
				throw (e);
			}
		}
	}

	@Override
	public boolean equals(Object other) {
		// Overwritten here to compensate an inappropriate equals()
		// implementation in JavaSE-1.7
		// (https://bugs.openjdk.java.net/browse/JDK-8120138)
		// TODO: Remove when dropping support for JavaSE-1.7
		if (other == this) {
			return true;
		}

		if (other == null || !(other instanceof Set)) {
			return false;
		}

		if (get() == null) {
			return false;
		}
		return get().equals(other);
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
	protected void fireValueChangedEvent(Change<? extends E> change) {
		if (helper != null) {
			helper.fireValueChangedEvent(change);
		}
		if (readOnlyProperty != null) {
			readOnlyProperty.fireValueChangedEvent(change);
		}
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
			ChangeListener<? super ObservableSet<E>> listener) {
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
	public void removeListener(SetChangeListener<? super E> listener) {
		if (helper != null) {
			helper.removeListener(listener);
		}
	}

	@Override
	public void unbindBidirectional(Property<ObservableSet<E>> other) {
		try {
			super.unbindBidirectional(other);
		} catch (IllegalArgumentException e) {
			if ("Cannot bind property to itself".equals(e.getMessage())
					&& this != other) {
				// XXX: In JavaSE-1.7, the super implementation relies on
				// equals() not on object identity (as in JavaSE-1.8) to infer
				// whether a binding is valid. It thus throws an
				// IllegalArgumentException if two equal properties are
				// passed in, even if they are not identical. We have to
				// ensure they are thus unequal to remove the binding; we
				// have to restore the current value afterwards.
				// TODO: Remove when dropping support for JavaSE-1.7
				ObservableSet<E> oldValue = getValue();
				if (other.getValue() == null) {
					// set to value != null
					setValue(FXCollections.observableSet(new HashSet<E>()));
				} else {
					// set to null value
					setValue(null);
				}
				// try again
				super.unbindBidirectional(other);
				setValue(oldValue);
			} else {
				throw (e);
			}
		}
	}
}
