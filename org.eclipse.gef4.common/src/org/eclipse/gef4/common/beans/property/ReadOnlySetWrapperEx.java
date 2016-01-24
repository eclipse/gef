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

import java.util.Set;

import com.sun.javafx.binding.SetExpressionHelper;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.SetChangeListener.Change;

/**
 * A replacement for {@link ReadOnlySetWrapper} to fix the following JavaFX
 * issues:
 * <ul>
 * <li>https://bugs.openjdk.java.net/browse/JDK-8089557: fixed by not forwarding
 * listeners to the read-only property but rather keeping the lists of listeners
 * distinct.</li>
 * </ul>
 *
 * @author anyssen
 *
 * @param <E>
 *            The element type of the wrapped {@link ObservableSet}.
 */
public class ReadOnlySetWrapperEx<E> extends ReadOnlySetWrapper<E> {

	private SetExpressionHelper<E> helper = null;

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
		helper = SetExpressionHelper.addListener(helper, this, listener);
	}

	@Override
	public void addListener(InvalidationListener listener) {
		helper = SetExpressionHelper.addListener(helper, this, listener);
	}

	@Override
	public void addListener(SetChangeListener<? super E> listener) {
		helper = SetExpressionHelper.addListener(helper, this, listener);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object other) {
		// Overwritten here to compensate an inappropriate equals()
		// implementation on Java 7
		// (https://bugs.openjdk.java.net/browse/JDK-8120138)
		if (other == this) {
			return true;
		}

		if (other == null || !(other instanceof Set)) {
			return false;
		}

		try {
			Set<E> otherSet = (Set<E>) other;
			if (otherSet.size() != size()) {
				return false;
			}
			if (isEmpty()) {
				return true;
			}
			return containsAll(otherSet);
		} catch (ClassCastException unused) {
			return false;
		}
	}

	@Override
	protected void fireValueChangedEvent() {
		SetExpressionHelper.fireValueChangedEvent(helper);
		super.fireValueChangedEvent();
	}

	@Override
	protected void fireValueChangedEvent(Change<? extends E> change) {
		SetExpressionHelper.fireValueChangedEvent(helper, change);
		super.fireValueChangedEvent(change);
	}

	@Override
	public int hashCode() {
		// Overwritten here to compensate an inappropriate hashCode()
		// implementation on Java 7
		// (https://bugs.openjdk.java.net/browse/JDK-8120138)
		int h = 0;
		for (E e : this) {
			if (e != null) {
				h += e.hashCode();
			}
		}
		return h;
	}

	@Override
	public void removeListener(
			ChangeListener<? super ObservableSet<E>> listener) {
		helper = SetExpressionHelper.removeListener(helper, listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		helper = SetExpressionHelper.removeListener(helper, listener);
	}

	@Override
	public void removeListener(SetChangeListener<? super E> listener) {
		helper = SetExpressionHelper.removeListener(helper, listener);
	}
}
