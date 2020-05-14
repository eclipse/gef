/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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

import org.eclipse.gef.common.beans.binding.SetExpressionHelperEx;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.SetChangeListener.Change;

/**
 * A replacement for {@link SimpleSetProperty} to fix the following JavaFX
 * issue:
 * <ul>
 * <li>Change notifications are fired even when the observed value did not
 * change.(https://bugs.openjdk.java.net/browse/JDK-8089169)</li>
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

}
