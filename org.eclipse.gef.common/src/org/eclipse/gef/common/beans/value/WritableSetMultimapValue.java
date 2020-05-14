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
package org.eclipse.gef.common.beans.value;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.common.collections.ObservableSetMultimap;

import com.google.common.collect.SetMultimap;

import javafx.beans.value.WritableListValue;
import javafx.beans.value.WritableMapValue;
import javafx.beans.value.WritableObjectValue;
import javafx.beans.value.WritableSetValue;

/**
 * A writable reference to an {@link ObservableSetMultimap}.
 * <p>
 * This interface provides identical functionality for {@link SetMultimap} as
 * {@link WritableMapValue} for {@link Map}, {@link WritableSetValue} for
 * {@link Set}, or {@link WritableListValue} for {@link List}.
 * 
 * @author anyssen
 *
 * @param <K>
 *            The key type of the {@link ObservableSetMultimap}.
 * @param <V>
 *            The value type of the {@link ObservableSetMultimap}.
 */
public interface WritableSetMultimapValue<K, V>
		extends WritableObjectValue<ObservableSetMultimap<K, V>>,
		ObservableSetMultimap<K, V> {
}