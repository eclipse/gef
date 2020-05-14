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
import org.eclipse.gef.geometry.planar.Ellipse;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class EllipseEllipseIntersection
		extends AbstractEllipseIntersectionExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new EllipseEllipseIntersection();
	}

	public EllipseEllipseIntersection() {
		super("Ellipse/Ellipse Intersection");
	}

	@Override
	protected Point[] computeIntersections(IGeometry g1, IGeometry g2) {
		return ((Ellipse) g1).getIntersections((Ellipse) g2);
	}

	@Override
	protected AbstractControllableShape createControllableShape2(
			Canvas canvas) {
		return new AbstractControllableShape(canvas) {
			@Override
			public void createControlPoints() {
				ControlPoint center = addControlPoint(new Point(300, 300));
				ControlPoint a = addControlPoint(new Point(400, 300));
				ControlPoint b = addControlPoint(new Point(300, 200));
				a.setYLink(center);
				b.setXLink(center);
			}

			@Override
			public Ellipse createGeometry() {
				Point[] points = getControlPoints();
				double a = Math.abs(points[0].x - points[1].x);
				double b = Math.abs(points[0].y - points[2].y);
				return new Ellipse(points[0].x - a, points[0].y - b, 2 * a,
						2 * b);
			}

			@Override
			public void drawShape(GC gc) {
				Ellipse ellipse = createGeometry();
				gc.drawPath(
						new org.eclipse.swt.graphics.Path(Display.getCurrent(),
								Geometry2SWT.toSWTPathData(ellipse.toPath())));
			}
		};
	}

}
