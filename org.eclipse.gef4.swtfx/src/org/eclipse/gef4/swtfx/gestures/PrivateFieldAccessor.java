/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Jan Köhnlein (itemis AG) - initial API and implementation (#427106)
 * 
 *******************************************************************************/
package org.eclipse.gef4.swtfx.gestures;

import java.lang.reflect.Field;

/**
 * Allows to access the value of private fields.
 * 
 * @author Jan Koehnlein
 */
public class PrivateFieldAccessor {
	@SuppressWarnings("unchecked")
	public static <T> T getPrivateField(Object owner, String fieldName) {
		Class<? extends Object> currentClass = owner.getClass();
		Field field = null;
		do {
			try {
				field = currentClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				currentClass = currentClass.getSuperclass();
				if (currentClass == null) {
					return null;
				}
			}
		} while (field == null);
		field.setAccessible(true);
		try {
			return (T) field.get(owner);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
