/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.examples.demos;

import org.eclipse.gef4.geometry.examples.intersection.AbstractIntersectionExample;
import org.eclipse.gef4.geometry.examples.intersection.AbstractIntersectionExample.AbstractControllableShape;
import org.eclipse.gef4.geometry.planar.CubicCurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class CubicCurveDeCasteljauExample extends AbstractIntersectionExample {
	public static void main(String[] args) {
		new CubicCurveDeCasteljauExample("Cubic Bezier Curve Example");
	}

	public CubicCurveDeCasteljauExample(String title) {
		super(title);
	}

	protected AbstractControllableShape createControllableShape1(Canvas canvas) {
		return new AbstractControllableShape(canvas) {
			@Override
			public void createControlPoints() {
				addControlPoint(new Point(100, 200));
				addControlPoint(new Point(200, 100));
				addControlPoint(new Point(300, 300));
				addControlPoint(new Point(400, 200));
			}

			@Override
			public IGeometry createGeometry() {
				Point[] points = getControlPoints();

				CubicCurve curve = new CubicCurve(points[0], points[1],
						points[2], points[3]);

				return curve;
			}

			@Override
			public void drawShape(GC gc) {
				CubicCurve curve = (CubicCurve) createGeometry();

				// draw curve
				gc.drawPath(new org.eclipse.swt.graphics.Path(Display
						.getCurrent(), curve.toPath().toSWTPathData()));

				// draw bounds
				Rectangle bounds = curve.getBounds();

				gc.setForeground(Display.getCurrent().getSystemColor(
						SWT.COLOR_DARK_GRAY));
				gc.drawRectangle(bounds.toSWTRectangle());

				// draw lerps
				Point[] points = getControlPoints();
				for (int i = 0; i < 3; i++) {
					gc.setForeground(Display.getCurrent().getSystemColor(
							SWT.COLOR_DARK_GREEN));
					gc.drawLine((int) points[i].x, (int) points[i].y,
							(int) points[i + 1].x, (int) points[i + 1].y);
					gc.setForeground(Display.getCurrent().getSystemColor(
							SWT.COLOR_BLACK));
					points[i] = points[i].getTranslated(points[i + 1]
							.getTranslated(points[i].getScaled(-1)).getScaled(
									0.25));
					gc.drawOval((int) (points[i].x - 2),
							(int) (points[i].y - 2), 4, 4);
				}
				for (int i = 0; i < 2; i++) {
					gc.setForeground(Display.getCurrent().getSystemColor(
							SWT.COLOR_BLUE));
					gc.drawLine((int) points[i].x, (int) points[i].y,
							(int) points[i + 1].x, (int) points[i + 1].y);
					gc.setForeground(Display.getCurrent().getSystemColor(
							SWT.COLOR_BLACK));
					points[i] = points[i].getTranslated(points[i + 1]
							.getTranslated(points[i].getScaled(-1)).getScaled(
									0.25));
					gc.drawOval((int) (points[i].x - 2),
							(int) (points[i].y - 2), 4, 4);
				}
				for (int i = 0; i < 1; i++) {
					gc.setForeground(Display.getCurrent().getSystemColor(
							SWT.COLOR_DARK_RED));
					gc.drawLine((int) points[i].x, (int) points[i].y,
							(int) points[i + 1].x, (int) points[i + 1].y);
					gc.setForeground(Display.getCurrent().getSystemColor(
							SWT.COLOR_BLACK));
					points[i] = points[i].getTranslated(points[i + 1]
							.getTranslated(points[i].getScaled(-1)).getScaled(
									0.25));
					gc.drawOval((int) (points[i].x - 2),
							(int) (points[i].y - 2), 4, 4);
				}
			}
		};
	}

	protected AbstractControllableShape createControllableShape2(Canvas canvas) {
		return new AbstractControllableShape(canvas) {
			@Override
			public void createControlPoints() {
			}

			@Override
			public IGeometry createGeometry() {
				return new Line(new Point(), new Point());
			}

			@Override
			public void drawShape(GC gc) {
			}
		};
	}

	@Override
	protected Point[] computeIntersections(IGeometry g1, IGeometry g2) {
		return new Point[] {};
	}
}
