/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - Initial API and contribution
 *     Tamas Miklossy   (itemis AG) - Initial API and contribution
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import org.eclipse.gef.dot.internal.language.color.Color;
import org.eclipse.gef.dot.internal.language.color.DotColors;
import org.eclipse.gef.dot.internal.language.color.HSVColor;
import org.eclipse.gef.dot.internal.language.color.RGBColor;
import org.eclipse.gef.dot.internal.language.color.StringColor;

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
}
