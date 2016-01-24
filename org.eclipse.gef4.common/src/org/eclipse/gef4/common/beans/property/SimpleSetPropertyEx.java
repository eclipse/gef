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

import javafx.beans.property.SimpleSetProperty;
import javafx.collections.ObservableSet;

/**
 * A replacement for {@link SimpleSetProperty} to fix the following JavaFX
 * issue:
 * <ul>
 * <li>https://bugs.openjdk.java.net/browse/JDK-8120138: fixed by overwriting
 * equals() and hashCode()</li>
 * </ul>
 *
 * @author anyssen
 *
 * @param <E>
 *            The element type of the {@link SimpleSetProperty}.
 *
 */
public class SimpleSetPropertyEx<E> extends SimpleSetProperty<E> {

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
}
