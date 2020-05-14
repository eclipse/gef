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
