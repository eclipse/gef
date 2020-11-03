/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
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

import org.eclipse.gef.fx.anchors.StaticAnchor;
import org.eclipse.gef.fx.internal.nodes.Traverse;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.Point;
import org.junit.Test;

import javafx.collections.ObservableList;

@SuppressWarnings("restriction")
public class TraverseTests {

	@Test
	public void pointsAndCoordinatesAreInSync() {
		Traverse t = new Traverse();
		assertSync(t);
		t.setStartPoint(new Point(50, 50));
		assertSync(t);
		t.setEndPoint(new Point(150, 150));
		assertSync(t);
		t.addControlPoint(0, new Point(75, 75));
		assertSync(t);
		t.removeControlPoint(0);
		assertSync(t);
		t.setControlPoints(Arrays.asList(new Point[] { new Point(50,50), new Point(75, 75), new Point(100, 100), new Point(125, 125), new Point(150, 150) }));
		assertSync(t);
		t.setControlPoints(Arrays.asList(new Point[] { new Point(125, 125) }));
		assertSync(t);
		t.setPoints(Arrays.asList(new Point[] { new Point(50,50), new Point(75, 75), new Point(100, 100), new Point(125, 125), new Point(150, 150) }));
		assertSync(t);
		
		t.setStartAnchor(new StaticAnchor(new Point(55, 55)));
		assertSync(t);
		t.setEndAnchor(new StaticAnchor(new Point(160, 160)));
		assertSync(t);
		
	}

	protected void assertSync(Traverse t) {
		ObservableList<Point> points = t.getPointsUnmodifiable();
		ObservableList<Double> coordinates = t.getCurve().getPoints();

		assertEquals(points.size(), coordinates.size() / 2);
		for (int i = 0; i < points.size(); i++) {
			assertEquals(points.get(i), NodeUtils.localToParent(t.getCurve(),
					new Point(coordinates.get(2 * i), coordinates.get(2 * i + 1))));
		}
	}

}
