/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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
package org.eclipse.gef.dot.internal.parser.validation;

import org.eclipse.gef.dot.internal.parser.color.ColorPackage;
import org.eclipse.gef.dot.internal.parser.color.DotColors;
import org.eclipse.gef.dot.internal.parser.color.StringColor;
import org.eclipse.xtext.validation.Check;

/**
 * This class contains custom validation rules.
 *
 * See
 * https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
public class DotColorJavaValidator extends
		org.eclipse.gef.dot.internal.parser.validation.AbstractDotColorJavaValidator {

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
		String colorScheme = color.getScheme();
		if (colorScheme != null && !colorScheme.isEmpty()
				&& !DotColors.getColorSchemes().contains(colorScheme)) {
			error("'" + colorScheme + "' is not a valid color scheme.",
					ColorPackage.Literals.STRING_COLOR__SCHEME);
		} else {
			String colorName = color.getName();
			if (colorName != null && !colorName.isEmpty() && !DotColors
					.getColorNames(colorScheme).contains(colorName)) {
				error("The '" + colorName + "' color is not valid within the '"
						+ colorScheme + "' color scheme.",
						ColorPackage.Literals.STRING_COLOR__SCHEME);
			}
		}
	}

}
