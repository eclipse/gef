/*******************************************************************************
 * Copyright (c) 2016, 2020 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Zoey Prigge     (itemis AG) - bind value converter (bug #559031)
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language;

import org.eclipse.gef.dot.internal.language.doubleValues.DotDoubleOnlyGrammarConverters;
import org.eclipse.gef.dot.internal.language.formatting.DotPointFormatter;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.formatting.IFormatter;

/**
 * Use this class to register components to be used at runtime / without the
 * Equinox extension registry.
 */
public class DotPointRuntimeModule extends
		org.eclipse.gef.dot.internal.language.AbstractDotPointRuntimeModule {
	@Override
	public Class<? extends IValueConverterService> bindIValueConverterService() {
		return DotDoubleOnlyGrammarConverters.class;
	}

	@Override
	public Class<? extends IFormatter> bindIFormatter() {
		return DotPointFormatter.class;
	}
}
