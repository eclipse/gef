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
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

@SuppressWarnings("restriction")
public class ConvexHullExample extends AbstractIntersectionExample {
	public static void main(String[] args) {
		new ConvexHullExample("Convex Hull Example");
	}

	public ConvexHullExample(String title) {
		super(title);
	}

	protected AbstractControllableShape createControllableShape1(Canvas canvas) {
		return new AbstractControllableShape(canvas) {
			@Override
			public void createControlPoints() {
				addControlPoint(new Point(100, 100));
				addControlPoint(new Point(150, 400));
				addControlPoint(new Point(200, 300));
				addControlPoint(new Point(250, 150));
				addControlPoint(new Point(300, 250));
				addControlPoint(new Point(350, 200));
				addControlPoint(new Point(400, 350));
			}

			@Override
			public Polygon createGeometry() {
				return new Polygon(Point.getConvexHull(getControlPoints()));
			}

			@Override
			public void drawShape(GC gc) {
				Polygon convexHull = createGeometry();
				gc.drawPath(new org.eclipse.swt.graphics.Path(Display
						.getCurrent(), convexHull.toPath().toSWTPathData()));
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
