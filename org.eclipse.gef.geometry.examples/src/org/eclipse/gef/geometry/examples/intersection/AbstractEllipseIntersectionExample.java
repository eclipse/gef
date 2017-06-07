/*******************************************************************************
 * Copyright (c) 2011, 2016 itemis AG and others.
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