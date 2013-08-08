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
package org.eclipse.gef4.swt.fx.examples;

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.gc.RgbaColor;

/**
 * The ColoredPiece is used to demonstrate different layout panes. It is
 * resizable, it keeps its preferred size constant, and it fills its interior
 * with the specified color.
 */
public class ColoredPiece extends ShapeFigure {

	public double sWidth;
	public double sHeight;
	public RgbaColor color;

	public ColoredPiece(double w, double h, double red, double green,
			double blue) {
		super(new Rectangle(0, 0, w, h));
		color = new RgbaColor((int) (255 * red), (int) (255 * green),
				(int) (255 * blue));
		setFill(color);
		sWidth = w;
		sHeight = h;
	}

	@Override
	public double computePrefHeight(double width) {
		return sHeight;
	}

	@Override
	public double computePrefWidth(double height) {
		return sWidth;
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public void resize(double width, double height) {
		((Rectangle) getShape()).setSize(width, height);
	}

}
