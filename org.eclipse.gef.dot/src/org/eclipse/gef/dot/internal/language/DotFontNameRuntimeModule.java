/*******************************************************************************
 * Copyright (c) 2019, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Zoey Gerrit Prigge (itemis AG) - initial API and implementation (bug #542663)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language;

import org.eclipse.gef.dot.internal.language.fontname.DotFontNameConverters;
import org.eclipse.gef.dot.internal.language.fontname.DotFontNameParserCustom;
import org.eclipse.gef.dot.internal.language.formatting.DotFontNameFormatter;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.formatting.IFormatter;
import org.eclipse.xtext.parser.IParser;

/**
 * Use this class to register components to be used at runtime / without the
 * Equinox extension registry.
 */
public class DotFontNameRuntimeModule extends
		org.eclipse.gef.dot.internal.language.AbstractDotFontNameRuntimeModule {
	@Override
	public Class<? extends IValueConverterService> bindIValueConverterService() {
		return DotFontNameConverters.class;
	}

	@Override
	public Class<? extends IParser> bindIParser() {
		return DotFontNameParserCustom.class;
	}

	@Override
	public Class<? extends IFormatter> bindIFormatter() {
		return DotFontNameFormatter.class;
	}
}
