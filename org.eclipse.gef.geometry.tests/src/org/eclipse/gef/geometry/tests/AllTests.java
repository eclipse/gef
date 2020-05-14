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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AffineTransformTests.class, AngleTests.class, ArcTests.class,
		AWTConversionTests.class, BezierCurveTests.class, CubicCurveTests.class,
		CurvedPolygonTests.class, CurveUtilsTests.class, DimensionTests.class,
		EllipseTests.class, IGeometryTests.class, LineTests.class,
		PathTests.class, PieTests.class, PointListUtilsTests.class,
		PointTests.class, PolygonTests.class, PolylineTests.class,
		PrecisionUtilsTests.class, QuadraticCurveTests.class,
		RectangleTests.class, RegionTests.class, RingTests.class,
		RoundedRectangleTests.class, StraightTests.class, VectorTests.class,
		Vector3DTests.class })
public class AllTests {

}
