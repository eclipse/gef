/*******************************************************************************
 * Copyright (c) 2018, 2021 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand   (itemis AG) - Initial API and contribution
 *     Tamas Miklossy     (itemis AG) - Initial API and contribution
 *     Zoey Gerrit Prigge (itemis AG) - compute HTML color (bug #321775)
 *                                    - avoid ungrammatical color causing NPE (bug #540508)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.conversion;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.color.Color;
import org.eclipse.gef.dot.internal.language.color.DotColors;
import org.eclipse.gef.dot.internal.language.color.HSVColor;
import org.eclipse.gef.dot.internal.language.color.RGBColor;
import org.eclipse.gef.dot.internal.language.color.StringColor;
import org.eclipse.gef.dot.internal.language.colorlist.ColorList;
import org.eclipse.gef.dot.internal.language.colorlist.WC;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.gef.dot.internal.ui.language.DotActivator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xtext.parser.IParser;

public class DotColorUtil {

	/**
	 * Returns the javafx representation of a dot color.
	 *
	 * @param colorScheme
	 *            The colorscheme attribute value (or null if not defined)
	 * @param dotColor
	 *            The color in dot representation.
	 * @return The color in javafx representation, or null if the javafx color
	 *         representation cannot be determined.
	 */
	public String computeZestColor(String colorScheme, Color dotColor) {
		String javaFxColor = null;
		if (dotColor instanceof RGBColor) {
			RGBColor rgbColor = (RGBColor) dotColor;
			StringBuffer sb = new StringBuffer();
			sb.append("#"); //$NON-NLS-1$
			sb.append(rgbColor.getR());
			sb.append(rgbColor.getG());
			sb.append(rgbColor.getB());
			if (rgbColor.getA() != null) {
				sb.append(rgbColor.getA());
			}
			javaFxColor = sb.toString();
		} else if (dotColor instanceof HSVColor) {
			HSVColor hsvColor = (HSVColor) dotColor;
			javaFxColor = String.format("hsb(%s, %s%%, %s%%)", //$NON-NLS-1$
					Double.parseDouble(hsvColor.getH()) * 360,
					Double.parseDouble(hsvColor.getS()) * 100,
					Double.parseDouble(hsvColor.getV()) * 100);
		} else if (dotColor instanceof StringColor) {
			StringColor stringColor = (StringColor) dotColor;
			// first evaluate the locally defined color scheme, if it is null,
			// fall back to the colorscheme dot attribute value, if it is null,
			// fall back to the default color scheme
			String currentColorScheme = stringColor.getScheme();
			if (currentColorScheme == null) {
				currentColorScheme = colorScheme;
			}
			if (currentColorScheme == null || currentColorScheme.isEmpty()) {
				currentColorScheme = "x11"; //$NON-NLS-1$
			}
			String colorName = stringColor.getName();
			javaFxColor = DotColors.get(currentColorScheme, colorName);
		}
		return javaFxColor;
	}

	public javafx.scene.paint.Color computeGraphBackgroundColor(
			String colorScheme, Color dotColor) {
		if (dotColor instanceof HSVColor) {
			HSVColor hsvColor = (HSVColor) dotColor;
			return javafx.scene.paint.Color.hsb(
					Double.parseDouble(hsvColor.getH()) * 360,
					Double.parseDouble(hsvColor.getS()),
					Double.parseDouble(hsvColor.getV()));
		} else {
			String javaFxStringColor = computeZestColor(colorScheme, dotColor);
			// avoid ungrammatical color attribute causing NPE (bug #540508)
			if (javaFxStringColor != null) {
				return javafx.scene.paint.Color.web(javaFxStringColor);
			}
		}
		return null;
	}

	/**
	 * Creates a {@link Color} object of a color attribute value.
	 *
	 * @param colorAttributeValue
	 *            The string attribute value.
	 * @return The corresponding {@link Color} object.
	 */
	public Color parseColorAttributeValue(String colorAttributeValue) {
		if (colorAttributeValue == null) {
			return null;
		}
		IParser parser = DotActivator.getInstance().getInjector(
				DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTCOLOR)
				.getInstance(IParser.class);
		EObject rootNode = parser.parse(new StringReader(colorAttributeValue))
				.getRootASTElement();
		if (rootNode instanceof Color) {
			return (Color) rootNode;
		}
		return null;
	}

	/**
	 * Creates a list of {@link Color} objects of a color list attribute value.
	 *
	 * @param colorListAttributeValue
	 *            The string attribute value.
	 * @return List of the corresponding {@link Color} objects.
	 */
	public List<Color> parseColorListAttributeValue(
			String colorListAttributeValue) {
		if (colorListAttributeValue == null) {
			return null;
		}
		IParser parser = DotActivator.getInstance().getInjector(
				DotActivator.ORG_ECLIPSE_GEF_DOT_INTERNAL_LANGUAGE_DOTCOLORLIST)
				.getInstance(IParser.class);
		EObject rootNode = parser
				.parse(new StringReader(colorListAttributeValue))
				.getRootASTElement();
		if (rootNode instanceof ColorList) {
			ColorList colorList = (ColorList) rootNode;
			List<Color> colors = new ArrayList<>();
			for (WC wc : colorList.getColorValues()) {
				Color color = wc.getColor();
				colors.add(color);
			}
			return colors;
		}
		return null;
	}

	public DotColorInfo getColorInfo(Attribute attribute) {
		org.eclipse.gef.dot.internal.language.color.Color dotColor = parseColorAttributeValue(
				attribute.getValue().toValue());
		DotColorInfo colorInfo = new DotColorInfo(attribute, dotColor);
		colorInfo.calculate();
		return colorInfo;
	}

	public List<DotColorInfo> getColorInfos(Attribute attribute) {
		List<DotColorInfo> colorInfos = new ArrayList<>();

		ID attributeValue = attribute.getValue();

		if (attributeValue != null) {
			List<org.eclipse.gef.dot.internal.language.color.Color> dotColors = parseColorListAttributeValue(
					attributeValue.toValue());

			for (Color dotColor : dotColors) {
				DotColorInfo colorInfo = new DotColorInfo(attribute, dotColor);
				colorInfo.calculate();
				colorInfos.add(colorInfo);
			}
		}

		return colorInfos;
	}

	public org.eclipse.swt.graphics.Color hex2Rgb(String colorStr) {
		return new org.eclipse.swt.graphics.Color(Display.getDefault(),
				Integer.valueOf(colorStr.substring(1, 3), 16),
				Integer.valueOf(colorStr.substring(3, 5), 16),
				Integer.valueOf(colorStr.substring(5, 7), 16));
	}
}
