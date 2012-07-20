/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
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
package org.eclipse.gef4.geometry.examples.scalerotate;

import org.eclipse.gef4.geometry.planar.CubicCurve;
import org.eclipse.gef4.geometry.planar.Point;
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

				CubicCurve me = new CubicCurve(padx, pady, w + w, h, -w, h, w
						- padx, pady);
				me.rotateCW(getRotationAngle(), getCenter());
				me.scale(getZoomFactor(), getCenter());

				return me;
			}

			@Override
			public void draw(GC gc) {
				CubicCurve me = createGeometry();
				gc.fillRectangle(me.getBounds().toSWTRectangle());
				gc.drawPath(new org.eclipse.swt.graphics.Path(Display
						.getCurrent(), me.toPath().toSWTPathData()));
			}
		};
	}
}
