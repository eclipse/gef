/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.examples.intersection.AbstractIntersectionExample;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Polygon.NonSimplePolygonException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class TriangulationExample extends AbstractIntersectionExample {
	public static void main(String[] args) {
		new TriangulationExample("Triangulation Example");
	}

	public TriangulationExample(String title) {
		super(title);
	}

	protected AbstractControllableShape createControllableShape1(Canvas canvas) {
		return new AbstractControllableShape(canvas) {
			@Override
			public void createControlPoints() {
				addControlPoint(new Point(300 / 2, 100 / 2));
				addControlPoint(new Point(100 / 2, 200 / 2));
				addControlPoint(new Point(200 / 2, 300 / 2));
				addControlPoint(new Point(100 / 2, 500 / 2));
				addControlPoint(new Point(300 / 2, 400 / 2));
				addControlPoint(new Point(500 / 2, 600 / 2));
				addControlPoint(new Point(600 / 2, 300 / 2));
				addControlPoint(new Point(500 / 2, 400 / 2));
				addControlPoint(new Point(500 / 2, 200 / 2));
				addControlPoint(new Point(300 / 2, 200 / 2));
			}

			@Override
			public Polygon createGeometry() {
				Point[] cp = getControlPoints();
				Polygon p = new Polygon(getControlPoints());
				return p;
			}

			@Override
			public void drawShape(GC gc) {
				Polygon p = createGeometry();

				// System.out.println("p = " + p);

				gc.setForeground(Display.getCurrent().getSystemColor(
						SWT.COLOR_RED));

				Polygon[] triangulation;
				try {
					triangulation = p.getTriangulation();
				} catch (NonSimplePolygonException x) {
					triangulation = new Polygon[] { p };
				}
				for (Polygon triangle : triangulation) {
					gc.drawPolygon(triangle.toSWTPointArray());
				}

				int lineWidth = gc.getLineWidth();
				gc.setLineWidth(lineWidth + 2);
				gc.setForeground(Display.getCurrent().getSystemColor(
						SWT.COLOR_BLACK));

				gc.drawPolygon(p.toSWTPointArray());

				gc.setLineWidth(lineWidth);
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
				return new Line(-10, -10, -10, -10);
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
