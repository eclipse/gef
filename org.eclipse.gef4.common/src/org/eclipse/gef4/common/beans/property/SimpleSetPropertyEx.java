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
package org.eclipse.gef4.common.beans.property;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef4.common.beans.binding.SetExpressionHelperEx;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.SetChangeListener.Change;

/**
 * A replacement for {@link SimpleSetProperty} to fix the following JavaFX
 * issue:
 * <ul>
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
 *            The element type of the {@link SimpleSetProperty}.
 *
 */
public class SimpleSetPropertyEx<E> extends SimpleSetProperty<E> {

	private SetExpressionHelperEx<E> helper = null;

	/**
	 * Creates a new unnamed {@link SimpleSetPropertyEx}.
	 */
	public SimpleSetPropertyEx() {
		super();
	}

	/**
	 * Constructs a new {@link SimpleSetPropertyEx} for the given bean and with
	 * the given name.
	 *
	 * @param bean
	 *            The bean this property is related to.
	 * @param name
	 *            The name of the property.
	 */
	public SimpleSetPropertyEx(Object bean, String name) {
		super(bean, name);
	}

	/**
	 * Constructs a new {@link SimpleSetPropertyEx} for the given bean and with
	 * the given name and initial value.
	 *
	 * @param bean
	 *            The bean this property is related to.
	 * @param name
	 *            The name of the property.
	 * @param initialValue
	 *            The initial value of the property
	 */
	public SimpleSetPropertyEx(Object bean, String name,
			ObservableSet<E> initialValue) {
		super(bean, name, initialValue);
	}

	/**
	 * Constructs a new unnamed {@link SimpleSetPropertyEx} that is not related
	 * to a bean, with the given initial value.
	 *
	 * @param initialValue
	 *            The initial value of the property
	 */
	public SimpleSetPropertyEx(ObservableSet<E> initialValue) {
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
	}

	@Override
	protected void fireValueChangedEvent(Change<? extends E> change) {
		if (helper != null) {
			helper.fireValueChangedEvent(change);
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
