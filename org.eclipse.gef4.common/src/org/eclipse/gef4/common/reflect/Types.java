/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.common.reflect;

import java.lang.reflect.ParameterizedType;

import com.google.common.reflect.TypeToken;

/**
 * Utilities around {@link TypeToken}
 *
 * @author anyssen
 *
 */
public class Types {

	/**
	 * Constructs a new {@link TypeToken} for an actual parameter type, which is
	 * inferred from a given context class.
	 *
	 * @param <T>
	 *            The parameter type to use.
	 * @param contextClass
	 *            A class that can be used to infer the actual parameter type of
	 *            the parameterized type.
	 * @return A new TypeToken representing the parameterized type.
	 */
	@SuppressWarnings("unchecked")
	public static <T> TypeToken<T> argumentOf(Class<?> contextClass) {
		while (contextClass != null && !(contextClass
				.getGenericSuperclass() instanceof ParameterizedType)) {
			contextClass = contextClass.getSuperclass();
		}
		return (TypeToken<T>) TypeToken
				.of(((ParameterizedType) contextClass.getGenericSuperclass())
						.getActualTypeArguments()[0]);
	}
}
