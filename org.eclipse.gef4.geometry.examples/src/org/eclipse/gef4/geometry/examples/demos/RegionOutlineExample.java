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
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.Region;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class RegionOutlineExample extends AbstractIntersectionExample {
	public static void main(String[] args) {
		new RegionOutlineExample("Region Example");
	}

	public RegionOutlineExample(String title) {
		super(title);
	}

	protected AbstractControllableShape createControllableShape1(Canvas canvas) {
		return new AbstractControllableShape(canvas) {
			@Override
			public void createControlPoints() {
				addControlPoint(new Point(100, 0));
				addControlPoint(new Point(300, 100));

				addControlPoint(new Point(250, 200));
				addControlPoint(new Point(350, 330));

				addControlPoint(new Point(100, 200));
				addControlPoint(new Point(190, 325));

				addControlPoint(new Point(150, 300));
				addControlPoint(new Point(280, 380));
			}

			@Override
			public Region createGeometry() {
				Point[] cp = getControlPoints();
				Rectangle[] rectangles = new Rectangle[cp.length / 2];
				for (int i = 0; i < rectangles.length; i++) {
					rectangles[i] = new Rectangle(cp[2 * i], cp[2 * i + 1]);
					// System.out.println("R" + i + " " + cp[2 * i] + "\\"
					// + cp[2 * i + 1]);
				}
				Region region = new Region(rectangles);
				return region;
			}

			@Override
			public void drawShape(GC gc) {
				Region region = createGeometry();

				gc.setAlpha(128);
				gc.setBackground(Display.getCurrent().getSystemColor(
						SWT.COLOR_BLUE));
				for (Rectangle r : region.getShapes()) {
					gc.fillRectangle(r.toSWTRectangle());
				}

				gc.setAlpha(255);
				gc.setForeground(Display.getCurrent().getSystemColor(
						SWT.COLOR_RED));
				for (Rectangle r : region.getShapes()) {
					gc.drawRectangle(r.toSWTRectangle());
				}
				gc.setForeground(Display.getCurrent().getSystemColor(
						SWT.COLOR_BLACK));
				for (Line l : region.getOutlineSegments()) {
					gc.drawLine((int) (l.getX1()), (int) (l.getY1()),
							(int) (l.getX2()), (int) (l.getY2()));
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
