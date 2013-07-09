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
package org.eclipse.gef4.swtfx;

import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.gef4.swtfx.gc.LineCap;
import org.eclipse.gef4.swtfx.gc.LineJoin;

public class ShapeFigure extends AbstractFigure {

	private IShape shape;
	private StrokeType strokeType = StrokeType.CENTER;

	public ShapeFigure(IShape shape) {
		this.shape = shape;
	}

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

	@Override
	public boolean contains(double localX, double localY) {
		return shape.contains(new Point(localX, localY));
	}

	@Override
	public Rectangle getBoundsInLocal() {
		Rectangle boundsInLocal = getLayoutBounds();

		Path clip = getPaintStateByReference().getClipPathByReference();
		if (clip != null) {
			// advance/reduce boundsInLocal by clip
			// FIXME: Atm, the clip is a Path which is not transformed, or
			// anything. In JavaFX, the clip can be set to any Node.
			boundsInLocal = Path.intersect(clip, boundsInLocal.toPath())
					.getBounds();
		}

		return boundsInLocal;
	}

	@Override
	public Path getClipPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle getLayoutBounds() {
		Rectangle layoutBounds = shape.getBounds();

		if (strokeType != StrokeType.INSIDE) {
			// advance layoutBounds by strokeWidth
			double strokeWidth = getPaintStateByReference().getLineWidth();
			if (strokeType == StrokeType.CENTER) {
				strokeWidth /= 2;
			}
			layoutBounds.expand(strokeWidth, strokeWidth, strokeWidth,
					strokeWidth);
		}

		return layoutBounds;
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

	public StrokeType getStrokeType() {
		return strokeType;
	}

	@Override
	public void paint(GraphicsContext g) {
		g.fillPath(shape.toPath());
		g.strokePath(shape.toPath());
	}

	@Override
	public void setClipPath(Path clipPath) {
		// TODO Auto-generated method stub

	}

	public void setLineCap(LineCap cap) {
		getPaintStateByReference().setLineCap(cap);
	}

	public void setLineJoin(LineJoin join) {
		getPaintStateByReference().setLineJoin(join);
	}

	public void setStrokeType(StrokeType strokeType) {
		this.strokeType = strokeType;
	}

	@Override
	public String toString() {
		return "ShapeFigure(" + shape + ")";
	}

}
