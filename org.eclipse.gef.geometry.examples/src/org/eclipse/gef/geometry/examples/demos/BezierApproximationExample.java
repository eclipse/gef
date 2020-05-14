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
package org.eclipse.gef.geometry.examples.demos;

import org.eclipse.gef.geometry.convert.swt.Geometry2SWT;
import org.eclipse.gef.geometry.examples.AbstractExample;
import org.eclipse.gef.geometry.examples.ControllableShape;
import org.eclipse.gef.geometry.planar.BezierCurve;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polyline;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class BezierApproximationExample extends AbstractExample {
	public static void main(String[] args) {
		new BezierApproximationExample("Bezier Approximation Example");
	}

	public BezierApproximationExample(String title) {
		super(title); // Creates the UI for us.
	}

	@Override
	protected ControllableShape[] getControllableShapes() {
		return new ControllableShape[] { new ControllableShape() {
			{
				/*
				 * These are the control points used to construct the
				 * BezierCurve below.
				 */
				addControlPoints(new Point(100, 200), new Point(150, 250),
						new Point(200, 150), new Point(250, 250),
						new Point(300, 150), new Point(350, 250),
						new Point(400, 200));
			}

			@Override
			public BezierCurve getShape() {
				/*
				 * Here, we construct the BezierCurve from the defined Points.
				 */
				return new BezierCurve(getPoints());
			}

			@Override
			public void onDraw(GC gc) {
				/*
				 * This is the code to display the BezierCurve via SWT.
				 */

				// Construct the Bezier curve.
				BezierCurve curve = getShape();

				// Display the BezierCurve as a Path.
				gc.drawPath(
						new org.eclipse.swt.graphics.Path(Display.getCurrent(),
								Geometry2SWT.toSWTPathData(curve.toPath())));

				// Display the connection line of its control points.
				gc.setLineStyle(SWT.LINE_DOT);
				gc.drawPolyline(Geometry2SWT
						.toSWTPointArray(new Polyline(curve.getPoints())));

			}
		} };
	}
}
