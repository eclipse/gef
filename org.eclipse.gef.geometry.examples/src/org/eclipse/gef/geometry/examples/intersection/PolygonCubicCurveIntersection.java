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
import org.eclipse.gef.geometry.planar.CubicCurve;
import org.eclipse.gef.geometry.planar.Ellipse;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polygon;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

/**
 * Simple example demonstrating the intersection of an {@link Ellipse} and a
 * {@link Line}.
 * 
 * @author Matthias Wienand (matthias.wienand@itemis.de)
 * 
 */
public class PolygonCubicCurveIntersection
		extends AbstractPolygonIntersectionExample {

	public static void main(String[] args) {
		new PolygonCubicCurveIntersection();
	}

	public PolygonCubicCurveIntersection() {
		super("Polygon/CubicCurve Intersection");
	}

	@Override
	protected Point[] computeIntersections(IGeometry g1, IGeometry g2) {
		return ((Polygon) g1).getOutline().getIntersections((CubicCurve) g2);
	}

	@Override
	protected AbstractControllableShape createControllableShape2(
			Canvas canvas) {
		return new AbstractControllableShape(canvas) {
			@Override
			public void createControlPoints() {
				addControlPoint(new Point(200, 100));
				addControlPoint(new Point(190, 310));
				addControlPoint(new Point(410, 90));
				addControlPoint(new Point(400, 300));
			}

			@Override
			public CubicCurve createGeometry() {
				Point[] controlPoints = getControlPoints();
				System.out.println("new CubicCurve(" + controlPoints[0] + ", "
						+ controlPoints[1] + ", " + controlPoints[2] + ", "
						+ controlPoints[3] + ")");
				return new CubicCurve(controlPoints);
			}

			@Override
			public void drawShape(GC gc) {
				CubicCurve c = createGeometry();
				gc.drawPath(
						new org.eclipse.swt.graphics.Path(Display.getCurrent(),
								Geometry2SWT.toSWTPathData(c.toPath())));
			}
		};
	}
}
