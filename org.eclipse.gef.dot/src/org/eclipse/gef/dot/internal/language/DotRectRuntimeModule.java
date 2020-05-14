/*******************************************************************************
 * Copyright (c) 2017, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Zoey Prigge     (itemis AG) - bind value converter (bug #559031)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language;

import org.eclipse.gef.dot.internal.language.doubleValues.DotDoubleOnlyGrammarConverters;
import org.eclipse.gef.dot.internal.language.formatting.DotRectFormatter;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.formatting.IFormatter;

/**
 * Use this class to register components to be used at runtime / without the
 * Equinox extension registry.
 */
public class DotRectRuntimeModule extends
		org.eclipse.gef.dot.internal.language.AbstractDotRectRuntimeModule {
	@Override
	public Class<? extends IValueConverterService> bindIValueConverterService() {
		return DotDoubleOnlyGrammarConverters.class;
	}

	@Override
	public Class<? extends IFormatter> bindIFormatter() {
		return DotRectFormatter.class;
	}
}
