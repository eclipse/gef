/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.common.adapt.inject;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import org.eclipse.gef4.common.adapt.IAdaptable;

/**
 * Implementation of {@link AdapterMap} annotation.
 *
 * @author anyssen
 *
 */
@SuppressWarnings("all")
class AdapterMapImpl implements AdapterMap, Serializable {

	private static final long serialVersionUID = 1L;
	private Class<? extends IAdaptable> type;
	private String role;

	/**
	 * Creates a new {@link AdapterMapImpl} with the given {@link IAdaptable}
	 * type as its value.
	 *
	 * @param type
	 *            The {@link IAdaptable} type being used as type of this
	 *            {@link AdapterMapImpl}.
	 */
	public AdapterMapImpl(Class<? extends IAdaptable> type) {
		this.type = type;
		this.role = AdapterMap.DEFAULT_ROLE;
	}

	/**
	 * Creates a new {@link AdapterMapImpl} with the given {@link IAdaptable}
	 * type and role.
	 *
	 * @param type
	 *            The {@link IAdaptable} type being used as type of this
	 *            {@link AdapterMapImpl}.
	 * @param role
	 *            The {@link String} being used as role of this
	 *            {@link AdapterMapImpl}.
	 */
	public AdapterMapImpl(Class<? extends IAdaptable> type, String role) {
		this.type = type;
		this.role = role;
	}

	@Override
	public String adaptableRole() {
		return role;
	}

	@Override
	public Class<? extends IAdaptable> adaptableType() {
		return type;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return AdapterMap.class;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AdapterMap)) {
			return false;
		}

		AdapterMap other = (AdapterMap) obj;
		return type.equals(other.adaptableType())
				&& role.equals(other.adaptableRole());
	}

	@Override
	public int hashCode() {
		return (127 * "type".hashCode())
				^ type.hashCode() + (127 * "role".hashCode()) ^ role.hashCode();
	}

	@Override
	public String toString() {
		return "@" + AdapterMap.class.getName() + "(type=" + type + ", "
				+ "role=" + role + ")";
	}
}
