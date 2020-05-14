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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.geometry.convert.swt.Geometry2SWT;
import org.eclipse.gef.geometry.examples.AbstractExample;
import org.eclipse.gef.geometry.examples.ControllableShape;
import org.eclipse.gef.geometry.planar.BezierCurve;
import org.eclipse.gef.geometry.planar.CurvedPolygon;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.QuadraticCurve;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class CurvedPolygonExample extends AbstractExample {

	public static void main(String[] args) {
		new CurvedPolygonExample("CurvePolygon Exampe", new String[] {});
	}

	public CurvedPolygonExample(String title, String[] infos) {
		super(title, infos);
	}

	@Override
	protected ControllableShape[] getControllableShapes() {
		return new ControllableShape[] { new ControllableShape() {
			{
				addControlPoints(new Point(100, 200));
				addControlPoints(new Point(0, 150), new Point(50, 50));
				addControlPoints(new Point(150, 100), new Point(200, 50));
				addControlPoints(new Point(300, 200), new Point(250, 300));
				addControlPoints(new Point(150, 400));
			}

			@Override
			public CurvedPolygon getShape() {
				Point[] points = getPoints();
				List<QuadraticCurve> segs = new ArrayList<>();
				segs.add(new QuadraticCurve(points[0], points[1], points[2]));
				for (int i = 2; i < points.length; i += 2) {
					if (i == points.length - 2) {
						segs.add(new QuadraticCurve(points[i], points[i + 1],
								points[0]));
						break;
					}
					segs.add(new QuadraticCurve(points[i], points[i + 1],
							points[i + 2]));
				}
				return new CurvedPolygon(segs.toArray(new QuadraticCurve[] {}));
			}

			@Override
			public void onDraw(GC gc) {
				CurvedPolygon curvedPoly = getShape();

				for (BezierCurve c : curvedPoly.getOutlineSegments()) {
					gc.drawPath(new org.eclipse.swt.graphics.Path(
							Display.getCurrent(),
							Geometry2SWT.toSWTPathData(c.toPath())));
				}
			}

		}, new ControllableShape() {
			{
				addControlPoints(new Point(350, 300));
			}

			@Override
			public IGeometry getShape() {
				return null;
			}

			@Override
			public void onDraw(GC gc) {
				this.controlColor = viewer.getShapes()[0].getShape().contains(
						getPoints()[0]) ? SWT.COLOR_GREEN : SWT.COLOR_RED;
			}
		} };
	}
}
