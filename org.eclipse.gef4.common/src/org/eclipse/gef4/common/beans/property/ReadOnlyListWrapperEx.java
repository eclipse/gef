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

import com.sun.javafx.binding.ListExpressionHelper;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * A replacement for {@link ReadOnlyListWrapper} to fix the following JavaFX
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
 *            The element type of the wrapped {@link ObservableList}.
 */
public class ReadOnlyListWrapperEx<E> extends ReadOnlyListWrapper<E> {

	private ListExpressionHelper<E> helper = null;

	/**
	 * Creates a new unnamed {@link ReadOnlyListWrapperEx}.
	 */
	public ReadOnlyListWrapperEx() {
		super();
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
		helper = ListExpressionHelper.addListener(helper, this, listener);
	}

	@Override
	public void addListener(InvalidationListener listener) {
		helper = ListExpressionHelper.addListener(helper, this, listener);
	}

	@Override
	public void addListener(ListChangeListener<? super E> listener) {
		helper = ListExpressionHelper.addListener(helper, this, listener);
	}

	@Override
	public void removeListener(
			ChangeListener<? super ObservableList<E>> listener) {
		helper = ListExpressionHelper.removeListener(helper, listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		helper = ListExpressionHelper.removeListener(helper, listener);
	}

	@Override
	public void removeListener(ListChangeListener<? super E> listener) {
		helper = ListExpressionHelper.removeListener(helper, listener);
	}

	@Override
	protected void fireValueChangedEvent() {
		ListExpressionHelper.fireValueChangedEvent(helper);
		super.fireValueChangedEvent();
	}

	@Override
	protected void fireValueChangedEvent(
			ListChangeListener.Change<? extends E> change) {
		ListExpressionHelper.fireValueChangedEvent(helper, change);
		super.fireValueChangedEvent(change);
	}

}
