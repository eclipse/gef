/*******************************************************************************
 * Copyright (c) 2012, 2013 itemis AG and others.
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
package org.eclipse.gef4.graphics;

import java.util.Arrays;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.font.Font;

/**
 * A GraphicsState object maintains one set of "lightweight" objects that
 * constitute a state of an {@link IGraphics}.
 * 
 * @author mwienand
 * 
 */
public class GraphicsState {

	private AffineTransform transform;
	private boolean antiAlias;
	private Path clip;
	private double[] dashes;
	private double dashBegin;
	private double lineWidth;
	private double miterLimit;
	private LineCap lineCap;
	private LineJoin lineJoin;
	private Pattern drawPattern;
	private Pattern fillPattern;
	private Pattern textPattern;
	private Color textBackground;
	private Font font;
	private InterpolationHint interpolation;
	private boolean xorMode;
	private int deviceDpi;
	private int logicalDpi;

	/**
	 * Creates a new GraphicsState object from the given set of "lightweight"
	 * objects.
	 * 
	 * @param transform
	 * @param antiAlias
	 * @param clip
	 * @param dashes
	 * @param dashBegin
	 * @param lineWidth
	 * @param miterLimit
	 * @param lineCap
	 * @param lineJoin
	 * @param drawPattern
	 * @param fillPattern
	 * @param textPattern
	 * @param textBackground
	 * @param font
	 * @param interpolation
	 * @param xorMode
	 * @param deviceDpi
	 * @param logicalDpi
	 */
	public GraphicsState(AffineTransform transform, boolean antiAlias,
			Path clip, double[] dashes, double dashBegin, double lineWidth,
			double miterLimit, LineCap lineCap, LineJoin lineJoin,
			Pattern drawPattern, Pattern fillPattern, Pattern textPattern,
			Color textBackground, Font font, InterpolationHint interpolation,
			boolean xorMode, int deviceDpi, int logicalDpi) {
		this.transform = transform;
		this.antiAlias = antiAlias;
		this.clip = clip;
		this.dashes = dashes;
		this.dashBegin = dashBegin;
		this.lineWidth = lineWidth;
		this.miterLimit = miterLimit;
		this.lineCap = lineCap;
		this.lineJoin = lineJoin;
		this.drawPattern = drawPattern;
		this.fillPattern = fillPattern;
		this.textPattern = textPattern;
		this.textBackground = textBackground;
		this.font = font;
		this.interpolation = interpolation;
		this.xorMode = xorMode;
		this.deviceDpi = deviceDpi;
		this.logicalDpi = logicalDpi;
	}

	/**
	 * Returns the exact (i.e. the same reference) {@link AffineTransform}
	 * currently maintained by this {@link GraphicsState}.
	 * 
	 * @return the exact {@link AffineTransform} of this {@link GraphicsState}
	 */
	public AffineTransform getAffineTransformByReference() {
		return transform;
	}

	/**
	 * Returns the exact (i.e. the same reference) clipping area ( {@link Path})
	 * currently maintained by this {@link GraphicsState}.
	 * 
	 * @return the exact clipping area ({@link Path}) of this
	 *         {@link GraphicsState}
	 */
	public Path getClippingAreaByReference() {
		return clip;
	}

	/**
	 * Returns a copy of this {@link GraphicsState}. The maintained objects are
	 * copied as well.
	 * 
	 * @return a deep copy of this {@link GraphicsState}
	 */
	public GraphicsState getCopy() {
		return new GraphicsState(transform.getCopy(), antiAlias,
				clip == null ? null : clip.getCopy(), Arrays.copyOf(dashes,
						dashes.length), dashBegin, lineWidth, miterLimit,
				lineCap, lineJoin, drawPattern.getCopy(),
				fillPattern.getCopy(), textPattern.getCopy(),
				textBackground.getCopy(), font.getCopy(), interpolation,
				xorMode, deviceDpi, logicalDpi);
	}

	/**
	 * Returns the exact (i.e. the same reference) dash-array currently
	 * maintained by this {@link GraphicsState}.
	 * 
	 * @return the exact dash-array maintained by this {@link GraphicsState}
	 */
	public double[] getDashArrayByReference() {
		return dashes;
	}

	/**
	 * Returns the dash-begin currently maintained by this {@link GraphicsState}
	 * .
	 * 
	 * @return the dash-begin maintained by this {@link GraphicsState}
	 */
	public double getDashBegin() {
		return dashBegin;
	}

	public int getDeviceDpi() {
		return deviceDpi;
	}

	/**
	 * Returns the exact (i.e. the same reference) draw {@link Pattern}
	 * currently maintained by this {@link GraphicsState}.
	 * 
	 * @return the exact draw {@link Pattern} maintained by this
	 *         {@link GraphicsState}
	 */
	public Pattern getDrawPatternByReference() {
		return drawPattern;
	}

