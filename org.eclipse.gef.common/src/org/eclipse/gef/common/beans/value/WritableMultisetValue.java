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

import org.eclipse.gef.common.collections.ObservableMultiset;

import com.google.common.collect.Multiset;

import javafx.beans.value.WritableListValue;
import javafx.beans.value.WritableMapValue;
import javafx.beans.value.WritableObjectValue;
import javafx.beans.value.WritableSetValue;

/**
 * A writable reference to an {@link ObservableMultiset}.
 * <p>
 * This interface provides identical functionality for {@link Multiset} as
 * {@link WritableMapValue} for {@link Map}, {@link WritableSetValue} for
 * {@link Set}, or {@link WritableListValue} for {@link List}.
 * 
 * @author anyssen
 *
 * @param <E>
 *            The element type of the {@link ObservableMultiset}.
 */
public interface WritableMultisetValue<E> extends
		WritableObjectValue<ObservableMultiset<E>>, ObservableMultiset<E> {
}