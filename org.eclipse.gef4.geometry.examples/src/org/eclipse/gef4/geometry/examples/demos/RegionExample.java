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

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.examples.intersection.AbstractIntersectionExample;
import org.eclipse.gef4.geometry.examples.intersection.AbstractIntersectionExample.AbstractControllableShape;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.Region;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class RegionExample extends AbstractIntersectionExample {
	public static void main(String[] args) {
		new RegionExample("Region Example");
	}

	public RegionExample(String title) {
		super(title);
	}

	protected AbstractControllableShape createControllableShape1(Canvas canvas) {
		return new AbstractControllableShape(canvas) {
			@Override
			public void createControlPoints() {
				addControlPoint(new Point(100, 100));
				addControlPoint(new Point(200, 200));

				addControlPoint(new Point(150, 150));
				addControlPoint(new Point(250, 250));
			}

			@Override
			public Region createGeometry() {
				Point[] cp = getControlPoints();
				Region region = new Region(new Rectangle(cp[0], cp[1]),
						new Rectangle(cp[2], cp[3]));
				return region;
			}

			@Override
			public void drawShape(GC gc) {
				Region region = createGeometry();

				gc.setClipping(region.toSWTRegion());

				for (int y = 0; y < 800; y += 20) {
					gc.drawString(
							"abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz",
							20, y);
				}

				gc.setClipping((org.eclipse.swt.graphics.Region) null);

				gc.setAlpha(128);
				gc.setBackground(Display.getCurrent().getSystemColor(
						SWT.COLOR_BLUE));
				for (Rectangle r : region.getShapes()) {
					gc.fillRectangle(r.toSWTRectangle());
				}
				gc.setAlpha(255);
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
