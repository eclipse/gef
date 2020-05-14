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
package org.eclipse.gef.common.beans.property;

import org.eclipse.gef.common.collections.ObservableMultiset;

import javafx.beans.property.Property;

/**
 * A concrete implementation of a {@link Property} wrapping an
 * {@link ObservableMultiset}.
 * 
 * @author anyssen
 * 
 * @param <E>
 *            The element type of the {@link ObservableMultiset}.
 */
public class SimpleMultisetProperty<E> extends MultisetPropertyBase<E> {

	private Object bean;
	private String name;

	/**
	 * Constructs a new unnamed {@link SimpleMultisetProperty}.
	 */
	public SimpleMultisetProperty() {
	}

	/**
	 * Constructs a new {@link SimpleMultisetProperty} for the given bean and
	 * with the given name.
	 * 
	 * @param bean
	 *            The bean this property is related to.
	 * @param name
	 *            The name of the property.
	 */
	public SimpleMultisetProperty(Object bean, String name) {
		this.bean = bean;
		this.name = name;
	}

	/**
	 * Constructs a new {@link SimpleMultisetProperty} for the given bean and
	 * with the given name and initial value.
	 * 
	 * @param bean
	 *            The bean this property is related to.
	 * @param name
	 *            The name of the property.
	 * @param initialValue
	 *            The initial value of the property
	 */
	public SimpleMultisetProperty(Object bean, String name,
			ObservableMultiset<E> initialValue) {
		super(initialValue);
		this.bean = bean;
		this.name = name;
	}

	/**
	 * Constructs a new unnamed {@link SimpleMultisetProperty} that is not
	 * related to a bean, with the given initial value.
	 * 
	 * @param initialValue
	 *            The initial value of the property
	 */
	public SimpleMultisetProperty(ObservableMultiset<E> initialValue) {
		super(initialValue);
	}

	@Override
	public Object getBean() {
		return bean;
	}

	@Override
	public String getName() {
		return name == null ? "" : name;
	}

}
