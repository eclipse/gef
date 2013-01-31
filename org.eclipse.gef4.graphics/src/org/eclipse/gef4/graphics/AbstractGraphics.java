/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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
import java.util.Stack;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IMultiShape;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.graphics.Pattern.Mode;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.font.Font;
import org.eclipse.gef4.graphics.image.Image;

/**
 * The AbstractGraphics class serves as the basis for any {@link IGraphics}
 * implementation as it provides all the accessors and state related stuff.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractGraphics implements IGraphics {

	/**
	 * A State object maintains one set of "lightweight" objects that constitute
	 * a state of an {@link IGraphics}.
	 * 
	 * @author mwienand
	 * 
	 */
	public static class State {

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

		/**
		 * Creates a new State object from the given set of "lightweight"
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
		 */
		public State(AffineTransform transform, boolean antiAlias, Path clip,
				double[] dashes, double dashBegin, double lineWidth,
				double miterLimit, LineCap lineCap, LineJoin lineJoin,
				Pattern drawPattern, Pattern fillPattern, Pattern textPattern,
				Color textBackground, Font font,
				InterpolationHint interpolation, boolean xorMode) {
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
		}

		/**
		 * Returns the exact (i.e. the same reference) {@link AffineTransform}
		 * currently maintained by this {@link State}.
		 * 
		 * @return the exact {@link AffineTransform} of this {@link State}
		 */
		public AffineTransform getAffineTransformByReference() {
			return transform;
		}

		/**
		 * Returns the exact (i.e. the same reference) clipping area (
		 * {@link Path}) currently maintained by this {@link State}.
		 * 
		 * @return the exact clipping area ({@link Path}) of this {@link State}
		 */
		public Path getClippingAreaByReference() {
			return clip;
		}

		/**
		 * Returns a copy of this {@link State}. The maintained objects are
		 * copied as well.
		 * 
		 * @return a deep copy of this {@link State}
		 */
		public State getCopy() {
			return new State(transform.getCopy(), antiAlias,
					clip == null ? null : clip.getCopy(), Arrays.copyOf(dashes,
							dashes.length), dashBegin, lineWidth, miterLimit,
					lineCap, lineJoin, drawPattern.getCopy(),
					fillPattern.getCopy(), textPattern.getCopy(),
					textBackground.getCopy(), font.getCopy(), interpolation,
					xorMode);
		}

		/**
		 * Returns the exact (i.e. the same reference) dash-array currently
		 * maintained by this {@link State}.
		 * 
		 * @return the exact dash-array maintained by this {@link State}
		 */
		public double[] getDashArrayByReference() {
			return dashes;
		}

		/**
		 * Returns the dash-begin currently maintained by this {@link State}.
		 * 
		 * @return the dash-begin maintained by this {@link State}
		 */
		public double getDashBegin() {
			return dashBegin;
		}

		/**
		 * Returns the exact (i.e. the same reference) draw {@link Pattern}
		 * currently maintained by this {@link State}.
		 * 
		 * @return the exact draw {@link Pattern} maintained by this
		 *         {@link State}
		 */
		public Pattern getDrawPatternByReference() {
			return drawPattern;
		}

		/**
		 * Returns the exact (i.e. the same reference) fill {@link Pattern}
		 * currently maintained by this {@link State}.
		 * 
		 * @return the exact fill {@link Pattern} maintained by this
		 *         {@link State}
		 */
		public Pattern getFillPatternByReference() {
			return fillPattern;
		}

		/**
		 * Returns the exact (i.e. the same reference) {@link Font} currently
		 * maintained by this {@link State}.
		 * 
		 * @return the exact {@link Font} maintained by this {@link State}
		 */
		public Font getFontByReference() {
			return font;
		}

		/**
		 * Returns the {@link InterpolationHint} currently maintained by this
		 * {@link State}.
		 * 
		 * @return the {@link InterpolationHint} currently maintained by this
		 *         {@link State}
		 */
		public InterpolationHint getInterpolationHint() {
			return interpolation;
		}

		/**
		 * Returns the {@link LineCap} currently maintained by this
		 * {@link State}.
		 * 
		 * @return the {@link LineCap} maintained by this {@link State}
		 */
		public LineCap getLineCap() {
			return lineCap;
		}

		/**
		 * Returns the {@link LineJoin} currently maintained by this
		 * {@link State}.
		 * 
		 * @return the {@link LineJoin} maintained by this {@link State}
		 */
		public LineJoin getLineJoin() {
			return lineJoin;
		}

		/**
		 * Returns the line width currently maintained by this {@link State}.
		 * 
		 * @return the line width maintained by this {@link State}
		 */
		public double getLineWidth() {
			return lineWidth;
		}

		/**
		 * Returns the miter limit currently maintained by this {@link State}.
		 * 
		 * @return the miter limit maintained by this {@link State}
		 */
		public double getMiterLimit() {
			return miterLimit;
		}

		/**
		 * Returns the exact (i.e. the same reference) write background
		 * {@link Color} currently maintained by this {@link State}.
		 * 
		 * @return the exact text background {@link Color} maintained by this
		 *         {@link State}
		 */
		public Color getWriteBackgroundByReference() {
			return textBackground;
		}

		/**
		 * Returns the exact (i.e. the same reference) text {@link Pattern}
		 * currently maintained by this {@link State}.
		 * 
		 * @return the exact write {@link Pattern} maintained by this
		 *         {@link State}
		 */
		public Pattern getWritePatternByReference() {
			return textPattern;
		}

		/**
		 * Returns <code>true</code> if anti-aliasing is enabled in this
		 * {@link State}, otherwise <code>false</code>.
		 * 
		 * @return <code>true</code> if anti-aliasing is enabled in this
		 *         {@link State}, otherwise <code>false</code>
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
		 * {@link State} to exactly the given value, i.e. "by reference".
		 * 
		 * @param at
		 *            the {@link AffineTransform} that is to be maintained by
		 *            this {@link State}
		 */
		public void setAffineTransformByReference(AffineTransform at) {
			this.transform = at;
		}

		/**
		 * Enables or disables anti-aliasing in this {@link State}.
		 * Anti-aliasing is enabled if <code>true</code> is passed-in,
		 * otherwise, anti-aliasing is disabled.
		 * 
		 * @param antiAliasing
		 *            if <code>true</code> enables anti-aliasing in this
		 *            {@link State}, if <code>false</code> disables
		 *            anti-aliasing in this {@link State}
		 */
		public void setAntiAliasing(boolean antiAliasing) {
			this.antiAlias = antiAliasing;
		}

		/**
		 * Sets the clipping area that is maintained by this {@link State} to
		 * exactly the given value, i.e. "by reference".
		 * 
		 * @param clip
		 *            a {@link Path} that is to be maintained by this
		 *            {@link State} as its clipping area
		 */
		public void setClippingAreaByReference(Path clip) {
			this.clip = clip;
		}

		/**
		 * Sets the dash-array that is maintained by this {@link State} to
		 * exactly the given value, i.e. "by reference".
		 * 
		 * @param dashes
		 *            a <code>double</code>-array that is to be maintained by
		 *            this {@link State} as its dash-array
		 */
		public void setDashArrayByReference(double[] dashes) {
			this.dashes = dashes;
		}

		/**
		 * Sets the dash begin of this {@link State} to the given value.
		 * 
		 * @param dashBegin
		 *            the new dash begin of this {@link State}
		 */
		public void setDashBegin(double dashBegin) {
			this.dashBegin = dashBegin;
		}

		/**
		 * Sets the draw {@link Pattern} that is maintained by this
		 * {@link State} to exactly the given value, i.e. "by reference".
		 * 
		 * @param drawPattern
		 *            the draw {@link Pattern} that is to be maintained by this
		 *            {@link State}
		 */
		public void setDrawPatternByReference(Pattern drawPattern) {
			this.drawPattern = drawPattern;
		}

		/**
		 * Sets the fill {@link Pattern} that is maintained by this
		 * {@link State} to exactly the given value, i.e. "by reference".
		 * 
		 * @param fillPattern
		 *            the fill {@link Pattern} that is to be maintained by this
		 *            {@link State}
		 */
		public void setFillPatternByReference(Pattern fillPattern) {
			this.fillPattern = fillPattern;
		}

		/**
		 * Sets the {@link Font} that is maintained by this {@link State} to
		 * exactly the given value, i.e. "by reference".
		 * 
		 * @param font
		 *            the {@link Font} that is to be maintained by this
		 *            {@link State}
		 */
		public void setFontByReference(Font font) {
			this.font = font;
		}

		/**
		 * Sets the {@link InterpolationHint} of this {@link State} to the given
		 * value.
		 * 
		 * @param interp
		 *            the new {@link InterpolationHint} of this {@link State}
		 */
		public void setInterpolationHint(InterpolationHint interp) {
			this.interpolation = interp;
		}

		/**
		 * Sets the {@link LineCap} of this {@link State} to the given value.
		 * 
		 * @param lineCap
		 *            the new {@link LineCap} of this {@link State}
		 */
		public void setLineCap(LineCap lineCap) {
			this.lineCap = lineCap;
		}

		/**
		 * Sets the {@link LineJoin} of this {@link State} to the given value.
		 * 
		 * @param lineJoin
		 *            the new {@link LineJoin} of this {@link State}
		 */
		public void setLineJoin(LineJoin lineJoin) {
			this.lineJoin = lineJoin;
		}

		/**
		 * Sets the line width of this {@link State} to the given value.
		 * 
		 * @param lineWidth
		 *            the new line width of this {@link State}
		 */
		public void setLineWidth(double lineWidth) {
			this.lineWidth = lineWidth;
		}

		/**
		 * Sets the miter limit of this {@link State} to the given value.
		 * 
		 * @param miterLimit
		 *            the new miter limit of this {@link State}
		 */
		public void setMiterLimit(double miterLimit) {
			this.miterLimit = miterLimit;
		}

		/**
		 * Sets the write background {@link Color} that is maintained by this
		 * {@link State} to exactly the given value, i.e. "by reference".
		 * 
		 * @param writeBg
		 *            the write background {@link Color} that is to be
		 *            maintained by this {@link State}
		 */
		public void setWriteBackgroundByReference(Color writeBg) {
			this.textBackground = writeBg;
		}

		/**
		 * Sets the write {@link Pattern} that is maintained by this
		 * {@link State} to exactly the given value, i.e. "by reference".
		 * 
		 * @param writePattern
		 *            the text {@link Pattern} that is to be maintained by this
		 *            {@link State}
		 */
		public void setWritePatternByReference(Pattern writePattern) {
			this.textPattern = writePattern;
		}

		/**
		 * Enables or disabled xor mode. If the given value is <code>true</code>
		 * xor mode is enabled, otherwise xor mode is disabled.
		 * 
		 * @param xor
		 *            <code>true</code> to enable xor mode, <code>false</code>
		 *            to disable it
		 */
		public void setXorMode(boolean xor) {
			xorMode = xor;
		}

	}

	private Stack<State> states = new Stack<State>();

	/**
	 * Creates a new {@link AbstractGraphics} object and initializes the
	 * {@link State}s stack with a "default"-{@link State}.
	 */
	public AbstractGraphics() {
		// push initial (default) state
		states.push(new State(IGraphics.DEFAULT_AFFINE_TRANSFORM,
				IGraphics.DEFAULT_ANTI_ALIASING,
				IGraphics.DEFAULT_CLIPPING_AREA, IGraphics.DEFAULT_DASH_ARRAY,
				IGraphics.DEFAULT_DASH_BEGIN, IGraphics.DEFAULT_LINE_WIDTH,
				IGraphics.DEFAULT_MITER_LIMIT, IGraphics.DEFAULT_LINE_CAP,
				IGraphics.DEFAULT_LINE_JOIN, IGraphics.DEFAULT_DRAW_PATTERN,
				IGraphics.DEFAULT_FILL_PATTERN,
				IGraphics.DEFAULT_WRITE_PATTERN,
				IGraphics.DEFAULT_WRITE_BACKGROUND, IGraphics.DEFAULT_FONT,
				IGraphics.DEFAULT_INTERPOLATION_HINT,
				IGraphics.DEFAULT_XOR_MODE).getCopy());
	}

	@Override
	public IGraphics clip(IMultiShape toClip) {
		if (toClip == null) {
			throw new IllegalArgumentException(
					"The given IMultiShape may not be null.");
		}
		return clip(toClip.toPath());
	}

	@Override
	public IGraphics clip(IShape toClip) {
		if (toClip == null) {
			throw new IllegalArgumentException(
					"The given IShape may not be null.");
		}
		return clip(toClip.toPath());
	}

	@Override
	public IGraphics clip(Path toClip) {
		if (toClip == null) {
			throw new IllegalArgumentException(
					"The given Path may not be null.");
		}
		Path clip = getCurrentState().getClippingAreaByReference();
		getCurrentState().setClippingAreaByReference(
				clip == null ? toClip : Path.intersect(clip, toClip));
		return this;
	}

	@Override
	public AffineTransform getAffineTransform() {
		return getCurrentState().getAffineTransformByReference().getCopy();
	}

	@Override
	public Path getClippingArea() {
		Path clip = getCurrentState().getClippingAreaByReference();
		return clip == null ? null : clip.getCopy();
	}

	/**
	 * Returns the current state of this {@link AbstractGraphics}.
	 * 
	 * @return the current state of this {@link AbstractGraphics}
	 */
	protected State getCurrentState() {
		return states.peek();
	}

	@Override
	public double[] getDashArray() {
		double[] dashes = getCurrentState().getDashArrayByReference();
		return Arrays.copyOf(dashes, dashes.length);
	}

	@Override
	public double getDashBegin() {
		return getCurrentState().getDashBegin();
	}

	@Override
	public Pattern getDrawPattern() {
		return getCurrentState().getDrawPatternByReference().getCopy();
	}

	@Override
	public Color getDrawPatternColor() {
		return getCurrentState().getDrawPatternByReference().getColor();
	}

	@Override
	public Gradient<?> getDrawPatternGradient() {
		return getCurrentState().getDrawPatternByReference().getGradient();
	}

	@Override
	public org.eclipse.gef4.graphics.image.Image getDrawPatternImage() {
		return getCurrentState().getDrawPatternByReference().getImage();
	}

	@Override
	public Pattern.Mode getDrawPatternMode() {
		return getCurrentState().getDrawPatternByReference().getMode();
	}

	@Override
	public Pattern getFillPattern() {
		return getCurrentState().getFillPatternByReference().getCopy();
	}

	@Override
	public Color getFillPatternColor() {
		return getCurrentState().getFillPatternByReference().getColor();
	}

	@Override
	public Gradient<?> getFillPatternGradient() {
		return getCurrentState().getFillPatternByReference().getGradient();
	}

	@Override
	public Image getFillPatternImage() {
		return getCurrentState().getFillPatternByReference().getImage();
	}

	@Override
	public Pattern.Mode getFillPatternMode() {
		return getCurrentState().getFillPatternByReference().getMode();
	}

	@Override
	public Font getFont() {
		return getCurrentState().getFontByReference().getCopy();
	}

	@Override
	public String getFontFamily() {
		return getCurrentState().getFontByReference().getFamily();
	}

	@Override
	public double getFontSize() {
		return getCurrentState().getFontByReference().getSize();
	}

	@Override
	public int getFontStyle() {
		return getCurrentState().getFontByReference().getStyle();
	}

	@Override
	public InterpolationHint getInterpolationHint() {
		return getCurrentState().getInterpolationHint();
	}

	@Override
	public LineCap getLineCap() {
		return getCurrentState().getLineCap();
	}

	@Override
	public LineJoin getLineJoin() {
		return getCurrentState().getLineJoin();
	}

	@Override
	public double getLineWidth() {
		return getCurrentState().getLineWidth();
	}

	@Override
	public double getMiterLimit() {
		return getCurrentState().getMiterLimit();
	}

	@Override
	public Color getWriteBackground() {
		return getCurrentState().getWriteBackgroundByReference().getCopy();
	}

	@Override
	public Pattern getWritePattern() {
		return getCurrentState().getWritePatternByReference().getCopy();
	}

	@Override
	public Color getWritePatternColor() {
		return getCurrentState().getWritePatternByReference().getColor();
	}

	@Override
	public Gradient<?> getWritePatternGradient() {
		return getCurrentState().getWritePatternByReference().getGradient();
	}

	@Override
	public Image getWritePatternImage() {
		return getCurrentState().getWritePatternByReference().getImage();
	}

	@Override
	public Pattern.Mode getWritePatternMode() {
		return getCurrentState().getWritePatternByReference().getMode();
	}

	@Override
	public boolean isAntiAliasing() {
		return getCurrentState().isAntiAliasing();
	}

	@Override
	public boolean isXorMode() {
		return getCurrentState().isXorMode();
	}

	@Override
	public IGraphics popState() {
		if (states.size() == 1) {
			throw new IllegalStateException("You have to push a State first.");
		}
		states.pop();
		return this;
	}

	@Override
	public IGraphics pushState() {
		if (states.isEmpty()) {
			throw new IllegalStateException(
					"No initial State pushed! The IGraphics is responsible for pushing an initial State on construction.");
		}
		State priorState = getCurrentState();
		states.push(priorState.getCopy());
		return this;
	}

	@Override
	public IGraphics restoreState() {
		popState();
		pushState();
		return this;
	}

	@Override
	public IGraphics rotate(Angle theta) {
		if (theta == null) {
			throw new IllegalArgumentException(
					"The given Angle may not be null.");
		}
		getCurrentState().getAffineTransformByReference().rotate(theta.rad());
		return this;
	}

	@Override
	public IGraphics scale(double sx, double sy) {
		getCurrentState().getAffineTransformByReference().scale(sx, sy);
		return this;
	}

	@Override
	public IGraphics setAffineTransform(AffineTransform transformations) {
		if (transformations == null) {
			getCurrentState().getAffineTransformByReference().setToIdentity();
		} else {
			getCurrentState().getAffineTransformByReference().setTransform(
					transformations);
		}
		return this;
	}

	@Override
	public IGraphics setAntiAliasing(boolean aa) {
		getCurrentState().setAntiAliasing(aa);
		return this;
	}

	@Override
	public IGraphics setClippingArea(Path clippingArea) {
		getCurrentState().setClippingAreaByReference(
				clippingArea == null ? null : clippingArea.getCopy());
		return this;
	}

	@Override
	public IGraphics setDashArray(double... dashArray) {
		getCurrentState().setDashArrayByReference(
				dashArray == null ? new double[] {} : Arrays.copyOf(dashArray,
						dashArray.length));
		return this;
	}

	@Override
	public IGraphics setDashBegin(double dashBegin) {
		getCurrentState().setDashBegin(dashBegin);
		return this;
	}

	@Override
	public IGraphics setDraw(Color color) {
		if (color == null) {
			throw new IllegalArgumentException(
					"The given Color may not be null.");
		}
		return setDrawPatternMode(Mode.COLOR).setDrawPatternColor(color);
	}

	@Override
	public IGraphics setDraw(Gradient<?> gradient) {
		if (gradient == null) {
			throw new IllegalArgumentException(
					"The given Gradient may not be null.");
		}
		return setDrawPatternMode(Mode.GRADIENT).setDrawPatternGradient(
				gradient);
	}

	@Override
	public IGraphics setDraw(Image image) {
		if (image == null) {
			throw new IllegalArgumentException(
					"The given Image may not be null.");
		}
		return setDrawPatternMode(Mode.IMAGE).setDrawPatternImage(image);
	}

	@Override
	public IGraphics setDrawPattern(Pattern pattern) {
		if (pattern == null) {
			throw new IllegalArgumentException(
					"The given Pattern may not be null.");
		}
		getCurrentState().setDrawPatternByReference(pattern.getCopy());
		return this;
	}

	@Override
	public IGraphics setDrawPatternColor(Color color) {
		if (color == null) {
			throw new IllegalArgumentException(
					"The given Color may not be null.");
		}
		getCurrentState().getDrawPatternByReference().setColor(color);
		return this;
	}

	@Override
	public IGraphics setDrawPatternGradient(Gradient<?> gradient) {
		if (gradient == null) {
			throw new IllegalArgumentException(
					"The given Gradient may not be null.");
		}
		getCurrentState().getDrawPatternByReference().setGradient(gradient);
		return this;
	}

	@Override
	public IGraphics setDrawPatternImage(Image image) {
		if (image == null) {
			throw new IllegalArgumentException(
					"The given Image may not be null.");
		}
		getCurrentState().getDrawPatternByReference().setImage(image);
		return this;
	}

	@Override
	public IGraphics setDrawPatternMode(Pattern.Mode drawMode) {
		if (drawMode == null) {
			throw new IllegalArgumentException(
					"The given Pattern.Mode may not be null.");
		}
		getCurrentState().getDrawPatternByReference().setMode(drawMode);
		return this;
	}

	@Override
	public IGraphics setFill(Color color) {
		if (color == null) {
			throw new IllegalArgumentException(
					"The given Color may not be null.");
		}
		return setFillPatternMode(Mode.COLOR).setFillPatternColor(color);
	}

	@Override
	public IGraphics setFill(Gradient<?> gradient) {
		if (gradient == null) {
			throw new IllegalArgumentException(
					"The given Gradient may not be null.");
		}
		return setFillPatternMode(Mode.GRADIENT).setFillPatternGradient(
				gradient);
	}

	@Override
	public IGraphics setFill(Image image) {
		if (image == null) {
			throw new IllegalArgumentException(
					"The given Image may not be null.");
		}
		return setFillPatternMode(Mode.IMAGE).setFillPatternImage(image);
	}

	@Override
	public IGraphics setFillPattern(Pattern pattern) {
		if (pattern == null) {
			throw new IllegalArgumentException(
					"The given Pattern may not be null.");
		}
		getCurrentState().setFillPatternByReference(pattern.getCopy());
		return this;
	}

	@Override
	public IGraphics setFillPatternColor(Color color) {
		if (color == null) {
			throw new IllegalArgumentException(
					"The given Color may not be null.");
		}
		getCurrentState().getFillPatternByReference().setColor(color);
		return this;
	}

	@Override
	public IGraphics setFillPatternGradient(Gradient<?> gradient) {
		if (gradient == null) {
			throw new IllegalArgumentException(
					"The given Gradient may not be null.");
		}
		getCurrentState().getFillPatternByReference().setGradient(gradient);
		return this;
	}

	@Override
	public IGraphics setFillPatternImage(Image image) {
		if (image == null) {
			throw new IllegalArgumentException(
					"The given Image may not be null.");
		}
		getCurrentState().getFillPatternByReference().setImage(image);
		return this;
	}

	@Override
	public IGraphics setFillPatternMode(Pattern.Mode fillMode) {
		if (fillMode == null) {
			throw new IllegalArgumentException(
					"The given Pattern.Mode may not be null.");
		}
		getCurrentState().getFillPatternByReference().setMode(fillMode);
		return this;
	}

	@Override
	public IGraphics setFont(Font font) {
		if (font == null) {
			throw new IllegalArgumentException(
					"The given Font may not be null.");
		}
		getCurrentState().setFontByReference(font.getCopy());
		return this;
	}

	@Override
	public IGraphics setFontFamily(String family) {
		if (family == null) {
			throw new IllegalArgumentException(
					"The given String may not be null.");
		}
		getCurrentState().getFontByReference().setFamily(family);
		return this;
	}

	@Override
	public IGraphics setFontSize(double size) {
		getCurrentState().getFontByReference().setSize(size);
		return this;
	}

	@Override
	public IGraphics setFontStyle(int style) {
		getCurrentState().getFontByReference().setStyle(style);
		return this;
	}

	@Override
	public IGraphics setInterpolationHint(InterpolationHint interpolationHint) {
		if (interpolationHint == null) {
			throw new IllegalArgumentException(
					"The given InterpolationHint may not be null.");
		}
		getCurrentState().setInterpolationHint(interpolationHint);
		return this;
	}

	@Override
	public IGraphics setLineCap(LineCap lineCap) {
		if (lineCap == null) {
			throw new IllegalArgumentException(
					"The given LineCap may not be null.");
		}
		getCurrentState().setLineCap(lineCap);
		return this;
	}

	@Override
	public IGraphics setLineJoin(LineJoin lineJoin) {
		if (lineJoin == null) {
			throw new IllegalArgumentException(
					"The given LineJoin may not be null.");
		}
		getCurrentState().setLineJoin(lineJoin);
		return this;
	}

	@Override
	public IGraphics setLineWidth(double lineWidth) {
		getCurrentState().setLineWidth(lineWidth);
		return this;
	}

	@Override
	public IGraphics setMiterLimit(double miterLimit) {
		getCurrentState().setMiterLimit(miterLimit);
		return this;
	}

	@Override
	public IGraphics setWrite(Color color) {
		if (color == null) {
			throw new IllegalArgumentException(
					"The given Color may not be null.");
		}
		return setWritePatternMode(Mode.COLOR).setWritePatternColor(color);
	}

	@Override
	public IGraphics setWrite(Gradient<?> gradient) {
		if (gradient == null) {
			throw new IllegalArgumentException(
					"The given Gradient may not be null.");
		}
		return setWritePatternMode(Mode.GRADIENT).setWritePatternGradient(
				gradient);
	}

	@Override
	public IGraphics setWrite(Image image) {
		if (image == null) {
			throw new IllegalArgumentException(
					"The given Image may not be null.");
		}
		return setWritePatternMode(Mode.IMAGE).setWritePatternImage(image);
	}

	@Override
	public IGraphics setWriteBackground(Color color) {
		if (color == null) {
			throw new IllegalArgumentException(
					"The given Color may not be null.");
		}
		getCurrentState().setWriteBackgroundByReference(color.getCopy());
		return this;
	}

	@Override
	public IGraphics setWritePattern(Pattern pattern) {
		if (pattern == null) {
			throw new IllegalArgumentException(
					"The given Pattern may not be null.");
		}
		getCurrentState().setWritePatternByReference(pattern.getCopy());
		return this;
	}

	@Override
	public IGraphics setWritePatternColor(Color color) {
		if (color == null) {
			throw new IllegalArgumentException(
					"The given Color may not be null.");
		}
		getCurrentState().getWritePatternByReference().setColor(color);
		return this;
	}

	@Override
	public IGraphics setWritePatternGradient(Gradient<?> gradient) {
		if (gradient == null) {
			throw new IllegalArgumentException(
					"The given Gradient may not be null.");
		}
		getCurrentState().getWritePatternByReference().setGradient(gradient);
		return this;
	}

	@Override
	public IGraphics setWritePatternImage(Image image) {
		if (image == null) {
			throw new IllegalArgumentException(
					"The given Image may not be null.");
		}
		getCurrentState().getWritePatternByReference().setImage(image);
		return this;
	}

	@Override
	public IGraphics setWritePatternMode(Pattern.Mode mode) {
		if (mode == null) {
			throw new IllegalArgumentException(
					"The given Pattern.Mode may not be null.");
		}
		getCurrentState().getWritePatternByReference().setMode(mode);
		return this;
	}

	@Override
	public IGraphics setXorMode(boolean xor) {
		getCurrentState().setXorMode(xor);
		return this;
	}

	@Override
	public IGraphics shear(double shx, double shy) {
		getCurrentState().getAffineTransformByReference().shear(shx, shy);
		return this;
	}

	@Override
	public String toString() {
		return "IGraphics()";
	}

	@Override
	public IGraphics translate(double x, double y) {
		getCurrentState().getAffineTransformByReference().translate(x, y);
		return this;
	}

	@Override
	public IGraphics unclip(IMultiShape toShow) {
		if (toShow == null) {
			throw new IllegalArgumentException(
					"The given IMultiShape may not be null.");
		}
		return unclip(toShow.toPath());
	}

	@Override
	public IGraphics unclip(IShape toShow) {
		if (toShow == null) {
			throw new IllegalArgumentException(
					"The given IShape may not be null.");
		}
		return unclip(toShow.toPath());
	}

	@Override
	public IGraphics unclip(Path toShow) {
		if (toShow == null) {
			throw new IllegalArgumentException(
					"The given Path may not be null.");
		}
		Path clip = getCurrentState().getClippingAreaByReference();
		getCurrentState().setClippingAreaByReference(
				clip == null ? null : Path.add(clip, toShow));
		return this;
	}

}
