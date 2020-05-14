/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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
package org.eclipse.gef.common.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.eclipse.gef.common.reflect.Types;
import org.junit.Test;

import com.google.common.reflect.TypeToken;

public class TypesTests {

	private class ParameterizedType<T> {
	}

	private class ParameterType1 extends Object {
	}

	private class ParameterType2 extends Object {
	}

	@SuppressWarnings("serial")
	@Test
	public void serializeAndDeserializeTypeToken() {
		TypeToken<ParameterizedType<ParameterType1>> typeToken = new TypeToken<ParameterizedType<ParameterType1>>() {
		};
		String typeTokenString = Types.serialize(typeToken);
		TypeToken<?> deserializedTypeToken = Types.deserialize(typeTokenString);
		assertEquals(typeToken, deserializedTypeToken);

		TypeToken<ParameterizedType<ParameterType2>> typeToken2 = new TypeToken<ParameterizedType<ParameterType2>>() {
		};
		String typeTokenString2 = Types.serialize(typeToken2);
		TypeToken<?> deserializedTypeToken2 = Types
				.deserialize(typeTokenString2);
		assertEquals(typeToken2, deserializedTypeToken2);

		assertNotEquals(typeToken, typeToken2);
		assertNotEquals(deserializedTypeToken, deserializedTypeToken2);
	}

}
