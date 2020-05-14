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
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class CubicCurvesIntersection extends AbstractIntersectionExample {
	public static void main(String[] args) {
		new CubicCurvesIntersection("Cubic Bezier Curve/Curve Intersection");
	}

	public CubicCurvesIntersection(String title) {
		super(title);
	}

	@Override
	protected Point[] computeIntersections(IGeometry g1, IGeometry g2) {
		return ((CubicCurve) g1).getIntersections((CubicCurve) g2);
	}

	private AbstractControllableShape createControllableCubicBezierCurveShape(
			Canvas canvas, Point... points) {
		final Point start = points[0];
		final Point ctrl1 = points[1];
		final Point ctrl2 = points[2];
		final Point end = points[3];

		return new AbstractControllableShape(canvas) {
			@Override
			public void createControlPoints() {
				addControlPoint(start);
				addControlPoint(ctrl1);
				addControlPoint(ctrl2);
				addControlPoint(end);
			}

			@Override
			public IGeometry createGeometry() {
				return new CubicCurve(getControlPoints());
			}

			@Override
			public void drawShape(GC gc) {
				CubicCurve curve = (CubicCurve) createGeometry();

				gc.drawPath(
						new org.eclipse.swt.graphics.Path(Display.getCurrent(),
								Geometry2SWT.toSWTPathData(curve.toPath())));
			}
		};
	}

	@Override
	protected AbstractControllableShape createControllableShape1(
			Canvas canvas) {
		return createControllableCubicBezierCurveShape(canvas,
				new Point(100, 100), new Point(150, 50), new Point(310, 300),
				new Point(400, 200));
	}

	@Override
	protected AbstractControllableShape createControllableShape2(
			Canvas canvas) {
		return createControllableCubicBezierCurveShape(canvas,
				new Point(400, 100), new Point(310, 110), new Point(210, 210),
				new Point(100, 200));
	}
}
