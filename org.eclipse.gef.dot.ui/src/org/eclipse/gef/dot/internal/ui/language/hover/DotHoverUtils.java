/*******************************************************************************
 * Copyright (c) 2019, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Prigge    (itemis AG) - initial API and refactoring (bug #549412)
 *     Tamas Miklossy (itemis AG) - original implementation in DotHoverProvider
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.hover;

import java.io.StringReader;

import org.eclipse.gef.dot.internal.language.DotAstHelper;
import org.eclipse.gef.dot.internal.language.color.Color;
import org.eclipse.gef.dot.internal.language.color.DotColors;
import org.eclipse.gef.dot.internal.language.color.HSVColor;
import org.eclipse.gef.dot.internal.language.color.RGBColor;
import org.eclipse.gef.dot.internal.language.color.StringColor;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.gef.dot.internal.ui.language.DotActivator;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;

import com.google.inject.Injector;

public class DotHoverUtils {
	static String colorDescription(Attribute color, ID attributeValue) {
		return colorDescription(color, attributeValue.toString(),
				attributeValue.toValue());
	}

	static String colorDescription(Attribute attribute, String encoded,
			String value) {
		Color color = parse(encoded);
		String colorScheme = null;
		String colorName = null;
		String colorCode = null;
		if (color instanceof StringColor) {
			StringColor stringColor = (StringColor) color;
			colorScheme = stringColor.getScheme();
			if (colorScheme == null) {
				colorScheme = DotAstHelper
						.getColorSchemeAttributeValue(attribute);
			}
			if (colorScheme == null) {
				colorScheme = "x11"; //$NON-NLS-1$
			}
			colorName = stringColor.getName();
			colorCode = DotColors.get(colorScheme, colorName);
		}
		if (color instanceof RGBColor) {
			colorCode = value;
		}
		if (color instanceof HSVColor) {
			// TODO: implement
		}
		return DotColors.getColorDescription(colorScheme, colorName, colorCode);
	}

	static private Color parse(String attributeValue) {
		Injector dotColorInjector = DotActivator.getInstance().getInjector(
				DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTCOLOR);
		IParser parser = dotColorInjector.getInstance(IParser.class);

		IParseResult result = parser.parse(new StringReader(attributeValue));

		return (Color) result.getRootASTElement();
	}
}
