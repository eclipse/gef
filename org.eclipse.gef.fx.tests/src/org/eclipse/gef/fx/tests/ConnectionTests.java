/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.tests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.anchors.StaticAnchor;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.geometry.planar.Point;
import org.junit.Test;

public class ConnectionTests {

	@Test
	public void controlPoints() throws IllegalArgumentException, IllegalAccessException {
		Connection connection = new Connection();
		Point startPoint = new Point(123, 456);
		Point endPoint = new Point(456, 789);
		connection.setStartPoint(startPoint);
		connection.setEndPoint(endPoint);

		connection.addControlPoint(0, new Point(50, 50));
		assertEquals(new Point(123, 456), connection.getStartPoint());
		assertEquals(new Point(456, 789), connection.getEndPoint());
		assertEquals(new Point(50, 50), connection.getControlPoint(0));

		connection.addControlPoint(0, new Point(100, 100));
		assertEquals(new Point(123, 456), connection.getStartPoint());
		assertEquals(new Point(456, 789), connection.getEndPoint());
		assertEquals(new Point(100, 100), connection.getControlPoint(0));
		assertEquals(new Point(50, 50), connection.getControlPoint(1));

		connection.addControlPoint(1, new Point(150, 150));
		assertEquals(new Point(123, 456), connection.getStartPoint());
		assertEquals(new Point(456, 789), connection.getEndPoint());
		assertEquals(new Point(100, 100), connection.getControlPoint(0));
		assertEquals(new Point(150, 150), connection.getControlPoint(1));
		assertEquals(new Point(50, 50), connection.getControlPoint(2));

		connection.addControlPoint(1, new Point(200, 200));
		assertEquals(new Point(123, 456), connection.getStartPoint());
		assertEquals(new Point(456, 789), connection.getEndPoint());
		assertEquals(new Point(100, 100), connection.getControlPoint(0));
		assertEquals(new Point(200, 200), connection.getControlPoint(1));
		assertEquals(new Point(150, 150), connection.getControlPoint(2));
		assertEquals(new Point(50, 50), connection.getControlPoint(3));

		connection.removeControlPoint(1);
		assertEquals(new Point(123, 456), connection.getStartPoint());
		assertEquals(new Point(456, 789), connection.getEndPoint());
		assertEquals(new Point(100, 100), connection.getControlPoint(0));
		assertEquals(new Point(150, 150), connection.getControlPoint(1));
		assertEquals(new Point(50, 50), connection.getControlPoint(2));

		connection.setStartPoint(new Point(654, 321));
		connection.setEndPoint(new Point(987, 654));
		assertEquals(new Point(654, 321), connection.getStartPoint());
		assertEquals(new Point(987, 654), connection.getEndPoint());
		assertEquals(new Point(100, 100), connection.getControlPoint(0));
		assertEquals(new Point(150, 150), connection.getControlPoint(1));
		assertEquals(new Point(50, 50), connection.getControlPoint(2));

		connection.setControlPoint(1, new Point(47, 11));
		assertEquals(new Point(654, 321), connection.getStartPoint());
		assertEquals(new Point(987, 654), connection.getEndPoint());
		assertEquals(new Point(100, 100), connection.getControlPoint(0));
		assertEquals(new Point(47, 11), connection.getControlPoint(1));
		assertEquals(new Point(50, 50), connection.getControlPoint(2));

		connection.setControlPoints(Arrays.asList(new Point(1, 1), new Point(2, 2)));
		assertEquals(2, connection.getControlPoints().size());
		assertEquals(new Point(1, 1), connection.getControlPoint(0));
		assertEquals(new Point(2, 2), connection.getControlPoint(1));

		connection.setEndAnchor(new StaticAnchor(connection, new Point(50, 50)));
		assertEquals(new Point(50, 50), connection.getEndPoint());
		connection.setControlAnchor(0, new StaticAnchor(connection, new Point(22, 22)));
		assertEquals(new Point(22, 22), connection.getControlPoint(0));
		assertEquals(2, connection.getControlAnchors().size());
		assertEquals(2, connection.getControlPoints().size());
	}

	@Test
	public void exchangeCurve() {
		// setup connection with start, control, end points
		Connection connection = new Connection();
		Point startPoint = new Point(123, 456);
		Point endPoint = new Point(456, 789);
		connection.setStartPoint(startPoint);
		connection.setEndPoint(endPoint);
		Point controlPoint = new Point(50, 50);
		connection.addControlPoint(0, controlPoint);

		// exchange curve
		connection.setCurve(new GeometryNode<ICurve>());
		assertEquals(startPoint, connection.getStartPoint());
		assertEquals(endPoint, connection.getEndPoint());
		assertEquals(controlPoint, connection.getControlPoint(0));

		// change control point
		Point newControlPoint = new Point(10, 20);
		connection.setControlPoint(0, newControlPoint);
		assertEquals(newControlPoint, connection.getControlPoint(0));

		// change start and end points
		Point newStartPoint = new Point(12, 34);
		connection.setStartPoint(newStartPoint);
		assertEquals(newStartPoint, connection.getStartPoint());

		Point newEndPoint = new Point(56, 78);
		connection.setEndPoint(newEndPoint);
		assertEquals(newEndPoint, connection.getEndPoint());
	}

	@Test
	public void pointConversions() {
		Point startPoint = new Point(123, 456);
		Point wayPoint = new Point(789, 123);
		Point endPoint = new Point(456, 789);

		Connection connection = new Connection();
		// ensure the local coordinate system of the curve node differs from
		// that of the connection
		connection.getCurve().setTranslateX(5);
		connection.getCurve().setTranslateY(5);

		connection.setStartPoint(startPoint);
		connection.addControlPoint(0, wayPoint);
		connection.setEndPoint(endPoint);

		List<Point> points = connection.getPointsUnmodifiable();
		List<IAnchor> anchors = connection.getAnchorsUnmodifiable();
		assertEquals(3, points.size());
		assertEquals(3, anchors.size());

		assertEquals(startPoint, connection.getStartPoint());
		assertEquals(wayPoint, connection.getControlPoint(0));
		assertEquals(endPoint, connection.getEndPoint());

		assertEquals(startPoint, points.get(0));
		assertEquals(wayPoint, points.get(1));
		assertEquals(endPoint, points.get(2));

		// check positions are still valid after changing translation
		connection.getCurve().setTranslateX(50);
		connection.getCurve().setTranslateY(50);

		assertEquals(3, points.size());
		assertEquals(3, anchors.size());

		assertEquals(startPoint, connection.getStartPoint());
		assertEquals(wayPoint, connection.getControlPoint(0));
		assertEquals(endPoint, connection.getEndPoint());

		assertEquals(startPoint, points.get(0));
		assertEquals(wayPoint, points.get(1));
		assertEquals(endPoint, points.get(2));
	}
}
