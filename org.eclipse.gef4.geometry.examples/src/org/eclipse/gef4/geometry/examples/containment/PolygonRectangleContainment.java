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
package org.eclipse.gef4.geometry.examples.containment;

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;

public class PolygonRectangleContainment extends
		AbstractPolygonContainmentExample {

	public static void main(String[] args) {
		new PolygonRectangleContainment();
	}

	public PolygonRectangleContainment() {
		super("Polygon/Rectangle containment");
	}

	@Override
	protected boolean computeIntersects(IGeometry g1, IGeometry g2) {
		return ((Polygon) g1).touches((Rectangle) g2);
	}

	@Override
	protected boolean computeContains(IGeometry g1, IGeometry g2) {
		return ((Polygon) g1).contains((Rectangle) g2);
	}

	@Override
	protected AbstractControllableShape createControllableShape2(Canvas canvas) {
		return new AbstractControllableShape(canvas) {
			private final double WIDTH = 50;
			private final double HEIGHT = 75;

			@Override
			public void createControlPoints() {
				addControlPoint(new Point(110, 70));
			}

			@Override
			public Rectangle createGeometry() {
				Point[] points = getControlPoints();
				return new Rectangle(points[0].x - WIDTH / 2, points[0].y
						- HEIGHT / 2, WIDTH, HEIGHT);
			}

			@Override
			public void drawShape(GC gc) {
				Rectangle rect = createGeometry();
				gc.drawRectangle(rect.toSWTRectangle());
			}

			@Override
			public void fillShape(GC gc) {
				Rectangle rect = createGeometry();
				gc.fillRectangle(rect.toSWTRectangle());
			}
		};
	}
}
