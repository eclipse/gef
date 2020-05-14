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
package org.eclipse.gef.geometry.examples.intersection;

import org.eclipse.gef.geometry.planar.Ellipse;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;

public abstract class AbstractEllipseIntersectionExample
		extends AbstractIntersectionExample {

	public AbstractEllipseIntersectionExample(String title) {
		super(title);
	}

	@Override
	protected AbstractControllableShape createControllableShape1(
			Canvas canvas) {
		return new AbstractControllableShape(canvas) {
			@Override
			public void createControlPoints() {
				// the ellipse does not have any control points
			}

			@Override
			public Ellipse createGeometry() {
				double w5 = getCanvas().getClientArea().width / 5;
				double h5 = getCanvas().getClientArea().height / 5;
				return new Ellipse(w5, h5, 3 * w5, 3 * h5);
			}

			@Override
			public void drawShape(GC gc) {
				Ellipse ellipse = createGeometry();
				gc.drawOval((int) ellipse.getX(), (int) ellipse.getY(),
						(int) ellipse.getWidth(), (int) ellipse.getHeight());
			}
		};
	}

}