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
import org.eclipse.gef.common.beans.binding.SetMultimapExpression;
import org.eclipse.gef.common.collections.ObservableSetMultimap;

import com.google.common.collect.SetMultimap;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlySetProperty;

/**
 * Abstract base class defining contract for a read-only {@link Property}
 * wrapping an {@link ObservableSetMultimap}.
 * <p>
 * This class provides identical functionality for {@link SetMultimap} as
 * {@link ReadOnlyMapProperty} for {@link Map}, {@link ReadOnlySetProperty} for
 * {@link Set}, or {@link ReadOnlyListProperty} for {@link List}.
 *
 * @param <K>
 *            The key type of the wrapped {@link ObservableSetMultimap}.
 * @param <V>
 *            The value type of the wrapped {@link ObservableSetMultimap}.
 *
 * @author anyssen
 */
public abstract class ReadOnlySetMultimapProperty<K, V>
		extends SetMultimapExpression<K, V>
		implements ReadOnlyProperty<ObservableSetMultimap<K, V>> {

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
	 * {@link ObservableSetMultimap}, that is wrapped in this
	 * {@link ReadOnlySetMultimapProperty}, and the given
	 * {@link ObservableSetMultimap}.
	 * <p>
	 * A content binding ensures that the content of the wrapped
	 * {@link ObservableSetMultimap} is the same as that of the other
	 * {@link ObservableSetMultimap}. If the content of the other
	 * {@link ObservableSetMultimap} changes, the wrapped
	 * {@link ObservableSetMultimap} will be updated automatically.
	 *
	 * @param target
	 *            The {@link ObservableSetMultimap} this property should be
	 *            unidirectionally bound to.
	 */
	public void bindContent(ObservableSetMultimap<K, V> target) {
		BindingUtils.bindContent(this, target);
	}

	/**
	 * Creates a bidirectional content binding of the
	 * {@link ObservableSetMultimap}, that is wrapped in this
	 * {@link ReadOnlySetMultimapProperty}, and the given
	 * {@link ObservableSetMultimap} .
	 * <p>
	 * A bidirectional content binding ensures that the content of the two
	 * {@link ObservableSetMultimap ObservableSetMultimaps} are the same. If the
	 * content of one of the {@link ObservableSetMultimap
	 * ObservableSetMultimaps} changes, the other one will be updated
	 * automatically.
	 *
	 * @param other
	 *            The {@link ObservableSetMultimap} this property should be
	 *            bidirectionally bound to.
	 */
	public void bindContentBidirectional(ObservableSetMultimap<K, V> other) {
		BindingUtils.bindContentBidirectional(this, other);
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (other == null || !(other instanceof SetMultimap)) {
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
	 * is wrapped in this {@link ReadOnlySetMultimapProperty}, and another
	 * {@link Object}.
	 *
	 * @param target
	 *            The {@link Object} to which the binding should be removed.
	 */
	@SuppressWarnings("unchecked")
	public void unbindContent(Object target) {
		try {
			BindingUtils.unbindContent(this,
					(ObservableSetMultimap<? extends K, ? extends V>) target);
		} catch (ClassCastException e) {
			// do nothing if types don't match
		}
	}

	/**
	 * Deletes a bidirectional content binding between the
	 * {@link ObservableSetMultimap}, that is wrapped in this
	 * {@link ReadOnlySetMultimapProperty}, and another {@link Object}.
	 *
	 * @param other
	 *            The {@link Object} to which the bidirectional binding should
	 *            be removed.
	 */
	@SuppressWarnings("unchecked")
	public void unbindContentBidirectional(Object other) {
		try {
			BindingUtils.unbindContentBidirectional(this,
					(ObservableSetMultimap<K, V>) other);
		} catch (ClassCastException e) {
			// do nothing if types don't match
		}
	}
}
