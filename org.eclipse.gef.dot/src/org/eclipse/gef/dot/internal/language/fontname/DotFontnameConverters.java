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
package org.eclipse.gef.dot.internal.language.fontname;

import java.util.Locale;

import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractDeclarativeValueConverterService;
import org.eclipse.xtext.conversion.impl.AbstractNullSafeConverter;
import org.eclipse.xtext.nodemodel.INode;

public class DotFontnameConverters
		extends AbstractDeclarativeValueConverterService {

	/**
	 * A value converter for the PSALIAS datatype rule
	 * 
	 * @return An IValueConverter<PostScriptFontAlias>
	 */
	@ValueConverter(rule = "PostScriptAlias")
	public IValueConverter<PostScriptFontAlias> postScriptFontAlias() {
		return new AbstractNullSafeConverter<PostScriptFontAlias>() {

			@Override
			protected String internalToString(PostScriptFontAlias value) {
				return value.toString();
			}

			@Override
			protected PostScriptFontAlias internalToValue(String string,
					INode node) throws ValueConverterException {
				String postscriptComparator = string.toUpperCase(Locale.ENGLISH)
						.replaceAll("-", "_");
				return PostScriptFontAlias.valueOf(postscriptComparator);
			}
		};
	}
}
