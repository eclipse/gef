/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
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
package org.eclipse.gef4.swtfx.examples;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.IFigure;
import org.eclipse.gef4.swtfx.IParent;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.gef4.swtfx.layout.Pane;

public class TestNestedPanes implements IExample {

	public static void main(String[] args) {
		new Example(new TestNestedPanes());
	}

	@Override
	public void addUi(final IParent root) {
		Pane main = new Pane();
		main.relocate(50, 50);
		main.setRotationAngle(Angle.fromDeg(15));

		{
			IFigure origin = new ShapeFigure(new Rectangle(-5, -5, 10, 10));
			IFigure topRight = new ShapeFigure(new Rectangle(95, -5, 10, 10));
			topRight.setFill(new RgbaColor(255, 0, 0));
			IFigure bottomRight = new ShapeFigure(new Rectangle(95, 95, 10, 10));
			bottomRight.setFill(new RgbaColor(0, 255, 0));
			main.addChildren(origin, topRight, bottomRight);
		}

		root.addChildren(main);

		Pane inner = new Pane();
		inner.relocate(50, 50);
		inner.setScaleX(1.5);
		inner.setRotationAngle(Angle.fromDeg(15));

		{
			IFigure origin = new ShapeFigure(new Rectangle(-5, -5, 10, 10));
			IFigure topRight = new ShapeFigure(new Rectangle(95, -5, 10, 10));
			topRight.setFill(new RgbaColor(255, 0, 0));
			IFigure bottomRight = new ShapeFigure(new Rectangle(95, 95, 10, 10));
			bottomRight.setFill(new RgbaColor(0, 255, 0));
			inner.addChildren(origin, topRight, bottomRight);
		}

		main.addChildren(inner);
	}

	@Override
	public int getHeight() {
		return 300;
	}

	@Override
	public String getTitle() {
		return "Test Nested Panes";
	}

	@Override
	public int getWidth() {
		return 400;
	}

}
