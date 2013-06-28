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
package org.eclipse.gef4.swt.fx;

import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.swt.fx.gc.GraphicsContext;
import org.eclipse.gef4.swt.fx.gc.LineCap;
import org.eclipse.gef4.swt.fx.gc.LineJoin;

public class ShapeFigure extends AbstractFigure {

	private IShape shape;

	// TODO

	// public Paint getFillPaint();
	// public void setFillPaint(...);

	// public Paint getStrokePaint();
	// public void setStrokePaint(...);

	// public double[] getDashes();
	// public double getDashOffset();
	// public void setDashes(double....);
	// public void setDashes(double[]);

	// ... (line width, miter limit, anti-aliasing)

	public ShapeFigure(IShape shape) {
		this.shape = shape;
	}

	@Override
	public IBounds getBounds() {
		return new GeneralBounds(shape, getPaintStateByReference()
				.getTransformByReference());
	}

	public LineCap getLineCap() {
		return getPaintStateByReference().getLineCap();
	}

	public LineJoin getLineJoin() {
		return getPaintStateByReference().getLineJoin();
	}

	public IShape getShape() {
		return shape;
	}

	@Override
	public void paint(GraphicsContext g) {
		g.fillPath(shape.toPath());
	}

	public void setLineCap(LineCap cap) {
		getPaintStateByReference().setLineCap(cap);
	}

	public void setLineJoin(LineJoin join) {
		getPaintStateByReference().setLineJoin(join);
	}

	@Override
	public String toString() {
		return "ShapeFigure(" + shape + ")";
	}

}
