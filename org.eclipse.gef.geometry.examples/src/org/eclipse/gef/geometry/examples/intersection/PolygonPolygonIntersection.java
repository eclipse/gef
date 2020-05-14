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
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.geometry.examples.intersection;

import org.eclipse.gef.geometry.convert.swt.Geometry2SWT;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polygon;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;

public class PolygonPolygonIntersection
		extends AbstractPolygonIntersectionExample {

	public static void main(String[] args) {
		new PolygonPolygonIntersection();
	}

	public PolygonPolygonIntersection() {
		super("Polygon/Polygon Intersection");
	}

	@Override
	protected Point[] computeIntersections(IGeometry g1, IGeometry g2) {
		return ((Polygon) g1).getOutline()
				.getIntersections(((Polygon) g2).getOutline());
	}

	@Override
	protected AbstractControllableShape createControllableShape2(
			Canvas canvas) {
		return new AbstractControllableShape(canvas) {
			@Override
			public void createControlPoints() {
				addControlPoint(new Point(100, 100));
				addControlPoint(new Point(600, 200));
				addControlPoint(new Point(100, 300));
			}

			@Override
			public Polygon createGeometry() {
				Point[] points = getControlPoints();
				Polygon polygon = new Polygon(points);
				return polygon;
			}

			@Override
			public void drawShape(GC gc) {
				Polygon polygon = createGeometry();
				gc.drawPolygon(Geometry2SWT.toSWTPointArray(polygon));
			}
		};
	}

}
