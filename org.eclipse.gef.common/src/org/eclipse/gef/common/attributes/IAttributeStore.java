/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
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
package org.eclipse.gef.common.attributes;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.collections.ObservableMap;

/**
 * An {@link IAttributeStore} allows to store and retrieve values of named
 * attributes.
 *
 */
public interface IAttributeStore {

	/**
	 * The name of the {@link #attributesProperty() attributes property}.
	 */
	public String ATTRIBUTES_PROPERTY = "attributes";

	/**
	 * Returns a map property of attributes, mapped to their names.
	 *
	 * @return A map property containing the attributes.
	 */
	public ReadOnlyMapProperty<String, Object> attributesProperty();

	/**
	 * Returns a map of attributes, mapped to their keys.
	 *
	 * @return A map containing the attributes.
	 */
	public ObservableMap<String, Object> getAttributes();

}