	/**
	 * Returns the exact (i.e. the same reference) fill {@link Pattern}
	 * currently maintained by this {@link GraphicsState}.
	 * 
	 * @return the exact fill {@link Pattern} maintained by this
	 *         {@link GraphicsState}
	 */
	public Pattern getFillPatternByReference() {
		return fillPattern;
	}

	/**
	 * Returns the exact (i.e. the same reference) {@link Font} currently
	 * maintained by this {@link GraphicsState}.
	 * 
	 * @return the exact {@link Font} maintained by this {@link GraphicsState}
	 */
	public Font getFontByReference() {
		return font;
	}

	/**
	 * Returns the {@link InterpolationHint} currently maintained by this
	 * {@link GraphicsState}.
	 * 
	 * @return the {@link InterpolationHint} currently maintained by this
	 *         {@link GraphicsState}
	 */
	public InterpolationHint getInterpolationHint() {
		return interpolation;
	}

	/**
	 * Returns the {@link LineCap} currently maintained by this
	 * {@link GraphicsState}.
	 * 
	 * @return the {@link LineCap} maintained by this {@link GraphicsState}
	 */
	public LineCap getLineCap() {
		return lineCap;
	}

	/**
	 * Returns the {@link LineJoin} currently maintained by this
	 * {@link GraphicsState}.
	 * 
	 * @return the {@link LineJoin} maintained by this {@link GraphicsState}
	 */
	public LineJoin getLineJoin() {
		return lineJoin;
	}

	/**
	 * Returns the line width currently maintained by this {@link GraphicsState}
	 * .
	 * 
	 * @return the line width maintained by this {@link GraphicsState}
	 */
	public double getLineWidth() {
		return lineWidth;
	}

	public int getLogicalDpi() {
		return logicalDpi;
	}

	/**
	 * Returns the miter limit currently maintained by this
	 * {@link GraphicsState}.
	 * 
	 * @return the miter limit maintained by this {@link GraphicsState}
	 */
	public double getMiterLimit() {
		return miterLimit;
	}

	/**
	 * Returns the exact (i.e. the same reference) write background
	 * {@link Color} currently maintained by this {@link GraphicsState}.
	 * 
	 * @return the exact text background {@link Color} maintained by this
	 *         {@link GraphicsState}
	 */
	public Color getWriteBackgroundByReference() {
		return textBackground;
	}

	/**
	 * Returns the exact (i.e. the same reference) text {@link Pattern}
	 * currently maintained by this {@link GraphicsState}.
	 * 
	 * @return the exact write {@link Pattern} maintained by this
	 *         {@link GraphicsState}
	 */
	public Pattern getWritePatternByReference() {
		return textPattern;
	}

	/**
	 * Returns <code>true</code> if anti-aliasing is enabled in this
	 * {@link GraphicsState}, otherwise <code>false</code>.
	 * 
	 * @return <code>true</code> if anti-aliasing is enabled in this
	 *         {@link GraphicsState}, otherwise <code>false</code>
	 */
	public boolean isAntiAliasing() {
		return antiAlias;
	}

	/**
	 * Returns <code>true</code> if xor mode is enabled. Otherwise,
	 * <code>false</code> is returned.
	 * 
	 * @return <code>true</code> if xor mode is enabled, otherwise
	 *         <code>false</code>
	 */
	public boolean isXorMode() {
		return xorMode;
	}

	/**
	 * Sets the {@link AffineTransform} that is maintained by this
	 * {@link GraphicsState} to exactly the given value, i.e. "by reference".
	 * 
	 * @param at
	 *            the {@link AffineTransform} that is to be maintained by this
	 *            {@link GraphicsState}
	 */
	public void setAffineTransformByReference(AffineTransform at) {
		this.transform = at;
	}

	/**
	 * Enables or disables anti-aliasing in this {@link GraphicsState}.
	 * Anti-aliasing is enabled if <code>true</code> is passed-in, otherwise,
	 * anti-aliasing is disabled.
	 * 
	 * @param antiAliasing
	 *            if <code>true</code> enables anti-aliasing in this
	 *            {@link GraphicsState}, if <code>false</code> disables
	 *            anti-aliasing in this {@link GraphicsState}
	 */
	public void setAntiAliasing(boolean antiAliasing) {
		this.antiAlias = antiAliasing;
	}

	/**
	 * Sets the clipping area that is maintained by this {@link GraphicsState}
	 * to exactly the given value, i.e. "by reference".
	 * 
	 * @param clip
	 *            a {@link Path} that is to be maintained by this
	 *            {@link GraphicsState} as its clipping area
	 */
	public void setClippingAreaByReference(Path clip) {
		this.clip = clip;
	}

