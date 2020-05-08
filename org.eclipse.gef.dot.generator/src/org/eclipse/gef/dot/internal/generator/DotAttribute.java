/**
 * Copyright (c) 2016, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
