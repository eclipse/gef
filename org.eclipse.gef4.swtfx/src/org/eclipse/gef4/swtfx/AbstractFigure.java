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

import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.swtfx.gc.Gradient;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.gef4.swtfx.gc.GraphicsContextState;
import org.eclipse.gef4.swtfx.gc.LineCap;
import org.eclipse.gef4.swtfx.gc.LineJoin;
import org.eclipse.gef4.swtfx.gc.PaintMode;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractFigure extends AbstractNode implements IFigure {

	// TODO: delegate to GraphicsContextState, do not allow direct access
	private GraphicsContextState paintState = new GraphicsContextState();

	@Override
	public double computeMaxHeight(double width) {
		return getLayoutBounds().getHeight();
	}

	@Override
	public double computeMaxWidth(double height) {
		return getLayoutBounds().getWidth();
	}

	@Override
	public double computeMinHeight(double width) {
		return getLayoutBounds().getHeight();
	}

	@Override
	public double computeMinWidth(double height) {
		return getLayoutBounds().getWidth();
	}

	@Override
	public double computePrefHeight(double width) {
		return getLayoutBounds().getHeight();
	}

	@Override
	public double computePrefWidth(double height) {
		return getLayoutBounds().getWidth();
	}

	abstract protected void doPaint(GraphicsContext g);

	@Override
	public Path getClipPath() {
		return paintState.getClipPathByReference();
	}

	@Override
	public double[] getDashes() {
		return paintState.getDashesByReference();
	}

	@Override
	public double getDashOffset() {
		return paintState.getDashOffset();
	}

	@Override
	public Object getFill() {
		return paintState.getFillByReference().getActive();
	}

	@Override
	public Font getFont() {
		// FIXME: return FontData or Font?
		return new Font(Display.getCurrent(),
				paintState.getFontDataByReference());
	}

	@Override
	public int getGlobalAlpha() {
		return (int) paintState.getGlobalAlpha();
	}

	@Override
	public LineCap getLineCap() {
		return paintState.getLineCap();
	}

	@Override
	public LineJoin getLineJoin() {
		return paintState.getLineJoin();
	}

	@Override
	public double getLineWidth() {
		return paintState.getLineWidth();
	}

	@Override
	public double getMiterLimit() {
		return paintState.getMiterLimit();
	}

	@Override
	public GraphicsContextState getPaintStateByReference() {
		return paintState;
	}

	@Override
	public Object getStroke() {
		return paintState.getStrokeByReference().getActive();
	}

	@Override
	public boolean isResizable() {
		return false;
	}

	@Override
	final public void paint(GraphicsContext g) {
		g.pushState(paintState);
		g.save();
		doPaint(g);
		g.restore();
		g.restore();
	}

	@Override
	public void resize(double width, double height) {
		// no resize per default
		// TODO: evaluate whether or not to throw the exception
		throw new UnsupportedOperationException("Cannot resize() this figure.");
	}

	@Override
	public void setClipPath(Path clipPath) {
		paintState.setClipPathByReference(clipPath);
	}

	@Override
	public void setDashes(double... dashes) {
		paintState.setDashesByReference(dashes);
	}

	@Override
	public void setDashOffset(double dashOffset) {
		paintState.setDashOffset(dashOffset);
	}

	@Override
	public void setFill(Gradient<?> gradient) {
		paintState.getFillByReference().setGradient(gradient);
		paintState.getFillByReference().setMode(PaintMode.GRADIENT);
	}

	@Override
	public void setFill(Image image) {
		paintState.getFillByReference().setImage(image);
		paintState.getFillByReference().setMode(PaintMode.IMAGE);
	}

	@Override
	public void setFill(RgbaColor color) {
		paintState.getFillByReference().setColor(color);
		paintState.getFillByReference().setMode(PaintMode.COLOR);
	}

	@Override
	public void setFont(Font font) {
		paintState.setFontDataByReference(font.getFontData()[0]);
	}

	@Override
	public void setGlobalAlpha(int alpha) {
		paintState.setGlobalAlpha(alpha);
	}

	@Override
	public void setLineCap(LineCap cap) {
		paintState.setLineCap(cap);
	}

	@Override
	public void setLineJoin(LineJoin join) {
		paintState.setLineJoin(join);
	}

	@Override
	public void setLineWidth(double width) {
		paintState.setLineWidth(width);
	}

	@Override
	public void setMiterLimit(double miterLimit) {
		paintState.setMiterLimit(miterLimit);
	}

	@Override
	public void setStroke(Gradient<?> gradient) {
		paintState.getStrokeByReference().setGradient(gradient);
		paintState.getStrokeByReference().setMode(PaintMode.GRADIENT);
	}

	@Override
	public void setStroke(Image image) {
		paintState.getStrokeByReference().setImage(image);
		paintState.getStrokeByReference().setMode(PaintMode.IMAGE);
	}

	@Override
	public void setStroke(RgbaColor color) {
		paintState.getStrokeByReference().setColor(color);
		paintState.getStrokeByReference().setMode(PaintMode.COLOR);
	}

	@Override
	public void update() {
		getScene().redraw();
	}

}
