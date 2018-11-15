/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.hover;

import java.io.StringReader;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.language.DotAstHelper;
import org.eclipse.gef.dot.internal.language.color.Color;
import org.eclipse.gef.dot.internal.language.color.DotColors;
import org.eclipse.gef.dot.internal.language.color.HSVColor;
import org.eclipse.gef.dot.internal.language.color.RGBColor;
import org.eclipse.gef.dot.internal.language.color.StringColor;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.gef.dot.internal.ui.language.internal.DotActivator;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;

import com.google.inject.Injector;

public class DotHoverProvider extends DefaultEObjectHoverProvider {

	@Override
	protected String getHoverInfoAsHtml(EObject o) {
		if (o instanceof Attribute) {
			Attribute attribute = (Attribute) o;
			String attributeName = attribute.getName().toValue();
			ID attributeValue = attribute.getValue();
			if (attributeValue != null) {
				switch (attributeName) {
				case DotAttributes.BGCOLOR__GC:
				case DotAttributes.COLOR__CNE:
				case DotAttributes.FILLCOLOR__CNE:
				case DotAttributes.FONTCOLOR__GCNE:
				case DotAttributes.LABELFONTCOLOR__E:
					Color color = parse(attributeValue.toString());
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
						colorCode = attributeValue.toValue();
					}
					if (color instanceof HSVColor) {
						// TODO: implement
					}
					return DotColors.getColorDescription(colorScheme, colorName,
							colorCode);
				default:
					break;
				}
			}
		}

		return super.getHoverInfoAsHtml(o);
	}

	private Color parse(String attributeValue) {
		Injector dotColorInjector = DotActivator.getInstance().getInjector(
				DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTCOLOR);
		IParser parser = dotColorInjector.getInstance(IParser.class);

		IParseResult result = parser.parse(new StringReader(attributeValue));

		return (Color) result.getRootASTElement();
	}
}
