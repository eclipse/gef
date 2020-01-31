/*******************************************************************************
 * Copyright (c) 2016, 2020 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #461506)
 *     
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.validation;

import org.eclipse.gef.dot.internal.language.color.ColorPackage;
import org.eclipse.gef.dot.internal.language.color.DotColors;
import org.eclipse.gef.dot.internal.language.color.StringColor;
import org.eclipse.xtext.validation.Check;

/**
 * This class contains custom validation rules.
 *
 * See
 * https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
public class DotColorValidator extends AbstractDotColorValidator {

	/**
	 * Represents the color scheme that is defined in the DOT ast. If this color
	 * scheme is not defined, the default color scheme should be used in the
	 * validation.
	 */
	static String globalColorScheme = null;

	// TODO: eliminate this field and find a clear solution how to check if the
	// used color corresponds to the default color scheme or to the globally
	// defined color scheme. This issue is related to validation calls coming
	// from either the DotJavaValidator or from the DotImport.
	static boolean considerDefaultColorScheme = false;

	private final String defaultColorScheme = "x11";

	/**
	 * Checks that the color scheme defined within the given <i>color</i> value
	 * is a valid dot color scheme and that the color name also defined within
	 * the given <i>color</i> value are consistent to the defined color scheme.
	 * 
	 * @param color
	 *            The StringColor element to check.
	 */
	@Check
	public void checkConsistentColorSchemeAndColorName(StringColor color) {
		String colorScheme = null;

		// start with the default color scheme if desired
		if (considerDefaultColorScheme) {
			colorScheme = defaultColorScheme;
		}

		String localColorScheme = color.getScheme();
		if (localColorScheme != null && !localColorScheme.isEmpty()) {
			// check if the localColorScheme is a valid colorScheme
			// (case insensitively)
			if (!DotColors.getColorSchemes()
					.contains(localColorScheme.toLowerCase())) {
				error("'" + localColorScheme + "' is not a valid color scheme.",
						ColorPackage.Literals.STRING_COLOR__SCHEME);
				return;
			}
			colorScheme = localColorScheme;
		} else if (globalColorScheme != null) {
			colorScheme = globalColorScheme;
		}

		if (colorScheme == null) {
			return;
		}

		// check if the color is valid in the colorScheme
		String colorName = color.getName();
		if (colorName != null && !colorName.isEmpty()
				&& !DotColors.getColorNames(colorScheme.toLowerCase())
						.contains(colorName.toLowerCase())) {
			error("The '" + colorName + "' color is not valid within the '"
					+ colorScheme + "' color scheme.",
					ColorPackage.Literals.STRING_COLOR__SCHEME);
		}
	}

}
