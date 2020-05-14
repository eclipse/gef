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
import org.eclipse.gef.geometry.planar.QuadraticCurve;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class EllipseQuadraticCurveIntersection
		extends AbstractEllipseIntersectionExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new EllipseQuadraticCurveIntersection();
	}

	public EllipseQuadraticCurveIntersection() {
		super("Ellipse/QuadraticCurve Intersection");
	}

	@Override
	protected Point[] computeIntersections(IGeometry g1, IGeometry g2) {
		return ((Ellipse) g1).getIntersections((QuadraticCurve) g2);
	}

	@Override
	protected AbstractControllableShape createControllableShape2(
			Canvas canvas) {
		return new AbstractControllableShape(canvas) {
			@Override
			public void createControlPoints() {
				addControlPoint(new Point(100, 150));
				addControlPoint(new Point(400, 200));
				addControlPoint(new Point(550, 300));
			}

			@Override
			public QuadraticCurve createGeometry() {
				return new QuadraticCurve(getControlPoints());
			}

			@Override
			public void drawShape(GC gc) {
				QuadraticCurve c = createGeometry();
				gc.drawPath(
						new org.eclipse.swt.graphics.Path(Display.getCurrent(),
								Geometry2SWT.toSWTPathData(c.toPath())));
			}
		};
	}
}
