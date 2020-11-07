/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.conversion;

import org.eclipse.gef.dot.internal.language.DotAstHelper;
import org.eclipse.gef.dot.internal.language.color.Color;
import org.eclipse.gef.dot.internal.language.color.DotColors;
import org.eclipse.gef.dot.internal.language.color.HSVColor;
import org.eclipse.gef.dot.internal.language.color.RGBColor;
import org.eclipse.gef.dot.internal.language.color.StringColor;
import org.eclipse.gef.dot.internal.language.dot.Attribute;

public class DotColorInfo {

	private Attribute attribute;
	private Color color;

	/**
	 * The name of the color scheme, can be null.
	 */
	private String colorScheme;

	/**
	 * The name of the color, can be null.
	 */
	private String colorName;

	/**
	 * The hex code of the color, should not be null.
	 */
	private String colorCode;

	public DotColorInfo(Attribute attribute, Color color) {
		this.attribute = attribute;
		this.color = color;
	}

	public void calculate() {
		calculate(attribute.getValue().toValue());
	}

	public void calculate(String value) {
		if (color == null) {
			return;
		}

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
	}

	public String getColorScheme() {
		return colorScheme;
	}

	public String getColorName() {
		return colorName;
	}

	public String getColorCode() {
		return colorCode;
	}
}
