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
package org.eclipse.gef.geometry.examples.containment;

import org.eclipse.gef.geometry.convert.swt.Geometry2SWT;
import org.eclipse.gef.geometry.planar.Ellipse;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;

public class EllipseRectangleContainment
		extends AbstractEllipseContainmentExample {
	public static void main(String[] args) {
		new EllipseRectangleContainment();
	}

	public EllipseRectangleContainment() {
		super("Ellipse/Rectangle containment");
	}

	@Override
	protected boolean computeContains(IGeometry g1, IGeometry g2) {
		return ((Ellipse) g1).contains(g2);
	}

	@Override
	protected boolean computeIntersects(IGeometry g1, IGeometry g2) {
		return ((Ellipse) g1).touches(g2);
	}

	@Override
	protected AbstractControllableShape createControllableShape2(
			Canvas canvas) {
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
				return new Rectangle(points[0].x - WIDTH / 2,
						points[0].y - HEIGHT / 2, WIDTH, HEIGHT);
			}

			@Override
			public void drawShape(GC gc) {
				Rectangle rect = createGeometry();
				gc.drawRectangle(Geometry2SWT.toSWTRectangle(rect));
			}

			@Override
			public void fillShape(GC gc) {
				Rectangle rect = createGeometry();
				gc.fillRectangle(Geometry2SWT.toSWTRectangle(rect));
			}
		};
	}
}