	/**
	 * Sets the dash-array that is maintained by this {@link GraphicsState} to
	 * exactly the given value, i.e. "by reference".
	 * 
	 * @param dashes
	 *            a <code>double</code>-array that is to be maintained by this
	 *            {@link GraphicsState} as its dash-array
	 */
	public void setDashArrayByReference(double[] dashes) {
		this.dashes = dashes;
	}

	/**
	 * Sets the dash begin of this {@link GraphicsState} to the given value.
	 * 
	 * @param dashBegin
	 *            the new dash begin of this {@link GraphicsState}
	 */
	public void setDashBegin(double dashBegin) {
		this.dashBegin = dashBegin;
	}

	public void setDeviceDpi(int dpi) {
		deviceDpi = dpi;
	}

	/**
	 * Sets the draw {@link Pattern} that is maintained by this
	 * {@link GraphicsState} to exactly the given value, i.e. "by reference".
	 * 
	 * @param drawPattern
	 *            the draw {@link Pattern} that is to be maintained by this
	 *            {@link GraphicsState}
	 */
	public void setDrawPatternByReference(Pattern drawPattern) {
		this.drawPattern = drawPattern;
	}

	/**
	 * Sets the fill {@link Pattern} that is maintained by this
	 * {@link GraphicsState} to exactly the given value, i.e. "by reference".
	 * 
	 * @param fillPattern
	 *            the fill {@link Pattern} that is to be maintained by this
	 *            {@link GraphicsState}
	 */
	public void setFillPatternByReference(Pattern fillPattern) {
		this.fillPattern = fillPattern;
	}

	/**
	 * Sets the {@link Font} that is maintained by this {@link GraphicsState} to
	 * exactly the given value, i.e. "by reference".
	 * 
	 * @param font
	 *            the {@link Font} that is to be maintained by this
	 *            {@link GraphicsState}
	 */
	public void setFontByReference(Font font) {
		this.font = font;
	}

	/**
	 * Sets the {@link InterpolationHint} of this {@link GraphicsState} to the
	 * given value.
	 * 
	 * @param interp
	 *            the new {@link InterpolationHint} of this
	 *            {@link GraphicsState}
	 */
	public void setInterpolationHint(InterpolationHint interp) {
		this.interpolation = interp;
	}

	/**
	 * Sets the {@link LineCap} of this {@link GraphicsState} to the given
	 * value.
	 * 
	 * @param lineCap
	 *            the new {@link LineCap} of this {@link GraphicsState}
	 */
	public void setLineCap(LineCap lineCap) {
		this.lineCap = lineCap;
	}

	/**
	 * Sets the {@link LineJoin} of this {@link GraphicsState} to the given
	 * value.
	 * 
	 * @param lineJoin
	 *            the new {@link LineJoin} of this {@link GraphicsState}
	 */
	public void setLineJoin(LineJoin lineJoin) {
		this.lineJoin = lineJoin;
	}

	/**
	 * Sets the line width of this {@link GraphicsState} to the given value.
	 * 
	 * @param lineWidth
	 *            the new line width of this {@link GraphicsState}
	 */
	public void setLineWidth(double lineWidth) {
		this.lineWidth = lineWidth;
	}

	public void setLogicalDpi(int dpi) {
		logicalDpi = dpi;
	}

	/**
	 * Sets the miter limit of this {@link GraphicsState} to the given value.
	 * 
	 * @param miterLimit
	 *            the new miter limit of this {@link GraphicsState}
	 */
	public void setMiterLimit(double miterLimit) {
		this.miterLimit = miterLimit;
	}

	/**
	 * Sets the write background {@link Color} that is maintained by this
	 * {@link GraphicsState} to exactly the given value, i.e. "by reference".
	 * 
	 * @param writeBg
	 *            the write background {@link Color} that is to be maintained by
	 *            this {@link GraphicsState}
	 */
	public void setWriteBackgroundByReference(Color writeBg) {
		this.textBackground = writeBg;
	}

	/**
	 * Sets the write {@link Pattern} that is maintained by this
	 * {@link GraphicsState} to exactly the given value, i.e. "by reference".
	 * 
	 * @param writePattern
	 *            the text {@link Pattern} that is to be maintained by this
	 *            {@link GraphicsState}
	 */
	public void setWritePatternByReference(Pattern writePattern) {
		this.textPattern = writePattern;
	}

	/**
	 * Enables or disabled xor mode. If the given value is <code>true</code> xor
	 * mode is enabled, otherwise xor mode is disabled.
	 * 
	 * @param xor
	 *            <code>true</code> to enable xor mode, <code>false</code> to
	 *            disable it
	 */
	public void setXorMode(boolean xor) {
		xorMode = xor;
	}

}