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

import java.util.List;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;

/**
 * A replacement for {@link SimpleListProperty} to fix the following JavaFX
 * issue:
 * <ul>
 * <li>https://bugs.openjdk.java.net/browse/JDK-8120138: fixed by overwriting
 * equals() and hashCode()</li>
 * </ul>
 *
 * @author anyssen
 *
 * @param <E>
 *            The element type of the {@link SimpleListProperty}.
 *
 */
public class SimpleListPropertyEx<E> extends SimpleListProperty<E> {

	/**
	 * Creates a new unnamed {@link SimpleListPropertyEx}.
	 */
	public SimpleListPropertyEx() {
		super();
	}

	/**
	 * Constructs a new {@link SimpleListPropertyEx} for the given bean and with
	 * the given name.
	 *
	 * @param bean
	 *            The bean this property is related to.
	 * @param name
	 *            The name of the property.
	 */
	public SimpleListPropertyEx(Object bean, String name) {
		super(bean, name);
	}

	/**
	 * Constructs a new {@link SimpleListPropertyEx} for the given bean and with
	 * the given name and initial value.
	 *
	 * @param bean
	 *            The bean this property is related to.
	 * @param name
	 *            The name of the property.
	 * @param initialValue
	 *            The initial value of the property
	 */
	public SimpleListPropertyEx(Object bean, String name,
			ObservableList<E> initialValue) {
		super(bean, name, initialValue);
	}

	/**
	 * Constructs a new unnamed {@link SimpleListPropertyEx} that is not related
	 * to a bean, with the given initial value.
	 *
	 * @param initialValue
	 *            The initial value of the property
	 */
	public SimpleListPropertyEx(ObservableList<E> initialValue) {
		super(initialValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object other) {
		// Overwritten here to compensate an inappropriate equals()
		// implementation on Java 7
		// (https://bugs.openjdk.java.net/browse/JDK-8120138)
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof List)) {
			return false;
		}

		try {
			final List<E> otherList = (List<E>) other;
			if (size() != otherList.size()) {
				return false;
			}
			for (int i = 0; i < size(); i++) {
				if (get(i) == null) {
					if (otherList.get(i) != null) {
						return false;
					}
				} else if (!get(i).equals(otherList.get(i))) {
					return false;
				}
			}
		} catch (ClassCastException e) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		// Overwritten here to compensate an inappropriate hashCode()
		// implementation on Java 7
		// (https://bugs.openjdk.java.net/browse/JDK-8120138)
		int hashCode = 1;
		for (E e : this) {
			hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
		}
		return hashCode;
	}
}
