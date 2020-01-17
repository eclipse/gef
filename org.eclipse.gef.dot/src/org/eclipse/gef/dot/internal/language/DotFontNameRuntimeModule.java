/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
