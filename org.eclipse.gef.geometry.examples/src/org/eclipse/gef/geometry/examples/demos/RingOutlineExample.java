/*******************************************************************************
 * Copyright (c) 2012, 2016 itemis AG and others.
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
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polygon;
import org.eclipse.gef.geometry.planar.Polyline;
import org.eclipse.gef.geometry.planar.Ring;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class RingOutlineExample extends AbstractExample {

	public static void main(String[] args) {
		new RingOutlineExample("Ring Outline Example");
	}

	public RingOutlineExample(String title) {
		super(title);
	}

	@Override
	protected ControllableShape[] getControllableShapes() {
		return new ControllableShape[] { new ControllableShape() {
			{
				addControlPoints(new Point(100, 100), new Point(400, 100),
						new Point(400, 200));
				addControlPoints(new Point(400, 100), new Point(400, 400),
						new Point(300, 400));
				addControlPoints(new Point(400, 400), new Point(100, 400),
						new Point(100, 300));
				addControlPoints(new Point(100, 400), new Point(100, 100),
						new Point(200, 100));
			}

			@Override
			public Ring getShape() {
				Point[] cp = getPoints();

				Polygon[] polygons = new Polygon[cp.length / 3];
				for (int i = 0; i < polygons.length; i++) {
					polygons[i] = new Polygon(cp[3 * i], cp[3 * i + 1],
							cp[3 * i + 2]);
				}

				return new Ring(polygons);
			}

			@Override
			public void onDraw(GC gc) {
				Ring ring = getShape();

				gc.setForeground(
						Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
				int lineWidth = gc.getLineWidth();
				gc.setLineWidth(1);

				for (Polyline outline : ring.getOutlines()) {
					gc.drawPolyline(Geometry2SWT.toSWTPointArray(outline));
					gc.setLineWidth(gc.getLineWidth() + 1);
				}

				gc.setLineWidth(lineWidth);
			}
		} };
	}

}
