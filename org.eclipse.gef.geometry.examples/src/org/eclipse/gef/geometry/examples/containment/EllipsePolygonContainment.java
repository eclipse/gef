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
package org.eclipse.gef.geometry.examples.containment;

import org.eclipse.gef.geometry.convert.swt.Geometry2SWT;
import org.eclipse.gef.geometry.planar.Ellipse;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polygon;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;

public class EllipsePolygonContainment
		extends AbstractEllipseContainmentExample {

	public static void main(String[] args) {
		new EllipsePolygonContainment();
	}

	public EllipsePolygonContainment() {
		super("Ellipse/Polygon Containment");
	}

	@Override
	protected boolean computeContains(IGeometry g1, IGeometry g2) {
		return ((Ellipse) g1).contains(g2);
	}

	@Override
	protected boolean computeIntersects(IGeometry g1, IGeometry g2) {
		return ((Ellipse) g1).touches(g2);
	}

	@Override
	protected AbstractControllableShape createControllableShape2(
			Canvas canvas) {
		return new AbstractControllableShape(canvas) {
			@Override
			public void createControlPoints() {
				addControlPoint(new Point(200, 200));
			}

			@Override
			public Polygon createGeometry() {
				Point[] points = getControlPoints();
				Polygon polygon = new Polygon(points[0].x - 50,
						points[0].y - 50, points[0].x + 100, points[0].y - 30,
						points[0].x - 40, points[0].y + 60);
				return polygon;
			}

			@Override
			public void drawShape(GC gc) {
				Polygon polygon = createGeometry();
				gc.drawPolygon(Geometry2SWT.toSWTPointArray(polygon));
			}

			@Override
			public void fillShape(GC gc) {
				Polygon polygon = createGeometry();
				gc.fillPolygon(Geometry2SWT.toSWTPointArray(polygon));
			}
		};
	}
}
