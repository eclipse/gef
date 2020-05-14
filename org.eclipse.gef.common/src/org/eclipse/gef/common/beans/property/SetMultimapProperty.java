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

import org.eclipse.gef.common.beans.value.WritableSetMultimapValue;
import org.eclipse.gef.common.collections.ObservableSetMultimap;

import com.google.common.collect.SetMultimap;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SetProperty;

/**
 * Abstract base class defining contract for a {@link Property} wrapping a
 * {@link ObservableSetMultimap}.
 * <p>
 * This class provides identical functionality for {@link SetMultimap} as
 * {@link MapProperty} for {@link Map}, {@link SetProperty} for {@link Set}, or
 * {@link ListProperty} for {@link List}.
 *
 * @param <K>
 *            The key type of the wrapped {@link ObservableSetMultimap}.
 * @param <V>
 *            The value type of the wrapped {@link ObservableSetMultimap}.
 *
 */
public abstract class SetMultimapProperty<K, V>
		extends ReadOnlySetMultimapProperty<K, V> implements
		Property<ObservableSetMultimap<K, V>>, WritableSetMultimapValue<K, V> {

	@Override
	public void bindBidirectional(Property<ObservableSetMultimap<K, V>> other) {
		Bindings.bindBidirectional(this, other);
	}

	@Override
	public void setValue(ObservableSetMultimap<K, V> v) {
		set(v);
	}

	@Override
	public void unbindBidirectional(
			Property<ObservableSetMultimap<K, V>> other) {
		Bindings.unbindBidirectional(this, other);
	}
}
