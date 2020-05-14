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
package org.eclipse.gef.geometry.examples.scalerotate;

import org.eclipse.gef.geometry.convert.swt.Geometry2SWT;
import org.eclipse.gef.geometry.planar.CubicCurve;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class CubicCurveScaleRotate extends AbstractScaleRotateExample {

	public static void main(String[] args) {
		new CubicCurveScaleRotate();
	}

	public CubicCurveScaleRotate() {
		super("Scale/Rotate - CubicCurve");
	}

	@Override
	protected AbstractScaleRotateShape createShape(Canvas canvas) {
		return new AbstractScaleRotateShape(canvas) {
			@Override
			public boolean contains(Point p) {
				return createGeometry().getBounds().contains(p);
			}

			@Override
			public CubicCurve createGeometry() {
				double w = getCanvas().getClientArea().width;
				double h = getCanvas().getClientArea().height;
				double padx = w / 10;
				double pady = h / 10;

				CubicCurve me = new CubicCurve(padx, pady, w + w, h, -w, h,
						w - padx, pady);
				me.rotateCW(getRotationAngle(), getCenter());
				me.scale(getZoomFactor(), getCenter());

				return me;
			}

			@Override
			public void draw(GC gc) {
				CubicCurve me = createGeometry();
				gc.fillRectangle(Geometry2SWT.toSWTRectangle(me.getBounds()));
				gc.drawPath(
						new org.eclipse.swt.graphics.Path(Display.getCurrent(),
								Geometry2SWT.toSWTPathData(me.toPath())));
			}
		};
	}
}
