/******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.common.beans.property;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.common.beans.value.WritableMultisetValue;
import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.common.collections.ObservableMultiset;

import com.google.common.collect.Multiset;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SetProperty;

/**
 * Abstract base class defining contract for a {@link Property} wrapping a
 * {@link ObservableMultiset}.
 * <p>
 * This class provides identical functionality for {@link Multiset} as
 * {@link MapProperty} for {@link Map}, {@link SetProperty} for {@link Set}, or
 * {@link ListProperty} for {@link List}.
 *
 * @param <E>
 *            The element type of the wrapped {@link ObservableMultiset}.
 *
 */
public abstract class MultisetProperty<E> extends ReadOnlyMultisetProperty<E>
		implements Property<ObservableMultiset<E>>, WritableMultisetValue<E> {

	@Override
	public void bindBidirectional(Property<ObservableMultiset<E>> other) {
		try {
			Bindings.bindBidirectional(this, other);
		} catch (IllegalArgumentException e) {
			if ("Cannot bind property to itself".equals(e.getMessage())
					&& this != other) {
				// XXX: With JavaFX 2.2, the super implementation relies on
				// equals() not on object identity to infer whether a binding is
				// valid. It thus throw an IllegalArgumentException if two equal
				// properties are passed in, even if they are not identical. We
				// have to ensure they are thus unequal to create the binding;
				// the value will be overwritten anyway.
				if (other.getValue() == null) {
					if (getValue() == null) {
						// set to value != null
						setValue(CollectionUtils.<E> observableHashMultiset());
					}
				} else {
					if (getValue().equals(other)) {
						// set to null value
						setValue(null);
					}
				}
				// try again
				Bindings.bindBidirectional(this, other);
			} else {
				throw (e);
			}
		}
	}

	@Override
	public void setValue(ObservableMultiset<E> v) {
		set(v);
	}

	@Override
	public void unbindBidirectional(Property<ObservableMultiset<E>> other) {
		try {
			Bindings.unbindBidirectional(this, other);
		} catch (IllegalArgumentException e) {
			if ("Cannot bind property to itself".equals(e.getMessage())
					&& this != other) {
				// XXX: With JavaFX 2.2, the super implementation relies on
				// equals() not on object identity to infer whether a binding is
				// valid. It thus throw an IllegalArgumentException if two equal
				// properties are passed in, even if they are not identical. We
				// have to ensure they are thus unequal to remove the binding
				// and restore the original value afterwards.
				ObservableMultiset<E> oldValue = getValue();
				if (other.getValue() == null) {
					// set to value != null
					setValue(CollectionUtils.<E> observableHashMultiset());
				} else {
					// set to null value
					setValue(null);
				}
				// try again
				Bindings.unbindBidirectional(this, other);
				setValue(oldValue);
			} else {
				throw (e);
			}
		}
	}
}
