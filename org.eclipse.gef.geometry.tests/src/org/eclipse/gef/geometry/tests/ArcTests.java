/*******************************************************************************
 * Copyright (c) 2012, 2016 itemis AG and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.geometry.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.planar.Arc;
import org.junit.Test;

public class ArcTests {

	@Test
	public void test_equals() {
		Arc a1 = new Arc(0, 0, 100, 100, Angle.fromDeg(0), Angle.fromDeg(100));
		Arc a2 = new Arc(0, 0, 100, 100,
				Angle.fromDeg(360d - TestUtils.getPrecisionFraction() / 10d),
				Angle.fromDeg(100));
		assertEquals(a1, a2);
	}

}
