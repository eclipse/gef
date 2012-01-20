/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AngleTests.class, CubicCurveTests.class, CurveUtilsTests.class,
		DimensionTests.class, EllipseTests.class, LineTests.class,
		PointListUtilsTests.class, PointTests.class, PolygonTests.class,
		PolylineTests.class, PolynomCalculationUtilsTests.class,
		PrecisionUtilsTests.class, QuadraticCurveTests.class,
		RectangleTests.class, StraightTests.class, VectorTests.class })
public class AllTests {

}
