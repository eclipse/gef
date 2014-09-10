/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.gef4.fx.nodes.FXCurveConnection;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.geometry.planar.Point;
import org.junit.Test;

public class FXCurveConnectionTests {

	@Test
	public void test_PointConversions() {
		IFXConnection connection = new FXCurveConnection();
		Point startPoint = new Point(123, 456);
		Point wayPoint = new Point(789, 123);
		Point endPoint = new Point(456, 789);
		connection.setStartPoint(startPoint);
		connection.addWayPoint(0, wayPoint);
		connection.setEndPoint(endPoint);
		assertEquals(startPoint, connection.getStartPoint());
		assertEquals(wayPoint, connection.getWayPoint(0));
		assertEquals(endPoint, connection.getEndPoint());
	}
	
}
