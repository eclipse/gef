/*******************************************************************************
 * Copyright (c) 2011, 2016 itemis AG and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *     
 *******************************************************************************/
package org.eclipse.gef.geometry.tests;

import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;

/**
 * Utilities for geometry unit tests.
 * 
 * @author anyssen
 * @author mwienand
 * 
 */
public class TestUtils {

	public static double getPrecisionFraction() {
		// TODO: remove TestUtils
		return PrecisionUtils.calculateFraction(0);
	}

	private TestUtils() {
		// this class should not be instantiated by clients
	}

}
