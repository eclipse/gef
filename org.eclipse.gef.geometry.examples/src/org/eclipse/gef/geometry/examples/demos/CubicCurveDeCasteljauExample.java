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
import org.eclipse.gef.geometry.planar.CubicCurve;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Slider;

public class CubicCurveDeCasteljauExample extends AbstractExample {

	public static void main(String[] args) {
		new CubicCurveDeCasteljauExample("Cubic Bezier Curve Example");
	}

	private double parameterValue;

	public CubicCurveDeCasteljauExample(String title) {
		super(title);
	}

	@Override
	protected ControllableShape[] getControllableShapes() {
		return new ControllableShape[] { new ControllableShape() {
			{
				/*
				 * These are the control points used to construct the CubicCurve
				 * later.
				 */
				addControlPoints(new Point(100, 200), new Point(200, 100),
						new Point(300, 300), new Point(400, 200));
			}

			@Override
			public CubicCurve getShape() {
				/*
				 * Constructs the CubicCurve of the defined control points.
				 */
				return new CubicCurve(getPoints());
			}

			@Override
			public void onDraw(GC gc) {
				/*
				 * Draws the CubicCurve and the de Casteljau construction for
				 * the current parameter value.
				 */

				// Construct the CubicCurve from the defined control points.
				CubicCurve curve = getShape();

				// Draw the CubicCurve as an SWT Path.
				gc.drawPath(
						new org.eclipse.swt.graphics.Path(Display.getCurrent(),
								Geometry2SWT.toSWTPathData(curve.toPath())));

				/*
				 * Retrieve control points to compute the linear interpolations
				 * of the de Casteljau algorithm.
				 */
				Point[] points = getPoints();

				/*
				 * Define the colors for the intermediate lines. We have three
				 * stages and therefore three different colors for a cubic
				 * Bezier curve. This is the case, because the de Casteljau
				 * algorithm reduces the number of control points in each
				 * iteration until it reaches the actual point on the curve.
				 */
				int[] colors = new int[] { SWT.COLOR_DARK_GREEN, SWT.COLOR_BLUE,
						SWT.COLOR_DARK_RED };

				for (int ci = 0; ci < colors.length; ci++) {
					for (int i = 0; i < 3 - ci; i++) {
						// set line color
						gc.setForeground(Display.getCurrent()
								.getSystemColor(colors[ci]));

						// draw line
						gc.drawLine((int) points[i].x, (int) points[i].y,
								(int) points[i + 1].x, (int) points[i + 1].y);

						// interpolate point for the next iteration
						points[i] = new Line(points[i], points[i + 1])
								.get(parameterValue);

						// set color to black
						gc.setForeground(Display.getCurrent()
								.getSystemColor(SWT.COLOR_BLACK));

						// draw point
						gc.drawOval((int) (points[i].x - 2),
								(int) (points[i].y - 2), 4, 4);
					}
				}
			}
		} };
	}

	@Override
	public void onInit() {
		/*
		 * Creates the slider to be able to change the parameter value for which
		 * the de Casteljau algorithm is demonstrated. (Just SWT buzz.)
		 */

		// default value for t
		parameterValue = 0.5;

		// add slider
		final Slider slider = new Slider(shell, SWT.HORIZONTAL);
		slider.setBounds(0, 0, 200, 20);
		slider.setValues(50, 0, 101, 1, 1, 1);
		slider.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				parameterValue = (double) slider.getSelection() / 100;
				if (parameterValue < 0) {
					parameterValue = 0;
				}
				if (parameterValue > 1) {
					parameterValue = 1;
				}

				shell.redraw();
			}
		});
	}
}
