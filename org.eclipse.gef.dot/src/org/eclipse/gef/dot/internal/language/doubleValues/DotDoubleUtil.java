/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Prigge (itemis AG) - initial API and implementation (bug #559031)
 *     
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language.doubleValues;

/**
 * 
 */
public class DotDoubleUtil {
	/**
	 * Provide a utility to parse dot hex numbers as doubles
	 * 
	 * @param string
	 *            the string representation of the double
	 * @return a Double object
	 */
	public static Double parseDotDouble(String string) {
		// if the string is a hex number without exponent, add exponent 0
		if (string.matches("^\\s*0[xX][^pP]+")) {
			string = string + "p0";
		}
		return Double.valueOf(string);
	}
}
