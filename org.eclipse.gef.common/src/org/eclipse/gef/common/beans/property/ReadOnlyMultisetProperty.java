/******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.common.beans.binding.BindingUtils;
import org.eclipse.gef.common.beans.binding.MultisetExpression;
import org.eclipse.gef.common.collections.ObservableMultiset;
import org.eclipse.gef.common.collections.ObservableSetMultimap;

import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlySetProperty;

/**
 * Abstract base class defining contract for a read-only {@link Property}
 * wrapping an {@link ObservableMultiset}.
 * <p>
 * This class provides identical functionality for {@link SetMultimap} as
 * {@link ReadOnlyMapProperty} for {@link Map}, {@link ReadOnlySetProperty} for
 * {@link Set}, or {@link ReadOnlyListProperty} for {@link List}.
 *
 * @param <E>
 *            The element type of the wrapped {@link ObservableMultiset}.
 *
 * @author anyssen
 */
public abstract class ReadOnlyMultisetProperty<E> extends MultisetExpression<E>
		implements ReadOnlyProperty<ObservableMultiset<E>> {

	/**
	 * Appends a representation of this {@link SetMultimapProperty}'s value to
	 * the given {@link StringBuilder}. Gets called from {@link #toString()} to
	 * allow subclasses to provide a changed value representation.
	 *
	 * @param result
	 *            A {@link StringBuilder} to append the value representation to.
	 */
	protected void appendValueToString(final StringBuilder result) {
		result.append("value: " + get());
	}

	/**
	 * Creates a unidirectional content binding between the
	 * {@link ObservableMultiset}, that is wrapped in this
	 * {@link ReadOnlyMultisetProperty}, and the given
	 * {@link ObservableMultiset}.
	 * <p>
	 * A content binding ensures that the content of the wrapped
	 * {@link ObservableMultiset} is the same as that of the other
	 * {@link ObservableMultiset}. If the content of the other
	 * {@link ObservableMultiset} changes, the wrapped
	 * {@link ObservableMultiset} will be updated automatically.
	 *
	 * @param target
	 *            The {@link ObservableSetMultimap} this property should be
	 *            unidirectionally bound to.
	 */
	public void bindContent(ObservableMultiset<E> target) {
		BindingUtils.bindContent(this, target);
	}

	/**
	 * Creates a bidirectional content binding of the {@link ObservableMultiset}
	 * , that is wrapped in this {@link ReadOnlyMultisetProperty}, and the given
	 * {@link ObservableMultiset} .
	 * <p>
	 * A bidirectional content binding ensures that the content of the two
	 * {@link ObservableMultiset ObservableMultisets} are the same. If the
	 * content of one of the {@link ObservableMultiset ObservableMultiset}
	 * changes, the other one will be updated automatically.
	 *
	 * @param other
	 *            The {@link ObservableSetMultimap} this property should be
	 *            bidirectionally bound to.
	 */
	public void bindContentBidirectional(ObservableMultiset<E> other) {
		BindingUtils.bindContentBidirectional(this, other);
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (other == null || !(other instanceof Multiset)) {
			return false;
		}

		if (get() == null) {
			return false;
		}
		return get().equals(other);
	}

	@Override
	public int hashCode() {
		// XXX: As we rely on equality to remove a binding again, we have to
		// ensure the hash code is the same for a pair of given properties.
		// We fall back to the very easiest case here (and use a constant).
		return 0;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(
				getClass().getSimpleName() + " [");
		final Object bean = getBean();
		if (bean != null) {
			sb.append("bean: " + bean + ", ");
		}
		final String name = getName();
		if ((name != null) && !name.equals("")) {
			sb.append("name: " + name + ", ");
		}
		appendValueToString(sb);
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Deletes a content binding between the {@link ObservableSetMultimap}, that
	 * is wrapped in this {@link ReadOnlyMultisetProperty}, and another
	 * {@link Object}.
	 *
	 * @param target
	 *            The {@link Object} to which the binding should be removed.
	 */
	@SuppressWarnings("unchecked")
	public void unbindContent(Object target) {
		try {
			BindingUtils.unbindContent(this,
					(ObservableMultiset<? extends E>) target);
		} catch (ClassCastException e) {
			// nothing to do in case the types don't match
		}
	}

	/**
	 * Deletes a bidirectional content binding between the
	 * {@link ObservableSetMultimap}, that is wrapped in this
	 * {@link ReadOnlyMultisetProperty}, and another {@link Object}.
	 *
	 * @param other
	 *            The {@link Object} to which the bidirectional binding should
	 *            be removed.
	 */
	@SuppressWarnings("unchecked")
	public void unbindContentBidirectional(Object other) {
		try {
			BindingUtils.unbindContentBidirectional(this,
					(ObservableMultiset<E>) other);
		} catch (ClassCastException e) {
			// nothing to do in case the types don't match
		}
	}
}
