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
package org.eclipse.gef.common.reflect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.ParameterizedType;
import java.util.Base64;

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

	/**
	 * Deserializes the given {@link String}-representation in Base64 encoding
	 * into a {@link TypeToken}.
	 *
	 * @param string
	 *            The {@link String}-representation to deserialize.
	 * @return The deserialized {@link TypeToken}.
	 */
	public static final TypeToken<?> deserialize(String string) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(
					Base64.getDecoder().decode(string));
			ObjectInputStream ois = new ObjectInputStream(bis);
			TypeToken<?> typeToken;

			typeToken = (TypeToken<?>) ois.readObject();

			ois.close();
			return typeToken;
		} catch (ClassNotFoundException | ClassCastException e) {
			throw new IllegalArgumentException(
					"String does not seem to be of type TokeToken.");
		} catch (IOException e2) {
			throw new IllegalArgumentException(
					"Could not deserialize TypeToken.");
		}
	}

	/**
	 * Serializes a given {@link TypeToken} into a {@link String}
	 * representation.
	 *
	 * @param typeToken
	 *            The {@link TypeToken} to serialize.
	 * @return The string representation of the {@link TypeToken} encoded in
	 *         Base64.
	 */
	public static final String serialize(TypeToken<?> typeToken) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bos);
			os.writeObject(typeToken);
			os.close();
			return Base64.getEncoder().encodeToString(bos.toByteArray());
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"Could not serialize " + typeToken);
		}
	}
}
