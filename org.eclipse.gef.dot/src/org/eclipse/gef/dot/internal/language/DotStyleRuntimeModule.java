/*******************************************************************************
 * Copyright (c) 2016, 2020 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language;

import org.eclipse.gef.dot.internal.language.formatting.DotStyleFormatter;
import org.eclipse.xtext.formatting.IFormatter;

/**
 * Use this class to register components to be used at runtime / without the
 * Equinox extension registry.
 */
public class DotStyleRuntimeModule extends
		org.eclipse.gef.dot.internal.language.AbstractDotStyleRuntimeModule {

	@Override
	public Class<? extends IFormatter> bindIFormatter() {
		return DotStyleFormatter.class;
	}
}
