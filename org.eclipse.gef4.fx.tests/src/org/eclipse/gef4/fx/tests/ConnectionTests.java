/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import java.util.List;

import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.geometry.planar.Point;
import org.junit.Test;

public class ConnectionTests {

	@Test
	public void test_PointConversions() {
		Point startPoint = new Point(123, 456);
		Point wayPoint = new Point(789, 123);
		Point endPoint = new Point(456, 789);

		Connection connection = new Connection();
		// ensure the local coordinate system of the curve node differs from
		// that of the connection
		connection.getCurveNode().setTranslateX(5);
		connection.getCurveNode().setTranslateY(5);

		connection.setStartPoint(startPoint);
		connection.addControlPoint(0, wayPoint);
		connection.setEndPoint(endPoint);

		assertEquals(startPoint, connection.getStartPoint());
		assertEquals(wayPoint, connection.getControlPoint(0));
		assertEquals(endPoint, connection.getEndPoint());

		List<Point> points = connection.getPoints();
		assertEquals(3, points.size());
		assertEquals(startPoint, points.get(0));
		assertEquals(wayPoint, points.get(1));
		assertEquals(endPoint, points.get(2));
	}

	@Test
	public void test_waypoints() throws IllegalArgumentException, IllegalAccessException {
		Connection connection = new Connection();
		Point startPoint = new Point(123, 456);
		Point endPoint = new Point(456, 789);
		connection.setStartPoint(startPoint);
		connection.setEndPoint(endPoint);

		connection.addControlPoint(0, new Point(50,50));
		assertEquals(new Point(50,50), connection.getControlPoint(0));

		connection.addControlPoint(0,  new Point(100, 100));
		assertEquals(new Point(100,100), connection.getControlPoint(0));
		assertEquals(new Point(50,50), connection.getControlPoint(1));
		
		connection.addControlPoint(1,  new Point(150, 150));
		assertEquals(new Point(100,100), connection.getControlPoint(0));
		assertEquals(new Point(150,150), connection.getControlPoint(1));
		assertEquals(new Point(50,50), connection.getControlPoint(2));
		
		connection.addControlPoint(1,  new Point(200, 200));
		assertEquals(new Point(100,100), connection.getControlPoint(0));
		assertEquals(new Point(200,200), connection.getControlPoint(1));
		assertEquals(new Point(150,150), connection.getControlPoint(2));
		assertEquals(new Point(50,50), connection.getControlPoint(3));
		
		connection.removeControlPoint(1);
		assertEquals(new Point(100,100), connection.getControlPoint(0));
		assertEquals(new Point(150,150), connection.getControlPoint(1));
		assertEquals(new Point(50,50), connection.getControlPoint(2));
	}

}
