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
package org.eclipse.gef4.swt.fx.gc;

import java.util.Arrays;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

public class GraphicsContextState {

	private double globalAlpha;

	private AffineTransform transform;

	private Paint fill;

	private Paint stroke;

	private double lineWidth;

	private LineCap lineCap;

	private LineJoin lineJoin;

	private double miterLimit;

	private Path clipPath;

	private FontData fontData;

	private TextAlignment textAlign;

	private TextVPos textBaseline;

	private FillRule fillRule;

	private boolean guarded;

	private double[] dashes;

	private double dashOffset;

	public GraphicsContextState() {
		this(1.0, new AffineTransform(), null, new Paint(new RgbaColor()),
				new Paint(new RgbaColor()), 1.0, LineCap.FLAT, LineJoin.BEVEL,
				10.0, new FontData("Times", 16, SWT.NORMAL),
				TextAlignment.LEFT, TextVPos.TOP, FillRule.EVEN_ODD, false,
				new double[] {}, 0);
	}

	public GraphicsContextState(double globalAlpha, AffineTransform transform,
			Path clipPath, Paint fill, Paint stroke, double lineWidth,
			LineCap lineCap, LineJoin lineJoin, double miterLimit,
			FontData fontData, TextAlignment textAlign, TextVPos textBaseline,
			FillRule fillRule, boolean guarded, double[] dashes,
			double dashOffset) {
		this.globalAlpha = globalAlpha;
		this.transform = transform;
		this.clipPath = clipPath;
		this.fill = fill;
		this.stroke = stroke;
		this.lineWidth = lineWidth;
		this.lineCap = lineCap;
		this.lineJoin = lineJoin;
		this.miterLimit = miterLimit;
		this.fontData = fontData;
		this.textAlign = textAlign;
		this.textBaseline = textBaseline;
		this.fillRule = fillRule;
		this.guarded = guarded;
		this.dashes = dashes;
		this.dashOffset = dashOffset;
	}

	public Path getClipPathByReference() {
		return clipPath;
	}

	public GraphicsContextState getCopy() {
		return new GraphicsContextState(globalAlpha, transform.getCopy(),
				clipPath, fill.getCopy(), stroke.getCopy(), lineWidth, lineCap,
				lineJoin, miterLimit, fontData, textAlign, textBaseline,
				fillRule, guarded, Arrays.copyOf(dashes, dashes.length),
				dashOffset);
	}

	public double[] getDashesByReference() {
		return dashes;
	}

	public double getDashOffset() {
		return dashOffset;
	}

	public Paint getFillByReference() {
		return fill;
	}

	public FillRule getFillRule() {
		return fillRule;
	}

	public FontData getFontDataByReference() {
		return fontData;
	}

	public double getGlobalAlpha() {
		return globalAlpha;
	}

	public LineCap getLineCap() {
		return lineCap;
	}

	public LineJoin getLineJoin() {
		return lineJoin;
	}

	public double getLineWidth() {
		return lineWidth;
	}

	public double getMiterLimit() {
		return miterLimit;
	}

	public Paint getStrokeByReference() {
		return stroke;
	}

	public TextAlignment getTextAlign() {
		return textAlign;
	}

	public TextVPos getTextBaseline() {
		return textBaseline;
	}

	public AffineTransform getTransformByReference() {
		return transform;
	}

	public boolean isGuarded() {
		return guarded;
	}

	public void setClipPathByReference(Path clipPath) {
		this.clipPath = clipPath;
	}

	public void setDashesByReference(double[] dashes) {
		this.dashes = dashes;
	}

	public void setDashOffset(double dashOffset) {
		this.dashOffset = dashOffset;
	}

	public void setFillByReference(Paint fill) {
		this.fill = fill;
	}

	public void setFillRule(FillRule fillRule) {
		this.fillRule = fillRule;
	}

	public void setFontDataByReference(FontData fontData) {
		this.fontData = fontData;
	}

	public void setGlobalAlpha(double globalAlpha) {
		this.globalAlpha = globalAlpha;
	}

	public void setGuarded(boolean guarded) {
		this.guarded = guarded;
	}

	public void setLineCap(LineCap lineCap) {
		this.lineCap = lineCap;
	}

	public void setLineJoin(LineJoin lineJoin) {
		this.lineJoin = lineJoin;
	}

	public void setLineWidth(double lineWidth) {
		this.lineWidth = lineWidth;
	}

	public void setMiterLimit(double miterLimit) {
		this.miterLimit = miterLimit;
	}

	public void setStrokeByReference(Paint stroke) {
		this.stroke = stroke;
	}

	public void setTextAlign(TextAlignment textAlign) {
		this.textAlign = textAlign;
	}

	public void setTextBaseline(TextVPos textBaseline) {
		this.textBaseline = textBaseline;
	}

	public void setTransformByReference(AffineTransform transform) {
		this.transform = transform;
	}
}
