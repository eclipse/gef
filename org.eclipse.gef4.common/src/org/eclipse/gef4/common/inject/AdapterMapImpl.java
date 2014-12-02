/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.common.inject;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import org.eclipse.gef4.common.adapt.IAdaptable;

@SuppressWarnings("all")
public class AdapterMapImpl implements AdapterMap, Serializable {

	private static final long serialVersionUID = 1L;
	private Class<? extends IAdaptable> value;

	public AdapterMapImpl(Class<? extends IAdaptable> value) {
		this.value = value;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return AdapterMap.class;
	}

	@Override
	public Class<? extends IAdaptable> adaptableType() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AdapterMap)) {
			return false;
		}

		AdapterMap other = (AdapterMap) obj;
		return value.equals(other.adaptableType());
	}

	@Override
	public int hashCode() {
		return (127 * "value".hashCode()) ^ value.hashCode();
	}

	@Override
	public String toString() {
		return "@" + AdapterMap.class.getName() + "(value=" + value + ")";
	}
}
