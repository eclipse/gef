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

import org.eclipse.gef.common.collections.ObservableSetMultimap;

import javafx.beans.property.Property;

/**
 * A concrete implementation of a {@link Property} wrapping an
 * {@link ObservableSetMultimap}.
 * 
 * @author anyssen
 * 
 * @param <K>
 *            The key type of the {@link ObservableSetMultimap}.
 * @param <V>
 *            The value type of the {@link ObservableSetMultimap}.
 *
 */
public class SimpleSetMultimapProperty<K, V>
		extends SetMultimapPropertyBase<K, V> {

	private Object bean;
	private String name;

	/**
	 * Constructs a new unnamed {@link SimpleSetMultimapProperty}.
	 */
	public SimpleSetMultimapProperty() {
	}

	/**
	 * Constructs a new {@link SimpleSetMultimapProperty} for the given bean and
	 * with the given name.
	 * 
	 * @param bean
	 *            The bean this property is related to.
	 * @param name
	 *            The name of the property.
	 */
	public SimpleSetMultimapProperty(Object bean, String name) {
		this.bean = bean;
		this.name = name;
	}

	/**
	 * Constructs a new {@link SimpleSetMultimapProperty} for the given bean and
	 * with the given name and initial value.
	 * 
	 * @param bean
	 *            The bean this property is related to.
	 * @param name
	 *            The name of the property.
	 * @param initialValue
	 *            The initial value of the property
	 */
	public SimpleSetMultimapProperty(Object bean, String name,
			ObservableSetMultimap<K, V> initialValue) {
		super(initialValue);
		this.bean = bean;
		this.name = name;
	}

	/**
	 * Constructs a new unnamed {@link SimpleSetMultimapProperty} that is not
	 * related to a bean, with the given initial value.
	 * 
	 * @param initialValue
	 *            The initial value of the property
	 */
	public SimpleSetMultimapProperty(ObservableSetMultimap<K, V> initialValue) {
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
