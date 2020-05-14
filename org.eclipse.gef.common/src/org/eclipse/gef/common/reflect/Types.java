/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
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
package org.eclipse.gef.common.reflect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;

import com.google.common.reflect.TypeToken;

/**
 * Utilities around {@link TypeToken}
 *
 * @author anyssen
 *
 */
public class Types {

	private static Method isTypeTokenAssignableMethod;

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
	 * Returns whether the given 'candidate' superType is a super type of the
	 * given 'candidate' subtype.
	 *
	 * This is replacement for TypeToken.isAssignableFrom(TypeToken), which has
	 * been deprecated and replaced by TypeToken.isSuperTypeOf(TypeToken) in
	 * Google Guava 19.0.0. As we want to support various Google Guava versions
	 * from 15.0.0 onwards, this methods delegates to the appropriate
	 * implementation using reflection.
	 *
	 * @param superType
	 *            The 'candidate' superType.
	 * @param subType
	 *            The 'candidate' subType.
	 * @return <code>true</code> when the given 'candidate' superType is indeed
	 *         a super type of the given 'candidate' subType, <code>false</code>
	 *         otherwise.
	 */
	public static final boolean isAssignable(TypeToken<?> superType,
			TypeToken<?> subType) {
		// TypeToken.isAssignableFrom(TypeToken) has been deprecated in Guava
		// 19, where TypeToken.isSuperTypeOf(TypeToken) has been introduced as a
		// workaround. As we want to support a broad range of Guava versions, we
		// have use reflection here to access the respective functionality.
		// XXX: Replace this with direct calls to
		// TypeToken.isSuperTypeOf(TypeToken) when removing support for Guava <
		// 19.
		if (isTypeTokenAssignableMethod == null) {
			try {
				isTypeTokenAssignableMethod = TypeToken.class
						.getDeclaredMethod("isSupertypeOf", TypeToken.class);
			} catch (NoSuchMethodException e) {
				// e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			if (isTypeTokenAssignableMethod == null) {
				try {
					isTypeTokenAssignableMethod = TypeToken.class
							.getDeclaredMethod("isAssignableFrom",
									TypeToken.class);
				} catch (NoSuchMethodException e) {
					throw new IllegalArgumentException(
							"Neither TypeToken.isAssignableFrom(TypeToken), nor TypeToken.isSuperTypeOf(TypeToken) seems to be supported by the given Guava version.");
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			return (boolean) isTypeTokenAssignableMethod.invoke(superType,
					subType);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalArgumentException(e);
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
