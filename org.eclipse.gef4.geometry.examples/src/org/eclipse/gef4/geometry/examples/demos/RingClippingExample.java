/*******************************************************************************
 * Copyright (c) 2011, 2012 itemis AG and others.
 * 
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

import org.eclipse.gef4.geometry.examples.AbstractExample;
import org.eclipse.gef4.geometry.examples.ControllableShape;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.geometry.planar.Polyline;
import org.eclipse.gef4.geometry.planar.Ring;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class RingClippingExample extends AbstractExample {
	public static void main(String[] args) {
		new RingClippingExample("Ring Clipping Example");
	}

	public RingClippingExample(String title) {
		super(title);
	}

	@Override
	protected ControllableShape[] getControllableShapes() {
		return new ControllableShape[] { new ControllableShape() {
			{
				addControlPoints(new Point(100, 100), new Point(200, 100),
						new Point(100, 200));
				addControlPoints(new Point(300, 300), new Point(400, 200),
						new Point(400, 300));
				addControlPoints(new Point(250, 50), new Point(450, 75),
						new Point(300, 125));
			}

			@Override
			public Ring getShape() {
				Point[] cp = getPoints();
				Ring ring = new Ring(new Polygon(cp[0], cp[1], cp[2]),
						new Polygon(cp[3], cp[4], cp[5]), new Polygon(cp[6],
								cp[7], cp[8]));
				return ring;
			}

			@Override
			public void onDraw(GC gc) {
				Ring ring = getShape();

				gc.setClipping(ring.toSWTRegion());
				gc.setBackground(Display.getCurrent().getSystemColor(
						SWT.COLOR_WIDGET_BACKGROUND));
				for (int y = 0; y < 800; y += 20) {
					gc.drawString(
							"abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz",
							20, y);
				}

				gc.setClipping((org.eclipse.swt.graphics.Region) null);
				gc.setAlpha(128);
				gc.setBackground(Display.getCurrent().getSystemColor(
						SWT.COLOR_BLUE));
				for (Polyline p : ring.getOutline()) {
					gc.fillPolygon(p.toSWTPointArray());
				}

				gc.setAlpha(255);
			}
		} };
	}
}
