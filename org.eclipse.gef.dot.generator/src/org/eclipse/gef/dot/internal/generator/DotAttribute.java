/**
 * Copyright (c) 2016, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 */
package org.eclipse.gef.dot.internal.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import org.eclipse.xtend.lib.macro.Active;

/**
 * Adds a lazy getter and an initializer method.
 */
@Target(ElementType.FIELD)
@Active(DotAttributeProcessor.class)
public @interface DotAttribute {
	/**
	 * A string matching an ID.Type to use for this attribute
	 */
	public String[] rawType() default { "" };

	/**
	 * Type of the attribute.
	 *
	 * @return The type of the attribute.
	 */
	public Class<?>[] parsedType();
}
